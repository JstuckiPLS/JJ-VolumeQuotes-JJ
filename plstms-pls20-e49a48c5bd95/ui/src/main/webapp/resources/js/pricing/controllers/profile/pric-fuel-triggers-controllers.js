angular.module('plsApp').controller('FuelAndTriggersCtrl', [
    '$scope', '$routeParams', 'GetLtlFuelAndTriggersListService', 'ChangeLtlFuelAndTriggersListService', 'GetDOTRegionsService', 'GetSelectedFuel',
    'NgGridPluginFactory', 'ProfileDetailsService', 'PricingDetailsDictionaryService', 'NgGridService',
    function ($scope, $routeParams, GetLtlFuelAndTriggersListService, ChangeLtlFuelAndTriggersListService, GetDOTRegionsService, GetSelectedFuel,
              NgGridPluginFactory, ProfileDetailsService, PricingDetailsDictionaryService, NgGridService) {
        'use strict';

        $scope.selectedRateToCopy = null;
        $scope.isEditTrigger = false;

        // Enum for determination which tab is active right now
        $scope.tabsEnum = {ACTIVE: 'Active', EXPIRED: 'Expired', ARCHIVED: 'Archived'};
        $scope.selectedFuel = {};
        $scope.selectedAddresses = [];
        $scope.currentTab = "";
        $scope.changeStatusButtonName = "Reactivate";

        $scope.clear = function () {
            NgGridService.refreshGrid($scope.gridOptions);
            $scope.selectedFuel = {};
            $scope.selectedItems.length = 0;
            $scope.selectedAddresses.length = 0;
            $scope.current = "";
            $scope.zipOriginText = undefined;
            $scope.isEditTrigger = false;
        };

        function initialize() {
            PricingDetailsDictionaryService.get({}, function (response) {
                $scope.detailsDictionary = response;
            });
        }

        initialize();

        // Load date for Region drop-down
        $scope.loadDOTRegionsAndFuelRatesService = function () {
            GetDOTRegionsService.get({}, function (responce) {
                $scope.dotRegionsList = _.sortBy(responce, function (item) {
                    if (item.description && item.description.trim() === 'National') {
                        return null;
                    }

                    return item.description;
                });
            });
        };

        $scope.loadDOTRegionsAndFuelRatesService();

        // Load list of fuel triggers for selected tab (active/expired/archived)
        $scope.loadLtlFuelAndTriggers = function (status) {
            $scope.clear();
            $scope.fuelandTriggersList = [];
            $scope.currentTab = status;

            switch (status) {
                case $scope.tabsEnum.ACTIVE:
                    $scope.currentTab = $scope.tabsEnum.ACTIVE;

                    GetLtlFuelAndTriggersListService.active({ltlPricingProfileId: $scope.profileDetailId}, function (response) {
                        $scope.fuelandTriggersList = response;
                    });

                    $scope.changeStatusButtonName = "Archive";
                    break;
                case $scope.tabsEnum.EXPIRED:
                    $scope.currentTab = $scope.tabsEnum.EXPIRED;

                    GetLtlFuelAndTriggersListService.expired({ltlPricingProfileId: $scope.profileDetailId}, function (response) {
                        $scope.fuelandTriggersList = response;
                    });

                    $scope.changeStatusButtonName = "Archive";
                    break;
                case $scope.tabsEnum.ARCHIVED:
                    $scope.currentTab = $scope.tabsEnum.ARCHIVED;

                    GetLtlFuelAndTriggersListService.inactive({ltlPricingProfileId: $scope.profileDetailId}, function (response) {
                        $scope.fuelandTriggersList = response;
                    });

                    $scope.changeStatusButtonName = "Reactivate";
                    break;
            }
        };

        $scope.selectedItems = [];

        // Concat origins ZIP codes to display them in one field as string
        $scope.getOrigins = function (originEntities) {
            return _.chain(originEntities).map(function (originEntity) {
                return originEntity.origin;
            }).value().join(', ');
        };

        $scope.gridOptions = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            multiSelect: false,
            data: 'fuelandTriggersList',
            columnDefs: [{
                field: 'id',
                displayName: 'ID',
                width: '15%'
            }, {
                field: 'origin',
                displayName: 'Origin',
                width: '25%'
            }, {
                field: 'region',
                displayName: 'Region',
                width: '30%'
            }, {
                field: 'effectiveDay',
                displayName: 'Effective Day',
                width: '30%'
            }],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            progressiveSearch: true
        };

        $scope.loadLtlFuelAndTriggers($scope.tabsEnum.ACTIVE);

        $scope.openDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', confirmButtonLabel: 'Copy',
                message: 'Copying will override all current fuel triggers data created. Do you want to continue?', okFunction: $scope.okClick
            });
        };

        $scope.selectedRateToCopy = null;

        var cloneByProfileId = function (fromProfileDetailId) {
            ChangeLtlFuelAndTriggersListService.copyFrom({
                copyToProfileId: $scope.profileDetailId,
                copyFromProfileId: fromProfileDetailId
            }, function () {
                $scope.loadLtlFuelAndTriggers($scope.tabsEnum.ACTIVE);
                $scope.$root.$emit('event:operation-success', 'Fuel triggers successfully copied');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Fuel triggers copying failed!');
            });
        };

        $scope.okClick = function () {
            cloneByProfileId($scope.selectedRateToCopy.id);
        };

        $scope.onChangeStatus = function () {
            switch ($scope.currentTab) {
                case $scope.tabsEnum.ACTIVE:
                    $scope.onArchive();
                    break;
                case $scope.tabsEnum.EXPIRED:
                    $scope.onArchive();
                    break;
                case $scope.tabsEnum.ARCHIVED:
                    $scope.onActivate();
                    break;
            }
        };

        $scope.onArchive = function () {
            var i;
            var idsToArchive = [];

            if ($scope.gridOptions.selectedItems.length === 0) {
                return;
            }

            for (i = 0; i < $scope.gridOptions.selectedItems.length; i += 1) {
                idsToArchive.push($scope.gridOptions.selectedItems[i].id);
            }

            ChangeLtlFuelAndTriggersListService.inactivate({
                profileDetailId: $scope.profileDetailId,
                isActiveList: $scope.currentTab === $scope.tabsEnum.ACTIVE
            }, idsToArchive, function (responce) {
                $scope.fuelandTriggersList = responce;
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Fuel Trigger was archived');
            });
        };

        $scope.onActivate = function () {
            var i;
            var idsToActivate = [];

            if ($scope.gridOptions.selectedItems.length === 0) {
                return;
            }

            for (i = 0; i < $scope.gridOptions.selectedItems.length; i += 1) {
                idsToActivate.push($scope.gridOptions.selectedItems[i].id);
            }

            ChangeLtlFuelAndTriggersListService.reactivate({profileDetailId: $scope.profileDetailId}, idsToActivate, function (responce) {
                $scope.fuelandTriggersList = responce;
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Fuel Trigger was activated');
            });
        };

        $scope.onExpire = function () {
            var i;
            var idsToExpire = [];

            if ($scope.gridOptions.selectedItems.length === 0) {
                return;
            }

            for (i = 0; i < $scope.gridOptions.selectedItems.length; i += 1) {
                idsToExpire.push($scope.gridOptions.selectedItems[i].id);
            }

            ChangeLtlFuelAndTriggersListService.expire({profileDetailId: $scope.profileDetailId}, idsToExpire, function (response) {
                $scope.fuelandTriggersList = response;
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Fuel Trigger was expired');
            });
        };

        // Star editing of selected fuel trigger
        $scope.startEdit = function () {
            GetSelectedFuel.get({id: $scope.gridOptions.selectedItems[0].id}, function (response) {
                $scope.isEditTrigger = true;
                $scope.selectedAddresses.length = 0;
                $scope.zipOriginText = '';
                $scope.selectedFuel = response;

                var selectedRegion = _.find($scope.dotRegionsList, function (item) {
                    return item.id === $scope.selectedFuel.dotRegion.id;
                });

                $scope.selectedFuel.dotRegion = selectedRegion;
                $scope.selectedFuel.dotRegionId = selectedRegion.id;
            });
        };

        $scope.save = function (isNew) {
            if (!$scope.selectedFuel.dotRegion) {
                $scope.$root.$emit('event:application-error', 'Please select Region.');
                return;
            }

            if ($scope.profileDetails.pricingType.ltlPricingType !== "MARGIN") {
                $scope.selectedFuel.dotRegionId = $scope.selectedFuel.dotRegion.id;

                if (!$scope.selectedFuel.dotRegionId || !$scope.selectedFuel.effectiveDay) {
                    $scope.$root.$emit('event:application-error', 'Saving Fuel Triggers validation failed!', 'Please fill Region and Effective Day.');
                    return;
                }
            }

            if (_.isEmpty($scope.selectedFuel.ltlFuelGeoServices)) {
                $scope.$root.$emit('event:application-error',
                        'Saving Fuel Triggers validation failed!', 'Please fill Origin (City/State/Zip/Country).');
                return;
            }

            if (isNew) {
                var i;

                //Clear all the primary keys
                $scope.selectedFuel.id = null;

                for (i = 0; i < $scope.selectedFuel.ltlFuelGeoServices.length; i += 1) {
                    $scope.selectedFuel.ltlFuelGeoServices[i].id = null;
                    $scope.selectedFuel.ltlFuelGeoServices[i].ltlFuelId = null;
                }
            }

            if (_.isUndefined($scope.selectedFuel.id) || $scope.selectedFuel.id === null) {
                $scope.selectedFuel.ltlPricingProfileId = $scope.profileDetailId;
            }

            if (!_.isUndefined($scope.selectedFuel.id) && $scope.selectedFuel.dotRegionFuel) {
                $scope.selectedFuel.dotRegion = $scope.selectedFuel.dotRegionFuel.dotRegion;
            }

            if (!$scope.selectedFuel.effectiveDate) {
                ProfileDetailsService.get({id: $routeParams.pricingId}, function (profile) {
                    $scope.selectedFuel.effectiveDate = profile.effDate;
                    $scope.processSaving();
                });
            } else {
                $scope.processSaving();
            }
        };

        $scope.processSaving = function () {
            ChangeLtlFuelAndTriggersListService.save($scope.selectedFuel, function () {
                $scope.$root.$emit('event:operation-success', 'Fuel Trigger was successfully saved.');
                $scope.loadLtlFuelAndTriggers($scope.currentTab);
            }, function () {
                $scope.$root.$emit('event:application-error', 'Failed to save Fuel Trigger!');
            });
        };

        $scope.addressGrid = {
            headerRowHeight: 25,
            multiSelect: false,
            enableColumnResize: true,
            selectedItems: $scope.selectedAddresses,
            data: 'selectedFuel.ltlFuelGeoServices',
            columnDefs: [{
                field: 'origin',
                displayName: 'Origin',
                width: '100%'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        var isEditMode = false;

        function clearAddress() {
            $scope.selectedAddresses.length = 0;
            $scope.zipOriginText = '';
            isEditMode = false;
        }

        $scope.submitGeoServiceChanges = function () {
            if (_.isEmpty($scope.zipOriginText)) {
                return;
            } else {
                $scope.zipOriginText = $scope.zipOriginText.toUpperCase();
            }

            var Address = {
                origin: $scope.zipOriginText
            };

            if (!isEditMode) {
                if (_.isUndefined($scope.selectedFuel.ltlFuelGeoServices)) {
                    $scope.selectedFuel.ltlFuelGeoServices = [];
                }

                $scope.selectedFuel.ltlFuelGeoServices.push(Address);
            } else {
                $scope.selectedAddresses[0].origin = $scope.zipOriginText;
            }

            clearAddress();
        };

        // Origin Zip corresponds to GeoService on back-end
        $scope.startEditGeoService = function () {
            $scope.zipOriginText = $scope.selectedAddresses[0].origin;
            isEditMode = true;
        };

        $scope.deleteGeoService = function () {
            $scope.selectedFuel.ltlFuelGeoServices.splice($scope.selectedFuel.ltlFuelGeoServices.indexOf($scope.selectedAddresses[0]), 1);
            clearAddress();
        };
    }
]);
