angular.module('users.controllers').controller('UserMgmtEditCtrl', ['$scope', '$route', '$window', 'UserDetailsService',
    'UserParentOrganizationsService', 'UserMgmtCheckUserIdService', 'UserAuthenticationService', 'UserDefaultContactService',
    function ($scope, $route, $window, UserDetailsService, UserParentOrganizationsService, UserMgmtCheckUserIdService, UserAuthenticationService,
              UserDefaultContactService) {
        'use strict';

        /**
         * true if this is new user. False if this is editing of existed user.
         */
        var createNew = $route.current.createNew === true;

        /**
         * Current user that should be displayed in details area. this is fill data
         * with organizations, permissions addresses etc.
         */
        $scope.user = undefined;

        /**
         * Flag indicating that entered login is already used by another user and though invalid.
         */
        $scope.userAlreadyExists = false;

        $scope.setCustomAdditionalInfo = function () {
            $scope.user.additionalInfo = {
                phone: {
                    countryCode: Number($scope.user.country.dialingCode)
                }
            };
        };

        $scope.updateSameAsUserProfileAdditionalInfo = function () {
            if ($scope.user.customerServiceContactInfoType === 'SAME_AS_USER_PROFILE') {
                $scope.user.additionalInfo = {
                    email: $scope.user.email,
                    phone: angular.copy($scope.user.phone)
                };

                if (!_.isUndefined($scope.user.firstName) && !_.isUndefined($scope.user.lastName)) {
                    $scope.user.additionalInfo.contactName = $scope.user.firstName + ' ' + $scope.user.lastName;
                } else {
                    $scope.user.additionalInfo.contactName = '';
                }
            }
        };

        $scope.setDefaultAdditionalInfo = function () {
            $scope.user.additionalInfo = $scope.user.defaultInfo;
        };

        $scope.isCustomContactInfoSelected = function () {
            return $scope.user.customerServiceContactInfoType === 'CUSTOM';
        };

        $scope.handleSave = function () {
            if ($scope.user.fax && !$scope.user.fax.number) {
                $scope.user.fax = undefined;
            }

            UserDetailsService.saveUser({}, $scope.user, function () {
                $scope.$emit('event:operation-success', "Success", "User has been saved successfully");
                $scope.$root.ignoreLocationChange();

                if ($scope.authData.personId === $scope.user.personId) {
                    UserAuthenticationService.reSetAuthData().then($scope.goHome);
                } else {
                    $scope.goHome();
                }
            }, function (error) {
                if(error && error.data && error.data.payload && error.data.payload.promoCode === "UNIQUE"){
                    $scope.$emit('event:application-error', "Promo Code already used!", "User with the same Promo Code already exists.");
                }
                $scope.$emit('event:application-error', "Failure", "User save failed!");
            });
        };

        $scope.goHome = function () {
            $window.history.back();
        };

        $scope.validateUserId = function () {
            UserMgmtCheckUserIdService.validate({
                "userId": $scope.user.userId,
                "personId": $scope.user.personId || null
            }, function (data) {
                if (data.result !== true && data.result !== "true") {
                    $scope.$emit('event:application-error', 'User already exists!', "User with the same User ID already exists.");
                    $scope.userAlreadyExists = true;
                } else {
                    $scope.userAlreadyExists = false;
                }
            });
        };

        $scope.findParentOrganization = function (criteria) {
            if (criteria && criteria.length > 2) {
                return UserParentOrganizationsService.getParentOrganizationsByName(criteria);
            }

            return {};
        };

        var zipAutoCompleteCountries = ['CAN', 'MEX', 'USA'];
        var previousCountry;

        $scope.$watch('user.country', function (newVal) {
            if (newVal) {
                $scope.zipAutoComplete = _.indexOf(zipAutoCompleteCountries, newVal.id) !== -1;

                if (previousCountry && newVal.id !== previousCountry.id) {
                    $scope.user.zip = undefined;
                    $scope.user.phone = {countryCode: newVal.dialingCode};
                    $scope.user.fax = {countryCode: newVal.dialingCode};

                    if ($scope.user.customerServiceContactInfoType === 'CUSTOM'
                            || $scope.user.customerServiceContactInfoType === 'SAME_AS_USER_PROFILE') {
                        $scope.user.additionalInfo.phone.countryCode = Number(newVal.dialingCode);
                    }
                }

                previousCountry = angular.copy(newVal);
            }
        });

        function checkAndFillUserData() {
            if (!$scope.user.country) {
                $scope.user.country = {id: 'USA', name: 'United States of America', dialingCode: '1'};
            } else {
                previousCountry = angular.copy($scope.user.country);
            }

            if (_.isEmpty($scope.user.phone) || !$scope.user.phone.countryCode) {
                $scope.user.phone = {
                    countryCode: Number($scope.user.country.dialingCode)
                };
            }

            if (_.isEmpty($scope.user.fax) || !$scope.user.fax.countryCode) {
                $scope.user.fax = {
                    countryCode: Number($scope.user.country.dialingCode)
                };
            }
            if (!$scope.user.promoCode && !$scope.user.discount) {
                $scope.user.discount = 10;
            }
        }

        // load user on startup
        if (!createNew && $route.current.params.personId) {
            UserDetailsService.getUser({"personId": $route.current.params.personId}, function (data) {
                $scope.user = data;
                $scope.user.permissions = $scope.user.permissions || [];
                $scope.user.roles = $scope.user.roles || [];
                $scope.user.customers = $scope.user.customers || [];
                checkAndFillUserData();
            }, function () {
                $scope.$emit('event:application-error', "User load failed!", "Unable to find user!");
            });
        } else {
            // Normally this is creation of prototype for new userData
            $scope.user = {
                customerServiceContactInfoType: 'SAME_AS_USER_PROFILE',
                customers: [],
                networkIds: [],
                permissions: [],
                roles: [],
                country: {id: 'USA', name: 'United States of America', dialingCode: '1'},
                phone: {countryCode: '1'},
                fax: {countryCode: '1'},
                discount: 10
            };

            if ($scope.authData.assignedOrganization) {
                $scope.user.parentOrganization = {
                    customer: true,
                    organizationId: $scope.authData.assignedOrganization.orgId,
                    organizationName: $scope.authData.assignedOrganization.name
                };
            }
            $scope.updateSameAsUserProfileAdditionalInfo();

            // load default contact information
            UserDefaultContactService.getDefaultContactInfo({}, function (data) {
                if (!_.isUndefined(data)) {
                    $scope.user.defaultInfo = data;
                }
            });
        }
    }
]);