angular.module('pls.controllers').controller('LoadListController', ['$scope', 'SavedQuotesService', function ($scope, SavedQuotesService) {
     'use strict';

        $scope.showLoadList = false;

        $scope.$on('event:openLoadIdDialog', function (event, savedQuoteId) {
           SavedQuotesService.getListOfLoadIds({propositionId: savedQuoteId}, function (data) {
                if (data.result) {
                    $scope.loadList = data.result.join(", ");
                    $scope.loadListOptions = data.parentDialog;
                    $scope.showLoadList = true;
                } else {
                    $scope.$root.$emit('event:application-error', 'No Information on List of Loads associated with this saved quote!');
                }
            });
        });

    }
]);