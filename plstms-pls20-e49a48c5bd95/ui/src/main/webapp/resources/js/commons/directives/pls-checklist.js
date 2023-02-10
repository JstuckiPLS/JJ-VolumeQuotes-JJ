/**
 * AngularJS directive.
 */
angular.module('plsApp.directives').directive('plsCheckList', [function () {
    return {
        restrict: 'A',
        priority: 10,
        scope: {
            list: '=',
            model: '='
        },
        replace: true,
        template: '<div class="span12 table-bordered plsCheckList" style="background-color: #fff"></div>',
        compile: function (element) {
            'use strict';

            element.append('<label data-ng-repeat="element in list">'
                    + '<input type="checkbox" checklist-model="model" checklist-value="element" class="checkbox-label-spacing">{{element}}</label>');
        }
    };
}]);
