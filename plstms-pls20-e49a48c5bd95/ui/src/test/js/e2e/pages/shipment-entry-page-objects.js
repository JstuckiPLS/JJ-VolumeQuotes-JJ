angular.module('PageObjectsModule').factory('ShipmentEntryPageObject', [function () {
    return {
        selectedCustomer: 'input[data-ng-model="selectedCustomer"]',
        inputOriginAddressName: '.origin [id^="autocomplete"]',
        inputDestinationAddressName: '.destination [id^="autocomplete"]',
        weightModel: 'material.weight',
        commodityClassModel: 'material.commodityClass',
        quantityModel: 'material.quantity',
        getQuoteButtonSelector: '.a_getQuoteButton',
        locationInput: '#location :input',
        inputBOL: '#BOL',
        inputPO: '#PO',
        inputPU: '#PU',
        inputSO: '#SO',
        inputRef: '#SR',
        inputTrailer: '#TR',
        inputGL: '#GL',
        inputCargoValue: '#CARGO',
        inputRequestedBy: '#RB',
        inputPickUpNotes: 'shipmentEntryData.shipment.finishOrder.pickupNotes',
        inputDeliveryNotes: 'shipmentEntryData.shipment.finishOrder.deliveryNotes',
        shippingLabelNotes: 'shipmentEntryData.shipment.finishOrder.shippingLabelNotes',
        buttonBookIt: '.a_bookItButton',
        buttonAgree: 'input[data-ng-model="confirmedTermsOfAgreement"]',
        shipmentDetailsDialog: '#shipmentDetailsDialogDiv',
        loadIdText: '#loadId',
        gridFirstRow: '[ng-row]:first',
        profileId: 'span:contains(Profile ID: 50 (LITECZ02_20070903_20718-RWM))',
        gridBlockedRow: '[data-ng-grid="carrierPropositionsGrid"] [ng-row] i:visible',
        inputCostOverride: '#costOverride',
        inputRevenueOverride: '#revenueOverride',
        copyShipment: '.a_copyFromButton',
        inputSearchByBOL: 'input[ng-model="col.searchValue"]:eq(0)',
        copyButton: '#copyFromItem',
        copyFromDialog: '#copyFromDialogDiv',
        originCode: '.origin label[data-ng-bind="address.addressCode"]',
        originAddress1: '.origin label[data-ng-bind="address.address1"]',
        destinationCode: '.destination label[data-ng-bind="address.addressCode"]',
        destinationAddress1: '.destination label[data-ng-bind="address.address1"]',
        materialsGrid: '[data-ng-grid="materialsGrid"] [ng-row]',
        getQuoteButton: '.a_getQuoteButton',
        shipmentEntrySaveDialog: '#shipmentEntrySaveDialog',
        okShipmentEntrySaveDialogButton: '[data-ng-click="closeShipmentEntrySaveDialog()"]',
        shipmentEntrySuccessWarningDialog: '#shipmentEntrySuccessWarning',
        okShipmentEntrySuccessWarningButton: '[data-ng-click="closeSuccessWarningDialog()"]',
        closeShipmentDetailsButton: '[data-ng-click="closeShipmentDetails();"]',

        triggerEvent: function (selector, event) {
            element(selector).query(function (el, done) {
                var evt = document.createEvent('Event');
                evt.initEvent(event, false, true);
                el[0].dispatchEvent(evt);
                done();
            });
        },

        setCustomer: function (value) {
            progressiveSearch(this.selectedCustomer).enter(value);
            progressiveSearch(this.selectedCustomer).select();
        },

        setOriginAddressName: function (value) {
            setValue(this.inputOriginAddressName, value);
            this.triggerEvent(this.inputOriginAddressName, 'keyup');
            element('.origin li a:first').click(); // pick the first one
        },

        setDestinationAddressName: function (value) {
            setValue(this.inputDestinationAddressName, value);
            this.triggerEvent(this.inputDestinationAddressName, 'keyup');
            element('.destination li a:first').click(); // pick the first one
        },

        setWeight: function (weight) {
            input(this.weightModel).enter(weight);
        },

        setCommodityClass: function (commodityClass) {
            select(this.commodityClassModel).option(commodityClass);
        },

        setQuantity: function (qty) {
            input(this.quantityModel).enter(qty);
        },

        selectProduct: function (productLabel) {
            productList('products.product').showList();
            productList('products.product').selectProduct(productLabel);
        },

        clickGetQuote: function () {
            element(this.getQuoteButtonSelector).click();
        },

        getPropositionsGridRowCount: function () {
            return element('[data-ng-grid="carrierPropositionsGrid"] [ng-row]').count();
        },

        setLocation: function (value) {
            setValue(this.locationInput, value);
        },

        setBOL: function (value) {
            setValue(this.inputBOL, value);
        },
        setPO: function (value) {
            setValue(this.inputPO, value);
        },
        setPU: function (value) {
            setValue(this.inputPU, value);
        },
        setSO: function (value) {
            setValue(this.inputSO, value);
        },
        setRef: function (value) {
            setValue(this.inputRef, value);
        },
        setTrailer: function (value) {
            setValue(this.inputTrailer, value);
        },
        setGL: function (value) {
            setValue(this.inputGL, value);
        },
        setCargoValue: function (value) {
            setValue(this.inputCargoValue, value);
        },
        setRequestedBy: function (value) {
            setValue(this.inputRequestedBy, value);
        },
        setPickupNotes: function (value) {
            input(this.inputPickUpNotes).enter(value);
        },
        setDeliveryNotes: function (value) {
            input(this.inputDeliveryNotes).enter(value);
        },
        setShippingLabelNotes: function (value) {
            input(this.shippingLabelNotes).enter(value);
        },
        clickBookItButton: function () {
            element(this.buttonBookIt).click();
        },
        getBookItButtonDisplay: function () {
            return element(this.buttonBookIt).attr("disabled");
        },
        clickAgreeButton: function () {
            element(this.buttonAgree).click();
        },
        getShipmentDialog: function () {
            return element(this.shipmentDetailsDialog);
        },
        getLoadIdText: function () {
            return element(this.loadIdText).text();
        },

        clickFirstRow: function (value) {
            element(this.gridFirstRow).click();
        },
        getProfileIdVal: function () {
            return element(this.profileId).text();
        },
        clickBlockedRow: function () {
            element(this.gridBlockedRow).click();
        },
        setCostOverride: function (value) {
            setValue(this.inputCostOverride, value);
        },
        setRevenueOverride: function (value) {
            setValue(this.inputRevenueOverride, value);
        },
        clickCopyShipmentButton: function () {
            element(this.copyShipment).click();
        },
        searchByBOL: function (value) {
            setValue(this.inputSearchByBOL, value);
        },
        clickCopyButton: function () {
            element(this.copyButton).click();
        },
        getCopyFromDialog: function () {
            return element(this.copyFromDialog);
        },
        getOriginCode: function () {
            return element(this.originCode).text();
        },
        getDestinationCode: function () {
            return element(this.destinationCode).text();
        },

        getOriginAddress1: function () {
            return element(this.originAddress1).text();
        },

        getDestinationAddress1: function () {
            return element(this.destinationAddress1).text();
        },
        getMaterialsGridRowCount: function () {
            return element(this.materialsGrid).count();
        },
        clickGetQuoteButton: function () {
            element(this.getQuoteButton).click();
        },
        getShipmentEntrySaveDialog: function () {
            return element(this.shipmentEntrySaveDialog);
        },
        getShipmentEntrySuccessWarningDialog: function () {
            return element(this.shipmentEntrySuccessWarningDialog);
        },
        clickOkShipmentEntrySaveDialogButton: function () {
            element(this.okShipmentEntrySaveDialogButton).click();
        },
        clickOkShipmentEntrySuccessWarningDialogButton: function () {
            element(this.okShipmentEntrySuccessWarningButton).click();
        },
        clickCloseShipmentDetailsButton: function () {
            element(this.closeShipmentDetailsButton).click();
        }
    };
}]);
