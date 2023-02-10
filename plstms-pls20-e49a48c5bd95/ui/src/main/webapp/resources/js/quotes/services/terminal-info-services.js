angular.module('plsApp').factory('TerminalInfoService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/shipment/proposal/:pathParam', {}, {
        getTerminalInfo: {
            method: 'GET',
            isArray: false,
            params: {
                pathParam: 'terminal-info'
            },
            cache: true
        },
        getTerminalInfoForManualBol: {
            method: 'GET',
            isArray: false,
            params: {
                pathParam: 'manual-bol-terminal-info'
            },
            cache: true
        }
    });
}]);