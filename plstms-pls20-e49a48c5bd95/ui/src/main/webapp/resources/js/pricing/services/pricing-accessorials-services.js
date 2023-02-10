/**
 * Service for accessing Pricing Details page dictionary rest service.
 */
angular.module('pricing').factory('AccessorialTypesService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/dictionary/allAccessorialTypes', {}, {
        get: {
            method: 'GET',
            isArray: true
        }
    });
}]);

angular.module('pricing').factory('AccessorialsListService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/accessorials/:status', {
        id: '@id',
        status: '@status'
    }, {
        active: {
            method: 'GET',
            isArray: true,
            params: {
                status: 'active'
            }
        },
        expired: {
            method: 'GET',
            isArray: true,
            params: {
                status: 'expired'
            }
        },
        archived: {
            method: 'GET',
            isArray: true,
            params: {
                status: 'inactive'
            }
        }
    });
}]);

angular.module('pricing').factory('AccessorialsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:profile/accessorials/:id', {}, {
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
                profile: '@profile'
            }
        }
    });
}]);

angular.module('pricing').factory('AccessorialStatusChangeService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:profile/accessorials/:status', {
        profile: '@profile',
        ids: '@ids',
        status: '@status'
    }, {
        inactivate: {
            method: 'POST',
            isArray: true,
            params: {
                status: 'inactivate',
                isActiveList: '@isActiveList'
            }
        },
        expire: {
            method: 'POST',
            isArray: true,
            params: {
                status: 'expire'
            }
        },
        reactivate: {
            method: 'POST',
            isArray: true,
            params: {
                status: 'reactivate'
            }
        }
    });
}]);

angular.module('pricing').factory('CloneAccessorialsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/accessorials/cloneActive/:copyFromProfileDetailId', {}, {
        copy: {
            method: 'POST',
            params: {
                id: '@id',
                copyFromProfileDetailId: '@copyFromProfileDetailId'
            }
        }
    });
}]);

angular.module('pricing').factory('AccessorialsMappingService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/carrierAccessorials/:pathParam/:carrierId', {}, {
        getAccessorialsMapping: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'getMapping',
                carrierId: '@carrierId'
            }
        },
        saveAccessorialsMapping: {
            method: 'POST',
            params: {
                pathParam: 'saveMapping'
            }
        }
    });
}]);