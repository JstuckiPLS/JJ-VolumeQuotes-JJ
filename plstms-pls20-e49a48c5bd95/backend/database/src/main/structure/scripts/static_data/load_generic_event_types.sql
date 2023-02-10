delete from FLATBED.LOAD_GENERIC_EVENT_TYPES;

INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('PREAWARD','Pre-Awarded');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('EM.AWARD','Award confirmation message sent to {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('EM.UNAWARD','Un-award confirmation message sent to {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('SCH.DO','Scheduled {0} for {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('SCH.REDO','Rescheduled {0} for {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('SCH.UNDO','Unscheduled');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('SCH.ADSP','Load set to DSP due to incomplete dock info');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('TRUCKTR','Receive {0} truck update request');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.MS','Load is manually set missed');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.UNMS','Load is manually set not missed');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('SRR','Load {0} the Shipper Rate Release Screen');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('ASN','Receive ASN reconciliation request');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('SP.UNSET','Shipper Premium Available flag is unset');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('SP.SET','Shipper Premium Available flag is set');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('EM.DECLINE','Decline offer message sent to {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('CUSTTRKCHG','The Customer Truck Carrier Details have been modified on the {0} screen. The new values are: Carrier SCAC: {1}, Name: {2}, Person: {3}, Phone: {4}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('DEST.ARRVL','Confirmed Arrival at Destination @ {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('TE.NEWMKT','Opened market "{0}" for tendering');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('TE.NEWPLAN','Tendering plan created');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('TE.NEW_ERR','Tendering could not begin. {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LOADCHG','Load modified: {0} has been changed from "{1}" to "{2}"');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('NOTAWARD','Load could not be awarded. {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.IAB','Invoice benchmark rate of {0} greater than award rate of {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.IAC','Invoice carrier rate of {0} greater than award rate of {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.IAS','Invoice shipper rate of {0} greater than award rate of {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.IRB','Invoice benchmark rate of {0} greater than rated amount of {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.IRC','Invoice carrier rate of {0} greater than rated amount of {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.IRS','Invoice shipper rate of {0} greater than rated amount of {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.PRO','PRO # missing');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.NOMAT','Material missing on Van LTL Load');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.ACCT','LTL account type missing');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.CC','LTL client code missing');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.PRICE','LTL pricing failed to execute');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LTL.RATE','LTL rates could not be found');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('FBH.MP','Billing Status is updated to Field Billing Hold. Reason: Missing Paperwork');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('FBH.MFB','Billing Status is updated to Field Billing Hold. Reason: Missing Freight Bill Number and Date');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('FBH.DED','Billing Status is updated to Field Billing Hold. Reason: Dedicated');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('EDI_IGNORE','EDI update not accepted because load is in {0} billing status.');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.HM','Hidden Markup Applied: {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.BSU','Released from {0} to {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.RSN','Reason: {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.CUSTCHG','Customer changed from {0} to {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('INV.SHIP','Only shipper cost is moved to Financials');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('INV.CARR','Only carrier cost is moved to Financials');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.AC','Above carrier cargo value is approved by {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('CARTRKNOTE','{0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('204.IGNORE', 'EDI 204 update was received and could not be applied. An email has been sent to the Customer Service Contact with shipment update details');

INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.AWD','Load Awarded to {0} for {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.SRC','Load created by {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('TRK.EMAIL','{0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.MV','Shipment is moved to {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.MV_RSN','Shipment is moved to {0}, Reason: {1}, Note: {2}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('DU.MARGIN', 'Revenue updated for Total Revenue {0}, Cost Difference {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('DU.AMOUNT', 'Revenue updated for Margin {0}, Cost Difference {1}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('DU.C.DIFF', 'Revenue updated Using Cost Difference {0}');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.VB_DET','Vendor Bill Detached');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('LD.VB_ATT','Vendor Bill Attached');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) values ('LD.BSURB','Rolled Back the CBI load scheduled to release to finance because of Audit failure. Moved to Audit.');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('FS.HOLD','Financials paused for shipment.');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('FS.ALLOW','Financials allowed for shipment.');

INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('SAVED','{0} - Adjustment Saved');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('DELETED','{0} - Adjustment Deleted');
INSERT INTO FLATBED.LOAD_GENERIC_EVENT_TYPES (EVENT_TYPE,DESCRIPTION) VALUES ('RLSD.ABR','{0} - Adjustment Released to ABR');
COMMIT;