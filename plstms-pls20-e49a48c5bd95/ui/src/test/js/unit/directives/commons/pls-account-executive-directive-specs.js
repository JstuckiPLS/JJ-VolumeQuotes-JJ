/**
 * Tests account executive directive.
 *
 * @author Sergey Kirichenko
 */
describe('PLS Account Executive (pls-account-executive-directive) Directive Test.', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;
    var timeoutService;
    var inp, dropDiv;

    var mockService = {
        getAccountExecutives : function() {
            return {
                then: function (fn) {
                    return fn({data: [
                        { id: 1, name: 'Test person 1'},
                        { id: 2, name: 'Test person 2'},
                        { id: 3, name: 'Test person 3'}
                    ]});
                }
            }
        },
        getAccountExecutivesByFilter : function(searchString) {
            return {
                then: function (fn) {
                    return fn({data: [
                        { id: 1, name: 'Test person 1:' + searchString || ''},
                        { id: 2, name: 'Test person 2:' + searchString || ''},
                        { id: 3, name: 'Test person 3:' + searchString || ''}
                    ]});
                }
            }
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/pls-typeahead-tpl.html', function($provide) {
        $provide.factory('CustomerService', function() {
            return mockService;
        });
    }));

    beforeEach(inject(function($rootScope, $compile, $timeout) {
        elm = angular.element('<div><input data-pls-account-executive="testObject.accountExecutive" ></div>');
        scope = $rootScope.$new();
        timeoutService = $timeout;
        scope.$apply(function() {
            scope.testObject = {
                accountExecutive: undefined
            };
        });
        $compile(elm)(scope);
        scope.$digest();
        inp = elm.find('input');
        dropDiv = elm.find('div.dropdown');
    }));

    it('should find accounts', function() {
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('account_name');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(3);
        c_expect(options.eq(0).find('a')).to.contain('account_name');
        c_expect(options.eq(1).find('a')).to.contain('account_name');
        c_expect(options.eq(2).find('a')).to.contain('account_name');
    });

    it('should select option', function() {
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('account_name');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var option = dropDiv.find('ul.typeahead li a');
        c_expect(option.length).to.equal(3);
        var selectedName = option.eq(0).text();
        option.eq(0).click();
        c_expect(scope.testObject.accountExecutive.id).to.equal(1);
        c_expect(inp).to.have.value(selectedName);
    });

    it('should call service with criteria', function() {
        spyOn(mockService, 'getAccountExecutivesByFilter').and.callThrough();
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('account_name');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        c_expect(mockService.getAccountExecutivesByFilter.calls.count()).to.equal(1);
        c_expect(mockService.getAccountExecutivesByFilter.calls.mostRecent().args).to.eql(['account_name']);
    });
});