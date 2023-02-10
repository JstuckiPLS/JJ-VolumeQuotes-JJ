angular.module('vendorBillServices', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/vendorBill', {redirectTo: '/vendorBill/unmatched'})
            .when('/vendorBill/unmatched', {templateUrl: 'pages/content/vendor-bill/vendor-bill-list.html', selectedTab: 'unmatched'})
            .when('/vendorBill/archived', {templateUrl: 'pages/content/vendor-bill/vendor-bill-list.html', selectedTab: 'archived'});
}]);
