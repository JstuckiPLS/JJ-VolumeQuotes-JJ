/**
 * This directive will find itself inside HTML as a class, and will remove that
 * class, so CSS will remove loading image and show app content. It is also
 * responsible for showing/hiding login form.
 */
angular.module('plsApp.directives').directive('plsApplication', function () {
    return {
        restrict: 'C',
        link: function (scope, elem) {
            'use strict';

            // once Angular is started, remove class:
            elem.removeClass('waiting-for-angular');

            var login = elem.find('#login-holder'), main = elem.find('#content');
            var userID = elem.find('input[name="login"]'), password = elem.find('input[name="password"]');

            login.hide();

            scope.$on('event:auth-loginRequired', function () {
                main.hide();
                login.addClass('loginHolder');
                login.show();
                userID.focus();
            });
            scope.$on('event:auth-loginConfirmed', function () {
                login.removeClass('loginHolder');
                login.hide();
                main.show();
                password.val('');
            });
        }
    };
});
