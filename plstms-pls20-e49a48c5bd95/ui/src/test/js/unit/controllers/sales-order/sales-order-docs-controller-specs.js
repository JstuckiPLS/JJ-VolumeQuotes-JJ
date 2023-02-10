///**
// * Tests for sales orders docs controller.
// *
// * @author Denis Zhupinsky
// */
//describe('SODocsCtrl (Sales Orders Docs) Controller Test.', function () {
//
//    // ANGULAR SERVICES
//    var scope = undefined;
//    var rootParams = undefined;
//    var rootScope;
//
//    //QuoteWizard controller
//    var controller = undefined;
//    var localUrlConfig = undefined;
//
//    var notInitSalesOrder = false;
//
//    function BreadCrumb(id, label) {
//        this.id = id;
//        this.label = label;
//
//        this.prev = undefined;
//        this.next = undefined;
//        this.validNext = undefined;
//        this.nextAction = undefined;
//        this.validDone = undefined;
//        this.doneAction = undefined;
//    }
//
//
//    var mockShipment = {shipmentId: 1, ediDispatch: true, billTo: {paymentMethod : ""}};
//    var mockBolDoc = {value: Math.floor((Math.random() * 100) + 1), label : 'BOL'};
//    var mockShipmentDocs = [];
//    mockShipmentDocs.push(mockBolDoc);
//
//    var mockShipmentSavingService = {
//        save: function (params, shipment, successCallback) {
//            successCallback(mockShipment);
//        }
//    };
//
//    var mockDocs = [
//        {docId: 1}
//    ];
//    
//    var mockCanDownload = {data: true};
//
//    var mockShipmentDocumentService = {
//        getDocumentList: function (params, successCallback) {
//            successCallback(mockDocs);
//        },
//        removeTemporaryDocument: function (params, successCallback) {
//            successCallback();
//        },
//        canDownload: function (params, successCallback) {
//        	successCallback(mockCanDownload);
//        },
//        prepareDocsForShipment : function(params, shipment, successCallback) {
//            successCallback(mockShipmentDocs);
//        },
//        getDocumentTypesList: function (params, successCallback) {
//            successCallback(docTypesWithBol);
//        }
//    };
//
//    var mockBillToDocumentService = {
//            query: function (params, successCallback) {
//                successCallback();
//            }
//    };
//
//    var docTypes = [
//        {value: 'etc', label: 'etc'},
//        {value: 'INVOICE', label:'Invoices'},
//        {value: 'Miscellaneous', label: 'Miscellaneous'},
//        {value: 'POD', label: 'Proof of delivery'},
//        {value: 'UNKNOWN', label: 'Unknow'},
//        {value: 'W9_DOCUMENT', label: 'W9 Document'}
//    ];
//
//    var docTypesWithBol = docTypes.slice(0);
//    docTypesWithBol.push({value: 'BOL', label : 'BOL'});
//
//    var authData = {
//        fullName: 'Test full name',
//        organization: {orgId: Math.floor((Math.random() * 100) + 1)},
//        personId: Math.floor((Math.random() * 100) + 1)
//    };
//
//    var selectedCustomer = {
//        id: authData.organization.orgId
//    };
//
//    var mockShipmentDocumentEmailService = {
//            
//    };
//
//    beforeEach(module('plsApp'));
//
//    beforeEach(inject(function ($rootScope, $controller, urlConfig, NgGridPluginFactory, $q, DateTimeUtils, $injector) {
//        scope = $rootScope.$new();
//        rootScope = $injector.get('$rootScope');
//
//        localUrlConfig = urlConfig;
//
//        scope.authData = authData;
//
//        scope.wizardData = {};
//        scope.wizardData.selectedCustomer = selectedCustomer;
//        scope.wizardData.emptyShipment = {
//            details: {
//                accessorials: []
//            },
//            finishOrder: {
//                quoteMaterials: [],
//                pickupDate: JSON.stringify(new Date()).replace(/\"/g, ''),
//                estimatedDelivery: JSON.stringify(new Date()).replace(/\"/g, '')
//            },
//            selectedProposition: {
//                carrier: {ediCapable: true},
//                ref: '',
//                costDetailItems: []
//            },
//            billTo: {
//                id: undefined
//            },
//            status: 'BOOKED'
//        };
//        scope.wizardData.shipment = angular.copy(scope.wizardData.emptyShipment);
//        if (!notInitSalesOrder) {
//            scope.editSalesOrderModel = {};
//        }
//
//        scope.fullViewDocModel = {
//            fullViewDocOption: {}
//        };
//
//        scope.docOptions = {};
//
//        scope.emailOptions = {};
//
//        function addBreadCrumb(id, label) {
//            var breadCrumb = new BreadCrumb(id, label);
//            if (scope.wizardData.breadCrumbs.list.length !== 0) {
//                var prevBreadCrumb = scope.wizardData.breadCrumbs.list[scope.wizardData.breadCrumbs.list.length - 1];
//                breadCrumb.prev = prevBreadCrumb;
//                prevBreadCrumb.next = breadCrumb;
//            }
//
//            scope.wizardData.breadCrumbs.list.push(breadCrumb);
//            scope.wizardData.breadCrumbs.map[breadCrumb.id] = breadCrumb;
//        }
//
//        scope.wizardData.breadCrumbs = {};
//        scope.wizardData.breadCrumbs.list = [];
//        scope.wizardData.breadCrumbs.map = {};
//
//        addBreadCrumb('general_information');
//        addBreadCrumb('addresses');
//        addBreadCrumb('details');
//        addBreadCrumb('docs');
//        addBreadCrumb('notes');
//        addBreadCrumb('ok_click');
//
//        scope.wizardData.step = 'docs';
//
//        controller = $controller('SODocsCtrl', {$scope: scope, ShipmentDocumentService: mockShipmentDocumentService,
//            ShipmentDocumentService: mockShipmentDocumentService, NgGridPluginFactory: NgGridPluginFactory, urlConfig: urlConfig,
//            ShipmentSavingService: mockShipmentSavingService, $q: $q, DateTimeUtils: DateTimeUtils,
//            BillToDocumentService: mockBillToDocumentService, ShipmentDocumentEmailService: mockShipmentDocumentEmailService});
//
//
//    }));
//
//    it('should test controller creation', function () {
//        c_expect(controller).to.be.defined;
//        c_expect(scope.docOptions).to.be.defined;
//        c_expect(scope.fullViewDocModel.shipmentFullViewDocumentModalOptions).to.be.defined;
//        c_expect(scope.docsGridColumnDefs).to.be.defined;
//        c_expect(scope.docsGrid).to.be.defined;
//        c_expect(scope.uploadModel).to.be.defined;
//
//        var uploadUrl = localUrlConfig.shipment + '/customer/shipmentdocs/temp';
//
//        c_expect(scope.uploadModel.uploadUrl).to.equal(uploadUrl);
//        c_expect(scope.uploadModel.setFile).to.be.a('function');
//        c_expect(scope.uploadModel.uploadCallback).to.be.a('function');
//    });
//
//    it('should get data for docs table', function () {
//        spyOn(mockShipmentDocumentService, 'getDocumentList').and.callThrough();
//        scope.$apply(function () {
//            scope.wizardData.shipment.id = Math.floor((Math.random() * 100) + 1);
//        });
//
//        scope.$broadcast('event:edit-sales-order-tab-change', 'docs');
//
//        c_expect(mockShipmentDocumentService.getDocumentList.calls.count()).to.equal(1);
//        c_expect(mockShipmentDocumentService.getDocumentList.calls.mostRecent().args[0]).to.deep.equal({shipmentId: scope.wizardData.shipment.id});
//        c_expect(scope.wizardData.docsGridData).to.equal(mockDocs);
//    });
//
//    it('prepare data for next test', function(){
//        notInitSalesOrder = true;
//    });
//
//    it('should get data for docs table for new shipment', function () {
//        c_expect(scope.wizardData.docsGridData).to.be.defined;
//        c_expect(scope.wizardData.docsGridData.length).to.equal(1);
//        c_expect(scope.wizardData.docsGridData[0].name).to.equal('BOL');
//        c_expect(scope.wizardData.docsGridData[0].tempDocId).to.equal(mockBolDoc.value);
//        c_expect(scope.wizardData.docsGridData[0].docFileType).to.equal('application/pdf');
//        c_expect(scope.wizardData.docsGridData[0].createdByName).to.equal('Auto-generated');
//        notInitSalesOrder = false;
//    });
//
//    it('should init data', function () {
//        spyOn(mockShipmentDocumentService, 'getDocumentTypesList').and.callThrough();
//        spyOn(mockShipmentDocumentService, 'getDocumentList').and.callThrough();
//        spyOn(mockShipmentDocumentService, 'canDownload').and.callThrough();
//        scope.$apply(function () {
//            scope.wizardData.shipment.id = Math.floor((Math.random() * 100) + 1);
//        });
//
//        scope.$broadcast('event:edit-sales-order-tab-change', 'docs');
//        scope.init();
//
//        c_expect(mockShipmentDocumentService.getDocumentList.calls.count()).to.equal(1);
//        c_expect(mockShipmentDocumentService.getDocumentTypesList.calls.count()).to.equal(1);
//        c_expect(scope.wizardData.documentTypes).to.deep.equal(docTypes);
//        c_expect(scope.isDownloadEnabled).equal(true);
//
//        c_expect(scope.wizardData.breadCrumbs.map[scope.wizardData.step].doneAction).to.be.a('function');
//    });
//
//    it('should do done actions that were initialized by init function', function () {
//        spyOn(mockShipmentSavingService, 'save').and.callThrough();
//        spyOn(scope.$root, '$emit').and.callThrough();
//
//        scope.$digest();
//
//        scope.init();
//        scope.wizardData.breadCrumbs.map[scope.wizardData.step].doneAction();
//
//        c_expect(mockShipmentSavingService.save.calls.count()).to.equal(1);
//        c_expect(mockShipmentSavingService.save.calls.mostRecent().args[0]).to.deep.equal({customerId: selectedCustomer.id, hideCreatedTime: undefined});
//    });
//
//    it('should check if add disabled', function () {
//        c_expect(scope.isAddDisabled()).to.be.true;
//    });
//
//    it('should check if add disabled - should not', function () {
//        scope.$apply(function () {
//            scope.uploadModel.uploadFile = true;
//            scope.uploadModel.selectedDocType = true;
//        });
//
//        c_expect(scope.isAddDisabled()).to.be.false;
//    });
//
//    it('should set file to upload', function () {
//        var fileName = 'test.pdf';
//        scope.uploadModel.setFile({value: fileName, files: [{size: 2.5*1024*1024, name: 'test.pDf'}]});
//
//        c_expect(scope.uploadModel.uploadFile).to.equal(fileName);
//    });
//
//    it('should not set file to upload with invalid extension', function () {
//        spyOn(scope.$root, '$emit').and.callThrough();
//        scope.uploadModel.setFile({value: 'test.pdf', files: [{size: 2.5*1024*1024, name: 'test.xls'}]});
//
//        c_expect(scope.uploadModel.uploadFile).to.be.undefined;
//        c_expect(scope.$root.$emit.calls.count()).to.equal(1);
//        c_expect(scope.$root.$emit.calls.mostRecent().args[0]).to.equal('event:application-error');
//    });
//
//    it('should not set file to upload with invalid file size', function () {
//        spyOn(scope.$root, '$emit').and.callThrough();
//        scope.uploadModel.setFile({value: 'test.pdf', files: [{size: 2.5*1024*1024 + 1, name: 'test.pdf'}]});
//
//        c_expect(scope.uploadModel.uploadFile).to.be.undefined;
//        c_expect(scope.$root.$emit.calls.count()).to.equal(1);
//        c_expect(scope.$root.$emit.calls.mostRecent().args[0]).to.equal('event:application-error');
//    });
//
//    it('should set data with callback after upload', function () {
//        var selectedDocTypeLabel = 'Test label';
//        var selectedDocTypeValue = 'Test value';
//        var uploadFile = {value: 'test.pdf'};
//        scope.$apply(function () {
//            scope.progressPanelOptions.showPanel = true;
//            scope.uploadModel.selectedDocType = {value: selectedDocTypeValue, label: selectedDocTypeLabel};
//            scope.uploadModel.uploadFile = uploadFile;
//        });
//
//        scope.uploadModel.uploadCallback('{"success" : "true", "tempDocId" : 123}', true);
//
//        c_expect(scope.progressPanelOptions.showPanel).to.be.false;
//        c_expect(scope.wizardData.docsGridData.length).to.equal(1);
//        var element = scope.wizardData.docsGridData[0];
//
//        c_expect(scope.uploadModel.selectedDocType).to.be.undefined;
//        c_expect(scope.uploadModel.uploadFile).to.be.undefined;
//        c_expect(element.name).to.equal(selectedDocTypeLabel);
//        c_expect(element.createdByName).to.equal(scope.authData.fullName);
//        c_expect(element.tempDocId).to.equal(123);
//        c_expect(element.tempFileName).to.equal(uploadFile);
//
//        c_expect(scope.wizardData.shipment.uploadedDocuments.length).to.equal(1);
//        var element = scope.wizardData.shipment.uploadedDocuments[0];
//        c_expect(element.id).to.equal(123);
//        c_expect(element.fileName).to.equal(uploadFile);
//        c_expect(element.docType).to.equal(selectedDocTypeValue);
//    });
//
//    it('should set data with callback after upload - not completed', function () {
//        scope.$apply(function () {
//            scope.progressPanelOptions.showPanel = true;
//            scope.uploadModel.uploadFile = {};
//        });
//
//        scope.uploadModel.uploadCallback('{"success" : "true", "tempDocId" : 123}', false);
//
//        c_expect(scope.progressPanelOptions.showPanel).to.be.true;
//        c_expect(scope.wizardData.docsGridData.length).to.equal(0);
//        c_expect(scope.wizardData.shipment.uploadedDocuments).to.be.undefined;
//    });
//
//    it('should set data with callback after upload - cannot parse json response', function () {
//        spyOn(scope.$root, '$emit').and.callThrough();
//
//        scope.$apply(function () {
//            scope.progressPanelOptions.showPanel = true;
//            scope.uploadModel.uploadFile = {};
//        });
//
//        scope.uploadModel.uploadCallback('not correct json', true);
//
//        c_expect(scope.progressPanelOptions.showPanel).to.be.false;
//        c_expect(scope.wizardData.docsGridData.length).to.equal(0);
//        c_expect(scope.wizardData.shipment.uploadedDocuments).to.be.undefined;
//
//        c_expect(scope.$root.$emit.calls.count()).to.equal(1);
//        c_expect(scope.$root.$emit.calls.mostRecent().args[0]).to.equal('event:application-error');
//    });
//
//    it('should fire "event:application-error" event in case of 401', function() {
//        scope.$apply(function () {
//            scope.progressPanelOptions.showPanel = true;
//            scope.uploadModel.uploadFile = {};
//        });
//
//        spyOn(scope.$root, '$broadcast').and.callThrough();
//
//        scope.uploadModel.uploadCallback('HTTP Status 401 - Unauthorized description This request requires HTTP authentication Apache Tomcat/7.0.42', true);
//
//        c_expect(scope.progressPanelOptions.showPanel).to.be.false;
//        c_expect(scope.wizardData.docsGridData.length).to.equal(0);
//        c_expect(scope.wizardData.shipment.uploadedDocuments).to.be.undefined;
//
//        c_expect(scope.$root.$broadcast.calls.count()).to.equal(1);
//        c_expect(scope.$root.$broadcast.calls.mostRecent().args[0]).to.equal('event:auth-loginRequired');
//    });
//
//    it('should set data with callback after upload - size exceeded', function () {
//        spyOn(scope.$root, '$emit').and.callThrough();
//
//        scope.$apply(function () {
//            scope.progressPanelOptions.showPanel = true;
//        });
//
//        scope.uploadModel.uploadCallback('{"success" : "false", "limitSizeExceeded" : "true"}', true);
//
//        c_expect(scope.progressPanelOptions.showPanel).to.be.false;
//        c_expect(scope.wizardData.docsGridData.length).to.equal(0);
//        c_expect(scope.wizardData.shipment.uploadedDocuments).to.be.undefined;
//
//        c_expect(scope.$root.$emit.calls.count()).to.equal(1);
//        c_expect(scope.$root.$emit.calls.mostRecent().args[0]).to.equal('event:application-error');
//    });
//
//    it('should upload submit', function () {
//        scope.uploadSubmit();
//
//        c_expect(scope.uploadSubmit()).to.be.true;
//        c_expect(scope.progressPanelOptions.showPanel).to.be.true;
//    });
//
//    it('should define if document is an image - tempfile is an image', function () {
//        scope.$apply(function () {
//            scope.selectedDocs = [
//                {tempFileName: 'test.png'}
//            ];
//        });
//
//        c_expect(scope.isDocumentImage()).to.be.true;
//    });
//
//    it('should define if document is an image - tempfile is not an image', function () {
//        scope.$apply(function () {
//            scope.selectedDocs = [
//                {tempFileName: 'test.pdf'}
//            ];
//        });
//
//        c_expect(scope.isDocumentImage()).to.be.false;
//    });
//
//    it('should define if document is an image - doc file is an image', function () {
//        scope.$apply(function () {
//            scope.selectedDocs = [
//                {docFileType: 'png'}
//            ];
//        });
//
//        c_expect(scope.isDocumentImage()).to.be.true;
//    });
//
//    it('should define if document is an image - doc file is not an image', function () {
//        scope.$apply(function () {
//            scope.selectedDocs = [
//                {docFileType: 'application/pdf'}
//            ];
//        });
//
//        c_expect(scope.isDocumentImage()).to.be.false;
//    });
//
//    it('should show content of temporary document', function () {
//        var tempDocId = Math.floor((Math.random() * 100) + 1);
//        scope.$apply(function () {
//            scope.selectedDocs = [
//                {tempFileName: 'test.pdf', tempDocId: tempDocId}
//            ];
//        });
//        scope.showDocumentContent();
//
//        c_expect(scope.docOptions.imageContent).to.be.false;
//
//        var pdfLocation = localUrlConfig.shipment + '/customer/shipmentdocs/' + tempDocId;
//        c_expect(scope.docOptions.pdfLocation.indexOf(pdfLocation)).to.equal(0);
//
//        c_expect(scope.docOptions.hidePdf).to.be.false;
//    });
//
//    it('should show content of already stored document', function () {
//        var docId = Math.floor((Math.random() * 100) + 1);
//        var shipmentId = Math.floor((Math.random() * 100) + 1);
//        scope.$apply(function () {
//            scope.selectedDocs = [
//                {tempFileName: 'test.pdf', id: docId}
//            ];
//            scope.wizardData.shipment.id = shipmentId;
//        });
//        scope.showDocumentContent();
//
//        c_expect(scope.docOptions.imageContent).to.be.false;
//
//        var pdfLocation = localUrlConfig.shipment + '/customer/shipmentdocs/' + docId;
//        c_expect(scope.docOptions.pdfLocation.indexOf(pdfLocation)).to.equal(0);
//
//        c_expect(scope.docOptions.hidePdf).to.be.false;
//    });
//
//    it('should close content of document', function () {
//        scope.showDocumentContent();
//
//        c_expect(scope.docOptions.pdfLocation).to.be.null;
//    });
//
//    it('should show content of temporary document', function () {
//        var tempDocId = Math.floor((Math.random() * 100) + 1);
//        scope.$apply(function () {
//            scope.selectedDocs = [
//                {tempFileName: 'test.pdf', tempDocId: tempDocId}
//            ];
//        });
//        scope.showDocumentContent();
//
//        c_expect(scope.docOptions.imageContent).to.be.false;
//
//        var pdfLocation = localUrlConfig.shipment + '/customer/shipmentdocs/' + tempDocId;
//        c_expect(scope.docOptions.pdfLocation.indexOf(pdfLocation)).to.equal(0);
//
//        c_expect(scope.docOptions.hidePdf).to.be.false;
//    });
//
//    it('should show content of temporary document in full view mode', function () {
//        var tempDocId = Math.floor((Math.random() * 100) + 1);
//        scope.$apply(function () {
//            scope.selectedDocs = [
//                {tempFileName: 'test.pdf', tempDocId: tempDocId}
//            ];
//        });
//        scope.viewDocument();
//
//        c_expect(scope.fullViewDocModel.showFullViewDocumentDialog).to.be.true;
//        c_expect(scope.fullViewDocModel.fullViewDocOption.imageContent).to.be.false;
//
//        var pdfLocation = localUrlConfig.shipment + '/customer/shipmentdocs/' + tempDocId;
//        c_expect(scope.fullViewDocModel.fullViewDocOption.pdfLocation.indexOf(pdfLocation)).to.equal(0);
//
//        c_expect(scope.fullViewDocModel.fullViewDocOption.hidePdf).to.be.undefined;
//        c_expect(scope.docOptions.hidePdf).to.be.true;
//    });
//
//    it('should show content of already stored document in full view mode', function () {
//        var docId = Math.floor((Math.random() * 100) + 1);
//        var shipmentId = Math.floor((Math.random() * 100) + 1);
//        scope.$apply(function () {
//            scope.selectedDocs = [
//                {tempFileName: 'test.pdf', id: docId}
//            ];
//            scope.wizardData.shipment.id = shipmentId;
//        });
//        scope.viewDocument();
//
//        c_expect(scope.fullViewDocModel.showFullViewDocumentDialog).to.be.true;
//        c_expect(scope.fullViewDocModel.fullViewDocOption.imageContent).to.be.false;
//
//        var pdfLocation = localUrlConfig.shipment + '/customer/shipmentdocs/' + docId;
//        c_expect(scope.fullViewDocModel.fullViewDocOption.pdfLocation.indexOf(pdfLocation)).to.equal(0);
//
//        c_expect(scope.fullViewDocModel.fullViewDocOption.hidePdf).to.be.undefined;
//        c_expect(scope.docOptions.hidePdf).to.be.true;
//    });
//
//    it('should close content of document in full view mode', function () {
//        scope.viewDocument();
//
//        c_expect(scope.fullViewDocModel.fullViewDocOption.pdfLocation).to.be.null;
//    });
//
//    it('should close full view document', function () {
//        scope.fullViewDocModel.closeFullViewDocument();
//
//        c_expect(scope.fullViewDocModel.showFullViewDocumentDialog).to.be.false;
//        c_expect(scope.fullViewDocModel.fullViewDocOption.pdfLocation).to.be.null;
//        c_expect(scope.fullViewDocModel.fullViewDocOption.hidePdf).to.be.undefined;
//        c_expect(scope.docOptions.hidePdf).to.be.false;
//    });
//
//    it('should delete temp document', function () {
//
//        var tempDocId = 1000;
//        var selectedDocument = {tempFileName: 'test.pdf', tempDocId: tempDocId};
//
//        spyOn(mockShipmentDocumentService, 'removeTemporaryDocument').and.callThrough();
//        scope.$apply(function () {
//            scope.selectedDocs = [selectedDocument];
//            scope.wizardData.docsGridData = [
//                {docsData: 'Mock data'},
//                selectedDocument
//            ];
//
//            scope.wizardData.shipment.uploadedDocuments = [
//                {
//                    id: 2000, docsData: 'Mock data should be left'
//                },
//                {
//                    id: tempDocId, docsData: 'Mock data'
//                }
//            ];
//        });
//
//        scope.deleteDocument();
//
//        c_expect(mockShipmentDocumentService.removeTemporaryDocument.calls.count()).to.equal(1);
//        c_expect(mockShipmentDocumentService.removeTemporaryDocument.calls.mostRecent().args[0]).to.deep.equal({docId: tempDocId});
//
//        c_expect(scope.wizardData.docsGridData.length).to.equal(1);
//        c_expect(scope.wizardData.docsGridData[0]).not.to.equal(selectedDocument);
//        c_expect(scope.wizardData.shipment.uploadedDocuments.length).to.equal(1);
//        c_expect(scope.wizardData.shipment.uploadedDocuments[0].id).not.to.equal(tempDocId);
//
//        c_expect(scope.docOptions.hidePdf).to.be.true;
//        c_expect(scope.progressPanelOptions.showPanel).to.be.false;
//    });
//
//    it('should delete already stored document', function () {
//        spyOn(mockShipmentDocumentService, 'removeTemporaryDocument').and.callThrough();
//        var tempDocId = Math.floor((Math.random() * 100) + 1);
//        var selectedDocument = {tempFileName: 'test.pdf', id: tempDocId};
//
//        scope.$apply(function () {
//            scope.selectedDocs = [selectedDocument];
//            scope.wizardData.docsGridData = [
//                {docsData: 'Mock data'},
//                selectedDocument
//            ];
//        });
//
//        scope.deleteDocument();
//
//        c_expect(mockShipmentDocumentService.removeTemporaryDocument.calls.count()).to.equal(0);
//        c_expect(scope.wizardData.docsGridData.length).to.equal(1);
//        c_expect(scope.wizardData.docsGridData[0]).not.to.equal(selectedDocument);
//
//        c_expect(scope.wizardData.shipment.removedDocumentsIds.length).to.equal(1);
//        c_expect(scope.wizardData.shipment.removedDocumentsIds[0]).to.equal(tempDocId);
//
//        c_expect(scope.docOptions.hidePdf).to.be.true;
//
//    });
//
//    it('should not start delete process if document was not selected', function () {
//        spyOn(mockShipmentDocumentService, 'removeTemporaryDocument').and.callThrough();
//        var tempDocId = Math.floor((Math.random() * 100) + 1);
//        var document = {tempFileName: 'test.pdf', id: tempDocId};
//
//        scope.$apply(function () {
//            scope.wizardData.docsGridData = [
//                {docsData: 'Mock data'},
//                document
//            ];
//        });
//
//        scope.deleteDocument();
//
//        c_expect(mockShipmentDocumentService.removeTemporaryDocument.calls.count()).to.equal(0);
//        c_expect(scope.wizardData.docsGridData.length).to.equal(2);
//
//        c_expect(scope.wizardData.shipment.uploadedDocuments).to.be.undefined;
//        c_expect(scope.wizardData.shipment.removedDocumentsIds).to.be.undefined;
//        c_expect(scope.docOptions.hidePdf).to.be.true;
//    });
//
//    it("is can or can't edit freight bill date", function () {
//        scope.requiredDocuments = 
//            [{
//                 customerRequestType: "REQUIRED",
//                 documentType: "VENDOR BILL",
//                 documentTypeDescription: "VENDOR BILL",
//                 id:1 
//            }];
//        scope.docsTableDataLoaded = true;
//        c_expect(scope.wizardData.shipment.isVendorBillMatched).to.be.undefined;
//        c_expect(scope.requiredDocuments.length).to.equal(1);
//        spyOn(scope.$root, 'isFieldRequired').and.callThrough();
//        c_expect(scope.$root.isFieldRequired('EDIT_FREIGHT_BILL_DATE')).to.be.false;
//        c_expect(scope.isCanEditFreightBillDate()).to.be.false;
//
//        rootScope.authData.privilegies.push('EDIT_FREIGHT_BILL_DATE');
//        scope.wizardData.shipment.isVendorBillMatched = false;
//        c_expect(scope.$root.isFieldRequired('EDIT_FREIGHT_BILL_DATE')).to.be.true;
//        c_expect(scope.isCanEditFreightBillDate()).to.be.false;
//
//        c_expect(scope.requiredDocuments.length).to.equal(1);
//        c_expect(scope.$root.isFieldRequired('EDIT_FREIGHT_BILL_DATE')).to.be.true;
//        c_expect(scope.requiredDocuments.length).to.equal(1);
//        c_expect(scope.wizardData.shipment.isVendorBillMatched).to.be.false;
//        c_expect(scope.isCanEditFreightBillDate()).to.be.false;
//
//        scope.wizardData.shipment.isVendorBillMatched = true;
//        c_expect(scope.$root.isFieldRequired('EDIT_FREIGHT_BILL_DATE')).to.be.true;
//        c_expect(scope.requiredDocuments.length).to.equal(1);
//        c_expect(scope.wizardData.shipment.isVendorBillMatched).to.be.true;
//        c_expect(scope.isCanEditFreightBillDate()).to.be.false;
//
//        scope.requiredDocuments.length = 0;
//        scope.wizardData.shipment.finishOrder.actualDeliveryDate = new Date();
//        c_expect(scope.$root.isFieldRequired('EDIT_FREIGHT_BILL_DATE')).to.be.true;
//        c_expect(scope.requiredDocuments.length).to.equal(0);
//        c_expect(scope.wizardData.shipment.isVendorBillMatched).to.be.true;
//        var ddd = scope.isCanEditFreightBillDate();
//        c_expect(scope.isCanEditFreightBillDate()).to.be.true;
//    });
//
//    it("update freight bill date if all doc present and  VendorBill Matched", function () {
//        scope.$apply(function () {
//        scope.wizardData.shipment.freightBillDate = undefined;
//        scope.requiredDocuments = 
//            [{
//                 customerRequestType: "REQUIRED",
//                 documentType: "VENDOR BILL",
//                 documentTypeDescription: "VENDOR BILL",
//                 id:1 
//            }];
//        scope.docsTableDataLoaded = true;
//        });
//        c_expect(scope.wizardData.shipment.freightBillDate).to.be.undefined;
//        scope.$apply(function () {
//            scope.wizardData.shipment.isVendorBillMatched = true;
//            scope.wizardData.shipment.finishOrder.actualDeliveryDate = new Date();
//            scope.isDocChangesByHand = true;
//            rootScope.authData.privilegies.push('EDIT_FREIGHT_BILL_DATE');
//            scope.requiredDocuments.length = 0;
//        });
//        c_expect(scope.wizardData.shipment.freightBillDate).not.to.be.undefined;
//        c_expect(scope.requiredDocuments.length).to.equal(0);
//        c_expect(scope.isCanEditFreightBillDate()).to.be.true;
//    });
//
//    it("update freight bill date if VendorBill unMatched", function () {
//        scope.$apply(function () {
//        scope.requiredDocuments = 
//            [{
//                 customerRequestType: "REQUIRED",
//                 documentType: "VENDOR BILL",
//                 documentTypeDescription: "VENDOR BILL",
//                 id:1 
//            }];
//        scope.docsTableDataLoaded = true;
//        });
//        scope.$apply(function () {
//            scope.wizardData.shipment.isVendorBillMatched = false;
//            scope.isDocChangesByHand = true;
//            scope.wizardData.shipment.freightBillDate = new Date();
//            scope.wizardData.shipment.finishOrder.actualDeliveryDate = new Date();
//            rootScope.authData.privilegies.push('EDIT_FREIGHT_BILL_DATE');
//            scope.requiredDocuments = [];
//        });
//        c_expect(scope.requiredDocuments.length).to.equal(0);
//        c_expect(scope.wizardData.shipment.freightBillDate).not.to.be.undefined;
//        var isCanEditFreightBillDate = scope.isCanEditFreightBillDate();
//        c_expect(isCanEditFreightBillDate).to.be.false;
//    });
//
//    it("update freight bill date if reqDoc absent ", function () {
//        
//        scope.$apply(function () {
//        scope.wizardData.shipment.freightBillDate = new Date();
//        scope.requiredDocuments = 
//            [{
//                 customerRequestType: "REQUIRED",
//                 documentType: "VENDOR BILL",
//                 documentTypeDescription: "VENDOR BILL",
//                 id:1 
//            }];           
//        scope.docsTableDataLoaded = true;
//        });
//        scope.$apply(function () {
//            scope.wizardData.shipment.isVendorBillMatched = true;
//            rootScope.authData.privilegies.push('EDIT_FREIGHT_BILL_DATE');
//            scope.isDocChangesByHand = true;
//            scope.requiredDocuments = 
//                [{
//                     customerRequestType: "REQUIRED",
//                     documentType: "VENDOR BILL",
//                     documentTypeDescription: "VENDOR BILL",
//                     id:2 
//                }];
//            });
//        c_expect(scope.requiredDocuments.length).to.equal(1);
//        c_expect(scope.isCanEditFreightBillDate()).to.be.false;
//        c_expect(scope.wizardData.shipment.freightBillDate).not.to.be.undefined;
//        });
//});
