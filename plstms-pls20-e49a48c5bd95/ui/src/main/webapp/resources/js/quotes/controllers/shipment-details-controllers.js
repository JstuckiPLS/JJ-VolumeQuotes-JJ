/**
 * Controllers for Shipment details dialog.
 *
 * @author: Sergey Kirichenko
 * Date: 5/13/13
 * Time: 5:43 PM
 */
angular.module('plsApp').controller('ShipmentDetailsCtrl', ['$scope', '$rootScope', '$location', 'CostDetailsUtils',
    'ShipmentDocumentEmailService', 'ShipmentUtils', 'ManualBolService', 'manualBolModel', 
    'ShipmentOperationsService','ShipmentDocumentService', '$route',
    function ($scope, $rootScope, $location, CostDetailsUtils, ShipmentDocumentEmailService, ShipmentUtils, ManualBolService,
              manualBolModel, ShipmentOperationsService, ShipmentDocumentService, $route) {
        'use strict';

        $scope.fullViewDocModel = {
            fullViewDocOption: {
                height: '556px',
                pdfLocation: null,
                imageContent: false
            },
            showFullViewDocumentDialog: false,
            shipmentFullViewDocumentModalOptions: {
                parentDialog: 'shipmentDetailsDialogDiv'
            }
        };

        $scope.shipmentDetailsModel = {
            showShipmentDetailsDialog: false,
            selectedCustomer: undefined,
            notifications: [],
            shipmentDetailsModalOptions: {},
            shipmentRedirectNotificationModalOptions: {
                parentDialog: 'shipmentDetailsDialogDiv'
            },
            showSendMailDocumentDialog: false,
            shipment: {
                details: {}
            },
            closeHandler: null
        };

        $scope.emailOptions = {
            showSendEmailDialog: false,
            docSendMailModalOptions: {
                parentDialog: 'shipmentDetailsDialogDiv'
            }
        };
        $scope.isCanNotBook = ($scope.$root.authData.organization && $scope.$root.authData.organization.statusReason)
                && _.contains(['CREDIT_HOLD', 'TAX_ID_EMPTY'], $scope.$root.authData.organization.statusReason);

        function clearShipmentDetailsModel() {
            $scope.shipmentDetailsModel.shipment = {details: {}};
            $scope.shipmentDetailsModel.notifications = [];
            $scope.shipmentDetailsModel.closeHandler = undefined;
            $scope.shipmentDetailsModel.shipmentDetailsModalOptions = {};
            $scope.shipmentDetailsModel.shipmentTrackingGridData = [];
            $scope.shipmentDetailsModel.docsGridData = [];
        }

        $scope.isActiveShipment = function () {
            return $scope.shipmentDetailsModel.shipment && ($scope.shipmentDetailsModel.shipment.status === 'BOOKED'
                    || $scope.shipmentDetailsModel.shipment.status === 'DISPATCHED');
        };

        function getManualBol(dialogDetails) {
            ManualBolService.getManualBol({customerId: dialogDetails.customerId, shipmentId: dialogDetails.shipmentId}, function (response) {
                $scope.shipmentDetailsModel.shipment = manualBolModel.prepareBolForViewing(response);

                $scope.shipmentDetailsModel.selectedCustomer = {
                    id: $scope.shipmentDetailsModel.shipment.organizationId,
                    name: $scope.shipmentDetailsModel.shipment.customerName
                };

                $scope.shipmentDetailsModel.isAvailableCancel = ShipmentUtils.isShipmentCancellable($scope.shipmentDetailsModel.shipment);
                $scope.$broadcast('event:shipmentDetailsLoaded');
            }, function (error) {
                $scope.$root.$emit('event:application-error', 'Failed to load manual bol',
                        'Can\'t load load with ID ' + dialogDetails.shipmentId);
            });
        }

        function getShipment(dialogDetails) {
            ShipmentOperationsService.getShipment({
                customerId: dialogDetails.customerId || $scope.$root.authData.organization.orgId,
                shipmentId: dialogDetails.shipmentId
            }, function (data) {
                $scope.shipmentDetailsModel.shipment = data;

                $scope.shipmentDetailsModel.selectedCustomer = {
                    id: $scope.shipmentDetailsModel.shipment.organizationId,
                    name: $scope.shipmentDetailsModel.shipment.customerName
                };
                $scope.shipmentDetailsModel.isAvailableCancel = ShipmentUtils.isShipmentCancellable($scope.shipmentDetailsModel.shipment);
                $scope.$broadcast('event:shipmentDetailsLoaded');
            }, function () {
                $scope.$root.$emit('event:application-error', 'Shipment load failed!', 'Can\'t load shipment with ID ' + dialogDetails.shipmentId);
            });
        }

        $scope.$on('event:showShipmentDetails', function (event, dialogDetails) {
            $scope.shipmentDetailsModel.shipmentDetailsModalOptions.parentDialog = dialogDetails.parentDialog;
            $scope.shipmentDetailsModel.showShipmentDetailsDialog = true;
            $scope.shipmentDetailsModel.hideEditOrderButton =
                    _.isUndefined(dialogDetails.hideEditOrderButton) ? false : dialogDetails.hideEditOrderButton;
            $scope.shipmentDetailsModel.hideCopyOrderButton =
                    _.isUndefined(dialogDetails.hideCopyOrderButton) ? false : dialogDetails.hideCopyOrderButton;
            $scope.shipmentDetailsModel.selectedTab = dialogDetails.selectedTab || 'images';
            $scope.shipmentDetailsModel.closeHandler = dialogDetails.closeHandler || null;
            $scope.shipmentDetailsModel.isViewMode = dialogDetails.isViewMode || false;
            $scope.shipmentDetailsModel.notifications = angular.copy(ShipmentUtils.getDictionaryValues().notificationTypes);
            $scope.shipmentDetailsModel.isManualBol = dialogDetails.isManualBol;

            if (dialogDetails.isManualBol) {
                getManualBol(dialogDetails);
            } else {
                getShipment(dialogDetails);
            }

        });

        $scope.closeShipmentDetails = function (avoidHandler) {
            $scope.shipmentDetailsModel.showShipmentDetailsDialog = false;

            if (!avoidHandler && $scope.shipmentDetailsModel.closeHandler) {
                $scope.shipmentDetailsModel.closeHandler();
            }

            $(window).scrollTop(0);
            clearShipmentDetailsModel();
        };

        $scope.copyShipment = function () {
            $scope.$root.ignoreLocationChange();

            var params = {
                loadId: $scope.shipmentDetailsModel.shipment.id,
                customerId: $scope.shipmentDetailsModel.selectedCustomer.id
            };

            $scope.$root.redirectToUrl("/shipment-entry", params);
            $scope.shipmentDetailsModel.showShipmentDetailsDialog = false;
            clearShipmentDetailsModel();
        };

        function isBeforePickUpStatus() {
            var beforePickupStatuses = ['OPEN', 'BOOKED', 'DISPATCHED', 'CANCELLED'];
            return _.contains(beforePickupStatuses, $scope.shipmentDetailsModel.shipment.status);
        }

        $scope.isPermittedToEdit = function () {
            return ($scope.$root.isFieldRequired('EDIT_SHIPMENT_BEFORE_PICKUP') && isBeforePickUpStatus()) ||
                    ($scope.$root.isFieldRequired('EDIT_SHIPMENT_AFTER_PICKUP') && !isBeforePickUpStatus());
        };

        $scope.editShipment = function (step) {
            if (!$scope.isPermittedToEdit()) {
                $scope.$root.$emit('event:application-error', 'Shipment edit failed!', 'Can\'t edit not active shipment.');
                return;
            }
            $scope.$root.ignoreLocationChange();
            $location.search({});//remove all URL variables
            if($location.url() === "/shipment-entry/" + $scope.shipmentDetailsModel.shipment.id) {
                $route.reload();
            } else {
                $scope.$root.redirectToUrl("/shipment-entry/" + $scope.shipmentDetailsModel.shipment.id);
            }
        };

        $scope.editManualBol = function () {
            //$location.path('/manual-bol/general-information/' + $scope.shipmentDetailsModel.shipment.id);
            // $scope.closeShipmentDetails(false, false);
            manualBolModel.edit($scope.shipmentDetailsModel.shipment.organizationId, $scope.shipmentDetailsModel.shipment.id);
        };

        $scope.copyManualBol = function () {
            //$scope.closeShipmentDetails(false, false);
            //$location.path('/manual-bol/general-information');
            manualBolModel.copy($scope.shipmentDetailsModel.shipment.organizationId, $scope.shipmentDetailsModel.shipment.id);
        };

        $scope.cancelManualBol = function () {
            $scope.closeShipmentDetails(false, true);
            $location.path('/manual-bol/general-information');
        };

        $scope.canCancelManualBol = function () {
            return $rootScope.isFieldRequired('CANCEL_MANUAL_BOL');
        };

        $scope.closeNotification = function () {
            $scope.closeShipmentDetails(true);
            $scope.$emit('event:closeAndRedirect', '/quotes/quote', {
                shipmentId: $scope.shipmentDetailsModel.shipment.id,
                stepName: 'rate_quote'
            });

            $scope.showRedirectNotification = false;
        };

        $scope.cancelShipment = function () {
            if ($scope.shipmentDetailsModel.isManualBol) {
                $scope.closeShipmentDetails(false, true);
            } else {
                $scope.closeShipmentDetails();
            }
        };

        $scope.cancelIsDisabled = function () {
            return !isBeforePickUpStatus();
        };

        $scope.showConfirmationCancelDialog = function () {
            $scope.showConfirmationDialog = true;
        };

        $scope.getTotalCost = function () {
            if ($scope.shipmentDetailsModel && $scope.shipmentDetailsModel.shipment) {
                return CostDetailsUtils.getTotalCost($scope.shipmentDetailsModel.shipment.selectedProposition,
                        $scope.shipmentDetailsModel.shipment.guaranteedBy);
            }

            return undefined;
        };

        $scope.getCarrierTotalCost = function () {
            if ($scope.shipmentDetailsModel && $scope.shipmentDetailsModel.shipment) {
                return CostDetailsUtils.getCarrierTotalCost($scope.shipmentDetailsModel.shipment.selectedProposition,
                        $scope.shipmentDetailsModel.shipment.guaranteedBy);
            }

            return undefined;
        };
    }
]);

