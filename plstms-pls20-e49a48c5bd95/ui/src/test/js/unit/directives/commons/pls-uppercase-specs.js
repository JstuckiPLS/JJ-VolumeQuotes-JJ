/**
 * Tests pls-uppercase directive.
 *
 * @author Sergey Kirichenko
 */
describe('PLS Uppercase (pls-uppercase) Directive Test.', function() {

    // angular scope
    var scope;
    var inp;
    var timeoutService;

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $compile, $timeout) {
        var element = angular.element('<div><input type="text" data-ng-model="inputValue" data-pls-uppercase/></div>');
        scope = $rootScope.$new();
        scope.$apply(function() {
            scope.inputValue = '';
        });
        $compile(element)(scope);
        scope.$digest();
        inp = element.find('input');
        timeoutService = $timeout;
    }));

    it('should change model to upper case when input in lowercase', function() {
        c_expect(scope.inputValue).to.be.empty();
        c_expect(inp).to.have.value('');
        input(inp).enter('test text');
        c_expect(scope.inputValue).to.equal('TEST TEXT');
    });

    it('should change model and input to upper case when model in lowercase', function() {
        c_expect(scope.inputValue).to.be.empty();
        c_expect(inp).to.have.value('');
        scope.$apply(function() {
            scope.inputValue = 'test text';
        });
        timeoutService.flush();
        c_expect(inp).to.have.value('TEST TEXT');
        c_expect(scope.inputValue).to.equal('TEST TEXT');
    });
});