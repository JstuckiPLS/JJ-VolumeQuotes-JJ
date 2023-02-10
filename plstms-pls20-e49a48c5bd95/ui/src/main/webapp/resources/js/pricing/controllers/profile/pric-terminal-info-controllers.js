angular.module('plsApp').controller('TerminalInfoCtrl', ['$scope', 'PricingTerminalInfoService', 'TerminalInfoCopyFromService',
    function ($scope, PricingTerminalInfoService, TerminalInfoCopyFromService) {
        'use strict';

        $scope.loadTerminalInfo = function () {
            PricingTerminalInfoService.get({
                profileId: $scope.profileDetailId
            }, function (response) {
                $scope.terminalInfo = response;
            });
        };

        $scope.loadTerminalInfo();

        $scope.selectedRateToCopy = null;

        var cloneByProfileId = function () {
            TerminalInfoCopyFromService.copy({
                detail: $scope.profileDetailId,
                detailToCopy: $scope.selectedRateToCopy
            }, function () {
                $scope.loadTerminalInfo();
                $scope.$root.$emit('event:operation-success', 'Terminal data was successfully copied');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Terminal data copying failed!');
            });
        };

        $scope.okClick = function () {
            cloneByProfileId($scope.selectedRateToCopy);
        };

        $scope.openDialog = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirmation', confirmButtonLabel: 'Copy',
                message: 'Copying will override all current Terminal data created. Do you want to continue?', okFunction: $scope.okClick
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
            $scope.terminalInfo.profileId = $scope.profileDetailId;

            if (_.isNull($scope.terminalInfo.visible)) {
                $scope.terminalInfo.visible = false;
            }

            var success = function () {
                $scope.$root.$emit('event:operation-success', 'Terminal data successfully saved');
                $scope.loadTerminalInfo();
            };

            var failure = function () {
                $scope.$root.$emit('event:application-error', 'Terminal data save failed!');
            };

            PricingTerminalInfoService.save({profileId: $scope.profileDetailId}, $scope.terminalInfo, success, failure);
        };

        $scope.clear = function () {
            $scope.terminalInfo = {};
        };
    }
]);
