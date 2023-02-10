angular.module('plsApp').controller('ZonesCtrl', [
    '$scope', 'LtlPricingZonesService', 'LtlPricingZoneStatusChangeService', 'LtlPricingZoneSaveService', 'NgGridPluginFactory', 'NgGridService',
    function ($scope, LtlPricingZonesService, LtlPricingZoneStatusChangeService, LtlPricingZoneSaveService, NgGridPluginFactory, NgGridService) {
        'use strict';

        $scope.selectedItems = [];
        $scope.statusFlag = "ACTIVE";
        $scope.zone = {};
        $scope.isEditZone = false;
        $scope.changeStatusButtonName = 'Archive';

        $scope.loadListItems = function (status) {
            $scope.currentTabName = status;
            $scope.isEditZone = false;
            NgGridService.refreshGrid($scope.gridItems);
            switch (status) {
                case 'ACTIVE':
                    LtlPricingZonesService.active({
                        id: $scope.profileDetailId
                    }, function (response) {
                        $scope.listItems = response;
                    });

                    $scope.changeStatusButtonName = 'Archive';
                    break;
                case 'ARCHIVED':
                    LtlPricingZonesService.archived({
                        id: $scope.profileDetailId
                    }, function (response) {
                        $scope.listItems = response;
                    });

                    $scope.changeStatusButtonName = 'Reactivate';
                    break;
            }

        };

        var getID = function () {
            var arr = [];

            $.each($scope.selectedItems, function (key, value) {
                arr.push(value.id);
            });

            return arr;
        };

        $scope.gridItems = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'listItems',
            multiSelect: false,
            columnDefs: [
                {
                    field: 'id',
                    displayName: 'ID',
                    width: '10%'
                }, {
                    field: 'name',
                    displayName: 'Name',
                    width: '30%'
                }, {
                    field: 'geography',
                    displayName: 'City/State/Zip/Country',
                    width: '60%'
                }],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            progressiveSearch: true
        };

        $scope.loadListItems('ACTIVE');

        var ZoneService = LtlPricingZoneSaveService, zone = new ZoneService();
        zone.ltlPricProfDetailId = $scope.profileDetailId;
        $scope.zone = zone;

        $scope.editZone = function () {
            if ($scope.currentTabName === 'ACTIVE' && "BLANKET_CSP" !== $scope.profileDetails.pricingType.ltlPricingType) {
                $scope.zone = angular.copy($scope.selectedItems[0]);

                LtlPricingZoneSaveService.get({
                    profile: $scope.profileDetailId,
                    id: $scope.selectedItems[0].id
                }, function (response) {
                    $scope.zone = response;
                    $scope.isEditZone = true;
                    $scope.selectedGeography.length = 0;
                });
            }
        };

        $scope.selectedGeography = [];

        $scope.addressGrid = {
            headerRowHeight: 25,
            multiSelect: false,
            enableColumnResize: true,
            selectedItems: $scope.selectedGeography,
            data: 'zone.ltlZoneGeoServicesEntities',
            columnDefs: [{
                field: 'location',
                displayName: 'City/State/Zip/Country'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        var isEditMode = false;

        $scope.zoneNameValidation = function () {
            $scope.validationFailed = false;
            $scope.errorMessage = "Zone with such name already exists.";

            if (_.isEmpty($scope.zone.name)) {
                $scope.validationFailed = true;
                return;
            }

            if ($scope.isEditZone && $scope.zone.id) {
                var list = _.difference($scope.listItems, $scope.selectedItems);

                _.each(list, function (zone) {
                    if ($scope.zone.name === zone.name) {
                        $scope.validationFailed = true;
                        $scope.$root.$emit('event:application-error', 'Zone validation failed!', $scope.errorMessage);
                        return;
                    }
                });
            } else {
                _.each($scope.listItems, function (zone) {
                    if ($scope.zone.name === zone.name) {
                        $scope.validationFailed = true;
                        $scope.$root.$emit('event:application-error', 'Zone validation failed!', $scope.errorMessage);
                        return;
                    }
                });
            }
        };

        $scope.saveZone = function () {
            $scope.zoneNameValidation();

            if ($scope.validationFailed) {
                return;
            }

            if (_.isEmpty($scope.zone.ltlZoneGeoServicesEntities)) {
                $scope.$root.$emit('event:application-error', 'Zone validation failed!', 'Please enter City/State/Zip/Country.');
                return;
            }

            if (_.isEmpty($scope.listItems)) {
                $scope.listItems = [];
            }

            $scope.zone.ltlPricProfDetailId = $scope.profileDetailId;
            $scope.zone.status = 'ACTIVE';
            $scope.isEditZone = false;

            // TODO: Need to revisit this some time.
            LtlPricingZoneSaveService.save({
                profile: $scope.profileDetailId
            }, $scope.zone, function () {
                $scope.loadListItems('ACTIVE');
                $scope.zone = {};
                $scope.$root.$emit('event:operation-success', 'Zone is successfully saved');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Failed to save Zone!');
            });
        };

        $scope.clear = function () {
            $scope.zone = {};
            $scope.geoText = undefined;
        };

        $scope.saveAsNew = function () {
            $scope.zone.id = undefined;
            $scope.zone.version = undefined;

            _.each($scope.zone.ltlZoneGeoServicesEntities, function (item) {
                item.id = undefined;
                item.ltlZoneId = undefined;
                item.version = undefined;
            });

            $scope.saveZone();
        };

        $scope.setGeography = function () {
            if (_.isEmpty($scope.geoText)) {
                return;
            } else {
                $scope.geoText = $scope.geoText.toUpperCase();
            }

            var Address = {
                location: $scope.geoText
            };

            if (!isEditMode) {
                if (_.isUndefined($scope.zone.ltlZoneGeoServicesEntities)) {
                    $scope.zone.ltlZoneGeoServicesEntities = [];
                }
                $scope.zone.ltlZoneGeoServicesEntities.push(Address);
            } else {
                $scope.selectedGeography[0].location = $scope.geoText;
            }

            $scope.geoText = '';
            isEditMode = false;
        };

        $scope.editGeography = function () {
            if ("BLANKET_CSP" !== $scope.profileDetails.pricingType.ltlPricingType) {
                $scope.geoText = $scope.selectedGeography[0].location;
                isEditMode = true;
            }
        };

        $scope.deleteGeography = function () {
            $scope.zone.ltlZoneGeoServicesEntities.splice($scope.zone.ltlZoneGeoServicesEntities.indexOf($scope.selectedGeography[0]), 1);
        };

        $scope.updateZoneStatus = function () {
            switch ($scope.currentTabName) {
                case 'ACTIVE':
                    LtlPricingZoneStatusChangeService.inactivate({
                        profile: $scope.profileDetailId,
                        isActiveList: true
                    }, getID(), function (response) {
                        $scope.listItems = response;
                        $scope.$root.$emit('event:operation-success', 'Zone was archived');
                    });
                    break;
                case 'ARCHIVED':
                    LtlPricingZoneStatusChangeService.reactivate({
                        profile: $scope.profileDetailId
                    }, getID(), function (response) {
                        $scope.listItems = response;
                        $scope.$root.$emit('event:operation-success', 'Zone was activated');
                    });
                    break;
            }
        };

        $scope.copyFrom = function () {
            LtlPricingZoneSaveService.copy({profile: $scope.profileDetailId}, {
                copyToId: $scope.profileDetailId,
                copyFromId: $scope.selectedRateToCopy.id
            }, function () {
                $scope.loadListItems('ACTIVE');
                $scope.$root.$emit('event:operation-success', 'Zones were successfully copied.');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Failed to copy zones!');
            });
        };

        $scope.openDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', okFunction: $scope.copyFrom,
                message: 'Copying will inactivate all active zones, continue?'
            });
        };
    }
]);