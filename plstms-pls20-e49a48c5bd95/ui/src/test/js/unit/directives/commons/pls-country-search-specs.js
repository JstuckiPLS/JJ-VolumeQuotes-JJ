/**
 * Tests country search directive.
 *
 * @author Sergey Kirichenko
 */
describe('PLS Country Search (pls-country-search-directive) Directive Test.', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;
    var timeoutService;

    var countries = [
        {id: "UGA", name: "Uganda", dialingCode: "256"},
        {id: "UKR", name: "Ukraine", dialingCode: "380"},
        {id: "ARE", name: "United Arab Emirates", dialingCode: "971"},
        {id: "GBR", name: "United Kingdom", dialingCode: "044"},
        {id: "URY", name: "Uraguay", dialingCode: "598"},
        {id: "USA", name: "United States of America", dialingCode: "001"},
        {id: "UZB", name: "Uzbekistan", dialingCode: "007"}
    ];

    var mockService = {
        searchCountries : function(criteria, count) {
            var result = _.filter(countries, function (country) {
                return country.id.indexOf(criteria) != -1 || country.name.indexOf(criteria) != -1;
            });
            if (count) {
                return result.slice(0, count);
            }
            return  result;
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/pls-typeahead-tpl.html', 'pages/tpl/pls-country-search-tpl.html', function($provide) {
        $provide.factory('CountryService', function() {
            return mockService;
        });
    }));

    beforeEach(inject(function($rootScope, $compile, $timeout) {
        elm = angular.element('<div><input data-pls-country-search="testObject.country" data-count="{{testObject.count}}" '
                    + '             data-country-disabled="testObject.disabled"></div>');
        scope = $rootScope.$new();
        timeoutService = $timeout;
        scope.$apply(function() {
            scope.testObject = {
                country: undefined,
                count: undefined,
                disabled: undefined
            };
        });
        $compile(elm)(scope);
        scope.$digest();
    }));

    it('should be invalid without model and valid with model and have default value', function() {
        var inp = elm.find('input');
        c_expect(inp.length).to.equal(1);
        c_expect(inp).not.to.have.class('ng-invalid');
        c_expect(inp).to.have.value('USA');
        c_expect(scope.testObject.country).to.deep.equal({id: "USA", name: "United States of America", dialingCode: "1"});
        scope.$apply(function() {
            scope.testObject.country = undefined;
        });
        c_expect(inp).to.have.class('ng-invalid');
    });

    it('should find countries by ID', function() {
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('US');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(1);
        var optionLink = options.eq(0).find('a');
        c_expect(optionLink.text()).to.equal('United States of America');
        optionLink.click();
        c_expect(scope.testObject.country).eql({id: "USA", name: "United States of America", dialingCode: "001"});
    });

    it('should find countries by name', function() {
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('United States');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(1);
        var optionLink = options.eq(0).find('a');
        c_expect(optionLink.text()).to.equal('United States of America');
        optionLink.click();
        c_expect(scope.testObject.country).eql({id: "USA", name: "United States of America", dialingCode: "001"});
    });

    it('should not find not existing country', function() {
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('not_existing_country_name');
        c_expect(dropDiv).not.to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options).to.not.exist;
        c_expect(inp).to.have.class('ng-invalid');
    });

    it('should be disabled depends on attribute', function() {
        var inp = elm.find('input');
        c_expect(inp.length).to.equal(1);
        c_expect(inp).not.to.be.disabled;
        scope.$apply(function() {
            scope.testObject.disabled = true;
        });
        c_expect(inp).to.be.disabled;
    });

    it('should find limited options', function() {
        scope.$apply(function() {
            scope.testObject.count = 3;
        });
        spyOn(mockService, 'searchCountries').and.callThrough();
        var dropDiv = elm.find('div.dropdown');
        var inp = elm.find('input');
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        //set new value in the input
        input(inp).enter('U');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li');
        c_expect(options.length).to.equal(3);
        //check thta service was called correctly
        c_expect(mockService.searchCountries.calls.count()).to.equal(1);
        c_expect(mockService.searchCountries.calls.mostRecent().args).to.eql(['U', '3']);
    });
});