--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.1
-- Dumped by pg_dump version 10.0

-- Started on 2017-10-23 18:24:10 MSK

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 7 (class 2615 OID 22633)
-- Name: hist; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA hist;


ALTER SCHEMA hist OWNER TO postgres;

SET search_path = hist, pg_catalog;

--
-- TOC entry 2488 (class 1247 OID 42134)
-- Name: trace_load; Type: TYPE; Schema: hist; Owner: postgres
--

CREATE TYPE trace_load AS (
	modtime timestamp(6) without time zone,
	tp character varying(100),
	id integer,
	weight numeric(13,3),
	commodity_class_code character varying(100),
	part_description character varying(1000),
	product_code character varying(50),
	nmfc character varying(100),
	dimensions character varying(300),
	qty character varying(30),
	adr1 character varying(300),
	contact1 character varying(100),
	adr2 character varying(300),
	contact2 character varying(100),
	load_status character varying(2),
	customer character varying(240),
	carrier character varying(240),
	bill_to character varying(50),
	carrier_reference_number character varying(50),
	shipper_reference_number character varying(50),
	po_num character varying(50),
	bol character varying(50),
	gl_number character varying(50),
	so_number character varying(50),
	trailer character varying(30),
	inbound_outbound_flg character varying(1),
	pay_terms character varying(3),
	source_ind character varying(3),
	finalization_status character varying(5),
	frt_bill_recv_flag character varying(1),
	award_date timestamp(0) without time zone,
	dep1 timestamp(0) without time zone,
	dep2 timestamp(0) without time zone,
	modified_by character varying(70),
	load_material_id integer,
	rnk integer,
	is_first integer,
	is_last integer,
	rw integer,
	diff integer
);


ALTER TYPE trace_load OWNER TO postgres;

--
-- TOC entry 1322 (class 1247 OID 42140)
-- Name: trace_load_ext; Type: TYPE; Schema: hist; Owner: postgres
--

CREATE TYPE trace_load_ext AS (
	modtime timestamp(6) without time zone,
	tp character varying(100),
	id integer,
	weight numeric(13,3),
	commodity_class_code character varying(100),
	part_description character varying(1000),
	product_code character varying(50),
	nmfc character varying(100),
	dimensions character varying(300),
	qty character varying(30),
	package_type character varying(30),
	pieces integer,
	stackable character varying(1),
	hazmat character varying(1),
	hazmat_class character varying(100),
	lh_rev numeric(10,2),
	acc_rev numeric(10,2),
	lh_cost numeric(10,2),
	acc_cost numeric(10,2),
	contact1 character varying(100),
	adrs1 character varying(200),
	adr1 character varying(300),
	country1 character varying(50),
	contact2 character varying(100),
	adrs2 character varying(200),
	adr2 character varying(300),
	country2 character varying(50),
	load_status character varying(2),
	customer character varying(240),
	carrier character varying(240),
	location_bt character varying(200),
	bill_to character varying(50),
	adrs3 character varying(200),
	adr3 character varying(300),
	carrier_reference_number character varying(50),
	shipper_reference_number character varying(50),
	po_num character varying(50),
	bol character varying(50),
	gl_number character varying(50),
	so_number character varying(50),
	trailer character varying(30),
	pro_num character varying(50),
	pickup_num character varying(2000),
	inbound_outbound_flg character varying(1),
	pay_terms character varying(3),
	source_ind character varying(5),
	finalization_status character varying(5),
	frt_bill_recv_flag character varying(1),
	award_date timestamp(0) without time zone,
	dep1 timestamp(0) without time zone,
	dep2 timestamp(0) without time zone,
	modified_by character varying(70),
	load_material_id integer,
	rnk integer,
	is_first integer,
	is_last integer,
	rw integer,
	diff integer
);


ALTER TYPE trace_load_ext OWNER TO postgres;

--
-- TOC entry 1325 (class 1247 OID 42143)
-- Name: trace_load_ext_tab; Type: TYPE; Schema: hist; Owner: postgres
--

CREATE TYPE trace_load_ext_tab AS (
	modtime timestamp(6) without time zone,
	tp character varying(100),
	id integer,
	weight numeric(13,3),
	commodity_class_code character varying(100),
	part_description character varying(1000),
	product_code character varying(50),
	nmfc character varying(100),
	dimensions character varying(300),
	qty character varying(30),
	package_type character varying(30),
	pieces integer,
	stackable character varying(1),
	hazmat character varying(1),
	hazmat_class character varying(100),
	lh_rev numeric(10,2),
	acc_rev numeric(10,2),
	lh_cost numeric(10,2),
	acc_cost numeric(10,2),
	contact1 character varying(100),
	adrs1 character varying(200),
	adr1 character varying(300),
	country1 character varying(50),
	contact2 character varying(100),
	adrs2 character varying(200),
	adr2 character varying(300),
	country2 character varying(50),
	load_status character varying(2),
	customer character varying(240),
	carrier character varying(240),
	location_bt character varying(200),
	bill_to character varying(50),
	adrs3 character varying(200),
	adr3 character varying(300),
	carrier_reference_number character varying(50),
	shipper_reference_number character varying(50),
	po_num character varying(50),
	bol character varying(50),
	gl_number character varying(50),
	so_number character varying(50),
	trailer character varying(30),
	pro_num character varying(50),
	pickup_num character varying(2000),
	inbound_outbound_flg character varying(1),
	pay_terms character varying(3),
	source_ind character varying(5),
	finalization_status character varying(5),
	frt_bill_recv_flag character varying(1),
	award_date timestamp(0) without time zone,
	dep1 timestamp(0) without time zone,
	dep2 timestamp(0) without time zone,
	modified_by character varying(70),
	load_material_id integer,
	rnk integer,
	is_first integer,
	is_last integer,
	rw integer,
	diff integer
);


ALTER TYPE trace_load_ext_tab OWNER TO postgres;

--
-- TOC entry 1328 (class 1247 OID 42146)
-- Name: trace_load_l1; Type: TYPE; Schema: hist; Owner: postgres
--

CREATE TYPE trace_load_l1 AS (
	grp character varying(100),
	fld_name character varying(100),
	pls_quoted character varying(1000),
	pls_current character varying(1000),
	vendor_bill character varying(1000),
	last_modified timestamp(0) without time zone,
	modified_by character varying(100),
	modified_table character varying(100),
	modified_id integer,
	ordr integer,
	flag integer,
	flag_quoted_current integer,
	flag_current_vb integer
);


ALTER TYPE trace_load_l1 OWNER TO postgres;

--
-- TOC entry 1331 (class 1247 OID 42149)
-- Name: trace_load_l1_tab; Type: TYPE; Schema: hist; Owner: postgres
--

CREATE TYPE trace_load_l1_tab AS (
	grp character varying(100),
	fld_name character varying(100),
	pls_quoted character varying(1000),
	pls_current character varying(1000),
	vendor_bill character varying(1000),
	last_modified timestamp(0) without time zone,
	modified_by character varying(100),
	modified_table character varying(100),
	modified_id integer,
	ordr integer,
	flag integer,
	flag_quoted_current integer,
	flag_current_vb integer
);


ALTER TYPE trace_load_l1_tab OWNER TO postgres;

--
-- TOC entry 1334 (class 1247 OID 42152)
-- Name: trace_load_tab; Type: TYPE; Schema: hist; Owner: postgres
--

CREATE TYPE trace_load_tab AS (
	modtime timestamp(6) without time zone,
	tp character varying(100),
	id integer,
	weight numeric(13,3),
	commodity_class_code character varying(100),
	part_description character varying(1000),
	product_code character varying(50),
	nmfc character varying(100),
	dimensions character varying(300),
	qty character varying(30),
	adr1 character varying(300),
	contact1 character varying(100),
	adr2 character varying(300),
	contact2 character varying(100),
	load_status character varying(2),
	customer character varying(240),
	carrier character varying(240),
	bill_to character varying(50),
	carrier_reference_number character varying(50),
	shipper_reference_number character varying(50),
	po_num character varying(50),
	bol character varying(50),
	gl_number character varying(50),
	so_number character varying(50),
	trailer character varying(30),
	inbound_outbound_flg character varying(1),
	pay_terms character varying(3),
	source_ind character varying(3),
	finalization_status character varying(5),
	frt_bill_recv_flag character varying(1),
	award_date timestamp(0) without time zone,
	dep1 timestamp(0) without time zone,
	dep2 timestamp(0) without time zone,
	modified_by character varying(70),
	load_material_id integer,
	rnk integer,
	is_first integer,
	is_last integer,
	rw integer,
	diff integer
);


ALTER TYPE trace_load_tab OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 448 (class 1259 OID 22817)
-- Name: addresses; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE addresses (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    address_id bigint,
    latitude bigint,
    longitude bigint,
    address1 character varying(200),
    address2 character varying(200),
    city character varying(30),
    postal_code character varying(10),
    state_code character varying(6),
    country_code character varying(3),
    date_created timestamp without time zone,
    preferred_city character varying(30),
    status character(1),
    address3 character varying(200),
    address4 character varying(200),
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer
);


ALTER TABLE addresses OWNER TO postgres;

--
-- TOC entry 452 (class 1259 OID 22840)
-- Name: bill_to; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE bill_to (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    bill_to_id bigint,
    name character varying(50),
    org_id bigint,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    rate_contact_name character varying(100),
    rate_email_address character varying(100),
    rate_fax_area_code character varying(6),
    rate_fax_number character varying(10),
    payment_method character varying(50),
    version integer,
    credit_limit bigint,
    credit_hold character varying(1),
    override_credit_hold character varying(1),
    auto_credit_hold character varying(1),
    warning_no_of_days smallint,
    warning_date_start timestamp without time zone,
    unbilled_rev bigint,
    currency_code character varying(15),
    pay_terms character varying(50),
    status character varying(1),
    is_default character(1),
    pay_terms_id bigint,
    required_audit smallint DEFAULT 0,
    audit_instructions character varying(4000),
    credit_card_email character varying(225)
);


ALTER TABLE bill_to OWNER TO postgres;

--
-- TOC entry 4769 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN bill_to.required_audit; Type: COMMENT; Schema: hist; Owner: postgres
--

COMMENT ON COLUMN bill_to.required_audit IS '1- required special audit for bill_to; 0-no special audit for bill_to required';


--
-- TOC entry 4770 (class 0 OID 0)
-- Dependencies: 452
-- Name: COLUMN bill_to.audit_instructions; Type: COMMENT; Schema: hist; Owner: postgres
--

COMMENT ON COLUMN bill_to.audit_instructions IS 'auditing instructions';


--
-- TOC entry 439 (class 1259 OID 22742)
-- Name: bill_to_default_values; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE bill_to_default_values (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    bill_to_default_value_id integer,
    bill_to_id integer NOT NULL,
    inbound_outbound character varying(20) NOT NULL,
    edi_inbound_outbound character varying(20) NOT NULL,
    pay_terms character varying(20) NOT NULL,
    edi_pay_terms character varying(20) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version smallint NOT NULL,
    manual_bol_inbound_outbound character varying(20),
    manual_bol_pay_terms character varying(20)
);


ALTER TABLE bill_to_default_values OWNER TO postgres;

