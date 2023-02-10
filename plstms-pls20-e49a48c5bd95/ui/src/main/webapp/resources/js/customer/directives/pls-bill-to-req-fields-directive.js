/**
 * Show Grid with Bill To Required Fields
 *
 * @author Vitaliy Gavrilyuk
 */
angular.module('plsApp').directive('plsBillToReqFields', ['ShipmentUtils', 'NgGridPluginFactory', 'DefaultReqFieldService',
    function (ShipmentUtils, NgGridPluginFactory, DefaultReqFieldService) {
        return {
            restrict: 'A',
            scope: {
                billTo: '=',
                customerId: '=',
                parentDialogId: '@'
            },
            templateUrl: 'pages/content/customer/billTo/templates/billto-req-fields-tpl.html',
            controller: ['$scope', function ($scope) {
                'use strict';

                $scope.selectedReqField = [];

                ShipmentUtils.getDictionaryValues().billToRequiredField.$promise.then(function (allRequiredFields) {
                    // this value is used in child scope (in add/edit popup)
                    if ($scope.$root.isBrandOrAluma($scope.customerId)) {
                        allRequiredFields = _.filter(allRequiredFields, function(field) {
                            return field.value !== 'JOB' && field.value !== 'GL';
                        });
                    }
                    $scope.identifiers = allRequiredFields;

                    if ($scope.billTo && $scope.billTo.billToRequiredFields.length < $scope.identifiers.length) {
                        $scope.billTo.billToRequiredFields = DefaultReqFieldService.getRequiredFields(allRequiredFields,
                                $scope.billTo.billToRequiredFields);
                    }

                    $scope.billToRequiredFields = angular.copy($scope.billTo.billToRequiredFields);
                });

                $scope.isNotEditable = function() {
                    return !$scope.selectedReqField || !$scope.selectedReqField[0]
                            || ($scope.$root.isBrandOrAluma($scope.customerId)
                                    && ($scope.selectedReqField[0].name === 'JOB' || $scope.selectedReqField[0].name === 'GL'));
                };

                $scope.reqFieldsGrid = {
                    data: 'billToRequiredFields',
                    multiSelect: false,
                    enableRowSelection: true,
                    enableColumnResize: true,
                    enableSorting: false,
                    selectedItems: $scope.selectedReqField,
                    columnDefs: [
                        {
                            field: 'name',
                            displayName: 'Identifier',
                            width: '10%',
                            cellFilter: 'billToIdentifierNames'
                        }, {
                            field: 'required',
                            displayName: 'Required',
                            width: '10%',
                            cellTemplate: 'pages/cellTemplate/checked-cell.html'
                        }, {
                            field: 'inboundOutbound',
                            displayName: 'Inbound/Outbound',
                            width: '12%',
                            cellClass: 'text-center',
                            cellFilter: 'shipmentDirection'
                        }, {
                            field: 'address',
                            displayName: 'Address',
                            width: '20%',
                            cellFilter: 'billToReqFieldsAddress'
                        }, {
                            field: 'originDestination',
                            displayName: 'Origin/Destination',
                            width: '12%',
                            cellClass: 'text-center',
                            cellFilter: 'directionType'
                        }, {
                            field: 'defaultValue',
                            displayName: 'Default Value',
                            cellClass: 'text-center',
                            width: '20%'
                        }, {
                            field: 'self',
                            displayName: 'Start/End with',
                            width: '16%',
                            cellClass: 'text-center',
                            cellFilter: 'billToReqFieldsRules'
                        }
                    ],
                    sortInfo: {
                        fields: ['name'],
                        directions: ['asc']
                    },
                    action: function () {
                        $scope.editReqField();
                    },
                    plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin()]
                };

                $scope.$watch('selectedReqField[0]', function (newValue) {
                    $scope.isDeletable = newValue && _.filter($scope.billTo.billToRequiredFields, {'name': $scope.selectedReqField[0].name})[1];
                });

                $scope.addReqField = function () {
                    $scope.reqFieldModel = {
                        inboundOutbound: 'B',
                        originDestination: 'B',
                        address: {}
                    };

                    $scope.reqFieldsGrid.selectedItems.length = 0;
                    $scope.reqFieldsGrid.selectVisible(false);
                    $scope.showDialog = true;
                };

                $scope.editReqField = function () {
                    $scope.reqFieldModel = angular.copy($scope.selectedReqField[0]);
                    $scope.showDialog = true;
                };

                $scope.deleteReqField = function () {
                    var index = _.findIndex($scope.billTo.billToRequiredFields, function (value) {
                        return value.id === $scope.selectedReqField[0].id;
                    });

                    $scope.billTo.billToRequiredFields.splice(index, 1);
                    $scope.billToRequiredFields.splice(index, 1);
                    delete $scope.selectedReqField[0];
                };
            }]
        };
    }
]);