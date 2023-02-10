angular.module('PageObjectsModule').factory('FinishOrderPageObject', [function () {
    return {
        inputBOL: '#BOL',
        inputPO: '#PO',
        inputPU: '#PU',
        inputSO: '#SO',
        inputRef: '#SR',
        inputTrailer: '#TR',
        inputGL: '#GL',
        inputCargoValue: '#CARGO',
        inputRequestedBy: '#RB',
        buttonNext: '.a_nextButton',
        buttonBookIt: '.a_bookItButton',
        buttonAgree: '.a_agreeButton',
        inputPickUpNotes: 'shipment.finishOrder.pickupNotes',
        inputDeliveryNotes: 'shipment.finishOrder.deliveryNotes',
        deliveryFromWindow: 'input[id="deliveryFromTime"]',
        deliveryToWindow: 'input[id="deliveryToTime"]',

        setPickupNotes: function (value) {
            input(this.inputPickUpNotes).enter(value);
        },
        setDeliveryNotes: function (value) {
            input(this.inputDeliveryNotes).enter(value);
        },
        setRef: function (value) {
            setValue(this.inputRef, value);
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
        clickNextButton: function () {
            element(this.buttonNext).click();
        },
        clickBookItButton: function () {
            element(this.buttonBookIt).click();
        },
        clickAgreeButton: function () {
            element(this.buttonAgree).click();
        },
        setDeliveryFromWindow: function (fromTime) {
            setValue(this.deliveryFromWindow, fromTime);
        },
        setDeliveryToWindow: function (toTime) {
            setValue(this.deliveryToWindow, toTime);
        }
    };
}]);
