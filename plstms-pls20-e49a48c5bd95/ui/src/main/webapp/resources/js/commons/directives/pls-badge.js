/**
 * This directive will adjust position of current element relative to parent element.
 * It's used to display small badge on the top of the tab element.
 *
 * Usage example:
 *      <li class="pls-menu-item">
 *          <a href="#/financialBoard/errors">Invoice Errors</a>
 *          <span class="badge badge-important pls-badge">3</span>
 *      </li>
 *
 * @author Aleksandr Leshchenko
 */
angular.module('plsApp.directives').directive('plsBadge', function () {
    return {
        restrict: 'C',
        link: function postLink($scope, element) {
            'use strict';

            element.css('position', 'absolute');

            $scope.$watch(function () {
                var parent = element.parent('li');
                var offset = parent.offset();

                return {
                    left: offset.left,
                    top: offset.top,
                    width: parent.outerWidth()
                };
            }, function (newValue) {
                var top = newValue.top - element.outerHeight() / 2;
                var left = newValue.left + newValue.width - element.outerWidth() - 10;
                element.css('left', left + 'px');
                element.css('top', top + 'px');
            }, true);
        }
    };
});
