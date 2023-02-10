angular.module('admin', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/admin', {redirectTo: '/admin/logs'})
            .when('/admin/logs', {templateUrl: 'pages/content/admin/admin-list.html', selectedTab: 'logs'});
}]);