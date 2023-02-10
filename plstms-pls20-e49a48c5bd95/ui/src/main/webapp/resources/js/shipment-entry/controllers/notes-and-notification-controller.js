angular.module('shipmentEntry').controller('NotesAndNotificationController', ['$scope', 'ShipmentUtils', 'UserNotificationsService',
    function ($scope, ShipmentUtils, UserNotificationsService) {
        'use strict';

        $scope.$watch(function () {
            return ShipmentUtils.getDictionaryValues().notificationTypes;
        }, function (newValue) {
            if (!_.isUndefined(newValue)) {
                $scope.shipmentEntryData.notificationTypes = angular.copy(newValue);
            }
        });

        $scope.clearSelectedNotificationTypes = function () {
            _.each($scope.shipmentEntryData.notificationTypes, function (notificationType) {
                notificationType.selected = false;
            });
        };

        $scope.selectEmailAddress = function (email) {
            if (!email || email === 'null') {
                $scope.clearSelectedNotificationTypes();
                return;
            }

            $scope.shipmentEntryData.editedEmail = email;

            if (!$scope.isNewNotification()) {
                $scope.clearSelectedNotificationTypes();

                _.each($scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications, function (notification) {
                    if ($scope.shipmentEntryData.editedEmail === notification.emailAddress) {
                        _.findWhere($scope.shipmentEntryData.notificationTypes, {value: notification.notificationType}).selected = true;
                    }
                });
            }
        };

        $scope.isNewNotification = function () {
            return _.find($scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications, function (notification) {
                        return notification.emailAddress === $scope.shipmentEntryData.editedEmail;
                    }) === undefined;
        };

        $scope.isAnyNotificationSelected = function () {
            return _.findWhere($scope.shipmentEntryData.notificationTypes, {selected: true}) !== undefined;
        };

        $scope.addEmailNotifications = function () {
            if (_.isUndefined($scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications)) {
                $scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications = [];
            }

            _.each($scope.shipmentEntryData.notificationTypes, function (notificationType) {
                if (notificationType.selected) {
                    $scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications.push({
                        emailAddress: $scope.shipmentEntryData.editedEmail,
                        notificationType: notificationType.value
                    });
                }
            });

            $scope.shipmentEntryData.editedEmail = undefined;
            $scope.clearSelectedNotificationTypes();
        };

        $scope.removeEmailNotifications = function () {
            $scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications =
                    _.filter($scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications,
                            function (notification) {
                                return notification.emailAddress !== $scope.shipmentEntryData.editedEmail;
                            });

            $scope.shipmentEntryData.editedEmail = undefined;
            $scope.clearSelectedNotificationTypes();
        };

        $scope.getEmails = function () {
            if ($scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications) {
                return _.uniq(_.pluck($scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications, 'emailAddress'));
            } else {
                return [];
            }
        };

        $scope.changeNotificationType = function (notificationType) {
            if ($scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications && !$scope.isNewNotification()) {
                if (notificationType.selected) {
                    $scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications.push({
                        emailAddress: $scope.shipmentEntryData.editedEmail,
                        notificationType: notificationType.value
                    });
                } else {
                    $scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications =
                            _.filter($scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications,
                                    function (notification) {
                                        return notification.emailAddress !== $scope.shipmentEntryData.editedEmail
                                                || notification.notificationType !== notificationType.value;
                                    }
                            );
                }
            }
        };

        $scope.$on('event:updateNotifications', function (event, selectedCustomer) {
            if (_.isUndefined(selectedCustomer) || _.isUndefined(selectedCustomer.id)) {
                return;
            }

            $scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications = [];
            $scope.clearSelectedNotificationTypes();

            UserNotificationsService.getUserNotifications({
                customerId: selectedCustomer.id,
                userId: $scope.authData.personId
            }, function (user) {
                if (user) {
                    _.each(user.notifications, function (notification) {
                        $scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications.push({
                            emailAddress: user.email,
                            notificationType: notification
                        });
                    });
                }
            });
        });
    }
]);