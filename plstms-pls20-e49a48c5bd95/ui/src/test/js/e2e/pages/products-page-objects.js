/**
 * Products page objects.
 *
 * @author Denis Zhupinsky
 */
angular.module('PageObjectsModule').factory('ProductsPageObject', [function () {
  var firstSelectedProduct = 'selectedProducts[0]';
    return {
        /**
         * Page object for the Add\Edit Product dialog.
         */
        addDialog: {
            dialog: '.a_products_add_edit',
            hazmatSectionDiv: '.a_add_product_hazmat_div',

            description: 'editProductModel.product.description',
            nmfc: 'editProductModel.product.nmfc',
            nmfcSubNum: 'editProductModel.product.nmfcSubNum',
            commodityClass: 'editProductModel.product.commodityClass',
            productCode: 'editProductModel.product.productCode',
            hazmat: 'editProductModel.product.hazmat',
            hazmatUnNumber: 'editProductModel.product.hazmatUnNumber',
            hazmatPackingGroup: 'editProductModel.product.hazmatPackingGroup',
            hazmatClass: 'editProductModel.product.hazmatClass',
            emergencyResponseCompany: 'editProductModel.product.hazmatEmergencyCompany',
            emergencyResponsePhoneCountryCode: 'editProductModel.product.hazmatEmergencyPhone.countryCode',
            emergencyResponsePhoneAreaCode: 'editProductModel.product.hazmatEmergencyPhone.areaCode',
            emergencyResponsePhone: 'editProductModel.product.hazmatEmergencyPhone.number',
            emergencyResponseContractNumber: 'editProductModel.product.hazmatEmergencyContract',
            emergencyResponseInstructions:'editProductModel.product.hazmatInstructions',

            okBtn: '.a_add_product_ok',
            cancelBtn: '.a_add_product_cancel',

            getDialogDisplay: function () {
                return element(this.dialog).css("display");
            },

            getOkDisplay: function () {
                return element(this.okBtn).attr("disabled");
            },

            clickOk: function () {
                element(this.okBtn).click();
            },

            clickCancel: function () {
                element(this.cancelBtn).click();
            }
        },

        /**
         * Page object Products list page.
         */
        listPage: {

            deleteDialog: '.a_confirmationDialog',
            deleteOkBtn: '.a_okButton',
            deleteCancelBtn: '.a_cancelButton',

            descriptionDetailsSection: '#details-description-div',
            commodityClassDetailsSection: '#details-commodity-class-div',

            clickDeleteOk: function () {
                element(this.deleteOkBtn).click();
            },
            clickDeleteCancel: function () {
                element(this.deleteCancelBtn).click();
            },

            getDeleteDialogDisplay: function () {
                return element(this.deleteDialog).css("display");
            },
            getDetailsDescriptionText: function () {
                return element(this.descriptionDetailsSection).text();
            },
            getDetailsCommodityClassText: function () {
                return element(this.commodityClassDetailsSection).text();
            },

            gridFirstRow: '[ng-row]:first',
            gridLastRow: '[ng-row]:last',

            selectFirstRow: function () {
                element(this.gridFirstRow).click();
            },
            selectLastRow: function () {
                element(this.gridLastRow).click();
            },

            addBtn: '[data-ng-click="addProduct()"]',
            editBtn: '[data-ng-click="editProduct()"]',
            deleteBtn: '[data-ng-click="deleteProduct()"]',
            importBtn: '[data-ng-click="importProducts()"]',

            clickAdd: function () {
                element(this.addBtn).click();
            },
            clickEdit: function () {
                element(this.editBtn).click();
            },
            clickDelete: function () {
                element(this.deleteBtn).click();
            },
            clickImport: function () {
                element(this.importBtn).click();
            },

            getAddDisplay: function () {
                return element(this.addBtn).attr("disabled");
            },
            getEditDisplay: function () {
                return element(this.editBtn).attr("disabled");
            },
            getDeleteDisplay: function () {
                return element(this.deleteBtn).attr("disabled");
            },
            getImportDisplay: function () {
                return element(this.importBtn).attr("disabled");
            },
            
            nameSearchField : 'input[ng-model="col.searchValue"]:eq(0)',
            templateDownloadLink : 'a[data-ng-href="/restful/customer/1/product/productImportTemplate"]'
        },
        
        getJquerySelectorForCell : function(rowNum, cellNum) {
          return '[ng-row]:eq(' + rowNum + ') > [ng-repeat]:eq(' + cellNum + ') > [ng-cell] > div > [ng-cell-text]';
        },
        
        getHazmatCheckBoxSelectorForRow : function(rowNum){
          return "[ng-row]:eq(" + rowNum + ") > [ng-repeat]:eq(4) > [ng-cell] > div > i";
        },
        
        getJquerySelectorForProductDescription : function(rowNum) {
          return this.getJquerySelectorForCell(rowNum,0);
        },
        
        getJquerySelectorForNMFC : function(rowNum) {
          return this.getJquerySelectorForCell(rowNum,1);
        },
        
        getJquerySelectorForClass : function(rowNum) {
          return this.getJquerySelectorForCell(rowNum,2);
        },
        
        getJquerySelectorForProductCode : function(rowNum) {
          return this.getJquerySelectorForCell(rowNum,3);
        },
        
        getGridRowCount: function(){
          return element('[ng-row]').count();
        },
        
        
        descriptionBinding : firstSelectedProduct + '.description',
        nmfcBinding : firstSelectedProduct + ' | nmfc',
        commodityBinding : firstSelectedProduct + '.commodityClass | commodityClass',
        productCodeBinding : firstSelectedProduct + '.productCode',
        hazmatUnNumberBinding : firstSelectedProduct + '.hazmatUnNumber',
        hazmatPackingGroupBinding : firstSelectedProduct + '.hazmatPackingGroup',
        hazmatClassBinding : firstSelectedProduct + '.hazmatClass',
        hazmatEmergencyBinding : firstSelectedProduct + '.hazmatEmergencyCompany',
        hazmatEmergencyPhoneBinding : firstSelectedProduct + '.hazmatEmergencyPhone | phone',
        hazmatEmergencyContractBinding : firstSelectedProduct + '.hazmatEmergencyContract',
        hazmatInstructionsBinding : firstSelectedProduct + '.hazmatInstructions',
        hazmatCheckBoxId : '#hazmat',
        hazmatInstructions : 'editProductModel.product.hazmatInstructions',
        
        selectedCustomer : "selectedCustomer"

    };
}]);
