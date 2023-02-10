angular.module('vendorBillServices').factory('VendorBillService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/vendor-bills/:vendorBillId:loadId/:pathParam/:subParamId', {
        vendorBillId: '',
        loadId: ''
    }, {
        list: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: '@pathParam'
            }
        },
        changeState: {
            method: 'POST',
            params: {
                vendorBillId: '@vendorBillId',
                pathParam: '@action'
            }
        },
        archiveList: {
            method: 'POST',
            params: {
                vendorBillId: 'archiveList'
            }
        },
        createOrder: {
            method: 'GET',
            params: {
                vendorBillId: '@vendorBillId',
                subParamId: '@customerId',
                pathParam: 'create'
            }
        },
        get: {
            method: 'GET',
            params: {
                pathParam: '@pathParam'
            }
        },
        getMatchedSalesOrders: {
            method: 'GET',
            params: {
                pathParam: 'sale-order',
                vendorBillId: '@vendorBillId'
            },
            isArray: true
        },
        match: {
            method: 'PUT',
            params: {
                vendorBillId: '@vendorBillId',
                subParamId: '@shipmentId',
                pathParam: 'match'
            }
        },
        detach: {
            method: 'PUT',
            params: {
                loadId: '@loadId',
                pathParam: 'detach',
                loadVersion: '@loadVersion'
            }
        },
        getForShipment: {
            method: 'GET',
            params: {
                pathParam: 'list-by-shipment',
                subParamId: '@shipmentId'
            }
        },
        saveVendorBill: {
            method: 'POST'
        }
    });
}]);