angular.module('plsApp').controller('CustomerActivityCtrl', ['$scope', 'CustomerActivityService', 'NgGridPluginFactory',
    function ($scope, CustomerActivityService, NgGridPluginFactory) {
        'use strict';

        $scope.dateRange = 'TODAY';
        $scope.dateRangeType = 'TODAY';
        $scope.selectedCustomer = [];

        $scope.loadData = function () {
            CustomerActivityService.getActivity({'dateRange': $scope.dateRange}, function (data) {
                $scope.customerActivityGridData = data;
            });
        };

        $scope.$watch('dateRange', $scope.loadData);

        $scope.customerUsersGrid = {
            sort: '',
            filter: '',
            options: {
                data: 'customerActivityGridData',
                selectedItems: $scope.selectedCustomer,
                columnDefs: [
                    {
                        field: 'customerName',
                        displayName: 'Customer Name',
                        width: '0%',
                        searchable: false,
                        resizable: false
                    },
                    {
                        field: 'fullName',
                        displayName: 'User Name',
                        width: '30%'
                    },
                    {
                        field: 'quotedShipmentsAmount',
                        displayName: 'Quoted',
                        width: '8%',
                        cellFilter: 'number'
                    },
                    {
                        field: 'bookedShipmentsAmount',
                        displayName: 'Booked',
                        width: '8%',
                        cellFilter: 'number'
                    },
                    {
                        field: 'revenue',
                        displayName: 'Revenue',
                        width: '10%',
                        cellFilter: 'number'
                    },
                    {
                        field: 'margin',
                        displayName: 'Margin',
                        width: '8%',
                        cellFilter: 'number'
                    },
                    {
                        field: 'marginPercent',
                        displayName: 'Margin %',
                        width: '8%',
                        cellFilter: 'number'
                    },
                    {
                        field: 'accountExecutive',
                        displayName: 'Account Executive',
                        width: '18%'
                    }
                ],
                plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
                enableColumnResize: true,
                multiSelect: false,
                progressiveSearch: true,
                groups: ['customerName'],
                groupsCollapsedByDefault: false
            }
        };
    }
]);