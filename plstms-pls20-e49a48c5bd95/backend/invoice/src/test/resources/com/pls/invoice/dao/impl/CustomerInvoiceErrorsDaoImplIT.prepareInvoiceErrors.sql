delete from INVOICE_HISTORY;
delete from CUSTOMER_INVOICE_ERRORS;

update FINAN_ADJ_ACC_DETAIL set GROUP_INVOICE_NUM = 'I-' || FAA_DETAIL_ID || '-adj';
update rater.LOAD_COST_DETAILS set GROUP_INVOICE_NUM = 'I-' || LOAD_ID || '-0000';

Insert into CUSTOMER_INVOICE_ERRORS (ID,INVOICE_ID,STATUS,SENT_EMAIL,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY,VERSION) values (1,1,'A',
    'N', to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2,1);
Insert into CUSTOMER_INVOICE_ERRORS (ID,INVOICE_ID,STATUS,SENT_EMAIL,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY,VERSION) values (2,2,'A',
    'Y', to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2,1);
Insert into CUSTOMER_INVOICE_ERRORS (ID,INVOICE_ID,STATUS,SENT_EMAIL,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY,VERSION) values (3,3,'I',
    'N', to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2,1);


--loads
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (1,1,'CBI',812,null,null,'S',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (2,1,'TRANSACTIONAL',813,null,null,'R',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (3,1,'CBI',814,null,null,'C',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);

--adjustments
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (4,1,'TRANSACTIONAL',null,1,null,'S',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (5,1,'CBI',null,2,null,'R',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (6,1,'CBI',null,3,null,'C',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);

--loads
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (7,2,'CBI',812,null,null,'S',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (8,2,'TRANSACTIONAL',813,null,null,'R',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (9,2,'CBI',814,null,null,'C',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);

--adjustments
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (10,2,'TRANSACTIONAL',null,1,null,'S',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (11,2,'CBI',null,2,null,'R',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (12,2,'CBI',null,3,null,'C',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);

--loads
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (13,3,'CBI',812,null,null,'S',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (14,3,'TRANSACTIONAL',813,null,null,'R',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (15,3,'CBI',814,null,null,'C',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);

--adjustments
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (16,3,'TRANSACTIONAL',null,1,null,'S',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (17,3,'CBI',null,2,null,'R',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (18,3,'CBI',null,3,null,'C',
    to_date('19-JAN-12 00.00','DD-MON-RR HH24.MI'),2,to_date('12-AUG-12 00.00','DD-MON-RR HH24.MI'),2);
