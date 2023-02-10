angular.module('plsApp').directive('plsBillToAddress', ['$timeout', function ($timeout) {
    return {
        restrict: 'A',
        scope: {
            billTo: '=',
            customer: '=',
            isAddCustomer: '='
        },
        replace: true,
        templateUrl: 'pages/content/customer/billTo/templates/billto-address-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            var previousBillToCountry;

            if ($scope.billTo && $scope.billTo.address && $scope.billTo.address.country) {
                previousBillToCountry = $scope.billTo.address.country;
            }

            var zipAutoCompleteCountry = ['CAN', 'MEX', 'USA'];

            function getNewPhone() {
                return {countryCode: Number($scope.billTo.address.country.dialingCode)};
            }

            $scope.$watch('billTo.address.country', function (newVal) {
                if (newVal) {
                    $scope.zipAutoComplete = _.indexOf(zipAutoCompleteCountry, newVal.id) !== -1;

                    if (!previousBillToCountry || newVal.id !== previousBillToCountry.id) {
                        delete $scope.billTo.address.zip;
                        $scope.billTo.address.phone = getNewPhone();
                        $scope.billTo.address.fax = getNewPhone();
                    }

                    previousBillToCountry = angular.copy(newVal);
                }
            });

            if (!$scope.billTo.address) {
                $scope.billTo.address = {
                    country: {id: 'USA', name: 'United States of America', dialingCode: '1'}
                };
            }

            if (_.isEmpty($scope.billTo.address.fax)) {
                $scope.billTo.address.fax = getNewPhone();
            }
            if (_.isEmpty($scope.billTo.address.phone)) {
                $scope.billTo.address.phone = getNewPhone();
            }

            $scope.copyFromCustomerContact = function () {
                $scope.$broadcast('event:cleaning-input');

                $scope.billTo.address.country = angular.copy($scope.customer.address.country);

                $timeout(function () {
                    $scope.billTo.address.country = angular.copy($scope.customer.address.country);

                    $scope.billTo.address.address1 = angular.copy($scope.customer.address.address1);
                    $scope.billTo.address.address2 = angular.copy($scope.customer.address.address2);

                    if ($scope.customer.contactFirstName && $scope.customer.contactLastName) {
                        $scope.billTo.address.contactName = $scope.customer.contactFirstName + ' ' + $scope.customer.contactLastName;
                    }

                    if (!_.isEmpty($scope.customer.contactPhone)) {
                        $scope.billTo.address.phone = angular.copy($scope.customer.contactPhone);
                    } else {
                        $scope.billTo.address.phone = getNewPhone();
                    }

                    if (!_.isEmpty($scope.customer.contactFax)) {
                        $scope.billTo.address.fax = angular.copy($scope.customer.contactFax);
                    } else {
                        $scope.billTo.address.fax = getNewPhone();
                    }

                    $scope.billTo.address.email = angular.copy($scope.customer.contactEmail);
                    $scope.billTo.address.zip = angular.copy($scope.customer.address.zip);
                }, 1);
            };
        }]
    };
}]);