angular.module('manualBol.controllers').controller('ManualBolDocsController', [
    '$scope', '$routeParams', '$location', 'manualBolModel', 'ShipmentDocumentService',
    'NgGridPluginFactory', 'urlConfig', 'ManualBolService', 'ShipmentUtils',
    function ($scope, $routeParams, $location, manualBolModel, ShipmentDocumentService,
            NgGridPluginFactory, urlConfig, ManualBolService, ShipmentUtils) {
        'use strict';

        $scope.$root.ignoreLocationChangeFlag = false;
        $scope.docsPage = {
            previous: '/manual-bol/details',
            bolModel: manualBolModel
        };

        var DocumentTypesEnum = {
            BOL: {name: 'BOL', value: 'BOL'},
            SHIPPING_LABELS: {name: 'SHIPPING_LABELS', value: 'Shipping Label'}
        };

        $scope.printTypes = angular.copy(ShipmentUtils.getDictionaryValues().printTypes);

        if (!$scope.docsPage.bolModel.docsGridData) {
            $scope.docsPage.bolModel.docsGridData = [];
        }

        $scope.selectedDocs = [];
        $scope.docOptions = {};
        $scope.docOptions.width = '100%';
        $scope.docOptions.height = '450px';
        $scope.docOptions.imageContent = false;
        $scope.docOptions.pdfLocation = null;
        $scope.docOptions.hidePdf = true;

        $scope.fullViewDocModel = {
            showFullViewDocumentDialog: false,
            fullViewDocOption: {
                height: '500px',
                pdfLocation: null,
                imageContent: false
            },
            shipmentFullViewDocumentModalOptions: {
                parentDialog: 'detailsDialogDiv'
            }
        };

        function setDocumentOptions(options) {
            if ($scope.selectedDocs && $scope.selectedDocs[0]) {
                options.imageContent = $scope.isDocumentImage();

                options.pdfLocation = urlConfig.shipment + '/customer/shipmentdocs/';

                if ($scope.selectedDocs[0].tempDocId) { // tempDocId - custom field that need for temporary loaded docs
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

        $scope.showDocumentContent = function () {
            if (setDocumentOptions($scope.docOptions)) {
                $scope.docOptions.hidePdf = false;
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
            var shipment = angular.copy($scope.docsPage.bolModel.shipment);
            shipment.status = undefined;
            shipment.organizationId = $scope.docsPage.bolModel.selectedCustomer.id;

            $scope.docOptions.url = urlConfig.shipment + '/customer/shipmentdocs/';
            if ($scope.selectedDocs[0].tempDocId) {
                $scope.docOptions.url += $scope.selectedDocs[0].tempDocId;
            } else {
                $scope.docOptions.url += $scope.selectedDocs[0].id;
            }

            if ($scope.isSelectedOnlyShippingLabelDocument()) {
                var requestData = {
                    customerId: $scope.docsPage.bolModel.selectedCustomer.id,
                    subPathParam: DocumentTypesEnum.SHIPPING_LABELS.name,
                    printType: $scope.selectedPrintType.value
                };
                $scope.docOptions.printDocument({requestData: requestData, shipment: shipment});
            } else {
                $scope.docOptions.printDocument();
            }
        };

        $scope.docsGridColumnDefs = [
            {
                field: 'name',
                displayName: 'Documents',
                width: '40%'
            },
            {
                field: 'createdByName',
                displayName: 'Uploaded By',
                width: '40%'
            },
            {
                field: 'createdDate',
                displayName: 'Date',
                width: '19%',
                cellFilter: 'date:appDateFormat'
            }
        ];

        $scope.docsGrid = {
            sort: '',
            filter: '',
            options: {
                data: 'docsPage.bolModel.docsGridData',
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
                enableColumnResize: true,
                useExternalSorting: false,
                multiSelect: false
            }
        };

        function isTiffExtension(fileExtension) {
            return fileExtension.toLowerCase() === 'tiff' || fileExtension.toLowerCase() === 'tif';
        }

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

        function prepareDocumentsForShipment() {
            var shipment = angular.copy($scope.docsPage.bolModel.shipment);
            shipment.status = undefined;
            shipment.organizationId = $scope.docsPage.bolModel.selectedCustomer.id;
            $scope.docsPage.bolModel.docsGridData.length = 0;

            ShipmentDocumentService.prepareDocsForShipment({
                subPathParam: DocumentTypesEnum.BOL.name + ',' + DocumentTypesEnum.SHIPPING_LABELS.name,
                isManualBol: true
            }, shipment, function (dataArr) {
                if (dataArr) {
                    _.each(dataArr, function (data) {
                        $scope.docsPage.bolModel.docsGridData.push({
                            createdDate: new Date(),
                            name: DocumentTypesEnum[data.label].value,
                            tempDocId: data.value,
                            docFileType: 'application/pdf',
                            createdByName: 'Auto-generated'
                        });

                        if (data.label === 'BOL') {
                            $scope.storedBolId = data.value;
                        } else {
                            $scope.storedLabelId = data.value;
                        }
                    });
                    $scope.docsGrid.options.selectedItems[0] = $scope.docsPage.bolModel.docsGridData[0];

                    if (setDocumentOptions($scope.docOptions)) {
                        $scope.docOptions.hidePdf = false;
                    }
                }
            });

            if ($scope.docsPage.bolModel.docsGridData[0]) {
                $scope.docsGrid.options.selectedItems[0] = $scope.docsPage.bolModel.docsGridData[0];

                if (setDocumentOptions($scope.docOptions)) {
                    $scope.docOptions.hidePdf = false;
                }
            }
        }

        prepareDocumentsForShipment();

        $scope.done = function () {
            $scope.$root.ignoreLocationChange();
            ManualBolService.saveBol({
                customerId: $scope.docsPage.bolModel.selectedCustomer.id,
                storedBolId: $scope.storedBolId,
                storedLabelId: $scope.storedLabelId
            }, manualBolModel.prepareBolForSaving($routeParams.id), function (response) {
                var manualBol = response;

                var shipmentDetails = {
                    isManualBol: true,
                    shipmentId: manualBol.id,
                    customerId: $scope.docsPage.bolModel.selectedCustomer.id,
                    customerName: $scope.docsPage.bolModel.selectedCustomer.name,
                    bol: manualBol.bol,
                    closeHandler: function () {
                        $scope.progressPanelOptions.showPanel = false;
                        manualBolModel.init();

                        if ($scope.docsPage.bolModel.redirectToTrackingBoard) {
                            $location.path('/trackingBoard/manualBol');
                        } else {
                            $location.path('/manual-bol/general-information');
                        }

                        $scope.docsPage.bolModel.redirectToTrackingBoard = false;
                    }
                };

                $scope.$broadcast('event:showShipmentDetails', shipmentDetails);

                $scope.$root.$emit('event:operation-success', 'Shipment has been successfully saved.',
                        'This shipment will not be electronically communicated to the carrier. '
                        + 'Please inform the carrier through your regular channels.<br/>'
                        + 'Load ID: ' + manualBol.id);
            }, function (error) {
                $scope.$root.$emit('event:application-error', 'Failed to save manual bol!', 'Can\'t save manual bol. ');
            });

            $scope.docOptions.hidePdf = true;
        };
    }
]);