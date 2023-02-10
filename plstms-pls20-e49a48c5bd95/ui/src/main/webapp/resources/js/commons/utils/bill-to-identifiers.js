angular.module('plsApp.utils').factory('isRequiredField', [ '_', function(_) {
    return function(billToRequiredFields, fieldCode) {
        return _.some(billToRequiredFields, function(item) {
            return item.name === fieldCode && _.isMatch(item, {
                required : true
            });
        });
    };
} ]);
