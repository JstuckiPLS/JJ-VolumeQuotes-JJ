angular.module('manualBol.services', ['ngResource', 'ngRoute']);
angular.module('manualBol.utils', ['manualBol.services']);
angular.module('manualBol.controllers', ['manualBol.utils', 'plsApp.utils', 'quotes.services']);

angular.module('manualBol', ['manualBol.controllers']).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider
                .when('/manual-bol', {redirectTo: '/manual-bol/general-information'})
                .when('/manual-bol/general-information', {
                    templateUrl: 'pages/content/manual-bol/manual-bol-general-information.html',
                    controller: 'ManualBolGeneralInformationController'
                })
                .when('/manual-bol/general-information/:id', {
                    templateUrl: 'pages/content/manual-bol/manual-bol-general-information.html',
                    controller: 'ManualBolGeneralInformationController'
                })
                .when('/manual-bol/addresses', {
                    templateUrl: 'pages/content/manual-bol/manual-bol-addresses.html',
                    controller: 'ManualBolAddressesController'
                })
                .when('/manual-bol/addresses/:id', {
                    templateUrl: 'pages/content/manual-bol/manual-bol-addresses.html',
                    controller: 'ManualBolAddressesController'
                })
                .when('/manual-bol/details', {
                    templateUrl: 'pages/content/manual-bol/manual-bol-details.html',
                    controller: 'ManualBolDetailsController'
                })
                .when('/manual-bol/details/:id', {
                    templateUrl: 'pages/content/manual-bol/manual-bol-details.html',
                    controller: 'ManualBolDetailsController'
                })
                .when('/manual-bol/docs', {
                    templateUrl: 'pages/content/manual-bol/manual-bol-docs.html',
                    controller: 'ManualBolDocsController'
                })
                .when('/manual-bol/docs/:id', {
                    templateUrl: 'pages/content/manual-bol/manual-bol-docs.html',
                    controller: 'ManualBolDocsController'
                });
    }
]);

angular.module('manualBol').run(['$rootScope', '$location', '$routeParams', function ($rootScope, $location, $routeParams) {
    $rootScope.navigateTo = function (page) {
        if ($location.path() === page) {
            return;
        }

        if ($routeParams.id) {
            page = page.concat('/', $routeParams.id);
        }

        $rootScope.ignoreLocationChange();
        $location.path(page);
    };
}]);

