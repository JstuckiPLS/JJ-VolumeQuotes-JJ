xdescribe('Address autocomplete directive unit test', function() {
    var scope, element;
    var $httpBackend;

    beforeEach(module('plsApp'));
    beforeEach(module('pls.directives'));
    beforeEach(module('pages/tpl/address-autocomplete-template.html', 'pages/tpl/address-autocomplete-template.html'));
    beforeEach(inject(function($compile, $rootScope, _$httpBackend_) {
        scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;

        scope.selectedCustomer = undefined;

//        var linkFunction = $compile('<data-pls-address-autocomplete class="origin" data-num-items="15"'+ 
//                'data-ng-model="address" data-customer="selectedCustomer" data-address-header="Origin"'+
//                'data-on-address-update="updateOriginAddress(addressToUpdate);" data-on-address-create="addAddress();"'+
//                'data-on-address-edit="editAddress(addressToEdit);"></data-pls-address-autocomplete>');
//        scope = $rootScope.$new();
//        element = linkFunction(scope);
        element = angular.element('<data-pls-address-autocomplete class="origin" data-num-items="15"'+ 
                'data-ng-model="address" data-customer="selectedCustomer" data-address-header="Origin"'+
                'data-on-address-update="updateOriginAddress(addressToUpdate);" data-on-address-create="addAddress();"'+
                'data-on-address-edit="editAddress(addressToEdit);"></data-pls-address-autocomplete>');
        $compile(element)(scope);
        scope = element.scope();
        return scope.$apply();
    }));

    it ('verifies no lables containing address info rendered if customer not selected', function() {
        expect(element.find('h4').text()).toEqual('Origin');

        var lables = element.find('label');
        expect(lables.length).toBe(14);
        expect(lables.eq(0).text()).toEqual('Name:');
        expect(lables.eq(1).text()).toEqual('Code:');
        expect(lables.eq(2).text()).toEqual('Address 1:');
        expect(lables.eq(3).text()).toEqual('Address 2:');
        expect(lables.eq(4).text()).toEqual('City, State, Zip:');
        expect(lables.eq(5).text()).toEqual('Country:');
        expect(lables.eq(6).text()).toEqual('Contact Name:');
        expect(lables.eq(7).text()).toEqual('Phone:');
        expect(lables.eq(8).text()).toEqual('Fax:');
        expect(lables.eq(9).text()).toEqual('Email:');
        expect(lables.eq(10).text()).toEqual('Pickup Window:');
        expect(lables.eq(11).text()).toEqual('From:');
        expect(lables.eq(12).text()).toEqual('To:');
        expect(lables.eq(13).text()).toEqual('Pickup Notes:');
    });

    it ('verifies lables, describing address rendered once customer selected', function() {
        var lables = element.find('label');
        expect(lables.length).toBe(14);

        var originAddress = {
            addressName: 'D double O G',
            addressCode: 'Snoop',
            address1: 'finest',
            address2: 'luxurous one',
            zip: {
                city: 'MONTEREO',
                state: 'California',
                zip: '09113'
            },
            country: {
                id: 'USA'
            },
            contactName: 'JR Snooppie',
            phone: {
                countryCode: '1',
                areaCode: '123',
                number: '1234567'
            },
            fax: {
                countryCode: '1',
                areaCode: '123',
                number: '1234567'
            },
            email: 'snoop@gmail.com',
            pickupNotes: 'I\'m okay, I\'m on play, I love the bay just like I love LA '
        };
        $httpBackend.whenGET('/restful/customer/1/address/list').respond([originAddress]);
        scope.selectedCustomer = {id: 1};
        $httpBackend.flush();
//        scope.$digest();
        
        scope.address = 'D double O G';
        scope.$digest();

        lables = element.find('label');
        expect(lables.length).toBe(14);
    });
});