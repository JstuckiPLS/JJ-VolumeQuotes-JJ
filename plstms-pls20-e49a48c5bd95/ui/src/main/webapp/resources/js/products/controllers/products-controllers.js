var ProductListPrimarySortEnum = {
    PRODUCT_DESCRIPTION: "Product Description",
    SKU_PRODUCT_CODE: "SKU/Product Code"
};

angular.module('plsApp').controller('ProductsListCtrl', [
    '$scope', '$window', '$log', 'urlConfig', 'ProductService', 'CustomerOrderService', 'NgGridPluginFactory',
    function ($scope, $window, $log, urlConfig, ProductService, CustomerOrderService, NgGridPluginFactory) {
        'use strict';

        $scope.selectedProducts = [];
        $scope.sortOrdersValues = ProductListPrimarySortEnum;

        $scope.productListModel = {
            sortOrder: '',
            selectedCustomer: {
                id: undefined,
                name: undefined
            }
        };

        $scope.loadProductsData = function () {
            ProductService.list({customerId: $scope.productListModel.selectedCustomer.id}, function (data) {
                $scope.productsGridData = data;
            });
        };

        $scope.$watch('productListModel.selectedCustomer', function (newValue, oldValue) {
            if (newValue && (newValue !== oldValue)) {
                $scope.getProductListSortOrder();
                $scope.selectedProducts.length = 0;
                $scope.loadProductsData();
            } else if (!$scope.productListModel.selectedCustomer) {
                $scope.productsGridData = [];
                $scope.selectedProducts.length = 0;
            }
        });

        function openAddEditProductDialog(productId) {
            var transferObject = {
                productId: productId,
                customerName: $scope.productListModel.selectedCustomer.name,
                closeHandler: function (product) {
                    $scope.selectedProducts[0] = product;
                    $scope.loadProductsData();
                }
            };

            $scope.$broadcast('event:showAddEditProduct', transferObject);
        }

        $scope.productsGrid = {
            sort: '',
            filter: '',
            options: {
                data: 'productsGridData',
                selectedItems: $scope.selectedProducts,
                primaryKey: 'id',
                columnDefs: [
                    {
                        field: 'description',
                        displayName: 'Product Description',
                        width: '30%'
                    },
                    {
                        field: 'self',
                        displayName: 'NMFC #',
                        width: '20%',
                        cellFilter: 'nmfc'
                    },
                    {
                        field: 'commodityClass',
                        displayName: 'Class',
                        width: '20%',
                        cellFilter: 'commodityClass'
                    },
                    {
                        field: 'productCode',
                        displayName: 'SKU/Product Code',
                        width: '20%'
                    },
                    {
                        field: 'hazmat',
                        displayName: 'Hazmat',
                        cellTemplate: 'pages/cellTemplate/checked-cell.html',
                        width: '8%',
                        searchable: false
                    }
                ],
                action: function (entity) {
                    if ($scope.selectedProducts && $scope.selectedProducts[0] && $scope.$root.isFieldRequired('ADD_EDIT_PRODUCT') && 
                            ($scope.selectedProducts[0].hazmat === $scope.$root.isFieldRequired('ADD_EDIT_HAZMAT_PRODUCT'))) {
                        openAddEditProductDialog(entity.id);
                    }
                },
                plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin(), NgGridPluginFactory.progressiveSearchPlugin()],
                enableColumnResize: true,
                multiSelect: false,
                progressiveSearch: true
            }
        };

        $scope.importDialog = {
            label: 'Import Product',
            isProduct: true,
            showDialog: false,
            importUrl: function () {
                return urlConfig.shipment + '/customer/' + $scope.productListModel.selectedCustomer.id + '/product/import';
            },
            fixUrl: function () {
                return urlConfig.shipment + '/customer/' + $scope.productListModel.selectedCustomer.id + '/product/fixNowDoc/';
            },
            removeFixDoc: function (docId) {
                ProductService.removeFixNowDoc({
                    customerId: $scope.productListModel.selectedCustomer.id,
                    docId: docId
                });
            },
            closeCallback: function () {
                $scope.loadProductsData();
            }
        };

        $scope.updateSortOrder = function () {
            CustomerOrderService.setProductListPrimarySort({
                customerId: $scope.productListModel.selectedCustomer.id
            }, $scope.productListModel.sortOrder);
        };

        $scope.getProductListSortOrder = function () {
            if ($scope.productListModel.selectedCustomer.id) {
                CustomerOrderService.getProductListPrimarySort({customerId: $scope.productListModel.selectedCustomer.id}, function (data) {
                    $scope.productListModel.sortOrder = data.result;
                });
            }
        };

        $scope.productRemoveDialog = {
            confirmDelete: function () {
                ProductService.archive({
                    customerId: $scope.productListModel.selectedCustomer.id,
                    productId: $scope.selectedProducts[0].id
                }, function () {
                    $scope.loadProductsData();
                    $scope.selectedProducts.length = 0;
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Product remove failed!',
                            'Can\'t remove product with ID :' + $scope.selectedProducts[0].id);
                });
            }
        };

        $scope.addProduct = function () {
            openAddEditProductDialog();
        };

        $scope.editProduct = function () {
            if ($scope.selectedProducts && $scope.selectedProducts[0] && $scope.$root.isFieldRequired('ADD_EDIT_PRODUCT')) {
                openAddEditProductDialog($scope.selectedProducts[0].id);
            }
        };

        $scope.deleteProduct = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Delete Product', okFunction: $scope.productRemoveDialog.confirmDelete,
                message: 'Are you sure you want to delete Product ' + $scope.selectedProducts[0].description + '?'
            });
        };

        $scope.disableProductListPrimarySort = function () {
            return !($scope.$root.isFieldRequired('ADD_EDIT_PRODUCT') || $scope.$root.isFieldRequired('DELETE_PRODUCT') ||
            $scope.$root.isFieldRequired('IMPORT_PRODUCT'));
        };

        $scope.importProducts = function () {
            $scope.importDialog.showDialog = true;
        };

        $scope.exportProducts = function () {
            $window.open('/restful/customer/' + $scope.productListModel.selectedCustomer.id + '/product/export', '_blank');
        };
    }
]);