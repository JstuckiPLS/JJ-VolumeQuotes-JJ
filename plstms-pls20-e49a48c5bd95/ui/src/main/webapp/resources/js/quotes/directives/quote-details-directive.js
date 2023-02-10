angular.module('plsApp').directive('plsQuoteDetails', ['NgGridPluginFactory', 'ShipmentUtils', 'ProductTotalsService',
    function (NgGridPluginFactory, ShipmentUtils, ProductTotalsService) {
        return {
            restrict: 'A',
            scope: {
                shipment: '=plsQuoteDetails'
            },
            templateUrl: 'pages/tpl/quote-details-tpl.html',
            compile: function () {
                return {
                    pre: function (scope) {
                        'use strict';

                        scope.hazmatInfo = {};

                        function isHazmat(entity) {
                            return entity.hazmat;
                        }

                        function calculateTotals() {
                            scope.totals = ProductTotalsService.calculateTotals(scope.shipment.finishOrder.quoteMaterials);
                        }

                        function onShowTooltip(row) {
                            if (row.entity.id || row.entity.productId) {
                                scope.hazmatInfo.exist = true;
                                scope.hazmatInfo.company = row.entity.emergencyResponseCompany;

                                scope.hazmatInfo.phone = {
                                    emergencyResponsePhoneCountryCode: row.entity.emergencyResponsePhoneCountryCode,
                                    emergencyResponsePhoneAreaCode: row.entity.emergencyResponsePhoneAreaCode,
                                    emergencyResponsePhone: row.entity.emergencyResponsePhone,
                                    emergencyResponsePhoneExtension: row.entity.emergencyResponsePhoneExtension
                                };

                                scope.hazmatInfo.contractNum = row.entity.emergencyResponseContractNumber;
                                scope.hazmatInfo.unNum = row.entity.unNum;
                                scope.hazmatInfo.packingGroup = row.entity.packingGroup;
                            } else {
                                scope.hazmatInfo.exist = false;
                            }

                            scope.itemsGridOptions.tooltipOptions.url = 'pages/content/quotes/hazmat-info-tooltip.html';
                        }

                        scope.itemGridColumnModel = [];

                        scope.itemsGridOptions = {
                            data: 'shipment.finishOrder.quoteMaterials',
                            enableRowSelection: false,
                            enableSorting: false,
                            columnDefs: 'itemGridColumnModel',
                            enableColumnResize: true,
                            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.tooltipPlugin(true)],
                            tooltipOptions: {
                                showIf: isHazmat,
                                onShow: onShowTooltip
                            }
                        };

                        scope.initGridOptions = function (gridOptions, columnsConfiguration) {
                            return scope.$root.initGridOptions(gridOptions, columnsConfiguration);
                        };

                        scope.$watch('shipment', function () {
                            if (scope.shipment.originDetails) {
                                scope.selectedPickupAccessorials = ShipmentUtils.getAccessorialsNames(scope.shipment.originDetails.accessorials);
                            } else {
                                scope.selectedPickupAccessorials = '';
                            }

                            if (scope.shipment.destinationDetails) {
                                scope.selectedDeliveryAccessorials =
                                        ShipmentUtils.getAccessorialsNames(scope.shipment.destinationDetails.accessorials);
                            } else {
                                scope.selectedDeliveryAccessorials = '';
                            }
                            calculateTotals();
                        });
                    },
                    post: function (scope) {
                        'use strict';

                        scope.itemGridColumnModel = [{
                            field: 'self',
                            displayName: 'Weight',
                            minWidth: 60,
                            width: '6%',
                            cellClass: 'text-center',
                            referenceId: 'weightColumn',
                            headerClass: 'text-center',
                            cellFilter: 'materialWeight'
                        }, {
                            field: 'commodityClass', displayName: 'Class', minWidth: 80, width: '7%', cellClass: 'text-center',
                            headerClass: 'text-center', cellFilter: 'commodityClass', referenceId: 'classColumn'
                        }, {
                            field: 'productDescription', displayName: 'Product Description', minWidth: 170, width: '17%',
                            cellClass: 'text-center', headerClass: 'text-center', referenceId: 'productColumn'
                        }, {
                            field: 'productCode', displayName: 'SKU/Product Code', minWidth: 140, width: '10%', cellClass: 'text-center',
                            headerClass: 'text-center', referenceId: 'productCodeColumn'
                        }, {
                            field: 'nmfc', displayName: 'NMFC', minWidth: 140, width: '7%', cellClass: 'text-center',
                            headerClass: 'text-center', referenceId: 'nmfcColumn'
                        }, {
                            field: 'self', displayName: 'Dimensions', minWidth: 125, width: '12%', cellClass: 'text-center',
                            headerClass: 'text-center', cellFilter: 'materialDimension', referenceId: 'dimensionsColumn'
                        }, {
                            field: 'quantity', displayName: 'Qty', minWidth: 35, width: '4%', cellClass: 'text-center',
                            headerClass: 'text-center', referenceId: 'quantityColumn'
                        }, {
                            field: 'packageType', displayName: 'Pack. Type', minWidth: 120, width: '11%', cellClass: 'text-center',
                            headerClass: 'text-center', cellFilter: 'packageType', referenceId: 'packageTypeColumn'
                        }, {
                            field: 'pieces', displayName: 'Pieces', minWidth: 35, width: '5%', cellClass: 'text-center',
                            headerClass: 'text-center', referenceId: 'piecesColumn'
                        }, {
                            field: 'stackable', displayName: 'Stackable', minWidth: 70, width: '6%',
                            cellTemplate: 'pages/cellTemplate/checked-cell.html',
                            referenceId: 'stackableColumn'
                        }, {
                            field: 'hazmat', displayName: 'Hazmat', minWidth: 60, width: '5%', headerClass: 'text-center',
                            showTooltip: true, cellTemplate: '<div class="ngCellText text-center">' +
                            '<i class="icon-warning-sign" data-ng-show="row.getProperty(col.field)"></i>' +
                            '</div>', referenceId: 'hazmatColumn'
                        }, {
                            field: 'hazmatClass', displayName: 'Hazmat Class', minWidth: 105, width: '9%', cellClass: 'text-center',
                            headerClass: 'text-center', referenceId: 'hazmatClassColumn'
                        }
                        ];
                    }
                };
            }
        };
    }
]);