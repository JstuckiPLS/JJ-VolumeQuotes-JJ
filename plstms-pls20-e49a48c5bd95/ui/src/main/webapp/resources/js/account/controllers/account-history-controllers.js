var BasedOnEnum = {
    BOOKED_DATE: 'BOOKED DATE',
    PICKUP_DATE: 'PICKUP DATE',
    DELIVERY_DATE: 'DELIVERY DATE'
};

var CalendarShipmentDetailsDateRange = {
    SINGLE_DAY: 'SINGLE_DAY',
    WEEK: 'WEEK',
    MONTH: 'MONTH'
};

angular.module('plsApp').controller('AccountHistoryCalendarCtrl', [
    '$scope', '$filter', '$log', 'AccountCalendarService', 'DateTimeUtils', '$location',
    function ($scope, $filter, $log, AccountCalendarService, DateTimeUtils, $location) {
        'use strict';

        $scope.updateCalendarIndex = function () {
            $scope.calendarData.index += 1; //JSLint doesn't allow to use ++ operation
        };

        function adjustDate(date) {
            var days = 8;
            return DateTimeUtils.addDays(date, days);
        }

        /**
         * Prepare date for service.
         * Calendar gives us visible date - in many case it can be date in previous month.
         * However our services works with current, so we need to adjust it.
         *
         * @param start visible date
         * @returns formatted date in current month
         */
        function prepareDateForRequest(start) {
            var date = adjustDate(start);
            return $scope.$root.formatDate(date);
        }

        function formatCurrency(currency) {
            return $filter('plsCurrency')(currency);
        }

        function buildDayShipmentsLabel(calendarDayDTO) {
            return calendarDayDTO.totalCount + " / " + formatCurrency(calendarDayDTO.totalCost);
        }

        function prepareCalendarData(data) {
            var events = [];
            angular.forEach(data, function (value) {
                events.push({
                    title: buildDayShipmentsLabel(value),
                    start: value.exactDate,
                    data: value,
                    dayData: true
                });

                if (value.monthlyTotal) {
                    events.push({
                        title: buildDayShipmentsLabel(value.monthlyTotal),
                        start: value.exactDate,
                        data: value,
                        color: $scope.account.monthlyColor,
                        monthData: true
                    });
                }

                if (value.weeklyTotal) {
                    events.push({
                        title: buildDayShipmentsLabel(value.weeklyTotal),
                        start: value.exactDate,
                        data: value,
                        color: $scope.account.weeklyColor,
                        weekData: true
                    });
                }
            });

            return events;
        }

        function openDetailsDialog(date, monthData, weekData) {
            $scope.detailsDialog.date = date;
            if (monthData) {
                $scope.detailsDialog.period = CalendarShipmentDetailsDateRange.MONTH;
            } else if (weekData) {
                $scope.detailsDialog.period = CalendarShipmentDetailsDateRange.WEEK;
            } else {
                $scope.detailsDialog.period = CalendarShipmentDetailsDateRange.SINGLE_DAY;
            }

            $scope.detailsDialog.open();
        }

        $scope.$on('event:closeAndRedirect', function (event, url, params) {
            $scope.detailsDialog.close();
            $location.url(url).search(params);
        });

        $scope.account = {
            basedOnValues: _.values(BasedOnEnum),
            showGridTooltip: false,
            basedOn: BasedOnEnum.BOOKED_DATE,
            viewDate: new Date(),
            weeklyColor: 'lightsalmon',
            monthlyColor: 'lightgreen'
        };

        $scope.events = function (start, end, callback) {
            AccountCalendarService.get({
                customerId: $scope.authData.organization.orgId,
                userId: $scope.authData.personId,
                basedOn: $scope.account.basedOn,
                dateOfMonth: prepareDateForRequest(start)
            }, function (data) {
                var events = prepareCalendarData(data);
                $scope.events = events;
                callback(events);
                $scope.calendarData.startDate = adjustDate(start);
                $scope.currentMonthYear = new Date($scope.calendarData.startDate.getFullYear(), $scope.calendarData.startDate.getMonth(), 1);
            });

            if ($scope.$$phase !== '$digest' && $scope.$$phase !== '$apply') {
                $scope.$digest();
            }
        };

        $scope.$watch('account.basedOn', function () {
            $('#account-calendar').fullCalendar('refetchEvents');
        });

        $scope.updateCalendar = function (updatedDate) {
            $('#account-calendar').fullCalendar('gotoDate', updatedDate.getFullYear(), updatedDate.getMonth());
        };

        $scope.calendarData = {
            index: 0,
            options: {
                height: 450,
                header: {center: 'prev,title, next', left: '', right: ''},
                columnFormat: {
                    month: 'dddd'
                },
                eventClick: function (event) {
                    var total;
                    if (event.monthData) {
                        total = event.data.monthlyTotal;
                    } else if (event.weekData) {
                        total = event.data.weeklyTotal;
                    } else if (event.dayData) {
                        total = event.data;
                    }
                    if (total && total.totalCount !== 0) {
                        openDetailsDialog(event.start, event.monthData, event.weekData);
                        $scope.$digest();
                    }
                }
            },
            startDate: undefined
        };

        $scope.calendarData.sources = [$scope.events];

        $scope.detailsDialog = {
            shouldBeOpen: false,
            open: function () {
                $scope.detailsDialog.shouldBeOpen = true;
            },
            close: function () {
                $scope.detailsDialog.shouldBeOpen = false;
            }
        };
    }
]);

