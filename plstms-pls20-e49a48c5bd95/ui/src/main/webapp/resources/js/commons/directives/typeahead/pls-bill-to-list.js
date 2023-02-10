/**
 * AngularJS directive which displays editable combo-box for Bill To Address name.
 *
 * @author: Alexander Kirichenko
 * Date: 5/13/13
 * Time: 5:32 PM
 */
angular.module('plsApp.directives').directive('plsBillToList', [function () {
    return {
        restrict: 'A',
        scope: {
            selectedAddress: '=plsBillToList',
            addresses: '=',
            addressForm: '=',
            ngDisabledVal: '=ngDisabled',
            changeFn: '&'
        },
        replace: true,
        templateUrl: 'pages/tpl/pls-bill-to-list-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            if (_.isFunction($scope.changeFn)) {
                $scope.$watch('selectedAddress.id', function (newValue, oldValue) {
                    if (newValue && newValue !== oldValue) {
                        $scope.changeFn();
                    }
                });
            }

            $scope.findAddress = function (filterValue) {
                var trimmedFilterValue = filterValue.trim();
                var filteredAddresses = [];
                if (angular.isArray($scope.addresses)) {
                    if (trimmedFilterValue.length === 0) {
                        return $scope.addresses;
                    }
                    angular.forEach($scope.addresses, function (address) {
                        var addressName = address.address.addressName;
                        if (addressName.toLowerCase().indexOf(trimmedFilterValue.toLowerCase()) !== -1) {
                            filteredAddresses.push(address);
                        }
                    });
                }
                return filteredAddresses;
            };

            $scope.$watch('addressSelectForm.$invalid', function (newValue) {
                if ($scope.addressForm) {
                    $scope.addressForm.invalid = newValue;
                }
            });
        }]
    };
}]);