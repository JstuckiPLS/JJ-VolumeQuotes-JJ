/**
 * AngularJS directive which displays grid with product items and allows to modify it.
 *
 * @author: Sergey Kirichenko
 * Date: 6/26/13
 * Time: 10:28 AM
 */
angular.module('plsApp').directive('plsQuoteProducts', [
    '$compile', '$filter', '$http', '$templateCache', '$location', 'NgGridPluginFactory', 'ProductService', 'ProductTotalsService',
    'CustomerOrderService', 'ShipmentUtils', 'ClassEstimatorService',
    function ($compile, $filter, $http, $templateCache, $location, NgGridPluginFactory, ProductService, ProductTotalsService,
        CustomerOrderService, ShipmentUtils, ClassEstimatorService) {
        return {
            restrict: 'A',
            scope: {
                shipment: '=plsQuoteProducts',
                rateQuoteDictionary: '=',
                selectedCustomer: '=',
                quoteProcessing: '&',
                parentDialog: '=',
                hideControls: '=',
                descriptionRequired: '=',
                dimensionsRequired: '=',
                classNotRequiredForCanada: '=?',
                showLargeLoadMessage: '=',
                renderGrid: '=',
                startQuoteTabIndex: '='
            },
            compile: function () {
                return {
                    pre: function (scope, element) {
                        'use strict';

                        scope.plsTabIndex = scope.startQuoteTabIndex || 0;

                        scope.emptyMaterial = {
                            hazmat: false, stackable: false, weightUnit: 'LBS', dimensionUnit: 'INCH',
                            weight: '', length: '', width: '', height: '',
                            quantity: 1
                        };

                        scope.material = angular.copy(scope.emptyMaterial);

                        scope.products = {};
                        scope.materialColumnModel = [];
                        scope.hazmatInfo = {};
                        scope.enableRowSelection = true;

                        scope.maxCountOfProducts = ShipmentUtils.MAX_COUNT_OF_PRODUCTS;

                        function isHazmat(entity) {
                            return entity.hazmat;
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

                            scope.materialsGrid.tooltipOptions.url = 'pages/content/quotes/hazmat-info-tooltip.html';
                        }

                        scope.materialsGrid = {
                            enableColumnResize: true,
                            data: 'shipment.finishOrder.quoteMaterials',
                            multiSelect: false,
                            selectedItems: [],
                            columnDefs: 'materialColumnModel',
                            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.tooltipPlugin(true)],
                            tooltipOptions: {
                                showIf: isHazmat,
                                onShow: onShowTooltip
                            },
                            beforeSelectionChange: function() {
                                return scope.enableRowSelection;
                            },
                            enableSorting: false,
                            progressiveSearch: false,
                            tabIndex: -10
                        };

                        scope.initGridOptions = function (gridOptions, columnsConfiguration) {
                            return scope.$root.initGridOptions(gridOptions, columnsConfiguration);
                        };

                        function calculateTotals() {
                            scope.totals = ProductTotalsService.calculateTotals(scope.shipment.finishOrder.quoteMaterials);
                        }

                        calculateTotals();

                        function isDMCWarningNeeded(cubicFeet, density, alternateCubicFeet) {
                            return (cubicFeet > 350 && density < 4)
                                || (cubicFeet > 750 && density < 6)
                                || (alternateCubicFeet > 350 && scope.totals.weight/alternateCubicFeet < 4)
                                || (alternateCubicFeet > 750 && scope.totals.weight/alternateCubicFeet < 6);
                        }

                        scope.addProduct = function () {
                            if (scope.addProductForm.$valid) {
                                if (scope.descriptionRequired
                                        && (!scope.material.productDescription || scope.material.productDescription.trim().length === 0)) {
                                    scope.$root.$emit('event:application-error', 'Missing Product!', 'Please select a product from the list');
                                } else if (scope.shipment.finishOrder.quoteMaterials.length < ShipmentUtils.MAX_COUNT_OF_PRODUCTS) {
                                    if (!scope.material.productId) {
                                        //If the material is not from the product list, then copy the current 'Hazardous' flag state into product list
                                        //If product is pre-defined, we shouldn't change hazmat flag for this product.
                                        scope.material.hazmat = scope.products.hazmatOnly;
                                    }

                                    if (scope.classNotRequiredForCanada && !scope.material.commodityClass) {
                                        scope.material.commodityClass = 'CLASS_50';
                                    }

                                    // make sure material has default dimension if not provided. 
                                    if(!scope.material.length){
                                        scope.material.length=48;
                                    }
                                    if(!scope.material.height){
                                        scope.material.height=48;
                                    }
                                    if(!scope.material.width){
                                        scope.material.width=48;
                                    }

                                    if (scope.material.self) {
                                        scope.material.self = angular.copy(scope.material);
                                    }

                                    // this line is added to make logic on build order work properly
                                    delete scope.material.product;

                                    scope.shipment.finishOrder.quoteMaterials.push(scope.material);
                                    calculateTotals();

                                    if (!_.includes($location.$$url, "manual-bol") && scope.$root.isFieldRequired("CAN_SEE_DMS_WARNING")) {
                                        var cubicFeet = ClassEstimatorService.calculateCubicFeet(scope.material);
                                        var density = ClassEstimatorService.calculateDensity(scope.material);
                                        var alternateCubicFeet = scope.shipment.finishOrder.quoteMaterials.length * 125;

                                        if (isDMCWarningNeeded(cubicFeet, density, alternateCubicFeet)) {
                                            scope.$emit('event:application-warning-nopacity', "Freight Density Warning:",
                                                    "Freight dimensions and density have triggered a cubic capacity / minimum " +
                                                    "density warning for one or more carriers. <b>Additional charges may apply.</b> " +
                                                    "Please contact your PLS operations team for an LTL volume rate quotation.");
                                        }
                                    }

                                    scope.material = angular.copy(scope.emptyMaterial);
                                    scope.products = {};

                                    scope.$root.$broadcast('event:productChanged');
                                    scope.enableRowSelection = true;
                                    return true;
                                } else {
                                    scope.$root.$emit('event:application-error', 'Product Grid size exceeded!',
                                            'Product Grid should contain not more than 100 products.');
                                }
                            }
                            return false;
                        };

                        scope.editProduct = function () {
                            if (scope.getSelectedMaterialRowNum() > -1) {
                                scope.material = scope.shipment.finishOrder.quoteMaterials[scope.getSelectedMaterialRowNum()];
                                scope.material.weightUnit = 'LBS';
                                scope.material.dimensionUnit = 'INCH';
                                if (scope.material.hazmat === true) {
                                    scope.products.hazmatOnly = true;
                                }
                                scope.products.product = $filter('materialProduct')({
                                    productDescription: scope.material.productDescription,
                                    productCode: scope.material.productCode,
                                    hazmatUnNumber: scope.material.hazmatUnNumber,
                                    hazmatClass: scope.material.hazmatClass,
                                    packingGroup: scope.material.packingGroup
                                }, scope.defaultProductSortOrder);
                                scope.removeProduct();
                                scope.enableRowSelection = false;
                            }
                        };

                        scope.removeProduct = function () {
                            if (scope.getSelectedMaterialRowNum() > -1) {
                                scope.shipment.finishOrder.quoteMaterials.splice(scope.getSelectedMaterialRowNum(), 1);
                                scope.$root.$broadcast('event:productChanged');
                                calculateTotals();
                            }
                        };

                        scope.getSelectedMaterialRowNum = function () {
                            return scope.shipment.finishOrder.quoteMaterials.indexOf(scope.materialsGrid.selectedItems[0]);
                        };

                        scope.openAddProductDialog = function () {
                            var transferObject = {productId: null, commodityClass: scope.material.commodityClass};
                            transferObject.customerId = scope.selectedCustomer.id;
                            transferObject.customerName = scope.selectedCustomer.name;
                            transferObject.parentDialog = scope.parentDialog;

                            scope.$root.$broadcast('event:showAddEditProduct', transferObject);
                        };

                        scope.getQuote = function () {
                            scope.addProduct();

                            if (scope.isProcessingRequired()) {
                                scope.quoteProcessing()();
                            }
                        };

                        scope.isProcessingRequired = function () {
                            return scope.quoteProcessing() && angular.isFunction(scope.quoteProcessing());
                        };

                        // using http get and manual compilation because otherwise validation of the nested form is working incorrectly on Sales Order
                        $http.get('pages/tpl/products-data-tpl.html', {cache: $templateCache}).success(function (html) {
                            element.html($compile($.trim(html))(scope));
                        });
                    },
                    post: function (scope) {
                        scope.materialColumnModel = [{
                            field: 'self',
                            referenceId: 'weightColumn',
                            displayName: 'Weight',
                            width: '7%',
                            cellFilter: 'materialWeight'
                        }, {
                            referenceId: 'classColumn',
                            field: 'commodityClass',
                            displayName: 'Class',
                            width: '6%',
                            cellFilter: 'commodityClass'
                        }, {
                            referenceId: 'productColumn',
                            field: 'productDescription',
                            displayName: 'Product Description',
                            width: '10%'
                        }, {
                            referenceId: 'productCodeColumn',
                            field: 'productCode',
                            displayName: 'SKU/Product Code',
                            width: '8%'
                        }, {
                            referenceId: 'nmfcColumn',
                            field: 'nmfc',
                            displayName: 'NMFC',
                            width: '7%'
                        }, {
                            referenceId: 'dimensionsColumn',
                            field: 'self',
                            displayName: 'Dimensions',
                            width: '10%',
                            cellFilter: 'materialDimension'
                        }, {
                            referenceId: 'quantityColumn',
                            field: 'quantity',
                            displayName: 'Qty',
                            width: '4%'
                        }, {
                            referenceId: 'packageTypeColumn',
                            field: 'packageType',
                            displayName: 'Packaging Type',
                            width: '8%',
                            cellFilter: 'packageType'
                        }, {
                            field: 'cube',
                            displayName: 'Cube (CU FT)',
                            width: '11%',
                            cellFilter: 'number: 2'
                        }, {
                            field: 'density',
                            displayName: 'Density (PCF)',
                            width: '11%',
                            cellFilter: 'number: 2'
                        }, {
                            referenceId: 'piecesColumn',
                            field: 'pieces',
                            displayName: 'Pieces',
                            width: '6%'
                        }, {
                            referenceId: 'stackableColumn',
                            field: 'stackable',
                            displayName: 'Stackable',
                            width: '8%',
                            cellTemplate: 'pages/cellTemplate/checked-cell.html'
                        }, {
                            referenceId: 'hazmatColumn',
                            field: 'hazmat',
                            displayName: 'Hazmat',
                            showTooltip: true,
                            width: '7%',
                            cellTemplate: '<div class="ngCellText text-center">'
                            + '<i class="icon-warning-sign" data-ng-show="row.entity.hazmat"></i>'
                            + '</div>'
                        }, {
                            referenceId: 'hazmatClassColumn',
                            field: 'hazmatClass',
                            displayName: 'Hazmat Class',
                            cellClass: 'text-center',
                            width: '8%'
                        }];

                        function setupProduct(productId) {
                            if (productId) {
                                ProductService.info({productId: productId, customerId: scope.selectedCustomer.id}, function (data) {
                                    scope.products.product = $filter('materialProduct')({
                                        productDescription: data.description,
                                        productCode: data.productCode,
                                        hazmatUnNumber: data.hazmatUnNumber,
                                        hazmatClass: data.hazmatClass,
                                        packingGroup: data.hazmatPackingGroup
                                    }, scope.defaultProductSortOrder);

                                    scope.material.productId = data.id;
                                    scope.material.commodityClass = data.commodityClass;
                                    scope.material.productCode = data.productCode;
                                    scope.material.productDescription = data.description;
                                    scope.material.packageType = data.packageType;
                                    scope.material.stackable = data.stackable || false;
                                    scope.material.hazmat = data.hazmat || false;
                                    scope.products.hazmatOnly = data.hazmat || false;
                                    scope.material.hazmatClass = data.hazmatClass;
                                    scope.material.packingGroup = data.hazmatPackingGroup;
                                    scope.material.unNum = data.hazmatUnNumber;
                                    scope.material.nmfc = data.nmfc;

                                    if (data.nmfc && data.nmfcSubNum) {
                                        scope.material.nmfc = scope.material.nmfc + '-' + data.nmfcSubNum;
                                    }

                                    scope.material.emergencyResponseCompany = data.hazmatEmergencyCompany;
                                    scope.material.emergencyResponseContractNumber = data.hazmatEmergencyContract;
                                    scope.material.emergencyResponseInstructions = data.hazmatInstructions;

                                    if (data.hazmatEmergencyPhone) {
                                        scope.material.emergencyResponsePhoneCountryCode = data.hazmatEmergencyPhone.countryCode;
                                        scope.material.emergencyResponsePhoneAreaCode = data.hazmatEmergencyPhone.areaCode;
                                        scope.material.emergencyResponsePhone = data.hazmatEmergencyPhone.number;
                                        scope.material.emergencyResponsePhoneExtension = data.hazmatEmergencyPhone.extension;
                                    }
                                });
                            }
                        }

                        scope.$on('event:newProductAdded', function (event, productId) {
                            setupProduct(productId);
                        });

                        scope.$watch('products.product', function () {
                            if (scope.products.product) {
                                setupProduct(scope.products.product.value);
                            } else {
                                scope.material.productId = undefined;
                                scope.material.productCode = undefined;
                                scope.material.productDescription = undefined;
                                scope.material.hazmat = false;
                                scope.material.hazmatClass = undefined;
                                scope.material.unNum = undefined;
                                scope.material.nmfc = undefined;
                                scope.material.emergencyResponseCompany = undefined;
                                scope.material.emergencyResponseContractNumber = undefined;
                                scope.material.emergencyResponseInstructions = undefined;
                                scope.material.emergencyResponsePhoneCountryCode = undefined;
                                scope.material.emergencyResponsePhoneAreaCode = undefined;
                                scope.material.emergencyResponsePhone = undefined;
                                scope.material.emergencyResponsePhoneExtension = undefined;
                                scope.hazmatInfo = {};
                                scope.hazmatInfo.exist = false;
                            }
                        });

                        scope.cleanProduct = function () {
                            if (scope.products.product) {
                                scope.products.product = undefined;
                            }
                        };

                        scope.changeTabIndex = function (index) {
                            var length = angular.element('#productLength');
                            length.attr('tabindex', index);
                            var width = angular.element('#width');
                            width.attr('tabindex', index);
                            var height = angular.element('#height');
                            height.attr('tabindex', index);
                            var dimensionsMeasure = angular.element('#dimensionsMeasure');
                            dimensionsMeasure.attr('tabindex', index);
                        };

                        scope.$watch('selectedCustomer.id', function (newValue, oldValue) {
                            if ((newValue !== undefined && scope.defaultProductSortOrder === undefined) || (newValue !== oldValue && newValue)) {
                                if (scope.shipment.productListPrimarySort && scope.shipment.organizationId === newValue) {
                                    scope.defaultProductSortOrder = scope.shipment.productListPrimarySort;
                                } else {
                                    CustomerOrderService.getProductListPrimarySort({customerId: scope.selectedCustomer.id}, function (data) {
                                        scope.defaultProductSortOrder = data.result;
                                    });
                                }
                            }
                        });

                        function clearProducts() {
                            scope.material = angular.copy(scope.emptyMaterial);
                            scope.products.product = '';
                            scope.products.productList = angular.copy(scope.recentProducts);
                            scope.products.hazmatOnly = undefined;
                        }

                        function resetQuantity(newAccessorialList) {
                            if (_.contains(newAccessorialList, 'ODM')) {
                                scope.material.quantity = '';
                            } else {
                                scope.material.quantity = 1;
                            }
                        }

                        function isCanadaAddress(address) {
                            return address && address.zip && address.zip.country && address.zip.country.id === 'CAN';
                        }

                        function isCanadaToCanadaShipment() {
                            return scope.shipment && isCanadaAddress(scope.shipment.originDetails)
                                    && isCanadaAddress(scope.shipment.destinationDetails);
                        }

                        scope.isClassRequired = function() {
                            return !scope.classNotRequiredForCanada || !isCanadaToCanadaShipment();
                        };

                        function changeDimensionsTabIndex() {
                            if (_.contains(scope.originAccessorials, 'ODM') || _.contains(scope.destAccessorials, 'ODM')
                                    || isCanadaToCanadaShipment()) {
                                scope.changeTabIndex(scope.plsTabIndex);
                            } else {
                                scope.changeTabIndex(-1);
                            }
                        }

                        scope.$watch('shipment.destinationDetails.zip.country.id === "CAN" && shipment.originDetails.zip.country.id === "CAN"', 
                                changeDimensionsTabIndex);

                        scope.$watch('shipment.originDetails.accessorials', function (newAccessorialList) {
                            scope.originAccessorials = _.union(newAccessorialList, scope.shipment.destinationDetails.accessorials);
                            resetQuantity(scope.originAccessorials);
                            changeDimensionsTabIndex();
                        }, true);

                        scope.$watch('shipment.destinationDetails.accessorials', function (newAccessorialList) {
                            scope.destAccessorials = _.union(newAccessorialList, scope.shipment.originDetails.accessorials);
                            resetQuantity(scope.destAccessorials);
                            changeDimensionsTabIndex();
                        }, true);

                        scope.$watch('shipment.finishOrder.quoteMaterials', function() {
                            scope.totals = ProductTotalsService.calculateTotals(scope.shipment.finishOrder.quoteMaterials);
                        });

                        scope.$on('event:newCommodityClassSelected', scope.cleanProduct);

                        scope.$on('event:pls-clear-form-data', function () {
                            clearProducts();
                        });

                        scope.$on('event:clear-products', function () {
                            clearProducts();
                        });

                        scope.$on('event:pls-add-quote-item', function () {
                            var result = scope.addProduct();
                            scope.$emit('event:pls-added-quote-item', scope.shipment.finishOrder.quoteMaterials.length, result);
                        });

                        function isNotEmpty(item) {
                            return !_.isUndefined(item) && item !== '';
                        }

                        scope.isDimensionsRequired = function() {
                            return scope.dimensionsRequired || isNotEmpty(scope.material.length) || isNotEmpty(scope.material.width)
                                    || isNotEmpty(scope.material.height) || scope.shipment.requestVLTLRates;
                        };
                    }
                };
            }
        };
    }
]);
