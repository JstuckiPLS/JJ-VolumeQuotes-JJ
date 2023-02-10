angular.module('admin').controller('AdminController', ['$scope', 'AuditService', 'NgGridPluginFactory',
    function ($scope, AuditService, NgGridPluginFactory) {
        'use strict';

        $scope.logData = [];
        $scope.selectedLog = [];
        $scope.adminModel = {
            selectedTab: $scope.route.current.selectedTab
        };

        $scope.isSearchAvailable = function () {
            return ($scope.fromDate && $scope.toDate) || $scope.loadId || $scope.bolNumber || $scope.shipmentNum;
        };

        $scope.isTabSelected = function (tabName) {
            return $scope.adminModel.selectedTab === tabName;
        };

        $scope.logDetailsPopup = {
            show: false,
            close: function () {
                $scope.logDetailsPopup.show = false;
            }
        };

        $scope.viewMessage = function () {
            $scope.logDetailsPopup.show = true;
            AuditService.getLogDetails({auditId: $scope.selectedLog[0].auditId}, function (data) {
                $scope.logDetails = data;
            }, $scope.logDetailsPopup.show = true, function (data, status) {
                $scope.$root.$emit('event:application-error', 'Retriving Logs failed!', 'Can\'t retrive Logs: ' + status);
            });
        };

        $scope.refreshTable = function () {
            AuditService.getLogs({
                customer: $scope.customer ? $scope.customer.id : '',
                carrier: $scope.carrier ? $scope.carrier.id : '',
                dateFrom: $scope.fromDate,
                dateTo: $scope.toDate,
                messageType: $scope.messageType,
                bolNumber: $scope.bolNumber,
                shipmentNum: $scope.shipmentNum,
                loadId: $scope.loadId
            }, function (data) {
                $scope.logData = data;
            }, function (data, status) {
                $scope.$root.$emit('event:application-error', 'Retriving Logs failed!', 'Can\'t retrive Logs: ' + status);
            });
        };

        $scope.resubmit = function () {
            AuditService.resubmit({message: $scope.logDetails.message, auditId: $scope.selectedLog[0].auditId});
            $scope.logDetailsPopup.show = false;
        };

        $scope.resetSearch = function () {
            $scope.$broadcast('event:cleaning-input');
            $scope.selectedLog.length = 0;
            $scope.logData = [];
        };

        $scope.items = [{desc: 'Load Tender', type: 'LoadTender'},
            {desc: 'Load Tracking', type: 'LoadTracking'},
            {desc: 'Vendor Invoice', type: 'VendorInvoice'},
            {desc: 'Customer Invoice', type: 'CustomerInvoice'},
            {desc: 'Acknowledgement', type: 'Acknowledgement'},
            {desc: 'Load Tender Acknowledgement', type: 'LdTenderAcknowledgement'},
            {desc: 'Customer', type: 'Customer'},
            {desc: 'Vendor', type: 'Vendor'},
            {desc: 'AR', type: 'AR'},
            {desc: 'AP', type: 'AP'}];

        $scope.adminGrid = {
            enableColumnResize: true,
            data: 'logData',
            multiSelect: false,
            selectedItems: $scope.selectedLog,
            columnDefs: [
                {
                    field: 'auditId',
                    displayName: 'Audit ID',
                    width: '10%'
                },
                {
                    field: 'messageType',
                    displayName: 'Message Type',
                    width: '10%'
                },
                {
                    field: 'bol',
                    displayName: 'BOL',
                    width: '10%'
                },
                {
                    field: 'inbOtb',
                    displayName: 'Inb/Out',
                    width: '10%'
                },
                {
                    field: 'shipmentNumber',
                    displayName: 'Shipment Number',
                    width: '10%'
                },
                {
                    field: 'shipperOrgId',
                    displayName: 'Organization ID',
                    width: '10%'
                },
                {
                    field: 'createdBy',
                    displayName: 'Created By',
                    width: '10%'
                },
                {
                    field: 'createdDate',
                    displayName: 'Date Created',
                    width: '10%'
                },
                {
                    field: 'viewedBy',
                    displayName: 'Viewed By',
                    width: '10%'
                },
                {
                    field: 'viewedDate',
                    displayName: 'Date Viewed',
                    width: '10%'
                }
            ],
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.actionPlugin()],
            progressiveSearch: true
        };
    }
]);