angular.module('manualBol.controllers').controller('ManualBolDetailsController', ['$scope', 'manualBolModel', 'isRequiredField',
    function ($scope, manualBolModel, isRequiredField) {
        'use strict';

        $scope.$root.ignoreLocationChangeFlag = false;
        $scope.invalidIdentifier = {};

        $scope.detailsPage = {
            next: '/manual-bol/docs',
            previous: '/manual-bol/addresses',
            bolModel: manualBolModel
        };

        $scope.canNextStep = function () {
            return !_.some($scope.invalidIdentifier);
        };
    }
]);