--
-- TOC entry 449 (class 1259 OID 22824)
-- Name: bill_to_req_field; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE bill_to_req_field (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    bill_to_req_field_id integer,
    bill_to_id bigint NOT NULL,
    field_name character varying(20) NOT NULL,
    status character varying(1) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version smallint NOT NULL
);


ALTER TABLE bill_to_req_field OWNER TO postgres;

--
-- TOC entry 434 (class 1259 OID 22705)
-- Name: bill_to_threshold_settings; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE bill_to_threshold_settings (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    bill_to_audit_threshold_id bigint,
    bill_to_id bigint,
    threshold_value double precision DEFAULT 1.99,
    total_revenue double precision,
    margin double precision,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer NOT NULL
);


ALTER TABLE bill_to_threshold_settings OWNER TO postgres;

--
-- TOC entry 447 (class 1259 OID 22809)
-- Name: billing_invoice_node; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE billing_invoice_node (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    billing_node_id bigint,
    bill_to_id bigint,
    network_id bigint,
    customer_id character varying(10),
    customer_number character varying(10),
    address_id bigint,
    version integer,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    contact_name character varying(255),
    phone_id bigint,
    fax_id bigint,
    contact_email character varying(255),
    customs_broker character varying(255),
    broker_phone_id bigint
);


ALTER TABLE billing_invoice_node OWNER TO postgres;

--
-- TOC entry 437 (class 1259 OID 22726)
-- Name: carrier_invoice_addr_details; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE carrier_invoice_addr_details (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    addr_det_id bigint NOT NULL,
    invoice_det_id bigint NOT NULL,
    address_type character varying(8) NOT NULL,
    address_name character varying(255),
    address1 character varying(200),
    address2 character varying(200),
    city character varying(30),
    state character varying(6),
    postal_code character varying(10),
    country_code character varying(3),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint NOT NULL
);


ALTER TABLE carrier_invoice_addr_details OWNER TO postgres;

--
-- TOC entry 429 (class 1259 OID 22675)
-- Name: carrier_invoice_cost_items; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE carrier_invoice_cost_items (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    invoice_cost_detail_item_id bigint NOT NULL,
    invoice_det_id bigint NOT NULL,
    ref_type character varying(10) NOT NULL,
    subtotal double precision,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint NOT NULL
);


ALTER TABLE carrier_invoice_cost_items OWNER TO postgres;

--
-- TOC entry 432 (class 1259 OID 22688)
-- Name: carrier_invoice_details; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE carrier_invoice_details (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    invoice_det_id bigint NOT NULL,
    invoice_num character varying(255),
    invoice_date timestamp without time zone,
    reference_num character varying(255),
    pay_terms character varying(8),
    net_amount double precision,
    delivery_date timestamp without time zone,
    est_delivery_date timestamp without time zone,
    bol character varying(25),
    po_num character varying(255),
    shipper_reference_number character varying(255),
    pro_number character varying(255),
    act_pickup_date timestamp without time zone,
    total_weight double precision,
    total_charges double precision,
    total_quantity bigint,
    matched character(1),
    matched_load_id bigint,
    carrier_id bigint NOT NULL,
    status character(1),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint NOT NULL,
    edi character(1),
    edi_account character varying(255)
);


ALTER TABLE carrier_invoice_details OWNER TO postgres;

--
-- TOC entry 423 (class 1259 OID 22634)
-- Name: cost_detail_items; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE cost_detail_items (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    item_id bigint,
    cost_detail_id bigint,
    ref_id bigint,
    ref_type character varying(10),
    subtotal double precision,
    quantity double precision,
    ship_carr character(1),
    unit_type character varying(10),
    unit_cost bigint,
    percentage character(1),
    percentage_of character(1),
    amount_uom character varying(3),
    version integer,
    billable_status character(1),
    override_flag character(1),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    tr_id bigint,
    rate_indicator character(1),
    extra_payable_org_id bigint,
    finan_adj_acc_detail_id bigint,
    bill_to_id bigint,
    supplier_site_code character varying(15),
    carrier_org_id bigint,
    dedicated_unit_id bigint,
    reason bigint,
    note character varying(500)
);


ALTER TABLE cost_detail_items OWNER TO postgres;

--
-- TOC entry 444 (class 1259 OID 22781)
-- Name: countries; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE countries (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    country_code character varying(3),
    name character varying(50),
    dialing_code character varying(3),
    status character varying(1),
    country_cd_short character varying(2)
);


ALTER TABLE countries OWNER TO postgres;

--
-- TOC entry 438 (class 1259 OID 22736)
-- Name: edi_settings; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE edi_settings (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    edi_settings_id bigint,
    bill_to_id bigint,
    edi_type character varying(60),
    edi_status character varying(90),
    unique_ref_bol character varying(1),
    created_by bigint,
    date_created timestamp without time zone,
    modified_by bigint,
    date_modified timestamp without time zone,
    version integer,
    ignore_204_updates character varying(1)
);


ALTER TABLE edi_settings OWNER TO postgres;

--
-- TOC entry 443 (class 1259 OID 22764)
-- Name: finan_adj_acc_detail; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE finan_adj_acc_detail (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    faa_detail_id bigint,
    load_id bigint,
    bol character varying(25),
    adj_acc character varying(3),
    revision integer,
    reason character varying(20),
    gl_date timestamp without time zone,
    faa_status character varying(5),
    total_revenue double precision,
    total_costs double precision,
    short_pay character(1) DEFAULT 'N'::bpchar,
    sent_to_finance character(1) DEFAULT 'N'::bpchar,
    created_by bigint,
    date_created timestamp without time zone,
    version integer DEFAULT 1,
    modified_by bigint,
    date_modified timestamp without time zone,
    status character(1) DEFAULT 'A'::bpchar,
    inv_approved character(1),
    customer_invoice_num character varying(20),
    do_not_invoice character varying(1),
    group_invoice_num character varying(25),
    invoiced_in_finan character(1)
);


ALTER TABLE finan_adj_acc_detail OWNER TO postgres;

--
-- TOC entry 424 (class 1259 OID 22642)
-- Name: invoice_settings; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE invoice_settings (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    invoice_settings_id bigint,
    bill_to_id bigint,
    invoice_type character varying(20),
    invoice_format_id bigint,
    processing_type character varying(20),
    processing_time integer,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint,
    processing_time_tz real,
    processing_period character varying(20),
    gainshare_only character(1),
    sort_type character varying(20),
    documents character varying(64),
    not_split_recipients character(1),
    edi_invoice character varying(1),
    cbi_invoice_type character varying(3),
    release_day_of_week smallint,
    processing_day_of_week smallint
);


ALTER TABLE invoice_settings OWNER TO postgres;

--
-- TOC entry 442 (class 1259 OID 22756)
-- Name: load_cost_details; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE load_cost_details (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    cost_detail_id bigint,
    load_id bigint,
    stopoffs smallint,
    weight bigint,
    miles double precision,
    total_revenue double precision,
    total_costs double precision,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    status character(1),
    unit_cost_id bigint,
    version integer,
    empty_miles smallint,
    truck_weight bigint,
    ship_date timestamp without time zone,
    billing_action character varying(10),
    invoice_number character varying(20),
    bol character varying(20),
    gl_date timestamp without time zone,
    sent_to_finance character(1),
    shipment_status character varying(30),
    accelerated_ind character(1),
    pieces bigint,
    override_margin_discrepancy character(1),
    new_prod_liab_amt double precision,
    used_prod_liab_amt double precision,
    service_type character varying(20),
    prohibited_commodities character varying(500),
    customer_invoice_num character varying(20),
    guaran_time bigint,
    guaran_bol_name character varying(500),
    pric_prof_detail_id bigint,
    group_invoice_num character varying(25)
);


ALTER TABLE load_cost_details OWNER TO postgres;

--
-- TOC entry 446 (class 1259 OID 22801)
-- Name: load_details; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE load_details (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    load_detail_id bigint,
    load_id bigint,
    load_action character varying(1),
    ticket character varying(50),
    bol character varying(25),
    arrival timestamp without time zone,
    departure timestamp without time zone,
    scheduled_arrival timestamp without time zone,
    need_appt character varying(1),
    contact character varying(100),
    instructions character varying(2000),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    po_num character varying(50),
    node_id character varying(50),
    op_bol character varying(20),
    point_type character(1),
    admit_date timestamp without time zone,
    no_early_schedule_reason character varying(3),
    part_num character varying(2000),
    sdsa_flag character(1),
    seq_in_route smallint,
    address_id bigint,
    early_scheduled_arrival timestamp without time zone,
    appointment_number character varying(20),
    no_late_schedule_reason character varying(3),
    not_yet_delivered timestamp without time zone,
    orig_deliver_no_later_than timestamp without time zone,
    orig_unload_dock_scheduled_at timestamp without time zone,
    arrival_tz real,
    departure_tz real,
    scheduled_arrival_tz real,
    admit_date_tz real,
    early_scheduled_arrival_tz real,
    not_yet_delivered_tz real,
    orig_deliver_no_later_than_tz real,
    orig_unload_dock_sched_at_tz real,
    contact_name character varying(50),
    contact_phone character varying(20),
    early_sdsa_flag character(1),
    loaded_date timestamp without time zone,
    loaded_date_tz real,
    version integer,
    arrival_window_start timestamp without time zone,
    arrival_window_end timestamp without time zone,
    contact_fax character varying(20),
    contact_email character varying(100),
    address_code character varying(50),
    notes character varying(3000),
    int_notes character varying(3000)
);


ALTER TABLE load_details OWNER TO postgres;

--
-- TOC entry 427 (class 1259 OID 22662)
-- Name: load_materials; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE load_materials (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    load_material_id bigint,
    load_detail_id bigint,
    release_id character varying(30),
    work_order character varying(25),
    shop_order character varying(25),
    cust_owned character varying(10),
    cust_po_num character varying(30),
    part_num character varying(25),
    cust_item_num character varying(250),
    weight double precision,
    material_type character varying(100),
    pickup_instr character varying(1000),
    delivery_instr character varying(1000),
    bol character varying(25),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    order_id bigint,
    status character varying(2),
    dropoff_load_detail_id bigint,
    is_ready character(1),
    heat_num character varying(30),
    pickup_date timestamp without time zone,
    pieces integer,
    length double precision,
    bundles smallint,
    grade character varying(30),
    cust_location character varying(30),
    dock_name character varying(30),
    node_id character varying(50),
    in_inventory character(1),
    pickup_date_tz real,
    hazmat character(1),
    package_type character varying(30),
    width double precision,
    height double precision,
    part_description character varying(1000),
    commodity_class_code character varying(8),
    gauge character varying(30),
    material_status character varying(30),
    mill_order_num character varying(30),
    mill_test_num character varying(30),
    nmfc character varying(30),
    product_id character varying(30),
    quantity character varying(30),
    sub_product_id character varying(30),
    thickness character varying(30),
    hazmat_class character varying(100),
    un_num character varying(32),
    packing_group character varying(30),
    emergency_number character varying(30),
    version integer,
    original_weight double precision,
    original_freight_class character varying(8),
    contract character varying(50),
    hazmat_instructions character varying(2000),
    emergency_contract character varying(32),
    emergency_company character varying(32),
    emergency_country_code character varying(10),
    emergency_area_code character varying(10),
    ltl_product_id bigint,
    ltl_package_type character varying(3),
    stackable character(1),
    emergency_extension character varying(6)
);


