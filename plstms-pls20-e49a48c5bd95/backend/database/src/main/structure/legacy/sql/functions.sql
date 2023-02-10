
SET search_path = flatbed, pg_catalog;
/

--
-- TOC entry 1042 (class 1255 OID 24563)
-- Name: trigger_fct_addresses_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_addresses_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN
insert into hist.addresses(
   moduser,modtime,modstatus,
   address_id, latitude, longitude,
   address1, address2, city,
   postal_code, state_code, country_code,
   date_created, preferred_city, status,
   address3, address4, created_by,
   date_modified, modified_by, version)
values (user,CURRENT_TIMESTAMP,null /*case when inserting then 'I' else 'D' end*/
,
   OLD.address_id, OLD.latitude, OLD.longitude,
   OLD.address1, OLD.address2, OLD.city,
   OLD.postal_code, OLD.state_code, OLD.country_code,
   OLD.date_created, OLD.preferred_city, OLD.status,
   OLD.address3, OLD.address4, OLD.created_by,
   OLD.date_modified, OLD.modified_by, OLD.version);

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_addresses_hist() OWNER TO postgres;
/
--
-- TOC entry 999 (class 1255 OID 24567)
-- Name: trigger_fct_ains_loads_invoice_audit(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_ains_loads_invoice_audit() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  -- local variables here
BEGIN
  if (NEW.finalization_status = 'AB' and OLD.finalization_status = 'ABH' and
     NEW.originating_system in ('PLS2_LT', 'GS')) then
    insert into invoice_audit_approves
    values (NEW.load_id,
       OLD.finalization_status,
       NEW.finalization_status,
       NEW.modified_by,
       NEW.date_modified);

  end if;

IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_ains_loads_invoice_audit() OWNER TO postgres;
/
--
-- TOC entry 995 (class 1255 OID 24569)
-- Name: trigger_fct_ains_organizations_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_ains_organizations_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE
  --v_org_id_parent ORGANIZATIONS.ORG_ID_PARENT%TYPE;
BEGIN
  --http://redmine.plspro.com/issues/6384
  --ORGANIZATION_PRICING record adding
  --Vadym Dudinov  2/3/2014
  IF (NEW.org_type = 'SHIPPER') THEN
    insert into ORGANIZATION_PRICING(ORG_ID,
       STATUS,
       MIN_ACCEPT_MARGIN,
       DEFAULT_MARGIN,
       GAINSHARE,
       DATE_CREATED,
       CREATED_BY,
       DATE_MODIFIED,
       MODIFIED_BY,
       VERSION,
       GS_PLS_PCT,
       GS_CUST_PCT,
       INCLUDE_BM_ACC,
       default_min_margin_amt)
    values
      (NEW.org_id,
       'A',
       (SELECT coalesce(MIN_ACCEPT_MARGIN, 0)
          from networks
         where network_id = NEW.network_id),
       null,
       'N',
       LOCALTIMESTAMP,
       NEW.created_by,
       LOCALTIMESTAMP,
       NEW.modified_by,
       1,
       0,
       0,
       'N',
       null);
  end if;
  ----------------------------------------------------
  IF (NEW.org_type = 'SHIPPER' and NEW.org_id_parent = 18594 and
     NEW.company_code is not null) THEN

    INSERT INTO MAP_ORG_COMPANY(ORG_ID, COMPANY, DATE_CREATED, CREATED_BY)
    VALUES (NEW.org_id, NEW.company_code, LOCALTIMESTAMP, 0);
  ELSIF (NEW.org_type = 'SHIPPER' and NEW.org_id_parent = 127525) THEN
    INSERT INTO MAP_ORG_COMPANY(ORG_ID, COMPANY, DATE_CREATED, CREATED_BY)
    VALUES (NEW.org_id, 'AG', LOCALTIMESTAMP, 0);
  ELSIF (NEW.org_type = 'SHIPPER' and NEW.org_id_parent = 193285) THEN
    INSERT INTO MAP_ORG_COMPANY(ORG_ID, COMPANY, DATE_CREATED, CREATED_BY, RATE_COMPANY_CODE)
    VALUES (NEW.org_id, 'LT', LOCALTIMESTAMP, 0, 'LT');
  ELSIF (NEW.org_type = 'SHIPPER' and NEW.org_id_parent = 211766) THEN
    INSERT INTO MAP_ORG_COMPANY(ORG_ID, COMPANY, DATE_CREATED, CREATED_BY, RATE_COMPANY_CODE)
    VALUES (NEW.org_id, 'CS', LOCALTIMESTAMP, 0, 'CS');
  ELSIF (NEW.org_type = 'SHIPPER' and NEW.org_id_parent = 0 and
        NEW.company_code is not null) THEN
    INSERT INTO MAP_ORG_COMPANY(ORG_ID, COMPANY, DATE_CREATED, CREATED_BY, RATE_COMPANY_CODE)
    VALUES (NEW.org_id, NEW.company_code, LOCALTIMESTAMP, 0, NEW.company_code);
  ELSIF (NEW.org_type = 'SHIPPER' and NEW.org_id_parent = 310739 and
        NEW.company_code is not null) THEN
    INSERT INTO MAP_ORG_COMPANY(ORG_ID, COMPANY, DATE_CREATED, CREATED_BY, RATE_COMPANY_CODE)
    VALUES (NEW.org_id, NEW.company_code, LOCALTIMESTAMP, 0, NEW.company_code);

  END IF;
RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_ains_organizations_trg() OWNER TO postgres;
/
--
-- TOC entry 998 (class 1255 OID 24565)
-- Name: trigger_fct_ainsupd_fin_acc_receivables(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_ainsupd_fin_acc_receivables() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  v_cbi_inv_type_count bigint;
BEGIN
  select count(cbi_invoice_type)
    into STRICT v_cbi_inv_type_count
    from invoice_settings
   where bill_to_id in (SELECT bill_to
            from loads
           where load_id = NEW.load_id and originating_system in ( 'PLS2_LT', 'GS'))
     and cbi_invoice_type = 'FIN';
  if v_cbi_inv_type_count > 0 then
    if NEW.faa_detail_id is null then
      update load_cost_details lcd
         set lcd.group_invoice_num    = NEW.inv_number,
             lcd.customer_invoice_num = NEW.inv_number,
             lcd.date_modified        = LOCALTIMESTAMP,
             lcd.modified_by          = NEW.created_by
       where lcd.load_id = NEW.load_id
         and lcd.status = 'A'
         and lcd.invoiced_in_finan = 'Y'
         and lcd.sent_to_finance = 'Y';
    else
      update finan_adj_acc_detail faad
         set faad.group_invoice_num    = NEW.inv_number,
             faad.customer_invoice_num = NEW.inv_number,
             faad.date_modified        = LOCALTIMESTAMP,
             faad.modified_by          = NEW.created_by
       where faad.faa_detail_id = NEW.faa_detail_id
         and faad.status = 'A'
         and faad.invoiced_in_finan = 'Y'
         and faad.sent_to_finance = 'Y';
    end if;
  end if;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_ainsupd_fin_acc_receivables() OWNER TO postgres;
/
--
-- TOC entry 1043 (class 1255 OID 24571)
-- Name: trigger_fct_aiu_organization_locations_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_aiu_organization_locations_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

  v_empnum_count  smallint;
  l_contact_email varchar(255);
  l_count         bigint;

BEGIN

  IF (NEW.EMPLOYER_NUM IS NOT NULL) THEN

    SELECT COUNT(*)
      INTO STRICT v_empnum_count
      FROM FLATBED.ORGANIZATIONS
     WHERE EMPLOYER_NUM = NEW.EMPLOYER_NUM;

    IF (v_empnum_count > 0) THEN
    
      RAISE unique_violation;

    END IF;

  END IF;

  select count(email_address)
    into STRICT l_count
    from (SELECT distinct uc.location_id, u.email_address
            from bill_to bt
           inner join user_customer uc
              on uc.org_id = bt.org_id
             and uc.location_id = NEW.location_id
             and bt.bill_to_id = NEW.bill_to
             and uc.status = 'A'
             and uc.exp_date > LOCALTIMESTAMP
           inner join users u
              on u.person_id = uc.person_id
             and u.status = 'A') alias1;
  if l_count > 0 then
    select distinct string_agg(email_address, ';' order by email_address) over (partition by location_id) as email
      into STRICT l_contact_email
      from (SELECT distinct uc.location_id, u.email_address
              from bill_to bt
             inner join user_customer uc
                on uc.org_id = bt.org_id
               and uc.location_id = NEW.location_id
               and bt.bill_to_id = NEW.bill_to
               and uc.status = 'A'
               and uc.exp_date > LOCALTIMESTAMP
             inner join users u
                on u.person_id = uc.person_id
               and u.status = 'A') alias2;

    update billing_invoice_node bin
       set contact_email = l_contact_email
     where bin.bill_to_id = NEW.bill_to
       and bin.contact_email is null;
  end if;

RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_aiu_organization_locations_trg() OWNER TO postgres;
/
--
-- TOC entry 993 (class 1255 OID 24576)
-- Name: trigger_fct_audit_load_details_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_audit_load_details_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

v_UPDATE_date timestamp := LOCALTIMESTAMP;
v_os_user varchar(250);
v_host varchar(64);

BEGIN

select sys_context('USERENV','OS_USER'), sys_context('USERENV','HOST')
into STRICT v_os_user, v_host;

INSERT INTO AUDIT_LOAD_DETAILS(LOAD_DETAIL_ID
,LOAD_ID
,LOAD_ACTION
,TICKET
,BOL
,ARRIVAL
,DEPARTURE
,SCHEDULED_ARRIVAL
,NEED_APPT
,CONTACT
,INSTRUCTIONS
,DATE_CREATED
,CREATED_BY
,DATE_MODIFIED
,MODIFIED_BY
,PO_NUM
,NODE_ID
,OP_BOL
,POINT_TYPE
,ADMIT_DATE
,NO_EARLY_SCHEDULE_REASON
,PART_NUM
,SDSA_FLAG
,SEQ_IN_ROUTE
,ADDRESS_ID
,EARLY_SCHEDULED_ARRIVAL
,APPOINTMENT_NUMBER
,NO_LATE_SCHEDULE_REASON
,NOT_YET_DELIVERED
,ORIG_DELIVER_NO_LATER_THAN
,ORIG_UNLOAD_DOCK_SCHEDULED_AT
,ARRIVAL_TZ
,DEPARTURE_TZ
,SCHEDULED_ARRIVAL_TZ
,ADMIT_DATE_TZ
,EARLY_SCHEDULED_ARRIVAL_TZ
,NOT_YET_DELIVERED_TZ
,ORIG_DELIVER_NO_LATER_THAN_TZ
,ORIG_UNLOAD_DOCK_SCHED_AT_TZ
,EARLY_SDSA_FLAG
,LOADED_DATE
,LOADED_DATE_TZ
,AUDIT_DATE
,AUDIT_OS_USER
,AUDIT_HOST
,AUDIT_ID)
VALUES (NEW.LOAD_DETAIL_ID
,NEW.LOAD_ID
,NEW.LOAD_ACTION
,NEW.TICKET
,NEW.BOL
,NEW.ARRIVAL
,NEW.DEPARTURE
,NEW.SCHEDULED_ARRIVAL
,NEW.NEED_APPT
,NEW.CONTACT
,NEW.INSTRUCTIONS
,NEW.DATE_CREATED
,NEW.CREATED_BY
,NEW.DATE_MODIFIED
,NEW.MODIFIED_BY
,NEW.PO_NUM
,NEW.NODE_ID
,NEW.OP_BOL
,NEW.POINT_TYPE
,NEW.ADMIT_DATE
,NEW.NO_EARLY_SCHEDULE_REASON
,NEW.PART_NUM
,NEW.SDSA_FLAG
,NEW.SEQ_IN_ROUTE
,NEW.ADDRESS_ID
,NEW.EARLY_SCHEDULED_ARRIVAL
,NEW.APPOINTMENT_NUMBER
,NEW.NO_LATE_SCHEDULE_REASON
,NEW.NOT_YET_DELIVERED
,NEW.ORIG_DELIVER_NO_LATER_THAN
,NEW.ORIG_UNLOAD_DOCK_SCHEDULED_AT
,NEW.ARRIVAL_TZ
,NEW.DEPARTURE_TZ
,NEW.SCHEDULED_ARRIVAL_TZ
,NEW.ADMIT_DATE_TZ
,NEW.EARLY_SCHEDULED_ARRIVAL_TZ
,NEW.NOT_YET_DELIVERED_TZ
,NEW.ORIG_DELIVER_NO_LATER_THAN_TZ
,NEW.ORIG_UNLOAD_DOCK_SCHED_AT_TZ
,NEW.EARLY_SDSA_FLAG
,NEW.LOADED_DATE
,NEW.LOADED_DATE_TZ
,v_update_date
,v_os_user
,v_host
,nextval('audit_load_details_seq'));

RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_audit_load_details_trg() OWNER TO postgres;
/
--
-- TOC entry 997 (class 1255 OID 24578)
-- Name: trigger_fct_audit_load_materials_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_audit_load_materials_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

v_UPDATE_date timestamp := LOCALTIMESTAMP;
v_os_user varchar(250);
v_host varchar(64);
v_action char(1);

BEGIN

select sys_context('USERENV','OS_USER'), sys_context('USERENV','HOST')
into STRICT v_os_user, v_host;

IF (TG_OP = 'INSERT' OR TG_OP = 'UPDATE') THEN

IF (TG_OP = 'INSERT') THEN

v_action := 'I';

ELSE

v_action := 'U';

END IF;

INSERT INTO AUDIT_LOAD_MATERIALS(LOAD_MATERIAL_ID
,LOAD_DETAIL_ID
,RELEASE_ID
,work_order
,SHOP_ORDER
,CUST_PO_NUM
,part_num
,CUST_ITEM_NUM
,WEIGHT
,material_type
,PICKUP_INSTR
,DELIVERY_INSTR
,BOL
,DATE_CREATED
,CREATED_BY
,DATE_MODIFIED
,MODIFIED_BY
,ORDER_ID
,STATUS
,DROPOFF_LOAD_DETAIL_ID
,IS_READY
,PICKUP_DATE
,PIECES
,LENGTH
,CUST_LOCATION
,DOCK_NAME
,NODE_ID
,IN_INVENTORY
,part_description
,commodity_class_code
,gauge
,material_status
,mill_order_num
,mill_test_num
,nmfc
,product_id
,quantity
,sub_product_id
,thickness
,PACKAGE_TYPE
,AUDIT_DATE
,AUDIT_OS_USER
,AUDIT_HOST
,AUDIT_ACTION
,AUDIT_ID)
VALUES (NEW.LOAD_MATERIAL_ID
,NEW.LOAD_DETAIL_ID
,NEW.RELEASE_ID
,NEW.work_order
,NEW.SHOP_ORDER
,NEW.CUST_PO_NUM
,NEW.part_num
,NEW.CUST_ITEM_NUM
,NEW.WEIGHT
,NEW.material_type
,NEW.PICKUP_INSTR
,NEW.DELIVERY_INSTR
,NEW.BOL
,NEW.DATE_CREATED
,NEW.CREATED_BY
,NEW.DATE_MODIFIED
,NEW.MODIFIED_BY
,NEW.ORDER_ID
,NEW.STATUS
,NEW.DROPOFF_LOAD_DETAIL_ID
,NEW.IS_READY
,NEW.PICKUP_DATE
,NEW.PIECES
,NEW.LENGTH
,NEW.CUST_LOCATION
,NEW.DOCK_NAME
,NEW.NODE_ID
,NEW.IN_INVENTORY
,NEW.part_description
,NEW.commodity_class_code
,NEW.gauge
,NEW.material_status
,NEW.mill_order_num
,NEW.mill_test_num
,NEW.nmfc
,NEW.product_id
,NEW.quantity
,NEW.sub_product_id
,NEW.thickness
,NEW.PACKAGE_TYPE
,v_update_date
,v_os_user
,v_host
,v_action
,nextval('audit_load_materials_seq'));

ELSE

v_action := 'D';

INSERT INTO AUDIT_LOAD_MATERIALS(LOAD_MATERIAL_ID
,LOAD_DETAIL_ID
,RELEASE_ID
,work_order
,SHOP_ORDER
,CUST_PO_NUM
,part_num
,CUST_ITEM_NUM
,WEIGHT
,material_type
,PICKUP_INSTR
,DELIVERY_INSTR
,BOL
,DATE_CREATED
,CREATED_BY
,DATE_MODIFIED
,MODIFIED_BY
,ORDER_ID
,STATUS
,DROPOFF_LOAD_DETAIL_ID
,IS_READY
,PICKUP_DATE
,PIECES
,LENGTH
,CUST_LOCATION
,DOCK_NAME
,NODE_ID
,IN_INVENTORY
,part_description
,commodity_class_code
,gauge
,material_status
,mill_order_num
,mill_test_num
,nmfc
,product_id
,quantity
,sub_product_id
,thickness
,PACKAGE_TYPE
,AUDIT_DATE
,AUDIT_OS_USER
,AUDIT_HOST
,AUDIT_ACTION
,AUDIT_ID)
VALUES (OLD.LOAD_MATERIAL_ID
,OLD.LOAD_DETAIL_ID
,OLD.RELEASE_ID
,OLD.work_order
,OLD.SHOP_ORDER
,OLD.CUST_PO_NUM
,OLD.part_num
,OLD.CUST_ITEM_NUM
,OLD.WEIGHT
,OLD.material_type
,OLD.PICKUP_INSTR
,OLD.DELIVERY_INSTR
,OLD.BOL
,OLD.DATE_CREATED
,OLD.CREATED_BY
,OLD.DATE_MODIFIED
,OLD.MODIFIED_BY
,OLD.ORDER_ID
,OLD.STATUS
,OLD.DROPOFF_LOAD_DETAIL_ID
,OLD.IS_READY
,OLD.PICKUP_DATE
,OLD.PIECES
,OLD.LENGTH
,OLD.CUST_LOCATION
,OLD.DOCK_NAME
,OLD.NODE_ID
,OLD.IN_INVENTORY
,OLD.part_description
,OLD.commodity_class_code
,OLD.gauge
,OLD.material_status
,OLD.mill_order_num
,OLD.mill_test_num
,OLD.nmfc
,OLD.product_id
,OLD.quantity
,OLD.sub_product_id
,OLD.thickness
,OLD.PACKAGE_TYPE
,v_update_date
,v_os_user
,v_host
,v_action
,nextval('audit_load_materials_seq'));

END IF;

IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_audit_load_materials_trg() OWNER TO postgres;
/
--
-- TOC entry 996 (class 1255 OID 24573)
-- Name: trigger_fct_audit_loads_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_audit_loads_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

v_UPDATE_date timestamp := LOCALTIMESTAMP;
v_os_user varchar(250);
v_host varchar(64);

BEGIN

select sys_context('USERENV','OS_USER'), sys_context('USERENV','HOST')
into STRICT v_os_user, v_host
;

INSERT INTO AUDIT_LOADS(LOAD_ID
,ROUTE_ID
,LOAD_STATUS
,MARKET_TYPE
,COMMODITY_CD
,CONTAINER_CD
,ORG_ID
,LOCATION_ID
,PERSON_ID
,SHIPPER_REFERENCE_NUMBER
,BROKER_REFERENCE_NUMBER
,INBOUND_OUTBOUND_FLG
,DATE_CLOSED
,FEATURE_CODE
,SOURCE_IND
,MILEAGE
,PIECES
,WEIGHT
,WEIGHT_UOM_CODE
,TARGET_PRICE
,AWARDED_OFFER
,AWARD_PRICE
,AWARD_DATE
,DATE_CREATED
,CREATED_BY
,DATE_MODIFIED
,MODIFIED_BY
,MIN_WEIGHT
,MIN_PRICE
,RATE_TYPE
,UNIT_COST
,PRIORITY
,PAY_TERMS
,BILL_TO
,ONE_TIME_RATE_ID
,OUT_ROUTE_MILES
,AUTO_CD_FLG
,PRODUCT_TYPE
,FINALIZATION_STATUS
,PERMIT_LOAD
,TRAVEL_TIME
,CHK_BRN
,PREMIUM_AVAILABLE
,SCHEDULED
,DISPATCHER_SCHEDULE_ONLY
,TRUCK_WEIGHT
,GROSS_WEIGHT
,BARGE_NUM
,SC_FLAG
,OP_FLAG
,SW_FLAG
,SRR_FLAG
,SRM_FLAG
,MM_FLAG
,TEMPLATE_SET
,RELEASE_NUM
,LOD_ATTRIBUTE1
,LOD_ATTRIBUTE2
,LOD_ATTRIBUTE3
,LOD_ATTRIBUTE4
,LOD_ATTRIBUTE5
,LOD_ATTRIBUTE6
,ORIGINAL_SCHED_PICKUP_DATE
,ORIGINAL_SCHED_DELIVERY_DATE
,CARRIER_REFERENCE_NUMBER
,SHIPPER_PREMIUM_AVAILABLE
,AFTER_HOURS_CONTACT
,AFTER_HOURS_PHONE
,IS_READY
,REASON_CODE
,FIN_STATUS_DATE
,HFR_FLAG
,DELIVERY_SUCCESS
,FREIGHT_PAID_BY
,RATE_CONTACT
,DRIVER_NAME
,TRACTOR_ID
,DRIVER_LICENSE
,TRAILER
,UNIT_NUMBER
,COMMODITY_DESC
,CUSTOMER_TRUCK_FLAG
,EMPTY_WEIGHT
,CUST_TRUCK_SCAC
,CUST_TRUCK_CARR_NAME
,CUST_TRUCK_PERSON_NAME
,CUST_TRUCK_PERSON_PHONE
,GL_DATE
,HAZMAT_FLAG
,RADIO_ACTIVE_FLAG
,OVER_HEIGHT_FLAG
,OVER_LENGTH_FLAG
,OVER_WIDTH_FLAG
,SUPER_LOAD_FLAG
,HEIGHT
,LENGTH
,WIDTH
,OVER_WEIGHT_FLAG
,NOTIFY_INITIAL_MSG_FLAG
,MULTI_DOCK_SCHED_RQRD
,PERMIT_NUM
,RADIO_ACTIVE_SECURE_FLAG
,BOOKED
,ORIG_SCHED_PICKUP_DATE_TZ
,ORIG_SCHED_DELIVERY_DATE_TZ
,ETD_OVR_FLG
,ETD_DATE
,ETD_DATE_TZ
,AWARDED_BY
,ORIGIN_REGION_ID
,DESTINATION_REGION_ID
,TARGET_RATE_MIN
,TARGET_RATE_MAX
,TARGET_RATE_OVR_FLG
,TARGET_RATE_ID_MIN
,TARGET_RATE_ID_MAX
,TARGET_TR_ID_MIN
,TARGET_TR_ID_MAX
,MILEAGE_TYPE
,MILEAGE_VERSION
,SHIP_LIGHT
,FRT_BILL_RECV_DATE
,FRT_BILL_RECV_BY
,FRT_BILL_NUMBER
,FINAN_NO_LOAD_FLAG
,GL_NUMBER
,BOL
,PO_NUM
,OP_BOL
,PART_NUM
,GL_REF_CODE
,AWARD_DEDICATED_UNIT_ID
,AUDIT_DATE
,AUDIT_OS_USER
,AUDIT_HOST
,AUDIT_ID)
VALUES (NEW.LOAD_ID
,NEW.ROUTE_ID
,NEW.LOAD_STATUS
,NEW.MARKET_TYPE
,NEW.COMMODITY_CD
,NEW.CONTAINER_CD
,NEW.ORG_ID
,NEW.LOCATION_ID
,NEW.PERSON_ID
,NEW.SHIPPER_REFERENCE_NUMBER
,NEW.BROKER_REFERENCE_NUMBER
,NEW.INBOUND_OUTBOUND_FLG
,NEW.DATE_CLOSED
,NEW.FEATURE_CODE
,NEW.SOURCE_IND
,NEW.MILEAGE
,NEW.PIECES
,NEW.WEIGHT
,NEW.WEIGHT_UOM_CODE
,NEW.TARGET_PRICE
,NEW.AWARDED_OFFER
,NEW.AWARD_PRICE
,NEW.AWARD_DATE
,NEW.DATE_CREATED
,NEW.CREATED_BY
,NEW.DATE_MODIFIED
,NEW.MODIFIED_BY
,NEW.MIN_WEIGHT
,NEW.MIN_PRICE
,NEW.RATE_TYPE
,NEW.UNIT_COST
,NEW.PRIORITY
,NEW.PAY_TERMS
,NEW.BILL_TO
,NEW.ONE_TIME_RATE_ID
,NEW.OUT_ROUTE_MILES
,NEW.AUTO_CD_FLG
,NEW.PRODUCT_TYPE
,NEW.FINALIZATION_STATUS
,NEW.PERMIT_LOAD
,NEW.TRAVEL_TIME
,NEW.CHK_BRN
,NEW.PREMIUM_AVAILABLE
,NEW.SCHEDULED
,NEW.DISPATCHER_SCHEDULE_ONLY
,NEW.TRUCK_WEIGHT
,NEW.GROSS_WEIGHT
,NEW.BARGE_NUM
,NEW.SC_FLAG
,NEW.OP_FLAG
,NEW.SW_FLAG
,NEW.SRR_FLAG
,NEW.SRM_FLAG
,NEW.MM_FLAG
,NEW.TEMPLATE_SET
,NEW.RELEASE_NUM
,NEW.LOD_ATTRIBUTE1
,NEW.LOD_ATTRIBUTE2
,NEW.LOD_ATTRIBUTE3
,NEW.LOD_ATTRIBUTE4
,NEW.LOD_ATTRIBUTE5
,NEW.LOD_ATTRIBUTE6
,NEW.ORIGINAL_SCHED_PICKUP_DATE
,NEW.ORIGINAL_SCHED_DELIVERY_DATE
,NEW.CARRIER_REFERENCE_NUMBER
,NEW.SHIPPER_PREMIUM_AVAILABLE
,NEW.AFTER_HOURS_CONTACT
,NEW.AFTER_HOURS_PHONE
,NEW.IS_READY
,NEW.REASON_CODE
,NEW.FIN_STATUS_DATE
,NEW.HFR_FLAG
,NEW.DELIVERY_SUCCESS
,NEW.FREIGHT_PAID_BY
,NEW.RATE_CONTACT
,NEW.DRIVER_NAME
,NEW.TRACTOR_ID
,NEW.DRIVER_LICENSE
,NEW.TRAILER
,NEW.UNIT_NUMBER
,NEW.COMMODITY_DESC
,NEW.CUSTOMER_TRUCK_FLAG
,NEW.EMPTY_WEIGHT
,NEW.CUST_TRUCK_SCAC
,NEW.CUST_TRUCK_CARR_NAME
,NEW.CUST_TRUCK_PERSON_NAME
,NEW.CUST_TRUCK_PERSON_PHONE
,NEW.GL_DATE
,NEW.HAZMAT_FLAG
,NEW.RADIO_ACTIVE_FLAG
,NEW.OVER_HEIGHT_FLAG
,NEW.OVER_LENGTH_FLAG
,NEW.OVER_WIDTH_FLAG
,NEW.SUPER_LOAD_FLAG
,NEW.HEIGHT
,NEW.LENGTH
,NEW.WIDTH
,NEW.OVER_WEIGHT_FLAG
,NEW.NOTIFY_INITIAL_MSG_FLAG
,NEW.MULTI_DOCK_SCHED_RQRD
,NEW.PERMIT_NUM
,NEW.RADIO_ACTIVE_SECURE_FLAG
,NEW.BOOKED
,NEW.ORIG_SCHED_PICKUP_DATE_TZ
,NEW.ORIG_SCHED_DELIVERY_DATE_TZ
,NEW.ETD_OVR_FLG
,NEW.ETD_DATE
,NEW.ETD_DATE_TZ
,NEW.AWARDED_BY
,NEW.ORIGIN_REGION_ID
,NEW.DESTINATION_REGION_ID
,NEW.TARGET_RATE_MIN
,NEW.TARGET_RATE_MAX
,NEW.TARGET_RATE_OVR_FLG
,NEW.TARGET_RATE_ID_MIN
,NEW.TARGET_RATE_ID_MAX
,NEW.TARGET_TR_ID_MIN
,NEW.TARGET_TR_ID_MAX
,NEW.MILEAGE_TYPE
,NEW.MILEAGE_VERSION
,NEW.SHIP_LIGHT
,NEW.FRT_BILL_RECV_DATE
,NEW.FRT_BILL_RECV_BY
,NEW.FRT_BILL_NUMBER
,NEW.FINAN_NO_LOAD_FLAG
,NEW.GL_NUMBER
,NEW.BOL
,NEW.PO_NUM
,NEW.OP_BOL
,NEW.PART_NUM
,NEW.GL_REF_CODE
,NEW.AWARD_DEDICATED_UNIT_ID
,v_update_date
,v_os_user
,v_host
,nextval('audit_loads_seq'));

RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_audit_loads_trg() OWNER TO postgres;
/
--
-- TOC entry 994 (class 1255 OID 24580)
-- Name: trigger_fct_audit_map_adr_node_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_audit_map_adr_node_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

v_UPDATE_date  timestamp := LOCALTIMESTAMP;
v_os_user       varchar(250);
v_host          varchar(64);

BEGIN

select sys_context('USERENV','OS_USER'), sys_context('USERENV','HOST')
into STRICT v_os_user, v_host
;

INSERT INTO AUDIT_MAP_ADR_NODE(NODE_ID
    ,ORG_ID
    ,COMPANY
    ,ADDRESS_ID
    ,DATE_CREATED
    ,CREATED_BY
    ,DATE_MODIFIED
    ,MODIFIED_BY
    ,NODE_NAME
    ,BILL_TO
    ,LOADING_HOURS
    ,RECEIVING_HOURS
    ,INSTRUCTIONS
    ,RAIL_CAPABLE
    ,MASTER_NODE_ID
    ,STATUS
    ,NODE_TYPE
    ,AUDIT_DATE
    ,AUDIT_OS_USER
    ,AUDIT_HOST
    ,audit_id)
VALUES (OLD.NODE_ID
    ,OLD.ORG_ID
    ,OLD.COMPANY
    ,OLD.ADDRESS_ID
    ,OLD.DATE_CREATED
    ,OLD.CREATED_BY
    ,OLD.DATE_MODIFIED
    ,OLD.MODIFIED_BY
    ,OLD.NODE_NAME
    ,OLD.BILL_TO
    ,OLD.LOADING_HOURS
    ,OLD.RECEIVING_HOURS
    ,OLD.INSTRUCTIONS
    ,OLD.RAIL_CAPABLE
    ,OLD.MASTER_NODE_ID
    ,OLD.STATUS
    ,OLD.NODE_TYPE
    ,v_update_date
    ,v_os_user
    ,v_host
    ,nextval('audit_map_adr_node_seq'));

IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_audit_map_adr_node_trg() OWNER TO postgres;
/
--
-- TOC entry 1000 (class 1255 OID 42846)
-- Name: trigger_fct_aupd_carrier_equipment_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_aupd_carrier_equipment_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

v_UPDATE_date  timestamp := LOCALTIMESTAMP;
v_os_user       varchar(250);
v_host          varchar(64);

BEGIN

select sys_context('USERENV','OS_USER'), sys_context('USERENV','HOST')
into STRICT v_os_user, v_host;

INSERT INTO AUDIT_CARRIER_EQUIPMENT(CEQ_ID
,EQUIPMENT_TYPE
,OWNED
,LEASED
,OWNER_OP
,GPS
,OVERWEIGHT
,MAX_WEIGHT
,DATE_CREATED
,CREATED_BY
,DATE_VERIFIED
,VERIFIED_BY
,CARRIER_ID
,AUDIT_DATE
,AUDIT_OS_USER
,AUDIT_HOST
,AUDIT_ID)
VALUES (OLD.CEQ_ID
,OLD.EQUIPMENT_TYPE
,OLD.OWNED
,OLD.LEASED
,OLD.OWNER_OP
,OLD.GPS
,OLD.OVERWEIGHT
,OLD.MAX_WEIGHT
,OLD.DATE_CREATED
,OLD.CREATED_BY
,OLD.DATE_VERIFIED
,OLD.VERIFIED_BY
,OLD.CARRIER_ID
,v_update_date
,v_os_user
,v_host
,nextval('audit_ceq_seq'));

IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_aupd_carrier_equipment_trg() OWNER TO postgres;
/
--
-- TOC entry 1001 (class 1255 OID 42847)
-- Name: trigger_fct_aupd_carrier_insurance_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_aupd_carrier_insurance_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

v_UPDATE_date  timestamp := LOCALTIMESTAMP;
v_os_user       varchar(250);
v_host          varchar(64);

BEGIN

select sys_context('USERENV','OS_USER'), sys_context('USERENV','HOST')
into STRICT v_os_user, v_host;

INSERT INTO AUDIT_CARRIER_INSURANCE(NOTIFICATION_LEVEL
,COUNTRY_CODE
,AGENT
,INSURER_ID
,CINS_ID
,INSURANCE_TYPE
,EXPIRE_DATE
,AMOUNT
,DATE_CREATED
,CREATED_BY
,DATE_VERIFIED
,VERIFIED_BY
,CARRIER_ID
,AUDIT_DATE
,AUDIT_OS_USER
,AUDIT_HOST
,AUDIT_ID)
VALUES (OLD.NOTIFICATION_LEVEL
,OLD.COUNTRY_CODE
,OLD.AGENT
,OLD.INSURER_ID
,OLD.CINS_ID
,OLD.INSURANCE_TYPE
,OLD.EXPIRE_DATE
,OLD.AMOUNT
,OLD.DATE_CREATED
,OLD.CREATED_BY
,OLD.DATE_VERIFIED
,OLD.VERIFIED_BY
,OLD.CARRIER_ID
,v_update_date
,v_os_user
,v_host
,nextval('audit_cins_seq'));

IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_aupd_carrier_insurance_trg() OWNER TO postgres;
/
--
-- TOC entry 1002 (class 1255 OID 42848)
-- Name: trigger_fct_aupd_finan_int_resp_stats(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_aupd_finan_int_resp_stats() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

BEGIN

insert into finan_int_responses_stats values (OLD.finan_int_resp_id, LOCALTIMESTAMP);


RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_aupd_finan_int_resp_stats() OWNER TO postgres;
/
--
-- TOC entry 1003 (class 1255 OID 42849)
-- Name: trigger_fct_aupd_finan_int_resp_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_aupd_finan_int_resp_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  if (NEW.SENT_AP_TO_FINANCE != 'N' or OLD.SENT_AP_TO_FINANCE = 'X') and (NEW.SENT_AR_TO_FINANCE != 'N' or OLD.SENT_AR_TO_FINANCE = 'X') and OLD.SENT_TO_FINANCE = 'N' then
        NEW.SENT_TO_FINANCE := 'Y';
  end if;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_aupd_finan_int_resp_trg() OWNER TO postgres;
/
--
-- TOC entry 1004 (class 1255 OID 42850)
-- Name: trigger_fct_aupd_users_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_aupd_users_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  l_usm_list_count bigint;
BEGIN

  if NEW.status = 'I' then
    update user_customer uc
       set uc.exp_date = date_trunc('day', LOCALTIMESTAMP),
        uc.modified_date = LOCALTIMESTAMP
     where uc.person_id = NEW.person_id
       and date_trunc('day', uc.exp_date) > date_trunc('day', LOCALTIMESTAMP);

    select count(1)
      into STRICT l_usm_list_count
      from user_monitor_list
     where person_id = NEW.person_id
       and status = 'A';

    if l_usm_list_count > 0 then
      update user_monitor_list
         set status = 'I'
       where person_id = NEW.person_id
         and status = 'A';
    end if;
  end if;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_aupd_users_trg() OWNER TO postgres;
/
--
-- TOC entry 1044 (class 1255 OID 42854)
-- Name: trigger_fct_bill_to_default_values_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bill_to_default_values_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.BILL_TO_DEFAULT_VALUES(
  moduser,modtime,modstatus,

  BILL_TO_DEFAULT_VALUE_ID     ,
  BILL_TO_ID                   ,
  INBOUND_OUTBOUND             ,
  EDI_INBOUND_OUTBOUND         ,
  PAY_TERMS                    ,
  EDI_PAY_TERMS                ,
  DATE_CREATED                 ,
  CREATED_BY                   ,
  DATE_MODIFIED                ,
  MODIFIED_BY                  ,
  VERSION                      ,
  MANUAL_BOL_INBOUND_OUTBOUND  ,
  MANUAL_BOL_PAY_TERMS         
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.BILL_TO_DEFAULT_VALUE_ID     ,
  OLD.BILL_TO_ID                   ,
  OLD.INBOUND_OUTBOUND             ,
  OLD.EDI_INBOUND_OUTBOUND         ,
  OLD.PAY_TERMS                    ,
  OLD.EDI_PAY_TERMS                ,
  OLD.DATE_CREATED                 ,
  OLD.CREATED_BY                   ,
  OLD.DATE_MODIFIED                ,
  OLD.MODIFIED_BY                  ,
  OLD.VERSION                      ,
  OLD.MANUAL_BOL_INBOUND_OUTBOUND  ,
  OLD.MANUAL_BOL_PAY_TERMS         
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bill_to_default_values_hist() OWNER TO postgres;
/
--
-- TOC entry 1008 (class 1255 OID 42856)
-- Name: trigger_fct_bill_to_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bill_to_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN

insert into hist.bill_to(
   moduser,modtime,modstatus,
   bill_to_id, name, org_id,
   date_created, created_by, date_modified,
   modified_by, rate_contact_name, rate_email_address,
   rate_fax_area_code, rate_fax_number, payment_method,
   version, credit_limit, credit_hold,
   override_credit_hold, auto_credit_hold, warning_no_of_days,
   warning_date_start, unbilled_rev, currency_code,
   pay_terms, status, is_default,
   pay_terms_id)
values (user,CURRENT_TIMESTAMP,null /*case when inserting then 'I' else 'D' end*/
,
   OLD.bill_to_id, OLD.name, OLD.org_id,
   OLD.date_created, OLD.created_by, OLD.date_modified,
   OLD.modified_by, OLD.rate_contact_name, OLD.rate_email_address,
   OLD.rate_fax_area_code, OLD.rate_fax_number, OLD.payment_method,
   OLD.version, OLD.credit_limit, OLD.credit_hold,
   OLD.override_credit_hold, OLD.auto_credit_hold, OLD.warning_no_of_days,
   OLD.warning_date_start, OLD.unbilled_rev, OLD.currency_code,
   OLD.pay_terms, OLD.status, OLD.is_default,
   OLD.pay_terms_id);

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bill_to_hist() OWNER TO postgres;
/
--
-- TOC entry 1045 (class 1255 OID 42858)
-- Name: trigger_fct_bill_to_req_field_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bill_to_req_field_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.BILL_TO_REQ_FIELD(
  moduser,modtime,modstatus,

  BILL_TO_REQ_FIELD_ID  ,
  BILL_TO_ID            ,
  FIELD_NAME            ,
  STATUS                ,
  DATE_CREATED          ,
  CREATED_BY            ,
  DATE_MODIFIED         ,
  MODIFIED_BY           ,
  VERSION               
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.BILL_TO_REQ_FIELD_ID  ,
  OLD.BILL_TO_ID            ,
  OLD.FIELD_NAME            ,
  OLD.STATUS                ,
  OLD.DATE_CREATED          ,
  OLD.CREATED_BY            ,
  OLD.DATE_MODIFIED         ,
  OLD.MODIFIED_BY           ,
  OLD.VERSION         
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bill_to_req_field_hist() OWNER TO postgres;
/
--
-- TOC entry 1009 (class 1255 OID 42860)
-- Name: trigger_fct_bill_to_thresh_sett_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bill_to_thresh_sett_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.BILL_TO_THRESHOLD_SETTINGS(
  moduser,modtime,modstatus,

  BILL_TO_AUDIT_THRESHOLD_ID  ,
  BILL_TO_ID                  ,
  THRESHOLD_VALUE             ,
  TOTAL_REVENUE               ,
  MARGIN                      ,
  DATE_CREATED                ,
  CREATED_BY                  ,
  DATE_MODIFIED               ,
  MODIFIED_BY                 ,
  VERSION                     
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.BILL_TO_AUDIT_THRESHOLD_ID  ,
  OLD.BILL_TO_ID                  ,
  OLD.THRESHOLD_VALUE             ,
  OLD.TOTAL_REVENUE               ,
  OLD.MARGIN                      ,
  OLD.DATE_CREATED                ,
  OLD.CREATED_BY                  ,
  OLD.DATE_MODIFIED               ,
  OLD.MODIFIED_BY                 ,
  OLD.VERSION                              
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bill_to_thresh_sett_hist() OWNER TO postgres;
/
--
-- TOC entry 1005 (class 1255 OID 42852)
-- Name: trigger_fct_billing_invoice_node_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_billing_invoice_node_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

BEGIN

IF (OLD.customer_id != NEW.customer_id AND OLD.customer_number != NEW.customer_number) THEN

    NEW.customer_id := OLD.customer_id;
    NEW.customer_number := OLD.customer_number;

END IF;


RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_billing_invoice_node_trg() OWNER TO postgres;
/
--
-- TOC entry 1047 (class 1255 OID 42875)
-- Name: trigger_fct_bin_bupd_bins_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bin_bupd_bins_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  l_contact_email varchar(255);
  l_count         bigint;
BEGIN
  if NEW.contact_email is null then
    select count(email_address)
      into STRICT l_count
      from (SELECT distinct loc.location_id, u.email_address
              from organization_locations loc
             inner join user_customer uc
                on uc.location_id = loc.location_id
               and uc.org_id = loc.org_id
               and loc.status = 'A'
               and loc.bill_to = NEW.bill_to_id
               and uc.status = 'A'
               and uc.exp_date > LOCALTIMESTAMP
             inner join users u
                on u.person_id = uc.person_id
               and u.status = 'A') alias1;
    if l_count > 0 then
      select string_agg(email_address, ';' order by email_address) over (partition by location_id) as email
        into STRICT l_contact_email
        from (SELECT distinct loc.location_id, u.email_address
                from organization_locations loc
               inner join user_customer uc
                  on uc.location_id = loc.location_id
                 and uc.org_id = loc.org_id
                 and loc.status = 'A'
                 and loc.bill_to = NEW.bill_to_id
                 and uc.status = 'A'
                 and uc.exp_date > LOCALTIMESTAMP
               inner join users u
                  on u.person_id = uc.person_id
                 and u.status = 'A') alias2;
      NEW.contact_email := l_contact_email;
    end if;
  end if;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bin_bupd_bins_trg() OWNER TO postgres;
/
--
-- TOC entry 1011 (class 1255 OID 42877)
-- Name: trigger_fct_bin_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bin_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN

insert into hist.billing_invoice_node(
   moduser,modtime,modstatus,
   billing_node_id, bill_to_id, network_id,
   customer_id, customer_number, address_id,
   version, date_created, created_by,
   date_modified, modified_by, contact_name,
   phone_id, fax_id, contact_email,
   customs_broker, broker_phone_id)
values (user,CURRENT_TIMESTAMP,null /*case when inserting then 'I' else 'D' end*/
,
   OLD.billing_node_id, OLD.bill_to_id, OLD.network_id,
   OLD.customer_id, OLD.customer_number, OLD.address_id,
   OLD.version, OLD.date_created, OLD.created_by,
   OLD.date_modified, OLD.modified_by, OLD.contact_name,
   OLD.phone_id, OLD.fax_id, OLD.contact_email,
   OLD.customs_broker, OLD.broker_phone_id);

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bin_hist() OWNER TO postgres;
/
--
-- TOC entry 1012 (class 1255 OID 42879)
-- Name: trigger_fct_bin_pls_cl_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bin_pls_cl_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN
 insert into pls_credit_limit_tmp(creditmax,accountnum,bill_to_id)
    SELECT
    1 creditmax,
    moc.company || NEW.customer_number || '-' || NEW.customer_id as accountnum,
    NEW.bill_to_id
    from bill_to bt
    inner join map_org_company moc
      on moc.org_id = bt.org_id      
    inner join organizations o
      on o.org_id = bt.org_id
    where bt.bill_to_id = NEW.bill_to_id
      and o.network_id in (4,6,7,19);
exception
 when OTHERS then
  null;
  END;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bin_pls_cl_trg() OWNER TO postgres;
/
--
-- TOC entry 1019 (class 1255 OID 42865)
-- Name: trigger_fct_bins_bill_to_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bins_bill_to_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  l_network_id bigint;
BEGIN

  IF (NEW.currency_code IS NULL) THEN
    NEW.currency_code := 'USD';
  END IF;

  IF (NEW.pay_terms_id IS NULL) THEN
    NEW.pay_terms_id := 17; --Net 30 days
  END IF;

  select coalesce(network_id, -1)
    into STRICT l_network_id
    from organizations org
   where org_id = NEW.org_id;
  if l_network_id in (4, 6) then
    NEW.email_account_executive := 1;
  else
    NEW.email_account_executive := 0;
  end if;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bins_bill_to_trg() OWNER TO postgres;
/
--
-- TOC entry 1006 (class 1255 OID 42867)
-- Name: trigger_fct_bins_finan_account_rec_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bins_finan_account_rec_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  l_total_revenue      double precision;

BEGIN
  NEW.TOTAL_AMT_APPLIED := NEW.AMT_APPLIED;
  if (NEW.INV_ACTUAL_DATE_CLOSED is not null) then
     if (NEW.FAA_DETAIL_ID is not null) then
      select total_revenue
        into STRICT l_total_revenue
        from  FINAN_ADJ_ACC_DETAIL faad
        where faad.faa_detail_id = NEW.FAA_DETAIL_ID;
     end if;
      if (NEW.FAA_DETAIL_ID is null) then
      select total_revenue
        into STRICT l_total_revenue
        from  LOAD_COST_DETAILS lcd
        where lcd.load_id = NEW.load_id and lcd.status = 'A' and lcd.gl_date is not null;
     end if;
     if (NEW.TOTAL_AMT_APPLIED <> l_total_revenue) then
        NEW.TOTAL_AMT_APPLIED := l_total_revenue;
     end if;
  end if;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bins_finan_account_rec_trg() OWNER TO postgres;
/
--
-- TOC entry 1007 (class 1255 OID 42869)
-- Name: trigger_fct_bins_ltl_product_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bins_ltl_product_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
    IF (NEW.person_id IS NOT NULL) THEN
      NEW.person_id := NULL;
   END IF;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bins_ltl_product_trg() OWNER TO postgres;
/
--
-- TOC entry 1010 (class 1255 OID 42871)
-- Name: trigger_fct_bins_organizations_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bins_organizations_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
   IF (NEW.org_type = 'CARRIER') THEN
          NEW.currency_code := 'USD';
   END IF;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bins_organizations_trg() OWNER TO postgres;
/
--
-- TOC entry 1025 (class 1255 OID 42873)
-- Name: trigger_fct_bins_user_addr_book_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bins_user_addr_book_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
    IF (NEW.person_id IS NOT NULL) THEN
      NEW.person_id := NULL;
   END IF;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bins_user_addr_book_trg() OWNER TO postgres;
/
--
-- TOC entry 1046 (class 1255 OID 42862)
-- Name: trigger_fct_binsupd_loads_disp_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_binsupd_loads_disp_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  l_load_id        bigint;
  l_date_created   timestamp;

  l_load_disp_info bigint;
  l_cnt_disp_loads bigint;
BEGIN
  BEGIN
 BEGIN
      select load_id, max(f.load_dispatch_info_id)
        into STRICT l_load_id, l_load_disp_info
        from load_dispatch_information f
       where load_id = NEW.load_id
       group by load_id;

    exception
      when no_data_found then
        l_load_id        := null;
        l_load_disp_info := null;
      end;
       select count(1)
    into STRICT l_cnt_disp_loads
    from load_dispatch_information
   where load_id = NEW.load_id;


  if (l_cnt_disp_loads < 1 and NEW.load_status = 'PP' and
     NEW.originating_system in ('PLS2_LT', 'GS')) then
    insert into load_dispatch_information
    values (nextval('load_dispatch_info_seq'),
       NEW.load_id,
       'A',
       LOCALTIMESTAMP,
       NEW.modified_by,
       LOCALTIMESTAMP,
       NEW.modified_by);

  elsif (NEW.load_status != 'PP' and OLD.load_status = 'PP' and
        NEW.originating_system in ('PLS2_LT', 'GS')) then


    update load_dispatch_information
       set status        = 'A',
           date_modified = LOCALTIMESTAMP,
           modified_by   = NEW.person_id
     where load_dispatch_info_id = l_load_disp_info
       and load_id = l_load_id;


  elsif (l_cnt_disp_loads > 0 and NEW.load_status = 'PP' and OLD.load_status != 'PP') then
    update load_dispatch_information
       set status        = 'I',
           date_modified = LOCALTIMESTAMP,
           modified_by   = NEW.person_id
     where load_id = l_load_id;

    insert into load_dispatch_information
    values (nextval('load_dispatch_info_seq'),
       NEW.load_id,
       'A',
       LOCALTIMESTAMP,
       NEW.modified_by,
       LOCALTIMESTAMP,
       NEW.modified_by);

  end if;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_binsupd_loads_disp_trg() OWNER TO postgres;
/
--
-- TOC entry 1013 (class 1255 OID 42881)
-- Name: trigger_fct_biu_load_part_num_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_biu_load_part_num_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  -- local variables here
BEGIN
if ((NEW.so_number is not null or OLD.so_number is not null)  and NEW.originating_system in ('PLS2_LT', 'GS'))
  then
    NEW.part_num := NEW.so_number;
    end if;

  
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_biu_load_part_num_trg() OWNER TO postgres;
/
--
-- TOC entry 1014 (class 1255 OID 42883)
-- Name: trigger_fct_bupd_bill_to_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bupd_bill_to_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  l_network_id bigint;
BEGIN
  if (NEW.currency_code is not null) or (NEW.currency_code <> '') then
    IF (NEW.currency_code <> 'CAD') THEN
      NEW.currency_code := 'USD';
    END IF;
  else
    NEW.currency_code := 'USD';
  end if;

  IF (NEW.pay_terms_id IS NULL) THEN
    NEW.pay_terms_id := 17; --Net 30 days
  END IF;

  select coalesce(network_id, -1)
    into STRICT l_network_id
    from organizations org
   where org_id = NEW.org_id;
  if l_network_id in (4, 6) then
    NEW.email_account_executive := 1;
  else
    NEW.email_account_executive := 0;
  end if;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bupd_bill_to_trg() OWNER TO postgres;
/
--
-- TOC entry 1015 (class 1255 OID 42885)
-- Name: trigger_fct_bupd_finan_account_rec_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bupd_finan_account_rec_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  l_total_revenue      double precision;

BEGIN
  NEW.TOTAL_AMT_APPLIED := NEW.AMT_APPLIED;
  if (NEW.INV_ACTUAL_DATE_CLOSED is not null) then
     if (NEW.FAA_DETAIL_ID is not null) then
      select total_revenue
        into STRICT l_total_revenue
        from  FINAN_ADJ_ACC_DETAIL faad
        where faad.faa_detail_id = NEW.FAA_DETAIL_ID;
     end if;
      if (NEW.FAA_DETAIL_ID is null) then
      select total_revenue
        into STRICT l_total_revenue
        from  LOAD_COST_DETAILS lcd
        where lcd.load_id = NEW.load_id and lcd.status = 'A' and lcd.gl_date is not null;
     end if;
     if (NEW.TOTAL_AMT_APPLIED <> l_total_revenue) then
        NEW.TOTAL_AMT_APPLIED := l_total_revenue;
     end if;
  end if;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bupd_finan_account_rec_trg() OWNER TO postgres;
/
--
-- TOC entry 1016 (class 1255 OID 42887)
-- Name: trigger_fct_bupd_organizations_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_bupd_organizations_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
   IF (NEW.org_type = 'CARRIER') THEN
      if (NEW.currency_code is not null) or (NEW.currency_code <> '') then
         if NEW.currency_code <> 'CAD' then
            NEW.currency_code := 'USD';
         end if;
      else
         NEW.currency_code := 'USD';
      end if;
   END IF;

RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_bupd_organizations_trg() OWNER TO postgres;
/
--
-- TOC entry 1018 (class 1255 OID 42891)
-- Name: trigger_fct_carrier_inv_addr_details_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_carrier_inv_addr_details_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.CARRIER_INVOICE_ADDR_DETAILS(
  moduser,modtime,modstatus,

  ADDR_DET_ID     ,
  INVOICE_DET_ID  ,
  ADDRESS_TYPE    ,
  ADDRESS_NAME    ,
  ADDRESS1        ,
  ADDRESS2        ,
  CITY            ,
  STATE           ,
  POSTAL_CODE     ,
  COUNTRY_CODE    ,
  DATE_CREATED    ,
  CREATED_BY      ,
  DATE_MODIFIED   ,
  MODIFIED_BY     ,
  VERSION         
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.ADDR_DET_ID     ,
  OLD.INVOICE_DET_ID  ,
  OLD.ADDRESS_TYPE    ,
  OLD.ADDRESS_NAME    ,
  OLD.ADDRESS1        ,
  OLD.ADDRESS2        ,
  OLD.CITY            ,
  OLD.STATE           ,
  OLD.POSTAL_CODE     ,
  OLD.COUNTRY_CODE    ,
  OLD.DATE_CREATED    ,
  OLD.CREATED_BY      ,
  OLD.DATE_MODIFIED   ,
  OLD.MODIFIED_BY     ,
  OLD.VERSION         
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_carrier_inv_addr_details_hist() OWNER TO postgres;
/
--
-- TOC entry 1048 (class 1255 OID 42893)
-- Name: trigger_fct_carrier_inv_cost_items_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_carrier_inv_cost_items_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.CARRIER_INVOICE_COST_ITEMS(
  moduser,modtime,modstatus,

  INVOICE_COST_DETAIL_ITEM_ID  ,
  INVOICE_DET_ID               ,
  REF_TYPE                     ,
  SUBTOTAL                     ,
  DATE_CREATED                 ,
  CREATED_BY                   ,
  DATE_MODIFIED                ,
  MODIFIED_BY                  ,
  VERSION                        
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.INVOICE_COST_DETAIL_ITEM_ID  ,
  OLD.INVOICE_DET_ID               ,
  OLD.REF_TYPE                     ,
  OLD.SUBTOTAL                     ,
  OLD.DATE_CREATED                 ,
  OLD.CREATED_BY                   ,
  OLD.DATE_MODIFIED                ,
  OLD.MODIFIED_BY                  ,
  OLD.VERSION                      
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_carrier_inv_cost_items_hist() OWNER TO postgres;
/
--
-- TOC entry 1017 (class 1255 OID 42889)
-- Name: trigger_fct_carrier_invoice_details_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_carrier_invoice_details_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.CARRIER_INVOICE_DETAILS(
  moduser,modtime,modstatus,

  INVOICE_DET_ID            ,
  INVOICE_NUM               ,
  INVOICE_DATE              ,
  REFERENCE_NUM             ,
  PAY_TERMS                 ,
  NET_AMOUNT                ,
  DELIVERY_DATE             ,
  EST_DELIVERY_DATE         ,
  BOL                       ,
  PO_NUM                    ,
  SHIPPER_REFERENCE_NUMBER  ,
  PRO_NUMBER                ,
  ACT_PICKUP_DATE           ,
  TOTAL_WEIGHT              ,
  TOTAL_CHARGES             ,
  TOTAL_QUANTITY            ,
  MATCHED                   ,
  MATCHED_LOAD_ID           ,
  CARRIER_ID                ,
  STATUS                    ,
  DATE_CREATED              ,
  CREATED_BY                ,
  DATE_MODIFIED             ,
  MODIFIED_BY               ,
  VERSION                   ,
  EDI                       ,
  EDI_ACCOUNT               
)
values (user,CURRENT_TIMESTAMP,v_status,
  OLD.INVOICE_DET_ID            ,
  OLD.INVOICE_NUM               ,
  OLD.INVOICE_DATE              ,
  OLD.REFERENCE_NUM             ,
  OLD.PAY_TERMS                 ,
  OLD.NET_AMOUNT                ,
  OLD.DELIVERY_DATE             ,
  OLD.EST_DELIVERY_DATE         ,
  OLD.BOL                       ,
  OLD.PO_NUM                    ,
  OLD.SHIPPER_REFERENCE_NUMBER  ,
  OLD.PRO_NUMBER                ,
  OLD.ACT_PICKUP_DATE           ,
  OLD.TOTAL_WEIGHT              ,
  OLD.TOTAL_CHARGES             ,
  OLD.TOTAL_QUANTITY            ,
  OLD.MATCHED                   ,
  OLD.MATCHED_LOAD_ID           ,
  OLD.CARRIER_ID                ,
  OLD.STATUS                    ,
  OLD.DATE_CREATED              ,
  OLD.CREATED_BY                ,
  OLD.DATE_MODIFIED             ,
  OLD.MODIFIED_BY               ,
  OLD.VERSION                   ,
  OLD.EDI                       ,
  OLD.EDI_ACCOUNT               
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_carrier_invoice_details_hist() OWNER TO postgres;
/
--
-- TOC entry 1049 (class 1255 OID 42895)
-- Name: trigger_fct_countries_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_countries_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.COUNTRIES(
  moduser,modtime,modstatus,

  COUNTRY_CODE      ,
  NAME              ,
  DIALING_CODE      ,
  STATUS            ,
  COUNTRY_CD_SHORT  
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.COUNTRY_CODE      ,
  OLD.NAME              ,
  OLD.DIALING_CODE      ,
  OLD.STATUS            ,
  OLD.COUNTRY_CD_SHORT  
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_countries_hist() OWNER TO postgres;
/
--
-- TOC entry 1020 (class 1255 OID 42897)
-- Name: trigger_fct_ctsi_audit_data_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_ctsi_audit_data_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  -- local variables here
BEGIN

insert into pls_ctsi_audit_data_stage
values (NEW.FILETYPE,
NEW.SHIPMENT_NO,
NEW.ORG_ID,
NEW.DATE_CREATED,
NEW.bol,
NEW.carrierreferencenumber,
NEW.scac
);
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_ctsi_audit_data_trg() OWNER TO postgres;
/
--
-- TOC entry 1021 (class 1255 OID 42898)
-- Name: trigger_fct_edi_bins_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_edi_bins_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

BEGIN

NEW.original_srn := NEW.shipper_reference_number;

--IF (:new.file_type IS NOT NULL and :new.file_type in ('LB','INV')) THEN
  --  :new.status := 'H';
--END IF;
RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_edi_bins_trg() OWNER TO postgres;
/
--
-- TOC entry 1022 (class 1255 OID 42900)
-- Name: trigger_fct_edi_settings_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_edi_settings_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.EDI_SETTINGS(
  moduser,modtime,modstatus,

  EDI_SETTINGS_ID     ,
  BILL_TO_ID          ,
  EDI_TYPE            ,
  EDI_STATUS          ,
  UNIQUE_REF_BOL      ,
  CREATED_BY          ,
  DATE_CREATED        ,
  MODIFIED_BY         ,
  DATE_MODIFIED       ,
  VERSION             ,
  IGNORE_204_UPDATES  
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.EDI_SETTINGS_ID     ,
  OLD.BILL_TO_ID          ,
  OLD.EDI_TYPE            ,
  OLD.EDI_STATUS          ,
  OLD.UNIQUE_REF_BOL      ,
  OLD.CREATED_BY          ,
  OLD.DATE_CREATED        ,
  OLD.MODIFIED_BY         ,
  OLD.DATE_MODIFIED       ,
  OLD.VERSION             ,
  OLD.IGNORE_204_UPDATES  
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_edi_settings_hist() OWNER TO postgres;
/
--
-- TOC entry 1050 (class 1255 OID 42902)
-- Name: trigger_fct_finan_adj_acc_detail_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_finan_adj_acc_detail_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.FINAN_ADJ_ACC_DETAIL(
  moduser,modtime,modstatus,

  FAA_DETAIL_ID, 
  LOAD_ID, 
  BOL, 
  ADJ_ACC, 
  REVISION, 
  REASON, 
  GL_DATE, 
  FAA_STATUS, 
  TOTAL_REVENUE, 
  TOTAL_COSTS, 
  SHORT_PAY, 
  SENT_TO_FINANCE, 
  CREATED_BY, 
  DATE_CREATED, 
  VERSION, 
  MODIFIED_BY, 
  DATE_MODIFIED, 
  STATUS, 
  INV_APPROVED, 
  CUSTOMER_INVOICE_NUM, 
  DO_NOT_INVOICE, 
  GROUP_INVOICE_NUM, 
  INVOICED_IN_FINAN
)
values (user,CURRENT_TIMESTAMP,v_status,
  OLD.FAA_DETAIL_ID, 
  OLD.LOAD_ID, 
  OLD.BOL, 
  OLD.ADJ_ACC, 
  OLD.REVISION, 
  OLD.REASON, 
  OLD.GL_DATE, 
  OLD.FAA_STATUS, 
  OLD.TOTAL_REVENUE, 
  OLD.TOTAL_COSTS, 
  OLD.SHORT_PAY, 
  OLD.SENT_TO_FINANCE, 
  OLD.CREATED_BY, 
  OLD.DATE_CREATED, 
  OLD.VERSION, 
  OLD.MODIFIED_BY, 
  OLD.DATE_MODIFIED, 
  OLD.STATUS, 
  OLD.INV_APPROVED, 
  OLD.CUSTOMER_INVOICE_NUM, 
  OLD.DO_NOT_INVOICE, 
  OLD.GROUP_INVOICE_NUM, 
  OLD.INVOICED_IN_FINAN

 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_finan_adj_acc_detail_hist() OWNER TO postgres;
/
--
-- TOC entry 1023 (class 1255 OID 42904)
-- Name: trigger_fct_invset_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_invset_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN

insert into hist.invoice_settings(
   moduser,modtime,modstatus,
   invoice_settings_id, bill_to_id, invoice_type,
   invoice_format_id, processing_type, processing_time,
   date_created, created_by, date_modified,
   modified_by, version, processing_time_tz,
   processing_period, gainshare_only, sort_type,
   documents, not_split_recipients, edi_invoice,
   cbi_invoice_type, release_day_of_week, processing_day_of_week)
values (user,CURRENT_TIMESTAMP,null /*case when inserting then 'I' else 'D' end*/
,
   OLD.invoice_settings_id, OLD.bill_to_id, OLD.invoice_type,
   OLD.invoice_format_id, OLD.processing_type, OLD.processing_time,
   OLD.date_created, OLD.created_by, OLD.date_modified,
   OLD.modified_by, OLD.version, OLD.processing_time_tz,
   OLD.processing_period, OLD.gainshare_only, OLD.sort_type,
   OLD.documents, OLD.not_split_recipients, OLD.edi_invoice,
   OLD.cbi_invoice_type, OLD.release_day_of_week, OLD.processing_day_of_week);

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_invset_hist() OWNER TO postgres;
/
--
-- TOC entry 1028 (class 1255 OID 42919)
-- Name: trigger_fct_load_details_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_load_details_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN
insert into hist.load_details(
   moduser,modtime,modstatus,
   load_detail_id, load_id, load_action,
   ticket, bol, arrival,
   departure, scheduled_arrival, need_appt,
   contact, instructions, date_created,
   created_by, date_modified, modified_by,
   po_num, node_id, op_bol,
   point_type, admit_date, no_early_schedule_reason,
   part_num, sdsa_flag, seq_in_route,
   address_id, early_scheduled_arrival, appointment_number,
   no_late_schedule_reason, not_yet_delivered, orig_deliver_no_later_than,
   orig_unload_dock_scheduled_at, arrival_tz, departure_tz,
   scheduled_arrival_tz, admit_date_tz, early_scheduled_arrival_tz,
   not_yet_delivered_tz, orig_deliver_no_later_than_tz, orig_unload_dock_sched_at_tz,
   contact_name, contact_phone, early_sdsa_flag,
   loaded_date, loaded_date_tz, version,
   arrival_window_start, arrival_window_end, contact_fax,
   contact_email, address_code, notes)
values (user,CURRENT_TIMESTAMP,null /*case when inserting then 'I' else 'D' end*/
,
 OLD.load_detail_id, OLD.load_id, OLD.load_action,
 OLD.ticket, OLD.bol, OLD.arrival,
 OLD.departure, OLD.scheduled_arrival, OLD.need_appt,
 OLD.contact, OLD.instructions, OLD.date_created,
 OLD.created_by, OLD.date_modified, OLD.modified_by,
 OLD.po_num, OLD.node_id, OLD.op_bol,
 OLD.point_type, OLD.admit_date, OLD.no_early_schedule_reason,
 OLD.part_num, OLD.sdsa_flag, OLD.seq_in_route,
 OLD.address_id, OLD.early_scheduled_arrival, OLD.appointment_number,
 OLD.no_late_schedule_reason, OLD.not_yet_delivered, OLD.orig_deliver_no_later_than,
 OLD.orig_unload_dock_scheduled_at, OLD.arrival_tz, OLD.departure_tz,
 OLD.scheduled_arrival_tz, OLD.admit_date_tz, OLD.early_scheduled_arrival_tz,
 OLD.not_yet_delivered_tz, OLD.orig_deliver_no_later_than_tz, OLD.orig_unload_dock_sched_at_tz,
 OLD.contact_name, OLD.contact_phone, OLD.early_sdsa_flag,
 OLD.loaded_date, OLD.loaded_date_tz, OLD.version,
 OLD.arrival_window_start, OLD.arrival_window_end, OLD.contact_fax,
 OLD.contact_email, OLD.address_code, OLD.notes);

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_load_details_hist() OWNER TO postgres;
/
--
-- TOC entry 1053 (class 1255 OID 42924)
-- Name: trigger_fct_load_materials_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_load_materials_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN
insert into hist.load_materials(
   moduser,modtime,modstatus,
   load_material_id, load_detail_id, release_id,
   work_order, shop_order, cust_owned,
   cust_po_num, part_num, cust_item_num,
   weight, material_type, pickup_instr,
   delivery_instr, bol, date_created,
   created_by, date_modified, modified_by,
   order_id, status, dropoff_load_detail_id,
   is_ready, heat_num, pickup_date,
   pieces, length, bundles,
   grade, cust_location, dock_name,
   node_id, in_inventory, pickup_date_tz,
   hazmat, package_type, width,
   height, part_description, commodity_class_code,
   gauge, material_status, mill_order_num,
   mill_test_num, nmfc, product_id,
   quantity, sub_product_id, thickness,
   hazmat_class, un_num, packing_group,
   emergency_number, version, original_weight,
   original_freight_class, contract, hazmat_instructions,
   emergency_contract, emergency_company, emergency_country_code,
   emergency_area_code, ltl_product_id, ltl_package_type,
   stackable)
values (user,CURRENT_TIMESTAMP,null /*case when inserting then 'I' else 'D' end*/
,
    OLD.load_material_id, OLD.load_detail_id, OLD.release_id,
    OLD.work_order, OLD.shop_order, OLD.cust_owned,
    OLD.cust_po_num, OLD.part_num, OLD.cust_item_num,
    OLD.weight, OLD.material_type, OLD.pickup_instr,
    OLD.delivery_instr, OLD.bol, OLD.date_created,
    OLD.created_by, OLD.date_modified, OLD.modified_by,
    OLD.order_id, OLD.status, OLD.dropoff_load_detail_id,
    OLD.is_ready, OLD.heat_num, OLD.pickup_date,
    OLD.pieces, OLD.length, OLD.bundles,
    OLD.grade, OLD.cust_location, OLD.dock_name,
    OLD.node_id, OLD.in_inventory, OLD.pickup_date_tz,
    OLD.hazmat, OLD.package_type, OLD.width,
    OLD.height, OLD.part_description, OLD.commodity_class_code,
    OLD.gauge, OLD.material_status, OLD.mill_order_num,
    OLD.mill_test_num, OLD.nmfc, OLD.product_id,
    OLD.quantity, OLD.sub_product_id, OLD.thickness,
    OLD.hazmat_class, OLD.un_num, OLD.packing_group,
    OLD.emergency_number, OLD.version, OLD.original_weight,
    OLD.original_freight_class, OLD.contract, OLD.hazmat_instructions,
    OLD.emergency_contract, OLD.emergency_company, OLD.emergency_country_code,
    OLD.emergency_area_code, OLD.ltl_product_id, OLD.ltl_package_type,
    OLD.stackable);

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_load_materials_hist() OWNER TO postgres;
/
--
-- TOC entry 1029 (class 1255 OID 42926)
-- Name: trigger_fct_load_materials_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_load_materials_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

v_node_id   LOAD_DETAILS.NODE_ID%TYPE;
v_org_id   bigint;
v_lodAttribute6 varchar(50);
v_originatingSystem varchar(50);
BEGIN
  BEGIN

IF (NEW.ORDER_ID IS NULL) THEN

    BEGIN

    select ldo.node_id
    into STRICT v_node_id
    from load_details ldo, loads l
    where l.load_id = ldo.load_id
    and l.org_id = 33033
    and ldo.point_type = 'O'
    and ldo.load_id = (SELECT load_id from load_details
                       where load_detail_id = NEW.load_detail_id);

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_node_id := NULL;
    END;

    NEW.NODE_ID := v_node_id;

END IF;

/**Added by Hima Challa on 01/22/2015 to fix issue with 2.0 loads being sent to CTSI . When CTSI sends 2.0 info, it will  send 92.5 commodity class code as 92 and 2.0 system fails to show the load in load details screens **/
IF (NEW.commodity_class_code is not null) THEN

    BEGIN

        select l.lod_attribute6
        into STRICT  v_lodAttribute6 
        from loads l, load_details ld 
        where NEW.load_detail_id = ld.load_detail_id 
        and  ld.point_type = 'O' 
        and ld.load_id = l.load_id;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_lodAttribute6 :=  'PLS1';
    END;

    if (v_lodAttribute6 = 'PLS2_CTSI') THEN

        IF (NEW.commodity_class_code = '92') THEN
                NEW.commodity_class_code := '92.5';
        END IF;
        IF (NEW.commodity_class_code = '77') THEN
                NEW.commodity_class_code := '77.5';
        END IF;
   END IF;

END IF;

--Added by Hima Challa on Dec 06, 2013 as requested by courtney in "RE: JIRA-Notification  (PS-1205) GL# field issue in Old & Flex" email
/*IF (:new.part_description IS NULL) THEN

BEGIN

select l.org_id 
into v_org_id 
from loads l, load_details ld 
where :new.load_detail_id = ld.load_detail_id 
and  ld.point_type = 'O' 
and ld.load_id = l.load_id;

if (v_org_id = 212542) THEN


    :new.part_description := 'XX';
    
    END IF;
    END;

END IF;*/
  END;
RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_load_materials_trg() OWNER TO postgres;
/
--
-- TOC entry 1051 (class 1255 OID 42906)
-- Name: trigger_fct_loads_ains_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_loads_ains_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN

--this logic creates an audit trail for newly inserted rows in the LOAD_HISTORY table
DECLARE
    p_created_by bigint := NEW.created_by;
    p_load bigint := NEW.load_id;
    p_status loads.load_status%type := NEW.load_status;
    p_priority loads.priority%type := NEW.priority;
    p_mkt loads.market_type%type := NEW.market_type;
    p_weight loads.weight%type := NEW.weight;
    p_awd_ofr bigint;
    p_awd_price bigint;
    p_prev_status loads.load_status%type;
    p_prev_mkt loads.market_type%type;

v_route_id          bigint;

v_orig_city         flatbed.routes.orig_city%type;
v_orig_state        flatbed.routes.orig_state%type;
v_orig_zip          flatbed.routes.orig_zip%type;
v_orig_country      flatbed.routes.orig_country%type;
v_dest_city         flatbed.routes.dest_city%type;
v_dest_state        flatbed.routes.dest_state%type;
v_dest_zip          flatbed.routes.dest_zip%type;
v_dest_country      flatbed.routes.dest_country%type;
v_network_id        bigint;

BEGIN

    INSERT INTO LOAD_HISTORY(LOAD_HISTORY_ID
        ,LOAD_ID
        ,LOAD_STATUS
        ,PRIORITY
        ,MARKET_TYPE
        ,AWARDED_OFFER
        ,AWARDED_PRICE
        ,PREV_LOAD_STATUS
        ,PREV_MARKET_TYPE
        ,WEIGHT
        ,DATE_CREATED
        ,CREATED_BY)
    VALUES (nextval('lh_seq')
        ,p_load
        ,p_status
        ,p_priority
        ,p_mkt
        ,p_awd_ofr
        ,p_awd_price
        ,p_prev_status
        ,p_prev_mkt
        ,p_weight
        ,LOCALTIMESTAMP
        ,p_created_by
        );

    BEGIN

    SELECT ORIG_CITY
        ,ORIG_STATE
        ,ORIG_ZIP
        ,ORIG_COUNTRY
        ,DEST_CITY
        ,DEST_STATE
        ,DEST_ZIP
        ,DEST_COUNTRY
    INTO STRICT
    v_orig_city
    ,v_orig_state
    ,v_orig_zip
    ,v_orig_country
    ,v_dest_city
    ,v_dest_state
    ,v_dest_zip
    ,v_dest_country
    FROM FLATBED.ROUTES
    WHERE ROUTE_ID = NEW.route_id;

    exception
        when no_data_found then

    v_orig_city     := null;
    v_orig_state    := null;
    v_orig_zip      := null;
    v_orig_country  := null;
    v_dest_city     := null;
    v_dest_state    := null;
    v_dest_zip      := null;
    v_dest_country := null;

    end;

    select network_id
    into STRICT v_network_id
    from flatbed.organizations
    where org_id = NEW.org_id;

    INSERT INTO LOAD_SEARCH_DATA(LOAD_ID
    ,LOAD_STATUS
    ,SHIPPER_REFERENCE_NUMBER
    ,ORG_ID
    ,LOCATION_ID
    ,PERSON_ID
    ,NETWORK_ID
    ,ORIGIN_REGION_ID
    ,DESTINATION_REGION_ID
    ,ROUTE_ID
    ,ORIG_CITY
    ,ORIG_STATE
    ,ORIG_ZIP
    ,ORIG_COUNTRY
    ,DEST_CITY
    ,DEST_STATE
    ,DEST_ZIP
    ,DEST_COUNTRY
    ,CARRIER_ORG_ID
    ,CARRIER_LOC_ID
    ,CARRIER_PERSON_ID
    ,RADIO_ACTIVE_FLAG
    ,CREATED_BY
    ,RADIO_ACTIVE_SECURE_FLAG
    ,CARRIER_REFERENCE_NUMBER
    ,CARR_REF_NUM_CHECK
    ,FINALIZATION_STATUS
    ,ORIG_BOL
    ,ORIG_OP_BOL)
    values (NEW.load_id
    ,NEW.load_status
    ,NEW.shipper_reference_number
    ,NEW.org_id
    ,NEW.location_id
    ,NEW.person_id
    ,v_network_id
    ,NEW.origin_region_id
    ,NEW.destination_region_id
    ,NEW.route_id
    ,v_orig_city
    ,v_orig_state
    ,v_orig_zip
    ,v_orig_country
    ,v_dest_city
    ,v_dest_state
    ,v_dest_zip
    ,v_dest_country
    ,-1
    ,-1
    ,-1
    ,NEW.radio_active_flag
    ,NEW.created_by
    ,NEW.radio_active_secure_flag
    ,NEW.CARRIER_REFERENCE_NUMBER
    ,NEW.PRO_NUM
    ,NEW.FINALIZATION_STATUS
    ,NEW.BOL
    ,NEW.OP_BOL);


END;
  END;
RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_loads_ains_trg() OWNER TO postgres;
/
--
-- TOC entry 1024 (class 1255 OID 42910)
-- Name: trigger_fct_loads_bins_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_loads_bins_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
  -- local variables here
BEGIN
    if NEW.originating_system in ( 'PLS2_LT', 'GS') and NEW.award_carrier_org_id in (278288,278319)
       then NEW.load_status := 'PO';
    end if;
RETURN NEW;
end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_loads_bins_trg() OWNER TO postgres;
/
--
-- TOC entry 1052 (class 1255 OID 42912)
-- Name: trigger_fct_loads_bupd_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_loads_bupd_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  --  Application_logic Pre-After-Update-row <<Start>>
  DECLARE

    v_ded_count      bigint;
    v_load_id        bigint;
    v_audit_group_id bigint;

    v_ship_fin_org_settings_count smallint := 0;
    v_carr_fin_org_settings_count smallint := 0;
    v_network_id                  bigint := null;

  BEGIN
    if   NEW.originating_system in ( 'PLS2_LT', 'GS') and NEW.award_carrier_org_id in (278288,278319)
       then NEW.load_status := 'PO';
    end if;

    IF (NEW.frt_bill_recv_flag IS NULL) THEN
    
      NEW.frt_bill_recv_flag := 'N';

    END IF;

    --18-October-2013 by Vadim Dudinov according request by Hima
    --19-June-2014 by Hima - Commented this logic as per change request from Courtney
    --if :new.org_id = 206962 then  --Safway Group
  
    --:new.gl_ref_code := :new.gl_number;
  
    --end if;
  
    IF (NEW.load_status != 'C' AND NEW.finalization_status = 'RB' AND
       NEW.org_id = 27350 AND NEW.awarded_offer IS NOT NULL) THEN
    
      SELECT COUNT(*)
        INTO STRICT v_ded_count
        FROM OFFERS O, WCC_DEDICATED_CARRIERS W, ORGANIZATIONS CORG
       WHERE O.OFFER_ID = NEW.awarded_offer
         AND O.ORG_ID = CORG.ORG_ID
         AND CORG.SCAC = W.SCAC;

      IF (v_ded_count > 0) THEN
        -- If > 0 than this is a dedicated carrier load for WCC
        -- Set finalization_status to AB - tmm 10/22/07
      
        NEW.finalization_status := 'AB';

      END IF;

    ELSIF (NEW.load_status != 'C' AND NEW.finalization_status = 'RB' AND
          NEW.org_id in (26043, 33032) AND
          NEW.created_by IN (40368, 44287) AND -- Created by EDI User
          NEW.awarded_offer IS NOT NULL) THEN
      -- if load is for AK Non-Steel or USS Inbound send straight to FBH - tmm 11/15/07
    
      NEW.finalization_status := 'FBH';

    END IF;

    IF (NEW.finalization_status = 'FP' and (OLD.INV_SHIP_RATES_ONLY IS NULL OR OLD.INV_SHIP_RATES_ONLY = 'N')) THEN
    
      select count(*)
        into STRICT v_ship_fin_org_settings_count
        from fin_org_settings
       where inv_ship_rate_only = 'Y'
         and inv_carr_rate_only = 'N'
         and org_id = NEW.org_id;

      select count(*)
        into STRICT v_carr_fin_org_settings_count
        from fin_org_settings
       where inv_ship_rate_only = 'Y'
         and inv_carr_rate_only = 'N'
         and org_id = NEW.award_carrier_org_id;

      IF (v_ship_fin_org_settings_count > 0 and
         v_carr_fin_org_settings_count > 0) THEN
      
        NEW.INV_SHIP_RATES_ONLY := 'Y';

      END IF;

    END IF;

    IF (NEW.load_status in ('PO') and
       NEW.org_id not in (190733 /*Magnesita*/
,
                            205113 /*ASARCO LLC*/
,
                            222159 /*ARCELOR MITTAL*/
) and
       coalesce(NEW.originating_system, 'PLS1_0') not in ( 'PLS2_LT', 'GS')) THEN
    
      select network_id
        into STRICT v_network_id
        from flatbed.organizations
       where org_id = NEW.org_id;

      if (v_network_id = 0) THEN
        NEW.pickup_num := '';
      end if;

    END IF;

    /** Added by Hima Bindu Challa on 01/22/2015. This is for 2.0 loads that are updated by CTSI process **/
    IF (coalesce(NEW.originating_system, 'PLS1') = 'PLS2_CTSI' or coalesce(NEW.lod_Attribute6, 'PLS1') = 'PLS2_CTSI')  THEN 
         NEW.originating_system := 'PLS2_LT';
         NEW.lod_Attribute6 := 'PLS2_CTSI';
         IF (NEW.finalization_status not in ('NF', 'FP', 'AB', 'ABH') ) THEN
               NEW.finalization_status := 'ABH';
         END IF;
    END IF;

  
  END;

RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_loads_bupd_trg() OWNER TO postgres;
/
--
-- TOC entry 1026 (class 1255 OID 42914)
-- Name: trigger_fct_loads_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_loads_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN
insert into hist.loads(
   moduser,modtime,modstatus,
   after_hours_contact, after_hours_phone, auto_cd_flg,
   awarded_by, awarded_offer, award_carrier_org_id,
   award_date, award_dedicated_unit_id, award_price,
   barge_num, bill_to, blind_carrier,
   bol, bol_instructions, booked,
   broker_reference_number, carrier_reference_number, chk_brn,
   commodity_cd, commodity_desc, container_cd,
   created_by, customer_comments, customer_tracking_email,
   customer_truck_flag, customs_broker, customs_broker_phone,
   cust_req_doc_recv_flag, cust_truck_carr_name, cust_truck_person_name,
   cust_truck_person_phone, cust_truck_scac, date_closed,
   date_created, date_modified, delivery_success,
   destination_region_id, dispatcher_schedule_only, driver_license,
   driver_name, empty_weight, etd_date,
   etd_date_tz, etd_ovr_flg, feature_code,
   finalization_status, finan_no_load_flag, fin_status_date,
   freight_paid_by, frt_bill_amount, frt_bill_number,
   frt_bill_pay_to_id, frt_bill_recv_by, frt_bill_recv_date,
   frt_bill_recv_flag, gl_date, gl_number,
   gl_ref_code, gross_weight, hazmat_flag,
   height, hfr_flag, inbound_outbound_flg,
   inv_approved, inv_carr_rates_only, inv_ship_rates_only,
   is_ready, length, load_id,
   load_status, location_id, lod_attribute1,
   lod_attribute2, lod_attribute3, lod_attribute4,
   lod_attribute5, lod_attribute6, market_type,
   mileage, mileage_type, mileage_version,
   min_price, min_weight, mm_flag,
   modified_by, multi_dock_sched_rqrd, notify_award_flag,
   notify_conf_deliv_flag, notify_conf_pick_up_flag, notify_gate_flag,
   notify_gate_flag_char, notify_initial_msg_flag, notify_schedule_flag,
   one_time_rate_id, op_bol, op_flag,
   org_id, original_sched_delivery_date, original_sched_pickup_date,
   originating_system, origin_region_id, orig_sched_delivery_date_tz,
   orig_sched_pickup_date_tz, out_route_miles, over_height_flag,
   over_length_flag, over_weight_flag, over_width_flag,
   part_num, pay_terms, permit_load,
   permit_num, person_id, pickup_num,
   pieces, po_num, premium_available,
   priority, product_type, pro_num,
   radio_active_flag, radio_active_secure_flag, rate_contact,
   rate_type, reason_code, release_num,
   route_id, routing_instructions, saved_quote_id,
   scheduled, sc_flag, service_level_cd,
   shipper_premium_available, shipper_reference_number, ship_date,
   ship_light, source_ind, so_number,
   special_instructions, special_message, srm_flag,
   srr_flag, super_load_flag, sw_flag,
   target_price, target_rate_id_max, target_rate_id_min,
   target_rate_max, target_rate_min, target_rate_ovr_flg,
   target_tr_id_max, target_tr_id_min, template_set,
   tender_cycle_id, tractor_id, trailer,
   travel_time, truck_weight, unit_cost,
   unit_number, version, volume_quote_id,
   weight, weight_uom_code, width)
values (user,CURRENT_TIMESTAMP,null /*case when inserting then 'I' else 'D' end*/
,
    OLD.after_hours_contact, OLD.after_hours_phone, OLD.auto_cd_flg,
    OLD.awarded_by, OLD.awarded_offer, OLD.award_carrier_org_id,
    OLD.award_date, OLD.award_dedicated_unit_id, OLD.award_price,
    OLD.barge_num, OLD.bill_to, OLD.blind_carrier,
    OLD.bol, OLD.bol_instructions, OLD.booked,
    OLD.broker_reference_number, OLD.carrier_reference_number, OLD.chk_brn,
    OLD.commodity_cd, OLD.commodity_desc, OLD.container_cd,
    OLD.created_by, OLD.customer_comments, OLD.customer_tracking_email,
    OLD.customer_truck_flag, OLD.customs_broker, OLD.customs_broker_phone,
    OLD.cust_req_doc_recv_flag, OLD.cust_truck_carr_name, OLD.cust_truck_person_name,
    OLD.cust_truck_person_phone, OLD.cust_truck_scac, OLD.date_closed,
    OLD.date_created, OLD.date_modified, OLD.delivery_success,
    OLD.destination_region_id, OLD.dispatcher_schedule_only, OLD.driver_license,
    OLD.driver_name, OLD.empty_weight, OLD.etd_date,
    OLD.etd_date_tz, OLD.etd_ovr_flg, OLD.feature_code,
    OLD.finalization_status, OLD.finan_no_load_flag, OLD.fin_status_date,
    OLD.freight_paid_by, OLD.frt_bill_amount, OLD.frt_bill_number,
    OLD.frt_bill_pay_to_id, OLD.frt_bill_recv_by, OLD.frt_bill_recv_date,
    OLD.frt_bill_recv_flag, OLD.gl_date, OLD.gl_number,
    OLD.gl_ref_code, OLD.gross_weight, OLD.hazmat_flag,
    OLD.height, OLD.hfr_flag, OLD.inbound_outbound_flg,
    OLD.inv_approved, OLD.inv_carr_rates_only, OLD.inv_ship_rates_only,
    OLD.is_ready, OLD.length, OLD.load_id,
    OLD.load_status, OLD.location_id, OLD.lod_attribute1,
    OLD.lod_attribute2, OLD.lod_attribute3, OLD.lod_attribute4,
    OLD.lod_attribute5, OLD.lod_attribute6, OLD.market_type,
    OLD.mileage, OLD.mileage_type, OLD.mileage_version,
    OLD.min_price, OLD.min_weight, OLD.mm_flag,
    OLD.modified_by, OLD.multi_dock_sched_rqrd, OLD.notify_award_flag,
    OLD.notify_conf_deliv_flag, OLD.notify_conf_pick_up_flag, OLD.notify_gate_flag,
    OLD.notify_gate_flag_char, OLD.notify_initial_msg_flag, OLD.notify_schedule_flag,
    OLD.one_time_rate_id, OLD.op_bol, OLD.op_flag,
    OLD.org_id, OLD.original_sched_delivery_date, OLD.original_sched_pickup_date,
    OLD.originating_system, OLD.origin_region_id, OLD.orig_sched_delivery_date_tz,
    OLD.orig_sched_pickup_date_tz, OLD.out_route_miles, OLD.over_height_flag,
    OLD.over_length_flag, OLD.over_weight_flag, OLD.over_width_flag,
    OLD.part_num, OLD.pay_terms, OLD.permit_load,
    OLD.permit_num, OLD.person_id, OLD.pickup_num,
    OLD.pieces, OLD.po_num, OLD.premium_available,
    OLD.priority, OLD.product_type, OLD.pro_num,
    OLD.radio_active_flag, OLD.radio_active_secure_flag, OLD.rate_contact,
    OLD.rate_type, OLD.reason_code, OLD.release_num,
    OLD.route_id, OLD.routing_instructions, OLD.saved_quote_id,
    OLD.scheduled, OLD.sc_flag, OLD.service_level_cd,
    OLD.shipper_premium_available, OLD.shipper_reference_number, OLD.ship_date,
    OLD.ship_light, OLD.source_ind, OLD.so_number,
    OLD.special_instructions, OLD.special_message, OLD.srm_flag,
    OLD.srr_flag, OLD.super_load_flag, OLD.sw_flag,
    OLD.target_price, OLD.target_rate_id_max, OLD.target_rate_id_min,
    OLD.target_rate_max, OLD.target_rate_min, OLD.target_rate_ovr_flg,
    OLD.target_tr_id_max, OLD.target_tr_id_min, OLD.template_set,
    OLD.tender_cycle_id, OLD.tractor_id, OLD.trailer,
    OLD.travel_time, OLD.truck_weight, OLD.unit_cost,
    OLD.unit_number, OLD.version, OLD.volume_quote_id,
    OLD.weight, OLD.weight_uom_code, OLD.width);

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_loads_hist() OWNER TO postgres;
/
--
-- TOC entry 1027 (class 1255 OID 42917)
-- Name: trigger_fct_loads_macsteel_bol_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_loads_macsteel_bol_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

BEGIN

IF (NEW.org_id = 33033) THEN

    IF (NEW.FINALIZATION_STATUS = 'RB' AND
        OLD.FINALIZATION_STATUS = 'NF' AND
        upper(NEW.BOL) like '%CLAIM%') THEN

        NEW.FINALIZATION_STATUS := 'FBH';

    END IF;

END IF;
RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_loads_macsteel_bol_trg() OWNER TO postgres;
/
--
-- TOC entry 1030 (class 1255 OID 42928)
-- Name: trigger_fct_lod_broker_ref_num_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_lod_broker_ref_num_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE
v_chk_brn   loads.chk_brn%type;
BEGIN

IF (NEW.broker_reference_number IS NULL) THEN

    NEW.broker_reference_number := NEW.load_id::varchar;

END IF;

-- Excluded Kone from this logic - tmm - 11/28/2011 Bug 12641
IF (NEW.org_id <> 156554 AND NEW.gl_ref_code IS NOT NULL AND NEW.op_bol IS NULL AND LENGTH(NEW.gl_ref_code) <= 20) THEN

    NEW.op_bol := NEW.gl_ref_code;

END IF;

RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_lod_broker_ref_num_trg() OWNER TO postgres;
/
--
-- TOC entry 1054 (class 1255 OID 42930)
-- Name: trigger_fct_ltl_product_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_ltl_product_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN
insert into hist.ltl_product(
   moduser,modtime,modstatus,
   ltl_product_id, ltl_product_tracking_id, org_id,
   location_id, product_code, description,
   nmfc_num, pieces, package_type,
   weight, commodity_class_code, hazmat_flag,
   hazmat_class, un_num, packing_group,
   emergency_number, created_by, date_created,
   version, modified_by, date_modified,
   status, nmfc_sub_num, hazmat_instructions,
   emergency_contract, emergency_company, emergency_country_code,
   emergency_area_code, person_id)
values (user,CURRENT_TIMESTAMP,null /*case when inserting then 'I' else 'D' end*/
,
   OLD.ltl_product_id, OLD.ltl_product_tracking_id, OLD.org_id,
   OLD.location_id, OLD.product_code, OLD.description,
   OLD.nmfc_num, OLD.pieces, OLD.package_type,
   OLD.weight, OLD.commodity_class_code, OLD.hazmat_flag,
   OLD.hazmat_class, OLD.un_num, OLD.packing_group,
   OLD.emergency_number, OLD.created_by, OLD.date_created,
   OLD.version, OLD.modified_by, OLD.date_modified,
   OLD.status, OLD.nmfc_sub_num, OLD.hazmat_instructions,
   OLD.emergency_contract, OLD.emergency_company, OLD.emergency_country_code,
   OLD.emergency_area_code, OLD.person_id);

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_ltl_product_hist() OWNER TO postgres;
/
--
-- TOC entry 1031 (class 1255 OID 42932)
-- Name: trigger_fct_notes_bins_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_notes_bins_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

BEGIN

if (NEW.note_type = 'CARINSTEDI') THEN

NEW.note := replace(NEW.note, '&'||'amp;gt;', '>');
NEW.note := replace(NEW.note,'&'||'amp;lt;', '<');

END IF;

RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_notes_bins_trg() OWNER TO postgres;
/
--
-- TOC entry 1032 (class 1255 OID 42937)
-- Name: trigger_fct_ol_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_ol_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN

insert into hist.organization_locations(
   moduser,modtime,modstatus,
   location_id, org_id, location_name,
   status, contact_last_name, contact_first_name,
   contact_title, contact_email, date_created,
   created_by, date_modified, modified_by,
   bill_to, op_org_id, op_loc_id,
   visible, pricing_on_location, auto_auto_start_time,
   auto_auto_end_time, rate_contact_name, rate_email_address,
   rate_fax_area_code, rate_fax_number, rate_default_minimum,
   rate_default_type, rate_car_default_minimum, rate_car_default_type,
   credit_limit, sales_lead, commodity_cd,
   employer_num, bill_to_adjustment, bill_to_accessorial,
   address_id, version, must_pre_pay,
   is_default)
values (user,CURRENT_TIMESTAMP,null /*case when inserting then 'I' else 'D' end*/
,
   OLD.location_id, OLD.org_id, OLD.location_name,
   OLD.status, OLD.contact_last_name, OLD.contact_first_name,
   OLD.contact_title, OLD.contact_email, OLD.date_created,
   OLD.created_by, OLD.date_modified, OLD.modified_by,
   OLD.bill_to, OLD.op_org_id, OLD.op_loc_id,
   OLD.visible, OLD.pricing_on_location, OLD.auto_auto_start_time,
   OLD.auto_auto_end_time, OLD.rate_contact_name, OLD.rate_email_address,
   OLD.rate_fax_area_code, OLD.rate_fax_number, OLD.rate_default_minimum,
   OLD.rate_default_type, OLD.rate_car_default_minimum, OLD.rate_car_default_type,
   OLD.credit_limit, OLD.sales_lead, OLD.commodity_cd,
   OLD.employer_num, OLD.bill_to_adjustment, OLD.bill_to_accessorial,
   OLD.address_id, OLD.version, OLD.must_pre_pay,
   OLD.is_default);

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_ol_hist() OWNER TO postgres;
/
--
-- TOC entry 1033 (class 1255 OID 42939)
-- Name: trigger_fct_onetime_rates_bins_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_onetime_rates_bins_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN

DECLARE

BEGIN

IF (NEW.unit_type = 'FL' and NEW.minimum > 1 AND NEW.rate_class_id = 25) THEN

    NEW.minimum := 1;

END IF;

END;

RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_onetime_rates_bins_trg() OWNER TO postgres;
/
--
-- TOC entry 1036 (class 1255 OID 42946)
-- Name: trigger_fct_org_users_bins_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_org_users_bins_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
if NEW.org_type is null then

   select org_type into STRICT NEW.org_type from organizations where org_id = NEW.org_id;

end if;
RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_org_users_bins_trg() OWNER TO postgres;
/
--
-- TOC entry 1035 (class 1255 OID 42944)
-- Name: trigger_fct_organization_phones_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_organization_phones_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.ORGANIZATION_PHONES(
  moduser,modtime,modstatus,

  ORG_PHONE_ID   ,
  ORG_ID         ,
  LOCATION_ID    ,
  DIALING_CODE   ,
  AREA_CODE      ,
  PHONE_NUMBER   ,
  EXTENSION      ,
  PHONE_TYPE     ,
  DATE_CREATED   ,
  CREATED_BY     ,
  DATE_MODIFIED  ,
  MODIFIED_BY                         
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.ORG_PHONE_ID   ,
  OLD.ORG_ID         ,
  OLD.LOCATION_ID    ,
  OLD.DIALING_CODE   ,
  OLD.AREA_CODE      ,
  OLD.PHONE_NUMBER   ,
  OLD.EXTENSION      ,
  OLD.PHONE_TYPE     ,
  OLD.DATE_CREATED   ,
  OLD.CREATED_BY     ,
  OLD.DATE_MODIFIED  ,
  OLD.MODIFIED_BY        
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_organization_phones_hist() OWNER TO postgres;
/
--
-- TOC entry 1055 (class 1255 OID 42940)
-- Name: trigger_fct_organizations_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_organizations_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
  BEGIN
insert into hist.organizations(
   moduser,modtime,modstatus,
   org_id, name, employer_num,
   org_type, status, contact_last_name,
   contact_first_name, contact_title, contact_email,
   mc_num, scac, org_id_parent,
   date_created, created_by, date_modified,
   modified_by, loc_rate_override, logo_path,
   css_path, status_reason, qualcomm_id,
   network_id, critical_email_address, tender_increment_time,
   open_market_time, max_num_carriers, not_exceed_shipper_amt,
   incl_carr_not_hauled_lane, auto_auto_start_time, auto_auto_end_time,
   rate_contact_name, rate_email_address, rate_fax_area_code,
   rate_fax_number, rate_default_minimum, rate_default_type,
   rate_auto_approve_days, not_exceed_type, max_carrier_rate,
   max_carr_rate_unit_type, rate_car_default_minimum, rate_car_default_type,
   address_id, credit_limit, sales_lead,
   ltl_account_type, ltl_client_code, region_id,
   accelerated_ind, version, ltl_rate_type,
   must_pre_pay, account_executive, eff_date,
   company_code, override_credit_hold, auto_credit_hold,
   warning_no_of_days, currency_code, edi_account,
   is_contract, exp_date, logo_id,
   from_vendor_bills, product_list_primary_sort,
   display_logo_on_bol)
values (user,CURRENT_TIMESTAMP,null /*case when inserting then 'I' else 'D' end*/
,
    OLD.org_id, OLD.name, OLD.employer_num,
    OLD.org_type, OLD.status, OLD.contact_last_name,
    OLD.contact_first_name, OLD.contact_title, OLD.contact_email,
    OLD.mc_num, OLD.scac, OLD.org_id_parent,
    OLD.date_created, OLD.created_by, OLD.date_modified,
    OLD.modified_by, OLD.loc_rate_override, OLD.logo_path,
    OLD.css_path, OLD.status_reason, OLD.qualcomm_id,
    OLD.network_id, OLD.critical_email_address, OLD.tender_increment_time,
    OLD.open_market_time, OLD.max_num_carriers, OLD.not_exceed_shipper_amt,
    OLD.incl_carr_not_hauled_lane, OLD.auto_auto_start_time, OLD.auto_auto_end_time,
    OLD.rate_contact_name, OLD.rate_email_address, OLD.rate_fax_area_code,
    OLD.rate_fax_number, OLD.rate_default_minimum, OLD.rate_default_type,
    OLD.rate_auto_approve_days, OLD.not_exceed_type, OLD.max_carrier_rate,
    OLD.max_carr_rate_unit_type, OLD.rate_car_default_minimum, OLD.rate_car_default_type,
    OLD.address_id, OLD.credit_limit, OLD.sales_lead,
    OLD.ltl_account_type, OLD.ltl_client_code, OLD.region_id,
    OLD.accelerated_ind, OLD.version, OLD.ltl_rate_type,
    OLD.must_pre_pay, OLD.account_executive, OLD.eff_date,
    OLD.company_code, OLD.override_credit_hold, OLD.auto_credit_hold,
    OLD.warning_no_of_days, OLD.currency_code, OLD.edi_account,
    OLD.is_contract, OLD.exp_date, OLD.logo_id,
    OLD.from_vendor_bills, OLD.product_list_primary_sort,
    OLD.display_logo_on_bol);

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_organizations_hist() OWNER TO postgres;
/
--
-- TOC entry 1034 (class 1255 OID 42942)
-- Name: trigger_fct_organizations_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_organizations_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

v_org_id_parent ORGANIZATIONS.ORG_ID_PARENT%TYPE;

BEGIN

IF (NEW.org_type = 'SHIPPER') THEN

    SELECT MAX(ORG_ID) INTO STRICT v_org_id_parent
    FROM ORGANIZATIONS
    WHERE NETWORK_ID = NEW.network_id
    AND ORG_TYPE ='UMBRELLA';

    INSERT INTO PRICING_ON_ORGS(LOAD_CREATION_DATE,ORG_ID)
    VALUES ('01-JAN-07',NEW.org_id);

    NEW.org_id_parent := v_org_id_parent;

END IF;

RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_organizations_trg() OWNER TO postgres;
/
--
-- TOC entry 1037 (class 1255 OID 42948)
-- Name: trigger_fct_phones_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_phones_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.PHONES(
  moduser,modtime,modstatus,

  PHONE_ID       ,
  PHONE_TYPE     ,
  COUNTRY_CODE   ,
  AREA_CODE      ,
  PHONE_NUMBER   ,
  EXTENSION      ,
  DATE_CREATED   ,
  CREATED_BY     ,
  DATE_MODIFIED  ,
  MODIFIED_BY    ,
  VERSION                              
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.PHONE_ID       ,
  OLD.PHONE_TYPE     ,
  OLD.COUNTRY_CODE   ,
  OLD.AREA_CODE      ,
  OLD.PHONE_NUMBER   ,
  OLD.EXTENSION      ,
  OLD.DATE_CREATED   ,
  OLD.CREATED_BY     ,
  OLD.DATE_MODIFIED  ,
  OLD.MODIFIED_BY    ,
  OLD.VERSION                                
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_phones_hist() OWNER TO postgres;
/
--
-- TOC entry 1038 (class 1255 OID 42950)
-- Name: trigger_fct_pls_customer_terms_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_pls_customer_terms_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.PLS_CUSTOMER_TERMS(
  moduser,modtime,modstatus,

  TERM_ID    ,
  TERM_NAME  ,
  DUE_DAYS   
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.TERM_ID    ,
  OLD.TERM_NAME  ,
  OLD.DUE_DAYS          
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_pls_customer_terms_hist() OWNER TO postgres;
/
--
-- TOC entry 1039 (class 1255 OID 42952)
-- Name: trigger_fct_populate_shipper_name(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_populate_shipper_name() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE
v_old_sorg_id bigint;
v_new_sorg_id bigint;
BEGIN
IF (NEW.shipper_org_id IS NOT NULL) THEN
    select btrim(substr(name,1,100)) into STRICT NEW.shipper_name
    from organizations
    where org_id = NEW.shipper_org_id;
END IF;
RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_populate_shipper_name() OWNER TO postgres;
/
--
-- TOC entry 1040 (class 1255 OID 42953)
-- Name: trigger_fct_production_orders_trg(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_production_orders_trg() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE

BEGIN

IF (NEW.delivery_due IS NULL) THEN

    NEW.delivery_due := LOCALTIMESTAMP;

END IF;

RETURN NEW;
END
$$;
/

ALTER FUNCTION flatbed.trigger_fct_production_orders_trg() OWNER TO postgres;
/
--
-- TOC entry 1041 (class 1255 OID 42954)
-- Name: trigger_fct_timezone_hist(); Type: FUNCTION; Schema: flatbed; Owner: postgres
--

CREATE FUNCTION trigger_fct_timezone_hist() RETURNS trigger
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
declare
 v_status varchar(1);
BEGIN
  BEGIN
v_status := case when TG_OP = 'UPDATE' then 'U' else 'D' end;
insert into hist.TIMEZONE(
  moduser,modtime,modstatus,

  TIMEZONE_CODE  ,
  TIMEZONE_NAME  ,
  LOCAL_OFFSET   ,
  TIMEZONE       
)
values (user,CURRENT_TIMESTAMP,v_status,

  OLD.TIMEZONE_CODE  ,
  OLD.TIMEZONE_NAME  ,
  OLD.LOCAL_OFFSET   ,
  OLD.TIMEZONE       
 );

exception
  when others then
    null;
  END;
IF TG_OP = 'DELETE' THEN
    RETURN OLD;
ELSE
    RETURN NEW;
END IF;

end
$$;
/

ALTER FUNCTION flatbed.trigger_fct_timezone_hist() OWNER TO postgres;
/

-- FUNCTION: flatbed.populate_general()

CREATE OR REPLACE FUNCTION flatbed.populate_general(
)
  RETURNS VOID
LANGUAGE 'plpgsql'
AS $BODY$
DECLARE
  --v_trace_load_l1_tab hist.trace_load_l1_tab;
  --bs hist.trace_load_ext_tab;
  vb hist.TRACE_LOAD_EXT_TAB;
  c  NUMERIC;
BEGIN
  FOR vb IN SELECT *
            FROM hist.TRACE_LOAD_EXT_TAB_TABlE LOOP
    INSERT INTO HIST.trace_load_l1_tab_table
      WITH bs0 AS
      (
          SELECT
            /*+ cardinalit(5) materialize */
            bs_table.*,
            CASE WHEN coalesce(lh_rev, -101) = lag(coalesce(lh_rev, -101))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d12,
            CASE WHEN coalesce(acc_rev, -101) = lag(coalesce(acc_rev, -101))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d13,
            CASE WHEN coalesce(lh_cost, -101) = lag(coalesce(lh_cost, -101))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d14,
            CASE WHEN coalesce(acc_cost, -101) = lag(coalesce(acc_cost, -101))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d15,
            CASE WHEN coalesce(bol, '.#.#.') = lag(coalesce(bol, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d16,
            CASE WHEN coalesce(po_num, '.#.#.') = lag(coalesce(po_num, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d17,
            CASE WHEN coalesce(so_number, '.#.#.') = lag(coalesce(so_number, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d18,
            CASE WHEN coalesce(shipper_reference_number, '.#.#.') = lag(coalesce(shipper_reference_number, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d19,
            CASE WHEN coalesce(trailer, '.#.#.') = lag(coalesce(trailer, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d20,
            CASE WHEN coalesce(pickup_num, '.#.#.') = lag(coalesce(pickup_num, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d21,
            CASE WHEN coalesce(gl_number, '.#.#.') = lag(coalesce(gl_number, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d22,
            CASE WHEN coalesce(carrier_reference_number, '.#.#.') = lag(coalesce(carrier_reference_number, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d23,
            CASE WHEN coalesce(contact1, '.#.#.') = lag(coalesce(contact1, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d24,
            CASE WHEN coalesce(adrs1, '.#.#.') = lag(coalesce(adrs1, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d25,
            CASE WHEN coalesce(adr1, '.#.#.') = lag(coalesce(adr1, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d26,
            CASE WHEN coalesce(country1, '.#.#.') = lag(coalesce(country1, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d27,
            CASE WHEN coalesce(dep1, DATE '1900-01-01') = lag(coalesce(dep1, DATE '1900-01-01'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d28,
            CASE WHEN coalesce(contact2, '.#.#.') = lag(coalesce(contact2, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d29,
            CASE WHEN coalesce(adrs2, '.#.#.') = lag(coalesce(adrs2, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d30,
            CASE WHEN coalesce(adr2, '.#.#.') = lag(coalesce(adr2, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d31,
            CASE WHEN coalesce(country2, '.#.#.') = lag(coalesce(country2, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d32,
            CASE WHEN coalesce(dep2, DATE '1900-01-01') = lag(coalesce(dep2, DATE '1900-01-01'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d33,
            CASE WHEN coalesce(location_bt, '.#.#.') = lag(coalesce(location_bt, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d34,
            CASE WHEN coalesce(bill_to, '.#.#.') = lag(coalesce(bill_to, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d35,
            CASE WHEN coalesce(adrs3, '.#.#.') = lag(coalesce(adrs3, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d36,
            CASE WHEN coalesce(adr3, '.#.#.') = lag(coalesce(adr3, '.#.#.'))
            OVER (
              PARTITION BY rnk
              ORDER BY modtime )
              THEN 0
            ELSE 1 END d37
          FROM hist.trace_load_ext_tab_table bs_table
          WHERE rnk = (SELECT max(rnk)
                       FROM hist.trace_load_ext_tab_table bs)
          ORDER BY 1 NULLS FIRST
      ),
          bs AS
        (
            SELECT
              /*+ cardinalit(5) materialize */
              bs0.*,
              CASE WHEN d12 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d12
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s12,
              CASE WHEN d13 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d13
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s13,
              CASE WHEN d14 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d14
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s14,
              CASE WHEN d15 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d15
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s15,
              CASE WHEN d16 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d16
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s16,
              CASE WHEN d17 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d17
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s17,
              CASE WHEN d18 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d18
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s18,
              CASE WHEN d19 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d19
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s19,
              CASE WHEN d20 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d20
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s20,
              CASE WHEN d21 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d21
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s21,
              CASE WHEN d22 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d22
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s22,
              CASE WHEN d23 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d23
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s23,
              CASE WHEN d24 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d24
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s24,
              CASE WHEN d25 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d25
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s25,
              CASE WHEN d26 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d26
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s26,
              CASE WHEN d27 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d27
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s27,
              CASE WHEN d28 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d28
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s28,
              CASE WHEN d29 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d29
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s29,
              CASE WHEN d30 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d30
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s30,
              CASE WHEN d31 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d31
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s31,
              CASE WHEN d32 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d32
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s32,
              CASE WHEN d33 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d33
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s33,
              CASE WHEN d34 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d34
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s34,
              CASE WHEN d35 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d35
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s35,
              CASE WHEN d36 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d36
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s36,
              CASE WHEN d37 = 1 AND row_number()
                                    OVER (
                                      PARTITION BY d37
                                      ORDER BY modtime DESC ) = 1
                THEN 1
              ELSE 0 END s37
            FROM bs0
        ),
          a AS
        (
          SELECT
            'Cost Info'                    grp,
            'Revenue Linehaul'             fld_name,
            (max(CASE WHEN is_first = 1
              THEN lh_rev END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN lh_rev END)) :: VARCHAR pls_current,
            (vb.lh_rev) :: VARCHAR         vendor_bill,
            max(CASE WHEN s12 = 1
              THEN modtime END)            last_modified,
            max(CASE WHEN s12 = 1
              THEN modified_by END)        modified_by,
            max(CASE WHEN s12 = 1 AND is_first != 1
              THEN tp END)                 modified_table,
            max(CASE WHEN s12 = 1 AND is_first != 1
              THEN id END)                 modified_id,
            12                             ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Cost Info'                     grp,
            'Revenue Accessorials'          fld_name,
            (max(CASE WHEN is_first = 1
              THEN acc_rev END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN acc_rev END)) :: VARCHAR pls_current,
            (vb.acc_rev) :: VARCHAR         vendor_bill,
            max(CASE WHEN s13 = 1
              THEN modtime END)             last_modified,
            max(CASE WHEN s13 = 1
              THEN modified_by END)         modified_by,
            max(CASE WHEN s13 = 1 AND is_first != 1
              THEN tp END)                  modified_table,
            max(CASE WHEN s13 = 1 AND is_first != 1
              THEN id END)                  modified_id,
            13                              ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Cost Info'                     grp,
            'Cost Linehaul'                 fld_name,
            (max(CASE WHEN is_first = 1
              THEN lh_cost END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN lh_cost END)) :: VARCHAR pls_current,
            (vb.lh_cost) :: VARCHAR         vendor_bill,
            max(CASE WHEN s14 = 1
              THEN modtime END)             last_modified,
            max(CASE WHEN s14 = 1
              THEN modified_by END)         modified_by,
            max(CASE WHEN s14 = 1 AND is_first != 1
              THEN tp END)                  modified_table,
            max(CASE WHEN s14 = 1 AND is_first != 1
              THEN id END)                  modified_id,
            14                              ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Cost Info'                      grp,
            'Cost Accessorials'              fld_name,
            (max(CASE WHEN is_first = 1
              THEN acc_cost END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN acc_cost END)) :: VARCHAR pls_current,
            (vb.acc_cost) :: VARCHAR         vendor_bill,
            max(CASE WHEN s15 = 1
              THEN modtime END)              last_modified,
            max(CASE WHEN s15 = 1
              THEN modified_by END)          modified_by,
            max(CASE WHEN s15 = 1 AND is_first != 1
              THEN tp END)                   modified_table,
            max(CASE WHEN s15 = 1 AND is_first != 1
              THEN id END)                   modified_id,
            15                               ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Identifier #s'             grp,
            'BOL#'                      fld_name,
            (max(CASE WHEN is_first = 1
              THEN bol END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN bol END)) :: VARCHAR pls_current,
            (vb.bol) :: VARCHAR         vendor_bill,
            max(CASE WHEN s16 = 1
              THEN modtime END)         last_modified,
            max(CASE WHEN s16 = 1
              THEN modified_by END)     modified_by,
            max(CASE WHEN s16 = 1 AND is_first != 1
              THEN tp END)              modified_table,
            max(CASE WHEN s16 = 1 AND is_first != 1
              THEN id END)              modified_id,
            16                          ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Identifier #s'                grp,
            'PO#'                          fld_name,
            (max(CASE WHEN is_first = 1
              THEN po_num END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN po_num END)) :: VARCHAR pls_current,
            (vb.po_num) :: VARCHAR         vendor_bill,
            max(CASE WHEN s17 = 1
              THEN modtime END)            last_modified,
            max(CASE WHEN s17 = 1
              THEN modified_by END)        modified_by,
            max(CASE WHEN s17 = 1 AND is_first != 1
              THEN tp END)                 modified_table,
            max(CASE WHEN s17 = 1 AND is_first != 1
              THEN id END)                 modified_id,
            17                             ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Identifier #s'                   grp,
            'SO#'                             fld_name,
            (max(CASE WHEN is_first = 1
              THEN so_number END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN so_number END)) :: VARCHAR pls_current,
            (vb.so_number) :: VARCHAR         vendor_bill,
            max(CASE WHEN s18 = 1
              THEN modtime END)               last_modified,
            max(CASE WHEN s18 = 1
              THEN modified_by END)           modified_by,
            max(CASE WHEN s18 = 1 AND is_first != 1
              THEN tp END)                    modified_table,
            max(CASE WHEN s18 = 1 AND is_first != 1
              THEN id END)                    modified_id,
            18                                ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Identifier #s'                                  grp,
            'Shipper Ref#'                                   fld_name,
            (max(CASE WHEN is_first = 1
              THEN shipper_reference_number END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN shipper_reference_number END)) :: VARCHAR pls_current,
            (vb.shipper_reference_number) :: VARCHAR         vendor_bill,
            max(CASE WHEN s19 = 1
              THEN modtime END)                              last_modified,
            max(CASE WHEN s19 = 1
              THEN modified_by END)                          modified_by,
            max(CASE WHEN s19 = 1 AND is_first != 1
              THEN tp END)                                   modified_table,
            max(CASE WHEN s19 = 1 AND is_first != 1
              THEN id END)                                   modified_id,
            19                                               ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Identifier #s'                 grp,
            'Trailer#'                      fld_name,
            (max(CASE WHEN is_first = 1
              THEN trailer END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN trailer END)) :: VARCHAR pls_current,
            (vb.trailer) :: VARCHAR         vendor_bill,
            max(CASE WHEN s20 = 1
              THEN modtime END)             last_modified,
            max(CASE WHEN s20 = 1
              THEN modified_by END)         modified_by,
            max(CASE WHEN s20 = 1 AND is_first != 1
              THEN tp END)                  modified_table,
            max(CASE WHEN s20 = 1 AND is_first != 1
              THEN id END)                  modified_id,
            20                              ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Identifier #s'                    grp,
            'PU#'                              fld_name,
            (max(CASE WHEN is_first = 1
              THEN pickup_num END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN pickup_num END)) :: VARCHAR pls_current,
            (vb.pickup_num) :: VARCHAR         vendor_bill,
            max(CASE WHEN s21 = 1
              THEN modtime END)                last_modified,
            max(CASE WHEN s21 = 1
              THEN modified_by END)            modified_by,
            max(CASE WHEN s21 = 1 AND is_first != 1
              THEN tp END)                     modified_table,
            max(CASE WHEN s21 = 1 AND is_first != 1
              THEN id END)                     modified_id,
            21                                 ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Identifier #s'                   grp,
            'GL#'                             fld_name,
            (max(CASE WHEN is_first = 1
              THEN gl_number END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN gl_number END)) :: VARCHAR pls_current,
            (vb.gl_number) :: VARCHAR         vendor_bill,
            max(CASE WHEN s22 = 1
              THEN modtime END)               last_modified,
            max(CASE WHEN s22 = 1
              THEN modified_by END)           modified_by,
            max(CASE WHEN s22 = 1 AND is_first != 1
              THEN tp END)                    modified_table,
            max(CASE WHEN s22 = 1 AND is_first != 1
              THEN id END)                    modified_id,
            22                                ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Identifier #s'                                  grp,
            'PRO#'                                           fld_name,
            (max(CASE WHEN is_first = 1
              THEN carrier_reference_number END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN carrier_reference_number END)) :: VARCHAR pls_current,
            (vb.pro_num) :: VARCHAR                          vendor_bill,
            max(CASE WHEN s23 = 1
              THEN modtime END)                              last_modified,
            max(CASE WHEN s23 = 1
              THEN modified_by END)                          modified_by,
            max(CASE WHEN s23 = 1 AND is_first != 1
              THEN tp END)                                   modified_table,
            max(CASE WHEN s23 = 1 AND is_first != 1
              THEN id END)                                   modified_id,
            23                                               ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Origin Info'                    grp,
            'Name'                           fld_name,
            (max(CASE WHEN is_first = 1
              THEN contact1 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN contact1 END)) :: VARCHAR pls_current,
            (vb.contact1) :: VARCHAR         vendor_bill,
            max(CASE WHEN s24 = 1
              THEN modtime END)              last_modified,
            max(CASE WHEN s24 = 1
              THEN modified_by END)          modified_by,
            max(CASE WHEN s24 = 1 AND is_first != 1
              THEN tp END)                   modified_table,
            max(CASE WHEN s24 = 1 AND is_first != 1
              THEN id END)                   modified_id,
            24                               ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Origin Info'                 grp,
            'Address'                     fld_name,
            (max(CASE WHEN is_first = 1
              THEN adrs1 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN adrs1 END)) :: VARCHAR pls_current,
            (vb.adrs1) :: VARCHAR         vendor_bill,
            max(CASE WHEN s25 = 1
              THEN modtime END)           last_modified,
            max(CASE WHEN s25 = 1
              THEN modified_by END)       modified_by,
            max(CASE WHEN s25 = 1 AND is_first != 1
              THEN tp END)                modified_table,
            max(CASE WHEN s25 = 1 AND is_first != 1
              THEN id END)                modified_id,
            25                            ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Origin Info'                grp,
            'City, State, ZIP'           fld_name,
            (max(CASE WHEN is_first = 1
              THEN adr1 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN adr1 END)) :: VARCHAR pls_current,
            (vb.adr1) :: VARCHAR         vendor_bill,
            max(CASE WHEN s26 = 1
              THEN modtime END)          last_modified,
            max(CASE WHEN s26 = 1
              THEN modified_by END)      modified_by,
            max(CASE WHEN s26 = 1 AND is_first != 1
              THEN tp END)               modified_table,
            max(CASE WHEN s26 = 1 AND is_first != 1
              THEN id END)               modified_id,
            26                           ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Origin Info'                    grp,
            'Country'                        fld_name,
            (max(CASE WHEN is_first = 1
              THEN country1 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN country1 END)) :: VARCHAR pls_current,
            (vb.country1) :: VARCHAR         vendor_bill,
            max(CASE WHEN s27 = 1
              THEN modtime END)              last_modified,
            max(CASE WHEN s27 = 1
              THEN modified_by END)          modified_by,
            max(CASE WHEN s27 = 1 AND is_first != 1
              THEN tp END)                   modified_table,
            max(CASE WHEN s27 = 1 AND is_first != 1
              THEN id END)                   modified_id,
            27                               ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Origin Info'                grp,
            'Pickup Date'                fld_name,
            (max(CASE WHEN is_first = 1
              THEN dep1 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN dep1 END)) :: VARCHAR pls_current,
            (vb.dep1) :: VARCHAR         vendor_bill,
            max(CASE WHEN s28 = 1
              THEN modtime END)          last_modified,
            max(CASE WHEN s28 = 1
              THEN modified_by END)      modified_by,
            max(CASE WHEN s28 = 1 AND is_first != 1
              THEN tp END)               modified_table,
            max(CASE WHEN s28 = 1 AND is_first != 1
              THEN id END)               modified_id,
            28                           ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Destination Info'               grp,
            'Name'                           fld_name,
            (max(CASE WHEN is_first = 1
              THEN contact2 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN contact2 END)) :: VARCHAR pls_current,
            (vb.contact2) :: VARCHAR         vendor_bill,
            max(CASE WHEN s29 = 1
              THEN modtime END)              last_modified,
            max(CASE WHEN s29 = 1
              THEN modified_by END)          modified_by,
            max(CASE WHEN s29 = 1 AND is_first != 1
              THEN tp END)                   modified_table,
            max(CASE WHEN s29 = 1 AND is_first != 1
              THEN id END)                   modified_id,
            29                               ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Destination Info'            grp,
            'Address'                     fld_name,
            (max(CASE WHEN is_first = 1
              THEN adrs2 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN adrs2 END)) :: VARCHAR pls_current,
            (vb.adrs2) :: VARCHAR         vendor_bill,
            max(CASE WHEN s30 = 1
              THEN modtime END)           last_modified,
            max(CASE WHEN s30 = 1
              THEN modified_by END)       modified_by,
            max(CASE WHEN s30 = 1 AND is_first != 1
              THEN tp END)                modified_table,
            max(CASE WHEN s30 = 1 AND is_first != 1
              THEN id END)                modified_id,
            30                            ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Destination Info'           grp,
            'City, State, ZIP'           fld_name,
            (max(CASE WHEN is_first = 1
              THEN adr2 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN adr2 END)) :: VARCHAR pls_current,
            (vb.adr2) :: VARCHAR         vendor_bill,
            max(CASE WHEN s31 = 1
              THEN modtime END)          last_modified,
            max(CASE WHEN s31 = 1
              THEN modified_by END)      modified_by,
            max(CASE WHEN s31 = 1 AND is_first != 1
              THEN tp END)               modified_table,
            max(CASE WHEN s31 = 1 AND is_first != 1
              THEN id END)               modified_id,
            31                           ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Destination Info'               grp,
            'Country'                        fld_name,
            (max(CASE WHEN is_first = 1
              THEN country2 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN country2 END)) :: VARCHAR pls_current,
            (vb.country2) :: VARCHAR         vendor_bill,
            max(CASE WHEN s32 = 1
              THEN modtime END)              last_modified,
            max(CASE WHEN s32 = 1
              THEN modified_by END)          modified_by,
            max(CASE WHEN s32 = 1 AND is_first != 1
              THEN tp END)                   modified_table,
            max(CASE WHEN s32 = 1 AND is_first != 1
              THEN id END)                   modified_id,
            32                               ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Destination Info'           grp,
            'Delivery Date'              fld_name,
            (max(CASE WHEN is_first = 1
              THEN dep2 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN dep2 END)) :: VARCHAR pls_current,
            (vb.dep2) :: VARCHAR         vendor_bill,
            max(CASE WHEN s33 = 1
              THEN modtime END)          last_modified,
            max(CASE WHEN s33 = 1
              THEN modified_by END)      modified_by,
            max(CASE WHEN s33 = 1 AND is_first != 1
              THEN tp END)               modified_table,
            max(CASE WHEN s33 = 1 AND is_first != 1
              THEN id END)               modified_id,
            33                           ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Bill To Info'                      grp,
            'Location'                          fld_name,
            (max(CASE WHEN is_first = 1
              THEN location_bt END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN location_bt END)) :: VARCHAR pls_current,
            (vb.location_bt) :: VARCHAR         vendor_bill,
            max(CASE WHEN s34 = 1
              THEN modtime END)                 last_modified,
            max(CASE WHEN s34 = 1
              THEN modified_by END)             modified_by,
            max(CASE WHEN s34 = 1 AND is_first != 1
              THEN tp END)                      modified_table,
            max(CASE WHEN s34 = 1 AND is_first != 1
              THEN id END)                      modified_id,
            34                                  ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Bill To Info'                  grp,
            'Name'                          fld_name,
            (max(CASE WHEN is_first = 1
              THEN bill_to END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN bill_to END)) :: VARCHAR pls_current,
            (vb.bill_to) :: VARCHAR         vendor_bill,
            max(CASE WHEN s35 = 1
              THEN modtime END)             last_modified,
            max(CASE WHEN s35 = 1
              THEN modified_by END)         modified_by,
            max(CASE WHEN s35 = 1 AND is_first != 1
              THEN tp END)                  modified_table,
            max(CASE WHEN s35 = 1 AND is_first != 1
              THEN id END)                  modified_id,
            35                              ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Bill To Info'                grp,
            'Address'                     fld_name,
            (max(CASE WHEN is_first = 1
              THEN adrs3 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN adrs3 END)) :: VARCHAR pls_current,
            (vb.adrs3) :: VARCHAR         vendor_bill,
            max(CASE WHEN s36 = 1
              THEN modtime END)           last_modified,
            max(CASE WHEN s36 = 1
              THEN modified_by END)       modified_by,
            max(CASE WHEN s36 = 1 AND is_first != 1
              THEN tp END)                modified_table,
            max(CASE WHEN s36 = 1 AND is_first != 1
              THEN id END)                modified_id,
            36                            ordr
          FROM bs
          --where rnk=1
          UNION ALL
          SELECT
            'Bill To Info'               grp,
            'City, State, ZIP'           fld_name,
            (max(CASE WHEN is_first = 1
              THEN adr3 END)) :: VARCHAR pls_quoted,
            (max(CASE WHEN is_last = 1
              THEN adr3 END)) :: VARCHAR pls_current,
            (vb.adr3) :: VARCHAR         vendor_bill,
            max(CASE WHEN s37 = 1
              THEN modtime END)          last_modified,
            max(CASE WHEN s37 = 1
              THEN modified_by END)      modified_by,
            max(CASE WHEN s37 = 1 AND is_first != 1
              THEN tp END)               modified_table,
            max(CASE WHEN s37 = 1 AND is_first != 1
              THEN id END)               modified_id,
            37                           ordr
          FROM bs
          --where rnk=1
        )
      SELECT
        grp,
        fld_name,
        pls_quoted,
        pls_current,
        vendor_bill,
        last_modified,
        modified_by,
        modified_table,
        modified_id,
        ordr + 1,
        NULL,
        NULL,
        NULL
      --  into v_trace_load_l1_tab
      FROM a;
  END LOOP;
  --execute flatbed.copydata();
END;

$BODY$;
/
ALTER FUNCTION flatbed.populate_general() OWNER TO postgres;
/

-- FUNCTION: hist.fn_trace_load_ext(bigint, integer)

CREATE OR REPLACE FUNCTION hist.fn_trace_load_ext(
  p_load_id BIGINT,
  p_diff    INTEGER DEFAULT 1)
  RETURNS SETOF hist.TRACE_LOAD_EXT_TAB
LANGUAGE 'plpgsql'

COST 100
VOLATILE
ROWS 1000
AS $BODY$

DECLARE
  v_trace_load_tab hist.TRACE_LOAD_EXT_TAB;
BEGIN
  FOR v_trace_load_tab IN
  WITH bs AS
  (SELECT
     /*+ cardinality(3) no_merge */
     DISTINCT *
   FROM (SELECT
           1                    ttp,
           modtime,
           'LOADS'              tp,
           modified_by,
           l.load_id            id,
           lead(modified_by, 1, modified_by)
           OVER (
             PARTITION BY load_id
             ORDER BY modtime ) mod_personal_id
         FROM hist.vw_loads_hist l
         WHERE l.load_id = p_load_id
         UNION ALL
         SELECT
           1                    ttp,
           ld.modtime,
           'LOAD_DETAILS'       tp,
           ld.modified_by,
           ld.load_detail_id    id,
           lead(modified_by, 1, modified_by)
           OVER (
             PARTITION BY load_detail_id, load_id
             ORDER BY modtime ) mod_personal_id
         FROM hist.vw_ld_hist ld
         WHERE ld.load_id = p_load_id
               AND ((ld.point_type = 'O' AND ld.load_action = 'P') OR
                    (ld.point_type = 'D' AND ld.load_action = 'D'))
         UNION ALL
         SELECT
           1                       ttp,
           lm.modtime,
           'LOAD_MATERIALS'        tp,
           lm.modified_by,
           lm.load_material_id     id,
           lead(lm.modified_by, 1, lm.modified_by)
           OVER (
             PARTITION BY lm.load_material_id, lm.load_detail_id
             ORDER BY lm.modtime ) mod_personal_id
         FROM hist.vw_lmat_hist lm
         WHERE lm.load_detail_id IN
               (SELECT ld.load_detail_id
                FROM hist.vw_ld_hist ld
                WHERE ld.load_id = p_load_id
                      AND (ld.point_type = 'O' AND ld.load_action = 'P'))
         UNION ALL
         SELECT
           0                   ttp,
           lm.date_created     modtime,
           'LOAD_MATERIALS'    tp,
           lm.modified_by,
           lm.load_material_id id,
           lm.created_by       mod_personal_id
         FROM hist.vw_lmat_hist lm
           INNER JOIN hist.vw_ld_hist ld
             ON ld.load_detail_id = lm.load_detail_id
         WHERE ld.load_id = p_load_id
               AND ((ld.point_type = 'O' AND ld.load_action = 'P') /*or (ld.point_type = 'D' and ld.load_action = 'D')*/
               )
         UNION ALL
         SELECT
           1                       ttp,
           lp.modtime,
           'LTL_PRODUCT'           tp,
           lp.modified_by,
           lm.ltl_product_id       id,
           lead(lp.modified_by, 1, lp.modified_by)
           OVER (
             PARTITION BY lp.ltl_product_id
             ORDER BY lp.modtime ) mod_personal_id
         FROM hist.vw_lmat_hist lm
           INNER JOIN hist.vw_ld_hist ld
             ON ld.load_detail_id = lm.load_detail_id
           INNER JOIN hist.vw_ltl_product_hist lp
             ON lp.ltl_product_id = lm.ltl_product_id
         WHERE ld.load_id = p_load_id
               AND ((ld.point_type = 'O' AND ld.load_action = 'P') /*or (ld.point_type = 'D' and ld.load_action = 'D')*/
               )
         UNION ALL
         SELECT
           1                        ttp,
           adr.modtime,
           'ADDRESSES'              tp,
           adr.modified_by,
           adr.address_id           id,
           lead(adr.modified_by, 1, adr.modified_by)
           OVER (
             PARTITION BY adr.address_id
             ORDER BY adr.modtime ) mod_personal_id
         FROM hist.vw_addresses_hist adr
           INNER JOIN hist.vw_ld_hist ld
             ON ld.address_id = adr.address_id
         WHERE ld.load_id = p_load_id
               AND ((ld.point_type = 'O' AND ld.load_action = 'P') OR
                    (ld.point_type = 'D' AND ld.load_action = 'D'))
         UNION ALL
         SELECT
           1                        ttp,
           org.modtime,
           'ORGANIZATIONS'          tp,
           org.modified_by,
           org.org_id               id,
           lead(org.modified_by, 1, org.modified_by)
           OVER (
             PARTITION BY org.org_id
             ORDER BY org.modtime ) mod_personal_id
         FROM hist.vw_organizations_hist org
           INNER JOIN hist.vw_loads_hist l
             ON l.org_id = org.org_id
         WHERE l.load_id = p_load_id
         UNION ALL
         SELECT
           1                        ttp,
           org.modtime,
           'ORGANIZATIONS'          tp,
           org.modified_by,
           org.org_id               id,
           lead(org.modified_by, 1, org.modified_by)
           OVER (
             PARTITION BY org.org_id
             ORDER BY org.modtime ) mod_personal_id
         FROM hist.vw_organizations_hist org
           INNER JOIN hist.vw_loads_hist l
             ON l.award_carrier_org_id = org.org_id
         WHERE l.load_id = p_load_id
         UNION ALL
         SELECT
           1                       ttp,
           bt.modtime,
           'BILL_TO'               tp,
           bt.modified_by,
           bt.bill_to_id           id,
           lead(bt.modified_by, 1, bt.modified_by)
           OVER (
             PARTITION BY bt.bill_to_id
             ORDER BY bt.modtime ) mod_personal_id
         FROM hist.vw_bill_to_hist bt
           INNER JOIN hist.vw_loads_hist l
             ON l.bill_to = bt.bill_to_id
         WHERE l.load_id = p_load_id
         UNION ALL
         SELECT
           1                        ttp,
           lcd.modtime,
           'LOAD_COST_DETAILS'      tp,
           lcd.modified_by,
           lcd.cost_detail_id       id,
           lead(lcd.modified_by, 1, lcd.modified_by)
           OVER (
             PARTITION BY /*lcd.cost_detail_id,*/ lcd.load_id
             ORDER BY lcd.modtime ) mod_personal_id
         FROM hist.vw_lcd_hist lcd
         WHERE lcd.load_id = p_load_id
               AND lcd.status = 'A'
         UNION ALL
         SELECT
           1                   ttp,
           cdi.date_modified,
           'COST_DETAIL_ITEMS' tp,
           cdi.modified_by,
           /*cdi.item_id*/
           NULL                id,
           NULL                mod_personal_id
         FROM hist.load_cost_details lcd
           INNER JOIN rater.cost_detail_items cdi
             ON cdi.cost_detail_id = lcd.cost_detail_id
         WHERE lcd.load_id = p_load_id --and lcd.status = 'A'
         UNION ALL
         SELECT
           1                        ttp,
           bin.modtime,
           'BILLING_INVOICE_NODE'   tp,
           bin.modified_by,
           bin.billing_node_id      id,
           lead(bin.modified_by, 1, bin.modified_by)
           OVER (
             PARTITION BY bin.billing_node_id, bin.bill_to_id
             ORDER BY bin.modtime ) mod_personal_id
         FROM hist.vw_bin_hist bin
           --inner join hist.vw_bill_to_hist bt on bt.bill_to_id=bin.bill_to_id
           INNER JOIN hist.vw_loads_hist l
             ON l.bill_to = bin.bill_to_id
         WHERE l.load_id = p_load_id
         UNION ALL
         SELECT
           1                        ttp,
           adr.modtime,
           'ADDRESSES'              tp,
           adr.modified_by,
           adr.address_id           id,
           lead(adr.modified_by, 1, adr.modified_by)
           OVER (
             PARTITION BY adr.address_id
             ORDER BY adr.modtime ) mod_personal_id
         FROM hist.vw_addresses_hist adr
           INNER JOIN hist.vw_bin_hist bin
             ON bin.address_id = adr.address_id
           --inner join hist.vw_bill_to_hist bt on bt.bill_to_id=bin.bill_to_id
           INNER JOIN hist.vw_loads_hist l
             ON l.bill_to = bin.bill_to_id
         WHERE l.load_id = p_load_id
         UNION ALL
         SELECT
           1                        ttp,
           ol.modtime,
           'ORGANIZATION_LOCATIONS' tp,
           ol.modified_by,
           ol.location_id           id,
           lead(ol.modified_by, 1, ol.modified_by)
           OVER (
             PARTITION BY ol.location_id, ol.bill_to
             ORDER BY ol.modtime )  mod_personal_id
         FROM hist.vw_ol_hist ol
           --inner join hist.vw_bill_to_hist bt on bt.bill_to_id=ol.bill_to
           INNER JOIN hist.vw_loads_hist l
             ON l.bill_to = ol.bill_to
         WHERE l.load_id = p_load_id) subq1 --p_load_id)
   WHERE modtime != current_timestamp
         AND modtime >= (SELECT min(modtime)
                         FROM hist.vw_loads_hist l
                         WHERE l.load_id = p_load_id) --p_load_id)
   UNION ALL
   SELECT
     1 ttp,
     current_timestamp,
     NULL,
     NULL,
     NULL,
     NULL),
      l AS
    (SELECT
       /*+ inline push_pred cardinality(2) */
       lag(modtime)
       OVER (
         PARTITION BY load_id
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       l.*
     FROM hist.vw_loads_hist l
     WHERE l.load_id = p_load_id), --p_load_id),
      ld AS
    (SELECT
       /*+ no_merge inline push_pred cardinality(2) */
       lag(modtime)
       OVER (
         PARTITION BY load_detail_id, load_id
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       l.*
     FROM hist.vw_ld_hist l),
      adr AS
    (SELECT
       /*+ no_merge inline push_pred cardinality(2) index_rs(l) */
       lag(modtime)
       OVER (
         PARTITION BY address_id
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       l.*
     FROM hist.vw_addresses_hist l),
      mat AS
    (SELECT
       /*+ no_merge inline push_pred cardinality(2) */
       lag(modtime)
       OVER (
         PARTITION BY load_material_id, load_detail_id
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       dense_rank()
       OVER (
         PARTITION BY load_detail_id
         ORDER BY load_material_id )                     rnk,
       l.*
     FROM hist.vw_lmat_hist l
      --where l.load_material_id in (select load_material_id from flatbed.load_materials)
    ),
      org AS
    (SELECT
       /*+ no_merge inline push_pred cardinality(2) */
       lag(modtime)
       OVER (
         PARTITION BY org_id
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       l.*
     FROM hist.vw_organizations_hist l),
      bt AS
    (SELECT
       /*+ no_merge inline push_pred cardinality(2) */
       lag(modtime)
       OVER (
         PARTITION BY bill_to_id
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       l.*
     FROM hist.vw_bill_to_hist l),
      ltl AS
    (SELECT
       /*+ no_merge inline push_pred cardinality(2) */
       lag(modtime)
       OVER (
         PARTITION BY ltl_product_id
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       l.*
     FROM hist.vw_ltl_product_hist l),
      lcd AS
    (SELECT
       /*+ no_merge inline push_pred cardinality(2) */
       lag(modtime)
       OVER (
         PARTITION BY /*cost_detail_id,*/ load_id
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       l.*
     FROM hist.vw_lcd_hist l
     WHERE status = 'A'),
      cdi AS
    (SELECT
       /*+ no_merge inline push_pred cardinality(2) */
       lag(modtime)
       OVER (
         PARTITION BY item_id, cost_detail_id
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       l.*
     FROM hist.vw_cdi_hist l),
      bin AS
    (SELECT
       /*+ no_merge inline push_pred cardinality(2) */
       lag(modtime)
       OVER (
         PARTITION BY billing_node_id, bill_to_id
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       l.*
     FROM hist.vw_bin_hist l),
      ol AS
    (SELECT
       /*+ no_merge inline push_pred cardinality(2) */
       lag(modtime)
       OVER (
         PARTITION BY location_id, bill_to
         ORDER BY modtime ) + INTERVAL '0.000001' SECOND dt_from,
       modtime                                           dt_till,
       l.*
     FROM hist.vw_ol_hist l)
  --insert into hist.trace_load_ext_tab_table_data

  SELECT
    load_id,
    modtime,
    tp,
    id,
    CASE
    WHEN is_hide = 1
      THEN
        weight
    END weight,
    CASE
    WHEN is_hide = 1
      THEN
        commodity_class_code
    END commodity_class_code,
    CASE
    WHEN is_hide = 1
      THEN
        part_description
    END part_description,
    CASE
    WHEN is_hide = 1
      THEN
        product_code
    END product_code,
    CASE
    WHEN is_hide = 1
      THEN
        nmfc
    END nmfc,
    CASE
    WHEN is_hide = 1
      THEN
        dimensions
    END dimensions,
    CASE
    WHEN is_hide = 1
      THEN
        qty
    END qty,
    CASE
    WHEN is_hide = 1
      THEN
        package_type
    END package_type,
    CASE
    WHEN is_hide = 1
      THEN
        pieces
    END pieces,
    CASE
    WHEN is_hide = 1
      THEN
        stackable
    END stackable,
    CASE
    WHEN is_hide = 1
      THEN
        hazmat
    END hazmat,
    CASE
    WHEN is_hide = 1
      THEN
        hazmat_class
    END hazmat_class,
    lh_rev,
    acc_rev,
    lh_cost,
    acc_cost,
    contact,
    adrs1,
    adr1,
    country1,
    contact2,
    adrs2,
    adr2,
    country2,
    load_status,
    customer,
    carrier,
    location_bt,
    bill_to,
    adrs3,
    adr3,
    carrier_reference_number,
    shipper_reference_number,
    po_num,
    bol,
    gl_number,
    so_number,
    trailer,
    pro_num,
    pickup_num,
    inbound_outbound_flg,
    pay_terms,
    source_ind,
    finalization_status,
    frt_bill_recv_flag,
    award_date,
    dep1,
    dep2,
    modified_by,
    load_material_id,
    rnk,
    is_first,
    is_last,
    rw,
    diff
  --into v_trace_load_tab
  FROM (
         SELECT
           /*+ ordered
            use_nl(l ld1 adr1 ld2 adr2 mat1 lcd cdi bin adr3 bt ol)
            use_hash(bs)
            push_pred(ld1) push_pred(ld2)
            push_pred(adr1) push_pred(adr2) push_pred(adr3)
            push_pred(org1) push_pred(org2)
            push_pred(lcd) push_pred(cdi)
            push_pred(bin) push_pred(bt)
            push_pred(ol)
           */
           l.load_id,
           lag(bs.modtime)
           OVER (
             PARTITION BY rnk
             ORDER BY bs.modtime )                                          modtime,
           lag(bs.tp, bs.ttp)
           OVER (
             PARTITION BY rnk
             ORDER BY bs.modtime )                                          tp,
           lag(bs.id, bs.ttp)
           OVER (
             PARTITION BY rnk
             ORDER BY bs.modtime )                                          id,
           mat1.weight,
           mat1.commodity_class_code,
           mat1.part_description,
           ltl.product_code,
           mat1.nmfc || (CASE WHEN mat1.un_num IS NULL
             THEN ''
                         ELSE '-' || mat1.un_num END) AS                    nmfc,
           mat1.length || (CASE WHEN mat1.width IS NULL
             THEN ''
                           ELSE 'x' || mat1.width END) ||
           (CASE WHEN mat1.height IS NULL
             THEN ''
            ELSE 'x' || mat1.height END)              AS                    dimensions,
           --mat1.length || 'x' || mat1.width || 'x' || mat1.height dimensions,
           mat1.quantity                                                    qty,
           coalesce(mat1.package_type, mat1.ltl_package_type)               package_type,
           mat1.pieces,
           mat1.stackable,
           mat1.hazmat,
           mat1.hazmat_class,
           cdi.lh_rev,
           cdi.acc_rev,
           cdi.lh_cost,
           cdi.acc_cost,
           ld1.contact,
           adr1.address1                                                    adrs1,
           adr1.city || ', ' || adr1.state_code || ', ' || adr1.postal_code adr1,
           (SELECT name
            FROM flatbed.countries
            WHERE country_code = adr1.country_code)                         country1,
           ld2.contact                                                      contact2,
           adr2.address1                                                    adrs2,
           adr2.city || ', ' || adr2.state_code || ', ' || adr2.postal_code adr2,
           (SELECT name
            FROM flatbed.countries
            WHERE country_code = adr2.country_code)                         country2,
           l.load_status,
           org1.name                                                        customer,
           org2.name                                                        carrier,
           ol.location_name                                                 location_bt,
           bt.name                                                          bill_to,
           adr3.address1                                                    adrs3,
           adr3.city || ', ' || adr3.state_code || ', ' || adr3.postal_code adr3,
           l.carrier_reference_number,
           l.shipper_reference_number,
           l.po_num,
           l.bol,
           l.gl_number,
           l.so_number,
           l.trailer,
           l.pro_num,
           l.pickup_num,
           l.inbound_outbound_flg,
           l.pay_terms,
           l.source_ind,
           l.finalization_status,
           l.frt_bill_recv_flag,
           l.award_date,
           ld1.departure                                                    dep1,
           ld2.departure                                                    dep2,
           lag(first_name || ' ' || last_name, bs.ttp)
           OVER (
             PARTITION BY rnk
             ORDER BY bs.modtime )                                          modified_by,
           mat1.load_material_id,
           rnk,
           CASE
           WHEN rank()
                OVER (
                  PARTITION BY rnk
                  ORDER BY bs.modtime ) = 1
             THEN
               1
           ELSE
             0
           END                                                              is_first,
           CASE
           WHEN rank()
                OVER (
                  PARTITION BY rnk
                  ORDER BY bs.modtime DESC ) = 1
             THEN
               1
           ELSE
             0
           END                                                              is_last,
           row_number()
           OVER (
             PARTITION BY rnk
             ORDER BY bs.modtime )                                          rw,
           CASE
           WHEN bs.modtime < mat1.date_created AND rnk > 1
             THEN
               0
           ELSE
             1
           END                                                              is_hide,
           CASE
           WHEN (mat1.weight || mat1.commodity_class_code ||
                 mat1.part_description || ltl.product_code || mat1.nmfc ||
                 mat1.un_num || mat1.length || mat1.width || mat1.height ||
                 mat1.quantity ||
                 coalesce(mat1.package_type, mat1.ltl_package_type) || mat1.pieces ||
                 mat1.stackable || mat1.hazmat || mat1.hazmat_class ||
                 cdi.lh_rev || cdi.acc_rev || cdi.lh_cost || cdi.acc_cost ||
                 adr1.city || adr1.state_code || adr1.postal_code ||
                 adr1.address1 || adr1.country_code || ld1.contact ||
                 adr2.city || adr2.state_code || adr2.postal_code ||
                 adr2.address1 || adr2.country_code || ld2.contact ||
                 l.load_status || org1.name || org2.name || bt.name ||
                 adr3.city || adr3.state_code || adr3.postal_code ||
                 adr3.address1 || ol.location_name ||
                 l.carrier_reference_number || l.shipper_reference_number ||
                 l.po_num || l.bol || l.gl_number || l.so_number || l.trailer ||
                 l.pro_num || l.pickup_num || l.inbound_outbound_flg ||
                 l.pay_terms || l.source_ind || l.finalization_status ||
                 l.frt_bill_recv_flag || l.award_date || ld1.departure ||
                 ld2.departure || CASE
                                  WHEN bs.modtime < mat1.date_created
                                    THEN
                                      0
                                  ELSE
                                    1
                                  END) =
                lag(mat1.weight || mat1.commodity_class_code ||
                    mat1.part_description || ltl.product_code || mat1.nmfc ||
                    mat1.un_num || mat1.length || mat1.width || mat1.height ||
                    mat1.quantity ||
                    coalesce(mat1.package_type, mat1.ltl_package_type) ||
                    mat1.pieces || mat1.stackable || mat1.hazmat ||
                    mat1.hazmat_class || cdi.lh_rev || cdi.acc_rev ||
                    cdi.lh_cost || cdi.acc_cost || adr1.city ||
                    adr1.state_code || adr1.postal_code || adr1.address1 ||
                    adr1.country_code || ld1.contact || adr2.city ||
                    adr2.state_code || adr2.postal_code || adr2.address1 ||
                    adr2.country_code || ld2.contact || l.load_status ||
                    org1.name || org2.name || bt.name || adr3.city ||
                    adr3.state_code || adr3.postal_code || adr3.address1 ||
                    ol.location_name || l.carrier_reference_number ||
                    l.shipper_reference_number || l.po_num || l.bol ||
                    l.gl_number || l.so_number || l.trailer || l.pro_num ||
                    l.pickup_num || l.inbound_outbound_flg || l.pay_terms ||
                    l.source_ind || l.finalization_status ||
                    l.frt_bill_recv_flag || l.award_date || ld1.departure ||
                    ld2.departure || CASE
                                     WHEN bs.modtime < mat1.date_created
                                       THEN
                                         0
                                     ELSE
                                       1
                                     END)
                OVER (
                  PARTITION BY rnk
                  ORDER BY bs.modtime )
             THEN
               0
           ELSE
             1
           END                                                              diff
         FROM l
           INNER JOIN bs
             ON bs.modtime BETWEEN l.dt_from AND l.dt_till
           LEFT OUTER JOIN ld ld1
             ON l.load_id = ld1.load_id
                AND bs.modtime BETWEEN ld1.dt_from AND ld1.dt_till
                AND ld1.point_type = 'O'
                AND ld1.load_action = 'P'
           LEFT OUTER JOIN ld ld2
             ON l.load_id = ld2.load_id
                AND bs.modtime BETWEEN ld2.dt_from AND ld2.dt_till
                AND ld2.point_type = 'D'
                AND ld2.load_action = 'D'
           LEFT OUTER JOIN adr adr1
             ON adr1.address_id = ld1.address_id
                AND bs.modtime BETWEEN adr1.dt_from AND adr1.dt_till
           LEFT OUTER JOIN adr adr2
             ON adr2.address_id = ld2.address_id
                AND bs.modtime BETWEEN adr2.dt_from AND adr2.dt_till
           LEFT OUTER JOIN mat mat1
             ON mat1.load_detail_id = ld1.load_detail_id
                AND bs.modtime BETWEEN mat1.dt_from AND mat1.dt_till
           --and bs.modtime>=mat1.date_created
           LEFT OUTER JOIN ltl
             ON ltl.ltl_product_id = mat1.ltl_product_id
                AND bs.modtime BETWEEN ltl.dt_from AND ltl.dt_till
           --and bs.modtime>=mat1.date_created
           LEFT OUTER JOIN org org1
             ON org1.org_id = l.org_id
                AND bs.modtime BETWEEN org1.dt_from AND org1.dt_till
           LEFT OUTER JOIN org org2
             ON org2.org_id = l.award_carrier_org_id
                AND bs.modtime BETWEEN org2.dt_from AND org2.dt_till
           LEFT OUTER JOIN bt
             ON bt.bill_to_id = l.bill_to
                AND bs.modtime BETWEEN bt.dt_from AND bt.dt_till
           LEFT OUTER JOIN flatbed.users us
             ON us.person_id = bs.mod_personal_id
           LEFT OUTER JOIN lcd
             ON l.load_id = lcd.load_id
                AND bs.modtime BETWEEN lcd.dt_from AND lcd.dt_till
           LEFT OUTER JOIN (SELECT
                              cost_detail_id,
                              dt_from,
                              dt_till,
                              sum(CASE
                                  WHEN /*cdi.billable_status='Y' and*/
                                    cdi.ref_type = 'SRA'
                                    THEN
                                      cdi.subtotal
                                  END) lh_rev,
                              sum(CASE
                                  WHEN /*cdi.billable_status='Y' and*/
                                    ship_carr = 'S' AND
                                    cdi.ref_type NOT IN
                                    ('HX',
                                     'GX',
                                     'SO',
                                     'TX',
                                     'SRA',
                                     'CRA',
                                     'SBR',
                                     'FS',
                                     'SFS')
                                    THEN
                                      cdi.subtotal
                                  END) acc_rev,
                              sum(CASE
                                  WHEN /*cdi.billable_status='Y' and*/
                                    cdi.ref_type = 'CRA'
                                    THEN
                                      cdi.subtotal
                                  END) lh_cost,
                              sum(CASE
                                  WHEN /*cdi.billable_status='Y' and*/
                                    ship_carr = 'C' AND
                                    cdi.ref_type NOT IN
                                    ('HX',
                                     'GX',
                                     'SO',
                                     'TX',
                                     'SRA',
                                     'CRA',
                                     'SBR',
                                     'FS',
                                     'SFS')
                                    THEN
                                      cdi.subtotal
                                  END) acc_cost
                            FROM cdi
                            GROUP BY cost_detail_id, dt_from, dt_till) cdi
             ON lcd.cost_detail_id = cdi.cost_detail_id
                AND bs.modtime BETWEEN cdi.dt_from AND cdi.dt_till
           LEFT OUTER JOIN bin
             ON bin.bill_to_id = bt.bill_to_id
                AND bs.modtime BETWEEN bin.dt_from AND bin.dt_till
           LEFT OUTER JOIN adr adr3
             ON adr3.address_id = bin.address_id
                AND bs.modtime BETWEEN adr3.dt_from AND adr3.dt_till
           LEFT OUTER JOIN ol
             ON ol.bill_to = bt.bill_to_id
                AND bs.modtime BETWEEN ol.dt_from AND ol.dt_till) a
  WHERE (diff >= p_diff
         OR is_last = 1)
        AND rnk IS NOT NULL LOOP --;
    RETURN NEXT v_trace_load_tab;
  END LOOP;
  -- RETURN v_trace_load_tab;
END;

$BODY$;
/

ALTER FUNCTION hist.fn_trace_load_ext( BIGINT, INTEGER ) OWNER TO postgres;
/

-- FUNCTION: flatbed.populate_item(bigint, bigint)

CREATE OR REPLACE FUNCTION flatbed.populate_item(
  p_load_id BIGINT,
  itemcount BIGINT)
  RETURNS VOID
LANGUAGE 'plpgsql'

AS $BODY$

DECLARE
  --vb_item hist.trace_load_ext;
  -- bs hist.trace_load_ext_tab;
  l_weight                   NUMERIC(10, 2);
  l_commodity_class_code     CHARACTER VARYING;
  l_description              CHARACTER VARYING;
  l_commodity_code           CHARACTER VARYING;
  l_quantity                 BIGINT;
  l_packaging_code           CHARACTER VARYING;
  l_trace_load_ext_table_cnt INTEGER;
  v_trace_load_l1_tab        hist.TRACE_LOAD_L1_TAB;
BEGIN
  INSERT INTO hist.TRACE_LOAD_EXT_TAB_TABlE
    SELECT
      NULL,
      NULL,
      NULL,
      b.weight,
      b.commodity_class_code,
      b.description,
      NULL,
      b.commodity_code,
      NULL,
      b.quantity,
      b.packaging_code,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL
    --into vb_item
    FROM flatbed.carrier_invoice_details a
      LEFT OUTER JOIN flatbed.carrier_invoice_line_items b ON a.invoice_det_id = b.invoice_det_id --and b.order_num=1
    WHERE a.status = 'A' AND a.matched_load_id = p_load_id
          AND exists(SELECT 1
                     FROM hist.trace_load_ext_tab_table bs
                     WHERE bs.rnk = itemcount AND bs.is_last = 1 AND upper(b.description) = upper(bs.part_description))
    LIMIT 1;

  SELECT count(1)
  INTO l_trace_load_ext_table_cnt
  FROM hist.TRACE_LOAD_EXT_TAB_TABlE;
  IF l_trace_load_ext_table_cnt = 0
  THEN
    INSERT INTO hist.TRACE_LOAD_EXT_TAB_TABlE
      SELECT
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL;
  END IF;

  SELECT
    b.weight,
    b.commodity_class_code,
    b.description,
    b.commodity_code,
    b.quantity,
    b.packaging_code
  INTO l_weight, l_commodity_class_code, l_description, l_commodity_code, l_quantity, l_packaging_code
  FROM flatbed.carrier_invoice_details a
    LEFT OUTER JOIN flatbed.carrier_invoice_line_items b ON a.invoice_det_id = b.invoice_det_id --and b.order_num=1
  WHERE a.status = 'A' AND a.matched_load_id = p_load_id
        AND exists(SELECT 1
                   FROM hist.trace_load_ext_tab_table bs
                   WHERE bs.rnk = itemcount AND bs.is_last = 1 AND upper(b.description) = upper(bs.part_description))
  LIMIT 1;

  /* exception
    when no_data_found then
      vb_item := hist.trace_load_ext (null,null,null,null,null,null,null,null,null,
                                 null,null,null,null,null,null,null,null,null,
                                 null,null,null,null,null,null,null,null,null,
                                 null,null,null,null,null,null,null,null,null,
                                 null,null,null,null,null,null,null,null,null,
                                 null,null,null,null,null,null,null,null,null,
                                 null,null,null,null);
   
   */
  INSERT INTO HIST.trace_load_l1_tab_table
    WITH bs0 AS
    (
        SELECT
          /*+ cardinalit(5) materialize */
          bs.*,
          CASE WHEN coalesce(weight, -101) = lag(coalesce(weight, -101))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d1,
          CASE WHEN coalesce(commodity_class_code, '.#.#.') = lag(coalesce(commodity_class_code, '.#.#.'))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d2,
          CASE WHEN coalesce(part_description, '.#.#.') = lag(coalesce(part_description, '.#.#.'))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d3,
          CASE WHEN coalesce(product_code, '.#.#.') = lag(coalesce(product_code, '.#.#.'))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d4,
          CASE WHEN coalesce(nmfc, '.#.#.') = lag(coalesce(nmfc, '.#.#.'))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d5,
          CASE WHEN coalesce(dimensions, '.#.#.') = lag(coalesce(dimensions, '.#.#.'))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d6,
          CASE WHEN coalesce(qty, '.#.#.') = lag(coalesce(qty, '.#.#.'))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d7,
          CASE WHEN coalesce(package_type, '.#.#.') = lag(coalesce(package_type, '.#.#.'))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d8,
          CASE WHEN coalesce(pieces, -101) = lag(coalesce(pieces, -101))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d8_1,
          CASE WHEN coalesce(stackable, '.#.#.') = lag(coalesce(stackable, '.#.#.'))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d9,
          CASE WHEN coalesce(hazmat, '.#.#.') = lag(coalesce(hazmat, '.#.#.'))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d10,
          CASE WHEN coalesce(hazmat_class, '.#.#.') = lag(coalesce(hazmat_class, '.#.#.'))
          OVER (
            PARTITION BY rnk
            ORDER BY modtime )
            THEN 0
          ELSE 1 END                                                  d11,
          coalesce((SELECT 1
                    FROM flatbed.load_materials
                    WHERE load_material_id = bs.load_material_id), 0) is_lm_valid
        FROM hist.trace_load_ext_tab_table bs
        WHERE rnk = itemcount
        ORDER BY 33, 1 NULLS FIRST
    ),

        bs AS
      (
          SELECT
            /*+ cardinalit(5) materialize */
            bs0.*,
            CASE WHEN d1 = 1 AND row_number()
                                 OVER (
                                   PARTITION BY d1
                                   ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s1,
            CASE WHEN d2 = 1 AND row_number()
                                 OVER (
                                   PARTITION BY d2
                                   ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s2,
            CASE WHEN d3 = 1 AND row_number()
                                 OVER (
                                   PARTITION BY d3
                                   ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s3,
            CASE WHEN d4 = 1 AND row_number()
                                 OVER (
                                   PARTITION BY d4
                                   ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s4,
            CASE WHEN d5 = 1 AND row_number()
                                 OVER (
                                   PARTITION BY d5
                                   ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s5,
            CASE WHEN d6 = 1 AND row_number()
                                 OVER (
                                   PARTITION BY d6
                                   ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s6,
            CASE WHEN d7 = 1 AND row_number()
                                 OVER (
                                   PARTITION BY d7
                                   ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s7,
            CASE WHEN d8 = 1 AND row_number()
                                 OVER (
                                   PARTITION BY d8
                                   ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s8,
            CASE WHEN d8_1 = 1 AND row_number()
                                   OVER (
                                     PARTITION BY d8_1
                                     ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s8_1,
            CASE WHEN d9 = 1 AND row_number()
                                 OVER (
                                   PARTITION BY d9
                                   ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s9,
            CASE WHEN d10 = 1 AND row_number()
                                  OVER (
                                    PARTITION BY d10
                                    ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s10,
            CASE WHEN d11 = 1 AND row_number()
                                  OVER (
                                    PARTITION BY d11
                                    ORDER BY modtime DESC ) = 1
              THEN 1
            ELSE 0 END s11
          FROM bs0
      ),

        a AS
      (
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 1' grp,
          'Weight'                                       fld_name,
          (max(CASE WHEN is_first = 1
            THEN weight END)) :: VARCHAR                 pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN weight END)) :: VARCHAR                 pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN l_weight END) :: VARCHAR                vendor_bill,
          max(CASE WHEN s1 = 1
            THEN modtime END)                            last_modified,
          max(CASE WHEN s1 = 1
            THEN modified_by END)                        modified_by,
          max(CASE WHEN s1 = 1 AND is_first != 1
            THEN tp END)                                 modified_table,
          max(CASE WHEN s1 = 1 AND is_first != 1
            THEN id END)                                 modified_id,
          1                                              ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 2' grp,
          'Class'                                        fld_name,
          (max(CASE WHEN is_first = 1
            THEN commodity_class_code END)) :: VARCHAR   pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN commodity_class_code END)) :: VARCHAR   pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN l_commodity_class_code END) :: VARCHAR  vendor_bill,
          max(CASE WHEN s2 = 1
            THEN modtime END)                            last_modified,
          max(CASE WHEN s2 = 1
            THEN modified_by END)                        modified_by,
          max(CASE WHEN s2 = 1 AND is_first != 1
            THEN tp END)                                 modified_table,
          max(CASE WHEN s2 = 1 AND is_first != 1
            THEN id END)                                 modified_id,
          2                                              ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 3' grp,
          'Product Description'                          fld_name,
          (max(CASE WHEN is_first = 1
            THEN part_description END)) :: VARCHAR       pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN part_description END)) :: VARCHAR       pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN NULL END) :: VARCHAR                    vendor_bill,
          max(CASE WHEN s3 = 1
            THEN modtime END)                            last_modified,
          max(CASE WHEN s3 = 1
            THEN modified_by END)                        modified_by,
          max(CASE WHEN s3 = 1 AND is_first != 1
            THEN tp END)                                 modified_table,
          max(CASE WHEN s3 = 1 AND is_first != 1
            THEN id END)                                 modified_id,
          3                                              ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 4' grp,
          'SKU/Product code'                             fld_name,
          (max(CASE WHEN is_first = 1
            THEN product_code END)) :: VARCHAR           pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN product_code END)) :: VARCHAR           pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN NULL END) :: VARCHAR                    vendor_bill,
          max(CASE WHEN s4 = 1
            THEN modtime END)                            last_modified,
          max(CASE WHEN s4 = 1
            THEN modified_by END)                        modified_by,
          max(CASE WHEN s4 = 1 AND is_first != 1
            THEN tp END)                                 modified_table,
          max(CASE WHEN s4 = 1 AND is_first != 1
            THEN id END)                                 modified_id,
          4                                              ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 5' grp,
          'NMFC'                                         fld_name,
          (max(CASE WHEN is_first = 1
            THEN nmfc END)) :: VARCHAR                   pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN nmfc END)) :: VARCHAR                   pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN NULL END) :: VARCHAR                    vendor_bill,
          max(CASE WHEN s5 = 1
            THEN modtime END)                            last_modified,
          max(CASE WHEN s5 = 1
            THEN modified_by END)                        modified_by,
          max(CASE WHEN s5 = 1 AND is_first != 1
            THEN tp END)                                 modified_table,
          max(CASE WHEN s5 = 1 AND is_first != 1
            THEN id END)                                 modified_id,
          5                                              ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 6' grp,
          'Dimensions'                                   fld_name,
          (max(CASE WHEN is_first = 1
            THEN dimensions END)) :: VARCHAR             pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN dimensions END)) :: VARCHAR             pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN NULL END) :: VARCHAR                    vendor_bill,
          max(CASE WHEN s6 = 1
            THEN modtime END)                            last_modified,
          max(CASE WHEN s6 = 1
            THEN modified_by END)                        modified_by,
          max(CASE WHEN s6 = 1 AND is_first != 1
            THEN tp END)                                 modified_table,
          max(CASE WHEN s6 = 1 AND is_first != 1
            THEN id END)                                 modified_id,
          6                                              ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 7' grp,
          'Qty'                                          fld_name,
          (max(CASE WHEN is_first = 1
            THEN qty END)) :: VARCHAR                    pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN qty END)) :: VARCHAR                    pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN NULL END) :: VARCHAR                    vendor_bill,
          max(CASE WHEN s7 = 1
            THEN modtime END)                            last_modified,
          max(CASE WHEN s7 = 1
            THEN modified_by END)                        modified_by,
          max(CASE WHEN s7 = 1 AND is_first != 1
            THEN tp END)                                 modified_table,
          max(CASE WHEN s7 = 1 AND is_first != 1
            THEN id END)                                 modified_id,
          7                                              ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 8' grp,
          'Package Type'                                 fld_name,
          (max(CASE WHEN is_first = 1
            THEN package_type END)) :: VARCHAR           pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN package_type END)) :: VARCHAR           pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN NULL END) :: VARCHAR                    vendor_bill,
          max(CASE WHEN s8 = 1
            THEN modtime END)                            last_modified,
          max(CASE WHEN s8 = 1
            THEN modified_by END)                        modified_by,
          max(CASE WHEN s8 = 1 AND is_first != 1
            THEN tp END)                                 modified_table,
          max(CASE WHEN s8 = 1 AND is_first != 1
            THEN id END)                                 modified_id,
          8                                              ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 9' grp,
          'Pieces'                                       fld_name,
          (max(CASE WHEN is_first = 1
            THEN pieces END)) :: VARCHAR                 pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN pieces END)) :: VARCHAR                 pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN NULL END) :: VARCHAR                    vendor_bill,
          max(CASE WHEN s8_1 = 1
            THEN modtime END)                            last_modified,
          max(CASE WHEN s8_1 = 1
            THEN modified_by END)                        modified_by,
          max(CASE WHEN s8_1 = 1 AND is_first != 1
            THEN tp END)                                 modified_table,
          max(CASE WHEN s8_1 = 1 AND is_first != 1
            THEN id END)                                 modified_id,
          9                                              ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 10' grp,
          'Stackable'                                     fld_name,
          (max(CASE WHEN is_first = 1
            THEN stackable END)) :: VARCHAR               pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN stackable END)) :: VARCHAR               pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN NULL END) :: VARCHAR                     vendor_bill,
          max(CASE WHEN s9 = 1
            THEN modtime END)                             last_modified,
          max(CASE WHEN s9 = 1
            THEN modified_by END)                         modified_by,
          max(CASE WHEN s9 = 1 AND is_first != 1
            THEN tp END)                                  modified_table,
          max(CASE WHEN s9 = 1 AND is_first != 1
            THEN id END)                                  modified_id,
          9                                               ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 11' grp,
          'Hazmat'                                        fld_name,
          (max(CASE WHEN is_first = 1
            THEN hazmat END)) :: VARCHAR                  pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN hazmat END)) :: VARCHAR                  pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN NULL END) :: VARCHAR                     vendor_bill,
          max(CASE WHEN s10 = 1
            THEN modtime END)                             last_modified,
          max(CASE WHEN s10 = 1
            THEN modified_by END)                         modified_by,
          max(CASE WHEN s10 = 1 AND is_first != 1
            THEN tp END)                                  modified_table,
          max(CASE WHEN s10 = 1 AND is_first != 1
            THEN id END)                                  modified_id,
          10                                              ordr
        FROM bs
        UNION ALL
        SELECT
          'item' || trim(itemcount :: VARCHAR) || ' - 12' grp,
          'Hazmat Class'                                  fld_name,
          (max(CASE WHEN is_first = 1
            THEN hazmat_class END)) :: VARCHAR            pls_quoted,
          (max(CASE WHEN is_last = 1 AND is_lm_valid = 1
            THEN hazmat_class END)) :: VARCHAR            pls_current,
          (CASE WHEN max(is_lm_valid) = 1
            THEN NULL END) :: VARCHAR                     vendor_bill,
          max(CASE WHEN s11 = 1
            THEN modtime END)                             last_modified,
          max(CASE WHEN s11 = 1
            THEN modified_by END)                         modified_by,
          max(CASE WHEN s11 = 1 AND is_first != 1
            THEN tp END)                                  modified_table,
          max(CASE WHEN s11 = 1 AND is_first != 1
            THEN id END)                                  modified_id,
          11                                              ordr
        FROM bs
      )
    SELECT
      grp,
      fld_name,
      pls_quoted,
      pls_current,
      vendor_bill,
      last_modified,
      modified_by,
      modified_table,
      modified_id,
      -- nextval('rownum') as rownum /*ordr*/,
      ordr,
      NULL,
      NULL,
      NULL

    --  bulk collect into v_trace_load_l1_tab
    --into v_trace_load_l1_tab
    FROM a;
  --execute flatbed.copydata();
END;

$BODY$;
/

ALTER FUNCTION flatbed.populate_item( BIGINT, BIGINT ) OWNER TO postgres;
/

-- FUNCTION: flatbed.pls_audit_load_data_fnc(bigint)
CREATE OR REPLACE FUNCTION flatbed.pls_audit_load_data_fnc(
  p_load_id BIGINT)
  RETURNS SETOF hist.TRACE_LOAD_L1_TAB
LANGUAGE 'plpgsql'
AS $BODY$
DECLARE
  bs                             hist.TRACE_LOAD_EXT_TAB;
  v_trace_load_l1_tab            hist.TRACE_LOAD_L1_TAB;
  vb                             hist.TRACE_LOAD_EXT_TAB;
  vb_item                        hist.TRACE_LOAD_EXT_TAB;
  rz                             hist.TRACE_LOAD_L1_TAB;
  --i hist.TRACE_LOAD_EXT;
  i                              INTEGER;
  l_rnk_cnt                      INTEGER;
  l_trace_load_ext_table_cnt     INTEGER;
  l_trace_load_ext_tab_table_cnt INTEGER;
BEGIN
  DELETE FROM hist.trace_load_ext_tab_table;
  DELETE FROM hist.trace_load_l1_tab_table;
  DELETE FROM hist.trace_load_ext_table;
  --rz :=hist.trace_load_l1_tab();
  INSERT INTO hist.TRACE_LOAD_EXT_TAB_TABlE
    SELECT
      modtime,
      tp,
      id,
      weight,
      commodity_class_code,
      part_description,
      product_code,
      nmfc,
      dimensions,
      qty,
      package_type,
      pieces,
      stackable,
      hazmat,
      hazmat_class,
      lh_rev,
      acc_rev,
      lh_cost,
      acc_cost,
      contact1,
      adrs1,
      adr1,
      country1,
      contact2,
      adrs2,
      adr2,
      country2,
      load_status,
      customer,
      carrier,
      location_bt,
      bill_to,
      adrs3,
      adr3,
      carrier_reference_number,
      shipper_reference_number,
      po_num,
      bol,
      gl_number,
      so_number,
      trailer,
      pro_num,
      pickup_num,
      inbound_outbound_flg,
      pay_terms,
      source_ind,
      finalization_status,
      frt_bill_recv_flag,
      award_date,
      dep1,
      dep2,
      modified_by,
      load_material_id,
      rnk,
      is_first,
      is_last,
      rw,
      diff
    --into bs
    FROM
        hist.fn_trace_load_ext(p_load_id);

  SELECT count(1)
  INTO l_trace_load_ext_tab_table_cnt
  FROM hist.TRACE_LOAD_EXT_TAB_TABlE;
  IF l_trace_load_ext_tab_table_cnt = 0
  THEN
    INSERT INTO hist.TRACE_LOAD_EXT_TAB_TABlE
      SELECT
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL;
  END IF;

  INSERT INTO hist.TRACE_LOAD_EXT_TABlE
    SELECT
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      NULL,
      cc.lh_cost,
      cc.acc_cost,
      c.address_name,
      c.address1,
      c.city || ', ' || c.state || ', ' || c.postal_code,
      (SELECT name
       FROM flatbed.countries
       WHERE country_code = c.country_code),
      d.address_name,
      d.address1,
      d.city || ', ' || d.state || ', ' || d.postal_code,
      (SELECT name
       FROM flatbed.countries
       WHERE country_code = d.country_code),
      NULL,
      NULL,
      org.name,
      NULL,
      NULL,
      NULL,
      NULL,
      a.reference_num,
      a.shipper_reference_number,
      a.po_num,
      a.bol,
      NULL,
      NULL,
      NULL,
      a.pro_number,
      a.reference_num,
      NULL,
      a.pay_terms,
      NULL,
      NULL,
      NULL,
      NULL,
      a.act_pickup_date,
      a.delivery_date,
      NULL,
      NULL,
      -1,
      NULL,
      NULL,
      NULL,
      NULL
    --into vb
    FROM flatbed.carrier_invoice_details a
      LEFT OUTER JOIN flatbed.carrier_invoice_addr_details c
        ON a.invoice_det_id = c.invoice_det_id AND c.address_type = 'SH'
      LEFT OUTER JOIN flatbed.carrier_invoice_addr_details d
        ON a.invoice_det_id = d.invoice_det_id AND d.address_type = 'CN'
      LEFT OUTER JOIN flatbed.organizations org ON a.carrier_id = org.org_id
      LEFT OUTER JOIN
      (
        SELECT
          invoice_det_id,
          sum(CASE WHEN cdi.ref_type = 'CRA'
            THEN cdi.subtotal END) lh_cost,
          sum(CASE WHEN cdi.ref_type NOT IN ('HX', 'GX', 'SO', 'TX', 'SRA', 'CRA', 'SBR', 'FS', 'SFS')
            THEN cdi.subtotal END) acc_cost
        FROM flatbed.carrier_invoice_cost_items cdi
        GROUP BY invoice_det_id
      ) cc ON a.invoice_det_id = cc.invoice_det_id
    WHERE a.status = 'A' AND a.matched_load_id = p_load_id
    LIMIT 1;

  SELECT count(1)
  INTO l_trace_load_ext_table_cnt
  FROM hist.TRACE_LOAD_EXT_TABlE;
  IF l_trace_load_ext_table_cnt = 0
  THEN
    INSERT INTO hist.TRACE_LOAD_EXT_TABlE
      SELECT
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL;
  END IF;

  SELECT count(rnk)
  INTO l_rnk_cnt
  FROM hist.TRACE_LOAD_EXT_TAB_TABlE;
  IF l_rnk_cnt > 0
  THEN
    FOR i IN SELECT DISTINCT rnk
             FROM hist.TRACE_LOAD_EXT_TAB_TABlE tbl
             ORDER BY rnk
    LOOP
      EXECUTE flatbed.populate_item(p_load_id, i.rnk);
    END LOOP;
  END IF;

  EXECUTE flatbed.populate_general();
  FOR rz IN SELECT *
            FROM HIST.trace_load_l1_tab_table LOOP
    RETURN NEXT rz;
  END LOOP;
END;
$BODY$;
/

ALTER FUNCTION flatbed.pls_audit_load_data_fnc( BIGINT ) OWNER TO postgres;
/

-- FUNCTION: flatbed.ltl_lost_sav_opp_rpt_proc(numeric, date, date)

CREATE OR REPLACE FUNCTION flatbed.ltl_lost_sav_opp_rpt_proc(
  p_shipper_org_id NUMERIC,
  p_start_date     DATE,
  p_end_date       DATE)
  RETURNS NUMERIC
LANGUAGE 'plpgsql'

AS $BODY$

DECLARE
  lsor_job_id             NUMERIC;
  v_lc_carrier_org_id     NUMERIC;
  v_lc_carr_total_revenue NUMERIC;
  v_lc_carr_total_cost    NUMERIC;
  v_lc_carr_transit_time  NUMERIC;
  v_lc_carr_service_type  CHARACTER VARYING;
  l_job_id                NUMERIC;
    c_ltl_lsor_data CURSOR FOR
    SELECT
      l.load_id,
      l.bol,
      l.po_num,
      l.so_number,
      l.shipper_reference_number,
      ldo.scheduled_arrival AS est_pickup_date,
      ldo.departure,
      l.org_id,
      l.award_carrier_org_id,
      ldo.load_detail_id,
      ldo.contact           AS shipper_name,
      adro.city             AS orig_city,
      adro.state_code       AS orig_state_code,
      adro.postal_code      AS orig_postal_code,
      adro.country_code     AS orig_country,
      ldd.contact           AS consignee_name,
      adrd.city             AS dest_city,
      adrd.state_code       AS dest_state_code,
      adrd.postal_code      AS dest_postal_code,
      adrd.country_code     AS dest_country,
      sel_prop.GUARANTEED_TIME,
      sel_prop.HAZMAT_FLAG,
      sel_prop.total_weight,
      sel_prop.total_revenue,
      sel_prop.total_cost,
      sel_prop.transit_time,
      sel_prop.service_type,
      sel_prop.LTL_PRIC_PROPOSAL_ID,
      l.created_by          AS ld_created_by,
      l.date_created        AS ld_created_date
    FROM flatbed.loads l
      INNER JOIN flatbed.load_details ldo
        ON l.load_id = ldo.load_id
           AND ldo.point_type = 'O'
           AND l.originating_system IN ('PLS2_LT', 'GS')
           AND l.load_status NOT IN ('C', 'PO', 'PA')
           AND l.org_id = p_shipper_org_id
           AND ldo.scheduled_arrival BETWEEN p_start_date :: DATE AND
           p_end_date :: DATE
      INNER JOIN flatbed.load_details ldd
        ON l.load_id = ldd.load_id
           AND ldd.point_type = 'D'
      INNER JOIN flatbed.addresses adro
        ON ldo.address_id = adro.address_id
      INNER JOIN flatbed.addresses adrd
        ON ldd.address_id = adrd.address_id
      INNER JOIN rater.load_cost_details lcd
        ON lcd.load_id = l.load_id
           AND lcd.status = 'A'
           AND (lcd.revenue_override IS NULL OR lcd.revenue_override = 'N')
           AND (lcd.cost_override IS NULL OR lcd.cost_override = 'N')
      INNER JOIN flatbed.LTL_PRICING_PROPOSALS sel_prop
        ON sel_prop.load_id = l.load_id
           AND sel_prop.proposal_selected = 'Y'
           AND sel_prop.status = 'A';
BEGIN
  FOR i IN c_ltl_lsor_data LOOP
    SELECT
      lpp.carrier_org_id,
      lcc_prop.total_revenue,
      lcc_prop.total_cost,
      lcc_prop.transit_time,
      lcc_prop.service_type
    INTO v_lc_carrier_org_id,
      v_lc_carr_total_revenue,
      v_lc_carr_total_cost,
      v_lc_carr_transit_time,
      v_lc_carr_service_type
    FROM (SELECT
            load_id,
            min(total_revenue) AS total_revenue
          FROM flatbed.LTL_PRICING_PROPOSALS
          WHERE load_id = i.load_id
                AND status = 'A'
          GROUP BY load_id) ltl_pric_prop,
      flatbed.LTL_PRICING_PROPOSALS lcc_prop,
      flatbed.ltl_pricing_profile lpp
    WHERE ltl_pric_prop.total_revenue = lcc_prop.total_revenue
          AND lpp.ltl_pricing_profile_id = lcc_prop.ltl_pricing_profile_id
          AND ltl_pric_prop.load_id = lcc_prop.load_id
          AND lcc_prop.status = 'A'
    LIMIT 1;
    INSERT INTO flatbed.LOST_SAV_OPP_RPT_DATA
    (LSO_RPT_DATA_ID,
     JOB_ID,
     LOAD_ID,
     BOL,
     PO_NUM,
     SO_NUMBER,
     SHIPPER_REFERENCE_NUMBER,
     EST_PICKUP_DATE,
     PICKUP_DATE,
     SHIPPER_ORG_ID,
     CARRIER_ORG_ID,
     SHIPPER_NAME,
     ORIG_CITY,
     ORIG_STATE,
     ORIG_ZIP,
     ORIG_COUNTRY,
     CONSIGNEE_NAME,
     DEST_CITY,
     DEST_STATE,
     DEST_ZIP,
     DEST_COUNTRY,
     GUARANTEED_TIME,
     HAZMAT_FLAG,
     TOTAL_WEIGHT,
     TOTAL_REVENUE,
     TOTAL_COST,
     TRANSIT_TIME,
     SERVICE_TYPE,
     LOAD_CREATED_BY,
     LOAD_CREATED_DATE,
     LC_CARRIER_ORG_ID,
     LC_CARR_TOTAL_REVENUE,
     LC_CARR_TOTAL_COST,
     LC_CARR_TRANSIT_TIME,
     LC_CARR_SERVICE_TYPE,
     REVENUE_SAVINGS,
     REV_SAVINGS_PERC,
     DATE_CREATED)
    VALUES
      (nextval('flatbed.LSO_RPT_DATA_ID_SEQ'),
        -1,
        i.load_id,
        i.bol,
        i.po_num,
        i.so_number,
        i.shipper_reference_number,
        i.est_pickup_date,
        i.departure,
        i.org_id,
        i.award_carrier_org_id,
        i.shipper_name,
        i.orig_city,
        i.orig_state_code,
        i.orig_postal_code,
        i.orig_country,
        i.consignee_name,
        i.dest_city,
        i.dest_state_code,
        i.dest_postal_code,
        i.dest_country,
        i.guaranteed_time,
        i.hazmat_flag,
        i.total_weight,
        i.total_revenue,
        i.total_cost,
        i.transit_time,
        i.service_type,
        i.ld_created_by,
        i.ld_created_date,
        v_lc_carrier_org_id,
       v_lc_carr_total_revenue,
       v_lc_carr_total_cost,
       v_lc_carr_transit_time,
       v_lc_carr_service_type,
       (i.total_revenue - v_lc_carr_total_revenue),
       (((i.total_revenue - v_lc_carr_total_revenue) * 100) /
        i.total_revenue),
       current_timestamp :: DATE);

    INSERT INTO flatbed.LOST_SAV_OPP_RPT_MATERIALS
    (LSO_RPT_MATERIAL_ID,
     LSO_RPT_DATA_ID,
     PART_DESCRIPTION,
     WEIGHT,
     COMMODITY_CLASS_CODE,
     HAZMAT_FLAG,
     DATE_CREATED)
      (SELECT
         nextval('flatbed.LSO_RPT_MATERIAL_ID_SEQ'),
         nextval('flatbed.LSO_RPT_DATA_ID_SEQ'),
         lm.PART_DESCRIPTION,
         lm.weight,
         lm.commodity_class_code,
         lm.hazmat,
         current_timestamp :: DATE
       FROM flatbed.LTL_PRIC_PROP_MATERIALS lm
       WHERE lm.load_id = i.load_id
             AND lm.status = 'A');

    INSERT INTO flatbed.LOST_SAV_OPP_RPT_ACC
    (LSO_RPT_ACCESSORIAL_ID,
     LSO_RPT_DATA_ID,
     ACCESSORIAL_TYPE,
     DATE_CREATED)
      (SELECT
         nextval('flatbed.LSO_RPT_ACCESSORIAL_ID_SEQ'),
         CURRVAL('flatbed.LSO_RPT_DATA_ID_SEQ'),
         sel_prop_details.REF_TYPE,
         current_timestamp :: DATE
       FROM flatbed.LTL_PRIC_PROP_COST_DETAILS sel_prop_details
       WHERE sel_prop_details.LTL_PRIC_PROPOSAL_ID =
             i.LTL_PRIC_PROPOSAL_ID
             AND sel_prop_details.SHIP_CARR = 'S'
             AND sel_prop_details.billable = 'Y'
             AND sel_prop_details.ref_type NOT IN
                 ('SRA', 'FS', 'CRA', 'SBR'));

  END LOOP;

  l_job_id := nextval('flatbed.LOST_SAV_OPP_RPT_JOB_SEQ');
  UPDATE flatbed.LOST_SAV_OPP_RPT_DATA
  SET JOB_ID = l_job_id
  WHERE JOB_ID = -1;
  lsor_job_id := l_job_id;
  --   exception when others then
  --    begin
  --   lsor_job_id:=-1;
  --  return lsor_job_id;
  -- end;
  RETURN lsor_job_id;
END;

$BODY$;
/

ALTER FUNCTION flatbed.ltl_lost_sav_opp_rpt_proc( NUMERIC, DATE, DATE ) OWNER TO postgres;
/ 


-- FUNCTION: flatbed.report_unbilled(numeric, date, character varying)

CREATE OR REPLACE FUNCTION flatbed.report_unbilled(
  p_customer     NUMERIC,
  p_enddate      DATE,
  p_businessunit CHARACTER VARYING)
  RETURNS TABLE(shipper_code CHARACTER VARYING, owner_name TEXT, load_id BIGINT, ship_date DATE, gl_date DATE, revenue NUMERIC, cost NUMERIC, margin NUMERIC)
LANGUAGE 'plpgsql'
AS $BODY$

BEGIN
  RETURN QUERY
  WITH data AS
  (SELECT
     sorg.company_code                             AS shipper_code,
     u.LAST_NAME || ', ' || u.FIRST_NAME           AS owner_name,
     l.load_id,
     l.ship_date,
     l.gl_date,
     SUM(lcd.total_revenue)                        AS revenue,
     SUM(lcd.total_costs)                          AS cost,
     SUM(lcd.total_revenue) - SUM(lcd.total_costs) AS margin
   FROM flatbed.loads l
     INNER JOIN rater.load_cost_details lcd
       ON l.load_id = lcd.load_id
          AND lcd.status = 'A'
          AND l.load_status IN ('PP', 'A', 'CD', 'DA')
          AND l.finalization_status IN
              ('NF', 'RB', 'FB', 'FBH', 'AB', 'ABH')
     INNER JOIN flatbed.bill_to bt
       ON bt.bill_to_id = l.bill_to
     INNER JOIN flatbed.billing_invoice_node bin
       ON bt.bill_to_id = bin.bill_to_id
          AND l.bill_to = bin.bill_to_id
     INNER JOIN flatbed.organizations sorg
       ON sorg.org_id = l.org_id
     INNER JOIN flatbed.company_codes cc
       ON sorg.network_id = cc.network_id
     LEFT OUTER JOIN flatbed.user_customer uc
       ON l.org_id = uc.org_id
          AND uc.status = 'A'
          AND uc.exp_date > current_timestamp
     LEFT JOIN flatbed.users u
       ON u.person_id = uc.person_id
   WHERE ((p_Customer IS NULL) OR
          (p_Customer IS NOT NULL AND sorg.org_id = p_Customer))
         AND (p_BusinessUnit IS NULL OR (p_BusinessUnit IS NOT NULL AND
                                         cc.company_code = p_BusinessUnit))
         AND l.award_date < p_EndDate + 1
         AND l.award_date >= p_EndDate - 6
   GROUP BY l.load_id,
     sorg.company_code,
     u.LAST_NAME || ', ' || u.FIRST_NAME,
     l.ship_date,
     l.gl_date,
     bt.name)
  SELECT
    d.shipper_code :: CHARACTER VARYING,
    d.owner_name,
    d.load_id,
    d.ship_date :: DATE,
    d.gl_date :: DATE,
    d.revenue :: NUMERIC(10, 2),
    d.cost :: NUMERIC(10, 2),
    d.margin :: NUMERIC(10, 2)
  FROM data d
  UNION ALL
  SELECT
    'Summary' :: CHARACTER VARYING   AS shipper_code,
    NULL                             AS owner_name,
    NULL                             AS load_id,
    NULL                             AS ship_date,
    NULL                             AS gl_date,
    sum(d.revenue) :: NUMERIC(10, 2) AS revenue,
    sum(d.cost) :: NUMERIC(10, 2)    AS cost,
    sum(d.margin) :: NUMERIC(10, 2)  AS margin
  FROM data d;
END;
$BODY$;
/

ALTER FUNCTION flatbed.report_unbilled( NUMERIC, DATE, CHARACTER VARYING ) OWNER TO postgres;
/

SET search_path = public;
/
