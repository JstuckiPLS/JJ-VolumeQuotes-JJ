angular.module('plsApp').controller('KpiCtrl', ['$scope', '$compile', 'kpiResource', function ($scope, $compile, kpiResource) {
    'use strict';

    $scope.paramToURL = function (obj) {
        var url = "?";

        _.each(obj, function (val, key) {
            if (_.isArray(val)) {
                _.each(val, function (inVal) {
                    url = url + key + '=' + inVal + '&';
                });
            } else {
                url = url + key + '=' + val + '&';
            }
        });

        return url;
    };

    $scope.weekday = [{
        key: '1',
        value: 'Sunday'
    }, {
        key: '2',
        value: 'Monday'
    }, {
        key: '3',
        value: 'Tuesday'
    }, {
        key: '4',
        value: 'Wednesday'
    }, {
        key: '5',
        value: 'Thursday'
    }, {
        key: '6',
        value: 'Friday'
    }, {
        key: '7',
        value: 'Saturday'
    }];

    $scope.month = [{
        key: '1',
        value: 'January'
    }, {
        key: '2',
        value: 'February'
    }, {
        key: '3',
        value: 'March'
    }, {
        key: '4',
        value: 'April'
    }, {
        key: '5',
        value: 'May'
    }, {
        key: '6',
        value: 'June'
    }, {
        key: '7',
        value: 'July'
    }, {
        key: '8',
        value: 'August'
    }, {
        key: '9',
        value: 'September'
    }, {
        key: '10',
        value: 'October'
    }, {
        key: '11',
        value: 'November'
    }, {
        key: '12',
        value: 'December'
    }];

    $scope.kpiChartOption = {
        width: 750,
        height: 350
    };

    $scope.kpiChartTitleTextStyle = {
        color: '#005500',
        fontSize: '14',
        paddingRight: '10',
        marginRight: '10'
    };

    $scope.listStatus = [{
        key: 'PA',
        value: 'Booked'
    }, {
        key: 'A',
        value: 'In transit'
    }, {
        key: 'DA',
        value: 'Out for delivey'
    }, {
        key: 'CD',
        value: 'Delivered'
    }, {
        key: 'C',
        value: 'Cancelled'
    }];

    $scope.ioFlag = [{
        key: 'I',
        value: 'Inbound'
    }, {
        key: 'O',
        value: 'Outbound'
    }];

    $scope.organization = undefined;

    $scope.scacFilter = [];
    $scope.destFilter = [];
    $scope.origFilter = [];
    $scope.classFilter = [];
    $scope.ioFlagFilter = [];
    $scope.weightFilter = [];
    $scope.yearFilter = [];
    $scope.weekdayFilter = [];
    $scope.monthFilter = [];

    $scope.thisDashboardTab = "summaries";
    $scope.thisSubTab = 'destination-report';

    $scope.updateButton = true;

    $scope.noDataDialog = {
        shown: false,
        show: function (shown) {
            $scope.noDataDialog.shown = shown;
        }
    };

    $scope.$watch('selectedCustomer', function () {
        $scope.updateButton = false;
    });

    $scope.paramConverter = function (list, mask) {
        return _.pluck(_.filter(mask, function (num) {
            return _.indexOf(list, num.value) >= 0;
        }), 'key');
    };

    $scope.responseConverter = function (list, mask) {
        return _.pluck(_.filter(mask, function (num) {
            return _.indexOf(list, num.key) >= 0;
        }), 'value');
    };

    $scope.loadFilters = function () {
        kpiResource.getFilterValues({
            orgId: $scope.organization
        }, function (response) {
            $scope.scacFilterList = response.scac;
            $scope.destFilterList = response.dest;
            $scope.origFilterList = response.orig;
            $scope.classFilterList = response.classCode;
            $scope.ioFlagFilterList = $scope.responseConverter(response.ioFlag, $scope.ioFlag);
            $scope.weightFilterList = response.weight;
            $scope.yearFilterList = response.year;
            $scope.weekdayFilterList = $scope.responseConverter(response.weekday, $scope.weekday);
            $scope.monthFilterList = _.pluck($scope.month, 'value');
        });
    };

    $scope.oldIiElement = null;
    $scope.activeClass = "active";
    $scope.activeClassDefault = $scope.activeClass;

    $scope.loadReport = function (dashboardTab, subTab, event) {
        if (event !== null) {
            var elem_a = angular.element(event.target);
            var elem_li = angular.element(elem_a[0].parentElement);

            $scope.activeClassDefault = "";

            if ($scope.oldIiElement !== null) {
                $scope.oldIiElement.removeClass($scope.activeClass);
            }

            $scope.oldIiElement = elem_li;
            elem_li.addClass($scope.activeClass);
        }

        $scope.thisDashboardTab = dashboardTab;
        $scope.thisSubTab = subTab;

        var url = 'pages/content/kpi/tabs/' + dashboardTab + '-tabs/' + subTab + '-tab.html';

        $.get(url, function (data) {
            $scope.$apply(function () {
                $('#views').html($compile(data)($scope));
            });
        });
    };

    $scope.loadFilters();
    $scope.loadReport($scope.thisDashboardTab, $scope.thisSubTab, null);

    $scope.update = function () {
        $scope.updateButton = true;

        if ($scope.selectedCustomer !== undefined && $scope.selectedCustomer.id !== undefined) {
            $scope.organization = $scope.selectedCustomer.id;
        } else {
            $scope.organization = undefined;
        }

        $scope.loadFilters();
        $scope.loadReport($scope.thisDashboardTab, $scope.thisSubTab, null);
    };

    $scope.uncheck = function () {
        $scope.scacFilter.length = 0;
        $scope.destFilter.length = 0;
        $scope.origFilter.length = 0;
        $scope.classFilter.length = 0;
        $scope.ioFlagFilter.length = 0;
        $scope.weightFilter.length = 0;
        $scope.yearFilter.length = 0;
        $scope.weekdayFilter.length = 0;
        $scope.monthFilter.length = 0;
    };
}]);

