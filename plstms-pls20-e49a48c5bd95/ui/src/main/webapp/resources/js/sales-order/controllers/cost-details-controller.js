angular.module('pls.controllers').controller('AddEditCostDetailsCtrl', ['$scope', function ($scope) {
    'use strict';

    $scope.addEditCostDetailsDialogOptions = {};

    $scope.editedCostDetail = {};
    $scope.accessorialTypes = [];
    $scope.isEdit = false;
    $scope.revenueOnlyTypeSelected = false;
    
    var revenueOnlyTypes = ['FVI'];

    $scope.cancelEditCostDetails = function () {
        $scope.editCostDetailsDialogVisible = false;
    };

    $scope.$on('event:showAddEditCostDetails', function (event, dialogDetails) {
        $scope.editCostDetailsDialogVisible = true;
        if (dialogDetails) {
            $scope.editedCostDetail = dialogDetails.editedCostDetail;
            $scope.isEdit = dialogDetails.isEdit;
            $scope.hideCost = dialogDetails.hideCost;
            $scope.addEditCostDetailsDialogOptions.parentDialog = dialogDetails.parentDialog;

            $scope.accessorialTypes = _.sortBy(dialogDetails.accessorialTypes, 'id');
            var SRA = _.findWhere(dialogDetails.accessorialTypes, {id: 'SRA'});

            if (SRA !== undefined) {
                $scope.accessorialTypes = _.reject($scope.accessorialTypes, function (accessorialType) {
                    return accessorialType.id === 'SRA';
                });

                $scope.accessorialTypes.unshift(SRA);
            }
            
            $scope.onTypeChange();
        }
    });

    $scope.saveCostDetails = function () {
        $scope.$root.$broadcast('event:saveCostDetails', $scope.editedCostDetail);
        $scope.cancelEditCostDetails();
    };
    
    $scope.onTypeChange = function () {
        if(revenueOnlyTypes.includes($scope.editedCostDetail.refType)){
            $scope.revenueOnlyTypeSelected = true;
            $scope.editedCostDetail.cost = 0;
        } else {
            $scope.revenueOnlyTypeSelected = false;
        }
    };
    
}]);