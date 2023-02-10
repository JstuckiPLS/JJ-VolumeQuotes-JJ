/**
 * Customer activity board module definition.
 */
angular.module('customerActivityServices', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/customer-activity', {templateUrl: 'pages/content/customer-activity/customer-activity-board.html'});
}]);

//TODO remove this functionality


