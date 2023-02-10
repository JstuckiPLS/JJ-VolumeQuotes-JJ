xdescribe('Unit test for AddEditAdjustmentCtrl', function() {
    var scope;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();

        var controller = $injector.get('$controller');
        controller('AddEditAdjustmentCtrl', {
            '$scope': scope
        });
    }));

    function createWizardData() {
        scope.wizardData = {
                shipment: {
                    organizationId: 1,
                    bolNumber: 'testBol',
                    shipmentDirection: 'O',
                    paymentTerms: 'PREPAID',
                    billTo: {
                        id: 1,
                        address: {
                            addressName: 'Odessa, UA'
                        }
                    },
                    finishOrder: {
                        pickupDate: [{data: new Date()}],
                        poNumber: 'testPO',
                        glNumber: 'testGL',
                        ref: 'testRef'
                    },
                    selectedProposition: {
                        carrier: {
                            name: 'carrier',
                            currencyCode: 'USD'
                        }
                    }
                },
                carrierTuple: {
                    id: 1,
                    name: 'AVERITT',
                    scac: 'AVRT'
                }
            };
    };

    var VOIDED_ADJUSTMENT_ID = 34;
    var WRONG_CARRIER_ADJUSTMENT_ID = 8;
    var REBILL_REASON_ID = 6;
    var REBILL_SHIPPER_REASON_ID = 44;

    it('should check adjustments dialog options', function() {
        expect(scope.addEditAdjustmentDialogOptions).toBeDefined();
        expect(scope.addEditAdjustmentDialogOptions.parentDialog).toEqual('detailsDialogDiv');
    });

    it('should check adjustment model object', function() {
        expect(scope.adjustmentModel).toBeDefined();
        expect(scope.adjustmentModel.isCanceledAdjustment).toBeFalsy();
        expect(scope.adjustmentModel.isWrongReasonAdjustment).toBeFalsy();
        expect(scope.adjustmentModel.isRebillReason).toBeFalsy();
    });

    it('should check accessorial types array is empty', function() {
        expect(scope.accessorialTypes.length).toBe(0);
    });

    it('should prove editable flag is falsy', function() {
        expect(scope.isEdit).toBeFalsy();
    });

    it('should cancel adjustment editing', function() {
        scope.cancelEditAdjustment();

        expect(scope.addEditAdjustmentDialogVisible).toBeFalsy();
        expect(scope.adjustmentModel.isCanceledAdjustment).toBeFalsy();
        expect(scope.adjustmentModel.isWrongCarrierAdjustment).toBeFalsy();
        expect(scope.adjustmentModel.isRebillAdjustment).toBeFalsy();
        expect(scope.adjustmentModel.carrier).toBeUndefined();
        expect(scope.adjustmentModel.revenue).toBe(0);
        expect(scope.adjustmentModel.cost).toBe(0);
    });

    it('should open dialog for adjustment editing', function() {
        var dialogDetails = {
            isEdit: true,
            adjustmentModel: {
                doNotInvoice: false
            },
            accessorialTypes: [{id: 'CBF'}, {id: 'SRA'}]
        };
        createWizardData();
        scope.wizardData.shipment.selectedProposition.carrier = {};
        scope.$broadcast('event:showAddEditAdjustment', dialogDetails);

        expect(scope.isEdit).toBeTruthy();
        expect(scope.adjustmentModel.notInvoice).toBeTruthy();
        expect(scope.accessorialTypes[0]).toEqual({id: 'SRA'});
        expect(scope.accessorialTypes[1]).toEqual({id: 'CBF'});
    });

    it('should save adjustment but not update carrier', function() {
        spyOn(scope.$root, '$broadcast');
        spyOn(scope, 'cancelEditAdjustment');

        createWizardData();
        scope.adjustmentModel.shipment = scope.wizardData.shipment;
        scope.adjustmentModel.isWrongCarrierAdjustment = false;
        scope.adjustmentModel.carrier = 'this one should not be changed also';

        scope.saveAdjustment();

        expect(scope.adjustmentModel.billToId).toBe(1);
        expect(scope.adjustmentModel.billToName).toEqual('Odessa, UA');
        expect(scope.wizardData.shipment.selectedProposition.carrier.name).toEqual('carrier');
        expect(scope.adjustmentModel.carrier).toEqual('this one should not be changed also');
        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:saveAdjustment', jasmine.any(Object));
        expect(scope.cancelEditAdjustment).toHaveBeenCalled();
    });

    it('should save adjustment with "Wrong Carrier" reason and update carrier', function() {
        spyOn(scope.$root, '$broadcast');
        spyOn(scope, 'cancelEditAdjustment');

        createWizardData();
        scope.adjustmentModel.shipment = scope.wizardData.shipment;
        scope.adjustmentModel.isWrongCarrierAdjustment = true;
        scope.adjustmentModel.carrier = {name : 'AVERITT'};

        scope.saveAdjustment();

        expect(scope.adjustmentModel.billToId).toBe(1);
        expect(scope.adjustmentModel.billToName).toEqual('Odessa, UA');
        expect(scope.wizardData.shipment.selectedProposition.carrier.name).toEqual('AVERITT');
        expect(scope.adjustmentModel.carrier.name).toEqual('AVERITT');
        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:saveAdjustment', jasmine.any(Object));
        expect(scope.cancelEditAdjustment).toHaveBeenCalled();
    });

    it('should toggle "voided" adjustment reason flag', function() {
        scope.adjustmentModel.reason = VOIDED_ADJUSTMENT_ID;
        scope.adjustmentReasonChange();

        expect(scope.adjustmentModel.isCanceledAdjustment).toBeTruthy();
        expect(scope.adjustmentModel.isWrongCarrierAdjustment).toBeFalsy();
        expect(scope.adjustmentModel.isRebillReason).toBeFalsy();
    });

    it('should toggle "wrong carrier" adjustment reason flag', function() {
        scope.adjustmentModel.reason = WRONG_CARRIER_ADJUSTMENT_ID;
        scope.adjustmentReasonChange();

        expect(scope.adjustmentModel.isCanceledAdjustment).toBeFalsy();
        expect(scope.adjustmentModel.isRebillReason).toBeFalsy();
        expect(scope.adjustmentModel.isWrongCarrierAdjustment).toBeTruthy();
    });

    it('should toggle "rebill" adjustment reason flag', function() {
        scope.adjustmentModel.reason = REBILL_REASON_ID;
        createWizardData();
        scope.adjustmentReasonChange();

        expect(scope.adjustmentModel.isRebillAdjustment).toBeTruthy();
        expect(scope.adjustmentModel.isCanceledAdjustment).toBeFalsy();
        expect(scope.adjustmentModel.isWrongCarrierAdjustment).toBeFalsy();

        expect(scope.billToFilter.customerId).toEqual(1);
        expect(scope.billToFilter.currency).toEqual('USD');
        expect(scope.adjustmentModel.carrier.name).toEqual('carrier');
        expect(scope.adjustmentModel.carrier.currencyCode).toEqual('USD');
        expect(scope.adjustmentModel.shipmentDirection).toEqual('O');
        expect(scope.adjustmentModel.paymentTerms).toEqual('PREPAID');
        expect(scope.adjustmentModel.pickupDate).toEqual(scope.wizardData.shipment.finishOrder.pickupDate);
        expect(scope.adjustmentModel.shipment.bolNumber).toEqual('testBol');
        expect(scope.adjustmentModel.shipment.finishOrder.poNumber).toEqual('testPO');
        expect(scope.adjustmentModel.shipment.finishOrder.glNumber).toEqual('testGL');
        expect(scope.adjustmentModel.shipment.finishOrder.ref).toEqual('testRef');
    });

    it('should toggle "rebill shipper" adjustment reason flag', function() {
        scope.adjustmentModel.reason = REBILL_SHIPPER_REASON_ID;
        createWizardData();
        scope.wizardData.shipment.selectedProposition.carrier = { name: 'Test Carrier', currencyCode : 'CAD'};
        scope.wizardData.shipment.finishOrder = {pickupDate: '2016-06-12', poNumber: 'POO', glNumber: 'GLL', ref: 'testRefNumber'}
        scope.adjustmentReasonChange();

        expect(scope.adjustmentModel.isRebillAdjustment).toBeTruthy();
        expect(scope.adjustmentModel.isCanceledAdjustment).toBeFalsy();
        expect(scope.adjustmentModel.isWrongCarrierAdjustment).toBeFalsy();
        expect(scope.billToFilter.currency).toEqual('CAD');
        expect(scope.adjustmentModel.carrier.name).toEqual('Test Carrier');
        expect(scope.adjustmentModel.pickupDate).toEqual('2016-06-12');
        expect(scope.adjustmentModel.shipment.finishOrder.poNumber).toEqual('POO');
        expect(scope.adjustmentModel.shipment.bolNumber).toEqual('testBol');
        expect(scope.adjustmentModel.shipment.finishOrder.glNumber).toEqual('GLL');
        expect(scope.adjustmentModel.shipment.finishOrder.ref).toEqual('testRefNumber');
    });
});