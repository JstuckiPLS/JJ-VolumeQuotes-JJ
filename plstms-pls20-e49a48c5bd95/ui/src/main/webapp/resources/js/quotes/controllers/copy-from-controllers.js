angular.module('pls.controllers').controller('CopyFromCntrl', [
    '$scope', '$cookies', 'ShipmentDetailsService', 'NgGridPluginFactory','ShipmentOperationsService',
    function ($scope, $cookies, ShipmentDetailsService, NgGridPluginFactory,ShipmentOperationsService) {
        'use strict';

        $scope.wizardData = {};

        $scope.wizardData.selectedCustomer = {
            id: undefined
        };

        $scope.loadCopyFromData = function () {
            if (_.isUndefined($scope.wizardData) || _.isUndefined($scope.wizardData.selectedCustomer.id)) {
                return;
            }

            $scope.lastQueryParams = {};
            $scope.lastQueryParams.customerId = $scope.wizardData.selectedCustomer.id;
            $scope.lastQueryParams.count = !_.isUndefined($scope.rangeType) ? $scope.rangeType : '25';

            ShipmentDetailsService.last($scope.lastQueryParams, function (data) {
                $scope.copyFromGridData = data;
            });
        };

        /**
         * Open modal dialog.
         */
        $scope.$on('event:openCopyFromsDialog', function (event, customer) {
            if (customer) {
                $scope.wizardData.selectedCustomer = _.isUndefined(customer) ? undefined : customer;
            }

            $scope.showCopyFromDialog = true;
            $scope.rangeType = $cookies.rangeType ? $cookies.rangeType : '25';
            $scope.copyFromModel = {};

            $scope.$broadcast('event:clear-progressive-search');
        });

        /**
         * Discard changes and close dialog.
         */
        $scope.closeCopyFrom = function () {
            $cookies.rangeType = $scope.rangeType;
            $scope.rangeType = undefined;
            $scope.showCopyFromDialog = false;
            $scope.copyFromItems.length = 0;

            $scope.$emit('event:closeCopyFromDialog');
        };

        $scope.$watch('rangeType', function (rangeTypeNewVal) {
            if (rangeTypeNewVal) {
                $scope.loadCopyFromData();
            }
        });

        $scope.copyFromItems = [];

        $scope.copyFromItem = function () {
            if ($scope.$parent.shipmentEntryData) {
                $scope.copyItemForShipmentEntry();
            } else {
                $scope.copyForRateQuote();
            }
        };

        $scope.copyItemForShipmentEntry = function () {
            $scope.$root.ignoreLocationChange();

            var params = {
                loadId: $scope.copyFromItems[0].shipmentId,
                customerId: $scope.wizardData.selectedCustomer.id
            };

            $scope.closeCopyFrom();
            $scope.$root.redirectToUrl("/shipment-entry", params);
        };

        $scope.copyForRateQuote = function () {
            if (!_.isEmpty($scope.copyFromItems)) {
                ShipmentOperationsService.getCopiedShipment({
                    customerId: $scope.wizardData.selectedCustomer.id,
                    shipmentId: $scope.copyFromItems[0].shipmentId
                }, function (data) {
                    $scope.wizardData.shipment = data;
                    $scope.$root.$broadcast('event:copyShipment', $scope.wizardData.shipment);
                    $scope.wizardData.shipment.finishOrder.pickupDate = $scope.$root.formatDate(new Date());

                    if (angular.isDefined($scope.pickup)) {
                        $scope.pickup.country = $scope.wizardData.shipment.originDetails.zip.country;
                    }

                    if (angular.isDefined($scope.deliver)) {
                        $scope.deliver.country = $scope.wizardData.shipment.destinationDetails.zip.country;
                    }

                    $scope.closeCopyFrom();
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Shipment copy failed!', 'Can\'t copy selected shipment.');
                });
            }
        };

        $scope.copyFromGridOptions = {
            data: 'copyFromGridData',
            columnDefs: [{
                field: 'bolNumber',
                displayName: 'BOL',
                width: '11%'
            }, {
                field: 'proNumber',
                displayName: 'Pro#',
                width: '8%'
            }, {
                field: 'refNumber',
                displayName: 'Quote Ref#',
                width: '8%'
            }, {
                field: 'poNumber',
                displayName: 'PO#',
                width: '8%'
            }, {
                field: 'puNumber',
                displayName: 'PU#',
                width: '8%'
            }, {
                field: 'origin',
                cellFilter: 'zip',
                displayName: 'Origin',
                width: '12%'
            }, {
                field: 'destination',
                cellFilter: 'zip',
                displayName: 'Destination',
                width: '12%'
            }, {
                field: 'carrier',
                displayName: 'Carrier',
                width: '10%'
            }, {
                field: 'total',
                displayName: 'Total',
                cellFilter: 'plsCurrency',
                width: '7%'
            }, {
                field: 'createdDate',
                displayName: 'Date',
                width: '7%',
                cellFilter: 'date:appDateFormat'
            }, {
                field: 'status',
                cellFilter: 'shipmentStatus',
                displayName: 'Status',
                width: '7%'
            }],
            action: $scope.copyFromItem,
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin(), NgGridPluginFactory.progressiveSearchPlugin()],
            sortInfo: {fields: ['createdDate'], directions: ['desc']},
            enableColumnResize: true,
            multiSelect: false,
            selectedItems: $scope.copyFromItems,
            useExternalSorting: false,
            progressiveSearch: true
        };
    }
]);