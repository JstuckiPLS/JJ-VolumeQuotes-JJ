/**
 * Service to get dictionary data.
 *
 * @author: Sergey Kirichenko
 * Date: 10/10/13
 * Time: 1:07 PM
 */
angular.module('plsApp.common.services').factory('ShipmentDictionaryService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/shipment/dictionary/:path', {}, {
        getPackageTypes: {
            method: "GET",
            cache: true,
            params: {
                path: "packageType"
            },
            isArray: true
        },
        getAuditReasonCode: {
            method: "GET",
            isArray: true,
            cache: true,
            params: {
                path: "auditReasonCode"
            }
        },
        getAllNetworks: {
            method: "GET",
            isArray: true,
            cache: false,
            params: {
                path: "getAllNetworks"
            }
        },
        getBillToRequiredField: {
            method: "GET",
            cache: true,
            params: {
                path: "billToReqField"
            },
            isArray: true
        }
    });
}]);