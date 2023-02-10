// Utility methods for strings
angular.module('plsApp.utils').factory('StringUtils', function () {
    return {
        format: function (string, args) {
            return string.replace(/\{(\d+)\}/g, function (match, number) {
                return args[number];
            });
        },
        lPadZero : function(value, size) {
            var result = _.isNumber(value) ? String(value) : '';
            while (result.length < size) {
                result = "0" + result;
            }
            return result;
        }
    };
});