/**
 * PLS custom filters unit tests.
 *
 * @author Mikhail Boldinov
 */
describe('Custom Filters Test', function () {
    beforeEach(module('plsApp'));

    /**
     * Currency filter unit test.
     */
    it('should filter currency', inject(function (plsCurrencyFilter) {
        c_expect(plsCurrencyFilter(1)).to.equal('$1.00');
        c_expect(plsCurrencyFilter(123)).to.equal('$123.00');
        c_expect(plsCurrencyFilter(321.45)).to.equal('$321.45');
        c_expect(plsCurrencyFilter(4672.15)).to.equal('$4,672.15');
        c_expect(plsCurrencyFilter(31415926.54)).to.equal('$31,415,926.54');
        c_expect(plsCurrencyFilter('25')).to.equal('$25.00');
        c_expect(plsCurrencyFilter('432.49')).to.equal('$432.49');
        c_expect(plsCurrencyFilter('12345')).to.equal('$12,345.00');
        c_expect(plsCurrencyFilter('7421.22')).to.equal('$7,421.22');
        c_expect(plsCurrencyFilter(-562)).to.equal('-$562.00');
        c_expect(plsCurrencyFilter(-12.42)).to.equal('-$12.42');
        c_expect(plsCurrencyFilter(-7853.01)).to.equal('-$7,853.01');
        c_expect(plsCurrencyFilter('-54312857.12')).to.equal('-$54,312,857.12');

        c_expect(plsCurrencyFilter('1745,16')).to.be.empty();
        c_expect(plsCurrencyFilter('12..11')).to.be.empty();
        c_expect(plsCurrencyFilter(undefined)).to.be.empty();
        c_expect(plsCurrencyFilter('')).to.be.empty();
        c_expect(plsCurrencyFilter(true)).to.be.empty();
        c_expect(plsCurrencyFilter(false)).to.be.empty();
        c_expect(plsCurrencyFilter(new Date())).to.be.empty();
    }));

    /**
     * Phone filter unit test.
     */
    it('should filter phone', inject(function (phoneFilter) {
        c_expect(phoneFilter({countryCode: '1', areaCode: '328', number: '8080201'})).to.equal('+1(328)808 0201');
        c_expect(phoneFilter({areaCode: '045', number: '8798063'})).to.equal('(045)879 8063');
        c_expect(phoneFilter({number: '1731511'})).to.equal('1731511');
        c_expect(phoneFilter({countryCode: '', areaCode: '', number: ''})).to.be.empty();
        c_expect(phoneFilter({})).to.be.empty();
        c_expect(phoneFilter('+1(987)4658084')).to.equal('+1(987)4658084');
        c_expect(phoneFilter('')).to.be.empty();
    }));


    /**
     * Contact filter unit test.
     */
    it('should filter contact', inject(function (contactFilter) {
        c_expect(contactFilter({contactFirstName: 'Delfina', contactLastName: 'Belvins'})).to.equal('Delfina Belvins');
        c_expect(contactFilter({contactLastName: 'Kimura'})).to.equal('Kimura');
        c_expect(contactFilter({contactFirstName: 'Rosaline'})).to.equal('Rosaline');
        c_expect(contactFilter({contactFirstName: '', contactLastName: ''})).to.be.empty();
        c_expect(contactFilter({})).to.be.empty();
    }));


    /**
     * Zzip filter unit test.
     */
    it('should filter zip', inject(function (zipFilter) {
        c_expect(zipFilter({label: 'Some Label'})).to.equal('Some Label');
        c_expect(zipFilter({label: 'Another Label', city: 'Kharkov', state: 'KH', zip: '61000'})).to.equal('Another Label');
        c_expect(zipFilter({city: 'Kharkov', state: 'KH', zip: '61000'})).to.equal('Kharkov, KH, 61000');
        c_expect(zipFilter({city: 'Kharkov', state: 'KH', zip: '61000'})).to.not.equal('Kharkov,KH,61000');
        c_expect(zipFilter({state: 'NY', zip: '10001'})).to.equal('NY, 10001');
        c_expect(zipFilter({city: 'Detroit', state: 'MI'})).to.equal('Detroit, MI');
        c_expect(zipFilter({city: 'Seattle', zip: '98101'})).to.equal('Seattle, 98101');
        c_expect(zipFilter({zip: '99999'})).to.equal('99999');
        c_expect(zipFilter({})).to.be.empty();
    }));


    /**
     * Material dimension filter unit test.
     */
    it('should filter materialDimension', inject(function (materialDimensionFilter) {
        c_expect(materialDimensionFilter({length: '77', width: '37', height: '81', dimensionUnit: 'INCH'})).to.equal('77x37x81 Inch');
        c_expect(materialDimensionFilter({length: '60', width: '26', height: '47', dimensionUnit: 'CMM'})).to.equal('60x26x47 Cm');
        c_expect(materialDimensionFilter({length: '28', width: '62', height: '25'})).to.equal('28x62x25 Inch');
        c_expect(materialDimensionFilter({length: '90', width: '86'})).to.be.empty();
        c_expect(materialDimensionFilter({length: '81', height: '64'})).to.be.empty();
        c_expect(materialDimensionFilter({width: '67', height: '40'})).to.be.empty();
        c_expect(materialDimensionFilter({length: '92', width: '30', dimensionUnit: 'CMM'})).to.be.empty();
        c_expect(materialDimensionFilter({})).to.be.empty();
    }));


    /**
     * Material weight filter unit test.
     */
    it('should filter materialWeight', inject(function (materialWeightFilter) {
        c_expect(materialWeightFilter({weight: '87', weightUnit: 'LBS'})).to.equal('87 Lbs');
        c_expect(materialWeightFilter({weight: '42', weightUnit: 'KG'})).to.equal('42 Kg');
        c_expect(materialWeightFilter({weight: '13'})).to.equal('13 Lbs');
        c_expect(materialWeightFilter({weightUnit: 'KG'})).to.be.empty();
        c_expect(materialWeightFilter({})).to.be.empty();
    }));

    /**
     * Material product filter unit test.
     */
    it('should filter materialProduct', inject(function (materialProductFilter) {
        c_expect(materialProductFilter({productDescription: 'cattle', productCode: 'DD-GNAN'}, 'PRODUCT_DESCRIPTION')).to.equal('cattle DD-GNAN');
        c_expect(materialProductFilter({
            productDescription: 'size A4 paper',
            productCode: 'SK-WRDM'
        }, 'PRODUCT_CODE')).to.equal('SK-WRDM size A4 paper');
        c_expect(materialProductFilter({
            productDescription: 'water coolers + heaters',
            productCode: 'MN-JXAU'
        })).to.equal('MN-JXAU water coolers + heaters');
        c_expect(materialProductFilter({productDescription: 'lupini beans', productCode: 'FK-CLSH'}, 'UNDEFINED')).to.equal('FK-CLSH lupini beans');
        c_expect(materialProductFilter({productDescription: 'fruit'})).to.equal('fruit');
        c_expect(materialProductFilter({productCode: 'JQ-TGNI'})).to.equal('JQ-TGNI');
        c_expect(materialProductFilter({productDescription: 'biscuits and sweets'}, 'PRODUCT_DESCRIPTION')).to.equal(materialProductFilter({productDescription: 'biscuits and sweets'}, 'PRODUCT_CODE'));
        c_expect(materialProductFilter({productCode: 'MI-NAVV'}, 'PRODUCT_CODE')).to.equal(materialProductFilter({productCode: 'MI-NAVV'}, 'PRODUCT_DESCRIPTION'));
        c_expect(materialProductFilter({})).to.be.empty();
    }));

    /**
     * Commodity Class filter unit test.
     */
    it('should filter commodityClass', inject(function (commodityClassFilter) {
        c_expect(commodityClassFilter('CLASS_50')).to.equal('50');
        c_expect(commodityClassFilter('CLASS_55')).to.equal('55');
        c_expect(commodityClassFilter('CLASS_60')).to.equal('60');
        c_expect(commodityClassFilter('CLASS_65')).to.equal('65');
        c_expect(commodityClassFilter('CLASS_70')).to.equal('70');
        c_expect(commodityClassFilter('CLASS_77_5')).to.equal('77.5');
        c_expect(commodityClassFilter('CLASS_85')).to.equal('85');
        c_expect(commodityClassFilter('CLASS_92_5')).to.equal('92.5');
        c_expect(commodityClassFilter('CLASS_100')).to.equal('100');
        c_expect(commodityClassFilter('CLASS_110')).to.equal('110');
        c_expect(commodityClassFilter('CLASS_125')).to.equal('125');
        c_expect(commodityClassFilter('CLASS_150')).to.equal('150');
        c_expect(commodityClassFilter('CLASS_175')).to.equal('175');
        c_expect(commodityClassFilter('CLASS_200')).to.equal('200');
        c_expect(commodityClassFilter('CLASS_250')).to.equal('250');
        c_expect(commodityClassFilter('CLASS_300')).to.equal('300');
        c_expect(commodityClassFilter('CLASS_400')).to.equal('400');
        c_expect(commodityClassFilter('CLASS_500')).to.equal('500');
        c_expect(commodityClassFilter('FOOBAR')).to.equal('FOOBAR');
        c_expect(commodityClassFilter('')).to.be.empty();
    }));

    /**
     * Shipment Status filter unit test.
     */
    it('should filter shipmentStatus', inject(function (shipmentStatusFilter) {
        c_expect(shipmentStatusFilter('OPEN')).to.equal('Open');
        c_expect(shipmentStatusFilter('BOOKED')).to.equal('Booked');
        c_expect(shipmentStatusFilter('DISPATCHED')).to.equal('Dispatched');
        c_expect(shipmentStatusFilter('IN_TRANSIT')).to.equal('In-Transit');
        c_expect(shipmentStatusFilter('DELIVERED')).to.equal('Delivered');
        c_expect(shipmentStatusFilter('CANCELLED')).to.equal('Cancelled');
        c_expect(shipmentStatusFilter('OUT_FOR_DELIVERY')).to.equal('Out for Delivery');
        c_expect(shipmentStatusFilter('UNKNOWN')).to.equal('Unknown');
        c_expect(shipmentStatusFilter('NOTVALID')).to.be.empty();
        c_expect(shipmentStatusFilter('')).to.be.empty();
    }));

    /**
     * Pickup Window time filter unit test.
     */
    it('should filter pickupWindowTime', inject(function (pickupWindowTimeFilter) {
        for (var hours = 0; hours < 12; hours++) {
            for (var minutes = 0; minutes < 60; minutes++) {
                c_expect(pickupWindowTimeFilter({hours: hours, minutes: minutes, am: true})).to.match(/\d\d:\d\d AM/);
                c_expect(pickupWindowTimeFilter({hours: hours, minutes: minutes, am: false})).to.match(/\d\d:\d\d PM/);
            }
        }
        c_expect(pickupWindowTimeFilter({hours: 0, minutes: 0, am: true})).to.equal('12:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 0, minutes: 30, am: true})).to.equal('12:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 1, minutes: 0, am: true})).to.equal('01:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 1, minutes: 30, am: true})).to.equal('01:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 2, minutes: 0, am: true})).to.equal('02:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 2, minutes: 30, am: true})).to.equal('02:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 3, minutes: 0, am: true})).to.equal('03:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 3, minutes: 30, am: true})).to.equal('03:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 4, minutes: 0, am: true})).to.equal('04:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 4, minutes: 30, am: true})).to.equal('04:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 5, minutes: 0, am: true})).to.equal('05:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 5, minutes: 30, am: true})).to.equal('05:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 6, minutes: 0, am: true})).to.equal('06:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 6, minutes: 30, am: true})).to.equal('06:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 7, minutes: 0, am: true})).to.equal('07:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 7, minutes: 30, am: true})).to.equal('07:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 8, minutes: 0, am: true})).to.equal('08:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 8, minutes: 30, am: true})).to.equal('08:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 9, minutes: 0, am: true})).to.equal('09:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 9, minutes: 30, am: true})).to.equal('09:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 10, minutes: 0, am: true})).to.equal('10:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 10, minutes: 30, am: true})).to.equal('10:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 11, minutes: 0, am: true})).to.equal('11:00 AM');
        c_expect(pickupWindowTimeFilter({hours: 11, minutes: 30, am: true})).to.equal('11:30 AM');
        c_expect(pickupWindowTimeFilter({hours: 0, minutes: 0, am: false})).to.equal('12:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 0, minutes: 30, am: false})).to.equal('12:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 1, minutes: 0, am: false})).to.equal('01:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 1, minutes: 30, am: false})).to.equal('01:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 2, minutes: 0, am: false})).to.equal('02:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 2, minutes: 30, am: false})).to.equal('02:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 3, minutes: 0, am: false})).to.equal('03:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 3, minutes: 30, am: false})).to.equal('03:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 4, minutes: 0, am: false})).to.equal('04:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 4, minutes: 30, am: false})).to.equal('04:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 5, minutes: 0, am: false})).to.equal('05:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 5, minutes: 30, am: false})).to.equal('05:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 6, minutes: 0, am: false})).to.equal('06:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 6, minutes: 30, am: false})).to.equal('06:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 7, minutes: 0, am: false})).to.equal('07:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 7, minutes: 30, am: false})).to.equal('07:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 8, minutes: 0, am: false})).to.equal('08:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 8, minutes: 30, am: false})).to.equal('08:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 9, minutes: 0, am: false})).to.equal('09:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 9, minutes: 30, am: false})).to.equal('09:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 10, minutes: 0, am: false})).to.equal('10:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 10, minutes: 30, am: false})).to.equal('10:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 11, minutes: 0, am: false})).to.equal('11:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 11, minutes: 30, am: false})).to.equal('11:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 1, minutes: 30})).to.equal('01:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 8, minutes: 0, am: undefined})).to.equal('08:00 PM');
        c_expect(pickupWindowTimeFilter({hours: 4})).to.equal('04:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 3, minutes: 20})).to.equal('03:30 PM');
        c_expect(pickupWindowTimeFilter({hours: 11, minutes: 5, am: true})).to.equal('11:30 AM');
    }));

    /**
     * Emergency phone filter unit test.
     */
    it('should filter emergencyPhone', inject(function (emergencyPhoneFilter) {
        c_expect(emergencyPhoneFilter({
            emergencyResponsePhoneCountryCode: '7',
            emergencyResponsePhoneAreaCode: '706',
            emergencyResponsePhone: '4495798'
        })).to.equal('+7(706)449 5798');
        c_expect(emergencyPhoneFilter({emergencyResponsePhoneAreaCode: '248', emergencyResponsePhone: '1996711'})).to.equal('(248)199 6711');
        c_expect(emergencyPhoneFilter({emergencyResponsePhone: '6715317'})).to.equal('6715317');
        c_expect(emergencyPhoneFilter({
            emergencyResponsePhoneCountryCode: '',
            emergencyResponsePhoneAreaCode: '',
            emergencyResponsePhone: ''
        })).to.be.empty();
        c_expect(emergencyPhoneFilter({})).to.be.empty();
    }));


    /**
     * Dimension Measure filter unit test.
     */
    it('should filter dimensionsMeasure', inject(function (dimensionsMeasureFilter) {
        c_expect(dimensionsMeasureFilter('INCH')).to.equal('Inch');
        c_expect(dimensionsMeasureFilter('CMM')).to.equal('Cm');
        c_expect(dimensionsMeasureFilter('FT')).to.equal('Ft');
        c_expect(dimensionsMeasureFilter('M')).to.equal('M');
        c_expect(dimensionsMeasureFilter('OTHER')).to.be.undefined();
    }));


    /**
     * Dimension Measure filter unit test.
     */
    it('should filter weightMeasure', inject(function (weightMeasureFilter) {
        c_expect(weightMeasureFilter('LBS')).to.equal('Lbs');
        c_expect(weightMeasureFilter('KG')).to.equal('Kg');
        c_expect(weightMeasureFilter('G')).to.equal('g');
        c_expect(weightMeasureFilter('OZ')).to.equal('Oz');
        c_expect(weightMeasureFilter('OTHER')).to.be.undefined();
    }));


    /**
     * Pickup window diapason filter unit test.
     */
    it('should filter pickupWindowDiapason', inject(function (pickupWindowDiapasonFilter) {

        for (var hoursFrom = 0; hoursFrom < 12; hoursFrom++) {
            for (var hoursTo = 0; hoursTo < 12; hoursTo++) {
                for (var minutesFrom = 0; minutesFrom < 60; minutesFrom = minutesFrom + 30) {
                    for (var minutesTo = 0; minutesTo < 60; minutesTo = minutesTo + 30) {
                        c_expect(pickupWindowDiapasonFilter({
                            pickupWindowFrom: {hours: hoursFrom, minutes: minutesFrom, am: true},
                            pickupWindowTo: {hours: hoursTo, minutes: minutesTo, am: false}
                        })).to.match(/From \d\d:\d\d AM to \d\d:\d\d PM/);
                        c_expect(pickupWindowDiapasonFilter({
                            pickupWindowFrom: {hours: hoursFrom, minutes: minutesFrom, am: false},
                            pickupWindowTo: {hours: hoursTo, minutes: minutesTo, am: true}
                        })).to.match(/From \d\d:\d\d PM to \d\d:\d\d AM/);
                        c_expect(pickupWindowDiapasonFilter({
                            pickupWindowFrom: {hours: hoursFrom, minutes: minutesFrom, am: true}
                        })).to.match(/From \d\d:\d\d AM/);
                        c_expect(pickupWindowDiapasonFilter({
                            pickupWindowTo: {hours: hoursTo, minutes: minutesTo, am: false}
                        })).to.match(/ to \d\d:\d\d PM/);
                    }
                }
            }
        }
        c_expect(pickupWindowDiapasonFilter({})).to.be.empty();
    }));


    /**
     * Long Time filter for Guaranteed By unit test.
     */
    it('should filter longTime', inject(function (longTimeFilter) {
        c_expect(longTimeFilter(0)).to.equal('12:00 AM');
        c_expect(longTimeFilter(30)).to.equal('12:30 AM');
        c_expect(longTimeFilter(100)).to.equal('1:00 AM');
        c_expect(longTimeFilter(130)).to.equal('1:30 AM');
        c_expect(longTimeFilter(200)).to.equal('2:00 AM');
        c_expect(longTimeFilter(230)).to.equal('2:30 AM');
        c_expect(longTimeFilter(300)).to.equal('3:00 AM');
        c_expect(longTimeFilter(330)).to.equal('3:30 AM');
        c_expect(longTimeFilter(400)).to.equal('4:00 AM');
        c_expect(longTimeFilter(430)).to.equal('4:30 AM');
        c_expect(longTimeFilter(500)).to.equal('5:00 AM');
        c_expect(longTimeFilter(530)).to.equal('5:30 AM');
        c_expect(longTimeFilter(600)).to.equal('6:00 AM');
        c_expect(longTimeFilter(630)).to.equal('6:30 AM');
        c_expect(longTimeFilter(700)).to.equal('7:00 AM');
        c_expect(longTimeFilter(730)).to.equal('7:30 AM');
        c_expect(longTimeFilter(800)).to.equal('8:00 AM');
        c_expect(longTimeFilter(830)).to.equal('8:30 AM');
        c_expect(longTimeFilter(900)).to.equal('9:00 AM');
        c_expect(longTimeFilter(930)).to.equal('9:30 AM');
        c_expect(longTimeFilter(1000)).to.equal('10:00 AM');
        c_expect(longTimeFilter(1030)).to.equal('10:30 AM');
        c_expect(longTimeFilter(1100)).to.equal('11:00 AM');
        c_expect(longTimeFilter(1130)).to.equal('11:30 AM');
        c_expect(longTimeFilter(1200)).to.equal('12:00 PM');
        c_expect(longTimeFilter(1230)).to.equal('12:30 PM');
        c_expect(longTimeFilter(1300)).to.equal('1:00 PM');
        c_expect(longTimeFilter(1330)).to.equal('1:30 PM');
        c_expect(longTimeFilter(1400)).to.equal('2:00 PM');
        c_expect(longTimeFilter(1430)).to.equal('2:30 PM');
        c_expect(longTimeFilter(1500)).to.equal('3:00 PM');
        c_expect(longTimeFilter(1530)).to.equal('3:30 PM');
        c_expect(longTimeFilter(1600)).to.equal('4:00 PM');
        c_expect(longTimeFilter(1630)).to.equal('4:30 PM');
        c_expect(longTimeFilter(1700)).to.equal('5:00 PM');
        c_expect(longTimeFilter(1730)).to.equal('5:30 PM');
        c_expect(longTimeFilter(1800)).to.equal('6:00 PM');
        c_expect(longTimeFilter(1830)).to.equal('6:30 PM');
        c_expect(longTimeFilter(1900)).to.equal('7:00 PM');
        c_expect(longTimeFilter(1930)).to.equal('7:30 PM');
        c_expect(longTimeFilter(2000)).to.equal('8:00 PM');
        c_expect(longTimeFilter(2030)).to.equal('8:30 PM');
        c_expect(longTimeFilter(2100)).to.equal('9:00 PM');
        c_expect(longTimeFilter(2130)).to.equal('9:30 PM');
        c_expect(longTimeFilter(2200)).to.equal('10:00 PM');
        c_expect(longTimeFilter(2230)).to.equal('10:30 PM');
        c_expect(longTimeFilter(2300)).to.equal('11:00 PM');
        c_expect(longTimeFilter(2330)).to.equal('11:30 PM');
        c_expect(longTimeFilter(2400)).to.equal('EOD');
        c_expect(longTimeFilter(2400, false)).to.equal('12:00 PM');
        c_expect(longTimeFilter(undefined)).to.equal('None');
        c_expect(longTimeFilter(null)).to.equal('12:00 AM');
        c_expect(longTimeFilter('')).to.equal('12:00 AM');
    }));


    /**
     * Minutes time filter for Transit estimated time unit test.
     */
    it('should filter minutesTime', inject(function (minutesTimeFilter) {
        for (var i = 1; i < 10000; i = i + 3) {
            var days = Math.floor(i / 1440);
            var hours = Math.floor((i % 1440) / 60);
            var minutes = (i % 1440) % 60;
            if (days == 1) {
                c_expect(minutesTimeFilter(i)).to.match(/1 day/);
            }
            if (days > 1) {
                c_expect(minutesTimeFilter(i)).to.match(new RegExp(days + ' days'));
            }
            if (hours === 1) {
                c_expect(minutesTimeFilter(i)).to.match(/1 hour/);
            }
            if (hours > 1) {
                c_expect(minutesTimeFilter(i)).to.match(new RegExp(hours + ' hours'));
            }
            if (minutes == 1) {
                c_expect(minutesTimeFilter(i)).to.match(/1 minute/);
            }
            if (minutes > 1) {
                c_expect(minutesTimeFilter(i)).to.match(new RegExp(minutes + ' minutes'));
            }
            c_expect(minutesTimeFilter(i, true)).to.equal((days !== 0 ? days + ' ' : '') + (hours !== 0 ? hours + ' ' : '') + (minutes !== 0 ? minutes : ''));
        }
    }));


    /**
     * Customer Status filter unit test.
     */
    it('should filter customerStatus', inject(function (customerStatusFilter) {
        c_expect(customerStatusFilter('ACTIVE')).to.equal('Active');
        c_expect(customerStatusFilter('INACTIVE')).to.equal('Inactive');
        c_expect(customerStatusFilter('HOLD')).to.equal('Hold');
        c_expect(customerStatusFilter('')).to.be.empty();
    }));


    /**
     * Customer Status Reason filter unit test.
     */
    it('should filter statusReason', inject(function (statusReasonFilter) {
        c_expect(statusReasonFilter('CUSTOMER_REQUEST')).to.equal('Customer Request');
        c_expect(statusReasonFilter('NO_ACTIVITY')).to.equal('No activity');
        c_expect(statusReasonFilter('OUT_OF_BUSINESS')).to.equal('No longer in business');
        c_expect(statusReasonFilter('ACTIVITY_REQUESTED')).to.equal('Activity/Operations Request');
        c_expect(statusReasonFilter('ENROLLMENT_ACCEPTED')).to.equal('Enrollment Application Accepted');
        c_expect(statusReasonFilter('CREDIT_HOLD')).to.equal('Credit Hold');
        c_expect(statusReasonFilter('')).to.be.empty();
    }));

    /**
     * NMFC filter unit test.
     */
    it('should filter nmfc', inject(function (nmfcFilter) {
        c_expect(nmfcFilter({nmfc: '123', nmfcSubNum: '456'})).to.equal('123-456');
        c_expect(nmfcFilter({nmfc: '123', nmfcSubNum: ''})).to.equal('123');
        c_expect(nmfcFilter({nmfc: '', nmfcSubNum: '456'})).to.equal('456');
        c_expect(nmfcFilter({})).to.be.empty();
        c_expect(nmfcFilter('')).to.be.empty();
        c_expect(nmfcFilter({prop1: 'val1'})).to.be.empty();
        c_expect(nmfcFilter({nmfc: null, nmfcSubNum: null})).to.be.empty();
        c_expect(nmfcFilter({nmfc: undefined, nmfcSubNum: undefined})).to.be.empty();
    }));

    /**
     * Shipment Payment Terms filter unit test.
     */
    it('should filter Payment terms', inject(function (paymentTermsFilter) {
        c_expect(paymentTermsFilter('COLLECT')).to.equal('Collect');
        c_expect(paymentTermsFilter('PREPAID')).to.equal('Prepaid');
        c_expect(paymentTermsFilter('THIRD_PARTY_COLLECT')).to.equal('Third Party Collect');
        c_expect(paymentTermsFilter('THIRD_PARTY_PREPAID')).to.equal('Third Party Prepaid');
        c_expect(paymentTermsFilter('')).to.be.empty();
    }));

    /**
     * Shipment Direction filter unit test.
     */
    it('should filter Shipment direction', inject(function (shipmentDirectionFilter) {
        c_expect(shipmentDirectionFilter('I')).to.equal('Inbound');
        c_expect(shipmentDirectionFilter('O')).to.equal('Outbound');
        c_expect(shipmentDirectionFilter('')).to.be.empty();
    }));

    /**
     * Invoice Type filter unit test.
     */
    it('should filter Invoice type', inject(function (invoiceTypeFilter) {
        c_expect(invoiceTypeFilter('TRANSACTIONAL')).to.equal('Transactional');
        c_expect(invoiceTypeFilter('CBI')).to.equal('CBI');
        c_expect(invoiceTypeFilter('')).to.be.empty();
    }));

    /**
     * Invoice Processing data filter unit test.
     */
    it('should filter Invoice Processing data', inject(function (processingInfoFilter) {
        c_expect(processingInfoFilter({
            processingType: 'AUTOMATIC',
            processingPeriod: 'DAILY',
            processingDayOfWeek: 'Monday',
            processingTimeInMinutes: 540,
            processingTimezone: {
                code: 'EST',
                localOffset: 0
            }
        }, true)).to.equal('Daily, @9:00 AM EST');

        c_expect(processingInfoFilter({
            processingType: 'AUTOMATIC',
            processingPeriod: 'WEEKLY',
            processingDayOfWeek: 'Tuesday',
            processingTimeInMinutes: 900,
            processingTimezone: {
                code: 'CST',
                localOffset: -1
            }
        }, true)).to.equal('Weekly, Tuesday');

        c_expect(processingInfoFilter({
            processingType: 'MANUAL',
            processingPeriod: 'DAILY',
            processingDayOfWeek: 'Friday',
            processingTimeInMinutes: 900,
            processingTimezone: {
                code: 'SST',
                localOffset: -6
            }
        }, false)).to.equal('Daily, @3:00 PM SST');

        c_expect(processingInfoFilter({
            processingType: 'MANUAL',
            processingPeriod: 'DAILY',
            processingDayOfWeek: 'Thursday',
            processingTimeInMinutes: 540,
            processingTimezone: {
                code: 'EST',
                localOffset: 0
            }
        }, true)).to.be.empty();
    }));

    /**
     * Shipment Event filter unit test.
     */
    it('should filter Shipment Event', inject(function (shipmentEventFilter) {
        c_expect(shipmentEventFilter({
            event: 'Shipment Damaged',
            city: 'RICHFIELD',
            stateCode: 'OH',
            carrierName: 'FEDEX FREIGHT'
        })).to.equal('(FEDEX FREIGHT) RICHFIELD, OH - Shipment Damaged');
        c_expect(shipmentEventFilter({
            event: 'Estimated Delivery',
            carrierName: 'UPS FREIGHT'
        })).to.equal('(UPS FREIGHT) - Estimated Delivery');
        c_expect(shipmentEventFilter({
            event: 'Shipment Canceled',
            city: 'HOUSTON',
            stateCode: 'TX'
        })).to.equal('HOUSTON, TX - Shipment Canceled');
        c_expect(shipmentEventFilter({
            event: 'Loading'
        })).to.equal('Loading');
    }));

    /**
     * Bill To Identifier Names filter unit test.
     */
    it('should filter Bill To Required Fields Identifier Names', inject(function (billToIdentifierNamesFilter) {
        c_expect(billToIdentifierNamesFilter('SR')).to.equal('Shipper Ref');
        c_expect(billToIdentifierNamesFilter('PRO')).to.equal('Pro #');
        c_expect(billToIdentifierNamesFilter('TR')).to.equal('Trailer');
        c_expect(billToIdentifierNamesFilter('JOB')).to.equal('Job#');
        c_expect(billToIdentifierNamesFilter('CARGO')).to.equal('Cargo Value');
        c_expect(billToIdentifierNamesFilter('RB')).to.equal('Requested By');

        c_expect(billToIdentifierNamesFilter('BOL')).to.equal('BOL');
        c_expect(billToIdentifierNamesFilter('PO')).to.equal('PO');
        c_expect(billToIdentifierNamesFilter('PU')).to.equal('PU');
        c_expect(billToIdentifierNamesFilter('SO')).to.equal('SO');
        c_expect(billToIdentifierNamesFilter('GL')).to.equal('GL');

        c_expect(billToIdentifierNamesFilter(undefined)).to.be.empty();
    }));

    /**
     * Bill To Required Fields Address filter unit test.
     */
    it('should filter Bill To Required Fields Addresses', inject(function (billToReqFieldsAddressFilter) {
        c_expect(billToReqFieldsAddressFilter({
            zip: '01010, 43210',
            city: "NEW YORK, WASHINGTON"
        })).to.equal('01010, 43210, NEW YORK, WASHINGTON');

        c_expect(billToReqFieldsAddressFilter({
            zip: '01010, 43210',
            country: 'USA'
        })).to.equal('01010, 43210, USA');

        c_expect(billToReqFieldsAddressFilter({
            zip: '01010, 43210',
            city: "NEW YORK, WASHINGTON",
            state: "MI",
            country: 'USA'
        })).to.equal('01010, 43210, NEW YORK, WASHINGTON, MI, USA');

        c_expect(billToReqFieldsAddressFilter(undefined)).to.be.empty();
    }));

    /**
     * Bill To Identifier Names filter unit test.
     */
    it('should filter Bill To Required Fields Rules', inject(function (billToReqFieldsRulesFilter) {
        c_expect(billToReqFieldsRulesFilter(undefined)).to.be.empty();

        c_expect(billToReqFieldsRulesFilter({
            startWith: '123',
            endWith: 'abc'
        })).to.equal('Start with: 123 | End with: abc');

        c_expect(billToReqFieldsRulesFilter({
            startWith: '123 abc'
        })).to.equal('Start with: 123 abc');

        c_expect(billToReqFieldsRulesFilter({
            endWith: 'abc 123'
        })).to.equal('End with: abc 123');
    }));

    describe('. Package Type filter test', function () {
        beforeEach(module('plsApp', function ($provide) {
            $provide.factory('ShipmentUtils', function () {
                return {
                    getDictionaryValues: function () {
                        return {
                            packageTypes: [{code: "BOX", label: "Boxes"}, {code: "ENV", label: "Envelopes"}, {code: "PLT", label: "Pallet"}]
                        };
                    }
                };
            });
        }));

        it('Should filter package type', inject(function (packageTypeFilter) {
            c_expect(packageTypeFilter('BOX')).to.equal('Boxes');
            c_expect(packageTypeFilter('ENV')).to.equal('Envelopes');
            c_expect(packageTypeFilter('PLT')).to.equal('Pallet');
            c_expect(packageTypeFilter('NON_EXISTED')).to.be.empty();
        }))
    });
});