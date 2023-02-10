/**
 * Special identifiers directive for Brand Industrial Services customer.
 */
angular.module('plsApp.directives').directive('brandIndustrialIdentifiers', ['DictionaryService',
    function (DictionaryService) {
        return {
            restrict: 'A',
            templateUrl: 'pages/tpl/identifiers/brand-industrial-identifiers.html',
            scope: {
                shipment: '=',
                customerId: '=',
                invalidIdentifierGl: '=',
                invalidIdentifierOpbol: '='
            },
            controller: ['$scope', function ($scope) {
                'use strict';

                var model = {};

                $scope.$watch('shipment.finishOrder.jobNumbers', function(){
                    if ($scope.shipment && $scope.shipment.finishOrder) {
                        if (!$scope.shipment.finishOrder.jobNumbers) {
                            $scope.shipment.finishOrder.jobNumbers = [{}];
                        } else if (!$scope.shipment.finishOrder.jobNumbers.length) {
                            $scope.shipment.finishOrder.jobNumbers.push({});
                        }
                    }
                });
                
                function validateFields(){
                    if ($scope.shipment && $scope.shipment.finishOrder && $scope.shipment.billTo) {
                        $scope.invalidIdentifierOpbol = !$scope.shipment.finishOrder.opBolNumber;
                        $scope.invalidIdentifierGl = !$scope.shipment.finishOrder.glNumber;
                    }
                }
                
                $scope.$watch('shipment.billTo', function(){
                    validateFields();
                });
                
                $scope.$watch('shipment.finishOrder.opBolNumber', function(){
                    validateFields();
                });
                
                $scope.$watch('shipment.finishOrder.glNumber', function(){
                    validateFields();
                });

                /* Call service for getting, formatting and saving GL Codes Data */
                if ($scope.$root.isBrandIndustrialServices($scope.customerId)) {
                    DictionaryService.getBrandNumComponents({}).$promise.then(function (data) {
                        model = {
                            'BRN_NUM_BRAND': [], // Branch / Cost Center
                            'GL_NUM_BRAND': [] // GL #
                        };
                        _.sortBy(data, 'description').forEach(function (value) {
                            model[value.group].push(value);
                        });
                    });
                } else {
                    DictionaryService.getAlumaNumComponents({}).$promise.then(function (data) {
                        model = {
                            'BRN_NUM_ALUMA': [], // Branch / Cost Center
                            'GL_NUM_ALUMA': [] // GL #
                        };
                        _.sortBy(data, 'description').forEach(function (value) {
                            model[value.group].push(value);
                        });
                    });
                }

                $scope.getBranch = function() {
                    return model.BRN_NUM_BRAND || model.BRN_NUM_ALUMA || [];
                };

                $scope.getGL = function() {
                    return model.GL_NUM_BRAND || model.GL_NUM_ALUMA || [];
                };
            }]
        };
    }
]);