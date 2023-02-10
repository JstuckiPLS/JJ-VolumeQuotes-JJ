/**
 * A helper service that can parse typeahead's syntax (string provided by users)
 * Extracted to a separate service for ease of unit testing
 */
angular.module('plsApp.directives').factory('plsTypeaheadParser', ['$parse', function ($parse) {
    'use strict';

    // 000001110000000000000222000000000000000033300000000000000004444444444444440000000000055000
    var TYPEAHEAD_REGEXP = /^\s*(.*?)(?:\s+as\s+(.*?))?(?:\s+lbl\s+(.*?))?\s+for\s+(?:([\$\w][\$\w\d]*))\s+in\s+(.*)$/;

    return {
        parse: function (input) {

            var match = input.match(TYPEAHEAD_REGEXP);

            if (!match) {
                throw new Error(
                        "Expected typeahead specification in form of '_modelValue_ (as _label_)? (lbl _option_label_)? for _item_ in _collection_'" +
                        " but got '" + input + "'.");
            }

            return {
                itemName: match[4],
                source: $parse(match[5]),
                labelMapper: $parse(match[3] || match[2] || match[1]),
                viewMapper: $parse(match[2] || match[1]),
                modelMapper: $parse(match[1])
            };
        }
    };
}]);

//options - min length
angular.module('plsApp.directives').directive('plsTypeahead', ['$compile', '$q', 'plsTypeaheadParser', '$timeout', '$browser',
    function ($compile, $q, plsTypeaheadParser, $timeout, $browser) {

        var HOT_KEYS = [9, 13, 27, 38, 40];

        return {
            require: 'ngModel',
            priority: 10,
            link: function (originalScope, element, attrs, modelCtrl) {
                'use strict';

                var selected = modelCtrl.$modelValue;

                //minimal no of characters that needs to be entered before typeahead kicks-in
                var minSearch = originalScope.$eval(attrs.typeaheadMinLength) || 1;
                var origModel = originalScope[attrs.ngModel];
                var autoSelect = originalScope.$eval(attrs.typeaheadAutoSelect) || false;
                originalScope.$watch(attrs.ngModel, function (newValue) {
                    if (newValue) {
                        modelCtrl.$setValidity('mistyped', true);
                    }
                    origModel = newValue;
                });

                //expressions used by typeahead
                var parserResult = plsTypeaheadParser.parse(attrs.plsTypeahead);

                //create a child scope for the typeahead directive so we are not polluting original scope
                //with typeahead-specific data (matches, query etc.)
                var scope = originalScope.$new();
                originalScope.$on('event:showTypeaheadList', function (event, tahdID) {
                    if (tahdID === attrs.typeaheadId) {
                        showTypeaheadList();
                    }
                });
                originalScope.$on('$destroy', function () {
                    scope.$destroy();
                });

                var shouldResetMatches = true;

                function resetMatches() {
                    scope.matches = [];
                    scope.activeIdx = 0;
                    scope.isOpen = false;
                    scope.openByButton = false;
                }

                resetMatches();

                function shouldAutoSelectItem(inputValue) {
                    return scope.matches.length === 1 && autoSelect && (scope.matches[0].optionLabel.toLowerCase() === inputValue.toLowerCase()
                            || scope.matches[0].label.toLowerCase() === inputValue.toLowerCase());
                }

                function indexOf(list, item) {
                    for (var i = 0; i < list.length; i++) {
                        if (_.isEqual(list[i], item) || _.isEqual(list[i].label, item)) {
                            return i;
                        }
                    }
                    return -1;
                }

                function getMatchesAsync(inputValue) {
                    var locals = {$viewValue: inputValue};
                    $q.when(parserResult.source(scope, locals)).then(function (matches) {
                        //it might happen that several async queries were in progress if a user were typing fast
                        //but we are interested only in responses that correspond to the current view value
                        if (inputValue === modelCtrl.$viewValue) {
                            if (matches.length > 0) {
                                if (attrs.getSelectedItemIndexFn) {
                                    scope.activeIdx = scope[attrs.getSelectedItemIndexFn](matches, modelCtrl.$viewValue);
                                } else {
                                    attrs.activeIdx = 0;
                                }
                                scope.matches.length = 0;

                                //transform labels
                                for (var i = 0; i < matches.length; i++) {
                                    locals[parserResult.itemName] = matches[i];
                                    scope.matches.push({
                                        label: parserResult.viewMapper(scope, locals),
                                        optionLabel: parserResult.labelMapper(scope, locals),
                                        model: matches[i]
                                    });
                                }

                                scope.query = inputValue;
                                if (shouldAutoSelectItem(inputValue)) {
                                    // auto select value only if user has typed full word or full key.
                                    // Otherwise he won't be able to edit this field with backspace/delete when there is only one option
                                    scope.select(0);
                                } else {
                                    if (attrs.getSelectedItemIndexFn && !element.is(':focus')) {
                                        var newActiveIdx = scope[attrs.getSelectedItemIndexFn](matches, modelCtrl.$viewValue);
                                        if (newActiveIdx >= 0) {
                                            scope.select(newActiveIdx, true);
                                            return;
                                        }
                                    }
                                    if (modelCtrl.$modelValue) {
                                        scope.activeIdx = indexOf(matches, modelCtrl.$modelValue);
                                    }
                                    scope.isOpen = true;
                                }
                            } else {
                                resetMatches();
                            }
                        }
                    }, resetMatches);
                }

                function getMatchesDelayed(inputValue) {
                    var delay = 300; // 0.3 seconds delay after last input
                    if (scope.plsSearchTimeoutPromise) {
                        $timeout.cancel(scope.plsSearchTimeoutPromise);
                    }
                    scope.plsSearchTimeoutPromise = $timeout(function () {
                        scope.plsSearchTimeoutPromise = undefined;
                        getMatchesAsync(inputValue);
                    }, delay);
                }

                //we need to propagate user's query so we can higlight matches
                scope.query = undefined;

                //plug into $parsers pipeline to open a typeahead on view changes initiated from DOM
                //$parsers kick-in on all the changes coming from the view as well as manually triggered by $setViewValue
                modelCtrl.$parsers.unshift(function (inputValue) {
                    if (selected && (!angular.isString(inputValue) || shouldAutoSelectItem(inputValue))) {
                        modelCtrl.$setValidity('mistyped', true);
                        scope.isOpen = false;
                        if (!angular.isString(inputValue)) {
                            return inputValue;
                        }
                        selected = undefined;
                        return scope.matches[0].model;
                    } else {
                        if (modelCtrl.$modelValue) {
                            var locals = {};
                            locals[parserResult.itemName] = modelCtrl.$modelValue;
                            if (inputValue === parserResult.viewMapper(scope, locals)) {
                                return modelCtrl.$modelValue;
                            }
                        }
                        modelCtrl.$setValidity('mistyped', !inputValue);
                        if (inputValue && inputValue.length >= minSearch) {
                            if (shouldResetMatches) {
                                resetMatches();
                            }
                            getMatchesDelayed(inputValue);
                        } else {
                            scope.isOpen = false;
                        }
                    }

                    return undefined;
                });

                modelCtrl.$render = function () {
                    var locals = {};
                    locals[parserResult.itemName] = selected || modelCtrl.$viewValue;
                    var parsedValue = parserResult.viewMapper(scope, locals);
                    var start = element[0].selectionStart;
                    var end = element[0].selectionEnd;
                    if (angular.isString(parsedValue)) {
                        element.val(parsedValue);
                        modelCtrl.$setValidity('mistyped', true);
                    } else if (angular.isString(modelCtrl.$viewValue)) {
                        element.val(modelCtrl.$viewValue);
                    } else {
                        element.val('');
                    }
                    if (element.is(":focus") && start && end && start === end) {
                        element[0].selectionStart = start;
                        element[0].selectionEnd = end;
                    }
                    selected = undefined;
                };

                scope.select = function (activeIdx, render) {
                    //called from within the $digest() cycle
                    var locals = {};
                    locals[parserResult.itemName] = selected = scope.matches[activeIdx].model;
                    origModel = selected;

                    modelCtrl.$setViewValue(parserResult.modelMapper(scope, locals));
                    if (render) {
                        modelCtrl.$render();
                        scope.$emit('event:typeaheadChanged', modelCtrl.$viewValue, modelCtrl.$valid);
                    }
                };
                //function copied from angular.js
                var trim = function (value) {
                    return angular.isString(value) ? value.replace(/^\s*/, '').replace(/\s*$/, '') : value;
                };
                //function copied from angular.js
                var toBoolean = function (value) {
                    if (value && value.length !== 0) {
                        var v = angular.lowercase("" + value);
                        value = !(v == 'f' || v == '0' || v == 'false' || v == 'no' || v == 'n' || v == '[]');
                    } else {
                        value = false;
                    }
                    return value;
                };
                //function copied from angular.js
                function listener() {
                    var value = element.val();

                    // By default we will trim the value
                    // If the attribute ng-trim exists we will avoid trimming
                    // e.g. <input ng-model="foo" ng-trim="false">
                    if (toBoolean(attrs.ngTrim || 'T')) {
                        value = trim(value);
                    }

                    if (modelCtrl.$viewValue !== value) {
                        scope.$apply(function () {
                            modelCtrl.$setViewValue(value);
                        });
                    }
                }

                var timeout;
                //function adopted from angular.js with fix for IE (problem with TAB key)
                function adoptedKeyDown(event) {
                    var key = event.keyCode;
                    // ignore command modifiers arrows,home,end,pageUp/Down tab
                    if (key === 91 || (15 < key && key < 19) || (33 <= key && key <= 40) || key === 9) {
                        return;
                    }
                    if (!timeout) {
                        timeout = $browser.defer(function () {
                            listener();
                            timeout = null;
                        });
                    }
                }

                element.bind('keyup', function (evt) {
                    scope.$emit('event:typeaheadChanged', modelCtrl.$viewValue, modelCtrl.$valid);
                });

                element.unbind('keydown');

                //bind keyboard events: arrows up(38) / down(40), enter(13) and tab(9), esc(9)
                element.bind('keydown', function (evt) {
                    //typeahead is open and an "interesting" key was pressed
                    if (scope.matches.length === 0 || HOT_KEYS.indexOf(evt.which) === -1) {
                        adoptedKeyDown(evt);
                        return;
                    }

                    if (evt.which === 40) {
                        scope.activeIdx = (scope.activeIdx + 1) % scope.matches.length;
                        scope.$digest();
                    } else if (evt.which === 38) {
                        scope.activeIdx = (scope.activeIdx > 0 ? scope.activeIdx : scope.matches.length) - 1;
                        scope.$digest();
                    } else if (evt.which === 13 || evt.which === 9) {
                        if (scope.isOpen && (scope.activeIdx >= 0 || scope.matches.length === 1)) {
                            scope.$apply(function () {
                                scope.select(scope.activeIdx >= 0 ? scope.activeIdx : 0, true);
                            });
                            if (evt.which === 9) {
                                return;
                            }
                        } else {
                            return;
                        }
                    } else if (evt.which === 27) {
                        scope.matches = [];
                        scope.$digest();
                    }
                    evt.preventDefault();
                });

                //bind focus lost event
                element.bind('blur', function (evt) {
                    if (scope.isOpen) {
                        $timeout(function () {
                            if (!modelCtrl.$modelValue || scope.openByButton) {
                                resetMatches();
                                scope.$digest();
                            }
                        }, 500);
                    }
                });

                var tplElCompiled = $compile("<div pls-typeahead-popup matches='matches' is-select-open='isOpen' active='activeIdx' " +
                        "select='select(activeIdx, true)' query='query'></div>")(scope);
                if (element.next().is('div')) {
                    element.next().after(tplElCompiled);
                } else {
                    element.after(tplElCompiled);
                }
                function showTypeaheadList() {
                    //Emulate user input by passing ' ' symbol into getMatchesAsync() function.
                    //The space character (' ') is passed instead of empty string ('') or 'undefined'
                    // to guarantee correct representation of drop down list.
                    //Please note, the ' ' value should be processed correctly by typeahead link function.
                    if (scope.isOpen) {
                        resetMatches();
                    } else {
                        getMatchesAsync(modelCtrl.$viewValue = '');
                        scope.isOpen = true;
                        scope.openByButton = true;
                        element.focus();
                    }
                }

                originalScope.showTypeaheadList = showTypeaheadList;
            }
        };
    }
]);

angular.module('plsApp.directives').directive('plsTypeaheadPopup', function () {
    return {
        restrict: 'A',
        scope: {
            matches: '=',
            query: '=',
            active: '=',
            isSelectOpen: '=',
            select: '&'
        },
        replace: true,
        templateUrl: 'pages/tpl/pls-typeahead-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.isOpen = function () {
                return $scope.matches.length > 0 && $scope.isSelectOpen;
            };

            $scope.isActive = function (matchIdx) {
                return $scope.active == matchIdx;
            };

            $scope.selectActive = function (matchIdx) {
                $scope.active = matchIdx;
            };

            $scope.selectMatch = function (activeIdx) {
                $scope.select({activeIdx: activeIdx}, true);
            };
        }]
    };
});

angular.module('plsApp.directives').filter('plsTypeaheadHighlight', function () {
    'use strict';

    function prepareRegExpString(str) {
        return str.replace(new RegExp('\\\\', 'g'), '\\\\');
    }

    return function (matchItem, query) {
        return angular.isString(query) ? matchItem.replace(new RegExp(prepareRegExpString(query), 'gi'), '<strong>$&</strong>') : query;
    };
});