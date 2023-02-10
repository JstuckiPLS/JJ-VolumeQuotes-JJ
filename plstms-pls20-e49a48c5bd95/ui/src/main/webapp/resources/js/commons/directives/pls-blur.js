/**
 * AngularJS bind for onblur event.
 */
angular.module('plsApp.directives').directive('plsBlur', function () {
    return function (scope, elem, attrs) {
        'use strict';

        elem.bind('blur', function () {
            scope.$apply(attrs.plsBlur);
        });
    };
});