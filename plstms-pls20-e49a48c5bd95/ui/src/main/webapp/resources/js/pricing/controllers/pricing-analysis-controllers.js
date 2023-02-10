/**
 * LTL Analysis controllers.
 */
angular.module('plsApp').controller('AnalysisController', ['$scope', '$rootScope', '$route', 'FreightAnalysisServices', 'urlConfig',
                                                    'NgGridPluginFactory', '$interval', '$window', '$http',
    function ($scope, $rootScope, $route, FreightAnalysisServices, urlConfig, NgGridPluginFactory, $interval, $window, $http) {
    'use strict';

    var isAnalysisJobsReceived = false;
    var interval;
    var REFRESH_ANALYSIS_JOBS_TIME = 10000;
    $scope.analysisJobsModel = [];

    function getAnalysisJobs() {
        isAnalysisJobsReceived = false;
        FreightAnalysisServices.getAnalysisJobs({}, function(data) {
            $scope.analysisJobsModel = data;
            isAnalysisJobsReceived = true;
        });
    }

    getAnalysisJobs();

    function canAnalysisJobsReceive() {
        return isAnalysisJobsReceived && _.find($scope.analysisJobsModel, function(job) {
            return !job.completedDocId;
        });
    }

    var clearUploadElement = function() {
        var fileElem = angular.element('#upload');

        fileElem.wrap('<form>').parent('form').trigger('reset');
        fileElem.unwrap();

        if ($scope.$root.browserDetect.browser === 'Explorer') {
            fileElem.replaceWith(angular.element('#upload').clone(true));
        }
    };

    var pricingTypes = {
        "Blanket" : "BLANKET",
        "Blanket/CSP" : "BLANKET_CSP",
        "Customer Specific Pricing (CSP)" : "CSP",
        "Buy/Sell" : "BUY_SELL",
        "Benchmark" : "BENCHMARK",
        "Margin" : "MARGIN",
        "SMC3" : "SMC3"
    };

    var analysisData = {
        tariffs : [],
        uploadedDocId : undefined,
        uploadedFileName : undefined,
        completedFileName : undefined
    };

    var analysisReportStatusData = {
        analysisId : undefined,
        uploadedFileName : undefined,
        uploadedDocId : undefined,
        uploadDate : undefined,
        completedFileName : undefined,
        status : '',
        seqNumber : undefined
    };

    function getFileNameWithoutExtension(fileName) {
        var n = fileName.lastIndexOf(".");
        return n > -1 ? fileName.substr(0, n) : fileName;
    }

    function getAnalysisJobsByInterval() {
        if (canAnalysisJobsReceive()) {
            getAnalysisJobs();
        }
    }

    function stopInterval() {
        if (interval) {
            $interval.cancel(interval);
            interval = undefined;
        }
    }

    $scope.analysisData = angular.copy(analysisData);
    $scope.analysisReportStatusData = angular.copy(analysisReportStatusData);
    $scope.analysisData.blockIndirectServiceType = true;

    $scope.analysis = {
        selectedTariffsData : []
    };

    $scope.openConfirmRemoveDialog = function() {
        $scope.confirmRemoveDialog = true;
    };

    $scope.closeConfirmRemoveDialog = function() {
        $scope.confirmRemoveDialog = false;
    };

    $scope.tariffsGrid = {
        enableColumnResize : true,
        data : 'analysis.selectedTariffsData',
        multiSelect : false,
        columnDefs : [ {
            field : 'rateName',
            displayName : 'Rate name',
            width : '20%'
        }, {
            field : 'scac',
            displayName : 'SCAC',
            width : '15%'
        }, {
            field : 'effDate',
            displayName : 'Effective',
            cellFilter : 'date:appDateFormat',
            width : '17%'
        }, {
            field : 'expDate',
            displayName : 'Expires',
            cellFilter : 'date:appDateFormat',
            width : '13%'
        }, {
            field : 'pricingType',
            displayName : 'Pricing Type',
            width : '17%'
        }, {
            field : 'name',
            displayName : 'Customer',
            width : '14%'
        } ],
        plugins : [ NgGridPluginFactory.plsGrid() ]
    };

    $scope.$on("confirmSelectedTarrifs", function(event, args) {
        $scope.analysis.selectedTariffsData = angular.copy(args);
    });

    $scope.openTariffSelection = function() {
        $scope.showTariffSelection = true;
        $scope.$broadcast('openTariffSelection', $scope.analysis.selectedTariffsData);
    };

    $scope.closeTariffSelection = function() {
        $scope.showTariffSelection = false;
        $scope.editSelectedTariffsData = [];
    };

    $scope.uploadSubmit = function() {
        $scope.progressPanelOptions.showPanel = true;
        return true;
    };

    $scope.uploadFileName = function(rowValue) {
        $scope.showResultIcon = false;
        $scope.uploadModel.uploadFile = undefined;
        clearUploadElement();
        $scope.analysisData.uploadedDocId = rowValue.uploadedDocId;
        $scope.analysisData.uploadedFileName = rowValue.uploadedFileName;
        $scope.analysisData.analysisId = rowValue.analysisId;
        $scope.analysisData.completedFileName = getFileNameWithoutExtension($scope.analysisData.uploadedFileName);
        $('.bootstrap-filestyle input.span9').val(rowValue.uploadedFileName);

        $scope.$root.$emit('event:operation-success', $scope.analysisData.uploadedFileName + ' selected successfully');
    };

    function checkValidationStatus() {
        FreightAnalysisServices.checkValidationStatus({id: $scope.analysisData.uploadedDocId}).$promise.then(function(data) {
            if (data && data.data) {
                $scope.validatedDocId = data.data;
                clearInterval($scope.intervalId);
                var fileName = $scope.analysisData.uploadedFileName;
                switch ($scope.validatedDocId) {
                case -1:
                    $scope.uploadMessage = fileName + " upload failed! File format is incorrect.";
                    break;
                case -2: 
                    $scope.uploadMessage = fileName + " uploaded successfully.";
                    $scope.$root.$emit('event:operation-success', $scope.analysisData.uploadedFileName
                            + ' upload successfully');
                    break;
                default:
                    $scope.uploadMessage = fileName + " upload failed! Please click on this link to edit errors now.";
                    break;
                }
            }
         }, function(errResponse) {
             $scope.$root.$emit('event:application-error', 'An error occured during file validation ' + errResponse);
         });
    }

    function startExcelValidation() {
        $scope.validatedDocId = undefined;
        FreightAnalysisServices.startValidation({id: $scope.analysisData.uploadedDocId});
        $scope.intervalId = setInterval(checkValidationStatus, 1000);
    }

    $scope.$on('$routeChangeStart', function(next, current) {
        clearInterval($scope.intervalId);
    });

    $scope.uploadModel = {
        uploadUrl : urlConfig.shipment + '/customer/shipmentdocs/temp',
        uploadFile : null,
        setFile : function(element) {
            if (element && element.files.length) {
                if (element && element.files[0].name.match(/\.xls$|\.xlsx$/i)) {
                    if ((element.files && element.files.length) || element.value) {
                        $scope.$apply(function(scope) {
                            scope.uploadModel.uploadFile = element.value;
                        });
                        return;
                    }
                } else {
                    $scope.$root.$emit('event:application-error', 'Document upload failed!',
                            'Your file can not be uploaded. File format is not supported.');
                }
            }
        },
        uploadCallback : function(content, completed) {
            if (completed) {
                $scope.progressPanelOptions.showPanel = false;
            }
            if (completed && content) {
                var responseObj;
                try {
                    responseObj = JSON.parse(content);
                } catch (e) {
                    if (content.indexOf('HTTP Status 401') !== -1) {
                        $scope.$root.$broadcast('event:auth-loginRequired');
                    } else {
                        $scope.$root.$emit('event:application-error', 'Document upload failed!',
                                'Your file has not been uploaded! Please try again later.');
                    }
                    return;
                }
                if (responseObj && responseObj.tempDocId) {

                    $scope.analysisData.uploadedDocId = responseObj.tempDocId;

                    // get file name out of path
                    $scope.analysisData.uploadedFileName = $scope.uploadModel.uploadFile.split('\\').pop().split('/').pop();
                    $scope.analysisData.completedFileName = getFileNameWithoutExtension($scope.analysisData.uploadedFileName);
                    $scope.analysisData.analysisId = null;
                    $scope.uploadModel.uploadFile = undefined;

                    clearInterval($scope.intervalId);
                    startExcelValidation();
                    $scope.showResultIcon = true;
                    clearUploadElement();
                } else {
                    $scope.$root.$emit('event:application-error', 'Document upload failed!',
                            'Your file has not been uploaded! Please try again later.');
                }
            }
        }
    };

    $scope.selectedReportedItems = [];

    $scope.reportStatusGrid = {
        enableColumnResize : true,
        progressiveSearch : true,
        selectedItems : $scope.selectedReportedItems,
        data : 'analysisJobsModel',
        multiSelect : false,
        enableSorting : true,
        sortInfo : {
            fields : [ 'seqNumber' ],
            directions : [ 'desc' ]
        },
        columnDefs : [
                {
                    field : 'rateName',
                    cellClass : 'text-center',
                    displayName : 'Select as Upload File',
                    cellTemplate : '<button type="button" class="btn" data-ng-click="uploadFileName(row.entity)">Select</button>',
                    searchable : false,
                    width : '10%'
                },
                {
                    field : 'analysisId',
                    cellClass : 'text-center',
                    displayName : 'ID',
                    width : '10%'
                },
                {
                    field : 'uploadedFileName',
                    cellClass : 'text-center',
                    displayName : 'Uploaded File Name',
                    width : '20%'
                },
                {
                    field : 'uploadDate',
                    cellClass : 'text-center',
                    displayName : 'Uploaded Date',
                    cellFilter : 'date:appDateFormat',
                    width : '10%'
                },
                {
                    field : 'completedFileName',
                    cellClass : 'text-center',
                    displayName : 'Completed File Name',
                    width : '20%',
                    cellTemplate : '<div  data-ng-if="!row.entity.completedDocId" class=\"ngCellText\" ng-class=\"col.colIndex()\">'
                            + '<span ng-cell-text>{{row.entity.completedFileName}}</span></div>'
                            + '<div  data-ng-if="row.entity.completedDocId" class=\"ngCellText\" ng-class=\"col.colIndex()\">'
                            + '<a target="_blank" href="/restful/customer/shipmentdocs/{{row.entity.completedDocId}}'
                            + '?fileName={{row.entity.completedFileName}}.xlsx&download=true">{{row.entity.completedFileName}}.xlsx</a></div>'
                }, {
                    field : 'completedTariffCounts',
                    cellClass : 'text-center',
                    displayName : 'Progress',
                    width : '14%'
                }, {
                    field : 'status',
                    cellClass : 'text-center',
                    displayName : 'Status',
                    width : '15%'
                }, {
                    field : 'seqNumber',
                    visible : false
                } ],
        plugins : [ NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin() ]
    };

    $scope.deleteAnalysis = function() {
        FreightAnalysisServices.deleteAnalysis({
            id : $scope.reportStatusGrid.selectedItems[0].analysisId
        }, function(data) {
            _.pull($scope.analysisJobsModel, $scope.reportStatusGrid.selectedItems[0]);
            $scope.analysisJobsModel = angular.copy($scope.analysisJobsModel);
            $scope.closeConfirmRemoveDialog();
            $scope.reportStatusGrid.selectedItems[0] = [];
            $scope.reportStatusGrid.selectedItems.length = 0;
            $scope.$root.$emit('event:operation-success', 'Pricing processing was successfully removed.');
        }, function() {
            $scope.closeConfirmRemoveDialog();
            $scope.$root.$emit('event:application-error', 'Pricing processing remove failed.');
        });
    };

    $scope.getPricing = function() {
        if (!$scope.analysisData.uploadedDocId) {
            $scope.$root.$emit('event:application-error', 'Can\'t get pricing.',
                    'Please upload a file to process or select existing one.');
            return;
        }

        if (!$scope.analysis.selectedTariffsData || !$scope.analysis.selectedTariffsData.length) {
            $scope.$root.$emit('event:application-error', 'Can\'t get pricing.', 'At least one tariff should be selected.');
            return;
        }

        $scope.showResultIcon = false;

        var tariffs = _.omit(angular.copy($scope.analysis.selectedTariffsData), "effDate", "expDate", "carrierOrgId",
                "carrierName");
        $scope.analysisData.tariffs = _.map(tariffs, function(tariff) {
            tariff.pricingType = pricingTypes[tariff.pricingType];
            return tariff;
        });
        FreightAnalysisServices.addPricing($scope.analysisData, function() {
            $scope.analysisData.completedFileName = null;
            $scope.analysis.selectedTariffsData = [];
            $scope.analysisData.uploadedDocId = null;
            $scope.analysisData.uploadedFileName = null;
            getAnalysisJobs();
            angular.element('#upload').trigger('change');
        });
    };

    $scope.moveProcessTop = function() {
        FreightAnalysisServices.swapAnalysisJobs({
            id : $scope.reportStatusGrid.selectedItems[0].analysisId,
            step : true
        }, function(data) {
            $scope.$root.$emit('event:operation-success', 'Pricing processing have been Moved Up');
            getAnalysisJobs();
        }, function() {
            $scope.$root.$emit('event:application-error', 'Pricing processing haven\'t been Moved Up');
        });
    };

    $scope.moveProcessDown = function() {
        FreightAnalysisServices.swapAnalysisJobs({
            id : $scope.reportStatusGrid.selectedItems[0].analysisId,
            step : false
        }, function(data) {
            $scope.$root.$emit('event:operation-success', 'Pricing processing have been Moved down');
            getAnalysisJobs();
        }, function() {
            $scope.$root.$emit('event:application-error', 'Pricing processing haven\'t been Moved down');
        });
    };

    $scope.startProcessing = function() {
        FreightAnalysisServices.restartAnalysis({
            id : $scope.reportStatusGrid.selectedItems[0].analysisId
        }, function(data) {
            $scope.reportStatusGrid.selectedItems[0].status = 'Processing';
            $scope.reportStatusGrid.selectedItems[0] = [];
            $scope.reportStatusGrid.selectedItems.length = 0;
            $scope.$root.$emit('event:operation-success', 'Pricing processing have been started');
            getAnalysisJobs();
        }, function() {
            $scope.$root.$emit('event:application-error', 'Pricing processing haven\'t been started');
        });
    };

    $scope.pauseProcessing = function() {
        FreightAnalysisServices.pauseAnalysis({
            id : $scope.reportStatusGrid.selectedItems[0].analysisId
        }, function(data) {
            $scope.reportStatusGrid.selectedItems[0].status = 'Stopped';
            $scope.reportStatusGrid.selectedItems[0] = [];
            $scope.reportStatusGrid.selectedItems.length = 0;
            $scope.$root.$emit('event:operation-success', 'Pricing processing is paused');
            getAnalysisJobs();
        }, function() {
            $scope.$root.$emit('event:application-error', 'Pricing processing is not paused');
        });
    };

    interval = $interval(getAnalysisJobsByInterval, REFRESH_ANALYSIS_JOBS_TIME);

    $scope.$on('$destroy', function() {
        stopInterval();
    });

}]);

