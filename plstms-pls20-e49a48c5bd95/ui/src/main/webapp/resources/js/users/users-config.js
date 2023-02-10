angular.module('users.services', ['ngResource', 'ngRoute']);
angular.module('users.utils', []);
angular.module('users.controllers', ['users.services', 'roles.services', 'plsApp.directives.services', 'plsApp.utils', 'users.utils']);
angular.module('roles.services', []);
angular.module('roles.controllers', ['roles.services']);
angular.module('announcements.services', []);
angular.module('announcements.controllers', ['announcements.services']);

angular.module('users', ['users.controllers','roles.controllers','announcements.controllers']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/user-mgmt', {redirectTo: '/user-mgmt/users/active'})
            .when('/user-mgmt/users', {redirectTo: '/user-mgmt/users/active'})
            .when('/user-mgmt/users/active', {
                templateUrl: 'pages/content/users/user-mgmt-tabs.html',
                userMgmtTab: 'pages/content/users/users/user-tabs.html',
                usersTab: 'pages/content/users/users/users-list.html',
                handleActiveUsers: true
            })
            .when('/user-mgmt/users/inactive', {
                templateUrl: 'pages/content/users/user-mgmt-tabs.html',
                userMgmtTab: 'pages/content/users/users/user-tabs.html',
                usersTab: 'pages/content/users/users/users-list.html',
                handleActiveUsers: false
            })
            .when('/user-mgmt/users/edit/:personId', {
                templateUrl: 'pages/content/users/user-mgmt-tabs.html',
                userMgmtTab: 'pages/content/users/users/user-edit-page.html',
                createNew: false
            })
            .when('/user-mgmt/users/add', {
                templateUrl: 'pages/content/users/user-mgmt-tabs.html',
                userMgmtTab: 'pages/content/users/users/user-edit-page.html',
                createNew: true
            })
            .when('/user-mgmt/roles', {redirectTo: '/user-mgmt/roles/permissions'})
            .when('/user-mgmt/roles/permissions', {
                templateUrl: 'pages/content/users/user-mgmt-tabs.html',
                userMgmtTab: 'pages/content/users/roles/role-mgmt-tabs.html',
                roleMgmtTab: 'pages/content/users/roles/permission-search.html'
            })
            .when('/user-mgmt/roles/roles', {
                templateUrl: 'pages/content/users/user-mgmt-tabs.html',
                userMgmtTab: 'pages/content/users/roles/role-mgmt-tabs.html',
                roleMgmtTab: 'pages/content/users/roles/role-search.html'
            })
            .when('/user-mgmt/announcements', {redirectTo: '/user-mgmt/announcements/unpublished'})
            .when('/user-mgmt/announcements/unpublished', {
                templateUrl: 'pages/content/users/user-mgmt-tabs.html',
                userMgmtTab: 'pages/content/users/announcements/announcement-mgmt-tabs.html',
                announcementMgmtTab: 'pages/content/users/announcements/unpublished.html'
            })
            .when('/user-mgmt/announcements/published', {
                templateUrl: 'pages/content/users/user-mgmt-tabs.html',
                userMgmtTab: 'pages/content/users/announcements/announcement-mgmt-tabs.html',
                announcementMgmtTab: 'pages/content/users/announcements/published.html'
            });
}]);