ALTER TABLE load_materials OWNER TO postgres;

--
-- TOC entry 451 (class 1259 OID 22833)
-- Name: loads; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE loads (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    load_id bigint,
    route_id bigint,
    load_status character varying(2),
    market_type character varying(2),
    commodity_cd character varying(10),
    container_cd character varying(10),
    org_id bigint,
    location_id bigint,
    person_id bigint,
    shipper_reference_number character varying(30),
    broker_reference_number character varying(20),
    inbound_outbound_flg character varying(1),
    date_closed timestamp without time zone,
    feature_code character varying(1),
    source_ind character varying(5),
    mileage double precision,
    pieces integer,
    weight bigint,
    weight_uom_code character varying(10),
    target_price double precision,
    awarded_offer bigint,
    award_price double precision,
    award_date timestamp without time zone,
    special_instructions character varying(3000),
    special_message character varying(3000),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    min_weight bigint,
    min_price double precision,
    rate_type character varying(2),
    unit_cost double precision,
    tender_cycle_id bigint,
    priority character varying(1),
    pay_terms character varying(3),
    bill_to bigint,
    one_time_rate_id bigint,
    out_route_miles integer,
    auto_cd_flg character(1),
    product_type character varying(2),
    finalization_status character varying(5),
    permit_load character(1),
    pro_num character varying(50),
    travel_time integer,
    chk_brn character varying(30),
    premium_available character(1),
    scheduled character(1),
    dispatcher_schedule_only character(1),
    truck_weight bigint,
    gross_weight bigint,
    barge_num character varying(6),
    sc_flag character(1),
    op_flag character(1),
    sw_flag character(1),
    srr_flag character(1),
    srm_flag character(1),
    mm_flag character(1),
    bol_instructions character varying(2000),
    template_set character varying(5),
    release_num character varying(30),
    lod_attribute1 character varying(30),
    lod_attribute2 character varying(30),
    lod_attribute3 character varying(30),
    lod_attribute4 character varying(30),
    lod_attribute5 character varying(50),
    lod_attribute6 character varying(30),
    original_sched_pickup_date timestamp without time zone,
    original_sched_delivery_date timestamp without time zone,
    carrier_reference_number character varying(30),
    shipper_premium_available character(1),
    after_hours_contact character varying(50),
    after_hours_phone character varying(50),
    is_ready character(1),
    reason_code character varying(25),
    fin_status_date timestamp without time zone,
    hfr_flag character(1),
    delivery_success character(1),
    freight_paid_by character varying(100),
    rate_contact character varying(100),
    routing_instructions character varying(2000),
    customer_tracking_email character varying(2000),
    notify_award_flag character(1),
    notify_schedule_flag character(1),
    notify_gate_flag character(1),
    notify_conf_pick_up_flag character(1),
    notify_conf_deliv_flag character(1),
    driver_name character varying(30),
    tractor_id character varying(30),
    driver_license character varying(30),
    trailer character varying(30),
    unit_number character varying(30),
    commodity_desc character varying(250),
    customer_truck_flag character(1),
    empty_weight bigint,
    cust_truck_scac character varying(4),
    cust_truck_carr_name character varying(240),
    cust_truck_person_name character varying(100),
    cust_truck_person_phone character varying(50),
    notify_gate_flag_char character(1),
    gl_date timestamp without time zone,
    hazmat_flag character(1),
    radio_active_flag character(1),
    customer_comments character varying(3000),
    over_height_flag character(1),
    over_length_flag character(1),
    over_width_flag character(1),
    super_load_flag character(1),
    height double precision,
    length double precision,
    width double precision,
    over_weight_flag character(1),
    notify_initial_msg_flag character(1),
    multi_dock_sched_rqrd character(1),
    permit_num character varying(30),
    radio_active_secure_flag character(1),
    booked character(1),
    orig_sched_pickup_date_tz real,
    orig_sched_delivery_date_tz real,
    etd_ovr_flg character(1),
    etd_date timestamp without time zone,
    etd_date_tz real,
    awarded_by bigint,
    origin_region_id bigint,
    destination_region_id bigint,
    target_rate_min double precision,
    target_rate_max double precision,
    target_rate_ovr_flg character(1),
    target_rate_id_min bigint,
    target_rate_id_max bigint,
    target_tr_id_min bigint,
    target_tr_id_max bigint,
    mileage_type character varying(2),
    mileage_version character varying(20),
    ship_light character(1),
    frt_bill_recv_date timestamp without time zone,
    frt_bill_recv_by bigint,
    frt_bill_number character varying(50),
    finan_no_load_flag character(1),
    service_level_cd character varying(3),
    award_carrier_org_id bigint,
    ship_date timestamp without time zone,
    blind_carrier character(1),
    gl_number character varying(50),
    bol character varying(25),
    po_num character varying(50),
    op_bol character varying(20),
    part_num character varying(2000),
    version integer,
    gl_ref_code character varying(50),
    award_dedicated_unit_id bigint,
    frt_bill_recv_flag character(1),
    pickup_num character varying(2000),
    frt_bill_amount double precision,
    originating_system character varying(10),
    inv_ship_rates_only character varying(1),
    inv_carr_rates_only character varying(1),
    customs_broker character varying(100),
    customs_broker_phone character varying(30),
    cust_req_doc_recv_flag character(1),
    so_number character varying(50),
    frt_bill_pay_to_id bigint,
    inv_approved character(1),
    volume_quote_id character varying(50),
    saved_quote_id bigint,
    assignee_person_id bigint,
    assigned_date timestamp without time zone,
    carrier_sales character(1)
);


ALTER TABLE loads OWNER TO postgres;

--
-- TOC entry 4771 (class 0 OID 0)
-- Dependencies: 451
-- Name: COLUMN loads.carrier_sales; Type: COMMENT; Schema: hist; Owner: postgres
--

COMMENT ON COLUMN loads.carrier_sales IS 'C or S flag';


--
-- TOC entry 436 (class 1259 OID 22719)
-- Name: ltl_product; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE ltl_product (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    ltl_product_id bigint,
    ltl_product_tracking_id bigint,
    org_id bigint,
    location_id bigint,
    product_code character varying(30),
    description character varying(1000),
    nmfc_num character varying(30),
    pieces integer,
    package_type character varying(100),
    weight double precision,
    commodity_class_code character varying(8),
    hazmat_flag character(1),
    hazmat_class character varying(100),
    un_num character varying(32),
    packing_group character varying(32),
    emergency_number character varying(32),
    created_by bigint,
    date_created timestamp without time zone,
    version integer,
    modified_by bigint,
    date_modified timestamp without time zone,
    status character(1),
    nmfc_sub_num character varying(30),
    hazmat_instructions character varying(2000),
    emergency_contract character varying(32),
    emergency_company character varying(32),
    emergency_country_code character varying(10),
    emergency_area_code character varying(10),
    person_id bigint,
    emergency_extension character varying(6)
);


ALTER TABLE ltl_product OWNER TO postgres;

--
-- TOC entry 433 (class 1259 OID 22697)
-- Name: organization_locations; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE organization_locations (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    location_id bigint,
    org_id bigint,
    location_name character varying(240),
    status character varying(1),
    contact_last_name character varying(30),
    contact_first_name character varying(20),
    contact_title character varying(50),
    contact_email character varying(50),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    bill_to bigint,
    op_org_id bigint,
    op_loc_id bigint,
    visible character(1),
    pricing_on_location character(1),
    auto_auto_start_time integer,
    auto_auto_end_time integer,
    rate_contact_name character varying(100),
    rate_email_address character varying(100),
    rate_fax_area_code character varying(6),
    rate_fax_number character varying(10),
    rate_default_minimum double precision,
    rate_default_type character varying(10),
    rate_car_default_minimum double precision,
    rate_car_default_type character varying(10),
    credit_limit bigint,
    sales_lead bigint,
    commodity_cd character varying(10),
    employer_num character varying(35),
    bill_to_adjustment bigint,
    bill_to_accessorial bigint,
    address_id bigint,
    version integer,
    must_pre_pay character(1),
    is_default character(1)
);


ALTER TABLE organization_locations OWNER TO postgres;

--
-- TOC entry 426 (class 1259 OID 22652)
-- Name: organization_phones; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE organization_phones (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    org_phone_id bigint,
    org_id bigint,
    location_id bigint,
    dialing_code character varying(3),
    area_code character varying(6),
    phone_number character varying(10),
    extension character varying(6),
    phone_type character varying(10),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint
);


ALTER TABLE organization_phones OWNER TO postgres;

--
-- TOC entry 435 (class 1259 OID 22712)
-- Name: organizations; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE organizations (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    org_id bigint,
    name character varying(240),
    employer_num character varying(35),
    org_type character varying(10),
    status character(1),
    contact_last_name character varying(30),
    contact_first_name character varying(20),
    contact_title character varying(50),
    contact_email character varying(50),
    mc_num character varying(15),
    scac character varying(4),
    org_id_parent bigint,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    loc_rate_override character varying(1),
    logo_path character varying(256),
    css_path character varying(256),
    status_reason character varying(8),
    qualcomm_id bigint,
    network_id bigint,
    critical_email_address character varying(100),
    tender_increment_time integer,
    open_market_time integer,
    max_num_carriers integer,
    not_exceed_shipper_amt character(1),
    incl_carr_not_hauled_lane character(1),
    auto_auto_start_time integer,
    auto_auto_end_time integer,
    rate_contact_name character varying(100),
    rate_email_address character varying(100),
    rate_fax_area_code character varying(6),
    rate_fax_number character varying(10),
    rate_default_minimum double precision,
    rate_default_type character varying(10),
    rate_auto_approve_days bigint,
    not_exceed_type character(1),
    max_carrier_rate double precision,
    max_carr_rate_unit_type character varying(10),
    rate_car_default_minimum double precision,
    rate_car_default_type character varying(10),
    address_id bigint,
    credit_limit bigint,
    sales_lead bigint,
    ltl_account_type character varying(3),
    ltl_client_code character varying(50),
    region_id bigint,
    accelerated_ind character(1),
    version integer,
    ltl_rate_type character varying(30),
    must_pre_pay character(1),
    account_executive bigint,
    eff_date timestamp without time zone,
    company_code character(2),
    override_credit_hold character varying(1),
    auto_credit_hold character varying(1),
    warning_no_of_days smallint,
    currency_code character varying(15),
    edi_account character varying(50),
    is_contract character(1),
    exp_date timestamp without time zone,
    logo_id bigint,
    from_vendor_bills character(1),
    product_list_primary_sort character varying(30),
    display_logo_on_bol character(1),
    PRINT_BARCODE character(1)
);


ALTER TABLE organizations OWNER TO postgres;

--
-- TOC entry 453 (class 1259 OID 22848)
-- Name: phones; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE phones (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    phone_id bigint,
    phone_type character varying(10),
    country_code character varying(3),
    area_code character varying(6),
    phone_number character varying(10),
    extension character varying(6),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer
);


ALTER TABLE phones OWNER TO postgres;

