/**
 * Service to get customer activity related data.
 *
 * @author: Eugene Borshch
 */
angular.module('plsApp').factory('CustomerActivityService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/customer/activity", {}, {
        getActivity: {
            method: 'GET',
            isArray: true,
            params: {
                dateRange: '@dateRange'
            }
        }
    });
}]);