/**
 * Controller for tracking tab.
 */
angular.module('plsApp').controller('ShipmentDetailsTrackingCtrl', 
             ['$scope', '$templateCache', 'NgGridPluginFactory','ShipmentDetailsService',
    function ($scope, $templateCache, NgGridPluginFactory,ShipmentDetailsService) {
        'use strict';

        $scope.shipmentDetailsModel.selectedRows = [];

        $scope.initTab = function () {
            if ($scope.shipmentDetailsModel.shipment && $scope.shipmentDetailsModel.shipment.id) {
                ShipmentDetailsService.findShipmentEvents({
                    customerId: $scope.shipmentDetailsModel.selectedCustomer.id,
                    shipmentId: $scope.shipmentDetailsModel.shipment.id
                }, function (data) {
                    $scope.shipmentDetailsModel.shipmentTrackingGridData = data;
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Shipment load failed!', 'Can\'t load shipment tracking data.');
                });
            }
        };

        $scope.$on('event:shipmentDetailsLoaded', function () {
            $scope.initTab();
        });

        function onShowTooltip(row) {
            $scope.tooltipData = row.entity.event;
        }

        $scope.shipmentDetailsModel.shipmentTrackingGridOptions = {
            data: 'shipmentDetailsModel.shipmentTrackingGridData',
            columnDefs: [{
                field: 'date',
                displayName: 'Date/Time',
                width: '24%',
                cellFilter: 'date:appDateTimeFormat + " " + row.entity.timezoneCode'
            }, {
                field: 'fullName',
                displayName: 'User Name',
                width: '25%'
            }, {
                field: 'event',
                displayName: 'Event',
                width: '50%',
                showTooltip: true
            }],
            tooltipOptions: {
                url: 'ng-grid/eventPlaceHolder.html',
                onShow: onShowTooltip
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.tooltipPlugin(true)],
            enableColumnResize: true,
            multiSelect: false,
            selectedItems: $scope.shipmentDetailsModel.selectedRows,
            enableRowSelection: false,
            progressiveSearch: false
        };

        $templateCache.put('ng-grid/eventPlaceHolder.html', '<div class="container-fluid tooltip-container" data-ng-cloak>{{tooltipData}}</div>');
    }
]);

