angular.module('plsApp.directives').directive('plsModal', ['$parse', function ($parse) {

    var body = angular.element(document.getElementsByTagName('body')[0]);

    var defaultOpts = {
        escape: true,
        ok: true
    };

    return {
        restrict: 'EA',
        link: function (originalScope, elm, attrs) {
            var scope = originalScope.$new();

            var opts;

            function evaluateOptions() {
                opts = angular.extend(angular.copy(defaultOpts), scope.$eval(attrs.uiOptions || attrs.bsOptions || attrs.options));
            }

            evaluateOptions();

            var shownExpr = attrs.plsModal || attrs.show;
            var setClosed;

            if (attrs.close) {
                setClosed = function () {
                    scope.$apply(attrs.close);
                };
            } else {
                setClosed = function () {
                    scope.$apply(function () {
                        $parse(shownExpr).assign(scope, false);
                    });
                };
            }
            elm.addClass('modal');

            function escapeClose(evt) {
                if (evt.which === 27) {
                    setClosed();
                }
            }

            function enter(evt) {
                var isTextArea = evt.target.nodeName === 'TEXTAREA';
                var isButton = evt.target.nodeName === 'BUTTON';
                var isTypeAhead = evt.target.hasAttribute('data-pls-zip-search') || evt.target.hasAttribute('data-pls-country-search');
                var isTextAngular = evt.target.hasAttribute('contenteditable') && evt.target.getAttribute('contenteditable') === 'true';
                if (isTextArea || isButton || isTypeAhead || isTextAngular) {
                    return;
                }
                if (attrs.enterAllowed && attrs.enterAllowed !== 'true') {
                    return;
                }
                if (evt.which === 13) {
                    if (attrs.enter) {
                        scope.$apply(attrs.enter);
                    }
                }
            }

            function clickClose() {
                setClosed();
            }

            function navigationClick(evt) {
                // 9 = tab key code
                // 33-40 = arrow codes + home + end + page up/down
                if (evt.which === 9 || (evt.which >= 33 && evt.which <= 40)) {
                    // if event occurred outside of pop up
                    if (elm.find(evt.target).length === 0) {
                        evt.preventDefault();
                    }
                }
            }

            function showBackdrop() {
                if (!originalScope.$root.backdropEl) {
                    originalScope.$root.backdropEl = angular.element('<div class="modal-backdrop"></div>');
                    originalScope.$root.backdropEl.css('display', 'none');
                    body.append(originalScope.$root.backdropEl);
                }
                originalScope.$root.backdropEl.css('display', 'block').addClass('in');
                body.css('overflow', 'hidden');
            }

            function close(justRendered, initClose) {
                if (scope.parentDialogEl && scope.parentDialogEl.length !== 0) {
                    scope.parentDialogEl.css('z-index', '1050');
                } else if (!initClose && originalScope.$root.backdropEl) {
                    originalScope.$root.backdropEl.css('display', 'none').removeClass('in');
                    originalScope.$root.backdropEl.removeClass('aboveLoginBackdrop');
                    body.css('overflow', 'auto');
                }
                elm.css('display', 'none').removeClass('in');
                elm.removeClass('aboveLogin');
                if (!justRendered) {
                    scope.$root.$emit('event:dialogIsClosed', scope);
                }
            }

            function open() {
                evaluateOptions();
                if (opts.parentDialog) {
                    scope.parentDialogEl = angular.element(document.getElementById(opts.parentDialog));
                    if (scope.parentDialogEl && scope.parentDialogEl.length !== 0) {
                        scope.parentDialogEl.css({'zIndex': '1030'});
                    } else {
                        showBackdrop();
                    }
                } else {
                    scope.parentDialogEl = undefined;
                    showBackdrop();
                }
                elm.css('display', 'block').addClass('in');
                if (attrs.showAboveLogin === true || attrs.showAboveLogin === 'true'
                        || opts.showAboveLogin === true || opts.showAboveLogin === 'true') {
                    elm.addClass('aboveLogin');
                    originalScope.$root.backdropEl.addClass('aboveLoginBackdrop');
                }
                scope.$root.$emit('event:dialogIsOpen', scope);

                // remove focus from all elements
                $(':focus').blur();
            }

            function unbindEvents() {
                body.unbind('keydown', navigationClick);

                if (opts.escape) {
                    body.unbind('keyup', escapeClose);
                }
                if (opts.ok) {
                    body.unbind('keypress', enter);
                }
            }

            function bindEvents() {
                body.bind('keydown', navigationClick);

                if (opts.escape) {
                    body.bind('keyup', escapeClose);
                }
                if (opts.ok) {
                    body.bind('keypress', enter);
                }
            }

            originalScope.$on('$destroy', function () {
                if (scope.$eval(shownExpr)) {
                    unbindEvents();
                }
                scope.$destroy();
            });

            scope.$root.$on('event:dialogIsOpen', function (event, targetScope) {
                if (targetScope === scope) {
                    bindEvents();
                } else {
                    unbindEvents();
                }
            });

            scope.$root.$on('event:dialogIsClosed', function (event, targetScope) {
                if (targetScope === scope) {
                    unbindEvents();
                } else if ((!targetScope.parentDialogEl || targetScope.parentDialogEl.attr('id') === elm.attr('id')) && scope.$eval(shownExpr)) {
                    bindEvents();
                }
            });

            scope.$watch(shownExpr, function (isShown, oldShown) {
                if (isShown !== oldShown) {
                    if (isShown) {
                        open();
                    } else {
                        close();
                    }
                }
            });

            close(true, true);

            scope.$on("$destroy", function () {
                if (scope.$eval(shownExpr)) {
                    close();
                }
            });
        }
    };
}]);