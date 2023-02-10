/**
 * Add/Edit Assigned Customers List in Modal Dialog component
 *
 * @Vitaliy Gavrilyuk
 */
angular.module('plsApp.directives').directive('plsAssignedCustomersDialog', [function () {
    return {
        restrict: 'A',
        scope: {
            assignedCustomers: '=',
            modelId: '='
        },
        templateUrl: 'pages/tpl/modals/add-edit-assigned-customers.html',
        controller: ['$scope', 'AssignedCustomersSearchService', 'NgGridPluginFactory', function ($scope, AssignedCustomersSearchService,
                NgGridPluginFactory) {
            'use strict';

            $scope.selectedCustomers = [];
            $scope.selectedApplicableCustomers = [];
            $scope.customerList = [];
            $scope.customerFilter = "";
            $scope.applicableCustomers = [];

            /**
             * Open modal dialog.
             */
            $scope.$on('openAssignedCustomersDialog', function () {
                $scope.isAssignedCustomersDialogOpen = true;
                $scope.applicableCustomers = [];
                if (!_.isEmpty($scope.assignedCustomers)) {
                    $scope.applicableCustomers = $scope.assignedCustomers.slice(0);
                }
            });

            function loadCustomers() {
                AssignedCustomersSearchService.get({name: $scope.customerFilter}).$promise.then(function (response) {
                    $scope.customerList = response;
                    $scope.selectedCustomers.length = 0;
                });
            }

            /**
             * Save changes and close dialog.
             */
            $scope.save = function () {
                $scope.assignedCustomers = $scope.applicableCustomers.slice(0);
                $scope.isAssignedCustomersDialogOpen = false;
                $scope.customerFilter = "";
            };

            /**
             * Discard changes and close dialog.
             */
            $scope.cancel = function () {
                if (!_.isEmpty($scope.assignedCustomers)) {
                    $scope.applicableCustomers = $scope.assignedCustomers.slice(0);
                }

                $scope.isAssignedCustomersDialogOpen = false;
                $scope.customerFilter = "";
            };

            $scope.customerListGrid = {
                enableColumnResize: true,
                selectedItems: $scope.selectedCustomers,
                data: 'customerList',
                multiSelect: false,
                columnDefs: [
                    {
                        field: 'name',
                        displayName: 'Customers',
                        width: '95%'
                    }],
                plugins: [NgGridPluginFactory.plsGrid()]
            };

            $scope.applicableCustomerListGrid = {
                enableColumnResize: true,
                selectedItems: $scope.selectedApplicableCustomers,
                data: 'applicableCustomers',
                multiSelect: false,
                columnDefs: [
                    {
                        field: 'customer.name',
                        displayName: 'Assigned Customers',
                        width: '95%'
                    }],
                plugins: [NgGridPluginFactory.plsGrid()]
            };

            $scope.$watch('customerFilter', function (newVal, oldVal) {
                if (!_.isEmpty($scope.customerFilter)) {
                    loadCustomers();
                } else {
                    $scope.customerList.length = 0;
                }
            }, true);

            function contains(customer) {
                var existed = _.find($scope.applicableCustomers, function (item) {
                    return item.customer.id === customer.id;
                });
                return !_.isEmpty(existed);
            }

            function addApplicableCustomers(custList) {
                var i = 0;
                for (i = 0; i < custList.length; i += 1) {
                    var selected = custList[i];
                    if (!contains(selected)) {
                        var applicableCustomer = {
                            ltlPricingProfileId: $scope.modelId,
                            customer: selected
                        };
                        $scope.applicableCustomers.push(applicableCustomer);
                        $scope.customerList.splice($scope.customerList.indexOf(selected), 1);
                        i -= 1;
                    }
                }
            }

            $scope.addCustomer = function () {
                addApplicableCustomers($scope.selectedCustomers);
                $scope.selectedCustomers.length = 0;
            };

            $scope.removeCustomer = function () {
                if (!_.isEmpty($scope.selectedApplicableCustomers)) {
                    $scope.applicableCustomers.splice($scope.applicableCustomers.indexOf($scope.selectedApplicableCustomers[0]), 1);
                    $scope.selectedApplicableCustomers.length = 0;
                }
            };

            $scope.addAllCustomers = function () {
                addApplicableCustomers($scope.customerList);
            };

            $scope.removeAllCustomers = function () {
                $scope.applicableCustomers = [];
            };
        }]
    };
}]);