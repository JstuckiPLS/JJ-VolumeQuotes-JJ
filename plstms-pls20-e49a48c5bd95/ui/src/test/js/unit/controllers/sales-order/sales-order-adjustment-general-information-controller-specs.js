xdescribe('Unit test for SOGeneralAdjustmentInfoCtrl', function() {
    var scope = null;
    var filter;
    var dateTimeUtils;
    var dictionaryService;
    var stringUtils;
    var pluginFactory;
    var httpBackend;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();
        filter = $injector.get('$filter');
        dateTimeUtils = $injector.get('DateTimeUtils');
        dictionaryService = $injector.get('ShipmentDictionaryTypeService');
        stringUtils = $injector.get('StringUtils');
        pluginFactory = $injector.get('NgGridPluginFactory');
        httpBackend = $injector.get('$httpBackend');

        httpBackend.when('GET', '/restful/shipment/dictionary/financialReasons').respond({});
        httpBackend.when('GET', '/restful/shipment/dictionary/packageType').respond({});
        httpBackend.when('GET', '/restful/dictionary/notificationTypes').respond({});
        httpBackend.when('GET', '/restful/shipment/dictionary/billToReqField').respond({});

        createWizardData();

        var controller = $injector.get('$controller');
        controller('SOGeneralAdjustmentInfoCtrl', {
            '$scope': scope, 
            '$filter': filter,
            'DateTimeUtils': dateTimeUtils,
            'ShipmentDictionaryTypeService': dictionaryService, 
            'StringUtils': stringUtils, 
            'NgGridPluginFactory': pluginFactory
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
                customerName: 'Stan',
                billTo: {
                    id: 1,
                    address: {
                        addressName: 'UA'
                    }
                },
                finishOrder : { 
                    poNumber: '12'
                },
                quotedTotalCost: 80,
                quotedTotalRevenue: 100,
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
                                  ],
                    carrier: {
                        name: 'Bob',
                    }
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

    function createNotInvoicedAdjustments() {
        scope.wizardData.nonInvoicedAdjustmentAccessorials = [{
                billToId: 1,
                billToName: "testee",
                cost: 1,
                isCanceledAdjustment: false,
                isWrongCarrierAdjustment: false,
                notInvoice: false,
                reason: 31,
                refType: "SRA",
                carrier: {
                    name: 'AVRT: AVERITT EXPRESS'
                },
                revenue: 1
        }];
    };

    it('should check initial controller model', function() {
        expect(scope.costDetailsForGrid.length).toBe(0);
        expect(scope.costDetailsForOrder.length).toBe(0);
        expect(scope.selectedCostDetails.length).toBe(0);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials.length).toBe(0);
        expect(scope.wizardData.removedAdjustments.length).toBe(0);
        expect(scope.wizardData.selectedCustomer.name).toEqual('Stan');
        expect(scope.accessorialTypes.length).toBe(2);
        expect(scope.accessorialTypes[0]).toEqual({id : 'SRA', description : 'Base Rate', status : 'ACTIVE'});
        expect(scope.accessorialTypes[1]).toBeUndefined();
    });

    it('should trigger selected proposition watch and generate cost details from proposals', function() {
        createWizardData();
        scope.wizardData.shipment.adjustments = [];
        scope.$digest();

        expect(scope.costDetailsForOrder.length).toBe(2);
        expect(scope.wizardData.shipment.quotedTotalCost).toBe(80);
        expect(scope.wizardData.shipment.quotedTotalRevenue).toBe(100);
        expect(scope.costDetailsForOrder.length).toBe(2);
        expect(scope.costDetailsForOrder[0].refType).toEqual('FS');
        expect(scope.costDetailsForOrder[0].revenue).toBe(30);
        expect(scope.costDetailsForOrder[0].cost).toEqual(20);
        expect(scope.costDetailsForOrder[0].revenueNote).toBeUndefined();
        expect(scope.costDetailsForOrder[0].costNote).toBeUndefined();
        expect(scope.costDetailsForOrder[0].reason).toEqual('');
        expect(scope.costDetailsForOrder[0].billToId).toBe(1);
        expect(scope.costDetailsForOrder[0].billToName).toEqual('UA');
        expect(scope.costDetailsForOrder[0].invoiceNumber).toBeUndefined();
        expect(scope.costDetailsForOrder[0].invoiceDate).toBeUndefined();
        expect(scope.costDetailsForOrder[0].notInvoice).toBeFalsy();

        expect(scope.costDetailsForOrder[1].refType).toEqual('SRA');
        expect(scope.costDetailsForOrder[1].revenue).toBe(70);
        expect(scope.costDetailsForOrder[1].cost).toEqual(60);
        expect(scope.costDetailsForOrder[1].revenueNote).toBeUndefined();
        expect(scope.costDetailsForOrder[1].costNote).toBeUndefined();
        expect(scope.costDetailsForOrder[1].reason).toEqual('');
        expect(scope.costDetailsForOrder[1].billToId).toBe(1);
        expect(scope.costDetailsForOrder[1].billToName).toEqual('UA');
        expect(scope.costDetailsForOrder[1].invoiceNumber).toBeUndefined();
        expect(scope.costDetailsForOrder[1].invoiceDate).toBeUndefined();
        expect(scope.costDetailsForOrder[1].notInvoice).toBeFalsy();

        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials.length).toBe(0);
    });

    it('should trigger selected proposition watch and generate cost details from adjustments', function() {
        createWizardData();
        scope.wizardData.shipment.selectedProposition.costDetailItems = [];
        scope.$digest();

        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials.length).toBe(1);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].billToId).toBe(5);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].billToName).toEqual('Metl-Span I');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].financialAccessorialsId).toBe(4);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].invoiceDate).toBeUndefined();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].invoiceNumber).toBeUndefined();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].notInvoice).toBeTruthy();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].refType).toEqual('SRA');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].revenue).toBe(112.5);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].version).toBe(1);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].cost).toBe(100.02);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].costNote).toBeUndefined();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].grouping).toBeDefined();

        expect(scope.costDetailsForOrder.length).toBe(0);
    });

    it('should trigger selected proposition watch and generate cost details from adjustments and propositions', function() {
        createWizardData();
        scope.$digest();

        expect(scope.costDetailsForOrder.length).toBe(2);
        expect(scope.wizardData.shipment.quotedTotalCost).toBe(80);
        expect(scope.wizardData.shipment.quotedTotalRevenue).toBe(100);
        expect(scope.costDetailsForOrder.length).toBe(2);
        expect(scope.costDetailsForOrder[0].refType).toEqual('FS');
        expect(scope.costDetailsForOrder[0].revenue).toBe(30);
        expect(scope.costDetailsForOrder[0].cost).toEqual(20);
        expect(scope.costDetailsForOrder[0].revenueNote).toBeUndefined();
        expect(scope.costDetailsForOrder[0].costNote).toBeUndefined();
        expect(scope.costDetailsForOrder[0].reason).toEqual('');
        expect(scope.costDetailsForOrder[0].billToId).toBe(1);
        expect(scope.costDetailsForOrder[0].billToName).toEqual('UA');
        expect(scope.costDetailsForOrder[0].invoiceNumber).toBeUndefined();
        expect(scope.costDetailsForOrder[0].invoiceDate).toBeUndefined();
        expect(scope.costDetailsForOrder[0].notInvoice).toBeFalsy();

        expect(scope.costDetailsForOrder[1].refType).toEqual('SRA');
        expect(scope.costDetailsForOrder[1].revenue).toBe(70);
        expect(scope.costDetailsForOrder[1].cost).toEqual(60);
        expect(scope.costDetailsForOrder[1].revenueNote).toBeUndefined();
        expect(scope.costDetailsForOrder[1].costNote).toBeUndefined();
        expect(scope.costDetailsForOrder[1].reason).toEqual('');
        expect(scope.costDetailsForOrder[1].billToId).toBe(1);
        expect(scope.costDetailsForOrder[1].billToName).toEqual('UA');
        expect(scope.costDetailsForOrder[1].invoiceNumber).toBeUndefined();
        expect(scope.costDetailsForOrder[1].invoiceDate).toBeUndefined();
        expect(scope.costDetailsForOrder[1].notInvoice).toBeFalsy();

        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials.length).toBe(1);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].billToId).toBe(5);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].billToName).toEqual('Metl-Span I');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].financialAccessorialsId).toBe(4);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].invoiceDate).toBeUndefined();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].invoiceNumber).toBeUndefined();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].notInvoice).toBeTruthy();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].refType).toEqual('SRA');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].revenue).toBe(112.5);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].version).toBe(1);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].cost).toBe(100.02);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].costNote).toBeUndefined();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].grouping).toBeDefined();
    });

    it('should trigger not invoiced accessorials watcher and populate cost details grid', function() {
        createWizardData();
        createNotInvoicedAdjustments();
        scope.$digest();

        expect(scope.costDetailsForGrid.length).toBe(3);
        expect(scope.costDetailsForGrid).toContain(scope.wizardData.nonInvoicedAdjustmentAccessorials[0]);

        expect(scope.costDetailsForGrid[0].refType).toEqual('FS');
        expect(scope.costDetailsForGrid[0].revenue).toBe(30);
        expect(scope.costDetailsForGrid[0].cost).toBe(20);
        expect(scope.costDetailsForGrid[0].revenueNote).toBeUndefined();
        expect(scope.costDetailsForGrid[0].costNote).toBeUndefined();
        expect(scope.costDetailsForGrid[0].reason).toEqual('');
        expect(scope.costDetailsForGrid[0].billToId).toBe(1);
        expect(scope.costDetailsForGrid[0].billToName).toBe('UA');
        expect(scope.costDetailsForGrid[0].invoiceNumber).toBeUndefined();
        expect(scope.costDetailsForGrid[0].invoiceDate).toBeUndefined();
        expect(scope.costDetailsForGrid[0].notInvoice).toBeFalsy();

        expect(scope.costDetailsForGrid[1].refType).toEqual('SRA');
        expect(scope.costDetailsForGrid[1].revenue).toBe(70);
        expect(scope.costDetailsForGrid[1].cost).toBe(60);
        expect(scope.costDetailsForGrid[1].revenueNote).toBeUndefined();
        expect(scope.costDetailsForGrid[1].costNote).toBeUndefined();
        expect(scope.costDetailsForGrid[1].reason).toEqual('');
        expect(scope.costDetailsForGrid[1].billToId).toBe(1);
        expect(scope.costDetailsForGrid[1].billToName).toBe('UA');
        expect(scope.costDetailsForGrid[1].invoiceNumber).toBeUndefined();
        expect(scope.costDetailsForGrid[1].invoiceDate).toBeUndefined();
        expect(scope.costDetailsForGrid[1].notInvoice).toBeFalsy();

        expect(scope.totalCost).toBe(180.01999999999998);
        expect(scope.totalRevenue).toEqual('212.50');
    });

    it('should return "true" for edit mode', function() {
        createWizardData();
        createNotInvoicedAdjustments();
        scope.selectedCostDetails = [{refType: 'CBF'}];
        expect(scope.getAccessorialTypesForPopup(true)).toBeTruthy();
    });

    it('should return "true" for non edit mode', function() {
        createWizardData();
        createNotInvoicedAdjustments();
        scope.selectedCostDetails = [{refType: 'CBF'}];
        expect(scope.getAccessorialTypesForPopup(false)).toBeTruthy();
    });

    it('should add addjustment', function() {
        spyOn(scope.$root, '$broadcast');
        spyOn(scope, 'getAccessorialTypesForPopup');

        scope.addAdjustment();

        expect(scope.isEdit).toBeFalsy();
        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:showAddEditAdjustment', {
            isEdit: false,
            adjustmentModel: {
                shipment: {},
                notInvoice: false,
                revenue : 0.00,
                cost : 0.00
            },
            accessorialTypes: undefined
        });
        expect(scope.getAccessorialTypesForPopup).toHaveBeenCalledWith(false);
    });

    it('should edit adjustment', function() {
        scope.selectedCostDetails = [{}];
        spyOn(scope.$root, '$broadcast');

        scope.editAdjustment();

        expect(scope.isEdit).toBeTruthy();
        expect(scope.$root.$broadcast).toHaveBeenCalledWith('event:showAddEditAdjustment', jasmine.any(Object));
    });

    it('should delete adjustment and saved removed one', function() {
        createWizardData();
        createNotInvoicedAdjustments();
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].financialAccessorialsId = 1;
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].version = 1;
        scope.selectedCostDetails.push(scope.wizardData.nonInvoicedAdjustmentAccessorials[0]);

        scope.deleteAdjustment();

        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials.length).toBe(0);
        expect(scope.wizardData.removedAdjustments.length).toBe(1);
        expect(scope.wizardData.removedAdjustments[0].financialAccessorialsId).toEqual(1);
        expect(scope.wizardData.removedAdjustments[0].version).toEqual(1);
    });

    it('should delete adjustment from not invoiced adjustments accessorials', function() {
        createWizardData();
        createNotInvoicedAdjustments();
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].financialAccessorialsId = undefined;
        scope.selectedCostDetails.push(scope.wizardData.nonInvoicedAdjustmentAccessorials[0]);

        scope.deleteAdjustment();

        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials.length).toBe(0);
        expect(scope.wizardData.removedAdjustments.length).toBe(0);
        expect(scope.selectedCostDetails.length).toBe(0);
    });

    it('should delete wrong carrier adjustment and revert load', function() {
        createWizardData();
        createNotInvoicedAdjustments();
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].isWrongCarrierAdjustment = true;
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].reason = 8;
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].cost = -30;
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].cost = -20;
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].financialAccessorialsId = undefined;
        scope.selectedCostDetails.push(scope.wizardData.nonInvoicedAdjustmentAccessorials[0]);
        expect(scope.wizardData.shipment.selectedProposition.carrier.name).toEqual('Bob');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials.length).toBe(1);
        scope.deleteAdjustment();
        expect(scope.wizardData.shipment.selectedProposition.carrier.name).toEqual('AVRT: AVERITT EXPRESS');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials.length).toBe(0);
    });

    it('should save new voided adjustment', function() {
        createWizardData();
        createNotInvoicedAdjustments();
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].isCanceledAdjustment = true;
        scope.$broadcast('event:saveAdjustment', scope.wizardData.nonInvoicedAdjustmentAccessorials[0]);

        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials.length).toBe(1);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].billToId).toBe(1);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].billToName).toEqual('testee');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].cost).toBe(1);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].isCanceledAdjustment).toBeTruthy();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].isWrongCarrierAdjustment).toBeFalsy();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].notInvoice).toBeFalsy();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].reason).toBe(31);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].refType).toEqual('SRA');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].revenue).toBe(1);
    });

    it('should save new wrong carrier adjustment', function() {
        createWizardData();
        createNotInvoicedAdjustments();
        scope.$digest();
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].isWrongCarrierAdjustment = true;
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].oldCarrier = { name: 'Test' };
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].reason = 8;
        scope.wizardData.nonInvoicedAdjustmentAccessorials[0].invoiceDate = '12/29/16';
        scope.$broadcast('event:saveAdjustment', scope.wizardData.nonInvoicedAdjustmentAccessorials[0]);

        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials.length).toBe(2);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].billToId).toBe(5);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].billToName).toEqual('Metl-Span I');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].cost).toBe(-100.02);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].notInvoice).toBeTruthy();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].reason).toBe(8);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].refType).toEqual('SRA');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[0].revenue).toBe(0);

        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[1].billToId).toBe(5);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[1].billToName).toEqual('Metl-Span I');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[1].cost).toBe(100.02);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[1].notInvoice).toBeTruthy();
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[1].reason).toBe(8);
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[1].refType).toEqual('SRA');
        expect(scope.wizardData.nonInvoicedAdjustmentAccessorials[1].revenue).toBe(0);
    });

    it('should edit adjustment', function() {
        createWizardData();
        createNotInvoicedAdjustments();
        scope.isEdit = true;
        var existingAdjustment = {
            billToId: 3,
            billToName: "testee",
            cost: 5,
            notInvoice: false,
            reason: 31,
            refType: "FS",
            revenue: 1
        };
        scope.selectedCostDetails.push(existingAdjustment);
        scope.wizardData.nonInvoicedAdjustmentAccessorials.push(existingAdjustment);
        scope.$broadcast('event:saveAdjustment', scope.wizardData.nonInvoicedAdjustmentAccessorials[0]);

        expect(scope.selectedCostDetails[0].billToId).toBe(1);
        expect(scope.selectedCostDetails[0].billToName).toEqual('testee');
        expect(scope.selectedCostDetails[0].cost).toBe(1);
        expect(scope.selectedCostDetails[0].isCanceledAdjustment).toBeFalsy();
        expect(scope.selectedCostDetails[0].isWrongCarrierAdjustment).toBeFalsy();
        expect(scope.selectedCostDetails[0].notInvoice).toBeFalsy();
        expect(scope.selectedCostDetails[0].reason).toBe(31);
        expect(scope.selectedCostDetails[0].refType).toEqual('SRA');
        expect(scope.selectedCostDetails[0].revenue).toBe(1);
    });

    it('should check adjustment grid', function() {
        expect(scope.adjustmentsGrid).toBeDefined();
        expect(scope.adjustmentsGrid.enableColumnResize).toBeTruthy();
        expect(scope.adjustmentsGrid.data).toEqual('costDetailsForGrid');
        expect(scope.adjustmentsGrid.selectedItems.length).toBe(0);
        expect(scope.adjustmentsGrid.columnDefs.length).toBe(9);

        expect(scope.adjustmentsGrid.columnDefs[0].field).toEqual('grouping');
        expect(scope.adjustmentsGrid.columnDefs[0].visible).toBeFalsy();

        expect(scope.adjustmentsGrid.columnDefs[1].field).toEqual('refType');
        expect(scope.adjustmentsGrid.columnDefs[1].displayName).toEqual('Acc. Description');
        expect(scope.adjustmentsGrid.columnDefs[1].cellFilter).toEqual('refCodeAndDesc');
        expect(scope.adjustmentsGrid.columnDefs[1].width).toEqual('20%');

        expect(scope.adjustmentsGrid.columnDefs[2].field).toEqual('reason');
        expect(scope.adjustmentsGrid.columnDefs[2].displayName).toEqual('Adj. Reason');
        expect(scope.adjustmentsGrid.columnDefs[2].width).toEqual('15%');
        expect(scope.adjustmentsGrid.columnDefs[2].cellFilter).toEqual('financialReason');

        expect(scope.adjustmentsGrid.columnDefs[3].field).toEqual('billToName');
        expect(scope.adjustmentsGrid.columnDefs[3].displayName).toEqual('Bill To');
        expect(scope.adjustmentsGrid.columnDefs[3].width).toEqual('14%');

        expect(scope.adjustmentsGrid.columnDefs[4].field).toEqual('invoiceNumber');
        expect(scope.adjustmentsGrid.columnDefs[4].displayName).toEqual('Inv Number');
        expect(scope.adjustmentsGrid.columnDefs[4].width).toEqual('15%');
        
        expect(scope.adjustmentsGrid.columnDefs[5].field).toEqual('invoiceDate');
        expect(scope.adjustmentsGrid.columnDefs[5].displayName).toEqual('Inv. Date');
        expect(scope.adjustmentsGrid.columnDefs[5].width).toEqual('7%');
        expect(scope.adjustmentsGrid.columnDefs[5].cellFilter).toEqual('date : appDateFormat');
        
        expect(scope.adjustmentsGrid.columnDefs[6].field).toEqual('revenue');
        expect(scope.adjustmentsGrid.columnDefs[6].displayName).toEqual('Revenue');
        expect(scope.adjustmentsGrid.columnDefs[6].width).toEqual('7%');
        expect(scope.adjustmentsGrid.columnDefs[6].cellFilter).toEqual('plsCurrency');
        
        expect(scope.adjustmentsGrid.columnDefs[7].field).toEqual('cost');
        expect(scope.adjustmentsGrid.columnDefs[7].displayName).toEqual('Cost');
        expect(scope.adjustmentsGrid.columnDefs[7].width).toEqual('7%');
        expect(scope.adjustmentsGrid.columnDefs[7].cellFilter).toEqual('plsCurrency');

        expect(scope.adjustmentsGrid.columnDefs[8].field).toEqual('notInvoice');
        expect(scope.adjustmentsGrid.columnDefs[8].displayName).toEqual('Do Not Invoice');
        expect(scope.adjustmentsGrid.columnDefs[8].width).toEqual('11%');
        expect(scope.adjustmentsGrid.columnDefs[8].cellTemplate).toEqual('pages/cellTemplate/checked-cell.html');

        expect(scope.adjustmentsGrid.plugins.length).toBe(1);
        expect(scope.adjustmentsGrid.groups).toContain('grouping');
        expect(scope.adjustmentsGrid.aggregateTemplate)
            .toEqual("<div data-ng-click=\"row.toggleExpand()\" data-ng-style=\"rowStyle(row)\" class=\"ngAggregate\">" +
                    "    <p style=\"white-space:pre-wrap;\" class=\"ngAggregateText\">{{getAggregateRowText(row)}}</p>" +
                    "    <div class=\"{{row.aggClass()}}\"></div>" +
                    "</div>");
        expect(scope.adjustmentsGrid.enableSorting).toBeFalsy();
        expect(scope.adjustmentsGrid.groupsCollapsedByDefault).toBeFalsy();
        expect(scope.adjustmentsGrid.multiSelect).toBeFalsy();
    });

    it('should get aggregate text', function() {
        scope.costDetailsForGrid = [{name: ''}, {name: ''}, {name: ''}];
        var row = {
            totalChildren: function() {return 1},
            aggIndex: 0,
            children: [
                       {
                           cursor: "pointer",
                           entity: {
                               billToId: 7,
                               billToName: "Belrun inc",
                               cost: 60,
                               costNote: undefined,
                               grouping: "undefined_7_LTL-59-0000_12/29/12",
                               invoiceDate: '12/29/12',
                               invoiceNumber: "LTL-59-0000",
                               notInvoice: false,
                               reason: "",
                               refType: "SRA",
                               revenue: 70,
                               revenueNote: undefined,
                               carrier: {
                                   name: 'AVRT: AVERITT EXPRESS'
                               }
                           },
                       }
                      ]
        };
        var invoiceHeader = scope.getAggregateRowText(row);

        expect(invoiceHeader)
            .toEqual('Invoice 1    ( Belrun inc, LTL-59-0000, 12/29/12)    1 cost item of Total: $60.00 / $70.00 ');
    });

    it('should return "true" if no cost details selected', function() {
        scope.selectedCostDetails = [];
        expect(scope.notInvoicedAdjustmentSelected()).toBeTruthy();
    });

    it('should return "true" if invoice number and date are both specified', function() {
        scope.selectedCostDetails = [];
        var invoice =  {invoiceDate: '12/29/12', invoiceNumber: "LTL-59-0000"};
        scope.selectedCostDetails.push(invoice);
        expect(scope.notInvoicedAdjustmentSelected()).toBeTruthy();
    });
});