angular.module('PageObjectsModule').factory('CustomerReqFieldsPageObject', [function () {
    return {
        addButton: '[data-ng-click="addReqField()"]',
        editButton: '[data-ng-click="editReqField()"]',
        deleteButton: '[data-ng-click="deleteReqField()"]',

        reqFieldsGridRow: '[data-ng-grid="reqFieldsGrid"] .ngRow',
        row1: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(0)',
        row2: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(1)',
        row3: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(2)',
        row4: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(3)',
        row5: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(4)',
        row6: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(5)',
        row7: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(6)',
        row8: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(7)',
        row9: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(8)',
        row10: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(9)',
        row11: '[data-ng-grid="reqFieldsGrid"] .ngRow:eq(10)',

        identifierDialog: '.modal.modalWidth5.modalHeight7',
        identifierDialogHeader: '.modal.modalWidth5.modalHeight7 .modal-header h4',

        identifierName: '[data-ng-model="reqFieldModel.name"]',
        identifierRequired: '[data-ng-model="reqFieldModel.required"]',
        identifierIO: '[data-ng-model="reqFieldModel.inboundOutbound"]',
        identifierZip: '[data-ng-model="reqFieldModel.address.zip"]',
        identifierCity: '[data-ng-model="reqFieldModel.address.city"]',
        identifierState: '[data-ng-model="reqFieldModel.address.state"]',
        identifierCountry: '[data-ng-model="reqFieldModel.address.country"]',
        identifierOD: '[data-ng-model="reqFieldModel.originDestination"]',
        identifierDefaultValue: '[data-ng-model="reqFieldModel.defaultValue"]',
        identifierStartWith: '[data-ng-model="reqFieldModel.startWith"]',
        identifierEndWith: '[data-ng-model="reqFieldModel.endWith"]',
        identifierAction: '[data-ng-model="reqFieldModel.actionForDefaultValues"]',

        identifierCancelButton: '[data-ng-click="closeDialog()"]',
        identifierSaveButton: '[data-ng-click="saveDialog()"]',

        editReqField: function () {
            element(this.editButton).click();
        },

        IdentifierRequired: function () {
            return element(this.identifierRequired);
        },

        selectIdentifierIO: function (value) {
            setValue(this.identifierIO, value);
        },

        setIdentifierZip: function (value) {
            setValue(this.identifierZip, value);
        },

        setIdentifierCity: function (value) {
            setValue(this.identifierCity, value);
        },

        setIdentifierState: function (value) {
            setValue(this.identifierState, value);
        },

        setIdentifierCountry: function (value) {
            setValue(this.identifierCountry, value);
        },

        selectIdentifierOD: function (value) {
            setValue(this.identifierOD, value);
        },

        setIdentifierDefaultValue: function (value) {
            setValue(this.identifierDefaultValue, value);
        },

        setIdentifierStartWith: function (value) {
            setValue(this.identifierStartWith, value);
        },

        setIdentifierEndWith: function (value) {
            setValue(this.identifierEndWith, value);
        },

        selectIdentifierAction: function (value) {
            setValue(this.identifierAction, value);
        },

        cancelIdentifier: function () {
            element(this.identifierCancelButton).click();
        },

        saveIdentifier: function () {
            element(this.identifierSaveButton).click();
        },

        selectRow1: function () {
            element(this.row1).click();
        },

        selectRow2: function () {
            element(this.row2).click();
        },

        selectRow3: function () {
            element(this.row3).click();
        },

        selectRow4: function () {
            element(this.row4).click();
        },

        selectRow5: function () {
            element(this.row5).click();
        },

        selectRow6: function () {
            element(this.row6).click();
        },

        selectRow7: function () {
            element(this.row7).click();
        },

        selectRow8: function () {
            element(this.row8).click();
        },

        selectRow9: function () {
            element(this.row9).click();
        },

        selectRow10: function () {
            element(this.row10).click();
        },

        selectRow11: function () {
            element(this.row11).click();
        }
    };
}]);