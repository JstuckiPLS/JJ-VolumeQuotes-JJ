angular.module('roles.controllers').controller('PermissionSearchCtrl', [
    '$scope', 'PermissionSearchService', 'PermissionSpecificService', 'NgGridPluginFactory', 'UserAuthenticationService',
    function ($scope, PermissionSearchService, PermissionSpecificService, NgGridPluginFactory, UserAuthenticationService) {
        'use strict';

        $scope.searchText = '';
        $scope.allUsersSelected = false;
        $scope.selectedPermission = [];

        var unassignCriteria = {};

        unassignCriteria.users = [];
        $scope.unassignCriteria = unassignCriteria;

        $scope.usersGrid = {
            data: 'usersGridData',
            columnDefs: [{
                field: 'name',
                displayName: 'User Name',
                width: '30%'
            }, {
                field: 'userid',
                displayName: 'User ID',
                width: '20%',
                searchable: true
            }, {
                field: 'personId',
                displayName: 'Person ID',
                width: '10%'
            }, {
                field: 'roles',
                displayName: 'Role',
                width: '30%'
            }, {
                field: 'remove',
                displayName: 'Remove',
                cellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ' +
                'data-ng-disabled="row.entity.directPermission != \'Y\'" data-ng-model="row.entity.remove" ' +
                'data-ng-click="setUserSelected($event, row.entity.personId)"/></div>',
                headerCellTemplate: '<div class="ngHeaderSortColumn {{col.headerClass}}" data-ng-style="{\'cursor\': col.cursor}" '
                + 'ng-class="{ \'ngSorted\': !noSortVisible }"><div data-ng-class="\'colt\' + col.index" class="ngHeaderText">'
                + '<input tabindex="-1" class="ngSelectionCheckbox" type="checkbox"  data-ng-checked="allUsersSelected" ' +
                'data-ng-click="setAllUsersSelected($event)"/> {{col.displayName}}</div></div>',
                width: '10%',
                searchable: false
            }],
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            afterRefresh: function () {
                $scope.resetSelectedUsers();
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            enableColumnResize: true,
            multiSelect: false,
            progressiveSearch: true
        };

        $scope.permissionsGrid = {
            data: 'filteredPermissions',
            selectedItems: $scope.selectedPermission,
            columnDefs: [{
                field: 'category.description',
                displayName: 'Category',
                width: '0%'
            }, {
                field: 'description',
                displayName: 'Permission',
                width: '100%',
                searchable: true
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            multiSelect: false,
            groups: ["category.description"],
            groupsCollapsedByDefault: false
        };

        function searchPermissions() {
            var nameToFind = $scope.searchText ? $scope.searchText.toLowerCase() : '';

            $scope.filteredPermissions = _.filter($scope.permissions, function (permission) {
                return !nameToFind || permission.category.description.toLowerCase().indexOf(nameToFind) !== -1
                        || permission.description.toLowerCase().indexOf(nameToFind) !== -1
                        || permission.name === $scope.searchText; // search by permission internal name for developers
            });
        }

        $scope.filterPermissions = function () {
            searchPermissions();
        };

        $scope.getUsersWithPermission = function () {
            PermissionSpecificService.getUsersWithPermission({
                pathDefineParam: $scope.selectedPermission[0].id
            }, function (data) {
                $scope.usersGridData = data;
                $scope.allUsersSelected = false;
            });
        };

        $scope.getAllPermissions = function () {
            PermissionSearchService.getAllPermissions({}, function (data) {
                $scope.permissions = _.sortBy(data, function (permission) {
                    return permission.category.description + '|||' + permission.description;
                });

                searchPermissions();

                $scope.selectedPermission.length = 0;
                $scope.usersGridData = [];
                $scope.allUsersSelected = false;
            });
        };

        $scope.loadPermission = function () {
            if ($scope.selectedPermission[0]) {
                $scope.unassignCriteria.id = $scope.selectedPermission[0].id;
                $scope.unassignCriteria.users = [];

                $scope.getUsersWithPermission();
            }
        };

        $scope.setUserSelected = function ($event, id) {
            var checkbox = $event.target;

            if (checkbox.checked) {
                $scope.unassignCriteria.users.push(id);
            } else {
                $scope.unassignCriteria.users.splice($scope.unassignCriteria.users.indexOf(id), 1);
            }
        };

        $scope.setAllUsersSelected = function ($event) {
            var checkbox = $event.target;
            $scope.unassignCriteria.users = [];

            if (checkbox.checked) {
                $scope.allUsersSelected = true;

                $.each($scope.usersGridData, function (key, value) {
                    if (value.directPermission === 'Y') {
                        value.remove = true;
                        $scope.unassignCriteria.users.push(value.personId);
                    }
                });
            } else {
                $scope.resetSelectedUsers();
            }
        };

        $scope.unassignUsers = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirm', confirmButtonLabel: 'Delete',
                okFunction: $scope.confirmRemoveDialog.confirmDelete,
                message: 'If the user has permission assigned through a role, user will still continue to have the permission.'
            });
        };

        $scope.resetSelectedUsers = function () {
            $scope.unassignCriteria.users = [];
            $scope.allUsersSelected = false;

            if ($scope.usersGridData) {
                $.each($scope.usersGridData, function (key, value) {
                    value.remove = false;
                });
            }

        };

        $scope.confirmRemoveDialog = {
            confirmDelete: function () {
                PermissionSpecificService.unassignUsers({
                    pathDefineParam: $scope.selectedPermission[0].id
                }, unassignCriteria, function () {
                    $scope.$root.$emit('event:operation-success', 'Remove Permission', 'Permission successfully removed for selected users.');
                    UserAuthenticationService.reSetAuthData();
                    $scope.getAllPermissions();
                    $scope.unassignCriteria.users = [];
                }, function () {
                });
            }
        };

        $scope.getAllPermissions();
    }
]);

