/**
 * Freight Bill Pay To Modal Dialog - directive
 *
 * @Vitaliy Gavrilyuk
 */
angular.module('plsApp.directives').directive('plsFreightBillPayToDialog', [function () {
    return {
        restrict: 'A',
        controller: ['$scope', '$http', '$compile', '$templateCache', function ($scope, $http, $compile, $templateCache) {
            'use strict';

            $http.get('pages/tpl/freight-bill-pay-to/pls-freight-bill-pay-to-dialog-tpl.html', {cache: $templateCache}).then(function (result) {
                var dialog = $compile(result.data)($scope);
                angular.element('#content').append(dialog);

                $scope.$on('$destroy', function() {
                    dialog.remove();
                });
            });

            var zipAutoCompleteCountry = ['CAN', 'MEX', 'USA'];
            $scope.zipAutoComplete = true;
            $scope.showDialog = false;

            $scope.closeDialog = function () {
                $scope.showDialog = false;
                $scope.$emit('event:typeaheadChanged', '', true);
            };

            $scope.resetDialog = function () {
                if (angular.isDefined($scope.defaultFreightBill)) {
                    $scope.shipment.freightBillPayTo = angular.copy($scope.defaultFreightBill);
                    $scope.closeDialog();
                }
            };

            $scope.isFreightBillNotChanged = function () {
                return angular.equals($scope.defaultFreightBill, $scope.freight);
            };

            $scope.saveDialog = function () {
                if (angular.isDefined($scope.freight)) {
                    $scope.shipment.freightBillPayTo = angular.copy($scope.freight);
                    if ($scope.freight.id === -1 && $scope.freightBillPayToForm.$dirty) {
                        delete $scope.shipment.freightBillPayTo.id;
                    }
                    $scope.closeDialog();
                }
            };

            $scope.$watch('freight.address.country', function () {
                if ($scope.freight && $scope.freight.address.country) {
                    $scope.zipAutoComplete = _.indexOf(zipAutoCompleteCountry, $scope.freight.address.country.id) !== -1;

                    if ($scope.previousCountry && $scope.freight.address.country.id !== $scope.previousCountry.id) {
                        $scope.freight.address.zip = undefined;
                        $scope.freight.phone = {countryCode: $scope.freight.address.country.dialingCode};
                    }

                    $scope.previousCountry = angular.copy($scope.freight.address.country);
                }
            });

            $scope.$watch('showDialog', function (newValue, oldValue) {
                if (newValue) {
                    $scope.freight = $scope.shipment.freightBillPayTo ? angular.copy($scope.shipment.freightBillPayTo) : {
                        address: {
                            country: {id: "USA", name: "United States of America", dialingCode: "1"}
                        },
                        phone: {countryCode: "1"}
                    };
                    $scope.previousCountry = angular.copy($scope.freight.address.country);
                    $scope.freightBillPayToForm.$setPristine();
                }
            });
        }]
    };
}]);
