angular.module('pls.controllers').controller('FinancialBoardTransactionalController', [
    '$scope', '$http', 'FinancialBoardService', 'NgGridPluginFactory', 'StringUtils',
    function ($scope, $http, FinancialBoardService, NgGridPluginFactory, StringUtils) {
        'use strict';

        $scope.invoices = [];
        $scope.selectedInvoices = [];
        $scope.gridRecords = 0;
        $scope.gridApproved = 0;
        $scope.gridSelected = 0;

        $scope.pageModel = {
            processAllowed: false,
            approveAllowed: false,
            exportAllowed: false,
            sendToAuditAllowed: false
        };

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

        function initPermissions() {
            $scope.pageModel.processAllowed = $scope.$root.isFieldRequired('CAN_PROCESS_INVOICE');
            $scope.pageModel.approveAllowed = $scope.$root.isFieldRequired('CAN_APPROVE_INVOICE');
            $scope.pageModel.exportAllowed = $scope.$root.isFieldRequired('CAN_EXPORT_INVOICE');
            $scope.pageModel.sendToAuditAllowed = $scope.$root.isFieldRequired('FIN_BOARD_SEND_TO_AUDIT');
        }

        $scope.initTransactionalTab = function () {
            initPermissions();
            $scope.loadTransactionalInvoices();
        };

        $scope.$watch('pageModel.approveAllowed', function (newValue) {
            if ($scope.invoiceGrid.$gridScope && $scope.invoiceGrid.$gridScope.columns && $scope.invoiceGrid.$gridScope.columns.length) {
                var approvedColumnIndex = $scope.invoiceGrid.$gridScope.columns.length - 1; //Last column

                if (newValue !== $scope.invoiceGrid.$gridScope.columns[approvedColumnIndex].visible) {
                    $scope.invoiceGrid.$gridScope.columns[approvedColumnIndex].toggleVisible();
                }
            }
        });

        var prepareTransactionalInvoices = function (invoices) {
            angular.forEach(invoices, function (invoice) {
                var adjustmentId = invoice.adjustmentId;
                var doNoInvoice = invoice.doNotInvoice;
                var compoundSortField;

                if (!_.isUndefined(adjustmentId) && doNoInvoice === false) {
                    compoundSortField = 0;
                } else if (!_.isUndefined(adjustmentId) && doNoInvoice === true) {
                    compoundSortField = 1;
                } else {
                    compoundSortField = 2;
                }

                invoice.compoundSortField = compoundSortField;
            });

            return invoices;
        };

        $scope.loadTransactionalInvoices = function () {
            FinancialBoardService.transactional({}, function (data) {
                $scope.invoices = prepareTransactionalInvoices(data);
            });
        };

        function approveInvoice(invoices) {
            $scope.setProgressText('Approving invoice. Please wait...');
            var approved = !invoices[0].approved;
            _.each(invoices, function (item) {
                FinancialBoardService.approve({
                    loadId: item.loadId,
                    adjustmentId: item.adjustmentId,
                    approve: approved
                }, function () {
                    item.approved = approved;
                    $scope.gridApproved = 0;
                    $scope.recordsUpdate();
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Approve invoice error!', 'Invoice wasn\'t approved.');
                    $(event.target).attr('checked', item.approved);
                });
            });
        }

        $scope.approve = function (invoice) {
            var invoices = [invoice];
            if (invoice.rebill) {
                // try to find second part of rebill adj on transactional page
                var rebillAdjustments = _.filter($scope.invoices, function (item) {
                    return item.loadId === invoice.loadId;
                });
                // if it doesn't exist then try to find it in database and then call approve api
                if (rebillAdjustments.length === 1) {
                    FinancialBoardService.getRebillAdjustments({rebillAdjId : invoice.adjustmentId}, function (data) {
                        data[0].approved = invoice.approved;
                        rebillAdjustments.push(data[0]);
                        approveInvoice(rebillAdjustments);
                    });
                } else {
                    approveInvoice(rebillAdjustments);
                }
            } else {
                approveInvoice(invoices);
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

        function sendToAudit(auditInvoices) {
            $scope.$broadcast('event:sendToAudit', {
                auditRecords: $scope.fillSelectedShipmentsForBusinessObjects(auditInvoices),
                isInvioceAudit: $scope.isInvioceAudit
            });
        }

        function highlightSelectedInvoices() {
            // highlight rebill adjustments, because $scope.selectedInvoices may be changed
            _.each($scope.selectedInvoices, function (item) {
                _.each($scope.invoiceGrid.$gridScope.renderedRows, function (row) {
                    if (row.entity.loadId === item.loadId && item.rebill) {
                        $scope.invoiceGrid.selectRow(row.rowIndex, true);
                    }
                });
            });
        }

        $scope.sendToAudit = function () {
            var auditInvoices = [$scope.selectedInvoices[0]];
            if ($scope.isSelectedRebillAdjustment($scope.selectedInvoices)) {
                FinancialBoardService.getRebillAdjustments({rebillAdjId : $scope.selectedInvoices[0].adjustmentId}, function (data) {
                    _.each(data, function (invoice) {
                        auditInvoices.push(invoice);
                    });
                    sendToAudit(auditInvoices);
                });
            } else {
                $scope.selectRebillAdjustment($scope.selectedInvoices, $scope.invoices);
                sendToAudit(auditInvoices);
            }
        };

        function checkIsScheduleApproved(selectedInvoices) {
            return !_.every(selectedInvoices, function (item) {
                return item.approved !== true;
            });
        }

        function checkIsSameBillToId(selectedInvoices) {
            return selectedInvoices.length && _.every(selectedInvoices, function (item) {
                return selectedInvoices[0].billToId === item.billToId;
            });
        }

        function isSameCustomerForBillTo(selectedInvoices) {
            return selectedInvoices.length > 0 && _.every(selectedInvoices, function (item) {
                return selectedInvoices[0].customerName === item.customerName;
            });
        }

        function processInvoices() {
            if (!isSameCustomerForBillTo($scope.selectedInvoices)) {
                $scope.$root.$emit('event:application-error', 'Process invoices error!',
                        'You can process several invoices only for the same Customer');
                return;
            }

            if (checkIsScheduleApproved($scope.selectedInvoices)) {
                $scope.$root.$emit('event:application-error', 'Process invoices error!',
                        'You must uncheck the "Process on Schedule" before clicking the Override Scheduled Process button.');
                return;
            }

            //FIXME: fix this logic for all bill to
            if ($scope.selectedInvoices[0].missingPaymentsTerms) {
                $scope.$root.$emit('event:application-error', 'Process invoices error!',
                        'Missing Payments Term on Bill To. Needs to be selected on Customer Bill To Invoice Preferences');
                return;
            }

            var processData = {
                customer: $scope.selectedInvoices[0].customerName,
                allInvoices: $scope.selectedInvoicesForProcessing,
                selectedBillToList: $scope.selectedBillToList
            };
            $scope.$broadcast('openProcessInvoiceDialog', processData);
        }

        function processWithRebillAdjustments() {
            var rebillAdjustments = $scope.getSelectedUniqueRebillAdjustments($scope.selectedInvoices);

            FinancialBoardService.getRebillAdjustments({rebillAdjId : _.pluck(rebillAdjustments, 'adjustmentId')}, function (data) {
                $scope.selectedInvoicesForProcessing = $scope.selectedInvoicesForProcessing.concat(data);
                _.each(data, function (adj) {
                    if (!_.findWhere($scope.selectedBillToList, {id: adj.billToId})) {
                        $scope.selectedBillToList.push({id: adj.billToId, name: adj.billToName, processType: adj.billToProcessType});
                    }
                });
                processInvoices();
            });
        }

        $scope.processInvoices = function () {
            $scope.selectRebillAdjustment($scope.selectedInvoices, $scope.invoices);
            $scope.selectedInvoicesForProcessing = $scope.selectedInvoices;

            $scope.selectedBillToList = [];
            _.each($scope.selectedInvoicesForProcessing, function (invoice) {
                if (!_.findWhere($scope.selectedBillToList, {id: invoice.billToId})) {
                    $scope.selectedBillToList.push({id: invoice.billToId, name: invoice.billToName, processType: 'TRANSACTIONAL'});
                }
            });

            var rebillAdjustments = $scope.getSelectedUniqueRebillAdjustments($scope.selectedInvoicesForProcessing);
            if (rebillAdjustments.length) {
                $scope.$root.$broadcast('event:showConfirmation', {
                    caption: 'Process Rebill Adjustments',
                    message: 'Selected list of loads contains Rebill adjustment(s).<br/>' +
                        'Other part of this Rebill adjustment belongs to<br/>' +
                        'another Bill To and will be processed together.',
                    okFunction: processWithRebillAdjustments
                });
            } else {
                processInvoices();
            }
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

        $scope.editShipmentCloseHandler = function () {
            $scope.invoices.length = 0;
            $scope.selectedInvoices.length = 0;
            $scope.loadTransactionalInvoices();
        };

        $scope.editShipment = function (selectedTab) {
            if ($scope.selectedInvoices.length) {
                $scope.$broadcast('event:showEditSalesOrder', {
                    shipmentId: $scope.selectedInvoices[0].loadId,
                    closeHandler: $scope.editShipmentCloseHandler,
                    isUnavailableCancel: false,
                    isUnavailableCopy: false,
                    selectedTab: selectedTab
                });
            }
        };

        $scope.exportInvoices = function () {
            $scope.$emit('event:exportInvoices', {
                sheetName: 'Transactional Invoices',
                grid: $scope.invoiceGrid,
                selectedRows: $scope.selectedInvoices,
                fileName: 'Transactional_Invoices_'
            });
        };

        $scope.invoiceGrid = {
            enableColumnResize: true,
            multiSelect: true,
            data: 'invoices',
            selectedItems: $scope.selectedInvoices,
            primaryKey: 'loadId',
            headerRowHeight: 36,
            progressiveSearch: true,
            columnDefs: [{
                field: 'numberOfNotes',
                displayName: 'Notes',
                showTooltip: true,
                width: '3%',
                searchable: false,
                cellTemplate: '<div class="ngCellText" data-ng-if="row.entity.noteComment"><i '
                + 'class="fa fa-sticky-note-o" style="background-color:yellow;">'
                + '</i>{{" " + row.entity.numberOfNotes}}</div>',
                exportTemplate: 'exportEntity.noteComment ?' +
                ' "Number of notes: " + exportEntity.numberOfNotes + " Last note: " + exportEntity.noteComment + "" : " "'
            }, {
                field: 'compoundSortField',
                headerCellTemplate: 'pages/cellTemplate/adjustment-header-cell.html',
                headerClass: 'cellToolTip',
                cellTemplate: 'pages/cellTemplate/adjustment-cell.html',
                cellClass: 'cellToolTip',
                width: '3%',
                searchable: false,
                sortable: true,
                exportTemplate: 'exportEntity.adjustmentId ? (row.entity.doNotInvoice ? "Not Invoice" : "Adjustment") : ""',
                exportDisplayName: 'Adjustment'
            }, {
                field: 'networkName',
                displayName: 'Business Unit',
                width: '6%'
            }, {
                field: 'customerName',
                displayName: 'Customer',
                width: '10%'
            }, {
                field: 'billToName + ", " + row.entity.currency',
                displayName: 'Bill To',
                width: '11%',
                searchable: false
            }, {
                field: 'deliveredDate',
                displayName: 'Delivered Date',
                cellFilter: 'date:wideAppDateFormat',
                width: '6%'
            }, {
                field: 'loadId',
                displayName: 'Load ID',
                width: '5%'
            }, {
                field: 'bolNumber',
                displayName: 'BOL',
                width: '8%'
            }, {
                field: 'proNumber',
                displayName: 'Pro #',
                width: '8%'
            }, {
                field: 'carrierName',
                displayName: 'Carrier',
                width: '13%'
            }, {
                field: 'revenue',
                displayName: 'Revenue',
                width: '7%',
                cellFilter: 'plsCurrency',
                searchable: false
            }, {
                field: 'cost',
                displayName: 'Cost',
                width: '7%',
                cellFilter: 'plsCurrency',
                searchable: false
            }, {
                field: 'marginAmt',
                displayName: 'Margin',
                cellFilter: 'plsCurrency',
                width: '5%',
                searchable: false
            }, {
                field: 'approved',
                displayName: 'Process on Schedule',
                headerClass: 'text-center',
                cellTemplate: '<div class="ngSelectionCell text-center">'
                + '<input class="ngSelectionCheckbox" type="checkbox" '
                + 'data-ng-checked="row.entity.approved" data-ng-click="approve(row.entity, $event)"/>'
                + '</div>',
                width: '8%',
                searchable: false
            }],
            sortInfo: {
                fields: ['customerName'],
                directions: ['asc']
            },
            filterOptions: {
                useExternalFilter: false
            },
            tooltipOptions: {
                url: 'pages/content/financialBoard/invoice-audit-grid-tooltip.html',
                onShow: onShowTooltip,
                showIf: isCommentAvailable
            },
            beforeSelectionChange: function (rowItem, event) {
                if (!event.ctrlKey && !event.shiftKey && $scope.invoiceGrid.multiSelect && !$(event.srcElement).is(".ngSelectionCheckbox")) {
                    angular.forEach($scope.invoices, function (invoice, index) {
                        $scope.invoiceGrid.selectRow(index, false);
                    });
                }
                return true;
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), 
                      NgGridPluginFactory.tooltipPlugin(true), NgGridPluginFactory.hideColumnPlugin()]
        };

        $scope.recordsUpdate = function () {
            $scope.gridRecords = $scope.invoiceGrid.ngGrid.filteredRows.length;

            $scope.gridSelected = 0;
            $scope.gridApproved = 0;

            _.each($scope.invoiceGrid.ngGrid.filteredRows, function (item) {
                if (item.selected) {
                    $scope.gridSelected += 1;
                }

                if (item.entity.approved) {
                    $scope.gridApproved += 1;
                }
            });
        };

        $scope.$watch('invoiceGrid.ngGrid.filteredRows.length', $scope.recordsUpdate);
        $scope.$watch('invoiceGrid.selectedItems.length', $scope.recordsUpdate);

        $scope.$on('event:updateDataAfterSendToAudit', function () {
            $scope.loadTransactionalInvoices();
            $scope.selectedInvoices.length = 0;
        });

        $scope.$on('event:updateDataAfterProcessing', function () {
            $scope.invoices.length = 0;
            $scope.selectedInvoices.length = 0;
            $scope.loadTransactionalInvoices();
        });
    }
]);