angular.module('PageObjectsModule').factory('DefaultManualBOLPageObject', [
    function () {
        return {
            inputCustomer: '#inputCustomer',
            inputPhone2: '#inputPhone2',

            getPhone2: function () {
                return element(this.inputPhone2).val();
            },

            setCustomer: function (customerName) {
                setValue(this.inputCustomer, customerName);
            },
            setPhone2: function (phone2) {
                setValue(this.inputPhone2, phone2);
            }
        }
    }
]);