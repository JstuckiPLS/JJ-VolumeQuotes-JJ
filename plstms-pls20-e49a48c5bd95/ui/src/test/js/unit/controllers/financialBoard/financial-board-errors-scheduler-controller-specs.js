describe('Unit test for FinancialBoardErrorsSchedulerController', function() {
    var scope;
    var invoiceErrorService;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();
        invoiceErrorService = $injector.get('FinancialBoardInvoiceErrorsService');

        var controller = $injector.get('$controller');

        controller('FinancialBoardErrorsSchedulerController', {
            '$scope': scope,
            "FinancialBoardInvoiceErrorsService": invoiceErrorService
        });
    }));

    it('should check initial error count', function() {
        expect(scope.errorsCount).toBe(0);
    });

    it('should init error scheduler', function() {
        spyOn(scope, 'repeatCheckErrorsActions');

        scope.initErrorsScheduler();

        expect(scope.repeatCheckErrorsActions).toHaveBeenCalled();
    });

    it('should get errors count', function() {
        spyOn(invoiceErrorService, 'errorsCount');

        scope.getErrorsCount();

        expect(invoiceErrorService.errorsCount).toHaveBeenCalledWith({}, jasmine.any(Function));
    });

    it('should repeat erros check', function() {
        expect(scope.errorsCountTimerPromise).toBeUndefined();
        scope.repeatCheckErrorsActions();
        expect(scope.errorsCountTimerPromise).toBeDefined();
    });

    it('should update errors count', function() {
        spyOn(scope, 'getErrorsCount');
        scope.$broadcast('event:financialBoardErrorsChanged');
        expect(scope.getErrorsCount).toHaveBeenCalled();
    });
});