/**
 * Service to obtain customers which can be handled by current user.
 */
angular.module('plsApp').factory('RateQuoteCustomerService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer', {}, {
        getAllCustomers: {
            isArray: true,
            method: 'GET'
        }
    });
}]);