angular.module('plsApp').controller('VendorBillController', [
    '$scope', 'VendorBillService', 'NgGridPluginFactory', 'ExportDataBuilder', 'ExportService', 'ProductTotalsService', 'ShipmentUtils',
    'MatchedLoadsService',
    function ($scope, VendorBillService, NgGridPluginFactory, ExportDataBuilder, ExportService, ProductTotalsService, ShipmentUtils,
            MatchedLoadsService) {
        'use strict';

        var defaultCountry = {id: 'USA', name: 'United States of America', dialingCode: '1'};

        $scope.VBReasonsCode = {
                "DUPLICATE" : "Duplicate",
                "NOT_OURS_TO_PAY" : "Not ours to pay",
                "SHORT_PAY" : "Short Pay",
                "ALREADY_INVOICED" : "Already Invoiced",
                "LOAD_1_0" : "1.0 Load ",
                "OTHER" : "Other"
            };

        $scope.vendorBillsModel = {
            selectedTab : 'unmatched',
            selectedVendorBills : [],
            confirmArchiveDialog : {
                show : false,
                showDialog : function() {
                    $scope.vendorBillsModel.confirmArchiveDialog.show = true;
                },
                close : function() {
                    $scope.vendorBillsModel.confirmArchiveDialog.reason = "";
                    $scope.vendorBillsModel.confirmArchiveDialog.loadId= "";
                    $scope.vendorBillsModel.confirmArchiveDialog.note= "";
                    $scope.vendorBillsModel.confirmArchiveDialog.show = false;
                },
                ok : function () {
                    var vendorBills = [];

                    _.each($scope.vendorBillsModel.selectedVendorBills, function (value) {
                        vendorBills.push(value.id);
                    });

                    var reason = {
                        reason : $scope.vendorBillsModel.confirmArchiveDialog.reason,
                        loadId : $scope.vendorBillsModel.confirmArchiveDialog.loadId,
                        note : $scope.vendorBillsModel.confirmArchiveDialog.note,
                        vendorBills : vendorBills
                    };

                    VendorBillService.archiveList(reason, function () {
                        $scope.refreshTable();
                    });
                    $scope.vendorBillsModel.confirmArchiveDialog.close();
                }
            },
            vendorBillView : {
                show : false
            },
            vendorBillModel : {
                showControls : false,
                totalCost : 0,
                vendorBill : {}
            }
        };

        $scope.isTabSelected = function (tabName) {
            return $scope.vendorBillsModel.selectedTab === tabName;
        };

        $scope.searchSalesOrder = function () {
            $scope.$broadcast("event:showSearchSalesOrder", {
                vendorBillId: $scope.vendorBillsModel.selectedVendorBills[0].id,

                closeHandler: function () {
                    $scope.refreshTable();
                }
            });
        };

        function showUnmatchedCarrierWarning(vendorBill, salesOrder) {
            if (vendorBill.scac !== salesOrder.scac) {
                ShipmentUtils.showUnmatchCarrierWarning({
                    vendorBillCarrierName: vendorBill.carrierName,
                    vendorBillScac: vendorBill.scac,
                    salesOrderCarrierName: salesOrder.carrier,
                    salesOrderScac: salesOrder.scac
                });
            }
        }

        function createSalesOrder() {
            if ($scope.vendorBillsModel.selectedVendorBills[0].customerId || $scope.vendorBillsModel.selectedCustomer) {
                var customerId = $scope.vendorBillsModel.selectedCustomer
                        ? $scope.vendorBillsModel.selectedCustomer.id : $scope.vendorBillsModel.selectedVendorBills[0].customerId;

                VendorBillService.createOrder({
                    subParamId: customerId,
                    vendorBillId: $scope.vendorBillsModel.selectedVendorBills[0].id
                }, function (data) {
                    $scope.checkDefaultData(data);
                    $scope.$broadcast('event:showNewSalesOrder', {
                        shipmentId: '',
                        formDisabled: false,
                        closeHandler: function () {
                            $scope.refreshTable();
                        }
                    }, data);
                    showUnmatchedCarrierWarning($scope.vendorBillsModel.selectedVendorBills[0], data.selectedProposition.carrier);
                    $scope.refreshTable();
                }, function (result) {
                    if (result && result.data && result.data.message) {
                        $scope.$root.$emit('event:application-error', 'Create sales order failed!', 'Can\'t create sales order.' +
                                result.data.message);
                    } else {
                        $scope.$root.$emit('event:application-error', 'Create sales order failed!', 'Can\'t create sales order.');
                    }
                });

                delete $scope.vendorBillsModel.selectedCustomer;
            } else {
                $scope.vendorBillsModel.showCustomerDialog = true;
            }
        }

        $scope.checkingMatchLoads = function() {
            if ($scope.vendorBillsModel.selectedVendorBills[0].proNumber &&
                    $scope.vendorBillsModel.selectedVendorBills[0].carrierId) {
                MatchedLoadsService.get($scope.vendorBillsModel.selectedVendorBills[0].proNumber,
                        $scope.vendorBillsModel.selectedVendorBills[0].carrierId).then(function (response) {
                    if (response.data.length > 0) {
                        $scope.$root.$broadcast('event:showConfirmation', {
                            caption: 'Duplicate Shipment',
                            message: "The PRO # and Carrier on this shipment is the same as Load ID " + response.data.join(', '),
                            closeButtonHide: true
                        });
                        var vendorBills = [];
                        vendorBills.push($scope.vendorBillsModel.selectedVendorBills[0].id);

                        var reason = {
                            reason : 'DUPLICATE',
                            loadId : response.data.join(', '),
                            vendorBills : vendorBills
                        };

                        VendorBillService.archiveList(reason, $scope.refreshTable);

                    } else {
                        createSalesOrder();
                    }
                }, function () {
                    $scope.$root.$emit('event:application-warning', 'Duplicate check was not done');
                    createSalesOrder();
                });
            } else {
                createSalesOrder();
            }
        };

        $scope.closeCustomerDialog = function () {
            $scope.vendorBillsModel.showCustomerDialog = false;
            delete $scope.vendorBillsModel.selectedCustomer;
        };

        $scope.createSalesOrderWithCustomer = function () {
            $scope.vendorBillsModel.showCustomerDialog = false;
            createSalesOrder();
        };

        $scope.checkDefaultData = function (data) {
            if (!_.isUndefined(data.originDetails.zip) && _.isEmpty(data.originDetails.zip.country)) {
                data.originDetails.zip.country = defaultCountry;
            }

            if (!_.isUndefined(data.destinationDetails.zip) && _.isEmpty(data.destinationDetails.zip.country)) {
                data.destinationDetails.zip.country = defaultCountry;
            }

            if (_.isUndefined(data.finishOrder.pickupDate)) {
                data.finishOrder.pickupDate = $scope.$root.formatDate(new Date());
            }

            if (_.isUndefined(data.finishOrder.estimatedDelivery)) {
                data.finishOrder.estimatedDelivery = $scope.$root.formatDate(new Date());
            }
        };

        $scope.viewVendorBill = function () {
            VendorBillService.get({vendorBillId: $scope.vendorBillsModel.selectedVendorBills[0].id}, function (data) {
                $scope.vendorBillsModel.vendorBillModel.vendorBill = data;

                $scope.vendorBillsModel.vendorBillModel.totalCost = _.reduce(_.pluck(data.costItems, 'subTotal'), function (subTotal, memo) {
                    return subTotal + memo;
                }, 0);

                $scope.vendorBillsModel.vendorBillView.show = true;
            }, function () {
                $scope.$root.$emit('event:application-error', 'Vendor Bill load failed!', 'Can\'t load Vendor Bill with id: '
                        + $scope.vendorBillsModel.selectedVendorBills[0].id);
            });
        };

        $scope.changeState = function (action) {
            VendorBillService.changeState({
                vendorBillId: $scope.vendorBillsModel.selectedVendorBills[0].id,
                action: action
            }, function () {
                $scope.refreshTable();
            });
        };

        $scope.refreshTable = function () {
            $scope.vendorBillsModel.selectedVendorBills.length = 0;
            VendorBillService.list({pathParam: $scope.vendorBillsModel.selectedTab}, function (data) {
                $scope.vendorBills = data;
            }, function () {
                $scope.vendorBills = [];
            });
        };

        $scope.vendorBills = [];

        if ($scope.route.current.selectedTab) {
            $scope.vendorBillsModel.selectedTab = $scope.route.current.selectedTab;
        }

        $scope.refreshTable();

        function isCommentAvailable(entity) {
            return !_.isEmpty(entity.note) || !_.isEmpty(entity.loadId) ;
        }

        var onShowTooltip = function (scope, entity) {
            scope.tooltipData = {
                date: entity.createdDate,
                user: entity.createdBy,
                note: entity.note,
                loadId: entity.loadId,
                reasonCode : $scope.VBReasonsCode[entity.reasonCode]
            };
        };

        $scope.vendorBillsGrid = {
            enableColumnResize: true,
            data: 'vendorBills',
            multiSelect: true,
            primaryKey: 'id',
            selectedItems: $scope.vendorBillsModel.selectedVendorBills,
            columnDefs: [{
                field: 'bolNumber',
                displayName: 'BOL#'
            }, {
                field: 'proNumber',
                displayName: 'PRO#'
            }, {
                field: 'poNumber',
                displayName: 'PO #'
            }, {
                field: 'shipper',
                displayName: 'Shipper'
            }, {
                field: 'originAddress',
                displayName: 'Origin',
                cellFilter: 'zip'
            }, {
                field: 'consignee',
                displayName: 'Consignee'
            }, {
                field: 'destinationAddress',
                displayName: 'Destination',
                cellFilter: 'zip'
            }, {
                field: 'customerName',
                displayName: 'Customer'
            }, {
                field: 'carrierName',
                displayName: 'Carrier'
            }, {
                field: 'actualPickupDate',
                displayName: 'Actual Pickup',
                cellFilter: 'date:wideAppDateFormat'
            }, {
                field: 'totalWeight',
                displayName: 'Weight',
                cellFilter: "appendSuffix:'Lbs'"
            }, {
                field: 'netAmount',
                displayName: 'Amount',
                cellFilter: 'plsCurrency'
            }, {
                field: 'reasonCode',
                displayName: 'Arch. Reason',
                cellFilter:"vbReasonCode"
            }, {
                field: 'note',
                displayName: 'Notes',
                showTooltip: true,
                searchable: false,
                cellTemplate: '<div class="ngCellText" data-ng-if="row.entity.note || row.entity.loadId"><i '
                + 'class="fa fa-sticky-note-o" style="background-color:yellow;">'
                + '</i></div>'
            }],
            afterSelectionChange: function (rowItem) {
                if (rowItem && rowItem.entity && $scope.vendorBillsModel.selectedTab === 'unmatched') {
                    $scope.searchSalesOrderTemplate = 'pages/content/vendor-bill/search-sales-order.html';
                    $scope.editSalesOrderTemplate = 'pages/content/sales-order/edit-sales-order.html';
                }
            },
            beforeSelectionChange: function (rowItem, event) {
                if (!event.ctrlKey && !event.shiftKey && $scope.vendorBillsGrid.multiSelect && !$(event.srcElement).is(".ngSelectionCheckbox")) {
                    angular.forEach($scope.vendorBills, function (invoice, index) {
                        $scope.vendorBillsGrid.selectRow(index, false);
                    });
                }
                return true;
            },
            tooltipOptions: {
                url: 'pages/content/vendor-bill/grid-tooltip.html',
                onShow: onShowTooltip,
                showIf: isCommentAvailable
            },
            action: $scope.viewVendorBill,
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin(),
                      NgGridPluginFactory.tooltipPlugin(true)],
            progressiveSearch: true
        };

        $scope.exportVendorBills = function (grid) {
            var fileFormat = 'Vendor_Bills{0,date,' + $scope.$root.exportFileNameDateFormat + '}.xlsx';
            var sheetName = "Vendor_Bills";

            var entities = _.map(grid.ngGrid.filteredRows, function (item) {
                return item.entity;
            });

            var exportData = ExportDataBuilder.buildExportData(grid, entities, fileFormat, sheetName, null);

            ExportService.exportData(exportData);
        };
    }
]);

