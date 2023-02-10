angular.module('termsAndConditions', ['ngResource']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/termsAndConditions', {redirectTo: '/termsAndConditions'})
            .when('/termsAndConditions', {templateUrl: 'pages/content/terms-and-conditions/terms-and-conditions.html'});
}]);