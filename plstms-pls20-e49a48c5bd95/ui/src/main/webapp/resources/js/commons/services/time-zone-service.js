/**
 * Resource to get the list of all timezones.
 * @author: Alex Kirichenko
 * Date: 9/3/13
 * Time: 1:55 PM
 */
angular.module('plsApp.directives.services').factory('TimeZoneResource', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + '/timezone', {}, {
        getAll: {
            method: 'GET',
            cache: true,
            isArray: true
        }
    });
}]);