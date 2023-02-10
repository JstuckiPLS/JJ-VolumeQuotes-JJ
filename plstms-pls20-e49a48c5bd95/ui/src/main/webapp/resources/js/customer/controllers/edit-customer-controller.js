angular.module('editCustomer').controller('EditCustomerCtrl', [
    '$scope', '$routeParams', '$route', '$cookies', 'CustomerLabelResource', 'ShipmentUtils',
    function ($scope, $routeParams, $route, $cookies, CustomerLabelResource, ShipmentUtils) {
        'use strict';

        $scope.editCustomerModel = {
            customerId: $routeParams.customerId,
            editCustomerPreviousTab: $cookies.editCustomerPreviousTab
        };

        if ($route.current.editCustomerTabName !== 'profile') {
            new CustomerLabelResource().$get({
                customerId: $scope.editCustomerModel.customerId
            }, function (data) {
                $scope.editCustomerModel.customerName = data.name;
                $scope.editCustomerModel.emailAccountExecutive = ShipmentUtils.isEmailAccountExecutive(data.customerNetworkId);
            });
        }
    }
]);