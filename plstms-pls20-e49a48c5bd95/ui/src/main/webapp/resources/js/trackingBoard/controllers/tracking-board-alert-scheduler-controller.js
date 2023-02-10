angular.module('pls.controllers').controller('TrackingBoardAlertsSchedulerController', ['$scope', 'TrackingBoardAlertService',
    function ($scope, TrackingBoardAlertService) {
        'use strict';

        $scope.alertsCount = 0;
        var ONE_MINUTE_MILLIS = 60 * 1000;

        $scope.initAlertsScheduler = function () {
            $scope.repeatCheckActions();
        };

        $scope.getAlertsCount = function () {
            TrackingBoardAlertService.count({}, function (data) {
                if (data) {
                    $scope.alertsCount = data.value;
                }
            });
        };

        $scope.repeatCheckActions = function () {
            $scope.alertTimerPromise = setInterval($scope.getAlertsCount(), ONE_MINUTE_MILLIS);
        };

        $scope.$on('$destroy', function () {
            if ($scope.alertTimerPromise) {
                clearInterval($scope.alertTimerPromise);
            }
        });

        $scope.$on('event:trackingBoardAlertsChanged', function () {
            $scope.alertsCount -= 1;
        });
    }
]);