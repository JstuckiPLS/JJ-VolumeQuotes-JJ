/**
 * Tests for pls notification directive.
 *
 * @author Denis Zhupinsky
 */
describe('PLS notification directive test.', function () {

    // directive element
    var elm = undefined;
    // angular scope
    var scope = undefined;

    var timeout;

    var ContactInfo = function (name, phone, fax, email) {
        this.name = name;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
    };

    var ContactInfoSet = function (salesRep, customerRep, plsCorporate, terminal) {
        this.terminal = terminal;
    };

    var mockService = {
        findContactSetInfo: function () {
            return new ContactInfoSet(
                    new ContactInfo('Sales name', 'Sales phone', 'Sales fax', 'Sales email'),
                    new ContactInfo('Customer name', 'Customer phone', 'Customer fax', 'Customer email'),
                    new ContactInfo('PlsCorporate name', 'PlsCorporate phone', 'PlsCorporate fax', 'PlsCorporate email'),
                    new ContactInfo('Terminal name', 'Terminal phone', 'Terminal fax', 'Terminal email')
            );
        }
    };



    beforeEach(module('plsApp', function ($provide) {
        $provide.factory('AddressService', function () {
            return mockService;
        });
    }));

    beforeEach(inject(function ($rootScope, $compile, $templateCache, $httpBackend, $timeout) {
        scope = $rootScope.$new();
        timeout = $timeout;

        $templateCache.put('pages/content/quotes/pickup-message.html', '<div>Fake pls notification template</div>');
        $httpBackend.whenGET('pages/content/quotes/pickup-message.html').respond($templateCache.get('pages/content/quotes/pickup-message.html'));



        scope.$apply(function () {
            scope.cancelFn = function() {
                //do nothing
            }
        });

        elm = angular.element('<div data-pls-notification data-dialog-open="openPlsNotificationTestDialog" data-on-cancel="cancelFn"></div>');
        $compile(elm)(scope);

        scope.$digest();
    }));

    it('should get data during dialog open', function () {
        spyOn(mockService, 'findContactSetInfo').and.callThrough();
        scope.$apply(function () {
            scope.openPlsNotificationTestDialog = true;
        });

        c_expect(mockService.findContactSetInfo.calls.count()).to.equal(1);

    });

    it('should call cancel function', function () {
        spyOn(scope, 'cancelFn').and.callThrough();
        scope.$digest();

        var child = scope.$$childHead;
        var closeFnCall;
        while(child) {
            if(!child.closePickupMessageDialog) {
                child = child.$$nextSibling;
            } else {
                closeFnCall = child.closePickupMessageDialog;
                child = undefined;
            }
        }

        closeFnCall();
        timeout.flush();

        c_expect(scope.cancelFn.calls.count()).to.equal(1);

    });
});