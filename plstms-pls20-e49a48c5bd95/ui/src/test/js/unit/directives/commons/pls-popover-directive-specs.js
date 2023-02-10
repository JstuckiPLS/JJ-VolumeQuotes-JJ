/**
 * Tests for pls popover directive.
 *
 * @author Denis Zhupinsky
 */
describe('PLS Popover Directive Test.', function () {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;

    var document = undefined;

    beforeEach(module('plsApp'));


    beforeEach(inject(function ($rootScope, $compile, $document) {
        jasmine.clock().install();
        elm = angular.element(
                '<div id="container"><div id="content">Test popover content</div><div id="test-element" data-pls-popover="content"></div></div>');
        document = $document;
        scope = $rootScope.$new();
        var compiled = $compile(elm)(scope)
        document.find('body').append(compiled);
        scope.$digest();
    }));

    afterEach(function () {
        jasmine.clock().uninstall();
        document.find('div#container').remove();
    });

    it('should show content from element with specified id as popover', function () {
        c_expect(document.find('.popover')).not.to.exist;

        var popoverEl = elm.find("#test-element");
        popoverEl.trigger('mouseenter');

        var popoverDiv = document.find('.popover');
        c_expect(popoverDiv).to.exist;

        c_expect(popoverDiv.find('div#content')).to.exist;
    });


    it('should close popover after mouseleave', function () {
        var popoverEl = elm.find("#test-element");
        popoverEl.trigger('mouseenter');

        c_expect(document.find('.popover')).to.exist;

        popoverEl.trigger('mouseleave');
        c_expect(document.find('.popover')).to.exist;
        jasmine.clock().tick(201);
        c_expect(document.find('.popover')).not.to.exist;
    });
});