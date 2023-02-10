angular.module('plsApp').value('ClassEstimatorValues', {
    commodityClasses: {
        50: {min: 50, max: Number.MAX_VALUE},
        55: {min: 35, max: 50},
        60: {min: 30, max: 35},
        65: {min: 22.5, max: 30},
        70: {min: 15, max: 22.5},
        '77.5': {min: 13.5, max: 15},
        85: {min: 12, max: 13.5},
        '92.5': {min: 10.5, max: 12},
        100: {min: 9, max: 10.5},
        110: {min: 8, max: 9},
        125: {min: 7, max: 8},
        150: {min: 6, max: 7},
        175: {min: 5, max: 6},
        200: {min: 4, max: 5},
        250: {min: 3, max: 4},
        300: {min: 2, max: 3},
        400: {min: 1, max: 2},
        500: {min: Number.MIN_VALUE, max: 1}
    },
    calcTypes: ['Standard', 'Scaffolding', 'Work Cage/Basket'],
    specialCalcTypes: ['Scaffolding', 'Work Cage/Basket'],
    dimensionUnits: {
        //Inches
        INCH: 1,
        //Meters
        M: 39.3701,
        //Centimeters
        CMM: 0.393701,
        //Feet
        FT: 12
    },
    weightUnits: {
        //Pounds.
        LBS: 1,
        //Kilograms.
        KG: 2.20462,
        //Grams.
        G: 0.00220462,
        //Ounce.
        OZ: 0.0625
    }
});