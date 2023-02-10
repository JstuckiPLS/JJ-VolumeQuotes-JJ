/**
 * Tests ShipmentDetailsCtrl controller.
 * 
 * @author Sergey Kirichenko
 */
describe('ShipmentDetailsCtrl (shipment-details-controllers) Controller Test.', function() {

    // angular scope
    var scope = undefined;

    // ShipmentDetailsCtrl controller
    var controller = undefined;

    var shipment = {
        id: 1, status: 'DISPATCHED',
        finishOrder: {
            pickupDate: '2020-08-20T00:00:00.000', poNumber: 'po-num-1', puNumber: 'pu-num-1',
            pickupWindowFrom: { hours: 2, minutes: 30, am: true }, pickupWindowTo: { hours: 7, minutes: 30, am: true },
            quoteMaterials: [
                { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                    productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false }
            ]
        },
        details: {
            pickupZip: { zip: '22222', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'VA', city: 'ARLINGTON' },
            deliverZip: { zip: '10101', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'NY', city: 'NEW YORK' }
        },
        selectedProposition: { ref: 'test-reference'}
    };
    
    var shipmentWithPickupDateInPast = {
            id: 2, status: 'DISPATCHED',
            finishOrder: {
                pickupDate: '2013-08-20T00:00:00.000', poNumber: 'po-num-1', puNumber: 'pu-num-1',
                pickupWindowFrom: { hours: 2, minutes: 30, am: true }, pickupWindowTo: { hours: 7, minutes: 30, am: true },
                quoteMaterials: [
                    { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                        productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false }
                ]
            },
            details: {
                pickupZip: { zip: '22222', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'VA', city: 'ARLINGTON' },
                deliverZip: { zip: '10101', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'NY', city: 'NEW YORK' }
            },
            selectedProposition: { ref: 'test-reference'}
        };
    var shipmentWithPickupDateNull = {
        id: 3, status: 'DISPATCHED',
        finishOrder: {
            pickupDate: null, poNumber: 'po-num-1', puNumber: 'pu-num-1',
            pickupWindowFrom: { hours: 2, minutes: 30, am: true }, pickupWindowTo: { hours: 7, minutes: 30, am: true },
            quoteMaterials: [
                { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                    productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false }
            ]
        },
        details: {
            pickupZip: { zip: '22222', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'VA', city: 'ARLINGTON' },
            deliverZip: { zip: '10101', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'NY', city: 'NEW YORK' }
        },
        selectedProposition: { ref: 'test-reference'}
    };
    var shipmentWithPickupDateUndefined = {
        id: 4, status: 'DISPATCHED',
        finishOrder: {
            pickupDate: undefined, poNumber: 'po-num-1', puNumber: 'pu-num-1',
            pickupWindowFrom: { hours: 2, minutes: 30, am: true }, pickupWindowTo: { hours: 7, minutes: 30, am: true },
            quoteMaterials: [
                { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                    productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false }
            ]
        },
        details: {
            pickupZip: { zip: '22222', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'VA', city: 'ARLINGTON' },
            deliverZip: { zip: '10101', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'NY', city: 'NEW YORK' }
        },
        selectedProposition: { ref: 'test-reference'}
    };


    var mockShipmentsService = {
        getShipment: function(params, success) {
          switch(params.shipmentId){
          case 1:
            success(angular.copy(shipment));
            break;
          case 2:
            success(angular.copy(shipmentWithPickupDateInPast));
            break;
          case 3:
            success(angular.copy(shipmentWithPickupDateNull));
            break;
          case 4:
            success(angular.copy(shipmentWithPickupDateUndefined));
            break;
          }
        },
        updateStatus: function(params, status, success) {
          success('done');
        }
    };

    var dictionaryValues = [Math.random(), Math.random()];

    var mockShipmentUtils = {
        getDictionaryValues : function() {
            return dictionaryValues;
        },
        isShipmentCancellable: function(){}
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        $rootScope.redirectToUrl = function(){};
        scope.$apply(function() {
            scope.pickup = {};
            scope.deliver = {};
            scope.wizardData = {};
            scope.$root.authData = {
                organization: {orgId: 1},
                personId: 1
            };
        });
        controller = $controller('ShipmentDetailsCtrl', {$scope: scope, ShipmentOperationsService: mockShipmentsService,
            ShipmentDocumentEmailService: {}, ShipmentUtils: mockShipmentUtils});
        scope.$digest();
    }));

    it('should be initialized with default parameters', function() {
        c_expect(controller).to.be.an('object');
        c_expect(scope.shipmentDetailsModel).to.be.an('object');
        c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.false();
        c_expect(scope.isActiveShipment).to.be.a('function');
        c_expect(scope.closeShipmentDetails).to.be.a('function');
        c_expect(scope.copyShipment).to.be.a('function');
        c_expect(scope.editShipment).to.be.a('function');
        c_expect(scope.cancelShipment).to.be.a('function');
        c_expect(scope.showConfirmationCancelDialog).to.be.a('function');
        c_expect(scope.getTotalCost).to.be.a('function');
    });

    it('should open dialog and setup default data', function() {
        spyOn(scope, '$broadcast').and.callThrough();
        spyOn(mockShipmentUtils, 'getDictionaryValues').and.callThrough();
        spyOn(mockShipmentsService, 'getShipment').and.callThrough();
        scope.$root.$broadcast('event:showShipmentDetails', {shipmentId: 1});
        c_expect(scope.shipmentDetailsModel.shipmentDetailsModalOptions.parentDialog).to.be.undefined();
        c_expect(scope.shipmentDetailsModel.showCustomerUserInfo).to.be.undefined();
        c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.true();
        c_expect(scope.shipmentDetailsModel.selectedTab).to.equal('images');
        c_expect(scope.shipmentDetailsModel.closeHandler).to.be.null();
        c_expect(scope.shipmentDetailsModel.isViewMode).to.be.false();
        c_expect(scope.$broadcast.calls.count()).to.equal(1);
        c_expect(scope.$broadcast.calls.mostRecent().args).to.eql(['event:shipmentDetailsLoaded']);
        c_expect(mockShipmentUtils.getDictionaryValues.calls.count()).to.equal(1);
        c_expect(mockShipmentsService.getShipment.calls.count()).to.equal(1);
        c_expect(mockShipmentsService.getShipment.calls.mostRecent().args[0]).to.eql({customerId:1,shipmentId:1});
        c_expect(mockShipmentsService.getShipment.calls.mostRecent().args[1]).to.be.a('function');
    });

    it('should open dialog and setup all data', function() {
        spyOn(scope, '$broadcast').and.callThrough();
        spyOn(mockShipmentUtils, 'getDictionaryValues').and.callThrough();
        spyOn(mockShipmentsService, 'getShipment').and.callThrough();
        var dialogData = {
            parentDialog: 'parentDialog',
            showCustomerUserInfo: true,
            selectedTab: 'order',
            customerId: 2,
            shipmentId: 2,
            isViewMode: true,
            closeHandler: function() { /* fake function */ }
        };
        scope.$root.$broadcast('event:showShipmentDetails', dialogData);
        c_expect(scope.shipmentDetailsModel.shipmentDetailsModalOptions.parentDialog).to.equal('parentDialog');
        c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.true();
        c_expect(scope.shipmentDetailsModel.selectedTab).to.equal('order');
        c_expect(scope.shipmentDetailsModel.closeHandler).to.be.a('function');
        c_expect(scope.shipmentDetailsModel.isViewMode).to.be.true();
        c_expect(scope.$broadcast.calls.count()).to.equal(1);
        c_expect(scope.$broadcast.calls.mostRecent().args).to.eql(['event:shipmentDetailsLoaded']);
        c_expect(mockShipmentUtils.getDictionaryValues.calls.count()).to.equal(1);
        c_expect(mockShipmentsService.getShipment.calls.count()).to.equal(1);
        c_expect(mockShipmentsService.getShipment.calls.mostRecent().args[0]).to.eql({customerId:2,shipmentId:2});
        c_expect(mockShipmentsService.getShipment.calls.mostRecent().args[1]).to.be.a('function');
    });

    it('should check is shipment active', function() {
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipment = angular.copy(shipment);
            scope.shipmentDetailsModel.shipment.status = 'OPEN';
        });
        c_expect(scope.isActiveShipment()).to.be.false();
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipment.status = 'BOOKED';
        });
        c_expect(scope.isActiveShipment()).to.be.true();
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipment.status = 'DISPATCHED';
        });
        c_expect(scope.isActiveShipment()).to.be.true();
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipment.status = 'IN_TRANSIT';
        });
        c_expect(scope.isActiveShipment()).to.be.false();
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipment.status = 'DELIVERED';
        });
        c_expect(scope.isActiveShipment()).to.be.false();
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipment.status = 'CANCELLED';
        });
        c_expect(scope.isActiveShipment()).to.be.false();
        scope.$apply(function() {
            scope.shipmentDetailsModel.shipment.status = 'OUT_FOR_DELIVERY';
        });
        c_expect(scope.isActiveShipment()).to.be.false();
    });

    it('should call closeShipmentDetails properly', function() {
        scope.$apply(function() {
            scope.shipmentDetailsModel.showShipmentDetailsDialog = true;
        });
        scope.closeShipmentDetails();
        c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.false();
    });

    it('should call closeShipmentDetails with close handler', function() {
        var dialogData = {
            shipmentId: 1,
            closeHandler: function() { /* fake function */ }
        };
        spyOn(mockShipmentUtils, 'getDictionaryValues').and.callThrough();
        spyOn(mockShipmentsService, 'getShipment').and.callThrough();
        spyOn(dialogData, 'closeHandler');
        scope.$root.$broadcast('event:showShipmentDetails', dialogData);
        scope.closeShipmentDetails();
        c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.false();
        c_expect(dialogData.closeHandler.calls.count()).to.equal(1);
    });

    it('should call closeShipmentDetails avoiding call close handler', function() {
        var dialogData = {
            shipmentId: 1,
            closeHandler: function() { /* fake function */ }
        };
        spyOn(mockShipmentUtils, 'getDictionaryValues').and.callThrough();
        spyOn(mockShipmentsService, 'getShipment').and.callThrough();
        spyOn(dialogData, 'closeHandler');
        scope.$root.$broadcast('event:showShipmentDetails', dialogData);
        scope.closeShipmentDetails(true);
        c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.false();
        c_expect(dialogData.closeHandler.calls.count()).to.equal(0);
    });

    function openDetailsDialog() {
        spyOn(mockShipmentUtils, 'getDictionaryValues').and.callThrough();
        spyOn(mockShipmentsService, 'getShipment').and.callThrough();
        scope.$root.$broadcast('event:showShipmentDetails', {shipmentId: 1});
    }

    it('should not call editShipment for inactive shipment', function() {
        scope.$apply(function() {
            scope.shipmentDetailsModel.showShipmentDetailsDialog = true;
            scope.shipmentDetailsModel.shipment = angular.copy(shipment);
            scope.shipmentDetailsModel.shipment.status = 'IN_TRANSIT';
        });
        spyOn(scope.$root, '$emit');
        scope.editShipment();
        c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.true();
        c_expect(scope.$root.$emit.calls.count()).to.equal(1);
        c_expect(scope.$root.$emit.calls.mostRecent().args).to.eql(['event:application-error', 'Shipment edit failed!', 'Can\'t edit not active shipment.']);
    });

    it('should call ConfirmCancelOrderDialog properly', function() {
        spyOn(mockShipmentsService, 'updateStatus').and.callThrough();
        spyOn(scope, '$emit');
        openDetailsDialog();
        scope.cancelShipment();
        c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.false();
    });

    it('should not call cancelShipment for inactive shipment', function() {
        scope.$apply(function() {
            scope.shipmentDetailsModel.showShipmentDetailsDialog = true;
            scope.shipmentDetailsModel.shipment = angular.copy(shipment);
            scope.shipmentDetailsModel.shipment.status = 'IN_TRANSIT';
        });
        spyOn(scope, '$emit');
        scope.cancelShipment();
        c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.false();
    });

    it('should display showConfirmationCancelDialog properly', function() {
        scope.$apply(function() {
            scope.showConfirmationDialog = false;
        });
        scope.showConfirmationCancelDialog();
        c_expect(scope.showConfirmationDialog).to.be.true();
    });

    xit('should redirect to rate quote if pickup date is in past', function() {
        spyOn(scope, 'closeDialogBeforeRedirect').and.callThrough();
        spyOn(mockShipmentUtils, 'getDictionaryValues').and.callThrough();
        spyOn(mockShipmentsService, 'getShipment').and.callThrough();
        scope.$root.$broadcast('event:showShipmentDetails', {shipmentId: 2});
        scope.editShipment();
        c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.true();
        c_expect(scope.showRedirectNotification).to.be.true();
        c_expect(scope.closeDialogBeforeRedirect.calls.count()).to.equal(0);
        scope.closeNotification();
        c_expect(scope.closeDialogBeforeRedirect.calls.mostRecent().args).to.eql(['/quotes/quote', {shipmentId : 2, stepName: 'rate_quote'}]);
    });

    xit('should redirect to rate quote if pickup date is null', function() {
      spyOn(scope, 'closeDialogBeforeRedirect').and.callThrough();
      spyOn(mockShipmentUtils, 'getDictionaryValues').and.callThrough();
      spyOn(mockShipmentsService, 'getShipment').and.callThrough();
      scope.$root.$broadcast('event:showShipmentDetails', {shipmentId: 3});
      scope.editShipment();
      c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.true();
      c_expect(scope.showRedirectNotification).to.be.true();
      c_expect(scope.closeDialogBeforeRedirect.calls.count()).to.equal(0);
      scope.closeNotification();
      c_expect(scope.closeDialogBeforeRedirect.calls.mostRecent().args).to.eql(['/quotes/quote', {shipmentId : 3, stepName: 'rate_quote'}]);
  });
    xit('should redirect to rate quote if pickup date is undefined', function() {
      spyOn(scope, 'closeDialogBeforeRedirect').and.callThrough();
      spyOn(mockShipmentUtils, 'getDictionaryValues').and.callThrough();
      spyOn(mockShipmentsService, 'getShipment').and.callThrough();
      scope.$root.$broadcast('event:showShipmentDetails', {shipmentId: 4});
      scope.editShipment();
      c_expect(scope.shipmentDetailsModel.showShipmentDetailsDialog).to.be.true();
      c_expect(scope.showRedirectNotification).to.be.true();
      c_expect(scope.closeDialogBeforeRedirect.calls.count()).to.equal(0);
      scope.closeNotification();
      c_expect(scope.closeDialogBeforeRedirect.calls.mostRecent().args).to.eql(['/quotes/quote', {shipmentId : 4, stepName: 'rate_quote'}]);
  });

});