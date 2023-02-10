/**
 * Customer module definition.
 */

angular.module('editCustomer', []);

angular.module('customer', ['ngResource', 'editCustomer']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/customer', {redirectTo: '/customer/active'})
            .when('/customer/active', {templateUrl: 'pages/content/customer/customer-list.html', customerListTab: 'active'})
            .when('/customer/inactive', {templateUrl: 'pages/content/customer/customer-list.html', customerListTab: 'inactive'})
            .when('/customer/hold', {templateUrl: 'pages/content/customer/customer-list.html', customerListTab: 'hold'})
            .when('/customer/:customerId', {redirectTo: '/customer/:customerId/profile'})
            .when('/customer/:customerId/profile', {
                templateUrl: 'pages/content/customer/edit/edit-customer.html', editCustomerTabName: 'profile',
                editCustomerTab: 'pages/content/customer/edit/edit-customer-profile.html'
            })
            .when('/customer/:customerId/billTo', {
                templateUrl: 'pages/content/customer/edit/edit-customer.html', editCustomerTabName: 'billTo',
                editCustomerTab: 'pages/content/customer/edit/edit-customer-billto.html'
            })
            .when('/customer/:customerId/billTo/:billToId', {
                templateUrl: 'pages/content/customer/edit/edit-customer.html', editCustomerTabName: 'billTo',
                editCustomerTab: 'pages/content/customer/edit/edit-customer-billto.html'
            })
            .when('/customer/:customerId/locations', {
                templateUrl: 'pages/content/customer/edit/edit-customer.html', editCustomerTabName: 'locations',
                editCustomerTab: 'pages/content/customer/edit/edit-customer-locations.html'
            })
            .when('/customer/:customerId/notes', {
                templateUrl: 'pages/content/customer/edit/edit-customer.html', editCustomerTabName: 'notes',
                editCustomerTab: 'pages/content/customer/edit/edit-customer-notes.html'
            });
}]);