/**
 * Controller for 'Login' modal window.
 */
angular.module('plsApp').controller('LoginDialogCtrl', ['$scope', '$rootScope', '$location', '$http', '$log', 'authService', 'urlConfig',
                                                        'AccessorialTypeService',
    function ($scope, $rootScope, $location, $http, $log, authService, urlConfig, AccessorialTypeService) {
        'use strict';

        $scope.ignoreLocationChangeFlag = $rootScope.ignoreLocationChangeFlag;

        $scope.updateCheckFlag = function () {
            $rootScope.ignoreLocationChangeFlag = $scope.ignoreLocationChangeFlag;
        };

        $scope.forgotPasswordDialogOpen = function () {
            $rootScope.$broadcast('event:forgotPasswordOpen', {
                userId: $scope.login
            });
        };

        $scope.hideErrorMsg = function () {
            $scope.showErrorMessage = false;
            $scope.inactiveCustomerLoginAttemptFailure = false;
        };

        $scope.openContactUsDialog = function (inactiveLogin) {
            $rootScope.$broadcast('event:openContactUs', {showAboveLogin: true, inactiveCustomerLoginAttemptFailure: inactiveLogin});
        };

        function automaticLoginCheck() {
            if ($scope.credentials && $scope.credentials.l && $scope.credentials.c) {
                $scope.submit($scope.credentials);
            }
        }

        function cleanAuthData(data) {
            $rootScope.authData = new AuthData(null);
            var matcher = data.match(/Customer user authentication attempt of inactive customer failure/);
            if (matcher !== null && matcher.length > 0) {
                $scope.openContactUsDialog(true);
            } else {
                $scope.showErrorMessage = true;
            }
        }

        var checkLoginFunction = function () {
            $http.get(urlConfig.login + '/auth/current_user').success(function (data) {
                try {
                    $rootScope.authData = new AuthData(data);

                    if ($scope.authData.loginSuccess()) {
                        $scope.hideErrorMsg();
                        authService.loginConfirmed();
                        $rootScope.redirectToUrl();
                    } else {
                        $log.info("error", "Login filed because user is not PLS or customer user", $scope.authData);
                        $scope.showErrorMessage = true;
                    }

                } catch (e) {
                    $log.info("error", "Unexpected exception when parsing response data", e, data);
                    cleanAuthData(data);
                }
            }).error(function (data, status) {
                $log.info("error", status, data);
                cleanAuthData(data);
                automaticLoginCheck();
            });

        };

        $rootScope.$on('event:auth-loginConfirmed', function () {
            AccessorialTypeService.getAccessoralTypes({}, function (data) {
                $rootScope.accessorialTypes = data;
            });
        });

        $scope.submit = function (paramsObj) {
            /**
             * this workaround fixes problem with password auto complete.
             */
            var params = {
                "login": paramsObj ? paramsObj.l : $('input[name=login]').val(),
                "password": paramsObj ? paramsObj.c : $('input[name=password]').val()
            };
            if ($('input[name=remember-me]').is(':checked')) {
                params['remember-me'] = 'on';
            }

            $http({
                method: 'POST',
                url: urlConfig.login + '/auth/login',
                data: $.param(params),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function () {
                checkLoginFunction();
            }).error(function (data) {
                cleanAuthData(data);
                if ($scope.credentials && $scope.credentials.l && $scope.credentials.c) {
                    checkLoginFunction();
                }
            });
        };

        $scope.hideErrorMsg();

        $scope.credentials = $location.search();
        $location.url($location.path());
        automaticLoginCheck();

        //We need to check user login each time page reloads.
        if (!$scope.credentials.l || !$scope.credentials.c) {
            checkLoginFunction();
        }
    }
]);

/**
 * Controller for header area. Displays user name, logo etc.
 */
angular.module('plsApp').controller('UserDetailsCtrl', ['$scope', '$rootScope', '$http', 'urlConfig', '$location',
    function ($scope, $rootScope, $http, urlConfig, $location) {
        'use strict';

        $scope.logout = function () {
            $http.get(urlConfig.login + '/auth/logout');
            $rootScope.authData = new AuthData(null);
            $rootScope.$broadcast('event:auth-loginRequired');
//        window.location.hash = ''; // not working in IE
            $location.url('/');
            document.loginForm.password.focus();
        };

        $scope.openContactUsDialog = function () {
            $rootScope.$broadcast('event:openContactUs');
        };

        $scope.changeLabelAccordingToPermission = function (permission, labelWithPermission, labelWithoutPermission) {
            return $rootScope.isFieldRequired(permission) ? labelWithoutPermission : labelWithPermission;
        };

        $scope.thisYear = new Date().getFullYear();
    }
]);