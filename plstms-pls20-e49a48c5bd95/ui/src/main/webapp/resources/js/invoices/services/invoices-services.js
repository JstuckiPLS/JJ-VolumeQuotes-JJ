angular.module('plsApp').factory('CustomerInvoiceService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/user/:userId/invoice/:subPath', {
        customerId: '@customerId',
        userId: '@userId'
    }, {
        findCustomerInvoices: {
            method: 'POST',
            isArray: true,
            params: {
                subPath: 'find'
            }
        },
        getCustomerInvoiceSummary: {
            method: 'GET',
            params: {
                subPath: 'summary'
            }
        },
        getBillingAndCreditInfo: {
            method: 'GET',
            params: {
                subPath: 'credit-billing'
            }
        },
        emailTo: {
            method: 'GET',
            params: {
                subPath: 'email-to',
                invoiceNum: '@invoiceNum'
            }
        },
        sendEmail: {
            method: 'PUT',
            params: {
                subPath: 'send-email'
            }
        }
    });
}]);