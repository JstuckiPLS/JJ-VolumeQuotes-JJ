angular.module('pls.controllers').controller('FinancialBoardConsolidatedController', ['$scope', '$timeout', '$http', '$cookieStore',
    'FinancialBoardService', 'FinancialBoardCBIService', 'NgGridPluginFactory', 'DateTimeUtils',
    function ($scope, $timeout, $http, $cookieStore, FinancialBoardService, FinancialBoardCBIService, NgGridPluginFactory, DateTimeUtils) {
        'use strict';

        var MAX_CBI_RANGE_IN_DAYS = -14;

        $scope.closeDatepicker = function () {
            $("#inputFilterDate").datepicker("hide");
        };

        var selectedConsolidatedInvoiceCookieId = 'consolidatedInvoice';

        $scope.cbiModel = {
            filterDate: undefined,
            minFilterDate: DateTimeUtils.addDays(new Date(), MAX_CBI_RANGE_IN_DAYS),
            maxFilterDate: new Date(),
            cbiInvoiceData: [],
            selectedCBIInvoices: [],
            cbiLoads: [],
            selectedCBILoads: [],
            reportData: undefined
        };

        $scope.loadCBILoads = function () {
            if ($scope.cbiModel.selectedCBIInvoices.length > 0) {

                var billToIds = _.pluck($scope.cbiModel.selectedCBIInvoices, "billToId");

                FinancialBoardCBIService.listLoads({billToId: billToIds}, function (data) {
                    $scope.cbiModel.cbiLoads = data;
                });
            } else {
                $scope.cbiModel.cbiLoads.length = 0;
            }

            $scope.cbiModel.selectedCBILoads.length = 0;
        };

        $scope.checkIfSelectedRecordsAreNotApproved = function () {
            if (!_.isEmpty($scope.cbiModel.selectedCBILoads)) {
                var isApproved = _.findWhere($scope.cbiModel.selectedCBILoads, {approved: true});
                return !_.isUndefined(isApproved);
            }

            return true;
        };

        function reloadPageData() {
            var selectedBillToId = [];

            if ($scope.cbiModel.selectedCBIInvoices && $scope.cbiModel.selectedCBIInvoices.length > 0) {
                selectedBillToId = _.pluck($scope.cbiModel.selectedCBIInvoices, "billToId");
            } else {
                if ($cookieStore.get(selectedConsolidatedInvoiceCookieId)) {
                    selectedBillToId = $cookieStore.get(selectedConsolidatedInvoiceCookieId);
                }
            }

            FinancialBoardCBIService.list({}, function (data) {
                $scope.cbiModel.cbiInvoiceData = data;

                var reloadedItem = [];

                if (selectedBillToId.length > 0) {
                    reloadedItem = _.filter(data, function (item) {
                        return _.indexOf(selectedBillToId, item.billToId) > -1 ? true : false;
                    });
                }

                if (selectedBillToId.length > 0) {
                    _.each(reloadedItem, function (item) {
                        $scope.cbiModel.selectedCBIInvoices.length = 0;
                        $scope.cbiModel.selectedCBIInvoices.push(item);
                    });
                } else {
                    $scope.cbiModel.selectedCBIInvoices.length = 0;
                }
            });

            $scope.cbiModel.selectedCBILoads.length = 0;

            if (selectedBillToId.length > 0) {
                FinancialBoardCBIService.listLoads({billToId: selectedBillToId}, function (data) {
                    $scope.cbiModel.cbiLoads = data;
                });
            } else {
                $scope.cbiModel.cbiLoads.length = 0;
            }
        }

        reloadPageData();

        $scope.approveAll = function (cbiEntry) {
            FinancialBoardCBIService.approveAll({
                billToId: cbiEntry.billToId,
                approved: cbiEntry.approved === 'ALL'
            }, reloadPageData, function () {
                $scope.$root.$emit('event:application-error', 'Approve invoices error!', 'Invoices approve status wasn\'t changed.');
                reloadPageData();
            });
        };

        function approve(invoices) {
            var approved = !invoices[0].approved;
            _.each(invoices, function (invoice) {
                FinancialBoardService.approve({
                    loadId: invoice.loadId,
                    adjustmentId: invoice.adjustmentId,
                    approve: invoice.isApproved ? !invoice.isApproved : approved
                }, function () {
                    invoice.approved = approved;
                    reloadPageData();
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Approve invoice error!', 'Invoice approve status wasn\'t changed.');
                    $(event.target).attr('checked', invoice.approved);
                });
            });
        }

        $scope.approveLoad = function (invoice, event) {
            var invoices = [invoice];
            if (invoice.rebill) {
                // try to find second part of rebill adj
                var rebillAdjustments = _.filter($scope.cbiModel.cbiLoads, function (item) {
                    return item.loadId === invoice.loadId;
                });

                if (rebillAdjustments.length === 1) {
                    FinancialBoardService.getRebillAdjustments({rebillAdjId : invoice.adjustmentId}, function (data) {
                        data[0].approved = invoice.approve;
                        invoices.push(data[0]);
                        approve(invoices);
                    });
                } else {
                    approve(rebillAdjustments);
                }
            } else {
                approve(invoices);
            }
        };

        $scope.closeHandler = function () {
            reloadPageData();
            $scope.cbiModel.selectedCBIInvoices.length = 0;
        };

        $scope.editLoad = function (selectedTab) {
            if ($scope.cbiModel.selectedCBILoads.length === 1) {
                $scope.$broadcast('event:showEditSalesOrder', {
                    shipmentId: $scope.cbiModel.selectedCBILoads[0].loadId,
                    closeHandler: $scope.closeHandler, isUnavailableCancel: false, isUnavailableCopy: false, selectedTab: selectedTab
                });
            }
        };

        $scope.exportLoads = function () {
            if ($scope.cbiModel.selectedCBIInvoices.length > 0) {
                $scope.$emit('event:exportInvoices', {
                    sheetName: 'Consolidated_Invoice',
                    fileName: $scope.cbiModel.selectedCBIInvoices[0].customerName + '_',
                    grid: $scope.cbiModel.consolidatedLoadsGrid,
                    selectedRows: false
                });
            }
        };

        $scope.sendToInvoiceAudit = function () {
            $scope.isInvioceAudit = true;
            $scope.sendToAudit();
        };

        $scope.sendToPriceAudit = function () {
            $scope.isInvioceAudit = false;
            $scope.sendToAudit();
        };

        function confirmSendToAudit(auditInvoices) {
            $scope.$broadcast('event:sendToAudit', {
                auditRecords: $scope.fillSelectedShipmentsForBusinessObjects(auditInvoices),
                isInvioceAudit: $scope.isInvioceAudit
            });
        }

        $scope.sendToAudit = function () {
            var auditInvoices = angular.copy($scope.cbiModel.selectedCBILoads);
            $scope.selectRebillAdjustment(auditInvoices, $scope.cbiModel.cbiLoads);
            var rebillAdjustments = $scope.getSelectedUniqueRebillAdjustments($scope.cbiModel.selectedCBILoads);
            if (rebillAdjustments.length) {
                var rebillAdjIds = _.pluck(rebillAdjustments, 'adjustmentId');
                FinancialBoardService.getRebillAdjustments({rebillAdjId : rebillAdjIds}, function (data) {
                    _.each(data, function (invoice) {
                        auditInvoices.push(invoice);
                    });
                    confirmSendToAudit(auditInvoices);
                });
            } else {
                confirmSendToAudit(auditInvoices);
            }
        };

        function getInvoicesForProcessing() {
            return _.filter($scope.cbiModel.cbiLoads, function (row) {
                return row.approved === true && (row.adjustmentId || DateTimeUtils.compareDates(row.deliveredDate, $scope.cbiModel.filterDate) < 1);
            });
        }

        $scope.$watch('cbiModel.selectedCBIInvoices', function () {
            $cookieStore.put(selectedConsolidatedInvoiceCookieId, _.pluck($scope.cbiModel.selectedCBIInvoices, "billToId"));
            $scope.loadCBILoads();
        }, true);

        $scope.canProcess = function () {
            return  $scope.checkIsSameSelectedCustomer()
                    && $scope.cbiModel.selectedCBIInvoices.length
                    && $scope.cbiModel.filterDate
                    && getInvoicesForProcessing().length;
        };

        $scope.cbiModel.consolidatedInvoicesGrid = {
            enableColumnResize: true,
            multiSelect: true,
            data: 'cbiModel.cbiInvoiceData',
            primaryKey: 'billToId',
            selectedItems: $scope.cbiModel.selectedCBIInvoices,
            columnDefs: [{
                field: 'networkName',
                displayName: 'Business Unit',
                headerClass: 'text-center',
                cellClass: 'text-center',
                searchable: true,
                width: '11%'
            }, {
                field: 'customerName',
                displayName: 'Customer',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '15%'
            }, {
                field: 'self',
                displayName: 'Bill To',
                headerClass: 'text-center',
                cellClass: 'text-center',
                searchable: true,
                cellFilter: 'billToAndCurrency',
                width: '15%'
            }, {
                field: 'invoiceDateInfo',
                displayName: 'Invoice Day',
                headerClass: 'text-center',
                cellClass: 'text-center',
                searchable: true,
                width: '14%'
            }, {
                field: 'includeCarrierRate',
                displayName: 'Include Carrier Rate',
                cellTemplate: 'pages/cellTemplate/checked-cell.html',
                headerClass: 'text-center',
                searchable: false,
                width: '8%'
            }, {
                field: 'sendBy',
                displayName: 'Send By',
                headerClass: 'text-center',
                cellClass: 'text-center',
                searchable: false,
                width: '8%'
            }, {
                field: 'totalRevenue',
                displayName: 'Total Revenue',
                cellFilter: 'plsCurrency',
                searchable: false,
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '8%'
            }, {
                field: 'totalCost',
                displayName: 'Total Cost',
                cellFilter: 'plsCurrency',
                searchable: false,
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '8%'
            }, {
                field: 'totalMargin',
                displayName: 'Total Margin',
                cellFilter: 'plsCurrency',
                searchable: false,
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '8%'
            }, {
                field: 'approved',
                displayName: 'Approved',
                cellTemplate: '<div class="ngSelectionCell text-center">'
                + '<input class="ngSelectionCheckbox" data-ng-attr-id="appChk{{row.entity.billToId}}" data-pls-tri-state-checkbox type="checkbox" '
                + 'data-ng-model="row.entity.approved" data-ng-true-value="ALL" data-ng-false-value="NONE" '
                + 'data-ng-change="approveAll(row.entity)"/>'
                + '</div>',
                headerClass: 'text-center',
                searchable: false,
                width: '5%'
            }],
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            sortInfo: {fields: ['customerName'], directions: ['asc']},
            beforeSelectionChange: function (rowItem, event) {
                if (!event.ctrlKey && !event.shiftKey &&
                        $scope.cbiModel.consolidatedInvoicesGrid.multiSelect && !$(event.srcElement).is(".ngSelectionCheckbox")) {
                    angular.forEach($scope.cbiModel.cbiInvoiceData, function (loads, index) {
                        $scope.cbiModel.consolidatedInvoicesGrid.selectRow(index, false);
                    });
                }

                return true;
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(),
                      NgGridPluginFactory.hideColumnPlugin("ConsolidatedInvoicesGrid")],
            useExternalSorting: false,
            progressiveSearch: true
        };

        if ($scope.$root.isFieldRequired('CUSTOMER_PROFILE_VIEW')) {
            $scope.cbiModel.consolidatedInvoicesGrid.columnDefs[3].cellTemplate = '<div class="ngCellText text-center" ' +
                    'data-ng-class="col.colIndex()">' +
                    '<a href="#/customer/{{row.entity.customerId}}/billTo/{{row.entity.billToId}}">{{row.getProperty(col.field)}}</a>' +
                    '</div>';
        }

        function isCommentAvailable(entity) {
            return !_.isEmpty(entity.noteComment);
        }

        var onShowTooltip = function (scope, entity) {
            scope.tooltipData = entity.comment;

            scope.tooltipData = {
                noteCreatedDate: entity.noteCreatedDate,
                noteModifiedBy: entity.noteModifiedBy,
                noteComment: entity.noteComment
            };
        };

        $scope.cbiModel.consolidatedLoadsGrid = {
            enableColumnResize: true,
            data: 'cbiModel.cbiLoads',
            multiSelect: true,
            selectedItems: $scope.cbiModel.selectedCBILoads,
            columnDefs: [{
                field: 'numberOfNotes',
                displayName: 'Notes',
                showTooltip: true,
                width: '3%',
                searchable: false,
                cellTemplate: '<div class="ngCellText" data-ng-if="row.entity.noteComment"><i '
                + 'class="fa fa-sticky-note-o" style="background-color:yellow;">'
                + '</i>{{" " + row.entity.numberOfNotes}}</div>',
                exportTemplate: 'exportEntity.noteComment ? ' +
                ' "Number of notes: " + exportEntity.numberOfNotes + " Last note: " + exportEntity.noteComment + "" : " "'
            }, {
                field: 'adjustmentId',
                displayName: 'Adj',
                headerCellTemplate: 'pages/cellTemplate/adjustment-header-cell.html',
                cellTemplate: 'pages/cellTemplate/adjustment-cell.html',
                width: '2%',
                headerClass: 'text-center',
                cellClass: 'text-center, cellToolTip',
                sortable: false,
                searchable: false,
                exportTemplate: 'exportEntity.adjustmentId ? (row.entity.doNotInvoice ? "Not Invoice" : "Adjustment") : ""'
            }, {
                field: 'self',
                displayName: 'Bill To',
                headerClass: 'text-center',
                cellClass: 'text-center',
                cellFilter: 'billToAndCurrency',
                width: '8%'
            }, {
                field: 'carrierName',
                displayName: 'Carrier',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '8%'
            }, {
                field: 'deliveredDate',
                displayName: 'Delivered',
                cellFilter: 'date:wideAppDateFormat',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'loadId',
                displayName: 'Load ID',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '5%'
            }, {
                field: 'bolNumber',
                displayName: 'BOL',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '5%'
            }, {
                field: 'proNumber',
                displayName: 'PRO #',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'poNumber',
                displayName: 'PO #',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'glNumber',
                displayName: 'GL #',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'soNumber',
                displayName: 'SO #',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'origin',
                displayName: 'Origin',
                headerClass: 'text-center',
                cellClass: 'text-center',
                cellFilter: 'zip',
                width: '5%'
            }, {
                field: 'destination',
                displayName: 'Destination',
                headerClass: 'text-center',
                cellClass: 'text-center',
                cellFilter: 'zip',
                width: '5%'
            }, {
                field: 'shipmentDirection',
                displayName: 'Inb/Outb',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'paymentTerms',
                displayName: 'Pay Terms',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'acc',
                displayName: 'Acc. Charges',
                cellFilter: 'plsCurrency',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'fs',
                displayName: 'FS',
                cellFilter: 'plsCurrency',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'revenue',
                displayName: 'Revenue',
                cellFilter: 'plsCurrency',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'cost',
                displayName: 'Cost',
                cellFilter: 'plsCurrency',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'marginAmt',
                displayName: 'Margin',
                cellFilter: 'plsCurrency',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'margin',
                displayName: 'Margin %',
                cellFilter: 'percentage',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'paidAmount',
                displayName: 'Paid',
                cellFilter: 'plsCurrency',
                headerClass: 'text-center',
                cellClass: 'text-center',
                width: '4%'
            }, {
                field: 'approved',
                displayName: 'Approved',
                cellTemplate: '<div class="ngSelectionCell text-center">'
                + '<input class="ngSelectionCheckbox" type="checkbox" '
                + 'data-ng-checked="row.entity.approved" data-ng-click="approveLoad(row.entity, $event)" />'
                + '</div>',
                searchable: false,
                headerClass: 'text-center',
                width: '4%'
            }],
            filterOptions: {filterText: "", useExternalFilter: false},
            beforeSelectionChange: function (rowItem, event) {
                if (!event.ctrlKey && !event.shiftKey && $scope.cbiModel.consolidatedLoadsGrid.multiSelect
                        && !$(event.srcElement).is(".ngSelectionCheckbox")) {
                    angular.forEach($scope.cbiModel.cbiLoads, function (loads, index) {
                        $scope.cbiModel.consolidatedLoadsGrid.selectRow(index, false);
                    });
                }

                return true;
            },
            tooltipOptions: {
                url: 'pages/content/financialBoard/invoice-audit-grid-tooltip.html',
                onShow: onShowTooltip,
                showIf: isCommentAvailable
            },
            sortInfo: {fields: ['carrierName'], directions: ['asc']},
            plugins: [
                NgGridPluginFactory.plsGrid(),
                NgGridPluginFactory.progressiveSearchPlugin(),
                NgGridPluginFactory.actionPlugin(),
                NgGridPluginFactory.tooltipPlugin(true),
                NgGridPluginFactory.hideColumnPlugin("ConsolidatedLoadsGrid")
            ],
            action: function () {
                $scope.editLoad();
            },
            useExternalSorting: false,
            progressiveSearch: true
        };

        $scope.recordsUpdate = function (value) {
            $scope.recordsCustomersUpdate();
            var gridApproved = 0;

            _.each(value, function (item) {
                if (item.entity.approved) {
                    gridApproved += 1;
                }
            });

            return gridApproved;
        };

        $scope.$on('event:showReasonMessage', function (event, data) {
            if (data.shipment.adjustmentId === undefined) {
                FinancialBoardService.getReason({loadId: data.shipment.id}, function (response) {
                    if (response.data !== "") {
                        $scope.$root.$broadcast('event:showConfirmation', {
                            caption: 'Warning messages:',
                            closeButtonHide: true,
                            message: "Warning: " + response.data,
                            parentDlgId: data.parentDlgId
                        });
                    }
                });
            }
        });

        $scope.checkIsSameSelectedCustomer = function() {
            return _.every($scope.cbiModel.selectedCBIInvoices, function (item) {
                return $scope.cbiModel.selectedCBIInvoices[0].customerId === item.customerId;
            });
        };

        $scope.recordsCustomersUpdate = function () {
            $scope.gridCustomers = $scope.cbiModel.consolidatedInvoicesGrid.ngGrid.filteredRows.length;
            $scope.gridCustomersApproved = 0;

            _.each($scope.cbiModel.consolidatedInvoicesGrid.ngGrid.filteredRows, function (item) {
                if (item.entity.approved === "ALL" || item.entity.approved === "SOME") {
                    $scope.gridCustomersApproved += 1;
                }
            });
        };

        $scope.$watch('cbiModel.consolidatedInvoicesGrid.ngGrid.filteredRows.length', $scope.recordsCustomersUpdate);

        $scope.$watch('cbiModel.consolidatedLoadsGrid.ngGrid.filteredRows.length', function () {
            $scope.gridRecords = $scope.cbiModel.consolidatedLoadsGrid.ngGrid.filteredRows.length;
        });

        $scope.$on('event:updateDataAfterSendToAudit', function () {
            reloadPageData();
        });

        $scope.$on('event:updateDataAfterProcessing', function () {
            reloadPageData();
            $scope.cbiModel.selectedCBIInvoices.length = 0;
        });

        function processCbiData() {
            if (!_.isEmpty($scope.cbiModel.selectedCBIInvoices)) {
                //FIXME: fix this logic for all bill to
                if ($scope.cbiModel.cbiLoads[0].missingPaymentsTerms) {
                    $scope.$root.$emit('event:application-error', 'Process invoices error!',
                            'Missing Payments Term on Bill To. Needs to be selected on Customer Bill To Invoice Preferences');
                    return;
                }

                var processData = {
                    invoiceDate: $scope.cbiModel.filterDate,
                    customer: $scope.cbiModel.selectedCBIInvoices[0].customerName,
                    allInvoices: $scope.cbiModel.filteredSelectedInvoices,
                    selectedBillToList: $scope.cbiModel.selectedBillToList
                };
                $scope.$broadcast('openProcessInvoiceDialog', processData);
            }
        }

        function processWithRebillAdjustments() {
            var rebillAdjustments = $scope.getSelectedUniqueRebillAdjustments($scope.cbiModel.filteredSelectedInvoices);

            FinancialBoardService.getRebillAdjustments({rebillAdjId: _.pluck(rebillAdjustments, 'adjustmentId')}, function(data) {
                $scope.cbiModel.filteredSelectedInvoices = $scope.cbiModel.filteredSelectedInvoices.concat(data);
                _.each(data, function (adj) {
                    if (!_.findWhere($scope.cbiModel.selectedBillToList, {id: adj.billToId})) {
                        $scope.cbiModel.selectedBillToList.push({id: adj.billToId, name: adj.billToName, processType: adj.billToProcessType});
                    }
                });
                processCbiData();
            });
        }

        $scope.processInvoices = function() {
            $scope.cbiModel.selectedBillToList = [];
            _.each($scope.cbiModel.selectedCBIInvoices, function (invoice) {
                if (invoice.approved !== 'NONE') {
                    $scope.cbiModel.selectedBillToList.push({id: invoice.billToId, name: invoice.billToName, processType: 'CBI'});
                }
            });

            $scope.cbiModel.filteredSelectedInvoices = getInvoicesForProcessing();
            $scope.selectRebillAdjustment($scope.cbiModel.filteredSelectedInvoices, $scope.cbiModel.cbiLoads);
            var rebillAdjustments = $scope.getSelectedUniqueRebillAdjustments($scope.cbiModel.filteredSelectedInvoices);
            if (rebillAdjustments.length) {
                $scope.$root.$broadcast('event:showConfirmation', {
                    caption: 'Process Rebill Adjustments',
                    message: 'Selected list of loads contains Rebill adjustment(s).<br/>' +
                        'Other part of this Rebill adjustment belongs to<br/>' +
                        'another Bill To and will be processed together.',
                    okFunction: processWithRebillAdjustments
                });
            } else {
                processCbiData();
            }
        };
    }
]);