angular.module('pricing').factory('LtlPricingZonesService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/zones/:url', {
        id: '@id',
        status: '@status'
    }, {
        active: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'active'
            }
        },
        archived: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'inactive'
            }
        }
    });
}]);

angular.module('pricing').factory('LtlPricingZoneStatusChangeService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:profile/zones/:status', {
        profile: '@profile',
        ids: '@ids',
        status: '@status'
    }, {
        inactivate: {
            method: 'PUT',
            isArray: true,
            params: {
                status: 'inactivate',
                isActiveList: '@isActiveList'
            }
        },
        reactivate: {
            method: 'PUT',
            isArray: true,
            params: {
                status: 'reactivate'
            }
        }
    });
}]);

angular.module('pricing').factory('LtlPricingZoneSaveService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:profile/zones/:id', {}, {
        get: {
            method: 'GET',
            params: {
                profile: '@profile',
                id: '@id'
            }
        },
        save: {
            method: 'POST',
            params: {
                profile: '@profile',
                id: 'save'
            }
        },
        copy: {
            method: 'POST',
            params: {
                profile: '@profile',
                id: 'copy'
            }
        }
    });
}]);
angular.module('pricing').factory('ZonesService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:profile/zones/:url', {}, {
        all: {
            method: 'GET',
            params: {
                profile: '@profile'
            }
        },
        dictionary: {
            method: 'GET',
            isArray: true,
            params: {
                profile: '@profile',
                url: 'dictionary'
            }
        }
    });
}]);
