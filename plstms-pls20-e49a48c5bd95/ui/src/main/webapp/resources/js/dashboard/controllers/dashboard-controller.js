angular.module('dashboard').controller('DashboardCtrl', ['$scope', '$compile', '$routeParams', '$location', 'TeamsService',
function ($scope, $compile, $routeParams, $location, TeamsService) {

    var jasperObj = $scope.$root.jasperReportsObj;
    $scope.getReportsBaseURL = function() {
        return jasperObj.URL;
    };

    $scope.dashboard = {
        dateGroupType: 'TODAY'
    };
    $scope.dataGroupBys = [{key: 'DAY', description: 'Day'}];
    $scope.groupByMR = 'DAY';
    $scope.tableRowStyle = "font-family: 'DejaVu Sans', Arial, Helvetica, sans-serif; color: #706E6E; font-size: 11px; line-height: 1.1635742;";

    $scope.dateGroupTypeChange = function(){
        $scope.dataGroupBys = [{key: 'DAY', description: 'Day'}];
        $scope.groupByMR = 'DAY';
        switch($scope.dashboard.dateGroupType) {
        case 'TODAY':
            $scope.dataGroupBys = [{key: 'DAY', description: 'Day'}];
            $scope.groupByMR = 'DAY';
            break;
        case 'YESTERDAY':
            $scope.dataGroupBys = [{key: 'DAY', description: 'Day'}];
            $scope.groupByMR = 'DAY';
            break;
        case 'WEEK_TO_DATE':
            $scope.dataGroupBys = [{key: 'DAY', description: 'Day'}, {key: 'WEEK', description: 'Week'}];
            $scope.groupByMR = 'DAY';
            break;
        case 'THIS_WEEK':
            $scope.dataGroupBys = [{key: 'DAY', description: 'Day'}, {key: 'WEEK', description: 'Week'}];
            $scope.groupByMR = 'DAY';
            break;
        case 'LAST_WEEK':
            $scope.dataGroupBys = [{key: 'DAY', description: 'Day'}, {key: 'WEEK', description: 'Week'}];
            $scope.groupByMR = 'DAY';
            break;
        case 'MONTH_TO_DATE':
            $scope.dataGroupBys = [{key: 'DAY', description: 'Day'}, {key: 'WEEK', description: 'Week'},
                                           {key: 'MONTH', description: 'Month'}];
            $scope.groupByMR = 'WEEK';
            break;
        case 'THIS_MONTH':
            $scope.dataGroupBys = [{key: 'DAY', description: 'Day'}, {key: 'WEEK', description: 'Week'},
                                           {key: 'MONTH', description: 'Month'}];
            $scope.groupByMR = 'WEEK';
            break;
        case 'LAST_MONTH':
            $scope.dataGroupBys = [{key: 'DAY', description: 'Day'}, {key: 'WEEK', description: 'Week'},
                                           {key: 'MONTH', description: 'Month'}];
            $scope.groupByMR = 'WEEK';
            break;
        case 'YEAR_TO_DATE':
            $scope.dataGroupBys = [{key: 'WEEK', description: 'Week'},   {key: 'MONTH', description: 'Month'},
                                           {key: 'YEAR', description: 'Year'}];
            $scope.groupByMR = 'WEEK';
            break;
        case 'LAST_YEAR':
            $scope.dataGroupBys = [{key: 'WEEK', description: 'Week'},   {key: 'MONTH', description: 'Month'},
                                           {key: 'YEAR', description: 'Year'}];
            $scope.groupByMR = 'WEEK';
            break;
        }
    };

    $scope.modeTypes = [
        {key: 'ALL', description: 'All'},
        {key: 'LTL', description: 'LTL'},
        {key: 'TL', description: 'TL'},
        {key: 'TL - Flatbed', description: 'TL - Flatbed'},
        {key: 'TL - VAN', description: 'TL - VAN'},
        {key: 'TL - Reefer', description: 'TL - Reefer'},
        {key: 'OTHER', description: 'TL - Others'},
        {key: 'INTERMODAL', description: 'Intermodal'}
    ];

    $scope.dateGroupTypes = [
        {key: 'TODAY', description: 'Today', index: 0},
        {key: 'YESTERDAY', description: 'Yesterday', index: 1},
        {key: 'WEEK_TO_DATE', description: 'Week to Date', index: 2},
        {key: 'THIS_WEEK', description: 'This Week', index: 3},
        {key: 'LAST_WEEK', description: 'Last Week', index: 4},
        {key: 'MONTH_TO_DATE', description: 'Month to Date', index: 5},
        {key: 'THIS_MONTH', description: 'This Month', index: 6},
        {key: 'LAST_MONTH', description: 'Last Month', index: 7},
        {key: 'YEAR_TO_DATE', description: 'Year to Date', index: 7}
    ];

    $scope.branches = [
        'All',
        'National Accounts',
        '3PL',
        'LTL',
        'FS - All',
        'FS - Cranberry',
        'FS - South Side',
        'FS - Philadelphia',
        'FS - Jacksonville',
        'FS - Charlotte',
        'FS - Tampa',
        'FS - St. Louis',
        'FS - Dallas',
        'FS - Houston',
        'FS - Phoenix'
    ];

    $scope.cleanSalesRep = function() {
        delete $scope.customer;
        delete $scope.accountExecutive;
        $scope.dashboard.dateGroupType = 'TODAY';
        $scope.modeType = 'ALL';
        $scope.branch = 'All';
        $scope.team = 'All';
        $scope.dateGroupTypeChange();
    };

    $scope.addFadeOutEffect = function(linkToElemPairs, tableRowsLength) {
        var isBackgroundWhite = true;
        linkToElemPairs.forEach(function showCursor(pair, index) {
            var el = pair.element;
            el.title = el.textContent; 
            el.textContent = el.textContent.substring(0, tableRowsLength[index % tableRowsLength.length]);
            el.style = $scope.tableRowStyle;
            angular.element(el).replaceWith($compile('<div class="fadeOut' + (isBackgroundWhite ? 1 : 0) + '">'+ el.outerHTML + '</div>')($scope));
            if ((index + 1) % tableRowsLength.length === 0) {
                isBackgroundWhite = !isBackgroundWhite;
            }
        });
    };

    $scope.getDateFrom  = function() {
        var date = new Date();
        switch($scope.dashboard.dateGroupType) {
        case 'TODAY':
            if (date.getDay() === 1) {
                date.setDate(date.getDate() - 1);
            }
            break;
        case 'YESTERDAY':
            if (date.getDay() === 1) {
                date.setDate(date.getDate() - 3);
            } else {
                date.setDate(date.getDate() - 1);
            }
            break;
        case 'WEEK_TO_DATE':
            date.setDate(date.getDate() - date.getDay());
            break;
        case 'THIS_WEEK':
            date.setDate(date.getDate() - date.getDay() + 1);
            break;
        case 'LAST_WEEK':
            date.setDate(date.getDate() - date.getDay() - 7);
            break;
        case 'MONTH_TO_DATE':
            date.setDate(1);
            break;
        case 'THIS_MONTH':
            date.setDate(1);
            break;
        case 'LAST_MONTH':
            date.setDate(0);
            date.setDate(1);
            break;
        case 'YEAR_TO_DATE':
            date.setDate(1);
            date.setMonth(0);
            break;
        case 'LAST_YEAR':
            date.setDate(1);
            date.setMonth(0);
            date.setFullYear(date.getFullYear() - 1);
            break;
        }
        return $scope.$root.formatDate(date);
    };

    $scope.getDateTo = function() {
        var date = new Date();
        switch($scope.dashboard.dateGroupType) {
        case 'YESTERDAY':
            if (date.getDay() === 1) {
                date.setDate(date.getDate() - 2);
            } else {
                date.setDate(date.getDate() - 1);
            }
            break;
        case 'THIS_WEEK':
            date.setDate(date.getDate() - date.getDay() + 7);
            break;
        case 'LAST_WEEK':
            date.setDate(date.getDate() - date.getDay() - 1);
            break;
        case 'LAST_MONTH':
            date.setDate(0);
            break;
        case 'THIS_MONTH':
            date.setDate(1);
            date.setMonth(date.getMonth() + 1);
            date.setDate(0);
            break;
        case 'LAST_YEAR':
            date.setMonth(0);
            date.setDate(0);
            break;
        }
        return $scope.$root.formatDate(date);
    };

    function format(tempDate) {
        var date = new Date(tempDate);
        return date.getMonth() + 1 + '/' + date.getDate() + '/' + date.getFullYear();
    }

    $scope.setDataText = function() {
        var startDate1, endDate1, startDate2 ,endDate2;
        switch($scope.dashboard.dateGroupType) {
        case 'TODAY':
            endDate1 = new Date();
            startDate1 = new Date().setDate(new Date().getDate() - 1);
            $scope.$parent.dateText = format(endDate1) + "  over  " + format(startDate1);
            break;
        case 'YESTERDAY':
            endDate1 = new Date().setDate(new Date().getDate() - 1);
            startDate1 = new Date().setDate(new Date().getDate() - 2);
            $scope.$parent.dateText = format(endDate1) + "  over  " + format(startDate1);
            break;
        case 'WEEK_TO_DATE':
            startDate1 = new Date().setDate(new Date().getDate() - new Date().getDay() + 1);
            endDate1 = new Date();
            startDate2 = new Date().setDate(new Date().getDate() - new Date().getDay() - 6);
            endDate2 = new Date().setDate(new Date().getDate() - 7);
            $scope.$parent.dateText = format(startDate1) + " - " + format(endDate1) + "  over  " 
            + format(startDate2) + " - " + format(endDate2);
            break;
        case 'THIS_WEEK':
            startDate1 = new Date().setDate(new Date().getDate() - new Date().getDay() + 1);
            endDate1 = new Date().setDate(new Date().getDate() - new Date().getDay() + 7);
            startDate2 = new Date().setDate(new Date().getDate() - new Date().getDay() - 6);
            endDate2 = new Date().setDate(new Date().getDate() - new Date().getDay());
            $scope.$parent.dateText = format(startDate1) + " - " + format(endDate1) + "  over  " 
            + format(startDate2) + " - " + format(endDate2);
            break;
        case 'LAST_WEEK':
            startDate1 = new Date().setDate(new Date().getDate() - new Date().getDay() - 6);
            endDate1 = new Date().setDate(new Date().getDate() - new Date().getDay());
            startDate2 = new Date().setDate(new Date().getDate() - new Date().getDay() - 13);
            endDate2 = new Date().setDate(new Date().getDate() - new Date().getDay() - 7);
            $scope.$parent.dateText = format(startDate1) + " - " + format(endDate1) + "  over  " 
            + format(startDate2) + " - " + format(endDate2);
            break;
        case 'MONTH_TO_DATE':
            startDate1 = new Date().setDate(1);
            endDate1 = new Date();
            startDate2 = new Date(new Date().setDate(1));
            startDate2.setMonth(new Date().getMonth()-1);
            endDate2 = new Date(new Date().setDate(new Date().getDate()));
            endDate2.setMonth(new Date().getMonth()-1);
            $scope.$parent.dateText = format(startDate1) + " - " + format(endDate1) + "  over  "
            + format(startDate2) + " - " + format(endDate2);
            break;
        case 'THIS_MONTH':
            startDate1 = new Date().setDate(1);
            endDate1 = new Date(new Date().setMonth(new Date().getMonth() + 1));  
            endDate1.setDate(0);
            startDate2 = new Date(new Date().setDate(1));
            startDate2.setMonth(new Date().getMonth()-1);
            endDate2 = new Date(new Date().setDate(0));
            $scope.$parent.dateText = format(startDate1) + " - " + format(endDate1) + "  over  " 
            + format(startDate2) + " - " + format(endDate2);
            break;
        case 'LAST_MONTH':
            startDate1 = new Date(new Date().setDate(1));
            startDate1.setMonth(new Date().getMonth()-1);
            endDate1 = new Date(new Date().setDate(0));
            startDate2 = new Date(new Date().setDate(1));
            startDate2.setMonth(new Date().getMonth()-2);
            endDate2 = new Date(new Date().setMonth(new Date().getMonth() - 1));  
            endDate2.setDate(0);
            $scope.$parent.dateText = format(startDate1) + " - " + format(endDate1) + "  over  " 
            + format(startDate2) + " - " + format(endDate2);
            break;
        case 'YEAR_TO_DATE':
            startDate1 = new Date(new Date().setMonth(0));
            startDate1.setDate(1);
            endDate1 = new Date();
            startDate2 = new Date(new Date().getFullYear() - 1, 0, 1);
            endDate2 = new Date().setFullYear(new Date().getFullYear() -1);
            $scope.$parent.dateText = format(startDate1) + " - " + format(endDate1) + "  over  "
            + format(startDate2) + " - " + format(endDate2);
            break;
        }
    };

    visualize({
        auth: {
            name: jasperObj.name,
            password: jasperObj.password,
            organization: ""
        }
    }, function (v) {
        $scope.v = v;
        $scope.$broadcast('dashboard:load-reports');
    });

    $scope.getCurrentUserOrgId = function() {
//        if ($scope.$root.authData.customerUser && $scope.$root.authData.organization && $scope.$root.authData.organization.orgId) {
//            return $scope.$root.authData.organization.orgId;
//        }
        return 0;
    };

    $scope.test = {
        testMode: $routeParams.test,
        person: 206286,
        customer: 0,
        reportType: 'customer' // customer, salesrep, manager
    };

    $scope.getRevenueAndMarginReport = function(){
        $scope.$broadcast('dashboard:load-reports-revenue-and-margin');
    };

    $scope.teams = [{name: 'All'}];

    $scope.isCustomerDashboard = function() {
        return (!$scope.test.testMode && $scope.$root.isPlsPermissions('CAN_ACCESS_CUSTOMER_DASHBOARD')
                && !$scope.$root.isPlsPermissions('CAN_ACCESS_SALES_REP_DASHBOARD || CAN_ACCESS_MANAGER_DASHBOARD'))
                        || ($scope.test.testMode && $scope.test.reportType==='customer');
    };

    $scope.isSalesRepDashboard = function() {
        return (!$scope.test.testMode && $scope.$root.isPlsPermissions('CAN_ACCESS_SALES_REP_DASHBOARD')
                && !$scope.$root.isPlsPermissions('CAN_ACCESS_MANAGER_DASHBOARD'))
                        || ($scope.test.testMode && $scope.test.reportType==='salesrep');
    };

    $scope.isManagerDashboard = function() {
        return (!$scope.test.testMode && $scope.$root.isPlsPermissions('CAN_ACCESS_MANAGER_DASHBOARD'))
                || ($scope.test.testMode && $scope.test.reportType==='manager');
    };

    if ($scope.test.testMode || $scope.isManagerDashboard()) {
        TeamsService.getTeams({}, function (data) {
            if (data && data.length) {
                $scope.teams = $scope.teams.concat(data);
            }
        });
    }

    $scope.updateAllReports = function() {
        setTimeout(function(){
            $scope.$broadcast('dashboard:load-reports');
        }, 200);
    };

    $scope.dashboards = {
        showDetailsDialog: false,
        detailDialogOptions: {
        }
    };

    $scope.pageSize = 10;
    $scope.currentFirstRow = 1;

    $scope.closeDialog = function() {
        $scope.dashboards.showDetailsDialog = false;
        $scope.currentFirstRow = 1;
    };

    function renderReport() {
        $scope.$root.progressPanelOptions.showPanel = true;
        $scope.$root.progressPanelOptions.progressText = 'Loading Report...';
        var reportParameters = angular.copy($scope.dashboards.reportParameters);
        if ($scope.dashboards.usePagination) {
            reportParameters.P_PAGE_SIZE_MIN = [$scope.currentFirstRow];
            reportParameters.P_PAGE_SIZE_MAX = [$scope.currentFirstRow + $scope.pageSize];
            if ($scope.test.testMode) {
                console.log(JSON.stringify(reportParameters), 'Render Details Report');
            }
        }
        $scope.report = $scope.v.report({
            resource: $scope.getReportsBaseURL() + $scope.dashboards.reportUrl,
            container: "#customerDetailsReport",
            params: reportParameters,
            success: function () {
                $scope.$apply(function() {
                    $scope.currentRowsCount = $('#customerDetailsReport tr:has(span)').size() - 1;
                    $scope.$root.progressPanelOptions.showPanel = false;
                });
            },
            error: function (err) {
                console.log('error', err);
                $scope.$apply(function() {
                    $scope.$root.progressPanelOptions.showPanel = false;
                });
            }
        });
    }

    $scope.getNextPage = function() {
        if ($scope.currentRowsCount && $scope.currentRowsCount === $scope.pageSize) {
            $scope.currentFirstRow += $scope.pageSize;
            renderReport();
        }
    };

    $scope.getPreviousPage = function() {
        if ($scope.currentFirstRow !== 1) {
            $scope.currentFirstRow -= $scope.pageSize;
            renderReport();
        }
    };

    $scope.showDetailsReport = function(modalHeader, reportUrl, reportParameters, usePagination) {
        $scope.dashboards.modalHeader = modalHeader;
        $scope.dashboards.reportUrl = reportUrl;
        $scope.dashboards.reportParameters = reportParameters;
        $scope.dashboards.usePagination = usePagination;
        renderReport();
        $scope.dashboards.showDetailsDialog = true;
    };

    function exportIt(report) {
        report['export']({ // export is a reserved word. Should call this method this strange way so that jslint doesn't complain about that.
            outputFormat: "xls"
        })
        .done(function (link) {
            $scope.$apply(function() {
                $scope.$root.progressPanelOptions.showPanel = false;
            });
            window.open(link.href);
        })
        .fail(function (err) {
            $scope.$apply(function() {
                $scope.$root.progressPanelOptions.showPanel = false;
            });
            console.log(err.message, err);
        });
    }

    $scope.exportReport = function() {
        $scope.$root.progressPanelOptions.showPanel = true;
        $scope.$root.progressPanelOptions.progressText = 'Loading Report...';
        if ($scope.dashboards.usePagination) {
            var reportParameters = angular.copy($scope.dashboards.reportParameters);
            reportParameters.P_PAGE_SIZE_MIN = [0];
            reportParameters.P_PAGE_SIZE_MAX = [0];
            var report = '';
            report = $scope.v.report({
                resource: $scope.getReportsBaseURL() + $scope.dashboards.reportUrl,
                container: "#hiddenReport",
                params: reportParameters,
                success: function () {
                    exportIt(report);
                },
                error: function (err) {
                    console.log('error', err);
                    $scope.$apply(function() {
                        $scope.$root.progressPanelOptions.showPanel = false;
                    });
                }
            });
        } else {
            exportIt($scope.report);
        }
    };

    $scope.isHideDetailsLink = function() {
        return window.innerWidth < 1190 || window.innerHeight < 630;
    };

    $scope.report = function(params) {
        $(params.container).addClass('loadingReport');
        $(params.container).innerHTML = 'Loading...';
        var parentSuccess = params.success;
        params.success = function() {
            if (parentSuccess) {
                parentSuccess();
            }
            $(params.container).removeClass('loadingReport');
        };
        var parentError = params.error;
        params.error = function() {
            if (parentError) {
                parentError();
            }
            $(params.container).innerHTML = 'Something went wrong :(';
        };
        $scope.v.report(params);
    };
    $scope.d = {
        report: $scope.report
    };
}]);

