angular.module('plsApp').controller('FuelCtrl', ['$scope', 'LibrariesFuelService', 'NgGridPluginFactory',
                                                 'DateRangeHelper',
    function ($scope, LibrariesFuelService, NgGridPluginFactory, DateRangeHelper) {
        'use strict';

        var jasperObj = $scope.$root.jasperReportsObj;

        var FUEL_GRAPH_URL = jasperObj.URL + 'Fuel_Prices_Chart',
            FUEL_TABLE_URL = jasperObj.URL + 'Fuel_Prices_Table',
            currentTab = 'Graph',
            LOADING_MESSAGE  = 'Loading...',
            WRONG_RANGE_MESSAGE = "Couldn't build report. Please, change or increase Date Range.";

        function getInfoHTML(message) {
            return "<h3><font color='black'>" + message + "</font></h3>";
        }

        function getDateObject(dateRange) {
            var dateRangeSelector = dateRange.indexOf(',') === -1 ? dateRange : undefined;
            if (dateRangeSelector) {
                return DateRangeHelper.getDates(dateRangeSelector);
            } else {
                var datesArray = dateRange.split(','); 
                return {
                    startDate: datesArray[1],
                    endDate: datesArray[2]
                };
            }
        }

        function isReportLoaded() {
            return document.querySelector('.highcharts-container');
        }

        function makeReportRequest(URL, elementId, parameters) {
            $scope.v.report({
                resource: URL,
                container: elementId,
                params: parameters,
                scale: "container",
                success: function (data) {
                    $scope.$apply(function() {
                        /*
                         * isReportLoaded() is added because sometimes returned data properties are empty or undefined even if graph is loaded. 
                         * */
                        if (!data.totalPages && _.isEmpty(data.links) && !isReportLoaded()) {
                            $scope.infoHTML = getInfoHTML(WRONG_RANGE_MESSAGE);
                        }
                    });
                },
                error: function (err) {
                    $scope.$root.$emit('event:application-error', 'Failed to load fuel report!');
                }
            });
        }

        function renderReport(dateRange) {
            var dateObject = getDateObject(dateRange);
            var reportParameters = {
                    BeginDate: [dateObject.startDate],
                    EndDate: [dateObject.endDate]
                };
            if (currentTab === 'Graph') {
                makeReportRequest(FUEL_GRAPH_URL, '#fuel-prices-graph', reportParameters);
            } else {
                makeReportRequest(FUEL_TABLE_URL, '#fuel-prices-table', reportParameters);
            }
        }

        $scope.onTabClick = function(tabName) {
            currentTab = tabName;
            if ($scope.dateRange) {
                $scope.loadJasperResource();
            }
        };

        $scope.loadJasperResource = function(tabName) {
            $scope.infoHTML = getInfoHTML(LOADING_MESSAGE);
            visualize({
                auth: {
                    name: jasperObj.name,
                    password: jasperObj.password,
                    organization: ""
                }
            }, function (v) {
                $scope.v = v;
                renderReport($scope.dateRange, tabName);
            });
        };

        $scope.$watch('dateRange', function () {
            if (!_.isUndefined($scope.dateRange) && $scope.validity) {
                $scope.loadJasperResource();
            }
        });

        $scope.loadFuel = function () {
            LibrariesFuelService.getRegionsRates({}, function (response) {
                $scope.fuelList = response;
            });
        };

        $scope.loadFuel();

        $scope.fuelGrid = {
            enableColumnResize: true,
            selectedItems: $scope.selectedItems,
            data: 'fuelList',
            enableCellSelection: true,
            enableRowSelection: false,
            enableCellEditOnFocus: true,
            columnDefs: [{
                field: 'dotRegion.description',
                displayName: 'Region',
                cellTemplate: '<div class="ngCellText" ng-class="getRowNgClass(col,row.entity)"><span ng-cell-text>' +
                '{{row.getProperty(col.field)}}</span></div>',
                enableCellEdit: false,
                width: '70%'
            }, {
                field: 'fuelCharge',
                displayName: 'Last Update',
                enableCellEdit: true,
                cellTemplate: '<div class="ngCellText" ng-class="getRowNgClass(col,row.entity)"><span ng-cell-text>' +
                '${{row.getProperty(col.field)}}</span></div>',
                editableCellTemplate: "<input data-ng-class=\"'colt' + col.index\" required data-pls-number=\"fuelCost\" data-forbid-zero=\"true\" "
                + "ng-input=\"row.entity.fuelCharge\" ng-model=\"row.entity.fuelCharge\" >",
                width: '30%'
            }],
            plugins: [NgGridPluginFactory.plsGrid()]
        };

        $scope.getRowNgClass = function (col, entity) {
            if ($scope.fuelList.indexOf(entity) === $scope.errorRowNum) {
                return "grid-error-row " + col.colIndex();
            } else {
                return col.colIndex();
            }
        };

        $scope.save = function () {
            $scope.dateRange = 'YEAR';

            var success = function (response) {
                $scope.fuelList = response;
                $scope.$root.$emit('event:operation-success', 'Fuel was successfully saved');
                $scope.errorRowNum = undefined;
            };

            var failed = function () {
                $scope.$root.$emit('event:application-error', 'Fuel save failed!');
            };

            var itemWithEmptyAverage = _.find($scope.fuelList, function (item) {
                return !item.fuelCharge;
            });

            if (itemWithEmptyAverage) {
                $scope.errorRowNum = $scope.fuelList.indexOf(itemWithEmptyAverage);
                $scope.$root.$emit('event:application-error', 'Average is required!');
                return;
            }

            LibrariesFuelService.save($scope.fuelList, success, failed);
        };

        $scope.fuelUpdate = function () {
            LibrariesFuelService.updateRegionsRates({}, function (response) {
                $scope.fuelList = response;
            });
        };
    }
]);