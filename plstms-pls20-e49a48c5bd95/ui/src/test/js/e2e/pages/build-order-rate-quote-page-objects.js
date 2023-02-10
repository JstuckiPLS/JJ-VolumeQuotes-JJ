angular.module('PageObjectsModule').factory('BuildOrderPageObject', [ function() {
    return {
        inputProductQty : 'wizardData.shipment.finishOrder.quoteMaterials[row.rowIndex].quantity',
        inputProduct : 'wizardData.shipment.finishOrder.quoteMaterials[row.rowIndex].product',

        inputOriginAddressName : '#addressDirectivetrue [id^="addressNameInp"]',
        inputDestinationAddressName : '#addressDirectivefalse [id^="addressNameInp"]',

        inputBillTo : '#billToAddressName :input',
        selectBillToPayTerms : 'shipment.paymentTerms',
        buttonNext:'.a_nextButton',
        locationInput: '#location :input',

        setProductQty : function(value) {
            input(this.inputProductQty).enter(value);
        },

        selectProduct : function(value) {
            select(this.inputProduct).option(value);
        },

        setOriginAddressName : function(value) {
            setValue(this.inputOriginAddressName, value);
        },

        setDestinationAddressName : function(value) {
            setValue(this.inputDestinationAddressName, value);
        },

        setBillTo : function(value) {
            setValue(this.inputBillTo, value);
        },

        setBillToPayTerms : function(value) {
            select(this.selectBillToPayTerms).option(value);
        },

        clickNextButton:function(){
            element(this.buttonNext).click();
        },

        setLocation : function(value) {
            setValue(this.locationInput, value);
        }
    };
} ]);
