xdescribe('Unit test for EditFreightBillPayToCtrl', function() {
    var scope = null;
    var proposalService;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();

        proposalService = $injector.get('ShipmentsProposalService');
        var controller = $injector.get('$controller');
        controller('EditFreightBillPayToCtrl', {
            '$scope': scope,
            'ShipmentsProposalService': proposalService
        });
    }));

    function createWizardData() {
        scope.wizardData = {
            shipment: {
                freightBillPayTo: {
                    id: 2,
                    address: {
                        country: {id: 'MEX', dialingCode: 3}
                    }
                }
            },
            emptyShipment: {
                freightBillPayTo: {id: 3}
            }
        }
    }

    function createBillto() {
        scope.freight = {
            address: {
                country: {id: 'MEX', dailingCode: 1}
            }
        };
    }

    function createFreightBill() {
        return {
            id: 1,
            address: {
                zip: {},
                country: {}
            },
            phone: {}
        }
    }

    it('should check initial controller state', function() {
        expect(scope.zipAutoComplete).toBeTruthy();
        expect(scope.showDialog).toBeFalsy();
        expect(scope.freightModel).toBeDefined();
        expect(scope.freightModel.parentDialog).toEqual('detailsDialogDiv');
    });

    it('should close dialog', function() {
        spyOn(scope, '$broadcast');
        scope.closeDialog();

        expect(scope.showDialog).toBeFalsy();
        expect(scope.$broadcast).toHaveBeenCalledWith('event:cleaning-input');
    });

    it('should save billto', function() {
        scope.freight = createFreightBill();
        scope.freightBillDefault = createFreightBill();
        createWizardData();
        scope.save();

        expect(scope.wizardData.shipment.freightBillPayTo).toEqual(scope.freight);
        expect(scope.showDialog).toBeFalsy();
    });

    it('should reset model by copying empty shipment freight billto', function() {
        spyOn(scope, '$broadcast');
        createWizardData();
        scope.generalInfoPage = {bolModel: {}};

        expect(scope.wizardData.shipment.freightBillPayTo.id).toBe(2);
        scope.reset();
        expect(scope.showDialog).toBeFalsy();
    });

    it('should reset model by fetching data from server', function() {
        spyOn(scope, '$broadcast');
        spyOn(proposalService, 'getFreightBillPayTo');
        createWizardData();
        scope.generalInfoPage = {bolModel: {}};
        scope.wizardData.emptyShipment.freightBillPayTo = undefined;

        scope.reset();
        //expect(proposalService.getFreightBillPayTo).toHaveBeenCalled();
        expect(scope.showDialog).toBeFalsy();
        //expect(scope.$broadcast).toHaveBeenCalledWith('event:cleaning-input');
    });

    it('should trigger freight watcher when country is changed', function() {
        createBillto();
        scope.$digest();
        expect(scope.zipAutoComplete).toBeTruthy();
        //expect(scope.previousCountry).toEqual(scope.freight.address.country);
    });

    it('should show dialog for freight bill to editiong', function() {
        createWizardData();
        scope.$root.$broadcast('event:showEditFreightBillPayToDialogSO');

        expect(scope.freight).toEqual(scope.wizardData.shipment.freightBillPayTo);
        expect(scope.previousCountry).toEqual(scope.wizardData.shipment.freightBillPayTo.address.country);
        expect(scope.freight.address.country.dialingCode).toEqual(scope.wizardData.shipment.freightBillPayTo.address.country.dialingCode);
        expect(scope.showDialog).toBeTruthy();
    });
});