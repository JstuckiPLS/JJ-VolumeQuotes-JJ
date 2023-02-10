angular.module('pricing').factory('LibrariesFuelService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.pricing + '/ltlfuel/:url', {
        url: '@url'
    }, {
        getRegionsRates: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'regionsrates'
            }
        },
        getRegionFuelBySelectedCriteria: {
            method: 'GET',
            isArray: true,
            params: {
                url: 'getRegionFuelBySelectedCriteria'
            }
        },
        save: {
            method: 'POST',
            isArray: true,
            params: {
                action: 'save',
                url: "saveRegionFuel"
            }
        },
        updateRegionsRates: {
            method: 'POST',
            isArray: true,
            params: {
                url: "updateRegionsRates"
            }
        }
    });
}]);