angular.module('financialBoardServices', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/financialBoard', {redirectTo: '/financialBoard/transactional'})
            .when('/financialBoard/transactional', {
                templateUrl: 'pages/content/financialBoard/financialBoard-tabs.html',
                financialTab: 'pages/content/financialBoard/transactional.html'
            })
            .when('/financialBoard/consolidated', {
                templateUrl: 'pages/content/financialBoard/financialBoard-tabs.html',
                financialTab: 'pages/content/financialBoard/consolidated.html'
            })
            .when('/financialBoard/audit', {
                templateUrl: 'pages/content/financialBoard/financialBoard-tabs.html',
                financialTab: 'pages/content/financialBoard/audit.html'
            })
            .when('/financialBoard/errors', {
                templateUrl: 'pages/content/financialBoard/financialBoard-tabs.html',
                financialTab: 'pages/content/financialBoard/errors.html'
            })
            .when('/financialBoard/history', {
                templateUrl: 'pages/content/financialBoard/financialBoard-tabs.html',
                financialTab: 'pages/content/financialBoard/history.html'
            })
            .when('/financialBoard/price', {
                templateUrl: 'pages/content/financialBoard/financialBoard-tabs.html',
                financialTab: 'pages/content/financialBoard/price.html'
            });
}]);