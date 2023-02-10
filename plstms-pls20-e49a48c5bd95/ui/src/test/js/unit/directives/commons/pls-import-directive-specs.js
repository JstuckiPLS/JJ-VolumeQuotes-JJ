/**
 * Test Pls Import Directive
 *
 * @author Eugene Borshch
 */
describe('PLS Import Directive Test', function() {

    var elm, scope,


        badFormatResponse = '{"success": false, "succeedCount": 0,"failedCount": 0, ' +
            '"fixNowDocId": null,"errorType": null, "errorMessageList": null}',

        successResopnse = '{"success":true,"succeedCount":10,"failedCount":0,"fixNowDocId":null,"errorType":null,"errorMessageList":[]}',

        failResponse = '{"success":true,"succeedCount":1,"failedCount":10,"fixNowDocId":7481,"errorType":null,' +
            '"errorMessageList":["ValidationException: errors:{addressName=UNIQUE}"]}',

        fileName = {
            'value': 'dummy.xls'
        };

    beforeEach(module('plsApp', 'pages/tpl/import-data-tpl.html',
        'pages/tpl/progress-panel-tpl.html'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        scope.$apply(function() {
            scope.importAddressOptions = {
                label: 'Import Address',
                showDialog: false,
                importUrl: function() {
                    return 'no-op';
                },
                fixUrl: function() {
                    return 'no-op';
                }
            };
        });
        elm = angular.element('<div class="pls-import-test" data-pls-import="importAddressOptions"></div>');
        $compile(elm)(scope);
        scope.$digest();
    }));

    it("should fail on bad format document", function() {
        $('body').append(elm);
        elm.isolateScope().uploadCallback(badFormatResponse, true);
        scope.$digest();

        var isSuccessfullyImported = elm.isolateScope().importItemsModel.importResults.success && !elm.isolateScope().importItemsModel.importResults.hasNoRecords;
        expect(isSuccessfullyImported).toBe(false);
        $('.pls-import-test').remove();
    });

    it("should handle 'partial import' response", function() {
        $('body').append(elm);
        elm.isolateScope().uploadCallback(failResponse, true);
        scope.$digest();

        var isSuccessfullyImported = elm.isolateScope().importItemsModel.importResults.success && !elm.isolateScope().importItemsModel.importResults.hasNoRecords;
        expect(isSuccessfullyImported).toBe(true);

        expect(elm.isolateScope().importItemsModel.importResults.hasFailedRecords).toBe(true);

        expect(elm.isolateScope().importItemsModel.importResults.failedCount).toBe(10);

        expect(elm.isolateScope().importItemsModel.importResults.succeedCount).toBe(1);

        expect(elm.isolateScope().importItemsModel.importResults.fixNowDocId).toBe(7481);
        $('.pls-import-test').remove();
    });

    it("should handle succesfull response", function() {
        $('body').append(elm);
        elm.isolateScope().uploadCallback(successResopnse, true);
        scope.$digest();

        var isSuccessfullyImported = elm.isolateScope().importItemsModel.importResults.success && !elm.isolateScope().importItemsModel.importResults.hasNoRecords;
        expect(isSuccessfullyImported).toBe(true);

        expect(elm.isolateScope().importItemsModel.importResults.hasFailedRecords).toBe(false);

        expect(elm.isolateScope().importItemsModel.importResults.failedCount).toBe(0);

        expect(elm.isolateScope().importItemsModel.importResults.succeedCount).toBe(10);

        expect(elm.isolateScope().importItemsModel.importResults.fixNowDocId).toBeNull();
        $('.pls-import-test').remove();
    });


    it('should fire "event:application-error" event in case of 401', function() {
        var listener = jasmine.createSpy('listener');
        scope.$on('event:application-error', listener);

        elm.isolateScope().uploadCallback('non valid json', true);
        scope.$digest();

        expect(listener).toHaveBeenCalled();
    });

    it('should fire "event:auth-loginRequired" event in case of 401', function() {
        elm.isolateScope().$apply(function () {
            elm.isolateScope().importItemsModel.progressPanelOptions.showPanel = true;
        });

        var listener = jasmine.createSpy('listener');
        elm.isolateScope().$on('event:auth-loginRequired', listener);

        elm.isolateScope().uploadCallback('HTTP Status 401 - Unauthorized description This request requires HTTP authentication Apache Tomcat/7.0.42', true);
        scope.$digest();

        c_expect(elm.isolateScope().importItemsModel.progressPanelOptions.showPanel).to.be.false;

        expect(listener).toHaveBeenCalled();
    });
});