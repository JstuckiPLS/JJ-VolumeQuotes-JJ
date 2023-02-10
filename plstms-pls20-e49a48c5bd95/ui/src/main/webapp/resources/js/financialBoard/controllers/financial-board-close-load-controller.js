angular.module('pls.controllers').controller('FinancialBoardCloseLoadController',
        [ '$scope', 'ShipmentOperationsService', function($scope, ShipmentOperationsService) {
            'use strict';

    $scope.isCloseLoadBoxVisible = false;
    $scope.loadId = undefined;
    $scope.note = undefined;

    $scope.cancelCloseLoad = function () {
        $scope.loadId = undefined;
        $scope.note = undefined;
        $scope.isCloseLoadBoxVisible = false;
    };

    $scope.$on('event:closeLoad', function (event, options) {
        $scope.loadId = options.loadId;
        $scope.isCloseLoadBoxVisible = true;
    });

    $scope.closeLoad = function () {
        $scope.isCloseLoadBoxVisible = false;

        ShipmentOperationsService.closeLoad({
            shipmentId: $scope.loadId,
            note: $scope.note,
            customerId: -1
        }, function () {
            $scope.$emit('event:updateDataAfterSendToAudit');
            $scope.cancelCloseLoad();
        }, function () {
            $scope.$root.$emit('event:application-error', 'Close Load failure!', 'Close Load has failed!');
        });
    };

    $scope.showCloseLoadBox = function () {
        $scope.isCloseLoadBoxVisible = true;
    };
}]);