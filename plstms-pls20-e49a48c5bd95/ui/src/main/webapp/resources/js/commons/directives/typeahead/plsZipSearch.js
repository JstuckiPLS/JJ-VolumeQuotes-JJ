/**
 * AngularJS directive which displays type-ahead input for zip search.
 */
angular.module('plsApp.directives').directive('plsZipSearch', ['ZipService', '$filter', function (ZipService, $filter) {
    return {
        restrict: 'A',
        priority: 10,
        scope: {
            plsZipSearch: '=',
            country: '=',
            count: '=',
            inputLabelFilter: '@',
            zipDisabled: '='
        },
        replace: true,
        templateUrl: 'pages/tpl/pls-zip-search-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.findZip = function (criteria) {
                return ZipService.findZip(criteria, $scope.country, $scope.count);
            };

            $scope.showViewValue = function (zipObj) {
                if ($scope.inputLabelFilter) {
                    return $filter($scope.inputLabelFilter)(zipObj);
                }
                return zipObj ? zipObj.zip : '';
            };

            /**
             * Specific logic for USA ZIP codes when user enters exactly 5 digits.
             */
            $scope.getSelectedItemIndex = function (items, viewVal) {
                if (items.length > 1 && viewVal.length === 5 && $scope.country === 'USA' && new RegExp("\\d+").test(viewVal)) {
                    if (_.uniq(_.pluck(items, 'prefCity')).length === 1) {
                        // return index of item where city === preferred city
                        return _.indexOf(items, _.findWhere(items, {city: items[0].prefCity}));
                    }
                }
                return -1;
            };
        }]
    };
}]);