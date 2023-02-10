/**
 * Service for login functionality.
 *
 */
angular.module('plsApp.directives.services').factory('PlsAuthService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.login + '/auth/:login/password/reset', {}, {
        resetPassword: {
            method: 'POST',
            params: {
                login: '@login'
            }
        }
    });
}]);

