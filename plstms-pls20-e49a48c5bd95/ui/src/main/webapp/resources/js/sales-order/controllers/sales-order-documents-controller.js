angular.module('pls.controllers').controller('SODocsCtrl', ['$scope', '$q', 'ShipmentDocumentService',
    'urlConfig', 'ShipmentSavingService', 'DateTimeUtils', 'BillToDocumentService', 'ShipmentDocumentEmailService',
    function ($scope, $q, ShipmentDocumentService, urlConfig, ShipmentSavingService,
              DateTimeUtils, BillToDocumentService, ShipmentDocumentEmailService) {
        'use strict';

        function sendEmailWithErrorDetails() {
            var wizardCopy = _.clone($scope.wizardData);
            delete wizardCopy.breadCrumbs;
            ShipmentDocumentEmailService.emailDoc({
                recipients: 'aleshchenko@plslogistics.com',
                subject: 'Wrong Bill To',
                content: 'Create Sales Order: ' + JSON.stringify(wizardCopy),
                loadId: -1
            });
        }

        $scope.init = function () {
            var stepObject;
            if ($scope.wizardData.breadCrumbs && $scope.wizardData.breadCrumbs.map && $scope.wizardData.step) {
                stepObject = $scope.wizardData.breadCrumbs.map[$scope.wizardData.step];

                stepObject.doneAction = function () {
                    angular.forEach($scope.wizardData.shipment.finishOrder.quoteMaterials, function (quoteMaterial) {
                        quoteMaterial.pickupDate = $scope.wizardData.shipment.finishOrder.pickupDate;
                    });

                    var deferred = $q.defer();

                    if (!$scope.wizardData.shipment.status) {
                        $scope.wizardData.shipment.status = 'BOOKED';
                    }
                    if ($scope.wizardData.paymentMethod === 'PREPAID_ONLY') {
                        $scope.wizardData.shipment.status = 'PENDING_PAYMENT';
                    }

                    ShipmentSavingService.save({
                        customerId: $scope.wizardData.selectedCustomer.id,
                        hideCreatedTime: $scope.wizardData.hideCreatedTime
                    }, $scope.wizardData.shipment, function (shipment) {
                        $scope.$root.$emit('event:operation-success', 'Save sales order',
                                'Sales order was successfully saved.<br/>Load ID: ' + shipment.id);
                        $scope.wizardData.shipment = shipment;

                        if (shipment.billTo.paymentMethod === 'PREPAID_ONLY') {
                            var data = {
                                customerId : shipment.organizationId,
                                locationId : shipment.location.id,
                                creditCardEmail : shipment.billTo.creditCardEmail,
                                loadId : shipment.id,
                                bolNumber : shipment.bolNumber
                            };
                            $scope.$root.$broadcast('event:showPaymentDialog', {
                                data : data,
                                closeHandler : function() {
                                    deferred.resolve();
                                }
                            });
                        } else {
                            deferred.resolve();
                        }

                    }, function (response) {
                        deferred.reject();
                        var errorMessage = 'Error during saving sales order.';

                        if (response && response.data && response.data.payload && response.data.payload.billToOrganization) {
                            errorMessage = "Unexpected error occurred. Please refresh page and try again.";
                            sendEmailWithErrorDetails();
                        } else if (response && response.data && response.data.message) {
                            errorMessage = 'Error during saving sales order due to ' + response.data.message;
                        }

                        $scope.$root.$emit('event:application-error', 'Error on sales order save!', errorMessage);
                    });

                    return deferred.promise;
                };
            } else {
                stepObject = $scope.wizardData.breadCrumbs.map.docs;
            }
        };
    }
]);