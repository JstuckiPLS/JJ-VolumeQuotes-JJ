describe('Unit test for FinancialBoardSendToAuditController', function() {
    var scope;
    var financialBoardService;
    var dictionaryService;

    var auditRecords = [{
        loadId: 1,
        adjustmentId: 2
    }];
    
    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();
        financialBoardService = $injector.get('FinancialBoardService');
        dictionaryService = $injector.get('ShipmentDictionaryService');

        var controller = $injector.get('$controller');
        controller('FinancialBoardSendToAuditController', {
            '$scope': scope,
            'FinancialBoardService': financialBoardService,
            'ShipmentDictionaryService': dictionaryService
        });
        
        scope.fillSelectedShipmentsForBusinessObjects = function()
        {
        	return auditRecords;
        }
    }));

    it('should check initial controller state', function() {
        expect(scope.sendToAuditVisible).toBeFalsy();
        expect(scope.auditReasonsParam.length).toBe(0);
        expect(scope.allAuditReasonsData.length).toBe(0);
        expect(scope.auditReason).toBeDefined();
        expect(scope.auditReason.code).toBeUndefined();
        expect(scope.auditReason.note).toBeUndefined();
        expect(scope.loadId).toBeUndefined();
        expect(scope.adjustmentId).toBeUndefined();
        expect(scope.isInvioceAudit).toBeTruthy();
    });

    it('should cancel sending to invoice audit', function() {
        scope.sendToAuditVisible = true;
        scope.auditReason.code = '1';
        scope.auditReason.note = 'note';
        scope.loadId = 1;
        scope.adjustmentId = 2;

        scope.cancelSendToAudit();

        expect(scope.sendToAuditVisible).toBeFalsy();
        expect(scope.auditReason.code).toBeUndefined();
        expect(scope.auditReason.note).toBeUndefined();
        expect(scope.loadId).toBeUndefined();
        expect(scope.adjustmentId).toBeUndefined();
    });

    it('should get billing audit reasons', function() {
        spyOn(dictionaryService, 'getAuditReasonCode');
        spyOn(scope.$root, '$emit');
        var options = {
            isInvioceAudit: true,
            loadId: 1,
            adjustmentId: 2
        };
        scope.allAuditReasonsData = [];
        scope.$broadcast('event:sendToAudit', options);

        expect(dictionaryService.getAuditReasonCode).toHaveBeenCalledWith({}, jasmine.any(Function), jasmine.any(Function));
        expect(scope.$root.$emit).not.toHaveBeenCalled();
    });

    it('should send to invoice audit', function() {
    	scope.selectedInvoices = {
                loadId : 1,
                adjustmentId : 2
            };
        scope.auditReason.code = '123';
        scope.auditReason.note = 'note';
        scope.isInvioceAudit = true;
        
        spyOn(financialBoardService, 'sendToInvoiceAudit');
        spyOn(financialBoardService, 'sendToPriceAudit');
        spyOn(scope, '$emit');
        spyOn(scope.$root, '$emit');
        
        scope.sendToAudit();

        expect(financialBoardService.sendToInvoiceAudit).toHaveBeenCalledWith(
                {auditRecords : auditRecords, code: '123', note: 'note'}, jasmine.any(Function), jasmine.any(Function));
        expect(financialBoardService.sendToPriceAudit).not.toHaveBeenCalled();
        expect(scope.$emit).not.toHaveBeenCalled();
        expect(scope.$root.$emit).not.toHaveBeenCalled();
    });

    it('should send to price audit', function() {
        scope.selectedInvoices = {
            loadId : 1,
            adjustmentId : 2
        };
        scope.auditReason.code = '123';
        scope.auditReason.note = 'note';
        scope.isInvioceAudit = false;
        spyOn(financialBoardService, 'sendToInvoiceAudit');
        spyOn(financialBoardService, 'sendToPriceAudit');
        spyOn(scope, '$emit');
        spyOn(scope.$root, '$emit');
        
        scope.sendToAudit();

        expect(financialBoardService.sendToPriceAudit).toHaveBeenCalledWith(
                {auditRecords : auditRecords, code: '123', note: 'note'}, jasmine.any(Function), jasmine.any(Function));
        expect(financialBoardService.sendToInvoiceAudit).not.toHaveBeenCalled();
        expect(scope.$emit).not.toHaveBeenCalled();
        expect(scope.$root.$emit).not.toHaveBeenCalled();
    });
});