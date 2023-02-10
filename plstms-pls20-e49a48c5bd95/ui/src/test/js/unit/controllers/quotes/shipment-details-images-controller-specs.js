///**
// * Tests ShipmentDetailsImagesCtrl controller.
// *
// * @author Sergey Kirichenko
// */
//describe('ShipmentDetailsImagesCtrl (shipment-details-controllers) Controller Test.', function() {
//
//    // angular scope
//    var scope = undefined;
//
//    //ShipmentDetailsImagesCtrl controller
//    var controller = undefined;
//
//    var shipment = {
//        id: 1, status: 'DISPATCHED',
//        finishOrder: {
//            pickupDate: '2013-08-20T00:00:00.000', poNumber: 'po-num-1', puNumber: 'pu-num-1',
//            pickupWindowFrom: { hours: 2, minutes: 30, am: true }, pickupWindowTo: { hours: 7, minutes: 30, am: true },
//            quoteMaterials: [
//                { weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
//                    productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false }
//            ]
//        },
//        details: {
//            pickupZip: { zip: '22222', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'VA', city: 'ARLINGTON' },
//            deliverZip: { zip: '10101', country: { id: 'USA', name: 'United States of America', dialingCode: '001' }, state: 'NY', city: 'NEW YORK' }
//        },
//        selectedProposition: { ref: 'test-reference'},
//        billTo: {id: 1}
//    };
//    var documentData = [
//        {id: 1, name: 'Document 1', date: new Date(), status: 'Active', docFileType: 'application/pdf'},
//        {id: 2, name: 'Document 2', date: new Date(), status: 'Active', docFileType: 'application/pdf'},
//        {id: 3, name: 'Document 3', date: new Date(), status: 'Active', docFileType: 'application/pdf'}
//    ];
//
//    var docToFind = undefined;
//    var docTypes = undefined;
//
//    var mockShipmentDocumentService = {
//        getDocumentList: function(params, success) {
//            success(docToFind);
//        },
//        getDocumentTypesList: function(params, success) {
//            success(docTypes);
//        }
//    };
//
//    beforeEach(module('plsApp'));
//
//    beforeEach(inject(function($rootScope, $controller) {
//        scope = $rootScope.$new();
//        scope.$apply(function() {
//            scope.shipmentDetailsModel = {
//                shipment: angular.copy(shipment),
//                loadingIndicator: {},
//                fullViewDocOption: {},
//                emailOptions: {},
//                customer: {id: 1},
//                customerId: 1,
//                userId: 1
//            };
//            scope.closeDialogBeforeRedirect = function() { /*fake function*/ };
//        });
//        docToFind = documentData;
//        controller = $controller('ShipmentDetailsImagesCtrl', {$scope: scope, ShipmentDocumentService: mockShipmentDocumentService});
//        scope.$digest();
//    }));
//
//    it('should be initialized with default parameters', function() {
//        c_expect(controller).to.be.an('object');
//        c_expect(scope.shipmentDetailsModel.selectedDocuments).to.be.an('array');
//        c_expect(scope.shipmentDetailsModel.selectedDocuments).to.be.empty();
//        c_expect(scope.shipmentDetailsModel.documentOptions).to.be.an('object');
//        c_expect(scope.initTab).to.be.a('function');
//        c_expect(scope.selectDocument).to.be.a('function');
//        c_expect(scope.isOneSelected).to.be.a('function');
//        c_expect(scope.isDocumentSelectedForAction).to.be.a('function');
//        c_expect(scope.viewDocument).to.be.a('function');
//        c_expect(scope.printDocument).to.be.a('function');
//        c_expect(scope.sendDocuments).to.be.a('function');
//        c_expect(scope.shipmentDetailsModel.shipmentDocumentGridOptions).to.be.an('object');
//    });
//
//    it('should call initTab properly', function() {
//        spyOn(mockShipmentDocumentService, 'getDocumentList').and.callThrough();
//        spyOn(mockShipmentDocumentService, 'getDocumentTypesList').and.callThrough();
//        spyOn(scope, 'selectDocument');
//        scope.initTab();
//        c_expect(mockShipmentDocumentService.getDocumentList.calls.count()).to.equal(1);
//        c_expect(mockShipmentDocumentService.getDocumentList.calls.mostRecent().args[0]).to.eql({shipmentId: 1});
//        c_expect(mockShipmentDocumentService.getDocumentList.calls.mostRecent().args[1]).to.be.a('function');
//    });
//
//    it('should call initTab properly and select BOL', function() {
//        docToFind = [
//            {id: 1, name: 'Document 1', date: new Date(), status: 'Active', docFileType: 'application/pdf'},
//            {id: 2, name: 'BOL', date: new Date(), status: 'Active', docFileType: 'application/pdf'},
//            {id: 3, name: 'Document 3', date: new Date(), status: 'Active', docFileType: 'application/pdf'}
//        ];
//        spyOn(mockShipmentDocumentService, 'getDocumentList').and.callThrough();
//        spyOn(scope, 'selectDocument');
//        scope.initTab();
//        c_expect(mockShipmentDocumentService.getDocumentList.calls.count()).to.equal(1);
//        c_expect(mockShipmentDocumentService.getDocumentList.calls.mostRecent().args[0]).to.eql({shipmentId: 1});
//        c_expect(mockShipmentDocumentService.getDocumentList.calls.mostRecent().args[1]).to.be.a('function');
//    });
//
//    it('should call initTab on event', function() {
//        spyOn(scope, 'initTab');
//        scope.$broadcast('event:shipmentDetailsLoaded');
//        c_expect(scope.initTab.calls.count()).to.equal(1);
//    });
//
//    it('should call selectDocument properly', function() {
//        scope.$apply(function() {
//            scope.shipmentDetailsModel.selectedDocuments[0] = documentData[0];
//        });
//        scope.selectDocument();
//        c_expect(scope.shipmentDetailsModel.documentOptions.pdfLocation).to.equal('/restful/customer/shipmentdocs/1');
//    });
//
//    it('should check is one document selected', function() {
//        scope.$apply(function() {
//            scope.shipmentDetailsModel.selectedDocuments = [1];
//        });
//        c_expect(scope.isOneSelected()).to.be.true();
//        scope.$apply(function() {
//            scope.shipmentDetailsModel.selectedDocuments = [];
//        });
//        c_expect(scope.isOneSelected()).to.be.false();
//        scope.$apply(function() {
//            scope.shipmentDetailsModel.selectedDocuments = [1,2];
//        });
//        c_expect(scope.isOneSelected()).to.be.false();
//    });
//
//    it('should call viewDocument properly', function() {
//        scope.$apply(function() {
//            scope.shipmentDetailsModel.shipmentDocumentGridData = documentData;
//        });
//        scope.viewDocument();
//        c_expect(scope.shipmentDetailsModel.documentOptions.hidePdf).to.be.false();
//        c_expect(scope.shipmentDetailsModel.showFullViewDocumentDialog).to.be.undefined();
//        c_expect(scope.shipmentDetailsModel.fullViewDocOption.imageContent).to.be.undefined();
//        c_expect(scope.shipmentDetailsModel.fullViewDocOption.pdfLocation).to.be.undefined();
//
//        scope.$apply(function() {
//            scope.shipmentDetailsModel.shipmentDocumentGridData = documentData;
//            scope.shipmentDetailsModel.selectedDocuments[0] = documentData[0];
//        });
//        scope.viewDocument();
//        c_expect(scope.shipmentDetailsModel.documentOptions.hidePdf).to.be.true();
//        c_expect(scope.shipmentDetailsModel.showFullViewDocumentDialog).to.be.true();
//        c_expect(scope.shipmentDetailsModel.fullViewDocOption.imageContent).to.be.false();
//        c_expect(scope.shipmentDetailsModel.fullViewDocOption.pdfLocation).to.equal('/restful/customer/shipmentdocs/1');
//    });
//
//    it('should call viewDocument for not pdf', function() {
//        scope.$apply(function() {
//            scope.shipmentDetailsModel.shipmentDocumentGridData = [
//                {id: 1, name: 'Document 1', date: new Date(), status: 'Active', docFileType: 'png'}
//            ];
//            scope.shipmentDetailsModel.selectedDocuments[0] = documentData[0];
//        });
//        scope.viewDocument();
//        c_expect(scope.shipmentDetailsModel.documentOptions.hidePdf).to.be.true();
//        c_expect(scope.shipmentDetailsModel.showFullViewDocumentDialog).to.be.true();
//        c_expect(scope.shipmentDetailsModel.fullViewDocOption.imageContent).to.be.true();
//        c_expect(scope.shipmentDetailsModel.fullViewDocOption.pdfLocation).to.equal('/restful/customer/shipmentdocs/1');
//    });
//
//});