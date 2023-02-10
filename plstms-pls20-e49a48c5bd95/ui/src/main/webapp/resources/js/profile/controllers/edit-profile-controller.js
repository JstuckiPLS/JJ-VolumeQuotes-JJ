angular.module('myProfile.controllers').controller('EditProfileCtrl', ['$scope', '$location', 'UserDetailsService', 'UserAuthenticationService',
    function ($scope, $location, UserDetailsService, UserAuthenticationService) {
        'use strict';

        var previousCountry;

        UserDetailsService.getUser({
            personId: $scope.authData.personId
        }, function (data) {
            $scope.user = data;
            $scope.user.permissions = $scope.user.permissions || [];
            $scope.user.roles = $scope.user.roles || [];
            $scope.user.customers = $scope.user.customers || [];

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
        });

        var zipAutoCompleteCountries = ['CAN', 'MEX', 'USA'];

        $scope.$watch('user.country', function (newVal) {
            if (newVal) {
                $scope.zipAutoComplete = _.indexOf(zipAutoCompleteCountries, newVal.id) !== -1;

                if (previousCountry && newVal.id !== previousCountry.id) {
                    $scope.user.zip = undefined;
                    $scope.user.phone = {countryCode: newVal.dialingCode};
                    $scope.user.fax = {countryCode: newVal.dialingCode};

                    if ($scope.user.customerServiceContactInfoType === 'CUSTOM') {
                        $scope.user.additionalInfo.phone.countryCode = Number(newVal.dialingCode);
                    }
                }

                previousCountry = angular.copy(newVal);
            }
        });

        $scope.saveUser = function () {
            if ($scope.user.fax && !$scope.user.fax.number) {
                $scope.user.fax = undefined;
            }

            UserDetailsService.saveUser({}, $scope.user, function (data) {
                $scope.$emit('event:operation-success', "Success", "User has been saved successfully");

                UserAuthenticationService.reSetAuthData().then(function () {
                    $scope.$root.ignoreLocationChange();
                    $location.path('/my-profile');
                });
            }, function () {
                $scope.$emit('event:application-error', "Failure", "User save failed!");
            });
        };

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

        $scope.setSameAsUserProfileAdditionalInfo = function () {
            $scope.user.additionalInfo = {};

            if (!_.isUndefined($scope.user.firstName) && !_.isUndefined($scope.user.lastName)) {
                $scope.user.additionalInfo.contactName = $scope.user.firstName + ' ' + $scope.user.lastName;
            } else {
                $scope.user.additionalInfo.contactName = '';
            }

            $scope.user.additionalInfo.email = $scope.user.email;
            $scope.user.additionalInfo.phone = angular.copy($scope.user.phone);
        };

        $scope.setDefaultAdditionalInfo = function () {
            $scope.user.additionalInfo = $scope.user.defaultInfo;
        };

        $scope.customContactInfoSelected = function () {
            return $scope.user.customerServiceContactInfoType === 'CUSTOM';
        };
    }
]);