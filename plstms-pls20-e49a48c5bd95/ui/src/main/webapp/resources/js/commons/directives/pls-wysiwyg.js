/**
 * AngularJS directive which provides WYSIWYG editing area based on textAngular directive.
 */
angular.module('plsApp.directives').directive('plsWysiwyg', ['$http', '$compile', function ($http, $compile) {
    return {
        restrict: 'A',
        scope: {
            parameters: '=',
            plsWysiwyg: '=',
            editorClass: '@',
            htmlTemplate: '@',
            disabled: '='
        },
        templateUrl: 'pages/tpl/wysiwyg-editor-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            var content;

            $scope.$watch('htmlTemplate', function (newValue, oldValue) {
                if (newValue && (newValue !== oldValue || !content)) {
                    $http.get(newValue).then(function (result) {
                        $scope.plsWysiwyg = $compile(result.data)($scope).html();
                        content = $scope.plsWysiwyg;
                    });
                }
            });
        }]
    };
}]);