angular.module('myProfile.controllers', ['users.services']);

angular.module('myProfile', ['myProfile.controllers', 'ngRoute']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/my-profile', {redirectTo: '/my-profile/info'})
            .when('/my-profile/info', {
                templateUrl: 'pages/content/profile/my-profile-tabs.html',
                myProfileTab: 'pages/content/profile/view/view-my-profile.html',
                myProfileTabName: 'info'
            })
            .when('/my-profile/edit', {
                templateUrl: 'pages/content/profile/my-profile-tabs.html',
                myProfileTab: 'pages/content/profile/edit/edit-my-profile.html',
                myProfileTabName: 'edit'
            })
            .when('/my-profile/assigned-customers', {
                templateUrl: 'pages/content/profile/my-profile-tabs.html',
                myProfileTab: 'pages/content/profile/assigned-customers.html'
            })
            .when('/my-profile/announcement', {
                templateUrl: 'pages/content/profile/my-profile-tabs.html',
                myProfileTab: 'pages/content/profile/announcements-profile.html'
            });
}]);

