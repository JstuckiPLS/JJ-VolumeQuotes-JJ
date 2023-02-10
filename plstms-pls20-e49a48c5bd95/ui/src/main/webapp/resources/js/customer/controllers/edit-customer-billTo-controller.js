angular.module('editCustomer').controller('EditCustomerBillToCtrl', [
    '$scope', '$timeout', '$routeParams', 'BillToService', 'DictionaryService', 'NgGridPluginFactory', 'ShipmentUtils', 'DateTimeUtils',
    function ($scope, $timeout, $routeParams, BillToService, DictionaryService, NgGridPluginFactory, ShipmentUtils, DateTimeUtils) {
        'use strict';

        $scope.billToListModel = {
            billToData: [],
            selectedBillTos: [],
            selectedTab: 'address',
            ediSettings: {
                ediStatuses: [],
                ediTypes: []
            },
            requiredFields: [],
            sortTypes: [
                {id: 'LOAD_ID', value: 'Load ID'},
                {id: 'GL_NUM', value: 'GL#'},
                {id: 'DELIV_DATE', value: 'Delivery Date'},
                {id: 'BOL', value: 'BOL'}],
            xlsDocuments: [
                {label: 'None', value: 'NONE'},
                {label: 'Standard Excel', value: 'STANDARD_EXCEL'},
                {label: 'Excel Grouped by GL#', value: 'GROUPED_BY_GL_NUMBER_EXCEL'},
                {label: 'Flex Version', value: 'FLEX_VERSION_EXCEL'},
                {label: 'Customized Inbound/Outbound', value: 'CUSTOMIZED_EXCEL'}],
            pdfDocuments: [
                {label: 'None', value: 'NONE'},
                {label: 'Standard Multi Transactional', value: 'PDF'}],
            creditLimit: undefined
        };

        $scope.$watch(function () {
            return ShipmentUtils.getDictionaryValues().shipmentStatusEnum;
        }, function (newValue) {
            if (!_.isUndefined(newValue)) {
                $scope.billToListModel.ediSettings.ediStatuses = _.pairs(angular.copy(newValue));
            }
        });

        $scope.$watch(function () {
            return ShipmentUtils.getDictionaryValues().ediType;
        }, function (newValue) {
            if (!_.isUndefined(newValue)) {
                $scope.billToListModel.ediSettings.ediTypes = _.pairs(angular.copy(newValue));
            }
        });

        $scope.$watch(function () {
            return ShipmentUtils.getDictionaryValues().billToRequiredField;
        }, function (newValue) {
            if (!_.isUndefined(newValue)) {
                var requiredFields = angular.copy(newValue);

                $scope.billToListModel.requiredFields = _.map(requiredFields, function (field) {
                    return [field.value, field.label];
                });
            }
        });

        DictionaryService.getCustomerPayTerms({}, function (data) {
            if (data && data.length) {
                $scope.billToListModel.payTerms = data;
            }
        });

        DictionaryService.getCustomerPayMethods({}, function (data) {
            if (data && data.length) {
                $scope.billToListModel.payMethods = data;
                $scope.billToListModel.payMethods.unshift({label: '', value: ""});
            }
        });

        $scope.getSortTypeValue = function (sortTypeId) {
            var sortType = _.findWhere($scope.billToListModel.sortTypes, {id: sortTypeId}) || {};
            return sortType.value || '';
        };

        $scope.getDocumentLabel = function (allDocuments) {
            var i = 0;

            if ($scope.billToListModel.selectedBillTos && $scope.billToListModel.selectedBillTos.length
                    && $scope.billToListModel.selectedBillTos[0].invoicePreferences
                    && $scope.billToListModel.selectedBillTos[0].invoicePreferences.documents
                    && $scope.billToListModel.selectedBillTos[0].invoicePreferences.documents.length) {
                for (; i < $scope.billToListModel.selectedBillTos[0].invoicePreferences.documents.length; i += 1) {
                    var document = _.findWhere(allDocuments, {value: $scope.billToListModel.selectedBillTos[0].invoicePreferences.documents[i]});

                    if (document) {
                        return document.label || '';
                    }
                }
            }

            return 'None';
        };

        $scope.getEDISettings = function () {
            return $scope.billToListModel.selectedBillTos.length ? $scope.billToListModel.selectedBillTos[0].ediSettings : {};
        };

        $scope.getSelectedBillTo = function () {
            return $scope.billToListModel.selectedBillTos.length ? $scope.billToListModel.selectedBillTos[0] : {};
        };

        $scope.getPayTermsDescription = function (payTermsId) {
            var payTerms = _.findWhere($scope.billToListModel.payTerms, {key: payTermsId}) || {};
            return payTerms.value || '';
        };
        
        $scope.getPayMethodDescription = function (paymentMethod) {
            var payMethods = _.findWhere($scope.billToListModel.payMethods, {value: paymentMethod}) || {};
            return payMethods.label || '';
        };

        if ($routeParams.billToId && /^\d+$/.test($routeParams.billToId)) {
            $scope.billToListModel.billToId = parseInt($routeParams.billToId, 10);
        }

        function openCustomersEditBillTo() {
            $scope.billToListModel.selectedBillTos[0] = _.findWhere($scope.billToListModel.billToData, {id: $scope.billToListModel.billToId});

            //clean up variables after use
            delete $scope.billToListModel.billToId;

            if (!$scope.$root.isFieldRequired('CUSTOMER_PROFILE_VIEW')) {
                return;
            }

            $scope.$broadcast('event:showEditBillToDialog', $scope.billToListModel.selectedBillTos[0].id, true);

            //Get the index of the selected row to highlight
            var index = _.indexOf($scope.billToListModel.billToData, $scope.billToListModel.selectedBillTos[0]);

            //To select the row in BillTo Grid
            $scope.billToGrid.options.selectRow(index, true);

            //To Automatically scroll the grid to the selected Row
            var grid = $scope.billToGrid.options.ngGrid;
            grid.$viewport.scrollTop(grid.rowMap[index] * grid.config.rowHeight);
        }

        function refreshBillToTable() {
            BillToService.list({customerId: $scope.editCustomerModel.customerId, userId: -1}, function (data) {
                $scope.billToListModel.billToData = _.sortBy(data, function (billTo) {
                    return billTo.address.addressName;
                });

                _.each($scope.billToListModel.billToData, function (billTo) {
                    if (billTo.address && !_.isEmpty(billTo.address.fax) && !billTo.address.fax.countryCode) {
                        billTo.address.fax.countryCode = billTo.address.country.dialingCode;
                    }

                    if (billTo.address && !_.isEmpty(billTo.address.phone) && !billTo.address.phone.countryCode) {
                        billTo.address.phone.countryCode = billTo.address.country.dialingCode;
                    }
                });

                if (!_.isUndefined($scope.billToListModel.billToId)) {
                    $timeout(openCustomersEditBillTo, 100);
                }
            }, function () {
                $scope.$root.$emit('event:application-error', 'Bill To load failed!', 'Can\'t load Bill To for customer with ID '
                        + $scope.editCustomerModel.customerId);
            });
        }

        refreshBillToTable();

        $scope.convertTime = function (timeInMinutes) {
            if (timeInMinutes !== undefined) {
                return DateTimeUtils.pickupWindowFromTimeInMinutes(timeInMinutes);
            }
        };

        $scope.billToGrid = {
            options: {
                data: 'billToListModel.billToData',
                selectedItems: $scope.billToListModel.selectedBillTos,
                columnDefs: [
                    {
                        field: 'address.addressName',
                        displayName: 'Address Name',
                        width: '20%'
                    },
                    {
                        field: 'currency',
                        displayName: 'Curr. Code',
                        width: '10%'
                    },
                    {
                        field: 'address.address1',
                        displayName: 'Address',
                        width: '33%'
                    },
                    {
                        field: 'address.zip.city',
                        displayName: 'City',
                        width: '20%'
                    },
                    {
                        field: 'address.zip.state',
                        displayName: 'ST',
                        width: '5%'
                    },
                    {
                        field: 'address.zip.zip',
                        displayName: 'ZIP',
                        width: '10%'
                    }
                ],
                action: function () {
                    if ($scope.$root.isFieldRequired('CUSTOMER_PROFILE_VIEW')) {
                        $scope.editBillTo();
                    }
                },
                plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.actionPlugin()],
                enableColumnResize: true,
                multiSelect: false,
                sortInfo: {
                    fields: ['address.addressName'],
                    directions: ['asc']
                }
            }
        };

        $scope.reqDocsGrid = {
            gridOptions: {
                enableColumnResize: true,
                data: 'billToListModel.selectedBillTos[0].invoicePreferences.requiredDocuments',
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
                        cellFilter: 'requiredDocument'
                    }
                ],
                plugins: [NgGridPluginFactory.plsGrid()]
            }
        };

        $scope.addBillTo = function () {
            $scope.$broadcast('event:showAddBillToDialog');
        };

        $scope.editBillTo = function () {
            if ($scope.billToListModel.selectedBillTos.length) {
                $scope.$broadcast('event:showEditBillToDialog', $scope.billToListModel.selectedBillTos[0].id);
            }
        };

        $scope.$on('event:billToSavedOrUpdated', function () {
            $scope.billToListModel.selectedBillTos.length = 0;
            refreshBillToTable();
        });
    }
]);