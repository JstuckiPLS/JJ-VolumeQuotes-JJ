angular.module('roles.services').factory('PermissionSearchService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/capabilities/:pathDefineParam', {}, {
        getAllPermissions: {
            method: 'GET',
            params: {
                pathDefineParam: 'all'
            },
            isArray: true
        }
    });
}]);

angular.module('roles.services').factory('PermissionSpecificService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/capabilities/:action/:pathDefineParam', {}, {
        getPermission: {
            method: 'GET',
            params: {
                action: 'get',
                pathDefineParam: '@pathDefineParam'
            }
        },
        getAvailablePermissions: {
            method: 'GET',
            params: {
                action: 'all',
                pathDefineParam: '@pathDefineParam'
            },
            isArray: true
        },
        getUsersWithPermission: {
            method: 'GET',
            params: {
                action: 'users',
                pathDefineParam: '@pathDefineParam'
            },
            isArray: true
        },
        unassignUsers: {
            method: 'PUT',
            params: {
                action: 'unassign',
                pathDefineParam: '@pathDefineParam'
            }
        }
    });
}]);

angular.module('roles.services').factory('RoleSearchService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/groups/:pathDefineParam', {}, {
        getAllRoles: {
            method: 'GET',
            params: {
                pathDefineParam: 'all'
            },
            isArray: true
        }
    });
}]);

angular.module('roles.services').factory('RoleSpecificService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/groups/:action/:pathDefineParam', {}, {
        getRole: {
            method: 'GET',
            params: {
                action: 'get',
                pathDefineParam: '@pathDefineParam'
            }
        },
        getUsersWithRole: {
            method: 'GET',
            params: {
                action: 'users',
                pathDefineParam: '@pathDefineParam'
            },
            isArray: true
        },
        unassignUsers: {
            method: 'PUT',
            params: {
                action: 'unassign',
                pathDefineParam: '@pathDefineParam'
            }
        },
        deleteRole: {
            method: 'PUT',
            params: {
                action: 'delete',
                pathDefineParam: '@pathDefineParam'
            }
        },
        saveRole: {
            method: 'POST',
            params: {
                action: 'save',
                pathDefineParam: '@pathDefineParam'
            }
        },
        isNameUnique: {
            method: 'GET',
            params: {
                action: 'isUnique',
                pathDefineParam: '@pathDefineParam',
                excludeGroup: '@excludeGroup'
            }
        }
    });
}]);