angular.module('customer').controller('CustomerSearchCtrl', [
    '$scope', '$location', '$cookies', 'GetCustomersService', 'NgGridPluginFactory', 'UserNetworksService',
    function ($scope, $location, $cookies, GetCustomersService, NgGridPluginFactory, UserNetworksService) {
        'use strict';

        if ($scope.authData.assignedOrganization) {
            $location.url('/customer/' + $scope.authData.assignedOrganization.orgId + '/profile');
        }

        $scope.customerModel = {
            selectedTab: $scope.route.current.customerListTab,
            selectedCustomers: [],
            customers: [],
            filterName: undefined,
            businessUnits: [],
            selectedBusinessUnit: ''
        };

        UserNetworksService.activeNetworks({}, function (data) {
            if (data.length > 1) {
                $scope.customerModel.businessUnits.push({name: "All", value: ""});
            }

            _.each(data, function (element) {
                $scope.customerModel.businessUnits.push({name: element.name, value: element.name});
            });

            if ($scope.customerModel.businessUnits.length) {
                $scope.customerModel.selectedBusinessUnit = $scope.customerModel.businessUnits[0].value;
            }
        });

        $scope.addCustomer = function () {
            $scope.$root.$broadcast('event:showAddCustomer');
        };

        $scope.editCustomer = function () {
            if ($scope.customerModel.selectedCustomers.length) {
                $cookies.editCustomerPreviousTab = $scope.customerModel.selectedTab;
                $location.url('/customer/' + $scope.customerModel.selectedCustomers[0].id + '/profile');
            }
        };

        $scope.getCustomersList = function () {
            if ($scope.customerModel.filterName || $scope.customerModel.selectedBusinessUnit.length) {
                GetCustomersService.list({
                    pathParam: $scope.customerModel.selectedTab,
                    name: $scope.customerModel.filterName,
                    businessUnitName: $scope.customerModel.selectedBusinessUnit
                }, function (data) {
                    $scope.customerModel.customers = data;
                }, function (data) {
                    $scope.customerModel.customers.length = 0;
                    $scope.$root.$emit('event:application-error', data.data.message, data);
                });
            }
        };

        $scope.$on('event:customerAdded', function () {
            $scope.getCustomersList();
        });

        $scope.openAccountExecutiveDialog = function (customerId, multipleAccountExecitive, accountExecutive) {
            $scope.$broadcast('event:openAccountExecutiveDialog', customerId, multipleAccountExecitive, accountExecutive);
        };

        $scope.customersGrid = {
            enableColumnResize: true,
            data: 'customerModel.customers',
            multiSelect: false,
            primaryKey: 'id',
            selectedItems: $scope.customerModel.selectedCustomers,
            columnDefs: [
                {
                    field: 'name',
                    displayName: 'Customer',
                    width: '17%'
                },
                {
                    field: 'contactFirstName',
                    displayName: 'Contact First Name',
                    width: '11%'
                },
                {
                    field: 'contactLastName',
                    displayName: 'Contact Last Name',
                    width: '11%'
                },
                {
                    field: 'phone',
                    displayName: 'Phone',
                    width: '13%',
                    cellFilter: 'phone'
                },
                {
                    field: 'email',
                    displayName: 'Email',
                    width: '12%'
                },
                {
                    field: 'id',
                    displayName: 'Account ID',
                    width: '8%',
                    visible: $scope.$root.isFieldRequired('CUSTOMER_PROFILE_VIEW')
                },
                {
                    field: 'ediNumber',
                    displayName: 'EDI#',
                    width: '8%',
                    visible: $scope.$root.isFieldRequired('CUSTOMER_PROFILE_VIEW')
                },
                {
                    field: 'contract',
                    displayName: 'Contract',
                    width: '7%',
                    headerClass: 'text-center',
                    searchable: false,
                    cellTemplate: 'pages/cellTemplate/checked-cell.html',
                    visible: $scope.$root.isFieldRequired('CUSTOMER_PROFILE_VIEW')
                },
                {
                    field: 'accountExecutive',
                    displayName: 'Account Executive',
                    width: '12%',
                    cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">'
                    + '<a href="" data-ng-click="openAccountExecutiveDialog(row.entity.id, row.entity.multipleAccountExecitive, '
                    + 'row.entity.accountExecutive)">{{row.getProperty(col.field)}}</a></div>'
                }
            ],
            action: function () {
                $scope.editCustomer();
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin()],
            progressiveSearch: true
        };
    }
]);