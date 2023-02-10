/**
 * This unit test checks pls-form-disabled directive.
 * 
 * @author Aleksandr Leshchenko
 */
describe('PLS Form Disabled (pls-form-disabled) Directive Test.', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $compile, $filter) {
        elm = angular.element('<div data-pls-form-disabled="formDisabled">'
                // inputs
                + '<input type="text"/>'
                + '<input type="text" data-ng-disabled="inputDisabled"/>'
                + '<input type="text" ng-disabled="inputDisabled"/>'
                // inputs which shouldn't be disabled 
                + '<input type="text" data-not-disable-me/>'
                + '<input type="text" data-not-disable-me data-ng-disabled="inputDisabled"/>'
                + '<input type="text" data-not-disable-me ng-disabled="inputDisabled"/>'
                // inputs rendered by condition
                + '<input type="text" data-ng-if="hiddenItemsRendered"/>'
                + '<input type="text" data-ng-if="hiddenItemsRendered" data-ng-disabled="inputDisabled"/>'
                + '<input type="text" data-ng-if="hiddenItemsRendered" ng-disabled="inputDisabled"/>'
                // inputs rendered by condition which shouldn't be disabled
                + '<input type="text" data-ng-if="hiddenItemsRendered" data-not-disable-me/>'
                + '<input type="text" data-ng-if="hiddenItemsRendered" data-not-disable-me data-ng-disabled="inputDisabled"/>'
                + '<input type="text" data-ng-if="hiddenItemsRendered" data-not-disable-me ng-disabled="inputDisabled"/>'

                // selects
                + '<select><option value="1">1</option></select>'
                + '<select data-ng-disabled="inputDisabled"><option value="1">1</option></select>'
                + '<select ng-disabled="inputDisabled"><option value="1">1</option></select>'
                // selects which shouldn't be disabled 
                + '<select data-not-disable-me><option value="1">1</option></select>'
                + '<select data-not-disable-me data-ng-disabled="inputDisabled"><option value="1">1</option></select>'
                + '<select data-not-disable-me ng-disabled="inputDisabled"><option value="1">1</option></select>'
                // selects rendered by condition
                + '<select data-ng-if="hiddenItemsRendered"><option value="1">1</option></select>'
                + '<select data-ng-if="hiddenItemsRendered" data-ng-disabled="inputDisabled"><option value="1">1</option></select>'
                + '<select data-ng-if="hiddenItemsRendered" ng-disabled="inputDisabled"><option value="1">1</option></select>'
                // selects rendered by condition which shouldn't be disabled
                + '<select data-ng-if="hiddenItemsRendered" data-not-disable-me><option value="1">1</option></select>'
                + '<select data-ng-if="hiddenItemsRendered" data-not-disable-me data-ng-disabled="inputDisabled"><option value="1">1</option></select>'
                + '<select data-ng-if="hiddenItemsRendered" data-not-disable-me ng-disabled="inputDisabled"><option value="1">1</option></select>'

                // buttons
                + '<button type="button"/>'
                + '<button type="button" data-ng-disabled="inputDisabled"/>'
                + '<button type="button" ng-disabled="inputDisabled"/>'
                // buttons which shouldn't be disabled 
                + '<button type="button" data-not-disable-me/>'
                + '<button type="button" data-not-disable-me data-ng-disabled="inputDisabled"/>'
                + '<button type="button" data-not-disable-me ng-disabled="inputDisabled"/>'
                // buttons rendered by condition
                + '<button type="button" data-ng-if="hiddenItemsRendered"/>'
                + '<button type="button" data-ng-if="hiddenItemsRendered" data-ng-disabled="inputDisabled"/>'
                + '<button type="button" data-ng-if="hiddenItemsRendered" ng-disabled="inputDisabled"/>'
                // buttons rendered by condition which shouldn't be disabled
                + '<button type="button" data-ng-if="hiddenItemsRendered" data-not-disable-me/>'
                + '<button type="button" data-ng-if="hiddenItemsRendered" data-not-disable-me data-ng-disabled="inputDisabled"/>'
                + '<button type="button" data-ng-if="hiddenItemsRendered" data-not-disable-me ng-disabled="inputDisabled"/>'

                // radio
                + '<input type="radio"/>'
                + '<input type="radio" data-ng-disabled="inputDisabled"/>'
                + '<input type="radio" ng-disabled="inputDisabled"/>'
                // radios which shouldn't be disabled 
                + '<input type="radio" data-not-disable-me/>'
                + '<input type="radio" data-not-disable-me data-ng-disabled="inputDisabled"/>'
                + '<input type="radio" data-not-disable-me ng-disabled="inputDisabled"/>'
                // radios rendered by condition
                + '<input type="radio" data-ng-if="hiddenItemsRendered"/>'
                + '<input type="radio" data-ng-if="hiddenItemsRendered" data-ng-disabled="inputDisabled"/>'
                + '<input type="radio" data-ng-if="hiddenItemsRendered" ng-disabled="inputDisabled"/>'
                // radios rendered by condition which shouldn't be disabled
                + '<input type="radio" data-ng-if="hiddenItemsRendered" data-not-disable-me/>'
                + '<input type="radio" data-ng-if="hiddenItemsRendered" data-not-disable-me data-ng-disabled="inputDisabled"/>'
                + '<input type="radio" data-ng-if="hiddenItemsRendered" data-not-disable-me ng-disabled="inputDisabled"/>'

                // checkbox
                + '<input type="checkbox"/>'
                + '<input type="checkbox" data-ng-disabled="inputDisabled"/>'
                + '<input type="checkbox" ng-disabled="inputDisabled"/>'
                // checkboxes which shouldn't be disabled 
                + '<input type="checkbox" data-not-disable-me/>'
                + '<input type="checkbox" data-not-disable-me data-ng-disabled="inputDisabled"/>'
                + '<input type="checkbox" data-not-disable-me ng-disabled="inputDisabled"/>'
                // checkboxes rendered by condition
                + '<input type="checkbox" data-ng-if="hiddenItemsRendered"/>'
                + '<input type="checkbox" data-ng-if="hiddenItemsRendered" data-ng-disabled="inputDisabled"/>'
                + '<input type="checkbox" data-ng-if="hiddenItemsRendered" ng-disabled="inputDisabled"/>'
                // checkboxes rendered by condition which shouldn't be disabled
                + '<input type="checkbox" data-ng-if="hiddenItemsRendered" data-not-disable-me/>'
                + '<input type="checkbox" data-ng-if="hiddenItemsRendered" data-not-disable-me data-ng-disabled="inputDisabled"/>'
                + '<input type="checkbox" data-ng-if="hiddenItemsRendered" data-not-disable-me ng-disabled="inputDisabled"/>'

                // file
                + '<input type="file"/>'
                + '<input type="file" data-ng-disabled="inputDisabled"/>'
                + '<input type="file" ng-disabled="inputDisabled"/>'
                // files which shouldn't be disabled 
                + '<input type="file" data-not-disable-me/>'
                + '<input type="file" data-not-disable-me data-ng-disabled="inputDisabled"/>'
                + '<input type="file" data-not-disable-me ng-disabled="inputDisabled"/>'
                // files rendered by condition
                + '<input type="file" data-ng-if="hiddenItemsRendered"/>'
                + '<input type="file" data-ng-if="hiddenItemsRendered" data-ng-disabled="inputDisabled"/>'
                + '<input type="file" data-ng-if="hiddenItemsRendered" ng-disabled="inputDisabled"/>'
                // files rendered by condition which shouldn't be disabled
                + '<input type="file" data-ng-if="hiddenItemsRendered" data-not-disable-me/>'
                + '<input type="file" data-ng-if="hiddenItemsRendered" data-not-disable-me data-ng-disabled="inputDisabled"/>'
                + '<input type="file" data-ng-if="hiddenItemsRendered" data-not-disable-me ng-disabled="inputDisabled"/>'

                + '</div>');
        scope = $rootScope;
        $compile(elm)(scope);
        scope.$digest();
    }));

    function applyScopeParameters(formDisabled, inputDisabled, hiddenItemsRendered) {
        scope.$apply(function() {
            scope.formDisabled = formDisabled;
            scope.inputDisabled = inputDisabled;
            scope.hiddenItemsRendered = hiddenItemsRendered;
        });
        scope.$digest();
    }

    it('should disable form', function() {
        applyScopeParameters(false, false, true);
        expect(elm.find('>:enabled').length).toBe(72);
        expect(elm.find('>:disabled').length).toBe(0);

        applyScopeParameters(true, false, true);
        expect(elm.find('>:enabled').length).toBe(36);
        expect(elm.find('>:disabled').length).toBe(36);

        applyScopeParameters(true, true, true);
        expect(elm.find('>:enabled').length).toBe(12);
        expect(elm.find('>:disabled').length).toBe(60);

        applyScopeParameters(false, false, false);
        expect(elm.find('>:enabled').length).toBe(36);
        expect(elm.find('>:disabled').length).toBe(0);

        applyScopeParameters(true, false, false);
        expect(elm.find('>:enabled').length).toBe(18);
        expect(elm.find('>:disabled').length).toBe(18);

        applyScopeParameters(true, false, true);
        expect(elm.find('>:enabled').length).toBe(36);
        expect(elm.find('>:disabled').length).toBe(36);

        applyScopeParameters(true, true, true);
        expect(elm.find('>:enabled').length).toBe(12);
        expect(elm.find('>:disabled').length).toBe(60);

        applyScopeParameters(false, true, false);
        expect(elm.find('>:enabled').length).toBe(12);
        expect(elm.find('>:disabled').length).toBe(24);
    });
});