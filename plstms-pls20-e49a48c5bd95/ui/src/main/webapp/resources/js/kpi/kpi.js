/**
 * kpi module definition.
 */
angular.module('kpiServices', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/kpi', {
        templateUrl: 'pages/content/kpi/kpi-tabs.html'
    });
}]);