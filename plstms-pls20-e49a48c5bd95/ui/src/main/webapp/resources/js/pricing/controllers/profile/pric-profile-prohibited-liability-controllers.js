angular.module('plsApp').controller('ProhibitedLiabilityCtrl', ['$scope', '$route', 'PricingProfileByIdService',
    'CarrierLiabilitiesByProfileIdService', 'CopyLiabilities', 'ProfilesListToCopyService', 'ProfileDetailsService', 'ProhibitedNLiabilitiesService',
    'CopyProhibitedCommoditiesService', 'NgGridPluginFactory',
    function ($scope, $route, PricingProfileByIdService, CarrierLiabilitiesByProfileIdService, CopyLiabilities, ProfilesListToCopyService,
              ProfileDetailsService, ProhibitedNLiabilitiesService, CopyProhibitedCommoditiesService, NgGridPluginFactory) {
        'use strict';

        $scope.copyProfiles = [];
        $scope.isPallet = false;

        $scope.setProfileToScope = function (isReset) {
            ProfileDetailsService.get({id: $route.current.params.pricingId}, function (profile) {
                $scope.profileDetails = profile;

                if ($scope.profileDetails.pricingType.ltlPricingType === 'BUY_SELL') {
                    if ($scope.profileDetails.profileDetails[0].pricingDetailType === 'BUY') {
                        $scope.isPallet = ($scope.profileDetails.profileDetails[0].carrierType === 'PALLET');
                    } else {
                        $scope.isPallet = ($scope.profileDetails.profileDetails[1].carrierType === 'PALLET');
                    }
                } else {
                    $scope.isPallet = ($scope.profileDetails.profileDetails[0].carrierType === 'PALLET');
                }

                $scope.getLiabilities($scope.profileDetails.id);

                if (!isReset) {
                    $scope.getProfilesToCopy();
                }
            });
        };

        $scope.getLiabilities = function (id) {
            CarrierLiabilitiesByProfileIdService.get({
                id: id,
                isPallet: $scope.isPallet
            }, function (response) {
                $scope.liabilitiesList = response;
            });
        };

        $scope.getProfilesToCopy = function () {
            var copyCriteria = {
                profileId: $scope.profileDetails.id,
                prohibitedNLiabilities: true
            };

            if ($scope.profileDetails.pricingType.ltlPricingType === 'BUY_SELL') {
                copyCriteria.copyFromPricingDetailType = 'BUY';
            }

            ProfilesListToCopyService.get({}, copyCriteria, function (profiles) {
                $scope.copyProfiles = profiles;
            });
        };

        $scope.showCopyCommoditiesDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', confirmButtonLabel: 'Copy', okFunction: $scope.copyCommodities,
                message: 'Copying will override all current prohibited commodities created, continue?'
            });
        };

        $scope.showCopyLiabilitiesDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', confirmButtonLabel: 'Copy', okFunction: $scope.copyLiabilities,
                message: 'Copying will override all current carrier liabilities created, continue?'
            });
        };

        $scope.okClick = function () {
        };

        $scope.copyCommodities = function () {
            CopyProhibitedCommoditiesService.copy({
                id: $scope.profileDetails.id,
                copyFrom: $scope.selectedProhibited.id
            }, function () {
                PricingProfileByIdService.get({id: $scope.profileDetails.id}, function (response) {
                    $scope.profileDetails.prohibitedCommodities = response.prohibitedCommodities;
                });
            });
        };

        $scope.copyLiabilities = function () {
            CopyLiabilities.copy({
                id: $scope.profileDetails.id,
                copyFrom: $scope.selectedLiabilities.id
            }, function () {
                $scope.getLiabilities($scope.profileDetails.id);
            });
        };

        $scope.gridOptions = {
            enableColumnResize: true,
            data: 'liabilitiesList',
            enableCellEdit: true,
            enableCellSelection: true,
            columnDefs: [{
                field: 'freightClassCode',
                displayName: 'Class',
                enableCellEdit: false
            }, {
                field: 'liability.newProdLiabAmt',
                displayName: 'New (Rate)',
                editableCellTemplate: "<input ng-class=\"'colt' + col.index\" ng-input=\"COL_FIELD\" data-pls-number ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">",
                enableCellEdit: true
            }, {
                field: 'liability.usedProdLiabAmt',
                displayName: 'Used (Rate)',
                editableCellTemplate: "<input ng-class=\"'colt' + col.index\" ng-input=\"COL_FIELD\" data-pls-number ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">",
                enableCellEdit: true
            }, {
                field: 'liability.maxNewProdLiabAmt',
                displayName: 'New (Max)',
                editableCellTemplate: "<input ng-class=\"'colt' + col.index\" ng-input=\"COL_FIELD\" data-pls-number ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">",
                enableCellEdit: true
            }, {
                field: 'liability.maxUsedProdLiabAmt',
                displayName: 'Used (Max)',
                editableCellTemplate: "<input ng-class=\"'colt' + col.index\" ng-input=\"COL_FIELD\" data-pls-number ng-model=\"COL_FIELD\" "
                + "ng-disabled=\"_.contains(['BLANKET_CSP'], profileDetails.pricingType.ltlPricingType)\">",
                enableCellEdit: true
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.reset = function () {
            $scope.setProfileToScope(true);
            $scope.selectedProhibited = undefined;
            $scope.selectedLiabilities = undefined;
        };

        var validateLiabilities = function () {
            var isValid = true;

            _.each($scope.liabilitiesList, function (item) {
                if (isValid && ((item.liability.newProdLiabAmt !== null && item.liability.maxNewProdLiabAmt !== null
                        && item.liability.newProdLiabAmt > item.liability.maxNewProdLiabAmt)
                        || (item.liability.usedProdLiabAmt !== null && item.liability.maxUsedProdLiabAmt !== null
                        && item.liability.usedProdLiabAmt > item.liability.maxUsedProdLiabAmt))) {
                    isValid = false;
                }
            });

            return isValid;
        };

        var getLiabilitiesToSave = function (profileId) {
            var arr = [];

            _.each($scope.liabilitiesList, function (item) {
                if (item.liability.newProdLiabAmt !== null || item.liability.usedProdLiabAmt !== null
                        || item.liability.maxNewProdLiabAmt !== null || item.liability.maxUsedProdLiabAmt !== null) {
                    item.liability.pricingProfileId = profileId;
                    item.liability.id = null;

                    arr.push(item.liability);
                }
            });

            return arr;
        };

        $scope.submit = function () {
            if (validateLiabilities()) {
                var vo = {};

                vo.profile = $scope.profileDetails;
                vo.liabilities = getLiabilitiesToSave($scope.profileDetails.id);

                ProhibitedNLiabilitiesService.save({id: $scope.profileDetails.id}, vo, function () {
                    $scope.$root.$emit('event:operation-success', 'Prohibited & Liability', 'Prohibited and Liabilities successfully saved.');
                    $scope.reset();
                });
            } else {
                $scope.$root.$emit('event:application-error', 'Liabilities validation failed!', 'Min value should not be greater than max value.');
            }
        };

        $scope.setProfileToScope(false);
    }
]);
