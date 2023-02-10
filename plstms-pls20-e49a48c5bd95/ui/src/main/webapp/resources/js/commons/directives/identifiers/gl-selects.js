/**
 * Text Identifier - directive
 *
 * @Vitaliy Gavrilyuk
 */
angular.module('plsApp.directives').directive('glSelects', ['DictionaryService',
    function (DictionaryService) {
        return {
            restrict: 'A',
            templateUrl: 'pages/tpl/identifiers/gl-selects.html',
            scope: {
                model: '=',
                inputDisabled: '='
            },
            controller: ['$scope', function ($scope) {
                'use strict';

                $scope.glCodes = [];

                var COUNTRY_CODES = {
                    '001': 'SVC-001',
                    '004': 'NES-004',
                    '005': 'CAN-005',
                    '008': 'REDI-008',
                    '009': 'AAS-009',
                    '320': 'AAS-320',
                    '720': 'SOL-720',
					'738': '720-738',
                    '740': 'REDI UT-740'
                };

                $scope.getCountryDescription = function(countryCode) {
                    return COUNTRY_CODES[countryCode] || countryCode;
                };

                $scope.glData = {
                    'CMP_NUM': [], // Country
                    'BRN_NUM': [], // Branch
                    'SBR_NUM': [], // Discipline
                    'DIV_NUM': []  // Division
                };

                function initGlId() {
                    var glCodes = $scope.model.split('-');

                    $scope.glCodes[0] = glCodes[0];
                    $scope.glCodes[1] = glCodes[1];
                    $scope.glCodes[2] = glCodes[2];
                    $scope.glCodes[3] = glCodes[3];
                }

                /* Call service for getting, formatting and saving GL Codes Data */
                DictionaryService.getGlNumComponents({}).$promise.then(function (data) {
                    /* Formatting */
                    data.forEach(function (value) {
                        /* Formatting for UI */
                        $scope.glData[value.group].push(value.description);
                    });

                    $scope.glData.CMP_NUM.sort();
                    $scope.glData.BRN_NUM.sort();
                    $scope.glData.SBR_NUM.sort();
                    $scope.glData.DIV_NUM.sort();

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

                /* Call service for formatting GL# on edit */
                $scope.editGl = function (index) {
                    var glCountry = $scope.glCodes[0];

                    if ($scope.glCodes[0]) {
                        $scope.glCodes[0] = $scope.glCodes[0].indexOf('-') > -1 ? $scope.glCodes[0].split('-')[1] : $scope.glCodes[0];
                    }

                    if (angular.isDefined($scope.model)) {
                        var gl = $scope.model.split('-');
                        gl[index] = $scope.glCodes[index];
                        $scope.model = gl.join('-');
                    } else {
                        $scope.model = $scope.glCodes.join('-');

                        if ($scope.glCodes[0] && $scope.glCodes[1] && $scope.glCodes[2] && $scope.glCodes[3]) {
                            $scope.model = $scope.glCodes.join('-');
                        }
                    }

                    $scope.glCodes[0] = glCountry;
                };
            }]
        };
    }
]);