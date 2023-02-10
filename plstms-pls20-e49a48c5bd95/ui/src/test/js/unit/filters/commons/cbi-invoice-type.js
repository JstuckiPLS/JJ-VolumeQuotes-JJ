/**
 * CBI Invoice Type filter specs
 *
 * @author Vitaliy Gavrilyuk
 */
describe('Custom Filters Test -', function () {

  beforeEach(module('plsApp'));

  it('should filter CBI Invoice "PLS" value to be - "Invoice in PLS 2.0"', inject(function (cbiInvoiceTypeFilter) {
    c_expect(cbiInvoiceTypeFilter('PLS')).to.equal('Invoice in PLS 2.0');
  }));

  it('should filter CBI Invoice "FIN" value to be - "Invoice in Financials"', inject(function (cbiInvoiceTypeFilter) {
    c_expect(cbiInvoiceTypeFilter('FIN')).to.equal('Invoice in Financials');
  }));

  it('should filter CBI Invoice "" value to be - Undefined', inject(function (cbiInvoiceTypeFilter) {
    c_expect(cbiInvoiceTypeFilter('')).to.be.undefined();
  }));

  it('should filter CBI Invoice "test" value to be - Undefined', inject(function (cbiInvoiceTypeFilter) {
    c_expect(cbiInvoiceTypeFilter('test')).to.be.undefined();
  }));

  it('should filter CBI Invoice EMPTY value to be - Undefined', inject(function (cbiInvoiceTypeFilter) {
    c_expect(cbiInvoiceTypeFilter()).to.be.undefined();
  }));

  it('should filter CBI Invoice INTEGER value to be - Undefined', inject(function (cbiInvoiceTypeFilter) {
    c_expect(cbiInvoiceTypeFilter(123)).to.be.undefined();
  }));

  it('should filter CBI Invoice NULL value to be - Undefined', inject(function (cbiInvoiceTypeFilter) {
    c_expect(cbiInvoiceTypeFilter(null)).to.be.undefined();
  }));

  it('should filter CBI Invoice FALSE value to be - Undefined', inject(function (cbiInvoiceTypeFilter) {
    c_expect(cbiInvoiceTypeFilter(false)).to.be.undefined();
  }));

  it('should filter CBI Invoice TRUE value to be - Undefined', inject(function (cbiInvoiceTypeFilter) {
    c_expect(cbiInvoiceTypeFilter(true)).to.be.undefined();
  }));

  it('should filter CBI Invoice UNDEFINED value to be - Undefined', inject(function (cbiInvoiceTypeFilter) {
    c_expect(cbiInvoiceTypeFilter(undefined)).to.be.undefined();
  }));
});