angular.module('pls.controllers').controller('BaseSalesOrderCtrl', ['$scope', 'ShipmentsProposalService', 'ShipmentDocumentEmailService',
    function ($scope, ShipmentsProposalService, ShipmentDocumentEmailService) {
        'use strict';

        var emptyShipment = {
            originDetails: {
                accessorials: []
            },
            destinationDetails: {
                accessorials: []
            },
            finishOrder: {
                quoteMaterials: [],
                pickupDate: $scope.$root.formatDate(new Date()),
                estimatedDelivery: $scope.$root.formatDate(new Date())
            },
            selectedProposition: {
                carrier: undefined,
                costDetailItems: []
            },
            status: 'BOOKED'
        };

        $scope.userCapabilities = ['QUOTES_VIEW'];

        $scope.initialize = function () {
            $scope.wizardData = {
                emptyShipment: emptyShipment,
                shipment: angular.copy(emptyShipment),
                selectedCustomer: {
                    id: undefined,
                    name: undefined
                },
                paymentTermsValues: []
            };

            $scope.wizardData.oldStatus = $scope.wizardData.shipment.status;
            $scope.areOriginAndDestinationFilledIn = false;
        };

        $scope.parentDialog = 'detailsDialogDiv';

        $scope.fullViewDocModel = {
            showFullViewDocumentDialog: false,
            fullViewDocOption: {
                height: '500px',
                pdfLocation: null,
                imageContent: false
            },
            shipmentFullViewDocumentModalOptions: {
                parentDialog: 'detailsDialogDiv'
            }
        };

        $scope.emailOptions = {
            showSendEmailDialog: false,
            docSendMailModalOptions: {
                parentDialog: 'detailsDialogDiv'
            }
        };

        $scope.clearAll = function () {
            $scope.$broadcast('event:cleaning-input');
            $scope.wizardData.shipment = angular.copy($scope.wizardData.emptyShipment);
            $scope.wizardData.carrierTuple = undefined;
            $scope.$broadcast('event:pls-clear-form-data');
        };

        $scope.openTerminalInfoModalDialog = function () {
            if ($scope.terminalResource && !$scope.terminalResource.$resolved) {
                return; // terminal information is being loaded
            }

            var terminalCriteria = {
                scac: $scope.wizardData.shipment.selectedProposition.carrier.scac,
                originAddress: {
                    city: $scope.wizardData.shipment.originDetails.zip.city,
                    stateCode: $scope.wizardData.shipment.originDetails.zip.state,
                    postalCode: $scope.wizardData.shipment.originDetails.zip.zip,
                    countryCode: $scope.wizardData.shipment.originDetails.zip.country.id
                },
                destinationAddress: {
                    city: $scope.wizardData.shipment.destinationDetails.zip.city,
                    stateCode: $scope.wizardData.shipment.destinationDetails.zip.state,
                    postalCode: $scope.wizardData.shipment.destinationDetails.zip.zip,
                    countryCode: $scope.wizardData.shipment.destinationDetails.zip.country.id
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

        $scope.updateStatusForActualDate = function () {
            if (!($scope.wizardData.shipment.status === 'CANCELLED' && $scope.wizardData.shipment.status === 'OPEN')) {
                if (!_.isEmpty($scope.wizardData.shipment.finishOrder.actualPickupDate)
                        && !_.isEmpty($scope.wizardData.shipment.finishOrder.actualDeliveryDate)) {
                    $scope.wizardData.shipment.status = 'DELIVERED';
                }
                if (_.isEmpty($scope.wizardData.shipment.finishOrder.actualDeliveryDate)
                        && !_.isEmpty($scope.wizardData.shipment.finishOrder.actualPickupDate)) {
                    $scope.wizardData.shipment.status = 'IN_TRANSIT';
                }
            }
            if (!_.isEmpty($scope.wizardData.shipment.finishOrder.actualDeliveryDate)) {
                $scope.$broadcast('event:updateFreightBillDate');
            }
        };
    }
]);