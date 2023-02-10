angular.module('pricing').factory('LtlFuelSurchargeCopyFromService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlfuelsurcharge/copyFrom',
            {action: '@action'}, {
                copyFrom: {
                    method: 'POST'
                }
            });
}]);

angular.module('pricing').factory('GetLtlFuelSurchargeService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlfuelsurcharge/activeFuelSurchargeForProfile/:profileDetailId', {}, {
        getActive: {
            method: 'GET',
            isArray: true
        }
    });
}]);

angular.module('pricing').factory('LtlFuelSurchargeSaveService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlfuelsurcharge/:action', {}, {
        save: {
            method: 'POST',
            params: {
                action: 'save'
            }
        },
        saveAll: {
            method: 'POST',
            params: {
                action: 'saveAll'
            },
            isArray: true
        },
        inactivate: {
            method: 'POST',
            params: {
                action: 'inactivate'
            },
            isArray: true
        }
    });
}]);

angular.module('pricing').factory('LtlFuelSurchargeUpdateStatusService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlfuelsurcharge/:profileDetailId/inactivate', {}, {
        inactivate: {
            method: 'POST',
            params: {
                profileDetailId: '@profileDetailId'
            },
            isArray: true
        }
    });
}]);

angular.module('pricing').factory('LtlFuelSurchargeChargeCountService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlfuelsurcharge/fuelSurchargeByFuelCharge/:charge',
            {}, {
                getCharge: {
                    method: 'GET'
                }
            }
    );
}]);

angular.module('pricing').factory('GetSelectedFuel', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlfuel/get/:id',
            {
                id: '@id'
            }, {
                get: {
                    method: 'GET'
                }
            }
    );
}]);

angular.module('pricing').factory('GetLtlFuelAndTriggersListService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlfuel/:action/:ltlPricingProfileId', {
        action: '@action',
        ltlPricingProfileId: '@ltlPricingProfileId'
    }, {
        active: {
            method: 'GET',
            isArray: true,
            params: {
                action: 'active'
            }
        },
        inactive: {
            method: 'GET',
            isArray: true,
            params: {
                action: 'inactive'
            }
        },
        expired: {
            method: 'GET',
            isArray: true,
            params: {
                action: 'expired'
            }
        }
    });
}]);

angular.module('pricing').factory('ChangeLtlFuelAndTriggersListService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlfuel/:action', {action: '@action'}, {
        save: {
            method: 'POST',
            params: {
                action: 'save'
            }
        },
        inactivate: {
            method: 'PUT',
            isArray: true,
            params: {
                action: 'inactivate'
            }
        },
        reactivate: {
            method: 'PUT',
            isArray: true,
            params: {
                action: 'reactivate'
            }
        },
        expire: {
            method: 'POST',
            isArray: true,
            params: {
                action: 'expire'
            }
        },
        copyFrom: {
            method: 'POST',
            params: {
                action: 'copyFrom'
            }
        }
    });
}]);

angular.module('pricing').factory('GetDOTRegionsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlfuel/getdotregions', {}, {
        get: {
            method: 'GET',
            isArray: true
        }
    });
}]);