angular.module('plsApp').controller('AddVendorBillController', [
    '$scope', 'VendorBillService', 'NgGridPluginFactory', 'ShipmentUtils', 'DateTimeUtils', 'ProductTotalsService',
    function ($scope, VendorBillService, NgGridPluginFactory, ShipmentUtils, DateTimeUtils, ProductTotalsService) {
        'use strict';

        var matchedCarrier = {
                scac: null,
                name: null
        };

        $scope.maxCountOfProducts = ShipmentUtils.MAX_COUNT_OF_PRODUCTS;
        $scope.weightUOM = angular.copy(ShipmentUtils.getDictionaryValues().weights);
        $scope.dimensionUOM = ShipmentUtils.getDictionaryValues().dimensions;
        $scope.commodityClasses = ShipmentUtils.getDictionaryValues().classes;
        $scope.packageTypes = ShipmentUtils.getDictionaryValues().packageTypes;
        $scope.paymentTerms = ShipmentUtils.getDictionaryValues().paymentTerms;

        $scope.addEditVendorBillModel = {
            carrierTuple: {},
            showEditVendorBillDialog: false,
            selectedLineItems: [],
            selectedCostItems: [],
            loadingIndicator: {
                showPanel: false
            },
            showEditVendorBillDialogOptions: {},
            closeHandler: null
        };
        $scope.$watch('addEditVendorBillModel.carrierTuple', function (carrierTuple) {
            if (carrierTuple && carrierTuple.name && $scope.addEditVendorBillModel.vendorBill) {
                var tupleSplitterIndex = carrierTuple.name.indexOf(':');

                if (tupleSplitterIndex !== -1) {
                    $scope.addEditVendorBillModel.vendorBill.carrier = {
                        id: carrierTuple.id,
                        name: carrierTuple.name.substr(tupleSplitterIndex + 1, carrierTuple.name.length - 1),
                        scac: carrierTuple.name.substr(0, tupleSplitterIndex)
                    };
                }
            }
        });

        $scope.emptyLineItem = {hazmat: false, weightUnit: 'LBS'};

        $scope.lineItem = angular.copy($scope.emptyLineItem);

        $scope.costItem = {};

        $scope.setVendorBillDatesValidity = function (isValid) {
            var ctrl = angular.element('[data-ng-model="addEditVendorBillModel.vendorBill.vendorBillDate"]').controller('ngModel');

            if (ctrl) {
                ctrl.$setValidity('date', isValid);
            }
        };

        $scope.$watch('addEditVendorBillModel.vendorBill.vendorBillDate', function (vendorBillDate) {
            if (vendorBillDate && $scope.addEditVendorBillModel.vendorBill.actualPickupDate) {
                $scope.checkPickupAndBillDates();
            }
        });

        $scope.checkPickupAndBillDates = function () {
            if ($scope.addEditVendorBillModel.vendorBill.actualPickupDate && $scope.addEditVendorBillModel.vendorBill.vendorBillDate) {
                var actualDate = DateTimeUtils.parseISODate($scope.addEditVendorBillModel.vendorBill.actualPickupDate);
                var vendorDate = DateTimeUtils.parseISODate($scope.addEditVendorBillModel.vendorBill.vendorBillDate);

                $scope.setVendorBillDatesValidity(DateTimeUtils.daysBetweenTwoDates(actualDate, vendorDate) < DateTimeUtils.daysInYear(actualDate));
            }
        };

        function calculateAcceccorialsData() {
            $scope.acceccorialsData = _.chain($scope.$root.accessorialTypes).map(function (item) {
                return {refType: item.id, description: item.description};
            }).filter(function (item) {
                return !_.findWhere($scope.addEditVendorBillModel.vendorBill.costItems, {refType: item.refType});
            }).sortBy('refType').value();

            if (!_.findWhere($scope.addEditVendorBillModel.vendorBill.costItems, {refType: 'CRA'})) {
                $scope.acceccorialsData.unshift({refType: 'CRA', description: 'Base rate'});
            }
        }

        var calculateAmount = function () {
            if ($scope.addEditVendorBillModel.vendorBill) {
                $scope.addEditVendorBillModel.vendorBill.amount = Number((_.reduce($scope.addEditVendorBillModel.vendorBill.costItems,
                        function (memo, num) {
                            return memo + num.subTotal;
                        }, 0)).toFixed(2));
            }
        };
        function calculateTotals() {
            $scope.totals = ProductTotalsService.calculateTotals($scope.addEditVendorBillModel.vendorBill.lineItems);
        }

        $scope.$on('event:showAddEditVendorBill', function (event, dialogDetails) {
            $scope.addEditVendorBillModel.showEditVendorBillDialog = true;
            $scope.addEditVendorBillModel.readOnly = false;
            $scope.addEditVendorBillModel.vendorBill = {lineItems: [], costItems: [], edi: false};
            $scope.addEditVendorBillModel.selectedLineItems.splice(0, $scope.addEditVendorBillModel.selectedLineItems.length);
            $scope.addEditVendorBillModel.carrierTuple = {};
            $scope.lineItem = angular.copy($scope.emptyLineItem);
            $scope.costItem = {};

            if (dialogDetails) {
                $scope.addEditVendorBillModel.readOnlyModeAllowed = dialogDetails.formDisabled;
                $scope.addEditVendorBillModel.showEditVendorBillDialogOptions.parentDialog = dialogDetails.parentDialog;
                $scope.addEditVendorBillModel.readOnly = dialogDetails.readOnly;
                $scope.addEditVendorBillModel.closeHandler = dialogDetails.closeHandler || null;

                if (dialogDetails.vendorBill && dialogDetails.vendorBill.id) {
                    $scope.addEditVendorBillModel.vendorBill = angular.copy(dialogDetails.vendorBill);
                    $scope.addEditVendorBillModel.vendorBill.id = null;
                    $scope.addEditVendorBillModel.vendorBill.edi = false;

                    if ($scope.addEditVendorBillModel.vendorBill.lineItems && $scope.addEditVendorBillModel.vendorBill.lineItems.length > 0) {
                        angular.forEach($scope.addEditVendorBillModel.vendorBill.lineItems, function (item) {
                            item.id = null;
                        });
                    }

                    if ($scope.addEditVendorBillModel.vendorBill.costItems && $scope.addEditVendorBillModel.vendorBill.costItems.length > 0) {
                        angular.forEach($scope.addEditVendorBillModel.vendorBill.costItems, function (item) {
                            item.id = null;
                        });
                    }

                    $scope.addEditVendorBillModel.carrierTuple = {
                        id: $scope.addEditVendorBillModel.vendorBill.carrier.id,
                        name: $scope.addEditVendorBillModel.vendorBill.carrier.scac + ':' + $scope.addEditVendorBillModel.vendorBill.carrier.name
                    };
                } else if (dialogDetails.matchedLoad) {
                    $scope.addEditVendorBillModel.vendorBill.matchedLoadId = dialogDetails.matchedLoad.id;
                    $scope.addEditVendorBillModel.vendorBill.vendorBillDate = $scope.$root.formatDate(new Date());

                    if (dialogDetails.matchedLoad.finishOrder) {
                        $scope.addEditVendorBillModel.vendorBill.actualPickupDate = dialogDetails.matchedLoad.finishOrder.actualPickupDate;
                        $scope.addEditVendorBillModel.vendorBill.estimatedDeliveryDate = dialogDetails.matchedLoad.finishOrder.estimatedDelivery;
                        $scope.addEditVendorBillModel.vendorBill.actualDeliveryDate = dialogDetails.matchedLoad.finishOrder.actualDeliveryDate;
                        $scope.addEditVendorBillModel.vendorBill.po = dialogDetails.matchedLoad.finishOrder.poNumber;
                        $scope.addEditVendorBillModel.vendorBill.pu = dialogDetails.matchedLoad.finishOrder.puNumber;
                        $scope.addEditVendorBillModel.vendorBill.quoteId = dialogDetails.matchedLoad.finishOrder.ref;

                        if (dialogDetails.matchedLoad.finishOrder.quoteMaterials
                                && angular.isArray(dialogDetails.matchedLoad.finishOrder.quoteMaterials)
                                && dialogDetails.matchedLoad.finishOrder.quoteMaterials.length > 0) {
                            angular.forEach(dialogDetails.matchedLoad.finishOrder.quoteMaterials, function (item) {
                                $scope.addEditVendorBillModel.vendorBill.lineItems.push({
                                    weight: item.weight,
                                    weightUnit: item.weightUnit,
                                    commodityClass: item.commodityClass,
                                    productDescription: item.productDescription,
                                    nmfc: item.nmfc,
                                    quantity: item.quantity,
                                    packageType: item.packageType,
                                    hazmat: item.hazmat
                                });
                            });
                        }
                    }

                    var originAddress = {};
                    var destinationAddress = {};

                    if (dialogDetails.matchedLoad.originDetails) {
                        originAddress.zip = dialogDetails.matchedLoad.originDetails.zip;

                        if (dialogDetails.matchedLoad.originDetails.address) {
                            originAddress.name = dialogDetails.matchedLoad.originDetails.address.addressName;
                            originAddress.address1 = dialogDetails.matchedLoad.originDetails.address.address1;
                        }
                    }

                    if (dialogDetails.matchedLoad.destinationDetails) {
                        destinationAddress.zip = dialogDetails.matchedLoad.destinationDetails.zip;

                        if (dialogDetails.matchedLoad.destinationDetails.address) {
                            destinationAddress.name = dialogDetails.matchedLoad.destinationDetails.address.addressName;
                            destinationAddress.address1 = dialogDetails.matchedLoad.destinationDetails.address.address1;
                        }
                    }

                    if (!_.isUndefined(dialogDetails.matchedLoad.paymentTerms)) {
                        $scope.addEditVendorBillModel.vendorBill.payTerm = dialogDetails.matchedLoad.paymentTerms;
                    }

                    $scope.addEditVendorBillModel.vendorBill.originAddress = originAddress;
                    $scope.addEditVendorBillModel.vendorBill.destinationAddress = destinationAddress;

                    $scope.addEditVendorBillModel.vendorBill.bol = dialogDetails.matchedLoad.bolNumber;
                    $scope.addEditVendorBillModel.vendorBill.pro = dialogDetails.matchedLoad.proNumber;

                    if (dialogDetails.matchedLoad.selectedProposition) {
                        if (dialogDetails.matchedLoad.selectedProposition.costDetailItems
                                && angular.isArray(dialogDetails.matchedLoad.selectedProposition.costDetailItems)
                                && dialogDetails.matchedLoad.selectedProposition.costDetailItems.length > 0) {

                            angular.forEach(dialogDetails.matchedLoad.selectedProposition.costDetailItems, function (item) {
                                if (item.costDetailOwner === 'C') {
                                    $scope.addEditVendorBillModel.vendorBill.costItems.push({
                                        refType: item.refType,
                                        subTotal: item.subTotal
                                    });
                                }
                            });
                        }

                        $scope.addEditVendorBillModel.vendorBill.carrier = dialogDetails.matchedLoad.selectedProposition.carrier;

                        if (dialogDetails.matchedLoad.selectedProposition.carrier) {
                            $scope.addEditVendorBillModel.carrierTuple = {
                                id: dialogDetails.matchedLoad.selectedProposition.carrier.id,
                                name: dialogDetails.matchedLoad.selectedProposition.carrier.scac + ':'
                                + dialogDetails.matchedLoad.selectedProposition.carrier.name
                            };
                        }
                    }
                }
            }

           if (dialogDetails.matchedLoad && dialogDetails.matchedLoad.selectedProposition
                   && dialogDetails.matchedLoad.selectedProposition.carrier) {
               matchedCarrier.scac = dialogDetails.matchedLoad.selectedProposition.carrier.scac;
               matchedCarrier.name = dialogDetails.matchedLoad.selectedProposition.carrier.name;
           }

            calculateAcceccorialsData();
            calculateAmount();
            calculateTotals();
        });

        $scope.closeEditVendorBill = function (doNotUpdate) {
            if (!doNotUpdate && $scope.addEditVendorBillModel.closeHandler && angular.isFunction($scope.addEditVendorBillModel.closeHandler)) {
                $scope.addEditVendorBillModel.closeHandler($scope.addEditVendorBillModel.vendorBill);
            }

            $scope.addEditVendorBillModel.showEditVendorBillDialog = false;
            $scope.$broadcast('event:cleaning-input');
        };

        function showUnmatchedCarrierWarning(vendorBill, salesOrder) {
            if (vendorBill.scac !== salesOrder.scac) {
                ShipmentUtils.showUnmatchCarrierWarning({
                    vendorBillCarrierName: vendorBill.name,
                    vendorBillScac: vendorBill.scac,
                    salesOrderCarrierName: salesOrder.name,
                    salesOrderScac: salesOrder.scac
                });
            }
        }

        $scope.saveVendorBill = function () {
            if ($scope.lineItem && ($scope.lineItem.productDescription || $scope.lineItem.quantity || $scope.lineItem.commodityClass ||
                    $scope.lineItem.weight || $scope.lineItem.weightUnit || $scope.lineItem.packageType || $scope.lineItem.nmfc)) {
                $scope.addLineItem();
            }

            if ($scope.costItem.refType && $scope.costItem.subTotal) {
                $scope.addCostItem();
                calculateAmount();
            }

            if ($scope.addEditVendorBillModel.vendorBill.costItems.length === 0) {
                $scope.$root.$emit('event:application-error', 'Vendor Bill save failed!', "Vendor Bill can't be saved without cost details.");
                return;
            }

            if ($scope.addEditVendorBillModel.vendorBill.amount <= 99999999.99) {
                VendorBillService.saveVendorBill($scope.addEditVendorBillModel.vendorBill, function (data) {
                    $scope.$root.$broadcast('event:vendorBillSaved', {
                        proNumber: $scope.addEditVendorBillModel.vendorBill.pro,
                        loadVersion: data.loadVersion,
                        vendorBillDate: $scope.addEditVendorBillModel.vendorBill.vendorBillDate,
                        costDetailItems: data.costDetailItems,
                        selectedPropositionId: data.activeCostDetailId
                    });
                    showUnmatchedCarrierWarning($scope.addEditVendorBillModel.vendorBill.carrier, matchedCarrier);
                    $scope.$root.$emit('event:operation-success', 'Vendor bill save success!', 'Vendor Bill has been saved.');
                    $scope.closeEditVendorBill(false);
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Vendor bill save failed!', 'Can\'t save vendor bill.');
                });
            } else {
                $scope.$root.$emit('event:application-error', 'Field "Amount:" max value is 99 999 999.99', 'Can\'t save vendor bill.');
            }
        };

        $scope.lineItemsGrid = {
            enableColumnResize: true,
            data: 'addEditVendorBillModel.vendorBill.lineItems',
            multiSelect: false,
            selectedItems: $scope.addEditVendorBillModel.selectedLineItems,
            columnDefs: 'lineItemsColumnModel',
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin()],
            enableSorting: false,
            action: function () {
                $scope.editLineItem();
            },
            progressiveSearch: false
        };

        $scope.costItemsGrid = {
            data: 'addEditVendorBillModel.vendorBill.costItems',
            selectedItems: $scope.addEditVendorBillModel.selectedCostItems,
            multiSelect: false,
            enableSorting: false,
            columnDefs: [{
                referenceId: 'refType',
                field: 'refType',
                displayName: 'Description',
                cellClass: 'text-left',
                width: '60%',
                cellFilter: 'refCodeAndDesc'
            }, {
                referenceId: 'productColumn',
                field: 'subTotal',
                cellClass: 'text-left',
                displayName: 'Cost',
                cellFilter: 'plsCurrency',
                width: '40%'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };


        $scope.$watch('addEditVendorBillModel.vendorBill.costItems', calculateAmount, true);

        $scope.addCostItem = function () {
            $scope.addEditVendorBillModel.vendorBill.costItems.push($scope.costItem);
            $scope.costItem = {};
            calculateAcceccorialsData();
        };
        $scope.editCostItem = function () {
            var rowNum = $scope.getSelectedCostItemRowNum();

            if (rowNum > -1) {
                $scope.costItem = $scope.addEditVendorBillModel.vendorBill.costItems[rowNum];
                $scope.removeCostItem();
            }
        };

        $scope.removeCostItem = function () {
            var rowNum = $scope.getSelectedCostItemRowNum();

            if (rowNum > -1) {
                $scope.addEditVendorBillModel.vendorBill.costItems.splice(rowNum, 1);
                $scope.addEditVendorBillModel.selectedCostItems.length = 0;
                calculateAcceccorialsData();
            }
        };

        $scope.getSelectedCostItemRowNum = function () {
            return $scope.addEditVendorBillModel.vendorBill.costItems.indexOf($scope.addEditVendorBillModel.selectedCostItems[0]);
        };

        var showCostColumn = $scope.addEditVendorBillModel.vendorBill && $scope.addEditVendorBillModel.vendorBill.edi === true;

        $scope.lineItemsColumnModel = [{
            referenceId: 'weightColumn',
            field: 'weight',
            displayName: 'Weight',
            cellClass: 'text-center',
            width: '15%'
        }, {
            referenceId: 'classColumn',
            field: 'commodityClass',
            cellClass: 'text-center',
            displayName: 'Class',
            width: '12%',
            cellFilter: 'commodityClass'
        }, {
            referenceId: 'productColumn',
            field: 'productDescription',
            cellClass: 'text-center',
            displayName: 'Description',
            width: '32%'
        }, {
            referenceId: 'nmfcColumn',
            field: 'nmfc',
            cellClass: 'text-center',
            displayName: 'NMFC',
            width: '15%'
        }, {
            referenceId: 'quantityColumn',
            field: 'quantity',
            cellClass: 'text-center',
            displayName: 'Qty',
            width: '6%'
        }, {
            referenceId: 'packageTypeColumn',
            field: 'packageType',
            displayName: 'Pack. Type',
            cellClass: 'text-center',
            width: '12%',
            cellFilter: 'packageType'
        }, {
            referenceId: 'costColumn',
            field: 'cost',
            displayName: 'Cost',
            cellClass: 'text-center',
            width: '7%',
            cellFilter: 'plsCurrency',
            visible: showCostColumn
        }];

        $scope.addLineItem = function () {
            var MAX_PRODUCTS_NUMBER = 100; //Product Grid should contain not more than 100 products.

            if ($scope.addLineItemForm.$valid) {
                if ($scope.addEditVendorBillModel.vendorBill.lineItems.length < MAX_PRODUCTS_NUMBER) {
                    $scope.addEditVendorBillModel.vendorBill.lineItems.push($scope.lineItem);
                    $scope.lineItem = angular.copy($scope.emptyLineItem);
                    calculateTotals();
                    return true;
                } else {
                    $scope.$root.$emit('event:application-error', 'Product Grid size exceeded!',
                            'Product Grid should contain not more than 100 products.');
                }
            }
            return false;
        };

        $scope.editLineItem = function () {
            var rowNum = $scope.getSelectedLineItemRowNum();

            if (rowNum > -1) {
                $scope.lineItem = $scope.addEditVendorBillModel.vendorBill.lineItems[rowNum];
                $scope.removeLimeItem();
            }
        };

        $scope.removeLimeItem = function () {
            var rowNum = $scope.getSelectedLineItemRowNum();

            if (rowNum > -1) {
                $scope.addEditVendorBillModel.vendorBill.lineItems.splice(rowNum, 1);
                calculateTotals();
            }
        };

        $scope.getSelectedLineItemRowNum = function () {
            return $scope.addEditVendorBillModel.vendorBill.lineItems.indexOf($scope.addEditVendorBillModel.selectedLineItems[0]);
        };
    }
]);

