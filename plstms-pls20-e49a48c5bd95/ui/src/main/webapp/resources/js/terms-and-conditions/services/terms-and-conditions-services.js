/**
 * Service for accessing Terms and Conditions
 */
angular.module('termsAndConditions').factory('TermsAndConditionsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.termsAndConditions, {}, {
        isTermsAndConditionsApplied: {
            method: 'GET',
            transformResponse: function (data) {
                return {
                    result: data
                };
            },
            isArray: false
        },
        applyTermsAndConditions: {
            method: 'POST'
        }
    });
}]);