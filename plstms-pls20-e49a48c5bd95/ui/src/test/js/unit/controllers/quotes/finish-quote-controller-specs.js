/**
 * Tests for finish quote controller of quote wizard.
 *
 * @author Denis Zhupinskij
 */
describe('FinishQuoteCtrl (Finish Quote) Controller Test.', function () {

    // ANGULAR SERVICES
    var scope = undefined;
    var rootParams = undefined;

    //QuoteWizard controller
    var controller = undefined;

    var localUrlConfig = undefined;

    var mockPreparedShipmentForBol = {value: 'mockvalue'};

    var mockBookedShipment = {id: Math.floor((Math.random() * 100) + 1), 
            billTo: {paymentMethod : ""},
            selectedProposition: {carrier:{ediCapable: true}}};

    var mockShipmentSavingService = {
        bookIt: function (params, shipment, successCallback) {
            successCallback(mockBookedShipment);
        }
    };

    var mockShipmentsProposalService = {
        findShipmentPropositions: function (params, shipment, success) {
            success({});
            return this;
        },
        getFreightBillPayTo: function (params, success) {
            success({});
            return this;
        }
    };
    
    var mockDocs = [
        {docId: 1}
    ];
    
    var mockShipmentDocumentService = {
        getDocumentList: function (params, successCallback) {
            successCallback(mockDocs);
        },
        prepareBolForShipment: function (params, shipment, successCallback) {
            successCallback(mockPreparedShipmentForBol);
        }
    };

    var selectedCustomer = {id: Math.floor((Math.random() * 100) + 1), name: undefined};

    beforeEach(module('plsApp'));

    beforeEach(inject(function ($rootScope, $controller, urlConfig, $location, LinkedListUtils) {
        scope = $rootScope.$new();

        localUrlConfig = urlConfig;

        scope.wizardData = {};
        scope.wizardData.steps = LinkedListUtils.getLinkedList();
        scope.wizardData.steps.add('rate_quote');
        scope.wizardData.steps.add('select_carrier');
        scope.wizardData.steps.add('build_order');
        scope.wizardData.steps.add('finish_order');
        scope.wizardData.steps.add('finish_quote');

        scope.wizardData.minPickupDate = new Date();

        scope.wizardData.selectedCustomer = selectedCustomer;

        scope.wizardData.emptyShipment = {
            billTo: {
                creditHold: false,
                availableAmount: 200,
                paymentMethod : ""
            },
            details: {
                accessorials: []
            },
            finishOrder: {
                quoteMaterials: [],
                pickupDate: JSON.stringify(scope.wizardData.minPickupDate).replace(/\"/g, '')
            },
            status: 'OPEN'
        };

        scope.wizardData.shipment = angular.copy(scope.wizardData.emptyShipment);
        scope.wizardData.shipment.selectedProposition = {
            pricingProfileId: 1,
            carrier: {
                ediCapable: true
            }
        };

        scope.wizardData.steps.find('finish_quote');

        controller = $controller('FinishQuoteCtrl', {$scope: scope, urlConfig: urlConfig,
            ShipmentSavingService: mockShipmentSavingService, $location: $location, ShipmentsProposalService: mockShipmentsProposalService, 
            ShipmentDocumentService: mockShipmentDocumentService, ShipmentDocumentEmailService: {}, ShipmentUtils: {
                isCreditLimitValid: function(){ return true; }
            }});
    }));

    it('should test controller creation', function () {
        c_expect(controller).to.be.defined;
        c_expect(scope.bolOptions).to.be.defined;
        c_expect(scope.termsOfAgreementModalOptions).to.be.defined;
        c_expect(scope.toaOptions).to.be.defined;
        c_expect(scope.toaOptions.pdfLocation).to.be.defined;
    });

    it('should go to previous step', function () {
        scope.previousStep();

        c_expect(scope.wizardData.step).to.equal('finish_order');
    });

    it('should init step properly', function () {
        spyOn(mockShipmentDocumentService, 'prepareBolForShipment').and.callThrough();
        scope.$digest();

        scope.initStep();

        c_expect(mockShipmentDocumentService.prepareBolForShipment.calls.count()).to.equal(1);

        var pdfLocation = localUrlConfig.shipment + '/customer/shipmentdocs/' + mockPreparedShipmentForBol.value;

        c_expect(scope.bolOptions.pdfLocation).to.equal(pdfLocation);
        c_expect(scope.storedBolId).to.equal(mockPreparedShipmentForBol.value);
    });

    it('should open terms of agreement dialog', function () {
        scope.openTermsOfAgreementDialog();

        c_expect(scope.bolOptions.hidePdf).to.be.true;
        c_expect(scope.showTermsOfAgreement).to.be.true;

    });

    it('should close terms of agreement dialog', function () {
        scope.closeTermsOfAgreementDialog();

        c_expect(scope.bolOptions.hidePdf).to.be.false;
        c_expect(scope.showTermsOfAgreement).to.be.false;
    });


    it('should confirm terms of agreement', function () {
        scope.confirmTermsOfAgreement();

        c_expect(scope.confirmedTermsOfAgreement).to.be.true;
        c_expect(scope.bolOptions.hidePdf).to.be.false;
        c_expect(scope.showTermsOfAgreement).to.be.false;
    });

    it('should fail book shipment until terms of agreement agreed', function () {
        spyOn(mockShipmentSavingService, 'bookIt').and.callThrough();
        scope.$digest();

        scope.bookIt();

        c_expect(mockShipmentSavingService.bookIt.calls.count()).to.equal(0);
    });

    it('should book shipment', function () {
        spyOn(mockShipmentSavingService, 'bookIt').and.callThrough();
        spyOn(scope, '$broadcast').and.callThrough();
        scope.$apply(function () {
            scope.confirmedTermsOfAgreement = true;
            scope.storedBolId = 1;
        });
        scope.bookIt();

        c_expect(mockShipmentSavingService.bookIt.calls.count()).to.equal(1);
        c_expect(mockShipmentSavingService.bookIt.calls.mostRecent().args[0]).to.deep.equal({
            customerId: selectedCustomer.id, storedBolId: scope.storedBolId});

        c_expect(scope.progressPanelOptions.showPanel).to.be.false;
        c_expect(scope.bolOptions.hidePdf).to.be.true;
        c_expect(scope.bolOptions.pdfLocation).to.be.undefined;

        c_expect(scope.$broadcast.calls.count()).to.equal(1);

        c_expect(scope.$broadcast.calls.mostRecent().args.length).to.equal(2);
        c_expect(scope.$broadcast.calls.mostRecent().args[0]).to.equal('event:showShipmentDetails');

        var broadcastParam = scope.$broadcast.calls.mostRecent().args[1];
        c_expect(broadcastParam).to.be.defined;
        c_expect(broadcastParam.shipmentId).to.equal(mockBookedShipment.id);
        c_expect(broadcastParam.customerId).to.equal(selectedCustomer.id);
        c_expect(broadcastParam.closeHandler).to.be.defined;
        c_expect(broadcastParam.closeHandler).to.be.a('function');
    });

});
