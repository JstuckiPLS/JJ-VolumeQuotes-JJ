function prepareShipmentForSend(origShipment, organizationId, proposals) {
    var shipmentToBeSent = angular.copy(origShipment);

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
}

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
            city: shipment.originDetails.zip.city,
            stateCode: shipment.originDetails.zip.state,
            postalCode: shipment.originDetails.zip.zip,
            countryCode: shipment.originDetails.zip.country.id
        },
        destinationAddress: {
            city: shipment.destinationDetails.zip.city,
            stateCode: shipment.destinationDetails.zip.state,
            postalCode: shipment.destinationDetails.zip.zip,
            countryCode: shipment.destinationDetails.zip.country.id
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

angular.module('plsApp').controller('QuoteWizard', ['$scope', '$routeParams', 'LinkedListUtils',
    'SavedQuotesService', 'CostDetailsUtils', 'ShipmentUtils','ShipmentOperationsService', 'CustomerInternalNoteService',
    function ($scope, $routeParams, LinkedListUtils, SavedQuotesService, CostDetailsUtils, ShipmentUtils, ShipmentOperationsService,
         CustomerInternalNoteService) {
        'use strict';

        $scope.wizardData = {};
        $scope.wizardData.rateQuotePath = 'pages/content/quotes/rate-quotes.html?altViewId=' + ($routeParams.altViewId || '');
        $scope.wizardData.steps = LinkedListUtils.getLinkedList();
        $scope.wizardData.steps.add('rate_quote');
        $scope.wizardData.steps.add('select_carrier');
        $scope.wizardData.steps.add('build_order');
        $scope.wizardData.steps.add('finish_order');
        $scope.wizardData.steps.add('finish_quote');
        $scope.wizardData.minPickupDate = new Date();
        $scope.wizardData.showCustomsBroker = false;
        $scope.wizardData.allPickupAccessorials = [];
        $scope.wizardData.allDeliveryAccessorials = [];
        $scope.wizardData.pickupLimitedAccess = undefined;
        $scope.wizardData.deliveryLimitedAccess = undefined;
        $scope.wizardData.maxCountOfProducts = ShipmentUtils.MAX_COUNT_OF_PRODUCTS;
        $scope.wizardData.isCopyShipment = false;
        $scope.wizardData.selectedCustomer = {
            id: undefined,
            name: undefined
        };

        $scope.rateQuoteDictionary = ShipmentUtils.getDictionaryValues();

        $scope.isGuaranteed = CostDetailsUtils.isGuaranteed;
        $scope.getAvailableGuaranteedSorted = CostDetailsUtils.getAvailableGuaranteedSorted;
        $scope.getCostDetailItemsForTotal = CostDetailsUtils.getCostDetailItemsForTotal;
        $scope.getSelectedGuaranteed = CostDetailsUtils.getSelectedGuaranteed;
        $scope.getSimilarCostDetailItem = CostDetailsUtils.getSimilarCostDetailItem;
        $scope.getTotalCost = CostDetailsUtils.getTotalCost;
        $scope.getCarrierTotalCost = CostDetailsUtils.getCarrierTotalCost;
        $scope.calculateCost = CostDetailsUtils.calculateCost;
        $scope.getBaseRate = CostDetailsUtils.getBaseRate;
        $scope.getAccessorials = CostDetailsUtils.getAccessorials;
        $scope.getFuelSurcharge = CostDetailsUtils.getFuelSurcharge;
        $scope.getAccessorialsExcludingFuel = CostDetailsUtils.getAccessorialsExcludingFuel;
        $scope.getAccessorialsRefType = CostDetailsUtils.getAccessorialsRefType;
        $scope.getNote = CostDetailsUtils.getNote;
        $scope.getGuranteedBy = CostDetailsUtils.getGuranteedBy;
        $scope.getItemCost = CostDetailsUtils.getItemCost;

        $scope.oldCustomerVal = undefined;

        $scope.pickup = {
            country: 'USA'
        };

        $scope.deliver = {
            country: 'USA'
        };

        $scope.selectedCustomerCopy = angular.copy($scope.wizardData.selectedCustomer);

        $scope.wizardData.emptyShipment = {
            originDetails: {
                accessorials: [],
                address: undefined
            },
            destinationDetails: {
                accessorials: [],
                address: undefined
            },
            finishOrder: {
                quoteMaterials: [],
                pickupDate: $scope.$root.formatDate(new Date())
            },
            status: 'OPEN',
            requestLTLRates: true
        };

        $scope.wizardData.shipment = angular.copy($scope.wizardData.emptyShipment);

        $scope.nonCommercials = $scope.rateQuoteDictionary.nonCommercials;

        $scope.userCapabilities = ['QUOTES_VIEW'];

        function resetSelectedCustomer() {
            if ($scope.wizardData.shipment.organizationId) {
                $scope.wizardData.selectedCustomer.id = $scope.wizardData.shipment.organizationId;
                $scope.wizardData.selectedCustomer.name = $scope.wizardData.shipment.customerName;
            }
        }

        $scope.$on('event:copyShipment', function (event, data) {
            $scope.wizardData.shipment = data;
            $scope.wizardData.isCopyShipment = true;
        });

        $scope.$watch('accessorialTypes', function (accessorialTypes) {
            if (accessorialTypes) {
                $scope.wizardData.allPickupAccessorials.length = 0;
                $scope.wizardData.allDeliveryAccessorials.length = 0;
                $scope.wizardData.pickupLimitedAccess = undefined;
                $scope.wizardData.deliveryLimitedAccess = undefined;

                _.each(accessorialTypes, function (accessorialType) {
                    if (accessorialType.applicableTo !== 'LTL') {
                        return;
                    }

                    if (accessorialType.accessorialGroup === 'PICKUP') {
                        if (accessorialType.id === 'LAP' || accessorialType.id === 'LAD') {
                            $scope.wizardData.pickupLimitedAccess = accessorialType.id;
                        } else {
                            $scope.wizardData.allPickupAccessorials.push(accessorialType.id);
                        }
                    }

                    if (accessorialType.accessorialGroup === 'DELIVERY') {
                        if (accessorialType.id === 'LAP' || accessorialType.id === 'LAD') {
                            $scope.wizardData.deliveryLimitedAccess = accessorialType.id;
                        } else {
                            $scope.wizardData.allDeliveryAccessorials.push(accessorialType.id);
                        }
                    }
                });
            }
        }, true);

        function clearShipmentModel() {
            var selectedCustomer = angular.copy($scope.wizardData.selectedCustomer);

            $scope.wizardData.shipment = angular.copy($scope.wizardData.emptyShipment);
            $scope.wizardData.selectedCustomer = selectedCustomer;

            $scope.$broadcast('event:pls-clear-form-data');
            $scope.$broadcast('event:update-address-list', $scope.wizardData.selectedCustomer);
        }

        function revertCustomer() {
            $scope.wizardData.selectedCustomer = $scope.oldCustomerVal;
        }

        function initWizard() {
            $scope.$on('event:changeCustomer', function (event, selectedCustomer, oldCustomer) {
                if (!selectedCustomer || !selectedCustomer.id || _.isEqual(selectedCustomer, oldCustomer) ||
                        _.isEqual($scope.wizardData.emptyShipment, $scope.wizardData.shipment)) {
                    
                    if (selectedCustomer && selectedCustomer.id) {
                        CustomerInternalNoteService.get({
                            customerId: selectedCustomer.id
                        }, function (data) {
                            selectedCustomer.internalNote = data.data;
                        });
                    }
                    
                    if (_.isEqual($scope.wizardData.emptyShipment, $scope.wizardData.shipment)) {
                        $scope.$broadcast('event:update-address-list', selectedCustomer);
                    }
                    if (selectedCustomer && selectedCustomer.id && oldCustomer && oldCustomer.id && !_.isEqual(oldCustomer, selectedCustomer)) {
                        $scope.$broadcast('event:pls-clear-form-data');
                    }
                    return;
                }

                $scope.oldCustomerVal = oldCustomer;

                $scope.$root.$broadcast('event:showConfirmation', {
                    caption: 'Warning!',
                    message: "This action will clear all fields on screen. Will you like to proceed?",
                    okFunction: clearShipmentModel,
                    closeFunction: revertCustomer,
                    confirmButtonLabel: "Yes",
                    closeButtonLabel: "No"
                });
            });
        }

        if ($routeParams.shipmentId) {
            ShipmentOperationsService.getShipment({
                customerId: $scope.authData.organization.orgId,
                shipmentId: $routeParams.shipmentId
            }, function (data) {
                $scope.wizardData.shipment = data;
                resetSelectedCustomer();
                initWizard();
                $scope.pickup.country = $scope.wizardData.shipment.originDetails.zip.country;
                $scope.deliver.country = $scope.wizardData.shipment.destinationDetails.zip.country;

                var selectedStep = $scope.wizardData.steps.first();

                if ($routeParams.stepName && $scope.wizardData.steps.find($routeParams.stepName)) {
                    selectedStep = $scope.wizardData.steps.find($routeParams.stepName);
                }

                $scope.wizardData.step = selectedStep;
            }, function () {
                $scope.$root.$emit('event:application-error', 'Shipment load failed!', 'Can\'t load shipment with ID ' + $routeParams.shipmentId);
            });
        } else if ($routeParams.copyShipmentId) {
            ShipmentOperationsService.getCopiedShipment({
                customerId: $scope.authData.organization.orgId,
                shipmentId: $routeParams.copyShipmentId
            }, function (data) {
                $scope.wizardData.shipment = data;
                resetSelectedCustomer();
                initWizard();
                $scope.wizardData.shipment.finishOrder.pickupDate = $scope.$root.formatDate(new Date());
                $scope.pickup.country = $scope.wizardData.shipment.originDetails.zip.country;
                $scope.deliver.country = $scope.wizardData.shipment.destinationDetails.zip.country;
                $scope.wizardData.step = $scope.wizardData.steps.first();
            }, function () {
                $scope.$root.$emit('event:application-error', 'Shipment copy failed!', 'Can\'t copy shipment with ID ' + $routeParams.copyShipmentId);
            });

            $scope.wizardData.isCopyShipment = true;
        } else if ($routeParams.savedQuoteId) {
            $scope.wizardData.editedQuoteId = $routeParams.savedQuoteId;
            SavedQuotesService.get({
                customerId: $scope.authData.organization.orgId,
                propositionId: $scope.wizardData.editedQuoteId
            }, function (data) {
                $scope.wizardData.shipment = data.shipmentDTO;
                resetSelectedCustomer();
                initWizard();

                if ($routeParams.stepName && $scope.wizardData.steps.find($routeParams.stepName)) {
                    $scope.wizardData.step = $scope.wizardData.steps.find($routeParams.stepName);
                    if ($scope.wizardData.step === 'rate_quote') {
                        $scope.wizardData.shipment.finishOrder.pickupDate = $scope.$root.formatDate(new Date());
                    }
                }
            });
        } else {
            $scope.wizardData.step = $scope.wizardData.steps.first();
            initWizard();
        }

        $scope.$watch('wizardData.shipment.originDetails.accessorials', function (accessorials) {
            if ((_.contains(accessorials, 'REP') || _.contains(accessorials, 'RES'))
                    && (_.contains(accessorials, 'LAP') || _.contains(accessorials, 'LAD'))) {
                $scope.wizardData.shipment.originDetails.accessorials = _.without(accessorials, 'LAP', 'LAD');
            }
        }, true);

        $scope.$watch('wizardData.shipment.destinationDetails.accessorials', function (accessorials) {
            if ((_.contains(accessorials, 'REP') || _.contains(accessorials, 'RES'))
                    && (_.contains(accessorials, 'LAP') || _.contains(accessorials, 'LAD'))) {
                $scope.wizardData.shipment.destinationDetails.accessorials = _.without(accessorials, 'LAP', 'LAD');
            }
        }, true);

        $scope.getAccessorialsNames = ShipmentUtils.getAccessorialsNames;
    }
]);

angular.module('plsApp').controller('RateQuoteCtrl', ['$scope', '$timeout', 'AddressesListService', 'ShipmentUtils',
    function ($scope, $timeout, AddressesListService, ShipmentUtils) {
        'use strict';

        function executeWhen(func, predicate, waitTime, maxAttempts) {
            var numberOfAttempts = 0;

            function timeoutFn() {
                $timeout(function () {
                    if (!predicate()) {
                        numberOfAttempts = numberOfAttempts + 1;

                        if (numberOfAttempts <= maxAttempts) {
                            timeoutFn();
                        }
                    } else {
                        func();
                    }
                }, waitTime);
            }

            timeoutFn();
        }

        $scope.$watch('wizardData.selectedCustomer', function(newValue) {
            if (newValue && newValue.id) {
                $scope.getAllAddressForCustomer(newValue.id);
            }
            $scope.$broadcast('event:clear-products');
        });

        if ($scope.authData.assignedOrganization) {
            executeWhen(function () {
                $('#zipInp:first').focus();
            }, function () {
                return $('#zipInp:first') && $('#zipInp:first').length !== 0;
            }, 500, 10);
        } else {
            $('#customerInput').focus();
        }

        $scope.openCopyFromDialog = function () {
            $scope.$broadcast('event:openCopyFromsDialog', $scope.wizardData.selectedCustomer);
        };

        $scope.isOverDimensionSelected = function () {
            return _.contains($scope.wizardData.shipment.originDetails.accessorials, 'ODM') ||
                    _.contains($scope.wizardData.shipment.destinationDetails.accessorials, 'ODM');
        };

        $scope.isAllowSelectAddressName = function () {
            return !($scope.wizardData.selectedCustomer && $scope.wizardData.selectedCustomer.id
                    && ($scope.$root.isFieldRequired('ADD_EDIT_ADDRESS_BOOK_PAGE') || $scope.$root.isFieldRequired('VIEW_ADDRESS_ONLY')));
        };

        $scope.getPickupDateIndex = function(){
            return document.getElementById('pickupDate').tabIndex;
        };
        $scope.getQuote = function () {
            if ((!$scope.wizardData.selectedCustomer || !$scope.wizardData.selectedCustomer.name) &&
                   !$scope.$root.isFieldRequired('ADD_QUOTE_WITHOUT_CUSTOMER')) {
                $scope.$root.$emit('event:application-error', 'Shipment validation failed!', 'Please select customer.');
                return;
            }

            if ($scope.isOverDimensionSelected()) {
                var overDimensionsProducts = _.filter($scope.wizardData.shipment.finishOrder.quoteMaterials, function (product) {
                    return product.height && product.width && product.length;
                });

                if (_.isEmpty(overDimensionsProducts)) {
                    $scope.$root.$emit('event:application-error', 'Over Dimension is selected!',
                            'Please add dimension for over dimensional product!');
                    return;
                }
            }

            if (!$scope.wizardData.shipment.originDetails.zip || !$scope.wizardData.shipment.destinationDetails.zip || $scope.addressesForm.$invalid
                    || $scope.wizardData.shipment.finishOrder.quoteMaterials.length === 0 || !$scope.wizardData.shipment.finishOrder.pickupDate) {
                $scope.$root.$emit('event:application-error', 'Shipment validation failed!', 'Please fill necessary data.');
                return;
            }
            
            if ($scope.$root.isFieldRequired('REQUEST_VLTL_RATES')
                    && !$scope.wizardData.shipment.requestLTLRates && !$scope.wizardData.shipment.requestVLTLRates) {
                $scope.$root.$emit('event:application-error', 'No shipment type selected!', 'Please select LTL or VLTL or both.');
                return;
            }

            $scope.wizardData.step = $scope.wizardData.steps.next();
        };

        $scope.originAndDestinationAreCanada = function() {
            return $scope.wizardData.shipment.originDetails.zip &&
                    $scope.wizardData.shipment.destinationDetails.zip &&
                    $scope.wizardData.shipment.originDetails.zip.country.id === "CAN" &&
                    $scope.wizardData.shipment.destinationDetails.zip.country.id === "CAN";
        };

        $scope.clearAll = function () {
            $scope.$broadcast('event:cleaning-input');
            $scope.wizardData.shipment = angular.copy($scope.wizardData.emptyShipment);
            $scope.$broadcast('event:pls-clear-form-data');
            $scope.wizardData.step = $scope.wizardData.steps.first();
        };

        $scope.isFormClean = function () {
            return _.isEqual($scope.wizardData.emptyShipment, $scope.wizardData.shipment);
        };

        $scope.getAllAddressForCustomer = function (selectedCustomerId) {
            if (selectedCustomerId) {
                $scope.dataAddressNames = AddressesListService.listUserContacts({customerId: selectedCustomerId, type: 'SHIPPING,BOTH'});
            }
        };

        $scope.$on('event:update-address-list', function (event, selectedCustomer) {
            if (selectedCustomer) {
                $scope.getAllAddressForCustomer(selectedCustomer.id);
            }
        });

        $scope.updateAddressName = function (address) {
            if (address.isDeliveryAddress) {
                $scope.wizardData.shipment.destinationDetails.address = angular.copy(address.addressToUpdate);

                if (address.addressToUpdate && address.addressToUpdate.zip) {
                    $scope.wizardData.shipment.destinationDetails.zip = angular.copy(address.addressToUpdate.zip);
                }
            } else {
                $scope.wizardData.shipment.originDetails.address = angular.copy(address.addressToUpdate);

                if (address.addressToUpdate && address.addressToUpdate.zip) {
                    $scope.wizardData.shipment.originDetails.zip = angular.copy(address.addressToUpdate.zip);
                }
            }
            if (address.addressToUpdate) {  
                if(!$scope.wizardData.shipment.finishOrder.shipmentNotifications){
                    $scope.wizardData.shipment.finishOrder.shipmentNotifications=[];
                }

                ShipmentUtils.addAddressNotificationsToLoadNotificationsWithoutDuplicates(
                        $scope.wizardData.shipment.finishOrder.shipmentNotifications,
                        address.addressToUpdate.shipmentNotifications, !address.isDeliveryAddress);
                }
        };
    }
]);

angular.module('plsApp').controller('SelectCarrierCtrl', [
    '$scope', '$filter', 'NgGridPluginFactory', 'ShipmentsProposalService', 'CostDetailsUtils', 'ProductTotalsService', 'ShipmentUtils',
    function ($scope, $filter, NgGridPluginFactory, ShipmentsProposalService, CostDetailsUtils, ProductTotalsService, ShipmentUtils) {
        'use strict';

        $scope.hazmatInfo = {};

        $scope.pageModel = {
            carrierPropositionsGridData: [],
            selectedPropositions: [],
            selectedProposition: undefined,
            sortBy: 'totalCost'
        };

        if (!$scope.wizardData.shipment.guaranteedBy && $scope.wizardData.shipment.guaranteedBy !== 0) {
            $scope.wizardData.shipment.guaranteedBy = undefined;
        }

        $scope.guaranteedTimeOptions = ShipmentUtils.getGuaranteedTimeOptions();

        function updateShipperRate(proposal, revOverridePerc, margin) {
            margin = margin ? Number(Number(margin).toFixed(2)) : 0;

            if (revOverridePerc === 'marginPerc') {
                _.each(proposal.costDetailItems, function (shipperItem) {
                    if (shipperItem.costDetailOwner === 'S') {
                        _.each(proposal.costDetailItems, function (carrierItem) {
                            if (carrierItem.costDetailOwner === 'C' && (carrierItem.refType === shipperItem.refType ||
                                    (carrierItem.refType === 'CRA' && shipperItem.refType === 'SRA'))) {
                                if (shipperItem.refType === 'GD' && carrierItem.guaranteedBy === shipperItem.guaranteedBy) {
                                    shipperItem.subTotal = Number(Number(carrierItem.subTotal / (1 - (margin / 100))).toFixed(2));
                                } else if (shipperItem.refType !== 'GD') {
                                    shipperItem.subTotal = Number(Number(carrierItem.subTotal / (1 - (margin / 100))).toFixed(2));
                                }
                            }
                        });
                    }
                });
            } else {
                _.each(proposal.costDetailItems, function (shipperItem) {
                    if (shipperItem.costDetailOwner === 'S') {
                        _.each(proposal.costDetailItems, function (carrierItem) {
                            if (carrierItem.costDetailOwner === 'C' && carrierItem.refType === shipperItem.refType) {
                                if (shipperItem.refType === 'GD' && carrierItem.guaranteedBy === shipperItem.guaranteedBy) {
                                    shipperItem.subTotal = carrierItem.subTotal;
                                } else if (shipperItem.refType !== 'GD') {
                                    shipperItem.subTotal = carrierItem.subTotal;
                                }
                            }
                            else if (carrierItem.refType === 'CRA' && shipperItem.refType === 'SRA') {
                                if (revOverridePerc === 'flatMarkup') {
                                    shipperItem.subTotal = carrierItem.subTotal + margin;
                                } else if (revOverridePerc === 'zeroRevOverride') {
                                    shipperItem.subTotal = carrierItem.subTotal;
                                }
                            }
                        });
                    }
                });
            }
        }

        function updateRateAndAccessorials(proposal, subTotal, owner, baseRateRefType) {
            _.each(proposal.costDetailItems, function (item) {
                if (item.costDetailOwner === owner) {
                    item.subTotal = item.refType === baseRateRefType ? subTotal : 0;
                }
            });
        }

        function prepopulateVolumeQuoteId(){
            if($scope.pageModel.selectedProposition && $scope.pageModel.selectedProposition.shipmentType === 'VLTL'){
                $scope.wizardData.shipment.volumeQuoteID = $scope.pageModel.selectedProposition.carrierQuoteNumber;
            } else {
                $scope.wizardData.shipment.volumeQuoteID = undefined;
            }
        }

        function updateCosts(proposal) {
            var revMargin = $scope.wizardData.shipment.margins.revenueOverride;

            proposal.revenueOverride = (revMargin !== null && revMargin !== undefined) ? 'YES' : 'NO';

            if ($scope.wizardData.shipment.margins.costOverride !== null && $scope.wizardData.shipment.margins.costOverride !== undefined) {
                proposal.costOverride = "YES";
            } else {
                proposal.costOverride = "NO";
            }

            if (!_.isUndefined($scope.wizardData.shipment.margins.costOverride) && _.isUndefined(revMargin)) {
                var appliedLinehaulMargin = proposal.appliedLinehaulMarginAmt;

                if (_.isUndefined(appliedLinehaulMargin)) {
                    appliedLinehaulMargin = CostDetailsUtils.getBaseRate(proposal, 'S') - CostDetailsUtils.getBaseRate(proposal, 'C');
                }

                updateRateAndAccessorials(proposal, $scope.wizardData.shipment.margins.costOverride, 'C', 'CRA');

                var subTotalSra;

                if (proposal.linehaulMarginPerc) {
                    subTotalSra = (100 * $scope.wizardData.shipment.margins.costOverride / (100 - proposal.linehaulMarginPerc));
                } else {
                    subTotalSra = appliedLinehaulMargin + $scope.wizardData.shipment.margins.costOverride;
                }

                updateRateAndAccessorials(proposal, subTotalSra, 'S', 'SRA');

                var diff = subTotalSra - $scope.wizardData.shipment.margins.costOverride;

                if (proposal.minLinehaulMarginAmt && diff < proposal.minLinehaulMarginAmt) {
                    subTotalSra = $scope.wizardData.shipment.margins.costOverride + proposal.minLinehaulMarginAmt;
                    updateRateAndAccessorials(proposal, subTotalSra, 'S', 'SRA');
                } else if (proposal.defaultMarginAmt && diff < proposal.defaultMarginAmt) {
                    subTotalSra = $scope.wizardData.shipment.margins.costOverride + proposal.defaultMarginAmt;
                    updateRateAndAccessorials(proposal, subTotalSra, 'S', 'SRA');
                }
            } else if (_.isUndefined($scope.wizardData.shipment.margins.costOverride)) {
                $scope.wizardData.shipment.volumeQuoteID = undefined;
            }

            if ((_.isUndefined($scope.wizardData.selectedCustomer) || _.isUndefined($scope.wizardData.selectedCustomer.id))
                    && _.isUndefined(revMargin) && $scope.wizardData.shipment.margins.revenueOverrideOption === 'marginPerc') {
                 revMargin = 0;
            }

            if (!_.isUndefined(revMargin)) {
                if ($scope.wizardData.shipment.margins.revenueOverrideOption === 'totalRevenue' &&
                        _.isUndefined($scope.wizardData.shipment.margins.costOverride)) {
                    updateRateAndAccessorials(proposal, revMargin, 'S', 'SRA');
                } else if ($scope.wizardData.shipment.margins.revenueOverrideOption === 'totalRevenue'
                        && !_.isUndefined($scope.wizardData.shipment.margins.costOverride)) {
                    updateRateAndAccessorials(proposal, $scope.wizardData.shipment.margins.costOverride, 'C', 'CRA');
                    updateRateAndAccessorials(proposal, revMargin, 'S', 'SRA');
                } else if ($scope.wizardData.shipment.margins.revenueOverrideOption !== 'totalRevenue' &&
                        _.isUndefined($scope.wizardData.shipment.margins.costOverride)) {
                    if (revMargin === 0) {
                        updateShipperRate(proposal, 'zeroRevOverride', 0);
                    } else {
                        updateShipperRate(proposal, $scope.wizardData.shipment.margins.revenueOverrideOption, revMargin);
                    }
                } else {
                    updateRateAndAccessorials(proposal, $scope.wizardData.shipment.margins.costOverride, 'C', 'CRA');
                    if (revMargin === 0) {
                        updateShipperRate(proposal, 'zeroRevOverride', 0);
                    } else {
                        var subTotal = $scope.wizardData.shipment.margins.revenueOverrideOption === 'marginPerc' ?
                        $scope.wizardData.shipment.margins.costOverride / (1 - (revMargin / 100)) :
                        $scope.wizardData.shipment.margins.costOverride + revMargin;
                        updateRateAndAccessorials(proposal, subTotal, 'S', 'SRA');
                    }
                }
            }
            
            prepopulateVolumeQuoteId();
        }

        function prepareProposalsForGrid(proposals) {
            $scope.pageModel.carrierPropositionsGridData = [];

            angular.forEach(proposals, function (proposal) {
                if ($scope.wizardData.shipment.margins) {
                    updateCosts(proposal);
                }

                var selectedGuaranteed = $scope.getSelectedGuaranteed(proposal, $scope.wizardData.shipment.guaranteedBy);

                var processedProposal = {
                    logoPath: proposal.carrier.logoPath,
                    carrierName: proposal.carrier.name,
                    serviceType: proposal.serviceType,
                    estimatedTransitDate: _.isUndefined(proposal.estimatedTransitDate) ? 'N/A' : $scope.parseISODate(proposal.estimatedTransitDate),
                    estimatedTransitTime: proposal.estimatedTransitTime === 0 ? 'N/A' : proposal.estimatedTransitTime,
                    liability: $filter('plsCurrency')(proposal.newLiability) + '/' + $filter('plsCurrency')(proposal.usedLiability),
                    currencyCode: proposal.carrier.currencyCode,
                    origProposal: proposal,
                    totalCost: $scope.getTotalCost(proposal, $scope.wizardData.shipment.guaranteedBy),
                    integrationType: proposal.integrationType,
                    ratingCarrierType: proposal.ratingCarrierType,
                    shipmentType: proposal.shipmentType,
                    serviceLevelCode: proposal.serviceLevelCode,
                    serviceLevelDescription: proposal.serviceLevelDescription
                };

                if (!proposal.hideCostDetails || $scope.$root.isFieldRequired('CAN_VIEW_HIDDEN_RATES')) {
                    if ($scope.showItemByPermissions()) {
                        processedProposal.totalCostText = $filter('plsCurrency')(processedProposal.totalCost) + "/" +
                                $filter('plsCurrency')($scope.getCarrierTotalCost(proposal, $scope.wizardData.shipment.guaranteedBy));
                    } else if ($scope.$root.isFieldRequired('VIEW_PLS_CUSTOMER_COST')) {
                        processedProposal.totalCostText = $filter('plsCurrency')(processedProposal.totalCost);
                    }
                }

                processedProposal.guaranteedBy = (selectedGuaranteed && selectedGuaranteed.subTotal) ? selectedGuaranteed.guaranteedBy
                        : $scope.wizardData.shipment.guaranteedBy;

                $scope.pageModel.carrierPropositionsGridData.push(processedProposal);
            });

        }

        function updateCostDetails() {
            if ($scope.wizardData.proposals && $scope.wizardData.proposals.length > 0) {
                $scope.wizardData.proposals = angular.copy($scope.originalProposals);

                _.each($scope.wizardData.proposals, function (proposal) {
                    updateCosts(proposal);
                });

                prepareProposalsForGrid(angular.copy($scope.originalProposals));
                $scope.sortCarrierPropositions();

                if ($scope.pageModel.selectedPropositions.length) {
                    $scope.pageModel.selectedProposition = _.find($scope.wizardData.proposals, function (proposal) {
                        return $scope.pageModel.selectedPropositions[0].origProposal.guid === proposal.guid;
                    });
                }
            }
        }

        $scope.showItemByPermissions = function () {
            return $scope.$root.isFieldRequired('EDIT_PLS_REVENUE') ||
                    $scope.$root.isFieldRequired('EDIT_CARRIER_COST') ||
                    $scope.$root.isFieldRequired('VIEW_PLS_REVENUE_AND_CARRIER_COST');
        };

// init model for the margin calculation drop downs
        if (!$scope.wizardData.shipment.margins) {
            $scope.wizardData.shipment.margins = {
                revenueOverrideOption: 'marginPerc',
                revenueOverride: undefined,
                costOverride: undefined
            };
        }

        $scope.$watch('wizardData.shipment.margins.revenueOverrideOption', function () {
            if ($scope.wizardData.shipment.margins) {
                $scope.wizardData.shipment.margins.revenueOverride = undefined;

                if ($scope.wizardData.shipment.margins.revenueOverrideOption === "marginPerc") {
                    $scope.forbidZero = false;
                    $scope.revOverrideNumPattern = 'positivePercentage';
                }

                if ($scope.wizardData.shipment.margins.revenueOverrideOption === "flatMarkup") {
                    $scope.forbidZero = false;
                    $scope.revOverrideNumPattern = 'positiveZeroDecimal';
                }

                if ($scope.wizardData.shipment.margins.revenueOverrideOption === "totalRevenue") {
                    $scope.forbidZero = true;
                    $scope.revOverrideNumPattern = 'positiveZeroDecimal';
                }
            }
        });

        $scope.$watch(function () {
            var result = 'c';

            if (!_.isUndefined($scope.wizardData.shipment.margins.costOverride) && $scope.wizardData.shipment.margins.costOverride > 0) {
                result = result + Number($scope.wizardData.shipment.margins.costOverride).toFixed(2);
            }

            result += 'r';

            if (!_.isUndefined($scope.wizardData.shipment.margins.revenueOverride) && $scope.wizardData.shipment.margins.revenueOverride >= 0) {
                result = result + Number($scope.wizardData.shipment.margins.revenueOverride).toFixed(2);
            }

            return result;
        }, updateCostDetails);

        $scope.$watch(function () {
            var popoverElement = $("[data-rel=popover]");

            if (popoverElement.length) {
                popoverElement.popover();
            }
        });

        $scope.sortCarrierPropositions = function () {
            if ($scope.pageModel.sortBy === 'estimatedTransitTime') {
                // For Customer Users we sort by total cost on server because we don't show prices of Tier1 carriers!
                $scope.pageModel.carrierPropositionsGridData = _.sortBy($scope.pageModel.carrierPropositionsGridData, $scope.pageModel.sortBy);
            } else {
                prepareProposalsForGrid(angular.copy($scope.originalProposals));
            }
        };

        function showEmptyPropositionDialog() {
            $scope.wizardData.shipment.guaranteedBy = undefined;
            $scope.showPickupMessage();
        }

        $scope.init = function () {
            if ($scope.wizardData.shipment.margins && !$scope.wizardData.shipment.margins.costOverride) {
                $scope.wizardData.shipment.volumeQuoteID = undefined;
            }

            $scope.isCanNotBook = ($scope.$root.authData.organization && $scope.$root.authData.organization.statusReason )
                    && _.contains(['CREDIT_HOLD', 'TAX_ID_EMPTY'], $scope.$root.authData.organization.statusReason);

            var shipmentToSend = prepareShipmentForRating($scope.wizardData.shipment,
                    $scope.wizardData.selectedCustomer ? $scope.wizardData.selectedCustomer.id : undefined, CostDetailsUtils);

            ShipmentsProposalService.findShipmentPropositions({guid: $scope.wizardData.shipment.guid}, shipmentToSend, function (data) {
                $scope.wizardData.proposals = data;
                $scope.originalProposals = angular.copy($scope.wizardData.proposals);

                if (data && angular.isArray(data) && data.length > 0) {
                    angular.forEach($scope.wizardData.proposals, function (proposal) {
                        if ($scope.wizardData.shipment.guaranteedBy && (!proposal.hideCostDetails || $scope.authData.plsUser
                                || $scope.$root.isFieldRequired('CAN_VIEW_HIDDEN_RATES'))) {
                            proposal.addlGuaranInfo = $scope.getAddlGuaranInfo(proposal);
                        }
                    });

                    if (($scope.wizardData.shipment.guaranteedBy !== undefined && !$scope.carrierPropositionsGrid.$gridScope.columns[4].visible) ||
                            ($scope.wizardData.shipment.guaranteedBy === undefined && $scope.carrierPropositionsGrid.$gridScope.columns[4].visible)) {
                        $scope.carrierPropositionsGrid.$gridScope.columns[4].toggleVisible();
                    }

                    if ($scope.wizardData.shipment.margins && ($scope.wizardData.shipment.margins.costOverride
                            || $scope.wizardData.shipment.margins.revenueOverride)) {
                        updateCostDetails();
                    } else {
                        prepareProposalsForGrid(data);
                        $scope.sortCarrierPropositions();

                        if ((_.isUndefined($scope.wizardData.selectedCustomer)
                               || _.isUndefined($scope.wizardData.selectedCustomer.id))
                               && (_.isUndefined($scope.wizardData.shipment.margins)
                               || _.isUndefined($scope.wizardData.shipment.margins.revenueOverride))
                               && !_.isUndefined($scope.wizardData.proposals)
                               && !_.isUndefined($scope.wizardData.proposals[0].linehaulMarginPerc)) {
                            $scope.wizardData.shipment.margins.revenueOverride = $scope.wizardData.proposals[0].linehaulMarginPerc;
                            $scope.wizardData.shipment.margins.revenueOverrideOption = "marginPerc";
                            // for some proposals this logic can work incorrectly when revenue was overridden by DefaultMarginAmt
                            // but it seems like nobody cares as on Production DefaultMarginAmt for LTL network is not set
                        }
                    }

                    $scope.pageModel.selectedPropositions[0] = $scope.pageModel.carrierPropositionsGridData[0];
                    $scope.pageModel.selectedProposition = $scope.pageModel.carrierPropositionsGridData[0].origProposal;
                } else {
                    showEmptyPropositionDialog();
                }
            }, function () {
                showEmptyPropositionDialog();
            });
        };

        $scope.showPickupMessage = function () {
            $scope.pickUpWindowOpen = true;
        };

        $scope.back = function () {
            $scope.wizardData.shipment.margins.revenueOverride = undefined;
            $scope.wizardData.shipment.margins.costOverride = undefined;
            $scope.wizardData.shipment.volumeQuoteID = undefined;
            $scope.wizardData.step = $scope.wizardData.steps.prev();
        };

        $scope.isQuoteAlreadySaved = function() {
            return _.has($scope.wizardData.savedQuoteDetails, $scope.pageModel.selectedProposition.guid);
        };

        $scope.saveProposition = function (proposition) {
            $scope.wizardData.shipment.selectedProposition = proposition;
            $scope.$broadcast('event:saveSelectedQuoteForWizard', {selectedCustomer: $scope.wizardData.selectedCustomer, saveQuoteDialog: true});
        };

        $scope.book = function (proposition) {
            if (!$scope.wizardData.shipment.finishOrder.pickupDate) {
                $scope.$root.$emit('event:application-error', 'Shipment validation failed!', 'Please fill necessary data.');
                return;
            }

            $scope.wizardData.shipment.selectedProposition = proposition;

            //set Estimation Delivery date based on selected proposal
            $scope.wizardData.shipment.finishOrder.estimatedDelivery = proposition.estimatedTransitDate;
            var savedQuoteDetails = $scope.wizardData.savedQuoteDetails[$scope.wizardData.shipment.selectedProposition.guid];
            if (savedQuoteDetails) {
                $scope.wizardData.shipment.quoteId = savedQuoteDetails.quoteId;
                $scope.wizardData.shipment.finishOrder.ref = savedQuoteDetails.quoteRef;
            } else if ($scope.wizardData.editedQuoteId) {
                $scope.wizardData.shipment.quoteId = $scope.wizardData.editedQuoteId;
            }
            $scope.wizardData.step = $scope.wizardData.steps.next();
        };

        $scope.getSelectedPropositionGuaranteed = function () {
            if ($scope.wizardData.shipment.guaranteedBy && $scope.pageModel.selectedProposition &&
                    (!$scope.pageModel.selectedProposition.hideCostDetails || $scope.authData.plsUser
                    || $scope.$root.isFieldRequired('CAN_VIEW_HIDDEN_RATES'))) {
                var guaranteed = $scope.getAddlGuaranInfo($scope.pageModel.selectedProposition);

                if ($scope.pageModel.selectedProposition.guaranteedNameForBOL) {
                    if (guaranteed.length) {
                        guaranteed += '&#10;&#10;';
                    }
                    guaranteed += 'Guaranteed name for BOL: ' + $scope.pageModel.selectedProposition.guaranteedNameForBOL;
                }

                return guaranteed;
            }
            return '';
        };

        $scope.getAddlGuaranInfo = function (proposition) {
            return _.map($scope.getAvailableGuaranteedSorted(proposition.costDetailItems, 'S'), function (acc) {
                        var result = $filter('longTime')(acc.guaranteedBy) + '&nbsp;';

                        if ($scope.authData.plsUser) {
                            result += $filter('plsCurrency')($scope.getSimilarCostDetailItem(proposition, acc, false).subTotal);
                            result += '&nbsp;/&nbsp;';
                        }

                        result += $filter('plsCurrency')(acc.subTotal);

                        return result;
                    }).join('; ') || '';
        };

        $scope.showTerminalInformation = function (entity) {
            var terminalCriteria = {
                scac: entity.origProposal.carrier.scac,
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
                },
                profileDetailId: entity.pricingProfileId,
                shipmentDate: $scope.wizardData.shipment.finishOrder.pickupDate
            };

            $scope.setProgressText('Loading...');

            ShipmentsProposalService.findTerminalInformation({}, terminalCriteria, function (data) {
                $scope.$broadcast('event:openTerminalInfoForPreparedCriteria', data);
            });
        };

        function showTotalCostColumn() {
            return $scope.$root.isFieldRequired('EDIT_PLS_REVENUE') ||
                    $scope.$root.isFieldRequired('EDIT_CARRIER_COST') ||
                    $scope.$root.isFieldRequired('VIEW_PLS_REVENUE_AND_CARRIER_COST') ||
                    $scope.$root.isFieldRequired('VIEW_PLS_CUSTOMER_COST');
        }

        $scope.carrierPropositionsGrid = {
            data: 'pageModel.carrierPropositionsGridData',
            selectedItems: $scope.pageModel.selectedPropositions,
            primaryKey: 'origProposal.guid',
            columnDefs: [
                {
                    width: '5%',
                    cellClass: 'text-center gridImageCell',
                    cellTemplate: '<div style="background-image: url({{row.entity.logoPath}});" class="gridImageCell"><div/>'
                },
                {
                    field: 'carrierName',
                    displayName: 'Carrier Name',
                    width: '18%',
                    cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
                    '<i class="fa fa-envelope-o" data-ng-show="row.entity.integrationType == \'MANUAL\'"></i>&nbsp;' +
                    '<i class="pls-integration-ltllc-icon" data-ng-show="row.entity.ratingCarrierType == \'LTLLC\' && authData.plsUser"></i>&nbsp;' +
                    '<i class="pls-vltl-icon" data-ng-show="row.entity.shipmentType == \'VLTL\'"></i>&nbsp;' +
                    '<a href="" data-ng-click="showTerminalInformation(row.entity)">{{row.getProperty(col.field)}}</a>' +
                    '</div>'
                },
                {
                    field: 'serviceType',
                    displayName: 'Direct/Indirect',
                    width: '9%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'liability',
                    headerCellTemplate: 'pages/cellTemplate/max-liability-header-cell.html',
                    width: '13%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    visible: $scope.$root.isFieldRequired('CAN_VIEW_LIABILITY')
                },
                {
                    field: 'guaranteedBy',
                    displayName: 'Guaranteed',
                    cellFilter: 'longTime',
                    width: '9%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    visible: false // column should be rendered only when guaranteedBy option other than 'None' is selected
                },
                {
                    field: 'estimatedTransitDate',
                    displayName: 'Transit Est. Date',
                    width: '12%',
                    cellFilter: 'date:wideAppDateFormat',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'estimatedTransitTime',
                    displayName: 'Transit Est. Time',
                    width: '8%',
                    cellFilter: 'minutesTime',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'totalCostText',
                    displayName: ($scope.showItemByPermissions() ? 'Total Revenue / Total Cost' : 'Total Cost'),
                    width: '14%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    cellTemplate: 'pages/cellTemplate/blocked-from-booking-cell.html',
                    visible: showTotalCostColumn()
                },
                {
                    field: 'currencyCode',
                    displayName: 'Currency Code',
                    width: '6%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'details',
                    displayName: 'Details',
                    width: '6%',
                    cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">'
                    + '<a href="" data-ng-click="showCarrierDetails(row.entity)">Details</a></div>',
                    visible: $scope.$root.isFieldRequired('VIEW_CARRIER_DETAILS')
                }
            ],
            action: function (entity) {
                $scope.saveProposition(entity.origProposal);
            },
            afterSelectionChange: function (item) {
                $scope.pageModel.selectedProposition = item.entity.origProposal;
                prepopulateVolumeQuoteId();
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin()],
            enableColumnResize: true,
            enableSorting: false,
            multiSelect: false
        };

        function isHazmat(entity) {
            return entity.hazmat;
        }

        function onShowTooltip(row) {
            if (row.entity.id || row.entity.productId) {
                $scope.hazmatInfo.exist = true;
                $scope.hazmatInfo.company = row.entity.emergencyResponseCompany;

                $scope.hazmatInfo.phone = {
                    emergencyResponsePhoneCountryCode: row.entity.emergencyResponsePhoneCountryCode,
                    emergencyResponsePhoneAreaCode: row.entity.emergencyResponsePhoneAreaCode,
                    emergencyResponsePhone: row.entity.emergencyResponsePhone,
                    emergencyResponsePhoneExtension: row.entity.emergencyResponsePhoneExtension
                };

                $scope.hazmatInfo.contractNum = row.entity.emergencyResponseContractNumber;
                $scope.hazmatInfo.unNum = row.entity.unNum;
                $scope.hazmatInfo.packingGroup = row.entity.packingGroup;
            } else {
                $scope.hazmatInfo.exist = false;
            }

            $scope.itemsGridOptions.tooltipOptions.url = 'pages/content/quotes/hazmat-info-tooltip.html';
        }

        function calculateTotals() {
            $scope.totals = ProductTotalsService.calculateTotals($scope.wizardData.shipment.finishOrder.quoteMaterials);
        }

        calculateTotals();

        $scope.itemsGridOptions = {
            data: 'wizardData.shipment.finishOrder.quoteMaterials',
            enableRowSelection: false,
            enableSorting: false,
            enableColumnResize: true,
            columnDefs: 'itemGridColumnModel',
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.tooltipPlugin(true)],
            tooltipOptions: {
                showIf: isHazmat,
                onShow: onShowTooltip
            }
        };

        $scope.itemGridColumnModel = [{
            field: 'self',
            displayName: 'Weight',
            minWidth: 60,
            width: '6%',
            cellClass: 'text-center',
            referenceId: 'weightColumn',
            headerClass: 'text-center',
            cellFilter: 'materialWeight'
        }, {
            field: 'commodityClass',
            displayName: 'Class',
            minWidth: 130, width: '7%',
            cellClass: 'text-center',
            headerClass: 'text-center',
            cellFilter: 'commodityClass',
            referenceId: 'classColumn'
        }, {
            field: 'productDescription',
            displayName: 'Product Description',
            minWidth: 150, width: '18%',
            cellClass: 'text-center',
            headerClass: 'text-center',
            referenceId: 'productColumn'
        }, {
            field: 'productCode',
            displayName: 'SKU/Product Code',
            minWidth: 140, width: '14%',
            cellClass: 'text-center',
            headerClass: 'text-center',
            referenceId: 'productCodeColumn'
        }, {
            field: 'nmfc',
            displayName: 'NMFC',
            minWidth: 140,
            width: '7%',
            cellClass: 'text-center',
            headerClass: 'text-center',
            referenceId: 'nmfcColumn'
        }, {
            field: 'self',
            displayName: 'Dimensions',
            minWidth: 95,
            width: '8%',
            cellClass: 'text-center',
            headerClass: 'text-center',
            cellFilter: 'materialDimension',
            referenceId: 'dimensionsColumn'
        }, {
            field: 'quantity',
            displayName: 'Qty',
            minWidth: 35,
            width: '4%',
            cellClass: 'text-center',
            headerClass: 'text-center',
            referenceId: 'quantityColumn'
        }, {
            field: 'packageType',
            displayName: 'Packaging Type',
            minWidth: 120,
            width: '12%',
            cellClass: 'text-center',
            headerClass: 'text-center',
            cellFilter: 'packageType',
            referenceId: 'packageTypeColumn'
        }, {
            field: 'pieces',
            displayName: 'Pieces',
            minWidth: 120,
            width: '4%',
            cellClass: 'text-center',
            headerClass: 'text-center',
            referenceId: 'piecesColumn'
        }, {
            field: 'stackable',
            displayName: 'Stackable',
            minWidth: 70,
            width: '6%',
            cellTemplate: 'pages/cellTemplate/checked-cell.html',
            referenceId: 'stackableColumn'
        }, {
            field: 'hazmat',
            displayName: 'Hazmat',
            minWidth: 60,
            width: '5%',
            headerClass: 'text-center',
            showTooltip: true,
            cellTemplate: '<div class="ngCellText text-center">' +
            '<i class="icon-warning-sign" data-ng-show="row.getProperty(col.field)"></i>' +
            '</div>',
            referenceId: 'hazmatColumn'
        }, {
            field: 'hazmatClass',
            displayName: 'Hazmat Class',
            minWidth: 105, width: '11%',
            cellClass: 'text-center',
            headerClass: 'text-center',
            referenceId: 'hazmatClassColumn'
        }];

        $scope.isRevenueOverrideDisabled = function () {
            return !$scope.$root.isFieldRequired('EDIT_PLS_REVENUE');
        };

        $scope.showCarrierDetails = function (data) {
            $scope.setProgressText('Loading...');
            $scope.$broadcast('event:openCarrierDetailsForPreparedCriteria', data, $scope.wizardData.shipment.finishOrder.quoteMaterials);
        };
    }
]);

