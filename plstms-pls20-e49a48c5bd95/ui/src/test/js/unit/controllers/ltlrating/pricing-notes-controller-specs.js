/**
 * Tests ShowProfileNotesCtrl controller.
 * 
 * @author Artem Arapov
 */
describe('ShowProfileNotesCtrl controller test', function() {

    // Angular scope
    var scope = undefined;

    // ShowProfileNotesCtrl controller
    var controller = undefined;

    var notes = [
         {createdDate: new Date(), createdBy: 'Some Author', note: 'Some notes'},
         {createdDate: new Date(), createdBy: 'Some Other Author', note: 'Some other notes'}
    ];

    var mockProfileNotesService = {
        getNotes: function(param, success) {
            success({list: notes});
        }
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        scope.$apply(function() {
            scope.note = {};
            scope.profileDetails = {
                id : 1
            };
        });
        controller = $controller('ShowProfileNotesCtrl', {$scope: scope, ProfileNotesService: mockProfileNotesService});
        scope.$digest();
    }));

    it('should be initialized with default parameters', function() {
        c_expect(controller).to.be.an('object');
        c_expect(scope.note).to.be.an('object');
        c_expect(scope.gridOptions).to.be.an('object');
        c_expect(scope.loadNotes).to.be.an('function');
        c_expect(scope.submit).to.be.an('function');
    });

    it('should be initialized properly', function() {
        spyOn(mockProfileNotesService, 'getNotes').and.callThrough();
        scope.loadNotes();
        c_expect(mockProfileNotesService.getNotes.calls.count()).to.equal(1);
    });
});