angular.module('manualBol.utils').factory('manualBolModel', ['$rootScope', '$location', '$http', '$routeParams', 'ManualBolService',
    function ($rootScope, $location, $http, $routeParams, ManualBolService) {
        var emptyShipment = {
            finishOrder: {
                jobNumbers: [],
                quoteMaterials: [],
                requestedBy: "",
                pickupDate: new Date()
            },
            selectedProposition: {},
            originDetails: {},
            destinationDetails: {},
            status: 'CUSTOMER_TRUCK'
        };

        function removeUncomparableFields(shipment) {
            var noFieldsShipment = angular.copy(shipment);
            noFieldsShipment.finishOrder = _.omit(noFieldsShipment.finishOrder, ['pickupDate']);
            return _.omit(noFieldsShipment, ['selectedProposition']);
        }

        var model = {
            steps: [{title: 'General information', label: 'general-information'},
                {title: 'Addresses', label: 'addresses'},
                {title: 'Details', label: 'details'},
                {title: 'Docs', label: 'docs'}],
            shipment: angular.copy(emptyShipment),
            emptyShipment: removeUncomparableFields(emptyShipment),
            carrierTuple: undefined,
            addresses: undefined
        };


        model.removeUncomparableFields = removeUncomparableFields;

        model.init = function () {
            model.shipment = angular.copy(emptyShipment);
            model.emptyShipment = removeUncomparableFields(emptyShipment);
            model.carrierTuple = undefined;
            model.selectedCustomer = undefined;
            model.addresses = undefined;
        };

        function prepareAddress(addressDetails, isOrigin) {
            var address = isOrigin ? model.shipment.origin : model.shipment.destination;

            if (!_.isUndefined(address) && !_.isUndefined(address.id)) {
                addressDetails.id = address.id;
                addressDetails.version = address.version;
            } else {
                addressDetails = _.omit(addressDetails, 'id', 'version');
            }
            return addressDetails;
        }

        model.prepareBolForSaving = function (id) {
            return {
                id: id,
                status: model.shipment.status,
                organizationId: model.selectedCustomer.id,
                carrier: model.shipment.selectedProposition.carrier,
                bol: model.shipment.bolNumber,
                pro: model.shipment.proNumber,
                po: model.shipment.finishOrder.poNumber,
                pu: model.shipment.finishOrder.puNumber,
                ref: model.shipment.finishOrder.ref,
                cargoValue: model.shipment.cargoValue,
                glNumber: model.shipment.finishOrder.glNumber,
                opBolNumber: model.shipment.finishOrder.opBolNumber,
                partNumber: model.shipment.finishOrder.partNumber,
                jobNumbers: model.shipment.finishOrder.jobNumbers,
                soNumber: model.shipment.finishOrder.soNumber,
                trailer: model.shipment.finishOrder.trailerNumber,
                billTo: model.shipment.billTo,
                location: model.shipment.location,
                origin: prepareAddress(model.shipment.originDetails.address, true),
                destination: prepareAddress(model.shipment.destinationDetails.address, false),
                pickupDate: model.shipment.finishOrder.pickupDate,
                pickupNotes: model.shipment.finishOrder.pickupNotes,
                requestedBy: model.shipment.finishOrder.requestedBy,
                shippingNotes: model.shipment.finishOrder.shippingLabelNotes,
                deliveryNotes: model.shipment.finishOrder.deliveryNotes,
                paymentTerms: model.shipment.paymentTerms,
                shipmentDirection: model.shipment.shipmentDirection,
                pickupWindowFrom: model.shipment.finishOrder.pickupWindowFrom,
                pickupWindowTo: model.shipment.finishOrder.pickupWindowTo,
                deliveryWindowFrom: model.shipment.finishOrder.deliveryWindowFrom,
                deliveryWindowTo: model.shipment.finishOrder.deliveryWindowTo,
                materials: model.shipment.finishOrder.quoteMaterials,
                freightBillPayTo: model.shipment.freightBillPayTo,
                customsBroker: model.shipment.customsBroker.name,
                customsBrokerPhone: model.shipment.customsBroker.phone
            };
        };

        model.prepareBolForViewing = function (bol) {
            var shipment = {
                id: bol.id,
                bolNumber: bol.bol,
                proNumber: bol.pro,
                status: bol.status,
                organizationId: bol.organizationId,
                location: bol.location,
                billTo: bol.billTo,
                customerName: bol.customerName,
                paymentTerms: bol.paymentTerms ? bol.paymentTerms : "COLLECT",
                shipmentDirection: bol.shipmentDirection ? bol.shipmentDirection : "O",
                originDetails: {
                    address: bol.origin
                },
                destinationDetails: {
                    address: bol.destination
                },
                origin: {
                    id: bol.origin.id,
                    version: bol.origin.version
                },
                destination: {
                    id: bol.destination.id,
                    version: bol.destination.version
                },
                selectedProposition: {
                    carrier: bol.carrier
                },
                cargoValue: bol.cargoValue,
                finishOrder: {
                    ref: bol.ref,
                    soNumber: bol.soNumber,
                    poNumber: bol.po,
                    puNumber: bol.pu,
                    trailerNumber: bol.trailer,
                    glNumber: bol.glNumber,
                    opBolNumber: bol.opBolNumber,
                    partNumber: bol.partNumber,
                    jobNumbers: bol.jobNumbers,
                    pickupNotes: bol.pickupNotes,
                    requestedBy: bol.requestedBy,
                    pickupDate: bol.pickupDate,
                    deliveryNotes: bol.deliveryNotes,
                    shippingLabelNotes: bol.shippingNotes,
                    quoteMaterials: bol.materials,
                    pickupWindowFrom: bol.pickupWindowFrom,
                    pickupWindowTo: bol.pickupWindowTo,
                    deliveryWindowFrom: bol.deliveryWindowFrom,
                    deliveryWindowTo: bol.deliveryWindowTo
                },
                isManualBol: true,
                freightBillPayTo: bol.freightBillPayTo,
                customsBroker: {
                    name: bol.customsBroker,
                    phone: bol.customsBrokerPhone
                }
            };

            //prepare addresses for correct work with pls-address-directive
            shipment.originDetails.address.country = bol.origin.zip.country;
            shipment.destinationDetails.address.country = bol.destination.zip.country;
            shipment.originDetails.address.pickupWindowFrom = bol.pickupWindowFrom;
            shipment.originDetails.address.pickupWindowTo = bol.pickupWindowTo;
            shipment.destinationDetails.address.deliveryWindowFrom = bol.deliveryWindowFrom;
            shipment.destinationDetails.address.deliveryWindowTo = bol.deliveryWindowTo;
            shipment.originDetails.address = _.omit(shipment.originDetails.address, 'id', 'version');
            shipment.destinationDetails.address = _.omit(shipment.destinationDetails.address, 'id', 'version');

            return shipment;
        };

        model.prepareBolForEditing = function (bol) {
            model.shipment.id = bol.id;

            if (model.shipment.originDetails) {
                model.shipment.originDetails.address = bol.origin;
                model.shipment.origin = {
                    id: bol.origin.id,
                    version: bol.origin.version
                };
            }

            if (model.shipment.destinationDetails) {
                model.shipment.destinationDetails.address = bol.destination;
                model.shipment.destination = {
                    id: bol.destination.id,
                    version: bol.destination.version
                };
            }

            if (model.shipment.billTo) {
                model.shipment.billTo = bol.billTo;
            }

            model.shipment.finishOrder.quoteMaterials = bol.materials;
        };

        function setUpTrackingBoardRedirect() {
            if ($location.path() === '/trackingBoard/manualBol') {
                model.redirectToTrackingBoard = true;
            }
        }

        /**
         *  Copying manual bol from operational board page.
         */
        model.copy = function (customerId, shipmentId) {
            ManualBolService.getManualBol({
                customerId: customerId,
                shipmentId: shipmentId
            }, function (response) {
                model.init();
                model.shipment = model.prepareBolForViewing(response);
                model.selectedCustomer = {
                    id: model.shipment.organizationId,
                    name: model.shipment.customerName
                };
                model.shipment.id = undefined;
                model.shipment.originDetails.address.id = undefined;
                model.shipment.originDetails.address.version = undefined;
                model.shipment.destinationDetails.address.id = undefined;
                model.shipment.destinationDetails.address.version = undefined;
                model.shipment.origin = undefined;
                model.shipment.destination = undefined;
                model.shipment.freightBillPayTo.id = undefined;
                setUpTrackingBoardRedirect();

                model.shipment.finishOrder.pickupDate = new Date();
                $location.path('/manual-bol/general-information');
            });
        };

        /**
         *  This method is used to edit manual bol from oparational board page.
         *  Before editing we need to load actual bol and set up model properly.
         */
        model.edit = function (customerId, shipmentId) {
            ManualBolService.getManualBol({
                customerId: customerId,
                shipmentId: shipmentId
            }, function (response) {
                model.shipment = model.prepareBolForViewing(response);
                $location.path('/manual-bol/general-information/' + model.shipment.id);
            });
        };

        model.cancel = function (customerId, shipmentId, callback) {
            $http.post('/restful/customer/' + customerId + '/manualbol/cancel/' + shipmentId).success(function () {
                $rootScope.$emit('event:operation-success', 'Cancel Oder', 'Order #' + shipmentId + ' was cancelled successfully');
                if (callback) {
                    callback();
                }
            }).error(function (error) {
                $rootScope.$emit('event:application-error', 'Oder cancellation failed!',
                        'Oder with id: ' + shipmentId + ' cannot be cancelled');
            });
        };

        function persistsWithinManualBolSteps(urlChunk) {
            return urlChunk.indexOf('/manual-bol/general-information') !== -1
                    || urlChunk.indexOf('/manual-bol/addresses') !== -1
                    || urlChunk.indexOf('/manual-bol/details') !== -1
                    || urlChunk.indexOf('/manual-bol/docs') !== -1;
        }

        if (window.location.href.indexOf('/manual-bol') !== -1) {
            $location.path('/manual-bol/general-information');
        }

        /**
         *  This watcher is intended to track attempts of following direct urls, like:
         * http://host:port/manual-bol/addresses/{id}. Such urls should be forbidden
         * because there is not chance to load manual bol instance without specified
         * customer. An attempt leads to cleaning model and redirecting to the first
         * step.
         */
        $rootScope.$watch(function () {
            return $location.path();
        }, function (newPath, oldPath) {
            if (newPath && newPath.match(/manual-bol/) && $routeParams.id && _.isUndefined(model.selectedCustomer)) {
                model.init();
                $location.path('/manual-bol/general-information');
            } else if (newPath !== oldPath && oldPath !== 'trackingBoard/manualBol'
                    && !persistsWithinManualBolSteps(newPath) && !persistsWithinManualBolSteps(oldPath)) {
                model.init();
            }
        });

        return model;
    }
]);

angular.module('manualBol.utils').value('shipmentStatuses', {
    OPEN: "Open",
    BOOKED: "Booked",
    DISPATCHED: "Dispatched",
    IN_TRANSIT: "In-Transit",
    OUT_FOR_DELIVERY: "Out for Delivery",
    DELIVERED: "Delivered",
    CANCELLED: "Cancelled",
    CUSTOMER_TRUCK: "Customer Truck"
});