angular.module('dashboard').controller('CustomerKPIDashboardCtrl', ['$scope', function ($scope) {
    $scope.getKPIReport = function() {
        var reportParameters = {
            P_ORG_ID: [$scope.getCurrentUserOrgId()],
            P_USER_ID: [$scope.$root.authData.personId]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getKPIReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "KPI_Header",
            container: "#customer-statistics",
            params: reportParameters,
            scale: "container"
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getKPIReport);
}]);

angular.module('dashboard').controller('CustomerSpendAndShipmentCountDashboardCtrl', ['$scope', '$compile', function ($scope, $compile) {
    var date = new Date();
    date.setDate(1);
    date.setMonth(date.getMonth() - 5);
    $scope.startDate = date;
    function getReportParameters(methodName) {
        var reportParameters = {
            P_START_DATE: [$scope.$root.formatDate($scope.startDate)],
            P_END_DATE: [$scope.$root.formatDate(new Date())],
            P_ORG_ID: [$scope.getCurrentUserOrgId()],
            P_USER_ID: [$scope.$root.authData.personId]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), methodName);
        }
        return reportParameters;
    }

    $scope.showSpendAndShipmentCountDetailsReport = function() {
        $scope.showDetailsReport('Cost and Shipment Count Details', 'Customer_SpendAndShipmentTableV2',
                getReportParameters('showSpendAndShipmentCountDetailsReport'), true);
    };

    $scope.getSpendAndShipmentCountReport = function() {
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Customer_SpendAndShipmentCount",
            container: "#customer-count",
            params: getReportParameters('getSpendAndShipmentCountReport'),
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    linkToElemPairs.forEach(function showCursor(pair){
                        var el = pair.element;
                        angular.element(el).replaceWith($scope.isHideDetailsLink() ? '<div></div>' :
                                $compile("<a data-ng-click='showSpendAndShipmentCountDetailsReport()'>click for details</a>")($scope));
                    });
                }
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getSpendAndShipmentCountReport);
}]);

