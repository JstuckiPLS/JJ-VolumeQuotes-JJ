/**
 * Tests for SaveQuoteController.
 *
 * @author Nikita Cherevko
 */
describe('Save Quote Controller Test', function() {
    // angular services
    var scope = undefined;
    var rootScope = undefined;

    // Save Quote controller
    var controller = undefined;

    var mockSavedQuotesService = {
        save: function(customerId, userId) {
            //
        },
        updateRefNumber: function() {
            //
        }
    };

    var locationService = {
        url: function() {
            return {
                search: function() { /*fake function*/ }
            };
        }
    };

    function FSavedQuoteService(shipment) {
        this.object = shipment;
    };

    FSavedQuoteService.save = function(params, shipmentToSend, success, failure) {
        mockSavedQuotesService.save(params);
        success({quoteId: 10});
    };

    FSavedQuoteService.updateRefNumber = function(params) {
        mockSavedQuotesService.updateRefNumber(params);
    };

    beforeEach(module('plsApp'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        rootScope = $rootScope;
        scope.$apply(function() {
            scope.wizardData = {
                shipment: {
                    selectedProposition: {
                        id: undefined
                    },
                    finishOrder: {
                        quoteMaterials: undefined
                    }
                }
            };
        });
        scope.$digest();
        controller = $controller('SaveQuoteController', {$scope: scope, $location: locationService, SavedQuotesService: FSavedQuoteService});
        rootScope.$broadcast('event:saveSelectedQuoteForWizard');
    }));

    it('should build order properly', function() {
        c_expect(scope.showSaveQuoteDialog).to.equal(true);
        scope.wizardData = {
            shipment: {
                quoteId: 12
            }
        };
        scope.quoteExpired = false;
        scope.quoteUnavailable = false;
        spyOn(locationService, 'url').and.callFake(function() {
            return { search: function(params) {
                searchParams = params;
            }};
        });

        scope.buildOrder();

        c_expect(scope.showSaveQuoteDialog).to.equal(false);
        c_expect(locationService.url.calls.count()).to.equal(1);
        c_expect(locationService.url.calls.mostRecent().args).to.eql(['/quotes/quote']);
        c_expect(searchParams).to.eql({savedQuoteId:12, stepName:'build_order'});

        scope.quoteExpired = true;
        scope.showSaveQuoteDialog = true;

        scope.buildOrder();

        c_expect(scope.showNotification).to.equal(true);
        c_expect(scope.showSaveQuoteDialog).to.equal(false);

        scope.quoteExpired = false;
        scope.quoteUnavailable = true;
        scope.showSaveQuoteDialog = true;

        scope.buildOrder();

        c_expect(scope.showNotification).to.equal(true);
        c_expect(scope.showSaveQuoteDialog).to.equal(false);
    });

    it('should save quote properly', function() {
        c_expect(scope.showSaveQuoteDialog).to.equal(true);
        c_expect(scope.saveQuoteDialog).to.equal(true);
        scope.wizardData = {
            shipment : {
                quoteId : 15,
                selectedProposition: {
                    guid: 'random-guid'
                },
                finishOrder: {}
            },
            selectedCustomer : {
                id: 1
            },
            savedQuoteDetails: {},
            editedQuoteId: 34123
        };
        scope.quoteRef = 1500;
        spyOn(mockSavedQuotesService, 'save').and.callThrough();
        scope.saveQuote();
        c_expect(mockSavedQuotesService.save.calls.count()).to.equal(1);
        c_expect(scope.showSaveQuoteDialog).to.equal(false);
        c_expect(scope.wizardData.savedQuoteDetails['random-guid']).to.eql({
            quoteId: 10,
            quoteRef: 1500
        });
        c_expect(scope.quoteRef).to.be.undefined;
        c_expect(scope.wizardData.editedQuoteId).to.be.undefined;
    });

    it('should close SaveQuoteDialog properly', function() {
        c_expect(scope.showSaveQuoteDialog).to.equal(true);
        scope.wizardData = {
            shipment: {
                selectedProposition: {
                    ref: '123ref'
                }
            }
        };
        scope.closeSaveQuoteDialog();
        c_expect(scope.showSaveQuoteDialog).to.equal(false);
        c_expect(scope.wizardData.shipment.selectedProposition.ref).not.to.be.defined;
    });
});
