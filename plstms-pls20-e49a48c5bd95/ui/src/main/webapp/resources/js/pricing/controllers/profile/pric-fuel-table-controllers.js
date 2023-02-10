angular.module('plsApp').controller('FuelTableCtrl', ['$scope', 'GetLtlFuelSurchargeService', 'LtlFuelSurchargeCopyFromService',
    'LtlFuelSurchargeSaveService', 'LtlFuelSurchargeUpdateStatusService', 'ExportService', 'ExportDataBuilder', 'NgGridPluginFactory',
    function ($scope, GetLtlFuelSurchargeService, LtlFuelSurchargeCopyFromService, LtlFuelSurchargeSaveService,
              LtlFuelSurchargeUpdateStatusService, ExportService, ExportDataBuilder, NgGridPluginFactory) {
        'use strict';

        $scope.selectedRateToCopy = null;

        $scope.onShowImportDialog = function () {
            $scope.fileSentOk = false;
            $scope.uploadProgressMsg = null;
            $scope.incorrectData = false;
        };

        // Import LtlFuelSurcharge records from Excel document
        $scope.importSurchargeFile = function () {
            if ($scope.fuelSurchargeFile === undefined || $scope.fuelSurchargeFile.files === undefined ||
                    $scope.fuelSurchargeFile.files.length === 0) {
                return;
            }

            $scope.fileSentOk = false;
            $scope.uploadProgressMsg = null;

            var formData = new FormData();

            formData.append('upload', $scope.fuelSurchargeFile.files[0]);
            formData.append('profileDetailId', $scope.profileDetailId);

            var xhr = new XMLHttpRequest();

            if (xhr.upload) {
                xhr.upload.onprogress = function (e) {
                    var done = e.position || e.loaded, total = e.totalSize || e.total;
                    $scope.uploadProgressMsg = 'Progress: ' + done + ' / ' + total + ' = ' + (Math.floor(done / total * 1000) / 10) + '%';
                };
            }

            xhr.onreadystatechange = function (e) {
                if (4 === this.readyState) {
                    if (xhr.status === 200) {
                        $scope.fuelSurchargeFile.value = '';
                        $scope.fuelTableList = $scope.fuelTableList.concat(angular.fromJson(e.target.response));
                        $scope.importResult = angular.fromJson(e.target.response);
                        $scope.fileSentOk = $scope.importResult.validation.ok;
                        $scope.incorrectHeader = $scope.importResult.validation.incorrectHeader;
                        $scope.incorrectData = $scope.importResult.validation.incorrectData;
                        $scope.$digest();

                        if ($scope.fileSentOk) {
                            $scope.fuelTableList = $scope.importResult.result;
                            $scope.$root.$emit('event:operation-success', 'File has been uploaded!', 'Your file has been successfully uploaded!');
                        }

                        if ($scope.incorrectHeader) {
                            $scope.$root.$emit('event:application-error',
                                    'Could not import, header row contains incorrect or missing names. See help for more information');
                        }

                        if ($scope.incorrectData) {
                            $scope.$root.$emit('event:application-error',
                                    'Could not import, data is not numeric or is an unacceptable value. See help for more information');
                        }
                    } else if (xhr.status === 401) {
                        $scope.$root.$broadcast('event:auth-loginRequired');
                    } else {
                        $scope.$root.$emit('event:application-error', 'File upload failed!', 'Can\'t upload file');
                    }
                }
            };

            xhr.open('post', '/restful/ltlfuelsurcharge/import', true);
            xhr.send(formData);
        };

        $scope.fuelTableList = null;
        var minRateValidationArray = [];
        var maxRateValidationArray = [];
        $scope.minRateExists = false;
        $scope.maxRateExists = false;
        $scope.rangeValidationFailed = false;

        var populateValidationArrays = function (sourceArray) {
            minRateValidationArray = [];
            maxRateValidationArray = [];

            _.each(sourceArray, function (item) {
                minRateValidationArray.push(Number(item.minRate));
                maxRateValidationArray.push(Number(item.maxRate));
            });

            minRateValidationArray = _.sortBy(minRateValidationArray, function (item) {
                return item;
            });

            maxRateValidationArray = _.sortBy(maxRateValidationArray, function (item) {
                return item;
            });
        };

        // Load list of active fuel surcharge records.
        $scope.loadFuelSurchargeList = function () {
            GetLtlFuelSurchargeService.getActive({profileDetailId: $scope.profileDetailId}, function (responce) {
                $scope.fuelTableList = responce;
                populateValidationArrays($scope.fuelTableList);
            });
        };

        $scope.loadFuelSurchargeList();

        var validateMinRate = function (value) {
            return _.indexOf(minRateValidationArray, Number(value), true) === _.lastIndexOf(minRateValidationArray, Number(value));
        };

        var validateMaxRate = function (value) {
            return _.indexOf(maxRateValidationArray, Number(value), true) === _.lastIndexOf(maxRateValidationArray, Number(value));
        };

        var isRateOverlapped = function (rate1, rate2) {
            return (rate1.minRate >= rate2.minRate && rate1.minRate <= rate2.maxRate)
                    || (rate1.maxRate >= rate2.minRate && rate1.maxRate <= rate2.maxRate)
                    || (rate2.minRate >= rate1.minRate && rate2.minRate <= rate1.maxRate)
                    || (rate2.maxRate >= rate1.minRate && rate2.maxRate <= rate1.maxRate);
        };

        var isRateOverlapsAnotherRates = function (rate) {
            return _.find($scope.fuelTableList, function (item) {
                return item !== rate && isRateOverlapped(item, rate);
            });
        };

        var minRateValidationFailed = function () {
            $scope.minRateExists = true;
            $scope.$root.$emit('event:application-error', "Min. Rate value should be unique");
        };

        var maxRateValidationFailed = function () {
            $scope.maxRateExists = true;
            $scope.$root.$emit('event:application-error', "Max. Rate value should be unique");
        };

        var rangeValidationFailed = function () {
            $scope.rangeValidationFailed = true;
            $scope.$root.$emit('event:application-error', "Range of Min. and Max. Rate values should be unique");
        };

        $scope.updateMinRate = function (value) {
            $scope.validationPassed = true;
            $scope.minRateExists = false;
            $scope.rangeValidationFailed = false;

            populateValidationArrays($scope.fuelTableList);

            if (!validateMinRate(value)) {
                $scope.validationPassed = false;
                minRateValidationFailed();
            } else if (!_.isNumber(value)) {
                $scope.validationPassed = false;
                $scope.$root.$emit('event:application-error', "Min. Rate is not a number");
            } else {
                populateValidationArrays($scope.fuelTableList);
            }
        };

        $scope.updateMaxRate = function (value) {
            $scope.validationPassed = true;
            $scope.maxRateExists = false;
            $scope.rangeValidationFailed = false;

            populateValidationArrays($scope.fuelTableList);

            if (!validateMaxRate(value)) {
                $scope.validationPassed = false;
                maxRateValidationFailed();
            } else if (!_.isNumber(value)) {
                $scope.validationPassed = false;
                $scope.$root.$emit('event:application-error', "Max. Rate is not a number");
            } else {
                populateValidationArrays($scope.fuelTableList);
            }
        };

        $scope.selectedItems = [];
        $scope.itemsToBeDeactivated = [];

        var deactivate = function (item) {
            $scope.itemsToBeDeactivated.push(item.id);
        };

        $scope.gridOptions = {
            enableCellEdit: true,
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            enableSorting: true,
            sortInfo: {fields: ['surcharge'], directions: ['desc']},
            data: 'fuelTableList',
            afterSelectionChange: function (rowItem) {
                $scope.currentlySelectedRowElement = rowItem;
            },
            multiSelect: false,
            columnDefs: [{
                field: 'minRate',
                displayName: 'Min. Rate',
                cellFilter: 'plsCurrency',
                cellTemplate: '<div class="ngCellText" ng-class="getRowNgClass(col,row.entity)">'
                + '<span ng-cell-text>{{row.getProperty(col.field) | plsCurrency}}</span></div>',
                editableCellTemplate: "<input data-pls-number='fuelCost' data-pls-blur=\"updateMinRate(row.entity.minRate)\" ng-class=\""
                + "'colt' + col.index\" ng-input=\"row.entity.minRate\" ng-model=\"row.entity.minRate\" class=\"ng-valid colt1 ng-dirty\""
                + " required ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">"
            }, {
                field: 'maxRate',
                displayName: 'Max. Rate',
                cellFilter: 'plsCurrency',
                cellTemplate: '<div class="ngCellText" ng-class="getRowNgClass(col,row.entity)">'
                + '<span ng-cell-text>{{row.getProperty(col.field) | plsCurrency}}</span></div>',
                editableCellTemplate: "<input data-pls-number='fuelCost' data-pls-blur=\"updateMaxRate(row.entity.maxRate)\" ng-class=\"'colt'"
                + " + col.index\" ng-input=\"row.entity.maxRate\" ng-model=\"row.entity.maxRate\" class=\"ng-valid colt1 ng-dirty\" required "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">"
            }, {
                field: 'surcharge',
                displayName: 'Surcharge',
                cellFilter: 'percentage',
                cellTemplate: '<div class="ngCellText" ng-class="getRowNgClass(col,row.entity)">'
                + '<span ng-cell-text>{{row.getProperty(col.field) | percentage}}</span></div>',
                editableCellTemplate: "<input data-pls-number='cost' data-pls-blur=\"updateEntity(row.entity)\" ng-class=\"'colt' + col.index\" "
                + "ng-input=\"row.entity.surcharge\" ng-model=\"row.entity.surcharge\" class=\"ng-valid colt1 ng-dirty\" required "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">"
            }
            ],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            progressiveSearch: true
        };

        // download excel document with active fuel surcharge records for current profile details.
        $scope.onExport = function () {
            var fileFormat = 'LTL_FUEL_SURCHARGE_{0,date,' + $scope.$root.exportFileNameDateFormat + '}.xlsx';
            var sheetName = "Fuel Surcharges";

            var fuelSurchargeEntities = _.map($scope.gridOptions.ngGrid.filteredRows, function (item) {
                return item.entity;
            });

            var exportData = ExportDataBuilder.buildExportData($scope.gridOptions, fuelSurchargeEntities, fileFormat, sheetName);

            ExportService.exportData(exportData);
        };

        $scope.onNewRecord = function () {
            $scope.fuelTableList.push({id: null, ltlPricingProfileId: $scope.profileDetailId, minRate: 0, maxRate: 0, surcharge: 0, active: true});
        };

        $scope.getRowNgClass = function (col, entity) {
            if ($scope.fuelTableList.indexOf(entity) === $scope.errorRowNum) {
                return "grid-error-row " + col.colIndex();
            } else {
                return col.colIndex();
            }
        };

        $scope.validationPassed = true;

        var onDeleteRecord = function () {
            if (!_.isEmpty($scope.selectedItems[0])) {
                deactivate($scope.selectedItems[0]);
                $scope.fuelTableList.splice($scope.fuelTableList.indexOf($scope.selectedItems[0]), 1);
                $scope.$root.$emit('event:operation-success', 'Record was successfully inactivated.');
            }

            $scope.validationPassed = true;
            populateValidationArrays($scope.fuelTableList);
            $scope.minRateExists = false;

            var itemWithWrongMinRate = _.find($scope.fuelTableList, function (item) {
                return !validateMinRate(item.minRate);
            });

            if (itemWithWrongMinRate) {
                minRateValidationFailed();
                $scope.errorRowNum = $scope.fuelTableList.indexOf(itemWithWrongMinRate);
                $scope.validationPassed = false;
                return;
            }

            $scope.maxRateExists = false;

            var itemWithWrongMaxRate = _.find($scope.fuelTableList, function (item) {
                return !validateMaxRate(item.maxRate);
            });

            if (itemWithWrongMaxRate) {
                maxRateValidationFailed();
                $scope.errorRowNum = $scope.fuelTableList.indexOf(itemWithWrongMaxRate);
                $scope.validationPassed = false;
                return;
            }

            $scope.currentlySelectedRowElement = null;
        };

        // Inactivate all active fuel surcharge entities in current profile and copy
        // active fuel surcharge entities from profile with specified ID.
        var cloneByProfileId = function (profileId) {
            LtlFuelSurchargeCopyFromService.copyFrom({
                copyToProfileId: $scope.profileDetailId,
                copyFromProfileId: profileId
            }, function () {
                $scope.loadFuelSurchargeList();
                $scope.$root.$emit('event:operation-success', 'Fuel table was successfully copied');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Failed to copy Fuel Table!');
            });
        };

        $scope.openCopyingDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', confirmButtonLabel: 'Copy',
                message: 'Copying will override all current fuel table data created. Do you want to continue?', okFunction: $scope.okCopyDlgClick
            });
        };

        $scope.openDeleteDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', confirmButtonLabel: 'Delete',
                message: 'Are you sure you want to delete selected record?', okFunction: $scope.okDeleteDlgClick
            });
        };

        $scope.okCopyDlgClick = function () {
            cloneByProfileId($scope.selectedRateToCopy);
        };

        $scope.okDeleteDlgClick = function () {
            onDeleteRecord();
        };

        var validateFuelSurchargeList = function () {
            var validate = true;

            _.each($scope.fuelTableList, function (item) {
                if (!item.minRate || !item.maxRate || !item.surcharge) {
                    validate = false;
                }
            });

            if (!validate) {
                $scope.$root.$emit('event:application-error', 'Fuel Table validation failed!', 'Min Rate, Max Rate and Surcharge are required.');
                return false;
            }

            var incorrectSurcharge = _.find($scope.fuelTableList, function (item) {
                return item.surcharge > 100;
            });

            if (incorrectSurcharge) {
                $scope.errorRowNum = $scope.fuelTableList.indexOf(incorrectSurcharge);
                $scope.$root.$emit('event:application-error', 'Fuel Table validation failed!', 'Surcharge cannot be greater than 100 %');
                return false;
            }

            var minRateWhichNotANumber = _.find($scope.fuelTableList, function (item) {
                return !_.isNumber(item.minRate);
            });

            if (minRateWhichNotANumber) {
                $scope.errorRowNum = $scope.fuelTableList.indexOf(minRateWhichNotANumber);
                $scope.$root.$emit('event:application-error', 'Fuel Table validation failed!', 'Min Rate is not a number.');
                return false;
            }

            var maxRateWhichNotANumber = _.find($scope.fuelTableList, function (item) {
                return !_.isNumber(item.maxRate);
            });

            if (maxRateWhichNotANumber) {
                $scope.errorRowNum = $scope.fuelTableList.indexOf(maxRateWhichNotANumber);
                $scope.$root.$emit('event:application-error', 'Fuel Table validation failed!', 'Max Rate is not a number.');
                return false;
            }

            var overlappedRate = _.find($scope.fuelTableList, function (item) {
                return isRateOverlapsAnotherRates(item);
            });

            if (overlappedRate) {
                $scope.errorRowNum = $scope.fuelTableList.indexOf(overlappedRate);
                $scope.$root.$emit('event:application-error', 'Fuel Table validation failed!', 'Rate overlaps another.');
                return false;
            }

            return true;
        };

        $scope.saveFuelSurchargeList = function () {
            $scope.validationPassed = true;

            if (!validateFuelSurchargeList()) {
                $scope.validationPassed = false;
                return;
            }

            if ($scope.itemsToBeDeactivated && $scope.itemsToBeDeactivated.length !== 0) {
                LtlFuelSurchargeUpdateStatusService.inactivate({
                    profileDetailId: $scope.profileDetailId
                }, $scope.itemsToBeDeactivated, function (response) {
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Failed to inactivate Fuel record!');
                });
            }

            LtlFuelSurchargeSaveService.saveAll($scope.fuelTableList, function () {
                $scope.errorRowNum = undefined;
                $scope.$root.$emit('event:operation-success', 'Fuel Table was successfully saved.');
                $scope.loadFuelSurchargeList();
            }, function () {
                $scope.$root.$emit('event:application-error', 'Failed to save Fuel Table!');
            });
        };
    }
]);
