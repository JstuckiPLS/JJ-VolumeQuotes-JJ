angular.module('invoicesServices', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/invoices', {redirectTo: '/invoices/invoices'})
            .when('/invoices/invoices', {
                templateUrl: 'pages/content/invoices/invoices-tabs.html',
                invoicesTab: 'pages/content/invoices/invoices.html'
            })
            .when('/invoices/credit-billing', {
                templateUrl: 'pages/content/invoices/invoices-tabs.html',
                invoicesTab: 'pages/content/invoices/credit-billing.html'
            });
}]);
