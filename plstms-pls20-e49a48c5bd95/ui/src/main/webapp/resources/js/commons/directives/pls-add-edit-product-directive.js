angular.module('plsApp.directives').directive('plsAddEditProduct', ['ProductService', '$filter', function (ProductService, $filter) {

    var defaultFax = {countryCode: '1'};
    var emptyProduct = {hazmatEmergencyPhone: angular.copy(defaultFax), sharedProduct: true};

    return {
        restrict: 'A',
        scope: {
            selectedCustomer: '=',
            parentDialog: '='
        },
        replace: true,
        templateUrl: 'pages/tpl/add-edit-product-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.editProductModel = {
                showEditProduct: false,
                commodityClassDefined: false,
                editProductModalOptions: {
                    parentDialog: $scope.parentDialog
                },
                product: angular.copy(emptyProduct),
                commodityClasses: ['CLASS_50', 'CLASS_55', 'CLASS_60', 'CLASS_65', 'CLASS_70', 'CLASS_77_5', 'CLASS_85', 'CLASS_92_5', 'CLASS_100',
                    'CLASS_110', 'CLASS_125', 'CLASS_150', 'CLASS_175', 'CLASS_200', 'CLASS_250', 'CLASS_300', 'CLASS_400', 'CLASS_500'],
                hazmatClasses: {
                    "1": "1. Explosives",
                    "1.1": "1.1 Explosives (Div 1.1)",
                    "1.2": "1.2 Explosives (Div 1.2)",
                    "1.3": "1.3 Explosives (Div 1.3)",
                    "1.4": "1.4 Explosives (Div 1.4)",
                    "1.5": "1.5 Blasting Agents (Div 1.5)",
                    "1.6": "1.6 Explosives (Division 1.6)",
                    "2": "2. Gases",
                    "2.1": "2.1 Flammable Gas (Div 2.1)",
                    "2.2": "2.2 Non-flammable, non-toxic gas (Div 2.2)",
                    "2.3": "2.3 Toxic Gas (Div 2.3)",
                    "3": "3. Flammable Liquid and Combustible Liquid",
                    "4": "4. Flammable Solids",
                    "4.1": "4.1 Flammable Solid (Div 4.1)",
                    "4.2": "4.2 Substances liable to spontaneous combustion (Div 4.2)",
                    "4.3": "4.3 Substances which, in contact with water, emit flammable gases (Div 4.3)",
                    "5": "5. Oxidizing Agents and Organic Peroxides",
                    "5.1": "5.1 Oxidizers (Div 5.1)",
                    "5.2": "5.2 Organic Peroxides (Div 5.2)",
                    "6": "6. Toxic and Infectious Substances",
                    "6.1": "6.1 Poisonous or Toxic Material",
                    "6.2": "6.2 Biohazard",
                    "7": "7. Radioactive Substances",
                    "8": "8. Corrosive Substances",
                    "9": "9. Miscellaneous"
                }
            };

            $scope.closeEditProductDialog = function () {
                $scope.editProductModel.showEditProduct = false;
            };

            function clearHazmat() {
                if (!$scope.editProductModel.product.hazmat) {
                    delete $scope.editProductModel.product.hazmatUnNumber;
                    delete $scope.editProductModel.product.hazmatPackingGroup;
                    delete $scope.editProductModel.product.hazmatClass;
                    delete $scope.editProductModel.product.hazmatEmergencyCompany;
                    delete $scope.editProductModel.product.hazmatEmergencyPhone;
                    delete $scope.editProductModel.product.hazmatEmergencyContract;
                    delete $scope.editProductModel.product.hazmatInstructions;
                }
            }
            
            function initHazmatPackingGroup() {
                if(!$scope.editProductModel.product.hazmatPackingGroup) {
                    $scope.editProductModel.product.hazmatPackingGroup = "";
                }
            }

            function saveProduct() {
                if ($scope.editProductModel.product.hazmatEmergencyPhone && !$scope.editProductModel.product.hazmatEmergencyPhone.number) {
                    delete $scope.editProductModel.product.hazmatEmergencyPhone;
                }
                if (!$scope.editProductModel.product.hazmat) {
                    delete $scope.editProductModel.product.hazmatPackingGroup;
                }
                ProductService.save({
                    customerId: $scope.editProductModel.selectedCustomerId
                }, $scope.editProductModel.product, function (data) {
                    $scope.editProductModel.product.id = data.data;
                    if ($scope.editProductModel.closeHandler) {
                        clearHazmat();
                        $scope.editProductModel.closeHandler($scope.editProductModel.product);
                    }
                    $scope.closeEditProductDialog();
                    $scope.$root.$broadcast('event:newProductAdded', data.data);

                }, function () {
                    if (!$scope.editProductModel.product.hazmatEmergencyPhone) {
                        $scope.editProductModel.product.hazmatEmergencyPhone = angular.copy(defaultFax);
                    }
                    $scope.$root.$emit('event:application-error', 'Product save failed!', 'Can\'t save product');
                });
            }

            $scope.saveEditProductDialog = function () {
                ProductService.isUnique({
                    customerId: $scope.editProductModel.selectedCustomerId,
                    productId: $scope.editProductModel.product.id || 0,
                    description: $scope.editProductModel.product.description,
                    commodityClass: $scope.editProductModel.product.commodityClass,
                    shared: $scope.editProductModel.product.sharedProduct
                }, function (data) {
                    if (angular.isString(data.result) ? data.result === 'true' : data.result) {
                        saveProduct();
                    } else {
                        $scope.$root.$emit('event:application-error', 'Combination of Product Description and Class is not unique.',
                                ($scope.editProductModel.product.sharedProduct ? 'Shared' : 'Non Shared') + ' product with the description \''
                                + $scope.editProductModel.product.description + '\' and with the class \''
                                + $filter('commodityClass')($scope.editProductModel.product.commodityClass)
                                + '\' already exists within your company!');
                    }
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Product uniqueness check failed!', 'Unable to check product uniqueness!');
                });
            };

            $scope.$on('event:showAddEditProduct', function (event, transferObject) {
                $scope.editProductModel.showEditProduct = true;
                $scope.editProductModel.closeHandler = transferObject.closeHandler;
                $scope.editProductModel.selectedCustomerId = transferObject.customerId || $scope.selectedCustomer.id;
                $scope.editProductModel.selectedCustomerName = transferObject.customerName || $scope.selectedCustomer.name;

                if (transferObject.productId) {
                    ProductService.get({
                        customerId: $scope.editProductModel.selectedCustomerId,
                        productId: transferObject.productId
                    }, function (data) {
                        $scope.editProductModel.product = data;
                        if (!$scope.editProductModel.product.hazmatEmergencyPhone) {
                            $scope.editProductModel.product.hazmatEmergencyPhone = angular.copy(defaultFax);
                        }
                        initHazmatPackingGroup();
                    }, function () {
                        $scope.$root.$emit('event:application-error', 'Product load failed!',
                                'Can\'t load product with ID ' + transferObject.productId);
                    });
                } else {
                    $scope.editProductModel.product = angular.copy(emptyProduct);
                    if (transferObject.commodityClass) {
                        $scope.editProductModel.product.commodityClass = transferObject.commodityClass;
                        $scope.editProductModel.commodityClassDefined = true;
                    } else {
                        $scope.editProductModel.commodityClassDefined = false;
                    }
                    initHazmatPackingGroup();
                }
                $scope.editProductModel.editProductModalOptions.parentDialog = transferObject.parentDialog || $scope.parentDialog;
            });
        }]
    };
}]);