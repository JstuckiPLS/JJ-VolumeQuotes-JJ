/**
 * Tests for pls-menu-item directive.
 * @author: Alexander Kirichenko
 */
describe('Test scenarios for pls-menu-item directive.', function() {
    var elm, scope;

    beforeEach(module('plsApp', function($provide) {
        $provide.factory('$location', function() {
            return {
                absUrl: function() {
                    return scope.highlightedUrl;
                },
                host: function() {
                    return 'localhost:8080';
                }
            };
        });
    }));

    beforeEach(inject(function($rootScope, $compile) {
        var html = '<ul class="nav nav-tabs">'
                + '<li class="pls-menu-item">'
                + '     <a href="#/quotes">Link 1</a>'
                + '</li>'
                + '<li class="pls-menu-item">'
                + '     <a href="#/customer">Link 2</a>'
                + '</li>'
                + '<li class="pls-menu-item">'
                + '     <a href="#/my-profile">Link 3</a>'
                + '</li>'
                + '</ul>';
        elm = angular.element(html);
        scope = $rootScope.$new();
        scope.highlightedUrl = '';
        scope.authData = new AuthData({personId: 1, firstName: 'sysadmin', fullName: 'sysadmin', plsUser: true,
            privilegies: ['QUOTES_VIEW', 'VIEW_MY_PROFILE']});
        $compile(elm)(scope);
        scope.$digest();
    }));

    it('Should highlight appropriate link', function() {
        scope.$apply(function() {
            scope.highlightedUrl = '#/quotes'
        });
        c_expect(elm.find('li:has(a[href="' + scope.highlightedUrl + '"])')).to.have.class('active');
        c_expect(elm.find('a[href!="' + scope.highlightedUrl + '"]').length).to.be.eql(2);
    });

    it('Should change link highlighting on url change', function() {
        scope.$apply(function() {
            scope.highlightedUrl = '#/quotes'
        });
        c_expect(elm.find('li:has(a[href="' + scope.highlightedUrl + '"])')).to.have.class('active');
        c_expect(elm.find('li:has(a[href!="' + scope.highlightedUrl + '"]):not(.active)').length).to.be.eql(2);
        scope.$apply(function() {
            scope.highlightedUrl = '#/my-profile'
        });
        c_expect(elm.find('li:has(a[href="#/quotes"])')).to.not.have.class('active');
        c_expect(elm.find('li:has(a[href="' + scope.highlightedUrl + '"])')).to.have.class('active');
        c_expect(elm.find('li:has(a[href!="' + scope.highlightedUrl + '"]):not(.active)').length).to.be.eql(2);
    });
});