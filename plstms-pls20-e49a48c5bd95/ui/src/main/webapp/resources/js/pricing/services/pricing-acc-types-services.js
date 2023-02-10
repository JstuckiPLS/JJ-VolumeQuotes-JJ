angular.module('pricing').factory('AccTypesServices', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + '/accessorial-types/:url', {}, {
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
        },
        applicableunits: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'applicable-to-units'
            }
        },
        get: {
            method: 'GET',
            isArray: true,
            params: {
                url: '@code'
            }
        },
        save: {
            method: 'POST'
        },
        activate: {
            method: 'POST',
            isArray: true,
            params: {
                url: 'activate'
            }
        },
        inactivate: {
            method: 'POST',
            isArray: true,
            params: {
                url: 'inactivate'
            }
        },
        listAccessorialsByGroup: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'listByGroup',
                group: '@group'
            }
        },
        isUnique: {
            method: 'GET',
            params: {
                url: 'isUnique',
                accessorialTypeCode: '@accessorialTypeCode'
            },
            transformResponse: function (data) {
                return {
                    result: data
                };
            },
            isArray: false
        }
    });
}]);

angular.module("pricing").factory("CheckAccTypeCodeExists", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.core + '/accessorial-types/:code/exists', {}, {
        validate: {
            method: "GET",
            params: {
                accessorialTypeCode: "@code"
            },
            transformResponse: function (data) {
                return {
                    result: data
                };
            },
            isArray: false
        }
    });
}]);