angular.module('plsApp').controller('SaveQuoteController', ['$scope', '$location', 'CostDetailsUtils', 'SavedQuotesService',
    function ($scope, $location, CostDetailsUtils, SavedQuotesService) {
        'use strict';

        $scope.showSaveQuoteDialog = false;
        $scope.showNotification = false;
        $scope.saveQuoteDialog = true;
        $scope.quoteExpired = false;
        $scope.quoteUnavailable = false;

        $scope.isCanNotBook = ($scope.$root.authData.organization && $scope.$root.authData.organization.statusReason)
                && _.contains(['CREDIT_HOLD', 'TAX_ID_EMPTY'], $scope.$root.authData.organization.statusReason);

        $scope.$on('event:saveSelectedQuoteForWizard', function (event, dialogOptions) {
            $scope.showSaveQuoteDialog = true;

            if ($scope.wizardData.customerStatus && $scope.$root.authData.plsUser) {
                $scope.isCanNotBook = _.contains(['CREDIT_HOLD', 'TAX_ID_EMPTY'], $scope.wizardData.customerStatus);
            }

            if (dialogOptions && dialogOptions.selectedCustomer) {
                $scope.wizardData.selectedCustomer = dialogOptions.selectedCustomer;
                dialogOptions = _.omit(dialogOptions, 'selectedCustomer');
            }

            if (dialogOptions && !_.isEmpty(dialogOptions)) {
                $scope.saveQuoteDialog = dialogOptions.saveQuoteDialog;
                $scope.quoteExpired = dialogOptions.expired;
                $scope.quoteUnavailable = dialogOptions.unavailable;
            }

            if ($scope.wizardData.editedQuoteId || !$scope.saveQuoteDialog) {
                $scope.quoteRef = $scope.wizardData.shipment.finishOrder.ref;
            } else {
                delete $scope.quoteRef;
            }
        });

        $scope.showRevenueAndCost = function () {
            return $scope.$root.isPlsPermissions('EDIT_PLS_REVENUE || EDIT_CARRIER_COST || VIEW_PLS_REVENUE_AND_CARRIER_COST');
        };

        $scope.showCostOnly = function () {
            return $scope.$root.isPlsPermissions('VIEW_PLS_CUSTOMER_COST') && !$scope.showRevenueAndCost();
        };

        $scope.getSelectedGuaranteed = function () {
            return CostDetailsUtils.getSelectedGuaranteed($scope.wizardData.shipment.selectedProposition, $scope.wizardData.shipment.guaranteedBy);
        };

        $scope.getSelectedCarrierGuaranteed = function () {
            return CostDetailsUtils.getSimilarCostDetailItem($scope.wizardData.shipment.selectedProposition, $scope.getSelectedGuaranteed());
        };

        $scope.getTotalCost = function () {
            return CostDetailsUtils.getTotalCost($scope.wizardData.shipment.selectedProposition, $scope.wizardData.shipment.guaranteedBy);
        };

        $scope.getCarrierTotalCost = function () {
            return CostDetailsUtils.getCarrierTotalCost($scope.wizardData.shipment.selectedProposition, $scope.wizardData.shipment.guaranteedBy);
        };

        $scope.getAccessorials = function (owner) {
            return CostDetailsUtils.getAccessorialsCost($scope.wizardData.shipment.selectedProposition, owner);
        };

        $scope.getFuelSurcharge = CostDetailsUtils.getFuelSurcharge;
        $scope.getAccessorialsRefType = CostDetailsUtils.getAccessorialsRefType;
        $scope.getNote = CostDetailsUtils.getNote;
        $scope.getGuranteedBy = CostDetailsUtils.getGuranteedBy;
        $scope.getItemCost = CostDetailsUtils.getItemCost;

        $scope.wizardData.savedQuoteDetails = {};

        $scope.saveQuote = function () {
            var selectedCustomerId = $scope.wizardData.selectedCustomer ? $scope.wizardData.selectedCustomer.id : undefined;
            var shipmentToSend = prepareShipmentForSend($scope.wizardData.shipment, selectedCustomerId,
                    $scope.wizardData.proposals);

            shipmentToSend.quoteId = $scope.wizardData.editedQuoteId;
            shipmentToSend.finishOrder.ref = $scope.quoteRef;

            SavedQuotesService.save({customerId: selectedCustomerId}, shipmentToSend, function (data) {
                $scope.wizardData.savedQuoteDetails[$scope.wizardData.shipment.selectedProposition.guid] = {
                    quoteId: data.quoteId,
                    quoteRef: $scope.quoteRef
                };
                delete $scope.wizardData.editedQuoteId;
                delete $scope.quoteRef;
                $scope.showSaveQuoteDialog = false;
            }, function (data, status) {
                $scope.$root.$emit('event:application-error', 'Save quote failed!', 'Can\'t save quote ' + status);
            });
        };

        $scope.buildOrder = function () {
            $scope.showSaveQuoteDialog = false;

            if ($scope.quoteExpired === false && $scope.quoteUnavailable === false) {
                $scope.showSaveQuoteDialog = false;
                $scope.$root.ignoreLocationChange();
                $location.url('/quotes/quote').search({savedQuoteId: $scope.wizardData.shipment.quoteId, stepName: 'build_order'});
            } else {
                $scope.showNotification = true;
            }
        };

        $scope.closeSaveQuoteDialog = function () {
            $scope.showSaveQuoteDialog = false;
        };

        $scope.closeNotification = function () {
            $scope.showNotification = false;
            $location.url('/quotes/quote').search({savedQuoteId: $scope.wizardData.shipment.quoteId, stepName: 'rate_quote'});
        };
    }
]);

