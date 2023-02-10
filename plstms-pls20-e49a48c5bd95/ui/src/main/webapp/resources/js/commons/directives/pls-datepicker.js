// https://github.com/eternicode/bootstrap-datepicker

angular.module('plsApp.directives').directive('plsDatepicker', ['$timeout', 'DateTimeUtils', function ($timeout, DateTimeUtils) {
    'use strict';

    var defaultDateFormat = 'mm/dd/yy',
            isTouch = 'ontouchstart' in window && !window.navigator.userAgent.match(/PhantomJS/i),
            DATE_REGEXP_MAP = {
                '/': '[\\/]',
                '-': '[-]',
                '.': '[.]',
                'dd': '(?:(?:[0-2]?[0-9]{1})|(?:[3][01]{1}))',
                'd': '(?:(?:[0-2]?[0-9]{1})|(?:[3][01]{1}))',
                'mm': '(?:[0]?[1-9]|[1][012])',
                'm': '(?:[0]?[1-9]|[1][012])',
                'yy': '(?:(?:[1-3]{1}[0-9]{3}))(?![[0-9]])',
                'y': '(?:(?:[0-9]{1}[0-9]{1}))(?![[0-9]])'
            };

    return {
        restrict: 'A',
        require: '?ngModel',
        link: function postLink(scope, element, attrs, controller) {

            var elementId = element.context.id;

            $("input[id|='" + element.context.id + "']").each(function (index, element) {
                if (index > 0) {
                    $(element).attr('id', elementId + index);
                }
            });

            var regexpForDateFormat = function (dateFormat, options) {
                options || (options = {});
                var re = dateFormat, regexpMap = DATE_REGEXP_MAP;
                angular.forEach(regexpMap, function (v, k) {
                    re = re.split(k).join(v);
                });
                return new RegExp('^' + re + '$', ['i']);
            };

            var dateFormatRegexp = regexpForDateFormat(attrs.dateFormat || defaultDateFormat);

            // Handle date validity according to dateFormat
            if (controller) {
                controller.$parsers.unshift(function (viewValue) {
                    if (viewValue) {
                        if (dateFormatRegexp.test(viewValue)) {
                            try {
                                var parsedDate = $.datepicker.parseDate(attrs.dateFormat || defaultDateFormat, viewValue);
                                if (element.data('datepicker').settings.minDate) {
                                    if (parsedDate < element.data('datepicker').settings.minDate) {
                                        throw new Exception('invalid date');
                                    }
                                }
                                if (element.data('datepicker').settings.maxDate) {
                                    if (parsedDate > element.data('datepicker').settings.maxDate) {
                                        throw new Exception('invalid date');
                                    }
                                }
                                controller.$setValidity('date', true);
                                return scope.$root.formatDate(parsedDate);
                            } catch (e) {
                                // Invalid dates like February 30 or April 31 will be handled here
                            }
                        }
                        controller.$setValidity('date', false);
                    } else {
                        controller.$setValidity('date', true);
                    }
                    return undefined;
                });

                controller.$formatters.unshift(function (viewValue) {
                    if (viewValue && !dateFormatRegexp.test(viewValue)) {
                        var formattedDate = $.datepicker.formatDate(attrs.dateFormat || defaultDateFormat, DateTimeUtils.parseISODate(viewValue));
                        controller.$setValidity('date', true);
                        return formattedDate || '';
                    }
                    return viewValue;
                });
            }

            // Support add-on
            var component = element.next('[data-toggle="datepicker"]');

            if (component.length) {
                component.on('click', function () {
                    isTouch ? element.trigger('focus') : element.datepicker('show');
                });
            }

            // Popover GarbageCollection
            var $popover = element.closest('.popover');

            if ($popover) {
                $popover.on('hide', function (e) {
                    var datepicker = element.data('datepicker');
                    if (datepicker) {
                        datepicker.picker.remove();
                        element.data('datepicker', null);
                    }
                });
            }

            if (attrs.minDate && typeof attrs.minDate != "object") {
                scope.$watch(attrs.minDate, function (newValue, oldValue) {
                    if (_.isDate(newValue)) {
                        newValue = scope.$root.formatDate(newValue);
                    }
                    if (newValue && _.isString(newValue)) {
                        newValue = DateTimeUtils.parseISODate(newValue);
                    }
                    if (newValue && _.isDate(newValue)) {
                        newValue.setHours(0, 0, 0, 0);
                    }
                    element.data('datepicker').settings.minDate = newValue;

                    controller.$setViewValue(element.val());
                });
            }

            if (attrs.maxDate && typeof attrs.maxDate != "object") {
                scope.$watch(attrs.maxDate, function (newValue, oldValue) {
                    if (_.isDate(newValue)) {
                        newValue = scope.$root.formatDate(newValue);
                    }
                    if (newValue && _.isString(newValue)) {
                        newValue = DateTimeUtils.parseISODate(newValue);
                    }
                    if (newValue && _.isDate(newValue)) {
                        newValue.setHours(23, 59, 59, 59);
                    }
                    element.data('datepicker').settings.maxDate = newValue;

                    controller.$setViewValue(element.val());
                });
            }

            var focusEventIE = false;

            // Create datepicker
            element.attr('data-toggle', 'datepicker');

            element.datepicker({
                autoclose: false,
                forceParse: attrs.forceParse || false,
                language: attrs.language || 'en',
                changeMonth: true,
                changeYear: true,
                dateFormat: attrs.dateFormat || defaultDateFormat,
                onSelect: function (dateText) {
                    scope.$apply(function (scope) {
                        controller.$setViewValue(element.val());
                    });
                    if (navigator.userAgent.indexOf("MSIE") !== -1) {
                        focusEventIE = true;
                        element.trigger('focus');
                    }
                },
                beforeShow: function () {
                    var result = navigator.userAgent.indexOf("MSIE") !== -1 ? !focusEventIE : true;
                    if (navigator.userAgent.indexOf("Trident") !== -1) {
                        scope.$emit('event:datePickerShowIE', null);
                    }
                    focusEventIE = false;
                    return result;
                },
                onClose: function () {
                    focusEventIE = true;
                }
            });

            element.bind('blur', function () {
                if (navigator.userAgent.indexOf("MSIE") !== -1) {
                    focusEventIE = false;
                }
                if (navigator.userAgent.indexOf("Trident") !== -1) {
                    scope.$emit('event:datePickerBlurIE', null);
                }
            });

            angular.element('div#ui-datepicker-div').attr('data-pls-ignore-location-change-check', true);
        }
    };
}]);