angular.module('plsApp'). factory('DateRangeHelper', ['DateTimeUtils', '$filter', function(DateTimeUtils, $filter) {

    function filterDate(date) {
        return $filter('date')(DateTimeUtils.parseISODate(date), "yyyy-MM-dd");
    }

    function getDates(selector) {
        var startDate,
            date = new Date();

        switch (selector) {
        case 'TODAY':
            startDate = date;
            break;
        case 'WEEK':
            startDate = DateTimeUtils.getFirstDayOfWeek(date);
            break;
        case 'MONTH':
            startDate = new Date(date.getFullYear(), date.getMonth(), 1);
            break;
        case 'QUARTER':
            var quarter = Math.floor((date.getMonth() + 3) / 3);
            startDate = new Date(date.getFullYear(), (quarter-1) * 3, 1);
            break;
        case 'YEAR':
            startDate = new Date(date.getFullYear(), 0, 1);
            break;
        }

        return {
            startDate: filterDate(startDate),
            endDate: filterDate(date)
        };
    }
    return {
        getDates: getDates
    };
}]);