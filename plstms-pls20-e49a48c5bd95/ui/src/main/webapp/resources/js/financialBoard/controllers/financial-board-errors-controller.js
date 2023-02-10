angular.module('pls.controllers').controller('FinancialBoardErrorsController', [
    '$scope', 'FinancialBoardInvoiceErrorsService', 'NgGridPluginFactory', 'StringUtils',
    function ($scope, FinancialBoardInvoiceErrorsService, NgGridPluginFactory, StringUtils) {
        'use strict';

        $scope.errors = [];
        $scope.selectedErrors = [];

        $scope.reprocessDialog = {
            show: false
        };

        $scope.init = function () {
            $scope.loadInvoiceErrors();
        };

        $scope.loadInvoiceErrors = function () {
            FinancialBoardInvoiceErrorsService.invoiceErrors({}, function (data) {
                $scope.errors = data;
                $scope.selectedErrors.length = 0;
            }, function (data, status) {
                $scope.$root.$emit('event:application-error', 'Invoice Errors load failed!', 'Can\'t load Invoice Errors: ' + status);
            });
        };

        $scope.cancelError = function () {
            FinancialBoardInvoiceErrorsService.cancelError({
                errorId: $scope.selectedErrors[0].id
            }, function () {
                $scope.$root.$broadcast('event:financialBoardErrorsChanged');
                $scope.loadInvoiceErrors();
            }, function (data, status) {
                $scope.$root.$emit('event:application-error', 'Invoice Errors cancelation failed!', 'Can\'t cancel invoice error: ' + status);
            });
        };

        $scope.exportErrors = function () {
            $scope.$emit('event:exportInvoices', {
                sheetName: 'Invoice Errors',
                grid: $scope.errorsGrid,
                fileName: 'Invoice_Error_Export file_',
                selectedRows: false
            });
        };

        $scope.reprocessInvoice = function () {
            if ($scope.selectedErrors.length === 1 && $scope.selectedErrors[0].id) {
                FinancialBoardInvoiceErrorsService.getEmailSubjectForReprocessError({errorId : $scope.selectedErrors[0].id}, function(result) {
                    if (_.isEmpty(result.data) || _.isUndefined(result.data)) {
                        $scope.submitReprocessInvoice();
                    } else {
                        $scope.reprocessDialog.subject = result.data;
                        $scope.reprocessDialog.comments = '';
                        $scope.processConfirmationDialog = true;
                    }
                }, function (data, status) {
                    $scope.$root.$emit('event:application-error', 'Invoice subject load failed!', 'Can\'t load Invoice Subject: ' + status);
                });
            }
        };

        $scope.submitReprocessInvoice = function() {
            FinancialBoardInvoiceErrorsService.reProcessErrors({invoiceProcessingDetails: [{
                        subject: $scope.reprocessDialog.subject,
                        comments: $scope.reprocessDialog.comments
                    }],
                    errorId : $scope.selectedErrors[0].id
                },
                function (data) {
                    if (data.data && data.data.length) {
                        $scope.$root.$emit('event:application-error', 'Re-Process Invoice failed!', data.data);
                    } else {
                        $scope.processConfirmationDialog = false;
                        $scope.loadInvoiceErrors();
                        $scope.$root.$broadcast('event:financialBoardErrorsChanged');
                        $scope.$root.$emit('event:operation-success', 'Re-Process Invoice success',
                                'Invoice Error has been re-processed successfully');
                    }
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Re-Process Invoice Error failed!',
                            'Invoice re-processing failed');
                });
        };

        $scope.errorsGrid = {
            enableColumnResize: true,
            data: 'errors',
            multiSelect: false,
            selectedItems: $scope.selectedErrors,
            columnDefs: [
                {
                    field: 'dateTime',
                    displayName: 'Date/Time',
                    cellFilter: 'date:appDateTimeFormat',
                    width: '7%'
                },
                {
                    field: 'userName',
                    displayName: 'User Name',
                    width: '10%'
                },
                {
                    field: 'event',
                    displayName: 'Event',
                    width: '40%'
                },
                {
                    field: 'message',
                    displayName: 'Error Message',
                    width: '42%'
                }
            ],
            sortInfo: {
                fields: ['dateTime'],
                directions: ['desc']
            },
            filterOptions: {
                filterText: "",
                useExternalFilter: false
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), 
                      NgGridPluginFactory.hideColumnPlugin()],
            progressiveSearch: true
        };
    }
]);