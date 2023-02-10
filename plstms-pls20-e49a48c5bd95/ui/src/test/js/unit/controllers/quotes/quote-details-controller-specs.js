describe('Unit test for QuoteDetailsController', function() {
    var filter = undefined;
	var scope = undefined;
    var savedQuotesService = undefined;
    var pluginFactory = undefined;
    var savedQuoteEmailService = undefined;
    var dateTimeUtils = undefined;

    beforeEach(module('plsApp'));
    beforeEach(inject(function($injector) {
        var rootScope = $injector.get('$rootScope');
        scope = rootScope.$new();

        scope.authData = {
            customerUser: true,
            plsUser: false,
            organization: {
                orgId: 1,
                name: 'Dzi-Dzy'
            }
        };

        scope.$root.authData = {
            organization: {
                orgId: 3
            }
        };

        savedQuotesService = $injector.get('SavedQuotesService');
        pluginFactory = $injector.get('NgGridPluginFactory');
        savedQuoteEmailService = $injector.get('SavedQuoteEmailService');
        dateTimeUtils = $injector.get('DateTimeUtils');

        var controller = $injector.get('$controller');
        controller('QuoteDetailsController', {
            '$filter': filter,
            '$scope': scope,
            'SavedQuotesService': savedQuotesService,
            'NgGridPluginFactory': pluginFactory,
            'SavedQuoteEmailService': savedQuoteEmailService,
            'DateTimeUtils': dateTimeUtils
        });
    }));

    function selectQuote() {
        scope.selectedQuotes = [
                                {
                                    carrierCost: 57147.75,
                                    carrierName: "MIDLAND TRANSPORT LIMITED",
                                    commodityClass: "Multi",
                                    customerName: "PLS SHIPPER",
                                    customerRevenue: 96858.31,
                                    destination: {},
                                    estimatedTransitTime: 25920,
                                    id: 3,
                                    origin: {},
                                    quoteId: "2012-03-28-05",
                                    weight: 365.37
                                }
                               ];
    }

    function selectCustomer() {
        scope.selectedCustomer = {
                id: 2,
                name: "MACSTEEL"
        };
        scope.fromDate = "2016-01-01";
        scope.toDate = "2016-03-01";
    }

    it('proves that array of saved quotes is empty', function() {
        expect(scope.savedQuotes.length).toBe(0);
    });

    it('proves that array of selected quotes is empty', function() {
        expect(scope.selectedQuotes.length).toBe(0);
    });

    it('checks initial model state', function() {
        expect(scope.wizardData).toBeDefined();
        expect(scope.wizardData.shipment).toBeDefined();
        expect(scope.wizardData.shipment.details).toBeDefined();
        expect(scope.wizardData.shipment.details.accessorials.length).toBe(0);
        expect(scope.wizardData.shipment.selectedProposition).toBeDefined();
        expect(scope.wizardData.shipment.finishOrder).toBeDefined();
        expect(scope.wizardData.shipment.finishOrder.quoteMaterials.length).toBe(0);
        expect(scope.wizardData.customerStatus).toBeUndefined();
    });

    it('should check selected customer is specified', function() {
        expect(scope.selectedCustomer).toBeDefined();
        expect(scope.selectedCustomer.id).toBeUndefined();
        expect(scope.selectedCustomer.name).toBeUndefined();
    });

    it('should check email options', function() {
        expect(scope.emailOptions).toBeDefined();
        expect(scope.emailOptions.showSendQuoteMailDialog).toBeFalsy();
        expect(scope.emailOptions.sendMailFunction).toEqual(jasmine.any(Function));
        expect(scope.emailOptions.getTemplate).toEqual(jasmine.any(Function));
    });

    it('should check user capabilities', function() {
        expect(scope.userCapabilities).toContain('QUOTES_VIEW');
    });

    it('should calculate total revenue and cost when grid data successfully modified', function() {
        scope.quotesGrid = {
             gridId: 'quotesTestGrid',
             ngGrid: {
                 filteredRows: [
                     {
                         entity: {
                             carrierCost: 1,
                             carrierName: "MIDLAND TRANSPORT LIMITED",
                             commodityClass: "Multi",
                             customerName: "PLS SHIPPER",
                             customerRevenue: 1
                         }
                     },
                     {
                         entity: {
                             carrierCost: 2,
                             carrierName: "MIDLAND TRANSPORT LIMITED",
                             commodityClass: "Multi",
                             customerName: "PLS SHIPPER",
                             customerRevenue: 2
                         }
                     }
                 ]
             }
        };
        scope.$broadcast('ngGridEventData', 'quotesTestGrid');

        expect(scope.totalRevenueSum).toBe(3);
        expect(scope.totalCostSum).toBe(3);
        expect(scope.rowCount).toBe(2);
    });

    it('should view quote details', function() {
        selectQuote();
        spyOn(savedQuotesService, 'get');

        scope.viewQuoteDetails();

        expect(savedQuotesService.get).toHaveBeenCalledWith({customerId: 3, propositionId: 3}, jasmine.any(Function));
    });

    it('should delete quote', function() {
        spyOn(scope.$root, '$broadcast');
        scope.deleteQuote();

        expect(scope.$root.$broadcast)
            .toHaveBeenCalledWith('event:showConfirmation',
                    {
                        caption: 'Delete Saved Quote',
                        message: 'The selected Quote will be deleted.<br/>Do you want to proceed?', 
                        okFunction: jasmine.any(Function)
                    });
    });

    it('should email quote', function() {
        selectQuote();
        scope.emailQuote();
        expect(scope.emailOptions.showSendEmailDialog).toBeTruthy();
        expect(scope.emailOptions.subject).toEqual('Quote Ref #: 2012-03-28-05');
    });

    it('should check saved quote removal dialog', function() {
        expect(scope.savedQuotesRemoveDialog).toBeDefined();
        expect(scope.savedQuotesRemoveDialog.confirmDelete).toEqual(jasmine.any(Function));
    });

    it('should fetch saved quotes on customer change', function() {
        spyOn(savedQuotesService, 'list');
        selectCustomer();
        scope.getSavedQuotes();
        scope.$digest();

        expect(savedQuotesService.list).toHaveBeenCalledWith({customerId: 2, fromDate: '2016-01-01',
             toDate: '2016-03-01'}, jasmine.any(Function), jasmine.any(Function));
    });

    it('should clear savedQuotes and selectedQuotes arrays when customer is not defined', function() {
        spyOn(savedQuotesService, 'list');
        selectQuote();
        scope.savedQuotes = [{quoteId: 1, name: 'test quote'}];
        scope.selectedCustomer.id = undefined;
        scope.$digest();

        expect(savedQuotesService.list).not.toHaveBeenCalled();
        expect(scope.savedQuotes.length).toBe(0);
        expect(scope.selectedQuotes.length).toBe(0);
    });

    it('should check quotes grid', function() {
        expect(scope.quotesGrid).toBeDefined();
        expect(scope.quotesGrid.enableColumnResize).toBeTruthy();
        expect(scope.quotesGrid.data).toEqual('savedQuotes');
        expect(scope.quotesGrid.multiSelect).toBeFalsy();
        expect(scope.quotesGrid.selectedItems.length).toBe(0);
        expect(scope.quotesGrid.columnDefs.length).toBe(14);

        expect(scope.quotesGrid.columnDefs[0].field).toEqual('quoteId');
        expect(scope.quotesGrid.columnDefs[0].displayName).toEqual('Quote Ref#');
        expect(scope.quotesGrid.columnDefs[0].showTooltip).toBeTruthy();
        expect(scope.quotesGrid.columnDefs[0].width).toEqual('3%');

        expect(scope.quotesGrid.columnDefs[1].field).toEqual('volumeQuoteId');
        expect(scope.quotesGrid.columnDefs[1].displayName).toEqual('Volume Quote ID');
        expect(scope.quotesGrid.columnDefs[1].width).toEqual('4%');

        expect(scope.quotesGrid.columnDefs[2].field).toEqual('customerName');
        expect(scope.quotesGrid.columnDefs[2].displayName).toEqual('Customer Name');
        expect(scope.quotesGrid.columnDefs[2].width).toEqual('9%');

        expect(scope.quotesGrid.columnDefs[3].field).toEqual('origin');
        expect(scope.quotesGrid.columnDefs[3].displayName).toEqual('Origin');
        expect(scope.quotesGrid.columnDefs[3].width).toEqual('14%');
        expect(scope.quotesGrid.columnDefs[3].cellFilter).toEqual('zip');

        expect(scope.quotesGrid.columnDefs[4].field).toEqual('destination');
        expect(scope.quotesGrid.columnDefs[4].displayName).toEqual('Destination');
        expect(scope.quotesGrid.columnDefs[4].width).toEqual('14%');
        expect(scope.quotesGrid.columnDefs[4].cellFilter).toEqual('zip');

        expect(scope.quotesGrid.columnDefs[5].field).toEqual('carrierName');
        expect(scope.quotesGrid.columnDefs[5].displayName).toEqual('Carrier');
        expect(scope.quotesGrid.columnDefs[5].width).toEqual('14%');

        expect(scope.quotesGrid.columnDefs[6].field).toEqual('estimatedTransitTime');
        expect(scope.quotesGrid.columnDefs[6].displayName).toEqual('Transit Est Days');
        expect(scope.quotesGrid.columnDefs[6].width).toEqual('5%');
        expect(scope.quotesGrid.columnDefs[6].cellFilter).toEqual('minutesTime:true');

        expect(scope.quotesGrid.columnDefs[7].field).toEqual('weight');
        expect(scope.quotesGrid.columnDefs[7].displayName).toEqual('Weight');
        expect(scope.quotesGrid.columnDefs[7].width).toEqual('5%');
        expect(scope.quotesGrid.columnDefs[7].cellFilter).toEqual('appendSuffix:\'Lbs\'');
        expect(scope.quotesGrid.columnDefs[7].searchable).toBeFalsy();

        expect(scope.quotesGrid.columnDefs[8].field).toEqual('commodityClass');
        expect(scope.quotesGrid.columnDefs[8].displayName).toEqual('Class');
        expect(scope.quotesGrid.columnDefs[8].width).toEqual('4%');
        expect(scope.quotesGrid.columnDefs[8].cellFilter).toEqual('commodityClass');
        expect(scope.quotesGrid.columnDefs[8].searchable).toBeFalsy();

        expect(scope.quotesGrid.columnDefs[9].field).toEqual('shipperBaseRate');
        expect(scope.quotesGrid.columnDefs[9].displayName).toEqual('Base Rate');
        expect(scope.quotesGrid.columnDefs[9].width).toEqual('6%');
        expect(scope.quotesGrid.columnDefs[9].cellFilter).toEqual('plsCurrency');
        expect(scope.quotesGrid.columnDefs[9].searchable).toBeFalsy();
        expect(scope.quotesGrid.columnDefs[9].visible).toBeTruthy();

        expect(scope.quotesGrid.columnDefs[10].field).toEqual('carrierCost');
        expect(scope.quotesGrid.columnDefs[10].displayName).toEqual('Carrier Cost');
        expect(scope.quotesGrid.columnDefs[10].width).toEqual('7%');
        expect(scope.quotesGrid.columnDefs[10].cellFilter).toEqual('plsCurrency');
        expect(scope.quotesGrid.columnDefs[10].searchable).toBeFalsy();
        expect(scope.quotesGrid.columnDefs[10].visible).toBeFalsy();

        expect(scope.quotesGrid.columnDefs[11].field).toEqual('customerRevenue');
        expect(scope.quotesGrid.columnDefs[11].displayName).toEqual('Total');
        expect(scope.quotesGrid.columnDefs[11].width).toEqual('7%');
        expect(scope.quotesGrid.columnDefs[11].cellFilter).toEqual('plsCurrency');
        expect(scope.quotesGrid.columnDefs[11].searchable).toBeFalsy();

        expect(scope.quotesGrid.columnDefs[12].field).toEqual('pricingProfileId');
        expect(scope.quotesGrid.columnDefs[12].displayName).toEqual('Profile ID');
        expect(scope.quotesGrid.columnDefs[12].width).toEqual('4%');
        expect(scope.quotesGrid.columnDefs[12].visible).toBeFalsy();

        expect(scope.quotesGrid.action).toEqual(jasmine.any(Function));
        expect(scope.quotesGrid.plugins.length).toBe(4);
        expect(scope.quotesGrid.tooltipOptions).toBeDefined();
        expect(scope.quotesGrid.tooltipOptions.url).toEqual('pages/content/quotes/shipments-grid-tooltip.html');
        expect(scope.quotesGrid.tooltipOptions.onShow).toEqual(jasmine.any(Function));
        expect(scope.quotesGrid.progressiveSearch).toBeTruthy();
    });
});