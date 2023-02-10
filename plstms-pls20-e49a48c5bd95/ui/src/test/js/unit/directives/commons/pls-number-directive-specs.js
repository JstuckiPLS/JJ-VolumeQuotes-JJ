/**
 * This unit test checks pls-number directive.
 * 
 * @author Aleksandr Leshchenko
 */
describe('PLS Number (pls-number) Directive Test.', function() {

    // angular scope
    var scope = undefined;
    var compile = undefined;

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope;
        compile = $compile;
    }));

    function getCompiledInputElementWithoutForbidZero() {
        var elm = angular.element('<div>'
                + '<input type="text" id="testField"'
                + '       data-ng-model="testValue"'
                + '       data-pls-number/>'
                + '</div>');
        compile(elm)(scope);
        scope.$digest();

        inp = elm.find('input');
        expect(inp.length).toBe(1);
        return inp;
    }

    function getCompiledInputElementWithForbidZero(shouldForbidZero) {
        var elm = angular.element('<div>'
                + '<input type="text" id="testField"'
                + '       data-ng-model="testValue"'
                + '       data-pls-number'
                + '       data-forbid-zero="' + shouldForbidZero + '"/>'
                + '</div>');
        compile(elm)(scope);
        scope.$digest();
        inp = elm.find('input');
        expect(inp.length).toBe(1);
        return inp;
    }

    var possibleZeroValues = ['0', '.0', '0.0', '0000'];
    var validValues = [ null, undefined, '', '1','01', '2','02', '12', '100', '999999999999.999', '01234567890', '0.00000001', '1.2', '.35' ];
    var validValuesWithZero = _.flatten([validValues, possibleZeroValues]);
    var invalidValues = [ '-1', '-0', '-', 'a', '.', '.1.' ];
    var invalidValuesWithZero = _.flatten([invalidValues, possibleZeroValues]);

    it('should allow valid values without forbidZero attribute', function() {
        var inp = getCompiledInputElementWithoutForbidZero();
        _.each(validValuesWithZero, function(value) {
            scope.$apply(function() {
                scope.testValue = value;
            });
            expect(inp.val()).toBe(value ? parseFloat(value).toString() : '');
            expect(scope.testValue).toBe(value);
            expect(inp.hasClass('ng-valid')).toBeTruthy();
            expect(inp.hasClass('ng-invalid')).toBeFalsy();
        });
    });

    it('should prohibit invalid values without forbidZero attribute', function() {
        var inp = getCompiledInputElementWithoutForbidZero();

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

    it('should allow valid values with forbidZero=false attribute', function() {
        var inp = getCompiledInputElementWithForbidZero('1===2');

        _.each(validValuesWithZero, function(value) {
            scope.$apply(function() {
                scope.testValue = value;
            });
            expect(inp.val()).toBe(value ? parseFloat(value).toString() : '');
            expect(scope.testValue).toBe(value);
            expect(inp.hasClass('ng-valid')).toBeTruthy();
            expect(inp.hasClass('ng-invalid')).toBeFalsy();
        });
    });

    it('should prohibit invalid values with forbidZero=false attribute', function() {
        var inp = getCompiledInputElementWithForbidZero('false');

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

    it('should allow valid values with forbidZero=true attribute', function() {
        var inp = getCompiledInputElementWithForbidZero('hello');

        _.each(validValues, function(value) {
            scope.$apply(function() {
                scope.testValue = value;
            });
            expect(inp.val()).toBe(value ? parseFloat(value).toString() : '');
            expect(scope.testValue).toBe(value);
            expect(inp.hasClass('ng-valid')).toBeTruthy();
            expect(inp.hasClass('ng-invalid')).toBeFalsy();
        });
    });

    it('should prohibit invalid values with forbidZero=true attribute', function() {
        var inp = getCompiledInputElementWithForbidZero('2===2');

        _.each(invalidValuesWithZero, function(value) {
            scope.$apply(function() {
                scope.testValue = value;
            });
            expect(inp.val()).toBe('');
            expect(scope.testValue).toBe(value);
            expect(inp.hasClass('ng-valid')).toBeFalsy();
            expect(inp.hasClass('ng-invalid')).toBeTruthy();
        });
    });
});