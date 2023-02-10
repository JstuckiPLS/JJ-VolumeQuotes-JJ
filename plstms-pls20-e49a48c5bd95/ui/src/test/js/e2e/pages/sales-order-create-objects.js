angular.module('PageObjectsModule').factory('EditDataSalesOrderCreateObject', [ function() {
    return {
        statusSelector : 'wizardData.shipment.status',
        customer : 'input[data-ng-model="selectedCustomer"]',
        carrier: 'input[data-ng-model="plsScacSearch"]',
        origin : 'input[data-ng-model="plsZipSearch"]:visible:first',
        destination : 'input[data-ng-model="plsZipSearch"]:visible:last',
        actualPickupDate : '#actualPickupDate',
        actualDeliveryDate : '#actualDeliveryDate',
        pickupDate : '#pickupDate',
        estimatedDelivery : '#estimatedDelivery',
        pro : 'wizardData.shipment.proNumber',
        bol : '#BOL',
        po : '#PO',
        pu : '#PU',
        soNumber : '#SO',
        trailerNumber : '#TR',
        glNumber : '#GL',
        cargoValue : '#CARGO',
        shipperRef : '#SR',
        requestedBy : '#RB',
        shipmentPickupDate : 'wizardData.shipment.finishOrder.pickupDate',
        estimateDeliveryDate : 'wizardData.shipment.finishOrder.estimatedDelivery',
        weight : 'material.weight',
        productClass: 'material.commodityClass',
        addCostButton: 'button[data-ng-click="addCostDetails()"]',
        costDescription: 'editedCostDetail.refType',
        revenue: 'editedCostDetail.revenue',
        cost: 'editedCostDetail.cost',
        saveCostButton: 'button[data-ng-click="saveCostDetails()"]',
        buttonNext : 'button[data-ng-click="nextStep()"]',
        originData : 'button[data-ng-click="addAddressEntry(origin)"]:first',
        destinationData : 'button[data-ng-click="addAddressEntry(origin)"]:last',
        addressName : 'editAddressModel.address.addressName',
        contactName : 'editAddressModel.address.contactName',
        address1 : 'editAddressModel.address.address1',
        phoneAreaCode : 'editAddressModel.address.phone.areaCode',
        phoneNumber : 'editAddressModel.address.phone.number',
        editAddressButton : 'button[data-ng-click="saveEditAddress()"]',
        billToInput: 'selectedAddress',
        doneButton : 'button[data-ng-click="done()"]',
        deliveryFromWindow : 'input[data-pls-pickup-window="shipment.finishOrder.pickupWindowFrom"]',
        deliveryToWindow : 'input[data-pls-pickup-window="shipment.finishOrder.pickupWindowTo"]',
        locationInput: '#location :input',
        actPickupDate : 'wizardData.shipment.finishOrder.actualPickupDate',
        actDeliveryDate : 'wizardData.shipment.finishOrder.actualDeliveryDate',
        selectOriginAddressNameButton: '.a_addressNameInp button :first',
        selectDestinationAddressNameButton: '.a_addressNameInp button :last',
        selectOriginAddressCodeButton: '.a_addressCodeInp button :first',
        selectDestinationAddressCodeButton: '.a_addressCodeInp button :last',
        addVendorBill: 'button[data-ng-click="vendorBillModel.addVendorBill()"]',
        saveVendorBill: 'button[data-ng-click="saveVendorBill()"]',
        saveEditSalesOrder: 'button[data-ng-click="updateOrder()"]',
        switchEditMode: 'button[data-ng-click="switchEditMode()"]',
        closeEditSalesOrder: 'button[data-ng-click="closeEditOrder(editSalesOrderModel.formDisabled)"]',

        setStatus : function(value) {
            select(this.statusSelector).option(value);
        },
        setCustomer : function(value) {
            progressiveSearch(this.customer).enter(value);
            progressiveSearch(this.customer).select();
        },
        setCarrier : function(value) {
            setValue(this.carrier, value);
        },
        setOrigin : function(value) {
            progressiveSearch(this.origin).enter(value);
            progressiveSearch(this.origin).select();
        },
        setDestination : function(value) {
            progressiveSearch(this.destination).enter(value);
            progressiveSearch(this.destination).select();
        },
        setBol : function(value) {
            setValue(this.bol, value);
        },
        setPro : function(value) {
            input(this.pro).enter(value);
        },
        setPo : function(value) {
            setValue(this.po, value);
        },
        setPu : function(value) {
            setValue(this.pu, value);
        },
        setRequestedBy : function(value) {
            setValue(this.requestedBy, value);
        },
        setShipperRef : function(value) {
            setValue(this.shipperRef, value);
        },
        setPickupDate : function(value) {
            input(this.shipmentPickupDate).enter(value);
        },
        setEstimateDeliveryDate : function(value) {
            input(this.estimateDeliveryDate).enter(value);
        },
        selectProduct: function(productLabel) {
            productList('products.product').showList();
            productList('products.product').selectProduct(productLabel);
        },
        setProductClass: function(value) {
            select(this.productClass).option(value);
        },
        setWeight : function(value) {
            input(this.weight).enter(value);
        },
        addCost: function(refType, revenue, cost) {
            element(this.addCostButton).click();
            select(this.costDescription).option(refType);
            input(this.revenue).enter(revenue);
            input(this.cost).enter(cost);
            element(this.saveCostButton).click();
        },
        clickNextButton : function() {
            element(this.buttonNext).click();
        },
        showOriginAddressDialog : function() {
            element(this.originData).click();
        },
        showDestinationAddressDialog : function() {
            element(this.destinationData).click();
        },
        setAddressName : function(value) {
            input(this.addressName).enter(value);
        } ,
        setContactName : function(value) {
            input(this.contactName).enter(value);
        },
        setAddress1 : function(value) {
            input(this.address1).enter(value);
        },
        setPhoneAreaCode : function(value) {
            input(this.phoneAreaCode).enter(value);
        },
        setPhoneNumber : function(value) {
            input(this.phoneNumber).enter(value);
        },
        saveEditAddress : function() {
            element(this.editAddressButton).click();
        },
        setBillTo: function(value) {
            input(this.billToInput).enter(value);
        },
        setSoNumber : function(value) {
            setValue(this.soNumber, value);
        },
        setGlNumber : function(value) {
            setValue(this.glNumber, value);
        },
        setCargoValue : function(value) {
            setValue(this.cargoValue, value);
        },
        setTrailerNumber : function(value) {
            setValue(this.trailerNumber, value);
        },
        clickdDoneButton : function() {
            element(this.doneButton).click();
        },
        cleanValueField : function() {
            input(this.estimateDeliveryDate).enter("");
            input(this.shipmentPickupDate).enter("");
        },
        setDeliveryFromWindow : function(fromTime) {
            setValue(this.deliveryFromWindow, fromTime);
        },
        setDeliveryToWindow : function(toTime) {
            setValue(this.deliveryToWindow, toTime);
        },
        setLocation : function(value) {
          setValue(this.locationInput, value);
        },
        setActualDeliveryDate : function(value) {
            input(this.actDeliveryDate).enter(value);
        },
        setActualPickupDate : function(value) {
            input(this.actPickupDate).enter(value);
        },
        selectOriginName : function() {
            element(this.selectOriginAddressNameButton).click();
            element('div[pls-typeahead-popup] a:first').click();
        },
        selectDestinationName : function() {
            element(this.selectDestinationAddressNameButton).click();
            element('div[pls-typeahead-popup] a:first').click();
        },
        selectLastDestinationCode : function() {
            element(this.selectDestinationAddressCodeButton).click();
            element('div[pls-typeahead-popup] a:last').click();
        },
        clickAddVendorBillButton : function() {
            element(this.addVendorBill).click();
        },
        clickSaveVendorBillButton : function() {
            element(this.saveVendorBill).click();
        },
        clickSaveEditSalesOrderButton : function() {
            element(this.saveEditSalesOrder).click();
        },
        clickSwitchEditModeButton : function() {
            element(this.switchEditMode).click();
        },
        clickCloseEditSalesOrderButton : function() {
            element(this.closeEditSalesOrder).click();
        },
        createSalesOrder : function(billTo, bol) {
            this.setStatus('Delivered');
            this.setCustomer('PLS');
            this.setCarrier('AVRT:AVERITT EXPRESS');
            this.setOrigin(83210);
            this.setDestination(16820);

            this.setPro(bol);
            this.setActualDeliveryDate('08/10/2014');
            this.setActualPickupDate('08/10/2014');
            this.setPickupDate('08/10/2014');
            this.setEstimateDeliveryDate('08/10/2014');
            this.setProductClass('85');
            this.selectProduct('Budweiser 24545214SKU');
            this.setWeight(50);
            this.addCost('LH', 80, 40);
            this.clickNextButton();

            this.showOriginAddressDialog();
            this.setAddressName('City 17');
            this.setContactName('Dr. Gordon Freeman');
            this.setAddress1('Seattle');
            this.setAddress1('Seattle');
            this.setPhoneAreaCode('123');
            this.setPhoneNumber('1234567');
            this.saveEditAddress();

            this.showDestinationAddressDialog();
            this.setAddressName('City 17');
            this.setContactName('Dr. Gordon Freeman');
            this.setAddress1('Seattle');
            this.setPhoneAreaCode('123');
            this.setPhoneNumber('1234567');
            this.saveEditAddress();

            this.setBillTo(billTo);
            this.clickNextButton();

            this.setBol(bol);
            this.setPo(bol);
            this.setPu(bol);
            this.setShipperRef(bol);
            this.setSoNumber(bol);
            this.setGlNumber(bol);
            this.setCargoValue(100500);
            this.setRequestedBy('Test test test');
            this.setTrailerNumber(bol);
            this.setDeliveryFromWindow('12:00 AM');
            this.setDeliveryToWindow('12:30 AM');

            this.clickNextButton();
            this.clickdDoneButton();
        }
    };
}]);
