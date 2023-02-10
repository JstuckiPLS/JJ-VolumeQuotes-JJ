angular.module('quotes.services', ['ngResource', 'ngRoute']);

angular.module('quotesServices', ['ngResource', 'quotes.services']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/quotes', {redirectTo: '/quotes/quote'})
            .when('/quotes/quote', {
                templateUrl: 'pages/content/quotes/quotes-tabs.html',
                quoteTab: 'pages/content/quotes/quote-wizard.html'
            })
            .when('/quotes/saved', {
                templateUrl: 'pages/content/quotes/quotes-tabs.html',
                quoteTab: 'pages/content/quotes/saved-quotes.html'
            })
            .when('/quotes/active', {
                templateUrl: 'pages/content/quotes/quotes-tabs.html',
                quoteTab: 'pages/content/quotes/active-shipments.html'
            })
            .when('/quotes/advanced', {
                templateUrl: 'pages/content/quotes/quotes-tabs.html',
                quoteTab: 'pages/content/quotes/advanced-search.html'
            })
            .when('/quotes/quote:param', {
                templateUrl: 'pages/content/quotes/quotes-tabs.html',
                quoteTab: 'pages/content/quotes/rate-quotes.html'
            })
            .when('/account-history', {redirectTo: '/account-history/search'})
            .when('/account-history/search', {
                templateUrl: 'pages/content/account/account-history.html',
                accountHistoryTab: 'pages/content/account/account-history-search.html'
            })
            .when('/account-history/calendar', {
                templateUrl: 'pages/content/account/account-history.html',
                accountHistoryTab: 'pages/content/account/account-calendar.html'
            })
            .otherwise({
                redirectTo: function (params, url) {
                    var result = new RegExp('/[^/]+').exec(url);
                    return result ? result[0] : url;
                }
            });
}]);
