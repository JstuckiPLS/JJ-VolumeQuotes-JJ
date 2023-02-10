/**
 * Tests for Edit Sales Order Controller.
 *
 * @author Nikita Cherevko
 */
describe('EditSalesOrderCtrl Test', function () {

    var scope = undefined;
    var promiseProvider = undefined;
    var controller = undefined;

    var shipment = {
        id: 7073,
        createdUserId: 2,
        organizationId: 1,
        createdDate: '2013-08-20T12:00:00.000',
        status: 'BOOKED',
        ediDispatch: 'true',
        billTo: {
            creditHold: false,
            availableAmount: 200
        },
        finishOrder: {
            pickupDate: '2013-08-20T00:00:00.000',
            quoteMaterials: [
                {
                    id: 7171, weight: 3000, commodityClass: 'CLASS_175', quantity: 100, packageType: 'BX', productCode: 'C6788-4',
                    productDescription: 'LCD TV', nmfc: 456454, hazmat: false, stackable: false
                }
            ]
        },
        originDetails: {
            zip: {
                zip: '22222',
                country: {id: 'USA', name: 'United States of America', dialingCode: '001'},
                state: 'VA', city: 'ARLINGTON'
            },
            address: {id: -1, addressName: 'ADDR_NAME135'},
            accessorials: [
                {id: 'LFC', name: 'Liftgate', group: 'PICKUP'}
            ]
        },
        destinationDetails: {
            zip: {
                zip: '10101',
                country: {id: 'USA', name: 'United States of America', dialingCode: '001'},
                state: 'NY', city: 'NEW YORK'
            },
            address: {id: -1, addressName: 'ADDR_NAME107'},
            accessorials: [
                {id: 'IDL', name: 'Inside', group: 'DELIVERY'},
                {id: 'RES', name: 'Residential', group: 'DELIVERY'}
            ]
        },
        selectedProposition: {ref: 'test-reference', carrier: {id: 123, name: 'test carrier', ediCapable: true}}
    };

    var dictionaryValues = [Math.random(), Math.random()];

    var mockShipmentUtils = {
        isCreditLimitValid: function(){ return true;
        },
        showUnmatchCarrierWarning : function() {
        }
    };

    var mockDocs = [
        {docId: 1}
    ];

    var mockResponse = {data: true};

    var mockShipmentOperationsService = {
        getShipment: function (params, success) {
            success(angular.copy(shipment));
        }
    };
    var mockShipmentSavingService = {
            save: function (params, shipment, success, failure) {
                success(angular.copy(shipment));
            }
        };
    var mockShipmentsProposalService = {
        getFreightBillPayTo: function (params, success) {
            success({});
            return this;
        }
    };

    var showEditOrderDialog = function () {
        scope.$apply(function () {
            scope.$parent.authData = {
                organization: {orgId: 1, name: 'Test Organization'},
                personId: 1,
                fullName: 'Test User',
                plsUser: false
            };
            scope.$broadcast('event:showEditSalesOrder', {
                shipmentId: 7073,
                closeHandler: function () {
                    //fake function
                }
            });
        });
    };

    var mockShipmentDocumentEmailService = {};

    beforeEach(module('plsApp'));
    beforeEach(module('plsApp', function ($provide) {
        $provide.factory('DictionaryService', function () {
            return {
                getPackageTypes: function () {
                    return {
                        success: function (handler) {
                            handler([{code: "BOX", label: "Boxes"}, {code: "ENV", label: "Envelopes"}, {code: "PLT", label: "Pallet"}]);
                        }
                    };
                }
            };
        });
    }));

    beforeEach(inject(function ($rootScope, $controller, $q, $injector) {
        scope = $rootScope.$new();
        $rootScope.redirectToUrl = function () {
        };
        $controller('BaseSalesOrderCtrl', {
            $scope: scope,
            ShipmentsProposalService: mockShipmentsProposalService, ShipmentDocumentEmailService: mockShipmentDocumentEmailService
        });
        promiseProvider = $q;

        controller = $controller('EditSalesOrderCtrl', {
            $scope: scope, $q: promiseProvider,
            ShipmentOperationsService: mockShipmentOperationsService, ShipmentSavingService: mockShipmentSavingService,
            ShipmentDocumentEmailService: mockShipmentDocumentEmailService, ShipmentUtils: mockShipmentUtils
        });

        spyOn(mockShipmentOperationsService, 'getShipment').and.callThrough();

        c_expect(scope.editSalesOrderModel.showEditOrderDialog).to.equal(false);

        var httpBackend = $injector.get('$httpBackend');
        httpBackend.expectGET('/restful/customer/1/address/list?type=BOTH,FREIGHT_BILL').respond();

        showEditOrderDialog();

        scope.$digest();
    }));

    it('should edit sales order window initialized and displayed properly', function () {
        c_expect(controller).to.be.defined;
        c_expect(scope.editSalesOrderModel.showEditOrderDialog).to.equal(true);
        c_expect(scope.editSalesOrderModel.readOnlyModeAllowed).to.be.undefined;
        c_expect(scope.wizardData.emptyShipment.status).to.eql('BOOKED');
        c_expect(scope.wizardData.breadCrumbs.list.length).to.equal(11);
        c_expect(mockShipmentOperationsService.getShipment.calls.count()).to.equal(1);
        c_expect(mockShipmentOperationsService.getShipment.calls.mostRecent().args[0]).to.eql({customerId: 1, shipmentId: 7073});
        c_expect(scope.editSalesOrderModel.originAddress).to.be.defined;
        c_expect(scope.editSalesOrderModel.destinationAddress).to.be.defined;
        c_expect(scope.wizardData.carrierTuple).to.be.defined;
//        c_expect(scope.editSalesOrderModel.selectedTab).to.eql('general_information'); // TODO defined phone
        scope.selectTab('addresses');
        c_expect(scope.editSalesOrderModel.selectedTab).to.eql('addresses');

        scope.wizardData = undefined;
        scope.selectTab('details');
        c_expect(scope.editSalesOrderModel.selectedTab).to.eql('addresses');
    });

    it('should addresses be cleared if zip changes', function () {
        scope.$apply(function () {
            scope.wizardData.shipment.originDetails.zip.zip = '12331';
            scope.wizardData.shipment.destinationDetails.zip.zip = '22331';
        });
        c_expect(scope.wizardData.shipment.originDetails.address).to.eql({
            addressName: '', phone: {},
            fax: {type: 'FAX'}, zip: scope.wizardData.shipment.originDetails.zip
        });
        c_expect(scope.wizardData.shipment.destinationDetails.address).to.eql({
            addressName: '', phone: {},
            fax: {type: 'FAX'}, zip: scope.wizardData.shipment.destinationDetails.zip
        });
    });

   it('should update order work properly', function () {
        scope.wizardData.shipmentBackup = shipment;
        scope.$apply(function () {
            scope.wizardData.vendorBillModel = {
                vendorBill: {
                    carrier: {id: 321, name: 'test', ediCapable: true}
                }
            };
        });
        spyOn(mockShipmentSavingService, 'save').and.callThrough();
        spyOn(mockShipmentUtils, 'showUnmatchCarrierWarning').and.callThrough();
        scope.updateOrder();
        c_expect(mockShipmentUtils.showUnmatchCarrierWarning.calls.count()).to.equal(0);
        c_expect(scope.wizardData.shipmentBackup.status).to.eql('BOOKED');
        c_expect(mockShipmentSavingService.save.calls.count()).to.equal(1);
        c_expect(mockShipmentSavingService.save.calls.mostRecent().args[0]).to.eql({customerId: 1, hideCreatedTime: false});
        c_expect(scope.editSalesOrderModel.showEditOrderDialog).to.equal(false);
    });
});
