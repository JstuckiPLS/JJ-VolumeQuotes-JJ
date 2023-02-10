/**
 * Directive that disables all child inputs of element which contains this directive.
 * If value (angular expression) that is assigned to pls-form-disabled attribute becomes true - then all inputs will be disabled.
 * If it becomes false - then
 *     all inputs will be reset to their actual state based on presence of data-ng-disabled or ng-disabled attribute in the input element.
 *
 * Use <code>data-not-disable-me</code> attribute for elements which shouldn't be processed by this directive.
 *
 * @author Aleksandr Leshchenko
 */
angular.module('plsApp.directives').directive('plsFormDisabled', function () {
    return function (scope, elem, attrs) {
        'use strict';

        scope.$watch(function () {
            var disable = scope.$eval(attrs.plsFormDisabled);
            var parentsWithFromDisabled = $(elem).parents('[data-pls-form-disabled]');

            if (parentsWithFromDisabled && parentsWithFromDisabled.length) {
                if (angular.element($(parentsWithFromDisabled[0])).scope().
                        $eval($(parentsWithFromDisabled[0]).attr('data-pls-form-disabled')) === true) {
                    return;
                }
            }

            var inputs = elem.find(disable ? ":enabled" : ":disabled").not("[data-not-disable-me]").filter(function () {
                return $(this).parentsUntil('div[data-pls-modal]:visible', 'div[data-not-disable-me]').length === 0;
            });

            if (inputs.length) {
                _.each(inputs, function (input) {
                    if (disable) {
                        $(input).attr("disabled", "disabled");
                    } else {
                        var disabled = $(input).attr('data-ng-disabled') || $(input).attr('ng-disabled');
                        if (disabled && angular.element(input).scope().$eval(disabled)) {
                            $(input).attr("disabled", "disabled");
                        } else {
                            $(input).attr("disabled", null);
                        }
                    }
                });
            }
        });
    };
});