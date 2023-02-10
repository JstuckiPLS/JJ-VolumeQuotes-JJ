/**
 * Service for accessing last Shipment REST service
 */
angular.module('plsApp').factory('ShipmentDetailsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/shipment/:shipmentId/:pathParam', {}, {
        last: {
            method: 'GET',
            params: {
                pathParam: 'last',
                customerId: '@customerId'
            },
            isArray: true
        },
        getTooltipData: {
            method: 'GET',
            params: {
                pathParam: 'tooltip',
                customerId: '@customerId',
                shipmentId: '@shipmentId'
            }
        },
        findShipmentPricingDetails: {
            method: 'GET',
            params: {
                pathParam: 'pricingDetails',
                customerId: '@customerId',
                shipmentId: '@shipmentId'
            }
        },
        findShipmentEvents: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'events',
                customerId: '@customerId',
                shipmentId: '@shipmentId'
            }
        },
        getShipmentDataHistory: {
            method: 'GET',
            params: {
                pathParam: 'audit',
                customerId: '@customerId',
                shipmentId: '@shipmentId'
            },
            isArray: true
        },
        getLocationDetails: {
            method: 'GET',
            params: {
                pathParam: 'locationDetails',
                customerId: '-1'
            },
            isArray: true
        },
        getLocationLoadDetails: {
            method: 'PUT',
            params: {
                pathParam: 'locationDetails',
                customerId: '-1'
            },
            isArray: true
        }
    });
}]);

angular.module('plsApp').factory('ShipmentNotesService',
        ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/shipment/:shipmentId/:vendorBillId/:pathDefineParam', {}, {
        addNewNote: {
            method: 'POST',
            params: {
                pathDefineParam: 'notes',
                customerId: '@customerId'
            }
        },
        findShipmentNotes: {
            method: 'GET',
            isArray: true,
            params: {
                pathDefineParam: 'notes',
                customerId: '@customerId',
                shipmentId: '@shipmentId'
            }
        }
    });
}]);

angular.module('quotes.services').factory('ShipmentsProposalService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/shipment/proposal/:pathDefineParam', {}, {
        findShipmentPropositions: {
            params: {
                pathDefineParam: 'propositions'
            },
            method: 'POST',
            isArray: true
        },
        findTerminalInformation: {
            params: {
                pathDefineParam: 'terminal'
            },
            method: 'POST'
        },
        getFreightBillPayTo: {
            params: {
                pathDefineParam: 'freightBillPayTo'
            },
            method: 'GET',
            cache: true
        }
    });
}]);

angular.module('plsApp').factory('ShipmentMileageService', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        getShipmentMileage: function (shipmentMileageData) {
            return $http.post(urlConfig.shipment + '/shipment/proposal/calculateMileage', shipmentMileageData);
        }
    };
}]);

angular.module('plsApp').factory('ShipmentOperationsService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/shipment/:shipmentId/:pathDefineParam/:subPathParam', {}, {
        bookIt: {
            method: 'PUT',
            params: {
                pathDefineParam: 'bookIt',
                customerId: '@customerId',
                storedBolId: '@storedBolId'
            }
        },
        saveAdjustments: {
            method: 'PUT',
            params: {
                pathDefineParam: '@pathDefineParam',
                subPathParam: 'adjustments',
                customerId: '@customerId',
                freightBillDate: '@freightBillDate'

            }
        },
        save: {
            method: 'POST',
            params: {
                pathDefineParam: '',
                customerId: '@customerId',
                hideCreatedTime: '@hideCreatedTime'
            }
        },
        bookShipmentEntry: {
            method: 'PUT',
            params: {
                pathDefineParam: 'bookShipmentEntry',
                customerId: '@customerId',
                storedBolId: '@storedBolId'
            }
        },
        cancelShipment: {
            method: 'POST',
            params: {
                pathDefineParam: 'cancel',
                customerId: '@customerId',
                shipmentId: '@shipmentId'
            }
        },
        closeLoad: {
            method: 'PUT',
            params: {
                pathDefineParam: 'close',
                customerId: '@customerId',
                shipmentId: '@shipmentId',
                note: '@note'
            }
        },
        getShipment: {
            method: 'GET',
            params: {
                customerId: '@customerId',
                shipmentId: '@shipmentId'
            }
        },
        getCopiedShipment: {
            method: 'GET',
            params: {
                pathDefineParam: 'copy',
                customerId: '@customerId',
                shipmentId: '@shipmentId'
            }
        }
    });
}]);