angular.module('plsApp').controller('AccountCalendarDetailsCtrl', ['$scope', '$log', '$window', 'urlConfig', 'AccountCalendarService',
    'DateTimeUtils', 'NgGridPluginFactory', 'ExportDataBuilder', 'ExportService', 'ShipmentDetailsService',
    function ($scope, $log, $window, urlConfig, AccountCalendarService, DateTimeUtils, NgGridPluginFactory, ExportDataBuilder,
              ExportService, ShipmentDetailsService) {
        'use strict';

        var urlToExport = '/customer/' + $scope.authData.organization.orgId + '/user/' + $scope.authData.personId + '/account/calendar/export';

        $scope.formattedDatePeriod = '';

        $scope.prepareDatePeriod = function () {
            var formattedDatePeriod;
            if ($scope.detailsDialog.period === CalendarShipmentDetailsDateRange.WEEK) {

                var firstDayOfWeek = DateTimeUtils.getFirstDayOfWeek($scope.detailsDialog.date);
                var lastDayOfWeek = DateTimeUtils.addDays(firstDayOfWeek, DateTimeUtils.DAYS_IN_WEEK - 1);
                var monthPartFormat = 'MMM dd';
                var datePartFormat = 'dd';
                var yearPartFormat = 'yyyy';
                if (firstDayOfWeek.getMonth() === lastDayOfWeek.getMonth()) {
                    formattedDatePeriod = $scope.$root.formatDate(firstDayOfWeek, monthPartFormat);
                    formattedDatePeriod += " - " + $scope.$root.formatDate(lastDayOfWeek, datePartFormat);
                } else {
                    formattedDatePeriod = $scope.$root.formatDate(firstDayOfWeek, monthPartFormat);
                    formattedDatePeriod += " - " + $scope.$root.formatDate(lastDayOfWeek, monthPartFormat);
                }

                formattedDatePeriod += ', ' + $scope.$root.formatDate(firstDayOfWeek, yearPartFormat);
            } else if ($scope.detailsDialog.period === CalendarShipmentDetailsDateRange.MONTH) {
                formattedDatePeriod = $scope.$root.formatDate($scope.detailsDialog.date, $scope.monthYearDateFormat);
            } else {
                formattedDatePeriod = $scope.$root.formatDate($scope.detailsDialog.date, $scope.wideAppDateFormat);
            }

            $scope.formattedDatePeriod = formattedDatePeriod;
        };

        $scope.queryParams = {};

        $scope.loadShipmentsData = function () {
            $scope.queryParams.startDate = $scope.$root.formatDate($scope.detailsDialog.date);
            $scope.queryParams.basedOn = $scope.account.basedOn;
            $scope.queryParams.period = $scope.detailsDialog.period;

            $scope.queryParams.customerId = $scope.authData.organization.orgId;
            $scope.queryParams.userId = $scope.authData.personId;
            AccountCalendarService.details($scope.queryParams, function (data) {
                $scope.detailsGridData = data;
            });
        };

        $scope.$on('ngGridEventData', function (event, gridId) {
            if (gridId === $scope.detailsGrid.options.gridId) {
                var totalCost = 0;
                angular.forEach($scope.detailsGrid.options.ngGrid.filteredRows, function (row) {
                    totalCost += row.entity.total;
                });
                $scope.detailsGrid.totalCost = totalCost;
                $scope.detailsGrid.totalCount = $scope.detailsGrid.options.ngGrid.filteredRows.length;
            }
        });

        $scope.$watch('detailsDialog.shouldBeOpen', function (newVal) {
            if (newVal === true) {
                $scope.selectedShipments.length = 0;
                $scope.prepareDatePeriod();
                $scope.loadShipmentsData();
            }
        });

        // this method just to show how to load additional data for tooltip
        var onShowTooltip = function (scope, entity) {
            ShipmentDetailsService.getTooltipData({
                customerId: scope.authData.organization.orgId,
                shipmentId: entity.shipmentId
            }, function (data) {
                if (data) {
                    scope.tooltipData = data;
                }
            });
        };

        $scope.selectedShipments = [];

        $scope.refreshTable = function () {
            if ($scope.detailsDialog.shouldBeOpen) {
                $scope.queryParams = {};
                $scope.loadShipmentsData();
            }
        };

        $scope.$on('event:shipmentDataUpdated', function () {
            $scope.refreshTable();
        });

        function showShipmentsDetailsDialog(shipment) {
            $scope.$root.$broadcast('event:showShipmentDetails', {
                shipmentId: shipment.shipmentId,
                parentDialog: 'detailsDialog'
            });
        }

        $scope.detailsGrid = {
            tooltip: {},
            options: {
                data: 'detailsGridData',
                selectedItems: $scope.selectedShipments,
                columnDefs: [
                    {
                        field: 'bolNumber',
                        displayName: 'BOL',
                        showTooltip: true,
                        width: '7%'
                    },
                    {
                        field: 'soNumber',
                        displayName: 'SO#',
                        width: '5%'
                    },
                    {
                        field: 'glNumber',
                        displayName: 'GL#',
                        width: '5%'
                    },
                    {
                        field: 'proNumber',
                        displayName: 'Pro#',
                        width: '5%'
                    },
                    {
                        field: 'refNumber',
                        displayName: 'Shipper Ref#',
                        width: '5%'
                    },
                    {
                        field: 'poNumber',
                        displayName: 'PO#',
                        width: '5%'
                    },
                    {
                        field: 'puNumber',
                        displayName: 'PU#',
                        width: '5%'
                    },
                    {
                        field: 'trailer',
                        displayName: 'Trailer#',
                        width: '5%'
                    },
                    {
                        field: 'origin',
                        cellFilter: 'zip',
                        displayName: 'Origin',
                        width: '10%'
                    },
                    {
                        field: 'destination',
                        cellFilter: 'zip',
                        displayName: 'Destination',
                        width: '10%'
                    },
                    {
                        field: 'carrier',
                        displayName: 'Carrier',
                        width: '8%'
                    },
                    {
                        field: 'createdDate',
                        displayName: 'Booked Date',
                        width: '6%',
                        cellFilter: "date:wideAppDateFormat",
                        searchable: false
                    },
                    {
                        field: 'pickupDate',
                        displayName: 'Pickup Date',
                        width: '6%',
                        cellFilter: "date:wideAppDateFormat",
                        searchable: false
                    },
                    {
                        field: 'deliveryDate',
                        displayName: 'Delivery Date',
                        width: '6%',
                        cellFilter: "date:wideAppDateFormat",
                        searchable: false
                    },
                    {
                        field: 'status',
                        cellFilter: 'shipmentStatus',
                        displayName: 'Status',
                        width: '6%'
                    },
                    {
                        field: 'total',
                        displayName: 'Total',
                        width: '5%',
                        cellFilter: 'plsCurrency',
                        cellClass: 'text-right'
                    }
                ],
                action: function (entity) {
                    showShipmentsDetailsDialog(entity);
                },
                plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.progressiveSearchPlugin(), NgGridPluginFactory.tooltipPlugin(true),
                    NgGridPluginFactory.actionPlugin()],
                tooltipOptions: {
                    url: 'pages/content/quotes/shipments-grid-tooltip.html',
                    onShow: onShowTooltip,
                    delay: 200
                },
                enableColumnResize: true,
                multiSelect: false,
                progressiveSearch: true
            },
            totalCount: 0,
            totalCost: 0
        };

        $scope.viewDetails = function () {
            showShipmentsDetailsDialog($scope.selectedShipments[0]);
        };

        $scope.exportShipments = function () {
            var fileFormat = 'Calendar_Export file_{0,date,' + $scope.$root.exportFileNameDateFormat + '}.xlsx';
            var sheetName = "Shipment Orders";

            var shipmentEntities = _.map($scope.detailsGrid.options.ngGrid.filteredRows, function (item) {
                return item.entity;
            });

            var columnNames = ExportDataBuilder.getColumnNames($scope.detailsGrid.options);
            var footerData = ExportDataBuilder.buildTotalSumFooterData($scope.detailsGrid.totalCount, columnNames,
                    $scope.$eval('detailsGrid.totalCost | plsCurrency'));
            var exportData = ExportDataBuilder.buildExportData($scope.detailsGrid.options,
                    shipmentEntities, fileFormat, sheetName, footerData);

            ExportService.exportData(exportData);
        };
    }
]);

