angular.module('shipmentEntry', ['ngRoute']).config(['$routeProvider', function ($routeProvider) {
    $routeProvider
            .when('/shipment-entry', {
                templateUrl: 'pages/content/shipment-entry/shipment-entry.html',
                controller: 'ShipmentEntryController as vm'
            })
            .when('/shipment-entry/:loadId', {
                templateUrl: 'pages/content/shipment-entry/shipment-entry.html',
                controller: 'ShipmentEntryController'
            });
}]);