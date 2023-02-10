/**
 * Service for accessing Pricing Details page dictionary rest service.
 */
angular.module('pricing').factory('PricingDetailsDictionaryService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/1/pricing/dictionary');
}]);

angular.module('pricing').factory('PricingDetailsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:detail/pricing/:url', {}, {
        active: {
            method: 'GET',
            isArray: true,
            params: {
                detail: '@detail',
                url: 'active'
            }
        },
        expired: {
            method: 'GET',
            isArray: true,
            params: {
                detail: '@detail',
                url: 'expired'
            }
        },
        inactive: {
            method: 'GET',
            isArray: true,
            params: {
                detail: '@detail',
                url: 'inactive'
            }
        },
        get: {
            method: 'GET',
            params: {
                detail: '@detail',
                url: '@id'
            }
        },
        save: {
            method: 'POST',
            params: {
                detail: '@detail'
            }
        }
    });
}]);

angular.module('pricing').factory('PricingDetailCopyFromService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:detail/pricing/copyfrom/:detailToCopy', {}, {
        copy: {
            method: 'POST',
            params: {
                detail: '@detail',
                detailToCopy: '@detailToCopy'
            }
        }
    });
}]);

angular.module('pricing').factory('PricingDetailStatusService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:profile/pricing/:url', {
        profile: '@profile',
        url: '@url'
    }, {
        inactivate: {
            method: 'POST',
            isArray: true,
            params: {
                url: 'inactivate',
                isActiveList: '@isActiveList'
            }
        },
        expirate: {
            method: 'POST',
            isArray: true,
            params: {
                url: 'expirate'
            }
        },
        reactivate: {
            method: 'POST',
            isArray: true,
            params: {
                url: 'reactivate'
            }
        }
    });
}]);
