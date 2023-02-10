angular.module('shipmentEntry').controller('ProductInformationController', ['$scope', 'ShipmentUtils', function ($scope, ShipmentUtils) {
    'use strict';

    $scope.shipmentEntryData.allPickupAccessorials = [];
    $scope.shipmentEntryData.allDeliveryAccessorials = [];
    $scope.shipmentEntryData.pickupLimitedAccess = undefined;
    $scope.shipmentEntryData.deliveryLimitedAccess = undefined;
    $scope.maxCountOfProducts = ShipmentUtils.MAX_COUNT_OF_PRODUCTS;

    $scope.rateQuoteDictionary = ShipmentUtils.getDictionaryValues();
    $scope.nonCommercials = $scope.rateQuoteDictionary.nonCommercials;

    $scope.originAndDestinationAreCanada = function() {
        return $scope.shipmentEntryData.shipment.originDetails.address.country &&
               $scope.shipmentEntryData.shipment.destinationDetails.address.country && 
               $scope.shipmentEntryData.shipment.originDetails.address.country.id === "CAN" &&
               $scope.shipmentEntryData.shipment.destinationDetails.address.country.id === "CAN";
    };

    $scope.$watch('accessorialTypes', function (accessorialTypes) {
        if (accessorialTypes) {
            $scope.shipmentEntryData.allPickupAccessorials.length = 0;
            $scope.shipmentEntryData.allDeliveryAccessorials.length = 0;
            $scope.shipmentEntryData.pickupLimitedAccess = undefined;
            $scope.shipmentEntryData.deliveryLimitedAccess = undefined;

            _.each(accessorialTypes, function (accessorialType) {
                if (accessorialType.applicableTo !== 'LTL') {
                    return;
                }

                if (accessorialType.accessorialGroup === 'PICKUP') {
                    if (accessorialType.id === 'LAP' || accessorialType.id === 'LAD') {
                        $scope.shipmentEntryData.pickupLimitedAccess = accessorialType.id;
                    } else {
                        $scope.shipmentEntryData.allPickupAccessorials.push(accessorialType.id);
                    }
                }

                if (accessorialType.accessorialGroup === 'DELIVERY') {
                    if (accessorialType.id === 'LAP' || accessorialType.id === 'LAD') {
                        $scope.shipmentEntryData.deliveryLimitedAccess = accessorialType.id;
                    } else {
                        $scope.shipmentEntryData.allDeliveryAccessorials.push(accessorialType.id);
                    }
                }
            });
        }
    }, true);

    var updateAccessorials = function (originAccessorials, destinationAccessorials) {
        $scope.$root.$broadcast('event:productChanged');
        $scope.activateRequote();

        if (originAccessorials && (_.contains(originAccessorials, 'REP') || _.contains(originAccessorials, 'RES'))
                && (_.contains(originAccessorials, 'LAP') || _.contains(originAccessorials, 'LAD'))) {
            $scope.shipmentEntryData.shipment.originDetails.accessorials = _.without(originAccessorials, 'LAP', 'LAD');
        }

        if (destinationAccessorials && (_.contains(destinationAccessorials, 'REP') || _.contains(destinationAccessorials, 'RES'))
                && (_.contains(destinationAccessorials, 'LAP') || _.contains(destinationAccessorials, 'LAD'))) {
            $scope.shipmentEntryData.shipment.destinationDetails.accessorials = _.without(destinationAccessorials, 'LAP', 'LAD');
        }
    };

    $scope.onClick = function () {
        updateAccessorials($scope.shipmentEntryData.shipment.originDetails.accessorials,
                $scope.shipmentEntryData.shipment.destinationDetails.accessorials);
    };
}]);