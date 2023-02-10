/**
 * AngularJS directive which displays type-ahead input for zip search country search and selected result.
 *
 * @author: Sergey Kirichenko
 * Date: 12/17/13
 * Time: 3:30 PM
 */
angular.module('plsApp.directives').directive('plsTriStateCheckbox', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            'use strict';

            if (!attrs.type || attrs.type !== 'checkbox' || !attrs.ngModel) {
                //wrong element or model is not defined
                return;
            }

            var trueValue = attrs.ngTrueValue || true;
            var falseValue = attrs.ngFalseValue || true;

            function setupState(value) {
                element[0].indeterminate = value !== trueValue && value !== falseValue;
            }

            scope.$watch(attrs.ngModel, function (newValue, oldValue) {
                if (newValue !== oldValue && !_.isUndefined(newValue) && !_.isNull(newValue)) {
                    setupState(newValue);
                }
            });

            //fix for checkbox in the grid
            attrs.$observe('id', function () {
                var value = scope.$eval(attrs.ngModel);

                if (!_.isUndefined(value) && !_.isNull(value)) {
                    setupState(value);
                }
            });
        }
    };
});
