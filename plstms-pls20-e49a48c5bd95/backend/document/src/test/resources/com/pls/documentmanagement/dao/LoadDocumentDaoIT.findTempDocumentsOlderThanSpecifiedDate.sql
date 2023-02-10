delete from image_metadata where DOCUMENT_TYPE='TEMP';
insert into image_metadata (IMAGE_META_ID, DOCUMENT_TYPE, STATUS, DATE_CREATED, CREATED_BY, DATE_MODIFIED, MODIFIED_BY, VERSION) values (777, 'TEMP', 'A', current_date - interval '3' day, 1, current_date - interval '3' day, 1, 1);


