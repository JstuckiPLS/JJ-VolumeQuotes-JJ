angular.module('plsApp').directive('plsBillToInvoicePreferences', ['TimeZoneResource', 'DateTimeUtils',
    function (TimeZoneResource, DateTimeUtils) {
        return {
            restrict: 'A',
            scope: {
                billTo: '=',
                payTerms: '=',
                payMethods: '=',
                sortTypes: '=',
                xlsDocuments: '=',
                pdfDocuments: '='
            },
            replace: true,
            templateUrl: 'pages/content/customer/billTo/templates/billto-invoice-preferences-tpl.html',
            controller: ['$scope', 'copyExactProperties', 'deleteExactProperties',
                function ($scope, copyExactProperties, deleteExactProperties) {
                    'use strict';

                    TimeZoneResource.getAll(function (data) {
                        $scope.timezones = data;
                    });

                    $scope.invoiceProcessingTypes = ['AUTOMATIC', 'MANUAL'];
                    $scope.processingPeriodValues = ['DAILY', 'WEEKLY'];
                    $scope.currencyCodes = ['USD', 'CAD'];
                    $scope.daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

                    var defaultPickupWindow = {hours: 5, minutes: 0, am: true};
                    var cache = {};

                    function findDocument(allDocuments) {
                        var i = 0;

                        if ($scope.billTo.invoicePreferences.documents) {
                            for (; i < $scope.billTo.invoicePreferences.documents.length; i += 1) {
                                var document = _.findWhere(allDocuments, {value: $scope.billTo.invoicePreferences.documents[i]});
                                if (document) {
                                    return document.value;
                                }
                            }
                        }

                        return allDocuments[0].value;
                    }

                    /* Private function for setting up default CBI Invoice Type */
                    function setDefaultCbiInvoiceType() {
                        if (angular.isDefined($scope.billTo.invoicePreferences)
                                && $scope.billTo.invoicePreferences.invoiceType === 'CBI'
                                && angular.isUndefined($scope.billTo.invoicePreferences.cbiInvoiceType)) {
                            $scope.billTo.invoicePreferences.cbiInvoiceType = 'PLS';
                        }
                    }

                    $scope.preferencesModel = {};

                    $scope.$watch('preferencesModel.pickupWindow', function (newValue) {
                        if ($scope.billTo.invoicePreferences) {
                            $scope.billTo.invoicePreferences.processingTimeInMinutes = newValue ?
                                    DateTimeUtils.timeInMinutesFromPickupWindow(newValue) : newValue;
                        }
                    });

                    $scope.documentsChanged = function () {
                        if (!$scope.billTo.invoicePreferences.documents) {
                            $scope.billTo.invoicePreferences.documents = [];
                        } else {
                            $scope.billTo.invoicePreferences.documents.length = 0;
                        }

                        if ($scope.preferencesModel.xlsSelectedDocument !== 'NONE') {
                            $scope.billTo.invoicePreferences.documents.push($scope.preferencesModel.xlsSelectedDocument);
                        }

                        if ($scope.preferencesModel.pdfSelectedDocument !== 'NONE') {
                            $scope.billTo.invoicePreferences.documents.push($scope.preferencesModel.pdfSelectedDocument);
                        }
                    };

                    $scope.noInvoiceDocumentSelected = function () {
                        if ($scope.billTo.invoicePreferences.noInvoiceDocument) {
                            $scope.billTo.invoicePreferences.notSplitRecipients = false;
                            $scope.preferencesModel.xlsSelectedDocument = 'NONE';
                            $scope.preferencesModel.pdfSelectedDocument = 'NONE';
                            $scope.documentsChanged();
                        }
                    };

                    $scope.isValidDocument = function () {
                        return $scope.billTo.invoicePreferences.ediInvoice
                            || $scope.preferencesModel.xlsSelectedDocument !== 'NONE' || $scope.preferencesModel.pdfSelectedDocument !== 'NONE';
                    };

                    function setDefaultTime() {
                        if (!$scope.preferencesModel.pickupWindow) {
                            $scope.preferencesModel.pickupWindow = angular.copy(defaultPickupWindow);
                        }

                        if (_.isUndefined($scope.billTo.invoicePreferences.processingTimezone)) {
                            $scope.billTo.invoicePreferences.processingTimezone = {localOffset: 0};
                        }
                    }

                    $scope.processingPeriodChanged = function () {
                        if ($scope.billTo.invoicePreferences.processingPeriod === 'WEEKLY') {
                            delete $scope.billTo.invoicePreferences.processingTimezone;
                            $scope.preferencesModel.pickupWindow = undefined;
                        } else {
                            deleteExactProperties($scope.billTo.invoicePreferences, ['processingDayOfWeek', 'releaseDayOfWeek']);
                            setDefaultTime();
                        }
                    };

                    $scope.processingTypeChanged = function () {
                        if ($scope.billTo.invoicePreferences.processingType === 'AUTOMATIC') {
                            $scope.billTo.invoicePreferences.processingPeriod = $scope.processingPeriodValues[0];
                            setDefaultTime();
                        } else {
                            deleteExactProperties($scope.billTo.invoicePreferences,
                                    ['processingTimezone', 'processingPeriod', 'processingDayOfWeek', 'releaseDayOfWeek']
                            );
                            $scope.preferencesModel.pickupWindow = undefined;
                        }
                    };

                    $scope.setTransactional = function () {
                        copyExactProperties(cache, ['xlsSelectedDocument', 'pdfSelectedDocument'], $scope.preferencesModel);

                        $scope.preferencesModel.xlsSelectedDocument = 'NONE';
                        $scope.preferencesModel.pdfSelectedDocument = 'NONE';
                        $scope.billTo.invoicePreferences.processingType = 'AUTOMATIC';

                        copyExactProperties($scope.billTo.invoicePreferences, ['cbiInvoiceType', 'documents', 'sortType', 'processingType',
                            'processingPeriod', 'processingDayOfWeek', 'releaseDayOfWeek'], cache
                        );

                        deleteExactProperties($scope.billTo.invoicePreferences,
                                ['cbiInvoiceType', 'documents', 'sortType', 'processingPeriod', 'processingDayOfWeek', 'releaseDayOfWeek']
                        );

                        setDefaultTime();
                    };

                    $scope.setCBI = function () {
                        copyExactProperties($scope.preferencesModel, ['xlsSelectedDocument', 'pdfSelectedDocument'], cache);

                        copyExactProperties(cache, ['cbiInvoiceType', 'documents', 'sortType', 'processingType', 'processingPeriod',
                            'processingDayOfWeek', 'releaseDayOfWeek'], $scope.billTo.invoicePreferences
                        );

                        if (angular.isUndefined($scope.billTo.invoicePreferences.sortType)) {
                            $scope.billTo.invoicePreferences.sortType = $scope.sortTypes[0].id;
                        }

                        if (angular.isUndefined($scope.billTo.invoicePreferences.invoiceProcessingTypes)) {
                            $scope.billTo.invoicePreferences.invoiceProcessingTypes = $scope.invoiceProcessingTypes[0];
                        }

                        if (angular.isUndefined($scope.billTo.invoicePreferences.processingPeriod)) {
                            $scope.billTo.invoicePreferences.processingPeriod = $scope.processingPeriodValues[0];
                        }

                        setDefaultCbiInvoiceType();
                        setDefaultTime();
                    };

                    $scope.isWeeklyProcessing = function () {
                        return $scope.billTo.invoicePreferences.invoiceType === 'CBI'
                                && $scope.billTo.invoicePreferences.processingPeriod === 'WEEKLY'
                                && $scope.billTo.invoicePreferences.processingType === 'AUTOMATIC';
                    };

                    $scope.isProcessOnTime = function () {
                        return $scope.billTo.invoicePreferences.invoiceType !== 'CBI'
                                || ($scope.billTo.invoicePreferences.processingPeriod !== 'WEEKLY'
                                && $scope.billTo.invoicePreferences.processingType === 'AUTOMATIC');
                    };

                    /* VM method for proper Invoice Type selection and remember previous selection */
                    $scope.setInvoiceType = function (type) {
                        if (type === 'TRANSACTIONAL') {
                            $scope.setTransactional();
                        } else if (type === 'CBI') {
                            $scope.setCBI();
                            $scope.billTo.paymentMethod = "";
                            $scope.billTo.creditCardEmail ="";
                        }
                    };

                    if($scope.billTo.paymentMethod === undefined){
                        $scope.billTo.paymentMethod = "";
                    }

                    $scope.setPaymentMethod = function(method) {
                        if (method === 'PREPAID_ONLY') {
                            $scope.setTransactional();
                            $scope.billTo.invoicePreferences.invoiceType = 'TRANSACTIONAL';
                        }
                    };

                    /* VM method for proper CBI Invoice Type selection and remember previous selection */
                    $scope.setCbiInvoiceType = function (type) {
                        if (type === 'PLS') {
                            if (cache.xlsSelectedDocument || cache.pdfSelectedDocument) {
                                copyExactProperties(cache, ['xlsSelectedDocument', 'pdfSelectedDocument'], $scope.preferencesModel);
                            }

                            copyExactProperties(cache, ['documents', 'sortType', 'ediInvoice', 'notSplitRecipients', 'noInvoiceDocument'],
                                    $scope.billTo.invoicePreferences);

                            if (angular.isUndefined($scope.billTo.invoicePreferences.sortType)) {
                                $scope.billTo.invoicePreferences.sortType = $scope.sortTypes[0].id;
                            }
                        } else if (type === 'FIN') {
                            copyExactProperties($scope.preferencesModel, ['xlsSelectedDocument', 'pdfSelectedDocument'], cache);

                            $scope.preferencesModel.xlsSelectedDocument = 'NONE';
                            $scope.preferencesModel.pdfSelectedDocument = 'NONE';

                            copyExactProperties($scope.billTo.invoicePreferences,
                                    ['documents', 'sortType', 'ediInvoice', 'notSplitRecipients', 'noInvoiceDocument'], cache);

                            deleteExactProperties($scope.billTo.invoicePreferences,
                                    ['documents', 'sortType', 'ediInvoice', 'notSplitRecipients', 'noInvoiceDocument']);
                        }
                    };

                    $scope.updateAutoCreditHoldValue = function () {
                        if (!$scope.billTo.overrideCreditHold) {
                            $scope.billTo.autoCreditHold = $scope.billTo.customerOverrideCreditHold ? $scope.billTo.customerAutoCreditHold
                                    : $scope.billTo.networkAutoCreditHold;
                        }
                    };

                    if (!$scope.billTo.id && !$scope.billTo.invoicePreferences) {
                        $scope.billTo.invoicePreferences = {
                            invoiceType: 'TRANSACTIONAL'
                        };

                        $scope.setTransactional();
                        $scope.billTo.currency = 'USD';
                        $scope.billTo.payTermsId = 17;
                    } else {
                        if (!_.isUndefined($scope.billTo.invoicePreferences.processingTimeInMinutes)) {
                            $scope.preferencesModel.pickupWindow = DateTimeUtils.
                            pickupWindowFromTimeInMinutes($scope.billTo.invoicePreferences.processingTimeInMinutes);
                        }

                        $scope.preferencesModel.xlsSelectedDocument = findDocument($scope.xlsDocuments);
                        $scope.preferencesModel.pdfSelectedDocument = findDocument($scope.pdfDocuments);
                        setDefaultCbiInvoiceType();
                    }
                }
            ],
            controllerAs: 'ip'
        };
    }
]);