/**
 * Controller for 'Accessorials' tab.
 *
 * @param $scope
 *            Model for this controller.
 * @returns {AccessorialsCtrl}
 */
angular.module('plsApp').controller('AccessorialsCtrl', [
    '$scope', '$routeParams', 'AccessorialTypesService', 'AccessorialsListService', 'AccessorialsService', 'AccessorialStatusChangeService',
    'CloneAccessorialsService', 'PricingDetailsDictionaryService', 'ProfileDetailsService', 'NgGridPluginFactory','NgGridService',
    function ($scope, $routeParams, AccessorialTypesService, AccessorialsListService, AccessorialsService, AccessorialStatusChangeService,
              CloneAccessorialsService, PricingDetailsDictionaryService, ProfileDetailsService, NgGridPluginFactory, NgGridService) {

        'use strict';

        $scope.selectedItems = [];
        $scope.selectedAddresses = [];
        $scope.ltlCostTypes = [];

        $scope.accessorial = {
            ltlPricProfDetailId: $scope.profileDetailId,
            movementType: $scope.profileDetails && $scope.profileDetails.profileDetails[0].carrierType === "CARRIER_API" ? "BOTH" : undefined,
            serviceType: $scope.profileDetails && $scope.profileDetails.profileDetails[0].carrierType === "LTLLC" ? "BOTH" : undefined
        };

        $scope.isEditAccessorial = false;
        $scope.isBlocked = false;
        $scope.selectedRateToCopy = null;
        $scope.changeStatusButtonName = 'Archive';

        var isEditMode = false;

        PricingDetailsDictionaryService.get({}, function (response) {
            $scope.detailsDictionary = response;

            $scope.ltlCostTypes = _.reject($scope.detailsDictionary.ltlCostTypes, function (item) {
                return item.value === 'DC';
            });
        });

        var ltllcOnlyAccessorialFilter = function(item){
            return true;// !$scope.profileDetails.isLTLLCApi() || item.id==='TX';
        };
        
        AccessorialTypesService.get({}, function (response) {
            $scope.accessorialTypes = response.filter(ltllcOnlyAccessorialFilter);
        });

        $scope.copyAccessorials = function () {
            CloneAccessorialsService.copy({
                id: $scope.profileDetailId,
                copyFromProfileDetailId: $scope.selectedRateToCopy
            }, function () {
                $scope.loadListItems('ACTIVE');
                $scope.$root.$emit('event:operation-success', 'Accessorial was successfully copied');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Accessorial copying failed!');
            });
        };

        $scope.selectedProfileToCopy = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', okFunction: $scope.copyAccessorials,
                message: 'Copying will override all current accessorials created. Do you want to continue?', confirmButtonLabel: 'Copy'
            });
        };

        $scope.resetAccessorial = function () {
            $scope.accessorial = {
                ltlPricProfDetailId: $scope.profileDetailId,
                serviceType: $scope.profileDetails && $scope.profileDetails.profileDetails[0].carrierType === "LTLLC" ? "BOTH" : undefined
            };

            $scope.isEditAccessorial = false;
            $scope.selectedItems.length = 0;
        };

        $scope.loadListItems = function (status) {
            $scope.currentTabName = status;
            $scope.resetAccessorial();
            NgGridService.refreshGrid($scope.gridOptions);

            switch (status) {
                case 'ACTIVE':
                    AccessorialsListService.active({
                        id: $scope.profileDetailId
                    }, function (response) {
                        $scope.listItems = response;
                    });

                    $scope.changeStatusButtonName = 'Archive';

                    break;
                case 'EXPIRED':
                    AccessorialsListService.expired({
                        id: $scope.profileDetailId
                    }, function (response) {
                        $scope.listItems = response;
                    });

                    $scope.changeStatusButtonName = 'Archive';

                    break;
                case 'ARCHIVED':
                    AccessorialsListService.archived({
                        id: $scope.profileDetailId
                    }, function (response) {
                        $scope.listItems = response;
                    });

                    $scope.changeStatusButtonName = 'Reactivate';

                    break;
            }
        };

        function getID() {
            var arr = [];

            $.each($scope.selectedItems, function (key, value) {
                arr.push(value.id);
            });

            return arr;
        }

        $scope.gridOptions = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'listItems',
            multiSelect: false,
            columnDefs: [{
                field: 'id',
                displayName: 'ID',
                width: '10%'
            }, {
                field: 'type',
                displayName: 'Type',
                width: '14%'
            }, {
                field: 'origin',
                displayName: 'Origin',
                width: '30%'
            }, {
                field: 'destination',
                displayName: 'Destination',
                width: '30%'
            }, {
                field: 'minCost',
                displayName: 'Min Cost',
                width: '16%'
            }],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            progressiveSearch: true
        };

        $scope.loadListItems('ACTIVE');

        $scope.editAccessorial = function () {
            if ($scope.currentTabName !== 'ARCHIVED') {
                var selectedItems = getID();

                AccessorialsService.get({
                    profile: $scope.profileDetailId,
                    id: selectedItems[0]
                }, function (response) {
                    $scope.accessorial = response;
                    $scope.isBlocked = $scope.accessorial.blocked === 'Y';
                    $scope.isEditAccessorial = true;
                    $scope.selectedAddresses.length = 0;
                    $scope.editOrigin = '';
                    $scope.editDestination = '';
                });
            }
        };

        $scope.addressGrid = {
            headerRowHeight: 25,
            multiSelect: false,
            enableColumnResize: true,
            selectedItems: $scope.selectedAddresses,
            data: 'accessorial.ltlAccGeoServicesEntities',
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

        $scope.saveAccessorial = function (isNew) {
            if (!($scope.customerMarginSetup || _.contains(['BLANKET_CSP', 'MARGIN'], $scope.profileDetails.pricingType.ltlPricingType))) {
                if (!($scope.profileDetails.isCSPAndCarrierApi() || $scope.profileDetails.isLTLLCApi()) &&
                        (!$scope.accessorial.costType || _.isUndefined($scope.accessorial.unitCost)))
                {
                    $scope.$root.$emit('event:application-error', 'Accessorial validation failed!', 'Please fill Cost Type and Cost Value.');
                    return false;
                }
            }

            if (($scope.accessorial.minCost && $scope.accessorial.maxCost) && $scope.accessorial.minCost > $scope.accessorial.maxCost) {
                $scope.$root.$emit('event:application-error', 'Accessorial validation failed!',
                'Min Cost should be less than Max Cost.');
                return false;
            }

            if (!_.contains(['BLANKET_CSP'], $scope.profileDetails.pricingType.ltlPricingType)
                    && _.isEmpty($scope.accessorial.ltlAccGeoServicesEntities)) {
                $scope.$root.$emit('event:application-error', 'Accessorial validation failed!',
                        'Please fill Origin (City/State/Zip/Country) and Destination (City/State/Zip/Country).');
                return false;
            }

            if (!_.contains(['BLANKET_CSP'], $scope.profileDetails.pricingType.ltlPricingType)
                    && $scope.accessorial.costApplMaxDist < $scope.accessorial.costApplMinDist) {
                $scope.$root.$emit('event:application-error', 'Accessorial validation failed!',
                        'Max Distance should be greater than Min Distance.');
                return false;
            }

            if (!_.contains(['BLANKET_CSP'], $scope.profileDetails.pricingType.ltlPricingType)
                    && $scope.accessorial.costApplMaxWt < $scope.accessorial.costApplMinWt) {
                $scope.$root.$emit('event:application-error', 'Accessorial validation failed!',
                        'Max Weight should be greater than Min Weight.');
                return false;
            }

            if (!_.contains(['BLANKET_CSP'], $scope.profileDetails.pricingType.ltlPricingType)
                    && $scope.accessorial.costApplMaxLength < $scope.accessorial.costApplMinLength) {
                $scope.$root.$emit('event:application-error', 'Accessorial validation failed!',
                        'Max Length should be greater than Min Length.');
                return false;
            }

            if (_.isEmpty($scope.listItems)) {
                $scope.listItems = [];
            }

            if (isNew) {
                var i;

                //Clear all the primary keys
                $scope.accessorial.id = null;

                for (i = 0; i < $scope.accessorial.ltlAccGeoServicesEntities.length; i += 1) {
                    $scope.accessorial.ltlAccGeoServicesEntities[i].id = null;
                    $scope.accessorial.ltlAccGeoServicesEntities[i].ltlAccessorialId = null;
                }
            }

            $scope.accessorial.blocked = ($scope.isBlocked === true) ? 'Y' : 'N';

            if (!$scope.accessorial.effDate) {
                ProfileDetailsService.get({
                    id: $routeParams.pricingId
                }, function (profile) {
                    $scope.accessorial.effDate = profile.effDate;
                    $scope.processSaving();
                });
            } else {
                $scope.processSaving();
            }
        };

        $scope.processSaving = function () {
            var success = function () {
                $scope.loadListItems($scope.currentTabName);
                $scope.resetAccessorial();
                $scope.$root.$emit('event:operation-success', 'Accessorial successfully saved');
            };

            var failure = function () {
                $scope.$root.$emit('event:application-error', 'Failed to save Accessorial!');
            };

            AccessorialsService.save({profile: $scope.profileDetailId}, $scope.accessorial, success, failure);
        };

        $scope.setZips = function () {
            var isEmptyOrigin = _.isEmpty($scope.editOrigin);
            var isEmptyDestination = _.isEmpty($scope.editDestination);

            if (isEmptyOrigin && isEmptyDestination) {
                return;
            }

            if (!isEmptyOrigin) {
                $scope.editOrigin = $scope.editOrigin.toUpperCase();
            }

            if (!isEmptyDestination) {
                $scope.editDestination = $scope.editDestination.toUpperCase();
            }

            var Address = {
                id: '',
                origin: $scope.editOrigin,
                destination: $scope.editDestination
            };

            if (!isEditMode) {
                if (_.isUndefined($scope.accessorial.ltlAccGeoServicesEntities)) {
                    $scope.accessorial.ltlAccGeoServicesEntities = [];
                }

                $scope.accessorial.ltlAccGeoServicesEntities.push(Address);
            } else {
                $scope.selectedAddresses[0].origin = $scope.editOrigin;
                $scope.selectedAddresses[0].destination = $scope.editDestination;
            }

            $scope.editOrigin = '';
            $scope.editDestination = '';
            isEditMode = false;
        };

        function clearAddresses() {
            $scope.selectedAddresses.length = 0;
            $scope.editOrigin = null;
            $scope.editDestination = null;
            isEditMode = false;
        }

        $scope.editZips = function () {
            $scope.editOrigin = $scope.selectedAddresses[0].origin;
            $scope.editDestination = $scope.selectedAddresses[0].destination;
            isEditMode = true;
        };

        $scope.deleteZips = function () {
            $scope.accessorial.ltlAccGeoServicesEntities.splice($scope.accessorial.ltlAccGeoServicesEntities.indexOf($scope.selectedAddresses[0]), 1);
            clearAddresses();
        };

        $scope.updateAccessorialStatus = function () {
            switch ($scope.currentTabName) {
                case 'ACTIVE':
                    $scope.onArchive(true);
                    break;
                case 'EXPIRED':
                    $scope.onArchive(false);
                    break;
                case 'ARCHIVED':
                    $scope.onActivate();
                    break;
            }
        };

        $scope.onArchive = function (activeList) {
            AccessorialStatusChangeService.inactivate({
                ids: getID(),
                profile: $scope.profileDetailId,
                isActiveList: activeList
            }, function (response) {
                $scope.listItems = response;
                $scope.resetAccessorial();
                $scope.$root.$emit('event:operation-success', 'Accessorial was Archived');
            });
        };

        $scope.onActivate = function () {
            AccessorialStatusChangeService.reactivate({
                ids: getID(),
                profile: $scope.profileDetailId
            }, function (response) {
                $scope.listItems = response;
                $scope.resetAccessorial();
                $scope.$root.$emit('event:operation-success', 'Accessorial was Activated');
            });
        };

        $scope.expireAccessorials = function () {
            AccessorialStatusChangeService.expire({
                ids: getID(),
                profile: $scope.profileDetailId
            }, function (response) {
                $scope.listItems = response;
                $scope.resetAccessorial();
                $scope.$root.$emit('event:operation-success', 'Accessorials was Expired');
            });
        };
    }
]);