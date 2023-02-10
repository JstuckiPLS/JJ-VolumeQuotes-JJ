/**
 * AngularJS directive which cleans field type of "Input".
 *
 * For using the directive plsInputCleaning you need to announce it using elements of "div" type which contains "input" types components that you want
 * to clean. So you have to throw "event: cleaning-input" every time you want to clear the fields.
 *
 * @author: Brichak Aleksandr
 */
angular.module('plsApp.directives').directive('plsInputCleaning', function () {
    return {
        restrict: 'A',
        link: function (scope, el) {
            'use strict';

            scope.$on('event:cleaning-input', function () {
                el.find("input[type=text][data-ng-model],input[type=text][ng-model]," +
                        "input[type=email][data-ng-model],input[type=email][ng-model]," +
                        "input[type=password][data-ng-model], textarea[data-ng-model],textarea[ng-model], select[data-ng-model]").each(function () {
                    angular.element(this).controller('ngModel').$setViewValue('');
                    angular.element(this).controller('ngModel').$render();
                });
            });
        }
    };
});