angular.module('pls.controllers').controller('FinancialBoardPriceController', ['$scope', 'FinancialBoardService', 'NgGridPluginFactory',
    function ($scope, FinancialBoardService, NgGridPluginFactory) {
        'use strict';

        $scope.shipments = [];
        $scope.selectedShipments = [];
        $scope.isCommentAvailable = false;


        $scope.initPriceAudit = function () {
            $scope.loadPriceAuditData();
        };

        $scope.loadPriceAuditData = function () {
            FinancialBoardService.priceAudit({}, function (data) {
                $scope.shipments = data;
                $scope.selectedShipments.length = 0;
            });
        };

        $scope.closeHandler = function () {
            $scope.loadPriceAuditData();
            $scope.selectedShipments.length = 0;
        };

        $scope.exportInvoices = function () {
            $scope.$emit('event:exportInvoices', {
                sheetName: 'Price Invoices',
                grid: $scope.shipmentsGrid,
                selectedRows: true,
                fileName: 'Price_Invoices_'
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
                    $scope.loadPriceAuditData();
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
            selectedItems: $scope.selectedShipments,
            multiSelect: true,
            columnDefs: [
                 {
                     field: 'diffDays',
                     displayName: 'Days on Hold',
                     width: '6%'
                 },
                {
                    field: 'loadId',
                    displayName: 'Load ID',
                    width: '7%'
                },
                {
                    field: 'customerName',
                    displayName: 'Customer',
                    width: '9%'
                },
                {
                    field: 'bol',
                    displayName: 'BOL',
                    width: '10%'
                },
                {
                    field: 'pro',
                    displayName: 'Pro #',
                    width: '9%'
                },
                {
                    field: 'deliveryDate',
                    displayName: 'Actual Delivery Date',
                    cellFilter: 'date:wideAppDateFormat',
                    width: '7%'
                },
                {
                    field: 'scac',
                    displayName: 'SCAC',
                    width: '5%'
                },
                {
                    field: 'networkName',
                    displayName: 'Business Unit',
                    width: '5%'
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
                    width: '6%',
                    field: 'reason'
                },
                {
                    field: 'priceAuditDate',
                    displayName: 'Billing Hold Date',
                    cellFilter: 'date:wideAppDateFormat',
                    width: '7%'
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
                    width: '6%',
                    cellFilter: 'appendSuffix:"%"'
                },
                {
                    field: 'vendorBillAmount',
                    displayName: 'VB Amount',
                    width: '7%',
                    cellFilter: 'plsCurrency'
                },
                {
                    field: 'accExecName',
                    displayName: 'Account Executive',
                    width: '8%'
                }
            ],
            action: function () {
                $scope.editShipment();
            },
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            beforeSelectionChange: function (rowItem, event) {
                if (!event.ctrlKey && !event.shiftKey && $scope.shipmentsGrid.multiSelect && !$(event.srcElement).is(".ngSelectionCheckbox")) {
                    angular.forEach($scope.shipments, function (shipment, index) {
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
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.tooltipPlugin(true),
                NgGridPluginFactory.actionPlugin(), NgGridPluginFactory.hideColumnPlugin()],
            progressiveSearch: true
        };

        $scope.recordsUpdate = function () {
            $scope.gridRecords = $scope.shipmentsGrid.ngGrid.filteredRows.length;
        };

        $scope.sendToInvoiceAudit = function () {
            $scope.isInvioceAudit = true;
            $scope.selectRebillAdjustment($scope.selectedShipments, $scope.shipments);
            $scope.sendToAudit();
        };

        $scope.sendToAudit = function () {
            $scope.$broadcast('event:sendToAudit', {
                auditRecords: $scope.fillSelectedShipmentsForBusinessObjects($scope.selectedShipments),
                isInvioceAudit: $scope.isInvioceAudit
            });
        };

        $scope.showCloseLoadBox = function () {
            if (_.isUndefined($scope.selectedShipments[0].adjustmentId)) {
                $scope.$broadcast('event:closeLoad', {loadId: $scope.selectedShipments[0].loadId});
            }
        };

        $scope.$watch('shipmentsGrid.ngGrid.filteredRows.length', $scope.recordsUpdate);

        $scope.isAdjustment = function () {
            return $scope.selectedShipments[0] && !_.isUndefined($scope.selectedShipments[0].adjustmentId);
        };

        $scope.$on('event:updateDataAfterSendToAudit', function () {
            $scope.selectedShipments.length = 0;
            $scope.loadPriceAuditData();
        });

        $scope.$on('event:updatePriceAuditData', function () {
            $scope.selectedShipments.length = 0;
            $scope.loadPriceAuditData();
        });
    }
]);