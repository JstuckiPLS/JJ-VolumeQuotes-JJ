/**
 * Tests ProfilesListCtrl controller.
 * 
 * @author Sergii Belodon
 */
describe('ProfilesListCtrl controller test', function() {
    var $scope = null;

    var controller;

    var activeProfileList = [ {
        carrierName : "SAIA",
        carrierOrgId : 68,
        effDate : "2012-11-24T00:00:00.000Z",
        expDate : "2012-12-24T00:00:00.000Z",
        ltlPricingProfileId : 48,
        pricingType : "Blanket",
        rateName : "048-SAIA",
        scac : "SAIA"
    }, {
        carrierName : "New penn",
        carrierOrgId : 67,
        effDate : "2012-11-24T00:00:00.000Z",
        expDate : "3016-03-01T00:00:00.000Z",
        ltlPricingProfileId : 47,
        pricingType : "Blanket",
        rateName : "047-NPME",
        scac : "NPME"
    }, {
        carrierName : "USF Holland",
        carrierOrgId : 66,
        effDate : "2012-11-24T00:00:00.000Z",
        expDate : "3016-03-01T00:00:00.000Z",
        ltlPricingProfileId : 46,
        pricingType : "Blanket",
        rateName : "046-HMES",
        scac : "HMES"
    } ];

    var inactiveProfileList = [ {
        carrierName : "SAIA",
        carrierOrgId : 68,
        effDate : "2012-11-24T00:00:00.000Z",
        expDate : "3016-03-01T00:00:00.000Z",
        ltlPricingProfileId : 48,
        pricingType : "Blanket",
        rateName : "048-SAIA",
        scac : "SAIA"
    }, {
        carrierName : "New penn",
        carrierOrgId : 67,
        effDate : "2012-11-24T00:00:00.000Z",
        expDate : "3016-03-01T00:00:00.000Z",
        ltlPricingProfileId : 47,
        pricingType : "Blanket",
        rateName : "047-NPME",
        scac : "NPME"
    } ];

    var mockProfilesList = {
        get : function(param, criteria, success) {
            if (criteria.status === 'ACTIVE') {
                success(activeProfileList);
            }
            if (criteria.status === 'INACTIVE') {
                success(inactiveProfileList);
            }
        }
    };

    var mockProfileDictionaryService = {
        get : function(success) {
        }
    };

    var mockLocation = {
        url : function(path,success) {
        }
    };

    var mockProfileStatusChangeService = {
        inactivate : function(param,criteria,success){
        },

        reactivate : function(param,criteria,success){
        }
    };

    var mockNgGridPluginFactory = {
        plsGrid: function() {
        },
        progressiveSearchPlugin: function() {
        }
    };

    var route = {
        current : {
            active: true
        }
    };

    beforeEach(module('ngRoute'));
    beforeEach(module('pricing'));
    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope) {
        $scope = $rootScope.$new();
    }));

    beforeEach(inject(function($controller) {
        controller = $controller('ProfilesListCtrl', {
            $scope: $scope,
            $route: route,
            ProfileDictionaryService: mockProfileDictionaryService,
            ProfilesListService: mockProfilesList,
            ProfileApplicableCustomersService: null,
            ProfileStatusChangeService: mockProfileStatusChangeService,
            $location: mockLocation,
            NgGridPluginFactory: mockNgGridPluginFactory
        });
    }));

    it('should check profile model initial state', function() {
        expect($scope.profileDictionary).toBeUndefined();
        expect($scope.profileModel.criteria.pricingGroup).toEqual('CARRIER');
        expect($scope.profileModel.criteria.dateType).toEqual('EFFECTIVE');
        expect($scope.profileModel.criteria.dateRange).toEqual('NONE');
        expect($scope.profileModel.criteria.status).toEqual('ACTIVE');
        expect($scope.profileModel.profileNextStatus).toEqual('Inactivate');
        expect($scope.profileModel.searchDateTypes).toBeDefined();
        expect($scope.profileModel.searchDateTypes[0].id).toEqual('NONE');
        expect($scope.profileModel.searchDateTypes[0].label).toEqual('');
        expect($scope.profileModel.searchDateTypes[1].id).toEqual('EFFECTIVE');
        expect($scope.profileModel.searchDateTypes[1].label).toEqual('Effective');
        expect($scope.profileModel.searchDateTypes[2].id).toEqual('EXPIRATION');
        expect($scope.profileModel.searchDateTypes[2].label).toEqual('Expires');
        expect($scope.profileModel.searchDateRanges).toBeDefined();
        expect($scope.profileModel.searchDateRanges[0].id).toEqual('NONE');
        expect($scope.profileModel.searchDateRanges[0].label).toEqual('');
        expect($scope.profileModel.searchDateRanges[1].id).toEqual('TODAY');
        expect($scope.profileModel.searchDateRanges[1].label).toEqual('Today');
        expect($scope.profileModel.searchDateRanges[2].id).toEqual('THIS_WEEK');
        expect($scope.profileModel.searchDateRanges[2].label).toEqual('This Week');
        expect($scope.profileModel.searchDateRanges[3].id).toEqual('LAST_WEEK');
        expect($scope.profileModel.searchDateRanges[3].label).toEqual('Last Week');
        expect($scope.profileModel.searchDateRanges[4].id).toEqual('CUSTOM');
        expect($scope.profileModel.searchDateRanges[4].label).toEqual('Custom');
    });

    it('should select all pricing types', function() {
        var event = {target: {checked: true}};
        $scope.profileDictionary = {pricingTypes: [{ltlPricingType: 'BLANKET', selected: false},
                                                   {ltlPricingType: 'BLANKET_CSP', selected: false},
                                                   {ltlPricingType: 'CSP', selected: false},
                                                   {ltlPricingType: 'BUY_SELL', selected: false}]};
        $scope.selectAll(event);

        expect($scope.profileModel.criteria.pricingTypes).toContain('BLANKET');
        expect($scope.profileModel.criteria.pricingTypes).toContain('BLANKET_CSP');
        expect($scope.profileModel.criteria.pricingTypes).toContain('CSP');
        expect($scope.profileModel.criteria.pricingTypes).toContain('BUY_SELL');

        expect($scope.profileDictionary.pricingTypes[0].selected).toBe(true);
        expect($scope.profileDictionary.pricingTypes[1].selected).toBe(true);
        expect($scope.profileDictionary.pricingTypes[2].selected).toBe(true);
        expect($scope.profileDictionary.pricingTypes[3].selected).toBe(true);
    });

    it('should load properly', function() {
        var event = {target: {checked: true}};
        $scope.profileDictionary = {pricingTypes: [{ltlPricingType: 'BLANKET', selected: false},
                                                   {ltlPricingType: 'BLANKET_CSP', selected: false},
                                                   {ltlPricingType: 'CSP', selected: false},
                                                   {ltlPricingType: 'BUY_SELL', selected: false}]};
        $scope.selectAll(event);
        c_expect(controller).to.be.an('object');
        $scope.loadProfilesByCriteria('ACTIVE');
        c_expect($scope.profileList.length).to.equal(3);
        $scope.loadProfilesByCriteria('INACTIVE');
        c_expect($scope.profileList.length).to.equal(2);
    });

    it('should search by date range properly', function() {
        c_expect(controller).to.be.an('object');
        var event = {target: {checked: true}};
        $scope.profileDictionary = {pricingTypes: [{ltlPricingType: 'BLANKET', selected: false},
                                                   {ltlPricingType: 'BLANKET_CSP', selected: false},
                                                   {ltlPricingType: 'CSP', selected: false},
                                                   {ltlPricingType: 'BUY_SELL', selected: false}]};
        $scope.selectAll(event);
        $scope.searchByDateRange();
        c_expect($scope.profileList.length).to.equal(3);
        $scope.profileModel.criteria.dateRange = 'TODAY';
        $scope.searchByDateRange();
        $scope.profileModel.criteria.dateType = 'EFFECTIVE';
        $scope.searchByDateRange();
        c_expect($scope.profileList.length).to.equal(3);
        $scope.profileModel.criteria.dateRange = 'THIS_WEEK';
        $scope.searchByDateRange();
        c_expect($scope.profileList.length).to.equal(3);
        $scope.profileModel.criteria.dateRange = 'LAST_WEEK';
        $scope.searchByDateRange();
        c_expect($scope.profileList.length).to.equal(3);
    });

    it('should navigate to edit profile properly', function() {
        $scope.selectedItems = undefined;
        $scope.profileModel.criteria.status = 'INACTIVE';
        spyOn(mockLocation,'url');
        $scope.editProfile(); 
        c_expect(mockLocation.url.calls.count()).to.equal(0);
        $scope.selectedItems = [activeProfileList[0]];
        $scope.profileModel.criteria.status = 'ACTIVE';
        $scope.editProfile();   
        c_expect(mockLocation.url.calls.count()).to.equal(1);
        c_expect(mockLocation.url.calls.mostRecent().args[0]).to.eql('/pricing/tariffs/48/edit?profile.details');
    });

    it('should navigate to copy profile properly', function() {
        $scope.selectedItems = undefined;
        $scope.profileModel.criteria.status = 'INACTIVE';
        spyOn(mockLocation,'url');
        $scope.copyProfile(); 
        c_expect(mockLocation.url.calls.count()).to.equal(0);
        $scope.selectedItems = [activeProfileList[0]];
        $scope.profileModel.criteria.status = 'ACTIVE';
        $scope.copyProfile();   
        c_expect(mockLocation.url.calls.count()).to.equal(1);
        c_expect(mockLocation.url.calls.mostRecent().args[0]).to.eql('/pricing/tariffs/48/cpy?profile.details');
    });

    it('should navigate to copy profile properly', function() {
        $scope.selectedItems = undefined;
        $scope.profileModel.criteria.status = 'INACTIVE';
        spyOn(mockLocation,'url');
        $scope.addProfile(); 
        c_expect(mockLocation.url.calls.count()).to.equal(1);
        c_expect(mockLocation.url.calls.mostRecent().args[0]).to.eql('/pricing/tariffs/-1/add?profile.details');
    });

    it('should change status properly', function() {
        $scope.profileModel.criteria.status = 'ACTIVE';
        $scope.selectedItems = [{ltlPricingProfileId: 1}];
        spyOn(mockProfileStatusChangeService,'inactivate');
        spyOn(mockProfileStatusChangeService,'reactivate');
        $scope.updateProfileStatus();
        c_expect(mockProfileStatusChangeService.inactivate.calls.count()).to.equal(1);
        c_expect(mockProfileStatusChangeService.reactivate.calls.count()).to.equal(0);
        $scope.profileModel.criteria.status = 'INACTIVE';
        $scope.updateProfileStatus();
        c_expect(mockProfileStatusChangeService.inactivate.calls.count()).to.equal(1);
        c_expect(mockProfileStatusChangeService.reactivate.calls.count()).to.equal(1);
    });
});