/**
 * Controller for details tab.
 */
angular.module('plsApp').controller('ShipmentDetailsSpecialitiesCtrl', ['$scope', function ($scope) {
    'use strict';

    $scope.shipmentDetailsModel.hazmatInfoType = null;
    $scope.shipmentDetailsModel.hasHazmat = false;
    $scope.shipmentDetailsModel.hazmatProduct = {};
    $scope.shipmentDetailsModel.hazmatProducts = [];
    $scope.shipmentDetailsModel.productPrimarySort = null;
    $scope.shipmentDetailsModel.shipmentNotifications = [];
    $scope.shipmentDetailsModel.selectedEmail = undefined;

    $scope.initTab = function () {
        if ($scope.shipmentDetailsModel.shipment && $scope.shipmentDetailsModel.shipment.id) {
            $scope.shipmentDetailsModel.shipmentNotifications = $scope.shipmentDetailsModel.shipment.finishOrder.shipmentNotifications;

            //select first notification if exist
            if ($scope.shipmentDetailsModel.shipmentNotifications && $scope.shipmentDetailsModel.shipmentNotifications.length > 0) {
                $scope.shipmentDetailsModel.selectedEmail = $scope.shipmentDetailsModel.shipmentNotifications[0].emailAddress;
            }

            //initialize quote materials
            if ($scope.shipmentDetailsModel.shipment.finishOrder.quoteMaterials) {
                angular.forEach($scope.shipmentDetailsModel.shipment.finishOrder.quoteMaterials, function (material) {
                    if (material.hazmat) {
                        $scope.shipmentDetailsModel.hazmatProducts.push(material);
                    }
                });
                if ($scope.shipmentDetailsModel.hazmatProducts.length > 0) {
                    $scope.shipmentDetailsModel.hasHazmat = true;
                    $scope.shipmentDetailsModel.hazmatProduct = $scope.shipmentDetailsModel.hazmatProducts[0];
                    $scope.shipmentDetailsModel.hazmatInfoType =
                            $scope.shipmentDetailsModel.hazmatProducts.length > 1 ? 'multy_hazmat' : 'single_hazmat';
                }
            }
        }
    };

    $scope.$on('event:shipmentDetailsLoaded', function () {
        $scope.initTab();
    });

    $scope.jobGrid = {
        data: 'shipmentDetailsModel.shipment.finishOrder.jobNumbers',
        enableSorting: false,
        enableRowSelection: false,
        columnDefs: [{
            field: 'jobNumber',
            displayName: 'Job#',
            width: '90%'
        }]
    };

    $scope.getEmails = function () {
        if ($scope.shipmentDetailsModel.shipmentNotifications) {
            return _.uniq(_.pluck($scope.shipmentDetailsModel.shipmentNotifications, 'emailAddress'));
        } else {
            return [];
        }
    };

    $scope.isVisibleJobGrid = function() {
        return $scope.$root.isFieldRequired('ADD_VIEW_JOB')
                && !$scope.$root.isBrandOrAluma($scope.shipmentDetailsModel.selectedCustomer.id);
    };

    $scope.isSelected = function (notificationType) {
        return _.findWhere($scope.shipmentDetailsModel.shipmentNotifications,
                        {notificationType: notificationType, emailAddress: $scope.shipmentDetailsModel.selectedEmail}) !== undefined;
    };

    $scope.notificationStyle = function (isSelected) {
        return isSelected ? {} : {visibility: 'hidden'};
    };
}]);

