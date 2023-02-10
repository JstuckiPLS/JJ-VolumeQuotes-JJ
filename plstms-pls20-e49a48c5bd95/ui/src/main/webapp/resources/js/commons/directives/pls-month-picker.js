/**
 *  AngularJS directive for ability select only month and year in datepicker.
 */
angular.module('plsApp.directives').directive('plsMonthPicker', ['$timeout', function ($timeout) {
    return {
        restrict: 'A',
        require: '?ngModel',
        scope: {
            currentMonthYear: '=',
            callbackFunction: '&'
        },
        link: function (scope, element, attrs) {
            'use strict';

            var el = attrs.linkingElement ? angular.element(attrs.linkingElement) : element;

            el.css('cursor', 'pointer');
            el.append('<input class="calendar-datepicker" style="position:absolute; left: -100px; width: 0px"/>');
            angular.element('.ui-datepicker-calendar').hide();

            angular.element('.calendar-datepicker').datepicker({
                forceParse: attrs.forceParse || false,
                language: attrs.language || 'en',
                changeMonth: true,
                changeYear: true,
                stepMonths: 1,
                showButtonPanel: true,
                maxDate: new Date(),
                minDate: new Date(2007, 0, 1),
                currentText: 'This month',
                onChangeMonthYear: function () {
                    $timeout(function () {
                        angular.element('.ui-datepicker-calendar').hide();
                    });
                }
            }).focus(function () {
                angular.element('.ui-datepicker-close').click(function () {
                    var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
                    var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();

                    scope.callbackFunction()(new Date(year, month, 1));
                });
            });

            el.bind('click', function () {
                angular.element('.calendar-datepicker').datepicker('setDate', $.datepicker.formatDate('mm/dd/yy', scope.currentMonthYear));
                angular.element('.calendar-datepicker').focus();
                angular.element('.ui-datepicker-calendar').hide();

                var offset = $(el).offset();
                var horizontalPosition = offset.left - $('.ui-datepicker').width() / 2 + el.width() / 2;
                angular.element('.ui-datepicker').css('top', offset.top - $('.ui-datepicker').height()).css('left', horizontalPosition);

                angular.element('.ui-datepicker').css('z-index', '2000');
            });


            $(window).resize(function () {
                angular.element('.calendar-datepicker').datepicker('hide');
            });
        }
    };
}]);