angular.module('dashboard').controller('CustomerOnTimePercentageDashboardCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.pointType = 'P';
    $scope.timeRange = 29;

    function getReportParameters(pointType, methodName) {
        var date = new Date();
        date.setDate(date.getDate() - $scope.timeRange);

        var reportParameters = {
            P_START_DATE: [$scope.$root.formatDate(date)],
            P_END_DATE: [$scope.$root.formatDate(new Date())],
            P_ORG_ID: [$scope.getCurrentUserOrgId()],
            P_USER_ID: [$scope.$root.authData.personId],
            P_PU_DEL: [pointType]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), methodName);
        }
        return reportParameters;
    }

    $scope.getOnTimePercentageDetailsReport = function() {
        $scope.showDetailsReport('On-Time Records', 'OnTimePerformance_Table', getReportParameters('G', 'getOnTimePercentageDetailsReport'), true);
    };

    $scope.getOnTimePercentageReport = function() {
        $scope.report({
            resource: $scope.getReportsBaseURL() + "OnTimePerformance",
            container: "#customer-on-time",
            params: getReportParameters($scope.pointType, 'getOnTimePercentageReport'),
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    linkToElemPairs.forEach(function showCursor(pair){
                        var el = pair.element;
                        angular.element(el).replaceWith($scope.isHideDetailsLink() ? '<div></div>' :
                                $compile("<a data-ng-click='getOnTimePercentageDetailsReport()'>click for details</a>")($scope));
                    });
                }
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getOnTimePercentageReport);
}]);

