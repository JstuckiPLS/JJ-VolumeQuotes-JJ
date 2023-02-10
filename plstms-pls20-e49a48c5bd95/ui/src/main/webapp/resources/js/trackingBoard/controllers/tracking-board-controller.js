angular.module('pls.controllers').controller('TrackingBoardController', ['$scope', '$templateCache', '$http', '$location', 'ShipmentUtils',
    'ShipmentOperationsService', 'ExportDataBuilder', 'ExportService', 'TrackingBoardService', '$q', '$filter',
    function ($scope, $templateCache, $http, $location, ShipmentUtils, ShipmentOperationsService, ExportDataBuilder, ExportService, 
            TrackingBoardService, $q, $filter) {
        'use strict';

        var pickUpStatuses = ['IN_TRANSIT', 'DELIVERED', 'CANCELLED', 'OUT FOR DELIVERY'];
        var selectedShipment;

        $scope.openEditSalesOrderDialog = function (options) {
            $scope.$broadcast('event:showEditSalesOrder', options);
        };

        _.each([
            'pages/content/sales-order/so-general-adjustment-information.html',
            'pages/content/sales-order/so-general-information.html',
            'pages/content/sales-order/so-addresses.html',
            'pages/content/sales-order/so-details.html',
            'pages/content/sales-order/so-docs.html',
            'pages/content/sales-order/so-notes.html',
            'pages/content/sales-order/so-vendor-bills.html',
            'pages/content/sales-order/so-tracking.html',
            'pages/content/sales-order/sales-order-customer-carrier.html',

            'pages/tpl/quote-address-tpl.html',
            'pages/tpl/edit-shipment-details-tpl.html',
            'pages/tpl/view-notes-tpl.html',
            'pages/tpl/view-vendor-bill-tpl.html',
            'pages/tpl/pls-zip-select-specific-tpl.html',
            'pages/tpl/quote-price-info-tpl.html',
            'pages/tpl/pls-add-note-tpl.html',
            'pages/tpl/products-data-tpl.html',
            'pages/tpl/product-list-tpl.html',
            'pages/tpl/pls-bill-to-list-tpl.html',
            'pages/tpl/pls-location-list-tpl.html'
        ], function (template) {
            if (!$templateCache.get(template)) {
                $http.get(template, {cache: $templateCache});
            }
        });

        $scope.toShipmentEntry = function (orderId) {
            $scope.$root.ignoreLocationChange();
            $scope.$root.redirectToUrl('/shipment-entry/' + orderId, {from: $location.url()});
        };

        $scope.copyShipment = function (shipmentId, customerId) {
            $scope.$root.ignoreLocationChange();
            $scope.$root.redirectToUrl("/shipment-entry", {loadId: shipmentId, customerId: customerId, from: $location.url() });
        };

        $scope.isPermittedToCancelShipment = function () {
            return $scope.$root.isFieldRequired('CANCEL_SHIPMENT_BEFORE_DISPATCHED');
        };

        $scope.isShipmentCancellable = function (shipment) {
            return !_.isUndefined(shipment);
        };

        $scope.isPermittedToCopyShipment = function () {
            return $scope.$root.isFieldRequired('ADD_SHIPMENT_ENTRY');
        };

        $scope.isPermittedToExport = function () {
            return $scope.$root.isFieldRequired('EXPORT_SHIPMENT_LIST') || $scope.$root.isFieldRequired('EXPORT_SHIPMENT_LIST_WITH_COST_AND_MARGIN');
        };

        $scope.exportWithoutCostMargin = function () {
            return $scope.$root.isFieldRequired('EXPORT_SHIPMENT_LIST') && !$scope.$root.isFieldRequired('EXPORT_SHIPMENT_LIST_WITH_COST_AND_MARGIN');
        };

        $scope.exportAllShipments = function (shipmentGrid, totals, fileName) {
            var exportFileName = fileName ? fileName : 'CustomersShipments_Exportfile';
            var fileFormat = exportFileName + '_{0,date,' + $scope.$root.exportFileNameDateFormat + '}.xlsx';
            var sheetName = "Shipment Orders";

            var shipmentEntities = _.map(shipmentGrid.ngGrid.filteredRows, function (item) {
                return item.entity;
            });

            var columnNames = ExportDataBuilder.getColumnNames(shipmentGrid);
            var footerData = ExportDataBuilder.buildTotalsFooterData(columnNames, totals);
            var headerData = [[]];
            var exportData = ExportDataBuilder.buildExportData(shipmentGrid, shipmentEntities, fileFormat, sheetName, footerData, headerData);

            ExportService.exportData(exportData);
        };

        $scope.isPermittedToViewEditButton = function (selectedShipments) {
            if (_.isEmpty(selectedShipments)) {
                return false;
            }

            return ($scope.$root.isFieldRequired('EDIT_SHIPMENT_BEFORE_PICKUP') || $scope.$root.isFieldRequired('EDIT_SHIPMENT_AFTER_PICKUP'))
                    && (!selectedShipments[0].requirePermissionChecking
                    || ((selectedShipments[0].requirePermissionChecking
                    && $scope.$root.isFieldRequired('EDIT_FROM_ALL_SHIPMENTS_WHEN_LOAD_ON_FINANCIAL_BOARD'))
                    || !$scope.isAllShipmentsPage));
        };

        $scope.isPermittedToEdit = function (selectedShipments) {
            var beforePickupStatuses = ['OPEN', 'BOOKED', 'DISPATCHED'];
            var afterPickupStatuses = ['IN_TRANSIT', 'OUT_FOR_DELIVERY', 'DELIVERED'];

            if (_.isEmpty(selectedShipments)) {
                return false;
            }

            if ($scope.$root.isFieldRequired('EDIT_SHIPMENT_BEFORE_PICKUP') && (beforePickupStatuses.indexOf(selectedShipments[0].status) !== -1)) {
                return true;
            }

            return $scope.$root.isFieldRequired('EDIT_SHIPMENT_AFTER_PICKUP') && (afterPickupStatuses.indexOf(selectedShipments[0].status) !== -1);
        };

        function shipmentHasBeenAlreadyPickedUp(shipment) {
            return _.contains(pickUpStatuses, shipment.status);
        }

        function showNotification(shipment) {
            TrackingBoardService.getContactInfo({shipmentId: shipment.shipmentId === undefined ? shipment.id : shipment.shipmentId},
                    function (response) {
                        var message = 'A shipment cannot be cancelled once it has been picked up.' +
                                'Contact your PLS representative to reroute or return the shipment.';

                        message = message.concat('<p>Contact: ').concat(response.contactName).concat('</p>');
                        message = message.concat('<p>Phone: ').concat($filter('phone')(response.phone)).concat('</p>');
                        message = message.concat('<p>Email: <a href="' + response.email + '">').concat(response.email).concat('</a></p>');

                        $scope.$root.$emit('event:application-warning', 'Cancellation failed!', message);
                    },
                    function (error) {
                        $scope.$root.$emit('event:application-error', 'Contact information retrival failed!',
                                'Failed to load contact information. ' + error.toString());
                    });
        }

        function cancelShipment() {
            var id = angular.isDefined(selectedShipment.shipmentId) ? selectedShipment.shipmentId : selectedShipment.id;
            var deferred = $q.defer();

            ShipmentOperationsService.cancelShipment({customerId: $scope.$root.authData.organization.orgId, shipmentId: id}, function () {
                $scope.$root.$emit('event:operation-success', 'Cancel order', 'Order #' + id + ' was cancelled successfully');
                selectedShipment.status = 'CANCELLED';
                deferred.resolve(selectedShipment.status);
            }, function (error) {
                $scope.$root.$emit('event:application-error', 'Shipment cancellation failed!', 'Shipment with id: ' + id + ' cannot be cancelled');
                deferred.reject(error);
            });

            return deferred.promise;
        }

        $scope.cancelShipment = function (shipment, isHideConfirmationDialog) {
            selectedShipment = shipment;
            var id = angular.isDefined(shipment.shipmentId) ? shipment.shipmentId : shipment.id;
            if (shipmentHasBeenAlreadyPickedUp(shipment) || !ShipmentUtils.isShipmentCancellable(shipment)) {
                showNotification(shipment);
            } else {
                if (isHideConfirmationDialog === true) {
                    return cancelShipment();
                } else {
                    $scope.$root.$broadcast('event:showConfirmation', {
                        caption: 'Cancel Order',
                        message: '<div class="text-center"><p>You are canceling the Order #<b>' + id +'</b></p> <br/>' +
                        '<p>The notification will be sent to Carrier.</p><br/>Do you want to proceed?</div>',
                        okFunction: cancelShipment
                    });
                }
            }
        };

        $scope.$on('event:closeAndRedirect', function (event, url, params) {
            $location.url(url).search(params);
        });
    }
]);