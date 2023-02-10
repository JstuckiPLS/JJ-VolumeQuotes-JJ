// Zip service
angular.module('plsApp.directives.services').factory('ZipService', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        findZip: function (criteria, country, count) {
            return $http.get(urlConfig.core + "/zip/", {params: {searchCriteria: criteria, country: country, count: count}}).
            then(function (response) {
                return response.data;
            });
        }
    };
}]);