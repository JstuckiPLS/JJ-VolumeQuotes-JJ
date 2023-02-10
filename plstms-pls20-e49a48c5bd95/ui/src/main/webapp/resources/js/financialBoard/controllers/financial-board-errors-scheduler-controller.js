angular.module('pls.controllers').controller('FinancialBoardErrorsSchedulerController', ['$scope', 'FinancialBoardInvoiceErrorsService',
    function ($scope, FinancialBoardInvoiceErrorsService) {
        'use strict';

        var ONE_MINUTE_MILLIS = 60 * 1000;

        $scope.errorsCount = 0;

        $scope.initErrorsScheduler = function () {
            $scope.repeatCheckErrorsActions();
        };

        $scope.getErrorsCount = function () {
            FinancialBoardInvoiceErrorsService.errorsCount({}, function (data) {
                if (data) {
                    $scope.errorsCount = data.value;
                }
            });
        };

        $scope.repeatCheckErrorsActions = function () {
            $scope.errorsCountTimerPromise = setInterval($scope.getErrorsCount(), ONE_MINUTE_MILLIS);
        };

        $scope.$on('$destroy', function () {
            if ($scope.errorsCountTimerPromise) {
                clearInterval($scope.errorsCountTimerPromise);
            }
        });

        $scope.$on('event:financialBoardErrorsChanged', function () {
            $scope.getErrorsCount();
        });
    }
]);