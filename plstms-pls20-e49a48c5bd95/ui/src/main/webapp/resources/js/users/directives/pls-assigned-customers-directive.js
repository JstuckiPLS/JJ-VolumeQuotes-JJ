angular.module('plsApp').directive('plsAssignedCustomers', ['NgGridPluginFactory', 'ShipmentUtils', 'UserUtils',
    function (NgGridPluginFactory, ShipmentUtils, UserUtils) {
        return {
            restrict: 'A',
            scope: {
                userCustomers: '=',
                userInfoTab: '=',
                userId: '=',
                viewMode: '='
            },
            replace: true,
            templateUrl: 'pages/content/users/users/assign-customers-tab.html',
            controller: ['$scope', function ($scope) {
                'use strict';

                $scope.selectedCustomers = [];

                function getAllNotifications() {
                    $scope.allNotifications = angular.copy(ShipmentUtils.getDictionaryValues().notificationTypes);
                }

                function setInitialState() {
                    $scope.selectedCustomers.length = 0;
                    getAllNotifications();
                    $scope.customersModel.options.$gridScope.toggleSelectAll(false, true);
                }

                $scope.customersModel = {
                    options: {
                        data: 'userCustomers',
                        selectedItems: $scope.selectedCustomers,
                        columnDefs: [{
                            field: 'customerName',
                            displayName: 'Customer',
                            cellClass: 'text-center'
                        }, {
                            field: 'accountExecutive',
                            displayName: 'Account Executive',
                            cellTemplate: '<div class="ngCellText text-center" data-ng-class="col.colIndex()">'
                            + '<a href="" data-ng-click="openAccountExecutiveDialog(row.entity.customerId)"'
                            + ' data-ng-if="row.entity.multipleAE">{{row.getProperty(col.field)}}</a>'
                            + '<span data-ng-if="!row.entity.multipleAE">{{row.getProperty(col.field)}}</span></div>'
                        }],
                        sortInfo: {fields: ['customerName'], directions: ['asc']},
                        plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
                        afterSelectionChange: function () {
                            if (!_.isEmpty($scope.selectedCustomers)) {
                                var maxCount = $scope.selectedCustomers[0].locations.length;

                                _.each($scope.allNotifications, function (notification) {
                                    var notificationsCount = _.filter($scope.selectedCustomers[0].locations, function (location) {
                                        return _.contains(location.notifications, notification.value);
                                    }).length;

                                    notification.selected = !notificationsCount ? 'NONE' : (maxCount === notificationsCount ? 'ALL' : 'SOME');
                                });
                            }
                        },
                        useExternalSorting: false,
                        multiSelect: false,
                        progressiveSearch: true,
                        enableColumnResize: true
                    }
                };

                $scope.openAccountExecutiveDialog = function (customerId) {
                    $scope.$root.$broadcast('event:openAccountExecutiveDialog', customerId, true, 'Multiple');
                };

                $scope.editCustomerList = function () {
                    var transferObject = {
                        personId: $scope.userId,
                        assignedCustomers: $scope.userCustomers
                    };

                    $scope.$root.$broadcast('event:showEditCustomersList', transferObject);
                };

                $scope.editLocationsList = function () {
                    var transferObject = {
                        customerId: $scope.selectedCustomers[0].customerId,
                        userId: $scope.userId,
                        locations: $scope.selectedCustomers[0].locations
                    };

                    $scope.$root.$broadcast('event:openlocationDialog', transferObject);
                };

                $scope.$on('event:updateAssignedCustomers', function (event, transferObject) {
                    setInitialState();
                    $scope.userCustomers = transferObject;
                });

                $scope.$on('event:updateCustomerlocation', function (event, transferObject) {
                    setInitialState();
                    UserUtils.updateCustomerLocations($scope.userCustomers, transferObject);
                });

                $scope.$watch(function () {
                    return !ShipmentUtils.getDictionaryValues().notificationTypes
                            ? undefined : ShipmentUtils.getDictionaryValues().notificationTypes.length;
                }, function () {
                    getAllNotifications();
                });
            }]
        };
    }
]);