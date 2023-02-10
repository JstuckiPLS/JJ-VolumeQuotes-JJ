/**
 * This unit test checks pls-wildcard-search directive.
 *
 * @author Sergey Kirichenko
 */
describe('PLS Wildcard Search (pls-wildcard-search) Directive Test.', function() {

    // angular scope
    var scope = undefined;
    var inp = undefined;

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        var elm = angular.element('<div>'
            + '<input type="text" id="testField" data-ng-model="testValue" data-pls-wildcard-search/>'
            + '</div>');
        $compile(elm)(scope);
        scope.$digest();

        inp = elm.find('input');
    }));

    var validValues = ['a', 'ab', 'abcdef', 'ab cd', 'a b', '*abc', '*a b', '*ab-cd', 'abc*', 'a b*', 'ab-cd*', '*abc*', '*a b*', '*ab-cd*'];
    var invalidValues = ['*', '**', '***', '* * *', '*a', '*ab', '*a ', 'a*', 'ab*', 'a *', '*a*', '*ab*', '*a *', '*a*b*'];

    it('should allow valid values for model', function() {
        _.each(validValues, function(value) {
            scope.$apply(function() {
                scope.testValue = value;
            });
            expect(inp.val()).toBe(value);
            expect(scope.testValue).toBe(value);
            expect(inp.hasClass('ng-valid')).toBeTruthy();
            expect(inp.hasClass('ng-invalid')).toBeFalsy();
        });
    });

    it('should allow enter valid values', function() {
        _.each(validValues, function(value) {
            input(inp).enter(value);
            expect(inp.val()).toBe(value);
            expect(scope.testValue).toBe(value);
            expect(inp.hasClass('ng-valid')).toBeTruthy();
            expect(inp.hasClass('ng-invalid')).toBeFalsy();
        });
    });

    it('should prohibit invalid values for model', function() {
        _.each(invalidValues, function(value) {
            scope.$apply(function() {
                scope.testValue = value;
            });
            expect(inp.val()).toBe('');
            expect(scope.testValue).toBe(value);
            expect(inp.hasClass('ng-valid')).toBeFalsy();
            expect(inp.hasClass('ng-invalid')).toBeTruthy();
        });
    });

    it('should prohibit enter invalid values', function() {
        _.each(invalidValues, function(value) {
            input(inp).enter(value);
            expect(inp.val()).toBe(value);
            expect(scope.testValue).not.toBeDefined();
            expect(inp.hasClass('ng-valid')).toBeFalsy();
            expect(inp.hasClass('ng-invalid')).toBeTruthy();
        });
    });
});