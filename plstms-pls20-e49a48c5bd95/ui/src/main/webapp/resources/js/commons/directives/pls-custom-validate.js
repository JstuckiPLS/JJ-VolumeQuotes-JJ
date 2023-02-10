/**
 * Directive that validate and highlights element.
 *
 * @author: Dmitry Nikolaenko
 */
angular.module('plsApp.directives').directive('plsCustomValidate', function () {
    return {
        restrict: 'A',
        require: '?ngModel',
        scope: {
            plsCustomValidate: '='
        },
        link: function (scope, elm, attrs, ctrl) {
            'use strict';

            scope.$watch('plsCustomValidate', function () {
                ctrl.$setValidity('customValidation', scope.plsCustomValidate);
            });
        }
    };
});