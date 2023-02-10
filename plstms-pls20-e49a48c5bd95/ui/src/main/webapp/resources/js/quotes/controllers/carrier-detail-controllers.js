angular.module('pls.controllers').controller('CarrierDetailsController', ['$scope', 'CostDetailsUtils', 'ShipmentUtils',
                                                                          function ($scope, CostDetailsUtils, ShipmentUtils) {
    'use strict';

    $scope.showCarrierDetails = false;
    $scope.carrierDetailsModel = {};

    $scope.closeCarrierDetailsDialog = function () {
        $scope.showCarrierDetails = false;
    };

    $scope.$on('event:openCarrierDetailsForPreparedCriteria', function (event, data, materials) {
        $scope.carrierDetails = data;
        $scope.carrierDetailsModel.parentDialog = data.parentDialog;

        $scope.getAccessorialsExcludingFuel = CostDetailsUtils.getAccessorialsExcludingFuel($scope.carrierDetails.origProposal, 'C',
                $scope.carrierDetails.guaranteedBy);

        $scope.getFuelSurcharge = CostDetailsUtils.getFuelSurcharge;
        $scope.getBaseRate = CostDetailsUtils.getBaseRate;
        $scope.accessorialTotal = 0;

        angular.forEach($scope.getAccessorialsExcludingFuel, function (accessorial, index) {
            $scope.accessorialTotal = $scope.accessorialTotal + accessorial.subTotal;
        });

        $scope.basePriceFromSmc3 = $scope.carrierDetails.origProposal.pricingDetails.totalChargeFromSmc3;

        if ($scope.carrierDetails.origProposal.pricingDetails.deficitChargeFromSmc3) {
            $scope.basePriceFromSmc3 = $scope.basePriceFromSmc3
                    + $scope.carrierDetails.origProposal.pricingDetails.deficitChargeFromSmc3;
        }

        if ($scope.carrierDetails.origProposal.pricingDetails.smc3MinimumCharge
                && $scope.basePriceFromSmc3 < $scope.carrierDetails.origProposal.pricingDetails.smc3MinimumCharge) {
            $scope.basePriceFromSmc3 = $scope.carrierDetails.origProposal.pricingDetails.smc3MinimumCharge;
        }

        ShipmentUtils.fillNmfcAndQtyFields($scope.carrierDetails.origProposal.pricingDetails, materials);

        $scope.showCarrierDetails = true;
    });

    $scope.getMoveId = function() {
        var details = $scope.carrierDetails.origProposal.pricingDetails;
        if (details.sellProfileDetailId && details.sellProfileDetailId !== details.buyProfileDetailId) {
            return details.buyProfileDetailId + ' / ' + details.sellProfileDetailId;
        }
        return details.buyProfileDetailId;
    };
}]);
