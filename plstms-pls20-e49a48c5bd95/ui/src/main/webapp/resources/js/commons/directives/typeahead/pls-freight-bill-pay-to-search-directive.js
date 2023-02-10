/**
 * AngularJS directive which displays type-ahead input for Freight Bill Pay To search.
 */
angular.module('plsApp.directives').directive('plsFreightBillPayToSearch', ['FreightBillService', function (FreightBillService) {
    return {
        restrict: 'A',
        priority: 10,
        scope: {
            plsFreightBillPayToSearch: '=',
            inputDisabled: '=',
            customerId: '='
        },
        replace: true,
        template: '<input type="text" data-ng-model="plsFreightBillPayToSearch" data-typeahead-auto-select="true" data-typeahead-min-length="3"'
                + ' data-ng-disabled="inputDisabled" required="required"' 
                + ' data-pls-typeahead="item as item.company lbl getDescription(item) for item in findItem($viewValue, count)">',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.getDescription = function (item) {
                return item ? [item.address.addressCode, item.company, item.address.address1, item.address.zip.city, item.address.zip.state,
                               item.address.zip.zip].join(', ') : '';
            };

            $scope.findItem = function (criteria, count) {
                return FreightBillService.search({customerId: $scope.customerId, filter: criteria}).$promise;
            };
        }]
    };
}]);
