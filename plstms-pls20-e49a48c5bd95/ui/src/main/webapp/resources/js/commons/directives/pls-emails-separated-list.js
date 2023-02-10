/**
 * Directive for validate element with several emails separated by semicolon.
 *
 * @author Denis Zhupinsky (Team International)
 */

angular.module('plsApp.directives').directive('plsEmailsSeparatedList', function () {
    var EMAIL_REGEXP = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/; //copy of private angularjs variable

    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            'use strict';

            var validator = function (viewValue) {
                var valid = true;
                var modelValue = '';
                var counter;

                if (viewValue) {
                    var values = viewValue.split(';');

                    for (counter = 0; counter < values.length; counter += 1) {
                        var value = $.trim(values[counter]);
                        valid = EMAIL_REGEXP.test(value);

                        if (!valid) {
                            break;
                        }

                        modelValue += value;

                        if (counter < values.length - 1) {
                            modelValue += '; ';
                        }
                    }
                }

                ctrl.$setValidity('format', valid);

                if (valid) {
                    return modelValue;
                } else {
                    return undefined;
                }
            };

            ctrl.$formatters.push(validator);
            ctrl.$parsers.unshift(validator);
        }
    };
});