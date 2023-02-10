// Utility Methods for shipments
angular.module('plsApp.utils').factory('ShipmentUtils', ['$rootScope', '$filter', 'ShipmentDictionaryService', 'DictionaryService',
    function ($rootScope, $filter, ShipmentDictionaryService, DictionaryService) {
        var dictionary = {
            classes: [
                'CLASS_50', 'CLASS_55', 'CLASS_60', 'CLASS_65', 'CLASS_70', 'CLASS_77_5', 'CLASS_85', 'CLASS_92_5', 'CLASS_100',
                'CLASS_110', 'CLASS_125', 'CLASS_150', 'CLASS_175', 'CLASS_200', 'CLASS_250', 'CLASS_300', 'CLASS_400', 'CLASS_500'
            ],
            dimensions: ['INCH', 'CMM'],
            weights: ['LBS', 'KG'],
            paymentTerms: ['COLLECT', 'PREPAID', 'THIRD_PARTY_COLLECT', 'THIRD_PARTY_PREPAID'],
            shipmentStatusEnum: {
                OPEN: "Open",
                BOOKED: "Booked",
                PENDING_PAYMENT: "Pending Payment",
                DISPATCHED: "Dispatched",
                IN_TRANSIT: "In-Transit",
                OUT_FOR_DELIVERY: "Out for Delivery",
                DELIVERED: "Delivered",
                CANCELLED: "Cancelled"
            },
            ediType: {
                EDI_990: "990",
                EDI_211: "211",
                EDI_214: "214"
            },
            printTypes: [
                {"label":"1 per sheet (5.5 x 8.5)", "value":1,
                    description:'Avery # 15665, 18665, 48165, 5165, 5265, 5353, 8165, 8255, 8465, 8665, 15265, 95920'},
                {"label":"2 per sheet (4.75 x 7.75)", "value":2, description:'Avery # 6876'},
                {"label":"2 per sheet (5.5 x 8.5)", "value":3,
                    description:'Avery # 15516, 18126, 48126, 48226, 48326, 48330, 5126, 5526, 5783, 8126, 85726, 85783, 95930, 95900, 5912'},
                {"label":"4 per sheet (3.75 x 4.75)", "value": 4, description:'Avery # 6878'},
                {"label":"6 per sheet (3.33 x 4)", "value":5,
                    description:'Avery # 15664, 18664, 45464, 48264, 48464, 48864, 5164, 5264, 55164, 5524, 55264, 55364, ' +
                        '55464, 5664, 58164, 58264, 8164, 8254, 8464, 8564, 15264, 95940, 95905'},
                {"label":"8 per sheet (2 x 3.75)", "value":6, description:'Avery # 6873'},
                {"label":"10 per sheet (2 x 4)", "value":7, 
                    description:'Avery # 15163, 15563, 15663, 18163, 18663, 18863, 28663, 38363, 38863, 48163, 48263, 48363, ' +
                    '48463, 48863, 5163, 5263, 55163, 55263, 55363, 55463, 5663, 58163, 58263, 5963, 8163, 8253, 8363, 8463, ' +
                    '85563, 8563, 8663, 8923, 95945, 95910'}
            ],
            nonCommercials: ['Business'].concat([
                    'Airport', 'Church', 'Construction Site', 'Container Freight', 'Correction Facility', 'Military Base', 'Country Club',
                    'Storage Facility', 'Golf Course', 'Government Site', 'Hotel', 'Mine', 'Nursing Home', 'Piers', 'Ranch', 'School',
                    'Shopping Mall', 'Station', 'Farm', 'Refinery', 'Camps/Fairs/Carnivals/Flea Markets', 'Casinos', 'Cemeteries',
                    'Court houses', 'Day cares', 'Hotel/Motel/Resort', 'Medical/urgent care site without a dock',
                    'Nuclear power plant', 'Park', 'Restaurant/bars/night clubs'
                ].sort())
        };

        var disputeCostEnum = {
            FINANCE: "Finance Review",
            ACCOUNT_EXEC: "Account Exec",
            RESOLVED_NEW_VB: "Resolved - New VB"
        };

        var updateRevenueEnum = {
            MARGIN_PERCENT: "Margin %",
            MARGIN_VALUE: "Margin $",
            TOTAL_REVENUE_AMOUNT: "Total Revenue Amount",
            UPDATE_USING_COST_DIFF: "Update using Cost Difference",
            INVOICE_WITHOUT_MARKUP: "Invoice without markup"
        };

        var guaranteedTimeOptions = function (defaultOption) {
            var timeOptions = [];

            timeOptions.push(defaultOption);
            timeOptions.push(1000);
            timeOptions.push(1030);
            timeOptions.push(1200);
            timeOptions.push(1400);
            timeOptions.push(1500);
            timeOptions.push(1530);
            timeOptions.push(1700);

            return timeOptions;
        };

        ShipmentDictionaryService.getPackageTypes({}, function (data) {
            dictionary.packageTypes = data;
        });

        DictionaryService.getAllNotificationTypes({}, function (data) {
            dictionary.notificationTypes = data;
        });

        ShipmentDictionaryService.getBillToRequiredField({}, function (data) {
            dictionary.billToRequiredField = data;
        });

        function isCreditLimitValid(newLoad) {
            var totalShipperAmt = 0;

            // @TODO create or use existing service
            _.each(newLoad.selectedProposition.costDetailItems, function (item) {
                if (item.costDetailOwner === 'S') {
                    totalShipperAmt += item.subTotal;
                }
            });

            if ((newLoad.billTo.autoCreditHold && totalShipperAmt > newLoad.billTo.availableAmount) || newLoad.billTo.creditHold) {
                toastr.error('We are unable to successfully book this shipment. Please contact an Account Receivable representative for ' +
                        'assistance: ar@plslogistics.com', 'Unable to book shipment!');
                return false;
            } else {
                return true;
            }
        }

        var SHIPMENT_CONFIRMED_STATUSES = ['DISPATCHED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY', 'DELIVERED'];
        var PLS_FREIGHT_SOLUTIONS_NETWORK = 4;
        var AGENT_NETWORK = 6;

        function isStatusChanged(newLoad, oldLoad) {
            return _.indexOf(SHIPMENT_CONFIRMED_STATUSES, newLoad.status) !== -1 && _.indexOf(SHIPMENT_CONFIRMED_STATUSES, oldLoad.status) === -1;
        }

        function isBillToChanged(newLoad, oldLoad) {
            return !oldLoad.billTo || oldLoad.billTo.id !== newLoad.billTo.id;
        }

        function removeAllNotificationsByType(notifications, notificationType) {
            _.remove(notifications, function(notification) {
                return notification.notificationSource === notificationType;
            });
        }

        /**
         * Special adjustment reasons.
         */
        var specialAdjustments = {
            OUTDATED_REBILL_REASON: 6,
            WRONG_CARRIER_REASON: 8,
            REBILL_SHIPPER_REASON: 44,
            VOIDED_REASON: 34,
            S_N_BE_BILLED: 41,
            CARR_BILLED_CUST_DIRECT: 33,
            DUPLICATE_BILLING: 31,
            WRONG_SHIPPER: 13
        };
        var canceledAdjustments = [ specialAdjustments.VOIDED_REASON, specialAdjustments.S_N_BE_BILLED, specialAdjustments.CARR_BILLED_CUST_DIRECT,
            specialAdjustments.DUPLICATE_BILLING, specialAdjustments.WRONG_SHIPPER ];

        return {
            /**
             * Check if shipment can be cancelled by current user.
             *
             * @param shipment
             * @returns <code>true</code> if shipment can be cancelled by current user
             */
            isShipmentCancellable: function (shipment) {
                if (!shipment) {
                    return false;
                }

                var shipmentInvoiced = !_.isUndefined(shipment.invoiceDate);
                var noPermissionRequired = shipment.status === 'BOOKED' || shipment.status === 'DISPATCHED' || shipment.status === 'OPEN';

                return !shipmentInvoiced && (noPermissionRequired || $rootScope.isFieldRequired('CAN_CANCEL_ORDER'));
            },

            getDictionaryValues: function () {
                return dictionary;
            },

            getDisputeCostOptions: function (){
                return disputeCostEnum;
            },
            
            getUpdateRevenueOptions: function (){
                return updateRevenueEnum;
            },

            getAccessorialsNames: function (accessorials) {
                return _.map(accessorials, function (accessorial) {
                    return $filter('ref')(accessorial);
                }).join(', ');
            },

            getGuaranteedTimeOptions: function (defaultOption) {
                return guaranteedTimeOptions(defaultOption);
            },

            /**
             * Check if credit limit will be exceeded when saving newLoad.
             * If oldLoad parameter is passed, then credit limit will be checked for load as if it was edited.
             *
             * @returns <code>true</code> if credit limit will not be exceeded. Otherwise returns <code>false</code> and shows toast with warning.
             */
            isCreditLimitValid: function (newLoad, oldLoad) {
                if (angular.isDefined(oldLoad)) {
                    if ((isStatusChanged(newLoad, oldLoad) || isBillToChanged(newLoad, oldLoad)) && !isCreditLimitValid(newLoad)) {
                        return false;
                    }
                } else if (_.indexOf(SHIPMENT_CONFIRMED_STATUSES, newLoad.status) !== -1 && !isCreditLimitValid(newLoad)) {
                    return false;
                }

                return true;
            },

            isEmailAccountExecutive: function (customerNetworkId) {
                return PLS_FREIGHT_SOLUTIONS_NETWORK === customerNetworkId || AGENT_NETWORK === customerNetworkId;
            },

            /**
             * Maximum count of products in one load.
             */
            MAX_COUNT_OF_PRODUCTS: 100,

            fillNmfcAndQtyFields: function (pricingDetails, materials) {
                var matchedProduct;
                var commodityClass;
                _.each(pricingDetails.smc3CostDetails, function (detail) {
                   commodityClass = 'CLASS_' + detail.enteredNmfcClass.replace(".","_");
                   matchedProduct = _.findWhere(materials, {commodityClass: commodityClass, weight: parseFloat(detail.weight)});
                   if (matchedProduct) {
                       detail.nmfc = matchedProduct.nmfc;
                       detail.quantity = matchedProduct.quantity;
                   }
                });
            },

            OUTDATED_REBILL_REASON: specialAdjustments.OUTDATED_REBILL_REASON,
            REBILL_SHIPPER_REASON: specialAdjustments.REBILL_SHIPPER_REASON,
            isOutdatedRebillAdjustment: function (adjustment) {
                return specialAdjustments.OUTDATED_REBILL_REASON === adjustment.reason;
            },
            isRebillShipperAdjustment: function (adjustment) {
                return specialAdjustments.REBILL_SHIPPER_REASON === adjustment.reason;
            },
            isWrongCarrierAdjustment: function (adjustment) {
                return specialAdjustments.WRONG_CARRIER_REASON === adjustment.reason;
            },
            isCanceledAdjustment: function (adjustment) {
                return _.contains(canceledAdjustments, adjustment.reason);
            },

            removeAllNotificationsByType: function(notifications, notificationType) {
                removeAllNotificationsByType(notifications, notificationType);
            },

            addAddressNotificationsToLoadNotificationsWithoutDuplicates: function(notifications, notificationsToAdd, isOrigin) {

                var notificationSourceVal = isOrigin ? "ORIGIN_ADDRESS" : "DESTINATION_ADDRESS";
                removeAllNotificationsByType(notifications, notificationSourceVal);

                if(_.isEmpty(notificationsToAdd)) {
                    return;
                }

                var duplicateNotification;
                var originMatching;
                var destinationMatching;
                var bothMatching;
                _.each(notificationsToAdd, function(notification) {
                    duplicateNotification = _.findWhere(notifications, {emailAddress:notification, notificationType: notification.notificationType});
                    originMatching = isOrigin && notification.direction === 'ORIGIN';
                    destinationMatching = !isOrigin && notification.direction === 'DESTINATION';
                    bothMatching = notification.direction === 'BOTH';
                    if(!duplicateNotification && (originMatching || destinationMatching || bothMatching)) {
                        notifications.push({
                            emailAddress : notification.emailAddress,
                            notificationType : notification.notificationType,
                            notificationSource : notificationSourceVal
                        });
                    }
                });
            },

            showUnmatchCarrierWarning: function(data) {
                $rootScope.$emit('event:application-warning', "Carrier doesn't match!", 'The Carrier on the Vendor Bill '
                        + data.vendorBillCarrierName + ' , ' + data.vendorBillScac + ' does not match the carrier on the shipment '
                        + data.salesOrderCarrierName + ' , ' + data.salesOrderScac);
            }
        };
    }
]);