angular.module('dashboard').controller('CustomerAccessorialSpendDashboardCtrl', ['$scope', '$compile', function ($scope, $compile) {
    var date = new Date();
    date.setMonth(date.getMonth() - 1);
    $scope.dateFrom = $scope.$root.formatDate(date);
    $scope.dateTo = $scope.$root.formatDate(new Date());

    function getReportParameters(resultType, methodName) {
        var reportParameters = {
            P_START_DATE: [$scope.dateFrom],
            P_END_DATE: [$scope.dateTo],
            P_ORG_ID: [$scope.getCurrentUserOrgId()],
            P_USER_ID: [$scope.$root.authData.personId],
            P_RESULT_TYPE: [resultType]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), methodName);
        }
        return reportParameters;
    }

    $scope.getAccessorialSpendDetailsReport = function() {
        if (!$scope.dateFrom || !$scope.dateTo) {
            return;
        }
        $scope.showDetailsReport('Cost and Accessorial Spend', 'Accessorial_Spend_Details',
                getReportParameters(1, 'getAccessorialSpendDetailsReport'), true);
    };

    $scope.getAccessorialSpendReport = function() {
        if (!$scope.dateFrom || !$scope.dateTo) {
            return;
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Accessorial_Spend",
            container: "#customer-accessorial",
            params: getReportParameters(0, 'getAccessorialSpendReport'),
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    linkToElemPairs.forEach(function showCursor(pair){
                        var el = pair.element;
                        angular.element(el).replaceWith($scope.isHideDetailsLink() ? '<div></div>' :
                                $compile("<a data-ng-click='getAccessorialSpendDetailsReport()'>click for details</a>")($scope));
                    });
                }
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getAccessorialSpendReport);
}]);

angular.module('dashboard').controller('CustomerCostPerPoundDashboardCtrl', ['$scope', '$compile', function ($scope, $compile) {
    var date = new Date();
    date.setDate(1);
    date.setMonth(date.getMonth() - 5);
    $scope.startDate = date; // this variable is used in UI
    
    $scope.details = {
        showDetailsDialog: false,
        detailDialogOptions: {
        },
        costEfficiencyGroups: [
            {key: 'ALL', description: 'None'},
            {key: 'SHIPPER', description: 'Shipper'},
            {key: 'CONSIGNEE', description: 'Consignee'},
//            {key: 'MODE', description: 'Mode'},
            {key: 'CLASS', description: 'Class'},
            {key: 'CREATOR', description: 'Creator'},
            {key: 'PRODUCT', description: 'Product'},
            {key: 'CARRIER', description: 'Carrier'},
            {key: 'WEIGHT_BREAK', description: 'Weight Break'}
        ]
    };

    $scope.costEfficiencyGroupByCategory = 'ALL';


    $scope.getCostPerPoundDetailsReport = function() {
        var reportParameters = {
            P_START_DATE: [$scope.$root.formatDate(date)],
            P_END_DATE: [$scope.$root.formatDate(new Date())],
            P_GROUP_BY: ['MONTH'],
            P_ORG_ID: [$scope.getCurrentUserOrgId()],
            P_CATEGORY: [$scope.costEfficiencyGroupByCategory],
            P_REPORT: ['COST_PER_POUND'],
            P_USER_ID: [$scope.$root.authData.personId]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getCostPerPoundDetailsReport');
        }
        $scope.d.report({
            resource: $scope.getReportsBaseURL() + 'Cost_Efficiency',
            container: "#costEfficiencyReport",
            params: reportParameters
        });
        $scope.details.showDetailsDialog = true;
    };

    $scope.getCostPerPoundReport = function() {
        var reportParameters = {
            P_START_DATE: [$scope.$root.formatDate(date)],
            P_END_DATE: [$scope.$root.formatDate(new Date())],
            P_ORG_ID: [$scope.getCurrentUserOrgId()],
            P_USER_ID: [$scope.$root.authData.personId],
            P_GROUP_BY: ['MONTH'],
            P_CATEGORY: ['ALL']
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getCostPerPoundReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Customer_CostPerPound",
            container: "#customer-cost-per-pound",
            params: reportParameters,
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    linkToElemPairs.forEach(function showCursor(pair){
                        var el = pair.element;
                        angular.element(el).replaceWith($scope.isHideDetailsLink() ? '<div></div>' :
                            $compile("<a data-ng-click='getCostPerPoundDetailsReport()'>click for details</a>")($scope));
                    });
                }
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getCostPerPoundReport);
}]);

