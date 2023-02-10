/**
 * Tests for pls progress panel directive.
 *
 * @author Denis Zhupinsky
 */
describe('PLS progress panel directive test.', function () {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;

    beforeEach(module('plsApp', 'pages/tpl/progress-panel-tpl.html'));

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();
        scope.$apply(function () {
            scope.progressPanelOptions = {
                showPanel: false
            };
        });

        elm = angular.element('<div id="test-element" data-pls-progress-panel="progressPanelOptions"></div>');
        $compile(elm)(scope);

        scope.$digest();
    }));

    it('should show progress panel', function () {
        c_expect(elm.find('img[src="resources/img/loading.gif"]')).to.exist;
        c_expect(elm).to.have.class('ng-hide');

        c_expect(elm.find('strong').text()).to.equal('Processing...');

        var progressText = 'Test progress text';
        scope.$apply(function () {
            scope.progressPanelOptions.showPanel = true;
            scope.progressPanelOptions.progressText = progressText;
        });

        c_expect(elm.css('display')).to.equal('');
        c_expect(elm.find('strong').text()).to.equal(progressText);
    });
});