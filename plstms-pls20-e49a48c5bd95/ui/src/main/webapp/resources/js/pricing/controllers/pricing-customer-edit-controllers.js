angular.module('plsApp').controller('CustomerPricingController', [
    '$scope', '$location', '$route', 'ProfilesListService', 'ProfileStatusChangeService', 'CustomerPricingService', 'NgGridPluginFactory',
    function ($scope, $location, $route, ProfilesListService, ProfileStatusChangeService, CustomerPricingService, NgGridPluginFactory) {
        'use strict';

        var customerId = $route.current.params.customerId;

        $scope.profileNextStatus = "Inactivate";
        $scope.updateDfltMrgnAndCarrBlockAllowed = !$scope.$root.isFieldRequired('PRICING_PAGE_VIEW')
                && ($scope.$root.isFieldRequired('UPDATE_DEFAULT_MARGIN') || $scope.$root.isFieldRequired('UPDATE_CARRIER_BLOCK')
                        || $scope.$root.isFieldRequired('UPDATE_BLOCK_LANE'));

        function setCustPricingDetails(custPricing) {
            $scope.custPricing = custPricing;
            if (!$scope.custPricing.orgPricing) {
                $scope.custPricing.orgPricing = {};
                $scope.custPricing.orgPricing.activateCustPricing = true; //Default Status
            } else {
                $scope.custPricing.orgPricing.activateCustPricing = $scope.custPricing.orgPricing.status === 'ACTIVE';
            }

            $scope.custPricing.orgPricing.gainshareAccount = $scope.custPricing.orgPricing.gainshare === 'YES';
            $scope.custPricing.orgPricing.includeBenchmarkAccessorial = $scope.custPricing.orgPricing.includeBenchmarkAcc === 'YES';

            $scope.custPricing.orgPricing.blkIndirectSrcTyp = $scope.custPricing.orgPricing.blkServCarrierType !== 'NONE'
                && $scope.custPricing.orgPricing.blkServCarrierType !== null && $scope.custPricing.orgPricing.blkServiceType === 'INDIRECT';
        }

        $scope.benchmarkCriteria = {
            status: 'ACTIVE', //Default Status - Active => the first tab.
            pricingGroup: "SHIPPER",
            pricingTypes: ["BENCHMARK"],
            customer: customerId
        };

        $scope.loadBMProfilesByCriteria = function (status) {
            $scope.benchmarkCriteria.status = status;

            if (status === 'ACTIVE') {
                $scope.profileNextStatus = "Inactivate";
            } else {
                $scope.profileNextStatus = "Reactivate";
            }

            ProfilesListService.get({}, $scope.benchmarkCriteria, function (profiles) {
                $scope.benchmarkProfileList = profiles;
            });
        };

        CustomerPricingService.get({
            id: customerId
        }, setCustPricingDetails);

        $scope.loadBMProfilesByCriteria('ACTIVE');

        $scope.selectedBMItems = [];

        $scope.editBMProfile = function () {
            var profileID = $scope.selectedBMItems[0].ltlPricingProfileId;

            if (profileID !== null && profileID !== undefined && profileID !== '' && profileID !== 'undefined') {
                $location.url('/pricing/tariffs/' + profileID + '/editbm?profile.details');
            }
        };

        $scope.addBMProfile = function () {
            $location.url('/pricing/tariffs/' + customerId + '/addbm?profile.details');
        };

        $scope.copyBMProfile = function () {
            var profileID = $scope.selectedBMItems[0].ltlPricingProfileId;

            if (profileID !== null && profileID !== undefined && profileID !== '' && profileID !== 'undefined') {
                $location.url('/pricing/tariffs/' + profileID + '/cpy?profile.details');
            }
        };

        $scope.updateBMProfileStatus = function () {
            switch ($scope.benchmarkCriteria.status) {
                case 'ACTIVE':
                    ProfileStatusChangeService.inactivate({
                            ids: $scope.selectedBMItems[0].ltlPricingProfileId,
                            isActiveList: true
                        }, $scope.benchmarkCriteria, function (response) {
                            $scope.benchmarkProfileList = response;
                        });
                    break;
                case 'INACTIVE':
                    ProfileStatusChangeService.reactivate({
                            ids: $scope.selectedBMItems[0].ltlPricingProfileId
                        }, $scope.benchmarkCriteria, function (response) {
                            $scope.benchmarkProfileList = response;
                        });
                    break;
            }
        };

        $scope.shouldDisableMarginFieds = function() {
            return $scope.custPricing && $scope.custPricing.goShipBusinessUnit && !$scope.$root.isFieldRequired("CAN_EDIT_GOSHIP_PRICING");
        };

        $scope.benchmarkProfilesGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedBMItems,
            data: 'benchmarkProfileList',
            multiSelect: false,
            columnDefs: [
                {
                    field: 'scac',
                    displayName: 'SCAC',
                    width: '10%'
                }, {
                    field: 'carrierName',
                    displayName: 'Carrier',
                    width: '36%'
                }, {
                    field: 'rateName',
                    displayName: 'Rate name',
                    width: '30%'
                }, {
                    field: 'effDate',
                    displayName: 'Effective',
                    cellFilter: 'date:appDateFormat',
                    width: '12%'
                }, {
                    field: 'expDate',
                    displayName: 'End',
                    cellFilter: 'date:appDateFormat',
                    width: '12%'
                }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.setProfileBlockedValue = function ($event, id) {
            var checkbox = $event.target;

            if (checkbox.checked) {
                $scope.unassignCriteria.users.push(id);
            } else {
                $scope.unassignCriteria.users.splice($scope.unassignCriteria.users.indexOf(id), 1);
            }
        };

    var tier1CheckboxCellTemplate='<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" '+
                                  'data-ng-model="row.entity.tier1" data-ng-disabled=\'{{updateDfltMrgnAndCarrBlockAllowed}}\'</div>';

    var blanketLinkCellTemplate='<div class="ngSelectionCell" data-ng-show="_.contains([\'BLANKET\'], row.getProperty(\'pricingType\')) && ' +
                                'row.getProperty(\'carrierType\') !== \'CARRIER_API\'"> '+ 
                                '<a href="" tabindex="-1" data-ng-click="updateDfltMrgnAndCarrBlockAllowed?(false):'+
                                '(overrideProfile($event, row.getProperty(\'ltlPricingProfileId\')))">Override</a></div>'; 

        $scope.toggleColumns = function () {
            $scope.custPricingProfilesGrid.$gridScope.columns[6].visible = $scope.custPricing.orgPricing.gainshareAccount;
            $scope.custPricingProfilesGrid.$gridScope.columns[7].visible = $scope.custPricing.orgPricing.gainshareAccount;
        };

        $scope.$watch('custPricing', function () {
            if (!_.isUndefined($scope.custPricing)) {
                $scope.toggleColumns();
            }
        });

        $scope.custPricingProfilesGrid = {
            enableColumnResize: true,
            data: 'custPricing.pricingProfiles',
            multiSelect: false,
            enableCellSelection: true,
            enableRowSelection: false,
            columnDefs: [
                {
                    field: 'ltlPricingProfileId',
                    displayName: 'Profile ID',
                    width: '3%'
                },
                {
                    field: 'carrierName',
                    displayName: 'PLS Carrier',
                    width: '37%'
                },
                {
                    field: 'effDate',
                    displayName: 'Start',
                    width: '7%',
                    cellFilter: 'date:appDateFormat'
                },
                {
                    field: 'expDate',
                    displayName: 'End',
                    width: '7%',
                    cellFilter: 'date:appDateFormat'
                },
                {
                    field: 'blocked',
                    displayName: 'Block',
                    cellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" '
                    + 'type="checkbox" data-ng-model="row.entity.blocked" '
                    + 'data-ng-disabled="!$root.isFieldRequired(\'UPDATE_CARRIER_BLOCK\') && '
                    + '!$root.isFieldRequired(\'PRICING_PAGE_VIEW\')"</div>',
                    width: '5%',
                    searchable: false
                },
                {
                    field: 'tier1',
                    displayName: 'Tier1',
                    cellTemplate: tier1CheckboxCellTemplate,
                    width: '5%',
                    searchable: false
                },
                {
                    field: 'pricingTypeDesc',
                    displayName: 'Pricing',
                    width: '15%'
                },
                {
                    field: 'gsPlsPercent',
                    displayName: 'PLS %',
                    width: '7%',
                    visible: false,
                    enableCellEdit: true,
                    editableCellTemplate: "<div><div data-ng-if=\"row.entity.pricingType == 'BLANKET'\"><div class="
                    + "\"ngCellText\" ng-class=\"col.colIndex()\"><span data-ng-cell-text>{{row.getProperty(col.field)}}"
                    + "</span></div></div><div data-ng-if=\"row.entity.pricingType != 'BLANKET'\"><input data-ng-class=\"'colt' + col.index\" "
                    + "ng-input=\"COL_FIELD\" data-pls-number ng-model=\"COL_FIELD\"></div></div>"
                },
                {
                    field: 'gsCustomerPercent',
                    displayName: 'Customer %',
                    width: '7%',
                    visible: false,
                    enableCellEdit: true,
                    editableCellTemplate: "<div><div data-ng-if=\"row.entity.pricingType == 'BLANKET'\"><div class="
                    + "\"ngCellText\" ng-class=\"col.colIndex()\"><span data-ng-cell-text>{{row.getProperty(col.field)}}"
                    + "</span></div></div><div data-ng-if=\"row.entity.pricingType != 'BLANKET'\"><input data-ng-class=\"'colt' + col.index\" "
                    + "ng-input=\"COL_FIELD\" data-pls-number ng-model=\"COL_FIELD\"></div></div>"
                },
                {
                    field: 'viewOverriddenProfile',
                    displayName: 'Override',
                    cellTemplate: blanketLinkCellTemplate,
                    width: '5%',
                    visible: !$scope.updateDfltMrgnAndCarrBlockAllowed,
                    searchable: false
                }
            ],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin()],
            progressiveSearch: true
        };

        $scope.setCustomerMargin = function () {
            $location.url('/pricing/tariffs/' + customerId + '/cm?profile.details');
        };

        $scope.blockLane = function () {
            $location.url('/pricing/customer/' + customerId + '/blockLane');
        };

        $scope.overrideProfile = function ($event, id) {
            $location.url('/pricing/tariffs/' + id + '/cpyBk?profile.details');
        };

        $scope.submit = function () {
            $scope.custPricing.orgPricing.status = ($scope.custPricing.orgPricing.activateCustPricing ? 'ACTIVE' : 'INACTIVE');
            $scope.custPricing.orgPricing.gainshare = ($scope.custPricing.orgPricing.gainshareAccount ? 'YES' : 'NO');
            $scope.custPricing.orgPricing.includeBenchmarkAcc = ($scope.custPricing.orgPricing.includeBenchmarkAccessorial ? 'YES' : 'NO');

            // TODO this should be simplified in DB
            if ($scope.custPricing.orgPricing.blkIndirectSrcTyp) {
                $scope.custPricing.orgPricing.blkServiceType = 'INDIRECT';
                $scope.custPricing.orgPricing.blkServCarrierType = 'ALL_CARRIER';
            } else {
                $scope.custPricing.orgPricing.blkServCarrierType = 'NONE';
            }

            var success = function (response) {
                $scope.$root.$emit('event:operation-success', 'Success saving customer pricing');
                setCustPricingDetails(response);
            };

            var failure = function (response) {
                $scope.$root.$emit('event:application-error', 'Failed saving customer pricing!');

                if (response && response.data && response.data.payload && response.data.payload.errors) {
                    angular.forEach(response.data.payload.errors, function (value, key) {
                        if (key === 'alias name') {
                            // add validation error to the input model and move
                            // focus to this field.
                            var input = angular.element('#inputDisplayName');
                            var inputModel = input.controller('ngModel');

                            inputModel.$setValidity(value, false);
                            input.focus();
                        }
                    });
                }
            };

            $scope.custPricing.$save(success, failure);
        };

        $scope.backToCustomerScreen = function () {
            $location.url('/pricing/customer/');
        };
    }
]);
