angular.module('pricing').factory('GetGuaranteedService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlGuaranteedPrice/profile/:id/:url', {
        url: '@url',
        id: '@id'
    }, {
        expired: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'expired'
            }
        },
        inactive: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'inactive'
            }
        },
        active: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'active'
            }
        }
    });
}]);

angular.module('pricing').factory('ChangeStatusGuaranteedService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlGuaranteedPrice/:url', {
        url: '@url'
    }, {
        inactivate: {
            method: 'PUT',
            isArray: true,
            params: {
                url: 'inactivate',
                isActiveList: '@isActiveList',
                //  guaranteedIds : '@guaranteedIds',
                profileDetailId: '@profileDetailId'
            }
        },
        reactivate: {
            method: 'PUT',
            isArray: true,
            params: {
                url: 'reactivate',
                // guaranteedIds : '@guaranteedIds',
                profileDetailId: '@profileDetailId'
            }
        },
        expire: {
            method: 'POST',
            isArray: true,
            params: {
                url: 'expire',
                profileDetailId: '@profileDetailId'
            }
        },
        copy: {
            method: 'POST',
            params: {
                url: 'copyfrom',
                profileId: '@profileId',
                profileToCopy: '@profileToCopy'
            }
        }
    });
}]);
