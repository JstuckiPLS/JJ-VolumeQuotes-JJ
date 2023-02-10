angular.module('plsApp').controller('FreightBillCtrl', ['$scope', 'ThirdPartyInfoService', 'ThirdPartyCopyFromService',
    function ($scope, ThirdPartyInfoService, ThirdPartyCopyFromService) {
        'use strict';

        $scope.defaultModel = {
            profileDetailId: $scope.profileDetailId,
            contactName: 'LTL Accounts Payable Team',
            phone: {
                countryCode: '',
                areaCode: '',
                number: ''
            },
            email: 'ltlap@plslogistics.com'
        };

        $scope.loadThirdParty = function () {
            ThirdPartyInfoService.get({
                profileDetailId: $scope.profileDetailId
            }, function (response) {
                $scope.model = response;

                if (_.isUndefined(response.id)) {
                    $scope.model = angular.copy($scope.defaultModel);
                }
            });
        };

        $scope.loadThirdParty();

        $scope.selectedRateToCopy = null;

        var cloneByProfileId = function () {
            ThirdPartyCopyFromService.copy({
                detail: $scope.profileDetailId,
                detailToCopy: $scope.selectedRateToCopy
            }, function () {
                $scope.loadThirdParty();
                $scope.$root.$emit('event:operation-success', 'FreightBill Pay To Information successfully copied');
            }, function () {
                $scope.$root.$emit('event:application-error', 'FreightBill Pay To Information copying failed!');
            });
        };

        $scope.okClick = function () {
            cloneByProfileId($scope.selectedRateToCopy);
        };

        $scope.openDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', confirmButtonLabel: 'Copy', okFunction: $scope.okClick,
                message: 'Copying will override all current FreightBill Pay To information created. Do you want to continue?'
            });
        };

        $scope.CountryList = [{
            id: 'USA',
            name: 'United States of America'
        }, {
            id: 'MEX',
            name: 'Mexico'
        }, {
            id: 'CAN',
            name: 'Canada'
        }];

        $scope.save = function () {
            $scope.model.profileDetailId = $scope.profileDetailId;

            var success = function () {
                $scope.$root.$emit('event:operation-success', 'FreightBill Pay To Information successfully saved');
                $scope.loadThirdParty();
            };

            var failure = function () {
                $scope.$root.$emit('event:application-error', 'FreightBill Pay To Information save failed!');
            };

            ThirdPartyInfoService.save({profileDetailId: $scope.profileDetailId}, $scope.model, success, failure);
        };
    }
]);
