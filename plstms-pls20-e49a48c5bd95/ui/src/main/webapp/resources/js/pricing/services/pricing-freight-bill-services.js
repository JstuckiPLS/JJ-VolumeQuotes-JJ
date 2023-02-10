angular.module('pricing').factory('ThirdPartyInfoService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:profileDetailId/thirdParty', {}, {
        get: {
            method: 'GET',
            params: {
                profileDetailId: '@profileDetailId'
            }
        },
        save: {
            method: 'POST',
            params: {
                profileDetailId: '@profileDetailId'
            }
        }
    });
}]);

angular.module('pricing').factory('ThirdPartyCopyFromService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:detail/thirdParty/copyfrom/:detailToCopy', {}, {
        copy: {
            method: 'POST',
            params: {
                detail: '@detail',
                detailToCopy: '@detailToCopy'
            }
        }
    });
}]);
