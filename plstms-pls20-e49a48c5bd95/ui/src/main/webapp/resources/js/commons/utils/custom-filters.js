/**
 * AngularJS custom filters.
 *
 * @author: Alexander Kirichenko
 * Date: 4/30/13
 * Time: 4:23 PM
 */
var DimensionsMeasure = {
    INCH: 'Inch',
    CMM: 'Cm',
    FT: 'Ft',
    M: 'M'
};

var WeightMeasure = {
    LBS: 'Lbs',
    KG: 'Kg',
    G: 'g',
    OZ: 'Oz'
};

var PaymentTerms = {
    'COLLECT': 'Collect',
    'PREPAID': 'Prepaid',
    'THIRD_PARTY_COLLECT': 'Third Party Collect',
    'THIRD_PARTY_PREPAID': 'Third Party Prepaid'
};

var ShipmentDirection = {
    'I': 'Inbound',
    'O': 'Outbound',
    'B': 'Both'
};

var DirectionType = {
    'D': 'Destination',
    'O': 'Origin',
    'B': 'Both'
};

var InvoiceProcessingType = {
    AUTOMATIC: 'Automatic',
    MANUAL: 'Manual'
};

var CurrencyCode = {
    USD: 'USD',
    CAD: 'CAD'
};

var ProcessingPeriod = {
    DAILY: 'Daily',
    WEEKLY: 'Weekly'
};

var InvoiceType = {
    TRANSACTIONAL: 'Transactional',
    CBI: 'CBI'
};

var IdentifierNames = {
    'SR': 'Shipper Ref',
    'PRO': 'Pro #',
    'TR': 'Trailer',
    'JOB': 'Job#',
    'CARGO': 'Cargo Value',
    'RB': 'Requested By'
};

var VBReasonsCode = {
    "DUPLICATE" : "Duplicate",
    "NOT_OURS_TO_PAY" : "Not ours to pay",
    "SHORT_PAY" : "Short Pay",
    "ALREADY_INVOICED" : "Already Invoiced",
    "LOAD_1_0" : "1.0 Load ",
    "OTHER" : "Other"
};

function formatPickupWindowDTO(pickupWindowDTO) {
    var out = '';

    if (pickupWindowDTO) {
        out += pickupWindowDTO.hours === 0 ? '12' : (pickupWindowDTO.hours >= 10 ? pickupWindowDTO.hours : '0' + pickupWindowDTO.hours);
        out += pickupWindowDTO.minutes === 0 ? ':00' : ':30';
        out += pickupWindowDTO.am ? ' AM' : ' PM';
    }

    return out;
}

function formatPhone(phone) {
    if (_.isString(phone)) {
        return phone;
    }

    var out = '';

    if (phone) {
        if (phone.countryCode) {
            out += '+' + phone.countryCode.replace(/^0+/, ''); // country code without leading zeroes
        }
        if (phone.areaCode) {
            out += '(' + phone.areaCode + ')';
        }
        if (phone.number) {
            if (phone.areaCode) {
                out += phone.number.substr(0, 3) + ' ' + phone.number.substr(3);
            } else {
                out = phone.number;
            }
        }
        if (phone.extension) {
            out += ' Ext.: ' + phone.extension;
        }
    }

    return out;
}

angular.module('plsApp.customFilters').filter('plsCurrency', ['$filter', function ($filter) {
    return function (inputValue) {
        if (!isNaN(parseFloat(inputValue)) && isFinite(inputValue)) {
            var prefix = inputValue < 0 ? '-' : '';
            return prefix + $filter('currency')(Math.abs(inputValue));
        } else {
            return '';
        }
    };
}]);

angular.module('plsApp.customFilters').filter('phone', function () {
    return function (phone) {
        return formatPhone(phone);
    };
});

