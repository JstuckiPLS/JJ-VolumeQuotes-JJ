/**
 * Text Identifier - directive
 */
angular.module('plsApp.directives').directive('safeworksGlSelects', ['DictionaryService',
    function (DictionaryService) {
        return {
            restrict: 'A',
            templateUrl: 'pages/tpl/identifiers/safeworks-gl-selects.html',
            scope: {
                model: '=',
                inputDisabled: '='
            },
            controller: ['$scope', function ($scope) {
                'use strict';

                $scope.glCodes = [];

                $scope.glData = {
                    'FRT_BILL': [], // Freight Charge To Branch
                    'FRT_TYPE': [] // Freight Charge Type
                };

                function initGlId() {
                    var glCodes = $scope.model.split('.');

                    $scope.glCodes[0] = glCodes[0];
                    $scope.glCodes[1] = glCodes[1];
                }

                /* Call service for getting, formatting and saving GL Codes Data */
                DictionaryService.getGLValuesForFreightCharge({}, function (data) {
                    data.forEach(function (value) {
                        $scope.glData[value.group].push({description: value.description, value: value.value});
                    });

                    $scope.glData.FRT_BILL = _.sortBy($scope.glData.FRT_BILL, 'description');
                    $scope.glData.FRT_TYPE = _.sortBy($scope.glData.FRT_TYPE, 'value');

                    if ($scope.model) {
                        initGlId();
                    }
                });

                $scope.$watch('model', function (newValue) {
                    if (newValue) {
                        initGlId();
                    } else {
                        $scope.glCodes = [];
                    }
                });

                $scope.editGl = function (index) {
                    var branch = $scope.glCodes[0];

                    if ($scope.glCodes[0]) {
                        $scope.glCodes[0] = $scope.glCodes[0].indexOf('.') > -1 ? $scope.glCodes[0].split('.')[1] : $scope.glCodes[0];
                    }

                    if (angular.isDefined($scope.model)) {
                        var gl = $scope.model.split('.');
                        gl[index] = $scope.glCodes[index];
                        $scope.model = gl.join('.');
                    } else {
                        $scope.model = $scope.glCodes.join('.');

                        if ($scope.glCodes[0] && $scope.glCodes[1]) {
                            $scope.model = $scope.glCodes.join('.');
                        }
                    }

                    $scope.glCodes[0] = branch;
                };
            }]
        };
    }
]);