/**
 * This directive will append\remove CSS class "active" for the enclosed link
 * depending on the current URL.
 *
 * @author Eugene Borshch
 */
angular.module('plsApp.directives').directive('plsMenuItem', ['$location', function ($location) {
    return {
        restrict: 'C',
        link: function postLink($scope, element) {
            'use strict';

            var link = element.find('a');
            if (!link || !link.length) {
                return;
            }

            function hideElement() {
                if (!$scope.authData.canAccessURL(link.attr('href'))) {
                    element.css('display', 'none');
                    var dropdown = element.parents('.dropdown');
                    if (dropdown && dropdown.length) {
                        var active = _.filter(dropdown.find('.pls-menu-item'), function(el) {
                            var display = $(el).css('display');
                            return !display || display !== 'none';
                        });
                        if (active.length === 1) {
                            dropdown.replaceWith(active[0]);
                        } else if (!active.length) {
                            dropdown.css('display', 'none');
                        }
                    }
                }
            }

            hideElement();

            $scope.$on('event:permissions-were-changed', hideElement);

            // Append\Remove CSS class "active" depending on current path.
            $scope.$watch(function () {return $location.absUrl();}, function() {
                if ($location.absUrl().indexOf(link.attr('href')) !== -1) {
                    element.addClass('active');
                    element.parents('.dropdown').addClass('active');
                } else if (element.hasClass('active')) {
                    element.removeClass('active');
                    var dropdown = element.parents('.dropdown');
                    if (!dropdown.find('.active').length) { // we can't simply remove active class from dropdown menu,
                                                            // because user can redirect to another menu item within the same dropdown menu.
                                                            // in this case behavior will depend on the order $watch executions which is wrong
                        dropdown.removeClass('active');
                    }
                }
            });
        }
    };
}]);
