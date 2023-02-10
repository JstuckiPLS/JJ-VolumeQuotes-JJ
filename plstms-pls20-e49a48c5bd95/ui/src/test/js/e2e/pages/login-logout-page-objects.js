/**
 * Login\logout page objects.
 *
 * @author Eugene Borshch
 */
angular.module('PageObjectsModule').factory('LoginLogoutPageObject', [
    function() {
        return {
            loginSelector: 'login',
            passwordSelector: 'password',

            submitSelector: '#login-holder :button',
            logoutSelector: '[data-ng-click="logout()"]',

            loginVisibleSelector: '#login-holder :text:visible',
            passwordVisibleSelector: '#login-holder :password:visible',

            mainPlsMenuSelector: '[data-ng-if="authData.plsUser"] li a',
            mainCustomerMenuSelector: '[data-ng-if="authData.customerUser"] li a',

            plsCustomer: {
                login: "SPARTAN1",
                pwd: "Qwerty123"
            },

            plsUser: {
                login: "sysadmin",
                pwd: "Qwerty123"
            },

            login: function(credentials) {
                input(this.loginSelector).enter(credentials.login);
                input(this.passwordSelector).enter(credentials.pwd);
                disableLocationChangeCheck();
                var result = element(this.submitSelector).click();
                sleep(1);
                return  result;
            },

            logout: function() {
                element(this.logoutSelector).click();
            }
        };
    }
]);