angular.module('dashboard').controller('CustomerMissedSavingsDashboardCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.category = 'CARRIER';

    function getReportParameters(category, methodName) {
        var date = new Date();
        date.setDate(1);
        date.setMonth(date.getMonth() - 5);
        var reportParameters = {
            P_START_DATE: [$scope.$root.formatDate(date)],
            P_END_DATE: [$scope.$root.formatDate(new Date())],
            P_ORG_ID: [$scope.getCurrentUserOrgId()],
            P_USER_ID: [$scope.$root.authData.personId],
            P_CATEGORY: [category]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), methodName);
        }
        return reportParameters;
    }

    $scope.getCustomerSavingsDetilsReport = function() {
        $scope.showDetailsReport('Missed Saving Opportunity', 'MissedSavingsOpp_Table',
                getReportParameters('DETAIL', 'getCustomerSavingsDetilsReport'), true);
    };

    $scope.getCustomerSavingsReport = function() {
        $scope.report({
            resource: $scope.getReportsBaseURL() + "MissedSavingOpp",
            container: "#customer-savings",
            params: getReportParameters($scope.category, 'getCustomerSavingsReport'),
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    linkToElemPairs.forEach(function showCursor(pair){
                        var el = pair.element;
                        angular.element(el).replaceWith($scope.isHideDetailsLink() ? '<div></div>' :
                                $compile("<a data-ng-click='getCustomerSavingsDetilsReport()'>click for details</a>")($scope));
                    });
                }
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getCustomerSavingsReport);
}]);

angular.module('dashboard').controller('CustomerPickupAndDeliveriesDashboardCtrl', ['$scope', '$filter', 'ShipmentDetailsService',
'NgGridPluginFactory', function ($scope, $filter, ShipmentDetailsService, NgGridPluginFactory) {
    $scope.timeRange = 0;

    $scope.pickups = [];
    $scope.deliveries = [];
    $scope.allLocations = [];

    $scope.details = {
        showDetailsDialog: false,
        modalHeader: '',
        gridData: []
    };

    $scope.gridOptions = {
        data: 'details.gridData',
        columnDefs: [
            {
                field: 'loadId',
                displayName: 'Load ID',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'bolNumber',
                displayName: 'BOL',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'refNumber',
                displayName: 'Ship Ref#',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'poNumber',
                displayName: 'PO#',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'status',
                displayName: 'Status',
                cellFilter: 'shipmentStatus',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'pickupDate',
                displayName: 'Pickup Date',
                cellFilter: 'date:appDateTimeFormat',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'estDeliveryDate',
                displayName: 'Est. Delivery Date',
                cellFilter: 'date:appDateTimeFormat',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'carrierName',
                displayName: 'Carrier',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'shipper',
                displayName: 'Shipper',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'origin',
                displayName: 'Origin',
                cellFilter: 'zip',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'consignee',
                displayName: 'Consignee',
                cellClass: 'text-center',
                headerClass: 'text-center'
            },
            {
                field: 'destination',
                displayName: 'Destination',
                cellFilter: 'zip',
                cellClass: 'text-center',
                headerClass: 'text-center'
            }
        ],
        plugins: [NgGridPluginFactory.plsGrid()],
        enableColumnResize: true,
        enableRowSelection: false
    };

    $scope.openDetails = function(item, origin) {
        $scope.gridOptions.columnDefs.forEach(function(c){c.visible = true;});
        if (item) {
            $scope.details.modalHeader = (origin ? 'Pickups@' : 'Deliveries@') + $filter('zip')(item);
            $scope.gridOptions.columnDefs[4].visible = false;
            $scope.gridOptions.columnDefs[9].visible = false;
            $scope.gridOptions.columnDefs[11].visible = false;
            if (origin) {
                $scope.gridOptions.columnDefs[6].visible = false;
                $scope.gridOptions.columnDefs[10].visible = false;
            } else {
                $scope.gridOptions.columnDefs[5].visible = false;
                $scope.gridOptions.columnDefs[8].visible = false;
            }
        } else {
            $scope.details.modalHeader = 'Pickups and Deliveries';
        }
        ShipmentDetailsService.getLocationLoadDetails({
            origin:origin,
            dateType: $scope.timeRange
        }, item, function(data) {
            $scope.details.gridData = data;
            $scope.details.showDetailsDialog = true;
        });
    };

    $scope.filterReportData = function() {
        $scope.pickups = _.where($scope.allLocations, {type: 'P', days: $scope.timeRange});
        $scope.pickupsCount = _.reduce($scope.pickups, function(memo, item){ return memo + item.count; }, 0);
        $scope.deliveries = _.where($scope.allLocations, {type: 'D', days: $scope.timeRange});
        $scope.deliveriesCount = _.reduce($scope.deliveries, function(memo, item){ return memo + item.count; }, 0);
    };

    ShipmentDetailsService.getLocationDetails({}, function(data) {
        if (data && data.length) {
            $scope.allLocations = data;
            $scope.filterReportData();
        }
    });
}]);

angular.module('dashboard').controller('CustomerLoadInfoDashboardCtrl', ['$scope', function ($scope) {
    $scope.cleanLoadInfo = function() {
        $scope.timeRange = 'TODAY';
        $scope.previousZipType = 'ALL';
        delete $scope.zipType;
        delete $scope.builtBy;
        delete $scope.zipCode;
    };
    $scope.cleanLoadInfo();

    $scope.$watch('zipCode', function(newVal, oldVal) {
        if (newVal && newVal !== oldVal) {
            $scope.zipType = $scope.previousZipType;
        } else if (!newVal && $scope.zipType) {
            $scope.previousZipType = $scope.zipType;
            delete $scope.zipType;
        }
    });

    $scope.setZipType = function(newZipType) {
        if ($scope.zipCode) {
            $scope.zipType = newZipType;
        }
    };

    function getDateFromForLoadInformationReport() {
        var date = new Date();
        switch($scope.timeRange) {
        case 'PAST':
            date.setFullYear(date.getFullYear() - 10);
            break;
        case 'TOMORROW':
        case 'TOMORROW+':
            date.setDate(date.getDate() + 1);
            break;
        }
        return $scope.$root.formatDate(date);
    }

    function getDateToForLoadInformationReport() {
        var date = new Date();
        switch($scope.timeRange) {
        case 'PAST':
            date.setDate(date.getDate() - 1);
            break;
        case 'TOMORROW':
            date.setDate(date.getDate() + 1);
            break;
        case 'TOMORROW+':
            date.setFullYear(date.getFullYear() + 10);
            break;
        }
        return $scope.$root.formatDate(date);
    }

    $scope.getLoadInfoReport = function() {
        var reportParameters = {
            P_START_DATE: [getDateFromForLoadInformationReport()],
            P_END_DATE: [getDateToForLoadInformationReport()],
            P_ORG_ID: [$scope.getCurrentUserOrgId()],
            P_USER_ID: [$scope.$root.authData.personId],
            P_INB_OUTB: [$scope.zipType ? $scope.zipType : "ALL"],
            P_BUILT_BY: [$scope.builtBy ? $scope.builtBy.id : 0],
            P_ZIP_CODE: [$scope.zipCode && $scope.zipCode.zip ? $scope.zipCode.zip : "ALL"]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getLoadInfoReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Load_Information_Map",
            container: "#load-info-map-container",
            params: reportParameters
        });
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Load_Information_List",
            container: "#load-info-list-container",
            params: reportParameters
        });
    };
    $scope.$on('dashboard:load-reports', $scope.getLoadInfoReport);
}]);

