angular.module('plsApp.directives').directive('plsPrepaidDetails', function () {
    return {
        restrict: 'A',
        scope: {
            prepaidDetails: '=plsPrepaidDetails',
            showDialog: '=',
            totalRevenue: '=?',
            parentDialogId: '@',
            mode: '@'
        },
        transclude: true,
        templateUrl: function (elem, attrs) {
            return attrs.mode === 'readonly' ? 'pages/tpl/prepaid/pls-prepaid-details.html' : 'pages/tpl/blank.html';
        },
        controller: ['$scope', '$http', '$templateCache', '$compile', function ($scope, $http, $templateCache, $compile) {
            'use strict';

            if ($scope.mode === 'editable' || $scope.mode === 'modal') {
                $http.get('pages/tpl/prepaid/pls-prepaid-dialog.html', {cache: $templateCache}).then(function (result) {
                    var dialog = $compile(result.data)($scope);
                    angular.element('#content').append(dialog);

                    $scope.$on('$destroy', function () {
                        dialog.remove();
                    });
                });

                $scope.dialogOptions = {
                    parentDialog: $scope.parentDialogId
                };
            }

            function unselectRows() {
                $scope.prepaidDetailsGrid.selectRow(0, false);
                $scope.prepaidDetailsGrid.selectRow(1, false);
            }

            $scope.prepaidDetailsBackup = $scope.prepaidDetails ? angular.copy($scope.prepaidDetails) : [];

            $scope.prepaidDetailsGrid = {
                data: 'prepaidDetails',
                multiSelect: false,
                enableRowSelection: $scope.mode === 'editable',
                enableColumnResize: true,
                enableSorting: false,
                columnDefs: [
                    {
                        field: 'paymentId',
                        displayName: 'Payment #',
                        width: $scope.mode === 'editable' ? '25%' : '30%',
                        editableCellTemplate: '<input type="text" ng-input="row.entity.paymentId" ng-model="row.entity.paymentId" maxlength="50" ' +
                        'required>'
                    }, {
                        field: 'date',
                        displayName: 'Date',
                        width: $scope.mode === 'editable' ? '50%' : '45%',
                        cellTemplate: 'pages/cellTemplate/prepaid-details-date-cell.html',
                        editableCellTemplate: 'pages/cellTemplate/prepaid-details-date-cell.html'
                    }, {
                        field: 'amount',
                        displayName: 'Amount',
                        width: '25%',
                        cellFilter: 'plsCurrency',
                        editableCellTemplate: '<input type="text" ng-input="row.entity.amount" ng-model="row.entity.amount" pls-number="cost" ' +
                        'required>'
                    }
                ]
            };

            $scope.$watch('prepaidDetailsGrid.ngGrid', function (newValue) {
                if (newValue) {
                    $scope.prepaidDetailsGrid.ngGrid.config.enableCellEdit = $scope.mode === 'editable';
                    $scope.prepaidDetailsGrid.ngGrid.buildColumns();
                }
            });

            $scope.addPrepaidDetails = function () {
                $scope.prepaidDetails.push({paymentId: '', date: '', amount: 0.00});
                $scope.prepaidDetails = angular.copy($scope.prepaidDetails);
                unselectRows();
            };

            $scope.removePrepaidDetails = function () {
                _.pull($scope.prepaidDetails, $scope.prepaidDetailsGrid.$gridScope.selectedItems[0]);
                $scope.prepaidDetails = angular.copy($scope.prepaidDetails);
                unselectRows();
            };

            function wasPrepayedEnough(prepaidDetails) {
                $scope.prepaidAmount = _.reduce(prepaidDetails, function(memo, item) {
                    return memo + item.amount;
                },0);
                return $scope.prepaidAmount >= $scope.totalRevenue;
            }

            $scope.saveDialog = function () {
                $scope.prepaidDetailsBackup = angular.copy($scope.prepaidDetails);
                unselectRows();
                $scope.showDialog = false;
                if (wasPrepayedEnough($scope.prepaidDetailsBackup)) {
                    $scope.$emit('event:wasPrepayedEnough');
                } else {
                    $scope.$emit('event:application-error', "Payment Amount is less than total Revenue and will remain in pending payment status");
                }
            };

            $scope.cancelDialog = function () {
                $scope.prepaidDetails = angular.copy($scope.prepaidDetailsBackup);
                unselectRows();
                $scope.showDialog = false;
            };

            $scope.$watch('prepaidDetails', function (newValue) {
                if (newValue) {
                    $scope.isWrongDetails = _.some(newValue, function (value) {
                        return _.isEmpty(value.paymentId) || _.isEmpty(value.date) || angular.isUndefined(value.amount);
                    });
                }
            }, true);
        }]
    };
});