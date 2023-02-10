/**
 * AngularJS directive which displays is element selected (element contains icon and text).
 *
 * @author: Sergey Kirichenko
 * Date: 5/20/13
 * Time: 3:55 PM
 */
angular.module('plsApp.directives').directive('plsSelectedItem', function () {
    'use strict';
    return {
        restrict: 'A',
        scope: {
            isSelected: '=plsSelectedItem',
            label: '@'
        },
        replace: true,
        template: '<span><i class="icon-ok" data-ng-class="{invisible:!isSelected}"></i>' +
        '<span data-ng-class="{muted:!isSelected}" class="nowrap"> {{label}}</span></span>'
    };
});