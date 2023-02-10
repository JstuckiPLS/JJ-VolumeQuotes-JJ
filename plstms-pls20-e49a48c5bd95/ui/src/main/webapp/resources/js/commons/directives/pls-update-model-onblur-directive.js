/**
 * AngularJS directive which overrides the input element to change model value on blur event.
 * @author Mikhail Boldinov
 */
angular.module('plsApp.directives').directive('plsUpdateModelOnblur', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, elm, attr, ngModelCtrl) {
            'use strict';

            elm.unbind('input').unbind('keydown').unbind('change');

            elm.bind('blur', function () {
                if (scope.$$phase) {
                    ngModelCtrl.$setViewValue(elm.val());
                } else {
                    scope.$apply(function () {
                        ngModelCtrl.$setViewValue(elm.val());
                    });
                }
            });
        }
    };
});