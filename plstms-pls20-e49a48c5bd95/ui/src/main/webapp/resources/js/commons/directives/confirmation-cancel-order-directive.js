/**
 * AngularJS directive for confirmation of cancelling order.
 *
 * @author: Dmitry Nikolaenko
 */
angular.module('plsApp.directives').directive('plsConfirmCancelOrderDialog', ['ShipmentOperationsService', 'manualBolModel',
    function (ShipmentOperationsService, manualBolModel) {
        return {
            restrict: 'A',
            scope: {
                closeHandler: '&',
                showConfirmationDialog: '=',
                parentDialog: '@',
                shipmentId: '=',
                status: '=',
                isManualBol: '=',
                apiCapable: '='
            },
            replace: true,
            templateUrl: 'pages/tpl/confirmation-cancel-order.html',
            controller: ['$scope', function ($scope) {
                'use strict';
                
                $scope.dialogOptions = {
                    parentDialog: $scope.parentDialog
                };

                $scope.$watch('showConfirmationDialog', function (newVal) {
                    if (newVal === false) {
                        $scope.$root.$broadcast('event:confirm-cancel-order-dialog-closed');
                    } else if (newVal === true) {
                        $scope.$root.$broadcast('event:confirm-cancel-order-dialog-open');
                    }
                });

                $scope.cancelOrder = function () {
                    $scope.showConfirmationDialog = false;
                    if ($scope.isManualBol) {
                        manualBolModel.cancel($scope.$root.authData.organization.orgId, $scope.shipmentId, $scope.closeHandler());
                    } else {
                        ShipmentOperationsService.cancelShipment({
                            customerId: $scope.$root.authData.organization.orgId,
                            shipmentId: $scope.shipmentId
                        }, function () {
                            $scope.closeHandler();
                            $scope.$root.$emit('event:operation-success', 'Cancel order', 'Order #' + $scope.shipmentId
                                    + ' was cancelled successfully');
                            $scope.$emit('event:shipmentDataUpdated');
                        }, function () {
                            $scope.$root.$emit('event:application-error', 'Shipment cancellation failed!',
                                    'Shipment with id: ' + $scope.shipmentId + ' cannot be cancelled');
                        });
                    }
                };
            }]
        };
    }
]);