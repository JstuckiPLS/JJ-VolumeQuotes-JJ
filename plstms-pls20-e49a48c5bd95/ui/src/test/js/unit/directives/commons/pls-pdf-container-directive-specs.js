/**
 * Tests for pls pdf container directive.
 *
 * @author Denis Zhupinsky
 */
describe('PLS PDF container directive Test.', function () {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;

    var height = "700px";
    var width = "800px";

    beforeEach(module('plsApp'));

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();
        scope.$apply(function () {
            scope.docOptions = {
                height : height,
                width : width,
                pdfLocation : 'test.pdf'
            };
        });

        elm = angular.element('<div id="test-element" data-pls-pdf-container="docOptions"></div>');

        $compile(elm)(scope);

        scope.$digest();
    }));

    it('should show pdf document', function () {
        var viewerEl = elm.find('object[type="application/pdf"][data="test.pdf"]');
        c_expect(viewerEl).to.exist;
        c_expect(viewerEl.attr("width")).to.equal(width);
        c_expect(viewerEl.attr("height")).to.equal(height);
    });

    it('should show image document', function () {
        scope.$apply(function () {
            scope.docOptions.pdfLocation = 'test.png';
            scope.docOptions.imageContent = true;
        });

        var viewerEl = elm.find('img[src="test.png"]');
        c_expect(viewerEl).to.exist;
        c_expect(viewerEl.css("max-height")).to.equal(height);
        c_expect(viewerEl.attr("width")).to.equal(width);
    });

    it('should hide/show document', function () {
        scope.$apply(function () {
            scope.docOptions.pdfLocation = 'test.pdf';
        });

        c_expect(elm.find('object[type="application/pdf"][data="test.pdf"]')).to.exist;

        scope.$apply(function () {
            scope.docOptions.hidePdf = true;
        });

        c_expect(elm.find('object[type="application/pdf"][data="test.pdf"]')).not.to.exist;

        scope.$apply(function () {
            scope.docOptions.hidePdf = false;
        });

        c_expect(elm.find('object[type="application/pdf"][data="test.pdf"]')).to.exist;
    });

    it('should react on pdfLocation change', function () {
        scope.$apply(function () {
            scope.docOptions.pdfLocation = 'test.pdf';
        });

        c_expect(elm.find('object[type="application/pdf"]').attr("data")).to.equal('test.pdf');

        scope.$apply(function () {
            scope.docOptions.pdfLocation = 'test2.pdf';
        });

        c_expect(elm.find('object[type="application/pdf"]').attr("data")).to.equal('test2.pdf');
    });

});