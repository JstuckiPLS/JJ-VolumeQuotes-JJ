describe('ShipmentEntryController test', function() {
    var scope;
    var route;
    var controller;

    var shipment = {};
    var shipmentOperationsService = {};
    var shipmentsProposalService = {};
    var shipmentDocumentEmailService = {};
    var costDetailsUtils = {
        guid: function() {}
    };
    var config = {};
    var shipmentsService = {};
    var userDetailsService = {};
    var shipmentDetailsService = {};

    beforeEach(module('plsApp'));
    beforeEach(module('shipmentEntry'));

    beforeEach(function() {
        shipment = {
            organizationId: 1,
            accessorialTypes: [],
            customerName: 'John Doe',
            originDetails: {
                address: {
                    paymentTerms: 'PREPAID',
                    address1: 'ONE RIVER ROAD',
                    address2: 'BDG 59,DR#2;THEN BDG273;A2WEST',
                    addressCode: '109 ADDRESS',
                    addressId: 129,
                    addressName: 'ADDR_NAME110',
                    contactName: 'Alethea Hypolite',
                    country: {
                        dialingCode: '001',
                        id: 'USA',
                        name: 'United States of America'
                    },
                    createdBy: 2,
                    deliveryNotes: 'Test delivery notes',
                    email: 'test109@test.com',
                    fax: {
                        areaCode: '509',
                        countryCode: '1',
                        number: '6984590'
                    },
                    id: '109',
                    latitude: '42.81011',
                    longitude: '-73.95108',
                    phone: {
                        countryCode: '380',
                        areaCode: '57',
                        number: '5550033'
                    },
                    pickupNotes: 'Test pickup notes',
                    pickupWindowFrom: {
                        am: 'false',
                        hours: '5',
                        minutes: '0'
                    },
                    pickupWindowTo: {
                        am: 'false',
                        hours: '7',
                        minutes: '30'
                    },
                    sharedAddress: 'true',
                    zip: {
                        city: 'ARLINGTON',
                        country: { 
                            id: 'USA',
                            name: 'United States of America',
                            dialingCode: '001'
                        },
                        zip: '22222',
                        state: 'VA', 
                    }
                }
            },
            destinationDetails: {
                address: {
                    address1: 'NO ADDRESS',
                    addressCode: 'LT-00000002',
                    addressId: 22,
                    addressName: '4 ADDR',
                    contactName: 'Jack Davis',
                    country: {
                        dialingCode: '001',
                        id: 'USA',
                        name: 'United States of America'
                    },
                    createdBy: 2,
                    deliveryNotes: 'Test delivery notes',
                    email: 'test109@test.com',
                    fax: {
                        areaCode: '509',
                        countryCode: '1',
                        number: '6984590'
                    },
                    id: '109',
                    latitude: '42.81011',
                    longitude: '-83.95108',
                    phone: {
                        countryCode: '380',
                        areaCode: '57',
                        number: '5550033'
                    },
                    pickupWindowFrom: {
                        am: 'true',
                        hours: '9',
                        minutes: '0'
                    },
                    pickupWindowTo: {
                        am: 'false',
                        hours: '5',
                        minutes: '0'
                    },
                    sharedAddress: 'true',
                    zip: {
                        zip: '10101',
                        country: { 
                            id: 'USA', 
                            name: 'United States of America', 
                            dialingCode: '001'
                        },
                        state: 'NY',
                        city: 'NEW YORK'
                    },
                    phone: {
                        countryCode: '1',
                        areaCode: '123',
                        number: '1234567'
                    },
                }
            },
            finishOrder: {
                pickupDate: '2015-07-02',
                quoteMaterials: [
                    { 
                       weight: 110, commodityClass: 'CLASS_85', quantity: 1, packageType: 'BOX', productCode: '24545214SKU',
                       productDescription: 'Budweiser', nmfc: 456454, hazmat: false, stackable: false, dimensionUnit: 'INCH',
                       productId: 16, weightUnit: 'LBS'
                    }
                ],
                pickupWindowFrom: {
                    am: 'false',
                    hours: '5',
                    minutes: '0'
                },
                pickupWindowTo: {
                    am: 'false',
                    hours: '7',
                    minutes: '30'
                },
                deliveryWindowFrom: 'deliveryFrom',
                deliveryWindowTo: 'deliveryTo'
            },
            selectedProposition: {
                ref: 'reference'
            },
            proposals : {}
        };
    });

    beforeEach(inject(function($controller, $rootScope, $filter, $route, $httpBackend, DateTimeUtils) {
        scope = $rootScope.$new();
        var current = {
            params: {}
        };
        $route.current = current;
        route = $route;
        
        controller = $controller('ShipmentEntryController', {
            $scope: scope,
            DateTimeUtils: DateTimeUtils,
            ShipmentsProposalService: shipmentsProposalService,
            CostDetailsUtils: costDetailsUtils,
            UserDetailsService: userDetailsService,
            SelectCarrFormValService: {},
            ShipmentDocumentService: {},
            ShipmentDocumentEmailService: shipmentDocumentEmailService,
            urlConfig: config,
            ShipmentDetailsService: shipmentDetailsService,
            ShipmentUtils: function(){return {};}
        });

        $httpBackend.when('GET', '/restful/dictionary/glNumComponents').respond({});
    }));

    it ('should check initial model state', function() {
        expect(scope.shipmentEntryData).toBeDefined();
        expect(scope.shipmentEntryData.selectedCustomer).toBeDefined();
        expect(scope.shipmentEntryData.selectedCustomer.id).toBeUndefined();
        expect(scope.shipmentEntryData.selectedCustomer.name).toBeUndefined();
        expect(scope.previewBOLOptions.width).toEqual('100%');
        expect(scope.previewBOLOptions.height).toEqual('380px');
        expect(scope.termsAgreementOptions.width).toEqual('100%');
        expect(scope.termsAgreementOptions.height).toEqual('380px');
        expect(scope.termsAgreementOptions.pdfLocation).toContain('/customer/shipmentdocs/termOfAgreement');
        expect(scope.shipmentEntryData.emptyShipment).toBeDefined();
        expect(scope.shipmentEntryData.emptyShipment.originDetails).toBeDefined();
        expect(scope.shipmentEntryData.emptyShipment.destinationDetails).toBeDefined();
        expect(scope.shipmentEntryData.emptyShipment.finishOrder).toBeDefined();
        expect(scope.shipmentEntryData.emptyShipment.guaranteedBy).toBeUndefined();
        expect(scope.shipmentEntryData.emptyShipment.originDetails.accessorials.length).toBe(0);
        expect(scope.shipmentEntryData.emptyShipment.originDetails.address).toBeDefined();
        expect(scope.shipmentEntryData.emptyShipment.originDetails.address.zip).toBeDefined();
        expect(scope.shipmentEntryData.emptyShipment.originDetails.address.zip.country).toBeDefined();
        expect(scope.shipmentEntryData.emptyShipment.destinationDetails.accessorials.length).toBe(0);
        expect(scope.shipmentEntryData.emptyShipment.destinationDetails.address).toBeDefined();
        expect(scope.shipmentEntryData.emptyShipment.destinationDetails.address.zip).toBeDefined();
        expect(scope.shipmentEntryData.emptyShipment.destinationDetails.address.zip.country).toBeDefined();
        expect(scope.shipmentEntryData.shipment).toEqual(scope.shipmentEntryData.emptyShipment);
        expect(scope.storedBolId).toBeNull();
        expect(scope.showSendMailDialog).toBeFalsy();
        expect(scope.isRequoteDisabled).toBeTruthy();
        expect(scope.emailOptions).toBeDefined();
        expect(scope.shipmentEntryData.editedEmail).toEqual('');
        expect(scope.isEditMode).toBeFalsy();
    });

    it ('should handle shipment properly', function() {
        spyOn(scope, '$broadcast');

        scope.handleShipment(shipment);

        expect(scope.shipmentEntryData.shipment).toEqual(shipment);
    });

    it ('should open terms of agreement dialog', function() {
        expect(scope.showTermsOfAgreement).toBeUndefined();
        scope.openTermsOfAgreementDialog();
        expect(scope.showTermsOfAgreement).toBeTruthy();
    });

    it ('should close terms of agreement dialog', function() {
        expect(scope.showTermsOfAgreement).toBeUndefined();
        scope.closeTermsOfAgreementDialog();
        expect(scope.showTermsOfAgreement).toBeFalsy();
    });

    it ('should confirm terms of agreement', function() {
        expect(scope.confirmedTermsOfAgreement).toBeUndefined();
        expect(scope.showTermsOfAgreement).toBeUndefined();
        scope.confirmTermsOfAgreement();
        expect(scope.confirmedTermsOfAgreement).toBeTruthy();
        expect(scope.showTermsOfAgreement).toBeFalsy();
    });

    it ('should show not fire pickup message', function() {
        spyOn(scope.$root, '$emit');

        var now = new Date();
        var time = now.getTime();
        scope.showPickupMessageIfNeeded(now, time);

        expect(scope.$root.$emit).not.toHaveBeenCalled();
    });

    it ('should prepare shipment', function() {
        var skipQuoteId = true;
        var organizationId = 1;
        var proposals = {};
        var preparedShipment = scope.prepareShipmentForSend(shipment, skipQuoteId, organizationId, proposals);

        expect(preparedShipment).toEqual(shipment);
    });

    it ('should intercept customer selection and clean all data', function() {
        spyOn(scope.$root, '$broadcast').and.callThrough();
        spyOn(scope, 'init');
        scope.$emit('event:changeCustomer');

        scope.$digest();

        expect(scope.shipmentEntryData.shipment).toEqual(scope.shipmentEntryData.emptyShipment);
        expect(scope.shipmentEntryData.shipment.finishOrder.quoteMaterials.length).toBe(0);
    });

   it ('should track origin address changes', function() {
        var firstAddress = {
            pickupWindowFrom: '12:00 PM',
            pickupWindowTo: '13:00 PM'
        };
        var secondAddress = {
            pickupWindowFrom: '15:30 PM',
            pickupWindowTo: '18:30 PM'
        };

        scope.shipmentEntryData.shipment.originDetails.address = firstAddress;
        scope.$digest();
        scope.shipmentEntryData.shipment.originDetails.address = secondAddress;
        scope.$digest();

        expect(scope.shipmentEntryData.shipment.finishOrder.pickupWindowFrom).toEqual(secondAddress.pickupWindowFrom);
        expect(scope.shipmentEntryData.shipment.finishOrder.pickupWindowTo).toEqual(secondAddress.pickupWindowTo);
    });

    it ('should track destination address changes', function() {
        var firstAddress = {
            pickupWindowFrom: '12:00 PM',
            pickupWindowTo: '13:00 PM'
        };
        var secondAddress = {
            pickupWindowFrom: '15:30 PM',
            pickupWindowTo: '18:30 PM'
        };

        scope.shipmentEntryData.shipment.originDetails.address = firstAddress;
        scope.$digest();
        scope.shipmentEntryData.shipment.originDetails.address = secondAddress;
        scope.$digest();

        expect(scope.shipmentEntryData.shipment.finishOrder.deliveryWindowFrom).toEqual(secondAddress.deliveryWindowFrom);
        expect(scope.shipmentEntryData.shipment.finishOrder.deliveryWindowTo).toEqual(secondAddress.deliveryWindowTo);
    });

    it ('should close preview bol window', function() {
        scope.closePreviewBolWindow();
        expect(scope.showPreviewBolWindow).toBeFalsy();
    });

    it ('should be truthy when over dimensional selected', function() {
        expect(scope.isOverDimensionSelected()).toBeFalsy();
        scope.shipmentEntryData.shipment.originDetails.accessorials.push('ODM');
        expect(scope.isOverDimensionSelected()).toBeTruthy();
    });

    it ('should be truthy when over dimensional selected', function() {
        expect(scope.isOverDimensionSelected()).toBeFalsy();
        scope.shipmentEntryData.shipment.destinationDetails.accessorials.push('ODM');
        expect(scope.isOverDimensionSelected()).toBeTruthy();
    });

    it ('test validation failed to select customer', function() {
        scope.shipmentEntryData.addressForm = {
            $invalid : false
        };
        scope.shipmentEntryData.selectedCustomer = undefined;
        spyOn(scope.$root, '$emit');
        spyOn(scope, '$broadcast');

        scope.getQuote();

        c_expect(scope.$root.$emit.calls.count()).to.equal(1);
        c_expect(scope.$root.$emit.calls.mostRecent().args).to.eql(['event:application-error', 'Shipment validation failed!', 'Please select customer.']);
    });

    it ('test validation failed to fill necessary data', function() {
        scope.shipmentEntryData.addressForm = {
            $invalid : undefined
        };
        scope.shipmentEntryData.selectedCustomer = {id: 1, name: 'PLS SHIPPER'};
        spyOn(scope.$root, '$emit');
        spyOn(scope, '$broadcast');
        scope.getQuote();

        c_expect(scope.$root.$emit.calls.count()).to.equal(1);
        c_expect(scope.$root.$emit.calls.mostRecent().args).to.eql(['event:application-error', 'Shipment validation failed!', 'Please fill necessary data.']);
    });

    it ('test success get quote', function() {
        scope.shipmentEntryData.addressForm = {
            $invalid : false
        };
        scope.shipmentEntryData.shipment = shipment;
        spyOn(scope, '$broadcast');
        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
        scope.getQuote();
        expect(scope.$broadcast).toHaveBeenCalledWith('event:get-quote', scope.shipmentToSend);
        c_expect(scope.$broadcast.calls.count()).to.equal(1);
    });

    it ('should clear all', function() {
        spyOn(scope, '$broadcast');
        spyOn(scope, 'init');

        scope.clearAll();

        expect(scope.init).toHaveBeenCalledWith();
        expect(scope.$broadcast).toHaveBeenCalledWith('event:cleaning-input');
        expect(scope.confirmedTermsOfAgreement).toBeFalsy();
        expect(scope.shipmentEntryData.editedEmail).toBeUndefined();
        expect(scope.$broadcast).toHaveBeenCalledWith('event:pls-clear-form-data');
    });
});