angular.module('plsApp.customFilters').filter('contact', function () {
    return function (contact) {
        var out = '';

        if (contact) {
            if (contact.contactFirstName) {
                out += contact.contactFirstName;
                if (contact.contactLastName) {
                    out += ' ';
                }
            }
            if (contact.contactLastName) {
                out += contact.contactLastName;
            }
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('zip', function () {
    return function (zip) {
        var out = '';

        if (zip) {
            if (zip.label) {
                out = zip.label;
            } else {
                out += zip.city || '';

                if (zip.state) {
                    if (out.length > 0) {
                        out += ', ';
                    }

                    out += zip.state;
                }

                if (zip.zip) {
                    if (out.length > 0) {
                        out += ', ';
                    }

                    out += zip.zip;
                }
            }
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('materialDimension', function () {
    return function (material) {
        var out = '';

        if (material && material.length && material.width && material.height) {
            out = material.length + 'x' + material.width + 'x' + material.height;

            if (material.dimensionUnit) {
                out += ' ' + DimensionsMeasure[material.dimensionUnit];
            } else {
                out += ' ' + DimensionsMeasure.INCH;
            }
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('materialWeight', function () {
    return function (material) {
        var out = '';

        if (material && material.weight) {
            out = material.weight;

            if (material.weightUnit) {
                out += ' ' + WeightMeasure[material.weightUnit];
            } else {
                out += ' ' + WeightMeasure.LBS;
            }
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('appendSuffix', function () {
    return function (value, suffix) {
        var out = '';

        if (value) {
            out = value;

            if (suffix) {
                out += ' ' + suffix;
            }
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('materialProduct', function () {
    return function (input, productSortType) {
        var out = '';

        if (input) {
            var separator = ' ';
            var description = input.productDescription ? input.productDescription + separator : '';
            var code = input.productCode ? input.productCode + separator : '';
            var hazmatUnNumber = input.hazmatUnNumber ? input.hazmatUnNumber + separator : '';
            var hazmatClass = input.hazmatClass ? input.hazmatClass + separator : '';
            var packingGroup = input.packingGroup ? input.packingGroup : '';

            if (productSortType && productSortType.indexOf('PRODUCT_DESCRIPTION') !== -1) {
                out = description + code + hazmatUnNumber + hazmatClass + packingGroup;
            } else {
                out = code + description + hazmatUnNumber + hazmatClass + packingGroup;
            }
        }

        return out.trim();
    };
});

angular.module('plsApp.customFilters').filter('commodityClass', function () {
    return function (input) {
        var out = input;

        if (input) {
            var searchValue = input.indexOf('CLASS_');

            if (!isNaN(searchValue) && searchValue !== -1) {
                out = input.replace('CLASS_', '');
                searchValue = input.indexOf('_5');

                if (!isNaN(searchValue) && searchValue !== -1) {
                    out = out.replace('_5', '.5');
                }
            }
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('packageType', ['ShipmentUtils', function (ShipmentUtils) {
    return function (input) {
        var out = '';
        if (input) {
            var matchType = _.findWhere(ShipmentUtils.getDictionaryValues().packageTypes, {code: input});
            out = matchType ? matchType.label : '';
        }
        return out;
    };
}]);

angular.module('plsApp.customFilters').filter('shipmentStatus', function () {
    var shipmentStatus = {
        OPEN: 'Open', BOOKED: 'Booked', PENDING_PAYMENT: "Pending Payment", DISPATCHED: 'Dispatched', IN_TRANSIT: 'In-Transit', 
        DELIVERED: 'Delivered', CANCELLED: 'Cancelled', OUT_FOR_DELIVERY: 'Out for Delivery', UNKNOWN: 'Unknown',
        CUSTOMER_TRUCK: 'Customer Truck'
    };

    return function (input) {
        var out = '';

        if (input) {
            out = shipmentStatus[input] || '';
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('notificationType', ['ShipmentUtils', function (ShipmentUtils) {
    return function (input) {
        var out = '';

        if (input) {
            var notificationType = _.findWhere(ShipmentUtils.getDictionaryValues().notificationTypes, {value: input});
            out = notificationType ? notificationType.label : '';
        }

        return out;
    };
}]);

angular.module('plsApp.customFilters').filter('emailStatus', function () {
    var emailStatus = {DOCUMENT: 'Document', INVOICE: 'Invoice', NOTIFICATION: 'Notification', PEN_PAY: 'Pending Payment'};

    return function (input) {
        var out = '';

        if (input) {
            out = emailStatus[input] || '';
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('pickupWindowTime', function () {
    return function (input) {
        return formatPickupWindowDTO(input);
    };
});

angular.module('plsApp.customFilters').filter('emergencyPhone', function () {
    return function (phone) {
        if (phone) {
            return formatPhone({
                countryCode: phone.emergencyResponsePhoneCountryCode,
                areaCode: phone.emergencyResponsePhoneAreaCode,
                number: phone.emergencyResponsePhone,
                extension: phone.emergencyResponsePhoneExtension
            });
        }

        return phone;
    };
});

angular.module('plsApp.customFilters').filter('dimensionsMeasure', function () {
    return function (input) {
        if (input && DimensionsMeasure.hasOwnProperty(input)) {
            return DimensionsMeasure[input];
        }

        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('weightMeasure', function () {
    return function (input) {
        if (input && WeightMeasure.hasOwnProperty(input)) {
            return WeightMeasure[input];
        }

        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('paymentTerms', function () {
    return function (input, defaultValue) {
        if (input && PaymentTerms.hasOwnProperty(input)) {
            return PaymentTerms[input];
        } else if (defaultValue) {
            return defaultValue;
        }

        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('shipmentDirection', function () {
    return function (input, defaultValue) {
        if (input && ShipmentDirection.hasOwnProperty(input)) {
            return ShipmentDirection[input];
        } else if (defaultValue) {
            return defaultValue;
        }

        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('directionType', function () {
    return function (input) {
        if (input && DirectionType.hasOwnProperty(input)) {
            return DirectionType[input];
        }
        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('pickupWindowDiapason', function () {
    return function (input) {
        var out = '';

        if (input) {
            if (input.pickupWindowFrom) {
                out += 'From ' + formatPickupWindowDTO(input.pickupWindowFrom);
            }
            if (input.pickupWindowTo) {
                out += ' to ' + formatPickupWindowDTO(input.pickupWindowTo);
            }
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('deliveryWindowDiapason', function () {
    return function (input) {
        var out = '';

        if (input) {
            if (input.deliveryWindowFrom) {
                out += 'From ' + formatPickupWindowDTO(input.deliveryWindowFrom);
            }
            if (input.deliveryWindowTo) {
                out += ' to ' + formatPickupWindowDTO(input.deliveryWindowTo);
            }
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('longTime', function () {
    return function (input, eod) {
        var hours = "12", minutes = "00", am = true;

        if (input === 2400 && eod !== false) {
            return 'EOD';
        }

        if (input === undefined || input < -1 || input > 2400) {
            return 'None';
        } else if (input === -1) {
            return 'Any Time';
        }

        if (input) {
            hours = String(input);

            if (hours.length > 2) {
                hours = parseInt(hours.substring(0, hours.length - 2), 10);

                if (hours > 12) {
                    am = false;
                    hours = hours - 12;
                } else if (hours === 12) {
                    am = false;
                }
            } else {
                hours = 12;
            }

            //minutes
            minutes = String(input);
            if (minutes.length > 2) {
                minutes = minutes.substring(minutes.length - 2);
            } else if (minutes.length === 1) {
                minutes = '0' + minutes;
            }
        }

        return hours + ':' + minutes + ' ' + (am ? 'AM' : 'PM');
    };
});

angular.module('plsApp.customFilters').filter('minutesTime', function () {
    return function (input, hideMeasure) {
        var out = '', days, hours, minutes;//3d 4h 15m = 4320 + 240 + 15 = 4575

        if (!input || input === 'N/A') {
            out = 'N/A';
        } else {
            days = Math.floor(input / 1440); // 24 * 60
            hours = Math.floor((input % 1440) / 60);
            minutes = (input % 1440) % 60;

            if (days > 0) {
                out += days + (hideMeasure ? '' : ' day');

                if (days > 1 && !hideMeasure) {
                    out += 's';
                }

                if (hours > 0 || minutes > 0) {
                    out += ' ';
                }
            }

            if (hours > 0) {
                out += hours + (hideMeasure ? '' : ' hour');

                if (hours > 1 && !hideMeasure) {
                    out += 's';
                }

                if (minutes > 0) {
                    out += ' ';
                }
            }

            if (minutes > 0) {
                out += minutes + (hideMeasure ? '' : ' minute');

                if (minutes > 1 && !hideMeasure) {
                    out += 's';
                }
            }
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('customerStatus', function () {
    var customerStatus = {
        ACTIVE: 'Active',
        INACTIVE: 'Inactive',
        HOLD: 'Hold'
    };

    return function (input) {
        var out = '';

        if (input) {
            out = customerStatus[input] || '';
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('statusReason', function () {
    var statusReasons = {
        CUSTOMER_REQUEST: 'Customer Request',
        NO_ACTIVITY: 'No activity',
        OUT_OF_BUSINESS: 'No longer in business',
        ACTIVITY_REQUESTED: 'Activity/Operations Request',
        ENROLLMENT_ACCEPTED: 'Enrollment Application Accepted',
        CREDIT_HOLD: 'Credit Hold',
        TAX_ID_EMPTY: 'Tax ID is absent'
    };

    return function (input) {
        var out = '';

        if (input) {
            out = statusReasons[input] || '';
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('requiredDocument', function () {
    var requiredDocuments = {
        REQUIRED: 'Required',
        ON_AVAIL: 'When Available'
    };

    return function (input) {
        var out = '';

        if (input) {
            out = requiredDocuments[input] || '';
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('nmfc', function () {
    return function (product) {
        var out = '';

        if (product) {
            out += product.nmfc || '';

            if (out.length > 0 && product.nmfcSubNum) {
                out += '-';
            }

            out += product.nmfcSubNum || '';
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('invoiceHistoryStatus', function () {
    var statuses = {
        UNPAID: "Unpaid",
        SHORT_PAID: "Short Paid",
        PAID: "Paid"
    };

    return function (input) {
        var out = '';

        if (input) {
            out = statuses[input] || '';
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('ref', ['$rootScope', function ($rootScope) {
    return function (ref) {
        if (ref) {
            if (ref === 'CRA' || ref === 'SRA') {
                return 'Base Rate';
            } else if ($rootScope.accessorialTypes && $rootScope.accessorialTypes.length) {
                var found = _.find($rootScope.accessorialTypes, function (type) {
                    return type.id === ref;
                });

                if (found) {
                    return found.description;
                }
            }
        }

        return ref;
    };
}]);

angular.module('plsApp.customFilters').filter('refCodeAndDesc', ['$rootScope', function ($rootScope) {
    return function (ref) {
        if (ref) {
            if (ref === 'CRA' || ref === 'SRA') {
                return 'LH - Base Rate';
            } else if ($rootScope.accessorialTypes && $rootScope.accessorialTypes.length) {
                var found = _.find($rootScope.accessorialTypes, function (type) {
                    return type.id === ref;
                });

                if (found) {
                    return found.id + ' - ' + found.description;
                }
            }
        }

        return ref;
    };
}]);

angular.module('plsApp.customFilters').filter('financialReason', ['$rootScope', function ($rootScope) {
    return function (reasonCode) {
        if (reasonCode && $rootScope.financialReasons && $rootScope.financialReasons.length) {
            var found = _.find($rootScope.financialReasons, function (reason) {
                return reason.value === reasonCode;
            });

            if (found) {
                return found.label;
            }
        }

        return reasonCode;
    };
}]);

angular.module('plsApp.customFilters').filter('capitalize', function () {
    return function (input) {
        var out = '';

        if (input) {
            input = input.toLowerCase();
            out = input.charAt(0).toUpperCase() + input.substring(1);
        }

        return out;
    };
});

angular.module('plsApp.customFilters').filter('invoiceProcessingType', function () {
    return function (input) {
        if (input && InvoiceProcessingType.hasOwnProperty(input)) {
            return InvoiceProcessingType[input];
        }

        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('currencyCode', function () {
    return function (input) {
        if (input && CurrencyCode.hasOwnProperty(input)) {
            return CurrencyCode[input];
        }

        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('processingPeriod', function () {
    return function (input) {
        if (input && ProcessingPeriod.hasOwnProperty(input)) {
            return ProcessingPeriod[input];
        }

        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('processingInfo', ['DateTimeUtils', function (DateTimeUtils) {
    return function (input, automaticOnly) {
        var result = "";

        if (automaticOnly && input.processingType !== 'AUTOMATIC') {
            return result;
        }

        if (input && ProcessingPeriod.hasOwnProperty(input.processingPeriod)) {
            result = ProcessingPeriod[input.processingPeriod];

            switch (input.processingPeriod) {
                case 'WEEKLY':
                    if (input.processingDayOfWeek) {
                        result += ", " + input.processingDayOfWeek;
                    }
                    break;
                case 'DAILY':
                    if (input.processingTimeInMinutes) {
                        var time = DateTimeUtils.timeStringFromTimeInMinutes(input.processingTimeInMinutes);

                        if (time) {
                            result += ", @" + time;
                        }

                        if (input.processingTimezone) {
                            result += " " + input.processingTimezone.code;
                        }
                    }
                    break;
            }
        }

        return result;
    };
}]);

angular.module('plsApp.customFilters').filter('invoiceType', function () {
    return function (input) {
        if (input && InvoiceType.hasOwnProperty(input)) {
            return InvoiceType[input];
        }

        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('cbiInvoiceType', function () {
    return function (input) {
        if (input === 'PLS') {
            return 'Invoice in PLS 2.0';
        } else if (input === 'FIN') {
            return 'Invoice in Financials';
        }

        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('billToAndCurrency', function () {
    return function (input) {
        if (input) {
            var result = input.billToName || '';
            result += result ? ', ' : '';
            result += input.currency || 'USD';
            return result;
        }

        return undefined;
    };
});

/**
 * Filtering examples:
 *
 * inputValue = "CAN, USA"; filteredValue = "CAN, USA"
 * inputValue = "null, USA"; filteredValue = "USA"
 * inputValue = "CAN, null"; filteredValue = "CAN"
 * inputValue = "null, null"; filteredValue = ""
 */
angular.module('plsApp.customFilters').filter('notNullFilter', function () {
    return function (inputValue) {
        var filteredValue = "";

        _.each(inputValue.split(','), function (valueToBeFiltered) {
            var filteredValueRejected = valueToBeFiltered.trim() === 'null';

            if (filteredValue.length !== 0 && !filteredValueRejected) {
                filteredValue += ', ';
            }

            filteredValue = filteredValueRejected ? filteredValue : filteredValue + valueToBeFiltered;
        });

        return filteredValue;
    };
});

angular.module('plsApp.customFilters').filter('percentage', ['$filter', function ($filter) {
            return function (input, symbol) {
                return $filter('number')(input, symbol) + '%';
            };
}]);

angular.module('plsApp.customFilters').filter('shipmentEvent', function () {
    return function (input) {
        var result = '';

        if (input) {
            if (input.carrierName) {
                result += '(' + input.carrierName + ') ';
            }

            if (input.city && input.stateCode) {
                result += input.city + ', ' + input.stateCode + ' ';
            }

            if (result) {
                result += '- ';
            }

            if (input.event) {
                result += input.event;
            }
        }

        return result;
    };
});

angular.module('plsApp.customFilters').filter('mile', function () {
    return function (input) {
        var result = '';

        if ((input === 0) || input) {
            result = input === 1 ? input + ' mile' : input + ' miles';
        }

        return result;
    };
});

angular.module('plsApp.customFilters').filter('booleanFilter', function () {
    return function (input, trueString, falseString) {
        if (input) {
            return trueString;
        }

        return falseString;
    };
});

angular.module('plsApp.customFilters').filter('htmlStringFilter', function () {
    return function (input) {
        if (_.isUndefined(input) || _.isNull(input)) {
            return input;
        }

        var container = document.createElement('div');
        container.innerHTML = input;

        return container.children.main ? container.children.main.innerText : container.innerText;
    };
});

/**
 * Get message of indication according to carrier integration type.
 * 
 */
angular.module('plsApp.customFilters').filter('integrationTypeFilter', function() {
    return function(integrationType) {
        switch (integrationType) {
        case 'MANUAL':
            return "Email";
        case 'EDI':
            return "EDI";
        case 'API':
            return '';
        default:
            return '';
        }
    };
});

angular.module('plsApp.customFilters').filter('billToIdentifierNames', function () {
    return function (input) {
        if (input && IdentifierNames.hasOwnProperty(input)) {
            return IdentifierNames[input];
        }

        return input;
    };
});

angular.module('plsApp.customFilters').filter('billToReqFieldsAddress', function () {
    return function (input) {
        return _.map(input, function (value) {
            return value;
        }).join(', ');
    };
});

angular.module('plsApp.customFilters').filter('billToReqFieldsRules', function () {
    return function (self) {
        if (angular.isUndefined(self)) {
            return '';
        }

        if (self.startWith && self.endWith) {
            return 'Start with: ' + self.startWith + ' | ' + 'End with: ' + self.endWith;
        } else if (self.startWith) {
            return 'Start with: ' + self.startWith;
        } else if (self.endWith) {
            return 'End with: ' + self.endWith;
        }
    };
});

angular.module('plsApp.customFilters').filter('contractionInvoiceType', function () {
    return function (invoiceType) {
        switch (invoiceType) {
        case 'TRANSACTIONAL': 
            return 'T';
        case 'CBI':
            return 'C';
        default:
            return '';
        }
    };
});

angular.module('plsApp.customFilters').filter('vbReasonCode', function() {
    return function(value) {
        if (value) {
            return VBReasonsCode[value];
        }
        return undefined;
    };
});

angular.module('plsApp.customFilters').filter('cube', ['$filter', function ($filter) {
    return function (cube) {
        if (cube) {
            return $filter('number')(cube, 2) + ' CU FT';
        } 
        return '';
    };
}]);

angular.module('plsApp.customFilters').filter('density', ['$filter',function ($filter) {
    return function (density) {
        if (density) {
            return $filter('number')(density, 2) + ' PCF';
        }
        return '';
    };
}]);
