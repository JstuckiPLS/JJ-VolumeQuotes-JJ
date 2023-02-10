/**
 * Date Range directive that described http://devtools.plslogistics.com/redmine/projects/pls-pro-2/wiki/FR4
 *
 * @Eugene Borshch
 * @Aleksandr Leshchenko
 */
angular.module('plsApp.directives').directive('plsDateRange', ['$rootScope', '$filter', 'DateTimeUtils',
    function ($rootScope, $filter, DateTimeUtils) {
        return {
            restrict: 'A',
            scope: {
                rangeType: '=',
                model: '=',
                validityState: '=',
                validity: '='
            },
            replace: true,
            transclude: true,
            templateUrl: 'pages/tpl/daterange-tpl.html',
            controller: ['$scope', function ($scope) {
                'use strict';

                var filterDate = function (date) {
                    if (date) {
                        date = DateTimeUtils.parseISODate(date);
                    }

                    return date ? $filter('date')(date, $rootScope.transferDateFormat) : '';
                };

                var initModel = function (range, startDate, endDate) {
                    if (range === 'DEFAULT') {
                        // decide which field we should highlight
                        $scope.fromDateAlreadyExists = !startDate;
                        $scope.toDateAlreadyExists = !endDate;

                        $scope.model = range + "," + filterDate(startDate) + ',' + filterDate(endDate);
                        $scope.validity = !($scope.fromDateAlreadyExists || $scope.toDateAlreadyExists);
                    } else {
                        $scope.startDate = undefined;
                        $scope.endDate = undefined;
                        $scope.fromDateAlreadyExists = false;
                        $scope.toDateAlreadyExists = false;
                        $scope.model = range;
                        $scope.validity = true;
                    }
                };

                $scope.fromDateAlreadyExists = false;
                $scope.toDateAlreadyExists = false;

                $scope.$watch('rangeType', function (newValue) {
                    initModel(newValue, $scope.startDate, $scope.endDate);
                });

                $scope.$watch('startDate', function (newValue) {
                    if (newValue) {
                        $scope.minToDate = DateTimeUtils.parseISODate(newValue);
                        $scope.maxToDate = new Date($scope.minToDate.getTime() + DateTimeUtils.MILLISECONDS_IN_YEAR);
                    } else {
                        $scope.minToDate = undefined;
                        $scope.maxToDate = undefined;
                    }

                    initModel($scope.rangeType, newValue, $scope.endDate);
                });

                $scope.$watch('endDate', function (newValue) {
                    if (newValue) {
                        $scope.maxFromDate = DateTimeUtils.parseISODate(newValue);
                        $scope.minFromDate = new Date($scope.maxFromDate.getTime() - DateTimeUtils.MILLISECONDS_IN_YEAR);
                    } else {
                        $scope.maxFromDate = undefined;
                    }

                    initModel($scope.rangeType, $scope.startDate, newValue);
                });

                if ($scope.validityState) {
                    $scope.$watch('plsDateRangeForm.$invalid', function (newValue) {
                        $scope.validityState.invalid = newValue;
                    });
                }
            }]
        };
    }
]);