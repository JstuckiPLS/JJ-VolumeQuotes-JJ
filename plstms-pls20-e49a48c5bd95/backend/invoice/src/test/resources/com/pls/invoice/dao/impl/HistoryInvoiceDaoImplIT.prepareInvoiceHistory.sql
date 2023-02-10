delete from INVOICE_HISTORY;

--successful loads
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (1,1,'CBI',812,null,null,'S',
    current_date,2,current_date,2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (2,1,'CBI',813,null,null,'S',
    current_date,2,current_date,2);

--successful adjustments
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (3,1,'CBI',null,1,null,'S',
    current_date,2,current_date,2);
update FINAN_ADJ_ACC_DETAIL set SHORT_PAY = 'N', GROUP_INVOICE_NUM = 'C-0000123', GL_DATE=current_date where FAA_DETAIL_ID = 1;
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (4,1,'CBI',null,2,null,'S',
    current_date,2,current_date,2);
update FINAN_ADJ_ACC_DETAIL set SHORT_PAY = 'N', GROUP_INVOICE_NUM = 'C-0000123', GL_DATE=current_date where FAA_DETAIL_ID = 2;

--not successful loads
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (5,1,'CBI',814,null,null,'C',
    current_date,2,current_date,2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (6,1,'CBI',815,null,null,'F',
    current_date,2,current_date,2);
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (7,1,'CBI',816,null,null,'R',
    current_date,2,current_date,2);

--not successful adjustments
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (8,1,'CBI',null,3,null,'C',
    current_date,2,current_date,2);
update FINAN_ADJ_ACC_DETAIL set SHORT_PAY = 'N', GROUP_INVOICE_NUM = 'C-0000123', GL_DATE=current_date where FAA_DETAIL_ID = 3;
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (9,1,'CBI',null,4,null,'F',
    current_date,2,current_date,2);
update FINAN_ADJ_ACC_DETAIL set SHORT_PAY = 'N', GROUP_INVOICE_NUM = 'C-0000123', GL_DATE=current_date where FAA_DETAIL_ID = 4;
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (10,1,'CBI',null,5,null,'R',
    current_date,2,current_date,2);
update FINAN_ADJ_ACC_DETAIL set SHORT_PAY = 'N', GROUP_INVOICE_NUM = 'C-0000123', GL_DATE=current_date where FAA_DETAIL_ID = 5;
Insert into INVOICE_HISTORY (INVOICE_HISTORY_ID,INVOICE_ID,INVOICE_TYPE,LOAD_ID,FAA_DETAIL_ID,BILLING_STATUS_REASON_CODE,
    RELEASE_STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY) values (11,1,'CBI',null,6,null,'S',
    current_date,2,current_date,2);
update FINAN_ADJ_ACC_DETAIL set SHORT_PAY = 'Y', GROUP_INVOICE_NUM = 'C-0000123', GL_DATE=current_date where FAA_DETAIL_ID = 6;

update rater.load_cost_details set group_invoice_num = 'C-0000123' where load_id in (1, 2, 812, 813, 814, 815, 816);