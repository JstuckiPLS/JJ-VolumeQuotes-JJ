describe('AccessorialTypesCtrl test', function() {
    var $scope = null;

    var activeAccessorialTypeList = [{"id":"HCD", "description":"High Cost Delivery", "applicableTo":"ALL", "accessorialGroup":"DELIVERY", "status":"ACTIVE"}];
    var inactiveAccessorialTypeList = [{"id":"NC","description":"Notify Charge","applicableTo":"ALL", "accessorialGroup":"PICKUP","status":"INACTIVE"}];

    var accessorialTypeServiceMock = {
        applicableunits: function() {},
        active: function(params, success) {
            success(activeAccessorialTypeList);
        },
        inactive: function(params, success) {
            success(inactiveAccessorialTypeList);
        },
        save: function() {},
        isUnique: function() {}
    };

    var mockNgGridPluginFactory = {
        progressiveSearchPlugin: function() {},
        actionPlugin: function() {},
        plsGrid: function() {}
    };

    var accessorialTypeCheckerMock = {
        validate: function(code, error) {
            error({"result": "true"});
        }
    };

    beforeEach(module('ngRoute'));
    beforeEach(module('pricing'));
    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope) {
        $scope = $rootScope.$new();
    }));

    beforeEach(inject(function($controller) {
        $controller('AccessorialTypesCtrl', {
            $scope: $scope,
            AccTypesServices: accessorialTypeServiceMock,
            CheckAccTypeCodeExists: accessorialTypeCheckerMock,
            NgGridPluginFactory: mockNgGridPluginFactory
        });
    }));

    it ('checks initial model state', function() {
        expect($scope.accessorialTypeModel.accessorialType.status).toEqual('INACTIVE');
        expect($scope.accessorialTypeModel.changeStatusBtnCaption).toEqual('Archive');
        expect($scope.accessorialTypeModel.editMode).toBe(false);
        expect($scope.accessorialTypeModel.selectedItems).toBeDefined();
        expect($scope.accessorialTypeModel.selectedItems.length).toBe(0);
        expect($scope.accessorialTypeModel.currentTab).toEqual('Active');
        expect($scope.accessorialTypeModel.accessorialGroups).toBeDefined();
        expect($scope.accessorialTypeModel.accessorialGroups.length).toBe(3);
        expect($scope.accessorialTypeModel.accessorialGroups[0].group).toBe('');
        expect($scope.accessorialTypeModel.accessorialGroups[0].value).toBeUndefined();
        expect($scope.accessorialTypeModel.accessorialGroups[1].group).toBe('Pickup');
        expect($scope.accessorialTypeModel.accessorialGroups[1].value).toBe('PICKUP');
        expect($scope.accessorialTypeModel.accessorialGroups[2].group).toBe('Delivery');
        expect($scope.accessorialTypeModel.accessorialGroups[2].value).toBe('DELIVERY');
    });

    it ('should prove accessorial type validation', function() {
        $scope.accessorialTypeModel.accessorialType.id = 1;
        $scope.accTypeValidation();

        expect($scope.errorMessage).toEqual('Accessorial Type with the same Code already exists.');
    });

    it ('should fetch active accessorial types list', function() {
        expect($scope.accessorialTypeModel.changeStatusBtnCaption).toEqual("Archive");
        expect($scope.accessorialTypeModel.currentTab).toEqual("Active");
        expect($scope.listItems).toBeDefined();
        expect($scope.listItems.length).toBe(1);
        expect($scope.listItems[0].id).toEqual(activeAccessorialTypeList[0].id);
        expect($scope.listItems[0].description).toEqual(activeAccessorialTypeList[0].description);
        expect($scope.listItems[0].applicableTo).toEqual(activeAccessorialTypeList[0].applicableTo);
        expect($scope.listItems[0].accessorialGroup).toEqual(activeAccessorialTypeList[0].accessorialGroup);
        expect($scope.listItems[0].status).toEqual(activeAccessorialTypeList[0].status);
        expect($scope.accessorialTypeModel.editMode).toBe(false);
        expect($scope.accessorialTypeModel.selectedItems.length).toBe(0);
        expect($scope.errorMessage).toEqual("");
    });

    it ('should fetch inactive accessorial types list', function() {
        $scope.showInactiveList();
        expect($scope.listItems).toBeDefined();
        expect($scope.listItems.length).toBe(1);
        expect($scope.listItems[0].id).toEqual(inactiveAccessorialTypeList[0].id);
        expect($scope.listItems[0].description).toEqual(inactiveAccessorialTypeList[0].description);
        expect($scope.listItems[0].applicableTo).toEqual(inactiveAccessorialTypeList[0].applicableTo);
        expect($scope.listItems[0].accessorialGroup).toEqual(inactiveAccessorialTypeList[0].accessorialGroup);
        expect($scope.listItems[0].status).toEqual(inactiveAccessorialTypeList[0].status);
        expect($scope.accessorialTypeModel.editMode).toBe(false);
        expect($scope.accessorialTypeModel.selectedItems.length).toBe(0);
        expect($scope.errorMessage).toEqual("");
    });

    it ('should edit accessorial type details', function() {
        expect($scope.accessorialTypeModel.editMode).toBe(false);
        expect($scope.accessorialTypeModel.selectedItems.length).toBe(0);

        $scope.accessorialTypeModel.selectedItems.push(activeAccessorialTypeList[0]);
        $scope.editDetails();

        expect($scope.accessorialTypeModel.editMode).toBe(true);
        expect($scope.accessorialTypeModel.selectedItems.length).toBe(1);
        expect($scope.accessorialTypeModel.accessorialType.id).toEqual(activeAccessorialTypeList[0].id);
        expect($scope.accessorialTypeModel.accessorialType.description).toEqual(activeAccessorialTypeList[0].description);
        expect($scope.accessorialTypeModel.accessorialType.applicableTo).toEqual(activeAccessorialTypeList[0].applicableTo);
        expect($scope.accessorialTypeModel.accessorialType.accessorialGroup).toEqual(activeAccessorialTypeList[0].accessorialGroup);
        expect($scope.accessorialTypeModel.accessorialType.status).toEqual(activeAccessorialTypeList[0].status);
    });

    it ('should clear model', function() {
        $scope.clear();

        expect($scope.accessorialTypeModel.accessorialType.status).toEqual('INACTIVE');
        expect($scope.accessorialTypeModel.editMode).toBe(false);
        expect($scope.accessorialTypeModel.selectedItems.length).toBe(0);
        expect($scope.errorMessage).toEqual("");
    });

    it ('should save accessorial type', function() {
        $scope.save();
        expect($scope.accessorialTypeModel.changeStatusBtnCaption).toEqual("Archive");
        expect($scope.accessorialTypeModel.currentTab).toEqual("Active");
        expect($scope.listItems).toBeDefined();
        expect($scope.listItems.length).toBe(1);
        expect($scope.listItems[0].id).toEqual(activeAccessorialTypeList[0].id);
        expect($scope.listItems[0].description).toEqual(activeAccessorialTypeList[0].description);
        expect($scope.listItems[0].applicableTo).toEqual(activeAccessorialTypeList[0].applicableTo);
        expect($scope.listItems[0].accessorialGroup).toEqual(activeAccessorialTypeList[0].accessorialGroup);
        expect($scope.listItems[0].status).toEqual(activeAccessorialTypeList[0].status);
        expect($scope.accessorialTypeModel.editMode).toBe(false);
        expect($scope.accessorialTypeModel.selectedItems.length).toBe(0);
        expect($scope.errorMessage).toEqual("");
    });

    it ('asserts accessorial type id less than three symbols', function() {
        $scope.accessorialTypeModel.accessorialType.id = '';
        expect($scope.accessorialTypeModel.accessorialType.id.length).toBe(0);

        $scope.accessorialTypeModel.accessorialType.id = 'this id is obviously longer than 3 chars';
        $scope.$apply();
        expect($scope.accessorialTypeModel.accessorialType.id.length).toBe(3);
    });

    it ('asserts accessorial type description less than fifty symbols', function() {
        $scope.accessorialTypeModel.accessorialType.description = '';
        expect($scope.accessorialTypeModel.accessorialType.description.length).toBe(0);

        $scope.accessorialTypeModel.accessorialType.description = 'very very veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery long description!';
        $scope.$apply();
        expect($scope.accessorialTypeModel.accessorialType.description.length).toBe(50);
    });
});
