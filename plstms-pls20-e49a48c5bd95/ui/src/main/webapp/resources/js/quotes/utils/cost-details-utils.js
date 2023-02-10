// Utilities for cost details
angular.module('plsApp').factory('CostDetailsUtils', function () {
    var util = {
        isGuaranteed: function (costDetailItem) {
            return costDetailItem.refType === 'GD';
        },
        getAvailableGuaranteedSorted: function (costDetailItems, owner) {
            var allGuaranteed = _.filter(costDetailItems, function (item) {
                return item.costDetailOwner === owner && util.isGuaranteed(item);
            });

            return _.sortBy(allGuaranteed, 'guaranteedBy');
        },
        getFuelSurcharge: function (proposal, owner) {
            if (!proposal || !proposal.costDetailItems) {
                return undefined;
            }

            return (_.find(proposal.costDetailItems, function (item) {
                return item.refType === 'FS' && item.costDetailOwner === owner;
            }) || {}).subTotal;
        },
        getAccessorials: function (proposal, owner, selectedGuaranteedBy) {
            if (!proposal || !proposal.costDetailItems) {
                return undefined;
            }

            var guaranteedBy = -1;

            // find first suitable
            var mostApplicableGuaranteed = util.getMostSuitableGuaranteed(proposal.costDetailItems, owner, selectedGuaranteedBy);

            if (mostApplicableGuaranteed) {
                guaranteedBy = mostApplicableGuaranteed.guaranteedBy;
            }

            return _.filter(proposal.costDetailItems, function (item) {
                return item.costDetailOwner === owner && item.refType !== 'SRA' && item.refType !== 'CRA'
                        && (!util.isGuaranteed(item) || guaranteedBy === item.guaranteedBy);
            });
        },
        getAccessorialsExcludingFuel: function (proposal, owner, selectedGuaranteedBy) {
            if (!proposal || !proposal.costDetailItems) {
                return undefined;
            }

            var guaranteedBy = -1;

            // find first suitable
            var mostApplicableGuaranteed = util.getMostSuitableGuaranteed(proposal.costDetailItems, owner, selectedGuaranteedBy);

            if (mostApplicableGuaranteed) {
                guaranteedBy = mostApplicableGuaranteed.guaranteedBy;
            }

            return _.filter(proposal.costDetailItems, function (item) {
                return item.costDetailOwner === owner && item.refType !== 'SRA' && item.refType !== 'CRA'
                        && item.refType !== 'FS' && (!util.isGuaranteed(item) || guaranteedBy === item.guaranteedBy);
            });
        },
        getAccessorialsCost: function (proposal, owner) {
            if (!proposal || !proposal.costDetailItems) {
                return undefined;
            }

            var costDetailItems = _.filter(proposal.costDetailItems, function (item) {
                return item.costDetailOwner === owner && !_.contains(['CRA', 'SRA', 'FS', 'GD'], item.refType);
            });

            return _.reduce(costDetailItems, function (memo, costDetail) {
                return memo + costDetail.subTotal;
            }, 0);
        },
        getCostDetailItemsForTotal: function (costDetailItems, owner, selectedGuaranteedBy) {
            var guaranteedBy = -1;

            // find first suitable
            var mostApplicableGuaranteed = util.getMostSuitableGuaranteed(costDetailItems, owner, selectedGuaranteedBy);

            if (mostApplicableGuaranteed) {
                guaranteedBy = mostApplicableGuaranteed.guaranteedBy;
            }

            return _.filter(costDetailItems, function (item) {
                return item.costDetailOwner === owner && (!util.isGuaranteed(item) || guaranteedBy === item.guaranteedBy);
            });
        },
        getSelectedGuaranteed: function (proposal, selectedGuaranteedBy) {
            if (proposal) {
                return util.getMostSuitableGuaranteed(proposal.costDetailItems, 'S', selectedGuaranteedBy);
            }
        },
        getMostSuitableGuaranteed: function (costDetailItems, owner, selectedGuaranteedBy) {
            var guaranteed;

            if (costDetailItems && selectedGuaranteedBy >= 0) {
                var availableGuaranteed = util.getAvailableGuaranteedSorted(costDetailItems, owner).reverse();

                guaranteed = _.find(availableGuaranteed, function (guaranteed) {
                    return guaranteed.guaranteedBy <= selectedGuaranteedBy;
                });

                if (!guaranteed && availableGuaranteed && availableGuaranteed.length) {
                    // select item with lowest guaranteed time
                    guaranteed = availableGuaranteed[availableGuaranteed.length - 1];
                }
            }

            return guaranteed;
        },
        getSimilarCostDetailItem: function (proposal, costDetailItem, benchmark) {
            if (!costDetailItem) {
                return;
            }

            var copy = angular.copy(costDetailItem);

            if (copy.refType === 'CRA') {
                copy.refType = benchmark ? 'SBR' : 'SRA';
            } else if (copy.refType === 'SRA') {
                copy.refType = benchmark ? 'SBR' : 'CRA';
            }

            if (benchmark) {
                copy.costDetailOwner = 'B';
            } else {
                copy.costDetailOwner = (copy.costDetailOwner === 'C') ? 'S' : 'C';
            }

            var similarCostDetailItem = {subTotal: 0};

            if (proposal && proposal.costDetailItems) {
                similarCostDetailItem = _.find(proposal.costDetailItems, function (item) {
                            return item.refType === copy.refType && item.costDetailOwner === copy.costDetailOwner
                                    && (!copy.guaranteedBy || item.guaranteedBy === copy.guaranteedBy);
                        }) || {subTotal: 0};
            }

            return similarCostDetailItem;
        },
        calculateCost: function (proposal, selectedGuaranteedBy, owner) {
            if (!proposal || !proposal.costDetailItems) {
                return undefined;
            }

            var costDetailItems = util.getCostDetailItemsForTotal(proposal.costDetailItems, owner, selectedGuaranteedBy);

            return _.reduce(costDetailItems, function (memo, costDetail) {
                return memo + costDetail.subTotal;
            }, 0);
        },
        getTotalCost: function (proposal, selectedGuaranteedBy) {
            return util.calculateCost(proposal, selectedGuaranteedBy, 'S');
        },
        getBaseRate: function (proposal, owner) {
            if (!proposal || !proposal.costDetailItems) {
                return undefined;
            }

            var baseRateRefType = 'SRA';

            if (owner === 'C') {
                baseRateRefType = 'CRA';
            } else if (owner === 'B') {
                baseRateRefType = 'SBR';
            }

            return (_.find(proposal.costDetailItems, function (item) {
                return item.refType === baseRateRefType;
            }) || {}).subTotal;
        },
        getCarrierTotalCost: function (proposal, selectedGuaranteedBy) {
            return util.calculateCost(proposal, selectedGuaranteedBy, 'C');
        },
        getBenchmarkTotalCost: function (proposal, selectedGuaranteedBy) {
            return util.calculateCost(proposal, selectedGuaranteedBy, 'B');
        },
        S4: function () {
            return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
        },
        guid: function () {
            return (util.S4() + util.S4() + "-" + util.S4() + "-" + util.S4() + "-" + util.S4() + "-" + util.S4() + util.S4() + util.S4());
        },

        getAccessorialsRefType: function (proposal) {
            if (!proposal || !proposal.costDetailItems) {
                return undefined;
            }
            return _.chain(_.filter(proposal.costDetailItems, function (item) {
                return item.refType !== 'SRA' && item.refType !== 'CRA' && item.costDetailOwner !== 'B';
            })).pluck('refType').unique().value();
        },

        getGuranteedBy: function (proposal, selectedGuaranteedBy) {
            if (!proposal || !proposal.costDetailItems) {
                return undefined;
            }
            var guaranteedBy = -1;
            // find first suitable
            var mostApplicableGuaranteed = util.getMostSuitableGuaranteed(proposal.costDetailItems, 'S', selectedGuaranteedBy);
            if (mostApplicableGuaranteed) {
                guaranteedBy = mostApplicableGuaranteed.guaranteedBy;
            }
            return (_.find(proposal.costDetailItems, function (item) {
                return util.isGuaranteed(item) && guaranteedBy === item.guaranteedBy;
            }) || {}).guaranteedBy;
        },

        getNote: function (proposal, refType) {
            if (!proposal || !proposal.costDetailItems) {
                return undefined;
            }
            return (_.find(proposal.costDetailItems, function (item) {
                return refType === item.refType && item.costDetailOwner === 'S';
            }) || {}).note;
        },

        getItemCost: function (proposal, refType, owner) {
            if (!proposal || !proposal.costDetailItems) {
                return undefined;
            }
            return (_.find(proposal.costDetailItems, function (item) {
                return refType === item.refType && item.costDetailOwner === owner;
            }) || {}).subTotal;
        }
    };

    return util;
});

