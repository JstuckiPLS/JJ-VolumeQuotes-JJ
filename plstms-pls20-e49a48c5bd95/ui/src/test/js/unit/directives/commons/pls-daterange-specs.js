/**
 * Test daterange directive.
 * @author: Alexander Kirichenko
 */
describe('PLS date range(pls-date-range) directive tests.', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;

    var dateFilter, dateTimeUtils;
    var dateFormatForDatePicker = 'MM/dd/yyyy';

    beforeEach(module('plsApp', 'pages/tpl/daterange-tpl.html'));

    beforeEach(inject(function($rootScope, $compile, $document, $filter, DateTimeUtils) {
        dateFilter = $filter('date');
        dateTimeUtils = DateTimeUtils;
        elm = angular.element('<div id="dateRangeContainer"><div data-pls-date-range data-range-type="dateRangeType" data-model="dateRange" ' +
                'data-validity-state="validityState" data-validity="validity"></div></div>');
        scope = $rootScope.$new();
        scope.$apply(function() {
            scope.dateRange = '';
            scope.dateRangeType = 'DEFAULT';
            scope.validityState = {};
            scope.validity = {};
        });
        $document.find('body').append($compile(elm)(scope));
        scope.$digest();
    }));

    afterEach(inject(function($document) {
        $document.find('div#dateRangeContainer').remove();
        $document.find('table.ui-datepicker-calendar').remove();
        $document.find('div#ui-datepicker-div').remove();
    }));

    it('Should disable datepickers if range type not DEFAULT', function() {
        scope.$apply(function() {
            scope.dateRangeType = 'TODAY';
        });
        c_expect(scope.dateRange).to.be.equal('TODAY');
        c_expect(elm.find('button[data-toggle="datepicker"]:disabled').length).to.be.equal(2);
        c_expect(elm.find('input.hasDatepicker:disabled').length).to.be.equal(2);
        scope.$apply(function() {
            scope.dateRangeType = 'WEEK';
        });
        c_expect(scope.dateRange).to.be.equal('WEEK');
        c_expect(elm.find('button[data-toggle="datepicker"]:disabled').length).to.be.equal(2);
        c_expect(elm.find('input.hasDatepicker:disabled').length).to.be.equal(2);
        scope.$apply(function() {
            scope.dateRangeType = 'MONTH';
        });
        c_expect(scope.dateRange).to.be.equal('MONTH');
        c_expect(elm.find('button[data-toggle="datepicker"]:disabled').length).to.be.equal(2);
        c_expect(elm.find('input.hasDatepicker:disabled').length).to.be.equal(2);
        scope.$apply(function() {
            scope.dateRangeType = 'QUARTER';
        });
        c_expect(scope.dateRange).to.be.equal('QUARTER');
        c_expect(elm.find('button[data-toggle="datepicker"]:disabled').length).to.be.equal(2);
        c_expect(elm.find('input.hasDatepicker:disabled').length).to.be.equal(2);
        scope.$apply(function() {
            scope.dateRangeType = 'YEAR';
        });
        c_expect(scope.dateRange).to.be.equal('YEAR');
        c_expect(elm.find('button[data-toggle="datepicker"]:disabled').length).to.be.equal(2);
        c_expect(elm.find('input.hasDatepicker:disabled').length).to.be.equal(2);
    });

    it('Should allow user to enter date range when selected DEFAULT range type by clicking on datepickers.', function() {
        c_expect(scope.dateRange).to.be.equal('DEFAULT,,');
        var datePickerBtns = elm.find('button[data-toggle="datepicker"]:not(:disabled)');
        c_expect(datePickerBtns.length).to.be.equal(2);
        c_expect(elm.parent().find('table.ui-datepicker-calendar').length).to.be.equal(0);
        datePickerBtns.eq(0).click();
        var yearSelect = elm.parent().find('select.ui-datepicker-year');
        c_expect(yearSelect.length).to.be.equal(1);
        select(yearSelect).option('2013');
        var monthSelect = elm.parent().find('select.ui-datepicker-month');
        c_expect(monthSelect.length).to.be.equal(1);
        select(monthSelect).option('8');

        c_expect(elm.parent().find('table.ui-datepicker-calendar').length).to.be.equal(1);
        elm.parent().find('td[data-month="8"][data-year="2013"]:has(a:contains(2))').filter(function() {
            return  $('a', this).text() === '2';
        }).click();

        c_expect(scope.dateRange).to.be.equal('DEFAULT,' + dateFilter(new Date(2013, 8, 2), scope.$root.transferDateFormat) + ',');
        datePickerBtns.eq(1).click();
        yearSelect = elm.parent().find('select.ui-datepicker-year');
        c_expect(yearSelect.length).to.be.equal(1);
        select(yearSelect).option('2013');
        monthSelect = elm.parent().find('select.ui-datepicker-month');
        c_expect(monthSelect.length).to.be.equal(1);
        select(monthSelect).option('8');

        c_expect(elm.parent().find('table.ui-datepicker-calendar').length).to.be.equal(1);
        elm.parent().find('td[data-month="8"][data-year="2013"]:has(a:contains(3))').filter(function() {
            return  $('a', this).text() === '3';
        }).click();

        c_expect(elm.parent().find('table.ui-datepicker-calendar').length).to.be.equal(1);
        c_expect(scope.dateRange).to.be.equal('DEFAULT,' + dateFilter(new Date(2013, 8, 2), scope.$root.transferDateFormat) + ','
                + dateFilter(dateTimeUtils.addDays(new Date(2013, 8, 2), 1), scope.$root.transferDateFormat));
    });

    it('Should allow user to enter date range when selected DEFAULT range type by entering data in datepickers.', function() {
        c_expect(scope.dateRange).to.be.equal('DEFAULT,,');
        var datePickerInputs = elm.find('input.hasDatepicker:not(:disabled)');
        c_expect(datePickerInputs.length).to.be.equal(2);
        var fromDate = new Date();
        var toDate = dateTimeUtils.addDays(fromDate, 1);
        input(datePickerInputs.eq(0)).enter(dateFilter(fromDate, dateFormatForDatePicker));
        input(datePickerInputs.eq(1)).enter(dateFilter(toDate, dateFormatForDatePicker));
        c_expect(scope.dateRange).to.be.equal('DEFAULT,' + dateFilter(fromDate, scope.$root.transferDateFormat) + ','
                + dateFilter(toDate, scope.$root.transferDateFormat));
    });

    it('Should mark element as invalid on wrong data entering.', function() {
        var datePickerInputs = elm.find('input.hasDatepicker:not(:disabled)');
        c_expect(datePickerInputs.length).to.be.equal(2);
        input(datePickerInputs.eq(0)).enter('blablabla');
        c_expect(scope.validityState.invalid).to.be.true();
        input(datePickerInputs.eq(0)).enter(dateFilter(new Date(), dateFormatForDatePicker));
        c_expect(scope.validityState.invalid).to.be.false();
        input(datePickerInputs.eq(1)).enter(dateFilter(dateTimeUtils.addDays(new Date(), -1), dateFormatForDatePicker));
        c_expect(scope.validityState.invalid).to.be.true();
    });

    it('Should mark element as invalid date difference more than 3 years.', function() {
        var datePickerInputs = elm.find('input.hasDatepicker:not(:disabled)');
        c_expect(datePickerInputs.length).to.be.equal(2);
        input(datePickerInputs.eq(0)).enter(dateFilter(new Date(), dateFormatForDatePicker));
        input(datePickerInputs.eq(1)).enter(dateFilter(dateTimeUtils.addDays(new Date(), 1096), dateFormatForDatePicker));
        c_expect(scope.validityState.invalid).to.be.true();
    });

    it('Shouldn\'t allow to pickup date within more than 1 years from other date.', function() {
        var datePickerInputs = elm.find('input.hasDatepicker:not(:disabled)');
        c_expect(datePickerInputs.length).to.be.equal(2);
        var fromDate = new Date(2013, 3, 7);
        var maxToDate = dateTimeUtils.addDays(fromDate, 365);
        input(datePickerInputs.eq(0)).enter(dateFilter(fromDate, dateFormatForDatePicker));
        c_expect(scope.dateRange).to.be.equal('DEFAULT,' + dateFilter(fromDate, scope.$root.transferDateFormat) + ',');
        c_expect(scope.validityState.invalid).to.be.false();

        var datePickerBtns = elm.find('button[data-toggle="datepicker"]:not(:disabled)');
        c_expect(datePickerBtns.length).to.be.equal(2);
        var datePickerDiv = elm.parent().find('div#ui-datepicker-div');
        c_expect(datePickerDiv).to.exist;
        c_expect(datePickerDiv.find('table.ui-datepicker-calendar').length).to.be.equal(0);
        datePickerBtns.eq(1).click();
        c_expect(datePickerDiv.find('table.ui-datepicker-calendar').length).to.be.equal(1);
        var selectYearElm = datePickerDiv.find('select.ui-datepicker-year');
        c_expect(selectYearElm.find('option[value=' + (maxToDate.getFullYear() + 1) + ']').length).to.be.equal(0);
        c_expect(selectYearElm.find('option[value=' + (fromDate.getFullYear() - 1) + ']').length).to.be.equal(0);
        select(selectYearElm).option('' + maxToDate.getFullYear());
        var selectMonthElm = datePickerDiv.find('select.ui-datepicker-month');
        c_expect(selectMonthElm.find('option[value=' + (maxToDate.getMonth() + 1) + ']').length).to.be.equal(0);
        select(selectMonthElm).option('' + maxToDate.getMonth());

        c_expect(datePickerDiv.find('table.ui-datepicker-calendar td[data-month=\'' + maxToDate.getMonth() + '\'][data-year=\''
                + maxToDate.getFullYear() + '\']:last').text()).to.be.eq('' + maxToDate.getDate());
        c_expect(datePickerDiv.find('table.ui-datepicker-calendar td.ui-datepicker-unselectable > span').eq(0).text()).to.be.eq(''
                + (maxToDate.getDate() + 1));
    });


    it('Shouldn\'t allow to pickup todate previous day of fromdate.', function() {
        var datePickerInputs = elm.find('input.hasDatepicker:not(:disabled)');
        c_expect(datePickerInputs.length).to.be.equal(2);
        var fromDate = new Date(2013, 3, 7);
        input(datePickerInputs.eq(0)).enter(dateFilter(fromDate, dateFormatForDatePicker));
        c_expect(scope.dateRange).to.be.equal('DEFAULT,' + dateFilter(fromDate, scope.$root.transferDateFormat) + ',');
        c_expect(scope.validityState.invalid).to.be.false();

        var datePickerBtns = elm.find('button[data-toggle="datepicker"]:not(:disabled)');
        c_expect(datePickerBtns.length).to.be.equal(2);
        var datePickerDiv = elm.parent().find('div#ui-datepicker-div');
        c_expect(datePickerDiv).to.exist;
        c_expect(datePickerDiv.find('table.ui-datepicker-calendar').length).to.be.equal(0);
        datePickerBtns.eq(1).click();
        c_expect(datePickerDiv.find('table.ui-datepicker-calendar').length).to.be.equal(1);
        var selectYearElm = datePickerDiv.find('select.ui-datepicker-year');
        select(selectYearElm).option('' + fromDate.getFullYear());
        var selectMonthElm = datePickerDiv.find('select.ui-datepicker-month');
        select(selectMonthElm).option('' + fromDate.getMonth());

        c_expect(datePickerDiv.find('table.ui-datepicker-calendar td[data-month=\'' + fromDate.getMonth() + '\'][data-year=\''
                + fromDate.getFullYear() + '\']:first').text()).to.be.eq('' + fromDate.getDate());
        c_expect(datePickerDiv.find('table.ui-datepicker-calendar td.ui-datepicker-unselectable > span').last().text()).to.be.eq(''
                + (fromDate.getDate() - 1));
    });
});