/**
 * AngularJS directive for Bill To.
 *
 * @author: Dmitry Nikolaenko
 */
angular.module('plsApp.directives').directive('plsBillTo', ['$q', 'ShipmentUtils', 'AddressService', 'LocationsService',
    'UserNotificationsService', 'Identifiers', 'DefaultReqFieldService', 'billToIdentifiers',
    function ($q, ShipmentUtils, AddressService, LocationsService, UserNotificationsService, Identifiers, DefaultReqFieldService, billToIdentifiers) {
        return {
            restrict: 'EA',
            scope: {
                type: '@',
                shipment: '=shipment',
                customer: '=',
                locationForm: '=locationForm',
                billToForm: '=billToForm'
            },
            replace: true,
            templateUrl: 'pages/tpl/bill-to-template.html',
            controller: ['$scope', function ($scope) {
                'use strict';

                $scope.shipmentDirectionValues = ["I", "O"];
                $scope.locations = undefined;
                $scope.billToNames = undefined;
                $scope.paymentTermsValues = angular.copy(ShipmentUtils.getDictionaryValues().paymentTerms);
                var notificationSourceVal = 'LOCATION_DEFAULT_NOTIFICATIONS';

                $scope.isExistSelectedProposition = function () {
                    return _.isUndefined($scope.shipment.selectedProposition);
                };

                function initDefaultValues() {
                    if ($scope.shipment.billTo && $scope.shipment.billTo.id) {
                        if (angular.isUndefined($scope.shipment.shipmentDirection)) {
                            $scope.shipment.shipmentDirection = $scope.shipmentDirectionValues[1];
                        }

                        if (angular.isUndefined($scope.shipment.paymentTerms)) {
                            $scope.shipment.paymentTerms = ($scope.type === 'manualBol')
                                    ? $scope.paymentTermsValues[0] : $scope.paymentTermsValues[1];
                        }
                    }
                }

                initDefaultValues();

                function updateBillTo() {
                    var billTo;

                    if ($scope.shipment.location && $scope.shipment.location.billToId) {
                        billTo = _.findWhere($scope.billToNames, {id: $scope.shipment.location.billToId});
                    }

                    if (!billTo) {
                        billTo = _.findWhere($scope.billToNames, {defaultNode: true});
                    }

                    if (!billTo && $scope.billToNames && $scope.billToNames.length === 1) {
                        billTo = $scope.billToNames[0];
                    }

                    $scope.shipment.billTo = billTo;
                }

                function updateDefaultNotifications() {
                    $scope.authData = $scope.$root.authData;

                    if (!$scope.shipment.finishOrder.jobNumbers) {
                        $scope.shipment.finishOrder.jobNumbers = [];
                    }

                   if (!$scope.shipment.finishOrder.shipmentNotifications) {
                        $scope.shipment.finishOrder.shipmentNotifications = [];
                    }

                    if ($scope.customer.id) {
                        UserNotificationsService.getUserNotifications({
                            customerId: $scope.customer.id,
                            userId: $scope.authData.personId,
                            locationId: $scope.shipment.location.id
                        }, function (users) {
                            if (users) {
                                ShipmentUtils.removeAllNotificationsByType(
                                       $scope.shipment.finishOrder.shipmentNotifications, notificationSourceVal);
                                _.each(users, function (user) {
                                    _.each(user.notifications, function (notification) {
                                        $scope.shipment.finishOrder.shipmentNotifications.push({
                                            emailAddress: user.email,
                                            notificationType: notification,
                                            notificationSource: notificationSourceVal
                                        });
                                    });

                                });
                            }
                        });
                    }
                }

                function updateLocation(data) {
                    if (!$scope.shipment.location && !$scope.shipment.selectedProposition) {
                        return;
                    }

                    _.each(data, function (location) {
                        if (!$scope.shipment.location && location.defaultNode) {
                            $scope.billToNames = undefined;
                            $scope.shipment.location = location;
                        }

                        if (!$scope.shipment.location && data.length === 1) {
                            $scope.shipment.location = location;
                        }
                    });
                }

                function isCurrencyCodeUnchanged() {
                    return $scope.shipment.selectedProposition.carrier
                            && $scope.billToFilter.currency === $scope.shipment.selectedProposition.carrier.currencyCode;
                }

                function initBillTo() {
                    if ($scope.billToNames && $scope.billToFilter.customerId === $scope.customer.id && isCurrencyCodeUnchanged()) {
                        return; // data to fetch bill to is not changed
                    }

                    $scope.billToFilter = {
                        customerId: $scope.customer.id
                    };

                    if (!$scope.billToFilter.customerId) {
                        return;
                    }

                    if ($scope.shipment.selectedProposition && $scope.shipment.selectedProposition.carrier) {
                        $scope.billToFilter.currency = $scope.shipment.selectedProposition.carrier.currencyCode;
                    }

                    var billToPromise = AddressService.listUserBillToAddresses($scope.billToFilter, function (data) {
                        if (data && data.length) {
                            $scope.billToNames = _.sortBy(data, function (billTo) {
                                return billTo.address.addressName;
                            });

                            var allRequiredFields = ShipmentUtils.getDictionaryValues().billToRequiredField;

                            _.forEach($scope.billToNames, function (billTo, index) {
                                if (billTo.billToRequiredFields.length < 11) {
                                    billTo.billToRequiredFields = DefaultReqFieldService.getRequiredFields(allRequiredFields,
                                            billTo.billToRequiredFields);
                                }
                            });
                        }
                    }).$promise;

                    var locationPromise = LocationsService.getAllForCustomer({customerId: $scope.customer.id}, function (data) {
                        if (data && data.length) {
                            $scope.locations = _.sortBy(data, function (location) {
                                return location.name;
                            });

                            updateLocation($scope.locations);
                        } else {
                            $scope.locations = [];
                        }
                    }).$promise;

                    $q.all([billToPromise, locationPromise]).then(function () {
                        if (!$scope.shipment.location) {
                            angular.forEach($scope.locations, function (item) {
                                if (item.defaultNode) {
                                    $scope.shipment.location = item;
                                }
                            });
                        }

                        if (!$scope.shipment.location && $scope.locations.length === 1) {
                            $scope.shipment.location = $scope.locations[0];
                        }

                        function matchesShipmentBillTo(el){return el.id === $scope.shipment.billTo.id;}

                        // update selected bill to, if none selected 
                        // OR if new shipment and selected not available in the filtered down list (e.g. currency mismatch)
                        if (!$scope.shipment.billTo || 
                             (!$scope.shipment.id && 
                               ( !$scope.billToNames || !$scope.billToNames.some(matchesShipmentBillTo) )
                             )
                            ) {
                            updateBillTo();
                        }
                    });
                }

                var srv = {
                    identifiers: Identifiers
                };

                function getDefaultValue(identifier) {
                    return srv.identifiers.getIdentifierRule($scope.shipment, identifier).defaultValue
                            || _.result($scope.shipment, billToIdentifiers[identifier].field);
                }

                $scope.updateDefaultValues = function () {
                    if ($scope.shipment.billTo && $scope.shipment.billTo.billToDefaultValues) {
                        $scope.shipment.shipmentDirection = ($scope.type === 'manualBol') ?
                                $scope.shipment.billTo.billToDefaultValues.manualBolDirection : $scope.shipment.billTo.billToDefaultValues.direction;
                        $scope.shipment.paymentTerms = ($scope.type === 'manualBol')
                                ? $scope.shipment.billTo.billToDefaultValues.manualBolPayTerms : $scope.shipment.billTo.billToDefaultValues.payTerms;
                    }

                    initDefaultValues();
                };

                $scope.locationChange = function () {
                    if ($scope.shipment.location && $scope.shipment.location.id && $scope.billToNames) {
                        updateBillTo();
                    } else {
                        initBillTo();
                    }

                    updateDefaultNotifications();
                };

                $scope.$on('event:initBillTo', function (event) {
                    delete $scope.billToNames;
                    initBillTo();
                });

                $scope.$watch('[shipment.originDetails.address.zip.zip, shipment.destinationDetails.address.zip.zip, shipment.shipmentDirection, ' +
                        'shipment.billTo.address.addressName]', function (newValues, oldValues) {

                    if (_.every(newValues) && (newValues[0] !== oldValues[0] || newValues[1] !== oldValues[1]
                            || newValues[2] !== oldValues[2] || newValues[3] !== oldValues[3])) {
                        $scope.shipment.bolNumber = getDefaultValue('BOL');
                        $scope.shipment.finishOrder.poNumber = getDefaultValue('PO');
                        $scope.shipment.finishOrder.puNumber = getDefaultValue('PU');
                        $scope.shipment.finishOrder.soNumber = getDefaultValue('SO');
                        $scope.shipment.finishOrder.ref = getDefaultValue('SR');
                        $scope.shipment.finishOrder.trailerNumber = getDefaultValue('TR');
                        $scope.shipment.finishOrder.glNumber = getDefaultValue('GL');
                        $scope.shipment.finishOrder.jobNumbers = getDefaultValue('JOB');
                        $scope.shipment.proNumber = getDefaultValue('PRO');
                        $scope.shipment.cargoValue = getDefaultValue('CARGO');
                        $scope.shipment.finishOrder.requestedBy = getDefaultValue('RB');
                    }
                }, true);

                if (($scope.shipment.status === 'OPEN' || ($scope.shipment.selectedProposition && $scope.shipment.selectedProposition.carrier
                        && $scope.shipment.selectedProposition.carrier.currencyCode)) && $scope.customer.id) {
                    initBillTo();
                }
            }]
        };
    }
]);