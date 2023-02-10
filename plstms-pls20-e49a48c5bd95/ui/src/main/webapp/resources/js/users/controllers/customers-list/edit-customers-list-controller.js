angular.module('users.controllers').controller('EditCustomersListCtrl', [
    '$scope', 'NgGridPluginFactory', 'UserDetailsService', 'UserUtils',
    function ($scope, NgGridPluginFactory, UserDetailsService, UserUtils) {
        'use strict';

        $scope.selectedAssignedCustomers = [];
        $scope.selectedUnassignedCustomers = [];

        var personId;

        $scope.customerOrAccExecutive = {
            value: 'CUSTOMER_NAME'
        };

        $scope.editCustomersListModel = {
            showCustomersListDialog: false,
            searchValue: undefined,
            customersList: {},
            assignedCustomers: [],
            unassignedCustomers: []
        };

        $scope.unassignedCustomersListGrid = {
            data: 'editCustomersListModel.unassignedCustomers',
            selectedItems: $scope.selectedUnassignedCustomers,
            multiSelect: true,
            progressiveSearch: true,
            enableColumnResize: true,
            columnDefs: [{
                field: 'customerName',
                displayName: 'Customers',
                cellClass: 'text-center'
            }, {
                field: 'accountExecutive',
                displayName: 'Account Executive',
                cellTemplate: '<div class="ngCellText text-center" data-ng-class="col.colIndex()">'
                + '<a href="" data-ng-click="openAccountExecutiveDialog(row.entity.customerId)"'
                + ' data-ng-if="row.entity.multipleAE">{{row.getProperty(col.field)}}</a>'
                + '<span data-ng-if="!row.entity.multipleAE">{{row.getProperty(col.field)}}</span></div>'
            }, {
                field: 'unassignmentDate',
                displayName: 'Unassignment Date',
                searchable: false,
                cellClass: 'text-center',
                cellFilter: 'date:$root.appDateFullFormat'
            }],
            sortInfo: {fields: ['customerName'], directions: ['asc']},
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()]
        };

        $scope.assignedCustomersListGrid = {
            data: 'editCustomersListModel.assignedCustomers',
            selectedItems: $scope.selectedAssignedCustomers,
            multiSelect: true,
            progressiveSearch: true,
            enableColumnResize: true,
            columnDefs: [{
                field: 'customerName',
                displayName: 'Customers',
                cellClass: 'text-center'
            }, {
                field: 'accountExecutive',
                displayName: 'Account Executive',
                cellTemplate: '<div class="ngCellText text-center" data-ng-class="col.colIndex()">'
                + '<a href="" data-ng-click="openAccountExecutiveDialog(row.entity.customerId)"'
                + ' data-ng-if="row.entity.multipleAE">{{row.getProperty(col.field)}}</a>'
                + '<span data-ng-if="!row.entity.multipleAE">{{row.getProperty(col.field)}}</span></div>'
            }, {
                field: 'assignmentDate',
                displayName: 'Assignment Date',
                cellClass: 'text-center',
                cellFilter: 'date:$root.appDateFullFormat'
            }, {
                field: 'locationsCount',
                displayName: 'Locations',
                searchable: false,
                cellTemplate: '<div class="ngCellText text-center">'
                + '<a href="" data-ng-click="openlocationDialog(row.entity)"> '
                + ' {{row.entity.locations.length}} of {{row.getProperty(col.field)}}</a></div>'
            }],
            sortInfo: {fields: ['customerName'], directions: ['asc']},
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()]
        };

        $scope.$on('event:showEditCustomersList', function (event, transferObject) {
            $scope.editCustomersListModel.showCustomersListDialog = true;
            $scope.editCustomersListModel.assignedCustomers = angular.copy(transferObject.assignedCustomers);
            personId = transferObject.personId;
        });

        $scope.closeEditCustomersListDialog = function () {
            $scope.editCustomersListModel.showCustomersListDialog = false;
            $scope.editCustomersListModel.unassignedCustomers = undefined;
            $scope.editCustomersListModel.assignedCustomers = undefined;
            $scope.selectedUnassignedCustomers.length = 0;
            $scope.selectedAssignedCustomers.length = 0;
            $scope.editCustomersListModel.searchValue = undefined;
        };

        $scope.searchCustomers = function () {
            UserDetailsService.findUnassignedCustomers({
                searchField: $scope.customerOrAccExecutive.value,
                criteria: $scope.editCustomersListModel.searchValue,
                userId: personId
            }, function (data) {
                $scope.editCustomersListModel.unassignedCustomers = _.filter(data, function (customer) {
                    return _.findWhere($scope.editCustomersListModel.assignedCustomers, {
                                customerId: customer.customerId
                            }) === undefined;
                });
            });
        };

        $scope.openAccountExecutiveDialog = function (customerId) {
            $scope.$root.$broadcast('event:openAccountExecutiveDialog', customerId, true, 'Multiple', 'editCustomersListDialog');
        };

        $scope.openlocationDialog = function (customer) {
            var transferObject = {
                customerId: customer.customerId,
                userId: personId,
                locations: angular.copy(customer.locations),
                parentDialog: 'editCustomersListDialog'
            };

            $scope.$root.$broadcast('event:openlocationDialog', transferObject);
        };

        function updateCustomersModel(customers) {
            _.each(customers, function (customer) {
                if (!customer.locations) {
                    customer.locations = [];
                    if (customer.locationsCount === 1) {
                        customer.locations.push({locationId: customer.locationId});
                    }
                }
            });
        }

        function switchCustomers(from, to, customers) {
            updateCustomersModel(customers);

            _.each(customers, function (customer) {
                from.splice(from.indexOf(customer), 1);
                to.push(customer);
            });

            $scope.selectedAssignedCustomers.length = 0;
            $scope.selectedUnassignedCustomers.length = 0;
        }

        $scope.removeCustomer = function () {
            $scope.selectedAssignedCustomers[0].unassignmentDate = new Date();
            switchCustomers($scope.editCustomersListModel.assignedCustomers, $scope.editCustomersListModel.unassignedCustomers,
                    $scope.selectedAssignedCustomers);
        };

        $scope.addCustomer = function () {
            $scope.selectedUnassignedCustomers[0].assignmentDate = new Date();
            switchCustomers($scope.editCustomersListModel.unassignedCustomers, $scope.editCustomersListModel.assignedCustomers,
                    $scope.selectedUnassignedCustomers);
        };

        $scope.updateCustomersList = function () {
            $scope.$root.$broadcast('event:updateAssignedCustomers', angular.copy($scope.editCustomersListModel.assignedCustomers));
            $scope.closeEditCustomersListDialog();
        };

        $scope.$on('event:updateCustomerlocation', function (event, transferObject) {
            UserUtils.updateCustomerLocations($scope.editCustomersListModel.assignedCustomers, transferObject);
        });
    }
]);