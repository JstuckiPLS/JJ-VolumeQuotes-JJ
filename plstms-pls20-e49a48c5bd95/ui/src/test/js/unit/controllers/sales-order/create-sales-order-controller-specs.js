/**
 * Tests for Create Sales Order Controller.
 *
 * @author Nikita Cherevko
 */
describe('CreateSalesOrderCtrl (Create Sales Order) Controller Test.', function () {

    //angular services
    var scope = undefined;
    var filter = undefined;

    //CreateSalesOrder controller
    var controller = undefined;

    var toLastStep = function () {
        for (var i = 0; i < 3; i++) {
            scope.nextStep();
        }
    };

    var mockShipmentDocumentEmailService = {};

    var emptyShipment = function (pickupDate) {
        return {
            originDetails: {
                accessorials: []
            },
            destinationDetails: {
                accessorials: []
            },
            finishOrder: {
                quoteMaterials: [],
                pickupDate: filter('date')(pickupDate, 'yyyy-MM-dd'),
                estimatedDelivery: filter('date')(pickupDate, 'yyyy-MM-dd')
            },
            selectedProposition: {
                carrier: undefined,
                costDetailItems: []
            },
            status: 'BOOKED'
        };
    };

    var billTo = {
        creditHold: false,
        availableAmount: 200
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function ($rootScope, $controller, $filter) {
        scope = $rootScope.$new();
        filter = $filter;

        controller = $controller('CreateSalesOrderCtrl', {
            $scope: scope, ShipmentsProposalService: {
                getFreightBillPayTo: function () {
                }
            },
            ShipmentUtils: {
                isCreditLimitValid: function(){ return true; }
            }
        });

        $controller('BaseSalesOrderCtrl', {
            $scope: scope, ShipmentsProposalService: {
                getFreightBillPayTo: function () {
                }
            }, ShipmentDocumentEmailService: mockShipmentDocumentEmailService
        });

        scope.init();
    }));

    it('should initialize properly', function () {
        c_expect(controller).to.be.defined;
        c_expect(scope.wizardData.breadCrumbs.list.length).to.equal(4);
        c_expect(scope.wizardData.step).to.eql('general_information');
        c_expect(scope.wizardData.shipment).to.eql(emptyShipment(new Date()));
    });

    it('should next step and previous step work properly', function () {
        scope.wizardData.shipment.billTo = billTo;
        c_expect(scope.canNextStep()).to.equal(true);
        c_expect(scope.canPrevStep()).to.equal(false);
        c_expect(scope.isLastStep()).to.equal(false);
        scope.nextStep();
        c_expect(scope.wizardData.step).to.eql('addresses');
        c_expect(scope.canPrevStep()).to.equal(true);
        scope.$root.isFieldRequired = function() {
            return true;
        };
        c_expect(Boolean(scope.canNextStep())).to.equal(true);
        scope.nextStep();
        c_expect(scope.wizardData.step).to.eql('details');
        scope.nextStep();
        c_expect(scope.wizardData.step).to.eql('docs');
        c_expect(scope.canNextStep()).to.equal(false);
        c_expect(scope.canPrevStep()).to.equal(true);
        c_expect(scope.isLastStep()).to.equal(true);
        scope.prevStep();
        c_expect(scope.wizardData.step).to.eql('details');
    });

    it('should button "done" work properly', function () {
        scope.wizardData.shipment.billTo = billTo;
        scope.nextStep();
        c_expect(scope.canDone()).to.equal(false);
        scope.nextStep();
        scope.nextStep();
        c_expect(scope.canDone()).to.equal(true);
        scope.done();
        c_expect(scope.wizardData.step).to.eql('general_information');
    });

    it('should button "cancel" work properly', function () {
        scope.wizardData.shipment.billTo = billTo;
        toLastStep();
        scope.done();
        c_expect(scope.wizardData.step).to.eql('general_information');
    });

    it('should button "Clear All" work properly', function () {
        spyOn(scope, '$broadcast').and.callThrough();

        scope.$apply(function () {
            scope.wizardData = {};
            scope.wizardData.emptyShipment = emptyShipment(new Date());
            scope.wizardData.shipment = {
                name: 'Filled shipment',
                status: 'BOOKED'
            };
            scope.wizardData.carrierTuple = {};
        });

        scope.clearAll();

        c_expect(scope.$broadcast.calls.count()).to.equal(2);
        c_expect(scope.$broadcast.calls.mostRecent().args[0]).to.eql('event:pls-clear-form-data');

        c_expect(scope.wizardData.shipment).to.eql(scope.wizardData.emptyShipment);
        c_expect(scope.wizardData.carrierTuple).to.be.undefined;
    });
});
