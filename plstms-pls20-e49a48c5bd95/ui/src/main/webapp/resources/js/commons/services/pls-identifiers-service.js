/**
 * Services dynamic Identifiers functionality
 * @author Vitaliy Gavriliuk
 */
angular.module('plsApp.common.services').factory('Identifiers', ['_', 'DefaultReqFieldService', function (_, DefaultReqFieldService) {
    'use strict';

    function inDirection(value, direction) {
        var zips = angular.isDefined(value.address.zip) ? value.address.zip.split(', ') : [];
        var cities = angular.isDefined(value.address.city) ? value.address.city.split(', ') : [];
        var states = angular.isDefined(value.address.state) ? value.address.state.split(', ') : [];
        var countries = angular.isDefined(value.address.country) ? value.address.country.split(', ') : [];

        var isInZips = _.contains(zips, direction.address.zip.zip);
        var isInCities = _.contains(cities, direction.address.zip.city);
        var isInStates = _.contains(states, direction.address.zip.state);
        var isInCountries = direction.address.zip.country ? _.contains(countries, direction.address.zip.country.id) : false;

        if (isInZips) {
            return 'zip';
        } else if (isInCities) {
            return 'city';
        } else if (isInStates) {
            return 'state';
        } else if (isInCountries) {
            return 'country';
        }

        return false;
    }
    function inOriginDestination(value, originDetails, destinationDetails) {
        if (_.isEmpty(value.address)) {
            return 'ALL';
        } else {
            switch (value.originDestination) {
                case 'B':
                    return inDirection(value, originDetails) || inDirection(value, destinationDetails);
                case 'O':
                    return inDirection(value, originDetails);
                case 'D':
                    return inDirection(value, destinationDetails);
            }
        }
    }

    return {
        isEmptyDefaultValue: function (value, plsIdentifier) {
            return plsIdentifier === 'JOB' ? _.isEmpty(value.defaultValue) : angular.isUndefined(value.defaultValue);
        },

        getIdentifierRule: function(shipment, identifier) {
            var result;
            var resultLocationLevel;
            angular.forEach(shipment.billTo.billToRequiredFields, function (value) {
                if (identifier === value.name && (value.inboundOutbound === 'B' || value.inboundOutbound === shipment.shipmentDirection)) {
                    var locationLevel = inOriginDestination(value, shipment.originDetails, shipment.destinationDetails);
                    if (locationLevel) {
                        if (angular.isUndefined(result)
                                || locationLevel === 'ALL'
                                || locationLevel === 'zip'
                                || (locationLevel === 'city' && resultLocationLevel !== 'zip')
                                || (locationLevel === 'state' && !_.contains(['zip', 'city'], resultLocationLevel))
                                || (locationLevel === 'country' && !_.contains(['zip', 'city', 'state'], resultLocationLevel))) {
                            result = value;
                            resultLocationLevel = locationLevel;
                        }
                    }
                }
            });
            return result || DefaultReqFieldService.getDefaultField(identifier);
        }
    };
}]);