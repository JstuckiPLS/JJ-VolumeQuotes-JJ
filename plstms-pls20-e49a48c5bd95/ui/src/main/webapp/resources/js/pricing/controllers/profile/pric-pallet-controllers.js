angular.module('plsApp').controller('PalletPricingCtrl', ['$scope', '$routeParams', 'PricingDetailsDictionaryService', 'PalletPricingService',
    'ZonesService', 'NgGridPluginFactory', 'PalletePricingCopyFromService', 'ProfileDetailsService',
    function ($scope, $routeParams, PricingDetailsDictionaryService, PalletPricingService, ZonesService, NgGridPluginFactory,
              PalletePricingCopyFromService, ProfileDetailsService) {
        'use strict';

        $scope.activeTab = true;
        $scope.ltlCostTypes = [];

        $scope.pricingProfileType = 'BLANKET';

        if ($scope.pricingType === 'BUY_SELL') {
            if ($scope.profileDetails.ltlPricingType === 'BLANKET') {
                $scope.pricingProfileType = 'BLANKET';
            } else if ($scope.profileDetails.ltlPricingType === 'BLANKET_CSP') {
                $scope.pricingProfileType = 'BLANKET_CSP';
            } else if ($scope.profileDetails.ltlPricingType === 'CSP') {
                $scope.pricingProfileType = 'CSP';
            } else if ($scope.profileDetails.ltlPricingType === 'MARGIN') {
                $scope.pricingProfileType = 'MARGIN';
            } else if ($scope.profileDetails.ltlPricingType === 'BENCHMARK') {
                $scope.pricingProfileType = 'BENCHMARK';
            }
        } else if ($scope.pricingType === 'BUY') {
            $scope.pricingProfileType = 'BUY';
        } else if ($scope.pricingType === 'SELL') {
            $scope.pricingProfileType = 'SELL';
        }

        $scope.loadActiveDetails = function () {
            $scope.activeTab = true;

            PalletPricingService.active({
                detail: $scope.profileDetailId
            }, function (response) {
                $scope.priceDetails = response;
            });
        };

        $scope.loadArchivedDetails = function () {
            $scope.activeTab = false;

            PalletPricingService.inactive({
                detail: $scope.profileDetailId
            }, function (response) {
                $scope.priceDetails = response;
            });
        };

        PricingDetailsDictionaryService.get(function (response) {
            $scope.detailsDictionary = response;

            if ($scope.profileDetails.profileDetails[0].carrierType === 'SMC3') {
                $scope.ltlCostTypes = _.filter($scope.detailsDictionary.ltlCostTypes, function (item) {
                    return item.value === 'DC';
                });
            } else if ($scope.profileDetails.profileDetails[0].carrierType === 'MANUAL') {
                $scope.ltlCostTypes = _.reject($scope.detailsDictionary.ltlCostTypes, function (item) {
                    return item.value === 'DC';
                });
            }
        });

        ZonesService.dictionary({profile: $scope.profileDetailId}, function (response) {
                    $scope.zonesDictionary = response;
                }
        );

        $scope.loadActiveDetails();

        $scope.addNewEmptyRecord = function () {
            var PriceDetailRecord = {};
            PriceDetailRecord.profileDetailId = $scope.profileDetailId;
            PriceDetailRecord.status = 'ACTIVE';
            PriceDetailRecord.serviceType = 'DIRECT';
            PriceDetailRecord.isExcludeFuel = false;
            $scope.priceDetails.push(PriceDetailRecord);
        };

        $scope.activeGridOptions = {
            enableCellEdit: true,
            multiSelect: false,
            enableColumnResize: true,
            enableCellSelection: true,
            enableRowSelection: false,
            data: 'priceDetails',
            columnDefs: [{
                enableCellEdit: false,
                field: 'id',
                displayName: 'ID',
                width: '3%',
                searchable: false
            }, {
                field: 'zoneFrom',
                displayName: 'From',
                cellTemplate: "<select data-ng-cell-input data-ng-class=\"'colt' + $index\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\" "
                + "ng-options=\"type.value as  type.label for type in zonesDictionary\" ng-model=\"COL_FIELD\"></select>",
                width: '10%'
            }, {
                field: 'zoneTo',
                displayName: 'To',
                cellTemplate: "<select data-ng-cell-input data-ng-class=\"'colt' + $index\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\" "
                + "ng-options=\"type.value as  type.label for type in zonesDictionary\" ng-model=\"COL_FIELD\"></select>",
                width: '10%'
            }, {
                field: 'minQuantity',
                displayName: 'Min Qty',
                cellTemplate: "<input data-ng-class=\"'colt' + col.index\" data-ng-input=\"COL_FIELD\" data-pls-number data-ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">",
                width: '7%'
            }, {
                field: 'maxQuantity',
                displayName: 'Max Qty',
                cellTemplate: "<input data-ng-class=\"'colt' + col.index\" data-ng-input=\"COL_FIELD\" data-pls-number data-ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">",
                width: '7%'
            }, {
                field: 'unitCost',
                displayName: 'Amount',
                cellTemplate: "<input data-ng-class=\"'colt' + col.index\" data-ng-input=\"COL_FIELD\" data-pls-number data-ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">",
                width: '6%',
                visible: ($scope.pricingProfileType !== 'MARGIN')
            }, {
                field: 'costType',
                displayName: 'Amt Type',
                cellTemplate: "<select data-ng-cell-input data-ng-class=\"'colt' + $index\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\" "
                + "ng-options=\"type.value as  type.label for type in ltlCostTypes\" ng-model=\"COL_FIELD\"></select>",
                width: '7%',
                visible: ($scope.pricingProfileType !== 'MARGIN')
            }, {
                field: 'isExcludeFuel',
                displayName: 'Exclude Fuel',
                cellTemplate: "<input type=\"checkbox\" data-ng-class=\"'colt' + col.index\" data-ng-model=\"COL_FIELD\""
                + "ng-disabled=\"profileDetails.pricingType.ltlPricingType === 'BLANKET_CSP'\">",
                width: '7%',
                visible: ($scope.pricingProfileType !== 'MARGIN')
            }, {
                field: 'costApplMinWt',
                displayName: 'Min Wt',
                cellTemplate: "<input data-ng-class=\"'colt' + col.index\" data-ng-input=\"COL_FIELD\" data-pls-number data-ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">",
                width: '5%'
            }, {
                field: 'costApplMaxWt',
                displayName: 'Max Wt',
                cellTemplate: "<input data-ng-class=\"'colt' + col.index\" data-ng-input=\"COL_FIELD\" data-pls-number data-ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">",
                width: '5%'
            }, {
                field: 'marginPercent',
                displayName: 'Margin %',
                cellTemplate: "<input data-ng-class=\"'colt' + col.index\" data-ng-input=\"COL_FIELD\" data-pls-number data-ng-model=\"COL_FIELD\">",
                width: '6%',
                visible: ($scope.pricingProfileType !== 'BLANKET'
                && $scope.pricingProfileType !== 'BUY' && $scope.pricingProfileType !== 'BENCHMARK')
            }, {
                field: 'transitTime',
                displayName: 'Transit',
                cellTemplate: "<input data-ng-class=\"'colt' + col.index\" data-ng-input=\"COL_FIELD\" data-pls-number data-ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">",
                width: '6%',
                visible: ($scope.pricingProfileType !== 'BENCHMARK' && $scope.pricingProfileType !== 'MARGIN')
            }, {
                field: 'serviceType',
                displayName: 'Type',
                cellTemplate: "<select data-ng-cell-input data-ng-class=\"'colt' + $index\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\""
                + "ng-options=\"type.value as  type.label for type in detailsDictionary.ltlServiceTypes\" ng-model=\"COL_FIELD\"></select>",
                width: '5%',
                visible: ($scope.pricingProfileType !== 'BENCHMARK' && $scope.pricingProfileType !== 'MARGIN')
            }, {
                field: 'effDate',
                displayName: 'Effective',
                cellTemplate: "<div class=\"input-append\" data-ng-cell-input data-ng-class=\"'colt' + $index\">"
                + "<input type=\"text\" data-ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\""
                + "data-max-date = \"row.entity.expDate\""
                + "data-pls-datepicker class=\"input-small\" />"
                + "<button type=\"button\" class=\"btn\" data-toggle=\"datepicker\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">"
                + "<i class=\"icon-calendar\"></i>"
                + "</button>"
                + "</div>",
                width: '9%'
            }, {
                field: 'expDate',
                displayName: 'Expires',
                cellTemplate: "<div class=\"input-append\" data-ng-cell-input data-ng-class=\"'colt' + $index\">"
                + "<input type=\"text\" data-ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\""
                + "data-min-date = \"row.entity.effDate\""
                + "data-pls-datepicker class=\"input-small\" />"
                + "<button type=\"button\" class=\"btn\" data-toggle=\"datepicker\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">"
                + "<i class=\"icon-calendar\"></i>"
                + "</button>"
                + "</div>",
                width: '9%'
            }, {
                field: '',
                displayName: '',
                enableCellEdit: false,
                cellTemplate: "<div class=\"ngSelectionCell\">"
                + "<a href=\"\" data-ng-hide=\"row.entity.id===undefined\" data-ng-click=\"inactivateRecord(row.entity.id)\">"
                + "Inactivate</a>"
                + "</div>",
                width: '5%',
                searchable: false,
                visible: ($scope.pricingProfileType !== 'BLANKET_CSP')
            }],
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin()],
            useExternalSorting: false,
            progressiveSearch: true
        };

        $scope.inactiveGridOptions = {
            multiSelect: false,
            enableColumnResize: true,
            data: 'priceDetails',
            columnDefs: [{
                enableCellEdit: false,
                field: 'id',
                displayName: 'ID',
                width: '3%',
                searchable: false
            }, {
                field: 'zoneFrom',
                displayName: 'From',
                cellTemplate: "<label class=\"label-middle\">{{getZoneDescription(row.entity.zoneFrom)}}</label>",
                width: '10%'
            }, {
                field: 'zoneTo',
                displayName: 'To',
                cellTemplate: "<label class=\"label-middle\">{{getZoneDescription(row.entity.zoneTo)}}</label>",
                width: '10%'
            }, {
                field: 'minQuantity',
                displayName: 'Min Qty',
                width: '7%'
            }, {
                field: 'maxQuantity',
                displayName: 'Max Qty',
                width: '7%'
            }, {
                field: 'unitCost',
                displayName: 'Amount',
                width: '6%',
                visible: ($scope.pricingProfileType !== 'MARGIN')
            }, {
                field: 'costType',
                displayName: 'Amt Type',
                cellTemplate: "<label class=\"label-middle\">{{getCostTypeDescription(row.entity.costType)}}</label>",
                width: '7%',
                visible: ($scope.pricingProfileType !== 'MARGIN')
            }, {
                field: 'isExcludeFuel',
                displayName: 'Exclude Fuel',
                cellTemplate: 'pages/cellTemplate/checked-cell.html',
                width: '6%',
                visible: ($scope.pricingProfileType !== 'MARGIN')
            }, {
                field: 'costApplMinWt',
                displayName: 'Min Wt',
                width: '5%'
            }, {
                field: 'costApplMaxWt',
                displayName: 'Max Wt',
                width: '5%'
            }, {
                field: 'marginPercent',
                displayName: 'Margin %',
                width: '6%',
                visible: ($scope.pricingProfileType !== 'BLANKET'
                && $scope.pricingProfileType !== 'BUY' && $scope.pricingProfileType !== 'BENCHMARK')
            }, {
                field: 'transitTime',
                displayName: 'Transit',
                width: '6%',
                visible: ($scope.pricingProfileType !== 'BENCHMARK' && $scope.pricingProfileType !== 'MARGIN')
            }, {
                field: 'serviceType',
                displayName: 'Type',
                cellTemplate: "<label class=\"label-middle\">{{getServiceDescription(row.entity.serviceType)}}</label>",
                width: '5%',
                visible: ($scope.pricingProfileType !== 'BENCHMARK' && $scope.pricingProfileType !== 'MARGIN')
            }, {
                field: 'effDate',
                displayName: 'Effective',
                cellFilter: 'date:appDateFormat',
                width: '8%'
            }, {
                field: 'expDate',
                displayName: 'Expires',
                cellFilter: 'date:appDateFormat',
                width: '8%'
            }, {
                field: '',
                displayName: '',
                cellTemplate: "<div class=\"ngSelectionCell\">"
                + "<a href=\"\" data-ng-hide=\"row.entity.id===undefined\" data-ng-click=\"activateRecord(row.entity.id)\">Activate</a>"
                + "</div>",
                width: '5%',
                searchable: false,
                visible: ($scope.pricingProfileType !== 'BLANKET_CSP')
            }],
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin()],
            useExternalSorting: false,
            progressiveSearch: true
        };

        $scope.save = function () {
            var validationResult = true;

            _.each($scope.priceDetails, function (item) {
                if (validationResult && item.maxQuantity < item.minQuantity) {
                    validationResult = false;
                    $scope.$root.$emit('event:application-error', 'Saving Pallet failed!', 'Max Quantity should be greater than Min');
                    return false;
                }

                if (validationResult && item.costApplMaxWt < item.costApplMinWt) {
                    validationResult = false;
                    $scope.$root.$emit('event:application-error', 'Saving Pallet failed!', 'Max Weight should be greater than Min');
                    return false;
                }

                if (validationResult && $scope.pricingProfileType !== 'BLANKET_CSP' && (!item.zoneFrom || !item.zoneTo)) {
                    validationResult = false;
                    $scope.$root.$emit('event:application-error', 'Saving Pallet failed!', 'Please fill Zone From and Zone To.');
                    return false;
                }

                if ((validationResult && $scope.pricingProfileType !== 'BLANKET_CSP' && $scope.pricingProfileType !== 'MARGIN')
                        && (!item.costType || !item.unitCost)) {
                    validationResult = false;
                    $scope.$root.$emit('event:application-error', 'Saving Pallet failed!', 'Please fill Amt Type and Amount.');
                    return false;
                }
            });

            if (validationResult) {
                ProfileDetailsService.get({id: $routeParams.pricingId}, function (profile) {
                    _.each($scope.priceDetails, function (item) {
                        if (!item.effDate) {
                            item.effDate = profile.effDate;
                        }
                    });

                    $scope.processSaving();
                });
            }
        };

        $scope.processSaving = function () {
            PalletPricingService.save({detail: $scope.profileDetailId}, $scope.priceDetails, function () {
                $scope.$root.$emit('event:operation-success', 'Pallet data successfully saved');
                $scope.loadActiveDetails();
            }, function () {
                $scope.$root.$emit('event:application-error', 'Pallet data save failed!');
            });
        };

        $scope.profileEffectiveDate = null;
        $scope.selectedRateToCopy = null;

        var cloneByProfileId = function () {
            PalletePricingCopyFromService.copy({
                detail: $scope.profileDetailId,
                detailToCopy: $scope.selectedRateToCopy
            }, function () {
                $scope.loadActiveDetails();
                $scope.$root.$emit('event:operation-success', 'Pallet data  successfully copied');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Pallet data copying failed!');
            });
        };

        $scope.okClick = function () {
            // First check if the zones are available. If so, then copy the pallet details.
            PalletePricingCopyFromService.areZonesMissing({
                detail: $scope.profileDetailId,
                detailToCopy: $scope.selectedRateToCopy
            }, function (response) {
                if (!response.data) {
                    cloneByProfileId($scope.selectedRateToCopy);
                } else {
                    $scope.$root.$emit('event:application-error', 'All zones are not available for this profile to copy pallet details!');
                }
            }, function () {
                $scope.$root.$emit('event:application-error', 'Pallet data copying failed!');
            });

        };

        $scope.openDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', confirmButtonLabel: 'Copy',
                message: 'Copying will override all current Pallets data created. Do you want to continue?', okFunction: $scope.okClick
            });
        };

        $scope.inactivateRecord = function (id) {
            PalletPricingService.inactivate({
                detail: $scope.profileDetailId
            }, id, function () {
                $scope.$root.$emit('event:operation-success', 'Pallet was inactivated');
                $scope.loadActiveDetails();
            }, function () {
                $scope.$root.$emit('event:application-error', 'Pallet inactivating failed!');
            });
        };

        $scope.activateRecord = function (id) {
            PalletPricingService.activate({
                detail: $scope.profileDetailId
            }, id, function () {
                $scope.$root.$emit('event:operation-success', 'Pallet was activated');
                $scope.loadArchivedDetails();
            }, function () {
                $scope.$root.$emit('event:application-error', 'Pallet activating failed!');
            });
        };

        $scope.getZoneDescription = function (id) {
            var object = _.find($scope.zonesDictionary, function (item) {
                return item.value === id;
            });

            return _.isUndefined(object) ? "" : object.label;
        };

        $scope.getCostTypeDescription = function (id) {
            var object = _.find($scope.detailsDictionary.ltlCostTypes, function (item) {
                return item.value === id;
            });

            return _.isUndefined(object) ? "" : object.label;
        };

        $scope.getServiceDescription = function (id) {
            var object = _.find($scope.detailsDictionary.ltlServiceTypes, function (item) {
                return item.value === id;
            });

            return _.isUndefined(object) ? "" : object.label;
        };
    }
]);