angular.module('plsApp').controller('AnalysisEditTariffsController', ['$scope', 'urlConfig', 'NgGridPluginFactory', 'ProfilesListService',
    function ($scope, urlConfig, NgGridPluginFactory, ProfilesListService) {
        'use strict';
    var initialPricingTypes = [ 'BLANKET', 'SMC3' ];
    var customerPricingTypes = [ 'BLANKET_CSP', 'CSP', 'BUY_SELL', 'BENCHMARK' ];
    var allPricingTypes = [];

    $scope.criteriaModel = {
        customer : {},
        criteria : {
            pricingGroup : 'CARRIER',
            dateType : 'EFFECTIVE',
            dateRange : 'NONE',
            status : 'ACTIVE',
            pricingTypes : initialPricingTypes
        }
    };

    function loadEditProfiles() {
        $scope.selectedTariffsGrid.selectedItems.length = 0;
        $scope.allTariffsGrid.selectedItems.length = 0;
        ProfilesListService.get({}, $scope.criteriaModel.criteria, function(profiles) {
            $scope.profileListData = _.reject(profiles, function(profile) {
                return $scope.criteriaModel.criteria.customer 
                ? _.findWhere($scope.editSelectedTariffsData, {
                    rateName : profile.rateName,
                    customerId : $scope.criteriaModel.criteria.customer}) !== undefined 
                : _.findWhere($scope.editSelectedTariffsData, {
                    rateName : profile.rateName}) !== undefined;
            });
        });
    }

    function loadInitialPricingProfiles() {
        $scope.profileListData = null;
        $scope.criteriaModel.criteria.customer = null;
        $scope.criteriaModel.criteria.pricingTypes = initialPricingTypes;
        loadEditProfiles();
    }

    $scope.$on("openTariffSelection", function(event, args) {
        loadInitialPricingProfiles();
        $scope.editSelectedTariffsData = angular.copy(args);
        $scope.selectedTariffsGrid.selectedItems.length = 0;
        $scope.allTariffsGrid.selectedItems.length = 0;
        $scope.criteriaModel.customer = {};
        angular.element('[data-pls-customer-lookup="criteriaModel.customer"]').controller('ngModel').$setViewValue('');
        angular.element('[data-pls-customer-lookup="criteriaModel.customer"]').controller('ngModel').$render();
    });

    $scope.confirmSelectedTarrifs = function() {
        $scope.$emit('confirmSelectedTarrifs', $scope.editSelectedTariffsData);
        $scope.closeTariffSelection();
    };

    $scope.editSelectedTariffsData = [];
    $scope.allTariffsSeletedItems = [];

    $scope.allTariffsGrid = {
        data : 'profileListData',
        progressiveSearch : true,
        multiSelect : true,
        enableColumnResize: true,
        selectedItems : $scope.allTariffsSeletedItems,
        columnDefs : [ {
            field : 'rateName',
            displayName : 'Rate name',
            width : '21%'
        }, {
            field : 'scac',
            displayName : 'SCAC',
            width : '18%'
        }, {
            field : 'effDate',
            displayName : 'Effective',
            cellFilter : 'date:appDateFormat',
            width : '18%'
        }, {
            field : 'expDate',
            displayName : 'Expires',
            cellFilter : 'date:appDateFormat',
            width : '18%'
        }, {
            field : 'pricingType',
            displayName : 'Pricing Type',
            width : '21%'
        } ],
        sortInfo: {
            fields: ['pricingType', 'rateName'],
            directions: ['desc', 'desc']
          },
        plugins : [ NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin() ]
    };

    $scope.selecteGridItemTariffs = [];

    $scope.selectedTariffsGrid = {
        data : 'editSelectedTariffsData',
        progressiveSearch : true,
        enableColumnResize: true,
        selectedItems : $scope.selecteGridItemTariffs,
        multiSelect : true,
        columnDefs : [ {
            field : 'rateName',
            displayName : 'Rate name',
            width : '20%'
        }, {
            field : 'scac',
            displayName : 'SCAC',
            width : '15%'
        }, {
            field : 'effDate',
            displayName : 'Effective',
            cellFilter : 'date:appDateFormat',
            width : '15%'
        }, {
            field : 'expDate',
            displayName : 'Expires',
            cellFilter : 'date:appDateFormat',
            width : '15%'
        }, {
            field : 'pricingType',
            displayName : 'Pricing Type',
            width : '17%'
        }, {
            field : 'name',
            displayName : 'Customer',
            width : '17%'
        } ],
        sortInfo: {
            fields: ['name', 'pricingType', 'rateName'],
            directions: ['desc', 'desc', 'desc']
          },
        plugins : [ NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin() ]
    };

    $scope.$watch('criteriaModel.customer.id', function(val, oldVal) {
        if (_.isUndefined(val)) {
            loadInitialPricingProfiles();
        }

        if (val && val !== oldVal) {
            $scope.criteriaModel.criteria.customer = val;
            $scope.criteriaModel.criteria.pricingTypes = customerPricingTypes;
            loadEditProfiles();
        }
    }, true);

    function tariffBelongsToCustomer(tariff) {
        var customer = $scope.criteriaModel.customer;
        return (customer && tariff.name === customer.name)
            || (!tariff.name && !customer);
    }

    function removeTariff(from, tariffs) {
        $.each(tariffs, function(key, tariff) {
            from.splice(from.indexOf(tariff), 1);
        });
    }

    $scope.removeSelectedTariff = function() {
        $.each($scope.selectedTariffsGrid.selectedItems, function(key, tariff) {
            if (tariffBelongsToCustomer(tariff)) {
                $scope.profileListData.push(tariff);
            }
        });
        removeTariff($scope.editSelectedTariffsData, $scope.selectedTariffsGrid.selectedItems);
        $scope.selectedTariffsGrid.selectedItems.length = 0;
    };

    $scope.addSelectedTariff = function() {
        if ($scope.criteriaModel.customer && $scope.criteriaModel.customer.id) {
            _.each($scope.allTariffsGrid.selectedItems, function(item) {
                item.customerId = $scope.criteriaModel.customer.id;
                item.name = $scope.criteriaModel.customer.name;
            });
        }
        $.each($scope.allTariffsGrid.selectedItems, function(key, tariff) {
            $scope.editSelectedTariffsData.push(tariff);
        });
        removeTariff($scope.profileListData, $scope.allTariffsGrid.selectedItems);
        $scope.allTariffsGrid.selectedItems.length = 0;
    };
}
]);