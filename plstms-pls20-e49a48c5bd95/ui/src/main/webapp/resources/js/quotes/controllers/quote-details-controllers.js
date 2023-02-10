angular.module('pls.controllers').controller('QuoteDetailsController', [
    '$scope', '$filter', 'SavedQuotesService', 'NgGridPluginFactory', 'SavedQuoteEmailService', 'DateTimeUtils',
    'ExportDataBuilder', 'ExportService', function ($scope, $filter, SavedQuotesService, NgGridPluginFactory,
    SavedQuoteEmailService, DateTimeUtils, ExportDataBuilder, ExportService) {
        'use strict';

        $scope.savedQuotes = [];
        $scope.selectedQuotes = [];

        var MAX_HISTORY_RANGE_IN_MONTHS = 3;

        $scope.wizardData = {};

        $scope.wizardData.shipment = {
            details: {
                accessorials: []
            },
            selectedProposition: {},
            finishOrder: {
                quoteMaterials: []
            }
        };

        $scope.wizardData.customerStatus = undefined;

        $scope.selectedCustomer = {
            id: undefined,
            name: undefined
        };

        $scope.emailOptions = {
            showSendEmailDialog: false,
            sendMailFunction: function (recipients, subject, content) {
                SavedQuoteEmailService.emailQuote({quoteId: $scope.selectedQuotes[0].id}, {
                    recipients: recipients,
                    subject: subject,
                    content: content
                }, function () {
                    $scope.$root.$emit('event:operation-success', 'Email send status', 'Email sent successfully');
                    $scope.emailOptions.showSendEmailDialog = false;
                }, function (data) {
                    $scope.$root.$emit('event:application-error', 'Email send status', data);
                });
            },
            shipmentSendMailModalOptions: {
                parentDialog: 'detailsDialogDiv'
            },
            getTemplate: function () {
                return SavedQuoteEmailService.getTemplate({quoteId: $scope.selectedQuotes[0].id});
            }
        };

        $scope.userCapabilities = ['QUOTES_VIEW'];

        function countRevenueTotalSum() {
            $scope.totalRevenueSum = _.reduce($scope.quotesGrid.ngGrid.filteredRows, function (memo, row) {
                return memo + row.entity.customerRevenue;
            }, 0);

            $scope.rowCount = $scope.quotesGrid.ngGrid.filteredRows.length;
        }

        function countCostTotalSum() {
            $scope.totalCostSum = _.reduce($scope.quotesGrid.ngGrid.filteredRows, function (memo, row) {
                return memo + row.entity.carrierCost;
            }, 0);
        }


        $scope.$on('ngGridEventData', function (event, gridId) {
            if (gridId === $scope.quotesGrid.gridId) {
                countRevenueTotalSum();
                countCostTotalSum();
            }
        });

        $scope.init = function () {
             $scope.getSavedQuotes();
        };

        $scope.getSavedQuotes = function () {
            if (!$scope.toDate && !$scope.fromDate) {
                $scope.toDate = DateTimeUtils.parseISODate(new Date());
                $scope.fromDate = DateTimeUtils.addMonths($scope.toDate, -MAX_HISTORY_RANGE_IN_MONTHS);
                $scope.toDate = $filter('date')($scope.toDate, $scope.$root.transferDateFormat);
                $scope.fromDate = $filter('date')($scope.fromDate, $scope.$root.transferDateFormat);
            }

            SavedQuotesService.list({
                customerId: _.isUndefined($scope.selectedCustomer) || _.isUndefined($scope.selectedCustomer.id)
                                 ? null : $scope.selectedCustomer.id,
                fromDate: $scope.fromDate,
                toDate: $scope.toDate
            }, function (data) {
                $scope.savedQuotes = data;
                $scope.selectedQuotes.length = 0;
            }, function () {
                $scope.savedQuotes.length = 0;
                $scope.selectedQuotes.length = 0;
            });
        };

        function showSavedQuoteDetailsDialog(quote) {
            SavedQuotesService.get({
                customerId: $scope.$root.authData.organization.orgId,
                propositionId: quote.id
            }, function (response) {
                $scope.wizardData.shipment = response.shipmentDTO;
                $scope.wizardData.customerStatus = response.customerStatus;
                var selectedCustomer = {};
                if (_.isUndefined($scope.selectedCustomer) || _.isUndefined($scope.selectedCustomer.id)) {
                    selectedCustomer.name = $scope.wizardData.shipment.customerName;
                    selectedCustomer.id = $scope.wizardData.shipment.organizationId;
                } else {
                    selectedCustomer = $scope.selectedCustomer;
                }

                $scope.$broadcast('event:saveSelectedQuoteForWizard', {
                    saveQuoteDialog: false,
                    expired: response.resolution === "EXPIRED",
                    unavailable: response.resolution === "UNAVAILABLE",
                    selectedCustomer: selectedCustomer
                });
            });
        }

        $scope.viewQuoteDetails = function () {
            showSavedQuoteDetailsDialog($scope.selectedQuotes[0]);
        };

        $scope.deleteQuote = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Delete Saved Quote',
                message: 'The selected Quote will be deleted.<br/>Do you want to proceed?',
                okFunction: $scope.savedQuotesRemoveDialog.confirmDelete
            });
        };

        $scope.emailQuote = function () {
            $scope.emailOptions.showSendEmailDialog = true;
            $scope.emailOptions.subject = $scope.selectedQuotes[0].quoteId ? 'Quote Ref #: ' + $scope.selectedQuotes[0].quoteId : 'Quote';
        };

        $scope.savedQuotesRemoveDialog = {
            confirmDelete: function () {
                SavedQuotesService.remove({
                    customerId: $scope.$root.authData.organization.orgId,
                    propositionId: $scope.selectedQuotes[0].id
                }, function () {
                    $scope.getSavedQuotes();
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

        $scope.$watch('selectedCustomer.id', function (newValue) {
            if (!newValue) {
                $scope.savedQuotes.length = 0;
                $scope.selectedQuotes.length = 0;
            }
        });

        function onShowTooltip(row) {
            SavedQuotesService.details({
                customerId: $scope.$root.authData.organization.orgId,
                propositionId: row.entity.id
            }, function (data) {
                if (data) {
                    $scope.tooltipData = data;
                }
            });
        }

        $scope.quotesGrid = {
            enableColumnResize: true,
            data: 'savedQuotes',
            multiSelect: false,
            selectedItems: $scope.selectedQuotes,
            columnDefs: [{
                field: 'quoteId',
                displayName: 'Quote Ref#',
                showTooltip: true,
                width: '3%'
            }, {
                field: 'volumeQuoteId',
                displayName: 'Volume Quote ID',
                width: '4%'
            }, {
                field: 'customerName',
                displayName: 'Customer Name',
                width: '9%'
            }, {
                field: 'origin',
                displayName: 'Origin',
                width: '14%',
                cellFilter: 'zip'
            }, {
                field: 'destination',
                displayName: 'Destination',
                width: '14%',
                cellFilter: 'zip'
            }, {
                field: 'carrierName',
                displayName: 'Carrier',
                width: '14%'
            }, {
                field: 'estimatedTransitTime',
                displayName: 'Transit Est Days',
                width: '5%',
                cellFilter: 'minutesTime:true'
            }, {
                field: 'weight',
                displayName: 'Weight',
                width: '5%',
                cellFilter: "appendSuffix:'Lbs'",
                searchable: false
            }, {
                field: 'commodityClass',
                displayName: 'Class',
                width: '4%',
                searchable: false,
                cellFilter: 'commodityClass'
            }, {
                field: 'shipperBaseRate',
                displayName: 'Base Rate',
                width: '6%',
                cellFilter: 'plsCurrency',
                searchable: false,
                visible: $scope.authData.customerUser
            }, {
                field: 'carrierCost',
                displayName: 'Carrier Cost',
                width: '7%',
                cellFilter: 'plsCurrency',
                searchable: false,
                visible: $scope.authData.plsUser
            }, {
                field: 'customerRevenue',
                displayName: (($scope.authData.plsUser) ? 'Customer Revenue' : 'Total' ),
                width: '7%',
                cellFilter: 'plsCurrency',
                searchable: false
            }, {
                field: 'pricingProfileId',
                displayName: 'Profile ID',
                width: '4%',
                visible: $scope.authData.plsUser
            }, {
                field: 'loadId',
                displayName: 'Load ID',
                width: '4%',
                cellTemplate : '<div class="ngCellText" data-ng-class="col.colIndex()" data-ng-show="row.entity.loadId===\'Multiple\'">'
                + '<a href="" data-ng-click="openLoadIdDialog(row.entity.id)">{{row.getProperty(col.field)}}</a></div>'
                + '<div class="ngCellText" data-ng-class="col.colIndex()" data-ng-hide="row.entity.loadId===\'Multiple\'">'
                + '{{row.getProperty(col.field)}}</div>'
            }],
            action: function (entity) {
                showSavedQuoteDetailsDialog(entity);
            },
            plugins: [
                NgGridPluginFactory.plsGrid(),
                NgGridPluginFactory.progressiveSearchPlugin(),
                NgGridPluginFactory.tooltipPlugin(true),
                NgGridPluginFactory.actionPlugin()
            ],
            tooltipOptions: {
                url: 'pages/content/quotes/shipments-grid-tooltip.html',
                onShow: onShowTooltip
            },
            progressiveSearch: true
        };

        $scope.exportAllSavedQuotes = function (quotesGrid) {
            var fileFormat = 'SavedQuotes_Exportfile_{0,date,' + $scope.$root.exportFileNameDateFormat + '}.xlsx';
            var sheetName = "Saved Quotes";

            var savedQuoteEntities = _.map(quotesGrid.ngGrid.filteredRows, function (item) {
                return item.entity;
            });

            var columnNames = ExportDataBuilder.getColumnNames(quotesGrid);
            var footerData = [[]];
            var headerData = [[]];
            var exportData = ExportDataBuilder.buildExportData(quotesGrid, savedQuoteEntities, fileFormat, sheetName, footerData, headerData);

            ExportService.exportData(exportData);
        };

        $scope.openLoadIdDialog = function (savedQuoteId) {
            $scope.$broadcast('event:openLoadIdDialog', savedQuoteId);
        };

    }
]);
