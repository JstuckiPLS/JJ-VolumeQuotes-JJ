describe('Unit test for TrackingBoardAlertsSchedulerController', function() {
    var scope;
    var alertService;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();

        alertService = $injector.get('TrackingBoardAlertService');
        var controller = $injector.get('$controller');
        controller('TrackingBoardAlertsSchedulerController', {
            '$scope': scope,
            'TrackingBoardAlertService': alertService
        });
    }));

    it('should check alert count is zero', function() {
        expect(scope.alertsCount).toBe(0);
    });

    it('init alerts scheduler', function() {
        spyOn(scope, 'repeatCheckActions');
        scope.initAlertsScheduler();

        expect(scope.repeatCheckActions).toHaveBeenCalled();
    });

    it('should get alerts count', function() {
        spyOn(alertService, 'count');
        scope.getAlertsCount();

        expect(alertService.count).toHaveBeenCalledWith({}, jasmine.any(Function));
    });

    it('should decrease alerts count', function() {
        scope.alertsCount = 3;
        expect(scope.alertsCount).toBe(3);

        scope.$broadcast('event:trackingBoardAlertsChanged');

        expect(scope.alertsCount).toBe(2);
    });
});