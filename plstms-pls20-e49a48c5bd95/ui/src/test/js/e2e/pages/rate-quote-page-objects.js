/**
 * Rate Quote page objects.
 * 
 * @author Aleksandr Leshchenko
 */
angular.module('PageObjectsModule').factory('RateQuotePageObject', [function() {
    return {
        selectedCustomer : 'input[data-ng-model="selectedCustomer"]',
        originZipSelector: 'input[data-ng-model="plsZipSearch"]:first',
        destinationZipSelector: 'input[data-ng-model="plsZipSearch"]:eq(1)',
        originAddressNameSelector: 'input[data-ng-model="selectedAddress"]:first',
        destinationAddressNameSelector: 'input[data-ng-model="selectedAddress"]:last',
        originZipModel: 'wizardData.shipment.details.originZip',
        destinationZipModel: 'wizardData.shipment.details.deliverZip',
        weightModel: 'material.weight',
        commodityClassModel: 'material.commodityClass',
        quantityModel : 'material.quantity',
        stackableModel : 'material.stackable',
        stackableCheckbox : "#stackable",
        hazmatCheckbox : "products.hazmatOnly",
        packageTypeModel : 'material.packageType',
        getQuoteButtonSelector: '.a_getQuoteButton',
        selectedProductModel : 'selectedProduct',
        addMaterialButtonSelector: '.a_addItemButton',
        editMaterialButtonSelector: '.a_editButton',
        removeMaterialButtonSelector: '.a_removeButton',
        clearAllButtonSelector: '.a_clearAllButton',
        copyFromButtonSelector: '.a_copyFromButton',
        addProductButtonSelector: '.a_addProductButton',
        pickupResidentialModel : "wizardData.accessorials.pickupResidential",
        deliverResidentialModel : "wizardData.accessorials.deliverResidential",
        pickupResidentialCheckboxSelector : "#pickupResidential",
        deliverResidentialCheckboxSelector : "#deliverResidential",
        hazmatIconSelector:"i.icon-warning-sign",
        notExistingEmergencyInfoTooltipSelector:"#hazmat-info-not-exist",
        existingEmergencyInfoTooltipSelector:"hazmat-info-exist",
        copyLastButton: '.a_copyButton',
        addProductCancelButton: '.a_add_product_cancel',

        gridFirstRow : '[ng-row]:first',
        gridLastRow : '[ng-row]:last',
        gridRows : '[ng-row]',

        densityClassEstimatorButton: '[data-ng-model="material.commodityClass"]',
        openDensityClassEstimator: function() {
            element(this.densityClassEstimatorButton).click();
        },
        selectFirstRow : function() { element(this.gridFirstRow).click(); },
        selectLastRow : function() { element(this.gridLastRow).click(); },

        setOriginZip: function(zip) {
            progressiveSearch(this.originZipSelector).enter(zip);
            progressiveSearch(this.originZipSelector).select();
        },
        setDestinationZip: function(zip) {
            progressiveSearch(this.destinationZipSelector).enter(zip);
            progressiveSearch(this.destinationZipSelector).select();
        },
        setOriginAddressName: function(address) {
            setValue(this.originAddressNameSelector, address);
        },
        setDestinationAddressName: function(address) {
            setValue(this.destinationAddressNameSelector, address);
        },
        getOriginZip: function () {
            return element(this.originZipSelector).val();
        },

        getDestinationZip: function () {
            return element(this.destinationZipSelector).val();
        },
        getOriginAddressName: function () {
            return element(this.originAddressNameSelector).val();
        },

        getDestinationAddressName: function () {
            return element(this.destinationAddressNameSelector).val();
        },
        setWeight: function(weight) {
            input(this.weightModel).enter(weight);
        },
        setCommodityClass: function(commodityClass) {
            select(this.commodityClassModel).option(commodityClass);
        },
        setQuantity: function(qty) {
            input(this.quantityModel).enter(qty);
        },

        setStackable: function(value) {
            element(this.stackableCheckbox).attr("checked", value);
        },

        setHazmat: function(value) {
            input(this.hazmatCheckbox).check(value);
        },

        setPickupResidential : function(value) {
            input(this.pickupResidentialModel).check(value);
        },

        setDeliverResidential : function(value) {
            input(this.deliverResidentialModel).check(value);
        },

        setPackageType: function(type) {
            select(this.packageTypeModel).option(type);
        },

        clickGetQuote : function() {
            element(this.getQuoteButtonSelector).click();
        },

        clickAddProdCancelButton : function() {
            element(this.addProductCancelButton).click();
        },

        getSelectedCommodityClass : function() {
            return element('#commodityClass option:selected').val();
        },

        getSelectedPackagingType : function() {
            return element('#packageType option:selected').val();
        },

        getAddButtonDisabled: function () {
            return element(this.addMaterialButtonSelector).attr("disabled");
        },

        getEditButtonDisabled: function () {
            return element(this.editMaterialButtonSelector).attr("disabled");
        },

        getRemoveButtonDisabled: function () {
            return element(this.removeMaterialButtonSelector).attr("disabled");
        },

        getCopyFromDialogDisplay: function () {
            return element('.a_copyFromDialog').css("display");
        },

        getCopyLastButton: function () {
            return element(this.copyLastButton);
        },

        getAddProductDialogDisplay: function () {
            return element('.a_products_add_edit').css("display");
        },

        addButtonClick: function () {
            return element(this.addMaterialButtonSelector).click();
        },

        editButtonClick: function () {
            return element(this.editMaterialButtonSelector).click();
        },

        removeButtonClick: function () {
            return element(this.removeMaterialButtonSelector).click();
        },

        clearAllButtonClick: function () {
            return element(this.clearAllButtonSelector).click();
        },

        copyFromButtonClick: function () {
            return element(this.copyFromButtonSelector).click();
        },

        addProductButtonClick: function () {
            return element(this.addProductButtonSelector).click();
        },

        getMaterialsRowsCount: function() {
            return element(this.gridRows).count();
        },

        getMaterialsRows: function() {
            return element(this.gridRows);
        },

        getHazmatIconsCount: function() {
            return element(this.hazmatIconSelector).count();
        },

        getHazmatIconsDisplay: function() {
            return element(this.hazmatIconSelector).css("display");
        },

        getNotExistingEmergencyInfoTooltip: function() {
            return element(this.notExistingEmergencyInfoTooltipSelector);
        },

        getExistingEmergencyInfoTooltip: function() {
            return element(this.existingEmergencyInfoTooltipSelector);
        },
        selectProduct: function(productLabel) {
            productList('products.product').showList();
            productList('products.product').selectProduct(productLabel);
        },
        setCustomer: function(customerName){
            setValue(this.selectedCustomer, customerName);
        }
    };
}]);
