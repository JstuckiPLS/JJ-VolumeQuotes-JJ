delete from shipment_alerts;

update LOAD_DETAILS set departure = TO_DATE('2013-11-12', 'yyyy-mm-dd');
--THIRTY_MIN_TO_PICKUP
update LOAD_DETAILS set SCHEDULED_ARRIVAL=(LOCALTIMESTAMP + interval '25' minute), departure=null where LOAD_ID=302 and LOAD_ACTION='P' and POINT_TYPE= 'O';

update loads set LOAD_STATUS='PO' where load_id in (1,3,4);
--PICKUP_TODAY
update LOAD_DETAILS set SCHEDULED_ARRIVAL=(LOCALTIMESTAMP + interval '31' minute), departure=null where LOAD_ID=1 and LOAD_ACTION='P' and POINT_TYPE= 'O';
--MISSED_PICKUP
update LOAD_DETAILS set SCHEDULED_ARRIVAL=(LOCALTIMESTAMP - interval '1' minute), departure=null where LOAD_ID=3 and LOAD_ACTION='P' and POINT_TYPE= 'O';
--MISSED_DELIVERY
update LOAD_DETAILS set SCHEDULED_ARRIVAL=(LOCALTIMESTAMP - interval '1' minute), departure=null where LOAD_ID=4 and LOAD_ACTION='D' and POINT_TYPE= 'D';
