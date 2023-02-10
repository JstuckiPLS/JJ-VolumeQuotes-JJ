angular.module('pls.controllers').controller('FinancialBoardController', ['$scope', '$http', '$templateCache', 'ExportDataBuilder', 'ExportService',
    function ($scope, $http, $templateCache, ExportDataBuilder, ExportService) {
        'use strict';

        angular.forEach([
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

        $scope.$on('event:exportInvoices', function (event, options) {
            var fileFormat = options.fileName + '{0,date,' + $scope.$root.exportFileNameDateFormat + '}.xlsx';
            var invoiceEntities;

            if (options.selectedRows && options.selectedRows.length > 0) {
                invoiceEntities = options.selectedRows;
            } else {
                invoiceEntities = _.map(options.grid.ngGrid.filteredRows, function (item) {
                    return item.entity;
                });
            }

            var exportData = ExportDataBuilder.buildExportData(options.grid, invoiceEntities, fileFormat, options.sheetName);

            ExportService.exportData(exportData);
        });

        $scope.fillSelectedShipmentsForBusinessObjects = function (selectedInvoices) {
            var selectedLoads = [];

            _.each(selectedInvoices, function (invoice) {
                var load = {};

                if (!_.isUndefined(invoice.id)) {
                    load.loadId = invoice.id;
                } else if (!_.isUndefined(invoice.loadId)) {
                    load.loadId = invoice.loadId;
                }

                load.adjustmentId = invoice.adjustmentId;
                selectedLoads.push(load);
            });

            return selectedLoads;
        };

        $scope.isSelectedRebillAdjustment = function (selectedInvoices) {
            return !_.isEmpty(selectedInvoices) && _.some(selectedInvoices, function(invoice) {
                return invoice.rebill === true;
            });
        };

        /**
         * Returns only those rebill adjustments which don't have pair.
         */
        $scope.getSelectedUniqueRebillAdjustments = function(invoices) {
            return _.filter(invoices, function (invoice) {
                return invoice.rebill && _.filter(invoices, {loadId: invoice.loadId}).length === 1;
            });
        };

        $scope.selectRebillAdjustment = function(selectedInvoices, invoices) {
            if ($scope.isSelectedRebillAdjustment(selectedInvoices)) {
                // if selected invoices contains first part of rebill adj we also add second part of rebill adj
                _.each(selectedInvoices, function (item) {
                    var rebillAdj = _.find(invoices, function(invoice) {
                        return invoice.loadId === item.loadId && item.rebill === true && !_.contains(selectedInvoices, invoice);
                    });
                    if (rebillAdj) {
                        selectedInvoices.push(rebillAdj);
                    }
                });
            }
        };
    }
]);