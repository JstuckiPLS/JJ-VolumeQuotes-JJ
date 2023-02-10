angular.module('plsApp').factory('ProductService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/product/:productId/:pathDefineParam/:docId', {}, {
        list: {
            method: 'GET',
            params: {
                productId: '',
                pathDefineParam: '',
                docId: ''
            },
            isArray: true
        },
        get: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                productId: '@productId',
                pathDefineParam: '',
                docId: ''
            }
        },
        save: {
            method: 'POST',
            params: {
                customerId: '@customerId',
                productId: '',
                pathDefineParam: '',
                docId: ''
            }
        },
        archive: {
            method: 'PUT',
            params: {
                customerId: '@customerId',
                productId: '@productId',
                pathDefineParam: 'archive',
                docId: ''
            }
        },
        removeFixNowDoc: {
            method: 'DELETE',
            params: {
                customerId: '@customerId',
                productId: '',
                pathDefineParam: 'fixNowDoc',
                docId: '@docId'
            }
        },
        info: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                productId: '@productId',
                pathDefineParam: '',
                docId: ''
            }
        },
        isUnique: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                productId: '@productId',
                pathDefineParam: 'isUnique',
                docId: '',
                description: '@description',
                commodityClass: '@commodityClass',
                shared: '@shared'
            },
            transformResponse: function (data) {
                return {
                    result: data
                };
            }
        }
    });
}]);

angular.module('plsApp').factory('ProductFilterService', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        filter: function (selectedCustomer, searchString, commodityClass, hazmatOnly) {
            return $http.get(urlConfig.shipment + "/customer/" + selectedCustomer + "/product/filter", {
                params: {
                    search: searchString,
                    commodityClass: commodityClass,
                    hazmat: hazmatOnly
                }
            }).then(function (response) {
                return response.data;
            });
        }
    };
}]);

angular.module('plsApp').factory('ProductTotalsService', [
    function () {
        return {
        calculateTotals: function (itemsList) {
            var totals = {};
            totals.weight = _.reduce(itemsList, function(memo, num) {
                return num.weight ? memo + parseFloat(num.weight) : memo;
            }, 0);
            totals.quantity = _.reduce(itemsList, function(memo, num) {
                return num.quantity ? memo + parseInt(num.quantity, 10) : memo;
            }, 0);
            totals.pieces = _.reduce(itemsList, function(memo, num) {
                return num.pieces ? memo + parseInt(num.pieces, 10) : memo;
            }, 0);
            totals.cubes = _.reduce(itemsList, function(memo, num) {
                var cube = ((parseInt(num.length, 10) * parseInt(num.width, 10) * parseInt(num.height, 10) * parseInt(num.quantity, 10)) / 1728);
                num.cube = _.isFinite(cube) ? cube : 0;
                num.density = num.cube === 0 ? 0 : (num.weight / num.cube);
                return memo + num.cube;
            }, 0);
            totals.density = 0;
            if (_.isEmpty(_.findWhere(itemsList, {density: 0}))) {
                totals.density = totals.cubes ? (totals.cubes === 0 ? 0 : (totals.weight / totals.cubes)) : 0;
            }
            totals.cubes = _.isEmpty(_.findWhere(itemsList, {cube: 0})) ? totals.cubes : 0;
            return totals;
        }
    };
}]);
