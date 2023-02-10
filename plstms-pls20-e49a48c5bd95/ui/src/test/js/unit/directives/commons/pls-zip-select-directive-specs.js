/**
 * Tests zip select directive.
 *
 * @author Sergey Kirichenko
 */
describe('PLS Zip Select (pls-zip-select-directive) Directive Test.', function() {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;
    var timeoutService;
    //zip filter
    var filter;
    //controls
    var zipInp, zipDropDiv, zipLabel, countryInp, countryDropDiv, countryLabel, zipResult;

    var postalCodes = [
        {zip:"60605",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"IL",city:"CHICAGO",timeZone:null},
        {zip:"10921",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"NY",city:"FLORIDA",timeZone:null},
        {zip:"90001",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"CA",city:"LOS ANGELES",timeZone:null},
        {zip:"10101",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"NY",city:"NEW YORK",timeZone:null},
        {zip:"94101",country:{id:"USA",name:"United States of America",dialingCode:"001"},state:"CA",city:"SAN FRANCISCO",timeZone:null},
        {zip:"01001",country:{id:"UKR",name:"Ukraine",dialingCode:"380"},state:"",city:"Kiev",timeZone:null}
    ];

    var mockZipService = {
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
    var countries = [
        {id: "UKR", name: "Ukraine", dialingCode: "380"},
        {id: "GBR", name: "United Kingdom", dialingCode: "044"},
        {id: "USA", name: "United States of America", dialingCode: "001"}
    ];

    var mockCountryService = {
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

    beforeEach(module('plsApp', 'pages/tpl/pls-zip-select-specific-tpl.html', 'pages/tpl/pls-zip-select-default-tpl.html',
            'pages/tpl/pls-typeahead-tpl.html', 'pages/tpl/pls-zip-search-tpl.html', 'pages/tpl/pls-country-search-tpl.html', function($provide) {
                $provide.factory('ZipService', function() {
                    return mockZipService;
                });
                $provide.factory('CountryService', function() {
                    return mockCountryService;
                });
            }));
    beforeEach(inject(function($timeout) {
        timeoutService = $timeout;
    }));

    var checkZipValidity = function() {
        c_expect(zipInp).to.have.class('ng-invalid');
        c_expect(countryInp).to.have.value('USA');
        scope.$apply(function() {
            scope.testObject.zip = postalCodes[0];
        });
        c_expect(zipInp).not.to.have.class('ng-invalid');
        c_expect(zipInp).to.have.value(postalCodes[0].zip);
        c_expect(zipResult).to.contain(filter(postalCodes[0]));
    };

    var checkZipShowingOptionAndSelection = function() {
        c_expect(zipDropDiv).not.to.have.class('open');
        c_expect(scope.testObject.zip).not.to.be.defined;
        input(zipInp).enter('0');
        timeoutService.flush();
        c_expect(zipDropDiv).to.have.class('open');
        var options = zipDropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(5);
        c_expect(options.eq(0)).to.contain(filter(postalCodes[0]));
        options.eq(0).click();
        c_expect(zipDropDiv).not.to.have.class('open');
        c_expect(zipInp).to.have.value(postalCodes[0].zip);
        c_expect(zipResult).to.contain(filter(postalCodes[0]));
        c_expect(scope.testObject.zip).eql(postalCodes[0]);
    };

    var checkZipAutoSelect = function() {
        c_expect(zipDropDiv).not.to.have.class('open');
        c_expect(scope.testObject.zip).not.to.be.defined;
        input(zipInp).enter(postalCodes[0].zip);
        c_expect(zipDropDiv).not.to.have.class('open');
        c_expect(zipInp).to.have.value(postalCodes[0].zip);
        timeoutService.flush();
        c_expect(zipResult).to.contain(filter(postalCodes[0]));
        c_expect(scope.testObject.zip).eql(postalCodes[0]);
    };

    var checkSelectZipFromOtherCountry = function() {
        c_expect(countryDropDiv).not.to.have.class('open');
        c_expect(countryInp).to.have.value('USA');
        input(countryInp).enter('U');
        timeoutService.flush();
        c_expect(countryDropDiv).to.have.class('open');
        var options = countryDropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(3);
        c_expect(options.eq(0)).to.contain(countries[0].name);
        options.eq(0).click();
        c_expect(countryDropDiv).not.to.have.class('open');
        c_expect(countryInp).to.have.value('UKR');
        c_expect(zipDropDiv).not.to.have.class('open');
        c_expect(scope.testObject.zip).not.to.be.defined;
    };

    var checkResetZipByCountrySelection = function() {
        scope.$apply(function() {
            scope.testObject.zip = postalCodes[0];
        });
        c_expect(zipInp).to.have.value(postalCodes[0].zip);
        c_expect(zipResult).to.contain(filter(postalCodes[0]));
        c_expect(countryDropDiv).not.to.have.class('open');
        c_expect(countryInp).to.have.value('USA');
        c_expect(scope.testObject.zip).to.be.defined;
        input(countryInp).enter('UKR');
        c_expect(countryDropDiv).not.to.have.class('open');
        c_expect(zipInp).not.to.have.value();
        c_expect(zipResult).not.to.have.text();
        c_expect(scope.testObject.zip).not.to.be.defined;
    };

    var checkFindZipFromOtherCountry = function() {
        c_expect(countryDropDiv).not.to.have.class('open');
        c_expect(countryInp).to.have.value('USA');
        input(countryInp).enter('UKR');
        c_expect(countryDropDiv).not.to.have.class('open');
        c_expect(zipDropDiv).not.to.have.class('open');
        c_expect(scope.testObject.zip).not.to.be.defined;
        input(zipInp).enter(postalCodes[0].zip);
        c_expect(zipDropDiv).not.to.have.class('open');
        c_expect(scope.testObject.zip).not.to.be.defined;
    };

    var checkFindZipByCity = function() {
        input(zipInp).enter(postalCodes[0].city);
        timeoutService.flush();
        c_expect(zipDropDiv).to.have.class('open');
        var options = zipDropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(1);
        c_expect(options).to.contain(postalCodes[0].city);
        options.click();
        c_expect(zipDropDiv).not.to.have.class('open');
        c_expect(zipInp).to.have.value(postalCodes[0].zip);
        c_expect(zipResult).to.contain(filter(postalCodes[0]));
        c_expect(scope.testObject.zip).to.eql(postalCodes[0]);
    };

    var checkFindZipByState = function() {
        input(zipInp).enter(postalCodes[0].state);
        timeoutService.flush();
        c_expect(zipDropDiv).to.have.class('open');
        var options = zipDropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(1);
        c_expect(options).to.contain(postalCodes[0].state);
        options.click();
        c_expect(zipDropDiv).not.to.have.class('open');
        c_expect(zipInp).to.have.value(postalCodes[0].zip);
        c_expect(zipResult).to.contain(filter(postalCodes[0]));
        c_expect(scope.testObject.zip).to.eql(postalCodes[0]);
    };

    var checkZipLabel = function() {
        scope.$apply(function() {
            scope.testObject.zipLabel = 'NEW_ZIP_LABEL';
        });
        c_expect(zipLabel).to.have.text('NEW_ZIP_LABEL');
        scope.$apply(function() {
            scope.testObject.zipLabel = undefined;
        });
        c_expect(zipLabel).to.have.text('ZIP:');
    };

    var checkCountryLabel = function() {
        scope.$apply(function() {
            scope.testObject.countryLabel = 'NEW_COUNTRY_LABEL';
        });
        c_expect(countryLabel).to.have.text('NEW_COUNTRY_LABEL');
        scope.$apply(function() {
            scope.testObject.countryLabel = undefined;
        });
        c_expect(countryLabel).to.have.text('Country:');
    };

    var checkZipTabIndex = function() {
        scope.$apply(function() {
            scope.testObject.zipTabIndex = 1;
        });
        scope.$apply(function() {
            scope.testObject.zipTabIndex = undefined;
        });
        c_expect(countryLabel).not.to.have.attr('tabindex');
    };

    var checkZipServiceCall = function() {
        spyOn(mockZipService, 'findZip').and.callThrough();
        input(zipInp).enter('101');
        timeoutService.flush();
        c_expect(zipDropDiv).to.have.class('open');
        var options = zipDropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(2);
        c_expect(options.eq(0)).to.contain('101');
        c_expect(options.eq(1)).to.contain('101');
        c_expect(mockZipService.findZip.calls.count()).to.equal(1);
        c_expect(mockZipService.findZip.calls.mostRecent().args).to.eql(['101', 'USA', undefined]);
    };

    var checkCountryServiceCall = function() {
        spyOn(mockCountryService, 'searchCountries').and.callThrough();
        c_expect(countryDropDiv).not.to.have.class('open');
        //set new value in the input
        input(countryInp).enter('U');
        timeoutService.flush();
        c_expect(countryDropDiv).to.have.class('open');
        var options = countryDropDiv.find('ul.typeahead li a');
        c_expect(options.length).to.equal(3);
        c_expect(options.eq(0)).to.contain('U');
        c_expect(options.eq(1)).to.contain('U');
        c_expect(options.eq(2)).to.contain('U');
        //check that service was called correctly
        c_expect(mockCountryService.searchCountries.calls.count()).to.equal(1);
        c_expect(mockCountryService.searchCountries.calls.mostRecent().args).to.eql(['U', undefined]);
    };

    describe('Check default layout.', function() {

        beforeEach(inject(function($rootScope, $compile, zipFilter) {
            filter = zipFilter;
            elm = angular.element('<div><input data-pls-zip-select="testObject.zip" '
                + '                 data-zip-label="{{testObject.zipLabel}}" data-zip-tab-index="{{testObject.zipTabIndex}}" '
                + '                 data-country-label="{{testObject.countryLabel}}" data-center-align="{{testObject.centerAlign}}"></div>');
            scope = $rootScope.$new();
            scope.$apply(function() {
                scope.testObject = {
                    zip: undefined,
                    zipLabel: undefined,
                    zipTabIndex: undefined,
                    countryLabel: undefined,
                    centerAlign: undefined
                };
            });
            $compile(elm)(scope);
            scope.$digest();
            zipInp = elm.find('[id^="zipInp"]');
            zipDropDiv = zipInp.next();
            zipLabel = elm.find('label[for^="zipInp"]');
            countryInp = elm.find('[id^="countryInp"]');
            countryDropDiv = countryInp.next();
            countryLabel = elm.find('label[for^="countryInp"]');
            zipResult = elm.find('label.span12.text-success.control-label')
        }));

        it('should have default behaviour', function() {
            //check whether or not default layout is chosen
            c_expect(elm).to.have('div.control-group');
            c_expect(elm.find('div.control-group').length).to.equal(3);
            //check controls presence
            c_expect(zipInp.length).to.equal(1);
            c_expect(zipDropDiv.length).to.equal(1);
            c_expect(countryInp.length).to.equal(1);
            c_expect(countryDropDiv.length).to.equal(1);
            c_expect(zipResult.length).to.equal(1);
            c_expect(zipInp).to.have.class('ng-invalid');
            c_expect(zipLabel).to.have.text('ZIP:');
            c_expect(countryInp).to.have.value('USA');
            c_expect(countryLabel).to.have.text('Country:');
            c_expect(zipResult).not.to.have.text();
        });

        it('should be invalid without model and valid with model', function() {
            checkZipValidity();
        });

        it('should show options and select one option', function() {
            checkZipShowingOptionAndSelection();
        });

        it('should auto-select zip', function() {
            checkZipAutoSelect();
        });

        it('should select zip from other country', function() {
            checkSelectZipFromOtherCountry();
        });

        it('should reset zip when country is changed', function() {
            checkResetZipByCountrySelection();
        });

        it('should not find zip from other country', function() {
            checkFindZipFromOtherCountry();
        });

        it('should find zip by city', function() {
            checkFindZipByCity();
        });

        it('should find zip by state', function() {
            checkFindZipByState();
        });

        it('should apply zip label', function() {
            checkZipLabel();
        });

        it('should apply country label', function() {
            checkCountryLabel();
        });

        it('should apply tabindex for zip input', function() {
            checkZipTabIndex();
        });

        it('should call zip service with all parameters', function() {
            checkZipServiceCall();
        });

        it('should call country service with all parameters', function() {
            checkCountryServiceCall();
        });
    });

    describe('Check center aligned layout.', function() {

        beforeEach(inject(function($rootScope, $compile, zipFilter) {
            filter = zipFilter;
            elm = angular.element('<div><input data-pls-zip-select="testObject.zip" '
                + '                 data-zip-label="{{testObject.zipLabel}}" data-zip-tab-index="{{testObject.zipTabIndex}}" '
                + '                 data-country-label="{{testObject.countryLabel}}" data-center-align="{{testObject.centerAlign}}"></div>');
            scope = $rootScope.$new();
            scope.$apply(function() {
                scope.testObject = {
                    zip: undefined,
                    zipLabel: undefined,
                    zipTabIndex: undefined,
                    countryLabel: undefined,
                    centerAlign: true
                };
            });
            $compile(elm)(scope);
            scope.$digest();
            zipInp = elm.find('input[data-ng-model="plsZipSearch"]');
            zipDropDiv = zipInp.next();
            zipLabel = elm.find('div.span4.text-right strong').eq(0);
            countryInp = elm.find('input[data-pls-country-search="selectedCountry"]');
            countryDropDiv = countryInp.next();
            countryLabel = elm.find('div.span4.text-right strong').eq(1);
            zipResult = elm.find('div.span8.offset4.alert-success');
        }));

        it('should have default behavior', function() {
            //check whether or not default layout is chosen
            c_expect(elm).not.to.have('div.control-group');
            c_expect(elm).to.have('div.row-fluid');
            c_expect(elm.find('div.row-fluid').length).to.equal(3);
            //check controls presence
            c_expect(zipInp.length).to.equal(1);
            c_expect(zipDropDiv.length).to.equal(1);
            c_expect(countryInp.length).to.equal(1);
            c_expect(countryDropDiv.length).to.equal(1);
            c_expect(zipResult.length).to.equal(1);
            c_expect(zipInp).to.have.class('ng-invalid');
            c_expect(zipLabel).to.have.text('ZIP:');
            c_expect(countryInp).to.have.value('USA');
            c_expect(countryLabel).to.have.text('Country:');
            c_expect(zipResult).not.to.have.text();
        });

        it('should be invalid without model and valid with model', function() {
            checkZipValidity();
        });

        it('should show options and select one option', function() {
            checkZipShowingOptionAndSelection();
        });

        it('should auto-select zip', function() {
            checkZipAutoSelect();
        });

        it('should select zip from other country', function() {
            checkSelectZipFromOtherCountry();
        });

        it('should reset zip when country is changed', function() {
            checkResetZipByCountrySelection();
        });

        it('should not find zip from other country', function() {
            checkFindZipFromOtherCountry();
        });

        it('should find zip by city', function() {
            checkFindZipByCity();
        });

        it('should find zip by state', function() {
            checkFindZipByState();
        });

        it('should apply zip label', function() {
            checkZipLabel();
        });

        it('should apply country label', function() {
            checkCountryLabel();
        });

        it('should apply tabindex for zip input', function() {
            checkZipTabIndex();
        });

        it('should call zip service with all parameters', function() {
            checkZipServiceCall();
        });

        it('should call country service with all parameters', function() {
            checkCountryServiceCall();
        });
    });

    describe('Check initialization with country.', function() {

        beforeEach(inject(function($rootScope, $compile, zipFilter) {
            filter = zipFilter;
            elm = angular.element('<div><input data-pls-zip-select="testObject.zip" '
                + '                 data-zip-label="{{testObject.zipLabel}}" data-zip-tab-index="{{testObject.zipTabIndex}}" '
                + '                 data-country-label="{{testObject.countryLabel}}" data-center-align="{{testObject.centerAlign}}"></div>');
            scope = $rootScope.$new();
            scope.$apply(function() {
                scope.testObject = {
                    zip: angular.copy(postalCodes[5])
                };
            });
            $compile(elm)(scope);
            scope.$digest();
            zipInp = elm.find('[id^="zipInp"]');
            zipDropDiv = zipInp.next();
            zipLabel = elm.find('label[for^="zipInp"]');
            countryInp = elm.find('[id^="countryInp"]');
            countryDropDiv = countryInp.next();
            countryLabel = elm.find('label[for^="countryInp"]');
            zipResult = elm.find('label.span12.text-success.control-label')
        }));

        it('should check country initialization', function() {
            c_expect(countryDropDiv).not.to.have.class('open');
            c_expect(countryInp).to.have.value('UKR');
            c_expect(zipDropDiv).not.to.have.class('open');
            c_expect(zipInp).to.have.value(postalCodes[5].zip);
            c_expect(zipResult).to.contain(filter(postalCodes[5]));
            c_expect(scope.testObject.zip).eql(postalCodes[5]);
        });
    });
});