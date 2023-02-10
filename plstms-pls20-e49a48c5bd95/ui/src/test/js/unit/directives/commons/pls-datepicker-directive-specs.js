/**
 * This unit test checks datepicker directive.
 * 
 * @author Aleksandr Leshchenko
 */
describe('PLS Datepicker (pls-datepicker) Directive Test.', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;
    var filter = undefined;
    var defaultDateFormat = 'MM/dd/yyyy';
    var plsApp = undefined;
    var $timezones = undefined;

    beforeEach(module('plsApp', 'Timezones', function($provide) {
        $provide.constant('$timezones.definitions.location', 'base/resources/lib/angular/tz/data');
    }));

    beforeEach(inject(function(_$timezones_) {
        $timezones = _$timezones_;
    }));

    beforeEach(inject(function($rootScope, $compile, $filter) {
        elm = angular.element('<div>' + '<input type="text" id="dateField"'
                + '       data-ng-model="selectedDate"'
                + '       data-min-date="minDate"'
                + '       data-max-date="maxDate"'
                + '       data-pls-datepicker/>'
                + '<button type="button" data-toggle="datepicker">'
                + '    <i class="icon-calendar"></i>' + '</button>'
                + '</div>');
        scope = $rootScope;
        $compile(elm)(scope);
        scope.$digest();
        filter = $filter;
    }));

    function getISOFormatted(date) {
        return filter('date')(date, 'yyyy-MM-dd');
    }

    function getTodayInZeroTimezone() {
        var today = new Date();
        today = $timezones.align(today, 'GMT');// zero timezone
        return today;
    }

    it('should render datepicker with default timezone', function() {
        var today = getTodayInZeroTimezone();
        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.minDate = getTodayInZeroTimezone();
            scope.minDate.setHours(23, 59, 59, 59);
            scope.maxDate = getTodayInZeroTimezone();
            scope.maxDate.setHours(0, 0, 0, 0);
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(getISOFormatted(today));
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).toHaveClass('ng-valid');
        expect(inp).toHaveClass('ng-valid-date');
        expect(inp).not.toHaveClass('ng-invalid');
        expect(inp).not.toHaveClass('ng-invalid-date');
    });

    it('should validate min date', function() {
        var today = getTodayInZeroTimezone();
        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.minDate = getTodayInZeroTimezone();
            scope.minDate.setHours(0, 0, 0, 0);
            scope.minDate.setDate(scope.minDate.getDate() + 1);
            scope.maxDate = getTodayInZeroTimezone();
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(undefined);
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).not.toHaveClass('ng-valid');
        expect(inp).not.toHaveClass('ng-valid-date');
        expect(inp).toHaveClass('ng-invalid');
        expect(inp).toHaveClass('ng-invalid-date');
    });

    it('should validate min date in negative timezone', function() {
        var today = getTodayInZeroTimezone();
        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.minDate = getTodayInZeroTimezone();
            scope.minDate.setHours(12);
            scope.minDate = $timezones.align(scope.minDate.getUTCDateProxy(), 'America/New_York');// -4
            scope.minDate.setHours(0, 0, 0, 0);
            scope.maxDate = getTodayInZeroTimezone();
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(getISOFormatted(today));
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).toHaveClass('ng-valid');
        expect(inp).toHaveClass('ng-valid-date');
        expect(inp).not.toHaveClass('ng-invalid');
        expect(inp).not.toHaveClass('ng-invalid-date');

        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.minDate = getTodayInZeroTimezone();
            scope.minDate.setHours(12);
            scope.minDate.setDate(scope.minDate.getDate() + 1);
            scope.minDate = $timezones.align(scope.minDate.getUTCDateProxy(), 'America/New_York');// -4
            scope.minDate.setHours(0, 0, 0, 0);
        });

        expect(scope.selectedDate).toBe(undefined);
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).not.toHaveClass('ng-valid');
        expect(inp).not.toHaveClass('ng-valid-date');
        expect(inp).toHaveClass('ng-invalid');
        expect(inp).toHaveClass('ng-invalid-date');
    });

    it('should validate min date in positive timezone', function() {
        var today = getTodayInZeroTimezone();
        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.minDate = getTodayInZeroTimezone();
            scope.minDate.setHours(12);
            scope.minDate = $timezones.align(scope.minDate.getUTCDateProxy(), 'Europe/Kiev');// +3
            scope.minDate.setHours(23, 59, 59, 59);
            scope.maxDate = getTodayInZeroTimezone();
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(getISOFormatted(today));
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).toHaveClass('ng-valid');
        expect(inp).toHaveClass('ng-valid-date');
        expect(inp).not.toHaveClass('ng-invalid');
        expect(inp).not.toHaveClass('ng-invalid-date');
    });

    it('should validate max date', function() {
        var today = getTodayInZeroTimezone();
        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.minDate = getTodayInZeroTimezone();
            scope.maxDate = getTodayInZeroTimezone();
            scope.maxDate.setHours(23, 59, 59, 59);
            scope.maxDate.setDate(scope.maxDate.getDate() - 1);
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(undefined);
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).not.toHaveClass('ng-valid');
        expect(inp).not.toHaveClass('ng-valid-date');
        expect(inp).toHaveClass('ng-invalid');
        expect(inp).toHaveClass('ng-invalid-date');
    });

    it('should validate max date in negative timezone', function() {
        var today = getTodayInZeroTimezone();
        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.minDate = getTodayInZeroTimezone();
            scope.minDate.setHours(23, 59, 59, 59);
            scope.maxDate = getTodayInZeroTimezone();
            scope.maxDate.setHours(12);
            scope.maxDate = $timezones.align(scope.maxDate.getUTCDateProxy(), 'America/New_York');// -4
            scope.maxDate.setHours(0, 0, 0, 0);
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(getISOFormatted(today));
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).toHaveClass('ng-valid');
        expect(inp).toHaveClass('ng-valid-date');
        expect(inp).not.toHaveClass('ng-invalid');
        expect(inp).not.toHaveClass('ng-invalid-date');
    });

    it('should validate max date in positive timezone', function() {
        var today = getTodayInZeroTimezone();
        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.minDate = getTodayInZeroTimezone();
            scope.minDate.setHours(23, 59, 59, 59);
            scope.maxDate = getTodayInZeroTimezone();
            scope.maxDate.setHours(12);
            scope.maxDate = $timezones.align(scope.maxDate.getUTCDateProxy(), 'Europe/Kiev');// +3
            scope.maxDate.setHours(0, 0, 0, 0);
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(getISOFormatted(today));
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).toHaveClass('ng-valid');
        expect(inp).toHaveClass('ng-valid-date');
        expect(inp).not.toHaveClass('ng-invalid');
        expect(inp).not.toHaveClass('ng-invalid-date');

        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.minDate = getTodayInZeroTimezone();
            scope.minDate.setHours(23, 59, 59, 59);
            scope.maxDate = getTodayInZeroTimezone();
            scope.maxDate.setHours(12);
            scope.maxDate = $timezones.align(scope.maxDate.getUTCDateProxy(), 'Europe/Kiev');// +3
            scope.maxDate.setHours(23, 59, 59, 59);
            scope.maxDate.setDate(scope.maxDate.getDate() - 1);
        });

        expect(scope.selectedDate).toBe(undefined);
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).not.toHaveClass('ng-valid');
        expect(inp).not.toHaveClass('ng-valid-date');
        expect(inp).toHaveClass('ng-invalid');
        expect(inp).toHaveClass('ng-invalid-date');
    });

    it('should render datepicker with negative timezone', function() {
        var today = getTodayInZeroTimezone();
        today.setHours(2, 0);

        scope.$apply(function() {
            scope.selectedDate = JSON.stringify(today).replace(/\"/g, '').replace(/Z/g, '') + '-09:00';
            scope.minDate = getTodayInZeroTimezone();
            scope.maxDate = getTodayInZeroTimezone();
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(getISOFormatted(today));
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).toHaveClass('ng-valid');
        expect(inp).toHaveClass('ng-valid-date');
        expect(inp).not.toHaveClass('ng-invalid');
        expect(inp).not.toHaveClass('ng-invalid-date');
    });

    it('should render datepicker with positive timezone', function() {
        var today = getTodayInZeroTimezone();
        today.setHours(22, 0);

        scope.$apply(function() {
            scope.selectedDate = JSON.stringify(today).replace(/\"/g, '').replace(/Z/g, '') + '+09:00';
            scope.minDate = getTodayInZeroTimezone();
            scope.maxDate = getTodayInZeroTimezone();
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(getISOFormatted(today));
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).toHaveClass('ng-valid');
        expect(inp).toHaveClass('ng-valid-date');
        expect(inp).not.toHaveClass('ng-invalid');
        expect(inp).not.toHaveClass('ng-invalid-date');
    });

    it('should allow min date as ISO string', function() {
        var today = getTodayInZeroTimezone();
        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.minDate = getTodayInZeroTimezone();
            scope.minDate.setHours(0, 0, 0, 0);
            scope.minDate.setDate(scope.minDate.getDate() + 1);
            scope.minDate = filter('date')(scope.minDate, 'yyyy-MM-dd');
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(undefined);
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).not.toHaveClass('ng-valid');
        expect(inp).not.toHaveClass('ng-valid-date');
        expect(inp).toHaveClass('ng-invalid');
        expect(inp).toHaveClass('ng-invalid-date');
    });

    it('should allow max date as ISO string', function() {
        var today = getTodayInZeroTimezone();
        scope.$apply(function() {
            scope.selectedDate = filter('date')(today, 'yyyy-MM-dd');
            scope.maxDate = getTodayInZeroTimezone();
            scope.maxDate.setHours(23, 59, 59, 59);
            scope.maxDate.setDate(scope.maxDate.getDate() - 1);
            scope.maxDate = filter('date')(scope.maxDate, 'yyyy-MM-dd');
        });
        var inp = elm.find('input');
        expect(inp.length).toBe(1);

        expect(scope.selectedDate).toBe(undefined);
        expect(inp.val()).toBe(filter('date')(today, defaultDateFormat));
        expect(inp).not.toHaveClass('ng-valid');
        expect(inp).not.toHaveClass('ng-valid-date');
        expect(inp).toHaveClass('ng-invalid');
        expect(inp).toHaveClass('ng-invalid-date');
    });
});