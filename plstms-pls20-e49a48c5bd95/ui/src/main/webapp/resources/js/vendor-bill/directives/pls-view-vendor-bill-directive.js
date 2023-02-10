/**
 * AngularJS directive which displays vendor bill information with possibility to detach it or add new one.
 *
 * @author: Sergey Kirichenko
 * Date: 19.03.14
 * Time: 11:18
 */
angular.module('plsApp').directive('plsViewVendorBill', ['NgGridPluginFactory', 'ProductTotalsService',
    function (NgGridPluginFactory, ProductTotalsService) {
    return {
        restrict: 'A',
        scope: {
            vendorBillModel: '=plsViewVendorBill',
            customerName: '=customerName'
        },
        templateUrl: 'pages/tpl/view-vendor-bill-tpl.html',
        compile: function () {
            return {
                pre: function (scope) {
                    'use strict';

                    scope.lineItemsGridOptions = {
                        data: 'vendorBillModel.vendorBill.lineItems',
                        enableRowSelection: false,
                        enableSorting: false,
                        columnDefs: 'lineItemGridColumnModel',
                        plugins: [NgGridPluginFactory.plsGrid()],
                        enableColumnResize: true
                    };

                    scope.$watch('vendorBillModel.vendorBill.lineItems', function(newVal) {
                        if (newVal) {
                            scope.totals = ProductTotalsService.calculateTotals(newVal);
                        }
                    });

                    scope.costItemsGridOptions = {
                        data: 'vendorBillModel.vendorBill.costItems',
                        enableRowSelection: false,
                        enableSorting: false,
                        columnDefs: [{
                            field: 'refType', displayName: 'Description', width: '59%', cellClass: 'text-left', headerClass: 'text-left',
                            cellFilter: 'refCodeAndDesc'
                        }, {
                            field: 'subTotal', displayName: 'Cost', width: '40%', cellClass: 'text-center', headerClass: 'text-center',
                            cellFilter: 'plsCurrency'
                        }
                        ],
                        plugins: [NgGridPluginFactory.plsGrid()],
                        enableColumnResize: true
                    };
                },
                post: function (scope) {
                    'use strict';

                    scope.lineItemGridColumnModel = [];

                    scope.$watch('vendorBillModel.vendorBill', function (newValue) {
                        if (newValue) {
                            var showCostColumn = newValue.edi === true;

                            scope.lineItemGridColumnModel = [
                                {field: 'self', displayName: 'Weight', cellClass: 'text-center', width: '13%', cellFilter: 'materialWeight'},
                                {
                                    field: 'commodityClass', cellClass: 'text-center', displayName: 'Class', width: '11%',
                                    cellFilter: 'commodityClass'
                                },
                                {field: 'productDescription', cellClass: 'text-center', displayName: 'Description', width: '30%'},
                                {field: 'nmfc', cellClass: 'text-center', displayName: 'NMFC', width: '11%'},
                                {field: 'quantity', cellClass: 'text-center', displayName: 'Qty', width: '8%'},
                                {
                                    field: 'packageType', displayName: 'Pack. Type', cellClass: 'text-center', width: '13%',
                                    cellFilter: 'packageType'
                                },
                                {
                                    field: 'cost', cellClass: 'text-center', cellFilter: 'plsCurrency', displayName: 'Cost', width: '13%',
                                    visible: showCostColumn
                                }
                            ];
                        }
                    });
                }
            };
        }
    };
}]);