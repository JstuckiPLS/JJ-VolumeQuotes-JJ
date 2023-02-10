angular.module('pls.controllers').controller('TrackingBoardHoldController', [
    '$scope', '$timeout', 'TrackingBoardService', 'NgGridPluginFactory', '$rootScope',
    function ($scope, $timeout, TrackingBoardService, NgGridPluginFactory, $rootScope) {
        'use strict';

        $scope.shipments = [];
        $scope.selectedShipments = [];
        $scope.personId = $scope.$root.authData.personId;

        $scope.getShipments = function () {
            TrackingBoardService.getHoldShipments({}, function (data) {
                $scope.shipments = data;
            });
        };

        $scope.getShipments();

        $timeout(function() {
            document.getElementsByClassName('search-query')[0].focus();
        });

        $scope.closeHandler = function () {
            $scope.getShipments();
            $scope.selectedShipments.length = 0;
        };

        $scope.viewShipmentDetails = function (entity) {
                $scope.openEditSalesOrderDialog({
                    shipmentId: entity.shipmentId,
                    formDisabled: true,
                    closeHandler: $scope.closeHandler,
                    isBillingStatusNone: (entity.billingStatus === 'NONE')
                });
        };

        $scope.openTerminalInfoModalDialog = function (shipmentId) {
            $scope.$broadcast('event:openTerminalInfoDialog', shipmentId);
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

        var gridColumns = [{
            field: 'numberOfNotes',
            displayName: 'Notes',
            showTooltip: true,
            searchable: false,
            cellTemplate: '<div class="ngCellText" data-ng-if="row.entity.noteComment"><i '
            + 'class="fa fa-sticky-note-o" style="background-color:yellow;">'
            + '</i>{{" " + row.entity.numberOfNotes}}</div>',
            exportTemplate: 'exportEntity.noteComment ? ' +
            ' "Number of notes: " + exportEntity.numberOfNotes + " Last note: " + exportEntity.noteComment + "" : " "'
        }, {
            field: 'indicators',
            displayName: 'Indicators',
            cellTemplate: 'pages/cellTemplate/indicators-tooltip-cell.html',
            plsHideColumn:true
        }, {
            field: 'shipmentId',
            displayName: 'Load ID',
            plsHideColumn:true
        }, {
            field: 'bolNumber',
            displayName: 'Bol'
        }, {
            field: 'pieces',
            displayName: 'QTY'
        }, {
            field: 'weight',
            displayName: 'Total Weight'
        }, {
            field: 'soNumber',
            displayName: 'SO #',
            plsHideColumn:true
        }, {
            field: 'glNumber',
            displayName: 'GL #',
            plsHideColumn:true
        }, {
            field: 'proNumber',
            displayName: 'Pro #'
        }, {
            field: 'refNumber',
            displayName: 'Shipper Ref #',
            plsHideColumn:true
        }, {
            field: 'poNumber',
            displayName: 'PO #',
            plsHideColumn:true
        }, {
            field: 'puNumber',
            displayName: 'PU #',
            plsHideColumn:true
        }, {
            field: 'jobNumber',
            displayName: 'Job #',
            plsHideColumn:true
        }, {
            field: 'shipper',
            displayName: 'Shipper'
        },{
            field: 'originAddress',
            displayName: 'Origin'
        }, {
            field: 'consignee',
            displayName: 'Consignee'
        }, {
            field: 'destinationAddress',
            displayName: 'Destination'
        }, {
            field: 'carrier',
            displayName: 'Carrier',
            cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
            '<a href="" data-ng-click="openTerminalInfoModalDialog(row.entity.shipmentId)">{{row.getProperty(col.field)}}</a>' +
            '</div>'
        }, {
            field: 'pickupDate',
            displayName: 'Pickup Date',
            cellFilter: "date:wideAppDateFormat"
        }, {
            field: 'deliveryDate',
            displayName: 'Del. Date',
            cellFilter: "date:wideAppDateFormat"
        }, {
            field : 'revenue',
            displayName : 'Revenue',
            cellFilter: 'plsCurrency'
        }, {
            field: 'cost',
            displayName: 'Cost',
            cellFilter: 'plsCurrency'
        }, {
            field: 'margin',
            displayName: 'Margin',
            cellFilter: 'plsCurrency'
        }, {
            field: 'status',
            displayName: 'Op Status',
            cellFilter: 'shipmentStatus'
        }];

        $scope.shipmentsGrid = {
            enableColumnResize: true,
            data: 'shipments',
            multiSelect: false,
            selectedItems: $scope.selectedShipments,
            columnDefs: gridColumns,
            action: function (entity) {
                if ($scope.$root.isFieldRequired('BOARD_CAN_VIEW_SALES_ORDER')) {
                    $scope.viewShipmentDetails(entity);
                }
            },
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            tooltipOptions: {
                url: 'pages/content/financialBoard/invoice-audit-grid-tooltip.html',
                onShow: onShowTooltip,
                showIf: isCommentAvailable
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), 
                      NgGridPluginFactory.actionPlugin(), NgGridPluginFactory.tooltipPlugin(true), NgGridPluginFactory.hideColumnPlugin()],
            progressiveSearch: true
        };

        if ($scope.$root.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')) {
            gridColumns.splice(9, 0, {
                field: 'network',
                displayName: 'Business Unit'
            });
        }
        gridColumns.splice(10, 0, {
            field: 'accountExecutive',
            displayName: 'Account Executive'
        });
    }
]);