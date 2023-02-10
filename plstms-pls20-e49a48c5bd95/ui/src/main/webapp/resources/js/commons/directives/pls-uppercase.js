/**
 * AngularJS directive that converts input string to uppercase.
 *
 * @author: Sergey Kirichenko
 * Date: 5/12/14
 * Time: 3:39 PM
 */
angular.module('plsApp.directives').directive('plsUppercase', ['$timeout', function ($timeout) {
    return {
        restrict: 'A',
        require: '?ngModel',
        link: function (scope, elm, attrs, ctrl) {
            'use strict';

            // ignore fields without ng-model
            if (!ctrl) {
                return;
            }

            function plsUppercase(value, onChange) {
                if (value && angular.isString(value) && value !== angular.uppercase(value)) {
                    var newValue = angular.uppercase(value);
                    onChange(newValue);
                    return newValue;
                }

                return value;
            }

            function plsUpperFormatter(value) {
                return plsUppercase(value, function (newValue) {
                    $timeout(function () {
                        ctrl.$setViewValue(newValue);
                    });
                });
            }

            function plsUpperParser(value) {
                return plsUppercase(value, function (newValue) {
                    ctrl.$viewValue = newValue;
                    ctrl.$render();
                });
            }

            ctrl.$formatters.push(plsUpperFormatter);
            ctrl.$parsers.unshift(plsUpperParser);
        }
    };
}]);
