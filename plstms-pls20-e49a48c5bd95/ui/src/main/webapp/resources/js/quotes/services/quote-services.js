angular.module('plsApp').factory('SavedQuotesService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/saved/:propositionId/:subPath', {}, {
        save: {
            method: 'POST',
            params: {
                propositionId: '',
                subPath: ''
            }
        },
        list: {
            method: 'GET',
            isArray: true,
            params: {
                propositionId: '',
                subPath: ''
            }
        },
        get: {
            method: 'GET',
            params: {
                propositionId: '@propositionId',
                subPath: ''
            }
        },
        details: {
            method: 'GET',
            params: {
                propositionId: '@propositionId',
                subPath: 'details'
            }
        },
        remove: {
            method: 'DELETE',
            params: {
                propositionId: '@propositionId',
                subPath: ''
            }
        },
        getListOfLoadIds: {
            method: 'GET',
            params: {
                propositionId: '@propositionId',
                subPath: 'getListOfLoadIds'
            },
            transformResponse: function (data) {
                return {
                    result: angular.fromJson(data)
                };
            }
        }
    });
}]);

angular.module('plsApp').factory('SavedQuoteEmailService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/quote/email', {}, {
        emailQuote: {
            method: 'POST',
            params: {}
        },
        getTemplate: {
            method: 'GET',
            transformResponse: function (data) {
                return {
                    result: data
                };
            },
            params: {
                quoteId: '@quoteId'
            }
        }
    });
}]);

angular.module('plsApp').factory('QuotePermissionsService', ['$rootScope', function ($rootScope) {
    var self = {};

    self = {
        showCarrierCost: function () {
            return $rootScope.isPlsPermissions('EDIT_PLS_REVENUE || EDIT_CARRIER_COST || VIEW_PLS_REVENUE_AND_CARRIER_COST');
        },
        showCostDetails: function () {
            return self.showCarrierCost() || $rootScope.isFieldRequired('VIEW_PLS_CUSTOMER_COST_DETAILS');
        },
        showCustomerRevenue: function () {
            return !self.showCarrierCost() && $rootScope.isFieldRequired('VIEW_PLS_CUSTOMER_COST_DETAILS');
        }
    };

    return self;
}]);