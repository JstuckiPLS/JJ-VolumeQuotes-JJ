/**
 * Text Identifier - directive
 *
 * @Vitaliy Gavrilyuk
 */
angular.module('plsApp.directives')
       .directive('plsIdentifier', ['plsJobIdRules', 'plsGLRules', 'billToIdentifiers', 'Identifiers', '$compile', 'plsGLSafeworksRules',
    function (plsJobIdRules, plsGLRules, billToIdentifiers, Identifiers, $compile, plsGLSafeworksRules) {
        return {
            restrict: 'A',
            templateUrl: function (elem, attrs) {
                switch (attrs.plsIdentifier) {
                    case 'RB':
                        return 'pages/tpl/identifiers/pls-requested-by.html';
                    case 'CARGO':
                        return 'pages/tpl/identifiers/pls-cargo-value.html';
                    case 'JOB':
                        return 'pages/tpl/identifiers/pls-job-numbers.html';
                    default:
                        return 'pages/tpl/identifiers/pls-bol-po-pu-so-sr-tr-gl.html';
                }
            },
            scope: {
                plsIdentifier: '@',
                popPlace: '@?',
                customerId: '=?',
                shipment: '=',
                invalidIdentifier: '=',
                isShipmentDetails: '='
            },
            controller: ['$scope', '$element', function ($scope, $element) {
                'use strict';

                var srv = {
                    identifiers: Identifiers
                };

                $scope.isSafwayGL = function() {
                    return $scope.plsIdentifier === 'GL' && $scope.$root.isSafway($scope.customerId);
                };

                $scope.isSafeworksGL = function() {
                    return $scope.plsIdentifier === 'GL' && $scope.$root.isSafeworks($scope.customerId);
                };

                var glInputs;
                function compileGlTemplate(tpl) {
                    glInputs = $compile(tpl)($scope);
                    if ($scope.isShipmentDetails) {
                        $element.parent().parent().after(glInputs);
                    } else {
                        $element.after(glInputs);
                    }

                    $scope.clearGl = function() {
                        delete $scope.inputModel;
                    };

                    $scope.$on("$destroy", function() {
                        glInputs.remove();
                    });
                }

                $scope.$watch('customerId', function(newVal) {
                    var tpl;
                    if ($scope.isSafwayGL()) {
                        tpl = '<div data-gl-selects data-model="inputModel" data-input-disabled="!shipment.billTo || disabledIdentifier"></div>';
                        compileGlTemplate(tpl);
                        return;
                    }
                    if ($scope.isSafeworksGL()) {
                        tpl = '<div data-safeworks-gl-selects data-model="inputModel"' 
                            + 'data-input-disabled="!shipment.billTo || disabledIdentifier"></div>';
                        compileGlTemplate(tpl);
                        return;
                    }
                    if (glInputs) {
                        glInputs.remove();
                    }
               });

                $scope.plsIdentifierDetails = billToIdentifiers[$scope.plsIdentifier];

                $scope.getMaxFieldLength = function() {
                    return $scope.plsIdentifierDetails.maxLength;
                };

                function getModelValue() {
                    return _.result($scope.shipment, $scope.plsIdentifierDetails.field);
                }

                function isEmptyAndRequired() {
                    return $scope.required && ($scope.plsIdentifier === 'JOB' ? _.isEmpty(getModelValue())
                            : angular.isUndefined(getModelValue()) || _.isEmpty(getModelValue().toString()));
                }

                function setInvalidIdentifier () {
                    $scope.invalidIdentifier = $scope.isModelFormatError || $scope.isModelValueError || isEmptyAndRequired();
                }

                function getIdentifierRule(identifierRule) {
                    if ($scope.plsIdentifier === 'JOB' && $scope.$root.isSafway($scope.customerId)) {
                        return plsJobIdRules.regExp;
                    }

                    if ($scope.isSafwayGL()) {
                        return plsGLRules.regExp;
                    } 

                    if ($scope.isSafeworksGL()) {
                        return plsGLSafeworksRules.regExp;
                    }
                    return identifierRule.ruleExp;
                }

                function updateDefaultValue() {
                    var identifierRule = srv.identifiers.getIdentifierRule($scope.shipment, $scope.plsIdentifier);

                    $scope.defaultValueBackup = identifierRule.defaultValue;
                    $scope.ruleExp = getIdentifierRule(identifierRule);
                    $scope.startWith = identifierRule.startWith;
                    $scope.endWith = identifierRule.endWith;
                    $scope.actionForDefaultValues = $scope.isSafwayGL() || $scope.isSafeworksGL() ? 'R' : identifierRule.actionForDefaultValues;

                    $scope.required = ($scope.plsIdentifier !== 'PRO' || $scope.shipment.status === 'DELIVERED')
                            && (identifierRule.required || $scope.$root.isFieldRequired($scope.plsIdentifierDetails.permission));

                    setInvalidIdentifier();

                    $scope.disabledIdentifier = !srv.identifiers.isEmptyDefaultValue(identifierRule) && $scope.actionForDefaultValues === 'R'
                            && getModelValue() === identifierRule.defaultValue;
                }

                $scope.$watch('[shipment.originDetails.address.zip.zip, shipment.destinationDetails.address.zip.zip, shipment.shipmentDirection, ' +
                        'shipment.billTo.address.addressName, shipment.status]', function (newValues) {

                    if (_.every(newValues)) {
                        updateDefaultValue();
                        $scope.isModelValueFormatWarn = $scope.isModelValueWarn = $scope.isModelFormatWarn = $scope.isModelFormatError =
                                $scope.isModelInvalid = $scope.isModelValueError = false;
                    }
                }, true);

                $scope.$watch('inputModel', function(newValue, oldValue) {
                    if (oldValue !== newValue) {
                        _.set($scope.shipment, $scope.plsIdentifierDetails.field, newValue);
                    }
                });

                $scope.$watch('shipment.' + $scope.plsIdentifierDetails.field, function (newValue) {
                    $scope.inputModel = newValue;
                    var stringValue;

                    if ($scope.plsIdentifier === 'JOB') {
                        stringValue = !_.isEmpty(newValue) && newValue[0].jobNumber ? newValue[0].jobNumber.toString() : '';
                        if (_.isUndefined(newValue)) {
                            _.set($scope.shipment, $scope.plsIdentifierDetails.field, []);
                            $scope.inputModel = [];
                        }
                    } else {
                        stringValue = angular.isDefined(newValue) ? newValue.toString() : '';
                    }

                    $scope.isModelInvalid = true;
                    $scope.isModelValueFormatWarn = $scope.isModelValueWarn = $scope.isModelFormatWarn = $scope.isModelFormatError = false;

                    if (!_.isEmpty($scope.defaultValueBackup) && newValue !== $scope.defaultValueBackup
                            && !new RegExp($scope.ruleExp).test(stringValue)) {
                        $scope.isModelValueFormatWarn = true;
                    } else if (!_.isEmpty($scope.defaultValueBackup) && stringValue !== $scope.defaultValueBackup) {
                        if (stringValue !== $scope.defaultValueBackup && $scope.actionForDefaultValues === 'R') {
                            $scope.isModelValueError = true;
                        } else {
                            $scope.isModelValueWarn = true;
                        }
                    } else if ($scope.plsIdentifier !== 'JOB' && !_.isEmpty(newValue) && !new RegExp($scope.ruleExp).test(stringValue)) {
                        if ($scope.actionForDefaultValues === 'R') {
                            $scope.isModelFormatError = true;
                        } else {
                            $scope.isModelFormatWarn = true;
                        }
                    } else {
                        $scope.isModelInvalid = $scope.isModelValueError = false;
                    }

                    setInvalidIdentifier();
                }, $scope.plsIdentifier === 'JOB');
            }]
        };
    }
]);