describe('AccessorialsCtrl controller test', function() {

    var scope = undefined;

    var controller = undefined;

    var dictionary = {
            classes: ['CLASS_50', 'CLASS_55', 'CLASS_60', 'CLASS_65', 'CLASS_70', 'CLASS_77_5', 'CLASS_85', 'CLASS_92_5', 'CLASS_100', 'CLASS_100',
                      'CLASS_125', 'CLASS_150', 'CLASS_175', 'CLASS_200', 'CLASS_250', 'CLASS_300', 'CLASS_400', 'CLASS_500'],
            ltlCostTypes: [
                         {label:'Discount %', value:'DC'},
                         {label:'Per Mile', value:'MI'},
                         {label:'Flat Fee', value:'FL'},
                         {label:'Per Piece', value:'PE'},
                         {label:'Per 100 Weight', value:'CW'}
                 ],
            ltlMarginTypes: [
                         {label:'Margin %', value:'MC'},
                         {label:'Per Mile', value:'MI'},
                         {label:'Flat Fee', value:'FL'},
                         {label:'Per Piece', value:'PE'},
                         {label:'Per 100 Weight', value:'CW'}
                 ],
             ltlServiceTypes: [
                         {label:'Direct', value:'DIRECT'},
                         {label:'Indirect', value:'INDIRECT'}
                 ]
        };

    var accessorialTypes = [
        {accessorialGroup: null, applicableTo: 'ALL', description: 'High Cost Delivery', id: 'HCD', status: 'ACTIVE'},
        {accessorialGroup: null, applicableTo: 'ALL', description: 'Notify Charge', id: 'NC', status: 'ACTIVE'}
    ];

    var accessorialsList = [
        {accessorialType: 'REP', blocked: "Y", costApplDistUom: "ML", costApplMaxDist: 250, costApplMaxWt: 5000, costApplMinDist: 50, costApplMinWt: 1,
            costApplWtUom: "LB", costType: "FL", ltlPricProfDetailId: 1, marginDollarAmt: 100, marginPercent: 7.5, marginType: "FL", status: "ACTIVE"},
        {accessorialType: 'LFC', blocked: "Y", costApplDistUom: "ML", costApplMaxDist: 250, costApplMaxWt: 5000, costApplMinDist: 50, costApplMinWt: 1,
            costApplWtUom: "LB", costType: "FL", ltlPricProfDetailId: 1, marginDollarAmt: 100, marginPercent: 7.5, marginType: "FL", status: "ACTIVE"}
    ];

    var accessorial = {
            accessorialType: "LFC",
            blocked: "Y",
            costApplDistUom: "ML",
            costApplMaxDist: 250,
            costApplMaxWt: 5000,
            costApplMinDist: 50,
            costApplMinWt: 1,
            costApplWtUom: "LB",
            costType: "FL",
            effDate: "2013-03-01T00:00:00.000",
            expDate: "2100-05-01T00:00:00.000",
            id: 1,
            ltlAccGeoServicesEntities: [],
            ltlPricProfDetailId: 1,
            marginDollarAmt: 100,
            marginPercent: 7.5,
            marginType: "FL",
            status: "ACTIVE",
            unitCost: 10,
            unitMargin: 2.5
    };

    var mockPricingDetailsDictionaryService = {
            get : function(param, success) {
                success(angular.copy(dictionary));
            }
    };

    var mockAccessorialTypesService = {
            get : function(param, success) {
                success(angular.copy(accessorialTypes));
            }
    };

    var mockAccessorialsListService = {
            active : function(param, success) {
                success(angular.copy(accessorialsList));
            },
            expired : function(param, success) {
                success(angular.copy(accessorialsList));
            },
            archived : function(param, success) {
                success(angular.copy(accessorialsList));
            }
    };

    var mockAccessorialsService = {
            get : function(param, success) {
                success(angular.copy(accessorial));
            }
    };

    var mockAccessorialStatusChangeService = {
            inactivate : function(param, success) {
                success();
            },
            expire : function(param, success) {
                success();
            },
            reactivate : function(param, success) {
                success();
            }
    };

    var mockCloneAccessorialsService = {
            copy : function() {
                success(param, success);
            }
    };

    var mockProfileDetailsService = {};
    var mockRouteParams = {};

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller){
        scope = $rootScope.$new();

        scope.$apply(function() {
            scope.profileDetailId = 1;
        });

        controller = $controller('AccessorialsCtrl', {$scope : scope, AccessorialTypesService : mockAccessorialTypesService, 
            AccessorialsListService : mockAccessorialsListService, AccessorialsService: mockAccessorialsService,
            AccessorialStatusChangeService : mockAccessorialStatusChangeService, CloneAccessorialsService : mockCloneAccessorialsService,
            PricingDetailsDictionaryService : mockPricingDetailsDictionaryService, ProfileDetailsService: mockProfileDetailsService,
            $routeParams: mockRouteParams});
        scope.$digest();
    }));

    it ('Should be initialized with default parameters', function() {
        c_expect(controller).to.be.defined;
        c_expect(scope.selectedItems).to.be.an('array');
        c_expect(scope.selectedAddresses).to.be.an('array');
        c_expect(scope.accessorial).to.be.an('object');
        c_expect(scope.isEditAccessorial).to.equal(false);
        c_expect(scope.changeStatusButtonName).to.equal('Archive');
        c_expect(scope.selectedProfileToCopy).to.be.an('function');
        c_expect(scope.copyAccessorials).to.be.an('function');
        c_expect(scope.resetAccessorial).to.be.an('function');
        c_expect(scope.loadListItems).to.be.an('function');
        c_expect(scope.gridOptions).to.be.an('object');
        c_expect(scope.editAccessorial).to.be.an('function');
        c_expect(scope.addressGrid).to.be.an('object');
        c_expect(scope.saveAccessorial).to.be.an('function');
        c_expect(scope.setZips).to.be.an('function');
        c_expect(scope.editZips).to.be.an('function');
        c_expect(scope.deleteZips).to.be.an('function');
        c_expect(scope.updateAccessorialStatus).to.be.an('function');
        c_expect(scope.expireAccessorials).to.be.an('function');
    });

    it('Should call loadListItems(Active) properly', function() {
        spyOn(scope, 'resetAccessorial').and.callThrough();
        spyOn(mockAccessorialsListService, 'active').and.callThrough();
        scope.loadListItems('ACTIVE');
        c_expect(mockAccessorialsListService.active.calls.count()).to.equal(1);
        c_expect(scope.resetAccessorial.calls.count()).to.equal(1);
        c_expect(scope.changeStatusButtonName).to.equal('Archive');
        c_expect(scope.currentTabName).to.equal('ACTIVE');
    });

    it('Should call loadListItems(Expired) properly', function() {
        spyOn(scope, 'resetAccessorial').and.callThrough();
        spyOn(mockAccessorialsListService, 'expired').and.callThrough();
        scope.loadListItems('EXPIRED');
        c_expect(mockAccessorialsListService.expired.calls.count()).to.equal(1);
        c_expect(scope.resetAccessorial.calls.count()).to.equal(1);
        c_expect(scope.changeStatusButtonName).to.equal('Archive');
        c_expect(scope.currentTabName).to.equal('EXPIRED');
    });

    it('Should call loadListItems(Expired) properly', function() {
        spyOn(scope, 'resetAccessorial').and.callThrough();
        spyOn(mockAccessorialsListService, 'archived').and.callThrough();
        scope.loadListItems('ARCHIVED');
        c_expect(mockAccessorialsListService.archived.calls.count()).to.equal(1);
        c_expect(scope.resetAccessorial.calls.count()).to.equal(1);
        c_expect(scope.changeStatusButtonName).to.equal('Reactivate');
        c_expect(scope.currentTabName).to.equal('ARCHIVED');
    });

    it('Should call updateAccessorialStatus(ACTIVE)properly', function() {
        scope.currentTabName = 'ACTIVE';
        spyOn(scope, 'resetAccessorial').and.callThrough();
        spyOn(mockAccessorialStatusChangeService, 'inactivate').and.callThrough();
        scope.updateAccessorialStatus();
        c_expect(mockAccessorialStatusChangeService.inactivate.calls.count()).to.equal(1);
        c_expect(scope.resetAccessorial.calls.count()).to.equal(1);
    });

    it('Should call updateAccessorialStatus(ARCHIVED)properly', function() {
        scope.currentTabName = 'ARCHIVED';
        spyOn(scope, 'resetAccessorial').and.callThrough();
        spyOn(mockAccessorialStatusChangeService, 'reactivate').and.callThrough();
        scope.updateAccessorialStatus();
        c_expect(mockAccessorialStatusChangeService.reactivate.calls.count()).to.equal(1);
        c_expect(scope.resetAccessorial.calls.count()).to.equal(1);
    });

    it('Should call expireAccessorials properly', function() {
        spyOn(scope, 'resetAccessorial').and.callThrough();
        spyOn(mockAccessorialStatusChangeService, 'expire').and.callThrough();
        scope.expireAccessorials();
        c_expect(mockAccessorialStatusChangeService.expire.calls.count()).to.equal(1);
        c_expect(scope.resetAccessorial.calls.count()).to.equal(1);
    });

    it ('Should call openDialog properly', function() {
        c_expect(controller).to.be.an('object');

        var eventParams = undefined;
        scope.$on('event:showConfirmation', function(event, params) {
            eventParams = params;
        });
        scope.selectedProfileToCopy();
        c_expect(eventParams).to.be.defined;
    });
});