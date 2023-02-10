angular.module('plsApp').controller('ProfilesListCtrl', ['$scope', '$location', '$route', 'ProfileDictionaryService', 'ProfilesListService',
    'ProfileApplicableCustomersService', 'ProfileStatusChangeService', 'NgGridPluginFactory', 'ProfileApplicableCustomersForSMC3Service',
    function ($scope, $location, $route, ProfileDictionaryService, ProfilesListService,
              ProfileApplicableCustomersService, ProfileStatusChangeService, NgGridPluginFactory, ProfileApplicableCustomersForSMC3Service) {
        'use strict';

        $scope.profileModel = {
            criteria: {
                pricingGroup: 'CARRIER',
                dateType: 'EFFECTIVE',
                dateRange: 'NONE',
                status: $route.current.active ? 'ACTIVE' : 'INACTIVE'
            },
            profileNextStatus: 'Inactivate',
            searchDateTypes: [{id: 'NONE', label: ''},
                {id: 'EFFECTIVE', label: 'Effective'},
                {id: 'EXPIRATION', label: 'Expires'}],
            searchDateRanges: [{id: 'NONE', label: ''},
                {id: 'TODAY', label: 'Today'},
                {id: 'THIS_WEEK', label: 'This Week'},
                {id: 'LAST_WEEK', label: 'Last Week'},
                {id: 'CUSTOM', label: 'Custom'}]
        };

        var isCanSearch = function () {
            var isCanSearchDateRange = function () {
                if ($scope.profileModel.criteria.dateType && $scope.profileModel.criteria.dateRange !== 'NONE') {
                    return $scope.profileModel.criteria.dateRange && $scope.profileModel.criteria.dateType === 'CUSTOM'
                            && (_.isUndefined($scope.profileModel.criteria.fromDate) || _.isUndefined($scope.profileModel.criteria.toDate));
                }

                return false;
            };

            var isCanSearchPricingType = function () {
                return !_.isEmpty($scope.profileModel.criteria.pricingTypes);
            };

            var isCanSearchCustomer = function () {
                return $scope.profileModel.customer && $scope.profileModel.customer.selected && !_.isEmpty($scope.profileModel.customer.selected);
            };

            return isCanSearchDateRange() || isCanSearchPricingType() || isCanSearchCustomer();
        };

        var loadProfiles = function () {
            if (isCanSearch()) {
                ProfilesListService.get({}, $scope.profileModel.criteria, function (profiles) {
                    $scope.profileList = profiles;
                    $scope.applicableCustomers = {};
                    $scope.applicableCustomersName = [];
                });
            }
        };

        function convertForDatePicker(date) {
            if (date && angular.isDate(date)) {
                return date.getTime();
            }

            return date;
        }

        $scope.searchByDateRange = function () {
            if ($scope.profileModel.criteria.dateRange !== 'CUSTOM') {
                $scope.profileModel.criteria.fromDateTmp = null;
                $scope.profileModel.criteria.toDateTmp = null;
            }

            $scope.profileModel.criteria.fromDate = convertForDatePicker($scope.profileModel.criteria.fromDateTmp);
            $scope.profileModel.criteria.toDate = convertForDatePicker($scope.profileModel.criteria.toDateTmp);

            if ($scope.profileModel.criteria.dateType === null ||
                    $scope.profileModel.criteria.dateType === undefined ||
                    $scope.profileModel.criteria.dateType === '' ||
                    $scope.profileModel.criteria.dateType === 'undefined' ||
                    $scope.profileModel.criteria.dateType === 'NONE') {
                $scope.profileModel.criteria.dateRange = 'NONE';
            }

            if ($scope.profileModel.criteria.dateType !== null &&
                    $scope.profileModel.criteria.dateType !== undefined &&
                    $scope.profileModel.criteria.dateType !== '' &&
                    $scope.profileModel.criteria.dateType !== 'undefined' &&
                    $scope.profileModel.criteria.dateType !== 'NONE'
                    && $scope.profileModel.criteria.dateRange !== null &&
                    $scope.profileModel.criteria.dateRange !== undefined
                    && $scope.profileModel.criteria.dateRange !== '' &&
                    $scope.profileModel.criteria.dateRange !== 'undefined' &&
                    $scope.profileModel.criteria.dateRange !== 'NONE') {
                var now = new Date();

                switch ($scope.profileModel.criteria.dateRange) {
                    case 'TODAY':
                        var fromDateValue = new Date();
                        fromDateValue.setHours(0, 0, 0, 0);
                        var toDateValue = new Date();
                        toDateValue.setHours(23, 59, 59, 0);
                        $scope.profileModel.criteria.fromDate = convertForDatePicker(fromDateValue);
                        $scope.profileModel.criteria.toDate = convertForDatePicker(toDateValue);
                        break;
                    case 'THIS_WEEK':
                        now.setHours(0, 0, 0, 0);
                        var previousSunday = new Date(now);
                        previousSunday.setDate(previousSunday.getDate() - previousSunday.getDay());
                        var nextSaturday = new Date(now);
                        nextSaturday.setDate(nextSaturday.getDate() - nextSaturday.getDay() + 6);
                        nextSaturday.setHours(23, 59, 59, 0);

                        $scope.profileModel.criteria.fromDate = convertForDatePicker(previousSunday);
                        $scope.profileModel.criteria.toDate = convertForDatePicker(nextSaturday);
                        break;
                    case 'LAST_WEEK':
                        now.setHours(0, 0, 0, 0);

                        var previousWeekSunday = new Date(now);
                        previousWeekSunday.setDate(previousWeekSunday.getDate() - previousWeekSunday.getDay() - 7);
                        var previousSaturday = new Date(now);
                        previousSaturday.setDate(previousSaturday.getDate() - previousSaturday.getDay() - 1);
                        previousSaturday.setHours(23, 59, 59, 0);

                        $scope.profileModel.criteria.fromDate = convertForDatePicker(previousWeekSunday);
                        $scope.profileModel.criteria.toDate = convertForDatePicker(previousSaturday);
                        break;
                }

                if ($scope.profileModel.criteria.fromDate !== null && $scope.profileModel.criteria.fromDate !== undefined
                        && $scope.profileModel.criteria.fromDate !== '' && $scope.profileModel.criteria.fromDate !== 'undefined'
                        && $scope.profileModel.criteria.toDate !== null && $scope.profileModel.criteria.toDate !== undefined
                        && $scope.profileModel.criteria.toDate !== '' && $scope.profileModel.criteria.toDate !== 'undefined') {
                    $scope.loadProfilesByCriteria($scope.profileModel.criteria.status);
                }

            } else {
                $scope.profileModel.criteria.fromDate = null;
                $scope.loadProfilesByCriteria($scope.profileModel.criteria.status);
            }
        };

        function checkAllPricingTypes() {
            $scope.profileModel.criteria.pricingTypes = [];

            _.each($scope.profileDictionary.pricingTypes, function (type) {
                $scope.profileModel.criteria.pricingTypes.push(type.ltlPricingType);
                type.selected = true;
            });
        }

        $scope.selectAll = function ($event) {
            var checkbox = $event.target;

            if (checkbox.checked) {
                checkAllPricingTypes();
            } else {
                _.each($scope.profileDictionary.pricingTypes, function (type) {
                    type.selected = false;
                });

                $scope.profileList = null;
            }
        };

        $scope.$watch('profileDictionary.pricingTypes', function (newValue) {
            $scope.profileModel.criteria.pricingTypes = [];

            _.each(newValue, function (pricingType) {
                if (pricingType.selected) {
                    $scope.profileModel.criteria.pricingTypes.push(pricingType.ltlPricingType);
                }
            });

            /* By default all pricing types are cheked*/
            if ($scope.allPricingTypes === undefined) {
                $scope.allPricingTypes = true;

                ProfileDictionaryService.get(function (response) {
                    $scope.profileDictionary = response;
                    checkAllPricingTypes();
                });
            } else {
                /* Check 'All' checkbox if all pricing types selected*/
                if ($scope.profileDictionary && $scope.profileModel.criteria.pricingTypes.length === $scope.profileDictionary.pricingTypes.length) {
                    $scope.allPricingTypes = true;
                }
                /* Uncheck 'All' checkbox if at least one of the other checkboxes unselected */
                if ($scope.profileDictionary && $scope.profileModel.criteria.pricingTypes.length !== $scope.profileDictionary.pricingTypes.length &&
                        $scope.allPricingTypes) {
                    $scope.allPricingTypes = false;
                }
            }
            /* Load pricing profiles if any pricing type selected */
            if ($scope.allPricingTypes || ($scope.profileDictionary && $scope.profileModel.criteria.pricingTypes.length > 0)
                    || isCanSearch()) {
                $scope.loadProfilesByCriteria($scope.profileModel.criteria.status);
            }
            /* Empty profile grid if no pricing types selected */
            if (_.isEmpty($scope.profileModel.criteria.pricingTypes)) {
                $scope.profileList = null;
            }

        }, true);

        $scope.loadProfilesByCriteria = function (status) {
            if (status !== null && status !== undefined && status !== '' && status !== 'undefined') {
                $scope.profileModel.criteria.status = status;
                if (status === 'ACTIVE') {
                    $scope.profileModel.profileNextStatus = "Inactivate";
                } else {
                    $scope.profileModel.profileNextStatus = "Reactivate";
                }
            }

            loadProfiles();
            $scope.selectedItems.length = 0;
        };

        $scope.$watch('profileModel.customer.selected.id', function (val, oldVal) {
            if (_.isUndefined(val)) {
                $scope.profileList = null;
            }

            if (val !== oldVal) {
                $scope.profileModel.criteria.customer = val;
                loadProfiles();
            }
        }, true);

        $scope.isNotPricingTariffSelected = function() {
            return !$scope.selectedItems.length || !$scope.selectedItems[0].ltlPricingProfileId;
        };

        $scope.editProfile = function () {
            if ($scope.profileModel.criteria.status === 'ACTIVE') {
                $location.url('/pricing/tariffs/' + $scope.selectedItems[0].ltlPricingProfileId + '/edit?profile.details');
            }
        };

        $scope.copyProfile = function () {
            if ($scope.profileModel.criteria.status === 'ACTIVE') {
                $location.url('/pricing/tariffs/' + $scope.selectedItems[0].ltlPricingProfileId + '/cpy?profile.details');
            }
        };

        $scope.addProfile = function () {
            $location.url('/pricing/tariffs/-1/add?profile.details');
        };

        $scope.isCopyProhibited = function() {
            return $scope.profileModel.criteria.status !== 'ACTIVE' || !$scope.selectedItems.length || !$scope.selectedItems[0].ltlPricingProfileId
                    || $scope.selectedItems[0].pricingType.toUpperCase() === 'BLANKET/CSP';
        };

        $scope.updateProfileStatus = function () {
            switch ($scope.profileModel.criteria.status) {
                case 'ACTIVE':
                    ProfileStatusChangeService.inactivate({
                        ids: $scope.selectedItems[0].ltlPricingProfileId,
                        isActiveList: true
                    }, $scope.profileModel.criteria, function (response) {
                        $scope.profileList = response;
                        $scope.$root.$emit('event:operation-success', 'Profile ' + $scope.selectedItems[0].rateName + '  has been deactivated.');
                        $scope.selectedItems.length = 0;
                    });
                    break;
                case 'INACTIVE':
                    ProfileStatusChangeService.reactivate({
                        ids: $scope.selectedItems[0].ltlPricingProfileId
                    }, $scope.profileModel.criteria, function (response) {
                        $scope.profileList = response;
                        $scope.$root.$emit('event:operation-success', 'Profile ' + $scope.selectedItems[0].rateName + ' has been activated.');
                        $scope.selectedItems.length = 0;
                    }, function (response) {
                        if (response && response.data && response.data.errorMsg) {
                            $scope.$root.$emit('event:application-error', 'Reactivating Profiles validation failed!', response.data.errorMsg);
                        } else {
                            $scope.$root.$emit('event:application-error', 'Reactivating Profiles failed!', '');
                        }
                    });
                    break;
            }
        };

        $scope.selectedItems = [];
        $scope.profCustomersSelectedItem = [];

        function getCustomersName(listOfNames){
            $scope.profCustomersSelectedItem.length = 0;
            return _.map(listOfNames, function(customer) {
                return {'name': customer.name ? customer.name : customer};
            });
        }

        function loadApplicableCustomers(rowItem) {
            if (rowItem.selected === true) {
                if (rowItem.entity.pricingType === "SMC3") {
                    ProfileApplicableCustomersForSMC3Service.getSMC3(rowItem.entity.smc3TariffName).then(function(response) {
                        $scope.applicableCustomersName = getCustomersName(response.data);
                    });
                } else {
                    ProfileApplicableCustomersService.get({
                        id: $scope.selectedItems[0].ltlPricingProfileId
                    }, function (applicableCustomers) {
                        $scope.applicableCustomers = applicableCustomers;
                        var customer = _.pluck($scope.applicableCustomers, 'customer');
                        $scope.applicableCustomersName = getCustomersName(customer);
                    });
                }
            }
        }

        var benchmarkCellTemplate = '<div class="ngCellText" data-ng-if="row.entity.pricingType !== \'Benchmark\'">' +
        '<span ng-cell-text class="ng-binding" style="cursor: default;">{{row.getProperty(col.field)}}</span></div>';

        $scope.gridOptions = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            afterSelectionChange: loadApplicableCustomers,
            data: 'profileList',
            multiSelect: false,
            progressiveSearch: true,
            columnDefs: [
                 {
                    field: 'ltlPricingProfileId',
                    displayName: 'Rate ID',
                    width: '9%'
                 },
                 {
                    field: 'rateName',
                    displayName: 'Rate Name',
                    width: '20%'
                },
                {
                    field: 'carrierName',
                    displayName: 'Carrier',
                    width: '25%',
                    cellTemplate: benchmarkCellTemplate
                },
                {
                    field: 'scac',
                    displayName: 'SCAC',
                    width: '9%',
                    cellTemplate: benchmarkCellTemplate
                },
                {
                    field: 'effDate',
                    displayName: 'Effective',
                    cellFilter: 'date:appDateFormat',
                    width: '11.5%'
                },
                {
                    field: 'expDate',
                    displayName: 'Expires',
                    cellFilter: 'date:appDateFormat',
                    width: '11.5%'
                },
                {
                    field: 'pricingType',
                    displayName: 'Pricing Type',
                    width: '12%'
                }],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            refreshTable: loadProfiles
        };

        $scope.profCustomersGridOptions = {
            enableColumnResize: true,
            selectedItems: $scope.profCustomersSelectedItem,
            multiSelect: false,
            data: 'applicableCustomersName',
            columnDefs: [{
                field: 'name',
                displayName: 'Customers',
                width: '100%'
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            refreshTable: loadApplicableCustomers
        };
    }
]);
