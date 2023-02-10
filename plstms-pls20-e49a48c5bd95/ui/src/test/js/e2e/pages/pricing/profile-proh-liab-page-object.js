/**
 * Pricing profile prohibited and liability page object.
 * 
 * @author Ashwini Neelgund
 */
angular.module('PageObjectsModule').factory('PricProfProhLiabPageObject', [function() {
    return {
          controller : '[data-ng-controller="ProhibitedLiabilityCtrl"]',
          prohibitedCommodities : '#prohibitedCommodities',
          prohibitedCommoditiesValue : 'profileDetails.prohibitedCommodities',
          inputProhibitedCopyFrom : '#inputProhibitedCopyFrom',
          inputLiabilitiesCopyFrom : '#inputLiabilitiesCopyFrom',
          externalNotes : '#externalNotes',
          internalNotes : '#internalNotes',
          gridRows : 'div[data-ng-grid="gridOptions"] [ng-row]',
          gridFirstRow: 'div[data-ng-grid="gridOptions"] [ng-row]:first',
          resetButton : 'button[data-ng-click="reset()"]',
          saveButton : 'button[data-ng-click="submit()"]',

          getJquerySelectorForCell : function(rowNum, cellNum) {
              return '[ng-row]:eq(' + rowNum + ') > [ng-repeat]:eq(' + cellNum + ') > [ng-cell] > div > [ng-cell-text]';
           },
          getJquerySelectorForNewMin : function(rowNum) {
              return this.getJquerySelectorForCell(rowNum,1);
           },
           getJquerySelectorForNewMax : function(rowNum) {
               return this.getJquerySelectorForCell(rowNum,3);
            },
            setProhibitedCommodities : function(value) {
                input(this.prohibitedCommoditiesValue).enter(value);
            },
            getProhibitedCommodities : function() {
                return element(this.prohibitedCommodities).val();
            },
            getProhibCommdtyDisplay : function () {
                return element(this.prohibitedCommodities).attr("disabled");
            },
            getInputProhibCopyFromDisplay : function () {
                return element(this.inputProhibitedCopyFrom).attr("disabled");
            },
            getInputLiabCopyFromDisplay : function () {
                return element(this.inputLiabilitiesCopyFrom).attr("disabled");
            },
            getExternalNotesDisplay : function () {
                return element(this.externalNotes).attr("disabled");
            },
            getInternalNotesDisplay : function () {
                return element(this.internalNotes).attr("disabled");
            },
            getResetButtonDisplay : function () {
                return element(this.resetButton).attr("disabled");
            },
            getSaveButtonDisplay : function () {
                return element(this.saveButton).attr("disabled");
            },
            clickResetButton : function() {
                element(this.resetButton).click();
            },
            clickSaveButton : function() {
                element(this.saveButton).click();
            },
            getCarrLiabRowsCount : function() {
                return element(this.gridRows).count();
            },
            selectFirstCarrLiabRow: function() {
                element(this.gridFirstRow).click();
            },
    };
}]);
