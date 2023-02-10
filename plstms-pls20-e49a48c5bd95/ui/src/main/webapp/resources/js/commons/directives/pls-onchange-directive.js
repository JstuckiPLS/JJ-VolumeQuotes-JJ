/**
 * AngularJS bind for onchange event.
 */
angular.module('plsApp.directives').directive('plsOnchange', function () {
    return function (scope, elem, attrs) {
        'use strict';

        elem.change(function () {
            scope.$apply(attrs.plsOnchange + "('" + $(this).val() + "')");
        });
    };
});