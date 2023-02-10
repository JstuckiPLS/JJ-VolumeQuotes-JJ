angular.module('PageObjectsModule').factory('ShipmentDetailsPageObject', [ function() {
    return {
        shipmentDetailsDialog : '#shipmentDetailsDialogDiv',
        shipmentDetailsCloseButton: '.a_closeButton',
        bolSearchField : 'input[ng-model="col.searchValue"]:eq(2)',
        gridFirstRow : '[ng-row]:first',

        getShipmentDialog : function() {
            return element(this.shipmentDetailsDialog);
        },

        clickShipmentDetailsCloseButton : function() {
            element(this.shipmentDetailsCloseButton).click();
        },

        getGridRowCount: function(){
            return element('[ng-row]').count();
        }
    };
} ]);
