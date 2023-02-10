--PLS2.0 specific document types
delete from FLATBED.IMAGE_DOCUMENT_TYPE;

INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE)VALUES ('BOL','BOL',1,'Y',0,TO_DATE('01-AUG-10','DD-MON-RR'),TO_DATE('01-AUG-10','DD-MON-RR'),0,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('INVOICE','Invoice',2,'Y',0,TO_DATE('01-AUG-10','DD-MON-RR'),TO_DATE('01-AUG-10','DD-MON-RR'),0,1,'LOAD_INTERNAL',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('UNKNOWN','Unknown',3,'Y',0,TO_DATE('01-AUG-10','DD-MON-RR'),TO_DATE('28-DEC-10','DD-MON-RR'),120052,2,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('POD','Proof of Delivery',21,'N',120052,TO_DATE('28-DEC-10','DD-MON-RR'),TO_DATE('29-APR-11','DD-MON-RR'),120052,2,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('Miscellaneous','Miscellaneous',22,'N',120052,TO_DATE('28-DEC-10','DD-MON-RR'),TO_DATE('29-APR-11','DD-MON-RR'),120052,2,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('W9_DOCUMENT','W9 Document',41,'Y',0,TO_DATE('03-FEB-11','DD-MON-RR'),TO_DATE('03-FEB-11','DD-MON-RR'),0,1,'ORG',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('FACTORING_DOC','Factoring Document',42,'N',0,TO_DATE('03-FEB-11','DD-MON-RR'),TO_DATE('03-FEB-11','DD-MON-RR'),0,1,'ORG',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('Scale Tickets','Scale Tickets',61,'N',120052,TO_DATE('21-DEC-11','DD-MON-RR'),TO_DATE('21-DEC-11','DD-MON-RR'),120052,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('Release Sheets','Release Sheets',62,'N',120052,TO_DATE('21-DEC-11','DD-MON-RR'),TO_DATE('21-DEC-11','DD-MON-RR'),120052,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('DO','DELIVERY ORDERS',81,'N',138292,TO_DATE('22-DEC-11','DD-MON-RR'),TO_DATE('22-DEC-11','DD-MON-RR'),138292,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('SIGNED PACKING SLIPS','SIGNED PACKING SLIPS',82,'N',138292,TO_DATE('22-DEC-11','DD-MON-RR'),TO_DATE('22-DEC-11','DD-MON-RR'),138292,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('LUMPER RECEIPTS','LUMPER RECEIPTS',83,'N',138292,TO_DATE('22-DEC-11','DD-MON-RR'),TO_DATE('22-DEC-11','DD-MON-RR'),138292,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('ORIGINALS','ORIG. DOCS ONLY',84,'N',138292,TO_DATE('22-DEC-11','DD-MON-RR'),TO_DATE('22-DEC-11','DD-MON-RR'),138292,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('blind bill','blind bill',86,'N',138292,TO_DATE('22-DEC-11','DD-MON-RR'),TO_DATE('22-DEC-11','DD-MON-RR'),138292,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('CC AUTHO FORM','CC ONLY CUSTOMERS',101,'N',138292,TO_DATE('04-JAN-12','DD-MON-RR'),TO_DATE('04-JAN-12','DD-MON-RR'),138292,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('QUOTES','QUOTES',121,'N',138292,TO_DATE('26-JAN-12','DD-MON-RR'),TO_DATE('26-JAN-12','DD-MON-RR'),138292,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('PO','PO NUMBERS',122,'N',138292,TO_DATE('26-JAN-12','DD-MON-RR'),TO_DATE('26-JAN-12','DD-MON-RR'),138292,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('REWEIGH CERT','REWEIGH CERT',142,'N',138292,TO_DATE('30-JAN-12','DD-MON-RR'),TO_DATE('30-JAN-12','DD-MON-RR'),138292,1,'LOAD',NULL);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION,DOCUMENT_LEVEL,DOCUMENT_ORG_TYPE) VALUES ('VENDOR BILL','VENDOR BILL',200,'Y',0,TO_DATE('01-AUG-10','DD-MON-RR'),TO_DATE('01-AUG-10','DD-MON-RR'),0,1,'LOAD',NULL);

INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,DOCUMENT_LEVEL,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION) VALUES ('W_AND_I','Weight and inspection ticket',201,'Y','LOAD',0,current_date,current_date,0,1);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,DOCUMENT_LEVEL,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION) VALUES ('CLAIMS','Claims form',202,'Y','LOAD',0,current_date,current_date,0,1);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,DOCUMENT_LEVEL,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION) VALUES ('SHIPPING_LABELS','Shipping Label',203,'Y','LOAD',0,current_date,current_date,0,1);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,DOCUMENT_LEVEL,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION) VALUES ('TEMP','Temp',204,'Y','TEMP',0,current_date,current_date,0,1);
INSERT INTO FLATBED.IMAGE_DOCUMENT_TYPE (DOCUMENT_TYPE,DESCRIPTION,IMAGE_DOC_TYPE_ID,SYSTEM_PROTECTED,DOCUMENT_LEVEL,CREATED_BY,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,VERSION) VALUES ('CONSIGNEE_INVOICE','Consignee Invoice',205,'Y','LOAD',0,current_date,current_date,0,1);
COMMIT;