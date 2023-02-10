describe('Unit test for BaseSalesOrderCtrl', function() {
    var scope;
    var proposalService;
    var documentEmailService;
    var isRequiredField;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();

        proposalService = $injector.get('ShipmentsProposalService');
        documentEmailService = $injector.get('ShipmentDocumentEmailService');
        isRequiredField = $injector.get('isRequiredField');
        var config = {};

        var controller = $injector.get('$controller');
        controller('BaseSalesOrderCtrl', {
            '$scope': scope,
            'ShipmentsProposalService': proposalService,
            'ShipmentDocumentEmailService': documentEmailService,
            'isRequiredField': isRequiredField,
            'urlConfig': config
        });
    }));

    var prepareTerminalCriteria = function() {
        scope.wizardData.shipment.selectedProposition.carrier = {
            scac: 'AVRT'
        };
        scope.wizardData.shipment.originDetails = {
            zip: {
                city: 'Odessa',
                country: {id: 1}
            }
        };
        scope.wizardData.shipment.destinationDetails = {
            zip: {
                city: 'Kiev',
                country: {id: 1}
            }
        };

        var terminalCriteria = {
            scac : scope.wizardData.shipment.selectedProposition.carrier.scac,
            originAddress : {
                city : scope.wizardData.shipment.originDetails.zip.city,
                stateCode : scope.wizardData.shipment.originDetails.zip.state,
                postalCode : scope.wizardData.shipment.originDetails.zip.zip,
                countryCode : scope.wizardData.shipment.originDetails.zip.country.id
            },
            destinationAddress : {
                city : scope.wizardData.shipment.destinationDetails.zip.city,
                stateCode : scope.wizardData.shipment.destinationDetails.zip.state,
                postalCode : scope.wizardData.shipment.destinationDetails.zip.zip,
                countryCode : scope.wizardData.shipment.destinationDetails.zip.country.id
            }
        };

        return terminalCriteria;
    };

    it('should check user capabilities', function() {
        expect(scope.userCapabilities.length).toBe(1);
        expect(scope.userCapabilities).toContain('QUOTES_VIEW');
    });

    it('should initialize wizard data', function() {
        expect(scope.wizardData).toBeUndefined();

        scope.initialize();

        expect(scope.wizardData).toBeDefined();
        expect(scope.wizardData.emptyShipment).toBeDefined();
        expect(scope.wizardData.emptyShipment.originDetails).toBeDefined();
        expect(scope.wizardData.emptyShipment.originDetails.accessorials.length).toBe(0);
        expect(scope.wizardData.emptyShipment.destinationDetails).toBeDefined();
        expect(scope.wizardData.emptyShipment.destinationDetails.accessorials.length).toBe(0);
        expect(scope.wizardData.emptyShipment.finishOrder).toBeDefined();
        expect(scope.wizardData.emptyShipment.finishOrder.quoteMaterials.length).toBe(0);
        expect(scope.wizardData.emptyShipment.finishOrder.pickupDate).toBeDefined();
        expect(scope.wizardData.emptyShipment.finishOrder.estimatedDelivery).toBeDefined();
        expect(scope.wizardData.emptyShipment.selectedProposition).toBeDefined();
        expect(scope.wizardData.emptyShipment.selectedProposition.carrier).toBeUndefined();
        expect(scope.wizardData.emptyShipment.finishOrder.ref).toBeUndefined();
        expect(scope.wizardData.emptyShipment.selectedProposition.costDetailItems).toBeDefined();
        expect(scope.wizardData.emptyShipment.status).toEqual('BOOKED');
        expect(scope.wizardData.shipment).toBeDefined();
        expect(scope.wizardData.selectedCustomer).toBeDefined();
        expect(scope.wizardData.selectedCustomer.id).toBeUndefined();
        expect(scope.wizardData.selectedCustomer.name).toBeUndefined();
        expect(scope.wizardData.paymentTermsValues.length).toBe(0);
        expect(scope.wizardData.oldStatus).toEqual('BOOKED');
        expect(scope.areOriginAndDestinationFilledIn).toBeFalsy();
    });

    it('should return "false" when shipment is undefined', function() {
        scope.wizardData = {
            shipment: {
                billTo: {
                    billToRequiredFields: []
                }
            }
        };
        expect(isRequiredField(scope.wizardData.shipment.billTo.billToRequiredFields, 'TEST_FIELD_CODE')).toBeFalsy();
    });

    it('should return "false" when bill to is undefined', function() {
        scope.wizardData = {
            shipment: {
                billTo: {
                    billToRequiredFields: [{name: 'TEST_FIELD', required: true}]
                }
            }
        };
        expect(isRequiredField(scope.wizardData.shipment.billTo.billToRequiredFields, 'TEST_FIELD_CODE')).toBeFalsy();
    });

    it('should return "false" if bill to required fields doesn"t contain specified field', function() {
        scope.wizardData = {
            shipment: {
                billTo: {
                    billToRequiredFields: [{name: 'TEST_FIELD_CODE', required: false}]
                }
            }
        };
        expect(isRequiredField(scope.wizardData.shipment.billTo.billToRequiredFields, 'TEST_FIELD_CODE')).toBeFalsy();
    });

    it('should return "true"', function() {
        scope.wizardData = {
            shipment: {
                billTo: {
                    billToRequiredFields: [{name: 'TEST_FIELD_CODE', required: true}]
                }
            }
        };
        expect(isRequiredField(scope.wizardData.shipment.billTo.billToRequiredFields, 'TEST_FIELD_CODE')).toBeTruthy();
    });

    it('should check document model', function() {
        expect(scope.fullViewDocModel).toBeDefined();
        expect(scope.fullViewDocModel.showFullViewDocumentDialog).toBeFalsy();
        expect(scope.fullViewDocModel.fullViewDocOption).toBeDefined();
        expect(scope.fullViewDocModel.fullViewDocOption.height).toEqual('500px');
        expect(scope.fullViewDocModel.fullViewDocOption.pdfLocation).toBeNull();
        expect(scope.fullViewDocModel.fullViewDocOption.imageContent).toBeFalsy();
        expect(scope.fullViewDocModel.shipmentFullViewDocumentModalOptions).toBeDefined();
        expect(scope.fullViewDocModel.shipmentFullViewDocumentModalOptions.parentDialog).toEqual('detailsDialogDiv');
    });

    it('should clear all', function() {
        spyOn(scope, '$broadcast');
        scope.initialize();
        scope.clearAll();

        expect(scope.$broadcast).toHaveBeenCalledWith('event:cleaning-input');
        expect(scope.wizardData.shipment).toBeDefined();
        expect(scope.wizardData.carrierTuple).toBeUndefined();
        expect(scope.$broadcast).toHaveBeenCalledWith('event:pls-clear-form-data');
    });

    it('should open terminal info modal dialog', function() {
        spyOn(proposalService, 'findTerminalInformation');
        spyOn(scope, '$broadcast')();
        spyOn(scope.$root, '$broadcast');
        scope.initialize();
        var terminalCriteria = prepareTerminalCriteria();
        
        scope.openTerminalInfoModalDialog();

        expect(proposalService.findTerminalInformation).toHaveBeenCalledWith({}, terminalCriteria, jasmine.any(Function));
        expect(scope.$broadcast).toHaveBeenCalled();
        expect(scope.$root.$broadcast).not.toHaveBeenCalled();
    });

    it('should update status and set it to DELIVERED', function() {
        scope.initialize();
        scope.wizardData.shipment.finishOrder.actualPickupDate = [{data: new Date()}];
        scope.wizardData.shipment.finishOrder.actualDeliveryDate = [{data: new Date()}];
        scope.updateStatusForActualDate();

        expect(scope.wizardData.shipment.status).toEqual('DELIVERED');
    });

    it('should update status and set it to IN_TRANSIT', function() {
        scope.initialize();
        scope.wizardData.shipment.finishOrder.actualPickupDate = [{data: new Date()}];
        scope.wizardData.shipment.finishOrder.actualDeliveryDate = [];
        scope.updateStatusForActualDate();

        expect(scope.wizardData.shipment.status).toEqual('IN_TRANSIT');
    });
});