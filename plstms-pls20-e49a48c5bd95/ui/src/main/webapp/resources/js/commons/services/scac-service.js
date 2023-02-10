// Scac service
angular.module('plsApp.directives.services').factory('ScacService', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        findScac: function (criteria, count) {
            return $http.get(urlConfig.core + "/carrier/list/byName/" + criteria,
                    {params: {limit: count, offset: 0}}).then(function (response) {
                return response.data;
            });
        }
    };
}]);