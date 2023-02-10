/**
 * Notification service
 * @author: Alexander Kirichenko
 */
angular.module('plsApp.common.services').factory('DictionaryService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/dictionary/:path', {}, {
        getAllNotificationTypes: {
            method: "GET",
            cache: true,
            params: {
                path: "notificationTypes"
            },
            isArray: true
        },
        getCompanyCodes: {
            method: 'GET',
            isArray: true,
            params: {
                path: 'companyCodes'
            }
        },
        getCustomerPayTerms: {
            method: "GET",
            cache: true,
            params: {
                path: "customerPayTerms"
            },
            isArray: true
        },
        getCustomerPayMethods: {
            method: "GET",
            cache: true,
            params: {
                path: "customerPayMethod"
            },
            isArray: true
        },
        getGlNumComponents: {
            method: "GET",
            cache: true,
            params: {
                path: "glNumComponents"
            },
            isArray: true
        },
        getBrandNumComponents: {
            method: "GET",
            cache: true,
            params: {
                path: "brandNumComponents"
            },
            isArray: true
        },
        getAlumaNumComponents: {
            method: "GET",
            cache: true,
            params: {
                path: "alumaNumComponents"
            },
            isArray: true
        },
        getPLSPayURL: {
            method: 'GET',
            cache: true,
            transformResponse: function (data) {
                return {
                    result: data
                };
            },
            params: {
                path: 'plsPayURL'
            }
        },        
        getGLValuesForFreightCharge: {
            method: "GET",
            cache: true,
            params: {
                path: "getGLNumberForFreightCharge"
            },
            isArray: true
        }
    });
}]);