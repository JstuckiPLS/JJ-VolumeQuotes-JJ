angular.module("pricing").factory("FreightAnalysisServices", ["$resource", "urlConfig", function ($resource, urlConfig) {
    return $resource(urlConfig.core + '/shipment/analysis/:pathParam/:id', {}, {
        restartAnalysis: {
            method: "PUT",
            params: {
                pathParam: 'start',
                id: '@id'
            }
        },
        pauseAnalysis: {
            method: "PUT",
            params: {
                pathParam: 'pause',
                id: '@id'
            }
        },
        deleteAnalysis: {
            method: "DELETE",
            params: {
                pathParam: 'delete',
                id: '@id'
            }
        },
        addPricing: {
            method: "POST",
            params: {
                pathParam: 'addPricing'
            }
        },
        getAnalysisJobs: {
            method: "GET",
            params: {
                pathParam: 'getAnalysisJobs'
            },
            isArray: true
        },
        swapAnalysisJobs: {
            method: "PUT",
            params: {
                pathParam: 'swap',
                id: '@id',
                step: '@step'
            }
        },
        checkValidationStatus: {
            method: "GET",
            params: {
                pathParam: 'checkValidationStatus',
                id: '@id'
            }
        },
        startValidation: {
            method: "PUT",
            params: {
                pathParam: 'validateExelFile',
                id: '@id'
            }
        }
    });
}]);
