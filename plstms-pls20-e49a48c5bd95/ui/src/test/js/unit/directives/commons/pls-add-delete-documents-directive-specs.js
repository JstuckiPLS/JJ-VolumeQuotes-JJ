/**
 * Unit Tests for pls-add-delete-documents-directive.
 * 
 * @author Dima Davydenko.
 */
describe("Test for Shipment Details/Sales Order add/delete documents directive", function() {
    var element = undefined;
    var scope = undefined;
    var isoScope = undefined;
    var localUrlConfid;
    var mockFullViewDocModel = {
        fullViewDocOption: {
            imageContent: false,
            pdfLocation: null
        },
        showFullViewDocumentDialog: false
    };

    var mockShipment = {
            billTo: { id: 23 },
            bolNumber: 444,
            id: 1,
            organizationId: 206962
    };

    var mockEmailOptions = {
            showSendEmailDialog: false
    };

    var requiredDocuments = [{customerRequestType: "REQUIRED", documentType: "VENDOR BILL", documentTypeDescription: "VENDOR BILL", id:1 }];

    var documentList = [
       {         
           createdByName: "Auto-generated",
           createdDate: "2016-08-31",
           date: "2016-08-31",
           docFileType: "application/pdf",
           fileName:"BOL.pdf",
           id: 1,
           name: "BOL",
           shipmentId: 1
       }, {
           createdByName: "Auto-generated",
           createdDate: "2016-08-31",
           date: "2016-08-31",
           docFileType: "application/pdf",
           fileName:"Consignee Invoice.pdf",
           id: 2,
           name: "Consignee Invoice",
           shipmentId: 1
       }, {
           createdByName: "Auto-generated",
           createdDate: "2016-08-31",
           date: "2016-08-31",
           docFileType: "application/pdf",
           fileName:"Shipping Label.pdf",
           id: 3,
           name: "Shipping Label",
           shipmentId: 1
       }
   ];

    var docTypes = [
        {value: 'etc', label: 'etc'},
        {value: 'INVOICE', label:'Invoices'},
        {value: 'Miscellaneous', label: 'Miscellaneous'},
        {value: 'POD', label: 'Proof of delivery'},
        {value: 'UNKNOWN', label: 'Unknow'},
        {value: 'W9_DOCUMENT', label: 'W9 Document'}
    ];

    var mockPrintTypes = [
        {"label":"1 per sheet (5.5 x 8.5)", "value":1,
            description:'Avery # 15665, 18665, 48165, 5165, 5265, 5353, 8165, 8255, 8465, 8665, 15265, 95920'},
        {"label":"2 per sheet (4.75 x 7.75)", "value":2, description:'Avery # 6876'},
        {"label":"2 per sheet (5.5 x 8.5)", "value":3,
            description:'Avery # 15516, 18126, 48126, 48226, 48326, 48330, 5126, 5526, 5783, 8126, 85726, 85783, 95930, 95900, 5912'},
        {"label":"4 per sheet (3.75 x 4.75)", "value": 4, description:'Avery # 6878'},
        {"label":"6 per sheet (3.33 x 4)", "value":5,
            description:'Avery # 15664, 18664, 45464, 48264, 48464, 48864, 5164, 5264, 55164, 5524, 55264, 55364, ' +
                '55464, 5664, 58164, 58264, 8164, 8254, 8464, 8564, 15264, 95940, 95905'},
        {"label":"8 per sheet (2 x 3.75)", "value":6, description:'Avery # 6873'},
        {"label":"10 per sheet (2 x 4)", "value":7, 
            description:'Avery # 15163, 15563, 15663, 18163, 18663, 18863, 28663, 38363, 38863, 48163, 48263, 48363, ' +
            '48463, 48863, 5163, 5263, 55163, 55263, 55363, 55463, 5663, 58163, 58263, 5963, 8163, 8253, 8363, 8463, ' +
            '85563, 8563, 8663, 8923, 95945, 95910'}
    ];

    var mockUserShipmentDocumentService = {
        getDocumentList: function (params, successCallback) {
            successCallback(documentList);
            var defer = promiseProvider.defer();
            defer.resolve();
            return {$promise: defer.promise};
        },
        removeDocument: function (params, successCallback) {
            successCallback(true);
            var defer = promiseProvider.defer();
            defer.resolve();
            return {$promise: defer.promise};
        }
    };

    var mockShipmentUtils = {
        getDictionaryValues: function () {
            return {
                printTypes: mockPrintTypes
            };
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/pls-add-delete-docs-tpl.html', function ($provide) {
        $provide.factory('ShipmentDocumentService', function () {
            return mockUserShipmentDocumentService;
        });
        $provide.factory('ShipmentUtils', function () {
            return mockShipmentUtils;
        });
    }));

    beforeEach(inject(function ($rootScope, $q, $compile, $httpBackend, urlConfig) {
        localUrlConfid = urlConfig;
        scope = $rootScope;
        promiseProvider = $q;

        scope.$apply(function () {
            scope.shipment = mockShipment;
            scope.selectedCustomer = {
                    id: 206962,
                    name: 'SAFWAY GROUP HOLDINGS LLC'
            }
            scope.fullViewDocModel = mockFullViewDocModel;
            scope.emailOptions = mockEmailOptions;
        });

        $httpBackend.when('GET', '/restful/customer/206962/billTo/23/requiredDocuments').respond(requiredDocuments);

        element = angular.element('<div data-pls-add-delete-documents data-full-view-doc-model="fullViewDocModel" data-email-options="emailOptions"' +
                'data-shipment="shipment" data-selected-customer="selectedCustomer"></div>');
        $compile(element)(scope);
        scope.$digest();
        isoScope = element.isolateScope();
        isoScope.init();
        isoScope.allRequiredDocuments = requiredDocuments;
    }));

    it('should call init() function for Shipment Details and get Document List', function() {
        spyOn(mockUserShipmentDocumentService, 'getDocumentList').and.callThrough();
        spyOn(isoScope, 'filterRequiredDocuments');
        isoScope.init();

        expect(mockUserShipmentDocumentService.getDocumentList).toHaveBeenCalled();
        expect(mockUserShipmentDocumentService.getDocumentList.calls.count()).toEqual(1);
        expect(mockUserShipmentDocumentService.getDocumentList.calls.mostRecent().args[0]).toEqual({shipmentId: scope.shipment.id});
        expect(isoScope.docsGridData).toEqual(documentList);
        expect(isoScope.filterRequiredDocuments).toHaveBeenCalled();
        expect(isoScope.filterRequiredDocuments.calls.count()).toEqual(1);
    });

    it('should call init() function for Shipment Details without shipment ID and don\'t get Document List', function() {
        spyOn(mockUserShipmentDocumentService, 'getDocumentList').and.callThrough();
        isoScope.docsGridData = undefined;
        scope.shipment.id = undefined;

        isoScope.init();

        expect(mockUserShipmentDocumentService.getDocumentList).not.toHaveBeenCalled();
        expect(isoScope.docsGridData).not.toBeDefined();
    });

    it('prepare data for next test', function() {
       scope.shipment.id = 1;
       isoScope.init();
    })

    it('should select Bol document', function() {
        expect(isoScope.docsGrid.options.selectedItems[0].name).toEqual('BOL');
    });

    it('should check if add disabled', function () {
        expect(isoScope.isAddDisabled()).toBeTruthy();
        var directiveDiv = element.find('[data-pls-add-delete-documents]');
        expect(directiveDiv.find('[data-upload-submit="uploadModel.uploadCallback(content, completed)]').is(':visible')).toBeFalsy();
    });

    it('should check if add disabled - should not', function () {
        isoScope.uploadModel.uploadFile = true;
        isoScope.uploadModel.selectedDocType = true;

        expect(isoScope.isAddDisabled()).toBeFalsy();
    });

      it('should set file to upload', function () {
      var fileName = 'test.pdf';
      isoScope.uploadModel.setFile({value: fileName, files: [{size: 2.5*1024*1024, name: 'test.pdf'}]});

      expect(isoScope.uploadModel.uploadFile).toEqual(fileName);
    });

    it('should not set file to upload with invalid extension', function () {
        spyOn(scope, '$emit').and.callThrough();
        isoScope.uploadModel.setFile({value: 'test.pdf', files: [{size: 2.5*1024*1024, name: 'test.xls'}]});
    
        expect(isoScope.uploadModel.uploadFile).toBeUndefined();
        expect(scope.$emit.calls.count()).toEqual(1);
        expect(scope.$emit.calls.mostRecent().args[0]).toEqual('event:application-error');
    });

    it('should not set file to upload with invalid file size', function () {
        spyOn(scope, '$emit').and.callThrough();
        isoScope.uploadModel.setFile({value: 'test.pdf', files: [{size: 2.5*1024*1024 + 1, name: 'test.pdf'}]});

        expect(isoScope.uploadModel.uploadFile).not.toBeDefined();
        expect(scope.$emit.calls.count()).toEqual(1);
        expect(scope.$emit.calls.mostRecent().args[0]).toEqual('event:application-error');
    });

    it('should set data with callback after upload', function () {
        var selectedDocTypeLabel = 'Test label';
        var selectedDocTypeValue = 'Test value';
        var uploadFile = {value: 'test.pdf'};
        scope.$apply(function () {
            scope.progressPanelOptions.showPanel = true;
            isoScope.uploadModel.selectedDocType = {value: selectedDocTypeValue, label: selectedDocTypeLabel};
            isoScope.uploadModel.uploadFile = uploadFile;
        });

        isoScope.uploadModel.uploadCallback('{"success":true,"limitSizeExceeded":false,"tempDocId":7055}', true);

        expect(scope.progressPanelOptions.showPanel).toBeFalsy();
        expect(isoScope.docsGridData.length).toEqual(4);
        var element = isoScope.docsGridData[3];

        expect(isoScope.uploadModel.selectedDocType).toBeUndefined();
        expect(isoScope.uploadModel.uploadFile).toBeUndefined();
        expect(element.name).toEqual(selectedDocTypeLabel);
        expect(element.createdByName).toEqual(scope.authData.fullName);
        expect(element.tempDocId).toEqual(7055);
        expect(element.tempFileName).toEqual(uploadFile);
    });

    it('should set data with callback after upload - not completed', function () {
        scope.progressPanelOptions.showPanel = true;
        isoScope.uploadModel.uploadFile = {};

        isoScope.uploadModel.uploadCallback('{"success" : "true", "tempDocId" : 123}', false);

        expect(scope.progressPanelOptions.showPanel).toBeTruthy();
        expect(isoScope.docsGridData.length).toEqual(3);
        expect(isoScope.shipment.uploadedDocuments).toBeUndefined();
    });

    it('should set data with callback after upload - cannot parse json response', function () {
        spyOn(scope, '$emit').and.callThrough();

        scope.progressPanelOptions.showPanel = true;
        isoScope.uploadModel.uploadFile = {};

        isoScope.uploadModel.uploadCallback('not correct json', true);

        expect(scope.progressPanelOptions.showPanel).toBeFalsy();
        expect(isoScope.docsGridData.length).toEqual(3);
        expect(isoScope.shipment.uploadedDocuments).toBeUndefined();

        expect(scope.$emit.calls.count()).toEqual(1);
        expect(scope.$emit.calls.mostRecent().args[0]).toEqual('event:application-error');
    });

    it('should fire "event:application-error" event in case of 401', function() {
        scope.progressPanelOptions.showPanel = true;
        isoScope.uploadModel.uploadFile = {};

        spyOn(scope.$root, '$broadcast').and.callThrough();

        isoScope.uploadModel.uploadCallback('HTTP Status 401 - Unauthorized description This request requires HTTP authentication Apache Tomcat/7.0.42', true);

        expect(scope.progressPanelOptions.showPanel).toBeFalsy()
        expect(isoScope.docsGridData.length).toEqual(3);
        expect(isoScope.shipment.uploadedDocuments).toBeUndefined();

        expect(scope.$broadcast.calls.count()).toEqual(1);
        expect(scope.$broadcast.calls.mostRecent().args[0]).toEqual('event:auth-loginRequired');
    });

    it('should set data with callback after upload - size exceeded', function () {
        spyOn(scope, '$emit').and.callThrough();
        scope.progressPanelOptions.showPanel = true;


        isoScope.uploadModel.uploadCallback('{"success" : "false", "limitSizeExceeded" : "true"}', true);

        expect(scope.progressPanelOptions.showPanel).toBeFalsy();
        expect(isoScope.docsGridData.length).toEqual(3);
        expect(isoScope.shipment.uploadedDocuments).toBeUndefined();

        expect(scope.$emit.calls.count()).toEqual(1);
        expect(scope.$emit.calls.mostRecent().args[0]).toEqual('event:application-error');
    });

    it('should define if document is an image - tempfile is an image', function () {
        isoScope.selectedDocs = [ {tempFileName: 'test.png'} ];

        expect(isoScope.isDocumentImage()).toBeTruthy();
    });

    it('should define if document is an image - tempfile is not an image', function () {
        scope.$apply(function () {
            isoScope.selectedDocs = [{tempFileName: 'test.pdf'}];
        });
        expect(isoScope.isDocumentImage()).toBeFalsy();
    });

    it('should define if document is an image - doc file is an image', function () {
        scope.$apply(function () {
            isoScope.selectedDocs = [{docFileType: 'png'}];
        });

        expect(isoScope.isDocumentImage()).toBeTruthy();
    });

    it('should define if document is an image - doc file is not an image', function () {
        scope.$apply(function () {
            isoScope.selectedDocs = [{docFileType: 'application/pdf'}];
        });

        expect(isoScope.isDocumentImage()).toBeFalsy();
    });

    it('should show content of stored document', function () {
        scope.$apply(function () {
            isoScope.selectedDocs = [{tempFileName: 'test.pdf', id: 1}];
            isoScope.shipment.id = 1;
        });
        isoScope.showDocumentContent();

        expect(isoScope.docOptions.imageContent).toBeFalsy();

        var pdfLocation = localUrlConfid.shipment + '/customer/shipmentdocs/' + 1;
        expect(isoScope.docOptions.pdfLocation.indexOf(pdfLocation)).toEqual(0);

        expect(isoScope.docOptions.hidePdf).toBeFalsy();
    });

    it('should close content of document', function () {
        isoScope.selectedDocs.length = 0;

        expect(isoScope.docOptions.imageContent).toBeFalsy();
    });

    it('should show content of BOL in full view mode', function () {
        isoScope.selectedDocs = [{fileName: 'BOL.pdf', id: 1}];

        isoScope.viewDocument();

        expect(isoScope.fullViewDocModel.showFullViewDocumentDialog).toBeTruthy();
        expect(isoScope.fullViewDocModel.fullViewDocOption.imageContent).toBeFalsy();

        var pdfLocation = localUrlConfid.shipment + '/customer/shipmentdocs/' + isoScope.selectedDocs[0].id;
        expect(isoScope.fullViewDocModel.fullViewDocOption.pdfLocation.indexOf(pdfLocation)).toEqual(0);

        expect(isoScope.fullViewDocModel.fullViewDocOption.hidePdf).toBeUndefined();
        expect(isoScope.docOptions.hidePdf).toBeTruthy();
        expect(isoScope.fullViewDocModel.showFullViewDocumentDialog).toBeTruthy();
    });

    it('should close full view document', function () {
        isoScope.fullViewDocModel.closeFullViewDocument();

        expect(isoScope.fullViewDocModel.fullViewDocOption.pdfLocation).toBe(null);
        expect(isoScope.fullViewDocModel.showFullViewDocumentDialog).toBeFalsy();
        expect(isoScope.docOptions.hidePdf).toBeFalsy();
    });

    it('should delete document', function () {
        spyOn(scope, '$emit').and.callThrough();
        spyOn(isoScope, 'filterRequiredDocuments');
        spyOn(mockUserShipmentDocumentService, 'removeDocument').and.callThrough();
        

        var emptyArray = [];
        var selectedFileName = "Proof of delivery.pdf";
        scope.$apply(function() {
            isoScope.selectedDocs = [];
            isoScope.docsGridData[3] = {
                    createdByName: "Test",
                    createdDate: "2016-08-31",
                    date: "2016-08-31",
                    docFileType: "application/pdf",
                    fileName:"Proof of delivery.pdf",
                    id: 4,
                    name: docTypes[4].value,
                    shipmentId: 1
            }
            isoScope.selectedDocs[0] = isoScope.docsGridData[3];
        })

        expect(isoScope.selectedDocs[0].fileName).toEqual(selectedFileName);
        expect(isoScope.selectedDocs[0]).toBeDefined();
        isoScope.deleteDocument();

        expect(scope.$emit.calls.count()).toEqual(0);
        expect(mockUserShipmentDocumentService.removeDocument.calls.count()).toEqual(1);
        expect(mockUserShipmentDocumentService.removeDocument.calls.mostRecent().args[0]).toEqual({docId: 4});
        expect(isoScope.filterRequiredDocuments).toHaveBeenCalled();
        expect(isoScope.docOptions.hidePdf).toBeTruthy();
        expect(isoScope.canAddDoc).toBeTruthy();
        expect(isoScope.docsGridData.length).toEqual(3);
        expect(isoScope.selectedDocs).toEqual(emptyArray);
    });

    it('should ckeck if view/print/emailTo buttons are disabled if no document is selected', function() {
        //var deleteButton = element.find('[data-ng-click="deleteDocument()"]');
        var viewButton = element.find('[data-ng-click="viewDocument()"]');
        var printButton = element.find('[data-ng-click="printDocument()"]');
        var emailToButton = element.find('[data-ng-click="emailToDocument()"]');

        scope.$apply(function() {
            isoScope.selectedDocs[0] = isoScope.docsGridData[0];
        });

        //expect(deleteButton).toBeDefined();

        expect(element.find('[data-ng-click="viewDocument()"]').is(':enabled')).toBeTruthy();
        expect(element.find('[data-ng-click="printDocument()"]').is(':enabled')).toBeTruthy();
        expect(element.find('[data-ng-click="emailToDocument()"]').is(':enabled')).toBeTruthy();

        scope.$apply(function() {
            isoScope.selectedDocs.length = 0;
        });
        expect(element.find('[data-ng-click="viewDocument()"]').is(':disabled')).toBeTruthy();
        expect(element.find('[data-ng-click="printDocument()"]').is(':disabled')).toBeTruthy();
        expect(element.find('[data-ng-click="emailToDocument()"]').is(':disabled')).toBeTruthy();
    });

    it('should ckeck conditions for delete button to be disabled', function() {
        expect(isoScope.selectedDocs[0].name).toEqual("BOL");
        expect(element.find('[data-ng-click="deleteDocument()"]').is(':disabled')).toBeTruthy();
        
        isoScope.selectedDocs[0] = isoScope.docsGridData[1];
        
        expect(element.find('[data-ng-click="deleteDocument()"]').is(':disabled')).toBeTruthy();
        
        isoScope.selectedDocs[0] = isoScope.docsGridData[2];
        expect(element.find('[data-ng-click="deleteDocument()"]').is(':disabled')).toBeTruthy();
        
        scope.$apply(function() {
            isoScope.docsGridData[3] = {
                    createdByName: "Test",
                    createdDate: "2016-08-31",
                    date: "2016-08-31",
                    docFileType: "application/pdf",
                    fileName:"Proof of delivery.pdf",
                    id: 4,
                    name: docTypes[4].value,
                    shipmentId: 1
            }
        });
        expect(element.find('[data-ng-click="deleteDocument()"]').is(':disabled')).toBeFalsy();
    });

    it('should remove required document from list if it was uploaded', function() {
        scope.$apply(function() {
            isoScope.docsGridData[3] = {
                    createdByName: "Test",
                    createdDate: "2016-08-31",
                    date: "2016-08-31",
                    docFileType: "application/pdf",
                    fileName:"Proof of delivery.pdf",
                    id: 4,
                    name: "VENDOR BILL",
                    shipmentId: 1

            }
        })
        isoScope.filterRequiredDocuments();
        expect(isoScope.requiredDocuments.length).toEqual(0);
    });

    it('should add required document to list of required docs if it was deleted', function() {
        scope.$apply(function() {
            isoScope.docsGridData[3] = {
                    createdByName: "Test",
                    createdDate: "2016-08-31",
                    date: "2016-08-31",
                    docFileType: "application/pdf",
                    fileName:"Proof of delivery.pdf",
                    id: 4,
                    name: "VENDOR BILL",
                    shipmentId: 1
            };
        })
        isoScope.filterRequiredDocuments();
        expect(isoScope.requiredDocuments.length).toEqual(0);
        scope.$apply(function() {
            isoScope.docsGridData.splice(3);
        });
        isoScope.filterRequiredDocuments();
        expect(isoScope.requiredDocuments.length).toEqual(1);
    });

    it('should check visibility of Regenerate Consignee Invoice button for ShipmentDetails for SafwayOrganization', function() {
        expect(element.find('[data-ng-click="regenerateConsigneeInvoice()"]')).toBeDefined();

        isoScope.selectedDocs.length = 0;

        expect(element.find('[data-ng-click="regenerateConsigneeInvoice()"]')).toBeDefined();
    });

    it('should not show Regenerate Consignee Invoice button for ShipmentDetails if organiz. doesnt ability to regen Consignee Invoices', function() {
        expect(isoScope.canRegenerateConsigneeInvoice()).toBeFalsy();

        isoScope.shipment.generateConsigneeInvoice = true;
        scope.authData.privilegies.push('CAN_GENERATE_CONSIGNEE_INVOICE');

        expect(isoScope.canRegenerateConsigneeInvoice()).toBeTruthy();
    });

    it('should not show Regenerate Ship. Label button for Shipment Details', function() {
        expect(isoScope.canViewAddShipLabel()).toBeFalsy();
    });

    it('should show Regenerate Ship. Label button for Edit Sales Order', function() {
        isoScope.shouldSaveTempDocuments = true;
        expect(isoScope.canViewAddShipLabel()).toBeTruthy();
    });
})