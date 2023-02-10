angular.module('plsApp.directives.services').factory('EmailRecipientsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + '/user/filterEmail', {}, {
        findMachingUsers: {
            method: 'GET',
            isArray: true,
            params: {
                param: '@param' // name of field for filtering
            }
        }
    });
}]);