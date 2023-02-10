describe('Unit test for AddEditCostDetailsCtrl', function() {
    var scope = null;

    beforeEach(module('pls.controllers'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();

        var controller = $injector.get('$controller');
        controller('AddEditCostDetailsCtrl', {
            '$scope': scope
        });
    }));

    it('should check cost details dialog options', function() {
        expect(scope.addEditCostDetailsDialogOptions).toBeDefined();
    });

    it('should check initial controller state', function() {
        expect(scope.editedCostDetail).toBeDefined();
        expect(scope.accessorialTypes.length).toBe(0);
        expect(scope.isEdit).toBeFalsy();
    });

    it('should cancel cost details editing', function() {
        scope.cancelEditCostDetails();
        expect(scope.editCostDetailsDialogVisible).toBeFalsy();
    });

    it('should open show edit cost details dialog', function() {
        var dialogDetails = {
            isEdit: true,
            parentDialog: 'cost dialog',
            editedCostDetail: {
                doNotInvoice: false
            },
            accessorialTypes: [{id: 'CBF'}, {id: 'SRA'}]
        };
        scope.$broadcast('event:showAddEditCostDetails', dialogDetails);

        expect(scope.editCostDetailsDialogVisible).toBeTruthy();
        expect(scope.editedCostDetail).toEqual({doNotInvoice: false});
        expect(scope.isEdit).toBeTruthy();
        expect(scope.addEditCostDetailsDialogOptions.parentDialog).toEqual('cost dialog');
        expect(scope.accessorialTypes[0]).toEqual({id: 'SRA'});
        expect(scope.accessorialTypes[1]).toEqual({id: 'CBF'});
    });

    it('should save cost details', function() {
        spyOn(scope.$root, '$broadcast');
        spyOn(scope, 'cancelEditCostDetails');
        scope.saveCostDetails();

        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:saveCostDetails', jasmine.any(Object));
        expect(scope.cancelEditCostDetails).toHaveBeenCalled();
    });
});