/**
 * AngularJS directive which displays order details information with possibility to edit it.
 *
 * @author: Sergey Kirichenko
 * Date: 30.06.13
 * Time: 17:02
 */
angular.module('plsApp').directive('plsEditShipmentDetails', ['ShipmentUtils', function (ShipmentUtils) {
    return {
        restrict: 'A',
        scope: {
            shipment: '=plsEditShipmentDetails',
            selectedCustomer: '=',
            invalidIdentifier: '=?',
            viewId: '@',
            parentViewId: '@',
            hideCarrierName: '=',
            readOnly: '=',
            salesOrder: '='
        },
        templateUrl: 'pages/tpl/edit-shipment-details-tpl.html',
        compile: function () {
            return {
                pre: function (scope) {
                    'use strict';

                    scope.pageModel = {};

                    if (!scope.shipment.finishOrder.jobNumbers) {
                        scope.shipment.finishOrder.jobNumbers = [];
                    }

                    if (!scope.shipment.finishOrder.shipmentNotifications) {
                        scope.shipment.finishOrder.shipmentNotifications = [];
                    }

                    scope.authData = scope.$root.authData;
                    scope.pageModel.notificationTypes = angular.copy(ShipmentUtils.getDictionaryValues().notificationTypes);

                    function clearSelectedNotificationTypes() {
                        _.each(scope.pageModel.notificationTypes, function (notificationType) {
                            notificationType.selected = false;
                        });
                    }

                    scope.openAddressesListDialog = function () {
                        clearSelectedNotificationTypes();
                        scope.pageModel.editedEmail = '';
                        scope.$root.$broadcast('event:show-customer-notification-list');
                    };

                    scope.$on('event:customer-notification-selected', function (event, addressItem) {
                        scope.selectEmailAddress(addressItem.email);
                    });

                    scope.selectEmailAddressPaste = function () {
                        clearSelectedNotificationTypes();
                    };

                    scope.selectEmailAddress = function (email) {
                        if (!email || email === 'null') {
                            clearSelectedNotificationTypes();
                            return;
                        }

                        scope.pageModel.editedEmail = email;

                        if (!scope.isNewNotification()) {
                            clearSelectedNotificationTypes();

                            _.each(scope.shipment.finishOrder.shipmentNotifications, function (notification) {
                                if (scope.pageModel.editedEmail === notification.emailAddress) {
                                    _.findWhere(scope.pageModel.notificationTypes, {value: notification.notificationType}).selected = true;
                                }
                            });
                        }
                    };

                    scope.getEmails = function () {
                        if (scope.shipment.finishOrder.shipmentNotifications) {
                            return _.uniq(_.pluck(scope.shipment.finishOrder.shipmentNotifications, 'emailAddress'));
                        } else {
                            return [];
                        }
                    };

                    scope.isNewNotification = function () {
                        return _.find(scope.shipment.finishOrder.shipmentNotifications, function (notification) {
                                    return notification.emailAddress === scope.pageModel.editedEmail;
                                }) === undefined;
                    };

                    scope.isAnyNotificationSelected = function () {
                        return _.findWhere(scope.pageModel.notificationTypes, {selected: true}) !== undefined;
                    };

                    scope.addEmailNotifications = function () {
                        _.each(scope.pageModel.notificationTypes, function (notificationType) {
                            if (notificationType.selected) {
                                scope.shipment.finishOrder.shipmentNotifications.push({
                                    emailAddress: scope.pageModel.editedEmail,
                                    notificationType: notificationType.value
                                });
                            }
                        });
                        scope.pageModel.editedEmail = undefined;
                        clearSelectedNotificationTypes();
                    };

                    scope.removeEmailNotifications = function () {
                        scope.shipment.finishOrder.shipmentNotifications = _.filter(scope.shipment.finishOrder.shipmentNotifications,
                                function (notification) {
                                    return notification.emailAddress !== scope.pageModel.editedEmail;
                                });

                        scope.pageModel.editedEmail = undefined;
                        clearSelectedNotificationTypes();
                    };

                    scope.changeNotificationType = function (notificationType) {
                        if (scope.shipment.finishOrder.shipmentNotifications && !scope.isNewNotification()) {
                            if (notificationType.selected) {
                                scope.shipment.finishOrder.shipmentNotifications.push({
                                    emailAddress: scope.pageModel.editedEmail,
                                    notificationType: notificationType.value
                                });
                            } else {
                                scope.shipment.finishOrder.shipmentNotifications = _.filter(scope.shipment.finishOrder.shipmentNotifications,
                                        function (notification) {
                                            return notification.emailAddress !== scope.pageModel.editedEmail
                                                    || notification.notificationType !== notificationType.value;
                                        }
                                );
                            }
                        }
                    };
                },
                post: function (scope) {
                    'use strict';

                    if (scope.shipment.originDetails.address) {
                        if (!scope.shipment.finishOrder.pickupWindowFrom) {
                            scope.shipment.finishOrder.pickupWindowFrom = scope.shipment.originDetails.address.pickupWindowFrom;
                        }

                        if (!scope.shipment.finishOrder.pickupWindowTo) {
                            scope.shipment.finishOrder.pickupWindowTo = scope.shipment.originDetails.address.pickupWindowTo;
                        }
                    }

                    if (scope.shipment.destinationDetails.address) {
                        if (!scope.shipment.finishOrder.deliveryWindowFrom) {
                            scope.shipment.finishOrder.deliveryWindowFrom = scope.shipment.destinationDetails.address.deliveryWindowFrom;
                        }

                        if (!scope.shipment.finishOrder.deliveryWindowTo) {
                            scope.shipment.finishOrder.deliveryWindowTo = scope.shipment.destinationDetails.address.deliveryWindowTo;
                        }
                    }

                    function setupFirstRowHeight() {
                        var notificationDiv = $('#notificationDiv');
                        var notesDiv = $('#notesDiv');

                        var maxHeight = notesDiv.height();

                        if (maxHeight) {
                            notificationDiv.height(maxHeight);
                        }
                    }

                    scope.$watch(function () {
                        return $('#notesDiv').outerHeight();
                    }, function (newValue, oldValue) {
                        if (newValue !== oldValue) {
                            setupFirstRowHeight();
                        }
                    });

                    setupFirstRowHeight();

                    scope.$watch(function () {
                        return $('#refsDiv').find(':first-child').outerHeight();
                    }, function (newValue, oldValue) {
                        if (newValue !== oldValue) {
                            var pickupWindowDiv = $('#pickupWindowDiv');
                            var parentRefsDiv = $('#refsDiv');
                            var refsDiv = parentRefsDiv.find(':first-child');

                            var maxHeight = pickupWindowDiv.height();
                            var blockHeight = refsDiv.height();
                            maxHeight = maxHeight > blockHeight ? maxHeight : blockHeight;

                            if (maxHeight) {
                                pickupWindowDiv.height(maxHeight);
                                parentRefsDiv.height(maxHeight);
                            }
                        }
                    });

                    /* Set cell editing config for jobGrid */
                    if (scope.salesOrder && angular.isDefined(scope.readOnly)) {
                        scope.$watch('readOnly', function (newValue) {
                            if (scope.jobGrid && scope.jobGrid.ngGrid) {
                                scope.jobGrid.ngGrid.config.enableCellEdit = !newValue;
                                scope.jobGrid.ngGrid.buildColumns();
                            }
                        });
                    } else {
                        scope.$watch('jobGrid.ngGrid', function (newValue) {
                            if (newValue) {
                                scope.jobGrid.ngGrid.config.enableCellEdit = true;
                                scope.jobGrid.ngGrid.buildColumns();
                            }
                        });
                    }
                }
            };
        }
    };
}]);