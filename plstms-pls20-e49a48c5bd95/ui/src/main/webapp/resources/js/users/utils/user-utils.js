/**
 * Utility Methods for users.
 */
angular.module('users.utils').factory('UserUtils', function () {
    return {
        /**
         * Goes through the list of customers and updates one with same id as updated customer with new locations.
         *
         * @param customers list of customers to update
         * @param updatedCustomer customer with new list of locations
         */
        updateCustomerLocations: function (customers, updatedCustomer) {
            _.each(customers, function (customer) {
                if (customer.customerId === updatedCustomer.customerId) {
                    if (updatedCustomer.locations) {
                        customer.locations = updatedCustomer.locations;
                    }
                }
            });
        }
    };
});

