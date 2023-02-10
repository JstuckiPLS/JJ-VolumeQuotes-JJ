/**
 Pls Class Estimator Page Object
*/
angular.module('PageObjectsModule').factory('PlsClassEstimatorPageObject', function() {
    return {
        calcTypeLabel: 'label[for="calculationType"]',
        calcTypeSelector: 'select[data-ng-model="estimCalcType.selected"]',
        selectedCalcType: 'select[data-ng-model="estimCalcType.selected"] option:selected',
        qtyLabel: 'label[for^="classEstimatorQuantity"]',
        qtyInput: 'input[data-ng-model="model.quantity"]',
        totalWeightLabel: 'label[for^="classEstimWeight"]',
        totalWeightInput: 'input[data-ng-model="model.weight"]',
        dimensionsLabel: 'label[for="length"]',
        lengthInput: 'input[data-ng-model="model.length"]',
        widthInput: 'input[data-ng-model="model.width"]',
        heightInput: 'input[data-ng-model="model.height"]',
        weightUnitSelect: 'select[data-ng-model="model.weightUnit"]',
        dimensionUnitSelect: 'select[data-ng-model="model.dimensionUnit"]',
        addButton: 'button[data-ng-click="addRow()"]',
        classEstimatorGrid: '[data-ng-grid="gridOptions"]',
        deleteRowButton: 'button[data-ng-click="deleteSelectedRows()"]',
        clearGridButton: 'button[data-ng-click="clearGrid()"]',
        closeButton: 'button[data-ng-click="close()"]',
        okButton: 'button[data-ng-click="ok()"]',

        gridFirstRow : '[ng-click="row.toggleSelected($event)"]:eq(1)',
        gridLastRow : '[ng-row]:last',
        gridRows : 'div[data-pls-modal="shouldBeOpen"] [ng-row]',

        setTotalWeight: function(weight) {
            setValue(this.totalWeightInput, weight);
        },
        setLength: function(length) {
            setValue(this.lengthInput, length);
        },
        setWidth: function(width) {
            setValue(this.widthInput, width);
        },
        setHeight: function(height) {
            setValue(this.heightInput, height);
        },
        selectFirstRow: function() {
            element(this.gridFirstRow).click();
        },
        clickOkButton: function() {
            element(this.okButton).click();
        },
        getSelectedCalculationType: function() {
            return element(this.selectedCalcType).val();
        },
        clearGrid: function() {
            element(this.clearGridButton).click();
        },
        getGridRowCount: function() {
            return element(this.gridRows).count();
        },
    };
});