angular.module('plsApp').controller('SalesOrderForVendorBillController', [
    '$scope', '$http', '$templateCache', 'VendorBillService', 'NgGridPluginFactory', 'ShipmentUtils',
    function ($scope, $http, $templateCache, VendorBillService, NgGridPluginFactory, ShipmentUtils) {
        'use strict';

        $scope.searchSalesOrderModel = {
            vendorBillId: undefined,
            vendorBill: undefined,
            showDialog: false,
            selectedSaleOrders: [],
            search: {},
            modalOptions: {}
        };

        function preInitResourcesForSalesOrder() {
            // pre-load html templates to cache them and have faster access when needed
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
        }

        $scope.$on('event:showSearchSalesOrder', function (event, dialogDetails) {
            $scope.searchSalesOrderModel.selectedSaleOrders.length = 0;

            $scope.searchSalesOrderModel.vendorBillId = dialogDetails.vendorBillId;
            $scope.searchSalesOrderModel.showDialog = true;
            $scope.searchSalesOrderModel.closeHandler = dialogDetails.closeHandler;

            VendorBillService.get({
                customerId: $scope.$root.authData.organization.orgId,
                userId: $scope.$root.authData.personId,
                vendorBillId: dialogDetails.vendorBillId
            }, function (data) {
                $scope.searchSalesOrderModel.vendorBill = data;
                preInitResourcesForSalesOrder();
            }, function () {
                $scope.$root.$emit('event:application-error', 'Vendor bill load failed!', 'Can\'t load vendor bill with ID '
                        + dialogDetails.vendorBillId);
            });

            $scope.findSalesOrders();
        });

        $scope.salesOrders = [];

        $scope.findSalesOrders = function () {
            var params = {};
            params.vendorBillId = $scope.searchSalesOrderModel.vendorBillId;

            if ($scope.searchSalesOrderModel.search.bol) {
                params.bol = $scope.searchSalesOrderModel.search.bol;
            }

            if ($scope.searchSalesOrderModel.search.pro) {
                params.pro = $scope.searchSalesOrderModel.search.pro;
            }

            if ($scope.searchSalesOrderModel.search.carrier) {
                params.carrierId = $scope.searchSalesOrderModel.search.carrier.id;
            }

            if ($scope.searchSalesOrderModel.search.origin) {
                params.originZip = $scope.searchSalesOrderModel.search.origin.zip;
            }

            if ($scope.searchSalesOrderModel.search.destination) {
                params.destinationZip = $scope.searchSalesOrderModel.search.destination.zip;
            }

            if ($scope.searchSalesOrderModel.search.actualPickupDate) {
                params.actualPickupDate = $scope.searchSalesOrderModel.search.actualPickupDate;
            }

            VendorBillService.getMatchedSalesOrders(params, function (data) {
                $scope.salesOrders = data;
                $scope.searchSalesOrderModel.selectedSaleOrders.length = 0;

            }, function () {
                $scope.salesOrders = [];
                $scope.searchSalesOrderModel.origData = [];
                $scope.searchSalesOrderModel.selectedSaleOrders.length = 0;
            });
        };

        $scope.clearSalesOrders = function () {
            $scope.salesOrders = undefined;
            $scope.$broadcast('event:cleaning-input');
        };

        $scope.closeSearchDialog = function () {
            $scope.searchSalesOrderModel.showDialog = false;
            $scope.clearSalesOrders();
        };

        function showUnmatchedCarrierWarning(vendorBill, salesOrder) {
            if (vendorBill.scac !== salesOrder.scac) {
                ShipmentUtils.showUnmatchCarrierWarning({
                    vendorBillCarrierName: vendorBill.name,
                    vendorBillScac: vendorBill.scac,
                    salesOrderCarrierName: salesOrder.carrier,
                    salesOrderScac: salesOrder.scac
                });
            }
        }

        $scope.attachVendorBill = function () {

            if ($scope.salesOrdersGrid.selectedItems.length !== 0) {
                VendorBillService.match({
                    vendorBillId: $scope.searchSalesOrderModel.vendorBillId,
                    shipmentId: $scope.searchSalesOrderModel.selectedSaleOrders[0].shipmentId
                }, function () {
                    if ($scope.searchSalesOrderModel.closeHandler && angular.isFunction($scope.searchSalesOrderModel.closeHandler)) {
                        $scope.searchSalesOrderModel.closeHandler();
                    }

                    $scope.closeSearchDialog();
                    showUnmatchedCarrierWarning($scope.searchSalesOrderModel.vendorBill.carrier, $scope.searchSalesOrderModel.selectedSaleOrders[0]);
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Can not attach vendor bill', 'Attach of Vendor Bill to Sales Order fails.');
                });
            }
        };

        $scope.viewOrder = function () {
            $scope.$root.$broadcast('event:showEditSalesOrder', {
                shipmentId: $scope.searchSalesOrderModel.selectedSaleOrders[0].shipmentId,
                formDisabled: true,
                parentDialog: "searchSalesOrderDialog",
                closeHandler: $scope.findSalesOrders
            });
        };

        $scope.salesOrdersGrid = {
            enableColumnResize: true,
            data: 'salesOrders',
            multiSelect: false,
            primaryKey: 'id',
            selectedItems: $scope.searchSalesOrderModel.selectedSaleOrders,
            filterOptions: {
                useExternalFilter: false
            },
            columnDefs: [{
                field: 'shipmentId',
                displayName: 'Load ID',
                width: '6%'
            }, {
                field: 'bolNumber',
                displayName: 'BOL',
                width: '6%'
            }, {
                field: 'proNumber',
                displayName: 'Pro #',
                width: '6%'
            }, {
                field: 'poNumber',
                displayName: 'PO #',
                width: '6%'
            }, {
                field: 'puNumber',
                displayName: 'PU #',
                width: '6%'
            }, {
                field: 'refNumber',
                displayName: 'Shipper Ref #',
                width: '6%'
            }, {
                field: 'origin',
                cellFilter: 'zip',
                displayName: 'Origin',
                width: '11%'
            }, {
                field: 'destination',
                cellFilter: 'zip',
                displayName: 'Dest.',
                width: '11%'
            }, {
                field: 'customerName',
                displayName: 'Customer',
                width: '10%'
            }, {
                field: 'carrier',
                displayName: 'Carrier',
                width: '10%'
            }, {
                field: 'pickupDate',
                displayName: 'Actual Pickup',
                width: '9%',
                cellFilter: 'date:appDateFormat',
                searchable: false
            }, {
                field: 'self',
                displayName: 'Weight',
                width: '6%',
                cellFilter: 'materialWeight'
            }, {
                field: 'total',
                cellFilter: 'plsCurrency',
                displayName: 'Cost',
                width: '5%'
            }],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin()],
            useExternalSorting: false,
            progressiveSearch: true,
            action: $scope.viewOrder
        };
    }
]);