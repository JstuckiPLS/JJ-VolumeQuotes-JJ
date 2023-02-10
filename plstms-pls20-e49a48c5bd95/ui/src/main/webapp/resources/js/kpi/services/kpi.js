angular.module('plsApp').factory('kpiResource', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/kpireport/:url", {}, {
        getDestinationReport: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getDestinationReport'
            }
        },
        getDailyLoadActivityReport: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getDailyLoadActivityReport'
            }
        },
        getFilterValues: {
            method: 'GET',
            params: {
                url: 'getFilterValues'
            }
        },
        getFreightSpendReports: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getFreightSpendReports'
            }
        },
        getShipmentOverviewReport: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getShipmentOverviewReport'
            }
        },
        getCarrierSummaryReport: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getCarrierSummaryReport'
            }
        },
        getWeightAnalysisReports: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getWeightAnalysisReports'
            }
        },
        getGeographicSummaryReports: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getGeographicSummaryReports'
            }
        },
        getClassSummaryReport: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getClassSummaryReport'
            }
        },
        getCustomerSummaryReport: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getCustomerSummaryReport'
            }
        },
        getVendorSummaryReport: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getVendorSummaryReport'
            }
        }, getSeasonalityReport: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getSeasonalityReport'
            }
        }
    });
}]);