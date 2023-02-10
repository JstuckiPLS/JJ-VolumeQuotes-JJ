describe('Unit test for TerminalInfoController', function() {
    var scope = undefined;
    var filter = undefined;
    var terminalInfoService = undefined;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();
        filter = $injector.get('$filter');
        terminalInfoService = $injector.get('TerminalInfoService');

        var controller = $injector.get('$controller');
        controller('TerminalInfoController', {
            '$scope': scope,
            '$filter': filter,
            'TerminalInfoService': terminalInfoService
        });
    }));

    it('should check terminal info dialog is hidden', function() {
        expect(scope.showTerminalInfo).toBeFalsy();
    });

    it('should check initial model state', function() {
        expect(scope.terminalInfoModel).toBeDefined();
    });

    it('should close terminal info dialog', function() {
        scope.showTerminalInfo = true;
        spyOn(scope, '$emit');
        scope.closeTerminalInfoDialog();

        expect(scope.showTerminalInfo).toBeFalsy();
        expect(scope.$emit).toHaveBeenCalledWith('event:closeTerminalInfoDialog');
    });

    it('should open termianl info dialog', function() {
        spyOn(terminalInfoService, 'getTerminalInfo');
        scope.$broadcast('event:openTerminalInfoDialog', 1);

        expect(terminalInfoService.getTerminalInfo).toHaveBeenCalledWith({shipmentId: 1}, jasmine.any(Function));
    });

    it('should open terminal info dialog for prepared criteria', function() {
        var preparedInfo = {
            originTerminal: {
                address1: {},
                city: 'Odessa',
                stateCode: '65000',
                postalCode: 'ODS'
            },
            destinationTerminal: {
                address1: {},
                city: 'Kiev',
                stateCode: '61000',
                postalCode: 'KIE'
            },
            parentDialog: 'Outer space',
        };
        scope.$broadcast('event:openTerminalInfoForPreparedCriteria', preparedInfo);

        expect(scope.terminalInfo).toBeDefined();
        expect(scope.originTerminalInfo).toContain('Odessa, 65000, ODS');
        expect(scope.destinationTerminalInfo).toContain('Kiev, 61000, KIE');
        expect(scope.terminalInfoModel.parentDialog).toEqual('Outer space');
        expect(scope.showTerminalInfo).toBeTruthy();
    });
});