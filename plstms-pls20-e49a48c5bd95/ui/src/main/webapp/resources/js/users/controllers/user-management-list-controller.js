angular.module('users.controllers').controller('UserMgmtListCtrl', ['$scope', '$route', 'UserStatusMgmtService', 'NgGridPluginFactory',
    'UserMgmtResetPasswordService', 'UserDetailsService', 'ShipmentUtils', 'RoleSearchService', 'PermissionSearchService', 'UserNetworksService',
    'UserSearchService',
    function ($scope, $route, UserStatusMgmtService, NgGridPluginFactory, UserMgmtResetPasswordService, UserDetailsService, ShipmentUtils,
              RoleSearchService, PermissionSearchService, UserNetworksService, UserSearchService) {
        'use strict';

        /**
         * true if controller should process active users. false to process
         * inactive.
         */
        $scope.activeTabSelected = $route.current.handleActiveUsers === true;

        /**
         * List of selected users. Normally should be only one record (because
         * multi-selection for table is disabled) or empty (when user does not click
         * by table yet).
         */
        $scope.selectedUsers = [];

        /**
         * List of user to display.
         */
        $scope.userInfoList = [];

        /**
         * Short info for current user. This is the selected object from users list.
         * undefined if none user selected.
         */
        $scope.selectedUser = undefined;

        /**
         * Available notifications types. field "value" is notification type and
         * field "label" is human readable label.
         */
        $scope.notifications = [];

        /**
         * Roles assigned to currently logged in user
         */
        $scope.allRoles = [];

        /**
         * Roles assigned to selected user and present in a list of roles assigned to currently logged in user.
         */
        $scope.selectedUserVisibleRoles = [];

        /**
         * Permissions assigned to currently logged in user directly or via any role.
         */
        $scope.allPermissions = [];

        $scope.additionalInfo = {};

        /**
         * Permissions assigned to selected user, but not present in his roles,
         * but present in a list of permissions assigned to currently logged in user directly or via any role.
         */
        $scope.selectedUserVisiblePermissions = [];

        $scope.searchName = 'ID';
        $scope.businessUnits = [];
        $scope.selectedBusinessUnit = '';
        $scope.company = '';
        $scope.searchValue = '';

        $scope.showResetPasswordUserDialog = false;
        $scope.showDeactivateUserDialog = false;
        $scope.showActivateUserDialog = false;

        $scope.searchUsers = function () {
            if ($scope.isValidSearchCriteria()) {
                UserSearchService.search({
                    status: $scope.activeTabSelected,
                    businessUnitId: $scope.selectedBusinessUnit >= 0 ? $scope.selectedBusinessUnit : '',
                    allBusinessUnits: $scope.selectedBusinessUnit === -1,
                    company: $scope.company,
                    searchName: $scope.searchName,
                    searchValue: $scope.searchValue
                }, function (data) {
                    $scope.userInfoList = data;
                });
            }
        };

        $scope.isValidSearchCriteria = function () {
            return (!_.isUndefined($scope.company) && $scope.company !== '') || (!_.isUndefined($scope.searchValue) && $scope.searchValue !== '') ||
                    $scope.selectedBusinessUnit !== -2;
        };

        $scope.resetPasswordForSelectedUser = function () {
            UserMgmtResetPasswordService.reset({
                "personId": $scope.selectedUser.personId
            });

            $scope.showResetPasswordUserDialog = false;
        };

        UserNetworksService.activeNetworks({}, function (data) {
            $scope.businessUnits.push({name: "", value: -2});

            if (data.length > 1) {
                $scope.businessUnits.push({name: "All", value: -1});
            }

            _.each(data, function (element) {
                $scope.businessUnits.push({name: element.name, value: element.id});
            });

            if ($scope.businessUnits.length) {
                $scope.selectedBusinessUnit = $scope.businessUnits[0].value;
            }

            $scope.showBusinessUnits = $scope.businessUnits.length > 1;
        });

        function updateSelectedUserVisibleRoles() {
            if ($scope.selectedUser !== undefined && _.isArray($scope.selectedUser.roles)) {
                $scope.selectedUserVisibleRoles = _.filter($scope.allRoles, function (num) {
                    return _.contains($scope.selectedUser.roles, num.id);
                });
            } else {
                $scope.selectedUserVisibleRoles = [];
            }
        }

        /**
         * User should see only those permissions that match all following criterias:
         *  - permission is assigned to currently logged in user directly or via some role
         *  - permission is not present in any role assigned to selected user
         */
        function getVisiblePermissionsForSelectedUser() {
            return _.filter($scope.allPermissions, function (permission) {
                return _.contains($scope.selectedUser.permissions, permission.id) && !_.find($scope.selectedUserVisibleRoles, function (role) {
                            return _.find(role.grpCapabilities, function (item) {
                                return item.capabilityId === permission.id && item.status === 'ACTIVE';
                            });
                        });
            });
        }

        function updateSelectedUserVisiblePermissions() {
            if ($scope.selectedUser !== undefined && _.isArray($scope.selectedUser.permissions)) {
                $scope.selectedUserVisiblePermissions = getVisiblePermissionsForSelectedUser();

                if ($scope.currentTab === 'additionalPermissions' && $scope.permissionsList.options.ngGrid) {
                    // because table with grouping column is not refreshed automatically when data is changed
                    $scope.permissionsList.options.ngGrid.rowFactory.filteredRowsChanged();
                }
            } else {
                $scope.selectedUserVisiblePermissions = [];
            }
        }

        $scope.clearSearchCriteria = function () {
            $scope.searchName = 'ID';
            $scope.selectedBusinessUnit = $scope.businessUnits[0].value;
            $scope.company = '';
            $scope.searchValue = '';
            $scope.userInfoList = [];
            $scope.selectedUser = undefined;
            $scope.isCleanBusinessUnits = true;
            updateSelectedUserVisibleRoles();
            updateSelectedUserVisiblePermissions();
        };

        /**
         * Method to display/refresh user details area.
         */
        function displayUserDetails() {
            $scope.selectedUser = undefined;
            $scope.selectedUserVisibleRoles = [];

            if ($scope.selectedUsers && $scope.selectedUsers.length) {
                UserDetailsService.getUser({personId: $scope.selectedUsers[0].personId}, function (data) {
                    $scope.selectedUser = data;

                    if (!_.isEmpty($scope.selectedUser.country) && !_.isEmpty($scope.selectedUser.phone) && !$scope.selectedUser.phone.countryCode) {
                        $scope.selectedUser.phone.countryCode = $scope.selectedUser.country.dialingCode;
                    }

                    if (!_.isEmpty($scope.selectedUser.country) && !_.isEmpty($scope.selectedUser.fax) && !$scope.selectedUser.fax.countryCode) {
                        $scope.selectedUser.fax.countryCode = $scope.selectedUser.country.dialingCode;
                    }

                    updateSelectedUserVisibleRoles();
                    updateSelectedUserVisiblePermissions();

                    $scope.isCleanBusinessUnits = false;
                });
            }
        }

        $scope.activateSelectedUser = function (activate) {
            var personId = $scope.selectedUser.personId;

            UserStatusMgmtService[activate ? 'activate' : 'deactivate']({"personId": personId}, function () {
                $scope.userInfoList = _.reject($scope.userInfoList, function (user) {
                    return user.personId === personId;
                });

                $scope.selectedUsers.length = 0;
                displayUserDetails();
            });

            $scope.showActivateUserDialog = false;
            $scope.showDeactivateUserDialog = false;
        };

        $scope.usersList = {
            options: {
                data: "userInfoList",
                selectedItems: $scope.selectedUsers,
                columnDefs: [{
                    field: "userId",
                    displayName: "User ID",
                    width: "16%"
                }, {
                    field: "fullName",
                    displayName: "User Name",
                    width: "30%"
                }, {
                    field: "email",
                    displayName: "Email",
                    width: "30%"
                }, {
                    field: "parentOrgName",
                    displayName: "Company",
                    width: "20%",
                    visible: $scope.authData.plsUser
                }],
                action: function () {
                    $scope.handleEditBtn();
                },
                afterSelectionChange: function () {
                    displayUserDetails();
                },
                plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin()],
                enableColumnResize: true,
                multiSelect: false,
                progressiveSearch: true
            }
        };

        /**
         * Helper function to check that selected customer user has specified notification type.
         *
         * @param notificationType
         *            Not null notification type.
         * @returns <code>true</code> if customer user is selected and it has specified notification.
         *          <code>false</code> otherwise.
         */
        $scope.hasNotification = function (notificationType) {
            var result = false;

            if ($scope.selectedUser && $scope.selectedUser.customers && $scope.selectedUser.customers.length
                    && $scope.selectedUser.customers[0].notifications && $scope.selectedUser.customers[0].notifications.length) {
                result = _.contains($scope.selectedUser.customers[0].notifications, notificationType);
            }

            return result;
        };

        $scope.roleList = {
            options: {
                data: "selectedUserVisibleRoles",
                columnDefs: [{
                    field: 'name',
                    displayName: 'Roles'
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

        $scope.permissionsList = {
            options: {
                data: "selectedUserVisiblePermissions",
                columnDefs: [{
                    field: "category",
                    displayName: "Category",
                    width: 0
                }, {
                    field: "description",
                    displayName: 'Additional Permissions',
                    width: '95%'
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
            }
        };

        $scope.$watch(function () {
            return !ShipmentUtils.getDictionaryValues().notificationTypes ? undefined : ShipmentUtils.getDictionaryValues().notificationTypes.length;
        }, function () {
            $scope.notifications = angular.copy(ShipmentUtils.getDictionaryValues().notificationTypes);
        });

        RoleSearchService.getAllRoles(function (data) {
            $scope.allRoles = data;
        });

        PermissionSearchService.getAllPermissions({}, function (data) {
            $scope.allPermissions = [];

            _.each(data, function (value) {
                value.category = value.category.description;
                $scope.allPermissions.push(value);
            });

            $scope.allPermissions = _.sortBy($scope.allPermissions, function (permission) {
                return permission.category + '|||' + permission.description;
            });

            updateSelectedUserVisiblePermissions();
        });
    }
]);