angular.module('plsApp').controller('dailyLoadActivityReportCtrl', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = true;
        $scope.$parent.destFilterShow = true;
        $scope.$parent.origFilterShow = true;
        $scope.$parent.classFilterShow = true;
        $scope.$parent.ioFlagFilterShow = true;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = true;
        $scope.$parent.monthFilterShow = false;

        $scope.$parent.refresh = function () {
            kpiResource.getDailyLoadActivityReport({
                orgId: $scope.organization,
                scac: $scope.scacFilter,
                dest: $scope.destFilter,
                orig: $scope.origFilter,
                classCode: $scope.classFilter,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.$parent.ioFlag),
                weekday: $scope.$parent.paramConverter($scope.weekdayFilter, $scope.weekday),
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.list = response;

                var cols = [{
                    id: 'date',
                    label: 'date',
                    type: 'date'
                }, {
                    id: 'total',
                    label: 'total',
                    type: 'number'
                }];

                var rows = [];

                _.each($scope.list, function (value) {
                    var row = [];

                    row.push({v: new Date(value.pickup)});
                    row.push({v: value.total});
                    rows.push({c: row});
                });

                var locDate = new Date();

                locDate.setFullYear(2012);
                locDate.setMonth(1, 1);

                $scope.chart = {
                    type: "AnnotatedTimeLine",
                    data: {
                        cols: cols,
                        rows: rows
                    },
                    option: {
                        displayZoomButtons: false,
                        zoomStartTime: locDate,
                        zoomEndTime: new Date()
                    }
                };
            });
        };

        $scope.dailyLoadActivityReportGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'list',
            columnDefs: [{
                field: 'bound',
                displayName: 'Bound'
            }, {
                field: 'destState',
                displayName: 'Destination State'
            }, {
                field: 'origState',
                displayName: 'Origin State'
            }, {
                field: 'classCode',
                displayName: 'Class Code'
            }, {
                field: 'customer',
                displayName: 'Customer'
            }, {
                field: 'pickup',
                displayName: 'Pickup Date',
                cellFilter: 'date:appDateFormat'
            }, {
                field: 'scac',
                displayName: 'Scac'
            }, {
                field: 'weekday',
                displayName: 'Weekday'
            }, {
                field: 'total',
                displayName: 'Total',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                scac: $scope.scacFilter,
                dest: $scope.destFilter,
                orig: $scope.origFilter,
                classCode: $scope.classFilter,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                weekday: $scope.$parent.paramConverter($scope.weekdayFilter, $scope.weekday),
                status: [$scope.$parent.status]
            };

            if ($scope.dailyLoadActivityReportGrid.ngGrid.filteredRows && $scope.dailyLoadActivityReportGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportDailyLoadActivityReport" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('destinationReportCtrl', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = true;
        $scope.$parent.destFilterShow = true;
        $scope.$parent.origFilterShow = true;
        $scope.$parent.classFilterShow = false;
        $scope.$parent.ioFlagFilterShow = false;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = false;

        var cols;
        var rows;
        var width;

        $scope.destinationReportGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'list',
            columnDefs: [{
                field: 'orgID',
                displayName: 'ID'
            }, {
                field: 'destState',
                displayName: 'Destination State'
            }, {
                field: 'origState',
                displayName: 'Origin State'
            }, {
                field: 'lCount',
                displayName: 'Order Count'
            }, {
                field: 'orgName',
                displayName: 'Customer Name'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        var colsTemp = function () {
            var mass = [];

            mass.push({
                id: 'state',
                label: 'state',
                type: 'string'
            });

            _.each($scope.list, function (value) {
                if (value.destState !== null) {
                    mass.push({
                        id: value.destState,
                        label: 'Destination state ' + value.destState,
                        type: 'number'
                    });
                }
            });

            mass = _.uniq(mass, false, function (obj) {
                return obj.id;
            });

            return mass;
        };

        var rowsTemp = function () {
            var mass = [];
            var massResult = [];

            _.each($scope.list, function (value) {
                if (value.destState !== null) {
                    mass.push(value.origState);
                }
            });

            mass = _.uniq(mass, false, function (obj) {
                return obj;
            });

            _.each(mass, function (orig) {
                var massRow = [];

                massRow.push({
                    v: orig
                });

                _.each(cols, function (col) {
                    if (col.id !== 'state') {
                        var obj = {};

                        _.each($scope.list, function (value) {
                            if (col.id === value.destState && orig === value.origState) {
                                obj.v = value.lCount;
                            }
                        });

                        massRow.push(obj);
                    }
                });

                massResult.push({
                    c: massRow
                });
            });

            return massResult;
        };

        $scope.$parent.refresh = function () {
            kpiResource.getDestinationReport({
                orgId: $scope.organization,
                scac: $scope.scacFilter,
                dest: $scope.destFilter,
                orig: $scope.origFilter,
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.list = response;
                cols = colsTemp();
                rows = rowsTemp();
                width = 250 + rows.length * 50;
                var chartTemp = {};
                chartTemp.data = {};
                chartTemp.type = "ColumnChart";

                chartTemp.data = {
                    cols: cols,
                    rows: rows
                };

                chartTemp.options = {
                    legend: 'none',
                    hAxis: {
                        title: 'Origin State',
                        titleTextStyle: $scope.kpiChartTitleTextStyle
                    },
                    vAxis: {
                        title: 'Order Count',
                        titleTextStyle: $scope.kpiChartTitleTextStyle
                    },
                    chartArea: {
                        left: 70
                    },
                    isStacked: true,
                    width: width,// $scope.kpiChartOption.width,
                    height: $scope.kpiChartOption.height
                };

                $scope.chart = chartTemp;
            });
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                scac: $scope.scacFilter,
                dest: $scope.destFilter,
                orig: $scope.origFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.destinationReportGrid.ngGrid.filteredRows && $scope.destinationReportGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportDestinationReport" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('freightSpendAnalysisController', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = false;
        $scope.$parent.destFilterShow = false;
        $scope.$parent.origFilterShow = false;
        $scope.$parent.classFilterShow = true;
        $scope.$parent.ioFlagFilterShow = false;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = false;

        $scope.freightSpendGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'reports',
            columnDefs: [{
                field: 'classCode',
                displayName: 'Class'
            }, {
                field: 'loadCount',
                displayName: 'Order count'
            }, {
                field: 'summaryCost',
                displayName: 'Summary Cost',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'avarageCost',
                displayName: 'Avarage Cost',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        var extractRows = function () {
            var cells = [];

            _.each($scope.reports, function (report) {
                var values = [];

                values.push({v: "Class " + report.classCode}, {v: report.loadCount});
                cells.push({c: values});
            });

            return cells;
        };

        var extractBarRows = function () {
            var cells = [];
            var values = [{}];

            _.each($scope.reports, function (report) {
                values.push({v: report.loadCount});
            });

            cells.push({c: values});

            return cells;
        };

        var extractBarCols = function () {
            var cells = [];

            cells.push({
                id: "classCode",
                label: "classCode",
                type: "string"
            });

            _.each($scope.reports, function (report) {
                cells.push({
                    id: "classCode" + report.classCode,
                    label: "Class " + report.classCode,
                    type: "number"
                });
            });

            return cells;
        };

        $scope.$parent.refresh = function () {
            kpiResource.getFreightSpendReports({
                orgId: $scope.organization,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.reports = response;

                $scope.pieChart = {
                    displayed: true,
                    type: "PieChart",
                    data: {
                        cols: [{
                            id: "classCode",
                            label: "Class",
                            type: "string"
                        }, {
                            id: "lcount",
                            label: "Order Count",
                            type: "number"
                        }],
                        rows: extractRows()
                    },
                    options: {
                        title: "Freight spend analysis",
                        width: $scope.kpiChartOption.width,
                        height: $scope.kpiChartOption.height
                    }
                };

                $scope.barChart = {
                    type: "BarChart",
                    data: {
                        cols: extractBarCols(),
                        rows: extractBarRows()
                    },
                    options: {
                        vAxis: {
                            title: "Class"
                        },
                        hAxis: {
                            title: "LCount"
                        },
                        title: "Class Breakdown",
                        width: $scope.kpiChartOption.width,
                        height: $scope.kpiChartOption.height
                    }
                };
            });
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.freightSpendGrid.ngGrid.filteredRows && $scope.freightSpendGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportFreightSpendReports" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('shipmentOverviewCtrl', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = true;
        $scope.$parent.destFilterShow = true;
        $scope.$parent.origFilterShow = true;
        $scope.$parent.classFilterShow = true;
        $scope.$parent.ioFlagFilterShow = false;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = false;

        $scope.$parent.refresh = function () {
            kpiResource.getShipmentOverviewReport({
                orgId: $scope.organization,
                scac: $scope.scacFilter,
                dest: $scope.destFilter,
                orig: $scope.origFilter,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.list = response;
            });
        };

        $scope.shipmentOverviewGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'list',
            columnDefs: [{
                field: 'orgID',
                displayName: 'Organization ID'
            }, {
                field: 'bound',
                displayName: 'Inbound/Outbound ID'
            }, {
                field: 'shipDate',
                displayName: 'Ship Date',
                cellFilter: 'date:appDateFormat'
            }, {
                field: 'lCount',
                displayName: 'Order Count'
            }, {
                field: 'weight',
                displayName: 'AVG Weight',
                cellFilter: 'appendSuffix:"Lbs"'
            }, {
                field: 'lhRev',
                displayName: 'LH Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'fuelRev',
                displayName: 'Fuel Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'accRev',
                displayName: 'ACC Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'sumTotal',
                displayName: 'Sum total',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'shipperBench',
                displayName: 'Shipper Bench',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'savings',
                displayName: 'Savings',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'bmSavingsPercent',
                displayName: 'BM savings percent',
                cellClass: 'text-right',
                cellFilter: 'percentage:2'
            }, {
                field: 'sumTotalShipm',
                displayName: 'Sum total shipment',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                scac: $scope.scacFilter,
                dest: $scope.destFilter,
                orig: $scope.origFilter,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.shipmentOverviewGrid.ngGrid.filteredRows && $scope.shipmentOverviewGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportShipmentOverviewReport" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('weightAnalysisReportController', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = false;
        $scope.$parent.destFilterShow = false;
        $scope.$parent.origFilterShow = false;
        $scope.$parent.classFilterShow = false;
        $scope.$parent.ioFlagFilterShow = false;
        $scope.$parent.weightFilterShow = true;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = false;

        $scope.weightGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'reports',
            columnDefs: [{
                field: 'loadId',
                displayName: 'Order id'
            }, {
                field: 'weight',
                displayName: 'Weight'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        var rows = function () {
            var orgs = [];

            _.each($scope.reports, function (item) {
                orgs.push(item.weight);
            });

            orgs = _.uniq(orgs, false);
            var res = [];

            _.each(orgs, function (org) {
                var count = 0;

                _.each($scope.reports, function (item) {
                    if (org === item.weight) {
                        count = count + 1;
                    }
                });

                res.push({c: [{v: org}, {v: count}]});
            });

            return res;
        };

        $scope.$parent.refresh = function () {
            kpiResource.getWeightAnalysisReports({
                orgId: $scope.organization,
                weight: $scope.weightFilter,
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.reports = response;

                $scope.chart = {
                    type: "PieChart",
                    displayed: true,
                    data: {
                        cols: [{
                            id: "weight",
                            label: "Weight",
                            type: "string"
                        }, {
                            id: "loadId",
                            label: "Order Count",
                            type: "number"
                        }],
                        rows: rows()
                    },
                    options: {
                        width: $scope.kpiChartOption.width,
                        height: $scope.kpiChartOption.height
                    }
                };
            });
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                weight: $scope.weightFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.weightGrid.ngGrid.filteredRows && $scope.weightGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportWeightAnalysisReports" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('geographicSummaryController', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = false;
        $scope.$parent.destFilterShow = true;
        $scope.$parent.origFilterShow = true;
        $scope.$parent.classFilterShow = true;
        $scope.$parent.ioFlagFilterShow = true;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = false;

        $scope.$parent.refresh = function () {
            kpiResource.getGeographicSummaryReports({
                orgId: $scope.organization,
                destination: $scope.destFilter,
                origin: $scope.origFilter,
                classCode: $scope.classFilter,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.reports = response;
            });
        };

        $scope.geographicSummaryGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'reports',
            columnDefs: [{
                field: 'customerId',
                displayName: 'Organization ID'
            }, {
                field: 'destination',
                displayName: 'Destination'
            }, {
                field: 'origin',
                displayName: 'Origin'
            }, {
                field: 'loadCount',
                displayName: 'Order Count'
            }, {
                field: 'averageWeight',
                displayName: 'AVG Weight',
                cellFilter: 'appendSuffix:"Lbs"'
            }, {
                field: 'linehaulRevenue',
                displayName: 'LH Revenue',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'fuelRevenue',
                displayName: 'Fuel Revenue',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'accessorialRevenue',
                displayName: 'ACC Revenue',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'summaryTotal',
                displayName: 'Summary Total',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'shipperBench',
                displayName: 'Shipper bench',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'savings',
                displayName: 'Savings',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'summaryTotalShipment',
                displayName: 'Summaty Total Shipment',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                destination: $scope.destFilter,
                origin: $scope.origFilter,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.geographicSummaryGrid.ngGrid.filteredRows && $scope.geographicSummaryGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportGeographicSummaryReports" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('carrierSummaryCtrl', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = false;
        $scope.$parent.destFilterShow = true;
        $scope.$parent.origFilterShow = true;
        $scope.$parent.classFilterShow = true;
        $scope.$parent.ioFlagFilterShow = true;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = false;

        var rows = function () {
            var orgs = [];

            _.each($scope.list, function (item) {
                orgs.push(item.scac);
            });

            orgs = _.uniq(orgs, false);
            var res = [];

            _.each(orgs, function (org) {
                var count = 0;

                _.each($scope.list, function (item) {
                    if (org === item.scac) {
                        count = count + item.lCount;
                    }
                });

                res.push({c: [{v: org}, {v: count}]});
            });

            return res;
        };

        var rowsVolume = function () {
            var it = {};

            _.each($scope.list, function (item) {
                it[item.scac] = (it[item.scac] ? it[item.scac] + 1 : 1);
            });

            var res = [];

            _.each(it, function (value, key) {
                res.push({c: [{v: key}, {v: value}]});
            });

            return res;
        };

        $scope.$parent.refresh = function () {
            kpiResource.getCarrierSummaryReport({
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                dest: $scope.destFilter,
                orig: $scope.origFilter,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.list = response;

                $scope.chart = {
                    type: "PieChart",
                    displayed: true,
                    data: {
                        cols: [{
                            id: "scac",
                            label: "Scac",
                            type: "string"
                        }, {
                            id: "lCount",
                            label: "Order Count",
                            type: "number"
                        }],
                        rows: rows()
                    },
                    options: $scope.kpiChartOption
                };

                $scope.chartVolume = {
                    type: "PieChart",
                    displayed: true,
                    data: {
                        cols: [{
                            id: "scac",
                            label: "Scac",
                            type: "string"
                        }, {
                            id: "shipments",
                            label: "Shipments",
                            type: "number"
                        }],
                        rows: rowsVolume()
                    },
                    options: $scope.kpiChartOption
                };
            });
        };

        $scope.carrierSummaryGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'list',
            columnDefs: [{
                field: 'scac',
                displayName: 'SCAC'
            }, {
                field: 'shipDate',
                displayName: 'Ship Date',
                cellFilter: 'date:appDateFormat'
            }, {
                field: 'lCount',
                displayName: 'Order Count'
            }, {
                field: 'weight',
                displayName: 'AVG Weight',
                cellFilter: 'appendSuffix:"Lbs"'
            }, {
                field: 'lhRev',
                displayName: 'LH Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'fuelRev',
                displayName: 'Fuel Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'accRev',
                displayName: 'ACC Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'sumTotal',
                displayName: 'Sum total',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'shipperBench',
                displayName: 'Shipper Bench',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'savings',
                displayName: 'Savings',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'bmSavingsPercent',
                displayName: 'BM savings percent',
                cellFilter: 'percentage:2',
                cellClass: 'text-right'
            }, {
                field: 'sumTotalShipm',
                displayName: 'Sum total shipment',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                dest: $scope.destFilter,
                orig: $scope.origFilter,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.carrierSummaryGrid.ngGrid.filteredRows && $scope.carrierSummaryGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportCarrierSummaryReport" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('classSummaryCtrl', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = true;
        $scope.$parent.destFilterShow = false;
        $scope.$parent.origFilterShow = false;
        $scope.$parent.classFilterShow = false;
        $scope.$parent.ioFlagFilterShow = true;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = false;

        var rows = function () {
            var orgs = [];

            _.each($scope.list, function (item) {
                orgs.push(item.classCode);
            });

            orgs = _.uniq(orgs, false);

            var res = [];

            _.each(orgs, function (org) {
                var count = 0;

                _.each($scope.list, function (item) {
                    if (org === item.classCode) {
                        count = count + item.lCount;
                    }
                });

                res.push({c: [{v: org}, {v: count}]});
            });

            return res;
        };

        $scope.$parent.refresh = function () {
            kpiResource.getClassSummaryReport({
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                scac: $scope.scacFilter,
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.list = response;

                $scope.chart = {
                    type: "PieChart",
                    displayed: true,
                    data: {
                        cols: [{
                            id: "classCode",
                            label: "Class",
                            type: "string"
                        }, {
                            id: "lCount",
                            label: "Order Count",
                            type: "number"
                        }],
                        rows: rows()
                    },
                    options: $scope.kpiChartOption
                };
            });
        };

        $scope.classSummaryGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'list',
            columnDefs: [{
                field: 'classCode',
                displayName: 'Class'
            }, {
                field: 'shipDate',
                displayName: 'Ship Date',
                cellFilter: 'date:appDateFormat'
            }, {
                field: 'lCount',
                displayName: 'Order Count'
            }, {
                field: 'percentLCount',
                displayName: 'Percent Order Count',
                cellFilter: 'percentage:2',
                cellClass: 'text-right'
            }, {
                field: 'weight',
                displayName: 'AVG Weight',
                cellFilter: 'appendSuffix:"Lbs"'
            }, {
                field: 'lhRev',
                displayName: 'LH Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'fuelRev',
                displayName: 'Fuel Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'accRev',
                displayName: 'ACC Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'sumTotal',
                displayName: 'Sum total',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'shipperBench',
                displayName: 'Shipper Bench',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'savings',
                displayName: 'Savings',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'bmSavingsPercent',
                displayName: 'BM savings percent',
                cellClass: 'text-right',
                cellFilter: 'percentage:2'
            }, {
                field: 'sumTotalShipm',
                displayName: 'Sum total shipment',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                scac: $scope.scacFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.classSummaryGrid.ngGrid.filteredRows && $scope.classSummaryGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportClassSummaryReport" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('customerSummaryCtrl', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = true;
        $scope.$parent.destFilterShow = false;
        $scope.$parent.origFilterShow = true;
        $scope.$parent.classFilterShow = true;
        $scope.$parent.ioFlagFilterShow = true;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = false;

        var rows = function () {
            var orgs = [];

            _.each($scope.list, function (item) {
                orgs.push(item.customer);
            });

            orgs = _.uniq(orgs, false);
            var res = [];

            _.each(orgs, function (org) {
                var count = 0;

                _.each($scope.list, function (item) {
                    if (org === item.customer) {
                        count = count + item.lCount;
                    }
                });

                res.push({c: [{v: org}, {v: count}]});
            });

            return res;
        };

        $scope.$parent.refresh = function () {
            kpiResource.getCustomerSummaryReport({
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                scac: $scope.scacFilter,
                orig: $scope.origFilter,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.list = response;

                $scope.chart = {
                    type: "PieChart",
                    displayed: true,
                    data: {
                        cols: [{
                            id: "customer",
                            label: "Customer",
                            type: "string"
                        }, {
                            id: "lCount",
                            label: "Order Count",
                            type: "number"
                        }],
                        rows: rows()
                    },
                    options: $scope.kpiChartOption
                };
            });
        };

        $scope.customerSummaryGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'list',
            columnDefs: [{
                field: 'customer',
                displayName: 'Customer'
            }, {
                field: 'destState',
                displayName: 'Dest State'
            }, {
                field: 'percentLCount',
                displayName: 'Percent Order Count',
                cellFilter: 'percentage:2',
                cellClass: 'text-right'
            }, {
                field: 'lCount',
                displayName: 'Order Count'
            }, {
                field: 'weight',
                displayName: 'AVG Weight',
                cellFilter: 'appendSuffix:"Lbs"'
            }, {
                field: 'lhRev',
                displayName: 'LH Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'fuelRev',
                displayName: 'Fuel Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'accRev',
                displayName: 'ACC Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'sumTotal',
                displayName: 'Sum total',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'shipperBench',
                displayName: 'Shipper Bench',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'savings',
                displayName: 'Savings',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'bmSavingsPercent',
                displayName: 'BM savings percent',
                cellClass: 'text-right',
                cellFilter: 'percentage:2'
            }, {
                field: 'sumTotalShipm',
                displayName: 'Sum total shipment',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                scac: $scope.scacFilter,
                orig: $scope.origFilter,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.customerSummaryGrid.ngGrid.filteredRows && $scope.customerSummaryGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportCustomerSummaryReport" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('vendorSummaryCtrl', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = true;
        $scope.$parent.destFilterShow = false;
        $scope.$parent.origFilterShow = false;
        $scope.$parent.classFilterShow = true;
        $scope.$parent.ioFlagFilterShow = true;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = false;

        $scope.$parent.refresh = function () {
            kpiResource.getVendorSummaryReport({
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                scac: $scope.scacFilter,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.list = response;
            });
        };

        $scope.vendorSummaryGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'list',
            columnDefs: [{
                field: 'origName',
                displayName: 'Origin Name'
            }, {
                field: 'origState',
                displayName: 'Origin State'
            }, {
                field: 'origCity',
                displayName: 'Origin City'
            }, {
                field: 'percentLCount',
                displayName: 'Percent Order Count',
                cellFilter: 'percentage:2',
                cellClass: 'text-right'
            }, {
                field: 'lCount',
                displayName: 'Order Count'
            }, {
                field: 'weight',
                displayName: 'AVG Weight',
                cellFilter: 'appendSuffix:"Lbs"'
            }, {
                field: 'lhRev',
                displayName: 'LH Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'fuelRev',
                displayName: 'Fuel Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'accRev',
                displayName: 'ACC Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'sumTotal',
                displayName: 'Sum total',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'shipperBench',
                displayName: 'Shipper Bench',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'savings',
                displayName: 'Savings',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'bmSavingsPercent',
                displayName: 'BM savings percent',
                cellClass: 'text-right',
                cellFilter: 'percentage:2'
            }, {
                field: 'sumTotalShipm',
                displayName: 'Sum total shipment',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                scac: $scope.scacFilter,
                classCode: $scope.classFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.vendorSummaryGrid.ngGrid.filteredRows && $scope.vendorSummaryGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportVendorSummaryReport" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('seasonalityCtrl', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = false;
        $scope.$parent.destFilterShow = false;
        $scope.$parent.origFilterShow = false;
        $scope.$parent.classFilterShow = false;
        $scope.$parent.ioFlagFilterShow = false;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = true;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = false;

        var rows = function () {
            var orgs = [];

            _.each($scope.list, function (item) {
                orgs.push(item.shipDateMonth);
            });

            orgs = _.uniq(orgs, false);
            var res = [];

            _.each(orgs, function (org) {
                var count = 0;

                _.each($scope.list, function (item) {
                    if (org === item.shipDateMonth) {
                        count = count + item.lCount;
                    }
                });

                res.push({c: [{v: org}, {v: count}]});
            });

            return res;
        };

        $scope.$parent.refresh = function () {
            kpiResource.getSeasonalityReport({
                orgId: $scope.organization,
                year: $scope.yearFilter,
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.list = response;

                $scope.chart = {
                    type: "PieChart",
                    displayed: true,
                    data: {
                        cols: [{
                            id: "month",
                            label: "month",
                            type: "string"
                        }, {
                            id: "percentLCount",
                            label: "Percent Order Count",
                            type: "number"
                        }],
                        rows: rows()
                    },
                    options: $scope.kpiChartOption
                };
            });
        };

        $scope.seasonalityGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'list',
            columnDefs: [{
                field: 'shipDateMonth',
                displayName: 'Month'
            }, {
                field: 'destState',
                displayName: 'Dest State'
            }, {
                field: 'percentLCount',
                displayName: 'Percent Order Count',
                cellFilter: 'percentage:2',
                cellClass: 'text-right'
            }, {
                field: 'lCount',
                displayName: 'Order Count'
            }, {
                field: 'weight',
                displayName: 'AVG Weight',
                cellFilter: 'appendSuffix:"Lbs"'
            }, {
                field: 'lhRev',
                displayName: 'LH Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'fuelRev',
                displayName: 'Fuel Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'accRev',
                displayName: 'ACC Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'sumTotal',
                displayName: 'Sum total',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'shipperBench',
                displayName: 'Shipper Bench',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'savings',
                displayName: 'Savings',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'bmSavingsPercent',
                displayName: 'BM savings percent',
                cellFilter: 'percentage:2',
                cellClass: 'text-right'
            }, {
                field: 'sumTotalShipm',
                displayName: 'Sum total shipment',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                year: $scope.yearFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.seasonalityGrid.ngGrid.filteredRows && $scope.seasonalityGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportSeasonalityReport" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('carrierTrendsCtrl', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
        'use strict';

        $scope.$parent.scacFilterShow = false;
        $scope.$parent.destFilterShow = true;
        $scope.$parent.origFilterShow = true;
        $scope.$parent.classFilterShow = true;
        $scope.$parent.ioFlagFilterShow = true;
        $scope.$parent.weightFilterShow = false;
        $scope.$parent.yearFilterShow = false;
        $scope.$parent.weekdayFilterShow = false;
        $scope.$parent.monthFilterShow = true;

        var rows = function () {
            var orgs = [];

            _.each($scope.list, function (item) {
                orgs.push(item.scac);
            });

            orgs = _.uniq(orgs, false);
            var res = [];

            _.each(orgs, function (org) {
                var count = 0;

                _.each($scope.list, function (item) {
                    if (org === item.scac) {
                        count = count + item.lCount;
                    }
                });

                res.push({c: [{v: org}, {v: count}]});
            });

            return res;
        };

        var rowsRevenue = function () {
            var orgs = [];

            _.each($scope.list, function (item) {
                orgs.push(item.scac);
            });

            orgs = _.uniq(orgs, false);
            var res = [];

            _.each(orgs, function (org) {
                var count = 0;

                _.each($scope.list, function (item) {
                    if (org === item.scac) {
                        count = count + item.sumTotal;
                    }
                });

                res.push({c: [{v: org}, {v: count}]});
            });

            return res;
        };

        $scope.$parent.refresh = function () {
            kpiResource.getCarrierSummaryReport({
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                dest: $scope.destFilter,
                orig: $scope.origFilter,
                classCode: $scope.classFilter,
                month: $scope.$parent.paramConverter($scope.monthFilter, $scope.month),
                status: [$scope.$parent.status]
            }, function (response) {
                $scope.list = response;

                $scope.chart = {
                    type: "PieChart",
                    displayed: true,
                    data: {
                        cols: [{
                            id: "scac",
                            label: "Scac",
                            type: "string"
                        }, {
                            id: "lCount",
                            label: "Order Count",
                            type: "number"
                        }],
                        rows: rows()
                    },
                    options: $scope.kpiChartOption
                };

                $scope.chartRevenue = {
                    type: "PieChart",
                    displayed: true,
                    data: {
                        cols: [{
                            id: "scac",
                            label: "Scac",
                            type: "string"
                        }, {
                            id: "sumTotal",
                            label: "sumTotal",
                            type: "number"
                        }],
                        rows: rowsRevenue()
                    },
                    options: $scope.kpiChartOption
                };
            });
        };

        $scope.carrierTrendsGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'list',
            columnDefs: [{
                field: 'scac',
                displayName: 'SCAC'
            }, {
                field: 'shipDate',
                displayName: 'Ship Date',
                cellFilter: 'date:appDateFormat'
            }, {
                field: 'lCount',
                displayName: 'Order Count'
            }, {
                field: 'weight',
                displayName: 'AVG Weight',
                cellFilter: 'appendSuffix:"Lbs"'
            }, {
                field: 'lhRev',
                displayName: 'LH Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'fuelRev',
                displayName: 'Fuel Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'accRev',
                displayName: 'ACC Rev',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'sumTotal',
                displayName: 'Sum total',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'shipperBench',
                displayName: 'Shipper Bench',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'savings',
                displayName: 'Savings',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }, {
                field: 'bmSavingsPercent',
                displayName: 'BM savings percent',
                cellClass: 'text-right',
                cellFilter: 'percentage:2'
            }, {
                field: 'sumTotalShipm',
                displayName: 'Sum total shipment',
                cellFilter: 'plsCurrency',
                cellClass: 'text-right'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.exportXLS = function () {
            var param = {
                orgId: $scope.organization,
                ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
                dest: $scope.destFilter,
                orig: $scope.origFilter,
                classCode: $scope.classFilter,
                month: $scope.monthFilter,
                status: [$scope.$parent.status]
            };

            if ($scope.carrierTrendsGrid.ngGrid.filteredRows && $scope.carrierTrendsGrid.ngGrid.filteredRows.length > 0) {
                window.open(urlConfig.core + "/kpireport/exportCarrierSummaryReport" + $scope.paramToURL(param));
            } else {
                $scope.noDataDialog.show(true);
            }
        };

        $scope.$parent.refresh();
    }
]);

angular.module('plsApp').controller('weeklyLoadActivityReportCtrl', ['$scope', 'kpiResource', 'urlConfig', 'NgGridPluginFactory',
    function ($scope, kpiResource, urlConfig, NgGridPluginFactory) {
    'use strict';

    $scope.$parent.scacFilterShow = true;
    $scope.$parent.destFilterShow = true;
    $scope.$parent.origFilterShow = true;
    $scope.$parent.classFilterShow = true;
    $scope.$parent.ioFlagFilterShow = true;
    $scope.$parent.weightFilterShow = false;
    $scope.$parent.yearFilterShow = false;
    $scope.$parent.weekdayFilterShow = true;
    $scope.$parent.monthFilterShow = false;

    var rowsRevenue = function () {
        var res = [];

        _.each($scope.weekday, function (day) {
            var count = 0;

            _.each($scope.list, function (item) {
                if (day.value === item.weekday.replace(/\s/g, '')) {
                    count = count + item.total;
                }
            });

            if (count > 0) {
                res.push({c: [{v: day.value}, {v: count}]});
            }
        });

        return res;
    };

    $scope.$parent.refresh = function () {
        kpiResource.getDailyLoadActivityReport({
            orgId: $scope.organization,
            scac: $scope.scacFilter,
            dest: $scope.destFilter,
            orig: $scope.origFilter,
            classCode: $scope.classFilter,
            ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
            weekday: $scope.$parent.paramConverter($scope.weekdayFilter, $scope.weekday),
            status: [$scope.$parent.status]
        }, function (response) {
            $scope.list = response;

            var cols = [{
                id: 'day',
                label: 'day',
                type: 'string'
            }, {
                id: 'total',
                label: 'Total',
                type: 'number'
            }];

            $scope.chart = {
                type: "LineChart",
                data: {
                    cols: cols,
                    rows: rowsRevenue()
                },
                options: $scope.kpiChartOption
            };
        });
    };

    $scope.weeklyLoadActivityReportGrid = {
        enableColumnResize: true,
        selectedItems: $scope.selectedItems,
        data: 'list',
        columnDefs: [{
            field: 'bound',
            displayName: 'Bound'
        }, {
            field: 'destState',
            displayName: 'Destination State'
        }, {
            field: 'origState',
            displayName: 'Origin State'
        }, {
            field: 'classCode',
            displayName: 'Class Code'
        }, {
            field: 'customer',
            displayName: 'Customer'
        }, {
            field: 'pickup',
            displayName: 'Pickup Date',
            cellFilter: 'date:appDateFormat'
        }, {
            field: 'scac',
            displayName: 'Scac'
        }, {
            field: 'weekday',
            displayName: 'Weekday'
        }, {
            field: 'total',
            displayName: 'Total',
            cellFilter: 'plsCurrency',
            cellClass: 'text-right'
        }],
        plugins: [NgGridPluginFactory.plsGrid()]
    };

    $scope.exportXLS = function () {
        var param = {
            orgId: $scope.organization,
            scac: $scope.scacFilter,
            dest: $scope.destFilter,
            orig: $scope.origFilter,
            classCode: $scope.classFilter,
            ioFlag: $scope.$parent.paramConverter($scope.ioFlagFilter, $scope.ioFlag),
            weekday: $scope.$parent.paramConverter($scope.weekdayFilter, $scope.weekday),
            status: [$scope.$parent.status]
        };

        if ($scope.weeklyLoadActivityReportGrid.ngGrid.filteredRows && $scope.weeklyLoadActivityReportGrid.ngGrid.filteredRows.length > 0) {
            window.open(urlConfig.core + "/kpireport/exportDailyLoadActivityReport" + $scope.paramToURL(param));
        } else {
            $scope.noDataDialog.show(true);
        }
    };

    $scope.$parent.refresh();
}]);