--
-- TOC entry 425 (class 1259 OID 22647)
-- Name: pls_credit_limit; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE pls_credit_limit (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    creditmax bigint,
    accountnum character varying(40),
    bill_to_id bigint
);


ALTER TABLE pls_credit_limit OWNER TO postgres;

--
-- TOC entry 440 (class 1259 OID 22746)
-- Name: pls_credit_limit_mview; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE pls_credit_limit_mview (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    creditmax bigint,
    accountnum character varying(20),
    bill_to_id bigint
);


ALTER TABLE pls_credit_limit_mview OWNER TO postgres;

--
-- TOC entry 430 (class 1259 OID 22680)
-- Name: pls_customer_terms; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE pls_customer_terms (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    term_id bigint NOT NULL,
    term_name character varying(100) NOT NULL,
    due_days bigint
);


ALTER TABLE pls_customer_terms OWNER TO postgres;

--
-- TOC entry 428 (class 1259 OID 22670)
-- Name: pls_open_balance; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE pls_open_balance (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    amountdue bigint,
    accountnum character varying(40),
    bill_to_id bigint
);


ALTER TABLE pls_open_balance OWNER TO postgres;

--
-- TOC entry 431 (class 1259 OID 22684)
-- Name: pls_open_balance_mview; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE pls_open_balance_mview (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    amountdue bigint,
    accountnum character varying(20),
    bill_to_id bigint
);


ALTER TABLE pls_open_balance_mview OWNER TO postgres;

--
-- TOC entry 441 (class 1259 OID 22751)
-- Name: pls_unbilled_rev_t; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE pls_unbilled_rev_t (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    org_id bigint,
    bill_to_id bigint,
    network_id bigint,
    company character varying(20),
    customer_name character varying(100),
    customer_number character varying(50),
    unbilled_rev bigint
);


ALTER TABLE pls_unbilled_rev_t OWNER TO postgres;

--
-- TOC entry 450 (class 1259 OID 22829)
-- Name: timezone; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE timezone (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    timezone_code character varying(10) NOT NULL,
    timezone_name character varying(40) NOT NULL,
    local_offset real NOT NULL,
    timezone real
);


ALTER TABLE timezone OWNER TO postgres;

--
-- TOC entry 445 (class 1259 OID 22785)
-- Name: users; Type: TABLE; Schema: hist; Owner: postgres
--

CREATE TABLE users (
    moduser character varying(15),
    modtime timestamp without time zone,
    modstatus character(1),
    person_id bigint NOT NULL,
    password character varying(100),
    accept_email character varying(1),
    email_address character varying(100) NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name character varying(30) NOT NULL,
    userid character varying(50) NOT NULL,
    status character varying(1) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    middle_name character varying(15),
    title character varying(100),
    prefix character varying(10),
    suffix character varying(10),
    org_id bigint,
    location_id bigint,
    im_flag character varying(1),
    last_login_date timestamp without time zone,
    brand_id bigint,
    multiip_allowed character(1) DEFAULT 'N'::bpchar,
    acceptance_ind character(1),
    user_reg_token character varying(100),
    version integer,
    parent_person_id bigint,
    master character(1) DEFAULT 'N'::bpchar NOT NULL,
    use_profile_contact character(1) DEFAULT 'N'::bpchar,
    cust_serv_info_type character varying(20),
    auth_token character varying(15),
    internal_external character varying(1) DEFAULT 'I'::character varying,
    password_expiry timestamp without time zone,
    password_modified timestamp without time zone,
    audit_os_user character varying(250),
    audit_host character varying(64)
);


ALTER TABLE users OWNER TO postgres;

--
-- TOC entry 4772 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN users.accept_email; Type: COMMENT; Schema: hist; Owner: postgres
--

COMMENT ON COLUMN users.accept_email IS 'Indicates whether person wants to receive email as part of email push functionality.';


--
-- TOC entry 4773 (class 0 OID 0)
-- Dependencies: 445
-- Name: COLUMN users.userid; Type: COMMENT; Schema: hist; Owner: postgres
--

COMMENT ON COLUMN users.userid IS 'User sign on ID for logging on to eFlatbed application';


-- Table: hist.trace_load_l1_tab_table

