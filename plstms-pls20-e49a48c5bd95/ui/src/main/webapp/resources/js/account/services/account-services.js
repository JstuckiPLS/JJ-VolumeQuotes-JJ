// Account services
angular.module('plsApp').factory('AccountCalendarService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/user/:userId/account/calendar/:pathDefineParam', {}, {
        get: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                userId: '@userId',
                basedOn: '@basedOn',
                dateOfMonth: "@dateOfMonth"
            },
            isArray: true
        },
        details: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                userId: '@userId',
                pathDefineParam: 'details'
            },
            isArray: true
        }
    });
}]);

angular.module('plsApp').factory('AccountHistoryService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/user/:userId/account/history/:pathDefineParam', {}, {
        get: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                userId: '@userId',
                search: '@search',
                sf1: '@sortField',
                so1: '@sortValue'
            },
            isArray: true
        }
    });
}]);