angular.module('dashboard').controller('AEStatisticsDashboardCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.timeFrame = 0;

    $scope.getSalesRepStatiscsReport = function() {
        $scope.dashboard.dateGroupType = _.findWhere($scope.dateGroupTypes, {index : $scope.timeFrame}).key;
        var reportParameters = {
            P_ORG_ID: [$scope.getCurrentUserOrgId()],
            P_USER_ID: [$scope.$root.authData.personId],
            P_START_DATE: [$scope.getDateFrom()],
            P_END_DATE : [$scope.getDateTo()]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getSalesRepStatiscsReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "KPI_Header_AE",
            container: "#ae-statistics",
            params: reportParameters,
            scale: "container",
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    linkToElemPairs.forEach(function showCursor(pair){
                        var el = pair.element;
                        var timeFrameIndex = _.findWhere($scope.dateGroupTypes, {description : el.innerText}).index;
                        angular.element(el).replaceWith(
                             $compile('<ul class="nav nav-pills pls-margin-bottom-0"><li data-ng-class="{active: timeFrame=='+ timeFrameIndex  
                                     + '}"><a data-ng-click="timeFrame='+ timeFrameIndex 
                                     + '; getSalesRepStatiscsReport()">' + el.innerText +'</a></li></ul>')($scope));
                    });
                }
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getSalesRepStatiscsReport);
}]);

angular.module('dashboard').controller('AELeaderBoardDashboardCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.timeFrame = 0;

    $scope.getLeaderBoardReport = function() {
        $scope.dashboard.dateGroupType = _.findWhere($scope.dateGroupTypes, {index : $scope.timeFrame}).key;
        var reportParameters = {
            P_USER_ID: [$scope.$root.authData.personId],
            P_START_DATE: [$scope.getDateFrom()],
            P_END_DATE : [$scope.getDateTo()]
        };
        if ($scope.test.testMode) {
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getLeaderBoardReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "LeaderBoard_AE",
            container: "#ae-leaderboard",
            params: reportParameters,
            scale: "container",
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    linkToElemPairs.forEach(function showCursor(pair){
                        var el = pair.element;
                        var dateType = _.findWhere($scope.dateGroupTypes, {description : el.innerText});
                        if (dateType) {
                            var timeFrameIndex = dateType.index;
                            angular.element(el).replaceWith(
                                $compile('<ul class="nav nav-pills"><li data-ng-class="{active: timeFrame=='+ timeFrameIndex  
                                     + '}"><a data-ng-click="timeFrame='+ timeFrameIndex 
                                     + '; getLeaderBoardReport()">' + el.innerText +'</a></li></ul>')($scope));
                        } else {
                            $scope.addFadeOutEffect(linkToElemPairs.slice(0, 3), [30]);
                        }
                    });
                }
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getLeaderBoardReport);
}]);

angular.module('dashboard').controller('AECollectionsAgingDashboardCtrl', ['$scope', function ($scope) {
    $scope.getCollectionsAgingReport = function() {
        var reportParameters = {
            P_SALES_REP: [$scope.$root.authData.personId]
        };
        if ($scope.test.testMode) {
            reportParameters.P_SALES_REP[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getCollectionsAgingReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Aging_AE",
            container: "#ae-collections-aging",
            params: reportParameters,
            scale: "container"
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getCollectionsAgingReport);
}]);

angular.module('dashboard').controller('AELoadStatusDashboardCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.loadStatusIndex = 0;

    $scope.loadStatusTypes = [
        {key: 'MP', description: 'Missed Picks', index: 0},
        {key: 'PT', description: 'Pick Today', index: 1},
        {key: 'NC', description: 'No Carrier', index: 2},
        {key: 'MD', description: 'Missed Drops', index: 3},
        {key: 'DT', description: 'Drop Today', index: 4}
    ];

    $scope.getLoadStatusReport = function() {
        $("#load-status-loading").show();
        var reportParameters = {
            P_USER_ID: [$scope.$root.authData.personId],
            P_LOAD_STATUS: [_.findWhere($scope.loadStatusTypes, {index: $scope.loadStatusIndex}).description]
        };
        if ($scope.test.testMode) {
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getLoadStatusReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Load_Statuses",
            container: "#ae-load-status",
            params: reportParameters,
            scale: "container",
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    $("#load-status-loading").hide();
                    linkToElemPairs.forEach(function showCursor(pair){
                        var el = pair.element;
                        var loadStatusIndex = _.findWhere($scope.loadStatusTypes, {description : el.textContent}).index;
                        var statusLabel = el.textContent.split(" ");
                        angular.element(el).replaceWith(
                             $compile('<ul class="nav nav-pills pls-margin-bottom-0"><li data-ng-class="{active: loadStatusIndex=='+ loadStatusIndex
                                     + '}"><a data-ng-click="loadStatusIndex='+ loadStatusIndex 
                                     + '; getLoadStatusReport()">' + statusLabel[0] + '<br/>' + statusLabel[1] +'</a></li></ul>')($scope));
                    });
                }
            }
        });
        reportParameters = {
            P_USER_ID: [$scope.$root.authData.personId],
            P_LOAD_STATUS: [_.findWhere($scope.loadStatusTypes, {index: $scope.loadStatusIndex}).key]
        };
        if ($scope.test.testMode) {
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getLoadStatusTableReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Load_Statuses_Table",
            container: "#ae-load-status-table",
            params: reportParameters,
            scale: "container",
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    $scope.addFadeOutEffect(linkToElemPairs, [15, 10, 10]);
                }
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getLoadStatusReport);
}]);

angular.module('dashboard').controller('AEPhoneStatsDashboardCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.timeFrame = 0;

    $scope.getPhoneStatsReport = function() {
        $scope.dashboard.dateGroupType = _.findWhere($scope.dateGroupTypes, {index : $scope.timeFrame}).key;
        var reportParameters = {
            P_SALES_REP: [$scope.$root.authData.personId],
            P_START_DATE: [$scope.getDateFrom()],
            P_END_DATE : [$scope.getDateTo()]
        };
        if ($scope.test.testMode) {
            reportParameters.P_SALES_REP[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getPhoneStatsReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "PhoneStats",
            container: "#ae-phone-stats",
            params: reportParameters,
            scale: "container",
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    linkToElemPairs.forEach(function showCursor(pair){
                        var el = pair.element;
                        var timeFrameIndex = _.findWhere($scope.dateGroupTypes, {description : el.innerText}).index;
                        angular.element(el).replaceWith(
                             $compile('<ul class="nav nav-pills"><li data-ng-class="{active: timeFrame=='+ timeFrameIndex
                                     + '}"><a data-ng-click="timeFrame='+ timeFrameIndex
                                     + '; getPhoneStatsReport()">' + el.innerText +'</a></li></ul>')($scope));
                    });
                }
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getPhoneStatsReport);
}]);

