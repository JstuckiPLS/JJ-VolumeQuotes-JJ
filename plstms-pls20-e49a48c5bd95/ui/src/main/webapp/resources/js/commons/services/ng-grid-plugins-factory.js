/**
 * Factory for creating plugin for ng-grid.
 *
 * @author: Alexander Kirichenko
 * Date: 4/19/13
 * Time: 9:07 AM
 */
angular.module('plsApp.directives.services').factory('NgGridPluginFactory', [
    '$rootScope', '$compile', '$templateCache', '$parse', '$filter', '$timeout', 'DateTimeUtils', '$cookies', '$location', 'UserSettingsService',
    function ($rootScope, $compile, $templateCache, $parse, $filter, $timeout, DateTimeUtils, $cookies, $location, UserSettingsService) {
        return {
            plsGrid: function () {
                'use strict';

                var PlsGrid = function () {
                    var self = this;

                    self.init = function (scope, grid) {
                        self.scope = scope;
                        self.scope.grid = grid;

                        /**
                         * Refresh grid view when grid data changed and user scrolled the grid.
                         */
                        self.scope.$watch('grid.data', function () {
                            if (grid.$viewport.scrollTop()) {
                                var scrollTop = grid.$viewport.scrollTop();
                                scrollTop -= 120;
                                grid.refreshDomSizes();
                                self.scope.adjustScrollTop(scrollTop, true);
                            }
                        });

                        if (!self.scope.columnHashKeys) {
                            self.scope.columnHashKeys = function () {
                                var hash = '', idx;

                                for (idx in self.scope.renderedColumns) {
                                    hash += self.scope.renderedColumns[idx].$$hashKey;
                                }

                                return hash;
                            };
                        }

                        /**
                         * Watches for changes of data displayed in the grid.
                         * If needed, adds self reference for each data elements.
                         */
                        function addSelfReference() {
                            var addSelfRef = false;

                            _.each(self.scope.renderedColumns, function (column) {
                                if (column.field === 'self' && column.cellFilter) {
                                    addSelfRef = true;
                                }
                            });

                            if (addSelfRef) {
                                self.scope.$watch('grid.data', function (newVal) {
                                    _.each(newVal, function (item) {
                                        delete item.self;
                                        item.self = angular.copy(item);
                                    });
                                });
                            }
                        }

                        function getFilteredValue(val, filter) {
                            if (filter.indexOf(':')) {
                                return $filter(filter.split(':')[0])(val, filter.split(':')[1], filter.split(':')[2]);
                            }

                            return $filter(filter)(val);
                        }

                        function getLowerCaseValue(val) {
                            return val ? String(val).toLowerCase() : val;
                        }

                        function sortNumber(a, b) {
                            return a - b;
                        }

                        function sortPlainObject(a, b) {
                            if (_.isNumber(a) && _.isNumber(b)) {
                                return sortNumber(a, b);
                            }

                            var strA = getLowerCaseValue(a), strB = getLowerCaseValue(b);

                            return strA === strB ? 0 : (strA < strB ? -1 : 1);
                        }

                        function sortFilteredString(cellFilter) {
                            return function (a, b) {
                                var strA = getLowerCaseValue(getFilteredValue(a, cellFilter)),
                                        strB = getLowerCaseValue(getFilteredValue(b, cellFilter));

                                return strA === strB ? 0 : (strA < strB ? -1 : 1);
                            };
                        }

                        var SHIPMENT_STATUSES = {
                            OPEN: 1,
                            BOOKED: 2,
                            DISPATCHED: 3,
                            IN_TRANSIT: 4,
                            OUT_FOR_DELIVERY: 5,
                            DELIVERED: 6,
                            CANCELLED: 7,
                            UNKNOWN: 8
                        };

                        function sortShipmentStatus(a, b) {
                            var a1 = SHIPMENT_STATUSES[a] || 9;
                            var b1 = SHIPMENT_STATUSES[b] || 9;
                            return sortNumber(a1, b1);
                        }

                        var COMMODITY_CLASSES = {
                            CLASS_50: 50,
                            CLASS_55: 55,
                            CLASS_60: 60,
                            CLASS_65: 65,
                            CLASS_70: 70,
                            CLASS_77_5: 77.5,
                            CLASS_85: 85,
                            CLASS_92_5: 92.5,
                            CLASS_100: 100,
                            CLASS_110: 110,
                            CLASS_125: 125,
                            CLASS_150: 150,
                            CLASS_175: 175,
                            CLASS_200: 200,
                            CLASS_250: 250,
                            CLASS_300: 300,
                            CLASS_400: 400,
                            CLASS_500: 500,
                            MULTI: 1000
                        };

                        function getCommodityClassIntValue(val) {
                            if (val) {
                                return COMMODITY_CLASSES[String(val).toUpperCase()] || 1500;
                            }

                            return 2000;
                        }

                        function sortCommodityClass(a, b) {
                            var a1 = getCommodityClassIntValue(a);
                            var b1 = getCommodityClassIntValue(b);

                            return sortNumber(a1, b1);
                        }

                        function sortMaterialWeight(a, b) {
                            var propA = a.weight;
                            var propB = b.weight;
                            var tem;

                            if (!propA || !propB) {
                                // we want to force nulls and such to the bottom when we sort... which effectively is "greater than"
                                // same thing is with 0 value, because this filter shows them as empty value
                                if (!propB && !propA) {
                                    tem = 0;
                                }
                                else if (!propA) {
                                    tem = 1;
                                }
                                else if (!propB) {
                                    tem = -1;
                                }
                            }
                            else {
                                tem = sortNumber(propA, propB);
                            }

                            return tem;
                        }

                        function getColumnSortFn(column) {
                            var cellFilter = column.cellFilter;

                            if (cellFilter) {
                                if (cellFilter.indexOf('date') === 0) {
                                    return DateTimeUtils.compareDates;
                                } else if (['plscurrency', 'appendsuffix', 'longtime', 'minutestime', 'number', 'percentage'].
                                        indexOf(cellFilter.split(':')[0].toLowerCase()) !== -1) {
                                    return sortNumber;
                                } else if (cellFilter === 'shipmentStatus') {
                                    return sortShipmentStatus;
                                } else if (cellFilter === 'commodityClass') {
                                    return sortCommodityClass;
                                } else if (cellFilter.split(':')[0] === 'materialWeight') {
                                    return sortMaterialWeight;
                                } else {
                                    return sortFilteredString(cellFilter);
                                }
                            }

                            return sortPlainObject;
                        }

                        function addSortingAlgorythms() {
                            if (grid.config.enableSorting) {
                                _.each(self.scope.renderedColumns, function (column) {
                                    if (column.sortable) {
                                        column.sortingAlgorithm = column.sortingAlgorithm || getColumnSortFn(column);
                                    }
                                });
                            }
                        }

                        function makeColumnsNonGroupableByDefault() {
                            angular.forEach(self.scope.renderedColumns, function (column) {
                                column.groupable = column.colDef.groupable === true;
                            });
                        }

                        function afterInit() {
                            addSelfReference();
                            addSortingAlgorythms();
                            makeColumnsNonGroupableByDefault();
                        }

                        self.scope.$watch('columnHashKeys()', afterInit);

                    };
                };

                return new PlsGrid();
            },
            progressiveSearchPlugin: function () {
                'use strict';

                var NgGridProgressiveSearchPlugin = function () {
                    var self = this;

                    self.init = function (scope, grid) {
                        self.scope = scope;
                        self.grid = grid;

                        self.scope.progressiveSearch = self.grid.config.progressiveSearch;
                        self.grid.$groupPanel.after($compile($templateCache.get('ng-grid/gridProgressiveSearch.html'))(self.scope));

                        scope.$headerSearchContainer = grid.$topPanel.find(".ngHeaderContainer");

                        scope.$watch(function () {
                            return self.grid.$viewport.scrollLeft();
                        }, function (newLeft) {
                            return scope.$headerSearchContainer.scrollLeft(newLeft);
                        });

                        self.scope.columnHashKeys = function () {
                            var hash = '', idx;

                            for (idx in self.scope.renderedColumns) {
                                hash += self.scope.renderedColumns[idx].$$hashKey;
                            }

                            return hash;
                        };

                        self.scope.shouldRefreshTable = function () {
                            if (self.grid.config.useExternalSorting === true) {
                                return '_' + self.grid.config.sortInfo.fields[0] + '_' + self.grid.config.sortInfo.directions[0];
                            }

                            return '';
                        };

                        if (self.scope.$parent.topPanelHeight && angular.isFunction(self.scope.$parent.topPanelHeight)) {
                            var origTopPanelHeightFunc = self.scope.$parent.topPanelHeight;

                            self.scope.$parent.topPanelHeight = function () {
                                var origTopPanelHeight = origTopPanelHeightFunc();
                                return self.grid.config.progressiveSearch === true ? origTopPanelHeight + 30 : origTopPanelHeight;
                            };
                        }

                        if (self.scope.$parent.headerStyle && angular.isFunction(self.scope.$parent.headerStyle)) {
                            self.scope.$parent.progressiveSearchHeaderStyle = function () {
                                var headerStyle = self.scope.$parent.headerStyle();
                                delete headerStyle.height;
                                return headerStyle;
                            };
                        }

                        function filterPlsGrid(columnFilters, data, columnDefs) {
                            var flatFilters = [];

                            if (columnFilters) {
                                angular.forEach(columnFilters, function (value, key) {
                                    flatFilters.push({filteredProperty: key, filteredValue: value});
                                });
                            }

                            if (angular.isArray(data) && data.length > 0 && flatFilters.length > 0) {
                                var result = data;

                                _.each(flatFilters, function (flatFilter) {
                                    var cellFilter;
                                    var cellFilterValue;

                                    if (angular.isArray(columnDefs) && columnDefs.length > 0) {
                                        var columnDef = _.findWhere(columnDefs, {field: flatFilter.filteredProperty});

                                        if (columnDef && columnDef.cellFilter) {
                                            var indexOfColon = columnDef.cellFilter.indexOf(":");

                                            if (indexOfColon > 0) {
                                                cellFilter = $filter(columnDef.cellFilter.substring(0, indexOfColon));
                                                cellFilterValue = columnDef.cellFilter.substring(indexOfColon + 1).replace("'", '').replace("'", '');

                                                try {
                                                    cellFilterValue = self.scope.$eval(cellFilterValue) || cellFilterValue;
                                                } catch (e) {
                                                }
                                            } else {
                                                cellFilter = $filter(columnDef.cellFilter);
                                            }
                                        }
                                    }

                                    result = _.filter(result, function (item) {
                                        var propObjVal = $parse(flatFilter.filteredProperty)(item.entity);
                                        var itemValue;

                                        if (cellFilter) {
                                            if (_.isString(cellFilterValue)) {
                                                itemValue = cellFilter(propObjVal, cellFilterValue.split(':')[0], cellFilterValue.split(':')[1]);
                                            } else if (cellFilterValue) {
                                                itemValue = cellFilter(propObjVal, cellFilterValue);
                                            } else {
                                                itemValue = cellFilter(propObjVal);
                                            }
                                        } else if (propObjVal === undefined || propObjVal === null) {
                                            itemValue = '';
                                        } else {
                                            itemValue = String(propObjVal);
                                        }

                                        itemValue = itemValue || '';

                                        return itemValue.toUpperCase().indexOf(flatFilter.filteredValue.toUpperCase()) !== -1;
                                    });
                                });

                                return result;
                            }
                            return data;
                        }

                        function refreshTable() {
                            var i = 0;
                            // the # of rows we want to add to the top and bottom of the rendered grid rows (hidden constant in the ng-grid)
                            var EXCESS_ROWS = 6;

                            var filteredValues = _.pick(_.object(_.map(self.scope.columns, function(item) {
                                return [item.colDef.field, item.searchValue];
                            })),  _.identity);
                            if (!_.isEmpty(filteredValues)) {
                                self.grid.columnFilters = filteredValues;
                            }

                            self.grid.filteredRows = filterPlsGrid(self.grid.columnFilters, self.grid.rowCache, self.grid.config.columnDefs);

                            for (; i < self.grid.filteredRows.length; i += 1) {
                                self.grid.filteredRows[i].rowIndex = i;
                            }

                            self.grid.afterRefresh();
                            self.grid.rowFactory.UpdateViewableRange({topRow: 0, bottomRow: self.grid.minRowsToRender() + EXCESS_ROWS});
                            self.scope.$emit("ngGridEventData", self.grid.gridId);

                            if (self.grid.config.groups && self.grid.config.groups.length > 0) {
                                //table with grouping is not refreshed automatically when data is changed
                                self.grid.rowFactory.filteredRowsChanged();
                            }

                            if (self.grid.filteredRows.length > 0) {
                                var selectedRows = _.where(self.grid.filteredRows, {selected: true});
                                if (self.scope.selectionProvider.selectedItems.length === 1) {
                                    if (_.isEmpty(selectedRows)) {
                                        self.scope.selectionProvider.selectedItems.shift();
                                    } else {
                                        self.scope.selectionProvider.selectedIndex = selectedRows[0].rowIndex;
                                    }
                                } else {
                                    if (selectedRows.length > 0) {
                                        _.each(selectedRows, function(row) {
                                            self.scope.selectionProvider.selectedItems.push(row.entity);
                                        });
                                    }
                                }
                            } else {
                                self.scope.selectionProvider.selectedItems.length = 0;
                            }
                        }

                        function afterInit() {
                            self.grid.searchProvider.evalFilter = function () {
                                refreshTable();
                            };

                            angular.extend(self.grid, {
                                columnFilters: {},
                                afterRefresh: self.grid.config.afterRefresh || function () {
                                }
                            });

                            angular.forEach(self.scope.renderedColumns, function (column) {
                                column.sortOrig = column.sortOrig || column.sort;

                                if (column.colDef.searchable === undefined || column.colDef.searchable === null) {
                                    column.colDef.searchable = self.scope.progressiveSearch && column.isAggCol !== true ? true : false;
                                }

                                column.originOnMouseMove = column.originOnMouseMove || column.onMouseMove;

                                angular.extend(column, {
                                    searchVisible: self.scope.progressiveSearch ? column.colDef.searchable : false,
                                    inputStyle: function () {
                                        return {
                                            "width": column.getWidth() + "px",
                                            "marginLeft": column.realColumnIndex() > 0 ? "1px" : "0"
                                        };
                                    },
                                    getWidth: function () {
                                        if ($rootScope.browserDetect.browser === 'Explorer') {
                                            return column.width - (column.realColumnIndex() > 0 ? 36 : 35) + 3;
                                        } else {
                                            // -13 because we use <input class="search-query"... with rounded corners
                                            return column.width - (column.realColumnIndex() > 0 ? 36 : 35) - 13;
                                        }
                                    },
                                    sort: function (evt) {
                                        if (self.grid.config.progressiveSearch && this.colDef.searchable && !this.searchVisible) {
                                            this.searchVisible = true;
                                            return true;
                                        }

                                        return this.sortOrig(evt);
                                    },
                                    escPress: function () {
                                        this.clearVisibleSearch();
                                    },
                                    search: function () {
                                        grid.$viewport.scrollTop(0);
                                        var delay = 200; // 0.2 seconds delay after last input

                                        if (self.grid.plsSearchTimeoutPromise) {
                                            $timeout.cancel(self.grid.plsSearchTimeoutPromise);
                                        }

                                        self.grid.plsSearchTimeoutPromise = $timeout(function () {
                                            self.grid.plsSearchTimeoutPromise = undefined;

                                            if (column.searchValue) {
                                                self.grid.columnFilters[column.field || column.cellFilter] = column.searchValue;
                                            } else {
                                                delete self.grid.columnFilters[column.field || column.cellFilter];
                                            }

                                            refreshTable();
                                        }, delay);
                                    },
                                    clearSearch: function () {
                                        column.searchVisible = false;

                                        if (column.searchValue && column.searchValue !== '') {
                                            column.searchValue = '';
                                            delete self.grid.columnFilters[column.field || column.cellFilter];
                                            refreshTable();
                                        }
                                    },
                                    clearVisibleSearch: function () {
                                        if (column.searchValue && column.searchValue !== '') {
                                            column.searchValue = '';
                                            delete self.grid.columnFilters[column.field || column.cellFilter];
                                            refreshTable();
                                        }
                                    },
                                    onMouseMove: function (event) {
                                        var scroller, cell, input, realIndex;
                                        column.originOnMouseMove(event);

                                        scroller = scope.$headerSearchContainer.find(".ngHeaderScroller");
                                        realIndex = column.realColumnIndex();

                                        if (scroller && scroller.children() && scroller.children().length > realIndex) {
                                            cell = scroller.children()[realIndex];
                                        }

                                        input = $(cell).find("input");

                                        if (input && input.css("width")) {
                                            input.css("width", column.getWidth());
                                        }

                                        return false;
                                    },
                                    /**
                                     * if some columns are hidden - real index of column can be different.
                                     */
                                    realColumnIndex: function () {
                                        var minus = _.reduce(self.scope.columns, function (memo, col) {
                                            return col.index < column.index && col.visible === false ? memo + 1 : memo;
                                        }, 0);

                                        return column.index - minus;
                                    }
                                });
                            });
                        }

                        self.scope.$watch('columnHashKeys()', afterInit);
                        self.scope.$watch('shouldRefreshTable()', refreshTable);

                        self.scope.$on('event:refresh-grid', function (event, gridId) {
                            if (gridId === self.grid.gridId) {
                                refreshTable();
                            }
                        });

                        self.scope.$on('event:clear-progressive-search', function () {
                            angular.forEach(self.scope.renderedColumns, function (column) {
                                column.clearVisibleSearch();
                            });
                        });
                    };
                };

                return new NgGridProgressiveSearchPlugin();
            },
            tooltipPlugin: function (forSpecifiedColumnOnly) {
                var NgGridTooltipPlugin = function () {
                    'use strict';

                    var self = this;

                    self.init = function (scope, grid, services) {
                        self.scope = scope;
                        self.grid = grid;
                        self.services = services;
                        self.scope.showGridTooltip = false;
                        self.scope.tooltipOptions = grid.config.tooltipOptions;

                        self.scope.tooltipOptions.showIf = self.scope.tooltipOptions.showIf || function () {
                                    return true;
                                };

                        self.scope.tooltipTopPosition = '0px';
                        self.scope.tooltipLeftPosition = '0px';

                        self.gridTooltipDivEl = $compile($templateCache.get('ng-grid/gridTooltipPlaceHolder.html'))(self.scope);
                        $('#content').append(self.gridTooltipDivEl);

                        self.scope.renderedHashKeys = function () {
                            var hash = '', list;

                            if (forSpecifiedColumnOnly) {
                                list = self.scope.renderedColumns;
                            } else {
                                list = self.scope.renderedRows;
                            }

                            _.each(list, function (hashElement) {
                                hash += hashElement.$$hashKey;
                            });

                            return hash;
                        };

                        self.scope.renderedRowsHashKeys = function () {
                            var hash = '';

                            _.each(self.scope.renderedRows, function (hashElement) {
                                hash += hashElement.$$hashKey;
                            });

                            return hash;
                        };

                        self.scope.$on("$destroy", function () {
                            self.gridTooltipDivEl.remove();
                        });

                        self.gridTooltipDivEl.css('min-width', '100px');
                        self.gridTooltipDivEl.css('max-width', ($(window).width() / 2) + 'px');
                        self.preventHide = false;

                        self.gridTooltipDivEl.on('mouseover', function () {
                            clearTimeout(self.gridTooltipDivEl.data('timer'));
                            self.preventHide = true;
                        });

                        self.gridTooltipDivEl.on('mouseout', function () {
                            self.preventHide = false;
                            self.scope.hideTooltip();
                        });

                        self.scope.$watch(function () {
                            return self.gridTooltipDivEl.outerHeight(true) + '_' + self.gridTooltipDivEl.outerWidth(true) + '_'
                                    + self.scope.tooltipEventTopPosition + '_' + self.scope.tooltipEventLeftPosition;
                        }, function (newVal, oldVal) {
                            if (newVal !== oldVal && self.scope.tooltipEventTopPosition && self.scope.tooltipEventLeftPosition) {
                                var windowHeight = $(window).height();
                                var windowWidth = $(window).width();

                                if (windowHeight < self.scope.tooltipEventTopPosition + self.gridTooltipDivEl.outerHeight() + 30
                                        && self.scope.tooltipEventTopPosition - self.gridTooltipDivEl.outerHeight() >= 0) {
                                    self.scope.tooltipTopPosition = (self.scope.tooltipEventTopPosition - self.gridTooltipDivEl.outerHeight()) + 'px';
                                } else {
                                    self.scope.tooltipTopPosition = (self.scope.tooltipEventTopPosition + 30) + 'px';
                                }

                                if (windowWidth < self.scope.tooltipEventLeftPosition + self.gridTooltipDivEl.outerWidth() + 30) {
                                    self.scope.tooltipLeftPosition = (self.scope.tooltipEventRightPosition - self.gridTooltipDivEl.outerWidth())
                                            + 'px';
                                } else {
                                    self.scope.tooltipLeftPosition = self.scope.tooltipEventLeftPosition + 'px';
                                }
                            }
                        });

                        function activateTooltip() {
                            var delay = self.scope.tooltipOptions.delay || 500;

                            var hide = function () {
                                if (self.scope.tooltipOptions.onHide && angular.isFunction(self.scope.tooltipOptions.onHide)) {
                                    self.scope.tooltipOptions.onHide(scope, scope.entity);
                                }

                                self.scope.showGridTooltip = false;

                                if (!self.scope.$root.$$phase) {
                                    self.scope.$root.$digest();
                                }
                            };

                            hide();

                            self.scope.showTooltip = function (element, entity) {
                                if (self.scope.tooltipOptions.showIf(entity)) {
                                    clearTimeout(self.gridTooltipDivEl.data('timer'));

                                    self.gridTooltipDivEl.data('timer', setTimeout(function () {
                                        scope.entity = entity;

                                        if (self.scope.tooltipOptions.onShow && angular.isFunction(self.scope.tooltipOptions.onShow)) {
                                            self.scope.tooltipOptions.onShow(scope, scope.entity);
                                        }

                                        self.gridTooltipDivEl.removeData('timer');
                                        self.scope.tooltipEventTopPosition = element.offset().top - $(window).scrollTop();
                                        self.scope.tooltipEventLeftPosition = element.offset().left - $(window).scrollLeft();
                                        self.scope.tooltipEventRightPosition = self.scope.tooltipEventLeftPosition + element.width();

                                        self.scope.showGridTooltip = true;
                                        self.scope.$root.$digest();
                                    }, delay));
                                }
                            };

                            self.scope.hideTooltip = function () {
                                if (self.scope.preventHide) {
                                    return;
                                }

                                clearTimeout(self.gridTooltipDivEl.data('timer'));
                                self.gridTooltipDivEl.data('timer', setTimeout(hide, delay));
                            };

                            self.scope.hideTooltipImmediately = function () {
                                clearTimeout(self.gridTooltipDivEl.data('timer'));
                                hide();
                            };

                            if (forSpecifiedColumnOnly) {
                                var cellMatcher = new RegExp('^(<[\\w-]+)', 'g');

                                _.each(self.scope.renderedColumns, function (column) {
                                    if (column.colDef.showTooltip) {
                                        column.cellTemplate = column.cellTemplate.replace(cellMatcher, '$1 ' +
                                                'data-ng-mouseover="row.showCellTooltip($event.target, row.entity)" ' +
                                                'data-ng-mouseleave="row.hideCellTooltip()"');
                                    }
                                });

                                scope.$watch('renderedRowsHashKeys()', function () {
                                    _.each(self.scope.renderedRows, function (row) {
                                        _.extend(row, {
                                            showCellTooltip: function (element, entity) {
                                                self.scope.showTooltip($(element), entity);
                                            },

                                            hideCellTooltip: self.scope.hideTooltip
                                        });
                                    });
                                });
                            } else {
                                _.each(self.scope.renderedRows, function (object) {
                                    _.extend(object, {
                                        showTooltip: function (event) {
                                            var element = $(event.target);

                                            if (!element.is('[ng-repeat="col in renderedColumns"]')) {
                                                element = element.parents('[ng-repeat="col in renderedColumns"]');
                                            }

                                            self.scope.showTooltip(element, object.entity);
                                        },

                                        hideTooltip: self.scope.hideTooltip,
                                        hideTooltipImmediately: self.scope.hideTooltipImmediately
                                    });

                                    object.elm.on('mouseover', object.showTooltip);
                                    object.elm.on('mouseout', object.hideTooltip);
                                });
                            }
                        }

                        scope.$watch('renderedHashKeys()', activateTooltip);
                    };
                };
                return new NgGridTooltipPlugin();
            },
            actionPlugin: function () {
                'use strict';

                var NgGridActionPlugin = function () {
                    var self = this;

                    self.init = function (scope, grid) {
                        function hideTooltip() {
                            if (scope.renderedRows && scope.renderedRows.length && _.has(scope.renderedRows[0], 'hideTooltipImmediately')) {
                                scope.renderedRows[0].hideTooltipImmediately();
                            }
                        }

                        self.scope = scope;
                        self.grid = grid;

                        self.grid.rowFactory.rowConfig.beforeSelectionChangeCallback = function (row, evt) {
                            var charCode = evt.which || evt.keyCode;
                            var evtType = evt.type || evt.originalEvent.type;

                            if (charCode === 13 && evtType === 'keydown') {
                                if (angular.isFunction(self.grid.config.action)) {
                                    hideTooltip();
                                    self.grid.config.action(self.scope.selectionProvider.selectedItems[0]);

                                    if (!self.scope.$root.$$phase) {
                                        self.scope.$root.$digest();
                                    }
                                }
                                return false;
                            }

                            if (self.grid.config.beforeSelectionChange && angular.isFunction(self.grid.config.beforeSelectionChange)) {
                                return self.grid.config.beforeSelectionChange(row, evt);
                            }

                            return true;
                        };

                        if (!angular.isFunction(scope.renderedRowsHashKeys)) {
                            scope.renderedRowsHashKeys = function () {
                                var hash = '', idx = 0;

                                for (; idx < scope.renderedRows.length; idx += 1) {
                                    hash += scope.renderedRows[idx].rowIndex + '_';
                                }

                                return hash;
                            };
                        }

                        function afterInit() {
                            function processGridAction() {
                                if (angular.isFunction(self.grid.config.action)) {
                                    hideTooltip();
                                    self.grid.config.action(self.scope.selectionProvider.selectedItems[0]);

                                    if (!self.scope.$root.$$phase) {
                                        self.scope.$root.$digest();
                                    }
                                }
                            }

                            angular.forEach(scope.renderedRows, function (row) {
                                if (row && row.elm) {
                                    row.elm.off('dblclick');
                                    row.elm.on('dblclick', processGridAction);
                                }
                            });
                        }

                        grid.$viewport.on('keydown', function (evt) {
                            var evtType = evt.type || evt.originalEvent.type;
                            var charCode = evt.which || evt.keyCode;

                            if (charCode === 13 && evtType === 'keydown' && angular.isFunction(self.grid.config.action)) {
                                hideTooltip();
                                self.grid.config.action(self.scope.selectionProvider.selectedItems[0]);

                                if (!self.scope.$root.$$phase) {
                                    self.scope.$root.$digest();
                                }

                                return false;
                            }
                            return true;
                        });

                        scope.$watch('renderedRowsHashKeys()', afterInit);
                    };
                };
                return new NgGridActionPlugin();
            },
            hideColumnPlugin : function(plsTableName) {
                'use strict';

                var HideColumnPlugin = function() {
                    var self = this;
                    
                    self.init = function(scope, grid) {
                        if (!plsTableName) {
                            plsTableName = $location.url().replace(/\//g, '_');
                        }
                        self.scope = scope;
                        self.grid = grid;
                        self.scope.$parent.showColumnMenu = false;
                        self.scope.$parent.showPLSColumnMenu = true;
                        var appropriateAliasForUserSettingsStorage = "hiddenTableCols_"+plsTableName;

                        function hideColumns() {
                            var currentColumnSelection;
                            if ($rootScope.userSettings && $rootScope.userSettings[appropriateAliasForUserSettingsStorage]) {
                                currentColumnSelection = JSON.parse($rootScope.userSettings[appropriateAliasForUserSettingsStorage]);
                                _.each(self.scope.$parent.columns, function(value) {
                                    if (_.indexOf(currentColumnSelection, value.displayName) !== -1) {
                                        value.visible = false;
                                    }
                                });
                            } else {
                                _.each(self.scope.$parent.columns, function(value) {
                                    if (value.colDef.plsHideColumn === true) {
                                        value.visible = false;
                                    }
                                });
                            }
                        }

                        if($rootScope.userSettings){
                            self.scope.$watch('renderedRowsHashKeys()', hideColumns);
                        } else {
                            $rootScope.$watch('userSettings', function(){
                                if($rootScope.userSettings){
                                    hideColumns();
                                }
                            });
                        }

                        self.scope.$parent.hideColumn = function(column) {
                            var columnsToHide = [];

                            _.each(self.scope.$parent.columns, function(value) {
                                if (value.visible === false) {
                                    columnsToHide.push(value.displayName);
                                }
                            });

                            var settingsString = JSON.stringify(columnsToHide);
                            $rootScope.userSettings[appropriateAliasForUserSettingsStorage] = settingsString;
                            UserSettingsService.setUserSettings({
                                "key": appropriateAliasForUserSettingsStorage,
                                "value": settingsString
                            });
                        };
                    };
                };
                return new HideColumnPlugin();
            }
        };
    }
]);

angular.module('plsApp.directives.services').run(['$templateCache', function ($templateCache) {
    $templateCache.put('ng-grid/gridProgressiveSearch.html',
            "<div class=\"ngHeaderContainer\" ng-style=\"progressiveSearchHeaderStyle()\" ng-show=\"progressiveSearch\" style=\"height: 30px;\">" +
            "   <div class=\"ngHeaderScroller\" ng-style=\"headerScrollerStyle()\" style=\"height: 30px;\">" +
            "      <div ng-style=\"{'z-index': col.zIndex()}\" style=\"height: 30px;\" " +
            "            ng-repeat=\"col in renderedColumns\" class=\"ngHeaderCell {{col.colIndex()}}\">" +
            "         <div class=\"input-append\" ng-show=\"col.searchVisible\">" +
            "            <input class=\"search-query\" type=\"text\" ng-style=\"col.inputStyle()\"" +
            "               ng-model=\"col.searchValue\" ng-change=\"col.search()\"}\" />" +
            "            <a ng-click=\"col.clearSearch();\" href=\"\"><i class=\"icon-remove\"></i></a>" +
            "         </div>" +
            "      </div>" +
            "   </div>" +
            "</div>"
    );

    $templateCache.put('ng-grid/gridTooltipPlaceHolder.html',
            "<div class=\"well well-small a_gridTooltip\" data-ng-show=\"showGridTooltip\" " +
            "data-ng-style=\"{position: 'fixed', top: tooltipTopPosition, left: tooltipLeftPosition, 'z-index': 1051}\" " +
            "><div data-ng-include='tooltipOptions.url'></div></div>");

    $templateCache.put("menuTemplate.html",
            "<div ng-show=\"showPLSColumnMenu || showFilter\"  class=\"ngHeaderButton\" ng-click=\"toggleShowMenu()\">" +
            "    <div class=\"ngHeaderButtonArrow\"></div>" +
            "</div>" +
            "<div ng-show=\"showMenu\" class=\"ngColMenu\">" +
            "    <div ng-show=\"showFilter\">" +
            "        <input placeholder=\"{{i18n.ngSearchPlaceHolder}}\" type=\"text\" ng-model=\"filterText\"/>" +
            "    </div>" +
            "    <div ng-show=\"showPLSColumnMenu\">" +
            "        <span class=\"ngMenuText\">{{i18n.ngMenuText}}</span>" +
            "        <ul class=\"ngColList\">" +
            "            <li class=\"ngColListItem\" ng-repeat=\"col in columns | ngColumns\">" +
            "                <label><input ng-disabled=\"col.pinned\" type=\"checkbox\" class=\"ngColListCheckbox\"" +
            "                        ng-change=\"hideColumn(col)\" ng-model=\"col.visible\"/>{{col.displayName}}</label>" +
            "               <a title=\"Group By\" ng-class=\"col.groupedByClass()\" " +
            "                  ng-show=\"col.groupable && col.visible\" ng-click=\"groupBy(col)\"></a>" +
            "               <span class=\"ngGroupingNumber\" ng-show=\"col.groupIndex > 0\">{{col.groupIndex}}</span>" +
            "            </li>" +
            "        </ul>" +
            "    </div>" +
            "</div>"
          );
}]);
