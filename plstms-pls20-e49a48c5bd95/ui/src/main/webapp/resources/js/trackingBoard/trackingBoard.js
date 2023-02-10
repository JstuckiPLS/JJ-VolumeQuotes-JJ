angular.module('trackingBoardServices', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/trackingBoard', {redirectTo: '/trackingBoard/alerts'})
            .when('/trackingBoard/alerts', {
                templateUrl: 'pages/content/trackingBoard/trackingBoard-tabs.html',
                boardTab: 'pages/content/trackingBoard/alerts.html'
            })
            .when('/trackingBoard/booked', {
                templateUrl: 'pages/content/trackingBoard/trackingBoard-tabs.html',
                boardTab: 'pages/content/trackingBoard/booked.html'
            })
            .when('/trackingBoard/undelivered', {
                templateUrl: 'pages/content/trackingBoard/trackingBoard-tabs.html',
                boardTab: 'pages/content/trackingBoard/undelivered.html'
            })
            .when('/trackingBoard/unbilled', {
                templateUrl: 'pages/content/trackingBoard/trackingBoard-tabs.html',
                boardTab: 'pages/content/trackingBoard/unbilled.html'
            })
            .when('/trackingBoard/open', {
                templateUrl: 'pages/content/trackingBoard/trackingBoard-tabs.html',
                boardTab: 'pages/content/trackingBoard/open.html'
            })
            .when('/trackingBoard/all', {
                templateUrl: 'pages/content/trackingBoard/trackingBoard-tabs.html',
                boardTab: 'pages/content/trackingBoard/all.html'
            })
            .when('/trackingBoard/hold', {
                templateUrl: 'pages/content/trackingBoard/trackingBoard-tabs.html',
                boardTab: 'pages/content/trackingBoard/hold.html'
            })
            .when('/trackingBoard/manualBol', {
                templateUrl: 'pages/content/trackingBoard/trackingBoard-tabs.html',
                boardTab: 'pages/content/trackingBoard/manualBol.html'
            });
}]);