angular.module('dashboard').controller('AECreditDashboardCtrl', ['$scope', function ($scope) {
    $scope.getCreditReport = function() {
        $("#ae-credit-loading").show();
        $scope.aeCreditLoading = true;
        var reportParameters = {
            P_SALES_REP: [$scope.$root.authData.personId]
        };
        if ($scope.test.testMode) {
            reportParameters.P_SALES_REP[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getCreditReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Credit",
            container: "#ae-credit",
            params: reportParameters,
            scale: "container",
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    $scope.addFadeOutEffect(linkToElemPairs, [20]);
                }
            },
            success: function () {
                $("#ae-credit-loading").hide();
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getCreditReport);
}]);

angular.module('dashboard').controller('AECommissionsCashDashboardCtrl', ['$scope', function ($scope) {
    $scope.getCommissionsCashReport = function() {
        var reportParameters = {
            P_SALES_REP: [$scope.$root.authData.personId],
            P_START_DATE: [$scope.$root.formatDate(new Date())]
        };
        if ($scope.test.testMode) {
            reportParameters.P_SALES_REP[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getCommissionsCashReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Commissions",
            container: "#ae-cash",
            params: reportParameters,
            scale: "container"
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getCommissionsCashReport);
}]);

angular.module('dashboard').controller('AECustomerTrendsDashboardCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.timePeriod = 'MoM';
    $scope.salesStatistic = 'NET';
    $scope.isShowTrendBarGraph = false;
    $scope.trendingType = true;
    $scope.customers = [];

    function getMoM() {
        var currentDate = new Date();
        return {
            P_START_CUR_DATE: $scope.$root.formatDate(new Date(currentDate.getFullYear(), currentDate.getMonth(), 1)),
            P_END_CUR_DATE: $scope.$root.formatDate(currentDate),
            P_END_PREV_DATE: $scope.$root.formatDate(new Date(currentDate.getFullYear(), currentDate.getMonth(), 0)),
            P_START_PREV_DATE: $scope.$root.formatDate(new Date(currentDate.getFullYear() - (currentDate.getMonth() > 0 ? 0 : 1),
                    (currentDate.getMonth() - 1 + 12) % 12, 1))
        };
    }

    function getQoQ() {
        var today = new Date();
        var quarter = Math.floor((today.getMonth() / 3));

        var currentStartDate = new Date(today.getFullYear(), quarter * 3, 1);
        var currentEndDate = new Date(currentStartDate.getFullYear(), currentStartDate.getMonth() + 3, 0);
        var previousStartDate = new Date(today.getFullYear(), quarter * 3 - 3, 1);
        var previousEndDate = new Date(previousStartDate.getFullYear(), previousStartDate.getMonth() + 3, 0);

        return {
            P_START_CUR_DATE: $scope.$root.formatDate(currentStartDate),
            P_END_CUR_DATE: $scope.$root.formatDate(today),
            P_START_PREV_DATE: $scope.$root.formatDate(previousStartDate),
            P_END_PREV_DATE: $scope.$root.formatDate(previousEndDate)
        };
    }

    function getYoY() {
        var today = new Date();
        return {
            P_START_CUR_DATE: $scope.$root.formatDate(new Date(today.getFullYear(), 0, 1)),
            P_END_CUR_DATE: $scope.$root.formatDate($scope.$root.formatDate(today)),
            P_START_PREV_DATE: $scope.$root.formatDate(new Date(today.getFullYear() - 1, 0, 1)),
            P_END_PREV_DATE: $scope.$root.formatDate(new Date(today.getFullYear() - 1, 11, 31))
        };
    }

    function getTimePeriod() {
        switch ($scope.timePeriod) {
            case "MoM": return getMoM();
            case "QoQ": return getQoQ();
            case "YoY": return getYoY();
            default: return getMoM();
        }
    }

    $scope.getCustomerTrendBarGraph = function() {
        var timePeriod = getTimePeriod();
        var reportParameters = {
            P_USER_ID: [$scope.$root.authData.personId],
            P_ORG_ID: [$scope.customer],
            P_START_DATE: [timePeriod.P_START_PREV_DATE],
            P_END_DATE: [timePeriod.P_END_CUR_DATE]
        };

        if ($scope.test.testMode) {
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getCustomerTrendBarGraph');
        }

        $scope.report({
            resource: $scope.getReportsBaseURL() + "Revenue_Sales_AE",
            container: "#ae-customer-trend-bar-graph",
            params: reportParameters,
            scale: "container"
        });
    };

    function replaceCustomerColumn(linkToElemPairs, isTrendingUpType) {
        linkToElemPairs.forEach(function showCursor(pair, index){
            var el = pair.element;
            var customerId = null;
            if (!_.isEmpty(el.parentNode.previousElementSibling.textContent.trim())) {
                customerId = parseInt(el.parentNode.previousElementSibling.textContent.trim(), 10);
            }
            var customerName = el.textContent.trim();
            if (!_.isEmpty(customerName) && customerId !== null) {
                var customer = {id: customerId, name: customerName, type: isTrendingUpType};
                if (!_.contains($scope.customers, customer)) {
                    $scope.customers.push(customer);
                }
                angular.element(el).replaceWith(
                    $compile('<div class="fadeOut' + index % 2 + '">'
                            + '<a data-ng-click="isShowTrendBarGraph=true; customer=' + customerId
                            + '; trendingType=' + isTrendingUpType + '; getCustomerTrendBarGraph()" title="' + el.textContent + '">'
                            + el.textContent.substring(0, 20) + '</a></div>')($scope));
            }
        });
    }

    $scope.getCustomerTrendsReport = function() {
        $("#trend-up-loading").show();
        $("#trend-down-loading").show();
        var timePeriod = getTimePeriod();
        $scope.customers = [];

        var reportParameters = {
            P_USER_ID: [$scope.$root.authData.personId],
            P_TIME_PERIOD: [$scope.timePeriod],
            P_SALES_STATISTICS: [$scope.salesStatistic], 
            P_START_PREV_DATE: [timePeriod.P_START_PREV_DATE],
            P_END_PREV_DATE: [timePeriod.P_END_PREV_DATE],
            P_START_CUR_DATE: [timePeriod.P_START_CUR_DATE],
            P_END_CUR_DATE: [timePeriod.P_END_CUR_DATE],
            P_ORDER_BY: ['DESC']
        };
        if ($scope.test.testMode) {
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getCustomerTrendUpReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "CustomerTrendUp",
            container: "#ae-customer-trend-up",
            params: reportParameters,
            scale: "container",
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    replaceCustomerColumn(linkToElemPairs, true);
                }
            },
            success: function () {
                $("#trend-up-loading").hide();
            }
        });
        reportParameters.P_ORDER_BY[0] = 'ASC';
        if ($scope.test.testMode) {
            console.log(JSON.stringify(reportParameters), 'getCustomerTrendDownReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "CustomerTrendUp",
            container: "#ae-customer-trend-down",
            params: reportParameters,
            scale: "container",
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    replaceCustomerColumn(linkToElemPairs, false);
                }
            },
            success: function () {
                $("#trend-down-loading").hide();
            }
        });
    };

    $scope.$on('dashboard:load-reports', $scope.getCustomerTrendsReport);
}]);

