/**
 * This scenario checks that application could be opened.
 *
 * @author Eugene Borshch
 */
describe('PLS Open Application ', function() {

    it('should open application entry point', function() {
        $injector = angular.injector(['PageObjectsModule']);
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');

        browser().navigateTo('/my-freight/');

        reLogin(loginLogoutPageObject);
    });

});