angular.module('plsApp').controller('BuildOrderCtrl', ['$scope', 'ProductService', 'NgGridPluginFactory', 'ShipmentUtils',
    'ProductTotalsService', 'CustomerInternalNoteService',
    function ($scope, ProductService, NgGridPluginFactory, ShipmentUtils, ProductTotalsService, CustomerInternalNoteService) {
        'use strict';

        $scope.wizardData.origForm = {};
        $scope.wizardData.destForm = {};
        $scope.wizardData.locationForm = {};
        $scope.wizardData.billToForm = {};
        $scope.requoteRequired = false;

        if ($scope.wizardData.selectedCustomer && $scope.wizardData.selectedCustomer.id) {
            CustomerInternalNoteService.get({
                customerId: $scope.wizardData.selectedCustomer.id
            }, function (data) {
                $scope.wizardData.selectedCustomer.internalNote = data.data;
            });
        }

        //set Estimation Delivery date based on selected proposal
        $scope.wizardData.shipment.finishOrder.estimatedDelivery = $scope.wizardData.shipment.selectedProposition.estimatedTransitDate;

        $scope.$on('event:addressAltered', function (event, address, isOrigin) {
            ShipmentUtils.addAddressNotificationsToLoadNotificationsWithoutDuplicates($scope.wizardData.shipment.finishOrder.shipmentNotifications,
                address.shipmentNotifications, isOrigin);
        });

        var checkAddress = function (address) {
            if (!address.addressName) {
                address.addressName = '';
            }

            if (!address.phone) {
                address.phone = {};
            }

            if (!address.fax) {
                address.fax = {type: 'FAX'};
            }
        };

        var setupCustomsBrokerFlag = function (origin, destination) {
            if (origin && origin.zip && origin.zip.country
                    && destination && destination.zip && destination.zip.country) {
                $scope.wizardData.showCustomsBroker = origin.zip.country.id !== destination.zip.country.id;
            }
        };

        $scope.canNextStep = function () {
            var invalidForms = $scope.wizardData.origForm.invalid
                    || $scope.wizardData.destForm.invalid || $scope.itemGridForm.$invalid
                    || $scope.wizardData.billToForm.invalid || $scope.wizardData.locationForm.invalid
                    || $scope.customsBrokerForm.$invalid || $scope.wizardData.billToForm.$invalid || $scope.requoteRequired
                    || !($scope.wizardData.shipment.freightBillPayTo && $scope.wizardData.shipment.freightBillPayTo.company);

            return !invalidForms;
        };

        $scope.$watch('wizardData.shipment.billTo', function (newValue) {
            if (newValue) {
                $scope.wizardData.shipment.customsBroker = {
                    name: newValue.customsBroker,
                    phone: newValue.brokerPhone
                };
            }
        });

        $scope.initStep = function () {
            var originAddress = $scope.wizardData.shipment.originDetails.address;

            if (!originAddress
                    || !_.isEqual(_.omit(originAddress.zip, 'prefCity'), _.omit($scope.wizardData.shipment.originDetails.zip, 'prefCity'))) {
                originAddress = {};
                originAddress.addressName = '';
                originAddress.phone = {};
                originAddress.fax = {type: 'FAX'};
                originAddress.zip = $scope.wizardData.shipment.originDetails.zip;
                $scope.wizardData.shipment.originDetails.address = originAddress;
                $scope.wizardData.shipment.finishOrder.pickupWindowFrom = undefined;
                $scope.wizardData.shipment.finishOrder.pickupWindowTo = undefined;
            }

            var destinationAddress = $scope.wizardData.shipment.destinationDetails.address;

            if (!destinationAddress
                || !_.isEqual(_.omit(destinationAddress.zip, 'prefCity'), _.omit($scope.wizardData.shipment.destinationDetails.zip, 'prefCity'))) {
                destinationAddress = {};
                destinationAddress.addressName = '';
                destinationAddress.zip = $scope.wizardData.shipment.destinationDetails.zip;
                destinationAddress.phone = {};
                destinationAddress.fax = {type: 'FAX'};
                $scope.wizardData.shipment.destinationDetails.address = destinationAddress;
            }

            checkAddress(originAddress);
            checkAddress(destinationAddress);
            setupCustomsBrokerFlag(originAddress, destinationAddress);

            ProductService.list({customerId: $scope.wizardData.selectedCustomer.id}, function (data) {
                $scope.products = data;

                _.each($scope.wizardData.shipment.finishOrder.quoteMaterials, function (material) {
                    if ((material.product && material.product.id) || material.productId) {
                        var productId = (material.product && material.product.id) ? material.product.id : material.productId;
                        var matchedProduct = _.findWhere($scope.products, {id: productId});

                        if (matchedProduct) {
                            material.product = matchedProduct;
                        }
                    } else {
                        var matchedProduct2 = _.findWhere($scope.products, {
                            commodityClass: material.commodityClass,
                            hazmat: material.hazmat,
                            description: material.productDescription
                        });

                        if (matchedProduct2) {
                            material.product = matchedProduct2;
                        }
                    }
                });

            });
        };

        $scope.filteredProducts = function (quoteMaterial) {
            var filteredProducts = [];

            angular.forEach($scope.products, function (product) {
                if (product.commodityClass === quoteMaterial.commodityClass && product.hazmat === Boolean(quoteMaterial.hazmat).valueOf()) {
                    filteredProducts.push(product);
                }
            });

            return filteredProducts;
        };

        $scope.back = function () {
            delete $scope.wizardData.shipment.quoteId;
            delete $scope.wizardData.shipment.freightBillPayTo;
            $scope.wizardData.step = $scope.wizardData.steps.prev();
        };

        function shouldDeleteFax(fax) {
            return fax && (!fax.countryCode || !fax.areaCode || !fax.number);
        }

        $scope.next = function () {
            if (shouldDeleteFax($scope.wizardData.shipment.originDetails.address.fax)) {
                delete $scope.wizardData.shipment.originDetails.address.fax;
            }

            if (shouldDeleteFax($scope.wizardData.shipment.destinationDetails.address.fax)) {
                delete $scope.wizardData.shipment.destinationDetails.address.fax;
            }

            $scope.wizardData.step = $scope.wizardData.steps.next();
        };

        $scope.checkQuoteMaterialsForBuildOrderStep = function () {
            var materialsProductString = '';

            angular.forEach($scope.wizardData.shipment.finishOrder.quoteMaterials, function (material) {
                if (material !== undefined && material.product !== undefined && angular.isObject(material.product)) {
                    materialsProductString += '_' + material.product.description;
                }
            });

            return materialsProductString;
        };

        $scope.$watch('checkQuoteMaterialsForBuildOrderStep()', function (newValue) {
            if (newValue) {
                angular.forEach($scope.wizardData.shipment.finishOrder.quoteMaterials, function (material) {
                    if (material !== undefined && material.product !== undefined && angular.isObject(material.product)
                            && material.productDescription !== material.product.description) {
                        material.productDescription = material.product.description;
                        material.productCode = material.product.productCode;
                        material.packageType = _.isUndefined(material.product.packageType) ? material.packageType : material.product.packageType;
                        material.productId = material.product.id;
                        material.nmfc = material.product.nmfc;
                        material.unNum = material.product.hazmatUnNumber;
                        material.packingGroup = material.product.hazmatPackingGroup;
                        material.hazmat = material.product.hazmat;
                        material.hazmatClass = material.product.hazmatClass;
                        material.emergencyResponseCompany = material.product.hazmatEmergencyCompany;
                        material.emergencyResponseContractNumber = material.product.hazmatEmergencyContract;
                        material.emergencyResponseInstructions = material.product.hazmatInstructions;

                        if (material.product.hazmatEmergencyPhone) {
                            material.emergencyResponsePhoneCountryCode = material.product.hazmatEmergencyPhone.countryCode;
                            material.emergencyResponsePhoneAreaCode = material.product.hazmatEmergencyPhone.areaCode;
                            material.emergencyResponsePhone = material.product.hazmatEmergencyPhone.number;
                            material.emergencyResponsePhoneExtension = material.product.hazmatEmergencyPhone.extension;
                        }
                    }
                });

                $scope.quoteMaterialsCopy = angular.copy($scope.wizardData.shipment.finishOrder.quoteMaterials);
            }
        });

        $scope.products = [];

        $scope.quoteMaterialsCopy = angular.copy($scope.wizardData.shipment.finishOrder.quoteMaterials);
        $scope.hazmatInfo = {};

        function isHazmat(entity) {
            return entity.hazmat;
        }

        function onShowTooltip(row) {
            if (row.entity.id || row.entity.productId) {
                $scope.hazmatInfo.exist = true;
                $scope.hazmatInfo.company = row.entity.emergencyResponseCompany;

                $scope.hazmatInfo.phone = {
                    emergencyResponsePhoneCountryCode: row.entity.emergencyResponsePhoneCountryCode,
                    emergencyResponsePhoneAreaCode: row.entity.emergencyResponsePhoneAreaCode,
                    emergencyResponsePhone: row.entity.emergencyResponsePhone,
                    emergencyResponsePhoneExtension: row.entity.emergencyResponsePhoneExtension
                };

                $scope.hazmatInfo.contractNum = row.entity.emergencyResponseContractNumber;
                $scope.hazmatInfo.unNum = row.entity.unNum;
                $scope.hazmatInfo.packingGroup = row.entity.packingGroup;
            } else {
                $scope.hazmatInfo.exist = false;
            }

            $scope.itemsGridOptions.tooltipOptions.url = 'pages/content/quotes/hazmat-info-tooltip.html';
        }

        $scope.openCreateProductDialog = function (productGridIndex) {
            var transferObject = {};
            transferObject.customerId = $scope.wizardData.selectedCustomer.id;
            transferObject.customerName = $scope.wizardData.selectedCustomer.name;
            $scope.productGridIndex = productGridIndex;
            $scope.$root.$broadcast('event:showAddEditProduct', transferObject);
        };

        $scope.setRequoteRequired = function () {
            $scope.requoteRequired = true;
        };

        $scope.$on('event:newProductAdded', function (event, productId) {
            if (productId) {
                ProductService.info({productId: productId, customerId: $scope.wizardData.selectedCustomer.id}, function (data) {
                    var product = $scope.wizardData.shipment.finishOrder.quoteMaterials[$scope.productGridIndex];
                    product.productDescription = data.description;
                    product.productId = data.id;

                    if (product.commodityClass !== data.commodityClass || product.hazmat !== data.hazmat) {
                        $scope.setRequoteRequired();
                    }

                    product.commodityClass = data.commodityClass;
                    if (!_.isUndefined(data.weight)) {
                        product.weight = data.weight;
                    }

                    product.productCode = data.productCode;
                    product.productDescription = data.description;
                    product.packageType = data.packageType;
                    product.stackable = data.stackable || false;
                    product.hazmat = data.hazmat || false;
                    product.hazmatClass = data.hazmatClass;
                    product.packingGroup = data.hazmatPackingGroup;
                    product.unNum = data.hazmatUnNumber;
                    product.nmfc = data.nmfc;

                    if (data.nmfc && data.nmfcSubNum) {
                        product.nmfc = product.nmfc + '-' + data.nmfcSubNum;
                    }

                    product.emergencyResponseCompany = data.hazmatEmergencyCompany;
                    product.emergencyResponseContractNumber = data.hazmatEmergencyContract;
                    product.emergencyResponseInstructions = data.hazmatInstructions;

                    if (data.hazmatEmergencyPhone) {
                        product.emergencyResponsePhoneCountryCode = data.hazmatEmergencyPhone.countryCode;
                        product.emergencyResponsePhoneAreaCode = data.hazmatEmergencyPhone.areaCode;
                        product.emergencyResponsePhone = data.hazmatEmergencyPhone.number;
                        product.emergencyResponsePhoneExtension = data.hazmatEmergencyPhone.extension;
                    }

                    product.product = data;

                    if ($scope.products) {
                        $scope.products.unshift(data);
                        if ('Explorer' === $scope.$root.browserDetect.browser && $scope.$root.browserDetect.version < 11) {
                            // Dirty huck for IE < v.11 to re-draw drop down
                            setTimeout(function () {
                                angular.forEach($("select"), function (currSelect) {
                                    currSelect.options[currSelect.selectedIndex].text += " ";
                                });
                            }, 10);
                        }
                    }
                    $scope.quoteMaterialsCopy = angular.copy($scope.wizardData.shipment.finishOrder.quoteMaterials);
                });
            }
        });

        $scope.resetProductDescription = function (index) {
            $scope.wizardData.shipment.finishOrder.quoteMaterials[index].productDescription = '';
            delete $scope.wizardData.shipment.finishOrder.quoteMaterials[index].productId;
            delete $scope.wizardData.shipment.finishOrder.quoteMaterials[index].product;
            $scope.quoteMaterialsCopy = angular.copy($scope.wizardData.shipment.finishOrder.quoteMaterials);
            $scope.setRequoteRequired();
        };

        $scope.updateProductOnSelectCarrier = function () {
            $scope.quoteMaterialsCopy = angular.copy($scope.wizardData.shipment.finishOrder.quoteMaterials);
            $scope.setRequoteRequired();
        };

        $scope.reQuote = function () {
            $scope.requoteRequired = false;
            $scope.back();
        };

        $scope.itemsGridOptions = {
            data: 'quoteMaterialsCopy',
            enableRowSelection: false,
            enableColumnResize: true,
            enableSorting: false,
            columnDefs: [{
                field: 'self', displayName: 'Weight(Lbs)', width: '9%', cellClass: 'text-center', headerClass: 'text-center',
                cellFilter: 'materialWeight', cellTemplate: '<input required type="text" ' +
                    'data-ng-change="updateProductOnSelectCarrier(); calculateTotals();"' +
                    'class="span8" data-ng-model="wizardData.shipment.finishOrder.quoteMaterials[row.rowIndex].weight">'
            }, {
                field: 'commodityClass',
                displayName: 'Class',
                minWidth: 130,
                width: '9%',
                cellClass: 'text-center',
                headerClass: 'text-center',
                cellFilter: 'commodityClass',
                cellTemplate: '<select required type="text" data-ng-change="resetProductDescription(row.rowIndex);" class="span8" ' +
                'data-ng-options="c as c | commodityClass for c in rateQuoteDictionary.classes" required tabindex="4" ' +
                'data-ng-model="wizardData.shipment.finishOrder.quoteMaterials[row.rowIndex].commodityClass"></select>'
            }, {
                field: 'productDescription',
                displayName: 'Product Description',
                width: '17%',
                cellClass: 'text-center',
                //Do not remove <div data-ng-if="true"> it's needed for proper validation
                headerClass: 'text-center',
                cellTemplate: '<div data-ng-if="true"><select required class="span10" ' +
                'data-ng-options="product as product.description for product in filteredProducts(row.entity)" ' +
                'data-ng-model="wizardData.shipment.finishOrder.quoteMaterials[row.rowIndex].product"></select>' +
                '<button class=\"btn\" style=\"margin-left: 3px; margin-bottom: 7px;\" ' +
                'data-ng-disabled=\"!$root.isFieldRequired(\'ADD_EDIT_PRODUCT\')" ' +
                'data-ng-click=\"openCreateProductDialog(row.rowIndex)\">+</button></div>'
            }, {
                field: 'productCode',
                displayName: 'SKU/Product Code',
                width: '10%', cellClass: 'text-center',
                headerClass: 'text-center'
            }, {
                field: 'nmfc', displayName: 'NMFC', width: '7%', cellClass: 'text-center', headerClass: 'text-center',
                //Do not remove <div data-ng-if="true"> it's needed for proper validation
                cellTemplate: '<div data-ng-if="true"><input class="span8" type="text"' +
                'data-ng-model="wizardData.shipment.finishOrder.quoteMaterials[row.rowIndex].nmfc" ng-maxlength="30"></div>'
            }, {
                field: 'self', displayName: 'Dimensions', width: '10%', cellClass: 'text-center', headerClass: 'text-center',
                cellFilter: 'materialDimension'
            }, {
                field: 'quantity', displayName: 'Qty', width: '9%', cellClass: 'text-center', headerClass: 'text-center',
                //Do not remove <div data-ng-if="true"> it's needed for proper validation
                cellTemplate: '<div data-ng-if="true"><input ' +
                'data-ng-required="isQtyRequired(wizardData.shipment.finishOrder.quoteMaterials[row.rowIndex].packageType)" ' +
                'class="span8" data-pls-number  type="text" ' +
                'data-ng-model="wizardData.shipment.finishOrder.quoteMaterials[row.rowIndex].quantity" ' +
                'maxlength="6" data-forbid-zero="true" data-ng-pattern="/^\\d+$/" ' +
                'data-ng-change="checkRequoteRequiredForQty(row.rowIndex); calculateTotals();"></div>'
            }, {
                field: 'packageType', displayName: 'Packaging Type', width: '13%', cellClass: 'text-center', headerClass: 'text-center',
                //Do not remove <div data-ng-if="true"> it's needed for proper validation
                cellTemplate: '<div data-ng-if="true"><select required class="span10" ' +
                'data-ng-model="wizardData.shipment.finishOrder.quoteMaterials[row.rowIndex].packageType" ' +
                'data-ng-options="packageType.code as packageType.label for packageType in rateQuoteDictionary.packageTypes">' +
                '<option value=""></option></select></div>'
            }, {
                field: 'pieces', displayName: 'Pieces', width: '9%', cellClass: 'text-center', headerClass: 'text-center',
                cellTemplate: '<div data-ng-if="true"><input class="span8" type="text" ' +
                'data-ng-model="wizardData.shipment.finishOrder.quoteMaterials[row.rowIndex].pieces" ' +
                'data-pls-number data-ng-pattern="/^\\d+$/" maxlength="6" ' +
                'data-ng-change="requoteRequiredPiecesChange(); calculateTotals();"></div>'
            }, {
                field: 'stackable', displayName: 'Stackable', width: '5%', headerClass: 'text-center',
                cellTemplate: 'pages/cellTemplate/checked-cell.html'
            }, {
                field: 'hazmat', displayName: 'Hazmat', width: '5%', headerClass: 'text-center',
                showTooltip: true, cellTemplate: '<div class="ngCellText text-center">' +
                '<i class="icon-warning-sign" data-ng-show="row.getProperty(col.field)"></i></div>'
            }, {
                field: 'hazmatClass', displayName: 'Hazmat Class', width: '5%', cellClass: 'text-center',
                headerClass: 'text-center'
            }],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.tooltipPlugin(true)],
            tooltipOptions: {
                showIf: isHazmat,
                onShow: onShowTooltip
            }
        };

        $scope.calculateTotals = function () {
            $scope.totals = ProductTotalsService.calculateTotals($scope.wizardData.shipment.finishOrder.quoteMaterials);
        };

        $scope.calculateTotals();

        $scope.$watch('wizardData.shipment.originDetails.address', function (newAddress, oldAddress) {
            if (newAddress && !_.isEqual(newAddress, oldAddress)) {
                $scope.wizardData.shipment.finishOrder.pickupWindowFrom = newAddress.pickupWindowFrom;
                $scope.wizardData.shipment.finishOrder.pickupWindowTo = newAddress.pickupWindowTo;
                if (newAddress !== oldAddress) {
                    ShipmentUtils.addAddressNotificationsToLoadNotificationsWithoutDuplicates(
                            $scope.wizardData.shipment.finishOrder.shipmentNotifications,
                            newAddress.shipmentNotifications, true);
                }
            }
        }, true);

        $scope.isQtyRequired = function (packageType) {
            return packageType === 'PLT';
        };

        $scope.$watch('wizardData.shipment.destinationDetails.address', function (newAddress, oldAddress) {
            if (newAddress && !_.isEqual(newAddress, oldAddress)) {
                $scope.wizardData.shipment.finishOrder.deliveryWindowFrom = newAddress.deliveryWindowFrom;
                $scope.wizardData.shipment.finishOrder.deliveryWindowTo = newAddress.deliveryWindowTo;
                if (newAddress !== oldAddress) {
                ShipmentUtils.addAddressNotificationsToLoadNotificationsWithoutDuplicates(
                    $scope.wizardData.shipment.finishOrder.shipmentNotifications,
                    newAddress.shipmentNotifications, false);
                }
            }
        }, true);

        $scope.checkRequoteRequiredForQty = function (index) {
            if ($scope.wizardData.shipment.finishOrder.quoteMaterials[index].packageType === 'PLT') {
                $scope.setRequoteRequired();
            }
        };

        $scope.requoteRequiredPiecesChange = function () {
            if (_.contains($scope.wizardData.shipment.destinationDetails.accessorials, 'SSD') ||
                    _.contains($scope.wizardData.shipment.originDetails.accessorials, 'SSD')) {
                $scope.setRequoteRequired();
            }
        };
    }
]);

