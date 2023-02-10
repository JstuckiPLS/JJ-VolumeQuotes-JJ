angular.module('reports').controller('ReportsController', ['$scope', 'urlConfig', 'DictionaryService', 'UserNetworksService',
    'DateTimeUtils', function ($scope, urlConfig, DictionaryService, UserNetworksService, DateTimeUtils) {
        'use strict';

        $scope.reportCapabilities = [
            {name: 'Unbilled Report', value: 'EXECUTE_UNBILLED_REPORT'},
            {name: 'Savings Report', value: 'EXECUTE_SAVINGS_REPORT'},
            {name: 'Activity Report', value: 'EXECUTE_ACTIVITY_REPORT'},
            {name: 'Carrier Activity Report', value: 'EXECUTE_CARRIER_ACTIVITY_REPORT'},
            {name: 'Lost Savings Opportunity Report', value: 'EXECUTE_LOST_SAVINGS_OPPORTUNITY_REPORT'},
            {name: 'Shipment Creation Report', value: 'EXECUTE_SHIPMENT_CREATION_REPORT'},
            {name: 'Batched Invoice Report', value: 'BATCHED_INVOICE_REPORT'}
        ];

        $scope.reportNames = [];
        $scope.selectedReportName = undefined;
        $scope.countryCodes = [];
        $scope.businessUnitNameList = [{name:'All', value: {name: 'All', id: -1}}];
        $scope.businessUnitName = undefined;
        $scope.selectedCarrier = undefined;
        $scope.hideCompanyCode = true;
        $scope.customerDisabled = false;
        $scope.selectedCountryCode = '';
        $scope.disableExportButton = true;
        $scope.showInvoiceReportButton = false;
        $scope.showExportJasperReportButton = false;
        $scope.invoicedShipmentsOnly = false;

        $scope.sortOrderList = [
            {name:'EstPickupDate', value:'Estimated Pickup Date'},
            {name:'UserName', value:'User Name'},
            {name:'PotentialSavings', value:'Potential Savings'}
        ];

        $scope.reportDateTypes = [
            {name: 'GL', value: 'GL Date'},
            {name: 'SHIP', value: 'Ship Date'}
        ];

        $scope.reportDateTypeSelection= $scope.reportDateTypes[0].name;

        // Increased from 13 to 25, gives opportunity to go back 2yrs.
        var MIN_RANGE_START_DATE_IN_MONTHS = 25;
        var MAX_RANGE_END_DATE_IN_DAYS = 30;
        var minStDate = new Date();
        var maxStDate = new Date();

        //for Unbilled & Savings & Activity Reports
        //End Date:
        $scope.minFromDate = new Date(minStDate.setMonth(minStDate.getMonth()- MIN_RANGE_START_DATE_IN_MONTHS));
        $scope.maxFromDate = new Date(maxStDate.setDate(maxStDate.getDate() + MAX_RANGE_END_DATE_IN_DAYS));

        //Start Date:
        $scope.minStartDate = DateTimeUtils.addMonths(new Date(), - MIN_RANGE_START_DATE_IN_MONTHS);
        $scope.maxStartDate = DateTimeUtils.addDays(new Date(), MAX_RANGE_END_DATE_IN_DAYS);

        $scope.setMinFromDate = function() {
            $scope.minFromDate = $scope.startDate;
        };

        //for Lost Savings Opp. Report
        //Start Date:
        $scope.minStartFromDate = DateTimeUtils.addMonths(new Date(), - MIN_RANGE_START_DATE_IN_MONTHS);
        $scope.maxStartFromDate = DateTimeUtils.addDays(new Date(), MAX_RANGE_END_DATE_IN_DAYS);

        //End Date:
        $scope.minEndFromDate = DateTimeUtils.addMonths(new Date(), - MIN_RANGE_START_DATE_IN_MONTHS);
        $scope.maxEndFromDate = DateTimeUtils.addDays(new Date(), MAX_RANGE_END_DATE_IN_DAYS);

        $scope.setMinEndFromDate = function(){
            $scope.minEndFromDate = $scope.startDate;
        };

        //Jasper Report Settings.
        var jasperObj = $scope.$root.jasperReportsObj;
        var BATCHED_INVOICE_REPORT = jasperObj.URL + 'RescoProducts_invoice';
        
        $scope.convertParamsToURL = function (obj) {
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

        $scope.init = function () {
            $scope.getBusinessUnitNameList();

            _.each($scope.reportCapabilities, function (capability) {
                if ($scope.$root.isFieldRequired(capability.value)) {
                    $scope.reportNames.push({name: capability.name.split(' ')[0], value: capability.name});
                }
            });
        };

        $scope.getBusinessUnitNameList = function () {
            UserNetworksService.activeNetworks({}, function (data) {
                if (data.length < 2) {
                    $scope.businessUnitNameList.length = 0;
                }
                _.each(data, function (element) {
                    $scope.businessUnitNameList.push({name: element.name, value: element});
                });
            });
        };
        
        $scope.businessUnitNameListFilter = function (item){
            return ($scope.selectedReportName === 'Unbilled' || item.name !== 'All' );
        };

        $scope.getAllCountryCodes = function () {
            DictionaryService.getCompanyCodes({}, function (data) {
                _.each(data, function (element) {
                    $scope.countryCodes.push({name: element, value: element.description + ' (' + element.companyCode + ')'});
                });
            });
        };

        function clearReportFields() {
            $scope.businessUnitName = undefined;
            $scope.selectedCountryCode = '';
            $scope.startDate = undefined;
            $scope.endDate = undefined;
            $scope.selectedSortOrder = undefined;
            $scope.$broadcast('event:cleaning-input');
        }

        $scope.$watch('businessUnitName', function (newValue) {
            $scope.hideCompanyCode = _.isUndefined(newValue) ? false : _.isEqual($scope.businessUnitName.id, 4);

            if ($scope.hideCompanyCode === true && _.isEmpty($scope.countryCodes)) {
                $scope.getAllCountryCodes();
            }

            $scope.selectedCountryCode = '';
        });

        $scope.$watch('selectedReportName', function () {
            clearReportFields();
        });

        $scope.isSelectedUnbilledReport = function () {
            return $scope.selectedReportName === 'Unbilled';
        };
        
        $scope.showBusinessUnitInput = function () {
            return $scope.selectedReportName !== 'Batched' && $scope.selectedReportName !== 'Carrier';
        };
        
        $scope.showCarrierInput = function () {
            return $scope.selectedReportName === 'Carrier';
        };

        $scope.isSelectedLostSavOppReport = function () {
            return $scope.selectedReportName === 'Lost';
        };

        $scope.isSelectedShipCreationRpt = function () {
            return $scope.selectedReportName === 'Shipment';
        };

        $scope.isSelectedActivityRpt = function () {
            return $scope.selectedReportName === 'Activity';
        };

        $scope.isBatchedInvoiceRpt = function () {
            return $scope.selectedReportName === 'BatchedInvoice';
        };

        $scope.isSelectedReportName = function () {
            return !_.isUndefined($scope.selectedReportName) && $scope.selectedReportName !== '';
        };

        $scope.$watch(function () {
            var checkCustomer = _.isUndefined($scope.businessUnitName) ? _.isUndefined($scope.selectedCustomer) : false;

            var checkBusinessUnit = _.isUndefined($scope.selectedCustomer)
                    ? ($scope.hideCompanyCode ? _.isEmpty($scope.selectedCountryCode) : _.isUndefined($scope.businessUnitName)) : false;

            $scope.showInvoiceReportButton = false;
            switch ($scope.selectedReportName) {
                case undefined:
                    $scope.disableExportButton = _.isUndefined($scope.selectedReportName) || _.isUndefined($scope.selectedCustomer);
                    break;
                case 'Unbilled':
                    $scope.disableExportButton = checkCustomer || checkBusinessUnit || _.isUndefined($scope.endDate);
                    break;
                case 'Savings':
                case 'Shipment':
                    $scope.disableExportButton = checkCustomer || _.isUndefined($scope.endDate) || _.isUndefined($scope.startDate);
                    break;
                case 'Activity':
                    $scope.disableExportButton = checkCustomer || _.isUndefined($scope.endDate) || _.isUndefined($scope.startDate);
                    break;
                case 'Lost':
                    $scope.disableExportButton = checkCustomer || _.isUndefined($scope.endDate)
                            || _.isUndefined($scope.startDate) || _.isUndefined($scope.selectedSortOrder);
                    break;
                case 'Carrier':
                    $scope.disableExportButton = _.isUndefined($scope.selectedCarrier) || _.isUndefined($scope.startDate) 
                            || _.isUndefined($scope.endDate);
                    break;
                case 'Batched':
                    $scope.showInvoiceReportButton = !(_.isUndefined($scope.selectedCustomer) || _.isUndefined($scope.endDate)
                        || _.isUndefined($scope.startDate));
                    break;
            }
        });

        $scope.exportToExcel = function () {
            var selectedbusinessUnitName = _.isUndefined($scope.businessUnitName)
                    ? '' : $scope.hideCompanyCode ? '' : _.isEqual($scope.businessUnitName, 'All') ? '' : $scope.businessUnitName;

            var reportParams;

            switch ($scope.selectedReportName) {
                case 'Unbilled':
                    reportParams = {
                        reportName: $scope.selectedReportName.split(' ')[0],
                        customerId: _.isUndefined($scope.selectedCustomer) ? '' : $scope.selectedCustomer.id,
                        customerName: _.isUndefined($scope.selectedCustomer) ? '' : $scope.selectedCustomer.name,
                        companyCode: $scope.hideCompanyCode ? $scope.selectedCountryCode.companyCode : '',
                        companyCodeDescription: $scope.hideCompanyCode ? $scope.selectedCountryCode.description : '',
                        businessUnitName: _.isEmpty(selectedbusinessUnitName) ? '' : $scope.businessUnitName.name,
                        businessUnitId: _.isEmpty(selectedbusinessUnitName) || _.isEqual($scope.businessUnitName.id, -1) ? ''
                                : $scope.businessUnitName.id,
                        endDate: $scope.endDate
                    };
                    break;
                case 'Savings':
                case 'Activity':
                    reportParams = {
                        reportName: $scope.selectedReportName.split(' ')[0],
                        customerId: _.isUndefined($scope.selectedCustomer) ? '' : $scope.selectedCustomer.id,
                        customerName: _.isUndefined($scope.selectedCustomer) ? '' : $scope.selectedCustomer.name,
                        companyCode: $scope.hideCompanyCode ? $scope.selectedCountryCode.companyCode : '',
                        companyCodeDescription: $scope.hideCompanyCode ? $scope.selectedCountryCode.description : '',
                        businessUnitName: _.isEmpty(selectedbusinessUnitName) ? '' : $scope.businessUnitName.name,
                        businessUnitId: _.isEmpty(selectedbusinessUnitName) || _.isEqual($scope.businessUnitName.id, -1) ? ''
                                : $scope.businessUnitName.id,
                        startDate: $scope.startDate,
                        endDate: $scope.endDate,
                        dateType: $scope.reportDateTypeSelection
                    };
                    break;
                case 'Carrier':
                    reportParams = {
                        reportName: 'Carrier Activity',
                        customerId: _.isUndefined($scope.selectedCustomer) ? '' : $scope.selectedCustomer.id,
                        customerName: _.isUndefined($scope.selectedCustomer) ? '' : $scope.selectedCustomer.name,
                        carrierId: $scope.selectedCarrier.id,
                        carrierName: $scope.selectedCarrier.name,
                        startDate: $scope.startDate,
                        endDate: $scope.endDate,
                        dateType: $scope.reportDateTypeSelection
                    };
                    break;
                case 'Shipment':
                    reportParams = {
                        reportName: $scope.selectedReportName.split(' ')[0],
                        customerId: _.isUndefined($scope.selectedCustomer) ? '' : $scope.selectedCustomer.id,
                        customerName: _.isUndefined($scope.selectedCustomer) ? '' : $scope.selectedCustomer.name,
                        businessUnitName: _.isEmpty(selectedbusinessUnitName) ? '' : $scope.businessUnitName.name,
                        businessUnitId: _.isEmpty(selectedbusinessUnitName) || _.isEqual($scope.businessUnitName.id, -1) ? ''
                                : $scope.businessUnitName.id,
                        startDate: $scope.startDate,
                        endDate: $scope.endDate,
                        invoicedShipmentsOnly: $scope.invoicedShipmentsOnly
                    };
                    break;

                case 'Lost':
                    reportParams = {
                        reportName: $scope.selectedReportName,
                        customerId: _.isUndefined($scope.selectedCustomer) ? '' : $scope.selectedCustomer.id,
                        customerName: _.isUndefined($scope.selectedCustomer) ? '' : $scope.selectedCustomer.name,
                        businessUnitName: _.isEmpty(selectedbusinessUnitName) ? '' : $scope.businessUnitName.name,
                        businessUnitId: _.isEmpty(selectedbusinessUnitName) || _.isEqual($scope.businessUnitName.id, -1) ? ''
                                : $scope.businessUnitName.id,
                        startDate: $scope.startDate,
                        endDate: $scope.endDate,
                        sortOrder: $scope.selectedSortOrder
                    };
                    break;
            }
            window.open(urlConfig.core + "/reports/export" + $scope.convertParamsToURL(reportParams));  
        };

        /**
         * Create Batched Invoiced Report using Jasper.
         */
        $scope.createBatchedInvoicedReport = function()
        {
            $scope.loadAndRenderJasperReport();
        };

        /**
         * Export the report rendered from Jasper (Only CSV as per requirement for now)
         *
         * @param report
         */
        function exportIt(report) {
            report['export']({ // export is a reserved word. Should call this method this strange way so that jslint doesn't complain about that.
                outputFormat: "csv"
            })
                .done(function (link) {
                    $scope.$apply(function () {
                        $scope.$root.progressPanelOptions.showPanel = false;
                    });
                    window.open(link.href);
                })
                .fail(function (err) {
                    $scope.$apply(function () {
                        $scope.$root.progressPanelOptions.showPanel = false;
                    });
                    console.log(err.message, err);
                });
        }

        /**
         * Jasper Integration: call Jasper and render or export report.
         */
        function makeReportRequest(URL, elementId, parameters, exportReport) {
            var report = '';
            report = $scope.v.report({
                resource: URL,
                container: elementId,
                params: parameters,
                //scale: "container",
                success: function (data) {
                    $scope.$apply(function () {
                        if (exportReport) {
                            exportIt(report);
                        } else
                        {
                            $scope.showExportJasperReportButton = true;
                            $scope.$root.$emit('event:application-success', 'Loaded Batch Invoice report!');
                        }
                    });
                },
                error: function (err) {
                    $scope.$root.$emit('event:application-error', 'Failed to load Batch Invoice report!');
                }
            });
        }


        /**
         * Render Jasper Report  (for now, we have only one report, this needs to customized once we have more reports coming from Jasper.
         *
         * @param isExport
         */
        function renderJasperReport(exportReport) {
            var reportParameters = {
                //ORG_ID : [$scope.selectedCustomer.id], Only Resco uses it for now.
                GL_DATE_FROM : [$scope.startDate],
                GL_DATE_TO : [$scope.endDate]
            };
            makeReportRequest(BATCHED_INVOICE_REPORT, '#batchedInvoiceReportContainer', reportParameters, exportReport);
        }

        /**
         * Load jasper resource and render report.
         */
        $scope.loadAndRenderJasperReport = function() {
            //$scope.infoHTML = getInfoHTML(LOADING_MESSAGE);
            visualize({
                auth: {
                    name: jasperObj.name,
                    password: jasperObj.password,
                    organization: ""
                }
            }, function (v) {
                $scope.v = v;
                renderJasperReport(false);
            });
        };

        /**
         * Export the Jasper Report( Once we have more reports and formats, this needs to be parameterized).
         */
        $scope.exportBatchedInvoiceReport = function()
        {
            renderJasperReport(true);//export = true;
        };
    }
]);
