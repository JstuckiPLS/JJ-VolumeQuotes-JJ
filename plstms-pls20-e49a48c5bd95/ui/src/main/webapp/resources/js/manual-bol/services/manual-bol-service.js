angular.module('manualBol.services').factory('ManualBolService', ['$resource', function ($resource) {
    return $resource('/restful/customer/:customerId/manualbol/:shipmentId', {}, {
        saveBol: {
            method: 'POST',
            params: {
                customerId: '@customerId',
                storedBolId: '@storedBolId',
                storedLabelId: '@storedLabelId'
            }
        },
        getManualBol: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                shipmentId: '@shipmentId'
            }
        }
    });
}]);

angular.module('manualBol.services').factory('ManualBolDocumentService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/shipmentdocs/manualBol/:shipmentId/:pathParam', {}, {
        getDocumentList: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'list',
                shipmentId: '@shipmentId'
            }
        }
    });
}]);
