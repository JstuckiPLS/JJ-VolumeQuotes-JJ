/**
 * Test DateTimeUtils service.
 * @author: Alexander Kirichenko
 * Date: 8/13/13
 * Time: 10:47 AM
 */
describe('Tests for DateTimeUtils.', function() {
    var dateTimeUtilsService;

    beforeEach(module('plsApp'));

    beforeEach(inject(function(DateTimeUtils) {
        dateTimeUtilsService = DateTimeUtils;
    }));

    it('Should add specified amount of days to the provided date.', function() {
        var date = new Date(2013, 1, 1);
        var newDate = dateTimeUtilsService.addDays(date, 1);
        c_expect(newDate.getDate()).to.be.equal(date.getDate() + 1);
        newDate = dateTimeUtilsService.addDays(date, -1);
        c_expect(newDate.getDate()).to.be.equal(31);
        c_expect(newDate.getMonth()).to.be.equal(0);
        date = new Date(2012, 11, 31);
        newDate = dateTimeUtilsService.addDays(date, 1);
        c_expect(newDate.getDate()).to.be.equal(1);
        c_expect(newDate.getMonth()).to.be.equal(0);
        c_expect(newDate.getFullYear()).to.be.equal(2013);
    });

    it('Should return first day of the week within provided date.', function() {
        var date = new Date(2013, 7, 13);
        var firstDayOfWeek = dateTimeUtilsService.getFirstDayOfWeek(date);
        c_expect(firstDayOfWeek.getDate()).to.be.equal(11);
        c_expect(firstDayOfWeek).to.be.eql(new Date(2013, 7, 11));
    });

    it('Should parse date string in ISO format into Date.', function() {
        var parsedDate = dateTimeUtilsService.parseISODate('2013-08-13T00:00:00');
        c_expect(parsedDate.getDate()).to.be.eql(13);
        c_expect(parsedDate.getMonth()).to.be.eql(7);
        c_expect(parsedDate.getFullYear()).to.be.eql(2013);
        c_expect(parsedDate.getHours()).to.be.eql(0);
        c_expect(parsedDate.getMinutes()).to.be.eql(0);
        c_expect(parsedDate.getSeconds()).to.be.eql(0);
    });

    it('Should throw error when parse date string not in ISO format into Date.', function() {
        try {
            dateTimeUtilsService.parseISODate('blablabla');
        } catch (error) {
            c_expect(error).to.be.eql('Couldn\'t parse ISO 8601 date string \'blablabla\'')
            return;
        }
        c_expect(false, 'We shouldn\'t be here.').to.be.true;
    });

    it('Should calculate pickup window difference.', function() {
        var from = {
            hours: 0,
            minutes: 0,
            am: false
        };
        var to = {
            hours: 0,
            minutes: 0,
            am: false
        };
        c_expect(dateTimeUtilsService.pickupWindowDifference(from, to)).to.be.equal(0);
        to.am = true;
        c_expect(dateTimeUtilsService.pickupWindowDifference(from, to)).to.be.equal(-12);
        to.am = false;
        from.am = true;
        c_expect(dateTimeUtilsService.pickupWindowDifference(from, to)).to.be.equal(12);
        to.am = from.am = false;
        from.hours = 1;
        c_expect(dateTimeUtilsService.pickupWindowDifference(from, to)).to.be.equal(-1);
        from.minutes = 30;
        c_expect(dateTimeUtilsService.pickupWindowDifference(from, to)).to.be.equal(-1.5);
    });

    it('Should compare dates.', function() {
        var date1 = new Date(2013,0,0);
        var date2 = new Date(2013,0,1);
        c_expect(dateTimeUtilsService.compareDates(date1, date2)).to.be.equal(-1);
        c_expect(dateTimeUtilsService.compareDates(date2, date1)).to.be.equal(1);
        c_expect(dateTimeUtilsService.compareDates(date2, new Date(2013,0,1))).to.be.equal(0);
    });

    it('Should convert time in minutes to pickupWindowDTO', function() {
        //Test passing unacceptable input.
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes(undefined)).to.be.eql({hours: 0, minutes: 0, am: true});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes('string')).to.be.eql({hours: 0, minutes: 0, am: true});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes('10')).to.be.eql({hours: 0, minutes: 0, am: true});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes({})).to.be.eql({hours: 0, minutes: 0, am: true});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes({prop1: 'value1'})).to.be.eql({hours: 0, minutes: 0, am: true});

        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes(0)).to.be.eql({hours: 0, minutes: 0, am: true});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes(10)).to.be.eql({hours: 0, minutes: 10, am: true});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes(60)).to.be.eql({hours: 1, minutes: 0, am: true});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes(95)).to.be.eql({hours: 1, minutes: 35, am: true});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes(720)).to.be.eql({hours: 0, minutes: 0, am: false});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes(780)).to.be.eql({hours: 1, minutes: 0, am: false});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes(1440)).to.be.eql({hours: 0, minutes: 0, am: true});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes(1441)).to.be.eql({hours: 0, minutes: 1, am: true});
        c_expect(dateTimeUtilsService.pickupWindowFromTimeInMinutes(2880)).to.be.eql({hours: 0, minutes: 0, am: true});
    });

    it('Should convert pickupWindowDTO into time in minutes', function() {
        //Test passing unacceptable input.
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 0, minutes: 0, am: true})).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow(undefined)).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({})).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({prop1: 'val1'})).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow(1)).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow('string')).to.be.eql(0);

        //Test passing partial input.
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 1})).to.be.eql(60);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({minutes: 7})).to.be.eql(7);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({am: false})).to.be.eql(720);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 1, minutes: 7})).to.be.eql(67);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 1, am: false})).to.be.eql(780);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({minutes: 1, am: false})).to.be.eql(721);

        //Test passing valid input.
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 0, minutes: 0, am: true})).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 0, minutes: 10, am: true})).to.be.eql(10);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 1, minutes: 0, am: true})).to.be.eql(60);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 1, minutes: 35, am: true})).to.be.eql(95);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 0, minutes: 0, am: false})).to.be.eql(720);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 1, minutes: 0, am: false})).to.be.eql(780);

        //Test passing over input.
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 12, minutes: 0, am: true})).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 24, minutes: 0, am: true})).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 13, minutes: 0, am: true})).to.be.eql(60);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 25, minutes: 0, am: true})).to.be.eql(60);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 0, minutes: 60, am: true})).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 0, minutes: 61, am: true})).to.be.eql(1);

        //Test passing wrong data type input.
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 0, minutes: 0, am: 'true'})).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 0, minutes: 0, am: 'string'})).to.be.eql(0);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: '0', minutes: 0, am: false})).to.be.eql(720);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: 'string', minutes: 0, am: false})).to.be.eql(720);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: undefined, minutes: 0, am: false})).to.be.eql(720);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({hours: null, minutes: 0, am: false})).to.be.eql(720);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({minutes: '0', hours: 0, am: false})).to.be.eql(720);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({minutes: 'string', hours: 0, am: false})).to.be.eql(720);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({minutes: undefined, hours: 0, am: false})).to.be.eql(720);
        c_expect(dateTimeUtilsService.timeInMinutesFromPickupWindow({minutes: null, hours: 0, am: false})).to.be.eql(720);
    });
    
    it ('Should calculate days in year', function() {
        c_expect(dateTimeUtilsService.daysInYear(new Date(2008,10,10))).to.be.equals(366);
        c_expect(dateTimeUtilsService.daysInYear(new Date(2009,10,10))).to.be.equals(365);
        c_expect(dateTimeUtilsService.daysInYear(new Date(2010,10,10))).to.be.equals(365);
        c_expect(dateTimeUtilsService.daysInYear(new Date(2011,10,10))).to.be.equals(365);
        c_expect(dateTimeUtilsService.daysInYear(new Date(2012,10,10))).to.be.equals(366);
    });

    it ('Should calculate days between two dates', function() {
        c_expect(dateTimeUtilsService.daysBetweenTwoDates(new Date(2014, 01, 10), new Date(2014, 01, 20))).to.be.equals(10);
        c_expect(dateTimeUtilsService.daysBetweenTwoDates(new Date(2014, 01, 10), new Date(2015, 01, 10))).to.be.equals(365);
    });
    
    it('Should add specified amount of months to the provided date.', function() {
      //for not leap year
      
      var date = new Date(2014, 0, 1);
      var newDate = dateTimeUtilsService.addMonths(date, 1);
      c_expect(newDate.getMonth()).to.be.equal(date.getMonth() + 1);
      
      var date = new Date(2014, 4, 1);
      var newDate = dateTimeUtilsService.addMonths(date, -1);
      c_expect(newDate.getMonth()).to.be.equal(date.getMonth() - 1);
      
      var date = new Date(2014, 0, 15);
      var newDate = dateTimeUtilsService.addMonths(date, 1);
      c_expect(newDate.getMonth()).to.be.equal(date.getMonth() + 1);
      c_expect(newDate.getDate()).to.be.equal(15);
      
      date = new Date(2014, 0, 31);
      var newDate = dateTimeUtilsService.addMonths(date, 1);
      c_expect(newDate.getMonth()).to.be.equal(date.getMonth() + 1);
      c_expect(newDate.getDate()).to.be.equal(28);
      
      date = new Date(2014, 11, 31);
      var newDate = dateTimeUtilsService.addMonths(date, 1);
      c_expect(newDate.getMonth()).to.be.equal(0);
      c_expect(newDate.getDate()).to.be.equal(31);
      c_expect(newDate.getFullYear()).to.be.equal(2015);
      
      date = new Date(2014, 0, 1);
      var newDate = dateTimeUtilsService.addMonths(date, -1);
      c_expect(newDate.getMonth()).to.be.equal(11);
      c_expect(newDate.getDate()).to.be.equal(1);
      c_expect(newDate.getFullYear()).to.be.equal(2013);
      
      //for leap year
      
      var date = new Date(2016, 0, 1);
      var newDate = dateTimeUtilsService.addMonths(date, 1);
      c_expect(newDate.getMonth()).to.be.equal(date.getMonth() + 1);
      
      date = new Date(2016, 0, 31);
      var newDate = dateTimeUtilsService.addMonths(date, 1);
      c_expect(newDate.getMonth()).to.be.equal(date.getMonth() + 1);
      c_expect(newDate.getDate()).to.be.equal(29);
      
      date = new Date(2016, 11, 31);
      var newDate = dateTimeUtilsService.addMonths(date, 1);
      c_expect(newDate.getMonth()).to.be.equal(0);
      c_expect(newDate.getDate()).to.be.equal(31);
      c_expect(newDate.getFullYear()).to.be.equal(2017);
  });
});