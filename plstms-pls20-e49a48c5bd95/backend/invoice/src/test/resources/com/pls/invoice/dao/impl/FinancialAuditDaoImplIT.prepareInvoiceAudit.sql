update loads set FINALIZATION_STATUS = 'NF';

update loads set FINALIZATION_STATUS = 'ABH', load_status = 'CD' where load_id in (560, 561);

update loads set FRT_BILL_AMOUNT = 10.35 where load_id = 560;
update loads set FRT_BILL_AMOUNT = 12, CARRIER_REFERENCE_NUMBER = 'TEST1' where load_id = 561;

Insert into LD_BILLING_AUDIT_REASONS (LD_BILL_AUDIT_RSN_ID,LOAD_ID,FAA_DETAIL_ID,REASON_CD,COMMENTS,STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY)
values (10,560,null,'CD','Comment 1','A',current_date,1,current_date - 1,1);
Insert into LD_BILLING_AUDIT_REASONS (LD_BILL_AUDIT_RSN_ID,LOAD_ID,FAA_DETAIL_ID,REASON_CD,COMMENTS,STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY)
values (11,560,null,'IF','Comment 2','I',current_date,1,current_date,1);
Insert into LD_BILLING_AUDIT_REASONS (LD_BILL_AUDIT_RSN_ID,LOAD_ID,FAA_DETAIL_ID,REASON_CD,COMMENTS,STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY)
values (12,560,null,'MD','Comment 3','A',current_date,1,current_date,1);

Insert into LD_BILLING_AUDIT_REASONS (LD_BILL_AUDIT_RSN_ID,LOAD_ID,FAA_DETAIL_ID,REASON_CD,COMMENTS,STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY)
values (13,561,null,'MD','Comment 3','I',current_date,1,current_date,1);

---------------------------------------------------

update FINAN_ADJ_ACC_DETAIL set FAA_STATUS = 'ABAA';

update FINAN_ADJ_ACC_DETAIL set FAA_STATUS = 'ABHAA' where FAA_DETAIL_ID in (3, 6, 7);
update loads set FRT_BILL_AMOUNT = 8.11 where load_id = 56;
update loads set FRT_BILL_AMOUNT = 8.12 where load_id = 59;

Insert into LD_BILLING_AUDIT_REASONS (LD_BILL_AUDIT_RSN_ID,LOAD_ID,FAA_DETAIL_ID,REASON_CD,COMMENTS,STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY)
values (14,56,3,'CD','Comment 1','A',current_date,1,current_date,1);
Insert into LD_BILLING_AUDIT_REASONS (LD_BILL_AUDIT_RSN_ID,LOAD_ID,FAA_DETAIL_ID,REASON_CD,COMMENTS,STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY)
values (15,56,3,'IF','Comment 2','I',current_date,1,current_date,1);
Insert into LD_BILLING_AUDIT_REASONS (LD_BILL_AUDIT_RSN_ID,LOAD_ID,FAA_DETAIL_ID,REASON_CD,COMMENTS,STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY)
values (16,56,3,'MD','Comment 3','A',current_date,1,current_date - 1,1);

Insert into LD_BILLING_AUDIT_REASONS (LD_BILL_AUDIT_RSN_ID,LOAD_ID,FAA_DETAIL_ID,REASON_CD,COMMENTS,STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY)
values (17,59,6,'IF','Comment 2','I',current_date,1,current_date,1);
Insert into LD_BILLING_AUDIT_REASONS (LD_BILL_AUDIT_RSN_ID,LOAD_ID,FAA_DETAIL_ID,REASON_CD,COMMENTS,STATUS,DATE_CREATED,CREATED_BY,DATE_MODIFIED,MODIFIED_BY)
values (18,59,6,'MD','Comment 3','A',current_date,1,current_date,1);