/**
 * Add and Edit Bill To Required Field in modal dialog
 *
 * @author Vitaliy Gavrilyuk
 */
angular.module('plsApp').directive('plsBillToIdentifierDialog', ['billToIdentifiers', 'plsJobIdRules',
    function (billToIdentifiers, plsJobIdRules) {
        return {
            restrict: 'A',
            controller: ['$scope', '$http', '$compile', '$templateCache', function ($scope, $http, $compile, $templateCache) {
                'use strict';

                /**
                 * Load html template, append it to the main content block to show it above another modal window.
                 * Remove template from DOM after switching to another controller (page).
                 */
                $http.get('pages/content/customer/billTo/templates/billto-identifier-dialog-tpl.html', {
                    cache: $templateCache
                }).then(function (result) {
                    var dialog = $compile(result.data)($scope);
                    angular.element('#content').append(dialog);

                    $scope.$on('$destroy', function () {
                        dialog.remove();
                    });
                });

                var rule = ['^', '', '.*', '', '$'];

                /**
                 * Settings for modal dialog. Need for dynamic assign parent dialog Id to show it above another modal window.
                 */
                $scope.dialogOptions = {
                    parentDialog: $scope.parentDialogId
                };

                /**
                 * Setting up input maximum length validity state.
                 *
                 * @param currentInput - current input model controller
                 * @param {number} length - length number for input
                 * @param {number} maxLength - maximum length number for current input
                 */
                function validateInputMaxLength(currentInput, length, maxLength) {
                    if (currentInput) {
                        currentInput.$setValidity('maxlength', length <= maxLength || !currentInput.$modelValue);
                    }
                }

                /**
                 * Setting up validity state for input after regular expression test and return state.
                 *
                 * @param currentInput - current input model controller
                 * @param {string} pattern - regular expression string for default value validation
                 * @param {string} warnText - error message string for displaying in the toast
                 * @return {boolean} - state if default value is valid or not
                 */
                function isValidInputPattern(currentInput, pattern, warnText) {
                    var value = $scope.reqFieldModel.name === 'JOB' && $scope.$root.isSafway($scope.customerId)
                            ? currentInput.$modelValue.toUpperCase() : currentInput.$modelValue;

                    if ((currentInput && _.isEmpty(currentInput.$modelValue)) || (new RegExp(pattern).test(value))) {
                        currentInput.$setValidity('pattern', true);
                        currentInput.$setViewValue(value);
                        return true;
                    } else {
                        currentInput.$setValidity('pattern', false);
                        toastr.error(warnText, 'Error!');
                        return false;
                    }
                }

                /**
                 * Check if the same directions already exist for the same type identifier and return state.
                 *
                 * @param {object} existingIdentifier - entity data of the same existing identifier
                 * @return {boolean} - state if the same directions exist or not for the same type identifier
                 */
                function isSameDirections(existingIdentifier) {
                    return ($scope.reqFieldModel.inboundOutbound === existingIdentifier.inboundOutbound
                            || $scope.reqFieldModel.inboundOutbound === 'B' || existingIdentifier.inboundOutbound === 'B')
                            && ($scope.reqFieldModel.originDestination === existingIdentifier.originDestination
                            || existingIdentifier.originDestination === 'B' || $scope.reqFieldModel.originDestination === 'B');
                }

                /**
                 * Check if some part of new address is not unique (exists) in the existing identifier with same type and return state.
                 *
                 * @param {object} newAddress - address entity data of the new/modified required field
                 * @param {object} existingIdentifier - entity data of the same existing identifier
                 * @return {boolean} - state if some part of new address is not unique (exists) in the existing identifier with same type
                 */
                function isNotUniqueAddress(newAddress, existingIdentifier) {
                    if (angular.isUndefined(existingIdentifier.address) || _.isEmpty(existingIdentifier.address) || angular.isUndefined(newAddress)
                            || _.isEmpty(newAddress)) {
                        return isSameDirections(existingIdentifier);
                    }

                    return _.some(existingIdentifier.address, function (value, key) {
                        if (value === '' || newAddress[key] === '') {
                            return isSameDirections(existingIdentifier);
                        }

                        /* Strict comparison each part in new/modified address with each part of existing address for same identifier  */
                        if (angular.isDefined(newAddress[key]) && angular.isDefined(value)) {
                            return _.some(newAddress[key].split(','), function (current) {
                                return _.some(value.split(','), function (old) {
                                    if (_.trim(old).toUpperCase() === _.trim(current).toUpperCase()) {
                                        return isSameDirections(existingIdentifier);
                                    }
                                });
                            });
                        }
                    });
                }

                /**
                 * Procedure where new or modified identifier is saved after all checks and validations.
                 */
                function saveReqField() {
                    delete $scope.reqFieldModel.self;

                    if (angular.isDefined($scope.selectedReqField[0])) {
                        var index = _.findIndex($scope.billToRequiredFields, function (value) {
                            return _.isEqual(value, $scope.selectedReqField[0]);
                        });

                        $scope.billToRequiredFields.splice(index, 1);
                        $scope.billToRequiredFields.push($scope.reqFieldModel);

                        $scope.billTo.billToRequiredFields.splice(index, 1);
                        $scope.billTo.billToRequiredFields.push($scope.reqFieldModel);
                        $scope.billToRequiredFields = angular.copy($scope.billToRequiredFields);

                        delete $scope.selectedReqField[0];
                        $scope.reqFieldModel = {};
                    } else {
                        delete $scope.reqFieldModel.id;
                        $scope.billToRequiredFields.push($scope.reqFieldModel);
                        $scope.billTo.billToRequiredFields.push($scope.reqFieldModel);
                    }

                    $scope.closeDialog();
                }

                /**
                 * Start all checks and validations for saving new or modified required field.
                 */
                function validateReqField() {
                    var isNotUnique = _.some($scope.billToRequiredFields, function (value) {
                        return $scope.reqFieldModel.name === value.name && !_.isEqual($scope.selectedReqField[0], value)
                                && isNotUniqueAddress($scope.reqFieldModel.address, value);
                    });

                    if (isNotUnique) {
                        toastr.error('Combination is not unique. There must be no contradictory combination for the selected identifier', 'Error!');
                    } else {
                        saveReqField();
                    }
                }

                function validateRuleMaxLength() {
                    var length = ($scope.reqFieldModel.startWith ? $scope.reqFieldModel.startWith.length : 0) +
                            ($scope.reqFieldModel.endWith ? $scope.reqFieldModel.endWith.length : 0);

                    validateInputMaxLength($scope.billToIdentifierForm.startWith, length, $scope.defaultValueMaxLength);
                    validateInputMaxLength($scope.billToIdentifierForm.endWith, length, $scope.defaultValueMaxLength);
                }

                function generateRule(item, index) {
                    rule[index] = item;
                    $scope.reqFieldModel.ruleExp = rule.join('');
                }

                /**
                 * Start maximum length validation on change Default Value input and reset pattern state.
                 */
                $scope.validateDefaultValueInput = function () {
                    var defaultValueModel = $scope.billToIdentifierForm.defaultValue;
                    var length = defaultValueModel && !_.isEmpty(defaultValueModel.$modelValue) ? defaultValueModel.$modelValue.length : 0;

                    validateInputMaxLength(defaultValueModel, length, $scope.defaultValueMaxLength);
                    defaultValueModel.$setValidity('pattern', true);
                };

                /**
                 * Watching for identifier change.
                 */
                $scope.$watch('reqFieldModel.name', function (newValue) {
                    if (newValue) {
                        /* Setting up Pro # as 'Required' by default */
                        $scope.reqFieldModel.required = $scope.reqFieldModel.id > 0 && newValue === $scope.selectedReqField[0].name
                                ? $scope.selectedReqField[0].required : newValue === 'PRO';

                        /* Removing defaultValue for Gargo Value */
                        if (newValue === 'CARGO') {
                            var cargoVal = parseInt($scope.reqFieldModel.defaultValue, 10);
                            $scope.reqFieldModel.defaultValue = (cargoVal < 1 || cargoVal > 999999) ? '' : cargoVal;
                        }

                        /* Dynamically setting up maximum length property for Default Value input */
                        $scope.defaultValueMaxLength = billToIdentifiers[newValue].maxLength;

                        var defaultValueModel = $scope.billToIdentifierForm.defaultValue;
                        var length = defaultValueModel && !_.isEmpty(defaultValueModel.$modelValue) ? defaultValueModel.$modelValue.length : 0;

                        validateInputMaxLength(defaultValueModel, length, $scope.defaultValueMaxLength);
                        defaultValueModel.$setValidity('pattern', true);
                        validateRuleMaxLength();
                    }
                });

                /**
                 * Deeply watching for any address input change.
                 */
                $scope.$watch('reqFieldModel.address', function (newValue) {
                    if (newValue) {
                        /* Clean up any empty address property */
                        _.forEach(newValue, function (value, key) {
                            if (_.isEmpty(value)) {
                                delete $scope.reqFieldModel.address[key];
                            }
                        });

                        /* Disable and select 'Both' option for Origin/Destination select if address is fully empty */
                        if (!_.isEmpty(newValue)) {
                            $scope.enabledOriginDestination = true;
                        } else {
                            $scope.enabledOriginDestination = false;
                            $scope.reqFieldModel.originDestination = 'B';
                        }
                    }
                }, true);

                $scope.$watch('reqFieldModel.startWith', function (newValue) {
                    if (angular.isDefined(newValue)) {
                        validateRuleMaxLength();
                        generateRule(newValue, 1);
                        $scope.billToIdentifierForm.defaultValue.$setValidity('pattern', true);
                    }
                });

                $scope.$watch('reqFieldModel.endWith', function (newValue) {
                    if (angular.isDefined(newValue)) {
                        validateRuleMaxLength();
                        generateRule(newValue, 3);
                        $scope.billToIdentifierForm.defaultValue.$setValidity('pattern', true);
                    }
                });

                $scope.setActionForDefaultValues = function () {
                    if (!$scope.reqFieldModel.startWith && !$scope.reqFieldModel.endWith && !$scope.reqFieldModel.defaultValue) {
                        $scope.reqFieldModel.actionForDefaultValues = undefined;
                    }
                };

                /**
                 * Start saving modal dialog model.
                 */
                $scope.saveDialog = function () {
                    var defaultValueModel = $scope.billToIdentifierForm.defaultValue;
                    var defaultValueRegexp = '';
                    var defaultValueText = '';

                    if (_.isEmpty($scope.reqFieldModel.actionForDefaultValues)) {
                        delete $scope.reqFieldModel.actionForDefaultValues;
                    }

                    if ($scope.$root.isSafway($scope.customerId) && $scope.reqFieldModel.name === 'JOB') {
                        defaultValueRegexp = plsJobIdRules.regExp;
                        defaultValueText = plsJobIdRules.warnText;
                    } else {
                        defaultValueRegexp = $scope.reqFieldModel.ruleExp;
                        defaultValueText = 'Incorrect format. Default Value should correspond to Start/End with format';
                    }

                    if (!isValidInputPattern(defaultValueModel, defaultValueRegexp, defaultValueText)) {
                        return;
                    }

                    _.forEach($scope.reqFieldModel.address, function (value, key) {
                        $scope.reqFieldModel.address[key] = _.uniq(_.map(value.toUpperCase().split(','), _.trim)).join(', ');
                    });

                    /* Send new/modified address entity data for existence checking */
                    $http.post('/restful/zip/validate_address', $scope.reqFieldModel.address).then(function (result) {
                        if (angular.isDefined(result.data) && !_.isEmpty(result.data)) {
                            /* Show toast error notification for each address type */
                            _.forEach(result.data, function (value, key) {
                                if (!_.isEmpty(value)) {
                                    toastr.error('Address: "' + result.data[key] + '" does not exist in PLS PRO 2.0', 'Error!');
                                }
                            });
                        } else {
                            validateReqField();
                        }
                    });
                };

                /**
                 * Close modal dialog without saving.
                 */
                $scope.closeDialog = function () {
                    $scope.billToIdentifierForm.defaultValue.$setValidity('pattern', true);
                    $scope.reqFieldModel = {};
                    rule = ['^', '', '.*', '', '$'];
                    $scope.showDialog = false;
                };
            }]
        };
    }
]);