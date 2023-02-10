/**
 * This unit test checks address list directive.
 *
 * @author Sergey Kirichenko
 */
describe('PLS Bill To List (pls-bill-to-list-directive) Directive Test.', function() {

    //directive element
    var elm = undefined;

    //angular scope
    var scope = undefined;
    var timeoutService;

    beforeEach(module('plsApp', 'pages/tpl/pls-typeahead-tpl.html', 'pages/tpl/pls-bill-to-list-tpl.html'));

    beforeEach(inject(function($rootScope, $compile, $timeout) {
        elm = angular.element('<div><input id="billToAddressNameTest12345" data-pls-bill-to-list="addressBookEntry" ' +
            'data-addresses="billToAddresses"></div>');
        scope = $rootScope;
        timeoutService = $timeout;
        scope.$apply(function() {
            scope.addressBookEntry = { id : 1, address : { id : 1, addressName : 'address1'} };
            scope.billToAddresses = [
                { id : 1, address : { id : 1, addressName : 'address1'} },
                { id : 2, address : { id : 2, addressName : 'address2'} }
            ];
        });
        $compile(elm)(scope);
        scope.$digest();
    }));

    it('should apply id attribute', function() {
        var rootElement = elm.find('#billToAddressNameTest12345');
        c_expect(rootElement.length).to.equal(1);
    });

    it('should show options', function() {
        var dropDiv = elm.find('div.dropdown');
        var btn = elm.find('button');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(btn.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        btn.click();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(2);
        //first option must be selected
        c_expect(options.eq(0)).to.have.class('active');
        //second option must not be selected
        c_expect(options.eq(1)).not.to.have.class('active');
    });

    it('should highlight selected option', function() {
        scope.$apply(function() {
            scope.addressBookEntry = scope.billToAddresses[1];
        });
        var dropDiv = elm.find('div.dropdown');
        var btn = elm.find('button');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(btn.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        btn.click();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(2);
        //first option must not be selected
        c_expect(options.eq(0)).not.to.have.class('active');
        //second option must be selected
        c_expect(options.eq(1)).to.have.class('active');
    });

    it('should show options when input text', function() {
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('addr');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
    });

    it('should change selection', function() {
        var dropDiv = elm.find('div.dropdown');
        var btn = elm.find('button');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(btn.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //show options
        btn.click();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(2);
        //select second option
        options.eq(1).find('a').click();
        c_expect(scope.addressBookEntry).not.to.be.null();
        c_expect(scope.addressBookEntry.id).to.equal(2);
        c_expect(scope.addressBookEntry.address.addressName).to.equal('address2');
    });

    it('should automatically select new address', function() {
        c_expect(scope.addressBookEntry).not.to.be.null();
        c_expect(scope.addressBookEntry.id).to.equal(1);
        c_expect(scope.addressBookEntry.address.addressName).to.equal('address1');
        var inp = elm.find('input');
        c_expect(inp.length).to.equal(1);
        //set new value in the input
        input(inp).enter('address2');
        timeoutService.flush();
        c_expect(scope.addressBookEntry).not.to.be.null();
        c_expect(scope.addressBookEntry.id).to.equal(2);
        c_expect(scope.addressBookEntry.address.addressName).to.equal('address2');
    });

    it('should input partially address name and then select from options', function() {
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('addr');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(2);
        //select second option
        options.eq(1).find('a').click();
        c_expect(scope.addressBookEntry).not.to.be.null();
        c_expect(scope.addressBookEntry.id).to.equal(2);
        c_expect(scope.addressBookEntry.address.addressName).to.equal('address2');
    });

    it('should not change selected address name', function() {
        var inp = elm.find('input');
        c_expect(inp.length).to.equal(1);
        c_expect(scope.addressBookEntry).not.to.be.null();
        c_expect(scope.addressBookEntry.id).to.equal(1);
        c_expect(scope.addressBookEntry.address.addressName).to.equal('address1');
        //set new value in the input
        input(inp).enter('new_address_name');
        c_expect(scope.addressBookEntry).to.be.undefined();
    });
});
