angular.module('plsApp.directives').directive('plsAddDeleteDocuments', ['ShipmentDocumentService', 'NgGridPluginFactory', 'urlConfig',
        'BillToDocumentService','DateTimeUtils', 'ShipmentSavingService','ShipmentDocumentEmailService', 'ManualBolDocumentService', 'ShipmentUtils',
    function (ShipmentDocumentService, NgGridPluginFactory, urlConfig, BillToDocumentService, DateTimeUtils, ShipmentSavingService,
            ShipmentDocumentEmailService, ManualBolDocumentService, ShipmentUtils) {
    return {
        restrict: 'A',
        scope: {
            shouldSaveTempDocuments : '=shouldSaveTempDocuments ',
            salesOrderModel: '=editSalesOrder',
            fullViewDocModel: '=',
            emailOptions: '=',
            shipment: '=',
            selectedCustomer: '=',
            step: '=?'
        },
        templateUrl: 'pages/tpl/pls-add-delete-docs-tpl.html',
        controller: ['$scope', '$rootScope', '$timeout', function($scope, $rootScope, $timeout) {
            'use strict';

            var DocumentTypesEnum = {
                BOL: {name: 'BOL', value: 'BOL'},
                SHIPPING_LABELS: {name: 'SHIPPING_LABELS', value: 'Shipping Label'},
                CONSIGNEE_INVOICE: {name: 'CONSIGNEE_INVOICE', value: 'Consignee Invoice'}
            };

            $scope.printTypes = angular.copy(ShipmentUtils.getDictionaryValues().printTypes);

            if (!$scope.docsGridData) {
                $scope.docsGridData = [];
            }

            $scope.timeOnBol = {
                hideCreatedTime: false
            };

            $scope.selectedDocs = [];

            $scope.requiredDocuments = [];
            $scope.canRegenerate = false;
            $scope.canAddDoc = true;
            $scope.isDocChangedByHand = false;
            if ($scope.shipment) {
                $scope.shipment.markup = $scope.shipment.markup ? $scope.shipment.markup : 25;
            }


            $scope.docOptions = {
                width: '100%',
                height: '600px',
                imageContent: false,
                pdfLocation: null,
                hidePdf: true
            };

            $scope.$on('event:confirm-cancel-order-dialog-open', function () {
                $scope.docOptions.hidePdf = true;
            });
            $scope.$on('event:confirm-cancel-order-dialog-closed', function () {
                $scope.docOptions.hidePdf = false;
            });

            $scope.docsGridColumnDefs = [{
                field: 'name',
                displayName: 'Documents',
                width: "40%"
            }, {
                field: 'createdByName',
                displayName: 'Uploaded By',
                width: "30%"
            }, {
                field: 'createdDate',
                displayName: 'Date',
                width: "30%",
                cellFilter: 'date:appDateFormat'
            }];

            $scope.docsGrid = {
                sort: '',
                filter: '',
                options: {
                    data: 'docsGridData',
                    selectedItems: $scope.selectedDocs,
                    columnDefs: 'docsGridColumnDefs',
                    plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin()],
                    afterSelectionChange: function () {
                        $scope.showDocumentContent();
                    },
                    sortInfo: {
                        fields: ['createdDate'],
                        directions: ['desc']
                    },
                    action: function () {
                        $scope.viewDocument();
                    },
                    refreshTable: function() {
                        $scope.getDocsTableData();
                    },
                    enableColumnResize: true,
                    useExternalSorting: false,
                    multiSelect: true,
                    beforeSelectionChange: function(rowItem, event) {
                        if (!event.ctrlKey &&  $scope.docsGrid.options.multiSelect
                                && !$(event.srcElement).is(".ngSelectionCheckbox")) {
                            angular.forEach($scope.docsGridData , function(docs, index) { 
                                $scope.docsGrid.options.selectRow(index, false);
                            });
                        }
                        return true;
                    }
                }
            };

            function selectDocument(docName) {
                _.some($scope.docsGridData, function (doc) {
                    if (doc.name === docName) {
                        $scope.docsGrid.options.selectedItems.length = 0;
                        $scope.docsGrid.options.selectedItems[0] = doc;
                        return true;
                    }
                    return false;
                });
            }

            $scope.canHideTimeOnBol = function() {
                if ($scope.shouldSaveTempDocuments) {
                    return $scope.isSelectedBOLDocument() && (!$scope.salesOrderModel || !$scope.salesOrderModel.formDisabled)
                            && $scope.$root.isFieldRequired('DO_NOT_DISPLAY_TIME_FOR_SHIPMENT_CREATED_BY');
                } else {
                    return false;
                }
            };

            $scope.canViewElement = function() {
                if ($scope.shouldSaveTempDocuments) {
                    return !$scope.salesOrderModel || !$scope.salesOrderModel.formDisabled;
                } else {
                    return true;
                }
            };

            $scope.canViewAddShipLabel = function() {
                return $scope.shouldSaveTempDocuments  && $scope.canViewElement();
            };

            $scope.canRegenerateConsigneeInvoice = function() {
                return $scope.$root.isFieldRequired('CAN_GENERATE_CONSIGNEE_INVOICE')
                        && $scope.shipment.generateConsigneeInvoice
                        && $scope.shipment.id;
            };

            $scope.canDeleteDocs = function() {
                if ($scope.shipment.isManualBol) {
                    return false;
                }
                if ($scope.shouldSaveTempDocuments ) {
                    return (!$scope.salesOrderModel || !$scope.salesOrderModel.formDisabled)
                            || ($scope.$root.isPlsPermissions('REMOVE_DOCUMENTS_AFTER_INVOICING') && $scope.shipment.invoiceDate);
                } else {
                    return true;
                }
            };

            $scope.canShowAddDocument = function() {
                if ($scope.shipment.isManualBol || !$scope.docTypes || $scope.docTypes.length === 0 ) {
                    return false;
                }
                if ($scope.shouldSaveTempDocuments) {
                    return (!$scope.salesOrderModel || !$scope.salesOrderModel.formDisabled)
                            || ($scope.$root.isPlsPermissions('ADD_DOCUMENTS_AFTER_INVOICING') && $scope.shipment.invoiceDate);
                } else {
                    return true;
                }
            };

            function getDocumentTypes() {
                ShipmentDocumentService.getDocumentTypesList({}, function (data) {
                    if ($scope.shouldSaveTempDocuments) {
                        $scope.docTypes = _.reject(data, function (el) {
                            return el.value === 'BOL' || el.value === 'SHIPPING_LABELS';
                        });
                    } else {
                        $scope.docTypes = _.filter(data, function(doc) {
                            return $scope.$root.isPlsPermissions('CAN_UPLOAD_' + doc.value.toUpperCase().replace(/ /g, '_'));
                        });
                    }
                    $scope.docTypes = _.sortBy($scope.docTypes, function (docType) {
                        return docType.label.toUpperCase();
                    });
                    $scope.init();
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Document types receiving failed!', 'Cannot get available document types');
                });
            }

            BillToDocumentService.query({
                customerId: $scope.selectedCustomer.id,
                billToId: $scope.shipment.billTo.id
            }, function (data) {
                $scope.allRequiredDocuments = _.filter(data, function (doc) {
                    return doc.customerRequestType === 'REQUIRED';
                });

                $scope.filterRequiredDocuments();
                getDocumentTypes();
            });

            function fillDocsGrid(responseObj) {
                $scope.docsGridData.push({
                    createdDate: new Date(),
                    name: $scope.uploadModel.selectedDocType.label,
                    createdByName: $rootScope.authData.fullName,
                    tempDocId: responseObj.tempDocId,
                    tempFileName: $scope.uploadModel.uploadFile,
                    fileName: $scope.uploadModel.uploadFile
                });

                $scope.filterRequiredDocuments();
                $scope.isDocChangedByHand = true;
            }

            function fillUploadedDocumentsList(responseObj) {
                if (!$scope.shipment.uploadedDocuments) {
                    $scope.shipment.uploadedDocuments = [];
                }

                $scope.shipment.uploadedDocuments.push({
                    id: responseObj.tempDocId,
                    fileName: $scope.uploadModel.uploadFile,
                    docType: $scope.uploadModel.selectedDocType.value,
                    docName: $scope.uploadModel.selectedDocType.label
                });
            }

            $scope.uploadModel = {
                selectedDocType: undefined,
                uploadUrl: undefined,
                uploadFile: undefined,
                setFile: function (element) {
                    if (element.files && element.files.length) {
                        if (element.files[0].size <= 2621440) {
                            if (element.files[0].name.match(/\.pdf$|\.png$|\.jpg$|\.jpeg$|\.bmp$|\.tif$|\.tiff$|\.gif$/i)) {
                                $scope.$apply(function ($scope) {
                                    $scope.uploadModel.uploadFile = element.value;
                                    $scope.uploadedFileType = element.files[0].type;
                                    $scope.uploadedFileName = element.files[0].name;
                                });
                                return;
                            } else {
                                $scope.$root.$emit('event:application-error', 'Document upload failed!',
                                        'Your file can not be uploaded. File format is not supported.');
                            }
                        } else {
                            $scope.$root.$emit('event:application-error', 'Document upload failed!',
                                    'Your file can not be uploaded. File size should be no more than 2.5 MB');
                        }
                    } else if (element.value) {
                        if (element.value.match(/\.pdf$|\.png$|\.jpeg$|\.jpg$|\.bmp$|\.tif$|\.tiff$|\.gif$/i)) {
                            $scope.$apply(function ($scope) {
                                $scope.uploadModel.uploadFile = element.value;
                            });
                            return;
                        } else {
                            $scope.$root.$emit('event:application-error', 'Document upload failed!',
                                    'Your file can not be uploaded. File format is not supported.');
                        }
                    }

                    $scope.uploadModel.uploadFile = undefined;
                    $scope.clearUploadElement();
                },
                uploadCallback: function (content, completed) {
                    if (completed) {
                        $rootScope.progressPanelOptions.showPanel = false;
                    }

                    if (completed && content) {
                        var responseObj;

                        try {
                            responseObj = JSON.parse(content);
                        } catch (e) {
                            if (content.indexOf('HTTP Status 401') !== -1) {
                                $scope.$root.$broadcast('event:auth-loginRequired');
                            } else {
                                $scope.$root.$emit('event:application-error', 'Document upload failed!',
                                        'Your file has not been uploaded! Please try again later.');
                            }
                            return;
                        }
                        if (responseObj.success === true || responseObj.success === 'true') {

                            fillDocsGrid(responseObj);
                            if ($scope.shouldSaveTempDocuments ) {
                                fillUploadedDocumentsList(responseObj);
                            }

                            $scope.uploadModel.uploadFile = undefined;
                            $scope.uploadModel.selectedDocType = undefined;
                            $scope.clearUploadElement();
                            angular.element('#upload').trigger('change');
                        } else if (responseObj.limitSizeExceeded) {
                            $scope.$root.$emit('event:application-error', 'Document upload failed!',
                                    'Your file has not been uploaded. File size should be no more than 2.5 MB');
                        } else {
                            $scope.$root.$emit('event:application-error', 'Document upload failed!',
                                    'Your file has not been uploaded!');
                        }
                    }
                }
            };

            function addUploadedDocs() {
                if ($scope.shipment.uploadedDocuments && $scope.shipment.uploadedDocuments.length) {
                    _.each($scope.shipment.uploadedDocuments, function (tempDoc) {
                        $scope.docsGridData.push({
                            createdDate: new Date(),
                            name: tempDoc.docName,
                            createdByName: $rootScope.authData.fullName,
                            tempDocId: tempDoc.tempDocId,
                            tempFileName: tempDoc.fileName,
                            fileName: tempDoc.fileName
                        });
                    });
                }
            }

            // @TODO need to switch to injected service
            function setDocumentOptions(options) {
                if ($scope.selectedDocs && $scope.selectedDocs[0]) {
                    options.imageContent = $scope.isDocumentImage();

                    options.pdfLocation = urlConfig.shipment + '/customer/shipmentdocs/';

                    if ($scope.selectedDocs[0].tempDocId) { // tempDocId - custom field that is needed for temporary loaded docs
                        options.pdfLocation += $scope.selectedDocs[0].tempDocId;
                    } else {
                        options.pdfLocation += $scope.selectedDocs[0].id;
                    }

                    options.pdfLocation += '?t=' + new Date().getTime(); //Workaround to prevent caching GET requests in IE
                    return true;
                } else {
                    options.pdfLocation = null;
                    return false;
                }
            }

            $scope.$watch('shipment', function () {
                if ($scope.shouldSaveTempDocuments ) {
                    $scope.canRegenerate = _.contains(['DISPATCHED', 'OUT_FOR_DELIVERY', 'IN_TRANSIT', 'DELIVERED'], $scope.shipment.status);
                    $scope.canRegenerateConsigneeInvoiceByHand = _.contains(['OPEN', 'CANCELLED'], $scope.shipment.status);
                }
            });

            function getRequiredDocumentsDate(dates) {
                var requiredDocumentNames = _.pluck($scope.allRequiredDocuments, 'documentTypeDescription');

                _.each($scope.docsGridData, function (doc) {
                    if (_.indexOf(requiredDocumentNames, doc.name) !== -1) {
                        dates.push(doc.createdDate);
                    }
                });
            }

            function updatefreightBillDate() {
                if ($scope.shouldSaveTempDocuments  && $scope.isCanEditFreightBillDate()) {
                    if (!$scope.shipment.freightBillDate) {
                        var dates = [$scope.shipment.finishOrder.actualDeliveryDate];

                        if ($scope.shipment.vendorBillDate) {
                            dates.push($scope.shipment.vendorBillDate);
                        }

                        getRequiredDocumentsDate(dates);
                        var freightBillDate = DateTimeUtils.getBiggestDate(dates);
                        $scope.shipment.freightBillDate = freightBillDate;

                        if ($scope.shipmentBackup) {
                            $scope.shipmentBackup.freightBillDate = freightBillDate;
                        }
                    }
                }
            }

            $scope.$watch('requiredDocuments', function (newVal, oldVal) {
                if (newVal !== oldVal && $scope.isDocChangedByHand) {
                    updatefreightBillDate();
                }

                $scope.isDocChangedByHand = false;
            }, true);

            $scope.$on('event:updateFreightBillDate', function () {
                updatefreightBillDate();
            });

            $scope.isAddDisabled = function () {
                return !$scope.uploadModel.uploadFile || !$scope.uploadModel.selectedDocType || !$scope.canAddDoc;
            };

            $scope.uploadSubmit = function () {
                $rootScope.progressPanelOptions.showPanel = true;
                if ($scope.shouldSaveTempDocuments ) {
                    $scope.uploadModel.uploadUrl = urlConfig.shipment + '/customer/shipmentdocs/temp';
                } else {
                    $scope.uploadModel.uploadUrl = urlConfig.shipment + '/customer/shipmentdocs/' + $scope.shipment.id + '/saveDoc?docType=' +
                            $scope.uploadModel.selectedDocType.value;
                }
                return true;
            };

            function isTiffExtension(fileExtension) {
                return fileExtension.toLowerCase() === 'tiff' || fileExtension.toLowerCase() === 'tif';
            }

            // @TODO need to switch to injected service
            $scope.isDocumentImage = function () {
                var tempFileName = $scope.selectedDocs[0].tempFileName;

                if (tempFileName) {
                    var indexOfPoint = tempFileName.lastIndexOf('.');

                    if (indexOfPoint > -1) {
                        if (isTiffExtension(tempFileName.substring(indexOfPoint + 1).trim())) {
                            return false;
                        } else {
                            return tempFileName.substring(indexOfPoint + 1).trim() !== 'pdf';
                        }
                    } else {
                        return false;
                    }
                } else {
                    return $scope.selectedDocs[0].docFileType && $scope.selectedDocs[0].docFileType !== 'application/pdf';
                }
            };

            $scope.showDocumentContent = function () {
                if (setDocumentOptions($scope.docOptions) && $scope.selectedDocs.length === 1) {
                    $scope.docOptions.hidePdf = false;
                } else {
                    $scope.docOptions.hidePdf = true;
                    $scope.docOptions.docsNumber = $scope.selectedDocs.length;
                }
            };

            $scope.viewDocument = function () {
                if (setDocumentOptions($scope.fullViewDocModel.fullViewDocOption)) {
                    $scope.docOptions.hidePdf = true;
                    $scope.fullViewDocModel.showFullViewDocumentDialog = true;
                }
            };

            $scope.fullViewDocModel.closeFullViewDocument = function () {
                $scope.fullViewDocModel.showFullViewDocumentDialog = false;
                $scope.fullViewDocModel.fullViewDocOption.pdfLocation = null;
                $scope.docOptions.hidePdf = false;
            };

            $scope.isSelectedBOLDocument = function () {
                return !_.isEmpty($scope.selectedDocs[0]) && $scope.selectedDocs[0].name === 'BOL';
            };

            $scope.isSelectedOnlyShippingLabelDocument = function () {
                return $scope.selectedDocs.length === 1 && $scope.selectedDocs[0].name === 'Shipping Label';
            };

            $scope.printDocument = function () {
                var shipment = angular.copy($scope.shipment);
                if (shipment.isManualBol) {
                    shipment.status = undefined;
                }

                $scope.docOptions.url = urlConfig.shipment + '/customer/shipmentdocs/';
                if ($scope.selectedDocs[0].tempDocId) {
                    $scope.docOptions.url += $scope.selectedDocs[0].tempDocId;
                } else {
                    $scope.docOptions.url += $scope.selectedDocs[0].id;
                }

                if ($scope.isSelectedOnlyShippingLabelDocument()) {
                    var requestData = {
                        customerId: $scope.selectedCustomer.id,
                        subPathParam: DocumentTypesEnum.SHIPPING_LABELS.name,
                        printType: $scope.selectedPrintType.value
                    };
                    $scope.docOptions.printDocument({requestData: requestData, shipment: shipment});
                } else {
                    $scope.docOptions.printDocument();
                }
            };

            function getDocumentExtension(fileName) {
                var index = fileName.indexOf('.');
                return fileName.substring(index);
            }

            function getEmailType() {
                if ($scope.shipment.isManualBol) {
                    return 'NOT_AUDITABLE';
                }
            }

            /*
             * Send email functionality
             */
            $scope.sendMailFunction = function (recipients, subject, content) {
                ShipmentDocumentEmailService.emailDoc({
                    recipients: recipients,
                    subject: subject,
                    content: content,
                    documents: $scope.emailOptions.documents,
                    loadId: $scope.shipment.id,
                    emailType: getEmailType()
                }, function () {
                    $scope.$root.$emit('event:operation-success', 'Email send status', 'Email sent successfully');
                    $scope.emailOptions.showSendEmailDialog = false;
                }, function (data) {
                    $scope.$root.$emit('event:application-error', 'Email send status', data);
                    $scope.$root.$emit('event:error-send-email');
                });
            };

            function setMailOptions() {
                $scope.emailOptions.sendMailFunction = $scope.sendMailFunction;
                $scope.emailOptions.getTemplate = function () {
                    return ShipmentDocumentEmailService.getTemplate({
                        loadId: $scope.shipment.id,
                        docName: $scope.emailOptions.docName,
                        isManualBol: $scope.shipment.isManualBol
                    });
                };
            }

            setMailOptions();

            var openEmailDialog = function() {
                $scope.emailOptions.showSendEmailDialog = true;
                $scope.docOptions.hidePdf = true;
                $scope.emailOptions.isViewMode = false;
                delete $scope.emailOptions.editEmailRecipientsList;

                if ($scope.selectedDocs.length === 1) {
                    $scope.emailOptions.closeSendMailDialogHandler = function () {
                        $scope.docOptions.hidePdf = false;
                    };
                }

                $scope.emailOptions.subject = 'PLS PRO document email for BOL: ' + $scope.shipment.bolNumber;

                $scope.emailOptions.documents = [];
                var filename = [], docName = [];

                $scope.selectedDocs.forEach(function(item, index) {
                    $scope.emailOptions.documents.push({
                        imageMetadataId: item.id,
                        attachmentFileName: item.fileName
                    });
                    filename.push(item.fileName);
                    docName.push(item.name);
                });

                $scope.emailOptions.attachedFileName = filename.join(", ");
                $scope.emailOptions.docName = docName.join(", ");

                setMailOptions();

            };

            $scope.emailToDocument = function () {
                $scope.selectedDocs.forEach(function(item, index) {
                    if (item.tempDocId) {
                        var docExtension = item.docFileType ?
                                item.docFileType.split('/').pop() : item.fileName.split('.').pop();

                                item.fileName = item.name + '.' + docExtension;
                                item.id = item.tempDocId;
                    }
                });

                $scope.docOptions.hidePdf = true;
                openEmailDialog();
            };

            $scope.$on('event:email-dialog-closed', function () {
                $scope.docOptions.hidePdf = false;
            });

            $scope.getDocsTableData = function() {
                if ($scope.shipment.id) {
                    var service = $scope.shipment.isManualBol ? ManualBolDocumentService : ShipmentDocumentService;
                    service.getDocumentList({shipmentId: $scope.shipment.id}, function (data) {
                        $scope.docsGridData = data;
                        if (!$scope.shouldSaveTempDocuments) { // filter data for shipment details
                            $scope.docsGridData = _.filter(data, function (doc) {
                                return doc.createdByName === "Auto-generated" || _.findWhere($scope.docTypes, {label: doc.name})
                                        || _.findWhere($scope.allRequiredDocuments, {documentTypeDescription: doc.name});
                            });
                        }
                        addUploadedDocs();

                        if ($scope.docsGridData[0]) {
                            var docsData = angular.copy($scope.docsGridData);
                            $scope.docsGridData = docsData;

                            selectDocument(DocumentTypesEnum.BOL.value);
                            if (setDocumentOptions($scope.docOptions)
                                    && (!$scope.salesOrderModel || $scope.salesOrderModel.selectedTab === 'docs')) {
                                $scope.docOptions.hidePdf = false;
                            }
                        }

                        $scope.filterRequiredDocuments();
                    }, function () {
                        $scope.$root.$emit('event:application-error', 'Documents list for shipment empty', 'Documents for shipment with id:' +
                                $scope.shipment.id + ' were not found! ');
                    });
                }
            };

            function fillInRegeneratedData(dataArr, type) {
                if (dataArr) {
                    $scope.$root.$emit('event:operation-success', 'Success!', type.value + ' was regenerated.');

                    _.each(dataArr, function (data) {
                        var label = DocumentTypesEnum[data.label].value;
                        var ind = _.findIndex($scope.docsGridData, function (docItem) {
                            return docItem.name === label;
                        });

                        if (ind === -1) {
                            ind = $scope.docsGridData.length;
                        }

                        var docsData = angular.copy($scope.docsGridData);

                        docsData.splice(ind, 1);
                        docsData.unshift({
                            createdDate: new Date(),
                            name: label,
                            tempDocId: data.value,
                            docFileType: 'application/pdf',
                            createdByName: 'Auto-generated'
                        });

                        $scope.docsGridData = docsData;

                        $scope.docsGrid.options.selectedItems.length = 0;
                        $scope.docsGrid.options.selectedItems[0] = $scope.docsGridData[0];

                        if (setDocumentOptions($scope.docOptions)) {
                            $scope.docOptions.hidePdf = false;
                        }

                        if (!$scope.shipment.regeneratedDocTypes) {
                            $scope.shipment.regeneratedDocTypes = [];
                        }

                        $scope.shipment.regeneratedDocTypes.push(DocumentTypesEnum[data.label].name);
                    });
                }

                $scope.filterRequiredDocuments();
            }

            function regenerateDocument(type, hideCreatedTime) {
                ShipmentDocumentService.prepareDocsForShipment({
                    customerId: $scope.selectedCustomer.id,
                    subPathParam: type.name,
                    hideCreatedTime: hideCreatedTime
                }, $scope.shipment, function (dataArr) {
                    fillInRegeneratedData(dataArr, type);
                }, function () {
                    $scope.$root.$emit('event:application-error', type.value + ' regeneration failed!', 'Cannot regenerate ' + type.value);
                });
            }

            $scope.$watch('timeOnBol.hideCreatedTime', function() {
                    $scope.$emit('hideCreatedTime', $scope.timeOnBol.hideCreatedTime);
            });

            $scope.regenerateBol = function () {
                regenerateDocument(DocumentTypesEnum.BOL, $scope.timeOnBol.hideCreatedTime);
            };

            $scope.regenerateShippingLabels = function () {
                regenerateDocument(DocumentTypesEnum.SHIPPING_LABELS);
            };

            $scope.regenerateConsigneeInvoice = function () {
                ShipmentDocumentService.prepareConsigneeInvoiceForShipment({}, {
                    loadId : $scope.shipment.id,
                    markup : $scope.shipment.markup
                }, function (dataArr) {
                    fillInRegeneratedData(dataArr, DocumentTypesEnum.CONSIGNEE_INVOICE);
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Consignee Invoice' + ' regeneration failed!',
                            'Cannot regenerate ' + 'Consignee Invoice');
                });
            };

            function checkIfDocumentsCanBeDownloaded() {
                ShipmentDocumentService.canDownload({
                    shipmentId: $scope.shipment.id
                }, function (response) {
                    $scope.isDownloadEnabled = response.data;
                }, function () {
                    $scope.isDownloadEnabled = false;
                });
            }

            $scope.getDocumentsFromAPI = function () {
                if ($scope.shipment.id) {
                    ShipmentDocumentService.downloadDocuments({shipmentId: $scope.shipment.id}, function (data) {
                        $scope.docsGridData = data;
                        addUploadedDocs();
                        checkIfDocumentsCanBeDownloaded();
                    }, function () {
                        $scope.$root.$emit('event:application-error', 'Documents list for shipment is empty', 'Documents for shipment with id:' +
                                $scope.shipment.id + ' were not found! ');
                    });
                }
            };

            $scope.$watch('uploadModel.selectedDocType', function () {
                $scope.canAddDoc = $scope.checkSameDocuments();

                if (!$scope.canAddDoc) {
                    $scope.$root.$emit('event:application-error', 'Document add failed!',
                            'You can not add more than 10 documents of the same type.');
                }
            });

            $scope.checkSameDocuments = function () {
                if ($scope.uploadModel.selectedDocType && $scope.docsGridData.length > 9) {
                    var sameDocuments = _.where($scope.docsGridData, {
                        name: $scope.uploadModel.selectedDocType.label
                    });

                    if (sameDocuments.length > 9) {
                        return false;
                    }
                }

                return true;
            };

            $scope.$on('event:edit-sales-order-tab-close', function () {
                if ($scope.salesOrderModel.formDisabled) {
                    $scope.getDocsTableData();
                }
            });

            function showDocument() {
                if ($scope.docsGridData.length) {
                    $scope.docsGrid.options.selectedItems[0] = $scope.docsGridData[0];
    
                    if (setDocumentOptions($scope.docOptions)) {
                        $scope.docOptions.hidePdf = false;
                    }
                }
            }

            function prepareDocumentsForShipment() {
                if (_.contains(['OPEN', 'BOOKED', 'DISPATCHED'], $scope.shipment.status) && $scope.shouldSaveTempDocuments ) {
                    var subPathParams = DocumentTypesEnum.BOL.name + ',' + DocumentTypesEnum.SHIPPING_LABELS.name;

                    subPathParams = $scope.shipment.generateConsigneeInvoice ? subPathParams + ','
                                    + DocumentTypesEnum.CONSIGNEE_INVOICE.name : subPathParams;
                    $scope.docsGridData.length = 0;

                    ShipmentDocumentService.prepareDocsForShipment({
                        subPathParam: subPathParams
                    }, $scope.shipment, function (dataArr) {
                        if (dataArr) {
                            _.each(dataArr, function (data) {
                                $scope.docsGridData.push({
                                    createdDate: new Date(),
                                    name: DocumentTypesEnum[data.label].value,
                                    id: data.value,
                                    docFileType: 'application/pdf',
                                    createdByName: 'Auto-generated'
                                });
                            });
                            showDocument();
                            $scope.filterRequiredDocuments();
                        }
                    });
                }
                showDocument();
            }

            $scope.init = function() {
                if (!$scope.salesOrderModel) {
                    if ($scope.shouldSaveTempDocuments) {
                        prepareDocumentsForShipment();
                    } else {
                        $scope.getDocsTableData();
                    }
                } else {
                    $scope.$on('event:edit-sales-order-tab-change', function (event, tabId) {
                        if (tabId === 'docs') {
                            if ($scope.shipment.id) {
                                checkIfDocumentsCanBeDownloaded();
                            }
                            if (!$scope.docsTableDataLoaded) {
                                $scope.getDocsTableData();
                                $scope.docsTableDataLoaded = true;
                            } else if ($scope.docsGridData[0] && $scope.salesOrderModel.selectedTab === 'docs'
                                        && $scope.docsGrid.options.selectedItems.length < 2) {
                                $scope.docOptions.hidePdf = false;
                            }
                        } else {
                            $scope.docOptions.hidePdf = true;
                        }
                    });
                }
            };

            $scope.isCanEditFreightBillDate = function () {
                if (!$scope.docsTableDataLoaded && !$scope.step && $scope.shipment.billTo) {
                    $scope.getDocsTableData();
                    $scope.docsTableDataLoaded = true;
                    return true;
                } else {
                    return $scope.shipment.finishOrder.actualDeliveryDate
                            && $scope.shipment && $scope.shipment.isVendorBillMatched;
                }
            };

            $scope.$root.$on('event:vendorBillSaved', function (event, data) {
                $scope.shipment.isVendorBillMatched = true;
                $scope.shipment.vendorBillDate = data.vendorBillDate;
                updatefreightBillDate();
            });

            $scope.$on('event:datePickerBlurIE', function () {
                $scope.openDatePickerIE = false;
                $scope.$apply();
            });

            $scope.$on('event:datePickerShowIE', function () {
                $scope.openDatePickerIE = true;
                $scope.$apply();
            });

            $scope.clearUploadElement = function () {
                var fileElem = angular.element('#upload');

                fileElem.wrap('<form>').parent('form').trigger('reset');
                fileElem.unwrap();

                if ($scope.$root.browserDetect.browser === 'Explorer') {
                    fileElem.replaceWith(angular.element('#upload').clone(true));
                }
            };

            $scope.filterRequiredDocuments = function () {
                var uploadedDocumentNames = _.pluck($scope.docsGridData, 'name');

                $scope.requiredDocuments = _.filter($scope.allRequiredDocuments, function (doc) {
                    return _.indexOf(uploadedDocumentNames, doc.documentTypeDescription) === -1;
                });
            };

            $scope.disableDeleteButton = function() {
                if ($scope.selectedDocs[0]) {
                    return $scope.selectedDocs[0].name === 'BOL' || $scope.selectedDocs[0].name === 'Consignee Invoice'
                        || ($scope.selectedDocs[0].name === 'Shipping Label'
                        && _.contains(['OPEN', 'BOOKED', 'DISPATCHED'], $scope.shipment.status));
                } else {
                    return true;
                }
            };

            function deleteElementFromGrid() {
                var index = $scope.docsGridData.indexOf($scope.selectedDocs[0]);
                $scope.docsGridData.splice(index, 1);
                $scope.selectedDocs.splice(0, 1);
            }

            function setAfterDeleteOperations() {
                $scope.docOptions.hidePdf = true;
                deleteElementFromGrid();
                delete $scope.docOptions.pdfLocation;
                $scope.canAddDoc = $scope.checkSameDocuments();
                $scope.filterRequiredDocuments();
                $scope.isDocChangedByHand = true;
            }

            $scope.deleteDocument = function () {
                if ($scope.selectedDocs && $scope.selectedDocs[0]) {
                    if ($scope.shouldSaveTempDocuments ) {
                        if ($scope.selectedDocs[0].tempDocId) {
                            ShipmentDocumentService.removeTemporaryDocument({docId: $scope.selectedDocs[0].tempDocId}, function () {
                                $scope.shipment.uploadedDocuments = _.reject($scope.shipment.uploadedDocuments, function (el) {
                                    return el.id === $scope.selectedDocs[0].tempDocId;
                                });
                                if ($scope.isSelectedOnlyShippingLabelDocument()) {
                                    $scope.shipment.regeneratedDocTypes =
                                        _.without($scope.shipment.regeneratedDocTypes, DocumentTypesEnum.SHIPPING_LABELS.name);
                                }
                                setAfterDeleteOperations();
                            }, function () {
                                $scope.$root.$emit('event:application-error', 'Delete of shipment document failed!',
                                        'Cannot remove shipment temporary loaded document');
                            });
                        } else {
                            if (!$scope.shipment.removedDocumentsIds) {
                                $scope.shipment.removedDocumentsIds = [];
                            }
                            $scope.shipment.removedDocumentsIds.push($scope.selectedDocs[0].id);
                            setAfterDeleteOperations();
                        }
                    } else {
                        var id = $scope.selectedDocs[0].tempDocId !== undefined ? $scope.selectedDocs[0].tempDocId : $scope.selectedDocs[0].id;
                        ShipmentDocumentService.removeDocument({docId: id}, function() {
                            setAfterDeleteOperations();
                        }, function () {
                            $scope.$root.$emit('event:application-error', 'Delete of shipment document failed!',
                            'Cannot remove loaded document');
                        });
                    }
                }
            };
            $scope.getDocsTableData();
        }]
    };
}]);