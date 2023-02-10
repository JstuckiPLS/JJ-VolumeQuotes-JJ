/**
 * UI unit tests for SelectCarrierCtrl.
 */

describe('SelectCarrierCtrl tests', function() {
    var controller;
    var promiseProvider;
    var costDetailsUtils = {
        guid : function() {
        },
        getSelectedGuaranteed : function() {
        },
        getTotalCost: function() {
        },
        getCarrierTotalCost: function() {
        }
    };

    var mockProposals = [
                         {"guid": "2b3cfca5-905a-4a73-8f69-e5d807694847", "estimatedTransitDate": "2013-09-27T00:00:00.000", "estimatedTransitTime": 1441,
                             "serviceType": "DIRECT", "ref": null,
                             "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
                             {"refType": "SRA",
                                 "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
                             {"refType": "DS", "description": "Discount", "subTotal": -192.18, "costDetailOwner": "S", "guaranteedBy": null},
                             {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 13.53, "costDetailOwner": "S", "guaranteedBy": null},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
                             {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
                         ]},
                         {"guid": "7d4937d3-2d49-43bd-9abf-e9ad198dce76", "estimatedTransitDate": "2013-09-27T00:00:00.000", "estimatedTransitTime": 1440,
                             "serviceType": "DIRECT", "ref": null,
                             "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
                             {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
                             {"refType": "DS", "description": "Discount", "subTotal": -199.55, "costDetailOwner": "S", "guaranteedBy": null},
                             {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 8.79, "costDetailOwner": "S", "guaranteedBy": null},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
                             {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
                             {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
                         ]}
                     ];


    var mockShipmentsProposalService = {
        findShipmentPropositions : function(params, shipment, success, failure) {
            success(mockProposals);
            return this;
        }
    };

    var pricingDetails = [{"carrierFSId":145,"carrierFuelDiscount":22,"costAfterDiscount":17.6,"costDiscount":80,
                           "effectiveDate":"2012-11-13","minimumCost":90,"movementType":"BOTH","pricingType":"BUY_SELL",
                           "smc3CostDetails":[{"charge":"68.95","nmfcClass":"50","enteredNmfcClass":"50","rate":"56.06",
                           "weight": "123.0"}],"smc3MinimumCharge":88,"totalChargeFromSmc3":68.95}];

    var mockShipmentDetailsService = {
        findShipmentPricingDetails : function(params, shipment, success, failure) {
            return pricingDetails;
        }
    };

    var dictionaryValues = [ Math.random(), Math.random() ];

    var mockShipmentUtils = {
        getDictionaryValues : function() {
            return dictionaryValues;
        }
    };

    var shipment = {
        organizationId : 1,
        customerName : 'Bruce Lee',
        originDetails : {
            address : {
                zip : {
                    country : {}
                }
            }
        },
        destinationDetails : {
            address : {
                zip : {
                    country : {}
                }
            }
        },
        finishOrder : {
            pickupWindowFrom : 'fromPickup',
            pickupWindowTo : 'toPickup',
            deliveryWindowFrom : 'deliveryFrom',
            deliveryWindowTo : 'deliveryTo'
        },
        selectedProposition : {
            ref : 'reference'
        }
    };

    var ngGridPluginFactoryMock = {
            plsGrid: function() {},
            progressiveSearchPlugin: function() {},
            actionPlugin: function() {}
        };

    beforeEach(module('shipmentEntry'));
    beforeEach(inject(function($controller, $rootScope, $q) {
        $scope = $rootScope.$new();

        $scope.$root = $rootScope.$new();

        $scope.$root.isFieldRequired = function() {
            return true;
        };
        $scope.$root.authData = {
                customerUser: undefined,
                organization: {
                  orgId: 1,
                  name: 'PLS SHIPPER'
                }
            };

        $scope.parseISODate = function() {
        };

        promiseProvider = $q;

        $scope.shipmentEntryData = {};
        $scope.shipmentEntryData.shipment = shipment;

        $scope.shipmentEntryData.selectedCustomer = {
            id : 1,
            name : 'PLS SHIPPER'
        };

        $scope.shipmentDirectionValues = {};

        controller = $controller('SelectCarrierController', {
            $scope : $scope,
            ShipmentsProposalService : mockShipmentsProposalService,
            NgGridPluginFactory : ngGridPluginFactoryMock,
            CostDetailsUtils : costDetailsUtils,
            ShipmentUtils : mockShipmentUtils,
            ShipmentDetailsService : mockShipmentDetailsService
        });

        $scope.carrierPropositionsGrid.$gridScope = {
            columns : [ {}, {}, {}, {}, {
                visible : false,
                toggleVisible : function() {
                }
            } ]
        };
        spyOn($scope.carrierPropositionsGrid.$gridScope.columns[4], 'toggleVisible').and.callThrough();
    }));

    it('should test controller creation', function() {
        c_expect(controller).to.be.defined;
    });
});