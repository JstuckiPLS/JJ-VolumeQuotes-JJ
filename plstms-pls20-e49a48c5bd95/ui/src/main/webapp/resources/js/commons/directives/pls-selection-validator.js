angular.module('plsApp.directives').directive('plsSelectionValidator', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            'use strict';

            var validator = function (viewValue) {
                var optionList = scope.$eval(attrs.optionList);

                var isValid = !_.find(optionList, function (item) {
                    return item.id === viewValue && item.invalid;
                });

                ctrl.$setValidity('format', isValid);
                return viewValue;
            };

            ctrl.$parsers.unshift(validator);
            ctrl.$formatters.push(validator);
        }
    };
});