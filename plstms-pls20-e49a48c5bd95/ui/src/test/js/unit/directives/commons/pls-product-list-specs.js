/**
 * Tests product list directive.
 *
 * @author Sergey Kirichenko
 */
describe('PLS Product List (pls-product-list-directive) Directive Test.', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;
    var timeoutService;

    var mockService = {
        filter : function(selectedCustomer, searchString, commodityClass, hazmatOnly) {
            if (searchString) {
                return [
                    { id: 1, label: 'Test product 1:' + searchString},
                    { id: 2, label: 'Test product 2:' + searchString},
                    { id: 3, label: 'Test product 3:' + searchString}
                ];
            }
            return [
                { id: 1, label: 'Test product 1'},
                { id: 2, label: 'Test product 2'},
                { id: 3, label: 'Test product 3'}
            ];
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/product-list-tpl.html', 'pages/tpl/pls-typeahead-tpl.html', function($provide) {
        $provide.factory('ProductFilterService', function() {
            return mockService;
        });
    }));

    beforeEach(inject(function($rootScope, $compile, $timeout) {
        elm = angular.element('<input id="product" class="span9 a_product" data-pls-product-list="testObject.product"'
                + '                 data-commodity-class="testObject.commodityClass" data-hazmat-only="testObject.hazmatOnly"'
                + '                 data-customer-id="testObject.selectedCustomer"/>');
        scope = $rootScope.$new();
        timeoutService = $timeout;
        scope.$apply(function() {
            scope.testObject = {
                product: undefined,
                commodityClass: undefined,
                hazmatOnly: undefined,
                selectedCustomer: 1
            };
        });
        $compile(elm)(scope);
        scope.$digest();
    }));

    it('should show all products and highlight first', function() {
        scope.$apply(function() {
            scope.testObject.product = undefined;
        });
        var dropDiv = elm.find('div.dropdown');
        var btn = elm.find('button');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(btn.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        btn.click();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(3);
        //first option must be selected
        c_expect(options.eq(0)).to.have.class('active');
        //second option must not be selected
        c_expect(options.eq(1)).not.to.have.class('active');
        //third option must not be selected
        c_expect(options.eq(2)).not.to.have.class('active');
    });

    it('should show all products and highlight selected', function() {
        scope.$apply(function() {
            scope.testObject.product = { id: 2, label: 'Test product 2'};
        });
        var dropDiv = elm.find('div.dropdown');
        var btn = elm.find('button');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(btn.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        btn.click();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(3);
        //first option must not be selected
        c_expect(options.eq(0)).not.to.have.class('active');
        //second option must be selected
        c_expect(options.eq(1)).to.have.class('active');
        //third option must not be selected
        c_expect(options.eq(2)).not.to.have.class('active');
    });

    it('should show all products and not highlight any products', function() {
        scope.$apply(function() {
            scope.testObject.product = { id: 5, label: 'Test product 5'};
        });
        var dropDiv = elm.find('div.dropdown');
        var btn = elm.find('button');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(btn.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        btn.click();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(3);
        //first option must not be selected
        c_expect(options.eq(0)).not.to.have.class('active');
        //second option must not be selected
        c_expect(options.eq(1)).not.to.have.class('active');
        //third option must not be selected
        c_expect(options.eq(2)).not.to.have.class('active');
    });

    it('should find product by search text', function() {
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('test_str');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(3);
        c_expect(options.eq(0).find('a').text()).to.contain('test_str');
        c_expect(options.eq(1).find('a').text()).to.contain('test_str');
        c_expect(options.eq(2).find('a').text()).to.contain('test_str');
    });

    it('should call product service with all parameters', function() {
        scope.$apply(function() {
            scope.testObject.commodityClass = 'CLASS_50';
            scope.testObject.hazmatOnly = true;
            scope.testObject.selectedCustomer = 1;
        });
        spyOn(mockService, 'filter').and.callThrough();
        var btn = elm.find('button');
        c_expect(btn.length).to.equal(1);
        btn.click();
        c_expect(mockService.filter.calls.count()).to.equal(1);
        c_expect(mockService.filter.calls.mostRecent().args).to.eql([1, '', 'CLASS_50', true]);
    });
});