angular.module('dashboard').controller('ManagerDashboardCtrl', ['$scope', 'StringUtils', '$compile','$timeout',
                                                                 function ($scope, StringUtils, $compile, $timeout) {

    $scope.clearTopItems = function(item) {
        if (item === 'customer') {
            delete $scope.accountExecutive;
        }
        if (item === 'customer' || item === 'AE') {
            $scope.team = 'All';
        }
        $scope.branch = 'All';
    };

    function getSalesRepId() {
        return $scope.accountExecutive ? $scope.accountExecutive.id : 0;
    }

    function getMyShipmentsReport() {
        var reportParameters = {
            P_ORG_ID: [$scope.customer ? $scope.customer.id : 0],
            P_START_DATE: [$scope.getDateFrom()],
            P_END_DATE: [$scope.getDateTo()],
            P_MODE: [$scope.modeType],
            P_BRANCH: [$scope.branch],
            P_TEAM: [$scope.team],
            P_USER_ID: [$scope.$root.authData.personId],
            P_SALES_REP: [getSalesRepId()]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getMyShipmentsReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "MyShipments_Graph",
            container: "#my-shipments-graph-container",
            params: reportParameters
        });
        $scope.report({
            resource: $scope.getReportsBaseURL() + "MyShipments_Table",
            container: "#my-shipments-list-container",
            params: reportParameters
        });
    }
    function getLeaderBoardReport(data) {
        var reportParameters = {
                P_ORG_ID: [$scope.customer ? $scope.customer.id : 0],
                P_START_DATE: [$scope.getDateFrom()],
                P_END_DATE: [$scope.getDateTo()],
                P_MODE: [$scope.modeType],
                P_BRANCH: [$scope.branch],
                P_TEAM: [$scope.team],
                P_USER_ID: [$scope.$root.authData.personId],
                P_SALES_REP: [getSalesRepId()]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getLeaderBoardReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "LeaderBoard",
            container: "#leader-board-container",
            params: reportParameters
        });
        $scope.report({
            resource: $scope.getReportsBaseURL() + "LeaderBoardTable",
            container: "#leader-board-list-container",
            params: reportParameters
        });
    }
    function getRevenueAndMarginReport() {
        var reportParameters = {
                P_ORG_ID: [$scope.customer ? $scope.customer.id : 0],
                P_START_DATE: [$scope.getDateFrom()],
                P_END_DATE: [$scope.getDateTo()],
                P_MODE: [$scope.modeType],
                P_BRANCH: [$scope.branch],
                P_TEAM: [$scope.team],
                P_USER_ID: [$scope.$root.authData.personId],
                P_SALES_REP: [getSalesRepId()],
                P_GROUP_BY: [$scope.groupByMR]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getRevenueAndMarginReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Revenue_Sales",
            container: "#revenue-sales-container",
            params: reportParameters,
            linkOptions: {
                beforeRender: function (linkToElemPairs) {
                    linkToElemPairs.forEach(function showCursor(pair){
                        var el = pair.element;
                        angular.element(el).replaceWith($scope.isHideDetailsLink() ? '<div></div>' :
                             $compile('<div style="margin-top: -21px;" class="form-inline span10 pull-right well-small">'
                            + '<label for="groupByMR">X-Axis Division: </label>'
                            + '<select id="groupByMR" class="input-medium" data-ng-model="groupByMR"' 
                            + 'data-ng-options="item.key as item.description for item in dataGroupBys" />'
                            + '<button class="btn" type="button" data-ng-click="getRevenueAndMarginReport()">Apply</button>'
                            + '<button class="btn" type="button" data-ng-click="showRevenueAndMarginDetailsReport()"'
                            + '>View Details</button></div>')($scope)); 
                        $timeout(function () {
                            if(!$scope.groupByMR){
                                $scope.dateGroupTypeChange();
                            }
                        });
                    });
                }
            }
        });
    }

    $scope.showRevenueAndMarginDetailsReport = function() {
        $scope.showDetailsReport('Revenue & Margin Details', 'Revenue_Sales_Table',{
            P_ORG_ID: [$scope.customer ? $scope.customer.id : 0],
            P_START_DATE: [$scope.getDateFrom()],
            P_END_DATE: [$scope.getDateTo()],
            P_MODE: [$scope.modeType],
            P_BRANCH: [$scope.branch],
            P_TEAM: [$scope.team],
            P_USER_ID: [$scope.$root.authData.personId],
            P_SALES_REP: [getSalesRepId()],
            P_GROUP_BY: [$scope.groupByMR],
            P_RESULT_TYPE : [1]
        }, true);
    };

    function getGoodCustomerHealthReport() {
        var reportParameters = {
                P_ORG_ID: [$scope.customer ? $scope.customer.id : 0],
                p_range: [$scope.dashboard.dateGroupType],
                P_MODE: [$scope.modeType],
                P_BRANCH: [$scope.branch],
                P_TEAM: [$scope.team],
                P_USER_ID: [$scope.$root.authData.personId],
                P_SALES_REP: [getSalesRepId()],
                p_order_by: ['DESC']
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getGoodCustomerHealthReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Good_Customer_Health",
            container: "#good-health-container",
            params: reportParameters
        });
    }
    function getPoorCustomerHealthReport() {
        var reportParameters = {
                P_ORG_ID: [$scope.customer ? $scope.customer.id : 0],
                p_range: [$scope.dashboard.dateGroupType],
                P_MODE: [$scope.modeType],
                P_BRANCH: [$scope.branch],
                P_TEAM: [$scope.team],
                P_USER_ID: [$scope.$root.authData.personId],
                P_SALES_REP: [getSalesRepId()],
                p_order_by: ['ASC']
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getPoorCustomerHealthReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Poor_Customer_Health",
            container: "#poor-health-container",
            params: reportParameters
        });
    }
    function getLoadInformationReport() {
        var reportParameters = {
                P_ORG_ID: [$scope.customer ? $scope.customer.id : 0],
                P_START_DATE: [$scope.getDateFrom()],
                P_END_DATE: [$scope.getDateTo()],
                P_MODE: [$scope.modeType],
                P_BRANCH: [$scope.branch],
                P_TEAM: [$scope.team],
                P_USER_ID: [$scope.$root.authData.personId],
                P_SALES_REP: [getSalesRepId()]
        };
        if ($scope.test.testMode) {
            reportParameters.P_ORG_ID[0] = $scope.test.customer;
            reportParameters.P_USER_ID[0] = $scope.test.person;
            console.log(JSON.stringify(reportParameters), 'getLoadInformationReport');
        }
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Load_Information_HeatMap",
            container: "#load-info-map-container",
            params: reportParameters
        });
        $scope.report({
            resource: $scope.getReportsBaseURL() + "Load_Information_List_Ship",
            container: "#load-info-list-container",
            params: reportParameters
        });
    }

    $scope.setDataText();

    $scope.cleanSalesRep();

    $scope.getManagerReports = function() {
        getMyShipmentsReport();
        getLeaderBoardReport();
        getRevenueAndMarginReport();
        getGoodCustomerHealthReport();
        getPoorCustomerHealthReport();
//        getLoadInformationReport();
        $scope.setDataText();
    };

    $scope.getRevenueAndMarginReport = function(){
        getRevenueAndMarginReport();
    };

    $scope.$on('dashboard:load-reports-revenue-and-margin', $scope.getRevenueAndMarginReport);

    $scope.$on('dashboard:load-reports', $scope.getManagerReports);
}]);