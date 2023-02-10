angular.module('reports', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/reports', {templateUrl: 'pages/content/report/reports.html'});
}]);