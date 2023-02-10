angular.module('manualBol.controllers').controller('ManualBolGeneralInformationController', [
    '$scope', '$rootScope', '$q', '$http', 'manualBolModel', 'ShipmentUtils', 'ShipmentsProposalService',
    function ($scope, $rootScope, $q, $http, manualBolModel, ShipmentUtils, ShipmentsProposalService) {
        'use strict';

        $scope.isFormClean = true;
        $scope.maxCountOfProducts = ShipmentUtils.MAX_COUNT_OF_PRODUCTS;
        var emptyShipment = manualBolModel.emptyShipment;

        $scope.showAccountId = true;

        $scope.generalInfoPage = {
            next: '/manual-bol/addresses',
            bolModel: manualBolModel,
            dictionary: ShipmentUtils.getDictionaryValues(),
            maxPickupDate: new Date()
        };

        $scope.$watch('generalInfoPage.bolModel.shipment', function(actualShipment) {
            if (!_.isEqual(emptyShipment, $scope.generalInfoPage.bolModel.removeUncomparableFields(actualShipment))) {
                $scope.isFormClean = false;
            }
        });

        function setUpDefaultCarrier() {
            if ($scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier) {
                var carrier = $scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier;

                $scope.generalInfoPage.bolModel.carrierTuple = {
                    currencyCode: carrier.currencyCode,
                    apiCapable: carrier.apiCapable,
                    id: carrier.id,
                    name: carrier.scac.concat(':', carrier.name)
                };
            } else if (!$scope.generalInfoPage.bolModel.shipment.id) {
                var requestOptions = {
                    method: 'GET',
                    url: '/restful/carrier/default'
                };

                $http(requestOptions).then(function (response) {
                    $scope.generalInfoPage.bolModel.carrierTuple = response.data;
                });
            }
        }

        setUpDefaultCarrier();

        function setUpCustomer() {
            if (!$scope.generalInfoPage.bolModel.shipment ||
                    (!$scope.generalInfoPage.bolModel.shipment.id && !$scope.generalInfoPage.bolModel.shipment.customerName)) {
                return;
            }

            if (_.isUndefined($scope.generalInfoPage.bolModel.selectedCustomer)) {
                $scope.generalInfoPage.bolModel.selectedCustomer = {
                    id: $scope.generalInfoPage.bolModel.shipment.organizationId,
                    name: $scope.generalInfoPage.bolModel.shipment.customerName
                };
            }
        }

        setUpCustomer();

        $scope.$on('event:changeCustomer', function (event, selectedCustomer, oldCustomer) {
            $scope.isFormClean = false;
            if (!selectedCustomer || !selectedCustomer.id || _.isEqual(selectedCustomer, oldCustomer)) {
                return;
            }
            if (selectedCustomer && selectedCustomer.id && oldCustomer && oldCustomer.id && !_.isEqual(oldCustomer, selectedCustomer)) {
                $scope.$broadcast('event:pls-clear-form-data');
            }

            $scope.generalInfoPage.bolModel.shipment.originDetails.address = {};
            $scope.generalInfoPage.bolModel.shipment.destinationDetails.address = {};
            $scope.generalInfoPage.bolModel.addresses = undefined;
            $scope.generalInfoPage.bolModel.shipment.billTo = undefined;
            $scope.generalInfoPage.bolModel.shipment.location = undefined;

            $scope.generalInfoPage.bolModel.shipment.bolNumber = undefined;
            $scope.generalInfoPage.bolModel.shipment.finishOrder.soNumber = undefined;
            $scope.generalInfoPage.bolModel.shipment.finishOrder.poNumber = undefined;
            $scope.generalInfoPage.bolModel.shipment.finishOrder.puNumber = undefined;
            $scope.generalInfoPage.bolModel.shipment.finishOrder.ref = undefined;
            $scope.generalInfoPage.bolModel.shipment.finishOrder.trailerNumber = undefined;
            $scope.generalInfoPage.bolModel.shipment.finishOrder.glNumber = undefined;
            $scope.generalInfoPage.bolModel.shipment.proNumber = undefined;
            $scope.generalInfoPage.bolModel.shipment.cargoValue = undefined;
            $scope.generalInfoPage.bolModel.shipment.finishOrder.jobNumbers = [];
            $scope.generalInfoPage.bolModel.shipment.finishOrder.quoteMaterials = [];
            $scope.generalInfoPage.bolModel.shipment.finishOrder.requestedBy = undefined;
        });

        $scope.$watch('generalInfoPage.bolModel.carrierTuple', function (carrierTuple) {
            if (carrierTuple) {
                if (!$scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier
                        || $scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier.id !== carrierTuple.id) {
                    var tupleSplitterIndex = carrierTuple.name.indexOf(':');

                    if (tupleSplitterIndex !== -1) {
                        $scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier = {
                            id: carrierTuple.id,
                            name: carrierTuple.name.substr(tupleSplitterIndex + 1, carrierTuple.name.length - 1),
                            scac: carrierTuple.name.substr(0, tupleSplitterIndex),
                            currencyCode: carrierTuple.currencyCode,
                            apiCapable: carrierTuple.apiCapable
                        };

                        if ($scope.generalInfoPage.bolModel.shipment.billTo &&
                                $scope.generalInfoPage.bolModel.shipment.billTo.currency !== carrierTuple.currencyCode) {
                            $scope.generalInfoPage.bolModel.shipment.billTo = undefined;
                        }
                    }
                }
            } else {
                $scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier = undefined;
            }
        });

        $scope.navigateTo = function () {
            var deferred = $q.defer();

            $scope.$on('event:pls-added-quote-item', function (event, quoteMaterialSize) {
                if (quoteMaterialSize === 0) {
                    deferred.reject();
                    $scope.$root.$emit('event:application-error', 'Sales order general info step error!',
                            'Product Grid should contain at lease one product.');
                } else {
                    deferred.resolve();
                    $rootScope.ignoreLocationChange();
                    $rootScope.navigateTo($scope.generalInfoPage.next);
                }
            });

            $scope.$broadcast('event:pls-add-quote-item');

            return deferred.promise;
        };

        $scope.openTerminalInfoModalDialog = function () {
            if ($scope.terminalResource && !$scope.terminalResource.$resolved) {
                return;
            }

            var terminalCriteria = {
                scac: $scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier.scac,
                originAddress: {
                    city: $scope.generalInfoPage.bolModel.shipment.originDetails.zip.city,
                    stateCode: $scope.generalInfoPage.bolModel.shipment.originDetails.zip.state,
                    postalCode: $scope.generalInfoPage.bolModel.shipment.originDetails.zip.zip,
                    countryCode: $scope.generalInfoPage.bolModel.shipment.originDetails.zip.country.id
                },
                destinationAddress: {
                    city: $scope.generalInfoPage.bolModel.shipment.destinationDetails.zip.city,
                    stateCode: $scope.generalInfoPage.bolModel.shipment.destinationDetails.zip.state,
                    postalCode: $scope.generalInfoPage.bolModel.shipment.destinationDetails.zip.zip,
                    countryCode: $scope.generalInfoPage.bolModel.shipment.destinationDetails.zip.country.id
                }
            };

            $scope.terminalResource = ShipmentsProposalService.findTerminalInformation({}, terminalCriteria, function (data) {
                if (data && (data.destinationTerminal || data.originTerminal || data.mileageBtwOrigTermDestTerm
                        || data.mileageFromDestTerminal || data.mileageToOrigTerminal)) {
                    data.parentDialog = 'detailsDialogDiv';
                    $scope.$broadcast('event:openTerminalInfoForPreparedCriteria', data);
                } else {
                    $scope.$root.$broadcast('event:application-error', 'No Terminal Information!',
                            'No Terminal Information found for selected Carrier, Origin and Destination.');
                }
            });
        };

        function isValidFreightBillPayTo() {
            return angular.isDefined($scope.generalInfoPage.bolModel.shipment.freightBillPayTo)
                    && angular.isDefined($scope.generalInfoPage.bolModel.shipment.freightBillPayTo.company);
        }

        function isValidCustomer() {
            return angular.isDefined($scope.generalInfoPage.bolModel.selectedCustomer)
                    && angular.isDefined($scope.generalInfoPage.bolModel.selectedCustomer.id);
        }

        $scope.canNextStep = function () {
            return isValidFreightBillPayTo() && isValidCustomer();
        };

        $scope.clearAll = function () {
            $scope.$broadcast('event:cleaning-input');
            manualBolModel.init();
            $scope.$broadcast('event:pls-clear-form-data');
            setUpDefaultCarrier();
            setUpCustomer();
            $scope.isFormClean = true;
        };
    }
]);
