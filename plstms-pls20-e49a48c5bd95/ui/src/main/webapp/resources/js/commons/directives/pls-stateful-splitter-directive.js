angular.module('plsApp.directives').directive('plsStatefulSplitter', ['$timeout', '$cookies', function ($timeout, $cookies) {
    return {
        scope: {
            onAction: '&'
        },
        link: function (scope, element) {
            'use strict';

            var visibleTabCookieId = 'visibleTab';
            var upperTabId = 'upper';
            var bottomTabId = 'bottom';
            var firstButton = element.find('[ui-splitbar] a:first');
            var lastButton = element.find('[ui-splitbar] a:last');

            function saveVisibleTabId(id) {
                $cookies[visibleTabCookieId] = id;
            }

            function getVisibleTabId() {
                return $cookies[visibleTabCookieId];
            }

            firstButton.on('click', function (event) {
                scope.onAction();

                if (getVisibleTabId() === bottomTabId && event.originalEvent instanceof MouseEvent) {
                    saveVisibleTabId(undefined);
                } else {
                    saveVisibleTabId(bottomTabId);
                }
            });

            lastButton.on('click', function (event) {
                scope.onAction();

                if (getVisibleTabId() === upperTabId && event.originalEvent instanceof MouseEvent) {
                    saveVisibleTabId(undefined);
                } else {
                    saveVisibleTabId(upperTabId);
                }
            });

            $timeout(function () {
                if (getVisibleTabId()) {
                    if (getVisibleTabId() === bottomTabId) {
                        firstButton.click();
                    } else {
                        lastButton.click();
                    }
                }
            });
        }
    };
}]);
