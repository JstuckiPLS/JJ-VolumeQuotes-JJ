/**
 * Select Carrier page objects.
 * 
 * @author Aleksandr Leshchenko
 */
angular.module('PageObjectsModule').factory('SelectCarrierPageObject', [function() {
    return {
        selectCarrierControllerSelector: '[data-ng-controller="SelectCarrierCtrl"]',
        saveQuoteButtonEnabledSelector: '.a_saveButton:enabled',
        saveQuoteButtonDisabledSelector: '.a_saveButton:disabled',
        saveQuoteButton: '.a_saveButton',
        quoteIdSelector: '#refInp:visible',
        quoteIdModel: 'quoteRef',
        buttonBook:'.a_bookButton',
        buttonBack:'.a_backButton',
        buttonOkOnSaveQuote: '.a_saveQuoteButton:enabled:visible',
        buttonBuildOrderOnSaveQuote : 'a_buildOrderButton:enabled:visible',
        buildOrderButtonSaveQuote : 'button[data-ng-click="buildOrder()"]',
        carrierPropositionsRowSelector: 'div[ng-row]',
        carrierPropositionsFirstRowSelector: 'div[ng-row]:first',
        carrierPropositionsThirdRowSelector: 'div[ng-row]:eq(2)',
        carrierPropositionsBlockedRowSelector: 'div[ng-row] i:visible',
        carrierPropositionsRoadWayCarrierNameSelector: 'div[ng-row]:contains(ROADWAY) div[ng-cell]:eq(1)',
        carrierPropositionsRoadWayTransitDateSelector: 'div[ng-row]:contains(ROADWAY) div[ng-cell]:eq(4)',
        carrierPropositionsRoadWayTransitTimeSelector: 'div[ng-row]:contains(ROADWAY) div[ng-cell]:eq(5)',

        clickSaveQuote: function() {
            element(this.saveQuoteButton).click();
        },
        getSaveQuoteButtonDisplay : function () {
            return element(this.saveQuoteButton).attr("disabled");
        },
        setQuoteId: function(quoteId) {
            input(this.quoteIdModel).enter(quoteId);
        },
        selectFirstRow: function(){
            element(this.carrierPropositionsFirstRowSelector).click();
        },
        getRoadWayCarrierName: function(){
            return getText(this.carrierPropositionsRoadWayCarrierNameSelector);
        },
        getRoadWayTransitDate: function(){
            return getText(this.carrierPropositionsRoadWayTransitDateSelector);
        },
        getRoadWayTransitTime: function(){
            return getText(this.carrierPropositionsRoadWayTransitTimeSelector);
        },
        selectThirdRow: function(){
            element(this.carrierPropositionsThirdRowSelector).click();
        },
        clickBook: function(){
            element(this.buttonBook).click();
        },
        getBookButtonDisplay : function () {
            return element(this.buttonBook).attr("disabled");
        },
        clickOkButton: function(){
            element(this.buttonOkOnSaveQuote).click();
        },
        clickBuildOrderButton: function(){
            element(this.buttonBuildOrderOnSaveQuote).click();
        },
        getBuildOrderButtonDisplay : function () {
            return element(this.buildOrderButtonSaveQuote).attr("disabled");
        },
        clickBack: function() {
            element(this.buttonBack).click();
        },
        getPropositionsGridRowCount: function(){
            return element('[data-ng-grid="carrierPropositionsGrid"] [ng-row]').count();
        },
        clickBlockedRow : function() {
            element(this.carrierPropositionsBlockedRowSelector).click();
        }
    };
}]);
