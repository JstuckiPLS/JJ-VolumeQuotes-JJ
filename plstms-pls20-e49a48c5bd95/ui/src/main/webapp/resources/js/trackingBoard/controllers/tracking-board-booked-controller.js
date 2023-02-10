angular.module('pls.controllers').controller('TrackingBoardBookedController', [
    '$scope', '$rootScope', '$timeout', 'TrackingBoardService', 'ShipmentStatus', 'NgGridPluginFactory',
    function ($scope, $rootScope, $timeout, TrackingBoardService, ShipmentStatus, NgGridPluginFactory) {
        'use strict';

        $scope.shipments = [];
        $scope.selectedShipments = [];

        $scope.init = function () {
            $scope.loadBookedShipments();
        };

        $scope.closeHandler = function () {
            $scope.loadBookedShipments();
            $scope.selectedShipments.length = 0;
        };

        $scope.loadBookedShipments = function () {
            TrackingBoardService.booked({}, function (data) {
                $scope.shipments = data;
            });
        };

        $scope.viewShipment = function () {
            $scope.viewShipmentDetails($scope.selectedShipments[0]);
        };

        $scope.isDisabled = function () {
           return ($scope.selectedShipments.length !== 1 || 
            ($scope.selectedShipments[0].status === 'PENDING_PAYMENT' && 
            ($scope.selectedShipments[0].prepaidTotalAmount - $scope.selectedShipments[0].revenue) < 0 ));
        };

        $scope.viewShipmentDetails = function (entity) {
            $scope.openEditSalesOrderDialog({shipmentId: entity.id, closeHandler: $scope.closeHandler});
        };

        $scope.openTerminalInfoModalDialog = function (shipmentId) {
            $scope.$broadcast('event:openTerminalInfoDialog', shipmentId);
        };

        $timeout(function() {
            document.getElementsByClassName('search-query')[0].focus();
        });

        function dispatch() {
            ShipmentStatus.dispatch({shipmentId: $scope.selectedShipments[0].id}, function () {
                $scope.$root.$emit('event:operation-success', 'Dispatch shipment', 'Shipment was dispatched successfully.');
                $scope.loadBookedShipments();
            }, function (result) {
                var errMessage = 'Cannot dispatch shipment. ';

                if (result.data && result.data.message) {
                    errMessage = errMessage + result.data.message;
                }

                $scope.$root.$emit('event:application-error', 'Dispatch shipment', errMessage);
            });
        }

        $scope.dispatchShipment = function () {
            if ($scope.selectedShipments[0].apiCapable) {
                $scope.$root.$emit('event:application-warning', 'Dispatch shipment', 'Shipment can\'t be dispatched automatically. ');
            } else {
                dispatch();
            }
        };

        $scope.getRowFontStyle = function (entity) {
            return entity.apiCapable ? 'normal' : 'bold';
        };

        $scope.shipmentsGrid = {
            enableColumnResize: true,
            data: 'shipments',
            multiSelect: false,
            selectedItems: $scope.selectedShipments,
            rowTemplate: "<div data-ng-style=\"{ 'cursor': row.cursor, 'font-weight': getRowFontStyle(row.entity)  }\" data-ng-repeat=\"col " +
            "in renderedColumns\" ng-class=\"col.colIndex()\" class=\"ngCell {{col.cellClass}}\" ng-cell></div>",
            columnDefs: [{
                field: 'indicators',
                displayName: 'Indicators',
                width: '3%',
                cellTemplate: 'pages/cellTemplate/indicators-tooltip-cell.html'
            }, {
                field: 'accountExecutive',
                displayName: 'Account Executive',
                width: '5%'
            }, {
                field: 'id',
                displayName: 'Load ID',
                width: '4%'
            }, {
                field: 'bol',
                displayName: 'BOL',
                width: '6%',
                cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
                '<div class="span8">{{row.getProperty(col.field)}}</div>'+
                '<div class="span4" data-ng-if="row.entity.status == \'PENDING_PAYMENT\'">'+
                '<img src="resources/img/PLSPay.png"/></div>'+
                '</div>'
            }, {
                field: 'pieces',
                displayName: 'QTY',
                width: '4%'
            }, {
                field: 'weight',
                displayName: 'Total Weight',
                width: '4%'
            }, {
                field: 'customerName',
                displayName: 'Customer',
                width: '6%'
            }, {
                field: 'carrierName',
                displayName: 'Carrier',
                width: '7%',
                cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
                '<a href="" data-ng-click="openTerminalInfoModalDialog(row.entity.id)">{{row.getProperty(col.field)}}</a>' +
                '</div>'
            }, {
                field: 'integrationType',
                displayName: 'EDI',
                width: '5%',
                cellFilter: "integrationTypeFilter"
            }, 
            {
                field: 'pickupDate',
                displayName: 'Pickup Date',
                width: '7%',
                cellFilter: "date:wideAppDateFormat"
            }, {
                field: 'shipper',
                displayName: 'Shipper',
                width: '9%'
            }, {
                field: 'origin',
                displayName: 'Origin',
                width: '8%'
            }, {
                field: 'consignee',
                displayName: 'Consignee',
                width: '8%'
            }, {
                field: 'destination',
                displayName: 'Destination',
                width: '8%'
            }, {
                field: 'createdBy',
                displayName: 'Created By',
                width: '5%'
            }, {
                field: 'createdDate',
                displayName: 'Created Date',
                width: '5%',
                cellFilter: "date:appDateFormat"
            }, {
                field: 'pickupWindowEnd',
                displayName: 'Late Pickup Date/Time',
                width: '5%',
                cellFilter: "date:appDateTimeFormat"
            }],
            action: function (entity) {
                if ($scope.$root.isFieldRequired('BOARD_CAN_EDIT_SALES_ORDER')) {
                    $scope.viewShipment(entity);
                }
            },
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin(),
                      NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.hideColumnPlugin()],
            progressiveSearch: true
        };

        if ($rootScope.isFieldRequired('CAN_VIEW_BUSINESS_UNIT')) {
            $scope.shipmentsGrid.columnDefs.splice(1, 0, {
                field: 'network',
                displayName: 'Business Unit',
                width: '4%'
            });
            $scope.shipmentsGrid.columnDefs[12].width = '7%';
            $scope.shipmentsGrid.columnDefs[14].width = '7%';
            $scope.shipmentsGrid.columnDefs[15].width = '4%';
            $scope.shipmentsGrid.columnDefs[16].width = '4%';
        }
    }
]);