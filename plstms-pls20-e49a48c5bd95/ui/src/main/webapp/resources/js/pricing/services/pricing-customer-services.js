/**
 * Service for Customer Pricing
 */
angular.module('pricing').factory('CustomerPricingService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/:url', {}, {
        get: {
            method: 'GET',
            params: {
                url: 'custPricProfiles',
                id: '@id'
            }
        },
        save: {
            method: 'POST',
            params: {
                url: 'saveCustPricProfiles',
                id: '@id'
            }
        }
    });
}]);

angular.module('pricing').factory('CustomerP44ConfigService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/:url', {}, {
        get: {
            method: 'GET',
            params: {
                url: 'custP44Config',
                id: '@id'
            }
        },
        list: {
            method: 'GET',
            params: {
                url: 'P44AccountGroups'
            },
            isArray: true
        },
        save: {
            method: 'POST',
            params: {
                url: 'custP44Config',
                id: '@id'
            }
        }
    });
}]);

angular.module('pricing').factory('SearchCustomersService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/searchCustomers', {}, {
        find: {
            method: 'POST',
            isArray: true
        }
    });
}]);

angular.module('pricing').factory('CustomerPricingSearchService', function() {
    var criteria;

    function getCriteria() {
        return criteria;
    }

    function setCriteria(c) {
        criteria = c;
    }

    return {
        getCriteria: getCriteria,
        setCriteria: setCriteria
    };
});