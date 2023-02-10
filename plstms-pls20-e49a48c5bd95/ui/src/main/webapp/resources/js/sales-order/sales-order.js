angular.module('salesOrderServices', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/sales-order', {redirectTo: '/sales-order/create'})
            .when('/sales-order/create', {templateUrl: 'pages/content/sales-order/create-sales-order.html'});
}]);
