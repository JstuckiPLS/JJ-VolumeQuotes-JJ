/**
 * Service to manipulate accessorials data.
 *
 * @author: Sergey Kirichenko
 * Date: 5/21/13
 * Time: 10:26 AM
 */
function PLSAccessorial(id, group, name) {
    this.id = id;
    this.group = group;
    this.name = name;
}

angular.module('plsApp.directives.services').factory('PLSAccessorialsService', function () {
    'use strict';

    var accessorialsMap = {};
    accessorialsMap.pickupResidential = new PLSAccessorial('REP', 'PICKUP', 'Residential');
    accessorialsMap.pickupLiftGate = new PLSAccessorial('LFC', 'PICKUP', 'Liftgate');
    accessorialsMap.pickupInside = new PLSAccessorial('IPU', 'PICKUP', 'Inside');
    accessorialsMap.pickupOverDimension = new PLSAccessorial('ODM', 'PICKUP', 'Over Dimension');
    accessorialsMap.pickupBlindBol = new PLSAccessorial('BLB', 'PICKUP', 'Blind BOL');
    accessorialsMap.pickupLimitedAccess = new PLSAccessorial('LAP', 'PICKUP', 'Limited Access');
    accessorialsMap.deliverResidential = new PLSAccessorial('RES', 'DELIVERY', 'Residential');
    accessorialsMap.deliverLiftGate = new PLSAccessorial('LFT', 'DELIVERY', 'Liftgate');
    accessorialsMap.deliverInside = new PLSAccessorial('IDL', 'DELIVERY', 'Inside');
    accessorialsMap.deliverSortSegregate = new PLSAccessorial('SSD', 'DELIVERY', 'Sort & Segregate');
    accessorialsMap.deliverNotify = new PLSAccessorial('NDR', 'DELIVERY', 'Appointment Required');
    accessorialsMap.deliverLimitedAccess = new PLSAccessorial('LAD', 'DELIVERY', 'Delivery Limited Access');

    return {
        isSet: function (key, accessorials) {
            if (key && angular.isArray(accessorials) && accessorialsMap[key]) {
                var ind = 0;
                var mask = accessorialsMap[key];

                for (; ind < accessorials.length; ind += 1) {
                    if (accessorials[ind].id === mask.id && accessorials[ind].group === mask.group) {
                        return true;
                    }
                }
            }

            return false;
        },
        set: function (key, value, accessorials) {
            if (value) {
                var ind = this.indexOf(accessorialsMap[key], accessorials);

                if (ind === -1) {
                    accessorials.push(accessorialsMap[key]);
                }
            } else {
                this.remove(key, accessorials);
            }
        },
        getNonCommercial: function (groupName, accessorials) {
            var result = null;
            var ind = 0;

            for (; ind < accessorials.length; ind += 1) {
                if (accessorials[ind].group === groupName) {
                    result = accessorials[ind];
                    break;
                }
            }

            return result;
        },
        getPickupNonCommercial: function (accessorials) {
            return this.getNonCommercial('PICKUP_NONCOMMERCIAL', accessorials);
        },
        getDeliveryNonCommercial: function (accessorials) {
            return this.getNonCommercial('DELIVERY_NONCOMMERCIAL', accessorials);
        },
        setNonCommercial: function (key, value, accessorials) {
            if (accessorials) {
                var oldAccessorials = accessorials.slice(0, accessorials.length);
                accessorials.splice(0, accessorials.length);

                angular.forEach(oldAccessorials, function (item) {
                    if (item.group !== key) {
                        accessorials.push(item);
                    }
                });

                if (value) {
                    accessorials.push(value);
                }
            }
        },
        setPickupNonCommercial: function (value, accessorials) {
            this.setNonCommercial('PICKUP_NONCOMMERCIAL', value, accessorials);
        },
        setDeliveryNonCommercial: function (value, accessorials) {
            this.setNonCommercial('DELIVERY_NONCOMMERCIAL', value, accessorials);
        },
        remove: function (key, accessorials) {
            if (key && angular.isArray(accessorials)) {
                var ind = this.indexOf(accessorialsMap[key], accessorials);

                if (ind !== -1) {
                    accessorials.splice(ind, 1);
                }
            }
        },
        indexOf: function (mask, accessorials) {
            var result = -1;
            var ind = 0;

            for (; ind < accessorials.length; ind += 1) {
                if (accessorials[ind].id === mask.id && accessorials[ind].group === mask.group) {
                    result = ind;
                    break;
                }
            }

            return result;
        }
    };
});