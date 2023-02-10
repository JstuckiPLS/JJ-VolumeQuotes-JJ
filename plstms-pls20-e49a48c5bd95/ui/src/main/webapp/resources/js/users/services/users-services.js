/**
 * Service to search users by filtering.
 */
angular.module('plsApp').factory('UserSearchService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/user/:searchType", {}, {
        search: {
            method: 'GET',
            isArray: true,
            params: {
                searchType: 'search',
                status: '@status',
                businessUnitId: '@businessUnitId',
                company: '@pathParam',
                searchName: '@searchName',
                searchValue: '@searchValue'
            }
        },
        progressiveSearch: {
            method: 'GET',
            isArray: true,
            params: {
                searchType: 'searchByName',
                filter: '@filter',
                count: '@count'
            }
        }
    });
}]);

/**
 * Service to load user details.
 */
angular.module('users.services').factory("UserDetailsService", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + "/user/:personId", {
        personId: ""
    }, {
        getUser: {
            method: "GET",
            params: {
                personId: "@personId"
            }
        },
        getCurrentUserContactDetails: {
            method: "GET",
            url: urlConfig.userMgmt + "/user/currentUserContactDetails",
            params: {
                personId: "@personId"
            }
        },
        saveUser: {
            method: "POST"
        },
        findUnassignedCustomers: {
            method: "GET",
            url: urlConfig.userMgmt + "/user/customers/search",
            params: {
                searchField: '@searchField',
                criteria: '@criteria',
                userId: '@userId'
            },
            isArray: true
        }
    });
}]);

angular.module('users.services').factory("UserNetworksService", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + "/user/activeNetworks", {}, {
        activeNetworks: {
            method: "GET",
            isArray: true
        }
    });
}]);

/**
 * Service to load user default notifications.
 */
angular.module('users.services').factory("UserNotificationsService", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + "/user/userId/:userId/notifications/:customerId/location/:locationId", {}, {
        getUserNotifications: {
            method: "GET",
            isArray: true,
            params: {
                customerId: '@customerId',
                userId: '@userId',
                locationId: '@locationId'
            }
        }
    });
}]);

/**
 * Service to activate/deactivate users.
 */
angular.module('users.services').factory("UserStatusMgmtService", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + "/user/:personId/status/:status", {}, {
        activate: {
            method: "POST",
            params: {
                status: "true",
                personId: "@personId"
            }
        },
        deactivate: {
            method: "POST",
            params: {
                status: "false",
                personId: "@personId"
            }
        }
    });
}]);

angular.module('users.services').factory("UserParentOrganizationsService", ["$http", "urlConfig", function ($http, urlConfig) {
    return {
        getParentOrganizationsByName: function (criteria, count) {
            return $http.get(urlConfig.userMgmt + "/user/parentOrganizationsByName", {
                params: {
                    name: criteria,
                    limit: count
                }
            }).then(function (response) {
                return response.data;
            });
        }
    };
}]);

/**
 * Service to load user details.
 */
angular.module('users.services').factory("UserMgmtCheckUserIdService", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + "/user/userId/:userId/:personId/valid", {}, {
        validate: {
            method: "GET",
            params: {
                userId: "@userId",
                personId: "@personId"
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

/**
 * Service to load user details.
 */
angular.module('users.services').factory("UserMgmtResetPasswordService", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + "/user/:personId/password/reset", {}, {
        reset: {
            method: "POST",
            params: {
                personId: "@personId"
            },
            isArray: false
        }
    });
}]);

/**
 * Service to change user password
 */
angular.module('users.services').factory("UserMgmtAlterPasswordService", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + "/user/password", {}, {
        changePassword: {
            method: "POST",
            params: {},
            isArray: false
        }
    });
}]);

angular.module('users.services').factory("UserAuthenticationService", ["$http", "$rootScope", "urlConfig", function ($http, $rootScope, urlConfig) {
    return {
        reSetAuthData: function () {
            return $http.get(urlConfig.login + '/auth/current_user').success(function (data) {
                $rootScope.authData = new AuthData(data);
                $rootScope.$broadcast('event:permissions-were-changed');
            });
        }
    };
}]);

/**
 * Service to load user default notifications.
 */
angular.module('users.services').factory("UserDefaultContactService", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + "/user/defaultContactInfo", {}, {
        getDefaultContactInfo: {
            method: "GET",
            isArray: false
        }
    });
}]);

/**
 * Service to get/update user settings
 */
angular.module('users.services').factory("UserSettingsService", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + "/user/settings", {}, {
        getUserSettings: {
            method: "GET",
            isArray: true
        },
        setUserSettings: {
            method: "POST"
        }
    });
}]);

angular.module('users.services').factory("TeamsService", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.userMgmt + "/user/teams", {}, {
        getTeams: {
            method: "GET",
            isArray: true
        }
    });
}]);