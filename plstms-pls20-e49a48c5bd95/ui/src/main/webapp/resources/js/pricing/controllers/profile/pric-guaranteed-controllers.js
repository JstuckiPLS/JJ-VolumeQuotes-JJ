angular.module('plsApp').controller('GuaranteedCtrl', ['$scope', '$filter', '$routeParams', 'ChangeStatusGuaranteedService', 'GetGuaranteedService',
    'NgGridPluginFactory', 'PricingDetailsDictionaryService', 'ProfileDetailsService','ShipmentUtils', 'NgGridService',
    function ($scope, $filter, $routeParams, ChangeStatusGuaranteedService, GetGuaranteedService, NgGridPluginFactory,
              PricingDetailsDictionaryService, ProfileDetailsService, ShipmentUtils, NgGridService) {
        'use strict';

        $scope.hideForMargin = false;
        $scope.isEditGuaranteed = false;
        $scope.isSell = false;

        $scope.guaranteedTimeOptions = ShipmentUtils.getGuaranteedTimeOptions(-1);

        if ($scope.pricingType === 'BUY_SELL') {
            if ($scope.profileDetails.ltlPricingType === 'BLANKET') {
                $scope.showCostPanel = true;
                $scope.showMarginPanel = false;
                $scope.disabledCostPanel = false;
            } else if ($scope.profileDetails.ltlPricingType === 'BLANKET_CSP') {
                $scope.showCostPanel = true;
                $scope.showMarginPanel = true;
                $scope.disabledCostPanel = true;
            } else if ($scope.profileDetails.ltlPricingType === 'CSP') {
                $scope.showCostPanel = true;
                $scope.showMarginPanel = true;
                $scope.disabledCostPanel = false;
            } else if ($scope.profileDetails.ltlPricingType === 'MARGIN') {
                $scope.showCostPanel = false;
                $scope.showMarginPanel = true;
                $scope.hideForMargin = true;
                $scope.disabledCostPanel = false;
            } else if ($scope.profileDetails.ltlPricingType === 'BENCHMARK') {
                $scope.showCostPanel = true;
                $scope.showMarginPanel = false;
                $scope.disabledCostPanel = false;
            }
        } else if ($scope.pricingType === 'BUY') {
            $scope.showCostPanel = true;
            $scope.showMarginPanel = false;
            $scope.disabledCostPanel = false;
        } else if ($scope.pricingType === 'SELL') {
            $scope.showCostPanel = true;
            $scope.showMarginPanel = true;
            $scope.hideForMargin = false;
            $scope.disabledCostPanel = false;
            $scope.isSell = true;
        }

        $scope.tabf1 = 'active';
        $scope.price = new ChangeStatusGuaranteedService();
        $scope.price.chargeRuleType = "PC";
        $scope.price.time = "-1";

        $scope.statusFlag = "ACTIVE";
        $scope.statusTab = "Active";

        $scope.selectedItems = [];
        $scope.selectBlocked = [];
        $scope.selectedItemsBlockDestinations = [];

        $scope.selectedBlocked = "";

        $scope.applyBeforeFuel = false;
        var isEditMode = false;

        var getID = function () {
            var arr = [];

            $.each($scope.selectedItems, function (key, value) {
                arr.push(value.id);
            });

            return arr;
        };

        var parseGuaranteedList = function (list) {
            _.each(list, function (value) {
                switch (value.chargeRuleType) {
                    case "FL":
                        value.plsCost = $filter('plsCurrency')(value.unitCost);
                        break;
                    case "PC":
                        value.plsCost = (value && value.unitCost) ? value.unitCost + '%' : '';
                        break;
                }
                switch (value.time) {
                    case -1:
                        value.displayTime = 'Any Time';
                        break;
                    case 0:
                        value.displayTime = '12:00 AM';
                        break;
                    case 30:
                        value.displayTime = '12:30 AM';
                        break;
                    case 100:
                        value.displayTime = '01:00 AM';
                        break;
                    case 130:
                        value.displayTime = '01:30 AM';
                        break;
                    case 200:
                        value.displayTime = '02:00 AM';
                        break;
                    case 230:
                        value.displayTime = '02:30 AM';
                        break;
                    case 300:
                        value.displayTime = '03:00 AM';
                        break;
                    case 330:
                        value.displayTime = '03:30 AM';
                        break;
                    case 400:
                        value.displayTime = '04:00 AM';
                        break;
                    case 430:
                        value.displayTime = '04:30 AM';
                        break;
                    case 500:
                        value.displayTime = '05:00 AM';
                        break;
                    case 530:
                        value.displayTime = '05:30 AM';
                        break;
                    case 600:
                        value.displayTime = '06:00 AM';
                        break;
                    case 630:
                        value.displayTime = '06:30 AM';
                        break;
                    case 700:
                        value.displayTime = '07:00 AM';
                        break;
                    case 730:
                        value.displayTime = '07:30 AM';
                        break;
                    case 800:
                        value.displayTime = '08:00 AM';
                        break;
                    case 830:
                        value.displayTime = '08:30 AM';
                        break;
                    case 900:
                        value.displayTime = '09:00 AM';
                        break;
                    case 930:
                        value.displayTime = '09:30 AM';
                        break;
                    case 1000:
                        value.displayTime = '10:00 AM';
                        break;
                    case 1030:
                        value.displayTime = '10:30 AM';
                        break;
                    case 1100:
                        value.displayTime = '11:00 AM';
                        break;
                    case 1130:
                        value.displayTime = '11:30 AM';
                        break;
                    case 1200:
                        value.displayTime = '12:00 PM';
                        break;
                    case 1230:
                        value.displayTime = '12:30 PM';
                        break;
                    case 1300:
                        value.displayTime = '01:00 PM';
                        break;
                    case 1330:
                        value.displayTime = '01:30 PM';
                        break;
                    case 1400:
                        value.displayTime = '02:00 PM';
                        break;
                    case 1430:
                        value.displayTime = '02:30 PM';
                        break;
                    case 1500:
                        value.displayTime = '03:00 PM';
                        break;
                    case 1530:
                        value.displayTime = '03:30 PM';
                        break;
                    case 1600:
                        value.displayTime = '04:00 PM';
                        break;
                    case 1630:
                        value.displayTime = '04:30 PM';
                        break;
                    case 1700:
                        value.displayTime = '05:00 PM';
                        break;
                    case 1730:
                        value.displayTime = '05:30 PM';
                        break;
                    case 1800:
                        value.displayTime = '06:00 PM';
                        break;
                    case 1830:
                        value.displayTime = '06:30 PM';
                        break;
                    case 1900:
                        value.displayTime = '07:00 PM';
                        break;
                    case 1930:
                        value.displayTime = '07:30 PM';
                        break;
                    case 2000:
                        value.displayTime = '08:00 PM';
                        break;
                    case 2030:
                        value.displayTime = '08:30 PM';
                        break;
                    case 2100:
                        value.displayTime = '09:00 PM';
                        break;
                    case 2130:
                        value.displayTime = '09:30 PM';
                        break;
                    case 2200:
                        value.displayTime = '10:00 PM';
                        break;
                    case 2230:
                        value.displayTime = '10:30 PM';
                        break;
                    case 2300:
                        value.displayTime = '11:00 PM';
                        break;
                    case 2330:
                        value.displayTime = '11:30 PM';
                        break;
                }
            });

            return list;
        };

        function initialize() {
            PricingDetailsDictionaryService.get({}, function (response) {
                $scope.detailsDictionary = response;
            });
        }

        initialize();

        $scope.clear = function () {
            NgGridService.refreshGrid($scope.gridOptions);
            $scope.selectedItems.length = 0;
            $scope.selectBlocked.length = 0;
            $scope.selectedItemsBlockDestinations.length = 0;

            $scope.price = new ChangeStatusGuaranteedService();
            $scope.price.chargeRuleType = "PC";
            $scope.price.time = "-1";

            $scope.selectedBlocked = "";
            $scope.blockedText = "";
            $scope.originText = "";

            $scope.applyBeforeFuel = false;
            $scope.isEditGuaranteed = false;

        };

        function setArchiveButtonName(name) {
            $scope.archiveButtonName = name;
        }

        $scope.loadGuaranteed = function (status) {
            $scope.clear();

            switch (status) {
                case 'Active':
                    GetGuaranteedService.active({id: $scope.profileDetailId}, function (response) {
                        $scope.guaranteedList = parseGuaranteedList(response);
                        $scope.statusFlag = "ACTIVE";
                        $scope.statusTab = "Active";
                        setArchiveButtonName("Archive");
                        $scope.archiveButtonAction = $scope.inactivateGuaranteed;
                    });
                    break;
                case 'Expired':
                    GetGuaranteedService.expired({id: $scope.profileDetailId}, function (response) {
                        $scope.guaranteedList = parseGuaranteedList(response);
                        $scope.statusFlag = "ACTIVE";
                        $scope.statusTab = "Expired";
                        setArchiveButtonName("Archive");
                        $scope.archiveButtonAction = $scope.inactivateGuaranteed;
                    });
                    break;
                case 'Archived':
                    GetGuaranteedService.inactive({id: $scope.profileDetailId}, function (response) {
                        $scope.guaranteedList = parseGuaranteedList(response);
                        $scope.statusFlag = "INACTIVE";
                        $scope.statusTab = "Archived";
                        setArchiveButtonName("Reactivate");
                        $scope.archiveButtonAction = $scope.reactivateGuaranteed;
                    });
                    break;
            }
        };

        $scope.loadGuaranteed('Active');

        $scope.inactivateGuaranteed = function () {
            var isActive = $scope.statusTab === "Active" ? true : false;
            ChangeStatusGuaranteedService.inactivate({
                profileDetailId: $scope.profileDetailId,
                isActiveList: isActive
            }, getID(), function (response) {
                $scope.guaranteedList = parseGuaranteedList(response);
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Guaranteed was archived');
            });
        };

        $scope.reactivateGuaranteed = function () {
            ChangeStatusGuaranteedService.reactivate({
                profileDetailId: $scope.profileDetailId
            }, getID(), function (response) {
                $scope.guaranteedList = parseGuaranteedList(response);
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Guaranteed was activated');
            });
        };

        $scope.expireGuaranteed = function () {
            ChangeStatusGuaranteedService.expire({
                profileDetailId: $scope.profileDetailId
            }, getID(), function (response) {
                $scope.guaranteedList = parseGuaranteedList(response);
                $scope.clear();
                $scope.$root.$emit('event:operation-success', 'Guaranteed was Expired');
            });
        };

        $scope.gridOptions = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            multiSelect: false,
            data: 'guaranteedList',
            columnDefs: [{
                field: 'id',
                displayName: 'ID'
            }, {
                field: 'plsCost',
                displayName: 'PLS Cost'
            }, {
                field: 'displayTime',
                displayName: 'Time'
            }, {
                field: 'minCost',
                displayName: 'Min Cost'
            }],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            progressiveSearch: true,
            rowTemplate: "<div  ng-dblclick='onGridDoubleClick(row)' ng-style=\"{ 'cursor': row.cursor }\" " +
            "ng-repeat=\"col in renderedColumns\" ng-class=\"col.colIndex()\" class=\"ngCell {{col.cellClass}}\">" +
            "   <div class=\"ngVerticalBar\" ng-style=\"{height: rowHeight}\" ng-class=\"{ ngVerticalBarVisible: !$last }\">&nbsp;</div>" +
            "   <div ng-cell></div>" +
            "</div>"
        };

        $scope.gridBlockDestinationsOptions = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItemsBlockDestinations,
            multiSelect: false,
            data: 'price.guaranteedBlockDestinations',
            columnDefs: [{
                field: 'origin',
                displayName: 'Origin'
            }, {
                field: 'destination',
                displayName: 'Destination'
            }],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            progressiveSearch: true
        };

        $scope.onGridDoubleClick = function () {
            $scope.edit();
        };

        $scope.blockedEdit = function () {
            $('#buttonSet').removeAttr('disabled');
            $scope.blockedText = $scope.selectedItemsBlockDestinations[0].destination;
            $scope.originText = $scope.selectedItemsBlockDestinations[0].origin;
            isEditMode = true;
        };

        $scope.blockedSet = function () {
            if (!isEditMode) {
                var item = {};

                item.destination = $scope.blockedText.toUpperCase();
                item.origin = $scope.originText.toUpperCase();

                if (_.isUndefined($scope.price.guaranteedBlockDestinations)) {
                    $scope.price.guaranteedBlockDestinations = [];
                }

                $scope.price.guaranteedBlockDestinations.push(item);
            } else {
                if ($scope.selectedItemsBlockDestinations.length === 1) {
                    $scope.selectedItemsBlockDestinations[0].destination = $scope.blockedText;
                    $scope.selectedItemsBlockDestinations[0].origin = $scope.originText;
                }
            }

            $scope.selectedItemsBlockDestinations.length = 0;
            $scope.blockedText = "";
            $scope.originText = "";
            isEditMode = false;
        };

        $scope.blockedAdd = function () {
            var item = {};

            item.destination = $scope.blockedText;
            item.origin = $scope.originText;
            item.guaranteedPriceId = $scope.selectedBlocked.id;

            if (!$scope.price.guaranteedBlockDestinations) {
                $scope.price.guaranteedBlockDestinations = [];
            }

            $scope.price.guaranteedBlockDestinations.push(item);
        };

        $scope.blockedDelete = function () {
            $scope.price.guaranteedBlockDestinations.splice($scope.price.guaranteedBlockDestinations
                    .indexOf($scope.selectedItemsBlockDestinations[0]), 1);
            $scope.selectedItemsBlockDestinations.length = 0;
            $scope.blockedText = "";
            $scope.originText = "";
        };

        var refresh = function () {
            switch ($scope.statusTab) {
                case 'Active':
                    $scope.loadGuaranteed('Active');
                    break;
                case 'Expired':
                    $scope.loadGuaranteed('Expired');
                    break;
                case 'Archived':
                    $scope.loadGuaranteed('Archived');
                    break;
            }

            $scope.selectedBlocked = null;
            $scope.blockedText = "";
        };

        $scope.edit = function () {
            if ($scope.statusTab !== "Archived") {
                $scope.price.$get({url: $scope.selectedItems[0].id}, function () {
                    $scope.selectedBlocked = $scope.price.guaranteedBlockDestinations[0];
                    $scope.statusFlag = $scope.price.status;
                    $scope.isEditGuaranteed = true;
                    $scope.selectedItemsBlockDestinations.length = 0;
                    $scope.selectedBlocked = "";
                    $scope.blockedText = "";
                    $scope.originText = "";
                });
            }
        };

        $scope.save = function () {
            if (($scope.price.minCost && $scope.price.maxCost) && $scope.price.minCost > $scope.price.maxCost) {
                $scope.$root.$emit('event:application-error', 'Saving Guaranteed failed!',
                'Min Cost should be less than Max Cost.');
                return;
            }
            if ($scope.showCostPanel && !$scope.disabledCostPanel) {
                if (!$scope.price.unitCost) {
                    $scope.$root.$emit('event:application-error', 'Saving Guaranteed failed!', 'Please fill PLS Cost.');
                    return;
                }
            }

            $scope.price.status = $scope.statusFlag;
            $scope.price.ltlPricProfDetailId = $scope.profileDetailId;

            if (!$scope.price.effDate) {
                ProfileDetailsService.get({id: $routeParams.pricingId}, function (profile) {
                    $scope.price.effDate = profile.effDate;
                    $scope.processSaving();
                });
            } else {
                $scope.processSaving();
            }
        };

        $scope.processSaving = function () {
            $scope.price.$save({url: 'save'}, function () {
                refresh();
                $scope.$root.$emit('event:operation-success', 'Guaranteed successfully saved!');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Guaranteed save failed!');
            });
        };

        $scope.saveAsNew = function () {
            $scope.price.id = undefined;
            $scope.price.version = undefined;

            _.each($scope.price.guaranteedBlockDestinations, function (item) {
                item.id = undefined;
                item.version = undefined;
            });

            $scope.save();
        };

        var cloneByProfileId = function () {
            ChangeStatusGuaranteedService.copy({
                profileId: $scope.profileDetailId,
                profileToCopy: $scope.selectedRateToCopy
            }, function () {
                $scope.loadGuaranteed('Active');
                $scope.$root.$emit('event:operation-success', 'Guaranteed was successfully copied');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Guaranteed copying failed!');
            });
        };

        $scope.okClick = function () {
            cloneByProfileId($scope.selectedRateToCopy);
        };

        $scope.openDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', confirmButtonLabel: 'Copy',
                message: 'Copying will override all current guaranteed information created. Do you want to continue?', okFunction: $scope.okClick
            });
        };
    }
]);