angular.module('plsApp').factory('ShipmentSavingService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/:customerId/shipment/:shipmentId/:pathDefineParam', {}, {
        bookIt: {
            method: 'PUT',
            params: {
                pathDefineParam: 'bookIt',
                customerId: '@customerId',
                storedBolId: '@storedBolId'
            }
        },
        save: {
            method: 'POST',
            params: {
                pathDefineParam: 'saveSalesOrder',
                customerId: '@customerId',
                hideCreatedTime: '@hideCreatedTime'
            }
        },
        bookShipmentEntry: {
            method: 'PUT',
            params: {
                pathDefineParam: 'bookShipmentEntry',
                customerId: '@customerId',
                storedBolId: '@storedBolId'
            }
        }
    });
}]);

angular.module('plsApp').factory('ShipmentDocumentService', ['$resource', 'urlConfig', function ($resource, urlConfig) {
    return $resource(urlConfig.shipment + '/customer/shipmentdocs/:shipmentId/:pathParam/:docId/:subPathParam', {}, {
        getDocumentList: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'list',
                shipmentId: '@shipmentId'
            }
        },
        removeTemporaryDocument: {
            method: 'DELETE',
            params: {
                pathParam: 'deleteTemp',
                docId: '@docId'
            }
        },
        removeDocument: {
            method: 'DELETE',
            params: {
                pathParam: 'delete',
                docId: '@docId'
            }
        },

        downloadDocuments: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam: 'download',
                shipmentId: '@shipmentId'
            }
        },
        canDownload: {
            method: 'GET',
            isArray: false,
            params: {
                pathParam: 'canDownload',
                shipmentId: '@shipmentId'
            }
        },
        prepareBolForShipment: {
            method: 'POST',
            params: {
                pathParam: 'tempDoc',
                subPathParam: 'BOL'
            },
            transformResponse: function (data) {
                return {
                    value: JSON.parse(data)[0].value
                };
            }
        },
        prepareDocsForShipment: {
            method: 'POST',
            params: {
                pathParam: 'tempDoc',
                subPathParam: '@subPathParam',
                hideCreatedTime: '@hideCreatedTime',
                isManualBol: '@isManualBol',
                printType: '@printType'
            },
            isArray: true
        },
        getDocumentTypesList: {
            method: 'GET',
            isArray: true,
            params: {
                pathParam : 'type',
                subPathParam : 'list'
            }
        },
        prepareConsigneeInvoiceForShipment : {
            method : 'POST',
            params : {
                pathParam : 'regenerate',
                subPathParam : 'CONSIGNEE_INVOICE'
            },
            isArray : true
        }
    });
}]);

angular.module('plsApp').factory('ShipmentNotificationSourceService', ['$resource', 'urlConfig',
    function ($resource, urlConfig) {
        return $resource(urlConfig.shipment + '/customer/:customerId/sourceNotifications', {}, {
            getShipmentNotificationsSourceItems: {
                method: 'GET',
                isArray: true,
                params: {
                    customerId: '@customerId'
                }
            }
        });
    }
]);

