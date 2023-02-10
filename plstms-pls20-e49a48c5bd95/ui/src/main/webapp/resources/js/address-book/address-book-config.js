angular.module('addressBookServices', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/address-book', {redirectTo: '/address-book/address-book-list'})
            .when('/address-book/address-book-list', {templateUrl: 'pages/content/address-book/address-book-list.html'});
}]);