angular.module('pricing').factory('PalletPricingService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:detail/pallet/:status', {}, {
        active: {
            method: 'GET',
            isArray: true,
            params: {
                detail: '@detail',
                status: 'active'
            }
        },
        inactive: {
            method: 'GET',
            isArray: true,
            params: {
                detail: '@detail',
                status: 'inactive'
            }
        },
        save: {
            method: 'POST',
            isArray: true,
            params: {
                detail: '@detail',
                status: 'save'
            }
        },
        activate: {
            method: 'POST',
            params: {
                detail: '@detail',
                status: 'activate'
            }
        },
        inactivate: {
            method: 'POST',
            params: {
                detail: '@detail',
                status: 'inactivate'
            }
        }
    });
}]);

angular.module('pricing').factory('PalletePricingCopyFromService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:detail/pallet/:action/:detailToCopy', {}, {
        copy: {
            method: 'POST',
            params: {
                detail: '@detail',
                detailToCopy: '@detailToCopy',
                action: 'copyfrom'
            }
        },
        areZonesMissing: {
            method: 'POST',
            params: {
                detail: '@detail',
                detailToCopy: '@detailToCopy',
                action: 'areZonesMissing'
            }
        }
    });
}]);
