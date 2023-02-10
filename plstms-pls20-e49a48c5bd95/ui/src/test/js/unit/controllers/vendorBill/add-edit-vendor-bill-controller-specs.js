/**
 * Tests AddVendorBillController controller.
 * @author Alexander Kirichenko
 */
describe('AddVendorBillController controller test.', function() {
    // angular scope
    var scope = undefined;

    //AddVendorBillController controller
    var controller = undefined;

    var promises = {}, defers = {};
    var mockVendorBill = {
        id: 1,
        carrier: {
            id: 1,
            scac: 'CNVY',
            name: 'Convey'
        },
        lineItems: []
    };

    var mockVendorBillService = {
        get: function(filterHash, success) {
            success(mockVendorBill);
        }
    };
    var mockDictionaryService = {
        getWeightUnits: function() {
            return promises.getWeightUnits;
        },
        getDimensionUnits: function() {
            return promises.getDimensionUnits;
        },
        getCommodityClasses: function() {
            return promises.getCommodityClasses;
        },
        getPackageTypes: function() {
            return promises.getPackageTypes;
        },
        getPaymentTerms: function() {
            return promises.getPaymentTerms;
        }
    };
    var mockWeightUnits = ['LBS', 'KG'];
    var mockDimensionUnits = ['INCH', 'CMM'];
    var mockCommodityClasses = ['CLASS_50', 'CLASS_55', 'CLASS_60'];
    var mockPackageTypes = ['BG', 'BL', 'BR'];
    var mockPaymentTerms = ['COLLECT', 'PREPAID', 'THIRD_PARTY_COLLECT', 'THIRD_PARTY_PREPAID'];

    var mockShipmentUtils = {
        getDictionaryValues : function() {
            return {
                classes: mockCommodityClasses,
                dimensions: mockDimensionUnits,
                weights: mockWeightUnits,
                paymentTerms: mockPaymentTerms,
                packageTypes: mockPackageTypes
            };
        }
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function ($rootScope, $controller, $q) {
        scope = $rootScope.$new();

        var createPromise = function(name) {
            var defer = $q.defer();
            defers[name] = defer;
            promises[name] = defer.promise;
        };

        createPromise('getWeightUnits');
        createPromise('getDimensionUnits');
        createPromise('getCommodityClasses');
        createPromise('getPackageTypes');
        createPromise('getPaymentTerms');

        controller = $controller('AddVendorBillController', {
            $scope: scope,
            VendorBillService: mockVendorBillService,
            ShipmentUtils: mockShipmentUtils
        });

        scope.$apply(function() {
            var resolveDictionary = function(name, result) {
                defers[name].resolve({data: result});
            };
            resolveDictionary('getWeightUnits', mockWeightUnits);
            resolveDictionary('getDimensionUnits', mockDimensionUnits);
            resolveDictionary('getCommodityClasses', mockCommodityClasses);
            resolveDictionary('getPackageTypes', mockPackageTypes);
            resolveDictionary('getPaymentTerms', mockPaymentTerms);
        });
    }));

    it('Should be initialized.', function() {
        c_expect(scope.weightUOM).to.be.eql(mockWeightUnits);
        c_expect(scope.dimensionUOM).to.be.eql(mockDimensionUnits);
        c_expect(scope.commodityClasses).to.be.eql(mockCommodityClasses);
        c_expect(scope.packageTypes).to.be.eql(mockPackageTypes);
        c_expect(scope.paymentTerms).to.be.eql(mockPaymentTerms);
    });

    it('Should open dialog to add new empty vendor bill.', function() {
        spyOn(mockVendorBillService, 'get').and.callThrough();
        scope.$apply(function() {
            scope.$root.$broadcast('event:showAddEditVendorBill', {});
        });
        expect(mockVendorBillService.get).not.toHaveBeenCalled();
        c_expect(scope.addEditVendorBillModel.showEditVendorBillDialog).to.be.true();
        c_expect(scope.addEditVendorBillModel.loadingIndicator.showPanel).to.be.false();
    });

    it('Should change carrier in vendor bill on progressive search result change.', function() {
        scope.$apply(function() {
            scope.$root.$broadcast('event:showAddEditVendorBill', {});
        });
        scope.$apply(function() {
            scope.addEditVendorBillModel.carrierTuple = {id: 5, name: 'AVRT:Averit'};
        });
        c_expect(scope.addEditVendorBillModel.vendorBill.carrier).to.be.eql({id: 5, scac: 'AVRT', name: 'Averit'});
    });

    it('Should open dialog to add new vendor bill and copy all data from provided sales order.', function() {
        var matchedLoad = {
            id: 2,
            finishOrder: {
                actualPickupDate: new Date(2013, 5, 24),
                estimatedDelivery: new Date(2013, 5, 24),
                actualDeliveryDate: new Date(2013, 5, 24),
                poNumber: 'po1',
                puNumber: 'pu1',
                ref: 'q1',
                quoteMaterials: [
                    {
                        weight: '200',
                        weightUnit: 'LBS',
                        commodityClass: 'CLASS_55',
                        productDescription: 'item1',
                        nmfc: '123',
                        quantity: 1,
                        packageType: 'BG',
                        hazmat: true
                    }
                ]
            },
            originDetails: {
                zip: 16066,
                address: {
                    addressName: 'o1',
                    address1: 'oa1'
                }
            },
            destinationDetails: {
                zip: 12345,
                address: {
                    addressName: 'd1',
                    address1: 'da1'
                }
            },
            bolNumber: '2013-10-22',
            proNumber: 'pro1',
            selectedProposition: {
                carrier: {id: 5, scac: 'AVRT', name: 'Averit'}
            }
        };
        scope.$apply(function() {
            scope.$root.$broadcast('event:showAddEditVendorBill', {matchedLoad: matchedLoad});
        });
        c_expect(scope.addEditVendorBillModel.vendorBill.matchedLoadId).to.be.eql(matchedLoad.id);
        c_expect(scope.addEditVendorBillModel.vendorBill.actualPickupDate).to.be.eql(matchedLoad.finishOrder.actualPickupDate);
        c_expect(scope.addEditVendorBillModel.vendorBill.estimatedDeliveryDate).to.be.eql(matchedLoad.finishOrder.estimatedDelivery);
        c_expect(scope.addEditVendorBillModel.vendorBill.actualDeliveryDate).to.be.eql(matchedLoad.finishOrder.actualDeliveryDate);
        c_expect(scope.addEditVendorBillModel.vendorBill.po).to.be.eql(matchedLoad.finishOrder.poNumber);
        c_expect(scope.addEditVendorBillModel.vendorBill.pu).to.be.eql(matchedLoad.finishOrder.puNumber);
        c_expect(scope.addEditVendorBillModel.vendorBill.quoteId).to.be.eql(matchedLoad.finishOrder.ref);

        c_expect(scope.addEditVendorBillModel.vendorBill.lineItems.length).to.be.eql(matchedLoad.finishOrder.quoteMaterials.length);
        c_expect(scope.addEditVendorBillModel.vendorBill.lineItems[0].weight).to.be.eql(matchedLoad.finishOrder.quoteMaterials[0].weight);
        c_expect(scope.addEditVendorBillModel.vendorBill.lineItems[0].weightUnit).to.be.eql(matchedLoad.finishOrder.quoteMaterials[0].weightUnit);
        c_expect(scope.addEditVendorBillModel.vendorBill.lineItems[0].commodityClass).to.be.eql(
            matchedLoad.finishOrder.quoteMaterials[0].commodityClass);
        c_expect(scope.addEditVendorBillModel.vendorBill.lineItems[0].productDescription).to.be.eql(
            matchedLoad.finishOrder.quoteMaterials[0].productDescription);
        c_expect(scope.addEditVendorBillModel.vendorBill.lineItems[0].nmfc).to.be.eql(matchedLoad.finishOrder.quoteMaterials[0].nmfc);
        c_expect(scope.addEditVendorBillModel.vendorBill.lineItems[0].quantity).to.be.eql(matchedLoad.finishOrder.quoteMaterials[0].quantity);
        c_expect(scope.addEditVendorBillModel.vendorBill.lineItems[0].packageType).to.be.eql(matchedLoad.finishOrder.quoteMaterials[0].packageType);
        c_expect(scope.addEditVendorBillModel.vendorBill.lineItems[0].hazmat).to.be.eql(matchedLoad.finishOrder.quoteMaterials[0].hazmat);

        c_expect(scope.addEditVendorBillModel.vendorBill.originAddress.zip).to.be.eql(matchedLoad.originDetails.zip);
        c_expect(scope.addEditVendorBillModel.vendorBill.destinationAddress.zip).to.be.eql(matchedLoad.destinationDetails.zip);
        c_expect(scope.addEditVendorBillModel.vendorBill.originAddress.name).to.be.eql(matchedLoad.originDetails.address.addressName);
        c_expect(scope.addEditVendorBillModel.vendorBill.originAddress.address1).to.be.eql(matchedLoad.originDetails.address.address1);
        c_expect(scope.addEditVendorBillModel.vendorBill.destinationAddress.name).to.be.eql(matchedLoad.destinationDetails.address.addressName);
        c_expect(scope.addEditVendorBillModel.vendorBill.destinationAddress.address1).to.be.eql(matchedLoad.destinationDetails.address.address1);

        c_expect(scope.addEditVendorBillModel.vendorBill.bol).to.be.eql(matchedLoad.bolNumber);
        c_expect(scope.addEditVendorBillModel.vendorBill.pro).to.be.eql(matchedLoad.proNumber);
        c_expect(scope.addEditVendorBillModel.vendorBill.carrier).to.be.eql(matchedLoad.selectedProposition.carrier);

        c_expect(scope.addEditVendorBillModel.carrierTuple).to.be.eql({id: 5, name: 'AVRT:Averit'});
        c_expect(scope.addEditVendorBillModel.vendorBill.amount).to.be.eql(0);
    });
});