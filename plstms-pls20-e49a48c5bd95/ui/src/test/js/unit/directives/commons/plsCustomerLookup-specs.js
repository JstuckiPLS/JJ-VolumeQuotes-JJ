/**
 * Tests customer lookup directive.
 *
 * @author Sergey Kirichenko
 */
describe('PLS Customer Lookup (plsCustomerLookup) Directive Test.', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;
    var timeoutService;

    var customers = [
        {id: 1, name: 'Customer 1'},
        {id: 2, name: 'Customer 2'},
        {id: 3, name: 'Customer 3'},
        {id: 4, name: 'Customer 4'},
        {id: 5, name: 'Customer 5'}
    ];

    var mockService = {
        findCustomer : function(criteria, count) {
            var result = _.filter(customers, function (customer) {
                return customer.name.indexOf(criteria) != -1;
            });
            if (count) {
                return result.slice(0, count);
            }
            return  result;
        }
    };

    var mockCustomerLabelResource = function () {
        return {
            $get: function (params, success) {
                success({id: params.customerId, label: _.findWhere(customers, {id: params.customerId}).name});
            }
        };
    };

    var plsUser = true;

    beforeEach(module('plsApp', 'pages/tpl/pls-typeahead-tpl.html', function($provide) {
        $provide.factory('CustomerLookupService', function() {
            return mockService;
        });
        $provide.factory('CustomerLabelResource', function() {
            return mockCustomerLabelResource;
        });
    }));

    beforeEach(inject(function($rootScope, $compile, $timeout) {
        elm = angular.element('<div><div data-pls-customer-lookup="testObject.customer" data-count="testObject.count" ' +
            'data-customer-disabled="testObject.disabled"></div></div>');
        scope = $rootScope.$new();
        timeoutService = $timeout;
        scope.$apply(function() {
            scope.testObject = {
                customer: undefined,
                count: 10,
                disabled: false
            };
        });
        $rootScope.authData.plsUser = plsUser;
        $rootScope.authData.customerUser = !plsUser;
        $rootScope.authData.organization.orgId = 10;
        $rootScope.authData.organization.name = 'test customer org';
        if (!plsUser) {
            $rootScope.authData.assignedOrganization = {
                orgId: 11,
                name: 'test customer org 1'
            }
        }
        $compile(elm)(scope);
        scope.$digest();
    }));

    it('should be initialized properly', function() {
        var inp = elm.find('input');
        c_expect(elm.length).to.equal(1);
        c_expect(elm).not.to.have.value();
        c_expect(scope.testObject.customer).not.to.be.defined;
        c_expect(elm).not.to.be.disabled;

        scope.$apply(function() {
            scope.testObject.customer = {id: 1, name: undefined};
        });
        c_expect(inp).to.have.value('');
        c_expect(scope.testObject.customer.name).to.be.undefined;
    });

    it('should find customer', function() {
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('Customer');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(5);
        var optionLink = options.eq(0).find('a');
        c_expect(optionLink).to.have.text('Customer 1');
        optionLink.click();
        c_expect(scope.testObject.customer.id).to.equal(1);
        input(inp).enter('');
        c_expect(scope.testObject.customer).to.not.exist;
    });

    it('should auto select customer', function() {
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('Customer 2');
        timeoutService.flush();
        c_expect(dropDiv).not.to.have.class('open');
        c_expect(scope.testObject.customer.id).to.equal(2);
    });

    it('should not find not existing customer', function() {
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('not_existing_customer_name');
        c_expect(dropDiv).not.to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options).to.not.exist;
        c_expect(scope.testObject.customer).to.not.exist;
    });

    it('should find limited options', function() {
        scope.$apply(function() {
            scope.testObject.count = 3;
        });
        spyOn(mockService, 'findCustomer').and.callThrough();
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('Customer');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(3);
        //check thta service was called correctly
        c_expect(mockService.findCustomer.calls.count()).to.equal(1);
        c_expect(mockService.findCustomer.calls.mostRecent().args).to.eql(['Customer', 3, undefined]);
    });

    it('should set customer user', function() {
        plsUser = false;
    });

    it('should init properly for customer user', function() {
        c_expect(scope.testObject.customer.id).to.equal(11);
        c_expect(scope.testObject.customer.name).to.equal('test customer org 1');
    });
});