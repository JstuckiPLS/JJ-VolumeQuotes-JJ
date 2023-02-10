angular.module('pricing').factory('GetBlockCarrierGeoService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/blockedCarriers/profile/:id/:url', {
        url: '@url',
        id: '@id'
    }, {
        active: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'active'
            }
        },
        inactive: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'inactive'
            }
        }
    });
}]);

angular.module('pricing').factory('CustomerBlockLaneService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/blockLane/:id', {}, {
        get: {
            method: 'GET',
            params: {
                id: '@id'
            }
        },
        save: {
            method: 'POST'
        }
    });
}]);

angular.module('pricing').factory('GetCustomerBlockLaneService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/blockLane/profile/:id/:url', {
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

angular.module('pricing').factory('ChangeStatusBlockCarrierGeoService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/blockedCarriers/:url', {
        url: '@url'
    }, {
        inactivate: {
            method: 'PUT',
            isArray: true,
            params: {
                url: 'inactivate',
                profileId: '@profileId'
            }
        },
        reactivate: {
            method: 'PUT',
            isArray: true,
            params: {
                url: 'reactivate',
                profileId: '@profileId'
            }
        }
    });
}]);

angular.module('pricing').factory('ChangeStatusBlockLaneService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/blockLane/:url', {
        url: '@url'
    }, {
        inactivate: {
            method: 'PUT',
            isArray: true,
            params: {
                url: 'inactivate',
                isActive: '@isActive',
                profileId: '@profileId'
            }
        },
        reactivate: {
            method: 'PUT',
            isArray: true,
            params: {
                url: 'reactivate',
                profileId: '@profileId'
            }
        },
        expire: {
            method: 'POST',
            isArray: true,
            params: {
                url: 'expire',
                profileId: '@profileId'
            }
        }
    });
}]);

angular.module('pricing').factory('CloneBlockCarrierGeoService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/blockedCarriers/:url', {
        url: '@url'
    }, {
        copy: {
            method: 'POST',
            params: {
                url: 'copyfrom'
            }
        }
    });
}]);

/**
 * Service for accessing the blanket carrier profiles scac + name.
 */
angular.module('pricing').factory('GetBlanketCarrListService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/blockLane/blanketCarrList/:id', {
        id: '@id'
    });
}]);