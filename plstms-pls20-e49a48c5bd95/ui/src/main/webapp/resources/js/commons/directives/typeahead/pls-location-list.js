/**
 * AngularJS directive which displays editable combo-box for Location name.
 *
 * @author: Alexander Leshchenko
 */
angular.module('plsApp.directives').directive('plsLocationList', [function () {
    return {
        restrict: 'A',
        scope: {
            selectedLocation: '=plsLocationList',
            changeFn: '&',
            locations: '=',
            locationForm: '=',
            ngDisabledVal: '=ngDisabled'
        },
        replace: true,
        templateUrl: 'pages/tpl/pls-location-list-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            if (_.isFunction($scope.changeFn)) {
                $scope.previousVal = $scope.selectedLocation ? $scope.selectedLocation.id : undefined;
                $scope.$watch('selectedLocation.id', function (newValue) {
                    if (newValue && newValue !== $scope.previousVal) {
                        $scope.previousVal = newValue;
                        $scope.changeFn();
                    }
                });
            }

            $scope.findLocation = function (filterValue) {
                var trimmedFilterValue = filterValue.trim();
                var filteredLocations = [];
                if (angular.isArray($scope.locations)) {
                    if (trimmedFilterValue.length === 0) {
                        return $scope.locations;
                    }
                    angular.forEach($scope.locations, function (location) {
                        if (location.name.toLowerCase().indexOf(trimmedFilterValue.toLowerCase()) !== -1) {
                            filteredLocations.push(location);
                        }
                    });
                }
                return filteredLocations;
            };

            $scope.$watch('locationSelectForm.$invalid', function (newValue) {
                if ($scope.locationForm) {
                    $scope.locationForm.invalid = newValue;
                }
            });
        }]
    };
}]);