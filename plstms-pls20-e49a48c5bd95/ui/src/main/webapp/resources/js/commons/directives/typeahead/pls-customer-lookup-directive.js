/**
 * AngularJS directive which displays type-ahead input for customer search.
 */
angular.module('plsApp.directives').directive('plsCustomerLookup', ['CustomerLookupService', function (CustomerLookupService) {
    return {
        restrict: 'A',
        priority: 10,
        scope: {
            plsCustomerLookup: '=',
            customerDisabled: '=',
            count: '=',
            showAllStatuses: '@',
            noDefaultClass: '='
        },
        replace: true,
        template: '<input data-ng-class="{\'span12\': !noDefaultClass}" data-ng-disabled="customerDisabled || $root.authData.assignedOrganization" '
            + 'type="text" data-ng-model="selectedCustomer" data-typeahead-auto-select="true" autocomplete="off" data-ng-change="changeCustomer()"'
            + 'data-pls-typeahead="customer as customer.name for customer in findCustomer($viewValue, count, showAllStatuses)" autofocus>',
        controller: ['$scope', function ($scope) {
            'use strict';

            function initCustomer() {
                if (!$scope.$root.authData.assignedOrganization) {
                    $scope.selectedCustomer = undefined;
                    $scope.oldCustomerVal = undefined;
                } else {
                    $scope.selectedCustomer = {
                        id: $scope.$root.authData.assignedOrganization.orgId,
                        name: $scope.$root.authData.assignedOrganization.name
                    };
                    $scope.plsCustomerLookup = $scope.selectedCustomer;
                }
            }

            if (!$scope.$root.authData.assignedOrganization) {
                $scope.selectedCustomer = $scope.plsCustomerLookup;

                $scope.$watch('selectedCustomer', function (newVal, oldValue) {
                    $scope.plsCustomerLookup = newVal || undefined;
                    $scope.oldCustomerVal = oldValue;
                });

                $scope.$watch('plsCustomerLookup', function (newValue, oldValue) {
                    if (newValue && newValue.id && newValue.name) {
                        $scope.selectedCustomer = newValue;
                    }
                });

                $scope.changeCustomer = function () {
                    $scope.$emit('event:changeCustomer', $scope.selectedCustomer, $scope.oldCustomerVal);
                };

                $scope.findCustomer = function (criteria, count, showAllStatuses) {
                    if (criteria && criteria.length > 2) {
                        return CustomerLookupService.findCustomer(criteria, count, showAllStatuses);
                    }
                    return {};
                };
            } else {
                initCustomer();
            }

            $scope.$on('event:cleaning-input', initCustomer);
        }]
    };
}]);

angular.module('plsApp.directives').directive('plsCustomerLookupRequired', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            'use strict';

            scope.isOptional = scope.$eval(attrs.isOptional);
            var validator = function (viewValue) {
                ctrl.$setValidity('required', scope.isOptional || (viewValue && viewValue.id));
                ctrl.$setValidity('mistyped', (scope.isOptional && !ctrl.$viewValue) || (viewValue && viewValue.id));
                return viewValue;
            };

            ctrl.$parsers.push(validator);
            ctrl.$formatters.push(validator);
        }
    };
});