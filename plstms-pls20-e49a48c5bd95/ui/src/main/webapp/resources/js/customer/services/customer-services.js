/**
 * Service to get customer product list sort.
 *
 * @author: Sergey Kirichenko
 * Date: 5/7/13
 * Time: 4:52 PM
 */
angular.module('plsApp').factory('CustomerService', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        getAccountExecutives: function () {
            return $http.get(urlConfig.core + "/customer/accountExec");
        },
        getAccountExecutivesByFilter: function (criteria) {
            return $http.get(urlConfig.core + "/customer/accountExecByFilter", {params: {filter: criteria}});
        },
        checkCustomerName: function (customerName) {
            return $http.get(urlConfig.core + "/customer/name/exist?name=" + encodeURIComponent(customerName));
        },
        checkTaxId: function (taxId) {
            return $http.get(urlConfig.core + "/customer/federalTaxId/exist?id=" + encodeURIComponent(taxId));
        },
        isCustomerLogoAvailable: function (customerId) {
            return $http.get(urlConfig.core + "/organization/" + customerId + "/isLogoAvailable");
        },
        checkEdiNum: function (ediNum) {
            return $http.get(urlConfig.core + "/customer/ediNumber/exists?ediNum=" + encodeURIComponent(ediNum));
        },
        getCreditLimit: function (customerId) {
            return $http.get(urlConfig.core + "/customer/" + customerId + "/creditLimit");
        }
    };
}]);

/**
 * Service to set customer product list sort.
 */
angular.module('plsApp').factory('CustomerOrderService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/product-list-sort', {}, {
        setProductListPrimarySort: {
            method: 'PUT',
            params: {
                customerId: '@customerId'
            },
            headers: {
                'Content-Type': 'text/plain'
            }
        },
        getProductListPrimarySort: {
            method: 'GET',
            params: {
                customerId: '@customerId'
            },
            transformResponse: function (data) {
                var rs = {
                    result: data
                };

                var unusedSymbol = '"';

                while (rs.result.indexOf(unusedSymbol) !== -1) {
                    rs.result = rs.result.replace(unusedSymbol, '');
                }

                return rs;
            }
        }
    });
}]);

angular.module('plsApp').factory('CustomerResource', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/customer/:customerId", {customerId: '@customerId'}, {});
}]);

angular.module('plsApp').factory('CustomerLabelResource', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/customer/customerLabel/:customerId", {customerId: '@customerId'}, {});
}]);

angular.module('plsApp').factory('CustomerNotesService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/customer/:customerId/note", {
        customerId: '@customerId'
    }, {
        getCustomerNotes: {
            method: 'GET',
            isArray: true
        },
        saveCustomerNote: {
            method: 'POST'
        }
    });
}]);

angular.module('plsApp').factory('GetCustomersService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/customer/list/:pathParam", {}, {
        list: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: '@pathParam',
                name: '@name',
                businessUnitName: '@businessUnitName'
            }
        }
    });
}]);

angular.module('plsApp').factory('CustomerInternalNoteService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/customer/:customerId/internal-note", {customerId: '@customerId'}, {
        get: {
            method: 'GET'
        }
    });
}]);
