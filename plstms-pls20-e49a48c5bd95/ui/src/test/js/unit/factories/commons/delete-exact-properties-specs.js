/**
 * PLS delete exact properties factory specs
 *
 * @author Vitaliy Gavriliuk
 */

describe('Delete Exact Properties Service -', function () {
  beforeEach(module('plsApp'));

  var obj = {foo: 123, bar: '123', baz: false};

  afterEach(function () {
    obj = {foo: 123, bar: '123', baz: false};
  });

  it('should remove only "foo" property', inject(function (deleteExactProperties) {
    c_expect(angular.equals(deleteExactProperties(obj, ['foo']), {bar: '123', baz: false})).to.equal(true);
  }));

  it('should remove only "bar" property', inject(function (deleteExactProperties) {
    c_expect(angular.equals(deleteExactProperties(obj, ['bar']), {foo: 123, baz: false})).to.equal(true);
  }));

  it('should remove only "baz" property', inject(function (deleteExactProperties) {
    c_expect(angular.equals(deleteExactProperties(obj, ['baz']), {foo: 123, bar: '123'})).to.equal(true);
  }));

  it('should remain only "baz" property', inject(function (deleteExactProperties) {
    c_expect(angular.equals(deleteExactProperties(obj, ['foo', 'bar']), {baz: false})).to.equal(true);
  }));

  it('should remain the same object', inject(function (deleteExactProperties) {
    c_expect(angular.equals(deleteExactProperties(obj, []), obj)).to.equal(true);
    c_expect(angular.equals(deleteExactProperties(obj, ['']), obj)).to.equal(true);
    c_expect(angular.equals(deleteExactProperties(obj, ['test']), obj)).to.equal(true);
  }));

  it('should be EMPTY object', inject(function (deleteExactProperties) {
    c_expect(angular.equals(deleteExactProperties(obj, ['foo', 'bar', 'baz']), {})).to.equal(true);
  }));

  it('should be changed initial object', inject(function (deleteExactProperties) {
    c_expect(angular.equals(deleteExactProperties(obj, ['foo', 'bar']), {foo: 123, bar: '123', baz: false})).to.equal(false);
  }));
});