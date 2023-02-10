angular.module('plsApp').controller('BlockCarrierZipCtrl', [
    '$scope', 'GetBlockCarrierGeoService', 'ChangeStatusBlockCarrierGeoService', 'CloneBlockCarrierGeoService', 'NgGridPluginFactory',
    'NgGridService',
    function ($scope, GetBlockCarrierGeoService, ChangeStatusBlockCarrierGeoService, CloneBlockCarrierGeoService, NgGridPluginFactory,
            NgGridService) {
        'use strict';

        $scope.activeTab = true;
        $scope.selectedItems = [];
        $scope.statusFlag = "ACTIVE";
        $scope.nextStatusAction = "Archive";
        $scope.isEditCarrierZip = false;

        $scope.status = new ChangeStatusBlockCarrierGeoService();

        var getID = function () {
            var arr = [];

            $.each($scope.selectedItems, function (key, value) {
                arr.push(value.id);
            });

            return arr;
        };

        $scope.clear = function () {
            NgGridService.refreshGrid($scope.addressGrid);
            $scope.status.id = undefined;
            $scope.status.version = undefined;
            $scope.editOrigin = undefined;
            $scope.editDestination = undefined;
            $scope.isEditCarrierZip = false;
            $scope.notes = undefined;
        };

        $scope.loadGeo = function (status) {
            $scope.selectedItems.length = 0;

            $scope.clear();

            if (status) {
                GetBlockCarrierGeoService.active({
                    id: $scope.profileDetailId
                }, function (response) {
                    $scope.addressList = response;
                    $scope.statusFlag = "ACTIVE";
                    $scope.nextStatusAction = "Archive";
                    $scope.activeTab = true;
                });
            } else {
                GetBlockCarrierGeoService.inactive({
                    id: $scope.profileDetailId
                }, function (response) {
                    $scope.addressList = response;
                    $scope.statusFlag = "INACTIVE";
                    $scope.nextStatusAction = "Reactivate";
                    $scope.activeTab = false;
                });
            }
        };

        $scope.loadGeo($scope.activeTab);

        $scope.addressGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'addressList',
            multiSelect: false,
            columnDefs: [{
                field: 'origin',
                displayName: 'Origin'
            }, {
                field: 'destination',
                displayName: 'Destination'
            }, {
                field: 'notes',
                displayName: 'Notes'
            }],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            progressiveSearch: true
        };

        $scope.changeGeoStatus = function () {
            switch ($scope.statusFlag) {
                case 'ACTIVE':
                    ChangeStatusBlockCarrierGeoService.inactivate({
                        profileId: $scope.profileDetailId
                    }, getID(), function (response) {
                        $scope.addressList = response;
                        $scope.activeTab = true;
                        $scope.$root.$emit('event:operation-success', 'Block Carrier Zip was archived');
                    });
                    break;
                case 'INACTIVE':
                    ChangeStatusBlockCarrierGeoService.reactivate({
                        profileId: $scope.profileDetailId
                    }, getID(), function (response) {
                        $scope.addressList = response;
                        $scope.activeTab = false;
                        $scope.$root.$emit('event:operation-success', 'Block Carrier Zip was activated');
                    });
                    break;
            }

            $scope.clear();
        };

        $scope.edit = function () {
            if ($scope.activeTab && ("BLANKET_CSP" !== $scope.profileDetails.pricingType.ltlPricingType)) {
                $scope.status.$get({
                    url: $scope.selectedItems[0].id
                }, function () {
                    $scope.editOrigin = $scope.status.origin;
                    $scope.editDestination = $scope.status.destination;
                    $scope.notes = $scope.status.notes;
                    $scope.statusFlag = $scope.status.status;
                    $scope.isEditCarrierZip = true;
                });
            }
        };

        $scope.setZips = function () {
            if (!_.isEmpty($scope.editOrigin)) {
                $scope.editOrigin = $scope.editOrigin.toUpperCase();
            }

            if (!_.isEmpty($scope.editDestination)) {
                $scope.editDestination = $scope.editDestination.toUpperCase();
            }

            $scope.status.origin = $scope.editOrigin;
            $scope.status.status = 'ACTIVE';
            $scope.status.destination = $scope.editDestination;
            $scope.status.profileId = $scope.profileDetailId;
            $scope.status.notes = $scope.notes;

            $scope.status.$save({
                url: 'save'
            }, function () {
                $scope.loadGeo($scope.activeTab);
                $scope.$root.$emit('event:operation-success', 'Block Carrier Zip was successfully saved');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Block Carrier Zip save failed!');
            });
        };

        $scope.saveAsNew = function () {
            $scope.status.id = undefined;
            $scope.status.version = undefined;
            $scope.setZips();
        };

        $scope.selectedRateToCopy = null;

        var cloneByProfileId = function () {
            CloneBlockCarrierGeoService.copy({
                copyToId: $scope.profileDetailId,
                copyFromId: $scope.selectedRateToCopy
            }, function () {
                $scope.loadGeo(true);
                $scope.$root.$emit('event:operation-success', 'Block Carrier Zip was successfully copied');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Block Carrier Zip copying failed!');
            });
        };

        $scope.okClick = function () {
            cloneByProfileId($scope.selectedRateToCopy);
        };

        $scope.openDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', confirmButtonLabel: 'Copy',
                message: 'Copying will override all current Block Carrier Zip data created. Do you want to continue?', okFunction: $scope.okClick
            });
        };
    }
]);