angular.module('plsApp').controller('FinishOrderCtrl', ['$scope', 'DateTimeUtils', 'isRequiredField',
    function ($scope, DateTimeUtils, isRequiredField) {
        'use strict';

        $scope.invalidIdentifier = {};

        function isInvalidIdentifiers() {
            return _.some($scope.invalidIdentifier);
        }

        $scope.back = function () {
            $scope.wizardData.step = $scope.wizardData.steps.prev();
        };

        $scope.showPickupMessage = function () {
            $scope.pickUpWindowOpen = true;
        };

        $scope.next = function () {
            var windowDiff = DateTimeUtils.pickupWindowDifference($scope.wizardData.shipment.finishOrder.pickupWindowFrom,
                    $scope.wizardData.shipment.finishOrder.pickupWindowTo);

            var minAllowedPickupWindowDifference = 0.5; // It's not allowed to have this difference less than half an hour

            if (windowDiff !== undefined && windowDiff < minAllowedPickupWindowDifference) {
                $scope.showPickupMessage();
                return;
            }

            if (windowDiff === undefined || isInvalidIdentifiers()) {
                $scope.$root.$emit('event:application-error', 'Shipment validation failed!', 'Required fields are not specified.');
                return;
            }

            $scope.wizardData.step = $scope.wizardData.steps.next();
        };
    }
]);

