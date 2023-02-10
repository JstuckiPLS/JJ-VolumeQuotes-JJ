angular.module('productsServices', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/products', {redirectTo: '/products/products-list'})
            .when('/products/products-list', {templateUrl: 'pages/content/products/products-list.html'});
}]);
