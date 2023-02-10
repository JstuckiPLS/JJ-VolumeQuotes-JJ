angular.module('plsApp').factory('BillToService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/customer/:customerId/user/:userId/bill_to/:billToId/:path", {}, {
        saveUpdateBillTo: {
            method: 'POST',
            params: {
                customerId: '@customerId',
                userId: '@userId'
            }
        },
        getBillToById: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                userId: '@userId',
                billToId: '@billToId'
            }
        },
        getIdValueByOrgId: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                userId: '@userId',
                billToId: 'keyValues'
            },
            isArray: true
        },
        list: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                userId: '@userId',
                billToId: 'list'
            },
            isArray: true
        },
        getCurrentBillToRequiredField: {
            method: 'GET',
            params: {
                path: "getBillToReqField",
                billToId: '@billToId',
                customerId: '@customerId',
                userId: '@userId'
            },
            isArray: true
        }
    });
}]);

angular.module('plsApp').factory('BillToEmailService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/customer/:customerId/user/:userId/bill_to/:path?ids=:billToId", {}, {
        getEmails: {
            method: 'GET',
            params: {
                customerId: '-1',
                userId: '-1',
                billToId: '@billToId',
                path: 'emails'
            },
            isArray: true
        }
    });
}]);

angular.module('plsApp').factory('BillToValidationService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/customer/:customerId/user/:userId/bill_to", {}, {
        validateNameDuplication: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                userId: '@userId'
            },
            transformResponse: function (data) {
                return {
                    result: data
                };
            }
        }
    });
}]);

angular.module('plsApp').factory('BillToDocumentService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.core + "/customer/:customerId/billTo/:billToId/requiredDocuments",
            {customerId: '@customerId', billToId: '@billToId'}, {});
}]);

angular.module('plsApp').factory('DefaultReqFieldService', function () {
    function getDefaultField(field) {
        return {
            name: field,
            required: field === 'PRO',
            inboundOutbound: 'B',
            originDestination: 'B',
            address: {}
        };
    }

    return {
        getDefaultField: getDefaultField,
        getRequiredFields: function (allRequiredFields, billToRequiredFields) {
            return _.map(allRequiredFields, function (field) {
                return _.findWhere(billToRequiredFields, {name : field.value}) || getDefaultField(field.value);
            });
        }
    };
});