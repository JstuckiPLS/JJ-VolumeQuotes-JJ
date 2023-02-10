angular.module('pls.controllers').controller('FinancialBoardHistoryController', ['$scope', '$window', '$filter', 'urlConfig', 'FinancialBoardService',
    'NgGridPluginFactory', 'DateTimeUtils', 'RateQuoteCustomerService', 'BillToEmailService',
    function ($scope, $window, $filter, urlConfig, FinancialBoardService,
              NgGridPluginFactory, DateTimeUtils, RateQuoteCustomerService, BillToEmailService) {
        'use strict';

        var MAX_HISTORY_RANGE_IN_MONTHS = 3;

        $scope.invoices = [];
        $scope.selectedInvoices = [];
        $scope.cbiItems = [];
        $scope.selectedCbiItems = [];

        $scope.reprocessDialog = {
            show: false,
            financial: false,
            customerEmail: false,
            sendEmailAllowed: false,
            emails: undefined,
            customerEDI: false,
            close: function () {
                $scope.reprocessDialog.show = false;
                $scope.reprocessDialog.financial = false;
                $scope.reprocessDialog.customerEmail = false;
                $scope.reprocessDialog.sendEmailAllowed = false;
                $scope.reprocessDialog.emails = undefined;
                $scope.reprocessDialog.customerEDI = false;
            }
        };

        $scope.cbiDetailsPopup = {
            show: false,
            close: function () {
                $scope.cbiDetailsPopup.show = false;
                $scope.selectedCbiItems.length = 0;
                $scope.cbiItems.length = 0;
            }
        };

        $scope.reprocessFinancialPopup = {
            show: false,
            selectAll: 'NONE',
            toggleSelectAll: function () {
                _.each($scope.cbiItems, function (item) {
                    item.isChecked = $scope.reprocessFinancialPopup.selectAll === 'ALL';
                });
            },
            toggleSelectRow: function () {
                var maxCount = $scope.cbiItems.length;
                var checkedItemsCount = _.where($scope.cbiItems, {isChecked: true}).length;
                $scope.reprocessFinancialPopup.selectAll = !checkedItemsCount ? 'NONE'
                        : (maxCount === checkedItemsCount ? 'ALL' : 'SOME');
            },
            close: function () {
                $scope.reprocessFinancialPopup.show = false;
                $scope.cbiItems.length = 0;
                $scope.reprocessFinancialPopup.selectAll = 'NONE';
            }
        };
             
        $scope.isReprocessDisabled = function() {
            var checkedItems = _.where($scope.cbiItems, {isChecked: true});
            return _.isEmpty(checkedItems) || _.uniq(checkedItems, false, function(item){  
                return item.billToId;
             }).length > 1;
        };

        $scope.invoiceBolProOrLoadIdSpecified = function () {
            return !$scope.invoiceNumber && !$scope.proNumber && !$scope.bolNumber && !$scope.loadId;
        };

        $scope.refreshTable = function () {
            FinancialBoardService.historyInvoices({
                fromDate: $scope.fromDate,
                toDate: $scope.toDate,
                customer: !_.isUndefined($scope.selectedCustomer) ? $scope.selectedCustomer.id : null,
                invoiceNumber: $scope.invoiceNumber,
                bol: $scope.bolNumber,
                pro: $scope.proNumber,
                loadId: $scope.loadId
            }, function (data) {
                $scope.invoices = data;
                $scope.selectedInvoices.length = 0;
            }, function (data, status) {
                $scope.$root.$emit('event:application-error', 'Invoice History load failed!', 'Can\'t load Invoice History: ' + status);
            });
        };

        $scope.viewShipment = function (cbi) {
            if (cbi === true) {
                $scope.$broadcast('event:showEditSalesOrder', {
                    shipmentId: $scope.selectedCbiItems[0].loadId,
                    formDisabled: true,
                    parentDialog: 'cbi-details'
                });
            } else {
                $scope.$broadcast('event:showEditSalesOrder', {shipmentId: $scope.selectedInvoices[0].loadId, formDisabled: true});
            }
        };

        function reprocess(reprocessParams) {
            FinancialBoardService.reProcessHistory(reprocessParams, function (data) {
                if (reprocessParams.financial) {
                    $scope.$root.$emit('event:operation-success', 'Re-Process Invoice success!',
                            'Invoice ' + ($scope.selectedInvoices[0].invoiceNumber || '') + ' has been successfully sent to finance system.');
                }

                if (reprocessParams.customerEmail) {
                    $scope.$root.$emit('event:operation-success', 'Re-Process Invoice success!',
                            'Invoice ' + ($scope.selectedInvoices[0].invoiceNumber || '') + ' has been successfully sent to customer through email.');
                }

                if (reprocessParams.customerEDI) {
                    $scope.$root.$emit('event:operation-success', 'Re-Process Invoice success!',
                            'Invoice ' + ($scope.selectedInvoices[0].invoiceNumber || '') + ' has been successfully sent to customer through EDI.');
                }
            }, function () {
                $scope.$root.$emit('event:application-error', 'Re-Process Invoice failed!',
                        'Can\'t Re-Process Invoice ' + ($scope.selectedInvoices[0].invoiceNumber || ''));
            });

            $scope.reprocessDialog.close();
            $scope.reprocessFinancialPopup.close();
        }

        $scope.reprocessToFinance = function () {
            var reprocessParams = {
                invoiceId: $scope.selectedInvoices[0].invoiceId,
                financial: true
            };

            var checkedItems = _.where($scope.cbiItems, {isChecked: true});

            reprocessParams.loadIds = _.pluck(_.filter(checkedItems, function (item) {
                return !item.adjustmentId;
            }), 'loadId');

            reprocessParams.adjustmentIds = _.pluck(_.filter(checkedItems, function (item) {
                return item.adjustmentId;
            }), 'adjustmentId');

            reprocess(reprocessParams);
        };

        $scope.reprocessInvoice = function () {
            var reprocessParams = {
                invoiceId: $scope.selectedInvoices[0].invoiceId,
                financial: $scope.reprocessDialog.financial,
                customerEmail: $scope.reprocessDialog.customerEmail,
                emails: $scope.reprocessDialog.emails,
                subject: $scope.reprocessDialog.subject,
                comments: $scope.reprocessDialog.comments,
                customerEDI: $scope.reprocessDialog.customerEDI
            };

            if ($scope.selectedInvoices[0].invoiceType === 'TRANSACTIONAL') {
                if ($scope.selectedInvoices[0].adjustmentId) {
                    reprocessParams.adjustmentIds = [$scope.selectedInvoices[0].adjustmentId];
                } else {
                    reprocessParams.loadIds = [$scope.selectedInvoices[0].loadId];
                }
            } else {
                // there can be more loads/adjustments with the same invoice ID, but with different group invoice number
                reprocessParams.invoiceNumber = $scope.selectedInvoices[0].invoiceNumber;
            }

            reprocess(reprocessParams);
        };

        $scope.viewDetails = function () {
            if ($scope.selectedInvoices[0].loadId) {
                $scope.viewShipment();
            } else if ($scope.selectedInvoices[0].invoiceType === 'CBI') {
                FinancialBoardService.historyCBIDetails({
                    invoiceId: $scope.selectedInvoices[0].invoiceId,
                    groupInvoiceNumber: $scope.selectedInvoices[0].invoiceNumber
                }, function (data) {
                    $scope.cbiItems = data;
                    $scope.cbiDetailsPopup.show = true;
                }, function (data, status) {
                    $scope.$root.$emit('event:application-error', 'CBI load failed!', 'Can\'t load CBI: ' + status);
                });
            }
        };

        $scope.isAllowInvoiceReprocess = function () {
            return !_.isEmpty($scope.selectedInvoices) && ($scope.selectedInvoices[0].invoiceType === 'TRANSACTIONAL'
                   || !$scope.selectedInvoices[0].invoiceInFinancials || $scope.$root.isFieldRequired('ALLOW_REPROCESS_TO_FINANCIALS'))
                   && !($scope.selectedInvoices[0].invoiceInFinancials === false && !$scope.selectedInvoices[0].invoiceNumber);
        };

        $scope.showReprocessingDialog = function () {
            if ($scope.selectedInvoices[0].invoiceInFinancials) {
                FinancialBoardService.historyCBIDetails({
                    invoiceId: $scope.selectedInvoices[0].invoiceId,
                    groupInvoiceNumber: $scope.selectedInvoices[0].invoiceNumber
                }, function (data) {
                    $scope.cbiItems = data;
                    $scope.reprocessFinancialPopup.show = true;
                }, function (e, status) {
                    $scope.$root.$emit('event:application-error', 'CBI load failed!', 'Can\'t load CBI: ' + status);
                });
            } else {
                BillToEmailService.getEmails({billToId: $scope.selectedInvoices[0].billToId}, function (data) {
                    $scope.reprocessDialog.sendEmailAllowed = data && data.length && data[0].key;
                    $scope.reprocessDialog.emails = data && data.length ? data[0].value : '';
                    $scope.reprocessDialog.subject = 'PLS-Invoice-' + $scope.selectedInvoices[0].invoiceNumber;
                    $scope.reprocessDialog.comments = '';
                    $scope.reprocessDialog.show = true;
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Bill To email load failed!', 'Failed loading list of emails for Bill To '
                            + $scope.selectedInvoices[0].billToId);
                });
            }
        };

        $scope.$watch('fromDate', function (newValue) {
            if (newValue) {
                $scope.minToDate = DateTimeUtils.parseISODate(newValue);
                $scope.maxToDate = DateTimeUtils.addMonths($scope.minToDate, MAX_HISTORY_RANGE_IN_MONTHS);
            } else {
                $scope.minToDate = undefined;
                $scope.maxToDate = undefined;
            }
        });

        $scope.$watch('toDate', function (newValue) {
            if (newValue) {
                $scope.maxFromDate = DateTimeUtils.parseISODate(newValue);
                $scope.minFromDate = DateTimeUtils.addMonths($scope.maxFromDate, -MAX_HISTORY_RANGE_IN_MONTHS);
            } else {
                $scope.maxFromDate = undefined;
                $scope.minFromDate = undefined;
            }
        });

        $scope.isSearchAvailable = function () {
            return $scope.bolNumber || $scope.proNumber || $scope.invoiceNumber || $scope.loadId ||
                    ($scope.selectedCustomer && (($scope.fromDate && $scope.toDate && $scope.plsHistorySearchForm.$valid)
                    || $scope.invoiceNumber || $scope.bolNumber || $scope.proNumber)) || ($scope.fromDate && $scope.toDate);
        };

        $scope.resetSearch = function () {
            $scope.$broadcast('event:cleaning-input');
            $scope.selectedInvoices.length = 0;
            $scope.invoices = [];
        };

        $scope.exportInvoices = function () {
            $scope.$emit('event:exportInvoices', {
                sheetName: 'History Invoices',
                grid: $scope.invoicesGrid,
                selectedRows: false,
                fileName: 'History_Invoices_'
            });
        };

        $scope.exportCBI = function () {
            $scope.$emit('event:exportInvoices', {
                sheetName: $scope.selectedInvoices[0].invoiceNumber || $scope.selectedInvoices[0].invoiceId,
                grid: $scope.cbiDetailsGrid,
                selectedRows: false,
                fileName: $scope.selectedInvoices[0].invoiceNumber || $scope.selectedInvoices[0].invoiceId + '_'
            });
        };

        $scope.invoicesGrid = {
            enableColumnResize: true,
            data: 'invoices',
            multiSelect: false,
            selectedItems: $scope.selectedInvoices,
            columnDefs: [{
                field: 'adjustment',
                headerCellTemplate: 'pages/cellTemplate/adjustment-header-cell.html',
                headerClass: 'cellToolTip',
                cellTemplate: '<div class="ngCellText text-center tooltip-wide" data-ng-show="row.entity.adjustment">'+
                    '<i class="icon-wrench" data-tooltip-placement="right" data-tooltip="Adjustment"></i></div>',
                cellClass: 'cellToolTip',
                width: '3%',
                searchable: false,
                sortable: true,
                exportTemplate: 'exportEntity.adjustment ? "Adjustment" : ""',
                exportDisplayName: 'Adjustment'
            }, {
                field: 'invoiceDate',
                displayName: 'Invoice Date',
                cellFilter: 'date:wideAppDateFormat',
                width: '6%'
            }, {
                field: 'invoiceNumber',
                displayName: 'Invoice #',
                width: '9%'
            }, {
                field: 'userName',
                displayName: 'User Name',
                width: '9%'
            }, {
                field: 'loadId',
                displayName: 'Load ID',
                width: '7%'
            }, {
                field: 'bol',
                displayName: 'BOL',
                width: '7%'
            }, {
                field: 'pro',
                displayName: 'PRO #',
                width: '7%'
            }, {
                field: 'networkName',
                displayName: 'Business Unit',
                width: '7%'
            }, {
                field: 'customerName',
                displayName: 'Customer',
                width: '8%'
            }, {
                field: 'carrierName',
                displayName: 'Carrier',
                width: '8%'
            }, {
                field: 'invoiceAmount',
                displayName: 'Invoice Amount',
                width: '9%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'paidAmount',
                displayName: 'Paid Amount',
                width: '8%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'paidDue',
                displayName: 'Paid Due',
                width: '8%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'dueDate',
                displayName: 'Due Date',
                width: '7%',
                cellFilter: 'date:wideAppDateFormat'
            }],
            action: function () {
                $scope.viewDetails();
            },
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin(), NgGridPluginFactory.hideColumnPlugin()]
        };

        $scope.cbiDetailsGrid = {
            enableColumnResize: true,
            data: 'cbiItems',
            multiSelect: false,
            selectedItems: $scope.selectedCbiItems,
            columnDefs: [{
                field: 'adjustment',
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
                field: 'invoiceNumber',
                displayName: 'Invoice #',
                width: '6%'
            }, {
                field: 'loadId',
                displayName: 'Load ID',
                width: '5%'
            }, {
                field: 'bol',
                displayName: 'BOL',
                width: '7%'
            }, {
                field: 'pro',
                displayName: 'PRO #',
                width: '7%'
            }, {
                field: 'po',
                displayName: 'PO #',
                width: '7%'
            }, {
                field: 'glNumber',
                displayName: 'GL #',
                width: '6%'
            }, {
                field: 'origin',
                displayName: 'Origin',
                cellFilter: 'zip',
                width: '12%'
            }, {
                field: 'destination',
                displayName: 'Dest.',
                cellFilter: 'zip',
                width: '12%'
            }, {
                field: 'carrierName',
                displayName: 'Carrier',
                width: '10%'
            }, {
                field: 'acc',
                displayName: 'Acc Charges',
                width: '5%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'fs',
                displayName: 'FS',
                width: '5%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'totalRevenue',
                displayName: 'Total Revenue',
                width: '5%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'totalCost',
                displayName: 'Total Cost',
                width: '5%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'paidAmount',
                displayName: 'Paid',
                width: '5%',
                cellFilter: 'plsCurrency',
                searchable: false
            }],
            action: function () {
                $scope.viewShipment(true);
            },
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin(), NgGridPluginFactory.progressiveSearchPlugin()],
            progressiveSearch: true
        };

        $scope.cbiReprocessGrid = {
            enableColumnResize: true,
            data: 'cbiItems',
            multiSelect: false,
            selectedItems: $scope.selectedCbiItems,
            columnDefs: [{
                field: 'isChecked',
                width: '5%',
                headerCellTemplate: '<div class="ngCellText text-center">'
                + '<input type="checkbox" data-ng-model="reprocessFinancialPopup.selectAll"'
                + ' data-ng-change="reprocessFinancialPopup.toggleSelectAll()"'
                + ' data-pls-tri-state-checkbox data-ng-true-value="ALL" data-ng-false-value="NONE"/></div>',
                cellTemplate: '<div class="ngCellText text-center"><input type="checkbox" data-ng-model="row.entity.isChecked"'
                + ' data-ng-change="reprocessFinancialPopup.toggleSelectRow()"/></div>'
            }, {
                field: 'loadId',
                displayName: 'Load ID',
                width: '5%'
            }, {
                field: 'bol',
                displayName: 'BOL',
                width: '7%'
            }, {
                field: 'pro',
                displayName: 'PRO #',
                width: '7%'
            }, {
                field: 'po',
                displayName: 'PO #',
                width: '7%'
            }, {
                field: 'glNumber',
                displayName: 'GL #',
                width: '7%'
            }, {
                field: 'origin',
                displayName: 'Origin',
                cellFilter: 'zip',
                width: '15%'
            }, {
                field: 'destination',
                displayName: 'Dest.',
                cellFilter: 'zip',
                width: '15%'
            }, {
                field: 'carrierName',
                displayName: 'Carrier',
                width: '12%'
            }, {
                field: 'acc',
                displayName: 'Acc Charges',
                width: '5%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'fs',
                displayName: 'FS',
                width: '5%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'totalRevenue',
                displayName: 'Total Revenue',
                width: '5%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'totalCost',
                displayName: 'Total Cost',
                width: '5%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'paidAmount',
                displayName: 'Paid',
                width: '5%',
                cellFilter: 'plsCurrency',
                searchable: false
            }],
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            progressiveSearch: true
        };

        $scope.recordsUpdate = function () {
            $scope.gridRecords = $scope.invoicesGrid.ngGrid.filteredRows.length;
        };

        $scope.$watch('invoicesGrid.ngGrid.filteredRows.length', $scope.recordsUpdate);
    }
]);