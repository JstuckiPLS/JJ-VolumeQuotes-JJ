/**
 * This directive allows to bind &lt;input type="file" / &gt; element to AngularJS controller.
 * It is required because standard Angular's ng-model doesn't work with &lt;input type="file" / &gt;
 *
 * <h2>Usage</h2
 * >
 * In view file:
 * &lt;input type="file" data-pls-file-bind="myFileInput" &gt;
 *
 * In controller:
 * $scope.myFileInput.files[0] // Selected file
 */
angular.module('plsApp.directives').directive('plsFileBind', function () {
    return {
        restrict: 'A',
        scope: {
            plsFileBind: '='
        },
        link: function (scope, el) {
            'use strict';

            el.bind('change', function (event) {
                scope.plsFileBind = event.target;
                scope.$apply();
            });
        }
    };
});