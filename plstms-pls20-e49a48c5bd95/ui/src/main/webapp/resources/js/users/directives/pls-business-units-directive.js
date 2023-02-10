angular.module('plsApp').directive('plsUserNetworks', ['NgGridPluginFactory', 'UserNetworksService',
    function (NgGridPluginFactory, UserNetworksService) {
        return {
            restrict: 'AE',
            scope: {
                userNetworkIds: '=',
                userInfoTab: '=',
                viewMode: '=',
                cleanBusinessUnits: '='
            },
            replace: true,
            templateUrl: 'pages/content/users/users/tpl/business-units-tpl.html',
            controller: ['$scope', function ($scope) {
                'use strict';

                $scope.businessUnitsModel = {
                    data: [],
                    options: {
                        data: "businessUnitsModel.data",
                        columnDefs: [{
                            field: 'enabled',
                            displayName: 'Enable',
                            cellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ' +
                            'data-ng-model="row.entity.enabled" data-ng-change="updateUserModel(row.entity)"/></div>',
                            width: '10%',
                            searchable: false,
                            headerClass: 'text-center',
                            cellClass: 'text-center'
                        }, {
                            field: 'name',
                            displayName: 'Business Units',
                            cellClass: 'text-center'
                        }],
                        plugins: [NgGridPluginFactory.plsGrid()],
                        enableColumnResize: false,
                        enableRowSelection: false
                    }
                };

                $scope.updateUserModel = function (network) {
                    if (network.enabled) {
                        $scope.userNetworkIds.push(network.id);
                    } else {
                        $scope.userNetworkIds = _.without($scope.userNetworkIds, network.id);
                    }
                };

                $scope.getActiveNetworks = function () {
                    UserNetworksService.activeNetworks({}, function (data) {
                        $scope.businessUnitsModel.data = data;
                        if ($scope.businessUnitsModel.data.length) {
                            _.each($scope.businessUnitsModel.data, function (network) {
                                network.enabled = _.contains($scope.userNetworkIds, network.id);
                            });
                        }
                    });
                };

                if (_.isUndefined($scope.viewMode)) {
                    $scope.getActiveNetworks();
                }

                $scope.$watch('userNetworkIds', function (newValue, oldValue) {
                    if (newValue && newValue !== oldValue) {
                        _.each($scope.businessUnitsModel.data, function (network) {
                            network.enabled = _.contains($scope.userNetworkIds, network.id);
                        });
                    }
                });

                $scope.$watch('cleanBusinessUnits', function () {
                    if ($scope.cleanBusinessUnits === true) {
                        $scope.businessUnitsModel.data = [];
                    } else if ($scope.cleanBusinessUnits === false) {
                        $scope.getActiveNetworks();
                    }
                });
            }]
        };
    }
]);