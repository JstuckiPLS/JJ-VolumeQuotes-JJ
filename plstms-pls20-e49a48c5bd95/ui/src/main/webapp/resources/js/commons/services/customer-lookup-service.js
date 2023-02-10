// Scac service
angular.module('plsApp.directives.services').factory('CustomerLookupService', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        findCustomer: function (criteria, count, status) {
            return $http.get(urlConfig.core + "/customer/idNameTuples", {
                params: {
                    name: criteria,
                    limit: count,
                    status: status,
                    offset: 0
                }
            }).then(function (response) {
                return response.data;
            });
        }
    };
}]);
