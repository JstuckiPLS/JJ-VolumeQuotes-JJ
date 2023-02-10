/**
 * AngularJS directive for filtering numeric data input,
 * allow only numbers to be typed into a text box.
 *
 * @author: Dmitry Nikolaenko
 */
angular.module('plsApp.directives').directive('plsDigitsValidation', function () {
    return {
        restrict: 'A',
        require: '?ngModel',
        link: function (scope, element, attrs, modelCtrl) {
            'use strict';

            modelCtrl.$parsers.push(function () {
                var inputValue = modelCtrl.$viewValue;

                if (!inputValue) {
                    return '';
                }

                var verifiedValue = inputValue.toString().replace(/[\D\.]+/g, '');

                if (verifiedValue !== inputValue) {
                    modelCtrl.$setViewValue(verifiedValue);
                    modelCtrl.$render();
                }

                return verifiedValue;
            });
        }
    };
});