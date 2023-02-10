/**
 * Angular JS Directive, to display google maps by the address passed.
 * Displays both origin and destination addresses, if present.
 * 
 * @author: Dmitriy Davydenko
 */
angular.module('plsApp').directive('googleMaps', ['$timeout', function($timeout) {
    return {
        restrict: 'A',
        scope: {
            origin: '=',
            destination: '='
        },
        templateUrl: 'pages/tpl/google-maps-tpl.html',
        controller: ['$scope', function ($scope) {
            'use strict';

            $scope.$on('event:showGoogleMaps', function(event, options) {
                $scope.isOrigin = options.isOrigin;
                $scope.fullMapDialog.options = {
                    parentDialog: options.parentDialog
                };
                $scope.fullMapDialog.open();
            });

            function addressToGeocodeString(address) {
                var addrStrings = [];
                addrStrings.push(address.address1);
                if (address.zip) {
                    addrStrings.push(address.zip.city);
                    addrStrings.push(address.zip.state);
                    addrStrings.push(address.zip.zip);
                }
                return addrStrings.join(', ');
            }
            $scope.bigMapOptions = {
                chosenGeoResult: undefined,
                chosenMarker: undefined,
                confirmedMarker: undefined,
                map: undefined,
                infoWindow: undefined,
                mapMarkers: undefined,
                mapIsReady: false,
                zoom: 15,
                noClear: true,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };

            $scope.bigMapMarkerClicked = function (marker) {
                $scope.bigMapOptions.chosenMarker = marker;
                $scope.bigMapOptions.infoWindow.open($scope.bigMapOptions.map, marker);
            };

            var createMarkersForBigMap = function () {
                $scope.bigMapOptions.mapMarkers = [];
                angular.forEach($scope.geocodeResults, function (geocodeResult, index) {
                    var marker = new google.maps.Marker({
                        map: $scope.bigMapOptions.map,
                        position: geocodeResult.geometry.location,
                        draggable: true,
                        title: geocodeResult.formatted_address,
                        icon: $scope.getMarkerImageUrl(index, geocodeResult.formatted_address)
                    });
                    marker.origGeoResult = geocodeResult;
                    $scope.bigMapOptions.mapMarkers.push(marker);
                });
            };

            $scope.doMapSearch = function () {
                new google.maps.Geocoder().geocode({
                    address: $scope.fullMapDialog.mapSearchString,
                    region: 'US'
                }, function (geocodeResults) {
                    $scope.bigMapOptions.mapMarkers = undefined;
                    $scope.geocodeResults = $scope.geocodeResultsBackup.concat(geocodeResults);
                    createMarkersForBigMap();
                    $scope.bigMapOptions.map.panTo($scope.bigMapOptions.mapMarkers[0].position);
                    $scope.$digest();
                });
            }; 

            $scope.onBigMapIdle = function () {
                if ($scope.bigMapOptions.mapMarkers === undefined) {
                    createMarkersForBigMap();
                    $scope.bigMapOptions.map.panTo($scope.bigMapOptions.mapMarkers[0].position);
                }
            };

            $scope.chooseGeocodeResult = function (geocodeResult) {
                if ($scope.bigMapOptions.map) {
                    $scope.bigMapOptions.map.setCenter(geocodeResult.geometry.location);
                }
            };

            $scope.getMarkerImageUrl = function (index, title) {
                var markerState = 'active';
                if (title && $scope.bigMapOptions.confirmedMarker && title !== $scope.bigMapOptions.confirmedMarker.title) {
                    markerState = 'inactive';
                }
                return 'resources/img/map_markers/' + markerState + '-marker' + String.fromCharCode(97 + index).toUpperCase() + '.png';
            };

            $scope.$on('event:dialogIsOpen', function () {
                $scope.bigMapOptions.mapIsReady = true;
            });

            function getArrayOfAddresses() {
                var addrArray = [];
                if ($scope.origin.address1) {
                    addrArray.push(addressToGeocodeString($scope.origin));
                }
                if ($scope.destination.address1) {
                    addrArray.push(addressToGeocodeString($scope.destination));
                }
                if (addrArray.length === 2 && !$scope.isOrigin) {
                    var temp = addrArray.pop();
                    addrArray.unshift(temp);
                }
                return addrArray;
            }

            $scope.fullMapDialog = {
                show: false,
                close: function () {
                    $scope.fullMapDialog.show = false;
                    $scope.bigMapOptions.map = undefined;
                    $scope.bigMapOptions.mapIsReady = false;
                    $scope.bigMapOptions.mapMarkers = undefined;
                    $scope.bigMapOptions.chosenGeoResult = undefined;
                    $scope.bigMapOptions.chosenMarker = undefined;
                    $scope.bigMapOptions.confirmedMarker = undefined;
                    $scope.fullMapDialog.mapSearchString = '';
                    $scope.geocodeResultsBackup = [];
                },
                open: function () {
                    $scope.fullMapDialog.show = true;
                    var addresses = getArrayOfAddresses();
                    $scope.geocodeResults = [];
                    $scope.geocodeResultsBackup = [];

                    new google.maps.Geocoder().geocode({
                        address: addresses[0],
                        componentRestrictions: {country: $scope.origin.zip.country.name}
                    }, function (geocodeResult) {
                        $scope.geocodeResults.push(geocodeResult[0]);
                        $scope.geocodeResultsBackup.push(geocodeResult[0]);
                        if(addresses[1]) {
                            new google.maps.Geocoder().geocode({
                                address: addresses[1],
                                componentRestrictions: {country: $scope.origin.zip.country.name}
                            }, function (geocodeResult) {
                                $scope.geocodeResults.push(geocodeResult[0]);
                                $scope.geocodeResultsBackup.push(geocodeResult[0]);
                            });
                        }
                        $scope.bigMapOptions.chosenGeoResult = $scope.geocodeResults[0];
                        $scope.bigMapOptions.mapIsReady = true;
                        $scope.$digest();
                    });
                }
            };
        }]
    };
}]);
