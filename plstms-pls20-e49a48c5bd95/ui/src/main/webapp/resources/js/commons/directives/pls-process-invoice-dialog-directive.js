/**
 * Process Load(s) to financial confirmation and result dialog.
 *
 * @author: Dmitry Nikolaenko
 */
angular.module('plsApp.directives').directive('plsProcessInvoiceDialog', [function () {
    return {
        restrict: 'A',
        scope: {},
        templateUrl: 'pages/tpl/process-invoice-dialog.html',
        controller: ['$scope', '$http', '$timeout', '$q', 'NgGridPluginFactory', 'BillToEmailService',
                     'FinancialBoardService', 'FinancialBoardCBIService', 'StringUtils',
                     function ($scope, $http, $timeout, $q, NgGridPluginFactory, BillToEmailService,
                             FinancialBoardService, FinancialBoardCBIService, StringUtils) {
            'use strict';

            $scope.selectedBillToList = [];
            $scope.allInvoices = [];

            $scope.processConfirmationDialog = {
                show: false,
                emails: '',
                sendEmail: true,
                invoicesGrid: {
                    enableColumnResize: true,
                    data: 'processingInvoices',
                    enableRowSelection: false,
                    columnDefs: [{
                        field: 'compoundSortField',
                        headerCellTemplate: 'pages/cellTemplate/adjustment-header-cell.html',
                        headerClass: 'cellToolTip',
                        cellTemplate: 'pages/cellTemplate/adjustment-cell.html',
                        cellClass: 'cellToolTip',
                        width: '6%',
                        sortable: true
                    }, {
                        field: 'loadId',
                        displayName: 'Load ID',
                        width: '47%'
                    }, {
                        field: 'bolNumber',
                        displayName: 'BOL',
                        width: '47%'
                    }],
                    plugins: [NgGridPluginFactory.plsGrid()],
                    progressiveSearch: false
                }
            };

            $scope.processResultsDialog = {
                show: false,
                invoicesGrid: {
                    enableColumnResize: true,
                    data: 'processResultsDialog.resultsForSelectedBillTo',
                    enableRowSelection: false,
                    columnDefs: [{
                        field: 'compoundSortField',
                        headerCellTemplate: 'pages/cellTemplate/adjustment-header-cell.html',
                        headerClass: 'cellToolTip',
                        cellTemplate: 'pages/cellTemplate/adjustment-cell.html',
                        cellClass: 'cellToolTip',
                        width: '5%',
                        sortable: true
                    }, {
                        field: 'invoiceNumber',
                        displayName: 'Invoice#',
                        width: '15%'
                    }, {
                        field: 'loadId',
                        displayName: 'Load ID',
                        width: '15%'
                    }, {
                        field: 'bol',
                        displayName: 'BOL',
                        width: '20%'
                    }, {
                        field: 'errorMessage ? "Failed" : "Successful"',
                        displayName: 'Status',
                        width: '10%'
                    }, {
                        field: 'errorMessage',
                        displayName: 'Error',
                        width: '35%'
                    }],
                    plugins: [NgGridPluginFactory.plsGrid()],
                    progressiveSearch: false
                }
            };

            $scope.isSelectedCBIBillTo = function() {
                return $scope.processConfirmationDialog.billTo && $scope.processConfirmationDialog.billTo.processType === 'CBI'
                    && $scope.processResultsDialog.invoiceNumber;
            };

            function initProcessingInvoices() {
                $scope.processingInvoices = _.filter($scope.allInvoices, function (item) {
                    return item.billToId === $scope.processConfirmationDialog.billTo.id;
                });
            }

            function generateTransactionalEmailSubject(billToId) {
                var invoiceNumbers = [];

                _.each($scope.allInvoices, function (invoice) {
                    if (!invoice.doNotInvoice && billToId === invoice.billToId) {
                        var invoiceNumber = 'T-' + invoice.loadId;

                        if (invoice.adjustmentId) {
                            invoiceNumber += '-AD' + StringUtils.lPadZero(invoice.adjustmentRevision, 2);
                        } else {
                            invoiceNumber += '-0000';
                        }

                        invoiceNumbers.push(invoiceNumber);
                    }
                });

                if (invoiceNumbers.length) {
                    return 'PLS-Invoice-' + invoiceNumbers.join(',');
                } else {
                    return undefined;
                }
            }

            $scope.updateProcessConfirmationDialog = function() {
                var billTo = _.findWhere($scope.selectedBillToList, {id: $scope.processConfirmationDialog.billTo.id});
                $scope.processConfirmationDialog.sendEmail = billTo.sendEmail;
                $scope.processConfirmationDialog.emails = billTo.email;
                $scope.processConfirmationDialog.comments = billTo.comment;
                $scope.processConfirmationDialog.subject = billTo.subject;

                if ($scope.processConfirmationDialog.sendEmail) {
                    var itemsToInvoiceCustomer = _.filter($scope.allInvoices, function (invoice) {
                        return !invoice.doNotInvoice;
                    });

                    if (itemsToInvoiceCustomer.length === 0) {
                        $scope.processConfirmationDialog.sendEmail = false;
                    }
                }

                initProcessingInvoices();
            };

            function isInvoiceNumberColumnVisible() {
                return $scope.processResultsDialog.invoicesGrid.$gridScope.columns[1].visible === true;
            }

            $scope.updateProcessResultsDialog = function () {
                // filtering displayed invoices by selected bill to
                $scope.processResultsDialog.resultsForSelectedBillTo = _.filter($scope.processResultsDialog.results, function (item) {
                    return item.billToId === $scope.processConfirmationDialog.billTo.id;
                });
                var billTo = _.findWhere($scope.selectedBillToList, {id: $scope.processConfirmationDialog.billTo.id});
                if (!_.findWhere($scope.processResultsDialog.results, {billToId: $scope.processConfirmationDialog.billTo.id}).invoiceNumber) {
                    delete $scope.processResultsDialog.invoiceNumber;
                } else {
                    $scope.processResultsDialog.invoiceNumber = billTo.invoiceNumber;
                }
                $scope.processResultsDialog.emails = billTo.email;
                $scope.processResultsDialog.subject = billTo.subject;
                $scope.processResultsDialog.comments = billTo.comment;
                if((billTo.processType === 'TRANSACTIONAL') !== isInvoiceNumberColumnVisible()) {
                    $scope.processResultsDialog.invoicesGrid.$gridScope.columns[1].toggleVisible();
                }
            };

            function processInvoices(invoiceProcessingDetails) {
                FinancialBoardService.processInvoices({
                    invoiceDate: $scope.invoiceDate,
                    invoiceProcessingDetails: invoiceProcessingDetails
                }, function (data) {
                    $scope.processConfirmationDialog.show = false;
                    $scope.processResultsDialog.results = data;

                    _.each($scope.processResultsDialog.results, function (item) {
                        if (_.contains(['ACCOUNTING_BILLING', 'ACCOUNTING_BILLING_ADJUSTMENT_ACCESSORIAL'], item.finalizationStatus)) {
                            item.errorMessage = 'Unexpected Error Occurred';
                        }
                    });

                    var successfulItems = _.filter($scope.processResultsDialog.results, function (invoice) {
                        return !invoice.errorMessage && invoice.finalizationStatus === 'ACCOUNTING_BILLING_RELEASE';
                    });
                    $scope.processResultsDialog.successfulCount = successfulItems.length;
                    $scope.processResultsDialog.failedCount = data.length - successfulItems.length;

                    $scope.processResultsDialog.totalRevenue = _.reduce(successfulItems, function (memo, item) {
                        return memo + item.revenue;
                    }, 0);

                    $scope.processResultsDialog.totalCost = _.reduce(successfulItems, function (memo, item) {
                        return memo + item.cost;
                    }, 0);

                    $timeout(function () {
                        $scope.updateProcessResultsDialog();
                        $scope.processResultsDialog.show = true;
                    }, 1);
                }, function (e) {
                    $scope.processConfirmationDialog.show = false;
                    $scope.$root.$emit('event:application-error', 'Process invoices error!', e && e.data && e.data.message ? e.data.message : '');
                });
            }

            $scope.processInvoicesToFinance = function () {
                var invoiceProcessingDetails = [];
                _.each($scope.selectedBillToList, function (item, index) {
                    var loadIds =  _.pluck(_.filter($scope.allInvoices, function (invoice) {
                        return !invoice.adjustmentId && item.id === invoice.billToId;
                    }), 'loadId');
                    var adjustmentIds = _.pluck(_.filter($scope.allInvoices, function (invoice) {
                        return invoice.adjustmentId && item.id === invoice.billToId;
                    }), 'adjustmentId');
                    var data = {
                        billToId: item.id,
                        email: item.email,
                        comments: item.comment,
                        subject: item.subject,
                        loadIds: loadIds,
                        adjustmentIds: adjustmentIds
                    };
                    if (item.processType === 'CBI') {
                        data.consolidatedInvoiceNumber = item.invoiceNumber;
                    }
                    invoiceProcessingDetails.push(data);
                });

                processInvoices(invoiceProcessingDetails);
            };

            $scope.getProcessedResultReport = function () {
                var reportParams = {
                    loads: $scope.processResultsDialog.resultsForSelectedBillTo,
                    successful: $scope.processResultsDialog.successfulCount,
                    failed: $scope.processResultsDialog.failedCount,
                    customer: $scope.processConfirmationDialog.customer,
                    billTo: $scope.processConfirmationDialog.billTo.name,
                    email: $scope.processConfirmationDialog.emails,
                    subject: $scope.processConfirmationDialog.subject,
                    comments: $scope.processConfirmationDialog.comments
                };

                $http.post('/restful/invoice/financial/board/transactional/processExport', reportParams, {
                    responseType: 'arraybuffer'
                }).success(function (response) {
                    var file = new window.Blob([response], {
                        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                    });
                    if ($scope.$root.browserDetect.browser === 'Explorer') {
                        window.navigator.msSaveOrOpenBlob(file, 'Export Processed Loads Summary for Transactional.xlsx');
                    } else {
                        var fileURL = window.URL.createObjectURL(file);
                        var a = document.createElement("a");

                        a.href = fileURL;
                        a.download = "Export Processed Loads Summary for Transactional.xlsx";
                        document.body.appendChild(a);
                        a.click();
                    }
                }).error(function (data, status) {
                    $scope.$root.$emit('event:application-error', 'Export error!', status + ': ' + data);
                });
            };

            $scope.$watch('processConfirmationDialog.comments', function (newValue, oldValue) {
                _.each($scope.selectedBillToList, function (billTo, index) {
                    if (billTo.id === $scope.processConfirmationDialog.billTo.id) {
                        $scope.selectedBillToList[index].comment = newValue ? newValue : '';
                    }
                });
            });

            $scope.$watch('processConfirmationDialog.subject', function (newValue, oldValue) {
                _.each($scope.selectedBillToList, function (billTo, index) {
                    if (billTo.id === $scope.processConfirmationDialog.billTo.id) {
                        $scope.selectedBillToList[index].subject = newValue ? newValue : '';
                    }
                });
            });

            $scope.$watch('processConfirmationDialog.emails', function (newValue, oldValue) {
                _.each($scope.selectedBillToList, function (billTo, index) {
                    if (billTo.id === $scope.processConfirmationDialog.billTo.id) {
                        $scope.selectedBillToList[index].email = newValue ? newValue : oldValue;
                    }
                });
            });

            $scope.isSendEmail = function() {
                return $scope.processConfirmationDialog.billTo && $scope.processConfirmationDialog.billTo.sendEmail;
            };

            var dialogInitPromises;

            function generateEmailSubject() {
                _.each($scope.selectedBillToList, function (billTo, index) {
                    if (billTo.processType === 'CBI') {
                        var resource = FinancialBoardCBIService.getNextInvoiceNumberForCBI({}, function (data) {
                            billTo.invoiceNumber = data.data;
                            billTo.subject = 'PLS-Invoice-' + billTo.invoiceNumber;
                        }, function() {
                            $scope.$root.$emit('event:application-error', 'Error preparing data for Invoice Processing!',
                                    'Email subject generation failed');
                        });
                        dialogInitPromises.push(resource.$promise);
                    } else {
                        $scope.selectedBillToList[index].subject = generateTransactionalEmailSubject(billTo.id);
                    }
                });
            }

            function getBillToEmails() {
                var resource = BillToEmailService.getEmails({billToId: _.pluck($scope.selectedBillToList, 'id')}, function (data) {
                    _.each($scope.selectedBillToList, function (billTo, index) {
                        var result = _.findWhere(data, {key: billTo.id});
                        $scope.selectedBillToList[index].sendEmail = !_.isUndefined(result);
                        if ($scope.selectedBillToList[index].sendEmail) {
                            var invoices = _.filter($scope.allInvoices, function (invoice) {
                                return !invoice.doNotInvoice && billTo.id === invoice.billToId;
                            });
                            if (!invoices.length) {
                                $scope.selectedBillToList[index].sendEmail = false;
                            }
                        }
                        $scope.selectedBillToList[index].email = result ? result.value : '';
                    });
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Bill To email load failed!', 'Failed loading list of emails for Bill To.');
                });
                dialogInitPromises.push(resource.$promise);
            }

            $scope.$on('openProcessInvoiceDialog', function (event, processData) {
                $scope.processConfirmationDialog.customer = processData.customer;
                $scope.allInvoices = processData.allInvoices;
                $scope.processConfirmationDialog.invoicesCount = $scope.allInvoices.length;
                $scope.selectedBillToList = processData.selectedBillToList;
                $scope.processConfirmationDialog.billTo = $scope.selectedBillToList[0];
                $scope.invoiceDate = processData.invoiceDate;

                dialogInitPromises = [];
                generateEmailSubject();
                getBillToEmails();
                $q.all(dialogInitPromises).then(function () {
                    $scope.updateProcessConfirmationDialog();
                    $scope.processConfirmationDialog.show = true;
                });
            });

            $scope.closeProcessResultsDialog = function () {
                $scope.processResultsDialog.show = false;
                $scope.processConfirmationDialog.comments = '';
                $scope.$emit('event:updateDataAfterProcessing');
            };
        }]
    };
}]);