/**
 * JOB Number Identifier - controller
 *
 * @Vitaliy Gavrilyuk
 */
angular.module('plsApp.directives').directive('plsJobNumbers', ['NgGridPluginFactory', '$timeout', function (NgGridPluginFactory, $timeout) {
    return {
        restrict: 'A',
        replace: false,
        controller: ['$scope', function ($scope) {
            'use strict';

            var local = {
                selectedJobs: []
            };

            $scope.jobGrid = {
                data: 'shipment.' + $scope.plsIdentifierDetails.field,
                selectedItems: local.selectedJobs,
                enableRowSelection: true,
                enableSorting: false,
                multiSelect: false,
                columnDefs: [{
                    field: 'jobNumber',
                    displayName: 'Job#',
                    editableCellTemplate: '<input type="text" ng-input="row.entity.jobNumber" ng-model="row.entity.jobNumber" '
                        + 'pls-job-number-input data-model="shipment.' + $scope.plsIdentifierDetails.field
                        + '" maxlength="{{getMaxFieldLength()}}" data-regexp="{{ruleExp}}" data-start-with="{{startWith}}" '
                        + 'data-end-with="{{endWith}}" data-customer-id="{{customerId}}" required>',
                    width: '90%'
                }],
                plugins: [NgGridPluginFactory.actionPlugin()]
            };

            /* Set cell editing config for jobGrid */
            $scope.$watch('jobGrid.ngGrid', function (newValue) {
                if (newValue) {
                    $scope.jobGrid.ngGrid.config.enableCellEdit = !$scope.disabledIdentifier;
                    $scope.jobGrid.ngGrid.buildColumns();
                }
            });

            $scope.$watch('disabledIdentifier', function (newValue) {
                if ($scope.jobGrid.ngGrid) {
                    $scope.jobGrid.ngGrid.config.enableCellEdit = !newValue;
                    $scope.jobGrid.ngGrid.buildColumns();
                }
            });

            $scope.addJob = function () {
                _.result($scope.shipment, $scope.plsIdentifierDetails.field).push({jobNumber: ''});

                $timeout(function () {
                    var grid = $scope.jobGrid.ngGrid;
                    grid.$viewport.find('[ng-row]:last').find('[ng-cell-has-focus]').dblclick();
                    grid.$viewport.scrollTop(((_.result($scope.shipment, $scope.plsIdentifierDetails.field).length - 1) * grid.config.rowHeight));
                }, 0);
            };

            $scope.removeJobs = function () {
                var jobs = _.result($scope.shipment, $scope.plsIdentifierDetails.field);
                _.set($scope.shipment, $scope.plsIdentifierDetails.field, _.difference(jobs, local.selectedJobs));
            };
        }]
    };
}]);