angular.module('pls.controllers').controller('FinancialBoardAuditController', ['$scope', 'FinancialBoardService', 'NgGridPluginFactory',
    function ($scope, FinancialBoardService, NgGridPluginFactory) {
        'use strict';

        $scope.shipments = [];
        $scope.selectedShipments = [];
        $scope.isCommentAvailable = false;

        $scope.loadAuditData = function () {
            FinancialBoardService.lowBenefit({}, function (data) {
                $scope.shipments = data;
                $scope.selectedShipments.length = 0;
            });
        };

        $scope.closeHandler = function () {
            $scope.loadAuditData();
            $scope.selectedShipments.length = 0;
        };

        $scope.exportInvoices = function () {
            $scope.$emit('event:exportInvoices', {
                sheetName: 'Audit Invoices',
                grid: $scope.shipmentsGrid,
                selectedRows: $scope.selectedShipments,
                fileName: 'Audit_Invoices_'
            });
        };

        $scope.editShipment = function (selectedTab) {
            $scope.$broadcast('event:showEditSalesOrder', {
                shipmentId: $scope.selectedShipments[0].loadId, closeHandler: $scope.closeHandler,
                isUnavailableCancel: false, isUnavailableCopy: false, selectedTab: selectedTab
            });
        };

        $scope.approve = function () {
            $scope.selectRebillAdjustment($scope.selectedShipments, $scope.shipments);
            FinancialBoardService.approveAudit({
                auditRecords: $scope.fillSelectedShipmentsForBusinessObjects($scope.selectedShipments)
            }, function (response) {
                if (response && response.data && response.data.length) {
                    $scope.$root.$emit('event:application-error', 'Approving failed!', response.data);
                } else {
                    $scope.loadAuditData();
                }
            }, function (result) {
                if (result.data && result.data.message) {
                    $scope.$root.$emit('event:application-error', 'Approving failed!', result.data.message);
                } else {
                    $scope.$root.$emit('event:application-error', 'Approving of shipment failed!',
                            'Can not approve shipment with ID:' + $scope.selectedShipments[0].loadId);
                }

                $scope.selectedShipments.length = 0;
            });
        };

        var onShowTooltip = function (scope, entity) {
            scope.tooltipData = {
                noteCreatedDate: entity.noteCreatedDate,
                noteModifiedBy: entity.noteModifiedBy,
                noteComment: entity.noteComment
            };
        };

        function isCommentAvailable(entity) {
            return !_.isEmpty(entity.noteComment);
        }

        $scope.shipmentsGrid = {
            enableColumnResize: true,
            data: 'shipments',
            multiSelect: true,
            selectedItems: $scope.selectedShipments,
            columnDefs: [
                {
                    field: 'diffDays',
                    displayName: 'Days in Audit',
                    width: '3%'
                },
                {
                    field: 'invoiceType',
                    width: '3%',
                    displayName: 'Inv. Type',
                    cellFilter: 'contractionInvoiceType'
                },
                {
                    field: 'loadId',
                    displayName: 'Load ID',
                    width: '5%'
                },
                {
                    field: 'bol',
                    displayName: 'BOL',
                    width: '6%'
                },
                {
                    field: 'po',
                    displayName: 'PO#',
                    width: '6%'
                },
                {
                    field: 'pro',
                    displayName: 'PRO#',
                    width: '6%'
                },
                {
                    field: 'networkName',
                    displayName: 'Business Unit',
                    width: '5%'
                },
                {
                    field: 'customerName',
                    displayName: 'Customer',
                    width: '9%'
                },
                {
                    field: 'carrierName',
                    displayName: 'Carrier',
                    width: '9%'
                },
                {
                    field: 'revenue',
                    displayName: 'Revenue',
                    width: '5%',
                    cellFilter: 'plsCurrency'
                },
                {
                    field: 'cost',
                    displayName: 'Cost',
                    width: '5%',
                    cellFilter: 'plsCurrency'
                },
                {
                    field: 'margin',
                    displayName: 'Margin',
                    cellFilter: 'appendSuffix:"%"',
                    width: '5%'
                },
                {
                    field: 'vendorBillAmount',
                    displayName: 'Total VB Amt',
                    width: '5%',
                    cellFilter: 'plsCurrency'
                },
                {
                    field: 'deliveryDate',
                    displayName: 'Del. Date',
                    cellFilter: 'date:wideAppDateFormat',
                    width: '6%'
                },
                {
                    field: 'numberOfNotes',
                    displayName: 'Notes',
                    showTooltip: true,
                    searchable: false,
                    width: '3%',
                    cellTemplate: '<div class="ngCellText" data-ng-if="row.entity.noteComment"><i '
                    + 'class="fa fa-sticky-note-o" style="background-color:yellow;">'
                    + '</i>{{" " + row.entity.numberOfNotes}}</div>',
                    exportTemplate: 'exportEntity.noteComment ? ' +
                    '"Number of notes: " + exportEntity.numberOfNotes + " Last note: " + exportEntity.noteComment + "" : " "'
                },
                {
                    displayName: 'Reason',
                    width: '9%',
                    field: 'reason'
                },
                {
                    field: 'accExecName',
                    displayName: 'Account Exec.',
                    width: '9%'
                }
            ],
            action: function () {
                $scope.editShipment();
            },
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            beforeSelectionChange: function(rowItem, event) {
                if (!event.ctrlKey && !event.shiftKey &&  $scope.shipmentsGrid.multiSelect 
                                && !$(event.srcElement).is(".ngSelectionCheckbox")) {
                    angular.forEach($scope.shipments , function(loads, index) { 
                        $scope.shipmentsGrid.selectRow(index, false);
                    });
                }
                return true;
            },
            tooltipOptions: {
                url: 'pages/content/financialBoard/invoice-audit-grid-tooltip.html',
                onShow: onShowTooltip,
                showIf: isCommentAvailable
            },
            sortInfo: {
                fields: ['diffDays'],
                directions: ['desc']
            },
            plugins: [
                NgGridPluginFactory.plsGrid(),
                NgGridPluginFactory.progressiveSearchPlugin(),
                NgGridPluginFactory.tooltipPlugin(true),
                NgGridPluginFactory.actionPlugin(),
                NgGridPluginFactory.hideColumnPlugin()
            ],
            progressiveSearch: true
        };

        $scope.recordsUpdate = function () {
            $scope.gridRecords = $scope.shipmentsGrid.ngGrid.filteredRows.length;
        };

        $scope.$watch('shipmentsGrid.ngGrid.filteredRows.length', $scope.recordsUpdate);

        $scope.sendToPriceAudit = function () {
            $scope.isInvioceAudit = false;
            $scope.selectRebillAdjustment($scope.selectedShipments, $scope.shipments);
            $scope.sendToAudit();
        };

        $scope.readyForConsolidated = function() {
            FinancialBoardService.readyForConsolidated($scope.fillSelectedShipmentsForBusinessObjects($scope.selectedShipments),
                    $scope.loadAuditData);
        };

        $scope.sendToAudit = function () {
            $scope.$broadcast('event:sendToAudit', {
                auditRecords: $scope.fillSelectedShipmentsForBusinessObjects($scope.selectedShipments),
                isInvioceAudit: $scope.isInvioceAudit
            });
        };

        $scope.$on('event:updateDataAfterSendToAudit', function (event, options) {
            $scope.loadAuditData();
        });

        $scope.isReadyForConsolidatedEnabled = function() {
            return $scope.selectedShipments.length === 0 || !_.every($scope.selectedShipments, function(selectedShipment) {
                return selectedShipment.invoiceType === 'CBI';
            });
        };
    }
]);