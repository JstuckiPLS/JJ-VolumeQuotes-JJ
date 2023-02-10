/**
 * PLS Identifiers factory specs
 *
 * @author Vitaliy Gavriliuk
 */
describe('PLS Identifiers Service -', function () {

    var defaultReqFields = [
        {
            name: 'BOL',
            inboundOutbound: 'B',
            address: {
                zip: '16066, 43210, 01010',
                city: 'NEW YORK, ODESSA',
                state: 'PA, NY, IL',
                country: 'USA, MEX'
            },
            originDestination: 'B'
        },
        {
            name: 'PU',
            inboundOutbound: 'B',
            address: {
                zip: '16066, 43210, 01010',
                city: 'NEW YORK, ODESSA',
                state: 'PA, NY, IL',
                country: 'USA, MEX'
            },
            originDestination: 'B'
        }
    ];

    var defaultShipment = {
        id: 123,
        bolNumber: 'qwerty123',
        cargoValue: 123456,
        billTo: {
            billToRequiredFields: defaultReqFields
        },
        originDetails: {
            address: {
                zip: {
                    country: {
                        id: ''
                    }
                }
            }
        },
        destinationDetails: {
            address: {
                zip: {
                    country: {
                        id: ''
                    }
                }
            }
        }
    };

    var shipment, reqFields;

    beforeEach(module('plsApp', 'plsApp.common.services'));
    beforeEach(function(){
        shipment = angular.copy(defaultShipment);
        reqFields = shipment.billTo.billToRequiredFields;
    });

    it('should check if Job# default value is Empty', inject(function (Identifiers) {
        c_expect(Identifiers.isEmptyDefaultValue({defaultValue: []}, 'JOB')).to.be.true();
        c_expect(Identifiers.isEmptyDefaultValue({defaultValue: [{jobNumber: 'qwerty123'}]}, 'JOB')).to.be.false();
    }));

    it('should check if identifier default value is Empty', inject(function (Identifiers) {
        c_expect(Identifiers.isEmptyDefaultValue({}, 'BOL')).to.be.true();
        c_expect(Identifiers.isEmptyDefaultValue({defaultValue: 'qwerty123'}, 'BOL')).to.be.false();
    }));

    describe('by ZIP.', function () {
        it('should get identifier rule by Both O/D, Both I/O and Origin Zip', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.zip = '43210'
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Both O/D, Inbound I/O and Dest Zip', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'I',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.shipmentDirection = 'I'
            shipment.destinationDetails.address.zip.zip = '01010'
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Origin O/D, Outbound I/O and Origin Zip', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'O',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.shipmentDirection = 'O'
            shipment.originDetails.address.zip.zip = '43210'
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Origin O/D, Both I/O and Origin Zip', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.originDetails.address.zip.zip = '43210'
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Both O/D, Both I/O and Origin Zip when other lower level rules exist', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010'
                },
                originDestination: 'B'
            }, {
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    city: 'CRANBERRY TWP, NEW YORK, ODESSA'
                },
                originDestination: 'B'
            }, {
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    state: 'PA, NY, IL'
                },
                originDestination: 'B'
            }, {
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.zip = '43210';
            shipment.originDetails.address.zip.city = 'CRANBERRY TWP';
            shipment.originDetails.address.zip.state = 'PA';
            shipment.originDetails.address.zip.country.id = 'USA';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 4]);
        }));

        it('should not get identifier rule by invalid Origin O/D, Both I/O and Destination Zip', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.destinationDetails.address.zip.zip = '43210'
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));

        it('should not get identifier rule by Origin O/D, Invalid Outbound I/O and Origin Zip', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'O',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.shipmentDirection = 'I'
            shipment.originDetails.address.zip.zip = '43210'
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));

        it('should not get identifier rule by Both O/D, Both I/O and Invalid Origin Zip', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.zip = 'I43210'
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));
    });

    describe('by City.', function () {
        it('should get identifier rule by Both O/D, Both I/O and Origin City', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.city = 'NEW YORK'
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Both O/D, Inbound I/O and Dest City', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'I',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.shipmentDirection = 'I'
            shipment.destinationDetails.address.zip.city = 'ODESSA';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Origin O/D, Outbound I/O and Origin City', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'O',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.shipmentDirection = 'O'
            shipment.originDetails.address.zip.city = 'NEW YORK';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Origin O/D, Both I/O and Origin City', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.originDetails.address.zip.city = 'NEW YORK';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Both O/D, Both I/O and Origin City when other lower level rules exist', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    city: 'CRANBERRY TWP, NEW YORK, ODESSA'
                },
                originDestination: 'B'
            }, {
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    state: 'PA, NY, IL'
                },
                originDestination: 'B'
            }, {
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.zip = '43210';
            shipment.originDetails.address.zip.city = 'CRANBERRY TWP';
            shipment.originDetails.address.zip.state = 'PA';
            shipment.originDetails.address.zip.country.id = 'USA';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 3]);
        }));

        it('should not get identifier rule by invalid Origin O/D, Both I/O and Destination City', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.destinationDetails.address.zip.city = 'ODESSA';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));

        it('should not get identifier rule by Origin O/D, Invalid Outbound I/O and Origin City', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'O',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.shipmentDirection = 'I'
            shipment.originDetails.address.zip.city = 'NEW YORK';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));

        it('should not get identifier rule by Both O/D, Both I/O and Invalid Origin City', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.city = 'PITTSBURGH';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));
    });

    describe('by State.', function () {
        it('should get identifier rule by Both O/D, Both I/O and Origin State', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.state = 'PA'
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Both O/D, Inbound I/O and Dest State', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'I',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.shipmentDirection = 'I'
            shipment.destinationDetails.address.zip.state = 'NY';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Origin O/D, Outbound I/O and Origin State', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'O',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.shipmentDirection = 'O'
            shipment.originDetails.address.zip.state = 'IL';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Origin O/D, Both I/O and Origin State', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.originDetails.address.zip.state = 'IL';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Both O/D, Both I/O and Origin State when other lower level rules exist', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    state: 'PA, NY, IL'
                },
                originDestination: 'B'
            }, {
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.zip = '43210';
            shipment.originDetails.address.zip.city = 'CRANBERRY TWP';
            shipment.originDetails.address.zip.state = 'PA';
            shipment.originDetails.address.zip.country.id = 'USA';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 2]);
        }));

        it('should not get identifier rule by invalid Origin O/D, Both I/O and Destination State', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.destinationDetails.address.zip.state = 'NY';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));

        it('should not get identifier rule by Origin O/D, Invalid Outbound I/O and Origin State', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'O',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.shipmentDirection = 'I'
            shipment.originDetails.address.zip.state = 'PA';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));

        it('should not get identifier rule by Both O/D, Both I/O and Invalid Origin State', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.state = 'OH';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));
    });

    describe('by Country.', function () {
        it('should get identifier rule by Both O/D, Both I/O and Origin Country', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.country.id = 'USA'
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Both O/D, Inbound I/O and Dest Country', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'I',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.shipmentDirection = 'I'
            shipment.destinationDetails.address.zip.country.id = 'MEX';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Origin O/D, Outbound I/O and Origin Country', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'O',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.shipmentDirection = 'O'
            shipment.originDetails.address.zip.country.id = 'USA';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should get identifier rule by Origin O/D, Both I/O and Origin Country', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.originDetails.address.zip.country.id = 'MEX';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
        }));

        it('should not get identifier rule by invalid Origin O/D, Both I/O and Destination Country', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.destinationDetails.address.zip.country.id = 'USA';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));

        it('should not get identifier rule by Origin O/D, Invalid Outbound I/O and Origin Country', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'O',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'O'
            });
            shipment.shipmentDirection = 'I'
            shipment.originDetails.address.zip.country.id = 'USA';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));

        it('should not get identifier rule by Both O/D, Both I/O and Invalid Origin Country', inject(function (Identifiers) {
            reqFields.push({
                name: 'PO',
                inboundOutbound: 'B',
                address: {
                    zip: '16066, 43210, 01010',
                    city: 'NEW YORK, ODESSA',
                    state: 'PA, NY, IL',
                    country: 'USA, MEX'
                },
                originDestination: 'B'
            });
            shipment.originDetails.address.zip.country.id = 'UKR';
            var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
            c_expect(idRule).to.eql({
                name: 'PO',
                required: false,
                inboundOutbound: 'B',
                originDestination: 'B',
                address: {}
            });
        }));
    });

    it('should get identifier rule by empty address rules', inject(function (Identifiers) {
        reqFields.push({
            name: 'PO',
            inboundOutbound: 'B'
        });
        var idRule = Identifiers.getIdentifierRule(shipment, 'PO');
        c_expect(idRule).to.eql(reqFields[reqFields.length - 1]);
    }));

    it('should get default identifier rule', inject(function (Identifiers) {
        var idRule = Identifiers.getIdentifierRule(shipment, 'PRO');
        c_expect(idRule).to.eql({
            name: 'PRO',
            required: true,
            inboundOutbound: 'B',
            originDestination: 'B',
            address: {}
        });
    }));
});