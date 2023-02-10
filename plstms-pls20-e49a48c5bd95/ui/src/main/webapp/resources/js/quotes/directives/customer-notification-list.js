/**
 * AngularJS directive which displays order details information with possibility to edit it.
 *
 * @author: Sergey Kirichenko
 * Date: 30.06.13
 * Time: 17:02
 */
angular.module('plsApp').directive('plsCustomerNotificationList', ['NgGridPluginFactory', 'ShipmentNotificationSourceService',
    function (NgGridPluginFactory, ShipmentNotificationSourceService) {
        return {
            restrict: 'A',
            scope: {
                dialogId: '=',
                customerId: '=plsCustomerNotificationList',
                parentDialog: '@'
            },
            replace: true,
            templateUrl: 'pages/tpl/customer-notification-list-tpl.html',
            compile: function () {
                return {
                    pre: function (scope) {
                        'use strict';

                        scope.pageModel = {};
                        scope.pageModel.addressesListModalOptions = {};
                        scope.pageModel.addressBookGridData = [];
                        scope.pageModel.addressItems = [];

                        scope.pageModel.adressBookColumnData = [{
                            field: 'name',
                            displayName: 'Name',
                            width: '21%'
                        }, {
                            field: 'contactName',
                            displayName: 'Contact Name',
                            width: '21%'
                        }, {
                            field: 'email',
                            displayName: 'Email',
                            width: '18%'
                        }, {
                            field: 'origin',
                            displayName: 'City, ST, ZIP',
                            width: '18%'
                        }, {
                            field: 'phone',
                            displayName: 'Phone',
                            width: '18%',
                            cellFilter: 'phone'
                        }];
                        scope.pageModel.addressBookGridOptions = {
                            data: 'pageModel.addressBookGridData',
                            columnDefs: 'pageModel.adressBookColumnData',
                            action: function () {
                                scope.selectAddresses();
                            },
                            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin(),
                                NgGridPluginFactory.progressiveSearchPlugin()],
                            enableColumnResize: true,
                            multiSelect: false,
                            selectedItems: scope.pageModel.addressItems,
                            useExternalSorting: false,
                            tabIndex: -10,
                            progressiveSearch: true
                        };

                        scope.closeAddressesListDialog = function () {
                            scope.pageModel.showAddressesList = false;
                        };

                        scope.selectAddresses = function () {
                            scope.$root.$broadcast('event:customer-notification-selected', scope.pageModel.addressItems[0], scope.dialogId);
                            scope.closeAddressesListDialog();
                        };
                    },
                    post: function (scope) {
                        'use strict';

                        scope.pageModel.addressesListModalOptions.parentDialog = scope.parentDialog;

                        scope.$on('event:show-customer-notification-list', function (event, dialogId) {
                            if(dialogId && dialogId !== scope.dialogId) {
                                return;
                            }
                            scope.pageModel.showAddressesList = true;

                            ShipmentNotificationSourceService.getShipmentNotificationsSourceItems({customerId: scope.customerId}, function (data) {
                                scope.pageModel.addressBookGridData = data;
                            });
                        });
                    }
                };
            }
        };
    }
]);