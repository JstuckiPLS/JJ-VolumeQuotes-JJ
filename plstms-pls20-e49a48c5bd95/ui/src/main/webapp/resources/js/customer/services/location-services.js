/**
 * Service for organization locations.
 */
angular.module('plsApp').factory('LocationsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/location', {}, {
        getAllForCustomer: {
            method: 'GET',
            params: {
                customerId: '@customerId'
            },
            isArray: true
        }
    });
}]);

angular.module('plsApp').factory('CustomerLocationsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/location/:path', {}, {
        getListForCustomer: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                path: 'list'
            },
            isArray: true
        },
        getLocation: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                path: '@path'
            }
        },
        saveLocation: {
            method: 'POST',
            params: {
                customerId: '@customerId'
            }
        },
        getListForAssociatedCustomer: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                userId: '@userId',
                path: 'associated'
            },
            isArray: true
        }
    });
}]);