DELETE FROM FLATBED.TIMEZONE;
INSERT INTO FLATBED.TIMEZONE (TIMEZONE_CODE, TIMEZONE_NAME, LOCAL_OFFSET, TIMEZONE) VALUES ('AST','US/Puerto_Rico', '1', '4');
INSERT INTO FLATBED.TIMEZONE (TIMEZONE_CODE, TIMEZONE_NAME, LOCAL_OFFSET, TIMEZONE) VALUES ('EST','US/Eastern', '0', '5');
INSERT INTO FLATBED.TIMEZONE (TIMEZONE_CODE, TIMEZONE_NAME, LOCAL_OFFSET, TIMEZONE) VALUES ('CST','US/Central', '-1', '6');
INSERT INTO FLATBED.TIMEZONE (TIMEZONE_CODE, TIMEZONE_NAME, LOCAL_OFFSET, TIMEZONE) VALUES ('MST','US/Mountain', '-2', '7');
INSERT INTO FLATBED.TIMEZONE (TIMEZONE_CODE, TIMEZONE_NAME, LOCAL_OFFSET, TIMEZONE) VALUES ('PST','US/Pacific', '-3', '8');
INSERT INTO FLATBED.TIMEZONE (TIMEZONE_CODE, TIMEZONE_NAME, LOCAL_OFFSET, TIMEZONE) VALUES ('AKST','US/Alaska', '-4', '9');
INSERT INTO FLATBED.TIMEZONE (TIMEZONE_CODE, TIMEZONE_NAME, LOCAL_OFFSET, TIMEZONE) VALUES ('HST','US/Hawaiin', '-5', '10');
INSERT INTO FLATBED.TIMEZONE (TIMEZONE_CODE, TIMEZONE_NAME, LOCAL_OFFSET, TIMEZONE) VALUES ('SST','US/Samoa', '-6', '11');

COMMIT;