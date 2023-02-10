/**
 * Unit tests for plsEmailsSeparatedList directive.
 *
 * @author Denis Zhupinsky
 */
describe('PLS emails separated list (plsEmailsSeparatedList) Directive Test.', function () {

    // angular scope
    var scope = undefined;
    var elm = undefined;

    beforeEach(module('plsApp'));

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();

        elm = angular.element('<textarea id="test-element" data-ng-model="emailsArea" data-pls-emails-separated-list></textarea>');
        $compile(elm)(scope);

        scope.$digest();
    }));

    it('should be non-valid if text is not emails list', function () {
        scope.$apply(function () {
            scope.emailsArea = 'wrong data';
        });
        c_expect(elm.val()).to.be.empty();
        c_expect(elm.hasClass('ng-valid')).to.be.false;
        c_expect(elm.hasClass('ng-invalid')).to.be.true;
    });

    it('should be valid if text is emails list', function () {
        scope.$apply(function () {
            scope.emailsArea = 'test@test.com';
        });
        c_expect(elm.val()).to.equal('test@test.com');
        c_expect(elm.hasClass('ng-valid')).to.be.true;
        c_expect(elm.hasClass('ng-invalid')).to.be.false;

        scope.$apply(function () {
            scope.emailsArea = 'test@test.com;more@more.com';
        });
    });

    it('should be non-valid if text contains not only emails', function () {
        scope.$apply(function () {
            scope.emailsArea = 'test@test.com;test2@test2.com;wrongdata';
        });
        c_expect(elm.val()).to.be.empty()
        c_expect(elm.hasClass('ng-valid')).to.be.false;
        c_expect(elm.hasClass('ng-invalid')).to.be.true;
    });

    it('should be non-valid if wrong separator', function () {
        scope.$apply(function () {
            scope.emailsArea = 'test@test.com/test2@test2.com';
        });
        c_expect(elm.val()).to.be.empty()
        c_expect(elm.hasClass('ng-valid')).to.be.false;
        c_expect(elm.hasClass('ng-invalid')).to.be.true;
    });
});