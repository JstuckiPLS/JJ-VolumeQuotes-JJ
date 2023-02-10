/**
 * Service for accessing Shipment Financial Boards REST service
 */
angular.module('admin').factory('AuditService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/logs/:pathParam', {}, {
        getLogs: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'searchLogs'
            }
        },
        resubmit: {
            method: 'POST',
            params: {
                pathParam: 'resubmit'
            }
        },
        getLogDetails: {
            method: 'GET',
            params: {
                pathParam: 'getLogDetails'
            }
        }
    });
}]);