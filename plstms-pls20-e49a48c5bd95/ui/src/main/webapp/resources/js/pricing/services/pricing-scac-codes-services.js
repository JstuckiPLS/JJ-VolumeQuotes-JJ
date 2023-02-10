angular.module('pricing').factory('SimpleCarrierService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/carrier/carrierInfos', {}, {
        getCarriers: {
            method: 'GET',
            isArray: true
        }
    });
}]);

angular.module('pricing').factory('CarrierService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/carrier/:carrierId', {}, {
        getCarrier: {
            method: 'GET',
            params: {
                carrierId: '@carrierId'
            }
        },
        saveCarrier: {
            method: 'POST'
        }
    });
}]);

angular.module('pricing').factory('CarrierApiService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/organization/getCarrierAPIDetails/:carrierId', {carrierId: '@carrierId'}, {
        getApiByCarrierId: {
            method: 'GET',
            isArray: false
        }
    });
}]);

angular.module('pricing').factory('UpdateCarrierApiService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/organization/updateCarrierAPIDetails', {}, {
        updateCarrierAPIDetails: {
            method: 'POST',
            isArray: false
        }
    });
}]);

angular.module('pricing').factory('OrganizationService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/organization/orgServices/:pathParam/:orgId', {}, {
        getOrgServices: {
            method: 'GET',
            isArray: false,
            params: {
                pathParam: 'get',
                orgId: '@orgId'
            }
        },
        saveOrgServices: {
            method: 'POST',
            params: {
                pathParam: 'save'
            }
        }
    });
}]);

angular.module('pricing').factory('ApiTypeService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/organization/apiType/:pathParam/:orgId/:category', {}, {
        getApiType: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'get',
                orgId: '@orgId',
                category: '@category'
            }
        },
        saveApiType: {
            method: 'POST',
            params: {
                pathParam: 'save'
            }
        }
    });
}]);