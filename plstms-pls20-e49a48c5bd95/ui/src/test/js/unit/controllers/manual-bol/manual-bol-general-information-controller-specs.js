describe('Unit test for ManualBolGeneralInformationController', function() {
    var rootScope, scope, bolModel, shipmentUtils, shipmentProposalService;

    var mockAddressesListService = {
        listUserContacts : function() {
        }
    };

    beforeEach(module('manualBol'));
    beforeEach(inject(function($injector) {
        rootScope = $injector.get('$rootScope');
        rootScope.authData = new AuthData(null);
        scope = rootScope.$new();
        bolModel = $injector.get('manualBolModel');
        shipmentUtils = $injector.get('ShipmentUtils');
        shipmentProposalService = $injector.get('ShipmentsProposalService');

        scope.$root.ignoreLocationChange = function() {};
        var controller = $injector.get('$controller');
        controller('ManualBolGeneralInformationController', {
            '$scope': scope, 
            'manualBolModel': bolModel, 
            'ShipmentUtils': shipmentUtils, 
            'ShipmentsProposalService': shipmentProposalService,
            AddressesListService: mockAddressesListService,
            '$rootScope': rootScope
        });

        var httpBackend = $injector.get('$httpBackend');
        httpBackend.expectGET('/restful/shipment/dictionary/packageType').respond();
        httpBackend.expectGET('/restful/dictionary/notificationTypes').respond();
        httpBackend.expectGET('/restful/shipment/dictionary/billToReqField').respond();
        httpBackend.expectGET('/restful/carrier/default').respond();
        httpBackend.expectGET('/restful/shipment/proposal/freightBillPayTo').respond();
        scope.$digest();
    }));

    function setUpCarrier() {
        var carrierTuple = {
            id: 1,
            name: 'AVRT:AVERIT EXPRESS',
            currencyCode: 'USD',
            ediCapable: true
        };
        scope.generalInfoPage.bolModel.carrierTuple = carrierTuple;
    }

    it('checks initial page model state', function() {
        expect(scope.generalInfoPage).toBeDefined();
        expect(scope.generalInfoPage.next).toEqual('/manual-bol/addresses');
        expect(scope.generalInfoPage.prevoius).toBeUndefined();
        expect(scope.generalInfoPage.bolModel).toBeDefined();
        expect(scope.generalInfoPage.bolModel.steps.length).toBe(4);
        expect(scope.generalInfoPage.bolModel.steps[0]).toEqual({title: 'General information', label: 'general-information'});
        expect(scope.generalInfoPage.bolModel.steps[1]).toEqual({title: 'Addresses', label: 'addresses'});
        expect(scope.generalInfoPage.bolModel.steps[2]).toEqual({title: 'Details', label: 'details'});
        expect(scope.generalInfoPage.bolModel.steps[3]).toEqual({title: 'Docs', label: 'docs'});
        expect(scope.generalInfoPage.bolModel.shipment).toBeDefined();
        expect(scope.generalInfoPage.bolModel.shipment.finishOrder).toBeDefined();
        expect(scope.generalInfoPage.bolModel.shipment.finishOrder.quoteMaterials.length).toBe(0);
        expect(scope.generalInfoPage.bolModel.shipment.finishOrder.pickupDate).toBeDefined();
        expect(scope.generalInfoPage.bolModel.shipment.selectedProposition).toBeDefined();
        expect(scope.generalInfoPage.dictionary).toBeDefined();
        expect(scope.generalInfoPage.dictionary.classes.length).toBe(18);
        expect(scope.generalInfoPage.dictionary.dimensions.length).toBe(2);
        expect(scope.generalInfoPage.dictionary.paymentTerms.length).toBe(4);
        expect(scope.generalInfoPage.maxPickupDate).toBeDefined();
    });

    it('should trigger carrier watcher', function() {
        expect(scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier).toBeUndefined();

        setUpCarrier();
        scope.$digest();

        expect(scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier).toBeDefined();
        expect(scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier.id).toBe(1);
        expect(scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier.name).toEqual('AVERIT EXPRESS');
        expect(scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier.scac).toEqual('AVRT');
        expect(scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier.currencyCode).toEqual('USD');
        expect(scope.generalInfoPage.bolModel.shipment.selectedProposition.carrier.apiCapable).toBeFalsy();
    });

    it('should call freight bill pay to dialog', function() {
        scope.generalInfoPage.bolModel.shipment.freightBillPayTo = {name: 'test bill to'};
        scope.freightBillDefault = scope.generalInfoPage.bolModel.shipment.freightBillPayTo;
        spyOn(scope.$root, '$broadcast');
        /*scope.editFreightBillPayTo();
        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:showFreightBillPayToDialog',
                scope.generalInfoPage.bolModel.shipment.freightBillPayTo,
                scope.generalInfoPage.bolModel.shipment.freightBillPayTo);*/
    });

    it('should navigate to addresses step', function() {
        spyOn(scope, '$broadcast');
        spyOn(rootScope, 'navigateTo');
        scope.navigateTo();

        expect(scope.$broadcast).toHaveBeenCalledWith('event:pls-add-quote-item');
    });

    it('should open terminal info dialog', function() {
        setUpCarrier();
        scope.$digest();
        scope.generalInfoPage.bolModel.shipment.originDetails = {
            zip: {city: 'timberwoods', state: 'TWD', zip: 65000, country: {id: 'UA'}}
        };
        scope.generalInfoPage.bolModel.shipment.destinationDetails = {
            zip: {city: 'wonderlands', state: 'WLD', zip: 65000, country: {id: 'UA'}}
        };
        spyOn(shipmentProposalService, 'findTerminalInformation').and.callThrough();

        scope.openTerminalInfoModalDialog();

        expect(shipmentProposalService.findTerminalInformation).toHaveBeenCalledWith({}, jasmine.any(Object), jasmine.any(Function));
    });
});