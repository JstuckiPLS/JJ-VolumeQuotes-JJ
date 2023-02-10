angular.module('pricing').factory('PricingTerminalInfoService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:profileId/terminalInfo', {}, {
        get: {
            method: 'GET',
            params: {
                profileId: '@profileId'
            }
        },
        save: {
            method: 'POST',
            params: {
                profileId: '@profileId'
            }
        }
    });
}]);

angular.module('pricing').factory('TerminalInfoCopyFromService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:detail/terminalInfo/copyfrom/:detailToCopy', {}, {
        copy: {
            method: 'POST',
            params: {
                detail: '@detail',
                detailToCopy: '@detailToCopy'
            }
        }
    });
}]);
