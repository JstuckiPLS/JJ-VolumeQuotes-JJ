/**
 * Service for accessing address information.
 *
 * @author: Alexander Kirichenko
 * Date: 4/30/13
 * Time: 3:07 PM
 */
angular.module('plsApp.directives.services').factory('AddressService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/address/:subPath', {
        customerId: '@customerId'
    }, {
        listUserBillToAddresses: {
            method: 'GET',
            isArray: true,
            params: {
                subPath: 'billaddresses'
            }
        },
        getAddressBookList: {
            method: 'GET',
            isArray: true,
            params: {
                subPath: 'ltl_list'
            }
        },
        findAddress: {
            method: 'GET',
            params: {
                subPath: '@subPath'
            }
        },
        saveOrUpdateAddress: {
            method: 'PUT',
            params: {
                subPath: 'save_address'
            }
        },
        findContactSetInfo: {
            method: 'GET',
            params: {
                subPath: 'contacts'
            }
        }
    });
}]);

angular.module('plsApp.directives.services').factory('AddressesListService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/address/list/:subPath', {}, {
        findAddressBookByZip: {
            method: 'GET',
            isArray: true,
            params: {
                subPath: 'by_zip',
                customerId: '@customerId',
                country: '@country',
                zip: '@zip',
                city: '@city'
            }
        },
        listUserContacts: {
            method: 'GET',
            isArray: true,
            params: {
                subPath: '',
                customerId: '@customerId'
            }
        },
        listSuggestions: {
            method: 'GET',
            isArray: true,
            params: {
                subPath: 'suggestions',
                customerId: '@customerId',
                query: '@query'
            }
        }
    });
}]);

angular.module('plsApp.directives.services').factory('FreightBillService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/address/:subPath', {}, {
        search: {
            method: 'GET',
            isArray: true,
            params: {
                subPath: 'freightBill',
                customerId: '@customerId',
                filter: '@filter'
            }
        },
        getDefault: {
            method: 'GET',
            params: {
                subPath: 'defaultFreightBillPayTo',
                customerId: '@customerId'
            }
        }
    });
}]);

angular.module('plsApp.directives.services').factory('CountryService', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        searchCountries: function (criteria, count) {
            return $http.get(urlConfig.core + "/country/", {params: {searchCriteria: criteria, count: count}}).then(function (response) {
                return _.map(response.data, function (country) {
                    var result = angular.copy(country);
                    var dialingCode = parseInt(country.dialingCode, 0);

                    if (!isNaN(dialingCode)) {
                        result.dialingCode = dialingCode;
                    }

                    return result;
                });
            });
        }
    };
}]);

angular.module('plsApp.directives.services').factory('AddressNameService', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        isAddressUnique: function (addressName, addressCode, params) {
            return $http.get(urlConfig.core + '/customer/' + params.customerId + '/address/unique?name='
                    + encodeURIComponent(addressName) + '&code=' + encodeURIComponent(addressCode));
        }
    };
}]);

angular.module('plsApp.directives.services').factory('DeleteAddressService', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        deleteAddressById: function (params) {
            return $http({method: 'DELETE', url: urlConfig.core + '/customer/' + params.customerId + '/address/' + params.addressId});
        }
    };
}]);

angular.module('plsApp.directives.services').factory('AddressImportService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/address/import_fix_now_doc/:docId', {}, {
        removeImportFixDoc: {
            method: 'DELETE',
            params: {
                customerId: '@customerId',
                docId: '@docId'
            }
        }
    });
}]);