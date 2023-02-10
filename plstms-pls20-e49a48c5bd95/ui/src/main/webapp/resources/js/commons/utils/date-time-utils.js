var MILLISECONDS_IN_A_DAY = 86400000;
var DAYS_IN_WEEK = 7;

// Date time utilities
angular.module('plsApp.utils').factory('DateTimeUtils', function () {
    return {
        DAYS_IN_WEEK: DAYS_IN_WEEK,
        MILLISECONDS_IN_A_DAY: MILLISECONDS_IN_A_DAY,
        MILLISECONDS_IN_YEAR: 31536000000,
        MILLISECONDS_IN_A_3_YEARS: 94608000000,
        MILLISECONDS_IN_A_90_DAYS: 90 * MILLISECONDS_IN_A_DAY,
        addDays: function (date, daysCount) {
            return new Date(date.getTime() + daysCount * MILLISECONDS_IN_A_DAY);
        },
        isLeapYear: function (year) {
            return (((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0));
        },
        getDaysInMonth: function (year, month) {
            return [31, (this.isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
        },
        addMonths: function (date, monthsCount) {
            var result = new Date(date);
            var dayOfMonth = result.getDate();
            result.setDate(1);
            result.setMonth(result.getMonth() + monthsCount);
            result.setDate(Math.min(dayOfMonth, this.getDaysInMonth(result.getFullYear(), result.getMonth())));
            return result;
        },
        getFirstDayOfWeek: function (date) {
            return new Date(date.getTime() - date.getDay() * MILLISECONDS_IN_A_DAY);
        },
        parseISODate: function (s) {
            if (!s) {
                return;
            }

            if (s && angular.isDate(s)) {
                return s;
            }

            var re = /(\d{4})-(\d\d)-(\d\d)/;

            var d = [];
            d = s.match(re);

            // "2010-12-07" parses to:
            //  ["2010-12-07", "2010", "12", "07"]

            if (!d) {
                throw "Couldn't parse ISO 8601 date string '" + s + "'";
            }

            // parse strings, leading zeros into proper ints
            for (var i = 1; i < 3; i++) {
                d[i] = parseInt(d[i], 10);
            }

            return new Date(d[1], d[2] - 1, d[3], 0, 0, 0, 0);
        },
        pickupWindowDifference: function (from, to) {
            var result = undefined;

            if (from && to) {
                var f = (from.hours === 12 ? 0 : from.hours) * 100 + (from.minutes === 30 ? 50 : 0) + (from.am ? 0 : 1200);
                var t = (to.hours === 12 ? 0 : to.hours) * 100 + (to.minutes === 30 ? 50 : 0) + (to.am ? 0 : 1200);
                result = (t - f) / 100;
            }

            return result;
        },
        compareDates: function (val1, val2) {
            var date1 = new Date(val1);
            var date2 = new Date(val2);

            if (date1 < date2) {
                return -1;
            } else if (date1 > date2) {
                return 1;
            } else {
                return 0;
            }
        },
        pickupWindowFromTimeInMinutes: function (timeInMinutes) {
            /**
             * Convert time in minutes into pickup window hash. In case of input value is not defined or it is not a number it will take it as 0.
             * If input value is more than 1440 (amount of minutes in a day) it will mod value by 1440.
             */
            var clearedTimeInMinutes = 0;

            if (angular.isNumber(timeInMinutes)) {
                clearedTimeInMinutes = (timeInMinutes || 0) % 1440;
            }

            return {
                hours: Math.floor(clearedTimeInMinutes % 720 / 60),
                minutes: clearedTimeInMinutes % 60,
                am: clearedTimeInMinutes < 720
            };
        },
        timeInMinutesFromPickupWindow: function (pickupWindow) {
            /**
             * Convert pickup window hash into time in minutes. In case of input value is not defined it will return 0.
             */
            var timeInMinutes = 0;

            if (pickupWindow && angular.isObject(pickupWindow)) {
                if (angular.isDefined(pickupWindow.hours) && angular.isNumber(pickupWindow.hours)) {
                    timeInMinutes += pickupWindow.hours % 12 * 60;
                }

                if (angular.isDefined(pickupWindow.minutes) && angular.isNumber(pickupWindow.minutes)) {
                    timeInMinutes += pickupWindow.minutes % 60;
                }

                if (angular.isDefined(pickupWindow.am) && pickupWindow.am === false) {
                    timeInMinutes += 720;
                }
            }

            return timeInMinutes;
        },
        timeStringFromWindowObject: function (timeObj) {
            var hours = timeObj.hours;

            if (!timeObj.am) {
                hours += 12;
            }

            hours = hours < 10 ? '0' + hours : hours;
            var minutes = timeObj.minutes < 10 ? '0' + timeObj.minutes : timeObj.minutes;

            return hours + ":" + minutes + ":00";
        },
        timeStringFromTimeInMinutes: function (timeInMinutes) {
            /**
             * Converts time in minutes into string representation formatted as 'hh:mm a'. (E.g. '12:28 PM', '01:13 AM').
             * "Time in minutes" means number of minutes elapsed from 00:00 AM.
             *
             * Examples (timeInMinutes : resultString):
             *          (30 : '00:30 AM')
             *          (190 : '03:10 AM')
             *          (510 : '08:30 AM')
             *          (990 : '4:30 PM')
             *          (1320 : '10:00 PM')
             *
             * This function uses DateTimeUtils.pickupWindowFromTimeInMinutes(timeInMinutes) to convert time in minutes into PickupWindow
             * object and then to string.
             */
            var pickupWindow = this.pickupWindowFromTimeInMinutes(timeInMinutes);

            if (pickupWindow) {
                var hours = pickupWindow.hours;
                var minutes = pickupWindow.minutes < 10 ? '0' + pickupWindow.minutes : pickupWindow.minutes;
                var midday = pickupWindow.am ? 'AM' : 'PM';
                return hours + ":" + minutes + " " + midday;
            }

            return undefined;
        },
        daysInYear: function (date) {
            var year = date.getFullYear();
            if (year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0)) {
                // Leap year
                return 366;
            } else {
                // Not a leap year
                return 365;
            }
        },
        daysBetweenTwoDates: function (startDate, endDate) {
            return Math.abs(Math.floor((startDate - endDate) / MILLISECONDS_IN_A_DAY));
        },
        composeDateTime: function (date, timeObject) {
            var time = new Date(date);
            time.setHours(timeObject.hours);

            if (!timeObject.am) {
                time.setHours(timeObject.hours + 12);
            }

            time.setMinutes(timeObject.minutes);

            return time;
        },
        /**
         * A function that takes an array of dates and returns the biggest date
         */
        getBiggestDate: function (dateArray) {
            if (dateArray && dateArray.length) {
                var biggestDate = new Date(dateArray[0]);

                for (var i = 1; i < dateArray.length; i++) {
                    var tempDate = new Date(dateArray[i]);
                    if (tempDate > biggestDate)
                        biggestDate = tempDate;
                }

                return biggestDate;
            }
        }
    };
});