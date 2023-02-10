angular.module('pls.controllers').controller('SOGeneralInfoCtrl', [
    '$scope', '$q', 'ZipService', 'CountryService', 'NgGridPluginFactory', 'ShipmentUtils','CustomerInternalNoteService',
    function ($scope, $q, ZipService, CountryService, NgGridPluginFactory, ShipmentUtils, CustomerInternalNoteService) {
        'use strict';

        $scope.maxCountOfProducts = ShipmentUtils.MAX_COUNT_OF_PRODUCTS;

        $scope.rateQuoteDictionary = ShipmentUtils.getDictionaryValues();

        if ($scope.wizardData.shipment.selectedProposition.carrier) {
            $scope.wizardData.carrierTuple = {
                id: $scope.wizardData.shipment.selectedProposition.carrier.id,
                name: $scope.wizardData.shipment.selectedProposition.carrier.scac + ':' + $scope.wizardData.shipment.selectedProposition.carrier.name,
                currencyCode: $scope.wizardData.shipment.selectedProposition.carrier.currencyCode,
                apiCapable: $scope.wizardData.shipment.selectedProposition.carrier.apiCapable
            };
        }

        function removeIdentifiers() {
            delete $scope.wizardData.shipment.bolNumber;
            delete $scope.wizardData.shipment.cargoValue;
            delete $scope.wizardData.shipment.proNumber;
            delete $scope.wizardData.shipment.finishOrder.glNumber;
            delete $scope.wizardData.shipment.finishOrder.poNumber;
            delete $scope.wizardData.shipment.finishOrder.puNumber;
            delete $scope.wizardData.shipment.finishOrder.ref;
            delete $scope.wizardData.shipment.finishOrder.soNumber;
            delete $scope.wizardData.shipment.finishOrder.trailerNumber;
        }

        $scope.$watch('wizardData.carrierTuple', function (carrierTuple) {
            if (carrierTuple) {
                if (!$scope.wizardData.shipment.selectedProposition.carrier
                        || $scope.wizardData.shipment.selectedProposition.carrier.id !== carrierTuple.id) {
                    var tupleSplitterIndex = carrierTuple.name.indexOf(':');

                    if (tupleSplitterIndex !== -1) {
                        $scope.wizardData.shipment.selectedProposition.carrier = {
                            id: carrierTuple.id,
                            name: carrierTuple.name.substr(tupleSplitterIndex + 1, carrierTuple.name.length - 1),
                            scac: carrierTuple.name.substr(0, tupleSplitterIndex),
                            currencyCode: carrierTuple.currencyCode,
                            apiCapable: carrierTuple.apiCapable
                        };

                        if ($scope.wizardData.shipment.billTo && $scope.wizardData.shipment.billTo.currency !== carrierTuple.currencyCode) {
                            $scope.wizardData.shipment.billTo = undefined;
                        }
                    }
                }
            } else {
                $scope.wizardData.shipment.selectedProposition.carrier = undefined;
            }
        });

        $scope.guaranteedTimeOptions = ShipmentUtils.getGuaranteedTimeOptions();

        $scope.$on('event:wasPrepayedEnough', function(event) {
            if ($scope.wizardData.shipment.status === "PENDING_PAYMENT") {
                $scope.wizardData.shipment.status = "DISPATCHED";
            }
        });

        /**
         * Clear guaranteed by option for cost detail items.
         */
        $scope.changeGuaranteed = function () {
            _.each($scope.wizardData.shipment.selectedProposition.costDetailItems, function (item) {
                item.guaranteedBy = undefined;
            });
        };

        $scope.setCCOCostDetails = function () {
            var cost = +$scope.carrierCostOverride;
            var cdItems = $scope.wizardData.shipment.selectedProposition.costDetailItems;
            
            var sraCd = cdItems.find(function (item){
                return item.refType === 'SRA';
            });
            if(!sraCd){
                sraCd = {refType: 'SRA', costDetailOwner: 'S', subTotal: cost, notes: ''};
                cdItems.push(sraCd);
            } else {
                sraCd.subTotal = cost;
            }

            var craCd = cdItems.find(function (item){
                return item.refType === 'CRA';
            });
            if(!craCd){
                craCd = {refType: 'CRA', costDetailOwner: 'C', subTotal: cost, notes: ''};
                cdItems.push(craCd);
            } else {
                craCd.subTotal = cost;
            }
        };

        $scope.originAndDestinationAreCanada = function () {
            return $scope.wizardData.shipment.originDetails.zip &&
                    $scope.wizardData.shipment.destinationDetails.zip &&
                    $scope.wizardData.shipment.originDetails.zip.country.id === "CAN" &&
                    $scope.wizardData.shipment.destinationDetails.zip.country.id === "CAN";
        };

        $scope.isGuaranteedValid = function () {
            var guaranteed = _.findWhere($scope.wizardData.shipment.selectedProposition.costDetailItems, {refType: 'GD'});

            if (guaranteed) {
                if ($scope.wizardData.shipment.guaranteedBy === undefined) {
                    $scope.soGeneralForm.guaranteedSelect.$setValidity('guaranteed', false);
                    return false;
                }
            } else {
                if ($scope.wizardData.shipment.guaranteedBy !== undefined) {
                    $scope.soGeneralForm.guaranteedSelect.$setValidity('guaranteed', false);
                    return false;
                }
            }

            if ($scope.soGeneralForm.guaranteedSelect) {
                $scope.soGeneralForm.guaranteedSelect.$setValidity('guaranteed', true);
            }

            return true;
        };

        $scope.shipmentStatuses = angular.copy(ShipmentUtils.getDictionaryValues().shipmentStatusEnum);

        if ($scope.wizardData.shipment.status !== "CANCELLED") {
            delete $scope.shipmentStatuses.CANCELLED;
        }

        $scope.init = function () {
            $scope.$watch('wizardData.selectedCustomer.id', function (newValue, oldValue) {

                if(newValue){
                    CustomerInternalNoteService.get({
                        customerId: newValue
                    }, function (data) {
                        $scope.wizardData.selectedCustomer.internalNote = data.data;
                    });
                }

                if (oldValue) {
                    $scope.oldCustomer = oldValue;
                }
                if (newValue && newValue !== $scope.wizardData.shipment.organizationId) {
                    $scope.wizardData.shipment.organizationId = newValue;

                    $scope.wizardData.shipment.finishOrder.quoteMaterials = _.filter($scope.wizardData.shipment.finishOrder.quoteMaterials,
                            function (material) {
                                return material.productId === undefined;
                            });

                    delete $scope.wizardData.shipment.billTo;
                    delete $scope.wizardData.shipment.location;
                    delete $scope.wizardData.shipment.originDetails.address;
                    delete $scope.wizardData.shipment.destinationDetails.address;
                    delete $scope.wizardData.shipment.customsBroker;
                    removeIdentifiers();

                    if (newValue && $scope.oldCustomer && $scope.oldCustomer !== newValue) {
                        $scope.$broadcast('event:pls-clear-form-data');
                    }

                    $scope.$root.$broadcast('event:customer-changed');
                }
            });
            if ($scope.wizardData.shipment.originDetails.zip && $scope.wizardData.shipment.originDetails.zip.country) {
                $scope.originCountry = {id: $scope.wizardData.shipment.originDetails.zip.country};
            }

            if ($scope.wizardData.shipment.destinationDetails.zip && $scope.wizardData.shipment.destinationDetails.zip.country) {
                $scope.destinationCountry = {id: $scope.wizardData.shipment.destinationDetails.zip.country};
            }
        };

        $scope.findZip = function (criteria, country, count) {
            return ZipService.findZip(criteria, country, count);
        };

        $scope.findCountry = function (criteria, count) {
            return CountryService.searchCountries(criteria, count);
        };

        if ($scope.wizardData.breadCrumbs && $scope.wizardData.breadCrumbs.map) {
            var stepObject = $scope.wizardData.breadCrumbs.map.general_information;

            stepObject.validNext = function () {
                return $scope.isGuaranteedValid() && $scope.soGeneralForm.$valid && $scope.wizardData.shipment.originDetails.zip
                        && $scope.wizardData.shipment.destinationDetails.zip;
            };

            stepObject.validateGeneralTab = function () {
                return true;
            };

            stepObject.nextAction = function () {
                if (!$scope.isGuaranteedValid()) {
                    return false;
                }

                var deferred = $q.defer();

                $scope.$on('event:pls-added-quote-item', function (event, quoteMaterialSize) {
                    if (quoteMaterialSize === 0) {
                        deferred.reject();

                        $scope.$root.$emit('event:application-error', 'Sales order general info step error!',
                                'Product Grid should contain at lease one product.');
                    } else {
                        deferred.resolve();
                    }
                });

                $scope.$broadcast('event:pls-add-quote-item');
                return deferred.promise;
            };
        }

        $scope.selectedCostDetails = [];
        $scope.costDetailsForGrid = [];

        function calculateTotals() {
            $scope.totalRevenue = 0;
            $scope.totalCost = 0;

            _.each($scope.wizardData.shipment.selectedProposition.costDetailItems, function (item) {
                if (item.costDetailOwner === 'S') {
                    $scope.totalRevenue += item.subTotal;
                } else if (item.costDetailOwner === 'C') {
                    $scope.totalCost += item.subTotal;
                }
            });
            $scope.wizardData.shipment.selectedProposition.totalShipperAmt = $scope.totalRevenue;
            $scope.wizardData.shipment.selectedProposition.totalCarrierAmt = $scope.totalCost;
        }

        $scope.$watch('wizardData.shipment.selectedProposition.costDetailItems', function () {
            calculateTotals();
            $scope.selectedCostDetails.length = 0; // valid way to empty an array

            if (!$scope.wizardData.shipment.selectedProposition.costDetailItems
                    || _.isEmpty($scope.wizardData.shipment.selectedProposition.costDetailItems)) {
                $scope.costDetailsForGrid = [];

                return;
            }

            var result = {};
            var keys = [];

            _.each($scope.wizardData.shipment.selectedProposition.costDetailItems, function (item) {
                var refType = item.refType;

                if (item.refType === 'CRA') {
                    refType = 'SRA';
                }

                if (item.costDetailOwner === 'S' || item.costDetailOwner === 'C') {
                    var field = item.costDetailOwner === 'S' ? 'revenue' : 'cost';
                    var fieldNote = field === 'revenue' ? 'revenueNote' : 'costNote';

                    if (!result[refType]) {
                        result[refType] = {};
                        keys.push(refType);
                    }

                    result[refType][field] = item.subTotal;
                    result[refType][fieldNote] = item.note;
                }
            });

            $scope.costDetailsForGrid = _.map(keys, function (key) {
                return {
                    refType: key,
                    revenue: result[key].revenue,
                    revenueNote: result[key].revenueNote,
                    cost: result[key].cost,
                    costNote: result[key].costNote
                };
            });
        }, true);

        $scope.costDetailsGrid = {
            enableColumnResize: true,
            data: 'costDetailsForGrid',
            selectedItems: $scope.selectedCostDetails,
            tabIndex: -10,
            columnDefs: [{
                field: 'refType',
                displayName: 'Description',
                width: '40%',
                cellFilter: 'refCodeAndDesc'
            }, {
                field: 'revenue',
                displayName: 'Revenue',
                width: '29%',
                cellFilter: 'plsCurrency'
            }, {
                field: 'cost',
                displayName: 'Cost',
                width: '28%',
                cellFilter: 'plsCurrency'
            }],
            plugins: [NgGridPluginFactory.plsGrid()],
            enableSorting: false,
            multiSelect: false
        };

        $scope.deleteSelectedCostDetails = function () {
            if ($scope.selectedCostDetails.length > 0) {
                var refTypes = _.pluck($scope.selectedCostDetails, 'refType');

                if (_.indexOf(refTypes, 'SRA') !== -1) {
                    refTypes.push('CRA');
                }

                $scope.wizardData.shipment.selectedProposition.costDetailItems =
                        _.filter($scope.wizardData.shipment.selectedProposition.costDetailItems, function (item) {
                            return item.costDetailOwner === 'B' || _.indexOf(refTypes, item.refType) === -1;
                        });
            }
        };

        $scope.getAccessorialTypesForPopup = function (edit) {
            $scope.accessorialTypes = _.flatten([{
                id: 'SRA',
                description: 'Base Rate',
                status: 'ACTIVE'
            }, $scope.$root.accessorialTypes], true);

            var usedAccessorialTypes = _.pluck($scope.costDetailsForGrid, 'refType');

            if (edit) {
                usedAccessorialTypes = _.without(usedAccessorialTypes, $scope.selectedCostDetails[0].refType);

                return _.filter($scope.accessorialTypes, function (item) {
                    return item && _.indexOf(usedAccessorialTypes, item.id) === -1;
                });
            } else {
                return _.filter($scope.accessorialTypes, function (item) {
                    return item && _.indexOf(usedAccessorialTypes, item.id) === -1 && item.status === 'ACTIVE';
                });
            }
        };

        $scope.addCostDetails = function () {
            $scope.isEdit = false;
            var parentDialog = $scope.editSalesOrderModel ? 'detailsDialogDiv' : undefined;

            $scope.$root.$broadcast('event:showAddEditCostDetails', {
                isEdit: $scope.isEdit,
                parentDialog: parentDialog,
                editedCostDetail: {},
                accessorialTypes: $scope.getAccessorialTypesForPopup($scope.isEdit)
            });
        };

        $scope.editCostDetails = function () {
            $scope.isEdit = true;
            var parentDialog = $scope.editSalesOrderModel ? 'detailsDialogDiv' : undefined;

            $scope.$root.$broadcast('event:showAddEditCostDetails', {
                isEdit: $scope.isEdit,
                parentDialog: parentDialog,
                editedCostDetail: angular.copy($scope.selectedCostDetails[0]),
                accessorialTypes: $scope.getAccessorialTypesForPopup($scope.isEdit)
            });
        };

        $scope.$on('event:saveCostDetails', function (event, editedCostDetail) {
            var costDetailItems = $scope.wizardData.shipment.selectedProposition.costDetailItems;

            if (!costDetailItems) {
                costDetailItems = [];
            }

            var index = costDetailItems.length;
            if ($scope.isEdit) {
                var refTypes = [$scope.selectedCostDetails[0].refType];

                if ($scope.selectedCostDetails[0].refType === 'SRA') {
                    refTypes.push('CRA');
                }

                for (index = 0; index < costDetailItems.length; index += 1) {
                    if (costDetailItems[index].costDetailOwner !== 'B' && _.indexOf(refTypes, costDetailItems[index].refType) !== -1) {
                        break;
                    }
                }

                costDetailItems = _.filter(costDetailItems, function (item) {
                    return item.costDetailOwner === 'B' || _.indexOf(refTypes, item.refType) === -1;
                });
            }

            // add cost detail items to the array at specified position
            costDetailItems.splice(index, 0, {
                refType: editedCostDetail.refType, costDetailOwner: 'S', subTotal: parseFloat(editedCostDetail.revenue),
                note: editedCostDetail.revenueNote
            }, {
                refType: editedCostDetail.refType === 'SRA' ? 'CRA' : editedCostDetail.refType, costDetailOwner: 'C',
                subTotal: parseFloat(editedCostDetail.cost), note: editedCostDetail.costNote
            });

            $scope.wizardData.shipment.selectedProposition.costDetailItems = costDetailItems;
        });

        // fill Pro# and loads version from Vendor Bill
        $scope.$root.$on('event:vendorBillSaved', function (event, data) {
            $scope.wizardData.shipment.proNumber = data.proNumber;
            $scope.wizardData.shipment.version = data.loadVersion;

            if ($scope.wizardData.shipmentBackup) {
                $scope.wizardData.shipmentBackup.proNumber = data.proNumber;
            }

            $scope.wizardData.shipment.isVendorBillMatched = true;

            if (!_.isEmpty(data.costDetailItems)) {
                $scope.wizardData.shipment.selectedProposition.costDetailItems = data.costDetailItems;
            }

            if (data.selectedPropositionId) {
                $scope.wizardData.shipment.selectedProposition.id = data.selectedPropositionId;
            }
        });

        $scope.today = new Date();
        $scope.maxPickupDate = $scope.today;
        $scope.maxDeliveryDate = $scope.today;

        $scope.$watch('wizardData.shipment.finishOrder.actualDeliveryDate', function (newValue) {
            if (newValue) {
                $scope.maxPickupDate = newValue;
            }
        });

        $('#customerInput').focus();
    }
]);