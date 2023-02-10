/**
 * Tracking Board page objects.
 * 
 * @author Aleksandr Nalapko
 */
angular.module('PageObjectsModule').factory('TrackingBoardPageObject', [ function() {
    return {
        open : {
            controller : '[data-ng-controller="TrackingBoardOpenController"]',
            inputBol : 'bol',
            inputFromDate : 'fromDate',
            inputToDate : 'toDate',
            buttonSearch : '.a_searchutton',
            buttonView : '.a_viewButton',
            gridRows : '[ng-row]',
            buttonClear : '.a_clearButton',
            buttonCancel : '.a_cancelButton',
            buttonDelete : '.a_deleteButton',
            buttonEdit : '.a_editButton', 
            modalWindowDelete : '[data-ok-function="deleteShipment()"]',
            modalWindowButtonOk : 'a_okButton ',
            alertsController : '[data-ng-controller="TrackingBoardAlertsController"]',
            gridFirstRow : '[ng-row]:first',
            detailsTab : '.a_details',
            addressesTab : '.a_addresses',
            originAddressEditButton : '.a_editAddressButton:first',
            destinationAddressEditButton : '.a_editAddressButton:last',
            pickupNotes : 'editAddressModel.address.pickupNotes',
            deliveryNotes : 'editAddressModel.address.deliveryNotes',
            editAddressOkButton : '.a_add_address_ok',
            detailPickupNotes : '[data-ng-model="shipment.finishOrder.pickupNotes"]',
            detailDeliveryNotes : '[data-ng-model="shipment.finishOrder.deliveryNotes"]', 
            buttonShipmentEdit : '.a_shipmentEditButton', 
            inputSearchByShipper: 'input[ng-model="col.searchValue"]:eq(10)',
            inputSearchByBOL: 'input[ng-model="col.searchValue"]:eq(4)',

            clickButtonShipmentEdit : function(){
                element(this.buttonShipmentEdit).click();
            },

            searchByShipper: function(value) {
                setValue(this.inputSearchByShipper, value);
            },

            searchByBOL: function(value) {
                setValue(this.inputSearchByBOL, value);
            },

            getDetailPickupNotes : function(){
              return element(this.detailPickupNotes).val(); 
            },

            getDetailDeliveryNotes : function(){
              return element(this.detailDeliveryNotes).val(); 
            },

            clickEditAddressOkButton : function(){
              element(this.editAddressOkButton).click();
            },

            inputPickupNotes : function(value){
              input(this.pickupNotes).enter(value);
            },

            inputDeliveryNotes : function(value){
              input(this.deliveryNotes).enter(value);
            },

            clickOriginAddressEditButton : function(value) {
              element(this.originAddressEditButton).click();
            },

            clickDestinationAddressEditButton : function(value) {
              element(this.destinationAddressEditButton).click();
            },

            clickFirstRow : function(value) {
              element(this.gridFirstRow).click();
            },

            clickDetailsTab : function(value) {
              element(this.detailsTab).click();
            },

            clickAddressesTab : function(value) {
              element(this.addressesTab).click();
            },

            setBol : function(value) {
                input(this.inputBol).enter(value);
            },

            setFromDate : function(value) {
                input(this.inputFromDate).enter(value);
            },

            setToDate : function(value) {
                input(this.inputToDate).enter(value);
            },

            clickSearch : function(value) {
                element(this.buttonSearch).click();
            },

            clickClear : function(value) {
                element(this.buttonClear).click();
            },

            getShipmentsRowsCount : function() {
                return element(this.gridRows).count();
            },

            clickRow : function(value) {
                element(this.gridRows).click();
            },

            clickView : function(value) {
                element(this.buttonView).click();
            },

            clickCancel : function(value) {
                element(this.buttonCancel).click();
            },

            clickEdit : function(value) {
              element(this.buttonEdit).click();
            },

            clickDelite : function(value) {
                element(this.buttonDelete).click();
            },

            clickDeliteWindowOk : function(value) {
                element(this.modalWindowButtonOk).click();
            }
        },

        unbilled : {
            controller : '[data-ng-controller="TrackingBoardUnbilledController"]',
            buttonShipmentEdit : '.a_shipmentEditButton',
            buttonEdit : 'button[data-ng-click="editSalesOrder()"]',
            gridRows : '[ng-row]',
            inputSearchByBOL: 'input[ng-model="col.searchValue"]:eq(2)',
            inputSearchByCustomer: 'input[ng-model="col.searchValue"]:eq(8)',
            overrideDateHoldButton: 'button[data-ng-click="OverrideDateHold(selectedShipments[0].shipmentId)"]',

            searchByBOL: function(value) {
                setValue(this.inputSearchByBOL, value);
            },
            searchByCustomer: function(value) {
                setValue(this.inputSearchByCustomer, value);
            },
            getRowsCount : function() {
                return element(this.gridRows).count();
            },
            clickRow : function(value) {
                element(this.gridRows).click();
            },
            clickEditButton : function(){
                element(this.buttonEdit).click();
            },
            clickButtonShipmentEdit : function(){
                element(this.buttonShipmentEdit).click();
            },
            clickOverrideDateHoldButton : function(){
                element(this.overrideDateHoldButton).click();
            }
        },

        undelivered : {
            controller : '[data-ng-controller="TrackingBoardUndeliveredController"]',
            buttonShipmentEdit : '.a_shipmentEditButton', 
            gridRows : '[ng-row]',
            gridFirstRow : '[ng-row]:first',
            inputSearchByBOL: 'input[ng-model="col.searchValue"]:eq(2)',

            clickRow : function(value) {
                element(this.gridRows).click();
            },
            clickButtonShipmentEdit : function(){
                element(this.buttonShipmentEdit).click();
            },
            searchByBOL: function(value) {
                setValue(this.inputSearchByBOL, value);
            },
            clickFirstRow : function(value) {
                element(this.gridFirstRow).click();
            },
            getShipmentsRowsCount : function() {
                return element(this.gridRows).count();
            },
        },

        booked : {
            controller : '[data-ng-controller="TrackingBoardBookedController"]',
            buttonShipmentEdit : '.a_shipmentEditButton', 
            gridRows : '[ng-row]',
            clickRow : function(value) {
                element(this.gridRows).click();
            },
            clickButtonShipmentEdit : function(){
                element(this.buttonShipmentEdit).click();
            }
        },

        alerts : {
            controller : '[data-ng-controller="TrackingBoardAlertsController"]',
            buttonShipmentEdit : '.a_shipmentEditButton', 
            gridRows : '[ng-row]',
            clickRow : function(value) {
                element(this.gridRows).click();
            },
            clickButtonShipmentEdit : function(){
                element(this.buttonShipmentEdit).click();
            }
        },

        all : {
            controller : '[data-ng-controller="TrackingBoardAllShipmentsController"]',
            buttonShipmentEdit : '.a_shipmentEditButton', 
            gridRows : '[ng-row]',
            buttonClearAllShipments: '[data-ng-click="clearSearchCriteria()"]',
            inputCarrier: 'input[data-ng-model="plsScacSearch"]',
            inputCustomer: 'input[data-ng-model="selectedCustomer"]',
            inputOrigin: 'input[data-pls-zip-search="origin"]',
            inputDest: 'input[data-pls-zip-search="destination"]',
            inputBOL: 'input[data-ng-model="bol"]',
            inputPro: 'input[data-ng-model="pro"]',
            inputLoadId: 'input[data-ng-model="loadId"]',
            inputFromDate: 'input[data-ng-model="fromDate"]',
            inputToDate: 'input[data-ng-model="toDate"]',
            dateSelector: 'searchDateSelector',
            searchButton: 'button[data-ng-click="searchAllShipmentsEntries()"]',
            viewButtton: 'button[data-ng-click="view()"]',
            grid: 'div[ng-row]',
            emailHistoryTab: '.a_email_history',

            clickEmailHistoryTab : function() {
              element(this.emailHistoryTab).click();
            },

            clickView : function() {
              element(this.viewButtton).click();
            },

            clickRow : function(value) {
                element(this.gridRows).click();
            },
            clickButtonShipmentEdit : function(){
                element(this.buttonShipmentEdit).click();
            },
            clickClearButton : function(value) {
                element(this.buttonClearAllShipments).click();
            },
            setCarrier : function(value) {
                setValue(this.inputCarrier, value);
            },
            setCustomer : function(value){
                setValue(this.inputCustomer, value);
            },
            setOrigin : function(value){
                progressiveSearch(this.inputOrigin).enter(value);
                progressiveSearch(this.inputOrigin).select();
            },
            setDest : function(value){
                progressiveSearch(this.inputDest).enter(value);
                progressiveSearch(this.inputDest).select();
            },
            setBOL : function(value){
                setValue(this.inputBOL, value);
            },
            setLoadId : function(value){
              setValue(this.inputLoadId, value);
            },
            setPro : function(value){
                setValue(this.inputPro, value);
            },
            setFromDate : function(value){
                setValue(this.inputFromDate, value);
            },
            setToDate : function(value){
                setValue(this.inputToDate, value);
            },
            selectDate : function(value){
                select(this.dateSelector).option(value);
            },
            clickButtonSearch : function(){
              element(this.searchButton).click();
            },
            getRowCount : function(){
              return element(this.grid).count();
            }
        },

        shipmentEntry : {
            cancelButton :'[data-ng-click="cancelShipment()"]',

            clickCancelButton : function(value) {
                element(this.cancelButton).click();
            }
        }
    };
} ]);
