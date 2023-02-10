angular.module('shipmentEntry').controller('SelectCarrierController', ['$scope', '$filter', 'NgGridPluginFactory', 'ShipmentsProposalService',
    'CostDetailsUtils', 'ShipmentUtils', 'SelectCarrFormValService', 'ShipmentDetailsService',
    function ($scope, $filter, NgGridPluginFactory, ShipmentsProposalService, CostDetailsUtils, ShipmentUtils, SelectCarrFormValService,
            ShipmentDetailsService) {
        'use strict';

        $scope.shipmentDirectionValues = ["I", "O"];
        $scope.toClean = false;
        $scope.paymentTermsValues = angular.copy(ShipmentUtils.getDictionaryValues().paymentTerms);
        $scope.shipmentEntryData.proposals = [];
        var originalProposals = [];
        $scope.shipmentEntryData.selectedPropositions = [];
        $scope.shipmentEntryData.locationForm = {};
        $scope.shipmentEntryData.billToForm = {};

        var selectFirstRow = function () {
            if ($scope.shipmentEntryData.carrierPropositionsGridData.length > 0) {
                $scope.shipmentEntryData.selectedPropositions[0] = $scope.shipmentEntryData.carrierPropositionsGridData[0];
                $scope.shipmentEntryData.shipment.selectedProposition = $scope.shipmentEntryData.carrierPropositionsGridData[0].origProposal;
                $scope.carrierPropositionsGrid.selectItem(0, true);
            }
        };

        $scope.$root.$on('event:productChanged', function () {
            $scope.shipmentEntryData.selectedPropositions = [];
            $scope.shipmentEntryData.carrierPropositionsGridData = [];
            $scope.shipmentEntryData.shipment.selectedProposition = undefined;
            $scope.activateRequote();
        });

        $scope.$root.$on('event:clearCarrierPropositions', function () {
            $scope.shipmentEntryData.selectedPropositions = [];
            $scope.shipmentEntryData.carrierPropositionsGridData = [];
            $scope.shipmentEntryData.shipment.selectedProposition = undefined;
            $scope.shipmentEntryData.proposals = [];
            originalProposals = [];
            $scope.shipmentEntryData.shipment.location = undefined;
            $scope.margins = angular.copy($scope.defaultMargins);
            $scope.shipmentEntryData.shipment.volumeQuoteID = undefined;
            $scope.activateRequote();

            $scope.carrierPropositionsGrid.$gridServices.DomUtilityService.RebuildGrid(
                    $scope.carrierPropositionsGrid.$gridScope,
                    $scope.carrierPropositionsGrid.ngGrid
            );
        });

        function updateShipperRate(proposal, revOverridePerc, margin) {
            margin = margin ? Number(Number(margin).toFixed(2)) : 0;
            if (revOverridePerc === 'marginPerc') {
                _.each(proposal.costDetailItems, function (shipperItem) {
                    if (shipperItem.costDetailOwner === 'S') {
                        _.each(proposal.costDetailItems, function (carrierItem) {
                            if (carrierItem.costDetailOwner === 'C' && (carrierItem.refType === shipperItem.refType ||
                                    (carrierItem.refType === 'CRA' && shipperItem.refType === 'SRA'))) {
                                if (shipperItem.refType === 'GD' && carrierItem.guaranteedBy === shipperItem.guaranteedBy) {
                                    shipperItem.subTotal = Number(Number(carrierItem.subTotal / (1 - (margin / 100))).toFixed(2));
                                } else if (shipperItem.refType !== 'GD') {
                                    shipperItem.subTotal = Number(Number(carrierItem.subTotal / (1 - (margin / 100))).toFixed(2));
                                }
                            }
                        });
                    }
                });
            } else {
                _.each(proposal.costDetailItems, function (shipperItem) {
                    if (shipperItem.costDetailOwner === 'S') {
                        _.each(proposal.costDetailItems, function (carrierItem) {
                            if (carrierItem.costDetailOwner === 'C' && carrierItem.refType === shipperItem.refType) {
                                if (shipperItem.refType === 'GD' && carrierItem.guaranteedBy === shipperItem.guaranteedBy) {
                                    shipperItem.subTotal = carrierItem.subTotal;
                                } else if (shipperItem.refType !== 'GD') {
                                    shipperItem.subTotal = carrierItem.subTotal;
                                }
                            } else if (carrierItem.refType === 'CRA' && shipperItem.refType === 'SRA') {
                                if (revOverridePerc === 'flatMarkup') {
                                    shipperItem.subTotal = carrierItem.subTotal + margin;
                                } else if (revOverridePerc === 'zeroRevOverride') {
                                    shipperItem.subTotal = carrierItem.subTotal;
                                }
                            }
                        });
                    }
                });
            }
        }

        function updateRateAndAccessorials(proposal, subTotal, owner, baseRateRefType) {
            _.each(proposal.costDetailItems, function (item) {
                if (item.costDetailOwner === owner) {
                    item.subTotal = item.refType === baseRateRefType ? subTotal : 0;
                }
            });
        }

        function updateCosts(proposal) {
            var revMargin = $scope.shipmentEntryData.shipment.margins.revenueOverride;

            proposal.revenueOverride = (revMargin !== null && revMargin !== undefined) ? 'YES' : 'NO';

            if ($scope.shipmentEntryData.shipment.margins.costOverride !== null &&
                    $scope.shipmentEntryData.shipment.margins.costOverride !== undefined) {
                proposal.costOverride = "YES";
            } else {
                proposal.costOverride = "NO";
            }

            if (!_.isUndefined($scope.shipmentEntryData.shipment.margins.costOverride) && _.isUndefined(revMargin)) {
                updateRateAndAccessorials(proposal, $scope.shipmentEntryData.shipment.margins.costOverride, 'C', 'CRA');

                var appliedLinehaulMargin = proposal.appliedLinehaulMarginAmt;

                if (_.isUndefined(appliedLinehaulMargin)) {
                    appliedLinehaulMargin = CostDetailsUtils.getBaseRate(proposal, 'S') - CostDetailsUtils.getBaseRate(proposal, 'C');
                }

                var subTotalSra;

                if (proposal.linehaulMarginPerc) {
                    subTotalSra = (100 * $scope.shipmentEntryData.shipment.margins.costOverride / (100 - proposal.linehaulMarginPerc));
                } else {
                    subTotalSra = appliedLinehaulMargin + $scope.shipmentEntryData.shipment.margins.costOverride;
                }

                updateRateAndAccessorials(proposal, subTotalSra, 'S', 'SRA');

                var diff = subTotalSra - $scope.shipmentEntryData.shipment.margins.costOverride;

                if (proposal.minLinehaulMarginAmt && diff < proposal.minLinehaulMarginAmt) {
                    subTotalSra = $scope.shipmentEntryData.shipment.margins.costOverride + proposal.minLinehaulMarginAmt;
                    updateRateAndAccessorials(proposal, subTotalSra, 'S', 'SRA');
                } else if (proposal.defaultMarginAmt && diff < proposal.defaultMarginAmt) {
                    subTotalSra = $scope.shipmentEntryData.shipment.margins.costOverride + proposal.defaultMarginAmt;
                    updateRateAndAccessorials(proposal, subTotalSra, 'S', 'SRA');
                }
            } else if (_.isUndefined($scope.shipmentEntryData.shipment.margins.costOverride)) {
                $scope.shipmentEntryData.shipment.volumeQuoteID = undefined;
            }

            if (!_.isUndefined(revMargin)) {
                if ($scope.shipmentEntryData.shipment.margins.revenueOverrideOption === 'totalRevenue' &&
                        _.isUndefined($scope.shipmentEntryData.shipment.margins.costOverride)) {
                    updateRateAndAccessorials(proposal, revMargin, 'S', 'SRA');
                } else if ($scope.shipmentEntryData.shipment.margins.revenueOverrideOption === 'totalRevenue'
                        && !_.isUndefined($scope.shipmentEntryData.shipment.margins.costOverride)) {
                    updateRateAndAccessorials(proposal, $scope.shipmentEntryData.shipment.margins.costOverride, 'C', 'CRA');
                    updateRateAndAccessorials(proposal, revMargin, 'S', 'SRA');
                } else if ($scope.shipmentEntryData.shipment.margins.revenueOverrideOption !== 'totalRevenue' &&
                        _.isUndefined($scope.shipmentEntryData.shipment.margins.costOverride)) {
                    if (revMargin === 0) {
                        updateShipperRate(proposal, 'zeroRevOverride', 0);
                    } else {
                        updateShipperRate(proposal, $scope.shipmentEntryData.shipment.margins.revenueOverrideOption, revMargin);
                    }
                } else {
                    updateRateAndAccessorials(proposal, $scope.shipmentEntryData.shipment.margins.costOverride, 'C', 'CRA');
                    if (revMargin === 0) {
                        updateShipperRate(proposal, 'zeroRevOverride', 0);
                    } else {
                        var subTotal =
                                $scope.shipmentEntryData.shipment.margins.revenueOverrideOption === 'marginPerc' ?
                                $scope.shipmentEntryData.shipment.margins.costOverride / (1 - (revMargin / 100)) :
                                $scope.shipmentEntryData.shipment.margins.costOverride + revMargin;
                        updateRateAndAccessorials(proposal, subTotal, 'S', 'SRA');
                    }
                }
            }
        }

        function prepareProposalsForGrid(proposals) {
            $scope.shipmentEntryData.carrierPropositionsGridData = [];

            angular.forEach(proposals, function (proposal) {
                if ($scope.shipmentEntryData.shipment.margins) {
                    updateCosts(proposal);
                }

                var selectedGuaranteed = CostDetailsUtils.getSelectedGuaranteed(proposal, $scope.shipmentEntryData.shipment.guaranteedBy);

                if (!_.isUndefined(proposal.carrier)) {
                    var processedProposal = {
                        logoPath: proposal.carrier.logoPath,
                        carrierName: proposal.carrier.name,
                        serviceType: proposal.serviceType,
                        estimatedTransitDate: _.isUndefined(proposal.estimatedTransitDate) ?
                                                            'N/A' : $scope.parseISODate(proposal.estimatedTransitDate),
                        estimatedTransitTime: proposal.estimatedTransitTime === 0 ? 'N/A' : proposal.estimatedTransitTime,
                        liability: $filter('plsCurrency')(proposal.newLiability) + '/' +
                        $filter('plsCurrency')(proposal.usedLiability),
                        currencyCode: proposal.carrier.currencyCode,
                        origProposal: proposal,
                        totalCost: CostDetailsUtils.getTotalCost(proposal, $scope.shipmentEntryData.shipment.guaranteedBy),
                        integrationType: proposal.integrationType,
                        ratingCarrierType: proposal.ratingCarrierType,
                        shipmentType: proposal.shipmentType,
                        serviceLevelCode: proposal.serviceLevelCode,
                        serviceLevelDescription: proposal.serviceLevelDescription
                    };

                    if (!proposal.hideCostDetails || $scope.$root.isFieldRequired('CAN_VIEW_HIDDEN_RATES')) {
                        if ($scope.showItemByPermissions()) {
                            processedProposal.totalCostText = $filter('plsCurrency')(processedProposal.totalCost) + "/" +
                                    $filter('plsCurrency')(CostDetailsUtils.getCarrierTotalCost(proposal,
                                            $scope.shipmentEntryData.shipment.guaranteedBy));
                        } else if ($scope.$root.isFieldRequired('VIEW_PLS_CUSTOMER_COST')) {
                            processedProposal.totalCostText = $filter('plsCurrency')(processedProposal.totalCost);
                        }
                    }

                    processedProposal.guaranteedBy = (selectedGuaranteed && selectedGuaranteed.subTotal) ?
                            selectedGuaranteed.guaranteedBy : $scope.shipmentEntryData.shipment.guaranteedBy;

                    $scope.shipmentEntryData.carrierPropositionsGridData.push(processedProposal);
                }
            });
        }

        function updateCostDetails() {
            if ($scope.shipmentEntryData.proposals && $scope.shipmentEntryData.proposals.length > 0 && !$scope.toClean) {
                $scope.shipmentEntryData.proposals = angular.copy(originalProposals);

                _.each($scope.shipmentEntryData.proposals, function (proposal) {
                    updateCosts(proposal);
                });

                prepareProposalsForGrid(angular.copy(originalProposals));
                $scope.sortCarrierPropositions();

                if ($scope.shipmentEntryData.selectedPropositions.length) {
                    $scope.shipmentEntryData.shipment.selectedProposition = _.find($scope.shipmentEntryData.proposals, function (proposal) {
                        return $scope.shipmentEntryData.shipment.selectedProposition.guid === proposal.guid;
                    });
                }
            }

            $scope.isGuaranteed = CostDetailsUtils.isGuaranteed;
            $scope.getAvailableGuaranteedSorted = CostDetailsUtils.getAvailableGuaranteedSorted;
            $scope.getCostDetailItemsForTotal = CostDetailsUtils.getCostDetailItemsForTotal;
            $scope.getSelectedGuaranteed = CostDetailsUtils.getSelectedGuaranteed;
            $scope.getSimilarCostDetailItem = CostDetailsUtils.getSimilarCostDetailItem;
            $scope.getTotalCost = CostDetailsUtils.getTotalCost;
            $scope.getCarrierTotalCost = CostDetailsUtils.getCarrierTotalCost;
            $scope.calculateCost = CostDetailsUtils.calculateCost;
            $scope.getAccessorials = CostDetailsUtils.getAccessorials;
            $scope.hazmatInfo = {};
            $scope.getBaseRate = CostDetailsUtils.getBaseRate;
            $scope.getFuelSurcharge = CostDetailsUtils.getFuelSurcharge;
            $scope.getAccessorialsExcludingFuel = CostDetailsUtils.getAccessorialsExcludingFuel;
            $scope.getAccessorialsRefType = CostDetailsUtils.getAccessorialsRefType;
            $scope.getNote = CostDetailsUtils.getNote;
            $scope.getGuranteedBy = CostDetailsUtils.getGuranteedBy;
            $scope.getItemCost = CostDetailsUtils.getItemCost;

            $scope.$watch('shipmentEntryData.shipment.selectedProposition.carrier.currencyCode', function (newValue) {
                if (newValue) {
                    $scope.$broadcast('event:initBillTo');
                }
            }, true);

            function updateCustomsBroker() {
                if ($scope.shipmentEntryData.shipment.billTo && $scope.shipmentEntryData.shipment.billTo.customsBroker) {
                    $scope.shipmentEntryData.shipment.customsBroker = {
                        name: $scope.shipmentEntryData.shipment.billTo.customsBroker,
                        phone: $scope.shipmentEntryData.shipment.billTo.brokerPhone
                    };
                }
            }

            $scope.$watch('shipmentEntryData.shipment.billTo', function () {
                updateCustomsBroker();
            });

            if (!$scope.shipmentEntryData.shipment.guaranteedBy && $scope.shipmentEntryData.shipment.guaranteedBy !== 0) {
                $scope.shipmentEntryData.shipment.guaranteedBy = undefined;
            }

            $scope.guaranteedTimeOptions = ShipmentUtils.getGuaranteedTimeOptions();

            $scope.toClean = false;
        }

        $scope.showItemByPermissions = function () {
            return $scope.$root.isFieldRequired('EDIT_PLS_REVENUE') || $scope.$root.isFieldRequired('EDIT_CARRIER_COST')
                    || $scope.$root.isFieldRequired('VIEW_PLS_REVENUE_AND_CARRIER_COST');
        };

        // init model for the margin calculation drop downs
        $scope.defaultMargins = {
            revenueOverrideOption: 'marginPerc',
            revenueOverride: undefined,
            costOverride: undefined
        };

        $scope.setFormValidity = function () {
            if ($scope.selectCarrierForm) {
                SelectCarrFormValService.setFormValidity($scope.selectCarrierForm.$valid);
            }
        };

        $scope.$on('onValidation', function () {
            $scope.setFormValidity();
        });

        $scope.$watch('shipmentEntryData.shipment.margins.revenueOverrideOption', function () {
            if ($scope.shipmentEntryData.shipment.margins) {
                $scope.shipmentEntryData.shipment.margins.revenueOverride = undefined;

                if ($scope.shipmentEntryData.shipment.margins.revenueOverrideOption === "marginPerc") {
                    $scope.forbidZero = false;
                    $scope.revOverrideNumPattern = 'positivePercentage';
                }

                if ($scope.shipmentEntryData.shipment.margins.revenueOverrideOption === "flatMarkup") {
                    $scope.forbidZero = false;
                    $scope.revOverrideNumPattern = 'positiveZeroDecimal';
                }

                if ($scope.shipmentEntryData.shipment.margins.revenueOverrideOption === "totalRevenue") {
                    $scope.forbidZero = true;
                    $scope.revOverrideNumPattern = 'positiveZeroDecimal';
                }
            }
        });

        $scope.$watch(function () {
            if (!$scope.shipmentEntryData.shipment.margins) {
                $scope.shipmentEntryData.shipment.margins = angular.copy($scope.defaultMargins);
            }

            var result = 'c';

            if ($scope.shipmentEntryData.shipment.margins.costOverride !== undefined &&
                    $scope.shipmentEntryData.shipment.margins.costOverride > 0) {
                result = result + Number($scope.shipmentEntryData.shipment.margins.costOverride).toFixed(2);
            }

            result += 'r';

            if ($scope.shipmentEntryData.shipment.margins.revenueOverride !== undefined &&
                    $scope.shipmentEntryData.shipment.margins.revenueOverride >= 0) {
                result = result + Number($scope.shipmentEntryData.shipment.margins.revenueOverride).toFixed(2);
            }

            return result;
        }, updateCostDetails);

        $scope.$watch(function () {
            var popoverElement = $("[data-rel=popover]");

            if (popoverElement.length) {
                popoverElement.popover();
            }
        });

        $scope.sortCarrierPropositions = function () {
            if ($scope.shipmentEntryData.sortBy === 'estimatedTransitTime') {
                // For Customer Users we sort by total cost on server because we don't show prices of Tier1 carriers!
                $scope.shipmentEntryData.carrierPropositionsGridData =
                        _.sortBy($scope.shipmentEntryData.carrierPropositionsGridData, $scope.shipmentEntryData.sortBy);
            } else {
                prepareProposalsForGrid(angular.copy(originalProposals));
            }
        };

        function showEmptyPropositionDialog() {
            $scope.shipmentEntryData.shipment.guaranteedBy = undefined;
            $scope.showPickupMessage();
            $scope.$root.$broadcast('event:productChanged');
        }

        function updateCarrierPropositionsGrid(data) {
            if (($scope.shipmentEntryData.shipment.guaranteedBy !== undefined && !$scope.carrierPropositionsGrid.$gridScope.columns[4].visible) ||
                    ($scope.shipmentEntryData.shipment.guaranteedBy === undefined && $scope.carrierPropositionsGrid.$gridScope.columns[4].visible)) {
                $scope.carrierPropositionsGrid.$gridScope.columns[4].toggleVisible();
            }

            if ($scope.shipmentEntryData.shipment.margins && ($scope.shipmentEntryData.shipment.margins.costOverride ||
                    $scope.shipmentEntryData.shipment.margins.revenueOverride)) {
                updateCostDetails();
            } else {
                prepareProposalsForGrid(data);
                $scope.sortCarrierPropositions();
            }

            setTimeout(selectFirstRow, 0);
        }

        $scope.$on('event:pls-clear-form-data', function () {
            $scope.toClean = true;

            if ($scope.margins) {
                $scope.margins = angular.copy($scope.defaultMargins);
            }

            $scope.shipmentEntryData.carrierPropositionsGridData = [];
        });

        $scope.$on('event:set-proposition', function (event, propostion) {
            var propositions = [];
            propositions.push(propostion);
            $scope.shipmentEntryData.proposals = propositions;
            originalProposals = angular.copy($scope.shipmentEntryData.proposals);
            updateCarrierPropositionsGrid($scope.shipmentEntryData.proposals);
        });

        $scope.$on('event:get-quote', function (event, shipmentToSend) {
            $scope.shipmentEntryData.shipment.margins = angular.copy($scope.defaultMargins);

            if ($scope.shipmentEntryData.shipment.margins && !$scope.shipmentEntryData.shipment.margins.costOverride) {
                $scope.shipmentEntryData.shipment.volumeQuoteID = undefined;
            }

            $scope.isCanNotBook = ($scope.$root.authData.organization && $scope.$root.authData.organization.statusReason )
                    && _.contains(['CREDIT_HOLD', 'TAX_ID_EMPTY'], $scope.$root.authData.organization.statusReason);

            ShipmentsProposalService.findShipmentPropositions({guid: $scope.shipmentEntryData.shipment.guid}, shipmentToSend, function (data) {
                $scope.shipmentEntryData.proposals = data;
                originalProposals = angular.copy($scope.shipmentEntryData.proposals);

                if (data && angular.isArray(data) && data.length > 0) {
                    angular.forEach($scope.shipmentEntryData.proposals, function (proposal) {
                        if ($scope.shipmentEntryData.shipment.guaranteedBy && (!proposal.hideCostDetails || $scope.authData.plsUser
                                || $scope.$root.isFieldRequired('CAN_VIEW_HIDDEN_RATES'))) {
                            proposal.addlGuaranInfo = $scope.getAddlGuaranInfo(proposal);
                        }
                    });

                    updateCarrierPropositionsGrid(data);
                } else {
                    showEmptyPropositionDialog();
                }
            }, function () {
                showEmptyPropositionDialog();
            });
        });

        $scope.showPickupMessage = function () {
            $scope.pickUpWindowOpen = true;
        };

        $scope.getSelectedPropositionGuaranteed = function () {
            if ($scope.shipmentEntryData.shipment.guaranteedBy && $scope.shipmentEntryData.shipment.selectedProposition &&
                    (!$scope.shipmentEntryData.shipment.selectedProposition.hideCostDetails || $scope.authData.plsUser
                    || $scope.$root.isFieldRequired('CAN_VIEW_HIDDEN_RATES'))) {
                var guaranteed = $scope.getAddlGuaranInfo($scope.shipmentEntryData.shipment.selectedProposition);

                if ($scope.shipmentEntryData.shipment.selectedProposition.guaranteedNameForBOL) {
                    if (guaranteed.length) {
                        guaranteed += '&#10;&#10;';
                    }

                    guaranteed += 'Guaranteed name for BOL: ' + $scope.shipmentEntryData.shipment.selectedProposition.guaranteedNameForBOL;
                }

                return guaranteed;
            }

            return '';
        };

        $scope.getAddlGuaranInfo = function (proposition) {
            return _.map($scope.getAvailableGuaranteedSorted(proposition.costDetailItems, 'S'), function (acc) {
                        var result = $filter('longTime')(acc.guaranteedBy) + '&nbsp;';

                        if ($scope.authData.plsUser) {
                            result += $filter('plsCurrency')($scope.getSimilarCostDetailItem(proposition, acc, false).subTotal);
                            result += '&nbsp;/&nbsp;';
                        }

                        result += $filter('plsCurrency')(acc.subTotal);
                        return result;
                    }).join('; ') || '';
        };

        function showTotalCostColumn() {
            return $scope.$root.isFieldRequired('EDIT_PLS_REVENUE') ||
                    $scope.$root.isFieldRequired('EDIT_CARRIER_COST') ||
                    $scope.$root.isFieldRequired('VIEW_PLS_REVENUE_AND_CARRIER_COST') ||
                    $scope.$root.isFieldRequired('VIEW_PLS_CUSTOMER_COST');
        }

        $scope.carrierPropositionsGrid = {
            data: 'shipmentEntryData.carrierPropositionsGridData',
            selectedItems: $scope.shipmentEntryData.selectedPropositions,
            primaryKey: 'origProposal.guid',
            columnDefs: [
                {
                    width: '5%',
                    cellClass: 'text-center gridImageCell',
                    cellTemplate: '<div style="background-image: url({{row.entity.logoPath}});" class="gridImageCell"><div/>'
                },
                {
                    field: 'carrierName',
                    displayName: 'Carrier Name',
                    width: '18%',
                    cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
                    '<i class="fa fa-envelope-o" data-ng-show="row.entity.integrationType == \'MANUAL\'"></i>&nbsp;'+
                    '<i class="pls-integration-ltllc-icon" data-ng-show="row.entity.ratingCarrierType == \'LTLLC\' && authData.plsUser"></i>&nbsp;' +
                    '<i class="pls-vltl-icon" data-ng-show="row.entity.shipmentType == \'VLTL\'"></i>&nbsp;' +
                    '<a href="" data-ng-click="showTerminalInformation(row.entity.origProposal)">{{row.getProperty(col.field)}}</a></div>'
                },
                {
                    field: 'serviceType',
                    displayName: 'Direct/Indirect',
                    width: '9%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'liability',
                    headerCellTemplate: 'pages/cellTemplate/max-liability-header-cell.html',
                    displayName: '',
                    resizable: true,
                    width: '13%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    visible: $scope.$root.isFieldRequired('CAN_VIEW_LIABILITY')
                },
                {
                    field: 'guaranteedBy',
                    displayName: 'Guaranteed',
                    cellFilter: 'longTime',
                    width: '9%',
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    visible: false // column should be rendered only when guaranteedBy option other than 'None' is selected
                },
                {
                    field: 'estimatedTransitDate',
                    displayName: 'Transit Est. Date',
                    width: '12%',
                    cellFilter: 'date:wideAppDateFormat',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'estimatedTransitTime',
                    displayName: 'Transit Est. Time',
                    width: '8%',
                    cellFilter: 'minutesTime',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'totalCostText',
                    displayName: ($scope.showItemByPermissions() ? 'Total Revenue / Total Cost' : 'Cost'),
                    width: '14%',
                    cellClass:'text-center',
                    headerClass:'text-center',
                    visible: showTotalCostColumn(),
                    cellTemplate: 'pages/cellTemplate/blocked-from-booking-cell.html'
                },
                {
                    field: 'currencyCode',
                    displayName: 'Currency Code',
                    width: '6%',
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    field: 'details',
                    displayName: 'Details',
                    width: '6%',
                    cellTemplate: '<div class="ngCellText" data-ng-class="col.colIndex()">' +
                    '<a href="" data-ng-click="showCarrierDetails(row.entity, shipmentEntryData.shipment)">Details</a></div>',
                    visible: $scope.$root.isFieldRequired('VIEW_CARRIER_DETAILS')
                }
            ],
            afterSelectionChange: function (item) {
                $scope.shipmentEntryData.shipment.selectedProposition = item.entity.origProposal;
            },
            plugins: [NgGridPluginFactory.actionPlugin(), NgGridPluginFactory.plsGrid()],
            enableColumnResize: true,
            enableSorting: false,
            multiSelect: false
        };

        $scope.isRevenueOverrideDisabled = function () {
            return !$scope.$root.isFieldRequired('EDIT_PLS_REVENUE');
        };

        $scope.showCarrierDetails = function (shipmentData, shipment) {
            if (shipmentData.origProposal.pricingDetails === undefined) {
                ShipmentDetailsService.findShipmentPricingDetails({
                    customerId: $scope.$root.authData.organization.orgId,
                    shipmentId: shipment.id
                }, function (pricingDetailsData) {
                    if (pricingDetailsData.effectiveDate === undefined) {
                        $scope.pricingDetailsWindowOpen = true;
                    } else {
                        shipmentData.origProposal.pricingDetails = pricingDetailsData;
                        ShipmentUtils.fillNmfcAndQtyFields(shipmentData.origProposal.pricingDetails, shipment.finishOrder.quoteMaterials);
                        $scope.setProgressText('Loading...');
                        $scope.$broadcast('event:openCarrierDetailsForPreparedCriteria', shipmentData);
                    }
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Shipment cost details load failed!',
                                       'Can\'t load cost details with shipment ID ' + shipment.id);
                });
            } else {
                ShipmentUtils.fillNmfcAndQtyFields(shipmentData.origProposal.pricingDetails, shipment.finishOrder.quoteMaterials);
                $scope.setProgressText('Loading...');
                $scope.$broadcast('event:openCarrierDetailsForPreparedCriteria', shipmentData);
            }
        };
    }
]);
