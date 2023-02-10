/**
 * Customize popover that can contains html code inside itself.
 * To fill it with html content you need to specify id of element that need to be placed at popover.
 *
 * @author Denis Zhupinsky (Team International)
 */
angular.module('plsApp.directives').directive('plsPopover', function () {
    return {
        restrict: 'A',
        scope: true,
        link: function (scope, el, attrs) {
            'use strict';

            var mouseenter, mouseleave;

            attrs.$observe('plsPopover', function (val) {
                scope.contentEl = val ? angular.element('#' + val) : angular.element(el[0].children[0]);
                scope.contentEl.hide();

                scope.popover = el.popover({
                    html: true,
                    content: scope.contentEl,
                    position: scope.position,
                    isOpen: false,
                    title: attrs.title || null,
                    placement: attrs.placement || 'bottom',
                    trigger: 'manual', // trigger and animation attributes are needed for proper displaying of popover. DON'T REMOVE THEM!!!
                    animation: false // trigger and animation attributes are needed for proper displaying of popover. DON'T REMOVE THEM!!!
                });

                function shiftPopover() {
                    var popoverDiv = scope.contentEl[0].parentNode.parentNode;

                    if (popoverDiv) {
                        var arrowDiv = angular.element('.arrow')[0];
                        var defaultArrowOffset = -11; //default bootstrap popover's offset

                        if (attrs.shiftLeft) {
                            var shiftLeft = parseInt(attrs.shiftLeft, 10);
                            if (shiftLeft) {
                                popoverDiv.style.left = (popoverDiv.offsetLeft - shiftLeft) + 'px';
                                arrowDiv.style.marginLeft = shiftLeft + defaultArrowOffset + 'px';
                            }
                        }
                        if (attrs.shiftRight) {
                            var shiftRight = parseInt(attrs.shiftRight, 10);
                            if (shiftRight) {
                                popoverDiv.style.left = (popoverDiv.offsetLeft + shiftRight) + 'px';
                                arrowDiv.style.marginLeft = -shiftRight + defaultArrowOffset + 'px';
                            }
                        }
                        if (attrs.shiftTop) {
                            var shiftTop = parseInt(attrs.shiftTop, 10);
                            if (shiftTop) {
                                popoverDiv.style.top = (popoverDiv.offsetTop - shiftTop) + 'px';
                                arrowDiv.style.marginTop = shiftTop + defaultArrowOffset + 'px';
                            }
                        }
                        if (attrs.shiftBottom) {
                            var shiftBottom = parseInt(attrs.shiftBottom, 10);
                            if (shiftBottom) {
                                popoverDiv.style.top = (popoverDiv.offsetTop + shiftBottom) + 'px';
                                arrowDiv.style.marginTop = -shiftBottom + defaultArrowOffset + 'px';
                            }
                        }
                    }
                }

                mouseleave = function () {
                    var delay = 200; // 0.2 seconds delay after last input

                    if (scope.timeoutId) {
                        clearTimeout(scope.timeoutId);
                        scope.timeoutId = undefined;
                    }

                    scope.timeoutId = setTimeout(function () {
                        if (scope.timeoutId) {
                            scope.popover.isOpen = false;
                            scope.timeoutId = undefined;
                            scope.contentEl.unbind('mouseenter');
                            el.popover("hide");
                            scope.contentEl.unbind('mouseleave');
                        }
                    }, delay);
                };

                mouseenter = function () {
                    if (scope.timeoutId) {
                        clearTimeout(scope.timeoutId);
                        scope.timeoutId = undefined;
                    }

                    if (!scope.popover.isOpen) {
                        scope.contentEl.show();
                        el.popover("show");
                        shiftPopover();
                        scope.popover.isOpen = true;
                        scope.contentEl.bind('mouseenter', mouseenter);
                        scope.contentEl.bind('mouseleave', mouseleave);
                    }
                };

                el.mouseenter(mouseenter);
                el.mouseleave(mouseleave);
            });
        }
    };
});