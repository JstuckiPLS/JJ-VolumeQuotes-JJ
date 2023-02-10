/**
 * Service for building export data.
 *
 * It uses ngGrid for obtaining export format. Column names are column names from ngGrid, but if you need to change
 * this name, you can add exportDisplayName property to ngGrid's columnDefs array.
 *
 * If you need to export data in another format, not just as some field, you can add exportTemplate property to
 * ngGrid's columnDefs array. All field names should start with 'exportEntity.' prefix, for example:
 * exportTemplate: 'exportEntity.adjustmentId|someFilter'
 *
 * @author Mykola Teslenko
 */
angular.module('plsApp.utils').factory('ExportDataBuilder', ['$rootScope', function ($rootScope) {
    return {
        getColumnDefsForReport: function(gridOptions) {
            var visibleColumns = _.where(gridOptions.$gridScope.columns, {visible: true});
            var columnDefs = _.pluck(visibleColumns, 'colDef');

            columnDefs = _.filter(columnDefs, function (item) {
                return !item.hideForReport;
            });
            return columnDefs;
        },
        getColumnNames: function (gridOptions) {
            var columnDefs = this.getColumnDefsForReport(gridOptions);
            return _.map(columnDefs, function (item) {
                if (typeof item.exportDisplayName === 'undefined') {
                    return item.displayName;
                } else {
                    return item.exportDisplayName;
                }
            });
        },
        buildExportData: function (gridOptions, exportEntities, fileNameFormat, sheetName, footerData, headerData) {
            var columnDefs = this.getColumnDefsForReport(gridOptions);
            var columnNames = this.getColumnNames(gridOptions);

            function processValue(input) {
                if (input === true || input === 'true') {
                    return 'Yes';
                }

                if (input === false || input === 'false') {
                    return 'No';
                }

                return input;
            }

            var resultExportData = _.map(exportEntities, function (exportEntity) {
                var exportScope = $rootScope.$new();
                exportScope.exportEntity = exportEntity;

                var data = _.map(columnDefs, function (columnDef) {
                    if (!columnDef.exportTemplate) {
                        var stringToEval = "exportEntity." + columnDef.field.replace("row.entity.", "exportEntity.");

                        if (columnDef.cellFilter) {
                            stringToEval += "|" + columnDef.cellFilter;
                        }

                        return processValue(exportScope.$eval(stringToEval));
                    } else {
                        return processValue(exportScope.$eval(columnDef.exportTemplate));
                    }
                });

                return {rowData: data, marked: exportEntity.selected};
            });

            return {
                fileName: fileNameFormat,
                sheetName: sheetName,
                columnNames: columnNames,
                data: resultExportData,
                footerData: footerData || [[]],
                headerData: headerData || [[]]
            };
        },
        getColumnIndex: function (columnNames, columnName) {
            var totalColumnIndex;
            var i;

            for (i = 0; i < columnNames.length; i = i + 1) {
                if (columnNames[i] === columnName) {
                    totalColumnIndex = i;
                    break;
                }
            }

            return totalColumnIndex;
        },
        buildTotalSumFooterData: function (recordsAmount, columnNames, totalSum) {
            var totalColumnIndex = this.getColumnIndex(columnNames, 'Total');

            var footerData = [[], []];

            if (totalColumnIndex !== undefined) {
                footerData[0][totalColumnIndex] = 'Records: ' + recordsAmount;
                footerData[1][totalColumnIndex] = 'Total: ' + totalSum;
            }

            return footerData;
        },
        buildTotalsFooterData: function (columnNames, totals) {
            var footerData = [[], []];
            if (totals) {
                var columnTotals = {
                    'Revenue': 'totalRevenue',
                    'Total': 'totalRevenue',
                    'Cost': 'totalCost',
                    'Margin': 'totalMargin'
                };
                var firstColumn;
                var self = this;
                _.each(columnTotals, function(value, key){
                    var index = self.getColumnIndex(columnNames, key);
                    if (index !== undefined) {
                        footerData[0][index] = totals[value];
                        if (!firstColumn || firstColumn > index) {
                            firstColumn = index;
                        }
                    }
                });
                if (firstColumn) {
                    footerData[0][firstColumn - 1] = 'Totals:';
                }
            }

            return footerData;
        }
    };
}]);