angular.module('plsApp').controller('CustomersTabCtrl', ['$scope', '$timeout', 'SearchCustomersService', 'CustomerPricingSearchService',
    'DateTimeUtils', 'NgGridPluginFactory', '$location', '$routeParams',
    function ($scope, $timeout, SearchCustomersService, CustomerPricingSearchService, DateTimeUtils, NgGridPluginFactory, $location, $routeParams) {
        'use strict';

        $scope.clearSearchCriteria = function () {
            var status = 'A';
            if ($scope.criteria && $scope.criteria.status) {
                status = $scope.criteria.status;
            }
            $scope.criteria = {
                status: status
            };
            CustomerPricingSearchService.setCriteria($scope.criteria);
        };

        $scope.criteria = CustomerPricingSearchService.getCriteria();
        if (!$scope.criteria) {
            $scope.clearSearchCriteria();
        }

        $scope.searchByCustomerName = function() {
            var delay = 500; // 0.5 seconds delay after last input
            if ($scope.searchByNameTimeoutPromise) {
                $timeout.cancel($scope.searchByNameTimeoutPromise);
            }
            $scope.searchByNameTimeoutPromise = $timeout(function () {
                $scope.searchByNameTimeoutPromise = undefined;
                $scope.searchCustomers();
            }, delay);
        };

        function createDateRange(rangeType, fromDate, toDate) {
            var from = null;
            var to = null;
            var currentDate = new Date();

            if ($scope.criteria.dateRange !== 'DEFAULT') {
                $scope.criteria.fromDate = null;
                $scope.criteria.toDate = null;
            }

            if ($scope.criteria.loadDateRange !== 'DEFAULT') {
                $scope.criteria.loadFromDate = null;
                $scope.criteria.loadToDate = null;
            }

            switch (rangeType) {
                case 'TODAY':
                    from = new Date();
                    to = new Date();
                    break;
                case 'WEEK':
                    from = DateTimeUtils.getFirstDayOfWeek(new Date());
                    to = DateTimeUtils.addDays(from, DateTimeUtils.DAYS_IN_WEEK - 1);
                    break;
                case 'MONTH':
                    from = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
                    to = currentDate;
                    break;
                case 'YEAR':
                    from = new Date(currentDate.getFullYear(), 0, 1);
                    to = currentDate;
                    break;
                case 'DEFAULT':
                    if (!_.isUndefined(fromDate) && !_.isUndefined(toDate) && !_.isNull(fromDate) && !_.isNull(toDate)) {
                        from = new Date(fromDate);
                        to = new Date(toDate);
                    }
                    break;
            }

            var DateRange = {
                fromDate: '',
                toDate: ''
            };

            if (!_.isUndefined(from) && !_.isUndefined(to) && !_.isNull(from) && !_.isNull(to)) {
                DateRange.fromDate = from.getFullYear() + '-' + (from.getMonth() + 1) + '-' + from.getDate();
                DateRange.toDate = to.getFullYear() + '-' + (to.getMonth() + 1) + '-' + to.getDate();
            }

            return DateRange;
        }

        $scope.searchCustomers = function () {
            var fromDateCriteria = '';
            var toDateCriteria = '';
            var loadFromDateCriteria = '';
            var loadToDateCriteria = '';
            var nameCriteria = '';

            if (!_.isUndefined($scope.criteria.dateRange) && !_.isNull($scope.criteria.dateRange)) {
                var dateRangeCritObj = createDateRange($scope.criteria.dateRange, $scope.criteria.fromDate, $scope.criteria.toDate);

                if (!_.isUndefined(dateRangeCritObj) && !_.isNull(dateRangeCritObj)) {
                    fromDateCriteria = dateRangeCritObj.fromDate;
                    toDateCriteria = dateRangeCritObj.toDate;
                }
            }

            if (!_.isUndefined($scope.criteria.loadDateRange) && !_.isNull($scope.criteria.loadDateRange)) {
                var loadDateRangeCritObj = createDateRange($scope.criteria.loadDateRange, $scope.criteria.loadFromDate, $scope.criteria.loadToDate);

                if (!_.isUndefined(loadDateRangeCritObj) && !_.isNull(loadDateRangeCritObj)) {
                    loadFromDateCriteria = loadDateRangeCritObj.fromDate;
                    loadToDateCriteria = loadDateRangeCritObj.toDate;
                }
            }

            if (!_.isUndefined($scope.criteria.name) && !_.isNull($scope.criteria.name)) {
                nameCriteria = $scope.criteria.name.trim();
            }

            if ((!_.isEmpty(fromDateCriteria) && !_.isEmpty(toDateCriteria))
                    || (!_.isEmpty(loadFromDateCriteria) && !_.isEmpty(loadToDateCriteria))
                    || !_.isEmpty(nameCriteria)
                    || ($scope.criteria.accountExecutive && _.isNumber($scope.criteria.accountExecutive.id))) {
                var criteria = {
                    fromDate: fromDateCriteria,
                    toDate: toDateCriteria,
                    fromLoadDate: loadFromDateCriteria,
                    toLoadDate: loadToDateCriteria,
                    personId: $scope.criteria.accountExecutive ? $scope.criteria.accountExecutive.id : '',
                    status: $scope.criteria.status,
                    name: nameCriteria
                };

                SearchCustomersService.find({}, criteria, function (response) {
                    $scope.listItems = response;
                });
            }
        };

        $scope.$watch('criteria.accountExecutive.id', $scope.searchCustomers);

        $scope.selectedItems = [];

        $scope.gridOptions = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'listItems',
            columnDefs: [{
                field: 'customerName',
                displayName: 'Customer',
                width: '35%'
            }, {
                field: 'accountExecName',
                displayName: 'Account Exec',
                width: '35%'
            }, {
                field: 'createdDate',
                displayName: 'Created On',
                cellFilter: 'date:appDateFormat',
                width: '15%'
            }, {
                field: 'lastLoadDate',
                displayName: 'Last Load',
                cellFilter: 'date:appDateFormat',
                width: '15%'
            }],
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin()],
            useExternalSorting: false,
            multiSelect: false,
            progressiveSearch: true
        };

        $scope.changeStatus = function (status) {
            $scope.criteria.status = status;
            $scope.searchCustomers();
        };

        $scope.searchCustomers();

        $scope.displayCustProf = function () {
            $location.url('/pricing/customer/' + $scope.selectedItems[0].customerId);
        };
        
        $scope.displayCustP44Config = function () {
            $location.url('/pricing/customer/' + $scope.selectedItems[0].customerId + '/p44Config');
        };
    }]);
