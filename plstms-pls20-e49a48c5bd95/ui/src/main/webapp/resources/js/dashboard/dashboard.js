/**
 * dashboard module definition.
 */
angular.module('dashboard', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/dashboard', {
        templateUrl: 'pages/content/dashboard/dashboard.html'
    });
}]);