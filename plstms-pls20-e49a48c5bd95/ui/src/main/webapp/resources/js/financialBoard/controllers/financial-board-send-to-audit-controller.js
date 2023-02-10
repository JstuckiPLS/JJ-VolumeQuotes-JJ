angular.module('pls.controllers').controller('FinancialBoardSendToAuditController', ['$scope', 'FinancialBoardService', 'ShipmentDictionaryService',
    function ($scope, FinancialBoardService, ShipmentDictionaryService) {
        'use strict';

        $scope.sendToAuditVisible = false;
        $scope.auditReasonsParam = [];
        $scope.allAuditReasonsData = [];

        $scope.auditReason = {
            code: undefined,
            note: undefined
        };

        $scope.loadId = undefined;
        $scope.adjustmentId = undefined;
        $scope.isInvioceAudit = true;

        $scope.cancelSendToAudit = function () {
            $scope.sendToAuditVisible = false;
            $scope.auditReason.code = undefined;
            $scope.auditReason.note = undefined;
            $scope.loadId = undefined;
            $scope.adjustmentId = undefined;
        };

        var updateCurrentReasons = function () {
            if (!_.isEmpty($scope.allAuditReasonsData)) {
                var currentReasonType = $scope.isInvioceAudit ? 'I' : 'P';

                $scope.auditReasonsParam = _.filter($scope.allAuditReasonsData, function (reason) {
                    return reason.reasonType === currentReasonType;
                });
            }
        };

        $scope.$on('event:sendToAudit', function (event, options) {
            $scope.isInvioceAudit = options.isInvioceAudit;
            $scope.sendToAuditVisible = true;
            $scope.auditRecords = options.auditRecords;

            if (_.isEmpty($scope.allAuditReasonsData)) {
                ShipmentDictionaryService.getAuditReasonCode({}, function (auditReasonsParam) {
                    $scope.allAuditReasonsData = auditReasonsParam;
                    updateCurrentReasons();
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Get audit reasonCode failure!',
                            'Getting of billing audit reason code has been failed!');
                });
            }

            updateCurrentReasons();
        });

        $scope.sendToAudit = function () {
            if ($scope.isInvioceAudit) {
                FinancialBoardService.sendToInvoiceAudit({
                    auditRecords: $scope.fillSelectedShipmentsForBusinessObjects($scope.auditRecords),
                    code: $scope.auditReason.code,
                    note: $scope.auditReason.note
                }, function () {
                    $scope.$emit('event:updateDataAfterSendToAudit');
                    $scope.cancelSendToAudit();
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Send invoices to audit failure!', 'Sending invoice to audit has failed!');
                });
            } else {
                FinancialBoardService.sendToPriceAudit({
                    auditRecords: $scope.fillSelectedShipmentsForBusinessObjects($scope.auditRecords),
                    code: $scope.auditReason.code,
                    note: $scope.auditReason.note
                }, function () {
                    $scope.$emit('event:updateDataAfterSendToAudit');
                    $scope.cancelSendToAudit();
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Send invoices to audit failure!', 'Sending invoice to audit has failed!');
                });
            }
        };
    }
]);