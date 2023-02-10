angular.module('plsApp.directives').directive('plsNumber', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            'use strict';

            // Retrieve  plsNumber attribute parameter
            // e.g. <input type="text" ... data-pls-number="cost" />
            var numberType = attrs.plsNumber;
            var oldValue = '';

            // For cost fields: 123456.89
            var costRegexp = new RegExp('^-?((\\d{1,6}(\\.\\d{1,2})?)|(\\.\\d{1,2}))$');
            // For cost with exceeding integer part
            var costIntExceedsRegexp = new RegExp('^-?((\\d{1,7}(\\.\\d{1,2})?)|(\\.\\d{1,2}))$');
            // For cost with exceeding decimal part
            var costDecExceedsRegexp = new RegExp('^-?((\\d{1,6}(\\.\\d{1,3})?)|(\\.\\d{1,3}))$');
            // For number fields with 3 decimals: 123456.899
            var fuelCostRegexp = new RegExp('^-?((\\d{1,6}(\\.\\d{1,3})?)|(\\.\\d{1,3}))$');
            // For positive percentage values with 2 decimal places and accepts values less than 100
            var positivePercRegexp = new RegExp('^(\\d{0,2})(\\.\\d{1,2})?$');
            // For positive decimal values with 2 decimal places and accepts zero
            var positiveZeroRegex = new RegExp('^(\\d+)(\\.\\d{1,2})?$');
            // For positive percentage values with 5 decimal places
            var positiveDecimalRegexp = new RegExp('^(\\d{0,5})(\\.\\d{1,2})?$');
            // For positive negative decimal
            var positiveNegativeDecimalRegex = new RegExp('^-?((\\d{1,7}(\\.\\d{1,2})?)|(\\.\\d{1,2}))$');

            // Just checks if the string is a number
            function anyNumberRegexp() {
                var integral = attrs.integral ? '{1,' + attrs.integral + '}' : '+';
                var fractional = attrs.fractional ? '{0,' + attrs.fractional + '}' : '+';
                return new RegExp('^((\\d' + integral + '(\\.\\d' + fractional + ')?)|(\\.\\d' + fractional + '))$');
            }

            //return object that contains length of integral part of the value and length of decimal part of the value
            function getNumberPartsLength(value) {
                var splitValue = value.split('.');

                return {
                    intLength: splitValue[0] ? splitValue[0].length : 0,
                    decLength: splitValue[1] ? splitValue[1].length : 0
                };
            }

            //returns true if one of the number's part(int or dec part) exceeds it length
            function checkCostExceedsLength(viewValue) {
                if (oldValue && (costIntExceedsRegexp.test(viewValue) || costDecExceedsRegexp.test(viewValue))) {
                    var oldParts = getNumberPartsLength(oldValue);
                    var newParts = getNumberPartsLength(viewValue);
                    return (oldParts.intLength === newParts.intLength && oldParts.decLength + 1 === newParts.decLength)
                            || (oldParts.intLength + 1 === newParts.intLength && oldParts.decLength === newParts.decLength);
                }
                //no need to check because old value is not set or there was no exceeding of length
                return false;
            }

            function ifDefined(viewValue) {
                return viewValue !== undefined && viewValue !== null && viewValue !== '';
            }

            var validator = function (viewValue) {
                var valid = true;
                numberType = attrs.plsNumber;

                // Select proper validator for given number type
                var validationalRegexp = null;

                switch (numberType) {
                    case 'cost' :
                        validationalRegexp = costRegexp;
                        break;
                    case 'fuelCost' :
                        validationalRegexp = fuelCostRegexp;
                        break;
                    case 'positivePercentage' :
                        validationalRegexp = positivePercRegexp;
                        break;
                    case 'positiveZeroDecimal' :
                        validationalRegexp = positiveZeroRegex;
                        break;
                    case 'positiveDecimal' :
                        validationalRegexp = positiveDecimalRegexp;
                        break;
                    case 'positiveNegativeDecimal':
                        validationalRegexp = positiveNegativeDecimalRegex;
                        break;
                    default:
                        validationalRegexp = anyNumberRegexp();
                }

                if (ifDefined(viewValue)) {
                    valid = validationalRegexp.test(viewValue);

                    //check if value equals zero(only if forbidZero attribute is defined)
                    if (valid && attrs.forbidZero && scope.$eval(attrs.forbidZero) && parseFloat(viewValue) === 0) {
                        valid = false;
                    }

                    //check if value exceeds max value(only if max attribute is defined)
                    if (valid && attrs.max && parseFloat(viewValue) > parseFloat(attrs.max)) {
                        valid = false;
                    }

                    if (numberType === 'cost' || numberType === 'fuelCost') {
                        if (valid) {
                            oldValue = viewValue;
                        } else if (checkCostExceedsLength(viewValue)) {
                            //reset to the old value
                            ctrl.$viewValue = oldValue;
                            ctrl.$render();
                            return ctrl.$modelValue;
                        }
                    }
                }

                ctrl.$setValidity('format', valid);
                return valid && ifDefined(viewValue) ? parseFloat(viewValue) : undefined;
            };

            ctrl.$parsers.unshift(validator);
            ctrl.$formatters.push(validator);
        }
    };
});