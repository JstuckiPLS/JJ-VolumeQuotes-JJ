angular.module('pls.controllers').controller('SODataHistoryCtrl', [
    '$scope', '$filter', 'NgGridPluginFactory', 'ExportDataBuilder', 'ExportService', 'ShipmentDetailsService',
    function ($scope, $filter, NgGridPluginFactory, ExportDataBuilder, ExportService, ShipmentDetailsService) {
        'use strict';

        $scope.exportDataHistory = function () {
            var fileFormat = 'Shipment Audit ' + '{0,date,' + $scope.$root.exportFileNameDateFormat + '}.xlsx';
            var auditEntities = _.map($scope.shipmentDataHistoryGridOptions.ngGrid.filteredRows, function (item) {
                return item.entity;
            });
            var exportData = ExportDataBuilder.buildExportData($scope.shipmentDataHistoryGridOptions, auditEntities, fileFormat,
                'Shipment Audit');

            ExportService.exportData(exportData);
        };

        $scope.shipmentDataHistoryGridOptions = {
            data: 'shipmentDataHistoryGridData',
            columnDefs: [{
                field: 'group',
                displayName: '',
                width: '8%'
            }, {
                field: 'fieldName',
                displayName: 'Field Name',
                width: '15%'
            }, {
                field: 'plsQuoted',
                displayName: 'PLS Quoted',
                width: '15%'
            }, {
                field: 'plsCurrent',
                displayName: 'PLS Current',
                width: '15%'
            }, {
                field: 'vendorBill',
                displayName: 'Vendor Bill',
                width: '15%'
            }, {
                field: 'lastModified',
                displayName: 'Last Modified',
                width: '14%',
                cellFilter: 'date:appDateTimeFormat + " " + row.entity.timezoneCode'
            }, {
                field: 'modifiedBy',
                displayName: 'Modified By',
                width: '16%'
            }],
            rowTemplate: '<div style="height: 100%" ng-class="{selected: row.getProperty(\'selected\'), ngRow: true}">' +
            '<div ng-style="{ \'cursor\': row.cursor }" ng-repeat="col in renderedColumns" ng-class="col.colIndex()" class="ngCell">' +
            '<div class="ngVerticalBar" ng-style="{height: rowHeight}" ng-class="{ ngVerticalBarVisible: !$last }"> </div>' +
            '<div ng-cell></div></div></div>',
            plugins: [NgGridPluginFactory.plsGrid()],
            enableColumnResize: true,
            enableRowSelection: false,
            progressiveSearch: false
        };

        $scope.$on('event:edit-sales-order-tab-change', function (event, tabId) {
            if (tabId === 'data_history' && $scope.wizardData.shipment.id && !$scope.shipmentDataHistoryGridData) {
                ShipmentDetailsService.getShipmentDataHistory({
                    customerId: $scope.wizardData.shipment.organizationId,
                    shipmentId: $scope.wizardData.shipment.id
                }, function (data) {
                    _.each(data, function (item) {
                        item.selected = !(item.plsQuoted === item.plsCurrent && item.plsQuoted === item.vendorBill);
                    });

                    $scope.shipmentDataHistoryGridData = data;
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Shipment data history load failed!', 'Can\'t load shipment data history.');
                });
            }
        });
    }
]);