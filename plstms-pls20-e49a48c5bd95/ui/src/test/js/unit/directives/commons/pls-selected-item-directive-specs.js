/**
 * This unit test checks pls-selected-item directive.
 * 
 * @author Aleksandr Leshchenko
 */
describe('PLS Selected Item Directive Test.', function() {

 // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $compile) {
        elm = angular.element('<span data-pls-selected-item="selected" data-label="{{label}}"></span>');
        scope = $rootScope;
        $compile(elm)(scope);
        scope.$digest();
    }));

    it('should render active selected item with icon', function() {
        scope.$apply(function() {
            scope.selected = true;
            scope.label = 'test label 1';
        });
        scope.$digest();

        expect(elm.find('i')).not.toHaveClass('invisible');
        expect(elm.find('i')).toHaveClass('icon-ok');

        expect(elm.find('span').text()).toBe(' test label 1');
        expect(elm.find('span')).not.toHaveClass('muted');
    });

    it('should render inactive selected item without icon', function() {
        scope.$apply(function() {
            scope.selected = false;
            scope.label = 'test label 2';
        });
        scope.$digest();

        expect(elm.find('i')).toHaveClass('invisible');
        expect(elm.find('i')).toHaveClass('icon-ok');

        expect(elm.find('span').text()).toBe(' test label 2');
        expect(elm.find('span')).toHaveClass('muted');
    });
});