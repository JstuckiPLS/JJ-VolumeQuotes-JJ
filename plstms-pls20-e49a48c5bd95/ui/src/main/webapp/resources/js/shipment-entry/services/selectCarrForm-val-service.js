angular.module('shipmentEntry').factory('SelectCarrFormValService', function () {
    var selectCarrFormValidity;

    return {
        setFormValidity: function (val) {
            selectCarrFormValidity = val;
        },
        getFormValidity: function () {
            return selectCarrFormValidity;
        }
    };
});