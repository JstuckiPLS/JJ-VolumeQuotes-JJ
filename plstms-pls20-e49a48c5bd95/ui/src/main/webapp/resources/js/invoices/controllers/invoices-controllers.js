angular.module('plsApp').controller('CreditAndBillingInfoController', ['$scope', 'CustomerInvoiceService', 'NgGridPluginFactory',
    function ($scope, CustomerInvoiceService, NgGridPluginFactory) {
        'use strict';

        $scope.weekDays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

        $scope.creditBillingInfo = {
            taxId: '',
            creditLimit: undefined,
            unpaid: undefined,
            available: undefined,
            billToData: [],
            accExec: {}
        };

        $scope.init = function () {
            CustomerInvoiceService.getBillingAndCreditInfo({
                customerId: $scope.$root.authData.organization.orgId,
                userId: $scope.$root.authData.personId
            }, function (data) {
                $scope.creditBillingInfo = data;
            });
        };

        $scope.billToGrid = {
            options: {
                data: 'creditBillingInfo.billToList',
                columnDefs: [
                    {
                        field: 'address.addressName',
                        displayName: 'Name',
                        width: '20%'
                    },
                    {
                        field: 'currency',
                        displayName: 'Currency',
                        width: '7%',
                        cellFilter: 'currencyCode'
                    },
                    {
                        field: 'invoicePreferences.invoiceType',
                        displayName: 'Invoices Type',
                        width: '10%',
                        cellFilter: 'invoiceType'
                    },
                    {
                        field: 'invoicePreferences.processingType',
                        displayName: 'Processing',
                        width: '10%',
                        cellFilter: 'invoiceProcessingType'
                    },
                    {
                        field: 'invoicePreferences',
                        displayName: 'Period',
                        cellFilter: 'processingInfo:true',
                        width: '10%'
                    },
                    {
                        field: 'address.address1',
                        displayName: 'Address',
                        width: '22%'
                    },
                    {
                        field: 'address.zip',
                        displayName: 'City, ST, ZIP',
                        cellFilter: 'zip',
                        width: '20%'
                    }
                ],
                enableColumnResize: true,
                multiSelect: false,
                sortInfo: {
                    fields: ['address.addressName'],
                    directions: ['asc']
                },
                plugins: [NgGridPluginFactory.plsGrid()]
            }
        };
    }
]);

