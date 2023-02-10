/**
 * Tests zip search directive.
 * Unfortunately there is no way to test special functionality related to focus and blur of input fields.
 *
 * @author Sergey Kirichenko
 */
describe('PLS Zip Search (plsZipSearch) Directive Test.', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;
    var timeoutService;
    var inp, dropDiv;

    var postalCodes = [
        {zip:"60605",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"IL",city:"CHICAGO",timeZone:null},
        {zip:"10921",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"NY",city:"FLORIDA",timeZone:null},
        {zip:"90001",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"CA",city:"LOS ANGELES",timeZone:null},
        {zip:"10101",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"NY",city:"NEW YORK",timeZone:null},
        {zip:"94101",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"CA",city:"SAN FRANCISCO",timeZone:null},
        {zip:"01001",country:{id:"UKR",name:"Ukraine",dialingCode:"380"},state:"",city:"Kiev",timeZone:null},
        {zip:"12345",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"CA",city:"SAN FRANCISCO", prefCity:'test',timeZone:null},
        {zip:"12345",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"CA",city:"test", prefCity:'test',timeZone:null},
        {zip:"12345",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"CA",city:"test", prefCity:'test',timeZone:null}
    ];

    var postalCodesBuilder = function(index) {
        return index >= 0 ? (postalCodes[index].city + ', ' + postalCodes[index].state + ', '  + postalCodes[index].zip) : '';
    };

    var mockService = {
        findZip : function(criteria, country, count) {
            var result = _.filter(postalCodes, function(zip) {
                return (!criteria || zip.zip.indexOf(criteria) != -1 || zip.city.indexOf(criteria) != -1 || zip.state.indexOf(criteria) != -1)
                    && (!country || zip.country.id == country);
            });
            if (count && result.length > count) {
                result = result.slice(0, count);
            }
            return  result;
        }
    };

    beforeEach(module('plsApp', 'pages/tpl/pls-typeahead-tpl.html', 'pages/tpl/pls-zip-search-tpl.html', function($provide) {
        $provide.factory('ZipService', function() {
            return mockService;
        });
    }));

    beforeEach(inject(function($rootScope, $compile, $timeout) {
        elm = angular.element('<div><input data-pls-zip-search="testObject.zip" data-country="testObject.country" data-count="testObject.count" '
                + '                 data-input-label-filter="zip" data-zip-disabled="testObject.disabled" required></div>');
        scope = $rootScope.$new();
        timeoutService = $timeout;
        scope.$apply(function() {
            scope.testObject = {
                zip: undefined,
                country: 'USA',
                count: undefined,
                inputLabelFilter: undefined,
                disabled: undefined
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
            scope.testObject.zip = postalCodes[0];
        });
        c_expect(inp).not.to.have.class('ng-invalid');
        c_expect(inp).to.have.value(postalCodesBuilder(0));
    });

    it('should be invalid with partially input data but not selected', function() {
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        input(inp).enter('101');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        c_expect(inp).to.have.class('ng-invalid');
    });

    it('should show options and select one option', function() {
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        c_expect(dropDiv).not.to.have.class('open');
        input(inp).enter('101');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(2);
        c_expect(options.eq(0)).to.contain('101');
        c_expect(options.eq(1)).to.contain('101');
        options.eq(0).click();
        c_expect(dropDiv).not.to.have.class('open');
        c_expect(inp).to.have.value(postalCodesBuilder(3));
        c_expect(scope.testObject.zip).to.eql(postalCodes[3]);
    });

    it('should auto-select single variant', function() {
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        input(inp).enter(postalCodes[0].zip);
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li a');
        options.eq(0).click();
        c_expect(dropDiv).not.to.have.class('open');
        c_expect(inp).to.have.value(postalCodesBuilder(0));
        c_expect(scope.testObject.zip).to.eql(postalCodes[0]);
    });

    it('should auto-select preferred city when more than one option available', function() {
        c_expect(dropDiv.length).to.equal(1);
        c_expect(inp.length).to.equal(1);
        input(inp).enter(postalCodes[6].zip);
        timeoutService.flush();
        c_expect(dropDiv).not.to.have.class('open');
        c_expect(inp).to.have.value(postalCodesBuilder(7));
        c_expect(scope.testObject.zip).to.eql(postalCodes[7]);
    });

    it('should find by city', function() {
        c_expect(inp.length).to.equal(1);
        input(inp).enter(postalCodes[0].city);
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(1);
        c_expect(options).to.contain(postalCodes[0].city);
        options.click();
        c_expect(dropDiv).not.to.have.class('open');
        c_expect(inp).to.have.value(postalCodesBuilder(0));
        c_expect(scope.testObject.zip).to.eql(postalCodes[0]);
    });

    it('should find by state', function() {
        c_expect(inp.length).to.equal(1);
        input(inp).enter(postalCodes[0].state);
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(1);
        c_expect(options).to.contain(postalCodes[0].state);
        options.click();
        c_expect(dropDiv).not.to.have.class('open');
        c_expect(inp).to.have.value(postalCodesBuilder(0));
        c_expect(scope.testObject.zip).to.eql(postalCodes[0]);
    });

    it('should find by other country', function() {
        scope.$apply(function() {
            scope.testObject.country = 'UKR';
        });
        c_expect(inp.length).to.equal(1);
        input(inp).enter(postalCodes[5].zip);
        c_expect(inp).to.have.value(postalCodes[5].zip);
    });

    it('should not find zip from other country', function() {
        scope.$apply(function() {
            scope.testObject.country = 'UKR';
        });
        c_expect(inp.length).to.equal(1);
        input(inp).enter(postalCodes[0].zip);
        c_expect(dropDiv).not.to.have.class('open');
        c_expect(inp).not.to.have.value();
        c_expect(scope.testObject.zip).not.to.be.defined;
    });

    it('should find limited count', function() {
        scope.$apply(function() {
            scope.testObject.count = 3;
        });
        c_expect(inp.length).to.equal(1);
        input(inp).enter('1');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(3);
        c_expect(options.eq(0)).to.contain('1');
        c_expect(options.eq(1)).to.contain('1');
        c_expect(options.eq(2)).to.contain('1');
    });

    it('should apply label function', function() {
        c_expect(inp.length).to.equal(1);
        input(inp).enter(postalCodes[0].city);
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(1);
        c_expect(options).to.contain(postalCodes[0].zip);
        options.click();
        c_expect(dropDiv).not.to.have.class('open');
        c_expect(inp).to.have.value(postalCodesBuilder(0));
    });

    it('should disable input', function() {
        scope.$apply(function() {
            scope.testObject.disabled = true;
        });
        c_expect(inp).to.be.disabled;
        scope.$apply(function() {
            scope.testObject.disabled = false;
        });
        c_expect(inp).not.to.be.disabled;
    });

    it('should call service with parameters', function() {
        spyOn(mockService, 'findZip').and.callThrough();
        scope.$apply(function() {
            scope.testObject.count = 3;
        });
        c_expect(inp.length).to.equal(1);
        input(inp).enter('101');
        timeoutService.flush();
        c_expect(dropDiv).to.have.class('open');
        var options = dropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(2);
        c_expect(options.eq(0)).to.contain('101');
        c_expect(options.eq(1)).to.contain('101');
        c_expect(mockService.findZip.calls.count()).to.equal(1);
        c_expect(mockService.findZip.calls.mostRecent().args).to.eql(['101', 'USA', 3]);
    });
});