angular.module('roles.controllers').controller('RoleSearchCtrl', [
    '$scope', 'RoleSearchService', 'RoleSpecificService', 'NgGridPluginFactory', 'UserAuthenticationService',
    function ($scope, RoleSearchService, RoleSpecificService, NgGridPluginFactory, UserAuthenticationService) {
        'use strict';

        $scope.selectedRole = {};
        $scope.allUsersSelected = false;
        $scope.searchText = '';
        $scope.selectedItems = [];

        var unassignCriteria = {};

        unassignCriteria.users = [];
        $scope.unassignCriteria = unassignCriteria;

        $scope.usersGrid = {
            data: 'usersGridData',
            columnDefs: [{
                field: 'name',
                displayName: 'User Name',
                width: '45%'
            }, {
                field: 'userid',
                displayName: 'User ID',
                width: '25%',
                searchable: true
            }, {
                field: 'personId',
                displayName: 'Person ID',
                width: '15%'
            }, {
                field: 'remove',
                displayName: 'Remove',
                cellTemplate: '<div class="ngSelectionCell"><input tabindex="-1" class="ngSelectionCheckbox" type="checkbox" ' +
                'data-ng-model="row.entity.remove" data-ng-click="setUserSelected($event, row.entity.personId)"/></div>',
                headerCellTemplate: '<div class="ngHeaderSortColumn {{col.headerClass}}" data-ng-style="{\'cursor\': col.cursor}" '
                + 'ng-class="{ \'ngSorted\': !noSortVisible }"><div data-ng-class="\'colt\' + col.index" class="ngHeaderText">'
                + '<input tabindex="-1" class="ngSelectionCheckbox" type="checkbox"  data-ng-checked="allUsersSelected" ' +
                'data-ng-click="setAllUsersSelected($event)"/> {{col.displayName}}</div></div>',

                width: '15%',
                searchable: false
            }],
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            afterRefresh: function () {
                $scope.resetSelectedUsers();
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin()],
            enableColumnResize: true,
            progressiveSearch: true,
            multiSelect: false
        };

        $scope.rolesGrid = {
            data: 'roles',
            selectedItems: $scope.selectedItems,
            columnDefs: [{
                field: 'name',
                displayName: 'Roles',
                width: '100%'
            }],
            multiSelect: false,
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.roleDetailsGrid = {
            data: 'selectedRole.capabilities',
            columnDefs: [{
                field: 'category.description',
                displayName: 'Category',
                width: '0%'
            }, {
                field: 'description',
                displayName: 'Permission',
                width: '100%',
                searchable: true
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            groups: ["category.description"]
        };

        $scope.filterRoles = function () {
            $scope.rolesGrid.filterOptions.filterText = 'name:' + $scope.searchText;
        };

        $scope.getUsersWithRole = function () {
            RoleSpecificService.getUsersWithRole({pathDefineParam: $scope.selectedRole.id}, function (data) {
                $scope.usersGridData = data;
                $scope.allUsersSelected = false;
            });
        };

        $scope.getAllRoles = function () {
            RoleSearchService.getAllRoles({}, function (data) {
                $scope.roles = data;
                $scope.selectedRole = {};
                $scope.selectedItems.length = 0;
                $scope.usersGridData = [];
                $scope.allUsersSelected = false;
            });
        };

        $scope.loadRole = function () {
            RoleSpecificService.getRole({pathDefineParam: $scope.selectedItems[0].id}, function (data) {
                $scope.selectedRole = data;

                $scope.selectedRole.capabilities = _.sortBy($scope.selectedRole.capabilities, function (permission) {
                    return permission.category.description + '|||' + permission.description;
                });

                unassignCriteria.id = data.id;
                $scope.unassignCriteria.users = [];

                $scope.getUsersWithRole();
            });
        };

        $scope.setUserSelected = function ($event, id) {
            var checkbox = $event.target;

            if (checkbox.checked) {
                $scope.unassignCriteria.users.push(id);
            } else {
                $scope.unassignCriteria.users.splice($scope.unassignCriteria.users.indexOf(id), 1);
            }
        };

        $scope.setAllUsersSelected = function ($event) {
            var checkbox = $event.target;
            $scope.unassignCriteria.users = [];

            if (checkbox.checked) {
                $scope.allUsersSelected = true;

                $.each($scope.usersGridData, function (key, value) {
                    value.remove = true;
                    $scope.unassignCriteria.users.push(value.personId);
                });
            } else {
                $scope.resetSelectedUsers();
            }
        };

        $scope.unassignUsers = function () {
            RoleSpecificService.unassignUsers({pathDefineParam: $scope.selectedRole.id}, unassignCriteria, function () {
                $scope.$root.$emit('event:operation-success', 'Remove Role', 'Role successfully removed for selected users.');
                UserAuthenticationService.reSetAuthData();
                $scope.getAllRoles();
                $scope.unassignCriteria.users = [];
            });
        };

        $scope.resetSelectedUsers = function () {
            $scope.unassignCriteria.users = [];
            $scope.allUsersSelected = false;

            if ($scope.usersGridData) {
                $.each($scope.usersGridData, function (key, value) {
                    value.remove = false;
                });
            }
        };

        $scope.getAllRoles();

        $scope.deleteRoleDialog = {
            confirmDelete: function () {
                RoleSpecificService.deleteRole({pathDefineParam: $scope.selectedRole.id}, function () {
                    UserAuthenticationService.reSetAuthData();
                    $scope.getAllRoles();
                    $scope.unassignCriteria.users = [];
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Role delete failed!', 'Can\'t delete role with ID :' + $scope.selectedRole.id);
                });
            }
        };

        function openAddEditRoleDialog(role) {
            var transferObject = {
                role: role, closeHandler: function () {
                    $scope.getAllRoles();
                }
            };

            $scope.$broadcast('event:showAddEditRole', transferObject);
        }

        $scope.addRole = function () {
            openAddEditRoleDialog();
        };

        $scope.editRole = function () {
            if ($scope.selectedRole && $scope.selectedRole.id) {
                openAddEditRoleDialog($scope.selectedRole);
            }
        };

        $scope.deleteRole = function () {
            $scope.$root.$broadcast('event:showConfirmation', {
                caption: 'Confirm Delete Role', confirmButtonLabel: 'Delete',
                okFunction: $scope.deleteRoleDialog.confirmDelete,
                message: 'Role will be removed from all users upon confirmation of Delete.<br/>Confirm Delete?'
            });
        };
    }
]);

angular.module('roles.controllers').controller('AddEditRoleCtrl', ['$scope', 'RoleSpecificService', 'PermissionSpecificService',
    'PermissionSearchService', 'UserAuthenticationService', 'NgGridPluginFactory',
    function ($scope, RoleSpecificService, PermissionSpecificService, PermissionSearchService, UserAuthenticationService, NgGridPluginFactory) {
        'use strict';

        $scope.permissionsToAdd = [];
        $scope.permissionsToRemove = [];
        $scope.search = {};

        $scope.editRoleModel = {
            showEditRole: false,
            role: {}
        };

        function sortPermissions(permissions) {
            return _.sortBy(permissions, function (permission) {
                return permission.category.description + '|||' + permission.description;
            });
        }

        $scope.closeEditRoleDialog = function () {
            $scope.searchText = undefined;
            $scope.editRoleModel.showEditRole = false;
            $scope.permissionsToAdd.length = 0;
            $scope.permissionsToRemove.length = 0;
            $scope.filteredRolePermissions.length = 0;
        };

        $scope.loadRole = function (role) {
            RoleSpecificService.getRole({pathDefineParam: role}, function (data) {
                $scope.editRoleModel.role = data;
                $scope.editRoleModel.role.capabilities = sortPermissions($scope.editRoleModel.role.capabilities);
                $scope.filteredRolePermissions = $scope.editRoleModel.role.capabilities;
            });
        };

        $scope.allPermissionsGrid = {
            data: 'filteredAllPermissions',
            selectedItems: $scope.permissionsToAdd,
            columnDefs: [{
                field: 'category.description',
                displayName: 'Category',
                width: '0%'
            }, {
                field: 'description',
                displayName: 'Available Permissions',
                width: '100%',
                searchable: true,
                headerCellTemplate: '<div class="ngHeaderSortColumn {{col.headerClass}}" data-ng-style="{\'cursor\': col.cursor}" '
                + 'ng-class="{ \'ngSorted\': !noSortVisible }"><div data-ng-class="\'colt\' + col.index" class="ngHeaderText">'
                + '{{col.displayName}}</div></div>'
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            multiSelect: true,
            groups: ["category.description"],
            groupsCollapsedByDefault: false
        };

        $scope.rolePermissionsGrid = {
            data: 'filteredRolePermissions',
            selectedItems: $scope.permissionsToRemove,
            columnDefs: [{
                field: 'category.description',
                displayName: 'Category',
                width: '0%'
            }, {
                field: 'description',
                displayName: 'Added Permissions',
                width: '92%',
                searchable: true,
                headerCellTemplate: '<div class="ngHeaderSortColumn {{col.headerClass}}" data-ng-style="{\'cursor\': col.cursor}" '
                + 'ng-class="{ \'ngSorted\': !noSortVisible }"><div data-ng-class="\'colt\' + col.index" class="ngHeaderText">'
                + '{{col.displayName}}</div></div>'
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            multiSelect: true,
            groups: ["category.description"],
            groupsCollapsedByDefault: false
        };

        $scope.getAvailablePermissions = function (id) {
            PermissionSpecificService.getAvailablePermissions({pathDefineParam: id}, function (data) {
                $scope.allPermissions = sortPermissions(data);
                $scope.filteredAllPermissions = $scope.allPermissions;
            });
        };

        $scope.getAllPermissions = function () {
            PermissionSearchService.getAllPermissions({}, function (data) {
                $scope.allPermissions = sortPermissions(data);
                $scope.filteredAllPermissions = $scope.allPermissions;
            });
        };

        $scope.$on('event:showAddEditRole', function (event, transferObject) {
            $scope.search = {};
            $scope.allPermissions = [];
            $scope.editRoleModel.role = {};

            $scope.editRoleModel.closeHandler = transferObject.closeHandler;

            if (transferObject.role) {
                $scope.loadRole(transferObject.role.id);
                $scope.getAvailablePermissions(transferObject.role.id);
            } else {
                $scope.editRoleModel.role.capabilities = [];
                $scope.getAllPermissions();
            }

            $scope.editRoleModel.showEditRole = true;
        });

        function searchPermissions() {
            var nameToFind = $scope.searchText ? $scope.searchText.toLowerCase() : '';

            $scope.filteredRolePermissions = _.filter($scope.editRoleModel.role.capabilities, function (permission) {
                return !nameToFind || permission.category.description.toLowerCase().indexOf(nameToFind) !== -1 ||
                        permission.description.toLowerCase().indexOf(nameToFind) !== -1;
            });

            $scope.filteredAllPermissions = _.filter($scope.allPermissions, function (permission) {
                return !nameToFind || permission.category.description.toLowerCase().indexOf(nameToFind) !== -1 ||
                        permission.description.toLowerCase().indexOf(nameToFind) !== -1;
            });
        }

        $scope.removePermissions = function () {
            $scope.togglePermissions($scope.editRoleModel.role.capabilities, $scope.allPermissions, $scope.permissionsToRemove);
            $scope.permissionsToRemove.length = 0;
            $scope.allPermissions = sortPermissions($scope.allPermissions);
            searchPermissions();
        };

        $scope.addPermissions = function () {
            $scope.togglePermissions($scope.allPermissions, $scope.editRoleModel.role.capabilities, $scope.permissionsToAdd);
            $scope.permissionsToAdd.length = 0;
            $scope.editRoleModel.role.capabilities = sortPermissions($scope.editRoleModel.role.capabilities);
            searchPermissions();
        };

        $scope.togglePermissions = function (from, to, permissions) {
            $.each(permissions, function (key, permission) {
                from.splice(from.indexOf(permission), 1);

                to.push(permission);
            });
        };

        $scope.saveRole = function () {
            RoleSpecificService.isNameUnique({
                pathDefineParam: $scope.editRoleModel.role.name,
                excludeGroup: $scope.editRoleModel.role.id ? $scope.editRoleModel.role.id : -1
            }, function (response) {
                if (response.data) {
                    $scope.prepareForSave();

                    RoleSpecificService.saveRole({}, $scope.editRoleModel.role, $scope.saveSuccessful, function () {
                        $scope.$root.$emit('event:application-error', 'Role save failed!', 'Can\'t save Role');
                    });
                } else {
                    $scope.$root.$emit('event:application-error', 'Role validation failed!',
                            'The Role with the same name was created by other user. Role name must be unique.');
                }
            });
        };

        $scope.prepareForSave = function () {
            $scope.editRoleModel.role.grpCapabilities = [];

            $.each($scope.editRoleModel.role.capabilities, function (key, permission) {
                var grpCapability = {};
                grpCapability.groupId = $scope.editRoleModel.role.id;
                grpCapability.capabilityId = permission.id;
                $scope.editRoleModel.role.grpCapabilities.push(grpCapability);
            });

            $scope.editRoleModel.role.capabilities = [];
        };

        $scope.saveSuccessful = function () {
            UserAuthenticationService.reSetAuthData();
            $scope.closeEditRoleDialog();

            if ($scope.editRoleModel.closeHandler) {
                $scope.editRoleModel.closeHandler();
            }

            $scope.$root.$emit('event:operation-success', 'Save Role', 'Role successfully saved.');
        };

        $scope.searchPermissions = function () {
            searchPermissions();
        };
    }
]);