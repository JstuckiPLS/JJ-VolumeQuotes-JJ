/**
 * PLS copy exact properties factory specs
 *
 * @author Vitaliy Gavriliuk
 */

describe('Copy Exact Properties Service -', function () {
  beforeEach(module('plsApp'));

  var objFrom = {foo: 123, bar: '123', baz: false};
  var objTo = {foo: null, test: 'test'};

  afterEach(function () {
    objFrom = {foo: 123, bar: '123', baz: false};
    objTo = {foo: null, test: 'test'};
  });

  it('should replace "foo" property', inject(function (copyExactProperties) {
    c_expect(angular.equals(copyExactProperties(objFrom, ['foo'], objTo), {foo: 123, test: 'test'})).to.equal(true);
  }));

  it('should add "baz" property', inject(function (copyExactProperties) {
    c_expect(angular.equals(copyExactProperties(objFrom, ['baz'], objTo), {foo: null, test: 'test', baz: false})).to.equal(true);
  }));

  it('should add no ony property', inject(function (copyExactProperties) {
    c_expect(angular.equals(copyExactProperties(objFrom, [], objTo), objTo)).to.equal(true);
    c_expect(angular.equals(copyExactProperties(objFrom, ['test'], objTo), objTo)).to.equal(true);
  }));

  it('should add all properties', inject(function (copyExactProperties) {
    c_expect(angular.equals(copyExactProperties(objFrom, ['foo', 'bar', 'baz'], objTo), {foo: 123, bar: '123', test: 'test', baz: false})).to.equal(true);
  }));
});