/**
 * Controller for order tab.
 */
angular.module('plsApp').controller('ShipmentDetailsOrderCtrl',
    ['$scope', 'NgGridPluginFactory', 'CostDetailsUtils', 'ShipmentUtils', 'QuotePermissionsService', 'ProductTotalsService',
    function ($scope, NgGridPluginFactory, CostDetailsUtils, ShipmentUtils, QuotePermissionsService, ProductTotalsService) {
        'use strict';

        $scope.initTab = function () {
            $scope.isCanNotBook = ($scope.$root.authData.organization && $scope.$root.authData.organization.statusReason)
                    && _.contains(['CREDIT_HOLD', 'TAX_ID_EMPTY'], $scope.$root.authData.organization.statusReason);

            if ($scope.shipmentDetailsModel.shipment && $scope.shipmentDetailsModel.shipment.id) {
                $scope.shipmentDetailsModel.showCustomsBroker = false;

                if ($scope.shipmentDetailsModel.shipment.originDetails && $scope.shipmentDetailsModel.shipment.originDetails.zip
                        && $scope.shipmentDetailsModel.shipment.originDetails.zip.country && $scope.shipmentDetailsModel.shipment.destinationDetails
                        && $scope.shipmentDetailsModel.shipment.destinationDetails.zip
                        && $scope.shipmentDetailsModel.shipment.destinationDetails.zip.country) {
                    var origCountryId = $scope.shipmentDetailsModel.shipment.originDetails.zip.country.id;
                    var destCountryId = $scope.shipmentDetailsModel.shipment.destinationDetails.zip.country.id;

                    $scope.shipmentDetailsModel.showCustomsBroker = origCountryId !== destCountryId;
                }
            }
        };

        $scope.$on('event:shipmentDetailsLoaded', function () {
            $scope.initTab();
        });

        $scope.hazmatInfo = {};
        $scope.getAccessorialsNames = ShipmentUtils.getAccessorialsNames;

        function isHazmat(entity) {
            return entity.hazmat;
        }

        function onShowTooltip(row) {
            if (row.entity.id || row.entity.productId) {
                $scope.hazmatInfo.exist = true;
                $scope.hazmatInfo.company = row.entity.emergencyResponseCompany;

                $scope.hazmatInfo.phone = {
                    emergencyResponsePhoneCountryCode: row.entity.emergencyResponsePhoneCountryCode,
                    emergencyResponsePhoneAreaCode: row.entity.emergencyResponsePhoneAreaCode,
                    emergencyResponsePhone: row.entity.emergencyResponsePhone,
                    emergencyResponsePhoneExtension: row.entity.emergencyResponsePhoneExtension
                };

                $scope.hazmatInfo.contractNum = row.entity.emergencyResponseContractNumber;
                $scope.hazmatInfo.unNum = row.entity.unNum;
                $scope.hazmatInfo.packingGroup = row.entity.packingGroup;
            } else {
                $scope.hazmatInfo.exist = false;
            }

            $scope.shipmentDetailsModel.materialsGrid.tooltipOptions.url = 'pages/content/quotes/hazmat-info-tooltip.html';
        }

        $scope.shipmentDetailsModel.materialsGrid = {
            enableColumnResize: true,
            enableRowSelection: false,
            data: 'shipmentDetailsModel.shipment.finishOrder.quoteMaterials',
            selectedItems: [],
            columnDefs: [
                {
                    field: 'self',
                    displayName: 'Weight',
                    width: '7%',
                    cellFilter: 'materialWeight',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'commodityClass',
                    displayName: 'Class',
                    width: '7%',
                    cellFilter: 'commodityClass',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'productDescription',
                    displayName: 'Product Description',
                    width: '15%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'productCode',
                    displayName: 'SKU/Product Code',
                    width: '15%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'nmfc',
                    displayName: 'NMFC',
                    width: '6%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'self',
                    displayName: 'Dimensions',
                    width: '10%',
                    cellFilter: 'materialDimension',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'quantity',
                    displayName: 'Qty',
                    width: '4%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'packageType',
                    displayName: 'Pack. Type',
                    width: '10%',
                    cellFilter: 'packageType',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'pieces',
                    displayName: 'Pieces',
                    width: '4%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'stackable',
                    displayName: 'Stackable',
                    width: '7%',
                    cellTemplate: 'pages/cellTemplate/checked-cell.html'
                },
                {
                    field: 'hazmat',
                    displayName: 'Hazmat',
                    width: '7%',
                    showTooltip: true,
                    cellTemplate: '<div class="ngCellText text-center"><i class="icon-warning-sign" data-ng-show="row.entity.hazmat"></i></div>'
                },
                {
                    field: 'hazmatClass',
                    displayName: 'Hazmat Class',
                    width: '10%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                }
            ],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.tooltipPlugin(true)],
            progressiveSearch: false,
            tooltipOptions: {
                showIf: isHazmat,
                onShow: onShowTooltip
            }
        };

        function calculateTotals() {
            $scope.totals = ProductTotalsService.calculateTotals($scope.shipmentDetailsModel.shipment.finishOrder.quoteMaterials);
        }

        calculateTotals();

        $scope.accessorialsDetailsVisibility = false;
        $scope.isEmptyAccessorialsDetails = false;

        $scope.$on('event:accessorialsDetails', function () {
            $scope.shipmentDetailsModel.accessorialsDetailsGrid.ngGrid.buildColumns();
            $scope.accessorialsDetailsVisibility = !$scope.accessorialsDetailsVisibility;
        });

        $scope.acceccorialsData = CostDetailsUtils.getAccessorials($scope.shipmentDetailsModel.shipment.selectedProposition, 'S',
                $scope.shipmentDetailsModel.shipment.guaranteedBy);

        if ($scope.acceccorialsData) {
            $scope.acceccorialsData = _.filter($scope.acceccorialsData, function (item) {
                return item.refType !== 'FS' && item.refType !== 'GD';
            });

            $scope.isEmptyAccessorialsDetails = !_.isEmpty($scope.acceccorialsData);

            if (QuotePermissionsService.showCarrierCost()) {
                _.each($scope.acceccorialsData, function (costDetailItem) {
                    costDetailItem.carrierCost = CostDetailsUtils.getSimilarCostDetailItem($scope.shipmentDetailsModel.shipment.selectedProposition,
                            costDetailItem).subTotal;
                });
            }
        }

        $scope.shipmentDetailsModel.accessorialsDetailsGrid = {
            enableColumnResize: true,
            enableRowSelection: false,
            data: 'acceccorialsData',
            selectedItems: [],
            columnDefs: [
                {
                    field: 'refType',
                    displayName: 'Accessorial',
                    width: '45%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    cellFilter: 'ref'
                },
                {
                    field: 'subTotal',
                    displayName: QuotePermissionsService.showCarrierCost() ? 'Revenue' : 'Cost',
                    width: '27%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    cellFilter: 'plsCurrency'
                },
                {
                    field: 'carrierCost',
                    displayName: 'Cost',
                    width: '27%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    cellFilter: 'plsCurrency',
                    visible: QuotePermissionsService.showCarrierCost()
                }
            ],
            plugins: [NgGridPluginFactory.plsGrid()],
            progressiveSearch: false
        };
    }
]);