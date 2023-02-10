/**
 * Service for communicating with profile list rest service.
 */
angular.module('pricing').factory('ProfilesListService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/list', {}, {
        get: {
            method: 'POST',
            isArray: true
        }
    });
}]);

/**
 * Service for accessing with profile dictionary rest service.
 */
angular.module('pricing').factory('ProfileDictionaryService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/defaultDictionary');
}]);

/**
 * Service for communicating with profile details rest service.
 */
angular.module('pricing').factory('ProfileDetailsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id', {}, {
        get: {
            method: 'GET',
            params: {
                id: '@id'
            }
        },
        save: {
            method: 'POST',
            params: {
                id: 'save'
            }
        },
        copy: {
            method: 'POST',
            params: {
                id: 'saveCopiedProfile'
            }
        }
    });
}]);

/**
 * Service for communicating with profile details rest service.
 */
angular.module('pricing').factory('ProfileApplicableCustomersService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/applicableCustomers', {}, {
        get: {
            method: 'GET',
            isArray: true,
            params: {
                id: '@id'
            }
        }
    });
}]);

/**
 * Service for communicating with profile details rest service.
 */
angular.module('pricing').factory('ProfileApplicableCustomersForSMC3Service', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        getSMC3: function (smc3TariffName) {
            return $http.get(urlConfig.pricing + '/profile/applicableCustomersForSMC3?tariffName=' + smc3TariffName);
        }
    };
}]);

/**
 * Service for communicating with profile details rest service.
 */
angular.module('pricing').factory('GetUnsavedProfileCopyService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/getUnsavedProfileCopy', {}, {
        get: {
            method: 'GET',
            params: {
                id: '@id'
            }
        }
    });
}]);

/**
 * Service for communicating with profile details rest service.
 */
angular.module('pricing').factory('GetCustomerMarginProfileService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/getCustomerMarginProfile', {}, {
        get: {
            method: 'GET',
            params: {
                id: '@id'
            }
        }
    });
}]);

/**
 * Service for communicating with profile details rest service.
 */
angular.module('pricing').factory('SaveCopiedProfileService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/saveCopiedProfile', {}, {
        copy: {
            method: 'POST'
        }
    });
}]);

/**
 * Service for communicating with profile details rest service and inactivate/activate profiles.
 */
angular.module('pricing').factory('ProfileStatusChangeService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:url', {
        url: '@url',
        ids: '@ids'
    }, {
        inactivate: {
            method: 'POST',
            isArray: true,
            params: {
                url: 'inactivate',
                isActiveList: '@isActiveList'
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

/**
 * Service for accessing list of active profiles for Copy From dropdown through restful call.
 */
angular.module('pricing').factory('ProfilesListToCopyService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/copylist', {}, {
        get: {
            method: 'POST',
            isArray: true
        }
    });
}]);

/**
 * Service for communicating with profile details rest service.
 */
angular.module('pricing').factory('ProfileAffectedCustomersService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/affectedCustomers', {}, {
        get: {
            method: 'GET',
            isArray: true,
            params: {
                id: '@id'
            }
        }
    });
}]);

/**
 * Service for communicating with profile details rest service.
 */
angular.module('pricing').factory('AssignedCustomersSearchService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/organization/idNameTuples/SHIPPER/:name', {}, {
        get: {
            method: 'GET',
            isArray: true,
            params: {
                name: '@name'
            }
        }
    });
}]);

/**
 * Service for accessing with profile assigned customers rest service.
 */
angular.module('pricing').factory('ProfileAssignedCustomersService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource('json/profileAssignedCustomers.json');
}]);

/**
 * Service for communicating with profile details rest service.
 */
angular.module('pricing').factory('ProfileSelectedCarrierAPIService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/organization/getCarrierAPIDetails/:id', {}, {
        get: {
            method: 'GET',
            params: {
                id: '@id'
            }
        }
    });
}]);

/**
 * Service for accessing with profile notes rest service.
 */
angular.module('pricing').factory('ProfileNotesService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/notes/', {}, {
        getNotes: {
            method: 'GET',
            isArray: true,
            params: {
                id: '@id'
            }
        },
        save: {
            method: 'POST',
            params: {
                id: '@id'
            }
        }
    });
}]);
/**
 * Service for accessing with profile active customers rest service.
 *
 */
angular.module('pricing').factory('ActivePricingProfilesService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/list?sort=null%2Cnull&filter=null%2Cnull&profileFilterParam=:id', {
        id: new Date(1).getTime() + ';' + new Date().getTime() + ';EFFECTIVE;1;' +
        '[BLANKET, BLANKET_CSP, CSP, BUY_SELL, GAINSHARE, BENCHMARK];[ACTIVE]'
    });
}]);

/**
 * Service for accessing with profile customers rest service.
 */
angular.module('pricing').factory('PricingProfileByIdService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id', {
        id: '@id'
    });
}]);

/**
 * Service for accessing with customer Liabilities rest service.
 */
angular.module('pricing').factory('CarrierLiabilitiesByProfileIdService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/liabilities/:action/:isPallet', {}, {
        get: {
            method: 'GET',
            params: {
                id: '@id',
                action: 'pallet',
                isPallet: '@isPallet'
            },
            isArray: true
        },
        copyLiabilities: {
            method: 'POST',
            params: {
                id: '@id',
                action: 'copyLiabilities',
                isPallet: '@isPallet'
            },
            isArray: true
        }
    });
}]);

angular.module('pricing').factory('CopyLiabilities', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/liabilities/copyLiabilities/:copyFrom', {}, {
        copy: {
            method: 'POST',
            params: {
                id: '@id',
                copyFrom: '@copyFrom'
            }
        }
    });
}]);

angular.module('pricing').factory('CopyProhibitedCommoditiesService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/liabilities/copyCommodities/:copyFrom', {}, {
        copy: {
            method: 'POST',
            params: {
                id: '@id',
                copyFrom: '@copyFrom'
            }
        }
    });
}]);

angular.module('pricing').factory('ProhibitedNLiabilitiesService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/profile/:id/liabilities/save', {}, {
        save: {
            method: 'POST'
        }
    });
}]);
