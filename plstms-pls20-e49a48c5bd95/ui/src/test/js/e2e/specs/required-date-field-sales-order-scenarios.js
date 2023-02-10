/**
 * This scenario checks required date fields depending on the selected shipment status. *
 * 
 * @author Brychak
 */
describe('Sales Order Required dates validation.', function() {
    beforeEach(function() {
        $injector = angular.injector([ 'PageObjectsModule' ]);
        EditDataSalesOrderCreateObject = $injector.get('EditDataSalesOrderCreateObject') ;
        loginLogoutPageObject = $injector.get('LoginLogoutPageObject');
    });
    it('should open Sales-order-create page', function() {
        browser().navigateTo('/my-freight/');
        loginLogoutPageObject.login(loginLogoutPageObject.plsUser);
        browser().navigateTo('#/sales-order/create');
        expect(browser().location().path()).toBe("/sales-order/create");
        EditDataSalesOrderCreateObject.cleanValueField();
    });
    it('should choose a shipment statuses BOOKED', function() {
        EditDataSalesOrderCreateObject.setStatus('Booked');
        expect(element(EditDataSalesOrderCreateObject.actualPickupDate).attr('required')).not().toBeDefined();
        expect(element(EditDataSalesOrderCreateObject.actualDeliveryDate).attr('required')).not().toBeDefined();

        expect(element(EditDataSalesOrderCreateObject.pickupDate).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid-required');

        expect(element(EditDataSalesOrderCreateObject.estimatedDelivery).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.estimatedDelivery)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.estimatedDelivery)).toHaveClass('ng-invalid-required');
    });
    it('should choose a shipment statuses DELIVERED', function() {
        EditDataSalesOrderCreateObject.setStatus('Delivered');
        expect(element(EditDataSalesOrderCreateObject.actualPickupDate).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.actualPickupDate)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.actualPickupDate)).toHaveClass('ng-invalid-required');

        expect(element(EditDataSalesOrderCreateObject.actualDeliveryDate).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.actualDeliveryDate)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.actualDeliveryDate)).toHaveClass('ng-invalid-required');

        expect(element(EditDataSalesOrderCreateObject.pickupDate).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid-required');

        expect(element(EditDataSalesOrderCreateObject.estimatedDelivery).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.estimatedDelivery)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.estimatedDelivery)).toHaveClass('ng-invalid-required');

    });
    it('should choose a shipment statuses DISPATCHED', function() {
        EditDataSalesOrderCreateObject.setStatus('Dispatched');
        expect(element(EditDataSalesOrderCreateObject.actualPickupDate).attr('required')).not().toBeDefined();
        expect(element(EditDataSalesOrderCreateObject.actualDeliveryDate).attr('required')).not().toBeDefined();

        expect(element(EditDataSalesOrderCreateObject.pickupDate).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid-required');

        expect(element(EditDataSalesOrderCreateObject.estimatedDelivery).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.estimatedDelivery)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.estimatedDelivery)).toHaveClass('ng-invalid-required');
    });
    it('should choose a shipment statuses IN_TRANSIT', function() {
        EditDataSalesOrderCreateObject.setStatus('In-Transit');
        expect(element(EditDataSalesOrderCreateObject.actualPickupDate).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.actualPickupDate)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.actualPickupDate)).toHaveClass('ng-invalid-required');

        expect(element(EditDataSalesOrderCreateObject.actualDeliveryDate).attr('required')).not().toBeDefined();

        expect(element(EditDataSalesOrderCreateObject.pickupDate).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid-required');

        expect(element(EditDataSalesOrderCreateObject.estimatedDelivery).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.estimatedDelivery)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.estimatedDelivery)).toHaveClass('ng-invalid-required');
    });
    it('should choose a shipment statuses OPEN', function() {
        EditDataSalesOrderCreateObject.setStatus('Open');
        expect(element(EditDataSalesOrderCreateObject.actualPickupDate).attr('required')).not().toBeDefined();
        expect(element(EditDataSalesOrderCreateObject.actualDeliveryDate).attr('required')).not().toBeDefined();

        expect(element(EditDataSalesOrderCreateObject.pickupDate).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid-required');

        expect(element(EditDataSalesOrderCreateObject.estimatedDelivery).attr('required')).toBe('required');
    });
    it('should choose a shipment statuses OUT_FOR_DELIVERY', function() {
        EditDataSalesOrderCreateObject.setStatus('Out for Delivery');

        expect(element(EditDataSalesOrderCreateObject.actualPickupDate).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.actualPickupDate)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.actualPickupDate)).toHaveClass('ng-invalid-required');

        expect(element(EditDataSalesOrderCreateObject.actualDeliveryDate).attr('required')).not().toBeDefined();

        expect(element(EditDataSalesOrderCreateObject.pickupDate).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.pickupDate)).toHaveClass('ng-invalid-required');

        expect(element(EditDataSalesOrderCreateObject.estimatedDelivery).attr('required')).toBe('required');
        expect(angularElement(EditDataSalesOrderCreateObject.estimatedDelivery)).toHaveClass('ng-invalid');
        expect(angularElement(EditDataSalesOrderCreateObject.estimatedDelivery)).toHaveClass('ng-invalid-required');

    });

    it('Should logout from application.', function() {
        loginLogoutPageObject.logout();
        expect(browser().location().path()).toBe("/");
    });

});