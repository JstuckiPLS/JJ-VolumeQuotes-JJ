angular.module('plsApp.directives')

/**
 * @description
 * Prevents ngModelController from nulling the model value when it's set invalid by some rule.
 * Works in both directions, making sure an invalid model value is copied into the view value and making sure an invalid
 * model value is copied into the model. **Warning:** totally bypasses formatters/parsers when invalid, but probably good 
 * enough to use in most cases, like maxlength or pattern.
 *
 * Usage: 
 * JS   : $scope.foo = 'abcdef';
 * HTML : <input type="text" ng-model="bar" pls-validity-bind ng-maxlength="5" />
 *
 * @param {object} ngModel Required `ng-model` value.
 */
.directive('plsValidityBind', function () {
    'use strict';

    return {
        require: '?ngModel',
        priority: 9999,
        restrict: 'A',
        link: function ($scope, $element, $attrs, ngModelController) {
            if (ngModelController) {
                ngModelController.$formatters.unshift(function (value) {
                    if (ngModelController.$invalid && angular.isUndefined(value)) {
                        return ngModelController.$modelValue;
                    } else {
                        return value;
                    }
                });
                ngModelController.$parsers.push(function (value) {
                    if (ngModelController.$invalid && angular.isUndefined(value)) {
                        return ngModelController.$viewValue;
                    } else {
                        return value;
                    }
                });
            }
        }
    };
});