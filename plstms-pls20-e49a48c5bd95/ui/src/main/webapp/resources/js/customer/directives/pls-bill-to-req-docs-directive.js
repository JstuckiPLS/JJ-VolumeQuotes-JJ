angular.module('plsApp').directive('plsBillToReqDocs', ['NgGridPluginFactory', 'BillToDocumentService',
    function (NgGridPluginFactory, BillToDocumentService) {
        return {
            restrict: 'A',
            scope: {
                billTo: '=',
                requiredFields: '='
            },
            replace: true,
            templateUrl: 'pages/content/customer/billTo/templates/billto-req-docs-tpl.html',
            controller: ['$scope', '$rootScope', function ($scope, $rootScope) {
                'use strict';

                $scope.paperWorksReq = ['REQUIRED', 'ON_AVAIL'];

                if (!$scope.billTo.invoicePreferences.requiredDocuments) {
                    BillToDocumentService.query({customerId: -1, billToId: -1}, function (data) {
                        $scope.billTo.invoicePreferences.requiredDocuments = data;

                        angular.forEach($scope.billTo.invoicePreferences.requiredDocuments, function (item) {
                            if (item.carrierRequestType === null) {
                                item.carrierRequestType = '';
                            }

                            if (item.customerRequestType === null) {
                                item.customerRequestType = '';
                            }
                        });
                    });
                }

                $scope.havePermissionToUpload = function(documentType) {
                    return $rootScope.isPlsPermissions('CAN_UPLOAD_' + documentType.toUpperCase().replace(/ /g, '_'));
                };

                $scope.reqDocs = {
                    gridOptions: {
                        enableColumnResize: true,
                        data: 'billTo.invoicePreferences.requiredDocuments',
                        enableRowSelection: false,
                        enableSorting: false,
                        columnDefs: [
                            {
                                field: 'documentTypeDescription',
                                displayName: 'Document Type',
                                width: '50%'
                            },
                            {
                                field: 'customerRequestType',
                                displayName: 'Req. for Invoice',
                                width: '45%',
                                cellTemplate: '<select class="span10 offset1" ' +
                                'data-ng-disabled="!$root.isFieldRequired(\'ADD_EDIT_REQUIRED_DOCS\')"' +
                                'data-ng-model="billTo.invoicePreferences.requiredDocuments[row.rowIndex].customerRequestType" ' +
                                'data-ng-options="pw | requiredDocument for pw in paperWorksReq"><option value="" selected></option></select>'
                            }
                        ],
                        plugins: [NgGridPluginFactory.plsGrid()]
                    }
                };
            }]
        };
    }
]);