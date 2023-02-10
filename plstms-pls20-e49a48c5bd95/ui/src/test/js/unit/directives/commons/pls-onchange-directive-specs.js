/**
 * Tests for pls onchange directive.
 *
 * @author Nikita Cherevko
 */
describe('PLS Onchange Directive Test.', function () {

    var scope = undefined;
    var elm = undefined;

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $compile) {
        elm = angular.element('<div><input data-pls-onchange="changeFunc" /></div>');
        scope = $rootScope.$new();
        scope.changeValue = 0;
        scope.$apply(function() {
            scope.changeFunc = function() {
                scope.changeValue++;
            }
        });
        $compile(elm)(scope);
        scope.$digest();
    }));

    it('should call changeFunc', function() {
        var inp = elm.find('input');
        c_expect(inp.length).to.equal(1);
        c_expect(scope.changeValue).to.equal(0);
        spyOn(scope, 'changeFunc').and.callThrough();
        input(inp).enter("text");
        c_expect(scope.changeValue).to.equal(1);
        c_expect(scope.changeFunc.calls.count()).to.equal(1);
        c_expect(scope.changeFunc.calls.mostRecent().args).to.eql(["text"]);
    });

});