angular.module('plsApp').controller('CustomerInvoicesController', ['$scope', '$rootScope', '$filter', 'CustomerInvoiceService',
    'NgGridPluginFactory', 'ExportDataBuilder', 'ExportService', 'ShipmentDetailsService', 'urlConfig', 'StringUtils',
    function ($scope, $rootScope, $filter, CustomerInvoiceService, NgGridPluginFactory, ExportDataBuilder, ExportService, ShipmentDetailsService,
              urlConfig, StringUtils) {
        'use strict';

        $scope.pageModel = {
            searchCriteria: {
                includeFirstThirtyDays: false,
                includeSecondThirtyDays: false,
                includeThirdThirtyDays: false,
                includeLastDays: false
            },
            showSendMailDialog: false,
            emailOptions: {},
            totalPaid: 0,
            totalDue: 0,
            customerInvoices: [],
            selectedInvoices: []
        };

        $scope.$on('ngGridEventData', function (event, gridId) {
            if (gridId === $scope.customerInvoicesGrid.gridId) {
                $scope.pageModel.totalPaid = 0;
                $scope.pageModel.totalDue = 0;
                angular.forEach($scope.customerInvoicesGrid.ngGrid.filteredRows, function (row) {
                    $scope.pageModel.totalPaid += row.entity.paid;
                    $scope.pageModel.totalDue += row.entity.due;
                });
            }
        });

        $scope.loadInvoices = function () {
            $scope.pageModel.customerInvoices.length = 0;
            $scope.pageModel.selectedInvoices.length = 0;
            CustomerInvoiceService.findCustomerInvoices({
                customerId: $scope.$root.authData.organization.orgId,
                userId: $scope.$root.authData.personId
            }, $scope.pageModel.searchCriteria, function (data) {
                $scope.pageModel.customerInvoices = data;
            });
        };

        $scope.viewInvoice = function (invoice) {
            var invoiceDetailsOption = {
                shipmentId: invoice.loadId,
                customerId: $scope.$root.authData.organization.orgId,
                userId: $scope.$root.authData.personId
            };

            $scope.$broadcast('event:showShipmentDetails', invoiceDetailsOption);
        };

        function sendInvoiceByEmail(recipients, subject, content, invoicesDocumentId, paperworksDocumentId, invoiceNumbers, invoicesDocumentName) {
            CustomerInvoiceService.sendEmail({
                customerId: $scope.$root.authData.organization.orgId,
                userId: $scope.$root.authData.personId,
                emails: recipients,
                subject: subject,
                invoicesDocumentId: invoicesDocumentId,
                invoicesDocumentName: invoicesDocumentName,
                paperworksDocumentId: paperworksDocumentId,
                invoiceNumbers: invoiceNumbers,
                emailContent: content
            }, function (data) {
                $scope.$root.$emit('event:operation-success', 'Email To', StringUtils.format('Invoice #{0} successfully sent.', invoiceNumbers));
                $scope.pageModel.showSendMailDialog = false;
            }, function () {
                $scope.$root.$emit('event:application-error', 'Email To', 'Cannot send Invoice by Email.');
            });
        }

        $scope.sendInvoicesByEmail = function () {
            if ($scope.pageModel.selectedInvoices.length === 1) {
                CustomerInvoiceService.emailTo({
                    customerId: $scope.$root.authData.organization.orgId,
                    userId: $scope.$root.authData.personId,
                    invoiceNum: $scope.pageModel.selectedInvoices[0].invoiceNumber
                }, function (data) {
                    if (data) {
                        $scope.pageModel.emailOptions.subject = 'PLS Invoice-' + data.invoiceNumbers;
                        $scope.pageModel.emailOptions.editEmailRecipientsList = data.emails;
                        $scope.pageModel.emailOptions.attachedFileName = '';

                        var invoicesDocumentName;

                        if (data.paperworksDocumentId) {
                            $scope.pageModel.emailOptions.attachedFileName = '<a target="_blank" href="' + urlConfig.appContext + '/document/' +
                                    data.invoicesDocumentId + '">Invoices.pdf</a>';
                            invoicesDocumentName = 'Invoices.pdf';
                        } else {
                            $scope.pageModel.emailOptions.attachedFileName = '<a target="_blank" href="' + urlConfig.appContext + '/document/' +
                                    data.invoicesDocumentId + '">' + data.invoiceNumbers[0] + '.xls</a>';
                            invoicesDocumentName = data.invoiceNumbers[0] + '.xls';
                        }

                        $scope.pageModel.emailOptions.getTemplate = function () {
                            return data.emailBody;
                        };

                        $scope.pageModel.emailOptions.sendMailFunction = function (recipients, subject, content) {
                            sendInvoiceByEmail(recipients, subject, content, data.invoicesDocumentId, data.paperworksDocumentId, data.invoiceNumbers,
                                    invoicesDocumentName);
                        };

                        $scope.pageModel.showSendMailDialog = true;
                    }
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Email To', 'Cannot send Invoice by Email.');
                });
            }
        };

        $scope.exportInvoices = function () {
            if ($scope.pageModel.customerInvoices.length > 0) {
                var fileFormat = 'Invoices Result on {0,date,' + $scope.$root.exportFileNameDateFormat + '}.xlsx';
                var sheetName = "Invoices";

                var customerInvoices = _.map($scope.customerInvoicesGrid.ngGrid.filteredRows, function (item) {
                    return item.entity;
                });

                var columnNames = ExportDataBuilder.getColumnNames($scope.customerInvoicesGrid);
                var totalColumnIndex = ExportDataBuilder.getColumnIndex(columnNames, 'Due');

                var footerData = [[], []];

                if (totalColumnIndex !== undefined) {
                    footerData[0].length = totalColumnIndex + 1;
                    footerData[0][totalColumnIndex - 5] = 'Total Paid: ' + $filter('plsCurrency')($scope.pageModel.totalPaid);
                    footerData[0][totalColumnIndex - 2] = 'Total Due: ' + $filter('plsCurrency')($scope.pageModel.totalDue);
                }

                var headerData = [[]];

                if (totalColumnIndex !== undefined) {
                    switch ($scope.pageModel.searchCriteria.invoiceType) {
                        case 'PAID':
                            headerData[0][0] = 'Paid Invoices from '
                                    + $rootScope.formatDate($scope.pageModel.searchCriteria.paidFrom, $rootScope.exportFileNameDateFormat)
                                    + ' to ' + $rootScope.formatDate($scope.pageModel.searchCriteria.paidTo, $rootScope.exportFileNameDateFormat);
                            break;
                        case 'OPEN':
                            headerData[0][0] = 'Open Invoices';
                            break;
                        case 'PAST_DUE':
                            var data = [];

                            if ($scope.pageModel.searchCriteria.includeFirstThirtyDays) {
                                data.push('1-30');
                            }

                            if ($scope.pageModel.searchCriteria.includeSecondThirtyDays) {
                                data.push('31-60');
                            }

                            if ($scope.pageModel.searchCriteria.includeThirdThirtyDays) {
                                data.push('61-90');
                            }

                            if ($scope.pageModel.searchCriteria.includeLastDays) {
                                data.push('+90');
                            }

                            headerData[0][0] = 'Past Due invoices : ' + data.join(',');

                            break;
                    }

                    if ($scope.pageModel.carrierTuple) {
                        data = [];
                        data [0] = 'Carrier: ' + $scope.pageModel.carrierTuple.name;
                        headerData.push(data);
                    }

                }

                var exportData = ExportDataBuilder.buildExportData($scope.customerInvoicesGrid, customerInvoices, fileFormat, sheetName, footerData,
                        headerData);

                ExportService.exportData(exportData);
            }
        };

        $scope.isPastDueFilterRequired = function () {
            return $scope.pageModel.searchCriteria.invoiceType === 'PAST_DUE'
                    && !$scope.pageModel.searchCriteria.includeFirstThirtyDays
                    && !$scope.pageModel.searchCriteria.includeSecondThirtyDays
                    && !$scope.pageModel.searchCriteria.includeThirdThirtyDays
                    && !$scope.pageModel.searchCriteria.includeLastDays;
        };

        $scope.isCantSearch = function () {
            return !$scope.pageModel.searchCriteria.invoiceType ||
                    ($scope.pageModel.searchCriteria.invoiceType === 'PAID' &&
                    (!$scope.pageModel.searchCriteria.paidFrom || !$scope.pageModel.searchCriteria.paidTo)) || $scope.isPastDueFilterRequired();
        };

        $scope.init = function () {
            CustomerInvoiceService.getCustomerInvoiceSummary({
                customerId: $scope.$root.authData.organization.orgId,
                userId: $scope.$root.authData.personId
            }, function (data) {
                $scope.pageModel.consolidatedInfo = data;
            });
        };

        $scope.$watch('pageModel.searchCriteria.invoiceType', function () {
            if ($scope.pageModel.searchCriteria.invoiceType !== 'PAID') {
                $scope.pageModel.searchCriteria.paidFrom = undefined;
                $scope.pageModel.searchCriteria.paidTo = undefined;
            }
        });

        $scope.$watch('pageModel.carrierTuple', function () {
            $scope.pageModel.searchCriteria.carrierId = $scope.pageModel.carrierTuple ? $scope.pageModel.carrierTuple.id : undefined;
        });

        function onShowTooltip(row) {
            ShipmentDetailsService.getTooltipData({
                customerId: $scope.authData.organization.orgId,
                shipmentId: row.entity.loadId
            }, function (data) {
                if (data) {
                    $scope.tooltipData = data;
                }
            });
        }

        $scope.customerInvoicesGrid = {
            enableColumnResize: true,
            data: 'pageModel.customerInvoices',
            selectedItems: $scope.pageModel.selectedInvoices,
            columnDefs: [
                {
                    field: 'bol',
                    displayName: 'BOL',
                    cellClass: 'text-center',
                    showTooltip: true,
                    headerClass: 'text-center',
                    width: '7%'
                },
                {
                    field: 'pro',
                    displayName: 'Pro#',
                    width: '7%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'ref',
                    displayName: 'Shipper Ref#',
                    width: '7%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'origin',
                    displayName: 'Origin',
                    width: '11%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'destination',
                    displayName: 'Destination',
                    width: '11%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'invoiceNumber',
                    displayName: 'Invoice #',
                    width: '10%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'invoiceDate',
                    displayName: 'Invoice Date',
                    cellFilter: 'date:appDateFormat',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    width: '7%'
                },
                {
                    field: 'dueDate',
                    displayName: 'Due Date',
                    cellFilter: 'date:appDateFormat',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    width: '7%'
                },
                {
                    field: 'overdue',
                    displayName: 'Overdue',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    width: '6%'
                },
                {
                    field: 'invoiced',
                    displayName: 'Invoiced',
                    width: '9%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    searchable: false,
                    cellFilter: 'plsCurrency'
                },
                {
                    field: 'paid',
                    displayName: 'Paid',
                    width: '9%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    searchable: false,
                    cellFilter: 'plsCurrency'
                },
                {
                    field: 'due',
                    displayName: 'Due',
                    width: '9%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    searchable: false,
                    cellFilter: 'plsCurrency'
                }
            ],
            action: function (entity) {
                $scope.viewInvoice(entity);
            },
            plugins: [
                NgGridPluginFactory.plsGrid(),
                NgGridPluginFactory.tooltipPlugin(true),
                NgGridPluginFactory.actionPlugin(),
                NgGridPluginFactory.progressiveSearchPlugin()
            ],
            tooltipOptions: {
                url: 'pages/content/quotes/shipments-grid-tooltip.html',
                onShow: onShowTooltip
            },
            progressiveSearch: true,
            enableSorting: true,
            multiSelect: false,
            filterOptions: {
                useExternalFilter: false
            },
            sortInfo: {
                fields: ['bol'],
                directions: ['asc']
            }
        };
    }
]);