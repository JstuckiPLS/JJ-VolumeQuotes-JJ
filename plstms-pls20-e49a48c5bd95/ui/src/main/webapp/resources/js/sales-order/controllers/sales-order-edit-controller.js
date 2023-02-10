angular.module('pls.controllers').controller('EditSalesOrderCtrl', ['$scope', '$rootScope', '$q', '$cacheFactory', '$filter', '$timeout',
    'ShipmentUtils', 'ShipmentOperationsService', 'AddressesListService', 'MatchedLoadsService',
    'ShipmentDocumentEmailService','ShipmentSavingService',
    function ($scope, $rootScope, $q, $cacheFactory, $filter, $timeout, ShipmentUtils, ShipmentOperationsService,
              AddressesListService, MatchedLoadsService, ShipmentDocumentEmailService, ShipmentSavingService) {
        'use strict';

        $scope.editSalesOrderModel = {
            selectedTab: '',
            showEditOrderDialog: false,
            showEditOrderDialogOptions: {},
            closeHandler: null,
            readOnlyModeAllowed: false,
            formDisabled: false,
            editOrder: true
        };
        
        $scope.hideCreatedTime = false;

        function addBreadCrumb(id, label) {
            var breadCrumb = new $scope.$root.BreadCrumb(id, label);
            $scope.wizardData.breadCrumbs.list.push(breadCrumb);
            $scope.wizardData.breadCrumbs.map[breadCrumb.id] = breadCrumb;
        }

        function init() {
            $scope.wizardData.breadCrumbs = {};
            $scope.wizardData.breadCrumbs.list = [];
            $scope.wizardData.breadCrumbs.map = {};

            // ATTENTION!!! order of bread crumbs affects validation and availability of tabs
            addBreadCrumb('emails_history');
            addBreadCrumb('notes');
            addBreadCrumb('vendor_bills');
            addBreadCrumb('tracking');
            addBreadCrumb('data_history');
            addBreadCrumb('general_information');
            addBreadCrumb('addresses');
            addBreadCrumb('details');
            addBreadCrumb('docs');
            addBreadCrumb('audit');
            addBreadCrumb('ok_click');
            $scope.isEstDeliveryRequired = true;
        }

        $scope.isPageOpen = function (pageName) {
            return $scope.editSalesOrderModel.selectedTab === pageName;
        };

        $scope.selectTab = function (id) {
            if ($scope.editSalesOrderModel.formDisabled || $scope.isAvailable(id)) {
                $scope.editSalesOrderModel.selectedTab = id;

                $timeout(function () {
                    $scope.$broadcast('event:edit-sales-order-tab-change', id);
                });
            }
        };

        $scope.switchEditMode = function () {
            if (!$scope.isAvailable($scope.editSalesOrderModel.selectedTab)) {
                $scope.editSalesOrderModel.selectedTab = 'general_information';
            }

            $scope.wizardData.vendorBillModel.shipmentStatus = $scope.wizardData.shipment.status;

            // backup shipment to be able to restore it after user edited load and pressed cancel (for readonly mode)
            $scope.wizardData.shipmentBackup = angular.copy($scope.wizardData.shipment);
            $scope.wizardData.shipmentBackup.totalShipperAmt = 0;

            // @TODO create or use existing service
            _.each($scope.wizardData.shipmentBackup.selectedProposition.costDetailItems, function (item) {
                if (item.costDetailOwner === 'S') {
                    $scope.wizardData.shipmentBackup.totalShipperAmt += item.subTotal;
                }
            });

            $scope.editSalesOrderModel.formDisabled = false;
            $scope.displayWarning = false;
        };

        $scope.isAvailable = function (id) {
            if (!$scope.editSalesOrderModel || !$scope.editSalesOrderModel.selectedTab || !$scope.wizardData
                    || !$scope.wizardData.breadCrumbs || !$scope.wizardData.breadCrumbs.list) {
                return false;
            }

            var i = 0;
            var stepObject = $scope.wizardData.breadCrumbs.list[i];

            while (stepObject.id !== id) {
                if (stepObject.validNext && angular.isFunction(stepObject.validNext) && !stepObject.validNext()) {
                    return false;
                }

                i += 1;
                stepObject = $scope.wizardData.breadCrumbs.list[i];
            }

            return true;
        };

        function resetOriginAddress() {
            if ($scope.wizardData.shipment.originDetails.zip && (!$scope.wizardData.shipment.originDetails.address
                    || !_.isEqual($scope.wizardData.shipment.originDetails.address.zip, $scope.wizardData.shipment.originDetails.zip))) {
                $scope.wizardData.shipment.originDetails.address = {};
                $scope.wizardData.shipment.originDetails.address.addressName = '';
                $scope.wizardData.shipment.originDetails.address.phone = {};
                $scope.wizardData.shipment.originDetails.address.fax = {type: 'FAX'};
                $scope.wizardData.shipment.originDetails.address.zip = $scope.wizardData.shipment.originDetails.zip;
                $scope.wizardData.origForm = {};
                $scope.wizardData.shipment.finishOrder.pickupWindowFrom = undefined;
                $scope.wizardData.shipment.finishOrder.pickupWindowTo = undefined;
            }
        }

        function resetDestinationAddress() {
            if ($scope.wizardData.shipment.destinationDetails.zip && (!$scope.wizardData.shipment.destinationDetails.address
                    || !_.isEqual($scope.wizardData.shipment.destinationDetails.address.zip, $scope.wizardData.shipment.destinationDetails.zip))) {
                $scope.wizardData.shipment.destinationDetails.address = {};
                $scope.wizardData.shipment.destinationDetails.address.addressName = '';
                $scope.wizardData.shipment.destinationDetails.address.phone = {};
                $scope.wizardData.shipment.destinationDetails.address.fax = {type: 'FAX'};
                $scope.wizardData.shipment.destinationDetails.address.zip = $scope.wizardData.shipment.destinationDetails.zip;
                $scope.wizardData.destForm = {};
                $scope.wizardData.shipment.finishOrder.deliveryWindowFrom = undefined;
                $scope.wizardData.shipment.finishOrder.deliveryWindowTo = undefined;
            }
        }

        /**
         * Clear address when zip changes
         */
        $scope.$watch('wizardData.shipment.originDetails.zip', resetOriginAddress, true);

        /**
         * Clear addresses when customer changes
         */
        $scope.$on('event:customer-changed', resetOriginAddress);
        $scope.$on('event:customer-changed', resetDestinationAddress);

        /**
         * Clear address when zip changes
         */
        $scope.$watch('wizardData.shipment.destinationDetails.zip', resetDestinationAddress, true);

        $scope.$on('event:showEditSalesOrder', function (event, dialogDetails) {
            if (dialogDetails) {
                $scope.initialize();
                init();
                $scope.editSalesOrderModel.showEditOrderDialog = true;
                $scope.displayWarning = false;
                $scope.editSalesOrderModel.readOnlyModeAllowed = dialogDetails.formDisabled;
                $scope.editSalesOrderModel.formDisabled = dialogDetails.formDisabled;
                $scope.editSalesOrderModel.showEditOrderDialogOptions.parentDialog = dialogDetails.parentDialog;
                $scope.editSalesOrderModel.closeHandler = dialogDetails.closeHandler || null;

                ShipmentOperationsService.getShipment({
                    customerId: $scope.$root.authData.organization.orgId,
                    shipmentId: dialogDetails.shipmentId
                }, function (data) {
                    $scope.handleShipment(dialogDetails, data);
                }, function () {
                    $scope.$root.$emit('event:application-error',
                            'Shipment load failed!', 'Can\'t load shipment with ID ' + dialogDetails.shipmentId);
                });
            }
        });

        $scope.$on('event:showNewSalesOrder', function (event, dialogDetails, data) {
            if (dialogDetails) {
                $scope.initialize();
                init();
                $scope.editSalesOrderModel.readOnlyModeAllowed = dialogDetails.formDisabled;
                $scope.editSalesOrderModel.formDisabled = dialogDetails.formDisabled;
                $scope.editSalesOrderModel.showEditOrderDialogOptions.parentDialog = dialogDetails.parentDialog;
                $scope.editSalesOrderModel.closeHandler = dialogDetails.closeHandler;
                $scope.handleShipment(dialogDetails, data);
                $scope.editSalesOrderModel.showEditOrderDialog = true;
                $scope.editSalesOrderModel.isLoaded = true;
            }
        });

        $scope.handleShipment = function (dialogDetails, data) {
            $scope.isEstDeliveryRequired = !_.isUndefined(data.finishOrder.estimatedDelivery);
            $scope.wizardData.shipment = data;
            $scope.wizardData.oldStatus = $scope.wizardData.shipment.status;
            $scope.wizardData.oldProNumber = $scope.wizardData.shipment.proNumber;
            $scope.wizardData.oldCarrier = $scope.wizardData.shipment.selectedProposition.carrier;
            $scope.wizardData.selectedCustomer.id = data.organizationId;
            $scope.wizardData.selectedCustomer.name = data.customerName;
            $scope.editSalesOrderModel.selectedTab = dialogDetails.selectedTab || 'general_information';
            $scope.selectTab($scope.editSalesOrderModel.selectedTab);
            $scope.wizardData.isBillingStatusNone = dialogDetails.isBillingStatusNone;

            if (!$scope.wizardData.shipment.guaranteedBy && $scope.wizardData.shipment.guaranteedBy !== 0) {
                $scope.wizardData.shipment.guaranteedBy = undefined;
            }

            if ($scope.wizardData.shipment.originDetails.address || $scope.wizardData.shipment.destinationDetails.address) {
                $scope.wizardData.origForm = {};
                $scope.wizardData.destForm = {};
            }

            $scope.editSalesOrderModel.originAddress = angular.copy($scope.wizardData.shipment.originDetails.address);
            $scope.editSalesOrderModel.destinationAddress = angular.copy($scope.wizardData.shipment.destinationDetails.address);

            if ($scope.wizardData.shipment.invoiceDate) {
                // invoiced shipment must not be edited
                $scope.editSalesOrderModel.formDisabled = true;
            }

            $scope.wizardData.shipmentBackup = angular.copy($scope.wizardData.shipment);
        };

        $scope.closeEditOrder = function (preventEditMode) {
            if (preventEditMode) {
                $scope.editSalesOrderModel.showEditOrderDialog = false;
                $scope.editSalesOrderModel.isLoaded = false;
                return;
            }

            if ($scope.editSalesOrderModel.readOnlyModeAllowed) {
                $scope.editSalesOrderModel.formDisabled = true;
                $scope.wizardData.shipment = $scope.wizardData.shipmentBackup;

                if (angular.isUndefined($scope.wizardData.shipmentBackup.selectedProposition.carrier)) {
                    angular.element('#inputSCAC').controller('ngModel').$setViewValue('');
                    angular.element('#inputSCAC').controller('ngModel').$render();
                    delete $scope.wizardData.carrierTuple;
                }

                if ($scope.wizardData.shipment.selectedProposition
                        && $scope.wizardData.shipment.selectedProposition.carrier) {
                    $scope.wizardData.carrierTuple = {
                        id: $scope.wizardData.shipment.selectedProposition.carrier.id,
                        name: $scope.wizardData.shipment.selectedProposition.carrier.scac + ':'
                        + $scope.wizardData.shipment.selectedProposition.carrier.name,
                        currencyCode: $scope.wizardData.shipment.selectedProposition.carrier.currencyCode
                    };

                    $scope.$broadcast('event:edit-sales-order-tab-close');
                }

                $scope.wizardData.selectedCustomer = {
                    id: $scope.wizardData.shipment.organizationId,
                    name: $scope.wizardData.shipment.customerName
                };
            } else {
                $scope.editSalesOrderModel.showEditOrderDialog = false;
                $scope.editSalesOrderModel.isLoaded = false;
            }
        };

        $scope.resetAfterCancel = function () {
            $scope.closeEditOrder(true);

            if ($scope.editSalesOrderModel.closeHandler) {
                $scope.editSalesOrderModel.closeHandler();
            }
        };

        function resetAfterSave() {
            if (!$scope.wizardData.shipment.guaranteedBy && $scope.wizardData.shipment.guaranteedBy !== 0) {
                $scope.wizardData.shipment.guaranteedBy = undefined;
            }

            if ($scope.editSalesOrderModel.readOnlyModeAllowed) {
                $scope.wizardData.shipmentBackup = angular.copy($scope.wizardData.shipment);
                $scope.editSalesOrderModel.formDisabled = true;
            } else {
                $scope.editSalesOrderModel.showEditOrderDialog = false;
                $scope.editSalesOrderModel.isLoaded = false;
            }

            if ($scope.editSalesOrderModel.closeHandler) {
                $scope.editSalesOrderModel.closeHandler();
            }

            //Re-init selected tab to refresh open page after saving
            $scope.$broadcast('event:edit-sales-order-tab-change', $scope.editSalesOrderModel.selectedTab);
            $scope.$broadcast('event:edit-sales-order-tab-close');
        }

        function sendEmailWithErrorDetails() {
            var wizardCopy = _.clone($scope.wizardData);
            delete wizardCopy.breadCrumbs;
            ShipmentDocumentEmailService.emailDoc({
                recipients: 'aleshchenko@plslogistics.com',
                subject: 'Wrong Bill To',
                content: 'Edit Sales Order: ' + JSON.stringify(wizardCopy),
                loadId: -1
            });
        }

        $scope.$on('hideCreatedTime', function(event, hideTime) {
            $scope.hideCreatedTime = hideTime;
        });

        function isExistVendorBillCarrier() {
            return !_.isUndefined($scope.wizardData.vendorBillModel.vendorBill)
                && !_.isUndefined($scope.wizardData.vendorBillModel.vendorBill.carrier);
        }

        function saveSalesOrder() {
            if (!$scope.wizardData.shipment.status) {
                $scope.wizardData.shipment.status = 'BOOKED';
            }

            ShipmentSavingService.save({
                customerId: $scope.wizardData.selectedCustomer.id,
                hideCreatedTime: $scope.hideCreatedTime
            }, $scope.wizardData.shipment, function (data) {
                $scope.$root.$emit('event:operation-success', 'Save sales order', 'Sales order was successfully saved.');
                if (_.isUndefined($scope.wizardData.shipment.id)
                        && isExistVendorBillCarrier() && !_.isUndefined($scope.wizardData.shipment.selectedProposition.carrier)
                        && $scope.wizardData.vendorBillModel.vendorBill.carrier.id !== $scope.wizardData.shipment.selectedProposition.carrier.id) {
                    ShipmentUtils.showUnmatchCarrierWarning({
                        vendorBillCarrierName: $scope.wizardData.vendorBillModel.vendorBill.carrier.name,
                        vendorBillScac: $scope.wizardData.vendorBillModel.vendorBill.carrier.scac,
                        salesOrderCarrierName: $scope.wizardData.shipment.selectedProposition.carrier.name,
                        salesOrderScac: $scope.wizardData.shipment.selectedProposition.carrier.scac
                    });
                }
                $scope.wizardData.shipment = data;
                $scope.wizardData.oldProNumber = $scope.wizardData.shipment.proNumber;
                $scope.wizardData.oldCarrier = $scope.wizardData.shipment.selectedProposition.carrier;

                // clear cache for terminal info. see TerminalInfoService for more details
                $cacheFactory.get('$http').remove('/restful/shipment/proposal/terminal-info?shipmentId=' + $scope.wizardData.shipment.id);
                resetAfterSave();

                $scope.$root.$broadcast('event:showReasonMessage', {
                    shipment: $scope.wizardData.shipment,
                    parentDlgId: ''
                });
            }, function (response) {
                var errorMessage = 'Error during saving sales order.';

                if (response.status === 426) {
                    errorMessage = 'Order has been updated by another user. Please refresh the page.';
                } else if (response.data && response.data.payload && response.data.payload.billToOrganization) {
                    errorMessage = "Unexpected error occurred. Please refresh page and try again.";
                    sendEmailWithErrorDetails();
                } else if (response.data && response.data.message) {
                    errorMessage = 'Error during saving sales order due to ' + response.data.message;
                }

                $scope.$root.$emit('event:application-error', 'Error on sales order save!', errorMessage);
            });
        }

        $scope.performOnSaveValidationAndSave = function () {
            if (!ShipmentUtils.isCreditLimitValid($scope.wizardData.shipment, $scope.wizardData.shipmentBackup)) {
                return;
            }

            var promises = [];

            _.each($scope.wizardData.breadCrumbs.list, function (breadCrumb) {
                if (breadCrumb.nextAction && angular.isFunction(breadCrumb.nextAction)) {
                    var result = breadCrumb.nextAction();

                    if (angular.isObject(result) && result.then) {
                        //step returns promises as validation function
                        promises.push(result);
                    }
                }
            });

            if (promises.length > 0) {
                $q.all(promises).then(function () {
                    // validation of all promises passed
                    saveSalesOrder();
                });
            } else {
                saveSalesOrder();
            }
        };

        $scope.saveInvoices = function () {
            ShipmentOperationsService.saveAdjustments({
                customerId: $scope.wizardData.selectedCustomer.id,
                pathDefineParam: $scope.wizardData.shipment.id,
                freightBillDate: $filter('date')($scope.wizardData.shipment.freightBillDate, $scope.$root.transferDateFormat)
            }, {
                adjustmentsToSave: {
                    costItems: $scope.wizardData.nonInvoicedAdjustmentAccessorials,
                    loadInfo: $scope.wizardData.newLoadInfo,
                    products: $scope.wizardData.newProducts
                },
                adjustmentsToRemove: $scope.wizardData.removedAdjustments,
                removedDocumentsIds: $scope.wizardData.shipment.removedDocumentsIds,
                uploadedDocuments: $scope.wizardData.shipment.uploadedDocuments,
                notes: $scope.wizardData.shipment.notes
            }, function () {
                $scope.$root.$emit('event:operation-success', 'Success!', 'Adjustments have been saved successfully.');
                $scope.editSalesOrderModel.showEditOrderDialog = false;
                $scope.editSalesOrderModel.isLoaded = false;
                resetAfterSave();
            }, function (response) {
                var errorMessage = 'Error during saving sales order.';

                if (response.status === 426) {
                    errorMessage = 'Order has been updated by another user. Please refresh the page.';
                } else if (response.data && response.data.message) {
                    errorMessage = 'Error during saving sales order due to ' + response.data.message
                            + '<br/>Probably it has been updated by another user.';
                }

                $scope.$root.$emit('event:application-error', 'Error on sales order save!', errorMessage);
            });
        };

        $scope.updateOrder = function () {
            var oldCarrierId = _.isUndefined($scope.wizardData.oldCarrier) ? "" : $scope.wizardData.oldCarrier.id;
            if (!_.isEmpty($scope.wizardData.shipment.proNumber)
                    && $scope.wizardData.shipment.selectedProposition.carrier !== undefined
                    && !($scope.wizardData.oldProNumber === $scope.wizardData.shipment.proNumber
                    && oldCarrierId === $scope.wizardData.shipment.selectedProposition.carrier.id)) {
                MatchedLoadsService.get($scope.wizardData.shipment.proNumber,
                        $scope.wizardData.shipment.selectedProposition.carrier.id).then(function (response) {
                    if (response.data.indexOf($scope.wizardData.shipment.id) > -1) {
                        response.data.splice(response.data.indexOf($scope.wizardData.shipment.id), 1);
                    }

                    if (response.data.length > 0) {
                        $scope.$root.$broadcast('event:showConfirmation', {
                            caption: 'Duplicate Shipment',
                            message: "The PRO # and Carrier on this shipment is the same as Load ID " + response.data.join(', '),
                            okFunction: $scope.performOnSaveValidationAndSave,
                            confirmButtonLabel: "Ok",
                            closeButtonLabel: "Cancel",
                            parentDlgId: 'detailsDialogDiv'
                        });
                    } else {
                        $scope.performOnSaveValidationAndSave();
                    }
                }, function () {
                    $scope.$root.$emit('event:application-warning', 'Duplicate check was not done');
                    $scope.performOnSaveValidationAndSave();
                });
            } else {
                $scope.performOnSaveValidationAndSave();
            }
        };

        $scope.canBeCancelled = function () {
            return ShipmentUtils.isShipmentCancellable($scope.wizardData.shipment) && !_.isUndefined($scope.wizardData.shipment.id);
        };

        $scope.confirmCancel = function () {
            $scope.cancelOrderConfirmationDialog.show = true;
        };

        $scope.cancelOrderConfirmationDialog = {
            show: false
        };

        $scope.isAllShipmentsPage = function () {
            return $rootScope.isAllShipmentsPage;
        };

        $scope.isEditAvailable = function () {
            return !$scope.wizardData.shipment.invoiceDate &&
                    $scope.editSalesOrderModel.formDisabled && $rootScope.isFieldRequired('BOARD_CAN_EDIT_SALES_ORDER') &&
                    (!$scope.wizardData.shipment.requirePermissionChecking || (($scope.wizardData.shipment.requirePermissionChecking &&
                    ($rootScope.isFieldRequired('EDIT_FROM_ALL_SHIPMENTS_WHEN_LOAD_ON_FINANCIAL_BOARD') ||
                    $scope.wizardData.isBillingStatusNone)) || !$scope.isAllShipmentsPage()));
        };
    }
]);
