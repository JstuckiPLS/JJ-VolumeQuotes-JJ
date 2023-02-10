angular.module('plsApp').controller('ScacCodesCtrl', ['$scope', 'SimpleCarrierService', 'ApiTypeService', 'NgGridPluginFactory', 'CarrierService',
                                                      'AccessorialsMappingService',
    function ($scope, SimpleCarrierService, ApiTypeService, NgGridPluginFactory, CarrierService, AccessorialsMappingService) {
        'use strict';

        $scope.carrierInfoSearchCriteria = {
            scacCode: '',
            carrier: '',
            status: '',
            incomplete: true
        };

        $scope.shouldShowDetails = true;

        $scope.searchCarriersByCriteria = function () {
            $scope.selectedItems.length = 0;
            $scope.clearFields();
            $scope.showOrgServices = false;
            $scope.carrierLogoUrl = null;

            SimpleCarrierService.getCarriers($scope.carrierInfoSearchCriteria, function (response) {
                $scope.scacCodesList = response;
            }, function () {
                $scope.$emit('event:application-error', 'Failed to load carrier info!');
            });
        };

        function isSearchFormBlank() {
            return $scope.carrierInfoSearchCriteria.carrier.length === 0
                    && $scope.carrierInfoSearchCriteria.scacCode.length === 0
                    && $scope.carrierInfoSearchCriteria.status.length === 0;
        }

        $scope.$watch('carrierInfoSearchCriteria.scacCode', function (newValue) {
            $scope.carrierInfoSearchCriteria.scacCode = newValue;
            $scope.carrierInfoSearchCriteria.incomplete = (newValue.length > 0 && newValue.length < 3) || $scope.scacSearchForm.scacCode.$pristine ||
                    isSearchFormBlank();

        });

        $scope.$watch('carrierInfoSearchCriteria.carrier', function (newValue) {
            $scope.carrierInfoSearchCriteria.carrier = newValue;
            $scope.carrierInfoSearchCriteria.incomplete = (newValue.length > 0 && newValue.length < 3) || $scope.scacSearchForm.carrier.$pristine ||
                    isSearchFormBlank();
        });

        $scope.$watch('carrierInfoSearchCriteria.status', function (newValue) {
            $scope.carrierInfoSearchCriteria.status = newValue;
            if (newValue !== undefined && newValue.length > 0) {
                $scope.carrierInfoSearchCriteria.incomplete = false;
            }
        });

        // Model for grid
        $scope.scacCodesList = null;

        // Object that corresponds to selected row in grid
        $scope.currentlySelectedCarrier = null;

        // Indicate if API (related to selected carrier) can be edited. For "Edit button"
        $scope.editEnabled = false;

        $scope.carrierLogoFile = null;

        $scope.carrier = {};

        $scope.orgService = null;

        $scope.apiType = null;

        // Flag to display the UI forms as per selection
        $scope.showApiPanel = false;
        $scope.showEdiPanel = false;
        $scope.showManualPanel = false;
        $scope.accessorialList = [];

        $scope.disabledPanel = $scope.$root.isFieldRequired('PRICING_PAGE_VIEW') ? false : $scope.$root.isFieldRequired('SCAC_CODES_PAGE_VIEW');

        $scope.loadAllAccessorials = function() {
            if (_.isEmpty($scope.accessorialList)) {
                $scope.filteredAccessorialList = _.filter($scope.$root.accessorialTypes, function(type) {
                    return (type.applicableTo === 'LTL' && (type.accessorialGroup === 'PICKUP' || type.accessorialGroup === 'DELIVERY'))
                            || _.contains(['FS', 'HZC', 'ODM', 'HX', 'GX'], type.id);
                });
                _.forEach($scope.filteredAccessorialList, function(item) {
                    $scope.accessorialList.push({plsCode: item.id, description: item.description, group: item.accessorialGroup});
                });
            }
        };

        $scope.loadAccessorialMappingForCarrier = function() {
            if (!$scope.shouldShowDetails && $scope.currentlySelectedCarrier && $scope.currentlySelectedCarrier.carrierId) {
                $scope.loadAllAccessorials();
                $scope.accGridData = angular.copy($scope.accessorialList);
                _.forEach($scope.accGridData, function(item) {
                    item.carrierId = $scope.currentlySelectedCarrier.carrierId;
                    if (item.group) {
                        item.description += ' (' + item.group + ')';
                    }
                });
                AccessorialsMappingService.getAccessorialsMapping({carrierId: $scope.currentlySelectedCarrier.carrierId}, function(data) {
                    $scope.accMappingList = data;
                    _.forEach($scope.accGridData, function(item) {
                        _.forEach($scope.accMappingList, function(acc) {
                            if (item.plsCode === acc.plsCode) {
                                item.carrierCode = acc.carrierCode;
                                item.defaultAccessorial = acc.defaultAccessorial;
                                item.id = acc.id;
                            }
                        });
                    });
                }, function() {
                    $scope.$root.$emit('event:application-error', 'Mapping status', 'Failed to load Accessorial Mapping for carrier!');
                });
            }
        };

        $scope.saveMapping = function() {
            $scope.mappingToSave = _.filter($scope.accGridData, function(item) {
                return !_.isUndefined(item.carrierCode) || !_.isUndefined(item.defaultAccessorial);
            });
            var mappingToSaveCopy = angular.copy($scope.mappingToSave);
            _.forEach(mappingToSaveCopy, function(item) {
                item.description = item.description.split(' (')[0];
                delete item.group;
            });
            AccessorialsMappingService.saveAccessorialsMapping(mappingToSaveCopy, function() {
                $scope.$root.$emit('event:operation-success', 'Mapping status', 'Accessorials Mapping had been successfully saved!');
                $scope.loadAccessorialMappingForCarrier();
            }, function(err) {
                $scope.$root.$emit('event:application-error', 'Mapping status', 'Failed to save Accessorial Mapping for carrier!');
            });
        };

        $scope.accGridOptions = {
            data: 'accGridData',
            enableCellEditOnFocus: true,
            multiSelect: false,
            columnDefs: [{
                field: 'plsCode',
                displayName: 'PLS Acc Code',
                enableCellEdit: false,
                width: '20%'
            }, {
                field: 'description',
                displayName: 'PLS Acc Name',
                enableCellEdit: false,
                width: '35%'
            }, {
                field: 'carrierCode',
                displayName: 'Carrier Acc Code',
                width: '25%',
                headerCellTemplate: 'pages/cellTemplate/info-icon-header-cell.html',
                infoText: 'Codes can be separated by comma.<br/>If more than one code is provided,'
                    + '<br/>then only first one will be used for carrier request.'
            }, {
                field: 'defaultAccessorial',
                displayName: 'By default',
                cellTemplate: '<input type="checkbox" class="centeredElement" data-ng-model="row.entity.defaultAccessorial">',
                enableCellEdit: false,
                width: '15%'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        // Currently selected in grid carrier
        var selectedGridItem = null;

        // Selected grid items
        $scope.selectedItems = [];
        $scope.gridOptions = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            multiSelect: false,
            data: 'scacCodesList',
            afterSelectionChange: function () {
                if ($scope.gridOptions.selectedItems.length === 0) {
                    return;
                }

                var selectedObj = $scope.gridOptions.selectedItems[0];

                if ($scope.currentlySelectedCarrier === selectedObj) {
                    return;
                }

                $scope.currentlySelectedCarrier = selectedObj;
                $scope.loadAccessorialMappingForCarrier();
                $scope.clearFields();
                $scope.showOrgServices = false;
                $scope.updateCarrierLogo();
            },
            columnDefs: [{
                field: 'scac',
                displayName: 'SCAC'
            }, {
                field: 'name',
                displayName: 'Carrier'
            }, {
                field: 'status',
                displayName: 'Status'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        // URL used to receive from server LOGO of selected carrier
        $scope.carrierLogoUrl = null;

        /**
         * Updates URL for carrier logo to new value. Should be called after selecting new carrier.
         */
        $scope.updateCarrierLogo = function () {
            $scope.carrierLogoUrl = '/restful/organization/' + $scope.currentlySelectedCarrier.carrierId + '/logo?' + new Date().getTime();
        };

        $scope.saveOrgServices = function () {
            var carrier = angular.copy($scope.carrier);

            carrier.rejectedCustomers = _.map($scope.carrier.rejectedCustomers, function (item) {
                return {
                    id: item.customer.id,
                    name: item.customer.name
                };
            });

            CarrierService.saveCarrier(carrier, function () {
                $scope.$root.$emit('event:operation-success', 'Organization services were successfully saved');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Failed to save carrier information!');
            });
        };

        $scope.clearFields = function () {
            $scope.showApiPanel = false;
            $scope.showEdiPanel = false;
            $scope.showManualPanel = false;

            $scope.orgService = {};
            $scope.apiType = {};
        };

        $scope.editApiDetails = function (category) {
            $scope.showEdiPanel = false;
            $scope.showApiPanel = true;
            $scope.showManualPanel = false;
            $scope.apiType = {};

            ApiTypeService.getApiType({orgId: selectedGridItem.carrierId, category: category}, function (response) {
                if (response && response.length > 0) {
                    $scope.apiType = response[0];
                }

                if (!$scope.apiType.id) {
                    $scope.apiType.carrierOrgId = selectedGridItem.carrierId;
                    $scope.apiType.wsType = 'SOAP';
                    $scope.apiType.apiCategory = category;
                    $scope.apiType.apiType = category;

                    if (category === 'DOCUMENT') {
                        $scope.apiType.apiType = 'ALL';
                    }

                    $scope.apiType.apiOrgType = 'CARRIER';
                    $scope.apiType.status = 'ACTIVE';
                }
            }, function () {
                $scope.$root.$emit('event:application-error', 'Failed to load API details!');
            });
        };

        $scope.editManualDetails = function () {
            $scope.showEdiPanel = false;
            $scope.showApiPanel = false;
            $scope.showManualPanel = true;
        };

        $scope.manualEmailNeeded = function () {
            if (!$scope.orgService) {
                return false;
            }

            return $scope.orgService.pickup === 'MANUAL' && !$scope.orgService.manualTypeEmail;
        };

        /**
         * Save/update call from the api details form.
         */
        $scope.saveApiDetails = function () {
            ApiTypeService.saveApiType($scope.apiType, function (response) {
                $scope.apiType = response;
            }, function () {
                $scope.$root.$emit('event:application-error', 'Saving API Details Failed!');
            });
        };

        $scope.editEdiDetails = function () {
            $scope.showEdiPanel = true;
            $scope.showApiPanel = false;
            $scope.showManualPanel = false;
            alert("yet to be implemented");
        };

        $scope.saveEdiDetails = function () {
            alert("yet to be implemented");
        };

        /**
         * Uploads new logo to server.
         */
        $scope.onUpdateLogoButtonClick = function () {
            if ($scope.carrierLogoFile === undefined ||
                    $scope.carrierLogoFile.files === undefined ||
                    $scope.carrierLogoFile.files.length === 0) {
                return;
            }

            $scope.fileSentOk = false;
            $scope.uploadProgressMsg = null;

            var formData = new FormData();
            formData.append('logo', $scope.carrierLogoFile.files[0]);

            var xhr = new XMLHttpRequest();

            if (xhr.upload) {
                xhr.upload.onprogress = function (e) {
                    var done = e.position || e.loaded, total = e.totalSize || e.total;
                    $scope.uploadProgressMsg = 'Progress: ' + done + ' / ' + total + ' = ' + (Math.floor(done / total * 1000) / 10) + '%';
                };
            }

            xhr.onreadystatechange = function (e) {
                // request finished, response is ready
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        $scope.$apply(function () {
                            $scope.fileSentOk = true;
                            $scope.carrierLogoFile.value = '';
                            $scope.updateCarrierLogo();
                        });
                    } else if (xhr.status === 401) {
                        $scope.$root.$broadcast('event:auth-loginRequired');
                    } else {
                        $scope.$root.$emit('event:application-error', 'Logo updated failed!', 'Can\'t update logo');
                    }
                }
            };

            xhr.open('post', '/restful/organization/' + $scope.currentlySelectedCarrier.carrierId + '/logo', true);
            xhr.send(formData);
        };

        function prepareData(data) {
            return _.map(data, function (item) {
                return {
                    customer: {
                        id: item.id,
                        name: item.name
                    }
                };
            });
        }

        $scope.$watch('selectedItems[0]', function (newValue) {
            if (newValue) {
                $scope.showOrgServices = true;
                $scope.clearFields();

                if ($scope.gridOptions.selectedItems.length === 0) {
                    return;
                }

                selectedGridItem = $scope.gridOptions.selectedItems[0];

                CarrierService.getCarrier({carrierId: selectedGridItem.carrierId}, function (response) {
                    $scope.carrier = angular.copy(response);
                    $scope.carrier.rejectedCustomers = angular.copy(prepareData(response.rejectedCustomers));
                });
            }
        });

        $scope.rejectedEdiGrid = {
            selectedItems: $scope.selectedCustomers,
            multiSelect: false,
            data: 'carrier.rejectedCustomers',
            columnDefs: [{
                field: 'customer.name',
                displayName: 'Customers'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.editRejectedCustomersList = function () {
            $scope.$broadcast('openAssignedCustomersDialog');
        };
    }
]);