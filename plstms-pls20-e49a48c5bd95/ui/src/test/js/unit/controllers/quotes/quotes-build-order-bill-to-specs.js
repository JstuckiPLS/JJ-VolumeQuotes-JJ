/**
 * Test scenarios for quote wizard Bill To.
 * @author: Aleksandr Leshchenko
 */
describe('quotes-build-order-bill-to-specs Test.', function () {
    var scope = undefined;
    var controller;
    var promiseProvider = undefined;

    var deffereds = [];

    var mockShipmentUtils = {
        getDictionaryValues : function() {
            return {};
        }
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function ($rootScope, $controller, $q) {
        promiseProvider = $q;
        scope = $rootScope.$new();
        deffereds = [];

        $controller('QuoteWizard', {$scope: scope, ShipmentDetailsService: {}, ShipmentUtils: mockShipmentUtils, 
            SavedQuotesService: {}, CustomerLabelResource: {}, AccTypesServices: {}});

        scope.wizardData.shipment = {
            finishOrder: {},
            originDetails: {},
            destinationDetails: {},
            selectedProposition: {
                carrier: {},
                "estimatedTransitDate": "2013-08-21T00:00:00.000"
            }
        };

        controller = $controller('BuildOrderCtrl', {$scope: scope, $q: $q, ProductService: {list:function(){}},
            AddressNameService: {}, ShipmentUtils: mockShipmentUtils});
    }));

    it('should test controller creation', function() {
        c_expect(controller).to.be.defined;
    });
});