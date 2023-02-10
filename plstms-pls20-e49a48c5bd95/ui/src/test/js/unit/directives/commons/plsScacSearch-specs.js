/**
 * Tests for plsScacSearch directive.
 *
 * @author Nikita Cherevko
 */
describe('PLS Scac Search (plsScacSearch) Directive Test.', function() {

    //directive element
    var elm = undefined;
    //angular scope
    var scope = undefined;
    var timeoutService;
    var inp, dropDiv;

    var carriers = [
        {id:1, name:"ALUK:ANK"},{id:2, name:"AVEL:Google"},{id:3, name:"AMVC:Yandex"},
        {id:4, name:"ALA:ESTES EXPRESS"},{id:5, name:"PITD:PITT OHIO EXPRESS, LLC"},{id: 6, name:"VOLT:VOLUNTEER EXPRESS INC"},
        {id:7, name:"LAXV:LAND AIR EXPRESS OF NEW ENGLAND"},{id:8, name:"MDLD:MIDLAND TRANSPORT LIMITED"},{id:9, name:"KTAI:KINGSWAY TRANSPORT"}
    ];

    var mockService = {
        findScac : function(criteria, count) {
            var result = _.filter(carriers, function(carr) {
                return (!criteria || carr.name.indexOf(criteria) != -1);
            });
            return result;
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/pls-typeahead-tpl.html', function($provide) {
        $provide.factory('ScacService', function() {
            return mockService;
        });
    }));

    beforeEach(inject(function($rootScope, $compile, $timeout) {
        elm = angular.element('<div><input pls-scac-search="testObj.plsScaSearch" required/></div>');
        scope = $rootScope.$new();
        timeoutService = $timeout;
        scope.$apply(function() {
            scope.testObj = {
                plsScaSearch: undefined,
                count: undefined
            };
        });
        $compile(elm)(scope);
        scope.$digest();
        inp = elm.find('input');
        dropDiv = elm.find('div.dropdown');
    }));

    it('should be invalid without model and valid with model', function() {
        c_expect(inp.length).to.equal(1);
        c_expect(inp).to.have.class('ng-invalid');
        scope.$apply(function() {
            scope.testObj.plsScaSearch = carriers[0];
        });
        c_expect(inp).not.to.have('ng-invalid');
        c_expect(inp).to.have.value(carriers[0].name);
    });

    it('should be invalid with partially input data but not selected', function() {
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        input(inp).enter('AL');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        c_expect(inp).to.have.class('ng-invalid');
    });

    it('should show options and select one option', function() {
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        input(inp).enter('AL');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(2);
        c_expect(options.eq(0)).to.contain('AL');
        c_expect(options.eq(1)).to.contain('AL');
        options.eq(0).click();
        c_expect(dropDiv).not.to.have.class('open');
        c_expect(inp).to.have.value(carriers[0].name);
        c_expect(scope.testObj.plsScaSearch).to.eql(carriers[0]);
    });

    it('should auto-select single variant', function() {
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        input(inp).enter(carriers[0].name);
        timeoutService.flush();
        c_expect(dropDiv).not.to.have.class('open');
        c_expect(inp).to.have.value(carriers[0].name);
        c_expect(scope.testObj.plsScaSearch).to.eql(carriers[0]);
    });

});
