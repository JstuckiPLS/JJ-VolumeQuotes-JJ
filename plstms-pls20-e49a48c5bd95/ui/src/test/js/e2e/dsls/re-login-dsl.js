/**
 * Future action to emulate Logout click if user is already logged in and then signing in as PLS User.
 */
angular.scenario.dsl('reLogin', function() {
    return function(loginLogoutPageObject) {
        return this.addFutureAction('Log out if needed and login', function($window, $document, done) {
            var $ = $window.$; // jQuery inside the iframe
            var elem = $(loginLogoutPageObject.logoutSelector);
            if (elem.length > 0) {
                loginLogoutPageObject.logout();
            }

            loginLogoutPageObject.login(loginLogoutPageObject.plsUser);

            expect(element(loginLogoutPageObject.logoutSelector, 'logout link').count()).toBe(1);

            done();
        });
    };
});