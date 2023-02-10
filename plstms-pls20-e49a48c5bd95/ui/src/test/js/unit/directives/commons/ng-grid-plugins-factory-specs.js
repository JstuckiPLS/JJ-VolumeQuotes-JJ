/**
 * UI unit tests for ng-grid plugins.
 *
 * @author: Alexander Kirichenko
 * Date: 8/9/13
 * Time: 3:47 PM
 */
describe('Test scenarios for ng-grid plugins.', function() {
    var elm, scope, pluginFactory, plsApp, filterProvider, timeoutService, compileService;

    beforeEach(module('plsApp', function($filterProvider) {
        filterProvider = $filterProvider;
    }));

    beforeEach(inject(function(NgGridPluginFactory, $rootScope, $compile, $timeout) {
        pluginFactory = NgGridPluginFactory
        scope = $rootScope.$new();
        timeoutService = $timeout;
        compileService = $compile;
    }));

    var initializeHelper = function(gridOptions) {
        if ($('body').find('#content').length) {
            $('div[data-ng-show="showGridTooltip"]').remove();
            $('#content').remove();
        }
        elm = angular.element('<div id="content"><div data-ng-grid="gridOptions" style="width: 1000px; height: 1000px"></div></div>');
        scope.myData = [{ name: "Moroni", age: 50 },
            { name: "Tiancum", age: 43 },
            { name: "Jacob", age: 27 },
            { name: "Nephi", age: 29 },
            { name: "Enos", age: 34 }];

        scope.gridOptions = $.extend(true, { data: 'myData' }, gridOptions);
        $('body').append(compileService(elm)(scope));
        scope.$digest();
    };

    afterEach(function() {
        $('div[data-ng-show="showGridTooltip"]').remove();
        $('#content').remove();
    });

    describe('Scenarios for progressive search plugin.', function() {

        var columnFiltersRef, pagingConfigRef, sortConfigRef, deferred, gridOptions;

        beforeEach(inject(function(NgGridPluginFactory) {
            deferred = undefined;
            gridOptions = {
                columnDefs: 'myGridColumnDefs',
                progressiveSearch: true,
                useExternalSorting: true,
                plugins: [NgGridPluginFactory.progressiveSearchPlugin()]
            };
            initializeHelper(gridOptions);
            scope.$on('ngGridEventData', function(event, gridId) {
                if (deferred && gridId === scope.gridOptions.gridId) {
                    deferred.resolve();
                }
            });
            scope.$apply(function() {
                scope.myGridColumnDefs = [
                    {
                        field: 'name',
                        displayName: 'Name',
                        width: '50%'
                    },
                    {
                        field: 'age',
                        displayName: 'Age',
                        width: '50%'
                    }
                ];
            });
        }));

        it('Grid should has a progressive search header', function() {
            var progressiveSearchInputs = elm.find('div.input-append:not([style*="display: none"]) > input.search-query');
            c_expect(progressiveSearchInputs).to.exist;
            c_expect(progressiveSearchInputs.length).to.be.equal(2);
        });

        it('Grid should filtered by entered text', function(done) {
            var progressiveSearchInputs = elm.find('div.input-append:not([style*="display: none"]) > input.search-query');
            c_expect(progressiveSearchInputs).to.exist;
            c_expect(progressiveSearchInputs.length).to.be.equal(2);
            deferred = jQuery.Deferred();
            input(progressiveSearchInputs.eq(0)).enter('Enos');
            deferred.always(function() {
                c_expect(scope.gridOptions.ngGrid.filteredRows.length).to.be.eql(1);
                c_expect(scope.gridOptions.ngGrid.filteredRows[0].entity).to.be.eql({ name: "Enos", age: 34 });
                done();
            });
            timeoutService.flush();
        });

        //TODO check test(sometimes it falls when second event is not processed yet)
        it('Grid should filtered by both fields', function(done) {
            var progressiveSearchInputs = elm.find('div.input-append:not([class*="ng-hide"]) > input.search-query');
            c_expect(progressiveSearchInputs).to.exist;
            c_expect(progressiveSearchInputs.length).to.be.equal(2);
            input(progressiveSearchInputs.eq(0)).enter('Enos');
            timeoutService.flush();
            input(progressiveSearchInputs.eq(1)).enter(27);
            timeoutService.flush();
            setTimeout(function() {
                c_expect(scope.gridOptions.ngGrid.filteredRows.length).to.be.eql(0);
                done();
            });
        });

        it('Should hide header by clicking on remove icon', function() {
            var progressiveSearchInputs = elm.find('div.input-append:not([class*="ng-hide"]) > input.search-query');
            c_expect(progressiveSearchInputs).to.exist;
            c_expect(progressiveSearchInputs.length).to.be.equal(2);
            var removeLinks = elm.find('div.input-append > a[ng-click="col.clearSearch();"]');
            c_expect(removeLinks).to.exist;
            c_expect(removeLinks.length).to.be.equal(2);
            removeLinks.eq(1).click();
            var progressiveSearchDivs = elm.find('div.input-append:not([class*="ng-hide"])');
            c_expect(progressiveSearchDivs).to.exist;
            c_expect(progressiveSearchDivs.length).to.be.equal(1);
        });

        it('Should hide header by clicking on remove icon and then reaper after clicking for sort', function() {
            var progressiveSearchInputs = elm.find('div.input-append:not([class*="ng-hide"]) > input.search-query');
            c_expect(progressiveSearchInputs).to.exist;
            c_expect(progressiveSearchInputs.length).to.be.equal(2);
            var removeLinks = elm.find('div.input-append:not([class*="ng-hide"]) > a[ng-click="col.clearSearch();"]');
            c_expect(removeLinks).to.exist;
            c_expect(removeLinks.length).to.be.equal(2);
            removeLinks.eq(1).click();
            var progressiveSearchDivs = elm.find('div.input-append:not([class*="ng-hide"])');
            c_expect(progressiveSearchDivs).to.exist;
            c_expect(progressiveSearchDivs.length).to.be.equal(1);
            var sortDivs = elm.find('div.ngHeaderText[ng-click="col.sort($event)"]');
            c_expect(sortDivs).to.exist;
            c_expect(sortDivs.length).to.be.equal(2);
            sortDivs.eq(1).click();
            progressiveSearchDivs = elm.find('div.input-append:not([class*="ng-hide"])');
            c_expect(progressiveSearchDivs).to.exist;
            c_expect(progressiveSearchDivs.length).to.be.equal(2);
        });

        it('Should show progressive search only for some columns', function(done) {
            scope.$apply(function() {
                scope.myGridColumnDefs = [
                    {
                        field: 'name',
                        displayName: 'Name',
                        width: '50%',
                        searchable: false
                    },
                    {
                        field: 'age',
                        displayName: 'Age',
                        width: '50%'
                    }
                ];
            });
            var progressiveSearchInputs = elm.find('div.input-append:not([class*="ng-hide"]) > input.search-query');
            c_expect(progressiveSearchInputs).to.exist;
            c_expect(progressiveSearchInputs.length).to.be.equal(1);
            input(progressiveSearchInputs.eq(0)).enter('27');
            deferred = jQuery.Deferred();
            deferred.always(function() {
                c_expect(scope.gridOptions.ngGrid.filteredRows.length).to.be.eql(1);
                c_expect(scope.gridOptions.ngGrid.filteredRows[0].entity).to.be.eql({name: "Jacob", age: 27});
                done();
            });
            timeoutService.flush();
        });

        it('Should show progressive search only for some columns and do not show missed on sort click', function(done) {
            scope.$apply(function() {
                scope.myGridColumnDefs = [
                    {
                        field: 'name',
                        displayName: 'Name',
                        width: '50%',
                        searchable: false
                    },
                    {
                        field: 'age',
                        displayName: 'Age',
                        width: '50%'
                    }
                ];
            });
            var progressiveSearchInputs = elm.find('div.input-append:not([class*="ng-hide"]) > input.search-query');
            c_expect(progressiveSearchInputs).to.exist;
            c_expect(progressiveSearchInputs.length).to.be.equal(1);
            var sortDivs = elm.find('div.ngHeaderText[ng-click="col.sort($event)"]');
            c_expect(sortDivs).to.exist;
            c_expect(sortDivs.length).to.be.equal(2);
            sortDivs.eq(0).click();
            var progressiveSearchInputs = elm.find('div.input-append:not([class*="ng-hide"]) > input.search-query');
            c_expect(progressiveSearchInputs).to.exist;
            c_expect(progressiveSearchInputs.length).to.be.equal(1);
            input(progressiveSearchInputs.eq(0)).enter('27');
            deferred = jQuery.Deferred();
            deferred.always(function() {
                c_expect(scope.gridOptions.ngGrid.filteredRows.length).to.be.eql(1);
                c_expect(scope.gridOptions.ngGrid.filteredRows[0].entity).to.be.eql({name: "Jacob", age: 27});
                done();
            });
            timeoutService.flush();
        });

        it('Should sort data on UI', function() {
            initializeHelper({
                columnDefs: 'myGridColumnDefs',
                useExternalSorting: false,
                sortInfo: {
                    fields: ['name'],
                    directions: ['asc']
                }
            });
            scope.$apply(function() {
                scope.myGridColumnDefs = [
                    {
                        field: 'name',
                        displayName: 'Name',
                        width: '50%'
                    },
                    {
                        field: 'age',
                        displayName: 'Age',
                        width: '50%'
                    }
                ];
            });
            var sortDivs = elm.find('div.ngHeaderText[ng-click="col.sort($event)"]');
            c_expect(sortDivs).to.exist;
            c_expect(sortDivs.length).to.be.equal(2);
            c_expect(elm.find('div.ngRow > div.ngCell span[ng-cell-text]').eq(0).text()).to.be.equal('Enos')
            sortDivs.eq(0).click();
            sortDivs.eq(0).click();
            c_expect(elm.find('div.ngRow > div.ngCell span[ng-cell-text]').eq(0).text()).to.be.equal('Tiancum')
        });
    });

    describe('Scenarios to test action plugin.', function() {
        var actionItem, deferred, firstRenderedRow;

        beforeEach(inject(function(NgGridPluginFactory) {
            deferred = undefined;
            actionItem = undefined;
            initializeHelper({
                columnDefs: 'myGridColumnDefs',
                action: function (entity) {
                    actionItem = entity;
                    if (deferred) {
                        deferred.resolve();
                    }
                },
                plugins: [NgGridPluginFactory.actionPlugin()]
            });
            firstRenderedRow = angular.element(elm.find('.ngGrid')).scope().renderedRows[0];
            angular.extend(firstRenderedRow, {
                hideTooltipImmediately: function() {}
            });
            spyOn(firstRenderedRow, 'hideTooltipImmediately');
            scope.$apply(function() {
                scope.myGridColumnDefs = [
                    {
                        field: 'name',
                        displayName: 'Name',
                        width: '50%'
                    },
                    {
                        field: 'age',
                        displayName: 'Age',
                        width: '50%'
                    }
                ];
            });
        }));

        it('Grid should has ability to handle enter click', function(done) {
            var tableRowDivs = elm.find('div.ngRow');
            c_expect(tableRowDivs).to.exist;
            c_expect(tableRowDivs.length).to.be.equal(5);
            deferred = jQuery.Deferred();
            tableRowDivs.eq(0).click();
            c_expect(firstRenderedRow.hideTooltipImmediately.calls.count()).to.equal(0);
            element(elm.find('div.ngViewport').eq(0)).pressEnterKey();
            c_expect(firstRenderedRow.hideTooltipImmediately.calls.count()).to.equal(2);
            deferred.always(function() {
                c_expect(actionItem).to.be.eql(scope.myData[0]);
                done();
            });
        });

        it('Grid should has ability to handle double click', function(done) {
            var tableRowDivs = elm.find('div.ngRow');
            c_expect(tableRowDivs).to.exist;
            c_expect(tableRowDivs.length).to.be.equal(5);
            deferred = jQuery.Deferred();
            tableRowDivs.eq(1).click();
            c_expect(firstRenderedRow.hideTooltipImmediately.calls.count()).to.equal(0);
            tableRowDivs.eq(1).dblclick();
            c_expect(firstRenderedRow.hideTooltipImmediately.calls.count()).to.equal(1);
            deferred.always(function() {
                c_expect(actionItem).to.be.eql(scope.myData[1]);
                done();
            });
        });
    });

    describe('Scenarios to test tooltip plugin.', function() {
        var tooltipItem, deferred;

        beforeEach(inject(function(NgGridPluginFactory) {
            deferred = undefined;
            actionItem = undefined;
            initializeHelper({
                columnDefs: 'myGridColumnDefs',
                tooltipOptions: {
                    onShow: function(scope, item) {
                        tooltipItem = item;
                        if (deferred) {
                            setTimeout(function() {
                                deferred.resolve();
                            }, 200);
                        }
                    },
                    delay: 200
                },
                plugins: [NgGridPluginFactory.tooltipPlugin()]
            });
            scope.$apply(function() {
                scope.myGridColumnDefs = [
                    {
                        field: 'name',
                        displayName: 'Name',
                        width: '50%'
                    },
                    {
                        field: 'age',
                        displayName: 'Age',
                        width: '50%'
                    }
                ];
            });
        }));

        it('Grid should contains tooltip placeholder', function() {
            var tooltipPlaceholder = $('body').find('div[data-ng-show="showGridTooltip"]');
            c_expect(tooltipPlaceholder).to.exist;
            c_expect(tooltipPlaceholder.eq(0).css('display')).to.be.equal('none');
        });

        it('Grid should display tooltip for selected cell', function(done) {
            var tableCells = elm.find('div[ng-cell]');
            c_expect(tableCells).to.exist;
            c_expect(tableCells.length).to.be.equal(10);
            var tooltipPlaceholder = $('body').find('div[data-ng-show="showGridTooltip"]');
            c_expect(tooltipPlaceholder).to.exist;
            c_expect(tooltipPlaceholder.eq(0).css('display')).to.be.equal('none');
            deferred = jQuery.Deferred();
            tableCells.eq(2).mouseover();
            deferred.always(function() {
                c_expect(tooltipPlaceholder.eq(0).css('display')).to.be.equal('block');
                c_expect(tooltipItem).to.be.eql(scope.myData[1]);
                done();
            });
        });

        it('Grid should hide tooltip if data was changed', function(done) {
            var tableCells = elm.find('div[ng-cell]');
            c_expect(tableCells).to.exist;
            c_expect(tableCells.length).to.be.equal(10);
            var tooltipPlaceholder = $('body').find('div[data-ng-show="showGridTooltip"]');
            c_expect(tooltipPlaceholder).to.exist;
            c_expect(tooltipPlaceholder.eq(0).css('display')).to.be.equal('none');
            deferred = jQuery.Deferred();
            tableCells.eq(2).mouseover();
            deferred.always(function() {
                c_expect(tooltipPlaceholder.eq(0).css('display')).to.be.equal('block');
                c_expect(tooltipItem).to.be.eql(scope.myData[1]);
                scope.$apply(function() {
                    scope.myData = [];
                });
                c_expect(tooltipPlaceholder.eq(0).css('display')).to.be.equal('none');
                done();
            });
        });
    });

    describe('Scenarios for progressive search plugin.', function() {

        var columnFiltersRef, pagingConfigRef, sortConfigRef, deferred, gridOptions;

        beforeEach(inject(function(NgGridPluginFactory) {
            initializeHelper({
                columnDefs: 'myGridColumnDefs',
                useExternalSorting: false,
                sortInfo: {
                    fields: [],
                    directions: []
                },
                plugins: [ NgGridPluginFactory.plsGrid() ]
            });
            scope.$apply(function() {
                scope.myGridColumnDefs = [
                    {
                        field: 'dateCol',
                        displayName: 'Date',
                        cellFilter: 'date:appDateFormat'
                    },
                    {
                        field: 'plsCurrencyCol',
                        displayName: 'PLS Currency',
                        cellFilter: 'plsCurrency'
                    },
                    {
                        field: 'appendSuffixCol',
                        displayName: 'Append Suffix',
                        cellFilter: 'appendSuffix:"%"'
                    },
                    {
                        field: 'longTimeCol',
                        displayName: 'Long Time',
                        cellFilter: 'longTime'
                    },
                    {
                        field: 'minutesTimeCol',
                        displayName: 'Minutes Time',
                        cellFilter: 'minutesTime'
                    },
                    {
                        field: 'numberCol',
                        displayName: 'Number',
                        cellFilter: 'number'
                    },
                    {
                        field: 'percentageCol',
                        displayName: 'Percentage',
                        cellFilter: 'percentage'
                    },
                    {
                        field: 'shipmentStatusCol',
                        displayName: 'Shipment Status',
                        cellFilter: 'shipmentStatus'
                    },
                    {
                        field: 'commodityClassCol',
                        displayName: 'Commodity Class',
                        cellFilter: 'commodityClass'
                    },
                    {
                        field: 'filteredObjectCol',
                        displayName: 'Filtered Object',
                        cellFilter: 'zip'
                    },
                    {
                        field: 'nonFilteredStringCol',
                        displayName: 'Non Filtered String'
                    },
                    {
                        field: 'nonFilteredNumberCol',
                        displayName: 'Non Filtered Number'
                    },
                    {
                        field: 'self',
                        displayName: 'Self',
                        cellFilter: 'materialWeight'
                    },
                    {
                        field: 'self',
                        displayName: 'Self',
                        cellFilter: 'contact'
                    }
                ];
            });
        }));

        function initDataWithValues(fieldName, values) {
            var newData = [];
            _.each(values, function(val) {
                var obj = {};
                obj[fieldName] = val;
                newData.push(fieldName === '' ? val : obj);
            });
            scope.myData = _.shuffle(newData);
        }

        function validateOrder(columnPosition, valuesInExpectedOrder) {
            var i = 0;
            for(; i<valuesInExpectedOrder.length; i++) {
                var selector = 'div.ngRow:eq(' + i + ') > div.ngCell:eq(' + columnPosition + ') span[ng-cell-text]';
                c_expect(elm.find(selector).eq(0).text()).to.be.equal(valuesInExpectedOrder[i]);
            }
        }

        it('Should sort by date filter', function() {
            scope.$apply(function() {
                initDataWithValues('dateCol', ['2012-11-13', '2014-11-13', '2014-10-13', '2014-11-15', '2014-11-10', '2014-12-19']);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('dateCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(0, ['11/13/12', '10/13/14', '11/10/14', '11/13/14', '11/15/14', '12/19/14']);
        });

        it('Should sort by plsCurrency filter', function() {
            scope.$apply(function() {
                initDataWithValues('plsCurrencyCol', [20, -10, 0, 3.5, 1, 100.01, 1500]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('plsCurrencyCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(1, ['-$10.00', '$0.00', '$1.00', '$3.50', '$20.00', '$100.01', '$1,500.00']);
        });

        it('Should sort by appendSuffix filter', function() {
            scope.$apply(function() {
                initDataWithValues('appendSuffixCol', [20, -10, 0, 3.5, 1, 100.01, 1500]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('appendSuffixCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(2, ['-10 %', '', '1 %', '3.5 %', '20 %', '100.01 %', '1500 %']);
        });

        it('Should sort by longTime filter', function() {
            scope.$apply(function() {
                initDataWithValues('longTimeCol', [-2, -1, 0, 1030, 1200, 1500, 2330, 2400]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('longTimeCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(3, ['None', 'Any Time', '12:00 AM', '10:30 AM', '12:00 PM', '3:00 PM', '11:30 PM', 'EOD']);
        });

        it('Should sort by minutesTime filter', function() {
            scope.$apply(function() {
                initDataWithValues('minutesTimeCol', [0, 13, 80, 300, 3067, 7560, 47575, 144000]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('minutesTimeCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(4, ['N/A', '13 minutes', '1 hour 20 minutes', '5 hours', '2 days 3 hours 7 minutes', '5 days 6 hours',
                              '33 days 55 minutes', '100 days']);
        });

        it('Should sort by number filter', function() {
            scope.$apply(function() {
                initDataWithValues('numberCol', [20, -10, 0, 3.5, 1, 100.01, 1500]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('numberCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(5, ['-10', '0', '1', '3.5', '20', '100.01', '1,500']);
        });

        it('Should sort by percentage filter', function() {
            scope.$apply(function() {
                initDataWithValues('percentageCol', [20, -10, 0, 3.5, 1, 100.01, 1500]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('percentageCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(6, ['-10%', '0%', '1%', '3.5%', '20%', '100.01%', '1,500%']);
        });

        it('Should sort by shipmentStatus filter', function() {
            scope.$apply(function() {
                initDataWithValues('shipmentStatusCol', ['OPEN', 'BOOKED', 'DISPATCHED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED',
                                                         'OUT_FOR_DELIVERY', 'UNKNOWN']);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('shipmentStatusCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(7, ['Open', 'Booked', 'Dispatched', 'In-Transit', 'Out for Delivery', 'Delivered', 'Cancelled', 'Unknown']);
        });

        it('Should sort by commodityClass filter', function() {
            scope.$apply(function() {
                initDataWithValues('commodityClassCol', ['CLASS_50', 'CLASS_55', 'CLASS_60', 'CLASS_65', 'CLASS_70', 'CLASS_77_5','CLASS_85',
                                                         'CLASS_92_5', 'CLASS_100', 'CLASS_110', 'CLASS_125', 'CLASS_150', 'CLASS_175',
                                                         'CLASS_200', 'CLASS_250', 'CLASS_300', 'CLASS_400', 'CLASS_500', 'Multi']);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('commodityClassCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(8, ['50', '55', '60', '65', '70', '77.5', '85', '92.5', '100', '110', '125', '150', '175', '200', '250', '300', '400',
                              '500', 'Multi']);
        });

        it('Should sort by any other filter as simple text', function() {
            scope.$apply(function() {
                initDataWithValues('filteredObjectCol', [{city: "CRANBERRY TWP", state: "PA", zip: "16066"},
                                                         {city: "COLUMBUS", state: "OH", zip: "43210"},
                                                         {city: "BRIMFIELD", state: "MA", zip: "01010"},
                                                         {city: "AFFTON", state: "MO", zip: "63123"},
                                                         {city: "ALLEY SPRINGS", state: "MO", zip: "65466"},
                                                         {city: "ALLEGANY", state: "NY", zip: "14706"},
                                                         {city: "ACADEMY", state: "SD", zip: "57369"}]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('filteredObjectCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(9, ['ACADEMY, SD, 57369', 'AFFTON, MO, 63123', 'ALLEGANY, NY, 14706', 'ALLEY SPRINGS, MO, 65466',
                              'BRIMFIELD, MA, 01010', 'COLUMBUS, OH, 43210', 'CRANBERRY TWP, PA, 16066']);
        });

        it('Should sort without filter as simple text', function() {
            scope.$apply(function() {
                initDataWithValues('nonFilteredStringCol', ['100', '12', 'test', 'sort', '0', 'Testa', '-', 'testz']);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('nonFilteredStringCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(10, ['-', '0', '100', '12', 'sort', 'test', 'Testa', 'testz']);
        });

        it('Should sort without filter as simple number', function() {
            scope.$apply(function() {
                initDataWithValues('nonFilteredNumberCol', [20, -10, 0, 3.5, 1, 100.01, 1500]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('nonFilteredNumberCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(11, ['-10', '0', '1', '3.5', '20', '100.01', '1500']);
        });

        it('Should sort by materialWeight filter', function() {
            scope.$apply(function() {
                initDataWithValues('', [{weight: 20},
                                        {weight: -10},
                                        {weight: 0},
                                        {weight: 3.5},
                                        {},
                                        {weight: 1},
                                        {weight: 100.01},
                                        {weight: 1500}]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('self');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('desc');
            });

            var sortDivs = elm.find('div.ngHeaderText[ng-click="col.sort($event)"]');
            sortDivs.eq(12).click();

            validateOrder(12, ['-10 Lbs', '1 Lbs', '3.5 Lbs', '20 Lbs', '100.01 Lbs', '1500 Lbs', '', '']);
        });

        it('Should sort and display valid data with self reference', function() {
            scope.$apply(function() {
                initDataWithValues('', [{contactFirstName: 'name', contactLastName: 'last'},
                                        {contactFirstName: 'test1', contactLastName: 'test2'},
                                        {contactFirstName: 'test2', contactLastName: 'test3'},
                                        {contactFirstName: 'test3', contactLastName: 'test4'},
                                        {contactFirstName: 'test4', contactLastName: 'test5'},
                                        {contactFirstName: 'test5', contactLastName: 'test6'},
                                        {contactFirstName: 'test6', contactLastName: 'test7'}]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('self');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('desc');
            });

            var sortDivs = elm.find('div.ngHeaderText[ng-click="col.sort($event)"]');
            sortDivs.eq(13).click();

            validateOrder(13, ['name last', 'test1 test2', 'test2 test3', 'test3 test4', 'test4 test5', 'test5 test6', 'test6 test7']);
        });
    });

    describe('Scenarios for NgGrid sorting cache.', function() {

        var columnFiltersRef, pagingConfigRef, sortConfigRef, deferred, gridOptions;

        beforeEach(inject(function(NgGridPluginFactory) {
            if ($('body').find('#content').length) {
                $('div[data-ng-show="showGridTooltip"]').remove();
                $('#content').remove();
            }
            elm = angular.element('<div id="content"><div data-ng-grid="gridOptions" style="width: 1000px; height: 1000px"></div>'
                    + '<div data-ng-grid="gridOptions2" style="width: 1000px; height: 1000px"></div></div>');
            scope.myData = [{ name: "Moroni", age: 50 },
                { name: "Tiancum", age: 43 },
                { name: "Jacob", age: 27 },
                { name: "Nephi", age: 29 },
                { name: "Enos", age: 34 }];

            scope.gridOptions = {
                columnDefs: [
                    {
                        field: 'testCol',
                        displayName: 'Filtered Object',
                        cellFilter: 'zip'
                    }
                ],
                data: 'myData',
                useExternalSorting: false,
                sortInfo: {
                    fields: [],
                    directions: []
                },
                plugins: [ NgGridPluginFactory.plsGrid() ]
            };
            scope.gridOptions2 = {
                    columnDefs: [
                        {
                            field: 'testCol',
                            displayName: 'Non Filtered Object'
                        }
                    ],
                    data: 'myData2',
                    useExternalSorting: false,
                    sortInfo: {
                        fields: [],
                        directions: []
                    },
                    plugins: [ NgGridPluginFactory.plsGrid() ]
                };
            $('body').append(compileService(elm)(scope));
            scope.$digest();
        }));

        function initDataWithValues(fieldName, values) {
            var newData = [];
            _.each(values, function(val) {
                var obj = {};
                obj[fieldName] = val;
                newData.push(fieldName === '' ? val : obj);
            });
            return _.shuffle(newData);
        }

        function validateOrder(rowOffset, valuesInExpectedOrder) {
            var i = 0;
            for(; i<valuesInExpectedOrder.length; i++) {
                var selector = 'div.ngRow:eq(' + (i + rowOffset) + ') > div.ngCell:eq(0) span[ng-cell-text]';
                c_expect(elm.find(selector).eq(0).text()).to.be.equal(valuesInExpectedOrder[i]);
            }
        }

        /**
         * NgGrid caches sorting functions for columns based on field name.
         * Instance of $sortService is a singleton in scope of user session.
         * So we must always clear it's cache.
         * 
         * This logic is implemented at app.js and this test validates it.
         */
        it('Should sort columns with the same name correctly', function() {
            scope.$apply(function() {
                scope.myData2 = initDataWithValues('testCol', ['100', '12', 'test', 'sort', '0', 'Testa', '-', 'testz']);
                scope.gridOptions2.ngGrid.config.sortInfo.fields.push('testCol');
                scope.gridOptions2.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(5, ['-', '0', '100', '12', 'sort', 'test', 'Testa', 'testz']);

            scope.$apply(function() {
                scope.myData = initDataWithValues('testCol', [{city: "CRANBERRY TWP", state: "PA", zip: "16066"},
                                                         {city: "COLUMBUS", state: "OH", zip: "43210"},
                                                         {city: "BRIMFIELD", state: "MA", zip: "01010"},
                                                         {city: "AFFTON", state: "MO", zip: "63123"},
                                                         {city: "ALLEY SPRINGS", state: "MO", zip: "65466"},
                                                         {city: "ALLEGANY", state: "NY", zip: "14706"},
                                                         {city: "ACADEMY", state: "SD", zip: "57369"}]);
                scope.gridOptions.ngGrid.config.sortInfo.fields.push('testCol');
                scope.gridOptions.ngGrid.config.sortInfo.directions.push('asc');
            });

            validateOrder(0, ['ACADEMY, SD, 57369', 'AFFTON, MO, 63123', 'ALLEGANY, NY, 14706', 'ALLEY SPRINGS, MO, 65466',
                              'BRIMFIELD, MA, 01010', 'COLUMBUS, OH, 43210', 'CRANBERRY TWP, PA, 16066']);
        });
    });
});