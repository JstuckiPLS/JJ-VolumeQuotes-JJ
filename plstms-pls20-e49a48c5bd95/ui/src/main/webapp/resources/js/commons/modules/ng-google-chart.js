angular.module('ng-google-chart', []);

angular.module('ng-google-chart').directive('googleChart', ['$timeout', function ($timeout) {
    return {
        restrict: 'A',
        scope: {
            chart: '=chart'
        },
        link: function ($scope, $elm) {
            'use strict';

            var isTitleChangeNeeded = false;

            function draw() {
                if (!draw.triggered && ($scope.chart !== undefined)) {
                    draw.triggered = true;

                    $timeout(function () {
                        draw.triggered = false;
                        var dataTable = new google.visualization.DataTable($scope.chart.data, 0.5);

                        var chartWrapperArgs = {
                            chartType: $scope.chart.type,
                            dataTable: dataTable,
                            options: $scope.chart.options,
                            containerId: $elm[0]
                        };

                        if ($scope.chartWrapper === undefined) {
                            $scope.chartWrapper = new google.visualization.ChartWrapper(chartWrapperArgs);

                            google.visualization.events.addListener($scope.chartWrapper, 'ready', function () {
                                $scope.chart.displayed = true;
                                isTitleChangeNeeded = true;
                            });

                            google.visualization.events.addListener($scope.chartWrapper, 'error', function (err) {
                                console.log("Chart not displayed due to error: " + err.message);
                            });
                        } else {
                            $scope.chartWrapper.setDataTable(dataTable);
                            $scope.chartWrapper.setOptions($scope.chart.options);
                        }

                        $timeout(function () {
                            $scope.chartWrapper.draw();
                        });

                        isTitleChangeNeeded = true;
                    }, 0, true);
                }
            }

            $scope.$watch('chart', function () {
                draw();
            }, true); // true is for deep object equality checking

            //check if used browser is IE
            var msie = window.navigator.userAgent.indexOf("MSIE ");

            if (msie > 0) {
                var originalTitle = document.title.split("#")[0];

                document.attachEvent('onpropertychange', function (evt) {
                    if (isTitleChangeNeeded) {
                        if (evt.propertyName === 'title' && document.title !== originalTitle) {
                            setTimeout(function () {
                                document.title = originalTitle;
                            }, 1);
                        }

                        isTitleChangeNeeded = false;
                    }
                });
            }
        }
    };
}]);