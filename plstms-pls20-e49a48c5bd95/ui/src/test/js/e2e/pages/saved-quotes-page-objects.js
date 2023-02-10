/**
 * Saved Quotes page objects.
 * 
 * @author Aleksandr Leshchenko
 */
angular.module('PageObjectsModule').factory('SavedQuotesPageObject', [function() {
    return {
        gridFirstRow : 'div[data-ng-grid="quotesGrid"] [ng-row]:eq(0)',
        selectedCustomer : 'input[data-ng-model="selectedCustomer"]',
        inputSearchByQuoteIdSelector: 'input[ng-model="col.searchValue"]:eq(0)',
        viewButton : 'button[data-ng-click="viewQuoteDetails()"]',
        searchButton : 'button[data-ng-click="getSavedQuotes()"]',
        saveButton : 'button[data-ng-click="saveProposition(pageModel.selectedProposition)"]',
        saveQuoteButton : 'button[data-ng-click="saveQuote()"]',
        saveQuoteModalSelector: '[data-pls-modal="showSaveQuoteDialog"]',
        quoteRowSelector: 'div[ng-row]',
        viewQuoteButton : 'button[data-ng-click="viewQuoteDetails()"]',
        buildOrderButton : 'button[data-ng-click="buildOrder()"]',
        backButton : 'button[data-ng-click="back()"]',
        startQuoteWizardSelector: "[wizardData.step === 'rate_quote']",

        searchByQuoteId: function(searchValue) {
            setValue(this.inputSearchByQuoteIdSelector, searchValue);
        },
        setCustomer : function(value) {
            progressiveSearch(this.selectedCustomer).enter(value);
            progressiveSearch(this.selectedCustomer).select();
        },
        clickFirstRow : function(value) {
            element(this.gridFirstRow).click();
        },
        clickViewButton : function() {
            element(this.viewButton).click();
        },
        clickSearchButton : function() {
            element(this.searchButton).click();
        },
        clicSaveButton : function() {
            element(this.saveButton).click();
        },
        clicSaveQuoteButton : function() {
            element(this.saveQuoteButton).click();
        },
        viewSaveQuoteButton : function() {
            element(this.viewQuoteButton).click();
        },
        clicBuildOrderButton : function() {
            element(this.buildOrderButton).click();
        },
        clicBackButton : function() {
            element(this.backButton).click();
        },
        getBuildOrderButtonDisplay : function () {
            return element(this.buildOrderButton).attr("disabled");
        }
    };
}]);