angular.module('plsApp').controller('AccountHistoryCtrl', ['$scope', 'urlConfig', '$window', '$filter', '$location', 'AccountHistoryService',
    'NgGridPluginFactory', 'DateTimeUtils', 'ExportDataBuilder', 'ExportService', 'ShipmentDetailsService',
    function ($scope, urlConfig, $window, $filter, $location, AccountHistoryService, NgGridPluginFactory, DateTimeUtils,
              ExportDataBuilder, ExportService, ShipmentDetailsService) {
        'use strict';

        var MAX_HISTORY_RANGE_IN_MONTHS = 3;

        $scope.manualRangeType = 'DEFAULT';
        $scope.defaultSelectionRangeType = 'MONTH';
        $scope.totalSumm = 0.0;

        $scope.accountHistoryEntries = [];
        $scope.selectedEntries = [];
        $scope.fromDateAlreadyExists = false;
        $scope.toDateAlreadyExists = false;
        $scope.validData = true;

        $scope.sortInfo = {columns: [], fields: [], directions: []};

        $scope.$watch('fromDate', function (newValue) {
            if (newValue) {
                $scope.minToDate = DateTimeUtils.parseISODate(newValue);
                $scope.maxToDate = DateTimeUtils.addMonths($scope.minToDate, MAX_HISTORY_RANGE_IN_MONTHS);
            } else {
                $scope.minToDate = undefined;
                $scope.maxToDate = undefined;
            }
            $scope.updatePickupDateRangeIsEmpty();
        });

        $scope.$watch('toDate', function (newValue) {
            if (newValue) {
                $scope.maxFromDate = DateTimeUtils.parseISODate(newValue);
                $scope.minFromDate = DateTimeUtils.addMonths($scope.maxFromDate, -MAX_HISTORY_RANGE_IN_MONTHS);
            } else {
                $scope.maxFromDate = undefined;
                $scope.minFromDate = undefined;
            }
            $scope.updatePickupDateRangeIsEmpty();
        });

        $scope.updatePickupDateRangeIsEmpty = function () {
            var isEmpty = ($scope.fromDate === null || $scope.fromDate === undefined) &&
                    ($scope.toDate === null || $scope.toDate === undefined);

            if (isEmpty) {
                // remove all highlighted validation fields
                $scope.fromDateAlreadyExists = false;
                $scope.toDateAlreadyExists = false;
                $scope.validData = true;
            } else {
                // decide which field we should highlight
                $scope.fromDateAlreadyExists = !$scope.fromDate;
                $scope.toDateAlreadyExists = !$scope.toDate;
                // if some field is not filled then disable 'Search' button, otherwise - enabled
                $scope.validData = !$scope.fromDateAlreadyExists && !$scope.toDateAlreadyExists;
            }
        };

        function getSortField() {
            var result = '';
            if ($scope.sortInfo && _.isArray($scope.sortInfo.fields) && $scope.sortInfo.fields.length > 0) {
                result = $scope.sortInfo.fields[0];
            }
            return result;
        }

        function getSortValue() {
            var result = '';
            if ($scope.sortInfo && _.isArray($scope.sortInfo.directions) && $scope.sortInfo.directions.length > 0) {
                result = $scope.sortInfo.directions[0];
            }
            return result;
        }

        function getDateFilter() {
            var fromDate, toDate;
            if (!fromDate && toDate) {
                fromDate = $filter('date')(DateTimeUtils.addDays(DateTimeUtils.parseISODate($scope.toDate), -7), $scope.$root.transferDateFormat);
                toDate = $scope.toDate;
            } else if (fromDate && !toDate) {
                toDate = $filter('date')(DateTimeUtils.addDays(DateTimeUtils.parseISODate($scope.fromDate), 7), $scope.$root.transferDateFormat);
                fromDate = $scope.fromDate;
            } else {
                fromDate = $scope.fromDate;
                toDate = $scope.toDate;
            }
            return {
                fromDate: fromDate,
                toDate: toDate
            };
        }

        $scope.noSearchFilterSelected = function () {
            var result = _.isEmpty($scope.bol) &&
                    _.isEmpty($scope.pro) &&
                    _.isEmpty($scope.origin) &&
                    _.isEmpty($scope.destination) &&
                    _.isEmpty($scope.carrier) &&
                    _.isEmpty($scope.fromDate) &&
                    _.isEmpty($scope.toDate);

            if (result) {
                $scope.fromDateAlreadyExists = false;
                $scope.toDateAlreadyExists = false;
            }

            return result;
        };

        var carrierFilterSelectedOnly = function () {
            return $scope.carrier && $scope.fromDate === undefined && $scope.toDate === undefined;
        };

        var originFiterSelectedOnly = function () {
            return $scope.origin && $scope.fromDate === undefined && $scope.toDate === undefined;
        };

        var destinationFiterSelectedOnly = function () {
            return $scope.destination && $scope.fromDate === undefined && $scope.toDate === undefined;
        };

        var calculateTotalSum = function (entries) {
            $scope.totalSumm = 0.0;
            angular.forEach(entries, function (item) {
                $scope.totalSumm += item.total;
            });
        };

        $scope.searchAccountHistoryEntries = function () {
            /* If no search filters has been selected no actions are performed */
            if ($scope.noSearchFilterSelected()) {
                console.log('');
                /* just to prevent pmd build failure */
            } else if (carrierFilterSelectedOnly() ||
                    (originFiterSelectedOnly() && destinationFiterSelectedOnly()) ||
                    originFiterSelectedOnly() ||
                    destinationFiterSelectedOnly()) {
                $scope.fromDateAlreadyExists = true;
                $scope.toDateAlreadyExists = true;
            } else {
                var sortField = getSortField();
                var sortValue = getSortValue();
                var dateFilter = getDateFilter();
                AccountHistoryService.get({
                            customerId: $scope.$root.authData.organization.orgId,
                            userId: $scope.$root.authData.personId,
                            sortField: sortField,
                            sortValue: sortValue,
                            bol: $scope.bol,
                            pro: $scope.pro,
                            origin: $scope.origin ? $scope.origin.zip : '',
                            destination: $scope.destination ? $scope.destination.zip : '',
                            carrier: $scope.carrier ? $scope.carrier.id : '',
                            fromDate: dateFilter.fromDate,
                            toDate: dateFilter.toDate
                        },
                        function (data) {
                            $scope.accountHistoryEntries = data;
                            calculateTotalSum(data);
                            $scope.selectedEntries.length = 0;
                        });
            }
        };

        //total sum should be updated when used progressive search and data in the grid is updated
        $scope.$watch('accountHistoryGrid.ngGrid.filteredRows', function (newValue) {
            calculateTotalSum(_.map(newValue, function (item) {
                return item.entity;
            }));
        });

        $scope.clearSearchCriteria = function () {
            $scope.keywords = null;
            $scope.sortConfig = null;
            $scope.accountHistoryEntries.length = 0;
            $scope.selectedEntries.length = 0;
            $scope.totalSumm = 0.0;
            $scope.$broadcast('event:cleaning-input');
        };


        $scope.$on('event:shipmentDataUpdated', function () {
            $scope.searchAccountHistoryEntries();
        });

        function showShipmentsDetailsDialog(shipment) {
            $scope.$broadcast('event:showShipmentDetails', {shipmentId: shipment.shipmentId, selectedTab: 'tracking'});
        }

        $scope.view = function () {
            showShipmentsDetailsDialog($scope.selectedEntries[0]);
        };

        function addUrlParam(url, paramName, value) {
            if (value) {
                return url + ((url.indexOf('?') === -1) ? '?' : '&') + encodeURIComponent(paramName) + '=' + encodeURIComponent(value);
            }
            return url;
        }

        $scope.exportAccountHistory = function () {
            var fileFormat = 'AccountHistory_Export file_{0,date,' + $scope.$root.exportFileNameDateFormat + '}.xlsx';
            var sheetName = "Shipment Orders";

            var shipmentEntities = _.map($scope.accountHistoryGrid.ngGrid.filteredRows, function (item) {
                return item.entity;
            });

            var columnNames = ExportDataBuilder.getColumnNames($scope.accountHistoryGrid);
            var footerData = ExportDataBuilder.buildTotalSumFooterData($scope.accountHistoryGrid.ngGrid.filteredRows.length,
                    columnNames, $scope.$eval('totalSumm | plsCurrency'));
            var exportData = ExportDataBuilder.buildExportData($scope.accountHistoryGrid,
                    shipmentEntities, fileFormat, sheetName, footerData);

            ExportService.exportData(exportData);
        };

        function onShowTooltip(row) {
            ShipmentDetailsService.getTooltipData({
                customerId: $scope.$root.authData.organization.orgId,
                shipmentId: row.entity.shipmentId
            }, function (data) {
                if (data) {
                    $scope.tooltipData = data;
                }
            });
        }

        $scope.keyPressed = function (evt) {
            if (evt.which === 13) {
                $scope.searchAccountHistoryEntries();
            }
        };

        $scope.accountHistoryGrid = {
            enableColumnResize: true,
            data: 'accountHistoryEntries',
            multiSelect: false,
            selectedItems: $scope.selectedEntries,
            columnDefs: [
                {
                    field: 'bolNumber',
                    displayName: 'BOL',
                    showTooltip: true,
                    width: '5%'
                },
                {
                    field: 'soNumber',
                    displayName: 'SO#',
                    width: '5%'
                },
                {
                    field: 'glNumber',
                    displayName: 'GL#',
                    width: '5%'
                },
                {
                    field: 'proNumber',
                    displayName: 'Pro#',
                    width: '5%'
                },
                {
                    field: 'refNumber',
                    displayName: 'Shipper Ref#',
                    width: '5%'
                },
                {
                    field: 'poNumber',
                    displayName: 'PO#',
                    width: '5%'
                },
                {
                    field: 'puNumber',
                    displayName: 'PU#',
                    width: '5%'
                },
                {
                    field: 'trailer',
                    displayName: 'Trailer#',
                    width: '5%'
                },
                {
                    field: 'origin',
                    cellFilter: 'zip',
                    displayName: 'Origin',
                    width: '10%'
                },
                {
                    field: 'destination',
                    cellFilter: 'zip',
                    displayName: 'Destination',
                    width: '10%'
                },
                {
                    field: 'carrier',
                    displayName: 'Carrier',
                    width: '8%'
                },
                {
                    field: 'pickupDate',
                    displayName: 'Pickup Date',
                    width: '10%',
                    cellFilter: 'date:wideAppDateFormat'
                },
                {
                    field: 'deliveryDate',
                    displayName: 'Del. Date',
                    width: '9%',
                    cellFilter: 'date:wideAppDateFormat'
                },
                {
                    field: 'status',
                    displayName: 'Status',
                    width: '6%',
                    cellFilter: 'shipmentStatus'
                },
                {
                    field: 'total',
                    displayName: 'Total',
                    width: '6%',
                    cellFilter: 'plsCurrency'
                }
            ],
            action: function (entity) {
                showShipmentsDetailsDialog(entity);
            },
            plugins: [NgGridPluginFactory.plsGrid(), NgGridPluginFactory.tooltipPlugin(true), NgGridPluginFactory.actionPlugin(),
                NgGridPluginFactory.progressiveSearchPlugin()],
            tooltipOptions: {
                url: 'pages/content/quotes/shipments-grid-tooltip.html',
                onShow: onShowTooltip
            },
            refreshTable: function (columnFilters, pagingConfig, sortConfig) {
                $scope.searchAccountHistoryEntries(sortConfig);
            },
            progressiveSearch: true,
            enableSorting: true,
            sortInfo: $scope.sortInfo
        };

        $scope.$on('event:closeAndRedirect', function (event, url, params) {
            $location.url(url).search(params);
        });

        $scope.detailsGrid = {};
    }
]);