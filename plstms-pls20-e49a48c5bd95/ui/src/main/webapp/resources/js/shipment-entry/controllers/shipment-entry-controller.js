angular.module('shipmentEntry').controller('ShipmentEntryController', ['$scope', '$timeout', '$window', '$route', '$location', 'DateTimeUtils',
    'ShipmentOperationsService', 'ShipmentsProposalService', 'CostDetailsUtils', 'urlConfig', 'ShipmentSavingService', 'UserDetailsService',
    'ShipmentDocumentService', 'SelectCarrFormValService', 'ShipmentUtils', 'ShipmentDocumentEmailService', 'isRequiredField',
    function ($scope, $timeout, $window, $route, $location, DateTimeUtils, ShipmentOperationsService, ShipmentsProposalService, CostDetailsUtils,
              urlConfig, ShipmentSavingService, UserDetailsService, ShipmentDocumentService, SelectCarrFormValService, ShipmentUtils,
              ShipmentDocumentEmailService, isRequiredField) {
        'use strict';

        $scope.shipmentEntryData = {};
        $scope.invalidIdentifier = {};

        $scope.init = function () {
            $scope.shipmentEntryData.selectedCustomer = {
                id: $scope.$root.authData.assignedOrganization ? $scope.$root.authData.assignedOrganization.orgId : undefined,
                name: $scope.$root.authData.assignedOrganization ? $scope.$root.authData.assignedOrganization.name : undefined
            };

            $scope.shipmentEntryData.emptyShipment = {
                bolNumber: undefined,
                cargoValue: undefined,
                originDetails: {
                    accessorials: [],
                    address: {
                        zip: {
                            country: {}
                        }
                    }
                },
                destinationDetails: {
                    accessorials: [],
                    address: {
                        zip: {
                            country: {}
                        }
                    }
                },
                finishOrder: {
                    jobNumbers: [],
                    quoteMaterials: [],
                    pickupDate: $scope.$root.formatDate(new Date()),
                    pickupWindowFrom: undefined,
                    pickupWindowTo: undefined,
                    pickupNotes: '',
                    deliveryWindowFrom: undefined,
                    deliveryWindowTo: undefined,
                    deliveryNotes: '',
                    shippingLabelNotes: '',
                    shipmentNotifications: [],
                    glNumber: undefined,
                    poNumber: undefined,
                    puNumber: undefined,
                    ref: undefined,
                    requestedBy: undefined,
                    soNumber: undefined,
                    trailerNumber: undefined
                },
                guaranteedBy: undefined,
                margins: {
                    revenueOverrideOption: 'marginPerc',
                    revenueOverride: undefined,
                    costOverride: undefined
                },
                proNumber: undefined,
                requestLTLRates: true
            };

            $scope.shipmentEntryData.shipment = angular.copy($scope.shipmentEntryData.emptyShipment);
            $scope.storedBolId = null;
            $scope.showSendMailDialog = false;
            $scope.isRequoteDisabled = true;
            $scope.emailOptions = {};
            $scope.shipmentEntryData.editedEmail = '';
            $scope.shipmentEntryData.originForm = {};
            $scope.shipmentEntryData.destinationForm = {};
        };

        $scope.shipmentEntryData.showCustomsBroker = false;

        $scope.previewBOLOptions = {
            width: '100%',
            height: '380px'
        };

        $scope.termsAgreementOptions = {
            width: '100%',
            height: '380px',
            pdfLocation: urlConfig.shipment + '/customer/shipmentdocs/termOfAgreement'
        };

        $scope.updateStatus = function () {
            $scope.shipmentEntryData.shipment.status = $scope.isFieldRequired('ALLOW_SHIPMENT_AUTO_DISPATCH') ? 'DISPATCHED' : 'BOOKED';
        };

        function setupCustomsBrokerFlag() {
            if ($scope.shipmentEntryData.shipment.originDetails && $scope.shipmentEntryData.shipment.destinationDetails
                    && $scope.shipmentEntryData.shipment.originDetails.address
                    && $scope.shipmentEntryData.shipment.destinationDetails.address
                    && $scope.shipmentEntryData.shipment.destinationDetails.address.country
                    && $scope.shipmentEntryData.shipment.originDetails.address.country) {
                $scope.shipmentEntryData.showCustomsBroker =
                        ($scope.shipmentEntryData.shipment.originDetails.address.country.id
                        !== $scope.shipmentEntryData.shipment.destinationDetails.address.country.id);
            }
        }

        function isLoadHasOpenStatusWithoutCarrier() {
            return $scope.shipmentEntryData.shipment.status === 'OPEN'
                    && (!$scope.shipmentEntryData.shipment.selectedProposition.carrier
                    || !$scope.shipmentEntryData.shipment.selectedProposition.costDetailItems
                    || !$scope.shipmentEntryData.shipment.selectedProposition.costDetailItems.length);
        }

        $scope.handleShipment = function (shipment, copy) {
            $scope.shipmentEntryData.shipment = shipment;

            $scope.shipmentEntryData.selectedCustomer = {
                id: shipment.organizationId,
                name: shipment.customerName
            };

            $scope.shipmentEntryData.shipment.originDetails.address.pickupWindowFrom = shipment.finishOrder.pickupWindowFrom;
            $scope.shipmentEntryData.shipment.originDetails.address.pickupWindowTo = shipment.finishOrder.pickupWindowTo;
            $scope.shipmentEntryData.shipment.destinationDetails.address.deliveryWindowFrom = shipment.finishOrder.deliveryWindowFrom;
            $scope.shipmentEntryData.shipment.destinationDetails.address.deliveryWindowTo = shipment.finishOrder.deliveryWindowTo;
            $scope.shipmentEntryData.shipment.selectedProposition = shipment.selectedProposition;

            $timeout(function () {
                $scope.$broadcast('event:set-proposition', shipment.selectedProposition);

                if (copy) {
                    $scope.$broadcast('event:editShipment', $scope.shipmentEntryData.selectedCustomer, true); // TODO remove this broadcast
                } else {
                    $scope.$broadcast('event:editShipment', $scope.shipmentEntryData.selectedCustomer); // TODO remove this broadcast
                }
            }, 100);

            $scope.shipmentBackup = angular.copy($scope.shipmentEntryData.shipment);
            $scope.shipmentBackup.totalShipperAmt = 0;

            // @TODO create or use existing service
            _.each($scope.shipmentBackup.selectedProposition.costDetailItems, function (item) {
                if (item.costDetailOwner === 'S') {
                    $scope.shipmentBackup.totalShipperAmt += item.subTotal;
                }
            });

            setupCustomsBrokerFlag();
        };

        if ($route.current.params.loadId) {
            $scope.loadId = $route.current.params.loadId;

            if ($route.current.params.customerId) {
                ShipmentOperationsService.getCopiedShipment({
                    customerId: $route.current.params.customerId,
                    shipmentId: $scope.loadId
                }, function (data) {
                    var copy = true;
                    $scope.init();
                    $scope.handleShipment(data, copy);
                    $scope.updateStatus();
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Shipment copy failed!', 'Can\'t copy shipment with ID ' + $scope.loadId);
                });

                $scope.confirmedTermsOfAgreement = true;
            } else {
                ShipmentOperationsService.getShipment({
                    customerId: $scope.$root.authData.organization.orgId,
                    shipmentId: $scope.loadId
                }, function (data) {
                    $scope.init();
                    $scope.handleShipment(data);
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Shipment load failed!', 'Can\'t load shipment with ID ' + $scope.loadId);
                });
            }
        } else {
            $scope.init();
            $scope.updateStatus();
            $scope.shipmentEntryData.emptyShipment.status = $scope.shipmentEntryData.shipment.status;
            $timeout(function () {
                $scope.$broadcast('event:changeCustomer', $scope.shipmentEntryData.selectedCustomer);
            }, 100);
        }

        $scope.openTermsOfAgreementDialog = function () {
            $scope.showTermsOfAgreement = true;
        };

        $scope.closeTermsOfAgreementDialog = function () {
            $scope.showTermsOfAgreement = false;
        };

        $scope.confirmTermsOfAgreement = function () {
            $scope.confirmedTermsOfAgreement = true;
            $scope.closeTermsOfAgreementDialog();
        };

        $scope.showPickupMessageIfNeeded = function (date, time) {
            var MILLISECONDS_IN_A_HOUR = 1000 * 60 * 60;
            var MAX_PICKUP_TIME_DIFFERENCE = 2;

            var pickUpTime = DateTimeUtils.composeDateTime(date, time);
            var difference = new Date() - pickUpTime;

            if (difference <= MAX_PICKUP_TIME_DIFFERENCE * MILLISECONDS_IN_A_HOUR && difference > 0) {
                $scope.$root.$emit('event:application-warning', 'Warning!',
                        'Please contact Carrier or PLS to ensure that the carrier will be able to pickup the Shipment');
            }
        };

        $scope.prepareShipmentForSend = function (origShipment, skipQuoteId, organizationId, proposals) {
            var shipmentToBeSent = angular.copy(origShipment);

            if (shipmentToBeSent.selectedProposition && shipmentToBeSent.selectedProposition.id && !skipQuoteId) {
                shipmentToBeSent.quoteId = shipmentToBeSent.selectedProposition.id;
                delete shipmentToBeSent.selectedProposition.id;
            }

            if (shipmentToBeSent.finishOrder.quoteMaterials) {
                angular.forEach(shipmentToBeSent.finishOrder.quoteMaterials, function (item) {
                    if (item.product) {
                        delete item.product;
                    }
                });
            }

            shipmentToBeSent.proposals = angular.copy(proposals);
            shipmentToBeSent.organizationId = organizationId;

            return shipmentToBeSent;
        };

        $scope.$watch('shipmentEntryData.shipment.originDetails.address', function (newAddress, oldAddress) {
            if (newAddress && !_.isEqual(newAddress, oldAddress)) {
                $scope.shipmentEntryData.shipment.finishOrder.pickupWindowFrom = newAddress.pickupWindowFrom;
                $scope.shipmentEntryData.shipment.finishOrder.pickupWindowTo = newAddress.pickupWindowTo;

                setupCustomsBrokerFlag();
            }
        }, true);

        $scope.$watch('shipmentEntryData.shipment.destinationDetails.address', function (newAddress, oldAddress) {
            if (newAddress && !_.isEqual(newAddress, oldAddress)) {
                $scope.shipmentEntryData.shipment.finishOrder.deliveryWindowFrom = newAddress.deliveryWindowFrom;
                $scope.shipmentEntryData.shipment.finishOrder.deliveryWindowTo = newAddress.deliveryWindowTo;

                setupCustomsBrokerFlag();
            }
        }, true);

        $scope.prepareBOL = function () {
            var shipmentToSend = $scope.prepareShipmentForSend($scope.shipmentEntryData.shipment, false,
                    $scope.shipmentEntryData.selectedCustomer.id, $scope.shipmentEntryData.proposals);

            $scope.prepareBolPromise = ShipmentDocumentService.prepareBolForShipment({
                customerId: $scope.shipmentEntryData.selectedCustomer.id,
                rateQuote: true
            }, shipmentToSend, function (data) {
                if (data && data.value) {
                    $scope.storedBolId = data.value;
                }
            });

        };

        function navigateAfterClosing(){
            $scope.$root.ignoreLocationChange();
            if($route.current.params.from) {
                $location.url($route.current.params.from);
            } else {
                $location.url('/shipment-entry');
            }
        }

        function showShipmentDetails(shipment) {
            $scope.shipmentDetailsOption = {
                shipmentId: shipment.id,
                customerId: $scope.shipmentEntryData.selectedCustomer.id,
                bol: shipment.bolNumber,
                closeHandler: function () {
                    if ($route.current.params.loadId) {
                        navigateAfterClosing();
                    } else {
                        $scope.clearAll();
                    }
                },
                hideRateQuoteButton: $scope.authData.plsUser
            };

            $scope.$broadcast('event:showShipmentDetails', $scope.shipmentDetailsOption);
        }

        function showPaymentDialog(shipment) {
            if (shipment.billTo.paymentMethod === 'PREPAID_ONLY') {
                var data = {
                    customerId : shipment.organizationId,
                    locationId : shipment.location.id,
                    creditCardEmail : shipment.billTo.creditCardEmail,
                    loadId : shipment.id,
                    bolNumber : shipment.bolNumber
                };
                $scope.$root.$broadcast('event:showPaymentDialog', {
                    data : data,
                    closeHandler : function() {
                        showShipmentDetails(shipment);
                    }
                });
            } else {
                showShipmentDetails(shipment);
            }
        }

        function shipmetBookSuccess(shipment) {
            if ($scope.shipmentEntryData.shipment.id) {
                if (isLoadHasOpenStatusWithoutCarrier()) {
                    $scope.showSuccessWarning = true;
                    return;
                }

                $scope.showTerminalInformation($scope.shipmentEntryData.shipment.selectedProposition, true);
                return;
            }

            $(this).scrollTop(0);
            $scope.$root.$emit('event:operation-success', 'Save shipment', 'Shipment was successfully saved.<br/>Load ID: ' + shipment.id);

            showPaymentDialog(shipment);

        }

        function sendEmailWithErrorDetails() {
            ShipmentDocumentEmailService.emailDoc({
                recipients: 'aleshchenko@plslogistics.com',
                subject: 'Wrong Bill To',
                content: 'Shipment Entry: ' + JSON.stringify($scope.shipmentEntryData),
                loadId: -1
            });
        }

        $scope.bookIt = function () {
            if (!$scope.confirmedTermsOfAgreement && !$scope.shipmentEntryData.shipment.id) {
                $scope.openTermsOfAgreementDialog();
                return;
            }

            if ($scope.shipmentEntryData.shipment.status === 'OPEN'
                    && $scope.shipmentEntryData.shipment.selectedProposition.carrier
                    && $scope.shipmentEntryData.shipment.selectedProposition.costDetailItems
                    && $scope.shipmentEntryData.shipment.selectedProposition.costDetailItems.length) {
                $scope.updateStatus();
            }

            if (!ShipmentUtils.isCreditLimitValid($scope.shipmentEntryData.shipment, $scope.shipmentBackup)) {
                return;
            }

            var finishOrder = $scope.shipmentEntryData.shipment;

            if (finishOrder.pickupDate && finishOrder.pickupWindowFrom) {
                $scope.showPickupMessageIfNeeded(finishOrder.pickupDate, finishOrder.pickupWindowFrom);
            }

            var shipmentToSend = $scope.prepareShipmentForSend($scope.shipmentEntryData.shipment, true,
                    $scope.shipmentEntryData.selectedCustomer.id, $scope.shipmentEntryData.proposals);

            if (shipmentToSend.billTo.paymentMethod === "PREPAID_ONLY") {
                shipmentToSend.status = 'PENDING_PAYMENT';
                var prePayedSum = _.reduce(shipmentToSend.prepaidDetails, function(memo, num){
                    return memo + num.amount;
                }, 0);

                if (shipmentToSend.selectedProposition.totalShipperAmt <= prePayedSum) {
                    shipmentToSend.status = "DISPATCHED";
                }
            }

            ShipmentSavingService.bookShipmentEntry({
                customerId: $scope.shipmentEntryData.selectedCustomer.id,
                storedBolId: $scope.storedBolId
            }, shipmentToSend, shipmetBookSuccess, function (data) {
                var message = data.data ? data.data.message : data;

                if (String(data.status) === "412" && data.data && data.data.payload) {
                    message = "Validation errors: ";

                    if (data.data.payload.origin_address === 'VERSION') {
                        message += "origin address was changed during quote booking!";
                    } else if (data.data.payload.destination_address === 'VERSION') {
                        message += "destination address was changed during quote booking!";
                    } else if (data.data.payload.billToOrganization) {
                        message = "Unexpected error occurred. Please refresh page and try again.";
                        sendEmailWithErrorDetails();
                    } else {
                        message = data.data.message;
                    }
                }

                $scope.$root.$emit('event:application-error', 'Quote wizard failed!', 'Can\'t book shipment. ' + message);
            });
        };

        function prepareShipmentForRating(shipment, orgId, CostDetailsUtils) {
            if (!shipment.guid) {
                shipment.guid = CostDetailsUtils.guid();
            }

            var quoteMaterials = _.map(shipment.finishOrder.quoteMaterials, function (material) {
                return {
                    weight: material.weight,
                    weightUnit: material.weightUnit,
                    height: material.height,
                    width: material.width,
                    length: material.length,
                    dimensionUnit: material.dimensionUnit,
                    quantity: material.quantity,
                    packageType: material.packageType,
                    commodityClassEnum: material.commodityClass,
                    hazmatBool: material.hazmat,
                    pieces: material.pieces,
                    nmfc: material.nmfc
                };
            });

            var quoteAccessorials = [];

            if (shipment.originDetails.accessorials) {
                quoteAccessorials.push(shipment.originDetails.accessorials);
            }

            if (shipment.destinationDetails.accessorials) {
                quoteAccessorials.push(shipment.destinationDetails.accessorials);
            }

            quoteAccessorials = _.flatten(quoteAccessorials);
            quoteAccessorials = _.filter(quoteAccessorials, function (accessorial) {
                return accessorial !== 'P_BUS' && accessorial !== 'D_BUS';
            });

            return {
                shipperOrgId: orgId,
                shipDate: shipment.finishOrder.pickupDate,
                originAddress: {
                    city: $scope.shipmentEntryData.shipment.originDetails.address.zip.city,
                    stateCode: $scope.shipmentEntryData.shipment.originDetails.address.zip.state,
                    postalCode: $scope.shipmentEntryData.shipment.originDetails.address.zip.zip,
                    countryCode: $scope.shipmentEntryData.shipment.originDetails.address.zip.country.id
                },
                destinationAddress: {
                    city: $scope.shipmentEntryData.shipment.destinationDetails.address.zip.city,
                    stateCode: $scope.shipmentEntryData.shipment.destinationDetails.address.zip.state,
                    postalCode: $scope.shipmentEntryData.shipment.destinationDetails.address.zip.zip,
                    countryCode: $scope.shipmentEntryData.shipment.destinationDetails.address.zip.country.id
                },
                materials: quoteMaterials,
                accessorialTypes: quoteAccessorials,
                totalWeight: _.reduce(shipment.finishOrder.quoteMaterials, function (sum, material) {
                    return sum + material.weight;
                }, 0),
                guaranteedTime: shipment.guaranteedBy,
                commodityClassSet: _.uniq(_.pluck(shipment.finishOrder.quoteMaterials, 'commodityClass')),
                requestLTLRates: shipment.requestLTLRates,
                requestVLTLRates: shipment.requestVLTLRates
            };
        }

        $scope.validatePreviewBol = function () {
            if (!$scope.shipmentEntryData.selectedCustomer) {
                return true;
            }

            if ($scope.isOverDimensionSelected()) {
                var overDimensionsProducts = _.filter($scope.shipmentEntryData.shipment.finishOrder.quoteMaterials, function (product) {
                    return product.height && product.width && product.length;
                });

                if (_.isEmpty(overDimensionsProducts)) {
                    return true;
                }
            }

            return !$scope.shipmentEntryData.shipment.originDetails.address.zip.zip
                    || !$scope.shipmentEntryData.shipment.destinationDetails.address.zip.zip
                    || $scope.shipmentEntryData.shipment.finishOrder.quoteMaterials.length === 0
                    || !$scope.shipmentEntryData.shipment.finishOrder.pickupDate
                    || !$scope.shipmentEntryData.shipment.location
                    || !$scope.shipmentEntryData.shipment.selectedProposition;
        };

        function validateCustomsBroker() {
            if ($scope.shipmentEntryData.shipment.originDetails.address.country.id
                    !== $scope.shipmentEntryData.shipment.destinationDetails.address.country.id) {
                return !$scope.shipmentEntryData.shipment.customsBroker
                        || !$scope.shipmentEntryData.shipment.customsBroker.name
                        || !$scope.shipmentEntryData.shipment.customsBroker.phone.countryCode
                        || !$scope.shipmentEntryData.shipment.customsBroker.phone.areaCode
                        || !$scope.shipmentEntryData.shipment.customsBroker.phone.number;
            }
        }

        $scope.validateClientBillTo = function () {
            return (!$scope.shipmentEntryData.shipment.location && !$scope.shipmentEntryData.shipment.location[0].id)
                    || !$scope.shipmentEntryData.shipment.billTo;
        };

        function validateProposition() {
            if (isLoadHasOpenStatusWithoutCarrier()) {
                return false;
            }

            return !$scope.shipmentEntryData.shipment.selectedProposition
                    || !$scope.shipmentEntryData.shipment.selectedProposition.carrier
                    || !$scope.shipmentEntryData.shipment.selectedProposition.costDetailItems
                    || !$scope.shipmentEntryData.shipment.selectedProposition.costDetailItems.length;
        }

        function isAddressFormInvalid() {
            return ($scope.shipmentEntryData.addressForm && $scope.shipmentEntryData.addressForm.$invalid)
                    || $scope.shipmentEntryData.originForm.invalid
                    || $scope.shipmentEntryData.destinationForm.invalid
                    || $scope.shipmentEntryData.shipment.finishOrder.quoteMaterials.length === 0
                    || !$scope.shipmentEntryData.shipment.finishOrder.pickupDate;
        }

        function isInvalidIdentifiers() {
            return _.some($scope.invalidIdentifier);
        }

        $scope.validateShipment = function () {
            $scope.$broadcast('onValidation');
            $scope.getFormValidity = SelectCarrFormValService.getFormValidity();

            return $scope.validatePreviewBol() || validateCustomsBroker() || !$scope.getFormValidity
                    || validateProposition() || $scope.validateClientBillTo() || isAddressFormInvalid()
                    || (!$scope.shipmentEntryData.shipment.freightBillPayTo || !$scope.shipmentEntryData.shipment.freightBillPayTo.company)
                    || isInvalidIdentifiers() || $scope.shipmentEntryData.shipment.selectedProposition.blockedFrmBkng === 'YES';
        };

        $scope.previewBol = function () {
            $scope.prepareBOL();

            $scope.prepareBolPromise.$promise.then(function () {
                $scope.previewBOLOptions.pdfLocation = urlConfig.shipment + '/customer/shipmentdocs/' + $scope.storedBolId;
                $scope.showPreviewBolWindow = true;
            });
        };

        $scope.closePreviewBolWindow = function () {
            $scope.showPreviewBolWindow = false;
        };

        $scope.isOverDimensionSelected = function () {
            return _.contains($scope.shipmentEntryData.shipment.originDetails.accessorials, 'ODM') ||
                    _.contains($scope.shipmentEntryData.shipment.destinationDetails.accessorials, 'ODM');
        };

        $scope.getQuote = function () {
            if (!$scope.shipmentEntryData.selectedCustomer) {
                $scope.$root.$emit('event:application-error', 'Shipment validation failed!', 'Please select customer.');
                return;
            }

            if ($scope.isOverDimensionSelected()) {
                var overDimensionsProducts = _.filter($scope.shipmentEntryData.shipment.finishOrder.quoteMaterials, function (product) {
                    return product.height && product.width && product.length;
                });

                if (_.isEmpty(overDimensionsProducts)) {
                    $scope.$root.$emit('event:application-error', 'Over Dimension is selected!',
                            'Please add dimension for over dimensional product!');
                    return;
                }
            }
            $scope.shipmentToSend =
                    prepareShipmentForRating($scope.shipmentEntryData.shipment, $scope.shipmentEntryData.selectedCustomer.id, CostDetailsUtils);

            if (isAddressFormInvalid()) {
                $scope.$root.$emit('event:application-error', 'Shipment validation failed!', 'Please fill necessary data.');
                return;
            }
            
            if ($scope.$root.isFieldRequired('REQUEST_VLTL_RATES') 
                    && !$scope.shipmentToSend.requestLTLRates && !$scope.shipmentToSend.requestVLTLRates) {
                $scope.$root.$emit('event:application-error', 'No shipment type selected!', 'Please select LTL or VLTL or both.');
                return;
            }

            $scope.$broadcast('event:get-quote', $scope.shipmentToSend);
        };

        $scope.clearAll = function () {
            if ($route.current.params.loadId) {
                $location.url('/shipment-entry');
            } else {
                $scope.init();
                $scope.updateStatus();
                $scope.$broadcast('event:cleaning-input');
                $scope.confirmedTermsOfAgreement = false;
                $scope.shipmentEntryData.editedEmail = undefined;
                $scope.$broadcast('event:pls-clear-form-data');
            }
        };

        $scope.setConfirmedTermsOfAgreement = function (isConfirmed) {
            $scope.confirmedTermsOfAgreement = isConfirmed;
        };

        $scope.cancelShipment = function () {
            navigateAfterClosing();
        };

        $scope.activateRequote = function () {
            if ($scope.shipmentEntryData.shipment.id) {
                $scope.isRequoteDisabled = false;
            }
        };

        $scope.reQuote = function () {
            if ($scope.shipmentEntryData.shipment.id) {
                $(window).scrollTop(800);
                $scope.getQuote();
                $scope.isRequoteDisabled = true;
            }
        };

        $scope.isFormClean = function () {
            // ignore freightBillPayTo, status and job numbers
            var shipment = _.omit($scope.shipmentEntryData.shipment, ['freightBillPayTo', 'status', 'finishOrder.jobNumbers']);
            shipment.finishOrder = _.omit(shipment.finishOrder, ['jobNumbers']);
            var emptyShipment = _.omit($scope.shipmentEntryData.emptyShipment, ['freightBillPayTo', 'status']);
            emptyShipment.finishOrder = _.omit(emptyShipment.finishOrder, ['jobNumbers']);
            return _.isEqual(shipment, emptyShipment);
        };

        $scope.showTerminalInformation = function (entity, isShipmetTerminalInformation) {
            var terminalCriteria = {
                scac: entity.carrier.scac,
                originAddress: {
                    city: $scope.shipmentEntryData.shipment.originDetails.address.zip.city,
                    stateCode: $scope.shipmentEntryData.shipment.originDetails.address.zip.state,
                    postalCode: $scope.shipmentEntryData.shipment.originDetails.address.zip.zip,
                    countryCode: $scope.shipmentEntryData.shipment.originDetails.address.zip.country.id
                },
                destinationAddress: {
                    city: $scope.shipmentEntryData.shipment.destinationDetails.address.zip.city,
                    stateCode: $scope.shipmentEntryData.shipment.destinationDetails.address.zip.state,
                    postalCode: $scope.shipmentEntryData.shipment.destinationDetails.address.zip.zip,
                    countryCode: $scope.shipmentEntryData.shipment.destinationDetails.address.zip.country.id
                },
                profileDetailId: entity.pricingProfileId,
                shipmentDate: $scope.shipmentEntryData.shipment.finishOrder.pickupDate
            };

            $scope.setProgressText('Loading...');

            ShipmentsProposalService.findTerminalInformation({}, terminalCriteria, function (data) {
                if ($scope.shipmentEntryData.shipment.id && isShipmetTerminalInformation) {
                    data.carrierName = entity.carrier.name;
                    $scope.getCurrentUserDetails(data);
                } else {
                    $scope.$broadcast('event:openTerminalInfoForPreparedCriteria', data);
                }
            });
        };

        $scope.getCurrentUserDetails = function (terminalInfo) {
            UserDetailsService.getCurrentUserContactDetails({}, function (data) {
                terminalInfo.name = data.fullName;
                terminalInfo.phone = data.phone;
                terminalInfo.email = data.email;
                terminalInfo.event = function() {
                    showShipmentDetails($scope.shipmentEntryData.shipment);
                    if(!_.contains(['OPEN', 'DELIVERED', 'CANCELLED'], $scope.shipmentEntryData.shipment.status)){
                        $scope.$root.$emit('event:application-warning', 'Review BOL', 
                                'Please review BOL and send necessary updates to carrier if needed.');
                    }
                };
                $scope.$root.$broadcast('event:openShipmentEntrySaveDialog', terminalInfo);
            });
        };

        $scope.$on('event:closeAndRedirect', function (event, url, params) {
            $location.url(url).search(params);
        });

        $scope.closeSuccessWarningDialog = function () {
            $scope.showSuccessWarning = false;
            $scope.$root.ignoreLocationChange();

            navigateAfterClosing();
        };
    }
]);