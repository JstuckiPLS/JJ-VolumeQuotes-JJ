angular.module('plsApp').directive('plsRolesAndAdditionalPermissions', ['$q', 'RoleSearchService', 'PermissionSearchService', 'NgGridPluginFactory',
    function ($q, RoleSearchService, PermissionSearchService, NgGridPluginFactory) {
        return {
            restrict: 'A',
            scope: {
                userPermissions: '=',
                userInfoTab: '=',
                userRoles: '=',
                viewMode: '='
            },
            replace: true,
            templateUrl: 'pages/content/users/users/tpl/edit-roles-and-permissions-tpl.html',
            controller: ['$scope', function ($scope) {
                'use strict';

                var allPermissions = [];

                $scope.permissionsModel = {
                    additionalPermissions: []
                };

                $scope.rolesModel = {
                    allRoles: [],
                    options: {
                        data: "rolesModel.allRoles",
                        columnDefs: [{
                            field: 'name',
                            displayName: 'Role'
                        }, {
                            field: 'enabled',
                            displayName: 'Enable',
                            cellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ' +
                            'data-ng-model="row.entity.enabled" data-ng-change="handleRoleSelection(row.entity)"/></div>',
                            width: '15%',
                            searchable: false
                        }],
                        filterOptions: {
                            filterText: "",
                            useExternalFilter: false
                        },
                        plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
                        enableColumnResize: false,
                        useExternalSorting: false,
                        multiSelect: false,
                        progressiveSearch: true
                    }
                };

                $scope.permissionsModel.options = {
                    data: "permissionsModel.additionalPermissions",
                    columnDefs: [{
                        field: "category",
                        displayName: "Category",
                        width: 0
                    }, {
                        field: "description",
                        displayName: 'Permission',
                        width: '74%'
                    }, {
                        field: 'enabled',
                        displayName: 'Enable',
                        cellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ' +
                        'data-ng-model="row.entity.enabled" data-ng-change="updateUserPermissions()"/></div>',
                        width: '15%',
                        searchable: false
                    }],
                    filterOptions: {
                        filterText: "",
                        useExternalFilter: false
                    },
                    plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
                    enableColumnResize: false,
                    useExternalSorting: false,
                    multiSelect: false,
                    progressiveSearch: true,
                    groups: ["category"],
                    showGroupPanel: false
                };

                $scope.updateUserPermissions = function () {
                    $scope.userPermissions.length = 0;
                    
                    _.each($scope.permissionsModel.additionalPermissions, function (value) {
                        if (value.enabled === true) {
                            $scope.userPermissions.push(value.id);
                        }
                    });
                };

                function updateSelectionOfUserRoles() {
                    _.each($scope.rolesModel.allRoles, function (role) {
                        role.enabled = _.isArray($scope.userRoles) && _.contains($scope.userRoles, role.id);
                    });
                }

                function updateSelectionOfAdditionalPermissionsForUser() {
                    _.each($scope.permissionsModel.additionalPermissions, function (value) {
                        value.enabled = _.isArray($scope.userPermissions) && _.contains($scope.userPermissions, value.id);
                    });
                }

                function selectRole(role) {
                    // remove permissions present in the selected role from additionalPermissions
                    var grpCapabilities = _.filter(role.grpCapabilities, function (capabilitie) {
                        return capabilitie.status === 'ACTIVE';});
                    
                    $scope.permissionsModel.additionalPermissions = _.filter($scope.permissionsModel.additionalPermissions, function (permission) {
                        return !_.findWhere(grpCapabilities, {capabilityId: permission.id});
                    });
                }

                function updateListOfAdditionalPermissionsBasedOnRoles() {
                    if ($scope.permissionsModel.additionalPermissions.length && $scope.rolesModel.allRoles.length) {
                        _.each($scope.rolesModel.allRoles, function (role) {
                            if (role.enabled) {
                                // update permissions in the table
                                selectRole(role);
                            }
                        });
                    }
                }

                var rolesPromise = RoleSearchService.getAllRoles(function (data) {
                    $scope.rolesModel.allRoles = data;
                });

                var permissionsPromise = PermissionSearchService.getAllPermissions(function (data) {
                    _.each(data, function (permission) {
                        permission.category = permission.category.description;
                        $scope.permissionsModel.additionalPermissions.push(permission);
                        allPermissions.push(permission);
                    });
                });

                function sortAdditionalPermissions() {
                    $scope.permissionsModel.additionalPermissions = _.sortBy($scope.permissionsModel.additionalPermissions, function (permission) {
                        return permission.category + '|||' + permission.description;
                    });
                }

                $q.all([rolesPromise.$promise, permissionsPromise.$promise]).then(function () {
                    setTimeout(function () {
                        updateSelectionOfUserRoles();
                        updateSelectionOfAdditionalPermissionsForUser();
                        updateListOfAdditionalPermissionsBasedOnRoles();
                        sortAdditionalPermissions();
                    });
                });

                function unselectRole(role) {
                    // find permissions in this role
                    var permissions = _.filter(allPermissions, function (permission) {
                        return _.findWhere(role.grpCapabilities, {capabilityId: permission.id});
                    });

                    // remove permissions that are already present at additional permissions
                    if ($scope.permissionsModel.additionalPermissions.length) {
                        permissions = _.filter(permissions, function (permission) {
                            return !_.findWhere($scope.permissionsModel.additionalPermissions, {id: permission.id});
                        });
                    }

                    // remove permissions that are present in other selected roles
                    permissions = _.filter(permissions, function (permission) {
                        return !_.find($scope.rolesModel.allRoles, function (role) {
                            return role.enabled && _.findWhere(role.grpCapabilities, {capabilityId: permission.id});
                        });
                    });

                    // add permissions
                    $scope.permissionsModel.additionalPermissions = _.union($scope.permissionsModel.additionalPermissions, permissions);
                    sortAdditionalPermissions();
                }

                $scope.handleRoleSelection = function (role) {
                    if (role.enabled) {
                        selectRole(role);
                        $scope.userRoles.push(role.id);
                    } else {
                        unselectRole(role);
                        var index = $scope.userRoles.indexOf(role.id);
                        $scope.userRoles.splice(index, 1);
                    }
                    
                    $scope.updateUserPermissions();
                };
            }]
        };
    }
]);