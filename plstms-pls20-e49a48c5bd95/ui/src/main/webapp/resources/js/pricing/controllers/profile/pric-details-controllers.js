angular.module('plsApp').controller('PricingDetailsCtrl', ['$scope', '$routeParams', 'PricingDetailsDictionaryService', 'PricingDetailsService',
    'PricingDetailStatusService', 'PricingDetailCopyFromService', 'ProfileDetailsService', 'NgGridPluginFactory', 'NgGridService',
    function ($scope, $routeParams, PricingDetailsDictionaryService, PricingDetailsService,
              PricingDetailStatusService, PricingDetailCopyFromService, ProfileDetailsService, NgGridPluginFactory, NgGridService) {
        'use strict';

        $scope.showCostPanel = true;
        $scope.disabledCostPanel = false;
        $scope.isBlanketCSP = false;
        $scope.isMarginSetup = false;

        $scope.priceDetail = {
            status: 'ACTIVE',
            serviceType: 'DIRECT',
            profileDetailId: $scope.profileDetailId,
            fakMapping: {},
            addresses: [],
            movementType: $scope.profileDetails.profileDetails[0].carrierType === "CARRIER_API"
                || $scope.profileDetails.profileDetails[0].carrierType === "LTLLC" ? "BOTH" : undefined
        };

        $scope.selectedAddresses = [];
        $scope.currentTabName = 'Active';
        $scope.changeStatusButtonName = 'Archive';
        $scope.selectedItems = [];

        $scope.editedAddress = {};

        if ($scope.pricingType === 'BUY') {
            $scope.showMarginPanel = false;
        } else if ($scope.pricingType === 'SELL') {
            $scope.showMarginPanel = true;
        } else {
            switch ($scope.profileDetails.ltlPricingType) {
                case 'BLANKET':
                    $scope.showMarginPanel = false;
                    break;
                case 'BLANKET_CSP':
                    $scope.showMarginPanel = true;
                    $scope.disabledCostPanel = true;
                    $scope.isBlanketCSP = true;
                    break;
                case 'CSP':
                    $scope.showMarginPanel = true;
                    break;
                case 'MARGIN':
                    $scope.showCostPanel = false;
                    $scope.showMarginPanel = true;
                    $scope.isMarginSetup = true;
                    break;
                case 'BENCHMARK':
                    $scope.showMarginPanel = false;
                    break;
            }
        }

        PricingDetailsDictionaryService.get({}, function (response) {
            $scope.detailsDictionary = response;

            if ($scope.profileDetails.profileDetails[0].carrierType === 'MANUAL') {
                $scope.detailsDictionary.ltlCostTypes = _.reject($scope.detailsDictionary.ltlCostTypes, function (item) {
                    return item.value === 'DC';
                });
            } else if ($scope.profileDetails.profileDetails[0].carrierType === 'SMC3' 
                    || $scope.profileDetails.profileDetails[0].carrierType === 'LTLLC') {
                $scope.detailsDictionary.ltlCostTypes = _.where($scope.detailsDictionary.ltlCostTypes, {value: 'DC'});
            }

            $scope.priceDetail.costType = $scope.detailsDictionary.ltlCostTypes[0].value;
            $scope.priceDetail.marginType = $scope.detailsDictionary.ltlMarginTypes[0].value;
        });

        PricingDetailsService.active({
            detail: $scope.profileDetailId
        }, function (response) {
            $scope.listItems = response;
        });

        function copyFrom() {
            PricingDetailCopyFromService.copy({
                detail: $scope.profileDetailId,
                detailToCopy: $scope.selectedRateToCopy
            }, function () {
                $scope.loadListItems('Active');
                $scope.$root.$emit('event:operation-success', 'Pricing was successfully copied');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Pricing copying failed!');
            });
        }

        $scope.confirmCopyFrom = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation',
                confirmButtonLabel: 'Copy',
                message: 'Copying will override all current pricing details created, continue?', okFunction: copyFrom
            });
        };

        function clearAddresses() {
            $scope.selectedAddresses.length = 0;
            $scope.editedAddress = {};
            delete $scope.editedAddressBackup;
            NgGridService.refreshGrid($scope.addressGrid);
        }

        $scope.clear = function () {
            NgGridService.refreshGrid($scope.gridOptions);
            $scope.selectedItems.length = 0;
            $scope.priceDetail = {
                status: 'ACTIVE',
                serviceType: 'DIRECT',
                profileDetailId: $scope.profileDetailId,
                costType: $scope.detailsDictionary.ltlCostTypes[0].value,
                marginType: $scope.detailsDictionary.ltlMarginTypes[0].value,
                fakMapping: {},
                addresses: [],
                movementType: $scope.profileDetails.profileDetails[0].carrierType === "LTLLC" ? "BOTH" : undefined
            };
            clearAddresses();
        };

        $scope.loadListItems = function (status) {
            $scope.currentTabName = status;

            switch (status) {
                case 'Active' :
                    PricingDetailsService.active({
                        detail: $scope.profileDetailId
                    }, function (response) {
                        $scope.listItems = response;
                        $scope.clear();
                    });

                    $scope.changeStatusButtonName = 'Archive';

                    break;
                case 'Expired' :
                    PricingDetailsService.expired({
                        detail: $scope.profileDetailId
                    }, function (response) {
                        $scope.listItems = response;
                        $scope.clear();
                    });

                    $scope.changeStatusButtonName = 'Archive';

                    break;
                case 'Archived' :
                    PricingDetailsService.inactive({
                        detail: $scope.profileDetailId
                    }, function (response) {
                        $scope.listItems = response;
                        $scope.clear();
                    });

                    $scope.changeStatusButtonName = 'Reactivate';

                    break;
            }
        };

        $scope.gridOptions = {
            enableColumnResize: true,
            multiSelect: false,
            selectedItems: $scope.selectedItems,
            data: 'listItems',
            columnDefs: [{
                field: 'id',
                displayName: 'ID',
                width: '12%'
            }, {
                field: 'origin',
                displayName: 'Origin',
                width: '25%',
                cellFilter: 'notNullFilter'
            }, {
                field: 'destination',
                displayName: 'Destination',
                width: '25%',
                cellFilter: 'notNullFilter'
            }, {
                field: 'plsCost',
                displayName: 'PLS Cost',
                width: '18%'
            }, {
                field: 'minCost',
                displayName: 'Min Cost',
                width: '20%'
            }],
            plugins: [NgGridPluginFactory.plsGrid(),NgGridPluginFactory.progressiveSearchPlugin()],
            progressiveSearch: true
        };

        $scope.editDetails = function () {
            var selectedId = $scope.selectedItems[0].id;

            PricingDetailsService.get({
                detail: $scope.profileDetailId,
                url: selectedId
            }, function (response) {
                $scope.priceDetail = response;
                if (!$scope.priceDetail.fakMapping) {
                    $scope.priceDetail.fakMapping = {};
                }
                if (!$scope.priceDetail.addresses) {
                    $scope.priceDetail.addresses = [];
                }

                clearAddresses();
            });
        };

        $scope.addressGrid = {
            headerRowHeight: 25,
            multiSelect: false,
            enableColumnResize: true,
            selectedItems: $scope.selectedAddresses,
            data: 'priceDetail.addresses',
            columnDefs: [{
                field: 'origin',
                displayName: 'Origin',
                width: '50%'
            }, {
                field: 'destination',
                displayName: 'Destination',
                width: '50%'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.setZips = function () {
            if ($scope.editedAddressBackup) {
                $scope.editedAddressBackup.origin = $scope.editedAddress.origin.toUpperCase();
                $scope.editedAddressBackup.destination = $scope.editedAddress.destination.toUpperCase();
            } else {
                $scope.priceDetail.addresses.push($scope.editedAddress);
            }
            clearAddresses();
        };

        $scope.editZips = function () {
            $scope.editedAddressBackup = $scope.selectedAddresses[0];
            $scope.editedAddress = angular.copy($scope.selectedAddresses[0]);
        };

        $scope.deleteZips = function () {
            $scope.priceDetail.addresses.splice(
                    $scope.priceDetail.addresses.indexOf($scope.selectedAddresses[0]), 1);
            clearAddresses();
        };

        $scope.save = function (saveAsNew) {
            if (!$scope.isBlanketCSP && _.isEmpty($scope.priceDetail.addresses)) {
                $scope.$root.$emit('event:application-error', 'Pricing validation failed!',
                        'Please fill Origin (City/State/Zip/Country) and Destination (City/State/Zip/Country).');
                return;
            }

            if (!$scope.isBlanketCSP && ($scope.priceDetail.maxWeight < $scope.priceDetail.minWeight)) {
                $scope.$root.$emit('event:application-error', 'Pricing validation failed!',
                        'Max Weight should be greater than Min Weight.');
                return;
            }

            if (!$scope.isBlanketCSP && ($scope.priceDetail.maxDistance < $scope.priceDetail.minDistance)) {
                $scope.$root.$emit('event:application-error', 'Pricing validation failed!',
                        'Max Distance should be greater than Min Distance.');
                return;
            }

            if (!$scope.priceDetail.effDate) {
                $scope.priceDetail.effDate = $scope.profileDetails.effDate;
            }
            if (saveAsNew) {
                delete $scope.priceDetail.id;
                _.each($scope.priceDetail.addresses, function(a) { delete a.id; });
            }
            $scope.processSaving();
        };

        $scope.processSaving = function () {
            $scope.priceDetail.profileDetailId = $scope.profileDetailId;
            PricingDetailsService.save({detail: $scope.profileDetailId}, $scope.priceDetail, function () {
                $scope.loadListItems($scope.currentTabName);
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Pricing was successfully saved');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Pricing save failed!');
            });
        };

        function makeInactiveReturnActive() {
            PricingDetailStatusService.inactivate({
                profile: $scope.profileDetailId,
                isActiveList: true
            }, [$scope.selectedItems[0].id], function (response) {
                $scope.listItems = response;
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Pricing was Archived');
            });
        }

        function makeInactiveReturnExpired() {
            PricingDetailStatusService.inactivate({
                profile: $scope.profileDetailId,
                isActiveList: false
            }, [$scope.selectedItems[0].id], function (response) {
                $scope.listItems = response;
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Pricing was Archived');
            });
        }

        function makeActiveReturnInactive() {
            PricingDetailStatusService.reactivate({
                profile: $scope.profileDetailId
            }, [$scope.selectedItems[0].id], function (response) {
                $scope.listItems = response;
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Pricing was Activated');
            });
        }

        $scope.changeStatusEvent = function () {
            switch ($scope.currentTabName) {
                case 'Active' :
                    makeInactiveReturnActive();
                    break;
                case 'Expired' :
                    makeInactiveReturnExpired();
                    break;
                case 'Archived' :
                    makeActiveReturnInactive();
                    break;
            }
        };

        $scope.expireEvent = function () {
            PricingDetailStatusService.expirate({
                profile: $scope.profileDetailId
            }, [$scope.selectedItems[0].id], function (response) {
                $scope.listItems = response;
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Pricing was Expired');
            });
        };
    }
]);
