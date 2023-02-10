/**
 * This unit test checks address list directive.
 *
 * @author Alexander Kirichenko
 */
describe('PLS Typeahead (pls-typeahead) Directive Test.', function() {

    beforeEach(module('plsApp', 'pages/tpl/pls-typeahead-tpl.html'));

    describe('Simple scenarios with array as a source', function() {

        //directive element
        var elm = undefined;

        //angular scope
        var scope = undefined;

        var timeoutService;

        beforeEach(inject(function($rootScope, $compile, $timeout) {
            elm = angular.element('<div><input id="typeaheadInp" data-pls-typeahead="obj as obj.label for obj in objs" ' +
                    'data-ng-model="selObj"></div>');
            scope = $rootScope;
            timeoutService = $timeout;
            scope.$apply(function() {
                scope.objs = [
                    { value : 1, label : 'object 1' },
                    { value : 2, label : 'object 2' },
                    { value : 3, label : 'test 1' },
                    { value : 4, label : 'test 2' },
                    { value : 5, label : 'loreipsum 1' },
                    { value : 6, label : 'loreipsum 2' }
                ];
            });
            $compile(elm)(scope);
            scope.$digest();
        }));

        afterEach(function() {
            scope.$apply(function() {
                scope.selObj = undefined;
            });
        });

        it('should display selected object within input ', function() {
            scope.$apply(function() {
                scope.selObj = { value : 1, label : 'object 1' };
            });
            var input = elm.find('input#typeaheadInp');
            c_expect(input).to.exist;
            var visibleValue = input.val();
            c_expect(visibleValue).not.to.be.empty;
            c_expect(visibleValue).to.be.equal('object 1');
        });

        it('should drop selected object', function() {
            scope.$apply(function() {
                scope.selObj = { value : 1, label : 'object 1' };
            });
            c_expect(scope.selObj).to.exist;
            var inputElem = elm.find('input#typeaheadInp');
            c_expect(inputElem).to.exist;
            input(inputElem).enter('');
            c_expect(scope.selObj).to.not.exist;
            c_expect(inputElem.val()).to.be.empty;
        });

        it('should display drop-down list with all elements', function() {
            var dropDiv = elm.find('div.dropdown');
            c_expect(dropDiv).not.to.have.class('open');
            c_expect(scope.selObj).to.not.exist;
            input(elm.find('#typeaheadInp')).enter('e');
            timeoutService.flush();
            c_expect(dropDiv).to.have.class('open');
            var options = elm.find('ul.dropdown-menu > li');
            c_expect(options.length).to.be.equal(6);
        });

        it('should display select one of the items', function() {
            var dropDiv = elm.find('div.dropdown');
            c_expect(dropDiv).not.to.have.class('open');
            c_expect(scope.selObj).to.not.exist;
            input(elm.find('#typeaheadInp')).enter('loreipsum');
            timeoutService.flush();
            c_expect(dropDiv).to.have.class('open');
            var options = elm.find('ul.dropdown-menu > li');
            options.eq(4).find('a').click();
            c_expect(dropDiv).not.to.have.class('open');
            c_expect(scope.selObj).to.exist;
            c_expect(scope.selObj).to.deep.equal(scope.objs[4]);
        });

        it('should display and hide drop-down by calling showTypeaheadList function twice', function() {
            var dropDiv = elm.find('div.dropdown');
            c_expect(dropDiv).not.to.have.class('open');
            c_expect(scope.selObj).to.not.exist;
            c_expect(scope.showTypeaheadList).to.be.a('function');
            scope.$apply(scope.showTypeaheadList);
            c_expect(dropDiv).to.have.class('open');
            var options = elm.find('ul.dropdown-menu > li');
            c_expect(options.length).to.be.equal(6);
            scope.$apply(scope.showTypeaheadList);
            c_expect(dropDiv).not.to.have.class('open');
        });
    });

    describe('Simple scenarios with function as a source', function() {

        //directive element
        var elm = undefined;

        //angular scope
        var scope = undefined;

        var timeoutService;

        beforeEach(inject(function($rootScope, $compile, $timeout) {
            elm = angular.element('<div><input id="typeaheadInp" data-pls-typeahead="obj as obj.label for obj in findMatches($viewValue)" ' +
                    'data-ng-model="selObj"></div>');
            scope = $rootScope;
            timeoutService = $timeout;
            scope.$apply(function() {
                scope.objs = [
                    { value : 1, label : 'object 1' },
                    { value : 2, label : 'object 2' },
                    { value : 3, label : 'test 1' },
                    { value : 4, label : 'test 2' },
                    { value : 5, label : 'loreipsum 1' },
                    { value : 6, label : 'loreipsum 2' }
                ];
                scope.findMatches = function(filteredText) {
                    return _.filter(scope.objs, function(item) {
                        return item.label.indexOf(filteredText) !== -1;
                    });
                };
            });
            $compile(elm)(scope);
            scope.$digest();
        }));

        afterEach(function() {
            scope.$apply(function() {
                scope.selObj = undefined;
            });
        });

        it('should display drop-down list with filtered elements', function() {
            c_expect(scope.selObj).to.not.exist;
            input(elm.find('#typeaheadInp')).enter('test');
            timeoutService.flush();
            var options = elm.find('ul.dropdown-menu > li');
            c_expect(options.length).to.be.equal(2);
        });

        it('should select one of the items', function() {
            c_expect(scope.selObj).to.not.exist;
            input(elm.find('#typeaheadInp')).enter('loreipsum');
            timeoutService.flush();
            var options = elm.find('ul.dropdown-menu > li');
            options.eq(0).find('a').click();
            c_expect(scope.selObj).to.exist;
            c_expect(scope.selObj).to.deep.equal(scope.objs[4]);
        });
    });

    describe('Scenarios to test auto-selection', function() {

        //directive element
        var elm = undefined;

        //angular scope
        var scope = undefined;

        var timeoutService;

        beforeEach(inject(function($rootScope, $compile, $timeout) {
            elm = angular.element('<div><input id="typeaheadInp" data-pls-typeahead="obj as obj.label for obj in findMatches($viewValue)" ' +
                    'data-ng-model="selObj" data-typeahead-auto-select="true"></div>');
            scope = $rootScope;
            timeoutService = $timeout;
            scope.$apply(function() {
                scope.objs = [
                    { value : 1, label : 'object 1' },
                    { value : 2, label : 'object 2' },
                    { value : 3, label : 'test 1' },
                    { value : 4, label : 'test 2' },
                    { value : 4, label : 'test value' },
                    { value : 5, label : 'loreipsum 1' },
                    { value : 6, label : 'loreipsum 2' }
                ];
                scope.findMatches = function(filteredText) {
                    return _.filter(scope.objs, function(item) {
                        return item.label.indexOf(filteredText) !== -1;
                    });
                };
            });
            $compile(elm)(scope);
            scope.$digest();
        }));

        afterEach(function() {
            scope.$apply(function() {
                scope.selObj = undefined;
            });
        });

        it('should select automatically the only item available', function() {
            var dropDiv = elm.find('div.dropdown');
            c_expect(dropDiv).not.to.have.class('open');
            c_expect(scope.selObj).to.not.exist;
            input(elm.find('#typeaheadInp')).enter('loreipsum 2');
            timeoutService.flush();
            c_expect(dropDiv).not.to.have.class('open');
            c_expect(scope.selObj).to.exist;
            c_expect(scope.selObj).to.deep.equal(scope.objs[6]);
        });

        it('shouldn\'t select automatically the only item available', function() {
            var dropDiv = elm.find('div.dropdown');
            c_expect(dropDiv).not.to.have.class('open');
            c_expect(scope.selObj).to.not.exist;
            input(elm.find('#typeaheadInp')).enter('test va');
            timeoutService.flush();
            c_expect(dropDiv).to.have.class('open');
            c_expect(scope.selObj).to.not.exist;
            var options = elm.find('ul.dropdown-menu > li');
            c_expect(options.length).to.be.equal(1);
            c_expect(options.eq(0).find('a').text()).to.be.equal('test value');
        });
    });
});
