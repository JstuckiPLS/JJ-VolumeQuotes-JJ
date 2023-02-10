/**
 * UI unit tests for SelectCarrierCtrl.
 * @author: Alexander Kirichenko
 * Date: 9/26/13
 * Time: 2:39 PM
 */
describe('SelectCarrierCtrl tests', function () {
    var scope, controller;

    var mockProposals = [
        {"guid": "2b3cfca5-905a-4a73-8f69-e5d807694847", "estimatedTransitDate": "2013-09-27T00:00:00.000", "estimatedTransitTime": 1441,
            "serviceType": "DIRECT", "carrier": {"id": 15, "scac": "DAFG", "name": "DAYTON FREIGHT LINES",
            "logoPath": "resources/img/carrier-logo/dayton-freight-logo.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA",
                "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -192.18, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 13.53, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "7d4937d3-2d49-43bd-9abf-e9ad198dce76", "estimatedTransitDate": "2013-09-27T00:00:00.000", "estimatedTransitTime": 1440,
            "serviceType": "DIRECT", "carrier": {"id": 25, "scac": "RLCA", "name": "R & L CARRIERS INC",
            "logoPath": "resources/img/carrier-logo/R-L-carriers-Logo.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -199.55, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 8.79, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "51587ce5-94c3-4172-b773-5bd7336b0685", "estimatedTransitDate": "2013-09-27T00:00:00.000", "estimatedTransitTime": 1442,
            "serviceType": "DIRECT", "carrier": {"id": 14, "scac": "CNWY", "name": "CON-WAY FREIGHT",
            "logoPath": "resources/img/carrier-logo/con-way-logo.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -177.95, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 11.00, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "7a651a01-8ca8-4a6b-8ba9-9e26dc1dce80", "estimatedTransitDate": "2013-09-27T00:00:00.000", "estimatedTransitTime": 1443,
            "serviceType": "DIRECT", "carrier": {"id": 17, "scac": "FXFE", "name": "FEDEX FREIGHT PRIORITY",
            "logoPath": "resources/img/carrier-logo/FedExF_Logo1.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -180.24, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 12.43, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "9a75be19-3c46-4939-b356-b5c90abb4895", "estimatedTransitDate": "2013-09-27T00:00:00.000", "estimatedTransitTime": 1444,
            "serviceType": "INDIRECT", "carrier": {"id": 27, "scac": "VOLT", "name": "VOLUNTEER EXPRESS INC",
            "logoPath": "resources/img/carrier-logo/volunteer-express-logo.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -180.49, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 13.43, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "62f96b6b-d6c3-4b76-9083-ccf2e3d71960", "estimatedTransitDate": "2013-09-30T00:00:00.000", "estimatedTransitTime": 2880,
            "serviceType": "INDIRECT", "carrier": {"id": 13, "scac": "AVRT", "name": "AVERITT EXPRESS",
            "logoPath": "resources/img/carrier-logo/Averitt.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null, "newLiability": 25000,
            "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -190.66, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 7.70, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "b0905216-3439-497e-8c6a-8fc676660b7e", "estimatedTransitDate": "2013-09-30T00:00:00.000", "estimatedTransitTime": 2881,
            "serviceType": "DIRECT", "carrier": {"id": 23, "scac": "NEMF", "name": "NEW ENGLAND MOTOR FREIGHT, INC.",
            "logoPath": "resources/img/carrier-logo/nemf-logo.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -158.04, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 6.66, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "d40a87c6-0b45-4561-8974-c0fb17b717bb", "estimatedTransitDate": "2013-09-30T00:00:00.000", "estimatedTransitTime": 2882,
            "serviceType": "DIRECT", "carrier": {"id": 26, "scac": "UPGF", "name": "UPS FREIGHT A",
            "logoPath": "resources/img/carrier-logo/ups-freight.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -197.52, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 8.55, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "c625ff81-72df-46bc-9a80-17a3504af686", "estimatedTransitDate": "2013-09-30T00:00:00.000", "estimatedTransitTime": 2883,
            "serviceType": "INDIRECT", "carrier": {"id": 24, "scac": "PITD", "name": "PITT OHIO EXPRESS, LLC",
            "logoPath": "resources/img/carrier-logo/pitt-ohio-express-logo.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -193.28, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 10.95, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "d7f509d8-a0f7-470c-b583-b6893634fbef", "estimatedTransitDate": "2013-09-30T00:00:00.000", "estimatedTransitTime": 2884,
            "serviceType": "DIRECT", "carrier": {"id": 18, "scac": "FXNL", "name": "FEDEX FREIGHT ECONOMY",
            "logoPath": "resources/img/carrier-logo/FedExF_Logo1.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -185.82, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 9.24, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "3159356a-9ba9-4121-bc17-a0d4c9ffc96d", "estimatedTransitDate": "2013-09-30T00:00:00.000", "estimatedTransitTime": 2885,
            "serviceType": "DIRECT", "carrier": {"id": 16, "scac": "EXLA", "name": "ESTES EXPRESS LINES",
            "logoPath": "resources/img/carrier-logo/Estes.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null, "newLiability": 25000,
            "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -196.25, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 8.97, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "574ac001-b5a2-4a7d-8a4e-b5611f94fe60", "estimatedTransitDate": "2013-10-02T00:00:00.000", "estimatedTransitTime": 5760,
            "serviceType": "INDIRECT", "carrier": {"id": 21, "scac": "LAXV", "name": "LAND AIR EXPRESS OF NEW ENGLAND",
            "logoPath": "resources/img/carrier-logo/land-air-express-logo.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -181.56, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 10.81, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]},
        {"guid": "2ccfd921-909e-47ae-993e-0768527085d3", "estimatedTransitDate": "2013-10-02T00:00:00.000", "estimatedTransitTime": 5759,
            "serviceType": "DIRECT", "carrier": {"id": 22, "scac": "MDLD", "name": "MIDLAND TRANSPORT LIMITED",
            "logoPath": "resources/img/carrier-logo/midland-transport-logo.png", "specialMessage": "Max LTL weight 25,000 lbs"}, "ref": null,
            "newLiability": 25000, "usedLiability": 100, "prohibited": "Drugs / Narcotics", hideTerminalDetails:true, "costDetailItems": [
            {"refType": "SRA", "description": "Shipper Base Rate", "subTotal": 254.21, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "DS", "description": "Discount", "subTotal": -182.29, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "FS", "description": "Fuel Surcharge", "subTotal": 10.02, "costDetailOwner": "S", "guaranteedBy": null},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 165.00, "costDetailOwner": "S", "guaranteedBy": 0},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 110.00, "costDetailOwner": "S", "guaranteedBy": 900},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 55.00, "costDetailOwner": "S", "guaranteedBy": 930},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 44.00, "costDetailOwner": "S", "guaranteedBy": 1130},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 33.00, "costDetailOwner": "S", "guaranteedBy": 1600},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 22.00, "costDetailOwner": "S", "guaranteedBy": 1730},
            {"refType": "GD", "description": "Guaranteed", "subTotal": 5.50, "costDetailOwner": "S", "guaranteedBy": 2200},
            {"refType": "SBR", "description": "Benchmark", "subTotal": 500, "costDetailOwner": "B", "guaranteedBy": null}
        ]}
    ];

    var mockShipmentsProposalService = {
        findShipmentPropositions: function (params, shipment, success, failure) {
            success(mockProposals);
            return this;
        }
    };

    var accessorialTypeServiceMock = {
        listAccessorialsByGroup: function() {
        }
    };

    var dictionaryValues = [Math.random(), Math.random()];

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

    var mockShipmentUtils = {
        getDictionaryValues : function() {
            return dictionaryValues;
        },
        getGuaranteedTimeOptions : function() {
            return guaranteedTimeOptions;
        }
    };


    beforeEach(module('plsApp'));
    beforeEach(module('plsApp', function($provide) {
        $provide.factory('DictionaryService', function() {
            return {
                getPackageTypes: function() {
                    return {
                        success: function(handler) {
                            handler([{code: "BOX", label: "Boxes"}, {code: "ENV", label: "Envelopes"}, {code: "PLT", label: "Pallet"}]);
                        }
                    };
                }
            };
        });
    }));

    beforeEach(inject(function ($rootScope, $controller, $routeParams, LinkedListUtils) {
        scope = $rootScope.$new();
        $controller('QuoteWizard', {$scope: scope, ShipmentDetailsService: {}, SavedQuotesService: {}, 
            CustomerLabelResource: {}, AccTypesServices: accessorialTypeServiceMock, ShipmentUtils: mockShipmentUtils});
        $controller('SaveQuoteController', {$scope: scope})
        controller = $controller('SelectCarrierCtrl', {$scope: scope, ShipmentsProposalService: mockShipmentsProposalService, ShipmentUtils: mockShipmentUtils});
        scope.carrierPropositionsGrid.$gridScope = {
            columns: [{},{},{},{},{visible: false, toggleVisible: function(){}}]
        };
        spyOn(scope.carrierPropositionsGrid.$gridScope.columns[4], 'toggleVisible').and.callThrough();
        scope.wizardData.steps.find('select_carrier');
    }));

    it('should test controller creation', function () {
        c_expect(controller).to.be.defined;
    });

    it('should test controller initialization', function () {
        scope.wizardData.shipment.originDetails.zip = {country: {}};
        scope.wizardData.shipment.destinationDetails.zip = {country: {}};
        scope.$apply(function() {
            scope.wizardData.shipment.originDetails.accessorials = ['Acc1', 'Acc2', 'Acc3', 'Acc4'];
            scope.init();
        });
        c_expect(scope.wizardData.shipment.guaranteedBy).to.be.undefined;
        c_expect(scope.carrierPropositionsGrid.$gridScope.columns[4].visible).to.be.false;
        c_expect(scope.carrierPropositionsGrid.$gridScope.columns[4].toggleVisible.calls.count()).to.equal(0);
        c_expect(scope.pageModel.carrierPropositionsGridData.length).to.eql(13);
        c_expect(scope.pageModel.carrierPropositionsGridData[0].origProposal).to.eql(scope.pageModel.selectedProposition);
        c_expect(scope.pageModel.carrierPropositionsGridData[0].estimatedTransitTime).to.eql(1441);
        c_expect(scope.pageModel.carrierPropositionsGridData[12].estimatedTransitTime).to.eql(5759);
    });

    it('should test proposal sorting', function () {
        scope.wizardData.shipment.originDetails.zip = {country: {}};
        scope.wizardData.shipment.destinationDetails.zip = {country: {}};
        scope.$apply(function() {
            scope.init();
        });
        scope.pageModel.carrierPropositionsGridData = _.shuffle(scope.pageModel.carrierPropositionsGridData);
        scope.$apply(function() {
            scope.pageModel.sortBy = 'estimatedTransitTime';
            scope.sortCarrierPropositions();
        });
        c_expect(scope.wizardData.shipment.guaranteedBy).to.be.undefined;
        c_expect(scope.pageModel.carrierPropositionsGridData.length).to.eql(13);
        c_expect(scope.pageModel.carrierPropositionsGridData[0].estimatedTransitTime).to.eql(1440);
        c_expect(scope.pageModel.carrierPropositionsGridData[12].estimatedTransitTime).to.eql(5760);
    });

    it('should test proposal sorting by cost', function () {
        scope.wizardData.shipment.originDetails.zip = {country: {}};
        scope.wizardData.shipment.destinationDetails.zip = {country: {}};
        scope.pageModel.sortBy = 'estimatedTransitTime';
        scope.$apply(function() {
            scope.init();
        });
        scope.pageModel.carrierPropositionsGridData = _.shuffle(scope.pageModel.carrierPropositionsGridData);
        scope.$apply(function() {
            scope.pageModel.sortBy = 'totalCost';
            scope.sortCarrierPropositions();
        });
        c_expect(scope.wizardData.shipment.guaranteedBy).to.be.undefined;
        c_expect(scope.pageModel.carrierPropositionsGridData.length).to.eql(13);
        c_expect(scope.pageModel.carrierPropositionsGridData[0].estimatedTransitTime).to.eql(1441);
        c_expect(scope.pageModel.carrierPropositionsGridData[12].estimatedTransitTime).to.eql(5759);
    });

    it('should display guaranteed column', function () {
        scope.wizardData.shipment.originDetails.zip = {country: {}};
        scope.wizardData.shipment.destinationDetails.zip = {country: {}};
        scope.$apply(function() {
            scope.wizardData.shipment.guaranteedBy = 2300;
            scope.wizardData.shipment.originDetails.accessorials = ['Acc1', 'Acc2', 'Acc3', 'Acc4'];
            scope.init();
        });
        c_expect(scope.carrierPropositionsGrid.$gridScope.columns[4].toggleVisible.calls.count()).to.equal(1);
    });

    it('should hide guaranteed column', function () {
        scope.wizardData.shipment.originDetails.zip = {country: {}};
        scope.wizardData.shipment.destinationDetails.zip = {country: {}};
        scope.$apply(function() {
            scope.carrierPropositionsGrid.$gridScope.columns[4].visible = true;
            scope.wizardData.shipment.guaranteedBy = undefined;
            scope.wizardData.shipment.originDetails.accessorials = ['Acc1', 'Acc2', 'Acc3', 'Acc4'];
            scope.init();
        });
        c_expect(scope.carrierPropositionsGrid.$gridScope.columns[4].toggleVisible.calls.count()).to.equal(1);
    });

    it('should remain guaranteed column', function () {
        scope.wizardData.shipment.originDetails.zip = {country: {}};
        scope.wizardData.shipment.destinationDetails.zip = {country: {}};
        scope.$apply(function() {
            scope.wizardData.shipment.guaranteedBy = 2300;
            scope.carrierPropositionsGrid.$gridScope.columns[4].visible = true;
            scope.wizardData.shipment.originDetails.accessorials = ['Acc1', 'Acc2', 'Acc3', 'Acc4'];
            scope.init();
        });
        c_expect(scope.carrierPropositionsGrid.$gridScope.columns[4].toggleVisible.calls.count()).to.equal(0);
    });

    it('should go to previous step', function () {
        scope.back();

        c_expect(scope.wizardData.step).to.equal('rate_quote');
    });

    it('should save proposal', function () {
        spyOn(scope, '$broadcast');

        var proposition = {fakeProposalField: 'fake value'};
        scope.saveProposition(proposition);

        c_expect(scope.wizardData.shipment.selectedProposition).to.equal(proposition);
        c_expect(scope.$broadcast.calls.count()).to.equal(1);
        c_expect(scope.$broadcast.calls.mostRecent().args[0]).to.equal('event:saveSelectedQuoteForWizard');
    });

    it('should book proposal', function () {
        var proposition = {estimatedTransitDate: '20/08/2013'};
        scope.book(proposition);

        c_expect(scope.wizardData.shipment.finishOrder.estimatedDelivery).to.equal(proposition.estimatedTransitDate);
        c_expect(scope.wizardData.step).to.equal('build_order');
    });

    it('should book proposal from new quote', function () {
        scope.$apply(function () {
            scope.wizardData.savedQuoteDetails['test-guid'] = {quoteId: 123, quoteRef: 'testRef'};
        });

        var proposition = {estimatedTransitDate: '20/08/2013', guid : 'test-guid'};
        scope.book(proposition);

        c_expect(scope.wizardData.shipment.finishOrder.estimatedDelivery).to.equal(proposition.estimatedTransitDate);
        c_expect(scope.wizardData.shipment.finishOrder.ref).to.equal('testRef');
        c_expect(scope.wizardData.shipment.quoteId).to.equal(123);

        c_expect(scope.wizardData.step).to.equal('build_order');
    });

    it('should book proposal from existing quote', function () {
      scope.$apply(function () {
          scope.wizardData.editedQuoteId = 321;
      });

      var proposition = {estimatedTransitDate: '20/08/2013'};
      scope.book(proposition);

      c_expect(scope.wizardData.shipment.finishOrder.estimatedDelivery).to.equal(proposition.estimatedTransitDate);
      c_expect(scope.wizardData.shipment.quoteId).to.equal(321);

      c_expect(scope.wizardData.step).to.equal('build_order');
  });

    it('should emit application-error event during book process if pickup date is empty', function () {
        spyOn(scope.$root, '$emit');

        scope.wizardData.shipment.finishOrder.pickupDate = undefined;
        scope.book({estimatedTransitDate: '20/08/2013'});

        c_expect(scope.$root.$emit.calls.count()).to.equal(1);
        c_expect(scope.$root.$emit.calls.mostRecent().args[0]).to.equal('event:application-error');
    });
});