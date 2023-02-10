/**
 * Pricing profile internal notes page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('ProfInternalNotesPageObject', [function() {
    return {
        controller : '[data-ng-controller="ShowProfileNotesCtrl"]',
        inputElement : '#inputElement',
        inputElementValue : 'note.note',
        saveButton : 'button[data-ng-click="submit()"]',
        gridRows : 'div[data-ng-grid="gridOptions"] [ng-row]',

        setInputElement : function(value) {
            input(this.inputElementValue).enter(value);
        },
        getInputElement : function() {
            return element(this.inputElement).val();
        },
        getInputElementDisplay : function () {
            return element(this.inputElement).attr("disabled");
        },
        getSaveButtonDisplay : function () {
            return element(this.saveButton).attr("disabled");
        },
        clickSaveButton : function() {
            element(this.saveButton).click();
        },
        getInternalNotesRowsCount : function() {
            return element(this.gridRows).count();
        },
    };
}]);
