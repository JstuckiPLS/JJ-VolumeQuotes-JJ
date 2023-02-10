angular.module('pls.controllers').controller('SOTrackingCtrl', 
             ['$scope', '$templateCache', 'NgGridPluginFactory','ShipmentDetailsService',
    function ($scope, $templateCache, NgGridPluginFactory, ShipmentDetailsService) {
        'use strict';

        function onShowTooltip(row) {
            $scope.tooltipData = row.entity.event;
        }

        $scope.editSalesOrderModel.shipmentTrackingGridOptions = {
            data: 'wizardData.shipmentTrackingGridData',
            columnDefs: [{
                field: 'date',
                displayName: 'Date/Time',
                width: '23%',
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
            enableRowSelection: false,
            progressiveSearch: false
        };

        $templateCache.put('ng-grid/eventPlaceHolder.html', '<div class="container-fluid tooltip-container" data-ng-cloak>{{tooltipData}}</div>');

        $scope.$on('event:edit-sales-order-tab-change', function (event, tabId) {
            if (tabId === 'tracking' && $scope.wizardData.shipment.id && !$scope.wizardData.shipmentTrackingGridData) {
                ShipmentDetailsService.findShipmentEvents({
                    customerId: $scope.authData.organization.orgId,
                    shipmentId: $scope.wizardData.shipment.id
                }, function (data) {
                    $scope.wizardData.shipmentTrackingGridData = data;
                }, function () {
                    $scope.$root.$emit('event:application-error', 'Shipment tracking data load failed!', 'Can\'t load shipment tracking data.');
                });
            }
        });

        $scope.$on('event:clear-tracking-shipmentTrackingGridData', function(event, item) {
            delete $scope.wizardData.shipmentTrackingGridData;
        });
    }
]);