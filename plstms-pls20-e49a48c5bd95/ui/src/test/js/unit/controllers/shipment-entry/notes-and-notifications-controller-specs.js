describe('Notes and Notification controller unit test', function() {
    var scope = undefined;
    var controller = undefined;

    var userNotificationsService = {};

    beforeEach(module('plsApp'));
    beforeEach(module('shipmentEntry'));

    beforeEach(inject(function($rootScope, $controller, ShipmentUtils) {
        scope = $rootScope.$new();

        scope.shipmentEntryData = {};
        scope.shipmentEntryData.shipment = {};
        scope.shipmentEntryData.selectedCustomer = {
            id : 1,
            name: 'PLS SHIPPER'
        };

        controller = $controller('NotesAndNotificationController', {
            $scope: scope,
            ShipmentUtils: ShipmentUtils,
            UserNotificationsService: userNotificationsService
        });
    }));

    it ('should add new email notification', function() {
        scope.shipmentEntryData.shipment.finishOrder = {
            shipmentNotifications: []
        };
        scope.shipmentEntryData.editedEmail = 'first@mail.com';
        scope.shipmentEntryData.notificationTypes = [
            { label: 'Dispatched', selected: true, value: 'DISPATCHED' }
        ];
        expect(scope.shipmentEntryData.editedEmail).toBeDefined();

        scope.addEmailNotifications();

        expect(scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications).toBeDefined();
        expect(scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications[0].emailAddress).toEqual('first@mail.com');
        expect(scope.shipmentEntryData.shipment.finishOrder.shipmentNotifications[0].notificationType).toEqual('DISPATCHED');
        expect(scope.shipmentEntryData.editedEmail).toBeUndefined();
        expect(scope.isAnyNotificationSelected()).toBeFalsy();

    });

    it ('should remove email notifications', function() {
        scope.shipmentEntryData.shipment.finishOrder = {
            shipmentNotifications: [
                { emailAddress: 'second@mail.com', notificationType: 'DISPATCHED' }
            ]
        };
        scope.shipmentEntryData.editedEmail = 'second@mail.com';

        expect(scope.getEmails()).toBeDefined();
        expect(scope.getEmails().length).toEqual(1);

        scope.removeEmailNotifications();

        expect(scope.getEmails().length).toEqual(0);
        expect(scope.shipmentEntryData.editedEmail).toBeUndefined();
    });
});