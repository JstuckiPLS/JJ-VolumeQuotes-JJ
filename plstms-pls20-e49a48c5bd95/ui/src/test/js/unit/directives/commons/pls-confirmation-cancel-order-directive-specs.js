/**
 * Tests for pls confirmation cancel order dialog directive.
 * 
 * @author: Dmitry Nikolaenko
 */
describe('PLS confirmation cancel order dialog directive test.', function() {

    var element = undefined;
    var scope = undefined;
    var cancelButton, okButton, shipmentIdLabel;
    
    var mockResponse = {data: true};

    var mockShipmentService = {
        cancelShipment : function(params, success, failure) {
        	success(mockResponse);
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/confirmation-cancel-order.html', function($provide) {
        $provide.factory('ShipmentOperationsService', function() {
            return mockShipmentService;
        });
    }));

    beforeEach(inject(function($rootScope, $compile) {
        element = angular.element('<div data-pls-confirm-cancel-order-dialog data-show-confirmation-dialog="testObject.showDialog" data-close-handler="executeCancelOrder()" data-shipment-id="testObject.testShipmentId" data-email-handler="executeCancelOrder()"></div>');

        scope = $rootScope.$new();
        scope.testObject = {
            testShipmentId: 123,
            showDialog: false
        };
        scope.executeCancelOrder = function() {
            //
        };
        $compile(element)(scope);
        scope.$digest(); 

        cancelButton = element.find('button[data-ng-click="showConfirmationDialog=false"]');
        okButton = element.find('button[data-ng-click="cancelOrder()"]');
        shipmentIdLabel = element.find('span[data-ng-bind="shipmentId"]');

        scope.$apply(function() {
            scope.testObject.showDialog = true;
        });
    }));

    it('should show confirmation cancel order dialog', function() {
        c_expect(shipmentIdLabel).to.exist;
        c_expect(shipmentIdLabel).to.contain("123");

        c_expect(element.css('display')).to.be.eql('block');
        cancelButton.click();
        c_expect(element.css('display')).to.be.eql('none');
    });
    
    it ('check execution ShipmentService#cancelShipment function after clicking ok button', function() {
        spyOn(mockShipmentService, 'cancelShipment').and.callThrough();
        c_expect(element).to.have.class('in');
        okButton.click();
        c_expect(element).not.to.have.class('in');

        c_expect(mockShipmentService.cancelShipment.calls.count()).to.equal(1);
    });
});
