/**
 * Address book page objects.
 * 
 * @author Eugene Borshch
 */
angular.module('PageObjectsModule').factory('AddressBookPageObject', [function() {
  var firstAddressBook = 'pageModel.selectedAddressBook[0]';
  return {
        /**
         * Page object for the Add\Edit Address dialog.
         */
        addDialog : {
                dialog : '.a_address_book_add_edit',
            
                addressName : 'editAddressModel.address.addressName',
                contactName : 'editAddressModel.address.contactName',
                address1 : 'editAddressModel.address.address1',
                address2 : 'editAddressModel.address.address2',
                cityStZip : 'plsZipSearch',
                phoneCountry : 'editAddressModel.address.phone.countryCode',
                phoneArea : 'editAddressModel.address.phone.areaCode',
                phoneNumber : 'editAddressModel.address.phone.number',
                email : 'editAddressModel.address.email',
                locationCode: 'editAddressModel.address.addressCode',
                
                okBtn : '.a_add_address_ok',
                cancleBtn : '.a_add_address_cancel',
                
                errorNameDuplicated : 'label.text-error:visible',
                
                getDialogDisplay : function (){ return element(this.dialog).css("display"); },
                
                getOkDisplay : function (){ return element(this.okBtn).attr("disabled"); },
                
                clickOk : function (){ element(this.okBtn).click(); },
                
                clickCancel : function (){ element(this.cancleBtn).click(); }            
            },
            
            /**
             * Page object Address list page.
             */
            listPage : {

                deleteDialog : '.a_confirmationDialog',
                deleteOkBtn : '.a_okButton',
                deleteCancelBtn : '.a_cancelButton',

                clickDeleteOk: function (){ element(this.deleteOkBtn).click(); },
                clickDeleteCancel: function (){ element(this.deleteCancelBtn).click(); },

                getDeleteDialogDisplay : function (){ return element(this.deleteDialog).css("display"); },
                
                gridFirstRow : '[ng-row]:first',
                gridLastRow : '[ng-row]:last',
                
                selectFirstRow : function() { element(this.gridFirstRow).click(); },
                selectLastRow : function() { element(this.gridLastRow).click(); },
                
                addBtn : '[data-ng-click="addAddress()"]',
                editBtn : '[data-ng-click="editAddress()"]',
                deleteBtn : '[data-ng-click="deleteAddress()"]',
                importBtn : '[data-ng-click="importAddresses()"]',
                
                clickAdd : function (){ element(this.addBtn).click(); },
                clickEdit : function (){ element(this.editBtn).click(); },
                clickDelete : function (){ element(this.deleteBtn).click(); },
                clickImport : function (){ element(this.importBtn).click(); },
                
                getAddDisplay : function (){ return element(this.addBtn).attr("disabled"); },
                getEditDisplay : function (){ return element(this.editBtn).attr("disabled"); },
                getDeleteDisplay : function (){ return element(this.deleteBtn).attr("disabled"); },
                getImportDisplay : function (){ return element(this.importBtn).attr("disabled"); },
                
                nameSearchField : 'input[ng-model="col.searchValue"]:eq(0)',
                templateDownloadLink : 'a[data-ng-href="/restful/customer/1/address/addressImportTemplate"]'
            },
            
            
            getJquerySelectorForCell : function(rowNum, cellNum) {
              return '[ng-row]:eq(' + rowNum + ') > [ng-repeat]:eq(' + cellNum + ') > [ng-cell] > div > [ng-cell-text]';
            },
            
            getJquerySelectorForAddressName : function(rowNum) {
              return this.getJquerySelectorForCell(rowNum,0);
            },
            
            getJquerySelectorForAddress1 : function(rowNum) {
              return this.getJquerySelectorForCell(rowNum,2);
            },
            
            getJquerySelectorForCity : function(rowNum) {
              return this.getJquerySelectorForCell(rowNum,3);
            },
            
            getJquerySelectorForState : function(rowNum) {
              return this.getJquerySelectorForCell(rowNum,4);
            },
            
            getJquerySelectorForContactName : function(rowNum) {
              return this.getJquerySelectorForCell(rowNum,6);
            },
            
            getGridRowCount: function(){
              return element('[ng-row]').count();
            },
            
            getLinkForEmail : function(email){
              return 'a[href="mailto:' + email + '"]';
            },

            setCustomer : function(customerName){
                setValue(this.selectedCustomer, customerName);
            },
            
            addressNameBinding : firstAddressBook + '.addressName',
            countryNameBinding : firstAddressBook + '.zip.country.name',
            address1Binding : firstAddressBook + '.address1',
            contactNameBinding : firstAddressBook + '.contactName',
            phoneNumberBinding : firstAddressBook + '.phone | phone',
            faxNumberBinding : firstAddressBook + '.fax | phone',
            
            selectedCustomer : 'input[data-ng-model="selectedCustomer"]'
    };
}]);