angular.module('plsApp').controller('FinishQuoteCtrl', ['$scope', '$route', 'urlConfig', '$location', '$timeout', 'ShipmentSavingService',
    'ShipmentsProposalService', 'DateTimeUtils', 'ShipmentUtils', 'ShipmentDocumentEmailService','ShipmentDocumentService',
    function ($scope, $route, urlConfig, $location, $timeout, ShipmentSavingService, ShipmentsProposalService, DateTimeUtils, ShipmentUtils,
              ShipmentDocumentEmailService, ShipmentDocumentService) {
        'use strict';

        $scope.bolOptions = {
            width: '100%',
            height: '600px',
            pdfLocation: null,
            hidePdf: false
        };

        $scope.confirmedTermsOfAgreement = $scope.wizardData.isCopyShipment;
        $scope.showTermsOfAgreement = false;
        $scope.showSendMailDialog = false;
        $scope.shipmentDetailsOption = {};
        $scope.emailOptions = {};
        $scope.storedBolId = null;

        $scope.toaOptions = {
            width: '100%',
            height: '380px',
            pdfLocation: urlConfig.shipment + '/customer/shipmentdocs/termOfAgreement'
        };

        function prepareBOL() {
            var shipmentToSend = prepareShipmentForSend($scope.wizardData.shipment, $scope.wizardData.selectedCustomer.id,
                    $scope.wizardData.proposals);

            ShipmentDocumentService.prepareBolForShipment({
                customerId: $scope.wizardData.selectedCustomer.id,
                rateQuote: true
            }, shipmentToSend, function (data) {
                if (data && data.value) {
                    $scope.bolOptions.pdfLocation = urlConfig.shipment + '/customer/shipmentdocs/' + data.value;
                    $scope.storedBolId = data.value;
                }
            });
        }

        $scope.initStep = function () {
            prepareBOL();
        };

        $scope.previousStep = function () {
            $scope.wizardData.step = $scope.wizardData.steps.prev();
        };

        $scope.hidePdf = function () {
            $scope.bolOptions.hidePdf = true;
            $scope.bolOptions.pdfLocation = undefined;
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

        function showShipmentDetails(shipment) {
            $scope.shipmentDetailsOption = {
                shipmentId: shipment.id,
                customerId: $scope.wizardData.selectedCustomer.id,
                bol: shipment.bolNumber,
                closeHandler: function () {
                    $scope.bolOptions.hidePdf = false;
                    $scope.progressPanelOptions.showPanel = true;
                    $scope.$root.ignoreLocationChange();
                    $location.url('/quotes/quote');
                    $timeout(function () {
                        $scope.wizardData.shipment = angular.copy($scope.wizardData.emptyShipment);
                        $scope.wizardData.step = $scope.wizardData.steps.first();

                        if (!$scope.authData.assignedOrganization) {
                            $scope.wizardData.selectedCustomer = {
                                id: undefined,
                                name: undefined
                            };
                        }

                        $scope.progressPanelOptions.showPanel = false;
                    });
                },
                hideRateQuoteButton: $scope.authData.plsUser
            };

            $scope.$broadcast('event:showShipmentDetails', $scope.shipmentDetailsOption);
        }

        function showPaymentDialog(shipment) {
            $scope.$root.$emit('event:operation-success', 'Create quote', 'Quote was successfully created.<br/>Load ID: ' + shipment.id);
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
            $scope.hidePdf();
            showPaymentDialog(shipment);
        }

        function sendEmailWithErrorDetails() {
            var wizardCopy = _.clone($scope.wizardData);
            delete wizardCopy.steps;
            ShipmentDocumentEmailService.emailDoc({
                recipients: 'aleshchenko@plslogistics.com',
                subject: 'Wrong Bill To',
                content: 'Quotes: ' + JSON.stringify(wizardCopy),
                loadId: -1
            });
        }

        $scope.bookIt = function () {
            if ($scope.wizardData.shipment.status === 'OPEN'
                    && $scope.wizardData.shipment.selectedProposition.carrier
                    && $scope.wizardData.shipment.selectedProposition.costDetailItems
                    && $scope.wizardData.shipment.selectedProposition.costDetailItems.length) {
                $scope.wizardData.shipment.status = $scope.$root.isFieldRequired('ALLOW_SHIPMENT_AUTO_DISPATCH') ? 'DISPATCHED' : 'BOOKED';
            }

            if (!ShipmentUtils.isCreditLimitValid($scope.wizardData.shipment)) {
                return;
            }

            var finishOrder = $scope.wizardData.shipment.finishOrder;

            if (finishOrder.pickupDate && finishOrder.pickupWindowFrom) {
                $scope.showPickupMessageIfNeeded(finishOrder.pickupDate, finishOrder.pickupWindowFrom);
            }

            if (!$scope.confirmedTermsOfAgreement) {
                $scope.openTermsOfAgreementDialog();
                return;
            }

            var shipmentToSend = prepareShipmentForSend($scope.wizardData.shipment, $scope.wizardData.selectedCustomer.id,
                    $scope.wizardData.proposals);

            if (shipmentToSend.billTo.paymentMethod === 'PREPAID_ONLY') {
                shipmentToSend.status = 'PENDING_PAYMENT';
            }

            ShipmentSavingService.bookIt({
                customerId: $scope.wizardData.selectedCustomer.id,
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

        $scope.$on('event:closeAndRedirect', function (event, url, params) {
            var currentUrl = $location.url();

            if (currentUrl.indexOf(params.shipmentId) !== -1 && currentUrl.indexOf(params.stepName) !== -1) {
                $scope.showTermsOfAgreement = true;
                $route.reload();
            } else {
                $location.url(url).search(params);
            }
        });

        $scope.openTermsOfAgreementDialog = function () {
            $scope.bolOptions.hidePdf = true;
            $scope.showTermsOfAgreement = true;
        };

        $scope.closeTermsOfAgreementDialog = function () {
            $scope.showTermsOfAgreement = false;
            $scope.bolOptions.hidePdf = false;
        };

        $scope.confirmTermsOfAgreement = function () {
            $scope.confirmedTermsOfAgreement = true;
            $scope.closeTermsOfAgreementDialog();
        };

        $scope.showTerminalInformation = function () {
            if ($scope.wizardData.shipment.selectedProposition) {
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
                    },
                    profileDetailId: $scope.wizardData.shipment.selectedProposition.pricingProfileId
                };

                $scope.bolOptions.hidePdf = true;
                $scope.setProgressText('Loading...');

                ShipmentsProposalService.findTerminalInformation({}, terminalCriteria, function (data) {
                    $scope.$broadcast('event:openTerminalInfoForPreparedCriteria', data);
                });
            }
        };

        $scope.$on('event:closeTerminalInfoDialog', function () {
            $scope.bolOptions.hidePdf = false;
        });
    }
]);
