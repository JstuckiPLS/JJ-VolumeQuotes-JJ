xdescribe('Unit test for SODetailsCtrl', function() {
    var scope = null;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();

        var controller = $injector.get('$controller');
        controller('SODetailsCtrl', {
            '$scope': scope
        });
    }));

    function createWizardData() {
        scope.wizardData = {
            breadCrumbs: {
                list: [{id: 'notes'}, 
                       {id: 'vendor_bills'}, 
                       {id: 'tracking'}, 
                       {id: 'general_information'}, 
                       {id: 'addresses'}, 
                       {id: 'details'},
                       {id: 'docs'},
                       {id: 'ok_click'}],
                map: {
                    notes: {},
                    vendor_bills: {},
                    tracking: {},
                    general_information: {},
                    addresses: {},
                    details: {},
                    docs: {},
                    ok_click: {}
                }
            },
            shipment: {
                finishOrder: {
                    deliveryWindowFrom: '11/12/2015'
                },
                
                customerName: 'Stan',
                billTo: {
                    id: 1,
                    address: {
                        addressName: 'UA'
                    }
                },
                selectedProposition: {
                    costDetailItems: [
                                      {
                                          costDetailOwner: "C",
                                          refType: "FS",
                                          subTotal: 20
                                      },
                                      {
                                          costDetailOwner: "C",
                                          refType: "CRA",
                                          subTotal: 60
                                      },
                                      {
                                          costDetailOwner: "S",
                                          refType: "FS",
                                          subTotal: 30
                                      },
                                      {
                                          costDetailOwner: "S",
                                          refType: "SRA",
                                          subTotal: 70
                                      }
                                  ]
                },
                adjustments: [
                              {
                                  billToId: 5,
                                  billToName: "Metl-Span I",
                                  financialAccessorialsId: 4,
                                  invoiceDate: undefined,
                                  invoiceNumber: undefined,
                                  notInvoice: true,
                                  reason: 1,
                                  refType: "SRA",
                                  revenue: 112.5,
                                  version: 1
                              },
                              {
                                  billToId: 5,
                                  billToName: "Metl-Span I",
                                  cost: 100.02,
                                  financialAccessorialsId: 4,
                                  invoiceDate: undefined,
                                  invoiceNumber: undefined,
                                  notInvoice: true,
                                  reason: 1,
                                  refType: "CRA",
                                  version: 1
                              }
                             ]
            },
            selectedCustomer: {
                name: undefined
            },
            nonInvoicedAdjustmentAccessorials: [],
            removedAdjustments: []
        };
    };

    it('should return false', function() {
        scope.validateForm();
    });
});