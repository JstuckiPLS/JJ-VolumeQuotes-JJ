/**
 * Service for accessing Shipment Tracking Boards REST service
 */
angular.module('trackingBoardServices').factory('TrackingBoardService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/shipment/tracking/board/:pathParam', {}, {
        alert: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'alert'
            }
        },
        undelivered: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'undelivered'
            }
        },
        unbilled: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'unbilled'
            }
        },
        booked: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'booked'
            }
        },
        open: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'open'
            }
        },
        cancel: {
            method: 'POST',
            params: {
                pathParam: 'cancel',
                shipmentId: '@shipmentId'
            }
        },
        all: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'all'
            }
        },
        allManualBol: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'allManualBol'
            }
        },
        getContactInfo: {
            method: 'GET',
            params: {
                pathParam: 'contact-info',
                customerId: '@customerId',
                shipmentId: '@shipmentId'
            }
        },
        getHoldShipments: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'hold'
            }
        }
    });
}]);

/**
 * Service for accessing Shipment Tracking Boards REST service
 */
angular.module('trackingBoardServices').factory('ShipmentStatus', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/shipment/tracking/board/:pathParam/:shipmentId', {}, {
        cancel: {
            method: 'POST',
            params: {
                pathParam: 'cancel',
                shipmentId: '@shipmentId'
            }
        },
        dispatch: {
            method: 'POST',
            params: {
                pathParam: 'dispatch',
                shipmentId: '@shipmentId'
            }
        },
        overridedate: {
            method: 'POST',
            params: {
                pathParam: 'override',
                shipmentId: '@shipmentId'
            }
        }
    });
}]);

angular.module('trackingBoardServices').factory('TrackingBoardAlertService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/shipment/tracking/board/alert/:pathParam/:entityId', {}, {
        alert: {
            method: 'GET',
            isArray: true
        },
        acknowledgeAlerts: {
            method: 'POST',
            params: {
                pathParam: 'acknowledge',
                entityId: '@shipmentId'
            }
        },
        count: {
            method: 'GET',
            params: {
                pathParam: 'count'
            }
        }
    });
}]);

angular.module('trackingBoardServices').factory('TrackingBoardTerminalInfoService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/shipment/proposal/terminal-info', {}, {
        getTerminalInfo: {
            method: 'GET',
            isArray: false
        }
    });
}]);
