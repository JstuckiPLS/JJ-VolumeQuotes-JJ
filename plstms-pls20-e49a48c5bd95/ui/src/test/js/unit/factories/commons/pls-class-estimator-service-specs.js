describe('Class Estimator Service', function() {

    var Service;
    var mockClasses = {
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
    };
    var mockCalcTypes = ['Standard', 'Scaffolding', 'Work Cage/Basket'];

    var mockDimensionUnits = {
        INCH: 1,
        M: 39.3701,
        CMM: 0.393701,
        FT: 12
    };
    var mockWeightUnits = {
        LBS: 1,
        KG: 2.20462,
        G: 0.00220462,
        OZ: 0.0625
    };
    var mockGridData = [{
        cubicFeet: 20.79688,
        density: 16.012,
        dimensionUnit: "INCH",
        height: 33,
        length: 33,
        nmfc: "70",
        quantity: 1,
        weight: 333,
        weightUnit: "LBS",
        width: 33
    },{
        cubicFeet: 197.18519,
        density: 2.252,
        dimensionUnit: "INCH",
        height: 44,
        length: 44,
        nmfc: "300",
        quantity: 4,
        weight: 444,
        weightUnit: "LBS",
        width: 44
    }];

    var mockGridDataForScaffolding = [{
        cubicFeet: 20.79688,
        density: 16.012,
        dimensionUnit: "INCH",
        height: 33,
        length: 33,
        nmfc: "85", // NMFC is calculated differently
        quantity: 1,
        weight: 333,
        weightUnit: "LBS",
        width: 33
    }];

    var mockTotals = {
            PCF: 3.565,
            cubicFeet: 217.98,
            estimatedClass: "300",
            weight: 777
    };

    var mockModel = {
            densityPCF: undefined,
            dimensionUnit: "INCH",
            height: 33,
            length: 33,
            quantity: 1,
            weight: 333,
            weightUnit: "LBS",
            width: 33
    };

    beforeEach(angular.mock.module('plsApp'));

    beforeEach(inject(function(ClassEstimatorService) {
        Service = ClassEstimatorService;
    }));

    afterEach(function() {
        delete mockModel.densityPCF;
    });
    it('.getCommodityClasses() should work properly', function() {
        expect(Service.getCommodityClasses).toBeDefined();
        var commodityClasses = Service.getCommodityClasses();
        expect(commodityClasses).toEqual(mockClasses);
    });
    it('.getCalcTypes() should work properly', function() {
        expect(Service.getCalcTypes).toBeDefined();
        var calcTypes = Service.getCalcTypes();
        expect(calcTypes).toEqual(mockCalcTypes);
    });
    it('.getDimensionUnits() should work properly', function() {
        expect(Service.getDimensionUnits).toBeDefined();
        var dimensionUnits = Service.getDimensionUnits();
        expect(dimensionUnits).toEqual(mockDimensionUnits);
    });
    it('.getWeightUnits() should work properly', function() {
        expect(Service.getWeightUnits).toBeDefined();
        var weightUnits = Service.getWeightUnits();
        expect(weightUnits).toEqual(mockWeightUnits);
    });
    it('.calculateForStandard() should calculate properly.', function() {
        expect(Service.calculateForStandard).toBeDefined();
        expect(mockModel.densityPCF).toBe(undefined);
        expect(mockModel.cubicFeet).not.toBeDefined();
        expect(mockModel.density).not.toBeDefined();
        expect(mockModel.nmfc).not.toBeDefined();

        var resultObj = Service.calculateForStandard(mockModel);

        expect(mockModel.densityPCF).toBeDefined();
        expect(resultObj.density).toBeDefined();
        expect(resultObj.density).toEqual(16.012);
        expect(resultObj.nmfc).toBeDefined();
        expect(resultObj.nmfc).toEqual("70");
        expect(resultObj.cubicFeet).toBeDefined();
        expect(resultObj.cubicFeet).toEqual(20.79688);
        expect(resultObj).toEqual(mockGridData[0]);
    });
    it('.calculateForScaffolding() should calculate properly.', function() {
        expect(Service.calculateForScaffolding).toBeDefined();
        expect(mockModel.densityPCF).toBe(undefined);
        expect(mockModel.cubicFeet).not.toBeDefined();
        expect(mockModel.density).not.toBeDefined();
        expect(mockModel.nmfc).not.toBeDefined();

        var resultObj = Service.calculateForScaffolding(mockModel);

        expect(mockModel.densityPCF).toBeDefined();
        expect(resultObj.density).toBeDefined();
        expect(resultObj.nmfc).toBeDefined();
        expect(resultObj.cubicFeet).toBeDefined();
        expect(resultObj).toEqual(mockGridDataForScaffolding[0]);
    });
    it('.calculateTotals() should return proper data.', function() {
        expect(Service.calculateTotals).toBeDefined();
        var totals = Service.calculateTotals(mockGridData);
        expect(totals).toBeDefined();
        expect(totals.PCF).toBeDefined();
        expect(totals.PCF).toEqual(3.565);
        expect(totals.cubicFeet).toBeDefined();
        expect(totals.cubicFeet).toEqual(217.98);
        expect(totals.estimatedClass).toBeDefined();
        expect(totals.estimatedClass).toEqual("300");
        expect(totals.estimatedClass).not.toEqual("70");
        expect(totals.weight).toBeDefined();
        expect(totals.weight).toEqual(777);
        expect(totals).toEqual(mockTotals);
    });
});