angular.module('plsApp').factory('ShipmentDocumentEmailService', ['$resource', 'urlConfig',
    function ($resource, urlConfig) {
        return $resource(urlConfig.shipment + '/shipment/email/:pathParam/:subParamId', {}, {
            emailDoc: {
                method: 'POST',
                params: {
                    pathParam: 'send'
                }
            },
            getTemplate: {
                method: 'GET',
                transformResponse: function (data) {
                    return {
                        result: data
                    };
                },
                params: {
                    pathParam: 'doc',
                    loadId: '@loadId'
                }
            },
            getTemplateByDTO: {
                method: 'POST',
                transformResponse: function (data) {
                    return {
                        result: data
                    };
                },
                params: {
                    pathParam: 'doc'
                }
            },
            getTemplateForLoad: {
                method: 'GET',
                transformResponse: function (data) {
                    return {
                        result: data
                    };
                },
                params: {
                    pathParam: 'template',
                    loadId: '@loadId',
                    template: '@template'
                }
            },
            getTemplateForPLSPay: {
                method: 'GET',
                transformResponse: function (data) {
                    return {
                        result: data
                    };
                },
                params: {
                    pathParam: 'plspaytemplate',
                    locationId :'@locationId'
                }
            },
            getEmailHistory: {
                method: 'GET',
                isArray: true,
                params: {
                    pathParam: 'history',
                    subParamId: '@shipmentId'
                }
            }
        });
    }
]);

angular.module('plsApp').factory('MatchedLoadsService', ['$http', 'urlConfig', function ($http, urlConfig) {
    return {
        get: function (proNum, orgId) {
            return $http.get(urlConfig.shipment + '/shipment/tracking/board/matchedLoads?proNum=' + encodeURIComponent(proNum) + '&orgId=' +
                    encodeURIComponent(orgId));
        }
    };
}]);

/**
 * Service for PLS document operations
 * @author Vitalii Gavriliuk
 */
angular.module('plsApp').factory('PlsDocument', ['urlConfig', function (urlConfig) {
    return function (scope) {
        var local = {};

        local.isTiffExtension = function (fileExtension) {
            return fileExtension.toLowerCase() === 'tiff' || fileExtension.toLowerCase() === 'tif';
        };

        /* Public methods */
        return {
            isImage: function (selectedDocumentsPath) {
                var tempFileName = _.result(scope, selectedDocumentsPath)[0].tempFileName;

                if (tempFileName) {
                    var indexOfPoint = tempFileName.lastIndexOf('.');

                    if (indexOfPoint > -1) {
                        if (local.isTiffExtension(tempFileName.substring(indexOfPoint + 1).trim())) {
                            return false;
                        } else {
                            return tempFileName.substring(indexOfPoint + 1).trim() !== 'pdf';
                        }
                    } else {
                        return false;
                    }
                } else {
                    return _.result(scope, selectedDocumentsPath)[0].docFileType &&
                            _.result(scope, selectedDocumentsPath)[0].docFileType !== 'application/pdf';
                }
            },
            isSetOptions: function (optionsPath, selectedDocumentsPath) {
                if (_.result(scope, selectedDocumentsPath) && _.result(scope, selectedDocumentsPath)[0]) {
                    _.set(scope, optionsPath + '.imageContent', this.isImage(selectedDocumentsPath));

                    _.set(scope, optionsPath + '.pdfLocation', urlConfig.shipment + '/customer/shipmentdocs/');

                    /* tempDocId - custom field that need for temporary loaded docs */
                    if (_.result(scope, selectedDocumentsPath)[0].tempDocId) {
                        var tempDocId = _.result(scope, selectedDocumentsPath)[0].tempDocId;
                        _.set(scope, optionsPath + '.pdfLocation', _.result(scope, optionsPath).pdfLocation + tempDocId);
                    } else {
                        var docId = _.result(scope, selectedDocumentsPath)[0].id;
                        _.set(scope, optionsPath + '.pdfLocation', _.result(scope, optionsPath).pdfLocation + docId);
                    }

                    /* Workaround to prevent caching GET requests in IE */
                    _.set(scope, optionsPath + '.pdfLocation', _.result(scope, optionsPath).pdfLocation + '?t=' + new Date().getTime());
                    return true;
                } else {
                    _.set(scope, optionsPath + '.pdfLocation', null);
                    return false;
                }
            }
        };
    };
}]);