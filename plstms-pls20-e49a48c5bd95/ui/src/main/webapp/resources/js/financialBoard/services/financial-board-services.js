/**
 * Service for accessing Shipment Financial Boards REST service
 */
angular.module('financialBoardServices').factory('FinancialBoardService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/invoice/financial/board/:pathParam/:subPathParam:rebillAdjId', {
        subPathParam: '', rebillAdjId: ''
    }, {
        lowBenefit: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'lowBenefit'
            }
        },
        transactional: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'transactional'
            }
        },
        getRebillAdjustments: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'rebill',
                rebillAdjIds: '@rebillAdjIds'
            }
        },
        approve: {
            method: 'PUT',
            params: {
                pathParam: 'approve',
                loadId: '@loadId',
                adjustmentId: '@adjustmentId',
                approve: '@approve'
            }
        },
        approveAudit: {
            method: 'PUT',
            params: {
                subPathParam: 'approve',
                pathParam: 'audit'
            },
            transformResponse: function (data) {
                return {
                    data: JSON.parse(data)
                };
            }
        },
        sendToInvoiceAudit: {
            method: 'PUT',
            params: {
                subPathParam: 'send',
                pathParam: 'invoiceAudit',
                auditRecords: '@auditRecords',
                code: '@code',
                note: '@note'
            }
        },
        sendToPriceAudit: {
            method: 'PUT',
            params: {
                subPathParam: 'send',
                pathParam: 'priceAudit',
                auditRecords: '@auditRecords',
                code: '@code',
                note: '@note'
            }
        },
        processInvoices: {
            method: 'PUT',
            isArray: true,
            params: {
                subPathParam: 'processInvoices'
            }
        },
        historyInvoices: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'history'
            }
        },
        historyCBIDetails: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'history',
                subPathParam: 'cbi'
            }
        },
        reProcessHistory: {
            method: 'PUT',
            isArray: true,
            params: {
                pathParam: 'history',
                subPathParam: 'reprocess'
            }
        },
        getReason: {
            method: 'GET',
            params: {
                pathParam: 'reason',
                loadId: '@loadId'
            }
        },
        priceAudit: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'priceAudit'
            }
        },
        isCustomerEdiEnabled: {
            method: 'GET',
            isArray: false,
            params: {
                pathParam: 'customerInvoice'
            }
        },
        readyForConsolidated: {
            method: 'PUT',
            params: {
                subPathParam: 'readyForConsolidated',
                pathParam: 'invoiceAudit'
            }
        }
    });
}]);

angular.module('financialBoardServices').factory('FinancialBoardInvoiceErrorsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/invoice/financial/board/errors/:errorId/:pathParam', {}, {
        invoiceErrors: {
            method: 'GET',
            params: {},
            isArray: true
        },
        exportErrors: {
            method: 'POST',
            isArray: true,
            params: {
                pathParam: 'export'
            }
        },
        errorsCount: {
            method: 'GET',
            params: {
                pathParam: 'count'
            }
        },
        cancelError: {
            method: 'PUT',
            params: {
                pathParam: 'cancel',
                errorId: '@errorId'
            }
        },
        reProcessErrors: {
            method: 'PUT',
            params: {
                pathParam: 'reprocess',
                errorId: '@errorId'
            }
        },
        getEmailSubjectForReprocessError: {
            method: 'GET',
            params: {
                pathParam: 'subject',
                errorId: '@errorId'
            }
        }
    });
}]);

angular.module('financialBoardServices').factory('FinancialBoardCBIService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/invoice/financial/board/cbi/:billToId:loadPath/:subPathParam:loadId/:loadSubPath', {
        billToId: '', subPathParam: '', loadPath: '', loadId: '', loadSubPath: ''
    }, {
        list: {
            method: 'GET',
            isArray: true
        },
        listLoads: {
            method: 'GET',
            isArray: true,
            params: {
                billToId: '@billToId'
            }
        },
        approveAll: {
            method: 'POST',
            params: {
                billToId: '@billToId',
                subPathParam: 'approve',
                approved: '@approved'
            }
        },
        processCBI: {
            method: 'PUT',
            isArray: true,
            params: {
                subPathParam: 'process'
            }
        },
        getNextInvoiceNumberForCBI: {
            method: 'GET',
            params : {
                subPathParam: 'invoiceNumber'
            }
        }
    });
}]);