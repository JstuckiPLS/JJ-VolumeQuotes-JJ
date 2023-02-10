/**
 * Test scenarios for sales order Bill To.
 * @author Aleksandr Leshchenko
 */
describe('sales-order-bill-to-specs Test.', function () {
    // ANGULAR SERVICES
    var scope = undefined;

    //SOAddressesCtrl controller
    var controller = undefined;

    var promiseProvider = undefined;

    var mockShipmentUtils = {
        getDictionaryValues : function() {
            return {};
        },
        addAddressNotificationsToLoadNotificationsWithoutDuplicates : function() {
        }
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function ($rootScope, $controller, $q) {
        scope = $rootScope.$new();

        $controller('BaseSalesOrderCtrl', {$scope: scope, ShipmentsProposalService: {}, ShipmentDocumentEmailService: {}});

        $controller('CreateSalesOrderCtrl', {$scope: scope, $routeParams: {step: 'addresses'}, ShipmentsProposalService: {
            getFreightBillPayTo: function(){}
        }, ShipmentUtils: function(){return {};}});
        scope.init();

        promiseProvider = $q;
        scope.editSalesOrderModel = {};
        controller = $controller('SOAddressesCtrl', {$scope: scope, 
            AccTypesServices : {}, DictionaryService: {}, 
            ShipmentMileageService: {}, ShipmentUtils: mockShipmentUtils });
        scope.$digest();

        scope.$apply(function () {
            scope.init();
        });
    }));

    it('should not select location', function () {
        scope.$broadcast('event:edit-sales-order-tab-change', 'addresses');
        scope.$apply();

        c_expect(scope.wizardData.shipment.location).not.to.exist;
    });

    it('should not select bill to', function () {
        scope.$broadcast('event:edit-sales-order-tab-change', 'addresses');
        scope.$apply();

        c_expect(scope.wizardData.shipment.billTo).not.to.exist;
    });
});