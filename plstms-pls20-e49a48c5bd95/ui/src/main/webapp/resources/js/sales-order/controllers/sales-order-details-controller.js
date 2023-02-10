angular.module('pls.controllers').controller('SODetailsCtrl', ['$scope', '$q', 'DateTimeUtils', 'isRequiredField',
    function ($scope, $q, DateTimeUtils, isRequiredField) {
        'use strict';

        $scope.invalidIdentifier = {};

        $scope.init = function () {
            if ($scope.wizardData.breadCrumbs && $scope.wizardData.breadCrumbs.map) {
                var stepObject = $scope.wizardData.breadCrumbs.map.details;

                stepObject.nextAction = function () {
                    if ($scope.wizardData.shipment.finishOrder.deliveryWindowFrom) {
                        $scope.wizardData.shipment.finishOrder.deliveryFrom =
                                DateTimeUtils.timeStringFromWindowObject($scope.wizardData.shipment.finishOrder.deliveryWindowFrom);
                    }

                    if ($scope.wizardData.shipment.finishOrder.deliveryWindowTo) {
                        $scope.wizardData.shipment.finishOrder.deliveryTo =
                                DateTimeUtils.timeStringFromWindowObject($scope.wizardData.shipment.finishOrder.deliveryWindowTo);
                    }

                    var deferred = $q.defer();
                    deferred.resolve();

                    return deferred.promise;
                };

                stepObject.validNext = $scope.validateForm;
            }
        };

        function isInvalidIdentifiers() {
            return _.some($scope.invalidIdentifier);
        }

        $scope.validateForm = function () {
            return !isInvalidIdentifiers();
        };
    }
]);