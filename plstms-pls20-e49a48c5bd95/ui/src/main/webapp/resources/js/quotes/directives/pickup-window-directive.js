/**
 * AngularJS directive which displays type-ahead inputs for pickup window.
 *
 * @author: Sergey Kirichenko
 * Date: 5/7/13
 * Time: 3:05 PM
 */
angular.module('plsApp').directive('plsPickupWindow', ['$filter', function ($filter) {
    return {
        restrict: 'A',
        priority: 10,
        scope: {
            timeDto: '=plsPickupWindow',
            requiredExpression: '&plsPickupWindowRequired'
        },
        replace: true,
        template: '<input data-ng-model="timeDto" type="text" data-typeahead-auto-select="true" data-ng-required="pickupWindowRequired"' +
        'data-pls-typeahead="time as time | pickupWindowTime for time in findTime($viewValue) | limitTo:4">',
        controller: ['$scope', function ($scope) {
            $scope.pickupWindowRequired = false;

            if ($scope.requiredExpression && angular.isFunction($scope.requiredExpression)) {
                $scope.$watch($scope.requiredExpression, function (req) {
                    $scope.pickupWindowRequired = req;
                });
            }

            var i;

            $scope.timeOptions = [];

            for (i = 0; i < 2; i += 1) {
                var am = i === 0;
                var j = 1;

                for (j = 1; j <= 12; j += 1) {
                    $scope.timeOptions.push({hours: j, minutes: 0, am: am});
                    $scope.timeOptions.push({hours: j, minutes: 30, am: am});
                }
            }

            $scope.findTime = function (inputValue) {
                var i;
                var matches = [];

                for (i = 0; i < $scope.timeOptions.length; i += 1) {
                    if ($filter('pickupWindowTime')($scope.timeOptions[i]).indexOf(inputValue.toUpperCase()) !== -1) {
                        matches.push($scope.timeOptions[i]);
                    }
                }

                return matches;
            };
        }]
    };
}]);