CREATE TABLE hist.trace_load_l1_tab_table
(
  grp                 CHARACTER VARYING(100) COLLATE pg_catalog."default",
  fld_name            CHARACTER VARYING(100) COLLATE pg_catalog."default",
  pls_quoted          CHARACTER VARYING(1000) COLLATE pg_catalog."default",
  pls_current         CHARACTER VARYING(1000) COLLATE pg_catalog."default",
  vendor_bill         CHARACTER VARYING(1000) COLLATE pg_catalog."default",
  last_modified       TIMESTAMP(0) WITHOUT TIME ZONE,
  modified_by         CHARACTER VARYING(100) COLLATE pg_catalog."default",
  modified_table      CHARACTER VARYING(100) COLLATE pg_catalog."default",
  modified_id         INTEGER,
  ordr                INTEGER,
  flag                INTEGER,
  flag_quoted_current INTEGER,
  flag_current_vb     INTEGER
)
WITH (
OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE hist.trace_load_l1_tab_table OWNER TO postgres;

  -- Table: hist.trace_load_ext_table

CREATE TABLE hist.trace_load_ext_table
(
    modtime timestamp with time zone,
    tp character varying(100) COLLATE pg_catalog."default",
    id bigint,
    weight numeric(10,2),
    commodity_class_code character varying COLLATE pg_catalog."default",
    part_description character varying COLLATE pg_catalog."default",
    product_code character varying COLLATE pg_catalog."default",
    nmfc character varying COLLATE pg_catalog."default",
    dimensions character varying COLLATE pg_catalog."default",
    qty character varying COLLATE pg_catalog."default",
    package_type character varying COLLATE pg_catalog."default",
    pieces integer,
    stackable character(1) COLLATE pg_catalog."default",
    hazmat character(1) COLLATE pg_catalog."default",
    hazmat_class character varying COLLATE pg_catalog."default",
    lh_rev numeric(10,2),
    acc_rev numeric(10,2),
    lh_cost numeric(10,2),
    acc_cost numeric(10,2),
    contact1 character varying COLLATE pg_catalog."default",
    adrs1 character varying COLLATE pg_catalog."default",
    adr1 character varying COLLATE pg_catalog."default",
    country1 character varying COLLATE pg_catalog."default",
    contact2 character varying COLLATE pg_catalog."default",
    adrs2 character varying COLLATE pg_catalog."default",
    adr2 character varying COLLATE pg_catalog."default",
    country2 character varying COLLATE pg_catalog."default",
    load_status character varying COLLATE pg_catalog."default",
    customer character varying COLLATE pg_catalog."default",
    carrier character varying COLLATE pg_catalog."default",
    location_bt character varying COLLATE pg_catalog."default",
    bill_to character varying COLLATE pg_catalog."default",
    adrs3 character varying COLLATE pg_catalog."default",
    adr3 character varying COLLATE pg_catalog."default",
    carrier_reference_number character varying COLLATE pg_catalog."default",
    shipper_reference_number character varying COLLATE pg_catalog."default",
    po_num character varying COLLATE pg_catalog."default",
    bol character varying COLLATE pg_catalog."default",
    gl_number character varying COLLATE pg_catalog."default",
    so_number character varying COLLATE pg_catalog."default",
    trailer character varying COLLATE pg_catalog."default",
    pro_num character varying COLLATE pg_catalog."default",
    pickup_num character varying COLLATE pg_catalog."default",
    inbound_outbound_flg character varying COLLATE pg_catalog."default",
    pay_terms character varying COLLATE pg_catalog."default",
    source_ind character varying COLLATE pg_catalog."default",
    finalization_status character varying COLLATE pg_catalog."default",
    frt_bill_recv_flag character(1) COLLATE pg_catalog."default",
    award_date timestamp without time zone,
    dep1 timestamp without time zone,
    dep2 timestamp without time zone,
    modified_by character varying(70) COLLATE pg_catalog."default",
    load_material_id bigint,
    rnk bigint,
    is_first integer,
    is_last integer,
    rw bigint,
    diff integer
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE hist.trace_load_ext_table OWNER to postgres;
  
-- Table: hist.trace_load_ext_tab_table


CREATE TABLE hist.trace_load_ext_tab_table
(
  modtime                  TIMESTAMP WITH TIME ZONE,
  tp                       CHARACTER VARYING(100) COLLATE pg_catalog."default",
  id                       BIGINT,
  weight                   NUMERIC(10,2),
  commodity_class_code     CHARACTER VARYING COLLATE pg_catalog."default",
  part_description         CHARACTER VARYING COLLATE pg_catalog."default",
  product_code             CHARACTER VARYING COLLATE pg_catalog."default",
  nmfc                     CHARACTER VARYING COLLATE pg_catalog."default",
  dimensions               CHARACTER VARYING COLLATE pg_catalog."default",
  qty                      CHARACTER VARYING COLLATE pg_catalog."default",
  package_type             CHARACTER VARYING COLLATE pg_catalog."default",
  pieces                   INTEGER,
  stackable                CHARACTER(1) COLLATE pg_catalog."default",
  hazmat                   CHARACTER(1) COLLATE pg_catalog."default",
  hazmat_class             CHARACTER VARYING COLLATE pg_catalog."default",
  lh_rev                   NUMERIC(10,2),
  acc_rev                  NUMERIC(10,2),
  lh_cost                  NUMERIC(10,2),
  acc_cost                 NUMERIC(10,2),
  contact1                 CHARACTER VARYING COLLATE pg_catalog."default",
  adrs1                    CHARACTER VARYING COLLATE pg_catalog."default",
  adr1                     CHARACTER VARYING COLLATE pg_catalog."default",
  country1                 CHARACTER VARYING COLLATE pg_catalog."default",
  contact2                 CHARACTER VARYING COLLATE pg_catalog."default",
  adrs2                    CHARACTER VARYING COLLATE pg_catalog."default",
  adr2                     CHARACTER VARYING COLLATE pg_catalog."default",
  country2                 CHARACTER VARYING COLLATE pg_catalog."default",
  load_status              CHARACTER VARYING COLLATE pg_catalog."default",
  customer                 CHARACTER VARYING COLLATE pg_catalog."default",
  carrier                  CHARACTER VARYING COLLATE pg_catalog."default",
  location_bt              CHARACTER VARYING COLLATE pg_catalog."default",
  bill_to                  CHARACTER VARYING COLLATE pg_catalog."default",
  adrs3                    CHARACTER VARYING COLLATE pg_catalog."default",
  adr3                     CHARACTER VARYING COLLATE pg_catalog."default",
  carrier_reference_number CHARACTER VARYING COLLATE pg_catalog."default",
  shipper_reference_number CHARACTER VARYING COLLATE pg_catalog."default",
  po_num                   CHARACTER VARYING COLLATE pg_catalog."default",
  bol                      CHARACTER VARYING COLLATE pg_catalog."default",
  gl_number                CHARACTER VARYING COLLATE pg_catalog."default",
  so_number                CHARACTER VARYING COLLATE pg_catalog."default",
  trailer                  CHARACTER VARYING COLLATE pg_catalog."default",
  pro_num                  CHARACTER VARYING COLLATE pg_catalog."default",
  pickup_num               CHARACTER VARYING COLLATE pg_catalog."default",
  inbound_outbound_flg     CHARACTER VARYING COLLATE pg_catalog."default",
  pay_terms                CHARACTER VARYING COLLATE pg_catalog."default",
  source_ind               CHARACTER VARYING COLLATE pg_catalog."default",
  finalization_status      CHARACTER VARYING COLLATE pg_catalog."default",
  frt_bill_recv_flag       CHARACTER(1) COLLATE pg_catalog."default",
  award_date               TIMESTAMP WITHOUT TIME ZONE,
  dep1                     TIMESTAMP WITHOUT TIME ZONE,
  dep2                     TIMESTAMP WITHOUT TIME ZONE,
  modified_by              CHARACTER VARYING(70) COLLATE pg_catalog."default",
  load_material_id         BIGINT,
  rnk                      BIGINT,
  is_first                 INTEGER,
  is_last                  INTEGER,
  rw                       BIGINT,
  diff                     INTEGER
)
WITH (
OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE hist.trace_load_ext_tab_table OWNER TO postgres;


--
-- TOC entry 899 (class 1259 OID 42069)
-- Name: vw_addresses_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_addresses_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.address_id,
    a.latitude,
    a.longitude,
    a.address1,
    a.address2,
    a.city,
    a.postal_code,
    a.state_code,
    a.country_code,
    a.date_created,
    a.preferred_city,
    a.status,
    a.address3,
    a.address4,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.version
   FROM addresses a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.address_id,
    a.latitude,
    a.longitude,
    a.address1,
    a.address2,
    a.city,
    a.postal_code,
    a.state_code,
    a.country_code,
    a.date_created,
    a.preferred_city,
    a.status,
    a.address3,
    a.address4,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.version
   FROM flatbed.addresses a
  ORDER BY 2 DESC;


ALTER TABLE vw_addresses_hist OWNER TO postgres;

--
-- TOC entry 900 (class 1259 OID 42077)
-- Name: vw_bill_to_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_bill_to_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.bill_to_id,
    a.name,
    a.org_id,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.rate_contact_name,
    a.rate_email_address,
    a.rate_fax_area_code,
    a.rate_fax_number,
    a.payment_method,
    a.version,
    a.credit_limit,
    a.credit_hold,
    a.override_credit_hold,
    a.auto_credit_hold,
    a.warning_no_of_days,
    a.warning_date_start,
    a.unbilled_rev,
    a.currency_code,
    a.pay_terms,
    a.status,
    a.is_default,
    a.pay_terms_id
   FROM bill_to a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.bill_to_id,
    a.name,
    a.org_id,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.rate_contact_name,
    a.rate_email_address,
    a.rate_fax_area_code,
    a.rate_fax_number,
    a.payment_method,
    a.version,
    a.credit_limit,
    a.credit_hold,
    a.override_credit_hold,
    a.auto_credit_hold,
    a.warning_no_of_days,
    a.warning_date_start,
    a.unbilled_rev,
    a.currency_code,
    a.pay_terms,
    a.status,
    a.is_default,
    a.pay_terms_id
   FROM flatbed.bill_to a
  ORDER BY 2 DESC;


ALTER TABLE vw_bill_to_hist OWNER TO postgres;

--
-- TOC entry 901 (class 1259 OID 42082)
-- Name: vw_bin_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_bin_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.billing_node_id,
    a.bill_to_id,
    a.network_id,
    a.customer_id,
    a.customer_number,
    a.address_id,
    a.version,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.contact_name,
    a.phone_id,
    a.fax_id,
    a.contact_email,
    a.customs_broker,
    a.broker_phone_id
   FROM billing_invoice_node a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.billing_node_id,
    a.bill_to_id,
    a.network_id,
    a.customer_id,
    a.customer_number,
    a.address_id,
    a.version,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.contact_name,
    a.phone_id,
    a.fax_id,
    a.contact_email,
    a.customs_broker,
    a.broker_phone_id
   FROM flatbed.billing_invoice_node a
  ORDER BY 2 DESC;


ALTER TABLE vw_bin_hist OWNER TO postgres;

--
-- TOC entry 923 (class 1259 OID 42233)
-- Name: vw_cdi_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_cdi_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.item_id,
    a.cost_detail_id,
    a.ref_id,
    a.ref_type,
    a.subtotal,
    a.quantity,
    a.ship_carr,
    a.unit_type,
    a.unit_cost,
    a.percentage,
    a.percentage_of,
    a.amount_uom,
    a.version,
    a.billable_status,
    a.override_flag,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.tr_id,
    a.rate_indicator,
    a.extra_payable_org_id,
    a.finan_adj_acc_detail_id,
    a.bill_to_id,
    a.supplier_site_code,
    a.carrier_org_id,
    a.dedicated_unit_id,
    a.reason,
    a.note
   FROM cost_detail_items a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.item_id,
    a.cost_detail_id,
    a.ref_id,
    a.ref_type,
    a.subtotal,
    a.quantity,
    a.ship_carr,
    a.unit_type,
    a.unit_cost,
    a.percentage,
    a.percentage_of,
    a.amount_uom,
    a.version,
    a.billable_status,
    a.override_flag,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.tr_id,
    a.rate_indicator,
    a.extra_payable_org_id,
    a.finan_adj_acc_detail_id,
    a.bill_to_id,
    a.supplier_site_code,
    a.carrier_org_id,
    a.dedicated_unit_id,
    a.reason,
    a.note
   FROM rater.cost_detail_items a
  ORDER BY 2 DESC;


ALTER TABLE vw_cdi_hist OWNER TO postgres;

--
-- TOC entry 902 (class 1259 OID 42092)
-- Name: vw_invset_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_invset_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.invoice_settings_id,
    a.bill_to_id,
    a.invoice_type,
    a.invoice_format_id,
    a.processing_type,
    a.processing_time,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.version,
    a.processing_time_tz,
    a.processing_period,
    a.gainshare_only,
    a.sort_type,
    a.documents,
    a.not_split_recipients,
    a.edi_invoice,
    a.cbi_invoice_type,
    a.release_day_of_week,
    a.processing_day_of_week
   FROM invoice_settings a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.invoice_settings_id,
    a.bill_to_id,
    a.invoice_type,
    a.invoice_format_id,
    a.processing_type,
    a.processing_time,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.version,
    a.processing_time_tz,
    a.processing_period,
    a.gainshare_only,
    a.sort_type,
    a.documents,
    a.not_split_recipients,
    a.edi_invoice,
    a.cbi_invoice_type,
    a.release_day_of_week,
    a.processing_day_of_week
   FROM flatbed.invoice_settings a
  ORDER BY 2 DESC;


ALTER TABLE vw_invset_hist OWNER TO postgres;

--
-- TOC entry 903 (class 1259 OID 42097)
-- Name: vw_lcd_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_lcd_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.cost_detail_id,
    a.load_id,
    a.stopoffs,
    a.weight,
    a.miles,
    a.total_revenue,
    a.total_costs,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.status,
    a.unit_cost_id,
    a.version,
    a.empty_miles,
    a.truck_weight,
    a.ship_date,
    a.billing_action,
    a.invoice_number,
    a.bol,
    a.gl_date,
    a.sent_to_finance,
    a.shipment_status,
    a.accelerated_ind,
    a.pieces,
    a.override_margin_discrepancy,
    a.new_prod_liab_amt,
    a.used_prod_liab_amt,
    a.service_type,
    a.prohibited_commodities,
    a.customer_invoice_num,
    a.guaran_time,
    a.guaran_bol_name,
    a.pric_prof_detail_id
   FROM load_cost_details a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.cost_detail_id,
    a.load_id,
    a.stopoffs,
    a.weight,
    a.miles,
    a.total_revenue,
    a.total_costs,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.status,
    a.unit_cost_id,
    a.version,
    a.empty_miles,
    a.truck_weight,
    a.ship_date,
    a.billing_action,
    a.invoice_number,
    a.bol,
    a.gl_date,
    a.sent_to_finance,
    a.shipment_status,
    a.accelerated_ind,
    a.pieces,
    a.override_margin_discrepancy,
    a.new_prod_liab_amt,
    a.used_prod_liab_amt,
    a.service_type,
    a.prohibited_commodities,
    a.customer_invoice_num,
    a.guaran_time,
    a.guaran_bol_name,
    a.pric_prof_detail_id
   FROM rater.load_cost_details a
  ORDER BY 2 DESC;


ALTER TABLE vw_lcd_hist OWNER TO postgres;

--
-- TOC entry 904 (class 1259 OID 42102)
-- Name: vw_ld_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_ld_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.load_detail_id,
    a.load_id,
    a.load_action,
    a.ticket,
    a.bol,
    a.arrival,
    a.departure,
    a.scheduled_arrival,
    a.need_appt,
    a.contact,
    a.instructions,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.po_num,
    a.node_id,
    a.op_bol,
    a.point_type,
    a.admit_date,
    a.no_early_schedule_reason,
    a.part_num,
    a.sdsa_flag,
    a.seq_in_route,
    a.address_id,
    a.early_scheduled_arrival,
    a.appointment_number,
    a.no_late_schedule_reason,
    a.not_yet_delivered,
    a.orig_deliver_no_later_than,
    a.orig_unload_dock_scheduled_at,
    a.arrival_tz,
    a.departure_tz,
    a.scheduled_arrival_tz,
    a.admit_date_tz,
    a.early_scheduled_arrival_tz,
    a.not_yet_delivered_tz,
    a.orig_deliver_no_later_than_tz,
    a.orig_unload_dock_sched_at_tz,
    a.contact_name,
    a.contact_phone,
    a.early_sdsa_flag,
    a.loaded_date,
    a.loaded_date_tz,
    a.version,
    a.arrival_window_start,
    a.arrival_window_end,
    a.contact_fax,
    a.contact_email,
    a.address_code,
    a.notes
   FROM load_details a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.load_detail_id,
    a.load_id,
    a.load_action,
    a.ticket,
    a.bol,
    a.arrival,
    a.departure,
    a.scheduled_arrival,
    a.need_appt,
    a.contact,
    a.instructions,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.po_num,
    a.node_id,
    a.op_bol,
    a.point_type,
    a.admit_date,
    a.no_early_schedule_reason,
    a.part_num,
    a.sdsa_flag,
    a.seq_in_route,
    a.address_id,
    a.early_scheduled_arrival,
    a.appointment_number,
    a.no_late_schedule_reason,
    a.not_yet_delivered,
    a.orig_deliver_no_later_than,
    a.orig_unload_dock_scheduled_at,
    a.arrival_tz,
    a.departure_tz,
    a.scheduled_arrival_tz,
    a.admit_date_tz,
    a.early_scheduled_arrival_tz,
    a.not_yet_delivered_tz,
    a.orig_deliver_no_later_than_tz,
    a.orig_unload_dock_sched_at_tz,
    a.contact_name,
    a.contact_phone,
    a.early_sdsa_flag,
    a.loaded_date,
    a.loaded_date_tz,
    a.version,
    a.arrival_window_start,
    a.arrival_window_end,
    a.contact_fax,
    a.contact_email,
    a.address_code,
    a.notes
   FROM flatbed.load_details a
  ORDER BY 2 DESC;


ALTER TABLE vw_ld_hist OWNER TO postgres;

--
-- TOC entry 905 (class 1259 OID 42107)
-- Name: vw_lmat_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_lmat_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.load_material_id,
    a.load_detail_id,
    a.release_id,
    a.work_order,
    a.shop_order,
    a.cust_owned,
    a.cust_po_num,
    a.part_num,
    a.cust_item_num,
    a.weight,
    a.material_type,
    a.pickup_instr,
    a.delivery_instr,
    a.bol,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.order_id,
    a.status,
    a.dropoff_load_detail_id,
    a.is_ready,
    a.heat_num,
    a.pickup_date,
    a.pieces,
    a.length,
    a.bundles,
    a.grade,
    a.cust_location,
    a.dock_name,
    a.node_id,
    a.in_inventory,
    a.pickup_date_tz,
    a.hazmat,
    a.package_type,
    a.width,
    a.height,
    a.part_description,
    a.commodity_class_code,
    a.gauge,
    a.material_status,
    a.mill_order_num,
    a.mill_test_num,
    a.nmfc,
    a.product_id,
    a.quantity,
    a.sub_product_id,
    a.thickness,
    a.hazmat_class,
    a.un_num,
    a.packing_group,
    a.emergency_number,
    a.version,
    a.original_weight,
    a.original_freight_class,
    a.contract,
    a.hazmat_instructions,
    a.emergency_contract,
    a.emergency_company,
    a.emergency_country_code,
    a.emergency_area_code,
    a.ltl_product_id,
    a.ltl_package_type,
    a.stackable
   FROM load_materials a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.load_material_id,
    a.load_detail_id,
    a.release_id,
    a.work_order,
    a.shop_order,
    a.cust_owned,
    a.cust_po_num,
    a.part_num,
    a.cust_item_num,
    a.weight,
    a.material_type,
    a.pickup_instr,
    a.delivery_instr,
    a.bol,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.order_id,
    a.status,
    a.dropoff_load_detail_id,
    a.is_ready,
    a.heat_num,
    a.pickup_date,
    a.pieces,
    a.length,
    a.bundles,
    a.grade,
    a.cust_location,
    a.dock_name,
    a.node_id,
    a.in_inventory,
    a.pickup_date_tz,
    a.hazmat,
    a.package_type,
    a.width,
    a.height,
    a.part_description,
    a.commodity_class_code,
    a.gauge,
    a.material_status,
    a.mill_order_num,
    a.mill_test_num,
    a.nmfc,
    a.product_id,
    a.quantity,
    a.sub_product_id,
    a.thickness,
    a.hazmat_class,
    a.un_num,
    a.packing_group,
    a.emergency_number,
    a.version,
    a.original_weight,
    a.original_freight_class,
    a.contract,
    a.hazmat_instructions,
    a.emergency_contract,
    a.emergency_company,
    a.emergency_country_code,
    a.emergency_area_code,
    a.ltl_product_id,
    a.ltl_package_type,
    a.stackable
   FROM flatbed.load_materials a
  ORDER BY 2 DESC;


ALTER TABLE vw_lmat_hist OWNER TO postgres;

--
-- TOC entry 906 (class 1259 OID 42112)
-- Name: vw_loads_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_loads_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.load_id,
    a.route_id,
    a.load_status,
    a.market_type,
    a.commodity_cd,
    a.container_cd,
    a.org_id,
    a.location_id,
    a.person_id,
    a.shipper_reference_number,
    a.broker_reference_number,
    a.inbound_outbound_flg,
    a.date_closed,
    a.feature_code,
    a.source_ind,
    a.mileage,
    a.pieces,
    a.weight,
    a.weight_uom_code,
    a.target_price,
    a.awarded_offer,
    a.award_price,
    a.award_date,
    a.special_instructions,
    a.special_message,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.min_weight,
    a.min_price,
    a.rate_type,
    a.unit_cost,
    a.tender_cycle_id,
    a.priority,
    a.pay_terms,
    a.bill_to,
    a.one_time_rate_id,
    a.out_route_miles,
    a.auto_cd_flg,
    a.product_type,
    a.finalization_status,
    a.permit_load,
    a.pro_num,
    a.travel_time,
    a.chk_brn,
    a.premium_available,
    a.scheduled,
    a.dispatcher_schedule_only,
    a.truck_weight,
    a.gross_weight,
    a.barge_num,
    a.sc_flag,
    a.op_flag,
    a.sw_flag,
    a.srr_flag,
    a.srm_flag,
    a.mm_flag,
    a.bol_instructions,
    a.template_set,
    a.release_num,
    a.lod_attribute1,
    a.lod_attribute2,
    a.lod_attribute3,
    a.lod_attribute4,
    a.lod_attribute5,
    a.lod_attribute6,
    a.original_sched_pickup_date,
    a.original_sched_delivery_date,
    a.carrier_reference_number,
    a.shipper_premium_available,
    a.after_hours_contact,
    a.after_hours_phone,
    a.is_ready,
    a.reason_code,
    a.fin_status_date,
    a.hfr_flag,
    a.delivery_success,
    a.freight_paid_by,
    a.rate_contact,
    a.routing_instructions,
    a.customer_tracking_email,
    a.notify_award_flag,
    a.notify_schedule_flag,
    a.notify_gate_flag,
    a.notify_conf_pick_up_flag,
    a.notify_conf_deliv_flag,
    a.driver_name,
    a.tractor_id,
    a.driver_license,
    a.trailer,
    a.unit_number,
    a.commodity_desc,
    a.customer_truck_flag,
    a.empty_weight,
    a.cust_truck_scac,
    a.cust_truck_carr_name,
    a.cust_truck_person_name,
    a.cust_truck_person_phone,
    a.notify_gate_flag_char,
    a.gl_date,
    a.hazmat_flag,
    a.radio_active_flag,
    a.customer_comments,
    a.over_height_flag,
    a.over_length_flag,
    a.over_width_flag,
    a.super_load_flag,
    a.height,
    a.length,
    a.width,
    a.over_weight_flag,
    a.notify_initial_msg_flag,
    a.multi_dock_sched_rqrd,
    a.permit_num,
    a.radio_active_secure_flag,
    a.booked,
    a.orig_sched_pickup_date_tz,
    a.orig_sched_delivery_date_tz,
    a.etd_ovr_flg,
    a.etd_date,
    a.etd_date_tz,
    a.awarded_by,
    a.origin_region_id,
    a.destination_region_id,
    a.target_rate_min,
    a.target_rate_max,
    a.target_rate_ovr_flg,
    a.target_rate_id_min,
    a.target_rate_id_max,
    a.target_tr_id_min,
    a.target_tr_id_max,
    a.mileage_type,
    a.mileage_version,
    a.ship_light,
    a.frt_bill_recv_date,
    a.frt_bill_recv_by,
    a.frt_bill_number,
    a.finan_no_load_flag,
    a.service_level_cd,
    a.award_carrier_org_id,
    a.ship_date,
    a.blind_carrier,
    a.gl_number,
    a.bol,
    a.po_num,
    a.op_bol,
    a.part_num,
    a.version,
    a.gl_ref_code,
    a.award_dedicated_unit_id,
    a.frt_bill_recv_flag,
    a.pickup_num,
    a.frt_bill_amount,
    a.originating_system,
    a.inv_ship_rates_only,
    a.inv_carr_rates_only,
    a.customs_broker,
    a.customs_broker_phone,
    a.cust_req_doc_recv_flag,
    a.so_number,
    a.frt_bill_pay_to_id,
    a.inv_approved,
    a.volume_quote_id,
    a.saved_quote_id
   FROM loads a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.load_id,
    a.route_id,
    a.load_status,
    a.market_type,
    a.commodity_cd,
    a.container_cd,
    a.org_id,
    a.location_id,
    a.person_id,
    a.shipper_reference_number,
    a.broker_reference_number,
    a.inbound_outbound_flg,
    a.date_closed,
    a.feature_code,
    a.source_ind,
    a.mileage,
    a.pieces,
    a.weight,
    a.weight_uom_code,
    a.target_price,
    a.awarded_offer,
    a.award_price,
    a.award_date,
    a.special_instructions,
    a.special_message,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.min_weight,
    a.min_price,
    a.rate_type,
    a.unit_cost,
    a.tender_cycle_id,
    a.priority,
    a.pay_terms,
    a.bill_to,
    a.one_time_rate_id,
    a.out_route_miles,
    a.auto_cd_flg,
    a.product_type,
    a.finalization_status,
    a.permit_load,
    a.pro_num,
    a.travel_time,
    a.chk_brn,
    a.premium_available,
    a.scheduled,
    a.dispatcher_schedule_only,
    a.truck_weight,
    a.gross_weight,
    a.barge_num,
    a.sc_flag,
    a.op_flag,
    a.sw_flag,
    a.srr_flag,
    a.srm_flag,
    a.mm_flag,
    a.bol_instructions,
    a.template_set,
    a.release_num,
    a.lod_attribute1,
    a.lod_attribute2,
    a.lod_attribute3,
    a.lod_attribute4,
    a.lod_attribute5,
    a.lod_attribute6,
    a.original_sched_pickup_date,
    a.original_sched_delivery_date,
    a.carrier_reference_number,
    a.shipper_premium_available,
    a.after_hours_contact,
    a.after_hours_phone,
    a.is_ready,
    a.reason_code,
    a.fin_status_date,
    a.hfr_flag,
    a.delivery_success,
    a.freight_paid_by,
    a.rate_contact,
    a.routing_instructions,
    a.customer_tracking_email,
    a.notify_award_flag,
    a.notify_schedule_flag,
    a.notify_gate_flag,
    a.notify_conf_pick_up_flag,
    a.notify_conf_deliv_flag,
    a.driver_name,
    a.tractor_id,
    a.driver_license,
    a.trailer,
    a.unit_number,
    a.commodity_desc,
    a.customer_truck_flag,
    a.empty_weight,
    a.cust_truck_scac,
    a.cust_truck_carr_name,
    a.cust_truck_person_name,
    a.cust_truck_person_phone,
    a.notify_gate_flag_char,
    a.gl_date,
    a.hazmat_flag,
    a.radio_active_flag,
    a.customer_comments,
    a.over_height_flag,
    a.over_length_flag,
    a.over_width_flag,
    a.super_load_flag,
    a.height,
    a.length,
    a.width,
    a.over_weight_flag,
    a.notify_initial_msg_flag,
    a.multi_dock_sched_rqrd,
    a.permit_num,
    a.radio_active_secure_flag,
    a.booked,
    a.orig_sched_pickup_date_tz,
    a.orig_sched_delivery_date_tz,
    a.etd_ovr_flg,
    a.etd_date,
    a.etd_date_tz,
    a.awarded_by,
    a.origin_region_id,
    a.destination_region_id,
    a.target_rate_min,
    a.target_rate_max,
    a.target_rate_ovr_flg,
    a.target_rate_id_min,
    a.target_rate_id_max,
    a.target_tr_id_min,
    a.target_tr_id_max,
    a.mileage_type,
    a.mileage_version,
    a.ship_light,
    a.frt_bill_recv_date,
    a.frt_bill_recv_by,
    a.frt_bill_number,
    a.finan_no_load_flag,
    a.service_level_cd,
    a.award_carrier_org_id,
    a.ship_date,
    a.blind_carrier,
    a.gl_number,
    a.bol,
    a.po_num,
    a.op_bol,
    a.part_num,
    a.version,
    a.gl_ref_code,
    a.award_dedicated_unit_id,
    a.frt_bill_recv_flag,
    a.pickup_num,
    a.frt_bill_amount,
    a.originating_system,
    a.inv_ship_rates_only,
    a.inv_carr_rates_only,
    a.customs_broker,
    a.customs_broker_phone,
    a.cust_req_doc_recv_flag,
    a.so_number,
    a.frt_bill_pay_to_id,
    a.inv_approved,
    a.volume_quote_id,
    a.saved_quote_id
   FROM flatbed.loads a
  ORDER BY 2 DESC;


ALTER TABLE vw_loads_hist OWNER TO postgres;

--
-- TOC entry 907 (class 1259 OID 42117)
-- Name: vw_ltl_product_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_ltl_product_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.ltl_product_id,
    a.ltl_product_tracking_id,
    a.org_id,
    a.location_id,
    a.product_code,
    a.description,
    a.nmfc_num,
    a.pieces,
    a.package_type,
    a.weight,
    a.commodity_class_code,
    a.hazmat_flag,
    a.hazmat_class,
    a.un_num,
    a.packing_group,
    a.emergency_number,
    a.created_by,
    a.date_created,
    a.version,
    a.modified_by,
    a.date_modified,
    a.status,
    a.nmfc_sub_num,
    a.hazmat_instructions,
    a.emergency_contract,
    a.emergency_company,
    a.emergency_country_code,
    a.emergency_area_code,
    a.person_id
   FROM ltl_product a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.ltl_product_id,
    a.ltl_product_tracking_id,
    a.org_id,
    a.location_id,
    a.product_code,
    a.description,
    a.nmfc_num,
    a.pieces,
    a.package_type,
    a.weight,
    a.commodity_class_code,
    a.hazmat_flag,
    a.hazmat_class,
    a.un_num,
    a.packing_group,
    a.emergency_number,
    a.created_by,
    a.date_created,
    a.version,
    a.modified_by,
    a.date_modified,
    a.status,
    a.nmfc_sub_num,
    a.hazmat_instructions,
    a.emergency_contract,
    a.emergency_company,
    a.emergency_country_code,
    a.emergency_area_code,
    a.person_id
   FROM flatbed.ltl_product a
  ORDER BY 2 DESC;


ALTER TABLE vw_ltl_product_hist OWNER TO postgres;

--
-- TOC entry 908 (class 1259 OID 42122)
-- Name: vw_ol_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_ol_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.location_id,
    a.org_id,
    a.location_name,
    a.status,
    a.contact_last_name,
    a.contact_first_name,
    a.contact_title,
    a.contact_email,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.bill_to,
    a.op_org_id,
    a.op_loc_id,
    a.visible,
    a.pricing_on_location,
    a.auto_auto_start_time,
    a.auto_auto_end_time,
    a.rate_contact_name,
    a.rate_email_address,
    a.rate_fax_area_code,
    a.rate_fax_number,
    a.rate_default_minimum,
    a.rate_default_type,
    a.rate_car_default_minimum,
    a.rate_car_default_type,
    a.credit_limit,
    a.sales_lead,
    a.commodity_cd,
    a.employer_num,
    a.bill_to_adjustment,
    a.bill_to_accessorial,
    a.address_id,
    a.version,
    a.must_pre_pay,
    a.is_default
   FROM organization_locations a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.location_id,
    a.org_id,
    a.location_name,
    a.status,
    a.contact_last_name,
    a.contact_first_name,
    a.contact_title,
    a.contact_email,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.bill_to,
    a.op_org_id,
    a.op_loc_id,
    a.visible,
    a.pricing_on_location,
    a.auto_auto_start_time,
    a.auto_auto_end_time,
    a.rate_contact_name,
    a.rate_email_address,
    a.rate_fax_area_code,
    a.rate_fax_number,
    a.rate_default_minimum,
    a.rate_default_type,
    a.rate_car_default_minimum,
    a.rate_car_default_type,
    a.credit_limit,
    a.sales_lead,
    a.commodity_cd,
    a.employer_num,
    a.bill_to_adjustment,
    a.bill_to_accessorial,
    a.address_id,
    a.version,
    a.must_pre_pay,
    a.is_default
   FROM flatbed.organization_locations a
  ORDER BY 2 DESC;


ALTER TABLE vw_ol_hist OWNER TO postgres;

--
-- TOC entry 909 (class 1259 OID 42127)
-- Name: vw_organizations_hist; Type: VIEW; Schema: hist; Owner: postgres
--

CREATE VIEW vw_organizations_hist AS
 SELECT a.moduser,
    a.modtime,
    a.modstatus,
    a.org_id,
    a.name,
    a.employer_num,
    a.org_type,
    a.status,
    a.contact_last_name,
    a.contact_first_name,
    a.contact_title,
    a.contact_email,
    a.mc_num,
    a.scac,
    a.org_id_parent,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.loc_rate_override,
    a.logo_path,
    a.css_path,
    a.status_reason,
    a.qualcomm_id,
    a.network_id,
    a.critical_email_address,
    a.tender_increment_time,
    a.open_market_time,
    a.max_num_carriers,
    a.not_exceed_shipper_amt,
    a.incl_carr_not_hauled_lane,
    a.auto_auto_start_time,
    a.auto_auto_end_time,
    a.rate_contact_name,
    a.rate_email_address,
    a.rate_fax_area_code,
    a.rate_fax_number,
    a.rate_default_minimum,
    a.rate_default_type,
    a.rate_auto_approve_days,
    a.not_exceed_type,
    a.max_carrier_rate,
    a.max_carr_rate_unit_type,
    a.rate_car_default_minimum,
    a.rate_car_default_type,
    a.address_id,
    a.credit_limit,
    a.sales_lead,
    a.ltl_account_type,
    a.ltl_client_code,
    a.region_id,
    a.accelerated_ind,
    a.version,
    a.ltl_rate_type,
    a.must_pre_pay,
    a.account_executive,
    a.eff_date,
    a.company_code,
    a.override_credit_hold,
    a.auto_credit_hold,
    a.warning_no_of_days,
    a.currency_code,
    a.edi_account,
    a.is_contract,
    a.exp_date,
    a.logo_id,
    a.from_vendor_bills,
    a.product_list_primary_sort,
    a.display_logo_on_bol
   FROM organizations a
UNION ALL
 SELECT NULL::character varying AS moduser,
    now() AS modtime,
    NULL::bpchar AS modstatus,
    a.org_id,
    a.name,
    a.employer_num,
    a.org_type,
    a.status,
    a.contact_last_name,
    a.contact_first_name,
    a.contact_title,
    a.contact_email,
    a.mc_num,
    a.scac,
    a.org_id_parent,
    a.date_created,
    a.created_by,
    a.date_modified,
    a.modified_by,
    a.loc_rate_override,
    a.logo_path,
    a.css_path,
    a.status_reason,
    a.qualcomm_id,
    a.network_id,
    a.critical_email_address,
    a.tender_increment_time,
    a.open_market_time,
    a.max_num_carriers,
    a.not_exceed_shipper_amt,
    a.incl_carr_not_hauled_lane,
    a.auto_auto_start_time,
    a.auto_auto_end_time,
    a.rate_contact_name,
    a.rate_email_address,
    a.rate_fax_area_code,
    a.rate_fax_number,
    a.rate_default_minimum,
    a.rate_default_type,
    a.rate_auto_approve_days,
    a.not_exceed_type,
    a.max_carrier_rate,
    a.max_carr_rate_unit_type,
    a.rate_car_default_minimum,
    a.rate_car_default_type,
    a.address_id,
    a.credit_limit,
    a.sales_lead,
    a.ltl_account_type,
    a.ltl_client_code,
    a.region_id,
    a.accelerated_ind,
    a.version,
    a.ltl_rate_type,
    a.must_pre_pay,
    a.account_executive,
    a.eff_date,
    a.company_code,
    a.override_credit_hold,
    a.auto_credit_hold,
    a.warning_no_of_days,
    a.currency_code,
    a.edi_account,
    a.is_contract,
    a.exp_date,
    a.logo_id,
    a.from_vendor_bills,
    a.product_list_primary_sort,
    a.display_logo_on_bol
   FROM flatbed.organizations a
  ORDER BY 2 DESC;


ALTER TABLE vw_organizations_hist OWNER TO postgres;

--
-- TOC entry 4590 (class 1259 OID 22735)
-- Name: addr_det_carr_inv_det_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX addr_det_carr_inv_det_fk_i2 ON carrier_invoice_addr_details USING btree (invoice_det_id);


--
-- TOC entry 4625 (class 1259 OID 22823)
-- Name: addresseshist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX addresseshist ON addresses USING btree (address_id);


--
-- TOC entry 4597 (class 1259 OID 22745)
-- Name: bill_to_default_id_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX bill_to_default_id_idx2 ON bill_to_default_values USING btree (bill_to_id);


--
-- TOC entry 4626 (class 1259 OID 22828)
-- Name: bill_to_field_type_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX bill_to_field_type_i2 ON bill_to_req_field USING btree ((
CASE
    WHEN ((status)::text = 'A'::text) THEN bill_to_id
    ELSE NULL::bigint
END), (
CASE
    WHEN ((status)::text = 'A'::text) THEN field_name
    ELSE NULL::character varying
END));


--
-- TOC entry 4627 (class 1259 OID 22827)
-- Name: bill_to_req_field_b_to_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX bill_to_req_field_b_to_fk_i2 ON bill_to_req_field USING btree (bill_to_id);


--
-- TOC entry 4585 (class 1259 OID 22709)
-- Name: bill_to_threshold_id_c_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX bill_to_threshold_id_c_idx2 ON bill_to_threshold_settings USING btree (created_by);


--
-- TOC entry 4586 (class 1259 OID 22710)
-- Name: bill_to_threshold_id_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX bill_to_threshold_id_idx2 ON bill_to_threshold_settings USING btree (bill_to_id);


--
-- TOC entry 4587 (class 1259 OID 22711)
-- Name: bill_to_threshold_id_m_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX bill_to_threshold_id_m_idx2 ON bill_to_threshold_settings USING btree (modified_by);


--
-- TOC entry 4630 (class 1259 OID 22847)
-- Name: bill_tohist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX bill_tohist ON bill_to USING btree (bill_to_id);


--
-- TOC entry 4623 (class 1259 OID 22815)
-- Name: binhist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX binhist ON billing_invoice_node USING btree (billing_node_id);


--
-- TOC entry 4624 (class 1259 OID 22816)
-- Name: binhist_bti; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX binhist_bti ON billing_invoice_node USING btree (bill_to_id);


--
-- TOC entry 4580 (class 1259 OID 22694)
-- Name: car_inv_details_loads_fk2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX car_inv_details_loads_fk2 ON carrier_invoice_details USING btree (matched_load_id);


--
-- TOC entry 4591 (class 1259 OID 22732)
-- Name: carr_inv_adr_cntry_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX carr_inv_adr_cntry_fk_i2 ON carrier_invoice_addr_details USING btree (country_code);


--
-- TOC entry 4592 (class 1259 OID 22733)
-- Name: carr_inv_adr_st_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX carr_inv_adr_st_fk_i2 ON carrier_invoice_addr_details USING btree (state, country_code);


--
-- TOC entry 4581 (class 1259 OID 22696)
-- Name: carr_inv_det_org_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX carr_inv_det_org_fk_i2 ON carrier_invoice_details USING btree (carrier_id);


--
-- TOC entry 4559 (class 1259 OID 22640)
-- Name: cdihist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX cdihist ON cost_detail_items USING btree (item_id);


--
-- TOC entry 4560 (class 1259 OID 22641)
-- Name: cdihist_cd; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX cdihist_cd ON cost_detail_items USING btree (cost_detail_id);


--
-- TOC entry 4614 (class 1259 OID 22784)
-- Name: cnt_pk2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX cnt_pk2 ON countries USING btree (country_code);


--
-- TOC entry 4594 (class 1259 OID 22739)
-- Name: edi_set_bill_to_id_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX edi_set_bill_to_id_idx2 ON edi_settings USING btree (bill_to_id);


--
-- TOC entry 4595 (class 1259 OID 22740)
-- Name: edi_set_created_by_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX edi_set_created_by_idx2 ON edi_settings USING btree (created_by);


--
-- TOC entry 4596 (class 1259 OID 22741)
-- Name: edi_set_modified_by_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX edi_set_modified_by_idx2 ON edi_settings USING btree (modified_by);


--
-- TOC entry 4604 (class 1259 OID 22772)
-- Name: faa_comp_idx12; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX faa_comp_idx12 ON finan_adj_acc_detail USING btree (load_id, faa_status);


--
-- TOC entry 4605 (class 1259 OID 22778)
-- Name: faa_comp_idx22; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX faa_comp_idx22 ON finan_adj_acc_detail USING btree (faa_status, load_id, status);


--
-- TOC entry 4606 (class 1259 OID 22779)
-- Name: faa_comp_idx32; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX faa_comp_idx32 ON finan_adj_acc_detail USING btree (load_id, status, adj_acc, sent_to_finance);


--
-- TOC entry 4607 (class 1259 OID 22776)
-- Name: faa_cust_inv_num_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX faa_cust_inv_num_idx2 ON finan_adj_acc_detail USING btree (customer_invoice_num);


--
-- TOC entry 4608 (class 1259 OID 22771)
-- Name: faa_date_created_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX faa_date_created_idx2 ON finan_adj_acc_detail USING btree (load_id, adj_acc, status, date_created);


--
-- TOC entry 4609 (class 1259 OID 22777)
-- Name: faa_detail_pk2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX faa_detail_pk2 ON finan_adj_acc_detail USING btree (faa_detail_id);


--
-- TOC entry 4610 (class 1259 OID 22774)
-- Name: faa_gl_date_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX faa_gl_date_idx2 ON finan_adj_acc_detail USING btree (gl_date);


--
-- TOC entry 4611 (class 1259 OID 22775)
-- Name: faa_load_id_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX faa_load_id_idx2 ON finan_adj_acc_detail USING btree (load_id);


--
-- TOC entry 4612 (class 1259 OID 22773)
-- Name: faa_status_date_idx; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX faa_status_date_idx ON finan_adj_acc_detail USING btree (status, date_modified);


--
-- TOC entry 4613 (class 1259 OID 22780)
-- Name: faa_status_idx2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX faa_status_idx2 ON finan_adj_acc_detail USING btree (faa_status);


--
-- TOC entry 4628 (class 1259 OID 22832)
-- Name: idx$$_048e00012; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX "idx$$_048e00012" ON timezone USING btree (local_offset);


--
-- TOC entry 4576 (class 1259 OID 22678)
-- Name: inv_cost_inv_det_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX inv_cost_inv_det_fk_i2 ON carrier_invoice_cost_items USING btree (invoice_det_id);


--
-- TOC entry 4561 (class 1259 OID 22646)
-- Name: invsethist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX invsethist ON invoice_settings USING btree (invoice_settings_id);


--
-- TOC entry 4562 (class 1259 OID 22645)
-- Name: invsethist_bti; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX invsethist_bti ON invoice_settings USING btree (bill_to_id);


--
-- TOC entry 4602 (class 1259 OID 22763)
-- Name: lcdhist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX lcdhist ON load_cost_details USING btree (cost_detail_id);


--
-- TOC entry 4603 (class 1259 OID 22762)
-- Name: lcdhistloadid; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX lcdhistloadid ON load_cost_details USING btree (load_id);


--
-- TOC entry 4621 (class 1259 OID 22808)
-- Name: ldhist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX ldhist ON load_details USING btree (load_detail_id);


--
-- TOC entry 4622 (class 1259 OID 22807)
-- Name: ldhist_load_id; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX ldhist_load_id ON load_details USING btree (load_id);


--
-- TOC entry 4572 (class 1259 OID 22668)
-- Name: lmathist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX lmathist ON load_materials USING btree (load_material_id);


--
-- TOC entry 4573 (class 1259 OID 22669)
-- Name: lmathist_ld_id; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX lmathist_ld_id ON load_materials USING btree (load_detail_id);


--
-- TOC entry 4629 (class 1259 OID 22839)
-- Name: loadshist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX loadshist ON loads USING btree (load_id);


--
-- TOC entry 4589 (class 1259 OID 22725)
-- Name: ltlprdhist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX ltlprdhist ON ltl_product USING btree (ltl_product_id);


--
-- TOC entry 4583 (class 1259 OID 22704)
-- Name: olhist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX olhist ON organization_locations USING btree (location_id);


--
-- TOC entry 4584 (class 1259 OID 22703)
-- Name: olhist_bt; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX olhist_bt ON organization_locations USING btree (bill_to);


--
-- TOC entry 4588 (class 1259 OID 22718)
-- Name: orghist; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX orghist ON organizations USING btree (org_id);


--
-- TOC entry 4565 (class 1259 OID 22661)
-- Name: orgp_loc_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX orgp_loc_fk_i2 ON organization_phones USING btree (location_id);


--
-- TOC entry 4566 (class 1259 OID 22658)
-- Name: orgp_org_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX orgp_org_fk_i2 ON organization_phones USING btree (org_id);


--
-- TOC entry 4567 (class 1259 OID 22659)
-- Name: orgp_pk2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX orgp_pk2 ON organization_phones USING btree (org_phone_id);


--
-- TOC entry 4568 (class 1259 OID 22657)
-- Name: orgp_pt_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX orgp_pt_fk_i2 ON organization_phones USING btree (phone_type);


--
-- TOC entry 4569 (class 1259 OID 22660)
-- Name: orgp_uk2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX orgp_uk2 ON organization_phones USING btree (org_id, phone_type, location_id);


--
-- TOC entry 4570 (class 1259 OID 22656)
-- Name: orgp_usr_created_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX orgp_usr_created_fk_i2 ON organization_phones USING btree (created_by);


--
-- TOC entry 4571 (class 1259 OID 22655)
-- Name: orgp_usr_mod_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX orgp_usr_mod_fk_i2 ON organization_phones USING btree (modified_by);


--
-- TOC entry 4631 (class 1259 OID 22852)
-- Name: phones_phone_type_fk_i2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX phones_phone_type_fk_i2 ON phones USING btree (phone_type);


--
-- TOC entry 4593 (class 1259 OID 22734)
-- Name: pk_carr_inv_addr_det2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pk_carr_inv_addr_det2 ON carrier_invoice_addr_details USING btree (addr_det_id);


--
-- TOC entry 4577 (class 1259 OID 22679)
-- Name: pk_carr_inv_cost_item2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pk_carr_inv_cost_item2 ON carrier_invoice_cost_items USING btree (invoice_cost_detail_item_id);


--
-- TOC entry 4582 (class 1259 OID 22695)
-- Name: pk_carr_inv_det2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pk_carr_inv_det2 ON carrier_invoice_details USING btree (invoice_det_id);


--
-- TOC entry 4632 (class 1259 OID 22851)
-- Name: pk_phones2; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pk_phones2 ON phones USING btree (phone_id);


--
-- TOC entry 4563 (class 1259 OID 22651)
-- Name: pls_credit_limit_acc; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pls_credit_limit_acc ON pls_credit_limit USING btree (accountnum);


--
-- TOC entry 4564 (class 1259 OID 22650)
-- Name: pls_credit_limit_bti; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pls_credit_limit_bti ON pls_credit_limit USING btree (bill_to_id);


--
-- TOC entry 4598 (class 1259 OID 22749)
-- Name: pls_credit_limit_mv_acc; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pls_credit_limit_mv_acc ON pls_credit_limit_mview USING btree (btrim(upper((accountnum)::text)));


--
-- TOC entry 4599 (class 1259 OID 22750)
-- Name: pls_credit_limit_mview_bti; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pls_credit_limit_mview_bti ON pls_credit_limit_mview USING btree (bill_to_id);


--
-- TOC entry 4574 (class 1259 OID 22674)
-- Name: pls_open_balance_acc; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pls_open_balance_acc ON pls_open_balance USING btree (accountnum);


--
-- TOC entry 4575 (class 1259 OID 22673)
-- Name: pls_open_balance_bti; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pls_open_balance_bti ON pls_open_balance USING btree (bill_to_id);


--
-- TOC entry 4579 (class 1259 OID 22687)
-- Name: pls_open_balance_mview_bti; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pls_open_balance_mview_bti ON pls_open_balance_mview USING btree (bill_to_id);


--
-- TOC entry 4600 (class 1259 OID 22755)
-- Name: pls_unbilled_rev_bti; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pls_unbilled_rev_bti ON pls_unbilled_rev_t USING btree (bill_to_id);


--
-- TOC entry 4601 (class 1259 OID 22754)
-- Name: pls_unbilled_rev_cn; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX pls_unbilled_rev_cn ON pls_unbilled_rev_t USING btree (customer_number);


--
-- TOC entry 4578 (class 1259 OID 22683)
-- Name: term_id_pk1; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX term_id_pk1 ON pls_customer_terms USING btree (term_id);


--
-- TOC entry 4615 (class 1259 OID 22795)
-- Name: users_date_created_i; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX users_date_created_i ON users USING btree (date_created, person_id);


--
-- TOC entry 4616 (class 1259 OID 22798)
-- Name: usr_first_name_i; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX usr_first_name_i ON users USING btree (first_name);


--
-- TOC entry 4617 (class 1259 OID 22799)
-- Name: usr_last_name_i; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX usr_last_name_i ON users USING btree (last_name);


--
-- TOC entry 4618 (class 1259 OID 22797)
-- Name: usr_org_fk_i; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX usr_org_fk_i ON users USING btree (org_id);


--
-- TOC entry 4619 (class 1259 OID 22800)
-- Name: usr_usr_create_fk_i; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX usr_usr_create_fk_i ON users USING btree (created_by);


--
-- TOC entry 4620 (class 1259 OID 22796)
-- Name: usr_usr_mod_fk_i; Type: INDEX; Schema: hist; Owner: postgres
--

CREATE INDEX usr_usr_mod_fk_i ON users USING btree (modified_by);


-- Completed on 2017-10-23 18:25:11 MSK

--
-- PostgreSQL database dump complete
--

SET search_path = public;

commit;