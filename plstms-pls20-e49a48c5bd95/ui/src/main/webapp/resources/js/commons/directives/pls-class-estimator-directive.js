angular.module('plsApp.directives').directive('plsClassEstimator', ['$rootScope', '$http', '$compile', '$templateCache', 'NgGridPluginFactory',
    'ClassEstimatorService', '$timeout',
    function ($rootScope, $http, $compile, $templateCache, NgGridPluginFactory, ClassEstimatorService, $timeout) {
        return {
            restrict: 'A',
            require: 'ngModel',
            scope: false,
            replace: false,
            link: function ($scope, element, attrs, ctrl) {
                'use strict';

                // this dirty hack is used because this directive is used inside another directive with isolated scope
                $scope._ = $scope.$root._;

                $http.get('pages/tpl/class-estimator-tpl.html', {cache: $templateCache}).then(function (result) {
                    var dialog = $compile(result.data)($scope);
                    angular.element('#content').append(dialog);

                    $scope.$on('$destroy', function () {
                        dialog.remove();
                    });
                });

                $scope.model = {
                    weightUnit: 'LBS',
                    dimensionUnit: 'INCH',
                    quantity: 1
                };

                $scope.canUseScaffoldingType = $scope.$root.isFieldRequired('CAN_USE_SCAFOLDING_DENSITY_EST');
                $scope.canUseCageBasketType = $scope.$root.isFieldRequired('CAN_USE_WORK_CAGE_AND_BASKET_EST');

                $scope.weightUnits = ClassEstimatorService.getWeightUnits();
                $scope.dimensionUnits = ClassEstimatorService.getDimensionUnits();
                $scope.specialCalcTypes = ClassEstimatorService.getSpecialCalcTypes();

                $scope.calcTypes = ClassEstimatorService.initCalculationTypes($scope.canUseScaffoldingType, $scope.canUseCageBasketType);
                $scope.estimCalcType = ClassEstimatorService.initSelectedType($scope.calcTypes);

                $scope.totals = {
                    PCF: 0,
                    estimatedClass: 'None',
                    cubicFeet: 0,
                    weight: 0
                };

                $scope.canUseAllCalcTypes = function() {
                    return $scope.canUseScaffoldingType && $scope.canUseCageBasketType;
                };

                $scope.isNotStandardCalculation = function() {
                    return $scope.specialCalcTypes.indexOf($scope.estimCalcType.selected) > -1;
                };

                $scope.selectedItems = [];
                $scope.shouldBeOpen = false;
                $scope.gridData = [];

                function resetInputs() {
                    $scope.model.weight = undefined;
                    $scope.model.length = undefined;
                    $scope.model.width = undefined;
                    $scope.model.height = undefined;
                    $scope.model.densityPCF = undefined;
                    $scope.model.quantity = 1;
                }

                $scope.open = function () {
                    $scope.shouldBeOpen = true;
                };

                $scope.savePrevState = function() {
                    $scope.prevType = angular.copy($scope.estimCalcType.selected);
                };

                $scope.changeCalculationType = function() {
                    setTimeout( function(){
                        if (confirm('The current page contains unsaved information that will be lost if you change the Calculation Type')) {
                            resetInputs();
                            $scope.clearGrid();
                        } else {
                            $scope.estimCalcType.selected = $scope.prevType;
                            if(!$scope.$root.$$phase) {
                                $scope.$digest();
                            }
                        }
                    },1);
                };

                $scope.close = function () {
                    $scope.shouldBeOpen = false;
                    $scope.clearGrid();
                    resetInputs();
                };

                $scope.opts = {
                    parentDialog: attrs.plsClassEstimator
                };

                $scope.isGridFilledForSpecialCalc = function() {
                    return $scope.isNotStandardCalculation() && $scope.gridData.length === 1;
                };

                $scope.gridOptions = {
                    enableColumnResize: true,
                    data: 'gridData',
                    enableSorting: false,
                    selectedItems: $scope.selectedItems,
                    tabIndex: -10,
                    columnDefs: [{
                        field: 'quantity',
                        cellFilter: 'number',
                        displayName: 'Qty',
                        width: '8%'
                    }, {
                        cellFilter: 'number',
                        field: 'length',
                        displayName: 'Length',
                        width: '10%'
                    }, {
                        cellFilter: 'number',
                        field: 'width',
                        displayName: 'Width',
                        width: '9%'
                    }, {
                        cellFilter: 'number',
                        field: 'height',
                        displayName: 'Height',
                        width: '10%'
                    }, {
                        cellFilter: 'dimensionsMeasure',
                        field: 'dimensionUnit',
                        displayName: 'UOM',
                        width: '9%'
                    }, {
                        field: 'self',
                        cellFilter: 'materialWeight',
                        displayName: 'Weight',
                        width: '13%'
                    }, {
                        field: 'cubicFeet',
                        displayName: 'Cubic Feet',
                        width: '16%'
                    }, {
                        cellFilter: 'number',
                        field: 'density',
                        displayName: 'Density (PCF)',
                        width: '17%'
                    }, {
                        cellFilter: 'number',
                        field: 'nmfc',
                        displayName: 'Class',
                        width: '9%'
                    }],
                    plugins: [NgGridPluginFactory.plsGrid()]
                };

                $scope.$watch('gridData.length', function () {
                    $scope.totals = ClassEstimatorService.calculateTotals($scope.gridData);
                });

                $scope.addRow = function () {
                    if ($scope.form.$valid){
                        if ($scope.isNotStandardCalculation() && $scope.estimCalcType.selected === $scope.calcTypes.SCAFF) {
                            $scope.gridData.push(ClassEstimatorService.calculateForScaffolding($scope.model));
                            delete $scope.model.maxDimension;
                            resetInputs();
                        } else if ($scope.isNotStandardCalculation() && $scope.estimCalcType.selected === $scope.calcTypes.CAGE_BASKET) {
                            $scope.gridData.push(ClassEstimatorService.calculateForCageAndBasket($scope.model));
                            resetInputs();
                        } else {
                            $scope.gridData.push(ClassEstimatorService.calculateForStandard($scope.model));
                            resetInputs();
                        }
                    }
                };

                $scope.deleteSelectedRows = function () {
                    $scope.gridData = _.difference($scope.gridData, $scope.selectedItems);
                    $scope.selectedItems.length = 0;
                };

                $scope.clearGrid = function () {
                    $scope.gridData = [];
                    $scope.selectedItems.length = 0;
                    $scope.totals.PCF = 0;
                    $scope.totals.estimatedClass = 'None';
                };

                $scope.ok = function () {
                    if ($scope.totals.estimatedClass && $scope.totals.estimatedClass !== 'None') {
                        ctrl.$viewValue.commodityClass = 'CLASS_' + $scope.totals.estimatedClass.replace(".", "_");
                        if ($scope.isNotStandardCalculation()) {
                            ctrl.$viewValue.weight = $scope.gridData[0].weight;
                            ctrl.$viewValue.length = $scope.gridData[0].length;
                            ctrl.$viewValue.width = $scope.gridData[0].width;
                            ctrl.$viewValue.height = $scope.gridData[0].height;
                            ctrl.$viewValue.quantity = $scope.gridData[0].quantity;
                        } else {
                            ctrl.$viewValue.weight = undefined;
                            ctrl.$viewValue.length = undefined;
                            ctrl.$viewValue.width = undefined;
                            ctrl.$viewValue.height = undefined;
                            ctrl.$viewValue.quantity = undefined;
                        }
                        $scope.$emit('event:newCommodityClassSelected'); 
                        $scope.close();
                    }
                };
            }
        };
    }
]);