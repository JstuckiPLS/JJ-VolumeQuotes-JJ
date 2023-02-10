angular.module('plsApp').service('ClassEstimatorService', ['ClassEstimatorValues', function(ClassEstimatorValues) {

    var CUBIC_FEET_COEFF = 1.728;
    var weightUnits = ClassEstimatorValues.weightUnits;
    var dimensionUnits = ClassEstimatorValues.dimensionUnits;
    var calcTypes = ClassEstimatorValues.calcTypes;
    var commodityClasses = ClassEstimatorValues.commodityClasses;
    var specialCalcTypes = ClassEstimatorValues.specialCalcTypes;

    function calculateDensity(model) {
        model.densityPCF = model.weight * weightUnits[model.weightUnit]
            / (model.length * model.width * model.height * model.quantity
            * Math.pow(dimensionUnits[model.dimensionUnit], 3) / 1728);
        return parseFloat(model.densityPCF < 0.0001 ? 0 : model.densityPCF.toFixed(3));
    }

    function calculateCubicFeet(model){
        var cubicFeet = (model.quantity * model.length * model.height * model.width) / 1000 / CUBIC_FEET_COEFF;
        return parseFloat(cubicFeet.toFixed(5));
    }

    function calculateClass(model) {
        if (model.densityPCF === 0) {
            return 'None';
        } else {
            return _.find(_.pairs(commodityClasses), function (commodityClass) {
                return commodityClass[1].min <= model.densityPCF && commodityClass[1].max > model.densityPCF;
            })[0];
        }
    }

    function calculateClassForScaffolding(model) {
        var max = model.maxDimension;
        if (max > 192) {
            model.nmfc = model.densityPCF < 15 ? '200' : '100';
        } else if (max > 96 && max <= 192) {
            model.nmfc = model.densityPCF < 15 ? '150' : '85';
        } else if (max > 48 && max <= 96) {
            model.nmfc = model.densityPCF < 15 ? '110' : '70';
        } else {
            model.nmfc = model.densityPCF < 22.5 ? '85' : '60';
        }
        return model.nmfc;
    }
    function calculateClassForCageAndBasket(model) {
        var max = model.maxDimension;
        if (model.densityPCF < 8) {
            model.nmfc = '200';
        } else if (model.densityPCF < 15) {
            model.nmfc = '92.5';
        } else if (model.densityPCF >=15) {
            model.nmfc = '70';
        }
        return model.nmfc;
    }

    function getMaxClass(gridData) {
        var maxClassItem;
        if (gridData.length > 0) {
            maxClassItem = _.max(gridData, function (value) {
                return parseFloat(value.nmfc);
            });
        }
        return maxClassItem ? maxClassItem.nmfc : 'None';
    }

    function getTotalVolumeInCubicFeet(gridData) {
        return _.reduce(gridData, function (memo, item) {
            return memo + item.length * item.width * item.height * item.quantity * Math.pow(dimensionUnits[item.dimensionUnit], 3)
                    / 1728;
        }, 0);
    }

    function getTotalWeightInPounds(gridData) {
        return _.reduce(gridData, function (memo, item) {
            return memo + item.weight * weightUnits[item.weightUnit];
        }, 0);
    }

    function getTotalPCF(gridData) {
        var density = 0;
        if (gridData.length > 0) {
            var volume = getTotalVolumeInCubicFeet(gridData);
            var weight = getTotalWeightInPounds(gridData);
            density = weight / volume;
        }
        return parseFloat(density.toFixed(3));
    }

    function getCalcTypes(canUseScaffoldingType, canUseCageBasketType) {
        var scaffType, cageBasketType;
        var allCalcTypes = angular.copy(calcTypes);
        var workingTypes = [allCalcTypes[0]];
        var standardType = workingTypes[0];
        if (canUseScaffoldingType) {
            workingTypes.push(allCalcTypes[1]);
            scaffType = workingTypes[1];
        }
        if (canUseCageBasketType) {
            workingTypes.push(allCalcTypes[2]);
            cageBasketType = workingTypes.length > 2 ? workingTypes[2] : workingTypes[1];
        }
        return {
            list: workingTypes,
            STANDARD: standardType,
            SCAFF: scaffType,
            CAGE_BASKET: cageBasketType
        };
    }

    function getSelectedCalcType(calcTypes) {
        var selectedType = calcTypes.STANDARD;
        if (calcTypes.SCAFF && !calcTypes.CAGE_BASKET) {
            selectedType = calcTypes.SCAFF;
        } else if (!calcTypes.SCAFF && calcTypes.CAGE_BASKET) {
            selectedType = calcTypes.CAGE_BASKET;
        }
        return selectedType;
    }

    return {
        initCalculationTypes: function(canUseScaffoldingType, canUseCageBasketType) {
            return getCalcTypes(canUseScaffoldingType, canUseCageBasketType);
        },
        initSelectedType: function(calcTypes) {
            return {
                selected: getSelectedCalcType(calcTypes)
            };
        },
        calculateForStandard: function(model) {
            return {
                quantity: model.quantity,
                weight: model.weight,
                weightUnit: model.weightUnit,
                length: model.length,
                width: model.width,
                height: model.height,
                cubicFeet: calculateCubicFeet(model),
                dimensionUnit: model.dimensionUnit,
                density: calculateDensity(model),
                nmfc: calculateClass(model)
            };
        },
        calculateForScaffolding: function(model) {
            model.maxDimension = Math.max(model.length, model.width, model.height);
            return {
                quantity: model.quantity,
                weight: model.weight,
                weightUnit: model.weightUnit,
                length: model.length,
                width: model.width,
                height: model.height,
                cubicFeet: calculateCubicFeet(model),
                dimensionUnit: model.dimensionUnit,
                density: calculateDensity(model),
                nmfc: calculateClassForScaffolding(model)
            };
        },
        calculateForCageAndBasket: function(model) {
            return {
                quantity: model.quantity,
                weight: model.weight,
                weightUnit: model.weightUnit,
                length: model.length,
                width: model.width,
                height: model.height,
                cubicFeet: calculateCubicFeet(model),
                dimensionUnit: model.dimensionUnit,
                density: calculateDensity(model),
                nmfc: calculateClassForCageAndBasket(model)
            };
        },
        calculateTotals: function(gridData) {
            return {
                PCF: getTotalPCF(gridData),
                estimatedClass: getMaxClass(gridData),
                cubicFeet: parseFloat(getTotalVolumeInCubicFeet(gridData).toFixed(2)),
                weight: getTotalWeightInPounds(gridData)
            };
        },
        calculateCubicFeet: function(model) {
            return calculateCubicFeet(model);
        },
        calculateDensity: function(model) {
            return calculateDensity(model);
        },
        getWeightUnits : function() {
            return angular.copy(weightUnits);
        },
        getDimensionUnits : function() {
            return angular.copy(dimensionUnits);
        },
        getCalcTypes: function() {
            return angular.copy(calcTypes);
        },
        getCommodityClasses: function() {
            return angular.copy(commodityClasses);
        },
        getSpecialCalcTypes: function() {
            return angular.copy(specialCalcTypes);
        }
    };
}]);