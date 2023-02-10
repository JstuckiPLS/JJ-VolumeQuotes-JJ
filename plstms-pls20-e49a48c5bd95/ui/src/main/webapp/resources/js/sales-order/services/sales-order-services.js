angular.module('plsApp').factory('AccessorialTypeService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/shipment/proposal/accessorialTypes', {}, {
        getAccessoralTypes: {
            method: 'GET',
            isArray: true
        }
    });
}]);

angular.module('plsApp').factory('ShipmentDictionaryTypeService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/shipment/dictionary/financialReasons', {}, {
        getFinancialReasons: {
            method: 'GET',
            isArray: true
        }
    });
}]);

angular.module('plsApp').factory('ShipmentAuditService',
        ['$resource', 'urlConfig', function ($resource, urlConfig) {
            return $resource(urlConfig.shipment + '/shipment/:shipmentId/audit', {}, {
                updateCost: {
                    method: 'POST',
                    params: {
                        shipmentId: '@shipmentId'
                    }
                },
                getInvoiceAdditionalDetails: {
                    method: 'GET',
                    params: {
                        shipmentId: '@shipmentId'
                    }
                }
            }
        );
}]);