angular.module('plsApp.directives').directive('plsDynamicRegexp', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            model: '=',
            matchCondition: '@',
            matchExp: '@',
            notMatchExp: '@'
        },
        link: function ($scope, elm, attrs, ctrl) {
            'use strict';

            var regExp;

            function validator(val) {
                if (!val) {
                    return undefined;
                }

                var valid = regExp.test(val);

                if (valid) {
                    ctrl.$setValidity('plsDynamicRegexp', true);
                    return val;
                }

                ctrl.$setValidity('plsDynamicRegexp', false);
                return undefined;
            }

            function applyExpression(keyValue) {
                regExp = keyValue === $scope.matchCondition ? new RegExp($scope.matchExp) : new RegExp($scope.notMatchExp);
                ctrl.$parsers.push(validator);
                ctrl.$formatters.push(validator);
            }

            if ($scope.model !== undefined && $scope.model === $scope.matchCondition) {
                applyExpression($scope.model);
            }

            $scope.$watch('model', function (newValue, oldValue) {
                if (newValue !== oldValue && newValue !== undefined) {
                    applyExpression(newValue);
                    ctrl.$setViewValue(ctrl.$viewValue);
                }
            });
        }
    };
});