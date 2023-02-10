--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.1
-- Dumped by pg_dump version 9.6.4

-- Started on 2017-10-26 05:50:57 EDT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 17417)
-- Name: flatbed; Type: SCHEMA; Schema: -; Owner: cloudsqlsuperuser
--

CREATE SCHEMA flatbed AUTHORIZATION flatbed;


--ALTER SCHEMA flatbed OWNER TO cloudsqlsuperuser;

SET search_path = flatbed, pg_catalog;

--
-- TOC entry 937 (class 1259 OID 43500)
-- Name: address_notifications; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE address_notifications (
    address_notification_id numeric(19,0) NOT NULL,
    user_address_book_id bigint NOT NULL,
    notification_type character varying(32) NOT NULL,
    email_address character varying(255) NOT NULL,
    direction character varying(1) NOT NULL
);


ALTER TABLE address_notifications OWNER TO postgres;

--
-- TOC entry 454 (class 1259 OID 22862)
-- Name: address_notifications_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE address_notifications_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE address_notifications_seq OWNER TO postgres;

--
-- TOC entry 419 (class 1259 OID 20036)
-- Name: addresses; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE addresses (
    address_id bigint NOT NULL,
    latitude numeric,
    longitude numeric,
    address1 character varying(200),
    address2 character varying(200),
    city character varying(30) NOT NULL,
    postal_code character varying(10),
    state_code character varying(6),
    country_code character varying(3) DEFAULT 'USA'::character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    preferred_city character varying(30),
    status character(1),
    address3 character varying(200),
    address4 character varying(200),
    created_by bigint DEFAULT 0 NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint DEFAULT 0 NOT NULL,
    version integer DEFAULT 1 NOT NULL
);


ALTER TABLE addresses OWNER TO postgres;

--
-- TOC entry 453 (class 1259 OID 22860)
-- Name: addresses_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE addresses_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE addresses_seq OWNER TO postgres;

--
-- TOC entry 456 (class 1259 OID 22866)
-- Name: ais_a_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ais_a_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ais_a_seq OWNER TO postgres;

--
-- TOC entry 457 (class 1259 OID 22868)
-- Name: ais_dtl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ais_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ais_dtl_seq OWNER TO postgres;

--
-- TOC entry 458 (class 1259 OID 22870)
-- Name: ais_s_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ais_s_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ais_s_seq OWNER TO postgres;

--
-- TOC entry 459 (class 1259 OID 22872)
-- Name: ais_z_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ais_z_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ais_z_seq OWNER TO postgres;

--
-- TOC entry 460 (class 1259 OID 22874)
-- Name: an_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE an_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE an_seq OWNER TO postgres;

--
-- TOC entry 465 (class 1259 OID 22884)
-- Name: ap_inb_exception_log_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ap_inb_exception_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ap_inb_exception_log_seq OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 17967)
-- Name: ap_terms; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ap_terms (
    term_id bigint NOT NULL,
    sequence_num bigint NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(240),
    start_date_active timestamp without time zone,
    enabled_flag character varying(1) NOT NULL,
    discount_percent numeric(10,2),
    discount_days bigint,
    due_days bigint,
    due_percent bigint,
    last_update_date timestamp without time zone
);


ALTER TABLE ap_terms OWNER TO postgres;

--
-- TOC entry 461 (class 1259 OID 22876)
-- Name: api_exception_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE api_exception_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE api_exception_seq OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 17907)
-- Name: api_exceptions; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE api_exceptions (
    api_exception_id bigint NOT NULL,
    api_type_id bigint NOT NULL,
    load_id bigint NOT NULL,
    bol character varying(20),
    carrier_reference_number character varying(30),
    field character varying(50) NOT NULL,
    old_value character varying(50),
    new_value character varying(50),
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint DEFAULT 0 NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE api_exceptions OWNER TO postgres;

--
-- TOC entry 6226 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN api_exceptions.carrier_reference_number; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_exceptions.carrier_reference_number IS 'This is the pronumber for the load.';


--
-- TOC entry 6227 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN api_exceptions.field; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_exceptions.field IS 'Field from the LOADS table for comparison.';


--
-- TOC entry 6228 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN api_exceptions.old_value; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_exceptions.old_value IS 'Value of the field from LOADS table before update.';


--
-- TOC entry 6229 (class 0 OID 0)
-- Dependencies: 214
-- Name: COLUMN api_exceptions.new_value; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_exceptions.new_value IS 'New value of the field from API.';


--
-- TOC entry 215 (class 1259 OID 17916)
-- Name: api_log; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE api_log (
    api_log_id bigint NOT NULL,
    api_type_id bigint NOT NULL,
    load_id bigint NOT NULL,
    bol character varying(20),
    carrier_reference_number character varying(30),
    shipper_reference_number character varying(30),
    request text,
    response text,
    error_message character varying(1000),
    status character varying(15) NOT NULL,
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint DEFAULT 0 NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    error text,
    tracking_status character varying(240)
);


ALTER TABLE api_log OWNER TO postgres;

--
-- TOC entry 6230 (class 0 OID 0)
-- Dependencies: 215
-- Name: TABLE api_log; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON TABLE api_log IS 'EVery request sent to the API and response received (except for document requests) are logged to this table.';


--
-- TOC entry 6231 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN api_log.carrier_reference_number; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_log.carrier_reference_number IS 'This is the pronumber for the load.';


--
-- TOC entry 6232 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN api_log.request; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_log.request IS 'Request data sent to the API.';


--
-- TOC entry 6233 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN api_log.response; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_log.response IS 'Response received from the API. Response is not logged for Document requests.';


--
-- TOC entry 6234 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN api_log.error; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_log.error IS 'Error trace if occurred while creating the request, calling API or parsing the response.';


--
-- TOC entry 6235 (class 0 OID 0)
-- Dependencies: 215
-- Name: COLUMN api_log.tracking_status; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_log.tracking_status IS 'To check if same status is returned again for tracking API.';


--
-- TOC entry 462 (class 1259 OID 22878)
-- Name: api_log_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE api_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE api_log_seq OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 17928)
-- Name: api_lookup_table; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE api_lookup_table (
    org_id bigint NOT NULL,
    lookup_group character varying(30) NOT NULL,
    pls_value character varying(50) NOT NULL,
    api_value character varying(50) NOT NULL
);


ALTER TABLE api_lookup_table OWNER TO postgres;

--
-- TOC entry 6236 (class 0 OID 0)
-- Dependencies: 216
-- Name: TABLE api_lookup_table; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON TABLE api_lookup_table IS 'Look up table to map pls and external api fields.';


--
-- TOC entry 217 (class 1259 OID 17931)
-- Name: api_metadata; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE api_metadata (
    api_metadata_id bigint NOT NULL,
    api_type_id bigint NOT NULL,
    pls_field_name character varying(100),
    pls_field_type character varying(10),
    pls_field_parent character varying(100),
    pls_field_description character varying(50),
    api_field_format character varying(250),
    api_field_name character varying(50),
    parent bigint,
    api_field_description character varying(50),
    default_value character varying(50),
    data_type character varying(20),
    multiple character varying(1) DEFAULT 'N'::character varying,
    lookup character varying(30),
    metadata_type character varying(10) NOT NULL,
    field_type character varying(10),
    metadata_order integer NOT NULL,
    namespace character varying(256),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    ns_element character varying(15),
    start_index smallint,
    maxlength smallint,
    CONSTRAINT check_atleast_one_not_null CHECK (((pls_field_name IS NOT NULL) OR (api_field_name IS NOT NULL))),
    CONSTRAINT check_data_types CHECK (((data_type)::text = ANY ((ARRAY['STATIC_VALUE'::character varying, 'WELL_FORMED_XML'::character varying, 'URL'::character varying, 'URI_PARAM'::character varying, 'LITERAL'::character varying, 'LOOKUP'::character varying, 'QUERY_PARAM'::character varying])::text[]))),
    CONSTRAINT check_field_types CHECK (((field_type)::text = ANY ((ARRAY['HEADER'::character varying, 'BODY'::character varying, 'ATTRIBUTE'::character varying])::text[]))),
    CONSTRAINT check_metadata_types CHECK (((metadata_type)::text = ANY ((ARRAY['REQUEST'::character varying, 'RESPONSE'::character varying])::text[]))),
    CONSTRAINT check_multiple CHECK (((multiple)::text = ANY ((ARRAY['Y'::character varying, 'N'::character varying])::text[]))),
    CONSTRAINT check_pls_field_types CHECK (((pls_field_type)::text = ANY ((ARRAY['LONG'::character varying, 'DOUBLE'::character varying, 'STRING'::character varying, 'DATE'::character varying, 'INTEGER'::character varying])::text[])))
);


ALTER TABLE api_metadata OWNER TO postgres;

--
-- TOC entry 6237 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN api_metadata.pls_field_name; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_metadata.pls_field_name IS 'Name of the PLS field name mapped to the API field.';


--
-- TOC entry 6238 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN api_metadata.pls_field_type; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_metadata.pls_field_type IS 'PLS field type like Long, Double. Null value is assumed as String.';


--
-- TOC entry 6239 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN api_metadata.api_field_format; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_metadata.api_field_format IS 'Format of the value the API field holds.';


--
-- TOC entry 6240 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN api_metadata.multiple; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_metadata.multiple IS 'Y/N. Y means that this set of metadata should be repeatedly processed.';


--
-- TOC entry 6241 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN api_metadata.lookup; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_metadata.lookup IS 'Data Look up from API_LOOKUP_TABLE if not null.';


--
-- TOC entry 6242 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN api_metadata.metadata_type; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_metadata.metadata_type IS 'Defines whether this record is for request or response.';


--
-- TOC entry 6243 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN api_metadata.field_type; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_metadata.field_type IS 'Defines whether this record is for defined the Header or Body content for SOAP/REST requests.';


--
-- TOC entry 6244 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN api_metadata.metadata_order; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_metadata.metadata_order IS 'Order in which metadata should be parsed.';


--
-- TOC entry 6245 (class 0 OID 0)
-- Dependencies: 217
-- Name: COLUMN api_metadata.namespace; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_metadata.namespace IS 'Namespace for the SOAP request elements.';


--
-- TOC entry 463 (class 1259 OID 22880)
-- Name: api_metadata_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE api_metadata_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE api_metadata_seq OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 17947)
-- Name: api_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE api_types (
    api_type_id bigint NOT NULL,
    api_type character varying(10) NOT NULL,
    api_description character varying(250),
    api_category character varying(10) NOT NULL,
    url character varying(1000) NOT NULL,
    ws_type character varying(5) NOT NULL,
    http_method character varying(5),
    auth_policy character varying(10),
    response_type character varying(5) DEFAULT 'XML'::character varying,
    soap_action character varying(256),
    status character varying(1) DEFAULT 'A'::character varying NOT NULL,
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint DEFAULT 0 NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    username character varying(255),
    password character varying(255),
    carrier_org_id bigint,
    shipper_org_id bigint,
    api_org_type character varying(10),
    CONSTRAINT check_auth_policy_values CHECK (((auth_policy)::text = ANY ((ARRAY['BASIC'::character varying, 'DIGEST'::character varying, 'KERBEROS'::character varying, 'SPNEGO'::character varying])::text[]))),
    CONSTRAINT check_http_method_values CHECK (((http_method)::text = ANY ((ARRAY['GET'::character varying, 'POST'::character varying])::text[]))),
    CONSTRAINT check_response_type_values CHECK (((response_type)::text = ANY ((ARRAY['XML'::character varying, 'BYTES'::character varying])::text[]))),
    CONSTRAINT check_status CHECK (((status)::text = ANY ((ARRAY['A'::character varying, 'I'::character varying])::text[]))),
    CONSTRAINT check_ws_type_values CHECK (((ws_type)::text = ANY ((ARRAY['SOAP'::character varying, 'REST'::character varying])::text[])))
);


ALTER TABLE api_types OWNER TO postgres;

--
-- TOC entry 6246 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN api_types.api_category; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_types.api_category IS 'Defines different category like tracking, document, etc.';


--
-- TOC entry 6247 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN api_types.url; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_types.url IS 'Carrier URL where request is sent.';


--
-- TOC entry 6248 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN api_types.ws_type; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_types.ws_type IS 'Type of webservice like soap and rest.';


--
-- TOC entry 6249 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN api_types.http_method; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_types.http_method IS 'The Http method for the rest service like Get, Post.';


--
-- TOC entry 6250 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN api_types.auth_policy; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_types.auth_policy IS 'Authorization polices supported by PLS PRO application.';


--
-- TOC entry 6251 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN api_types.response_type; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_types.response_type IS 'Response type like xml, bytes mainly for rest apis.';


--
-- TOC entry 6252 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN api_types.soap_action; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_types.soap_action IS 'Carriers having different soap action than URL.';


--
-- TOC entry 6253 (class 0 OID 0)
-- Dependencies: 218
-- Name: COLUMN api_types.status; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN api_types.status IS 'A - active, I - inactive';


--
-- TOC entry 464 (class 1259 OID 22882)
-- Name: api_types_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE api_types_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE api_types_seq OWNER TO postgres;

--
-- TOC entry 466 (class 1259 OID 22886)
-- Name: ar_inb_exception_log_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ar_inb_exception_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ar_inb_exception_log_seq OWNER TO postgres;

--
-- TOC entry 467 (class 1259 OID 22888)
-- Name: audit_ceq_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE audit_ceq_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE audit_ceq_seq OWNER TO postgres;

--
-- TOC entry 468 (class 1259 OID 22890)
-- Name: audit_cins_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE audit_cins_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE audit_cins_seq OWNER TO postgres;

--
-- TOC entry 470 (class 1259 OID 22894)
-- Name: audit_load_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE audit_load_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE audit_load_details_seq OWNER TO postgres;

--
-- TOC entry 471 (class 1259 OID 22896)
-- Name: audit_load_materials_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE audit_load_materials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE audit_load_materials_seq OWNER TO postgres;

--
-- TOC entry 858 (class 1259 OID 24841)
-- Name: audit_loads; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE audit_loads (
    load_id bigint NOT NULL,
    route_id bigint NOT NULL,
    load_status character varying(2) NOT NULL,
    market_type character varying(2),
    commodity_cd character varying(10) NOT NULL,
    container_cd character varying(10) NOT NULL,
    org_id bigint NOT NULL,
    location_id bigint NOT NULL,
    person_id bigint NOT NULL,
    shipper_reference_number character varying(30),
    broker_reference_number character varying(20),
    inbound_outbound_flg character varying(1) NOT NULL,
    date_closed timestamp(0) without time zone,
    feature_code character varying(1),
    source_ind character varying(5) NOT NULL,
    mileage numeric(8,3),
    pieces integer NOT NULL,
    weight bigint NOT NULL,
    weight_uom_code character varying(10),
    target_price numeric(10,2),
    awarded_offer bigint,
    award_price numeric(10,2),
    award_date timestamp(0) without time zone,
    special_instructions character varying(3000),
    special_message character varying(3000),
    date_created timestamp(0) without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp(0) without time zone,
    modified_by bigint,
    min_weight bigint,
    min_price numeric(13,2),
    rate_type character varying(2),
    unit_cost numeric(13,2),
    tender_cycle_id bigint,
    priority character varying(1),
    pay_terms character varying(3) NOT NULL,
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
    premium_available character(1) NOT NULL,
    scheduled character(1) NOT NULL,
    dispatcher_schedule_only character(1) NOT NULL,
    truck_weight bigint,
    gross_weight bigint,
    barge_num character varying(6),
    sc_flag character(1),
    op_flag character(1) NOT NULL,
    sw_flag character(1) NOT NULL,
    srr_flag character(1) NOT NULL,
    srm_flag character(1) NOT NULL,
    mm_flag character(1) NOT NULL,
    bol_instructions character varying(2000),
    template_set character varying(5),
    release_num character varying(30),
    lod_attribute1 character varying(30),
    lod_attribute2 character varying(30),
    lod_attribute3 character varying(30),
    lod_attribute4 character varying(30),
    lod_attribute5 character varying(50),
    lod_attribute6 character varying(30),
    original_sched_pickup_date timestamp(0) without time zone,
    original_sched_delivery_date timestamp(0) without time zone,
    carrier_reference_number character varying(30),
    shipper_premium_available character(1) NOT NULL,
    after_hours_contact character varying(50),
    after_hours_phone character varying(50),
    is_ready character(1),
    reason_code character varying(25),
    fin_status_date timestamp(0) without time zone,
    hfr_flag character(1) NOT NULL,
    audit_date timestamp(0) without time zone,
    audit_os_user character varying(250),
    audit_host character varying(64),
    audit_id numeric(10,2),
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
    customer_tracking_email character varying(2000),
    rate_contact character varying(100),
    freight_paid_by character varying(100),
    delivery_success character(1),
    notify_gate_flag_char character(1),
    gl_date timestamp(0) without time zone,
    hazmat_flag character(1),
    radio_active_flag character(1),
    customer_comments character varying(3000),
    over_height_flag character(1),
    over_length_flag character(1),
    over_width_flag character(1),
    super_load_flag character(1),
    height numeric(10,2),
    length numeric(10,2),
    width numeric(10,2),
    over_weight_flag character(1),
    notify_initial_msg_flag character(1),
    multi_dock_sched_rqrd character(1),
    permit_num character varying(30),
    radio_active_secure_flag character(1),
    booked character(1),
    orig_sched_pickup_date_tz numeric(4,1),
    orig_sched_delivery_date_tz numeric(4,1),
    etd_ovr_flg character(1),
    etd_date timestamp(0) without time zone,
    etd_date_tz numeric(4,1),
    awarded_by bigint,
    origin_region_id bigint,
    destination_region_id bigint,
    target_rate_min numeric(10,2),
    target_rate_max numeric(10,2),
    target_rate_ovr_flg character(1),
    target_rate_id_min bigint,
    target_rate_id_max bigint,
    target_tr_id_min bigint,
    target_tr_id_max bigint,
    mileage_type character varying(2),
    mileage_version character varying(20),
    ship_light character(1),
    frt_bill_recv_date timestamp(0) without time zone,
    frt_bill_recv_by bigint,
    frt_bill_number character varying(50),
    finan_no_load_flag character(1),
    gl_number character varying(50),
    bol character varying(25),
    po_num character varying(50),
    op_bol character varying(20),
    part_num character varying(2000),
    gl_ref_code character varying(50),
    award_dedicated_unit_id bigint
);


ALTER TABLE audit_loads OWNER TO postgres; 

--
-- TOC entry 469 (class 1259 OID 22892)
-- Name: audit_loads_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE audit_loads_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE audit_loads_seq OWNER TO postgres;

--
-- TOC entry 472 (class 1259 OID 22898)
-- Name: audit_map_adr_node_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE audit_map_adr_node_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE audit_map_adr_node_seq OWNER TO postgres;

--
-- TOC entry 473 (class 1259 OID 22900)
-- Name: audit_notes_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE audit_notes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE audit_notes_seq OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 17972)
-- Name: audit_shipment_cost_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE audit_shipment_cost_details (
    add_detail_id bigint NOT NULL,
    cost_detail_id bigint,
    update_revenue character varying(50),
    update_revenue_value numeric(10,2),
    dispute_cost character varying(2),
    request_paperwork character varying(1),
    created_by bigint,
    date_created timestamp without time zone,
    modified_by bigint,
    date_modified timestamp without time zone
);


ALTER TABLE audit_shipment_cost_details OWNER TO postgres;

--
-- TOC entry 474 (class 1259 OID 22902)
-- Name: batch_job_execution_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE batch_job_execution_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE;


ALTER TABLE batch_job_execution_seq OWNER TO postgres;

--
-- TOC entry 475 (class 1259 OID 22904)
-- Name: batch_job_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE batch_job_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE;


ALTER TABLE batch_job_seq OWNER TO postgres;

--
-- TOC entry 476 (class 1259 OID 22906)
-- Name: batch_step_execution_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE batch_step_execution_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE;


ALTER TABLE batch_step_execution_seq OWNER TO postgres;

--
-- TOC entry 477 (class 1259 OID 22908)
-- Name: bie_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE bie_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE bie_seq OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 18005)
-- Name: bill_to; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE bill_to (
    bill_to_id bigint NOT NULL,
    name character varying(50) NOT NULL,
    org_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
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
    currency_code character varying(15) DEFAULT 'USD'::character varying,
    pay_terms character varying(50),
    status character varying(1) DEFAULT 'A'::character varying NOT NULL,
    is_default character(1) DEFAULT 'N'::bpchar,
    pay_terms_id bigint,
    required_audit smallint DEFAULT 0,
    audit_instructions character varying(4000),
    finan_open_balance numeric(10,2),
    email_account_executive smallint DEFAULT 0,
    credit_card_email character varying(225),
    SEND_INVOICES_REPORTS character(1)
);


ALTER TABLE bill_to OWNER TO postgres;

--
-- TOC entry 6254 (class 0 OID 0)
-- Dependencies: 223
-- Name: COLUMN bill_to.required_audit; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN bill_to.required_audit IS '1- required special audit for bill_to; 0-no special audit for bill_to required';


--
-- TOC entry 6255 (class 0 OID 0)
-- Dependencies: 223
-- Name: COLUMN bill_to.audit_instructions; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN bill_to.audit_instructions IS 'auditing instructions';


--
-- TOC entry 481 (class 1259 OID 22916)
-- Name: bill_to_audit_threshold_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE bill_to_audit_threshold_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE bill_to_audit_threshold_seq OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 18023)
-- Name: bill_to_default_values; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE bill_to_default_values (
    bill_to_default_value_id integer NOT NULL,
    bill_to_id integer NOT NULL,
    inbound_outbound character varying(20) NOT NULL,
    edi_inbound_outbound character varying(20) NOT NULL,
    pay_terms character varying(20) NOT NULL,
    edi_pay_terms character varying(20) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer NOT NULL,
    manual_bol_inbound_outbound character varying(20),
    manual_bol_pay_terms character varying(20),
    edi_customs_broker character varying(50),
    edi_broker_phone_id bigint
);


ALTER TABLE bill_to_default_values OWNER TO postgres;

--
-- TOC entry 482 (class 1259 OID 22918)
-- Name: bill_to_default_values_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE bill_to_default_values_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE bill_to_default_values_seq OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 18028)
-- Name: bill_to_req_doc; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE bill_to_req_doc (
    bill_to_req_doc_id bigint NOT NULL,
    bill_to_id bigint NOT NULL,
    document_type character varying(20) NOT NULL,
    status character varying(1) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer NOT NULL,
    shipper_req_type character varying(20),
    image_document_type_id bigint
);


ALTER TABLE bill_to_req_doc OWNER TO postgres;

--
-- TOC entry 483 (class 1259 OID 22920)
-- Name: bill_to_req_doc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE bill_to_req_doc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE bill_to_req_doc_seq OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 18036)
-- Name: bill_to_req_field; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE bill_to_req_field (
    bill_to_req_field_id integer NOT NULL,
    bill_to_id bigint NOT NULL,
    field_name character varying(20) NOT NULL,
    status character varying(1) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer NOT NULL,
    action character varying(10),
    rule_exp character varying(100),
    direction character(1),
    required character(1) DEFAULT 'N'::bpchar,
    default_value character varying(100),
    zip character varying(255),
    city character varying(255),
    state character varying(255),
    country character varying(255),
    address_direction character(1),
    start_with character varying(255),
    end_with character varying(255)
);


ALTER TABLE bill_to_req_field OWNER TO postgres;

--
-- TOC entry 484 (class 1259 OID 22922)
-- Name: bill_to_req_field_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE bill_to_req_field_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE bill_to_req_field_seq OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 18046)
-- Name: bill_to_threshold_settings; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE bill_to_threshold_settings (
    bill_to_audit_threshold_id bigint NOT NULL,
    bill_to_id bigint,
    threshold_value numeric(10,2) DEFAULT 1.99,
    total_revenue numeric(10,2),
    margin numeric(10,2),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer NOT NULL
);


ALTER TABLE bill_to_threshold_settings OWNER TO postgres;

--
-- TOC entry 478 (class 1259 OID 22910)
-- Name: billing_inquiry_action_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE billing_inquiry_action_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE billing_inquiry_action_seq OWNER TO postgres;

--
-- TOC entry 479 (class 1259 OID 22912)
-- Name: billing_inquiry_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE billing_inquiry_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE billing_inquiry_seq OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 17978)
-- Name: billing_invoice_node; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE billing_invoice_node (
    billing_node_id bigint NOT NULL,
    bill_to_id bigint NOT NULL,
    network_id bigint NOT NULL,
    customer_id character varying(10) NOT NULL,
    customer_number character varying(10) NOT NULL,
    address_id bigint NOT NULL,
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
-- TOC entry 480 (class 1259 OID 22914)
-- Name: billing_status_jobs_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE billing_status_jobs_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE billing_status_jobs_seq OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 17999)
-- Name: billing_status_reason_codes; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE billing_status_reason_codes (
    status_reason character varying(4) NOT NULL,
    description character varying(250) NOT NULL,
    visible character(1) DEFAULT 'N'::bpchar NOT NULL
);


ALTER TABLE billing_status_reason_codes OWNER TO postgres;

--
-- TOC entry 485 (class 1259 OID 22924)
-- Name: blk_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE blk_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE blk_seq OWNER TO postgres;

--
-- TOC entry 486 (class 1259 OID 22926)
-- Name: bol_metadata_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE bol_metadata_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE bol_metadata_seq OWNER TO postgres;

--
-- TOC entry 487 (class 1259 OID 22928)
-- Name: branch_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE branch_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE branch_seq OWNER TO postgres;

--
-- TOC entry 488 (class 1259 OID 22930)
-- Name: branch_user_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE branch_user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE branch_user_seq OWNER TO postgres;

--
-- TOC entry 489 (class 1259 OID 22932)
-- Name: broad_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE broad_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE broad_seq OWNER TO postgres;

--
-- TOC entry 490 (class 1259 OID 22934)
-- Name: bto_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE bto_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE bto_seq OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 18097)
-- Name: cap_category_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE cap_category_types (
    category character varying(20) NOT NULL,
    description character varying(50) NOT NULL
);


ALTER TABLE cap_category_types OWNER TO postgres;

--
-- TOC entry 491 (class 1259 OID 22936)
-- Name: cap_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cap_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cap_seq OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 18076)
-- Name: capabilities; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE capabilities (
    capability_id bigint NOT NULL,
    name character varying(100),
    description character varying(1000) NOT NULL,
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    admin_restricted character(1) DEFAULT 'N'::bpchar NOT NULL,
    created_by bigint DEFAULT 0 NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint DEFAULT 0 NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    enabled_for_all character(1) DEFAULT 'N'::bpchar NOT NULL,
    category character varying(20),
    notes character varying(1000),
    sys_20 character(1) DEFAULT 'N'::bpchar NOT NULL,
    CONSTRAINT cap_sys_20_values CHECK ((sys_20 = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE capabilities OWNER TO postgres;

--
-- TOC entry 6264 (class 0 OID 0)
-- Dependencies: 230
-- Name: COLUMN capabilities.capability_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN capabilities.capability_id IS 'Sequence-generated PK.';


--
-- TOC entry 6265 (class 0 OID 0)
-- Dependencies: 230
-- Name: COLUMN capabilities.name; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN capabilities.name IS 'Programmatic ID -- search, param, context identifer';


--
-- TOC entry 6266 (class 0 OID 0)
-- Dependencies: 230
-- Name: COLUMN capabilities.description; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN capabilities.description IS 'Narrative description of capability';


--
-- TOC entry 6267 (class 0 OID 0)
-- Dependencies: 230
-- Name: COLUMN capabilities.enabled_for_all; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN capabilities.enabled_for_all IS 'If Y, the capability is given to all users.';


--
-- TOC entry 6268 (class 0 OID 0)
-- Dependencies: 230
-- Name: COLUMN capabilities.notes; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN capabilities.notes IS 'Additional information about the capability.';


--
-- TOC entry 6269 (class 0 OID 0)
-- Dependencies: 230
-- Name: COLUMN capabilities.sys_20; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN capabilities.sys_20 IS 'Flag accepting "Y" or "N" values. This flag is used to differentiate between 1.0 and 2.0 capabilities.';


--
-- TOC entry 240 (class 1259 OID 18228)
-- Name: carr_edi_cost_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE carr_edi_cost_types (
    carr_edi_cost_type_id bigint NOT NULL,
    carr_org_id bigint NOT NULL,
    carr_cost_ref_type character varying(10) NOT NULL,
    description character varying(1024),
    ref_type character varying(10) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint NOT NULL
);


ALTER TABLE carr_edi_cost_types OWNER TO postgres;

--
-- TOC entry 498 (class 1259 OID 22950)
-- Name: carr_edi_cst_tp_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carr_edi_cst_tp_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carr_edi_cst_tp_seq OWNER TO postgres;

--
-- TOC entry 500 (class 1259 OID 22954)
-- Name: carr_inv_add_det_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carr_inv_add_det_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carr_inv_add_det_seq OWNER TO postgres;

--
-- TOC entry 499 (class 1259 OID 22952)
-- Name: carr_inv_addr_det_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carr_inv_addr_det_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carr_inv_addr_det_seq OWNER TO postgres;

--
-- TOC entry 501 (class 1259 OID 22956)
-- Name: carr_perf_iss_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carr_perf_iss_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carr_perf_iss_seq OWNER TO postgres;

--
-- TOC entry 502 (class 1259 OID 22958)
-- Name: carr_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carr_seq OWNER TO postgres;

--
-- TOC entry 492 (class 1259 OID 22938)
-- Name: carrier_capacity_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carrier_capacity_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carrier_capacity_seq OWNER TO postgres;

--
-- TOC entry 493 (class 1259 OID 22940)
-- Name: carrier_group_carriers_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carrier_group_carriers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carrier_group_carriers_seq OWNER TO postgres;

--
-- TOC entry 494 (class 1259 OID 22942)
-- Name: carrier_group_tid_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carrier_group_tid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carrier_group_tid_seq OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 18174)
-- Name: carrier_invoice_addr_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE carrier_invoice_addr_details (
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
-- TOC entry 234 (class 1259 OID 18185)
-- Name: carrier_invoice_cost_items; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE carrier_invoice_cost_items (
    invoice_cost_detail_item_id bigint NOT NULL,
    invoice_det_id bigint NOT NULL,
    ref_type character varying(10) NOT NULL,
    subtotal numeric(10,2),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint NOT NULL
);


ALTER TABLE carrier_invoice_cost_items OWNER TO postgres;

--
-- TOC entry 495 (class 1259 OID 22944)
-- Name: carrier_invoice_cost_items_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carrier_invoice_cost_items_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carrier_invoice_cost_items_seq OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 18191)
-- Name: carrier_invoice_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE carrier_invoice_details (
    invoice_det_id bigint NOT NULL,
    invoice_num character varying(255),
    invoice_date timestamp without time zone,
    reference_num character varying(255),
    pay_terms character varying(8),
    net_amount numeric(10,2),
    delivery_date timestamp without time zone,
    est_delivery_date timestamp without time zone,
    bol character varying(25),
    po_num character varying(255),
    shipper_reference_number character varying(255),
    pro_number character varying(255),
    act_pickup_date timestamp without time zone,
    total_weight numeric(10,2),
    total_charges numeric(10,2),
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
-- TOC entry 496 (class 1259 OID 22946)
-- Name: carrier_invoice_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carrier_invoice_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carrier_invoice_details_seq OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 18201)
-- Name: carrier_invoice_line_items; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE carrier_invoice_line_items (
    invoice_line_item_id bigint NOT NULL,
    invoice_det_id bigint NOT NULL,
    order_num bigint,
    description character varying(1024),
    weight numeric(10,2),
    quantity bigint,
    packaging_code character varying(30),
    commodity_code character varying(30),
    commodity_class_code character varying(8),
    charge numeric(10,2),
    special_charge_code character varying(30),
    status character(1),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint NOT NULL
);


ALTER TABLE carrier_invoice_line_items OWNER TO postgres;

--
-- TOC entry 497 (class 1259 OID 22948)
-- Name: carrier_invoice_line_items_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE carrier_invoice_line_items_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE carrier_invoice_line_items_seq OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 18218)
-- Name: carrier_invoice_reason_links; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE carrier_invoice_reason_links (
    link_id bigint NOT NULL,
    reason_id bigint,
    carrier_invoice_id bigint,
    status character varying(1)
);


ALTER TABLE carrier_invoice_reason_links OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 18210)
-- Name: carrier_invoice_reasons; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE carrier_invoice_reasons (
    reason_id bigint NOT NULL,
    reason_code character varying(2),
    load_id character varying(50),
    note character varying(500),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL
);


ALTER TABLE carrier_invoice_reasons OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 18102)
-- Name: carriers; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE carriers (
    org_id bigint NOT NULL,
    us_dot character varying(25),
    internet character(1) DEFAULT 'N'::bpchar NOT NULL,
    home_page character varying(100),
    edi_capable character(1) DEFAULT 'N'::bpchar NOT NULL,
    satellite_equipped character(1) DEFAULT 'N'::bpchar NOT NULL,
    ltl character(1) DEFAULT 'N'::bpchar NOT NULL,
    broker character(1) DEFAULT 'N'::bpchar NOT NULL,
    use_agents character(1) DEFAULT 'N'::bpchar,
    union_drivers character(1) DEFAULT 'N'::bpchar,
    radioactive character(1) DEFAULT 'N'::bpchar NOT NULL,
    hazmat character(1) DEFAULT 'N'::bpchar NOT NULL,
    chains character(1) DEFAULT 'N'::bpchar NOT NULL,
    straps character(1) DEFAULT 'N'::bpchar NOT NULL,
    steel_tarps character(1) DEFAULT 'N'::bpchar NOT NULL,
    lumber_tarps character(1) DEFAULT 'N'::bpchar NOT NULL,
    truck_rev_day numeric(10,2),
    cost_per_mile numeric(10,2),
    preferred_lanes character varying(1000),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    carrier_type character(2) DEFAULT 'FC'::bpchar NOT NULL,
    taxpayer_status character(2),
    safety_rating smallint,
    carrier_id bigint NOT NULL,
    network_id bigint NOT NULL,
    status character(1) NOT NULL,
    status_reason character varying(8),
    restricted character(1) DEFAULT 'N'::bpchar NOT NULL,
    canadian_authority_flag character(1),
    mexican_authority_flag character(1),
    number_of_units bigint,
    code_red_mgmt_flag character(1) DEFAULT 'N'::bpchar,
    code_red_comments character varying(2000),
    ctpat_flag character(1) DEFAULT 'N'::bpchar,
    company_eqpt_flag character(1) DEFAULT 'N'::bpchar,
    radioactive_date timestamp without time zone,
    hazmat_date timestamp without time zone,
    bonded character(1) DEFAULT 'N'::bpchar NOT NULL,
    ace_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    fast_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    team_drivers_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    mexico_through_service_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    pip_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    pls_contract_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    pls_contract_flag_date timestamp without time zone,
    gm_contract_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    gm_contract_flag_date timestamp without time zone,
    etrack_van character(1) DEFAULT 'N'::bpchar NOT NULL,
    logistics_post_van character(1) DEFAULT 'N'::bpchar NOT NULL,
    tiedowns_van character(1) DEFAULT 'N'::bpchar NOT NULL,
    wstnghs_contract_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    wstnghs_contract_flag_date timestamp without time zone,
    elliot_contract_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    elliot_contract_flag_date timestamp without time zone,
    worthington_contract_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    worthington_contract_flag_date timestamp without time zone,
    rti_contract_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    rti_contract_flag_date timestamp without time zone,
    paperwork_req_ind character(1) DEFAULT 'Y'::bpchar,
    payment_type character varying(30) DEFAULT 'PNC Check'::character varying,
    term_id bigint,
    version integer DEFAULT 1 NOT NULL,
    high_value_comm_carr character(1),
    high_value_comm_carr_date timestamp without time zone,
    hv_comm_carr_modified_by bigint,
    fda_compliant character varying(1) DEFAULT 'N'::character varying,
    fda_compliant_date timestamp without time zone,
    eld_compliant character varying(1) DEFAULT 'N'::character varying,
    eld_compliant_date timestamp without time zone,
    CONSTRAINT avcon_1049920042_broke_000 CHECK ((broker = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_chain_000 CHECK ((chains = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_edi_c_000 CHECK ((edi_capable = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_hazma_000 CHECK ((hazmat = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_inter_000 CHECK ((internet = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_ltl_000 CHECK ((ltl = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_lumbe_000 CHECK ((lumber_tarps = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_radio_000 CHECK ((radioactive = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_satel_000 CHECK ((satellite_equipped = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_steel_000 CHECK ((steel_tarps = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_strap_000 CHECK ((straps = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_union_000 CHECK ((union_drivers = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1049920042_use_a_000 CHECK ((use_agents = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar]))),
    CONSTRAINT avcon_1134837737_statu_001 CHECK ((status = ANY (ARRAY['A'::bpchar, 'I'::bpchar, 'P'::bpchar, 'R'::bpchar]))),
    CONSTRAINT avcon_1196267923_bonde_000 CHECK ((bonded = ANY (ARRAY['Y'::bpchar, 'N'::bpchar, 'U'::bpchar])))
);


ALTER TABLE carriers OWNER TO postgres;

--
-- TOC entry 503 (class 1259 OID 22960)
-- Name: cbi_invoice_numb_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cbi_invoice_numb_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cbi_invoice_numb_seq OWNER TO postgres;

--
-- TOC entry 505 (class 1259 OID 22964)
-- Name: cc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cc_seq OWNER TO postgres;

--
-- TOC entry 504 (class 1259 OID 22962)
-- Name: ccrt_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ccrt_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ccrt_seq OWNER TO postgres;

--
-- TOC entry 506 (class 1259 OID 22966)
-- Name: ceq_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ceq_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ceq_seq OWNER TO postgres;

--
-- TOC entry 507 (class 1259 OID 22968)
-- Name: cgp_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cgp_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cgp_seq OWNER TO postgres;

--
-- TOC entry 508 (class 1259 OID 22970)
-- Name: cinq_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cinq_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cinq_seq OWNER TO postgres;

--
-- TOC entry 509 (class 1259 OID 22972)
-- Name: cins_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cins_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cins_seq OWNER TO postgres;

--
-- TOC entry 510 (class 1259 OID 22974)
-- Name: client_ftp_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE client_ftp_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE client_ftp_details_seq OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 18243)
-- Name: commodity_class_codes; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE commodity_class_codes (
    class_code numeric NOT NULL
);


ALTER TABLE commodity_class_codes OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 18246)
-- Name: company_codes; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE company_codes (
    company_code character varying(2) NOT NULL,
    network_id smallint NOT NULL,
    description character varying(100)
);


ALTER TABLE company_codes OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 18270)
-- Name: container_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE container_types (
    container_cd character varying(10) NOT NULL,
    description character varying(100) NOT NULL,
    billing_container_cd character varying(100) NOT NULL,
    mode_of_transport character varying(4) DEFAULT 'FLTB'::character varying NOT NULL,
    mobile_category_cd character varying(15),
    icon_url character varying(255)
);


ALTER TABLE container_types OWNER TO postgres;

--
-- TOC entry 6276 (class 0 OID 0)
-- Dependencies: 245
-- Name: COLUMN container_types.container_cd; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN container_types.container_cd IS 'Code describing transport containers (i.e. -- flatbed trailer types)';


--
-- TOC entry 6277 (class 0 OID 0)
-- Dependencies: 245
-- Name: COLUMN container_types.description; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN container_types.description IS 'Description of transport container'; 

--
-- TOC entry 511 (class 1259 OID 22976)
-- Name: cot_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cot_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cot_seq OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 18277)
-- Name: countries; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE countries (
    country_code character varying(3) NOT NULL,
    name character varying(50) NOT NULL,
    dialing_code character varying(3) NOT NULL,
    status character varying(1) DEFAULT 'I'::character varying NOT NULL,
    country_cd_short character varying(2)
);


ALTER TABLE countries OWNER TO postgres;

--
-- TOC entry 512 (class 1259 OID 22978)
-- Name: cpe_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cpe_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cpe_seq OWNER TO postgres;

--
-- TOC entry 513 (class 1259 OID 22980)
-- Name: credit_limit_thresholds_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE credit_limit_thresholds_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE credit_limit_thresholds_seq OWNER TO postgres;

--
-- TOC entry 514 (class 1259 OID 22982)
-- Name: crt_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE crt_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE crt_seq OWNER TO postgres;

--
-- TOC entry 515 (class 1259 OID 22984)
-- Name: ctsi_acc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ctsi_acc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ctsi_acc_seq OWNER TO postgres;

--
-- TOC entry 516 (class 1259 OID 22986)
-- Name: ctsi_fs_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ctsi_fs_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ctsi_fs_seq OWNER TO postgres;

--
-- TOC entry 517 (class 1259 OID 22988)
-- Name: ctsi_rating_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ctsi_rating_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ctsi_rating_seq OWNER TO postgres;

--
-- TOC entry 518 (class 1259 OID 22990)
-- Name: ctt_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ctt_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ctt_seq OWNER TO postgres;

--
-- TOC entry 524 (class 1259 OID 23002)
-- Name: cust_field_manual_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cust_field_manual_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cust_field_manual_seq OWNER TO postgres;

--
-- TOC entry 525 (class 1259 OID 23004)
-- Name: cust_inb_exception_log_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cust_inb_exception_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cust_inb_exception_log_seq OWNER TO postgres;

--
-- TOC entry 526 (class 1259 OID 23006)
-- Name: cust_invoice_err_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cust_invoice_err_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cust_invoice_err_seq OWNER TO postgres;

--
-- TOC entry 527 (class 1259 OID 23008)
-- Name: cust_prof_hist_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE cust_prof_hist_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cust_prof_hist_seq OWNER TO postgres;

--
-- TOC entry 521 (class 1259 OID 22996)
-- Name: custom_field_ref_id_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE custom_field_ref_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE custom_field_ref_id_seq OWNER TO postgres;

--
-- TOC entry 522 (class 1259 OID 22998)
-- Name: custom_field_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE custom_field_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE custom_field_seq OWNER TO postgres;

--
-- TOC entry 523 (class 1259 OID 23000)
-- Name: custom_mileage_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE custom_mileage_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE custom_mileage_seq OWNER TO postgres;

--
-- TOC entry 520 (class 1259 OID 22994)
-- Name: customer_group_users_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE customer_group_users_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE customer_group_users_seq OWNER TO postgres;

--
-- TOC entry 519 (class 1259 OID 22992)
-- Name: customer_groups_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE customer_groups_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE customer_groups_seq OWNER TO postgres;

--
-- TOC entry 249 (class 1259 OID 18297)
-- Name: customer_invoice_errors; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE customer_invoice_errors (
    id bigint NOT NULL,
    invoice_num character varying(20),
    message character varying(2000),
    sent_email character(1),
    sent_to_finance character(1),
    status character(1) DEFAULT 'A'::bpchar,
    version bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    stack_trace text,
    sent_edi character varying(1),
    sent_documents character(1),
    invoice_id bigint
);


ALTER TABLE customer_invoice_errors OWNER TO postgres;

--
-- TOC entry 528 (class 1259 OID 23010)
-- Name: dcu_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE dcu_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE dcu_seq OWNER TO postgres;

--
-- TOC entry 529 (class 1259 OID 23012)
-- Name: dde_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE dde_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE dde_seq OWNER TO postgres;

--
-- TOC entry 530 (class 1259 OID 23014)
-- Name: dft_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE dft_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE dft_seq OWNER TO postgres;

--
-- TOC entry 531 (class 1259 OID 23016)
-- Name: dok_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE dok_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE dok_seq OWNER TO postgres;

--
-- TOC entry 252 (class 1259 OID 18319)
-- Name: dot_region_fuel; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE dot_region_fuel (
    dot_region_fuel_id bigint NOT NULL,
    dot_region_id bigint NOT NULL,
    fuel_charge numeric(10,2) NOT NULL,
    eff_date timestamp without time zone NOT NULL,
    exp_date timestamp without time zone,
    status character varying(1),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE dot_region_fuel OWNER TO postgres;

--
-- TOC entry 533 (class 1259 OID 23020)
-- Name: dot_region_fuel_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE dot_region_fuel_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE dot_region_fuel_seq OWNER TO postgres;

--
-- TOC entry 251 (class 1259 OID 18314)
-- Name: dot_regions; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE dot_regions (
    dot_region_id bigint NOT NULL,
    dot_region_name character varying(100) NOT NULL,
    description character varying(100)
);


ALTER TABLE dot_regions OWNER TO postgres;

--
-- TOC entry 532 (class 1259 OID 23018)
-- Name: dot_regions_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE dot_regions_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE dot_regions_seq OWNER TO postgres;

--
-- TOC entry 534 (class 1259 OID 23022)
-- Name: dpm_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE dpm_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE dpm_seq OWNER TO postgres;

--
-- TOC entry 535 (class 1259 OID 23024)
-- Name: driver_phone_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE driver_phone_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE driver_phone_seq OWNER TO postgres;

--
-- TOC entry 536 (class 1259 OID 23026)
-- Name: dse_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE dse_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE dse_seq OWNER TO postgres;

--
-- TOC entry 253 (class 1259 OID 18325)
-- Name: edi; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE edi (
    edi_id bigint NOT NULL,
    org_id character varying(255),
    location_id character varying(255),
    load_id character varying(255),
    shipper_reference_number character varying(255),
    status character(1) DEFAULT 'R'::bpchar NOT NULL,
    message character varying(255),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    date_viewed timestamp without time zone,
    original_srn character varying(255),
    file_type character varying(5)
);


ALTER TABLE edi OWNER TO postgres;

--
-- TOC entry 537 (class 1259 OID 23028)
-- Name: edi_ack_loads_temp_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE edi_ack_loads_temp_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE edi_ack_loads_temp_seq OWNER TO postgres;

--
-- TOC entry 254 (class 1259 OID 18335)
-- Name: edi_load_data; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE edi_load_data (
    load_id bigint,
    edi_num smallint,
    edi_file_name character varying(255),
    gs bigint NOT NULL,
    isa bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL
);


ALTER TABLE edi_load_data OWNER TO postgres;

--
-- TOC entry 538 (class 1259 OID 23030)
-- Name: edi_ltl_load_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE edi_ltl_load_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE edi_ltl_load_seq OWNER TO postgres;

--
-- TOC entry 255 (class 1259 OID 18340)
-- Name: edi_qualifiers; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE edi_qualifiers (
    edi_qual_id bigint NOT NULL,
    transaction_set_id character varying(10) NOT NULL,
    org_id bigint NOT NULL,
    element character varying(10) NOT NULL,
    reference_qual character varying(30) NOT NULL,
    pls_reference character varying(50) NOT NULL
);


ALTER TABLE edi_qualifiers OWNER TO postgres;

--
-- TOC entry 539 (class 1259 OID 23032)
-- Name: edi_qualifiers_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE edi_qualifiers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE edi_qualifiers_seq OWNER TO postgres;

--
-- TOC entry 256 (class 1259 OID 18348)
-- Name: edi_rejected_customers; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE edi_rejected_customers (
    id bigint NOT NULL,
    carrier_id bigint NOT NULL,
    customer_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE edi_rejected_customers OWNER TO postgres;

--
-- TOC entry 540 (class 1259 OID 23034)
-- Name: edi_rejected_customers_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE edi_rejected_customers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE edi_rejected_customers_seq OWNER TO postgres;

--
-- TOC entry 541 (class 1259 OID 23036)
-- Name: edi_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE edi_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE edi_seq OWNER TO postgres;

--
-- TOC entry 257 (class 1259 OID 18353)
-- Name: edi_settings; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE edi_settings (
    edi_settings_id bigint NOT NULL,
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
-- TOC entry 542 (class 1259 OID 23038)
-- Name: edi_settings_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE edi_settings_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE edi_settings_seq OWNER TO postgres;

--
-- TOC entry 258 (class 1259 OID 18361)
-- Name: email_history; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE email_history (
    email_history_id bigint NOT NULL,
    email_type character varying(12),
    send_to character varying(225),
    subject character varying(250),
    text text,
    notification_type character varying(32),
    send_by bigint,
    send_time timestamp without time zone NOT NULL
);


ALTER TABLE email_history OWNER TO postgres;

--
-- TOC entry 259 (class 1259 OID 18369)
-- Name: email_history_attachment; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE email_history_attachment (
    email_history_attachment_id bigint NOT NULL,
    image_metadata_id bigint,
    email_history_id bigint,
    filename_for_user character varying(100)
);


ALTER TABLE email_history_attachment OWNER TO postgres;

--
-- TOC entry 543 (class 1259 OID 23040)
-- Name: email_history_attachment_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE email_history_attachment_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE email_history_attachment_seq OWNER TO postgres;

--
-- TOC entry 260 (class 1259 OID 18374)
-- Name: email_history_load; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE email_history_load (
    email_history_load_id bigint NOT NULL,
    load_id bigint NOT NULL,
    email_history_id bigint NOT NULL
);


ALTER TABLE email_history_load OWNER TO postgres;

--
-- TOC entry 544 (class 1259 OID 23042)
-- Name: email_history_load_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE email_history_load_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE email_history_load_seq OWNER TO postgres;

--
-- TOC entry 545 (class 1259 OID 23044)
-- Name: email_history_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE email_history_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE email_history_seq OWNER TO postgres;

--
-- TOC entry 546 (class 1259 OID 23046)
-- Name: eq_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE eq_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE eq_seq OWNER TO postgres;

--
-- TOC entry 547 (class 1259 OID 23048)
-- Name: export_column_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE export_column_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE export_column_seq OWNER TO postgres;

--
-- TOC entry 548 (class 1259 OID 23050)
-- Name: export_template_column_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE export_template_column_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE export_template_column_seq OWNER TO postgres;

--
-- TOC entry 549 (class 1259 OID 23052)
-- Name: export_template_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE export_template_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE export_template_seq OWNER TO postgres;

--
-- TOC entry 262 (class 1259 OID 18386)
-- Name: fa_accessorials; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE fa_accessorials (
    accessorial_id bigint NOT NULL,
    accessorial_type character varying(3),
    input_detail_id bigint,
    seq_number integer
);


ALTER TABLE fa_accessorials OWNER TO postgres;

--
-- TOC entry 552 (class 1259 OID 23058)
-- Name: fa_accessorials_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE fa_accessorials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE fa_accessorials_seq OWNER TO postgres;

--
-- TOC entry 263 (class 1259 OID 18391)
-- Name: fa_financial_analysis; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE fa_financial_analysis (
    analysis_id bigint NOT NULL,
    input_file bigint,
    seq_number integer,
    input_file_name character varying(255),
    output_file bigint,
    output_file_name character varying(255),
    status character(1),
    block_indirect_type character(1),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL
);


ALTER TABLE fa_financial_analysis OWNER TO postgres;

--
-- TOC entry 553 (class 1259 OID 23060)
-- Name: fa_financial_analysis_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE fa_financial_analysis_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE fa_financial_analysis_seq OWNER TO postgres;

--
-- TOC entry 264 (class 1259 OID 18400)
-- Name: fa_input_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE fa_input_details (
    input_detail_id bigint NOT NULL,
    analysis_id integer,
    completed character(1),
    seq_number integer,
    user_seq_number integer,
    org_id bigint,
    shipment_date timestamp without time zone,
    origin_city character varying(50),
    origin_state character varying(25),
    origin_zip character varying(25),
    origin_override_zip character varying(25),
    origin_country character varying(25),
    dest_city character varying(50),
    dest_state character varying(25),
    dest_zip character varying(25),
    dest_override_zip character varying(25),
    dest_country character varying(25),
    pallet integer,
    pieces integer,
    calculate_fsc character(1),
    user_scac character varying(4)
);


ALTER TABLE fa_input_details OWNER TO postgres;

--
-- TOC entry 554 (class 1259 OID 23062)
-- Name: fa_input_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE fa_input_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE fa_input_details_seq OWNER TO postgres;

--
-- TOC entry 265 (class 1259 OID 18407)
-- Name: fa_materials; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE fa_materials (
    material_id bigint NOT NULL,
    commodity_class_code character varying(8),
    weight numeric(10,2),
    input_detail_id bigint,
    seq_number integer
);


ALTER TABLE fa_materials OWNER TO postgres;

--
-- TOC entry 555 (class 1259 OID 23064)
-- Name: fa_materials_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE fa_materials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE fa_materials_seq OWNER TO postgres;

--
-- TOC entry 266 (class 1259 OID 18413)
-- Name: fa_output_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE fa_output_details (
    output_detail_id bigint NOT NULL,
    org_id bigint,
    input_detail_id bigint,
    cost_detail_owner character(1),
    date_created timestamp without time zone,
    subtotal numeric(10,2),
    transit_days integer,
    seq_number integer,
    fa_tariff_id bigint,
    error_message character varying(4000)
);


ALTER TABLE fa_output_details OWNER TO postgres;

--
-- TOC entry 556 (class 1259 OID 23066)
-- Name: fa_output_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE fa_output_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE fa_output_details_seq OWNER TO postgres;

--
-- TOC entry 267 (class 1259 OID 18424)
-- Name: fa_tariffs; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE fa_tariffs (
    tariff_id bigint NOT NULL,
    tariff_name character varying(100),
    tariff_type character varying(25),
    ltl_pricing_profile_id bigint,
    org_id bigint,
    analysis_id bigint
);


ALTER TABLE fa_tariffs OWNER TO postgres;

--
-- TOC entry 557 (class 1259 OID 23068)
-- Name: fa_tariffs_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE fa_tariffs_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE fa_tariffs_seq OWNER TO postgres;

--
-- TOC entry 550 (class 1259 OID 23054)
-- Name: faa_det_prod_info_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE faa_det_prod_info_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE faa_det_prod_info_seq OWNER TO postgres;

--
-- TOC entry 551 (class 1259 OID 23056)
-- Name: factoring_document_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE factoring_document_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE factoring_document_seq OWNER TO postgres;

--
-- TOC entry 568 (class 1259 OID 23090)
-- Name: fin_org_settings_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE fin_org_settings_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE fin_org_settings_seq OWNER TO postgres;

--
-- TOC entry 268 (class 1259 OID 18430)
-- Name: finan_account_payables; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_account_payables (
    finan_ap_id bigint NOT NULL,
    load_id bigint NOT NULL,
    faa_detail_id bigint,
    finan_load_id character varying(30) NOT NULL,
    adj_acc character varying(10),
    inv_number character varying(50),
    inv_date timestamp without time zone,
    inv_due_date timestamp without time zone,
    amt_invoiced numeric(10,2),
    amt_due numeric(10,2),
    amt_applied numeric(10,2),
    inv_paid_date timestamp without time zone,
    inv_actual_date_closed timestamp without time zone,
    currency_code character varying(3),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer DEFAULT 1 NOT NULL,
    imported_from_oracle_finan character varying(1)
);


ALTER TABLE finan_account_payables OWNER TO postgres;

--
-- TOC entry 558 (class 1259 OID 23070)
-- Name: finan_account_payables_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE finan_account_payables_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE finan_account_payables_seq OWNER TO postgres;

--
-- TOC entry 269 (class 1259 OID 18438)
-- Name: finan_account_receivables; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_account_receivables (
    finan_ar_id bigint NOT NULL,
    load_id bigint NOT NULL,
    faa_detail_id bigint,
    finan_load_id character varying(30) NOT NULL,
    adj_acc character varying(10),
    inv_number character varying(50),
    inv_date timestamp without time zone,
    inv_due_date timestamp without time zone,
    amt_invoiced numeric(10,2),
    amt_due numeric(10,2),
    amt_applied numeric(10,2),
    inv_paid_date timestamp without time zone,
    inv_actual_date_closed timestamp without time zone,
    currency_code character varying(3),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer DEFAULT 1 NOT NULL,
    total_amt_applied numeric(10,2)
);


ALTER TABLE finan_account_receivables OWNER TO postgres;

--
-- TOC entry 559 (class 1259 OID 23072)
-- Name: finan_account_receivables_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE finan_account_receivables_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE finan_account_receivables_seq OWNER TO postgres;

--
-- TOC entry 270 (class 1259 OID 18446)
-- Name: finan_adj_acc_detail; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_adj_acc_detail (
    faa_detail_id bigint NOT NULL,
    load_id bigint NOT NULL,
    bol character varying(25),
    adj_acc character varying(3),
    revision integer,
    reason character varying(20),
    gl_date timestamp without time zone,
    faa_status character varying(5),
    total_revenue numeric(10,2) NOT NULL,
    total_costs numeric(10,2) NOT NULL,
    short_pay character(1) DEFAULT 'N'::bpchar,
    sent_to_finance character(1) DEFAULT 'N'::bpchar,
    created_by bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    modified_by bigint,
    date_modified timestamp without time zone,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    inv_approved character(1),
    customer_invoice_num character varying(20),
    do_not_invoice character varying(1),
    group_invoice_num character varying(25),
    invoiced_in_finan character(1)
);


ALTER TABLE finan_adj_acc_detail OWNER TO postgres;

--
-- TOC entry 271 (class 1259 OID 18464)
-- Name: finan_adj_acc_detail_addl_info; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_adj_acc_detail_addl_info (
    faa_detail_addl_id bigint NOT NULL,
    faa_detail_id bigint NOT NULL,
    po_num character varying(50),
    shipper_reference_number character varying(30),
    gl_number character varying(50),
    pickup_date timestamp without time zone,
    inbound_outbound_flg character varying(1),
    pay_terms character varying(3),
    version integer DEFAULT 1 NOT NULL,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    so_number character varying(50)
);


ALTER TABLE finan_adj_acc_detail_addl_info OWNER TO postgres;

--
-- TOC entry 560 (class 1259 OID 23074)
-- Name: finan_adj_acc_detail_addl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE finan_adj_acc_detail_addl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE finan_adj_acc_detail_addl_seq OWNER TO postgres;

--
-- TOC entry 272 (class 1259 OID 18471)
-- Name: finan_adj_acc_detail_prod_info; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_adj_acc_detail_prod_info (
    faa_detail_addl_prod_id bigint NOT NULL,
    faa_detail_id bigint NOT NULL,
    load_material_id bigint NOT NULL,
    weight numeric(10,2),
    version integer DEFAULT 1 NOT NULL,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint
);


ALTER TABLE finan_adj_acc_detail_prod_info OWNER TO postgres;

--
-- TOC entry 561 (class 1259 OID 23076)
-- Name: finan_adj_acc_detail_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE finan_adj_acc_detail_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE finan_adj_acc_detail_seq OWNER TO postgres;

--
-- TOC entry 273 (class 1259 OID 18479)
-- Name: finan_adj_acc_reasons; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_adj_acc_reasons (
    adj_acc_type_id bigint NOT NULL,
    adj_acc_type_code character varying(20) NOT NULL,
    adj_acc character varying(3) NOT NULL,
    description character varying(250) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer,
    applicable_to character varying(10)
);


ALTER TABLE finan_adj_acc_reasons OWNER TO postgres;

--
-- TOC entry 274 (class 1259 OID 18484)
-- Name: finan_ap_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_ap_details (
    finan_ap_detail_id bigint NOT NULL,
    finan_ap_id bigint NOT NULL,
    amt_applied numeric(10,2),
    check_num character varying(50) NOT NULL,
    check_date timestamp without time zone,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer DEFAULT 1 NOT NULL,
    imported_from_oracle_finan character varying(1)
);


ALTER TABLE finan_ap_details OWNER TO postgres;

--
-- TOC entry 562 (class 1259 OID 23078)
-- Name: finan_ap_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE finan_ap_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE finan_ap_details_seq OWNER TO postgres;

--
-- TOC entry 275 (class 1259 OID 18491)
-- Name: finan_ar_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_ar_details (
    finan_ar_detail_id bigint NOT NULL,
    finan_ar_id bigint NOT NULL,
    amt_applied numeric(10,2),
    check_num character varying(50) NOT NULL,
    check_date timestamp without time zone,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer DEFAULT 1 NOT NULL
);


ALTER TABLE finan_ar_details OWNER TO postgres;

--
-- TOC entry 563 (class 1259 OID 23080)
-- Name: finan_ar_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE finan_ar_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE finan_ar_details_seq OWNER TO postgres;

--
-- TOC entry 276 (class 1259 OID 18497)
-- Name: finan_billers; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_billers (
    finan_biller_id bigint NOT NULL,
    person_id bigint NOT NULL,
    biller_type character varying(50) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE finan_billers OWNER TO postgres;

--
-- TOC entry 564 (class 1259 OID 23082)
-- Name: finan_billers_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE finan_billers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE finan_billers_seq OWNER TO postgres;

--
-- TOC entry 277 (class 1259 OID 18502)
-- Name: finan_busn_unit; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_busn_unit (
    unit_code character varying(30) NOT NULL,
    unit_name character varying(100),
    network_id bigint,
    org_id bigint,
    status character varying(1) DEFAULT 'A'::character varying
);


ALTER TABLE finan_busn_unit OWNER TO postgres;

--
-- TOC entry 278 (class 1259 OID 18508)
-- Name: finan_cost_center; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_cost_center (
    cost_center_code character varying(30) NOT NULL,
    cost_center_name character varying(100),
    network_id bigint,
    company_code character varying(2),
    status character varying(1) DEFAULT 'A'::character varying
);


ALTER TABLE finan_cost_center OWNER TO postgres;

--
-- TOC entry 279 (class 1259 OID 18512)
-- Name: finan_cust_group; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_cust_group (
    group_code character varying(30) NOT NULL,
    group_name character varying(100),
    network_id bigint,
    company_code character varying(2),
    org_id bigint,
    status character varying(1) DEFAULT 'A'::character varying
);


ALTER TABLE finan_cust_group OWNER TO postgres;

--
-- TOC entry 565 (class 1259 OID 23084)
-- Name: finan_frt_bill_upd_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE finan_frt_bill_upd_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE finan_frt_bill_upd_seq OWNER TO postgres;

--
-- TOC entry 963 (class 1259 OID 44345)
-- Name: finan_int_responses; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_int_responses (
    finan_int_resp_id bigint NOT NULL,
    request_id bigint NOT NULL,
    load_id bigint NOT NULL,
    faa_detail_id bigint,
    sent_to_finance character varying(1) DEFAULT 'N'::character varying,
    sent_ap_to_finance character varying(1) DEFAULT 'N'::character varying,
    sent_ar_to_finance character varying(1) DEFAULT 'N'::character varying,
    ap_file_name character varying(100),
    ar_file_name character varying(100),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    ap_message_id character varying(100),
    ar_message_id character varying(100)
);


ALTER TABLE finan_int_responses OWNER TO postgres; 

--
-- TOC entry 566 (class 1259 OID 23086)
-- Name: finan_int_responses_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE finan_int_responses_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE finan_int_responses_seq OWNER TO postgres;

--
-- TOC entry 567 (class 1259 OID 23088)
-- Name: finan_payment_method_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE finan_payment_method_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE finan_payment_method_seq OWNER TO postgres;

--
-- TOC entry 280 (class 1259 OID 18516)
-- Name: finan_requests; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE finan_requests (
    request_id bigint NOT NULL,
    request_name character varying(30),
    start_date timestamp without time zone,
    end_date timestamp without time zone,
    loads_in_batch integer,
    load_status_changes integer,
    loads_finalized integer,
    loads_processed integer,
    percent_complete smallint,
    parameter_list character varying(2000),
    p_load_list character varying(255),
    p_program_id character varying(1000),
    p_low_pud timestamp without time zone,
    p_high_pud timestamp without time zone,
    p_carrier character varying(255),
    p_fin_status character varying(5),
    p_org_list character varying(2000),
    p_hold_release character(1),
    p_get_count_only character(1),
    load_errors integer,
    p_person_id bigint,
    p_low_deld timestamp without time zone,
    p_high_deld timestamp without time zone,
    p_loc_list character varying(255),
    p_gl_date timestamp without time zone,
    p_to_fin_status character varying(5),
    status character(1),
    p_network_id bigint,
    p_adj_acc character varying(9),
    p_ignore_paperwork_req character(1),
    p_invoice_group_id bigint,
    p_job_id bigint,
    ar_finalized integer DEFAULT 0,
    ap_finalized integer DEFAULT 0,
    total_costs_finalized numeric(10,2) DEFAULT 0,
    total_revenue_finalized numeric(10,2) DEFAULT 0,
    fb_count integer DEFAULT 0,
    fbh_count integer DEFAULT 0,
    ab_count integer DEFAULT 0,
    abh_count integer DEFAULT 0,
    abr_count integer DEFAULT 0,
    rb_count integer DEFAULT 0,
    nf_count integer DEFAULT 0,
    fbad_count integer DEFAULT 0,
    fbac_count integer DEFAULT 0,
    fbhad_count integer DEFAULT 0,
    fbhac_count integer DEFAULT 0,
    abaa_count integer DEFAULT 0,
    abhaa_count integer DEFAULT 0,
    finan_int_status character varying(1),
    ap_in_finan integer DEFAULT 0,
    ar_in_finan integer DEFAULT 0,
    loads_in_finan integer DEFAULT 0,
    reprocessed_date timestamp without time zone,
    originating_system character varying(10)
);


ALTER TABLE finan_requests OWNER TO postgres;

--
-- TOC entry 569 (class 1259 OID 23092)
-- Name: flyingj_loads_batch_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE flyingj_loads_batch_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE flyingj_loads_batch_seq OWNER TO postgres;

--
-- TOC entry 570 (class 1259 OID 23094)
-- Name: forms_surfaces_gl_code_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE forms_surfaces_gl_code_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE forms_surfaces_gl_code_seq OWNER TO postgres;

--
-- TOC entry 281 (class 1259 OID 18548)
-- Name: freight_bill_pay_to; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE freight_bill_pay_to (
    frt_bill_pay_to_id bigint NOT NULL,
    company character varying(200),
    contact_name character varying(100),
    account_num character varying(32),
    address_id bigint,
    phone_id bigint,
    email_address character varying(255),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    fax_id bigint
);


ALTER TABLE freight_bill_pay_to OWNER TO postgres;

--
-- TOC entry 571 (class 1259 OID 23096)
-- Name: freight_bill_pay_to_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE freight_bill_pay_to_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE freight_bill_pay_to_seq OWNER TO postgres;

--
-- TOC entry 572 (class 1259 OID 23098)
-- Name: freight_bill_update_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE freight_bill_update_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE freight_bill_update_seq OWNER TO postgres;

--
-- TOC entry 573 (class 1259 OID 23100)
-- Name: freight_solns_metrics_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE freight_solns_metrics_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE freight_solns_metrics_seq OWNER TO postgres;

--
-- TOC entry 574 (class 1259 OID 23102)
-- Name: frq_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE frq_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE frq_seq OWNER TO postgres;

--
-- TOC entry 575 (class 1259 OID 23104)
-- Name: fvd_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE fvd_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE fvd_seq OWNER TO postgres;

--
-- TOC entry 576 (class 1259 OID 23106)
-- Name: gps_tracking_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE gps_tracking_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE gps_tracking_seq OWNER TO postgres;

--
-- TOC entry 283 (class 1259 OID 18570)
-- Name: group_capabilities; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE group_capabilities (
    group_capability_id bigint NOT NULL,
    group_id bigint NOT NULL,
    capability_id bigint NOT NULL,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    CONSTRAINT group_caps_status_values CHECK ((status = ANY (ARRAY['A'::bpchar, 'I'::bpchar])))
);


ALTER TABLE group_capabilities OWNER TO postgres;

--
-- TOC entry 6278 (class 0 OID 0)
-- Dependencies: 283
-- Name: TABLE group_capabilities; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON TABLE group_capabilities IS 'Junction table for Groups-Capabilities relation';


--
-- TOC entry 6279 (class 0 OID 0)
-- Dependencies: 283
-- Name: COLUMN group_capabilities.group_capability_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN group_capabilities.group_capability_id IS 'PK for this table';


--
-- TOC entry 6280 (class 0 OID 0)
-- Dependencies: 283
-- Name: COLUMN group_capabilities.group_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN group_capabilities.group_id IS 'Related Group. Refers to GROUPS.GROUP_ID';


--
-- TOC entry 6281 (class 0 OID 0)
-- Dependencies: 283
-- Name: COLUMN group_capabilities.capability_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN group_capabilities.capability_id IS 'Related Capability. Refers to CAPABILITIES.CAPABILITY_ID';


--
-- TOC entry 6282 (class 0 OID 0)
-- Dependencies: 283
-- Name: COLUMN group_capabilities.status; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN group_capabilities.status IS 'A - active, I - inactive';


--
-- TOC entry 6283 (class 0 OID 0)
-- Dependencies: 283
-- Name: COLUMN group_capabilities.date_created; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN group_capabilities.date_created IS 'When this record was created';


--
-- TOC entry 6284 (class 0 OID 0)
-- Dependencies: 283
-- Name: COLUMN group_capabilities.created_by; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN group_capabilities.created_by IS 'Who created this record. Refers to USERS.PERSON_ID';


--
-- TOC entry 6285 (class 0 OID 0)
-- Dependencies: 283
-- Name: COLUMN group_capabilities.date_modified; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN group_capabilities.date_modified IS 'When this record was updated last time';


--
-- TOC entry 6286 (class 0 OID 0)
-- Dependencies: 283
-- Name: COLUMN group_capabilities.modified_by; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN group_capabilities.modified_by IS 'Who updated this record last time. Refers to USERS.PERSON_ID';


--
-- TOC entry 6287 (class 0 OID 0)
-- Dependencies: 283
-- Name: COLUMN group_capabilities.version; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN group_capabilities.version IS 'Version for optimistic locking';


--
-- TOC entry 578 (class 1259 OID 23110)
-- Name: group_capabilities_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE group_capabilities_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE group_capabilities_seq OWNER TO postgres;

--
-- TOC entry 282 (class 1259 OID 18558)
-- Name: groups; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE groups (
    group_id bigint NOT NULL,
    name character varying(100) NOT NULL,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    CONSTRAINT groups_status_values CHECK ((status = ANY (ARRAY['A'::bpchar, 'I'::bpchar])))
);


ALTER TABLE groups OWNER TO postgres;

--
-- TOC entry 6288 (class 0 OID 0)
-- Dependencies: 282
-- Name: TABLE groups; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON TABLE groups IS 'User groups for simple assign permissions/capabilities';


--
-- TOC entry 6289 (class 0 OID 0)
-- Dependencies: 282
-- Name: COLUMN groups.group_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN groups.group_id IS 'PK for this table';


--
-- TOC entry 6290 (class 0 OID 0)
-- Dependencies: 282
-- Name: COLUMN groups.name; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN groups.name IS 'Human readable label';


--
-- TOC entry 6291 (class 0 OID 0)
-- Dependencies: 282
-- Name: COLUMN groups.status; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN groups.status IS 'A - active, I - inactive';


--
-- TOC entry 6292 (class 0 OID 0)
-- Dependencies: 282
-- Name: COLUMN groups.date_created; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN groups.date_created IS 'When this record was created';


--
-- TOC entry 6293 (class 0 OID 0)
-- Dependencies: 282
-- Name: COLUMN groups.created_by; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN groups.created_by IS 'Who created this record. Refers to USERS.PERSON_ID';


--
-- TOC entry 6294 (class 0 OID 0)
-- Dependencies: 282
-- Name: COLUMN groups.date_modified; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN groups.date_modified IS 'When this record was updated last time';


--
-- TOC entry 6295 (class 0 OID 0)
-- Dependencies: 282
-- Name: COLUMN groups.modified_by; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN groups.modified_by IS 'Who updated this record last time. Refers to USERS.PERSON_ID';


--
-- TOC entry 6296 (class 0 OID 0)
-- Dependencies: 282
-- Name: COLUMN groups.version; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN groups.version IS 'Version for optimistic locking';


--
-- TOC entry 577 (class 1259 OID 23108)
-- Name: groups_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE groups_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE groups_seq OWNER TO postgres;

--
-- TOC entry 579 (class 1259 OID 23112)
-- Name: gs_num_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE gs_num_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE gs_num_seq OWNER TO postgres;

--
-- TOC entry 580 (class 1259 OID 23114)
-- Name: help_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE help_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE help_seq OWNER TO postgres;

--
-- TOC entry 581 (class 1259 OID 23116)
-- Name: http_blob_test_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE http_blob_test_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE http_blob_test_seq OWNER TO postgres;

--
-- TOC entry 285 (class 1259 OID 18593)
-- Name: image_document_type; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE image_document_type (
    document_type character varying(20) NOT NULL,
    description character varying(100) NOT NULL,
    image_doc_type_id bigint NOT NULL,
    system_protected character(1) DEFAULT 'N'::bpchar NOT NULL,
    created_by bigint DEFAULT 0 NOT NULL,
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint DEFAULT 0 NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    document_level character varying(30),
    document_org_type character varying(30)
);


ALTER TABLE image_document_type OWNER TO postgres;

--
-- TOC entry 582 (class 1259 OID 23118)
-- Name: image_document_type_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE image_document_type_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE image_document_type_seq OWNER TO postgres;

--
-- TOC entry 286 (class 1259 OID 18606)
-- Name: image_metadata; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE image_metadata (
    image_meta_id bigint NOT NULL,
    image_id bigint,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    status character(1) NOT NULL,
    version integer NOT NULL,
    date_received timestamp without time zone,
    load_id bigint,
    document_type character varying(20) NOT NULL,
    shipment_number character varying(30),
    order_number character varying(25),
    shipper_name character varying(240),
    bol character varying(20),
    org_id numeric(30,0),
    orig_name character varying(100),
    orig_address1 character varying(200),
    orig_address2 character varying(200),
    orig_address3 character varying(200),
    orig_city character varying(30),
    orig_state character varying(2),
    orig_postal_code character varying(10),
    orig_country_code character varying(3),
    dest_name character varying(100),
    dest_address1 character varying(200),
    dest_address2 character varying(200),
    dest_address3 character varying(200),
    dest_city character varying(30),
    dest_state character varying(2),
    dest_postal_code character varying(10),
    dest_country_code character varying(3),
    weight bigint,
    upload_status character varying(1),
    notes character varying(500),
    image_file_type character varying(255),
    image_file_path character varying(255),
    image_file_name character varying(100),
    api_type_id bigint,
    download_token character varying(100),
    manual_bol_id bigint
);


ALTER TABLE image_metadata OWNER TO postgres;

--
-- TOC entry 6297 (class 0 OID 0)
-- Dependencies: 286
-- Name: COLUMN image_metadata.api_type_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN image_metadata.api_type_id IS 'API type used for downloading documents.';


--
-- TOC entry 583 (class 1259 OID 23120)
-- Name: image_metadata_exception_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE image_metadata_exception_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE image_metadata_exception_seq OWNER TO postgres;

--
-- TOC entry 584 (class 1259 OID 23122)
-- Name: image_metadata_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE image_metadata_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE image_metadata_seq OWNER TO postgres;

--
-- TOC entry 585 (class 1259 OID 23124)
-- Name: image_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE image_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE image_seq OWNER TO postgres;

--
-- TOC entry 586 (class 1259 OID 23126)
-- Name: import_loads_errors_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE import_loads_errors_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE import_loads_errors_seq OWNER TO postgres;

--
-- TOC entry 587 (class 1259 OID 23128)
-- Name: import_loads_jobs_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE import_loads_jobs_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE import_loads_jobs_seq OWNER TO postgres;

--
-- TOC entry 589 (class 1259 OID 23132)
-- Name: incorrect_smc_quote_dtls_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE incorrect_smc_quote_dtls_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE incorrect_smc_quote_dtls_seq OWNER TO postgres;

--
-- TOC entry 588 (class 1259 OID 23130)
-- Name: incorrect_smc_quotes_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE incorrect_smc_quotes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE incorrect_smc_quotes_seq OWNER TO postgres;

--
-- TOC entry 590 (class 1259 OID 23134)
-- Name: inqa_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE inqa_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE inqa_seq OWNER TO postgres;

--
-- TOC entry 591 (class 1259 OID 23136)
-- Name: insurers_syn_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE insurers_syn_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE insurers_syn_seq OWNER TO postgres;

--
-- TOC entry 287 (class 1259 OID 18618)
-- Name: int_audit; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE int_audit (
    audit_id bigint NOT NULL,
    bol character varying(25),
    inb_otb character varying(1) NOT NULL,
    load_id bigint,
    message_type character varying(30) NOT NULL,
    scac character varying(4),
    shipment_num character varying(30),
    shipper_org_id bigint,
    viewed_by bigint,
    viewed_date timestamp without time zone,
    created_by bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    modified_by bigint,
    date_modified timestamp without time zone,
    version integer NOT NULL,
    status character varying(10),
    CONSTRAINT check_io CHECK (((inb_otb)::text = ANY ((ARRAY['I'::character varying, 'O'::character varying])::text[])))
);


ALTER TABLE int_audit OWNER TO postgres;

--
-- TOC entry 288 (class 1259 OID 18624)
-- Name: int_audit_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE int_audit_details (
    audit_detail_id bigint NOT NULL,
    audit_id bigint,
    message text
);


ALTER TABLE int_audit_details OWNER TO postgres;

--
-- TOC entry 592 (class 1259 OID 23138)
-- Name: int_audit_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE int_audit_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE int_audit_details_seq OWNER TO postgres;

--
-- TOC entry 593 (class 1259 OID 23140)
-- Name: int_audit_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE int_audit_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE int_audit_seq OWNER TO postgres;

--
-- TOC entry 594 (class 1259 OID 23142)
-- Name: invoice_group_load_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE invoice_group_load_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE invoice_group_load_seq OWNER TO postgres;

--
-- TOC entry 595 (class 1259 OID 23144)
-- Name: invoice_group_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE invoice_group_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE invoice_group_seq OWNER TO postgres;

--
-- TOC entry 289 (class 1259 OID 18633)
-- Name: invoice_history; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE invoice_history (
    invoice_history_id bigint NOT NULL,
    invoice_id bigint NOT NULL,
    invoice_type character varying(20) NOT NULL,
    load_id bigint,
    faa_detail_id bigint,
    billing_status_reason_code character varying(4),
    release_status character varying(1) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL
);


ALTER TABLE invoice_history OWNER TO postgres;

--
-- TOC entry 6298 (class 0 OID 0)
-- Dependencies: 289
-- Name: TABLE invoice_history; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON TABLE invoice_history IS 'Link between invoices, loads and  adjustments ';


--
-- TOC entry 6299 (class 0 OID 0)
-- Dependencies: 289
-- Name: COLUMN invoice_history.load_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN invoice_history.load_id IS 'load_id (null in case of adjustment)';


--
-- TOC entry 6300 (class 0 OID 0)
-- Dependencies: 289
-- Name: COLUMN invoice_history.faa_detail_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN invoice_history.faa_detail_id IS 'adjustment_id (null in case of load)';


--
-- TOC entry 6301 (class 0 OID 0)
-- Dependencies: 289
-- Name: COLUMN invoice_history.billing_status_reason_code; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN invoice_history.billing_status_reason_code IS 'link to billing_status_reason_codes table';


--
-- TOC entry 6302 (class 0 OID 0)
-- Dependencies: 289
-- Name: COLUMN invoice_history.release_status; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN invoice_history.release_status IS 'S(Success), F(Failure), C(Cancelled), R - Reprocess';


--
-- TOC entry 596 (class 1259 OID 23146)
-- Name: invoice_history_inv_id_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE invoice_history_inv_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE invoice_history_inv_id_seq OWNER TO postgres;

--
-- TOC entry 597 (class 1259 OID 23148)
-- Name: invoice_history_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE invoice_history_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE invoice_history_seq OWNER TO postgres;

--
-- TOC entry 598 (class 1259 OID 23150)
-- Name: invoice_metadata_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE invoice_metadata_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE invoice_metadata_seq OWNER TO postgres;

--
-- TOC entry 600 (class 1259 OID 23154)
-- Name: invoice_reason_links_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE invoice_reason_links_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE invoice_reason_links_seq OWNER TO postgres;

--
-- TOC entry 599 (class 1259 OID 23152)
-- Name: invoice_reasons_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE invoice_reasons_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE invoice_reasons_seq OWNER TO postgres;

--
-- TOC entry 290 (class 1259 OID 18641)
-- Name: invoice_settings; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE invoice_settings (
    invoice_settings_id bigint NOT NULL,
    bill_to_id bigint NOT NULL,
    invoice_type character varying(20),
    invoice_format_id bigint,
    processing_type character varying(20),
    processing_time integer,
    date_created timestamp without time zone NOT NULL,
    created_by bigint,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint,
    version bigint NOT NULL,
    processing_time_tz real,
    processing_period character varying(20),
    gainshare_only character(1) DEFAULT 'N'::bpchar,
    sort_type character varying(20),
    documents character varying(64),
    not_split_recipients character(1) DEFAULT 'N'::bpchar NOT NULL,
    edi_invoice character varying(1) DEFAULT 'N'::character varying,
    cbi_invoice_type character varying(3) DEFAULT 'PLS'::character varying,
    release_day_of_week smallint,
    processing_day_of_week smallint,
    no_invoice_document character(1) DEFAULT 'N'::bpchar
);


ALTER TABLE invoice_settings OWNER TO postgres;

--
-- TOC entry 601 (class 1259 OID 23156)
-- Name: invoice_settings_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE invoice_settings_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE invoice_settings_seq OWNER TO postgres;

--
-- TOC entry 602 (class 1259 OID 23158)
-- Name: isa_num_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE isa_num_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE isa_num_seq OWNER TO postgres;

--
-- TOC entry 603 (class 1259 OID 23160)
-- Name: iso_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE iso_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE iso_seq OWNER TO postgres;

--
-- TOC entry 604 (class 1259 OID 23162)
-- Name: isr_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE isr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE isr_seq OWNER TO postgres;

--
-- TOC entry 607 (class 1259 OID 23168)
-- Name: lan_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lan_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lan_seq OWNER TO postgres;

--
-- TOC entry 605 (class 1259 OID 23164)
-- Name: lane_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lane_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lane_seq OWNER TO postgres;

--
-- TOC entry 606 (class 1259 OID 23166)
-- Name: lanm_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lanm_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lanm_seq OWNER TO postgres;

--
-- TOC entry 608 (class 1259 OID 23170)
-- Name: lby_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lby_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lby_seq OWNER TO postgres;

--
-- TOC entry 293 (class 1259 OID 18674)
-- Name: ld_bill_audit_reason_codes; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ld_bill_audit_reason_codes (
    reason_cd character varying(2) NOT NULL,
    description character varying(200),
    is_comment_req character(1) DEFAULT 'N'::bpchar,
    reason_type character(1),
    CONSTRAINT is_comment_req_chk CHECK ((is_comment_req = ANY (ARRAY['N'::bpchar, 'Y'::bpchar]))),
    CONSTRAINT reas_type_chk CHECK ((reason_type = ANY (ARRAY['A'::bpchar, 'I'::bpchar, 'P'::bpchar, 'R'::bpchar])))
);


ALTER TABLE ld_bill_audit_reason_codes OWNER TO postgres;

--
-- TOC entry 292 (class 1259 OID 18663)
-- Name: ld_billing_audit_reasons; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ld_billing_audit_reasons (
    ld_bill_audit_rsn_id bigint NOT NULL,
    load_id bigint NOT NULL,
    faa_detail_id bigint,
    reason_cd character varying(2) NOT NULL,
    comments character varying(200),
    status character(1),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    CONSTRAINT sys_c009167315 CHECK ((status = ANY (ARRAY['A'::bpchar, 'I'::bpchar]))),
    CONSTRAINT sys_c009167317 CHECK ((status = ANY (ARRAY['A'::bpchar, 'I'::bpchar]))),
    CONSTRAINT sys_c009167318 CHECK ((status = ANY (ARRAY['A'::bpchar, 'I'::bpchar])))
);


ALTER TABLE ld_billing_audit_reasons OWNER TO postgres;

--
-- TOC entry 610 (class 1259 OID 23174)
-- Name: ld_billing_audit_reasons_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ld_billing_audit_reasons_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ld_billing_audit_reasons_seq OWNER TO postgres;

--
-- TOC entry 611 (class 1259 OID 23176)
-- Name: ld_driver_notes_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ld_driver_notes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ld_driver_notes_seq OWNER TO postgres;

--
-- TOC entry 612 (class 1259 OID 23178)
-- Name: ld_geo_track_req_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ld_geo_track_req_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ld_geo_track_req_seq OWNER TO postgres;

--
-- TOC entry 613 (class 1259 OID 23180)
-- Name: ld_geo_track_updates_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ld_geo_track_updates_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ld_geo_track_updates_seq OWNER TO postgres;

--
-- TOC entry 857 (class 1259 OID 24824)
-- Name: ld_inv_audit_info; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ld_inv_audit_info (
    ld_inv_audit_info_id bigint NOT NULL,
    load_id bigint NOT NULL,
    rebill_audit_type character varying(10) NOT NULL,
    rebill_assignee character varying(10) NOT NULL,
    status character varying(1) NOT NULL,
    date_created date NOT NULL,
    created_by bigint NOT NULL,
    date_modified date,
    modified_by bigint,
    version bigint NOT NULL
);


ALTER TABLE ld_inv_audit_info OWNER TO postgres; 
--
-- TOC entry 614 (class 1259 OID 23182)
-- Name: ld_inv_audit_info_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ld_inv_audit_info_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ld_inv_audit_info_seq OWNER TO postgres;

--
-- TOC entry 609 (class 1259 OID 23172)
-- Name: lds_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lds_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lds_seq OWNER TO postgres;

--
-- TOC entry 615 (class 1259 OID 23184)
-- Name: lfh_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lfh_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lfh_seq OWNER TO postgres;

--
-- TOC entry 616 (class 1259 OID 23186)
-- Name: lgev_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lgev_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lgev_seq OWNER TO postgres;

--
-- TOC entry 617 (class 1259 OID 23188)
-- Name: lh_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lh_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lh_seq OWNER TO postgres;

--
-- TOC entry 618 (class 1259 OID 23190)
-- Name: lin_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lin_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lin_seq OWNER TO postgres;

--
-- TOC entry 619 (class 1259 OID 23192)
-- Name: llc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE llc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE llc_seq OWNER TO postgres;

--
-- TOC entry 298 (class 1259 OID 18814)
-- Name: load_additional_fields; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_additional_fields (
    load_additional_fields_id bigint NOT NULL,
    cargo_value numeric(10,2),
    version integer DEFAULT 1 NOT NULL,
    manual_bol_id bigint,
    load_id bigint,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL
);


ALTER TABLE load_additional_fields OWNER TO postgres;

--
-- TOC entry 622 (class 1259 OID 23198)
-- Name: load_additional_fields_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_additional_fields_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_additional_fields_seq OWNER TO postgres;

--
-- TOC entry 299 (class 1259 OID 18824)
-- Name: load_addl_info; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_addl_info (
    load_addl_info_id bigint NOT NULL,
    load_id bigint NOT NULL,
    resp_to_tender_by timestamp without time zone,
    resp_to_tender_by_tz real,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    markup smallint,
    tender_accepted_date timestamp without time zone,
    tender_rejected_date timestamp without time zone,
    enroute_to_del timestamp without time zone,
    enr_to_del_address_id bigint,
    reason_code character varying(2)
);


ALTER TABLE load_addl_info OWNER TO postgres;

--
-- TOC entry 623 (class 1259 OID 23200)
-- Name: load_addl_info_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_addl_info_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_addl_info_seq OWNER TO postgres;

--
-- TOC entry 300 (class 1259 OID 18831)
-- Name: load_billing_history; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_billing_history (
    history_id bigint NOT NULL,
    load_id bigint NOT NULL,
    old_finan_status character varying(5) NOT NULL,
    new_finan_status character varying(5) NOT NULL,
    cost_detail_id bigint,
    status_reason character varying(4) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    faa_detail_id bigint,
    request_id bigint
);


ALTER TABLE load_billing_history OWNER TO postgres;

--
-- TOC entry 301 (class 1259 OID 18840)
-- Name: load_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_details (
    load_detail_id bigint NOT NULL,
    load_id bigint NOT NULL,
    load_action character varying(1) NOT NULL,
    ticket character varying(50),
    bol character varying(25),
    arrival timestamp without time zone,
    departure timestamp without time zone,
    scheduled_arrival timestamp without time zone,
    need_appt character varying(1),
    contact character varying(100),
    instructions character varying(2000),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    po_num character varying(50),
    node_id character varying(50),
    op_bol character varying(20),
    point_type character(1),
    admit_date timestamp without time zone,
    no_early_schedule_reason character varying(3),
    part_num character varying(2000),
    sdsa_flag character(1) DEFAULT 'Y'::bpchar NOT NULL,
    seq_in_route smallint NOT NULL,
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
    contact_phone character varying(30),
    early_sdsa_flag character(1) DEFAULT 'Y'::bpchar,
    loaded_date timestamp without time zone,
    loaded_date_tz real DEFAULT 0,
    version integer DEFAULT 1 NOT NULL,
    arrival_window_start timestamp without time zone,
    arrival_window_end timestamp without time zone,
    contact_fax character varying(20),
    contact_email character varying(100),
    address_code character varying(50),
    notes character varying(3000),
    scheduled_date timestamp without time zone,
    scheduled_date_tz real,
    location_type character varying(255),
    int_notes character varying(3000)
);


ALTER TABLE load_details OWNER TO postgres;

--
-- TOC entry 6303 (class 0 OID 0)
-- Dependencies: 301
-- Name: COLUMN load_details.load_detail_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_details.load_detail_id IS 'sequence-generated primary key';


--
-- TOC entry 6304 (class 0 OID 0)
-- Dependencies: 301
-- Name: COLUMN load_details.load_action; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_details.load_action IS 'Specifies the activity of the carrier at either the origin or destination point of the ROUTE_LANE.';


--
-- TOC entry 6305 (class 0 OID 0)
-- Dependencies: 301
-- Name: COLUMN load_details.ticket; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_details.ticket IS 'An alternative to BOL for identifying materials to be dropped off at a specific point. can be used instead of BOL for verifying that the correct CARRIER is picking up the correct LOAD.  Either TICKET or SHIPPER_BOL must be populated at award of LOAD.';


--
-- TOC entry 6306 (class 0 OID 0)
-- Dependencies: 301
-- Name: COLUMN load_details.bol; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_details.bol IS 'Bill of lading associated with the LOAD_ACTION';


--
-- TOC entry 6307 (class 0 OID 0)
-- Dependencies: 301
-- Name: COLUMN load_details.arrival; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_details.arrival IS 'Full date-time denoting the CARRIER''S arrival at the ROUTE_LANE point.';


--
-- TOC entry 6308 (class 0 OID 0)
-- Dependencies: 301
-- Name: COLUMN load_details.departure; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_details.departure IS 'Full date-time denoting the CARRIER''S departure from the ROUTE_LANE point.';


--
-- TOC entry 6309 (class 0 OID 0)
-- Dependencies: 301
-- Name: COLUMN load_details.scheduled_arrival; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_details.scheduled_arrival IS 'Full date-time of scheduled appointment for CARRIER arrival at ROUTE_LANE point.';


--
-- TOC entry 6310 (class 0 OID 0)
-- Dependencies: 301
-- Name: COLUMN load_details.contact; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_details.contact IS 'Person/phone to be contacted at denoted ROUTE_LANE point.';


--
-- TOC entry 6311 (class 0 OID 0)
-- Dependencies: 301
-- Name: COLUMN load_details.arrival_window_start; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_details.arrival_window_start IS 'Same as PNET but for LTL loads';


--
-- TOC entry 6312 (class 0 OID 0)
-- Dependencies: 301
-- Name: COLUMN load_details.arrival_window_end; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_details.arrival_window_end IS 'Same as PNLT but for LTL loads';


--
-- TOC entry 624 (class 1259 OID 23202)
-- Name: load_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_details_seq OWNER TO postgres;

--
-- TOC entry 625 (class 1259 OID 23204)
-- Name: load_dispatch_info_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_dispatch_info_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_dispatch_info_seq OWNER TO postgres;

--
-- TOC entry 302 (class 1259 OID 18901)
-- Name: load_dispatch_information; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_dispatch_information (
    load_dispatch_info_id bigint NOT NULL,
    load_id bigint,
    status character(1),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint
);


ALTER TABLE load_dispatch_information OWNER TO postgres;

--
-- TOC entry 303 (class 1259 OID 18906)
-- Name: load_finalization_history; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_finalization_history (
    lfh_id bigint NOT NULL,
    load_id bigint NOT NULL,
    finalization_status character varying(5) NOT NULL,
    prev_fin_status character varying(5) NOT NULL,
    request_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    faa_detail_id bigint
);


ALTER TABLE load_finalization_history OWNER TO postgres;

--
-- TOC entry 306 (class 1259 OID 18927)
-- Name: load_generic_event_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_generic_event_types (
    event_type character varying(10) NOT NULL,
    description character varying(150) NOT NULL
);


ALTER TABLE load_generic_event_types OWNER TO postgres;

--
-- TOC entry 304 (class 1259 OID 18913)
-- Name: load_generic_events; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_generic_events (
    event_id bigint NOT NULL,
    event_type character varying(10) NOT NULL,
    load_id bigint NOT NULL,
    is_failure character(1) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL
);


ALTER TABLE load_generic_events OWNER TO postgres;

--
-- TOC entry 305 (class 1259 OID 18922)
-- Name: load_generic_events_data; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_generic_events_data (
    event_id bigint NOT NULL,
    ordinal smallint NOT NULL,
    data_type character(1) NOT NULL,
    data character varying(240) NOT NULL
);


ALTER TABLE load_generic_events_data OWNER TO postgres;

--
-- TOC entry 308 (class 1259 OID 18944)
-- Name: load_job_numbers; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_job_numbers (
    load_job_number_id bigint NOT NULL,
    load_id bigint,
    job_number character varying(30),
    percentage numeric(10,2),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer
);


ALTER TABLE load_job_numbers OWNER TO postgres;

--
-- TOC entry 626 (class 1259 OID 23206)
-- Name: load_job_numbers_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_job_numbers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_job_numbers_seq OWNER TO postgres;

--
-- TOC entry 309 (class 1259 OID 18949)
-- Name: load_materials; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_materials (
    load_material_id bigint NOT NULL,
    load_detail_id bigint,
    release_id character varying(30),
    work_order character varying(25),
    shop_order character varying(25),
    cust_owned character varying(10),
    cust_po_num character varying(30),
    part_num character varying(25),
    cust_item_num character varying(250),
    weight numeric(10,2),
    material_type character varying(100),
    pickup_instr character varying(1000),
    delivery_instr character varying(1000),
    bol character varying(25),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    order_id bigint,
    status character varying(2) DEFAULT 'S'::character varying,
    dropoff_load_detail_id bigint,
    is_ready character(1),
    heat_num character varying(30),
    pickup_date timestamp without time zone,
    pieces integer,
    length numeric(10,2),
    bundles smallint,
    grade character varying(30),
    cust_location character varying(30),
    dock_name character varying(30),
    node_id character varying(50),
    in_inventory character(1) DEFAULT 'Y'::bpchar NOT NULL,
    pickup_date_tz real,
    hazmat character(1) DEFAULT 'N'::bpchar,
    package_type character varying(30),
    width numeric(10,2),
    height numeric(10,2),
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
    version integer DEFAULT 1 NOT NULL,
    original_weight numeric(10,2),
    original_freight_class character varying(8),
    contract character varying(50),
    hazmat_instructions character varying(2000),
    emergency_contract character varying(32),
    emergency_company character varying(32),
    emergency_country_code character varying(10),
    emergency_area_code character varying(10),
    ltl_product_id bigint,
    ltl_package_type character varying(3),
    stackable character(1) DEFAULT 'N'::bpchar,
    emergency_extension character varying(6),
    CONSTRAINT avcon_1074967799_is_re_000 CHECK ((is_ready = ANY (ARRAY['N'::bpchar, 'Y'::bpchar])))
);


ALTER TABLE load_materials OWNER TO postgres;

--
-- TOC entry 6316 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.load_detail_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.load_detail_id IS 'sequence-generated primary key';


--
-- TOC entry 6317 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.release_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.release_id IS 'the shipper''s internal unique ID for each release of material contained in the load';


--
-- TOC entry 6318 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.work_order; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.work_order IS 'the shipper''s internal product ID';


--
-- TOC entry 6319 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.shop_order; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.shop_order IS 'for WO = shop order number or PO number, depending on release type';


--
-- TOC entry 6320 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.cust_owned; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.cust_owned IS 'is the material owned by the customer?';


--
-- TOC entry 6321 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.cust_po_num; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.cust_po_num IS 'the PO number issued by the shipper''s customer for this material';


--
-- TOC entry 6322 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.cust_item_num; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.cust_item_num IS 'the consignee''s internal identifier for the ordered material (not really relevant to us)';


--
-- TOC entry 6323 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.weight; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.weight IS 'the allocated weight for this material release, as a subtotal of the total load''s weight';


--
-- TOC entry 6324 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.material_type; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.material_type IS 'the physical specifications of the material release (width, gauge, type of product, etc.)';


--
-- TOC entry 6325 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.pickup_instr; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.pickup_instr IS 'although associated with the drop off load action, each material release may have it''s own pickup instructions';


--
-- TOC entry 6326 (class 0 OID 0)
-- Dependencies: 309
-- Name: COLUMN load_materials.delivery_instr; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN load_materials.delivery_instr IS 'unloading/delivery instructions for the material release';


--
-- TOC entry 627 (class 1259 OID 23208)
-- Name: load_materials_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_materials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_materials_seq OWNER TO postgres;

--
-- TOC entry 310 (class 1259 OID 18978)
-- Name: load_notifications; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_notifications (
    load_notification_id bigint NOT NULL,
    load_id bigint NOT NULL,
    notification_type character varying(32) NOT NULL,
    email_address character varying(255) NOT NULL,
    notification_source character varying(50)
);


ALTER TABLE load_notifications OWNER TO postgres;

--
-- TOC entry 628 (class 1259 OID 23210)
-- Name: load_notifications_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_notifications_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_notifications_seq OWNER TO postgres;

--
-- TOC entry 629 (class 1259 OID 23212)
-- Name: load_outbound_messages_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_outbound_messages_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_outbound_messages_seq OWNER TO postgres;

--
-- TOC entry 630 (class 1259 OID 23214)
-- Name: load_preferred_carriers_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_preferred_carriers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_preferred_carriers_seq OWNER TO postgres;

--
-- TOC entry 631 (class 1259 OID 23216)
-- Name: load_prepaid_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_prepaid_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_prepaid_details_seq OWNER TO postgres;

--
-- TOC entry 312 (class 1259 OID 18991)
-- Name: load_pric_material_dtls; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_pric_material_dtls (
    load_pric_material_dtls_id bigint NOT NULL,
    load_pricing_detail_id bigint NOT NULL,
    charge character varying(30),
    nmfc_class character varying(30),
    entered_nmfc_class character varying(30),
    rate character varying(30),
    weight character varying(30),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer,
    description character varying(1000),
    quantity character varying(30),
    nmfc character varying(30)
);


ALTER TABLE load_pric_material_dtls OWNER TO postgres;

--
-- TOC entry 633 (class 1259 OID 23220)
-- Name: load_pric_material_dtls_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_pric_material_dtls_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_pric_material_dtls_seq OWNER TO postgres;

--
-- TOC entry 311 (class 1259 OID 18986)
-- Name: load_pricing_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_pricing_details (
    load_pricing_detail_id bigint NOT NULL,
    load_id bigint NOT NULL,
    smc3_minimum_charge numeric(10,2),
    total_charge_from_smc3 numeric(10,2),
    deficit_charge_from_smc3 numeric(10,2),
    cost_after_discount numeric(10,2),
    minimum_cost numeric(10,2),
    cost_discount numeric(10,2),
    carrier_fs_id bigint,
    carrier_fuel_discount numeric(10,2),
    pricing_type character varying(30),
    movement_type character varying(30),
    effective_date timestamp without time zone,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer
);


ALTER TABLE load_pricing_details OWNER TO postgres;

--
-- TOC entry 632 (class 1259 OID 23218)
-- Name: load_pricing_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_pricing_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_pricing_details_seq OWNER TO postgres;

--
-- TOC entry 634 (class 1259 OID 23222)
-- Name: load_rev_share_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_rev_share_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_rev_share_seq OWNER TO postgres;

--
-- TOC entry 313 (class 1259 OID 18999)
-- Name: load_search_data; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_search_data (
    load_id bigint NOT NULL,
    load_status character varying(2),
    shipper_reference_number character varying(30),
    org_id bigint,
    location_id bigint,
    person_id bigint,
    network_id bigint,
    origin_region_id bigint,
    destination_region_id bigint,
    route_id bigint,
    carrier_org_id bigint,
    carrier_loc_id bigint,
    carrier_person_id bigint,
    orig_city character varying(30),
    orig_state character varying(6),
    orig_zip character varying(10),
    orig_country character varying(3),
    orig_latitude numeric(10,2),
    orig_longitude numeric(10,2),
    dest_city character varying(30),
    dest_state character varying(6),
    dest_zip character varying(10),
    dest_country character varying(3),
    dest_latitude numeric(10,2),
    dest_longitude numeric(10,2),
    orig_load_detail_id bigint,
    orig_arrival timestamp without time zone,
    orig_arrival_tz real,
    orig_departure timestamp without time zone,
    orig_departure_tz real,
    orig_scheduled_arrival timestamp without time zone,
    orig_scheduled_arrival_tz real,
    dest_load_detail_id bigint,
    dest_arrival timestamp without time zone,
    dest_arrival_tz real,
    dest_departure timestamp without time zone,
    dest_departure_tz real,
    dest_scheduled_arrival timestamp without time zone,
    dest_scheduled_arrival_tz real,
    radio_active_flag character(1),
    created_by bigint,
    radio_active_secure_flag character(1),
    carrier_reference_number character varying(30),
    carr_ref_num_check character varying(30),
    orig_bol character varying(25),
    orig_op_bol character varying(20),
    finalization_status character varying(3)
);


ALTER TABLE load_search_data OWNER TO postgres;

--
-- TOC entry 635 (class 1259 OID 23224)
-- Name: load_status_order_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_status_order_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_status_order_seq OWNER TO postgres;

--
-- TOC entry 636 (class 1259 OID 23226)
-- Name: load_status_order_seq_ord_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_status_order_seq_ord_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_status_order_seq_ord_seq OWNER TO postgres;


--
-- TOC entry 314 (class 1259 OID 19029)
-- Name: load_tracking; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_tracking (
    tracking_id bigint NOT NULL,
    load_id bigint,
    track_status_code character varying(10),
    status_reason_code character varying(10),
    track_date_time timestamp without time zone,
    track_timezone character varying(10),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    track_city character varying(200),
    track_state character varying(30),
    track_country character varying(30),
    carrier_id bigint,
    source smallint,
    addl_msg character varying(500),
    edi_account character varying(200),
    track_postal_code character varying(10),
    departure_date_time timestamp without time zone,
    carrier_pickup_confirmation character varying(20)
);


ALTER TABLE load_tracking OWNER TO postgres;

--
-- TOC entry 637 (class 1259 OID 23228)
-- Name: load_tracking_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE load_tracking_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE load_tracking_seq OWNER TO postgres;

--
-- TOC entry 315 (class 1259 OID 19041)
-- Name: load_tracking_status_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_tracking_status_types (
    track_status_code character varying(10) NOT NULL,
    description character varying(500),
    source smallint NOT NULL
);


ALTER TABLE load_tracking_status_types OWNER TO postgres;

--
-- TOC entry 316 (class 1259 OID 19049)
-- Name: load_trk_status_reason_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE load_trk_status_reason_types (
    reason_code character varying(10) NOT NULL,
    description character varying(500),
    source smallint NOT NULL
);


ALTER TABLE load_trk_status_reason_types OWNER TO postgres;

--
-- TOC entry 294 (class 1259 OID 18682)
-- Name: loads; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE loads (
    load_id bigint NOT NULL,
    route_id bigint NOT NULL,
    load_status character varying(2) NOT NULL,
    market_type character varying(2),
    commodity_cd character varying(10) NOT NULL,
    container_cd character varying(10) NOT NULL,
    org_id bigint NOT NULL,
    location_id bigint NOT NULL,
    person_id bigint NOT NULL,
    shipper_reference_number character varying(30),
    broker_reference_number character varying(20),
    inbound_outbound_flg character varying(1) DEFAULT 'O'::character varying NOT NULL,
    date_closed timestamp without time zone,
    feature_code character varying(1),
    source_ind character varying(5) NOT NULL,
    mileage numeric(10,2),
    pieces integer NOT NULL,
    weight bigint NOT NULL,
    weight_uom_code character varying(10),
    target_price numeric(10,2),
    awarded_offer bigint,
    award_price numeric(10,2),
    award_date timestamp without time zone,
    special_instructions character varying(3000),
    special_message character varying(3000),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    min_weight bigint,
    min_price numeric(10,2),
    rate_type character varying(2),
    unit_cost numeric(10,2),
    tender_cycle_id bigint,
    priority character varying(1),
    pay_terms character varying(3) DEFAULT 'PPD'::character varying NOT NULL,
    bill_to bigint,
    one_time_rate_id bigint,
    out_route_miles integer,
    auto_cd_flg character(1),
    product_type character varying(2),
    finalization_status character varying(5) DEFAULT 'NF'::character varying,
    permit_load character(1),
    pro_num character varying(50),
    travel_time integer,
    chk_brn character varying(30),
    premium_available character(1) DEFAULT 'N'::bpchar NOT NULL,
    scheduled character(1) DEFAULT 'N'::bpchar NOT NULL,
    dispatcher_schedule_only character(1) DEFAULT 'N'::bpchar NOT NULL,
    truck_weight bigint,
    gross_weight bigint,
    barge_num character varying(6),
    sc_flag character(1),
    op_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    sw_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    srr_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    srm_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    mm_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
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
    shipper_premium_available character(1) DEFAULT 'N'::bpchar NOT NULL,
    after_hours_contact character varying(50),
    after_hours_phone character varying(50),
    is_ready character(1),
    reason_code character varying(25),
    fin_status_date timestamp without time zone,
    hfr_flag character(1) DEFAULT '1'::bpchar NOT NULL,
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
    radio_active_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    customer_comments character varying(3000),
    over_height_flag character(1),
    over_length_flag character(1),
    over_width_flag character(1),
    super_load_flag character(1),
    height numeric(10,2),
    length numeric(10,2),
    width numeric(10,2),
    over_weight_flag character(1),
    notify_initial_msg_flag character(1),
    multi_dock_sched_rqrd character(1) DEFAULT 'N'::bpchar NOT NULL,
    permit_num character varying(30),
    radio_active_secure_flag character(1),
    booked character(1) DEFAULT 'N'::bpchar,
    orig_sched_pickup_date_tz real,
    orig_sched_delivery_date_tz real,
    etd_ovr_flg character(1),
    etd_date timestamp without time zone,
    etd_date_tz real,
    awarded_by bigint,
    origin_region_id bigint,
    destination_region_id bigint,
    target_rate_min numeric(10,2),
    target_rate_max numeric(10,2),
    target_rate_ovr_flg character(1) DEFAULT 'N'::bpchar NOT NULL,
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
    finan_no_load_flag character(1) DEFAULT 'N'::bpchar,
    service_level_cd character varying(3),
    award_carrier_org_id bigint,
    ship_date timestamp without time zone,
    blind_carrier character(1),
    gl_number character varying(50),
    bol character varying(25),
    po_num character varying(50),
    op_bol character varying(20),
    part_num character varying(2000),
    version integer DEFAULT 1 NOT NULL,
    gl_ref_code character varying(50),
    award_dedicated_unit_id bigint,
    frt_bill_recv_flag character(1) DEFAULT 'N'::bpchar,
    pickup_num character varying(2000),
    frt_bill_amount numeric(10,2),
    originating_system character varying(10),
    inv_ship_rates_only character varying(1),
    inv_carr_rates_only character varying(1),
    customs_broker character varying(100),
    customs_broker_phone character varying(31),
    cust_req_doc_recv_flag character(1),
    so_number character varying(50),
    frt_bill_pay_to_id bigint,
    inv_approved character(1),
    volume_quote_id character varying(50),
    saved_quote_id bigint,
    assignee_person_id bigint,
    assigned_date timestamp without time zone,
    carrier_sales character(1),
    driver_id bigint,
    CONSTRAINT avcon_1063995804_pay_t_000 CHECK (((pay_terms)::text = ANY ((ARRAY['TPD'::character varying, 'PPD'::character varying, 'TPL'::character varying, 'COL'::character varying])::text[]))),
    CONSTRAINT avcon_1074967799_permi_000 CHECK ((permit_load = ANY (ARRAY['N'::bpchar, 'Y'::bpchar]))),
    CONSTRAINT avcon_1142700800_shipp_000 CHECK ((shipper_premium_available = ANY (ARRAY['N'::bpchar, 'Y'::bpchar]))),
    CONSTRAINT avcon_1196267923_booke_000 CHECK ((booked = ANY (ARRAY['N'::bpchar, 'Y'::bpchar])))
);


ALTER TABLE loads OWNER TO postgres;

--
-- TOC entry 6329 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.load_status; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.load_status IS 'Code describing the current state of a LOAD.';


--
-- TOC entry 6330 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.commodity_cd; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.commodity_cd IS 'SCTG level 4 code for type of goods being transported by the LOAD';


--
-- TOC entry 6331 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.container_cd; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.container_cd IS 'Code describing transport containers (i.e. -- flatbed trailer types)';


--
-- TOC entry 6332 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.shipper_reference_number; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.shipper_reference_number IS 'SHIPPER''S Internal Load/Shipment Identifier';


--
-- TOC entry 6333 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.broker_reference_number; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.broker_reference_number IS 'If the LOAD is being brokered, this is the BROKER''s identifier.';


--
-- TOC entry 6334 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.date_closed; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.date_closed IS 'Actual Date/time the bid was closed.  (this may be earlier than the bid close date)';


--
-- TOC entry 6335 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.source_ind; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.source_ind IS 'Code indicating the original source of the LOAD''s entry into the EFLATBED system.  SYS = entered using the EFLATBED application;  EDI = entered from the XML translator interface (i.e. -- external origin)';


--
-- TOC entry 6336 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.mileage; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.mileage IS 'Total distance of haul';


--
-- TOC entry 6337 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.pieces; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.pieces IS 'Number of pieces in the load';


--
-- TOC entry 6338 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.weight; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.weight IS 'Weight';


--
-- TOC entry 6339 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.weight_uom_code; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.weight_uom_code IS 'Weight unit of measure code';


--
-- TOC entry 6340 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.target_price; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.target_price IS 'Target price established by the SHIPPER for carrying the LOAD.';


--
-- TOC entry 6341 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.award_price; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.award_price IS 'Price awarded to CARRIER who will carry the LOAD.';


--
-- TOC entry 6342 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.award_date; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.award_date IS 'Date on which the LOAD was awarded to the CARRIER.';


--
-- TOC entry 6343 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.priority; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.priority IS 'Indicator to flag if the load is  NORMAL,MEDIUM, HIGH (C,N,M)';


--
-- TOC entry 6344 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.pay_terms; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.pay_terms IS 'Accounting indicator, determining how carrier rating and payment should occur.';


--
-- TOC entry 6345 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.fin_status_date; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.fin_status_date IS 'The date the load finalization status was changed.';


--
-- TOC entry 6346 (class 0 OID 0)
-- Dependencies: 294
-- Name: COLUMN loads.carrier_sales; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN loads.carrier_sales IS 'C or S flag';

--
-- TOC entry 297 (class 1259 OID 18808)
-- Name: loads_ltl_accessorials; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE loads_ltl_accessorials (
    ltl_accessorial_id bigint NOT NULL,
    load_id bigint NOT NULL,
    ref_type character varying(10)
);


ALTER TABLE loads_ltl_accessorials OWNER TO postgres;

--
-- TOC entry 6347 (class 0 OID 0)
-- Dependencies: 297
-- Name: TABLE loads_ltl_accessorials; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON TABLE loads_ltl_accessorials IS 'Stores the accessorials selected for LTL Load.';


--
-- TOC entry 620 (class 1259 OID 23194)
-- Name: loads_ltl_accessorials_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE loads_ltl_accessorials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE loads_ltl_accessorials_seq OWNER TO postgres;

--
-- TOC entry 621 (class 1259 OID 23196)
-- Name: loads_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE loads_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE loads_seq OWNER TO postgres;

--
-- TOC entry 638 (class 1259 OID 23230)
-- Name: loc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE loc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE loc_seq OWNER TO postgres;

--
-- TOC entry 639 (class 1259 OID 23232)
-- Name: lodc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lodc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lodc_seq OWNER TO postgres;

--
-- TOC entry 640 (class 1259 OID 23234)
-- Name: lodeq_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lodeq_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lodeq_seq OWNER TO postgres;

--
-- TOC entry 317 (class 1259 OID 19057)
-- Name: lookup_value; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE lookup_value (
    lookup_value_id bigint NOT NULL,
    lookup_value character varying(20) NOT NULL,
    description character varying(50) NOT NULL,
    lookup_group character varying(20) NOT NULL,
    lookup_value_order smallint
);


ALTER TABLE lookup_value OWNER TO postgres;

--
-- TOC entry 641 (class 1259 OID 23236)
-- Name: lookup_value_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lookup_value_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lookup_value_seq OWNER TO postgres;



CREATE TABLE flatbed.lost_sav_opp_rpt_data
(
    lso_rpt_data_id numeric(30,0) NOT NULL,
    job_id bigint NOT NULL,
    load_id bigint NOT NULL,
    bol character varying(25) COLLATE pg_catalog."default",
    po_num character varying(50) COLLATE pg_catalog."default",
    so_number character varying(50) COLLATE pg_catalog."default",
    shipper_reference_number character varying(30) COLLATE pg_catalog."default",
    est_pickup_date timestamp without time zone NOT NULL,
    pickup_date timestamp without time zone,
    shipper_org_id bigint NOT NULL,
    carrier_org_id bigint NOT NULL,
    shipper_name character varying(100) COLLATE pg_catalog."default",
    orig_city character varying(30) COLLATE pg_catalog."default",
    orig_state character varying(6) COLLATE pg_catalog."default",
    orig_zip character varying(10) COLLATE pg_catalog."default" NOT NULL,
    orig_country character varying(3) COLLATE pg_catalog."default",
    consignee_name character varying(100) COLLATE pg_catalog."default",
    dest_city character varying(30) COLLATE pg_catalog."default",
    dest_state character varying(6) COLLATE pg_catalog."default",
    dest_zip character varying(10) COLLATE pg_catalog."default" NOT NULL,
    dest_country character varying(3) COLLATE pg_catalog."default",
    guaranteed_time bigint,
    hazmat_flag character varying(1) COLLATE pg_catalog."default",
    total_weight numeric(10,2),
    total_revenue numeric(10,2),
    total_cost numeric(10,2),
    transit_time integer,
    service_type character varying(10) COLLATE pg_catalog."default",
    load_created_by bigint NOT NULL,
    load_created_date timestamp without time zone NOT NULL,
    lc_carrier_org_id bigint,
    lc_carr_total_revenue numeric(10,2),
    lc_carr_total_cost numeric(10,2),
    lc_carr_transit_time integer,
    lc_carr_service_type character varying(10) COLLATE pg_catalog."default",
    revenue_savings numeric(10,2),
    rev_savings_perc real,
    date_created timestamp without time zone NOT NULL
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE flatbed.lost_sav_opp_rpt_data OWNER to postgres;

--
-- TOC entry 320 (class 1259 OID 19075)
-- Name: lost_sav_opp_rpt_materials; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE lost_sav_opp_rpt_materials (
    lso_rpt_material_id numeric(30,0) NOT NULL,
    lso_rpt_data_id numeric(30,0) NOT NULL,
    weight double precision NOT NULL,
    commodity_class_code character varying(8) NOT NULL,
    hazmat_flag character varying(1),
    date_created timestamp without time zone,
    part_description character varying(1000)
);


ALTER TABLE lost_sav_opp_rpt_materials OWNER TO postgres;

--
-- TOC entry 318 (class 1259 OID 19062)
-- Name: lost_sav_opp_rpt_acc; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE lost_sav_opp_rpt_acc (
    lso_rpt_accessorial_id numeric(30,0) NOT NULL,
    lso_rpt_data_id numeric(30,0) NOT NULL,
    accessorial_type character varying(10) NOT NULL,
    date_created timestamp without time zone NOT NULL
);


ALTER TABLE lost_sav_opp_rpt_acc OWNER TO postgres;

--
-- TOC entry 642 (class 1259 OID 23238)
-- Name: lost_sav_opp_rpt_job_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lost_sav_opp_rpt_job_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lost_sav_opp_rpt_job_seq OWNER TO postgres;

--
-- TOC entry 643 (class 1259 OID 23240)
-- Name: lso_rpt_accessorial_id_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lso_rpt_accessorial_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lso_rpt_accessorial_id_seq OWNER TO postgres;

--
-- TOC entry 644 (class 1259 OID 23242)
-- Name: lso_rpt_data_id_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lso_rpt_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lso_rpt_data_id_seq OWNER TO postgres;

--
-- TOC entry 645 (class 1259 OID 23244)
-- Name: lso_rpt_material_id_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lso_rpt_material_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lso_rpt_material_id_seq OWNER TO postgres;

--
-- TOC entry 695 (class 1259 OID 23344)
-- Name: lt_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE lt_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lt_seq OWNER TO postgres;

--
-- TOC entry 647 (class 1259 OID 23248)
-- Name: ltl_acc_geo_serv_dtl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_acc_geo_serv_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_acc_geo_serv_dtl_seq OWNER TO postgres;

--
-- TOC entry 323 (class 1259 OID 19104)
-- Name: ltl_acc_geo_serv_dtls; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_acc_geo_serv_dtls (
    ltl_acc_geo_serv_dtl_id numeric(30,0),
    ltl_acc_geo_service_id numeric(30,0),
    geo_value character varying(100),
    geo_type smallint,
    geo_serv_type smallint,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer,
    searchable_geo_value character varying(100) NOT NULL
);


ALTER TABLE ltl_acc_geo_serv_dtls OWNER TO postgres;

--
-- TOC entry 648 (class 1259 OID 23250)
-- Name: ltl_acc_geo_serv_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_acc_geo_serv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_acc_geo_serv_seq OWNER TO postgres;

--
-- TOC entry 322 (class 1259 OID 19095)
-- Name: ltl_acc_geo_services; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_acc_geo_services (
    ltl_acc_geo_service_id bigint NOT NULL,
    ltl_accessorial_id bigint NOT NULL,
    origin character varying(3000),
    destination character varying(3000),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE ltl_acc_geo_services OWNER TO postgres;

--
-- TOC entry 321 (class 1259 OID 19083)
-- Name: ltl_accessorials; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_accessorials (
    ltl_accessorial_id bigint NOT NULL,
    ltl_pric_prof_detail_id bigint NOT NULL,
    accessorial_type character varying(30) NOT NULL,
    blocked character varying(1) DEFAULT 'N'::character varying NOT NULL,
    cost_type character varying(30),
    unit_cost numeric(10,2),
    cost_appl_min_wt bigint,
    cost_appl_max_wt bigint,
    cost_appl_wt_uom character varying(2),
    cost_appl_min_dist bigint,
    cost_appl_max_dist bigint,
    cost_appl_dist_uom character varying(2),
    margin_type character varying(30),
    unit_margin numeric(10,2),
    margin_percent numeric(10,2),
    margin_dollar_amt numeric(10,2),
    eff_date timestamp without time zone,
    exp_date timestamp without time zone,
    notes character varying(500),
    status character varying(1) DEFAULT 'A'::character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    copied_from bigint,
    min_cost numeric(10,2),
    max_cost numeric(10,2),
    movement_type character varying(30),
    service_type character varying(30),
    ext_notes character varying(2000),
    int_notes character varying(2000),
    auto_calculate smallint,
    cost_appl_min_length numeric(10,2),
    cost_appl_max_length numeric(10,2),
    cost_appl_length_uom character varying(2),
    apply_before_fuel character varying(1)
);


ALTER TABLE ltl_accessorials OWNER TO postgres;

--
-- TOC entry 646 (class 1259 OID 23246)
-- Name: ltl_accessorials_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_accessorials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_accessorials_seq OWNER TO postgres;

--
-- TOC entry 649 (class 1259 OID 23252)
-- Name: ltl_bk_carr_geo_serv_dtl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_bk_carr_geo_serv_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_bk_carr_geo_serv_dtl_seq OWNER TO postgres;

--
-- TOC entry 324 (class 1259 OID 19108)
-- Name: ltl_bk_carr_geo_serv_dtls; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_bk_carr_geo_serv_dtls (
    ltl_bk_carr_geo_serv_dtl_id numeric(30,0),
    ltl_block_carr_geo_service_id numeric(30,0),
    geo_value character varying(100),
    geo_type smallint,
    geo_serv_type smallint,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer,
    searchable_geo_value character varying(100)
);


ALTER TABLE ltl_bk_carr_geo_serv_dtls OWNER TO postgres;

--
-- TOC entry 650 (class 1259 OID 23254)
-- Name: ltl_bk_lane_geo_serv_dtl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_bk_lane_geo_serv_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_bk_lane_geo_serv_dtl_seq OWNER TO postgres;

--
-- TOC entry 325 (class 1259 OID 19113)
-- Name: ltl_bk_lane_geo_serv_dtls; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_bk_lane_geo_serv_dtls (
    ltl_bk_lane_geo_serv_dtl_id numeric(30,0) NOT NULL,
    ltl_block_lane_id bigint NOT NULL,
    geo_value character varying(100) NOT NULL,
    geo_type smallint NOT NULL,
    geo_serv_type smallint NOT NULL,
    searchable_geo_value character varying(100),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE ltl_bk_lane_geo_serv_dtls OWNER TO postgres;

--
-- TOC entry 326 (class 1259 OID 19119)
-- Name: ltl_block_carr_geo_services; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_block_carr_geo_services (
    ltl_block_carr_geo_service_id bigint NOT NULL,
    ltl_pric_prof_detail_id bigint NOT NULL,
    origin character varying(2000),
    destination character varying(2000),
    status character varying(1) DEFAULT 'A'::character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    copied_from bigint,
    notes character varying(500)
);


ALTER TABLE ltl_block_carr_geo_services OWNER TO postgres;

--
-- TOC entry 651 (class 1259 OID 23256)
-- Name: ltl_block_carr_geo_srv_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_block_carr_geo_srv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_block_carr_geo_srv_seq OWNER TO postgres;

--
-- TOC entry 652 (class 1259 OID 23258)
-- Name: ltl_block_lane_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_block_lane_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_block_lane_seq OWNER TO postgres;

--
-- TOC entry 327 (class 1259 OID 19130)
-- Name: ltl_carrier_liabilities; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_carrier_liabilities (
    ltl_carrier_liability_id bigint NOT NULL,
    ltl_pricing_profile_id bigint NOT NULL,
    class character varying(10),
    new_prod_liab_amt numeric(10,2),
    used_prod_liab_amt numeric(10,2),
    max_new_prod_liab_amt numeric(10,2),
    max_used_prod_liab_amt numeric(10,2),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE ltl_carrier_liabilities OWNER TO postgres;

--
-- TOC entry 653 (class 1259 OID 23260)
-- Name: ltl_carrier_liabilities_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_carrier_liabilities_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_carrier_liabilities_seq OWNER TO postgres;

--
-- TOC entry 654 (class 1259 OID 23262)
-- Name: ltl_cust_hide_pric_det_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_cust_hide_pric_det_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_cust_hide_pric_det_seq OWNER TO postgres;

--
-- TOC entry 328 (class 1259 OID 19137)
-- Name: ltl_cust_hide_pric_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_cust_hide_pric_details (
    cust_hide_pric_detail_id bigint NOT NULL,
    ltl_pricing_profile_id bigint NOT NULL,
    shipper_org_id bigint NOT NULL,
    status character varying(1) NOT NULL,
    version integer NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint
);


ALTER TABLE ltl_cust_hide_pric_details OWNER TO postgres;

--
-- TOC entry 655 (class 1259 OID 23264)
-- Name: ltl_cust_pp_override_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_cust_pp_override_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_cust_pp_override_seq OWNER TO postgres;

--
-- TOC entry 329 (class 1259 OID 19147)
-- Name: ltl_fak_map; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_fak_map (
    ltl_fak_map_id bigint NOT NULL,
    ltl_pricing_detail_id bigint NOT NULL,
    actual_class character varying(30) NOT NULL,
    mapping_class character varying(30) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE ltl_fak_map OWNER TO postgres;

--
-- TOC entry 656 (class 1259 OID 23266)
-- Name: ltl_fak_map_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_fak_map_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_fak_map_seq OWNER TO postgres;

--
-- TOC entry 330 (class 1259 OID 19154)
-- Name: ltl_fuel; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_fuel (
    ltl_fuel_id bigint NOT NULL,
    ltl_pric_prof_detail_id bigint NOT NULL,
    dot_region_id bigint,
    eff_day character varying(10),
    upcharge_type character varying(2),
    upcharge_flat numeric(10,2),
    upcharge_percent numeric(10,2),
    eff_date timestamp without time zone,
    exp_date timestamp without time zone,
    status character varying(1),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    copied_from bigint
);


ALTER TABLE ltl_fuel OWNER TO postgres;

--
-- TOC entry 658 (class 1259 OID 23270)
-- Name: ltl_fuel_geo_serv_dtl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_fuel_geo_serv_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_fuel_geo_serv_dtl_seq OWNER TO postgres;

--
-- TOC entry 332 (class 1259 OID 19170)
-- Name: ltl_fuel_geo_serv_dtls; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_fuel_geo_serv_dtls (
    ltl_fuel_geo_serv_dtl_id numeric(30,0),
    ltl_fuel_geo_service_id numeric(30,0),
    geo_value character varying(100),
    geo_type smallint,
    geo_serv_type smallint,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer,
    searchable_geo_value character varying(100)
);


ALTER TABLE ltl_fuel_geo_serv_dtls OWNER TO postgres;

--
-- TOC entry 331 (class 1259 OID 19161)
-- Name: ltl_fuel_geo_services; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_fuel_geo_services (
    ltl_fuel_geo_service_id bigint NOT NULL,
    ltl_fuel_id bigint NOT NULL,
    origin character varying(500),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE ltl_fuel_geo_services OWNER TO postgres;

--
-- TOC entry 657 (class 1259 OID 23268)
-- Name: ltl_fuel_geo_services_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_fuel_geo_services_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_fuel_geo_services_seq OWNER TO postgres;

--
-- TOC entry 659 (class 1259 OID 23272)
-- Name: ltl_fuel_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_fuel_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_fuel_seq OWNER TO postgres;

--
-- TOC entry 333 (class 1259 OID 19173)
-- Name: ltl_fuel_surcharge; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_fuel_surcharge (
    ltl_fuel_surcharge_id bigint NOT NULL,
    ltl_pric_prof_detail_id bigint NOT NULL,
    min_rate numeric(10,2),
    max_rate numeric(10,2),
    surcharge numeric(10,2),
    status character varying(1),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    copied_from bigint
);


ALTER TABLE ltl_fuel_surcharge OWNER TO postgres;

--
-- TOC entry 660 (class 1259 OID 23274)
-- Name: ltl_fuel_surcharge_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_fuel_surcharge_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_fuel_surcharge_seq OWNER TO postgres;

--
-- TOC entry 661 (class 1259 OID 23276)
-- Name: ltl_gainshare_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_gainshare_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_gainshare_details_seq OWNER TO postgres;

--
-- TOC entry 663 (class 1259 OID 23280)
-- Name: ltl_guaran_bk_dest_dtl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_guaran_bk_dest_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_guaran_bk_dest_dtl_seq OWNER TO postgres;

--
-- TOC entry 335 (class 1259 OID 19190)
-- Name: ltl_guaran_block_dest; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_guaran_block_dest (
    ltl_guaran_block_dest_id bigint NOT NULL,
    ltl_guaranteed_price_id bigint NOT NULL,
    origin character varying(500),
    destination character varying(500),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE ltl_guaran_block_dest OWNER TO postgres;

--
-- TOC entry 336 (class 1259 OID 19199)
-- Name: ltl_guaran_block_dest_dtls; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_guaran_block_dest_dtls (
    ltl_guaran_bk_dest_dtl_id numeric(30,0),
    ltl_guaran_block_dest_id numeric(30,0),
    geo_value character varying(100),
    geo_type smallint,
    geo_serv_type smallint,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer,
    searchable_geo_value character varying(100)
);


ALTER TABLE ltl_guaran_block_dest_dtls OWNER TO postgres;

--
-- TOC entry 664 (class 1259 OID 23282)
-- Name: ltl_guaran_block_dest_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_guaran_block_dest_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_guaran_block_dest_seq OWNER TO postgres;

--
-- TOC entry 334 (class 1259 OID 19179)
-- Name: ltl_guaranteed_price; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_guaranteed_price (
    ltl_guaranteed_price_id bigint NOT NULL,
    ltl_pric_prof_detail_id bigint NOT NULL,
    apply_before_fuel character varying(1) DEFAULT 'N'::character varying,
    bol_carrier_name character varying(250),
    charge_rule_type character varying(3),
    unit_cost numeric(10,2),
    min_cost numeric(10,2),
    unit_margin numeric(10,2),
    min_margin numeric(10,2),
    "time" integer,
    eff_date timestamp without time zone,
    exp_date timestamp without time zone,
    status character varying(1) DEFAULT 'A'::character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    copied_from bigint,
    max_cost numeric(10,2),
    movement_type character varying(30),
    cost_appl_min_wt bigint,
    cost_appl_max_wt bigint,
    cost_appl_wt_uom character varying(2),
    cost_appl_min_dist bigint,
    cost_appl_max_dist bigint,
    cost_appl_dist_uom character varying(2),
    service_type character varying(30),
    ext_notes character varying(2000),
    int_notes character varying(2000)
);


ALTER TABLE ltl_guaranteed_price OWNER TO postgres;

--
-- TOC entry 662 (class 1259 OID 23278)
-- Name: ltl_guaranteed_price_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_guaranteed_price_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_guaranteed_price_seq OWNER TO postgres;

--
-- TOC entry 337 (class 1259 OID 19203)
-- Name: ltl_lookup_value; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_lookup_value (
    ltl_lookup_value_id bigint NOT NULL,
    ltl_lookup_value character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    ltl_lookup_group character varying(255) NOT NULL,
    ltl_lookup_value_order integer
);


ALTER TABLE ltl_lookup_value OWNER TO postgres;

--
-- TOC entry 665 (class 1259 OID 23284)
-- Name: ltl_lookup_value_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_lookup_value_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_lookup_value_seq OWNER TO postgres;

--
-- TOC entry 666 (class 1259 OID 23286)
-- Name: ltl_lsor_addr_crit_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_lsor_addr_crit_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_lsor_addr_crit_seq OWNER TO postgres;

--
-- TOC entry 667 (class 1259 OID 23288)
-- Name: ltl_lsor_crit_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_lsor_crit_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_lsor_crit_seq OWNER TO postgres;

--
-- TOC entry 668 (class 1259 OID 23290)
-- Name: ltl_lsor_materials_crit_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_lsor_materials_crit_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_lsor_materials_crit_seq OWNER TO postgres;

--
-- TOC entry 338 (class 1259 OID 19214)
-- Name: ltl_org_block_lane; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_org_block_lane (
    ltl_block_lane_id bigint NOT NULL,
    carrier_org_id bigint NOT NULL,
    shipper_org_id bigint NOT NULL,
    origin character varying(500),
    destination character varying(500),
    status character varying(1) DEFAULT 'A'::character varying NOT NULL,
    eff_date timestamp without time zone,
    exp_date timestamp without time zone,
    notes character varying(2000),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE ltl_org_block_lane OWNER TO postgres;

--
-- TOC entry 339 (class 1259 OID 19224)
-- Name: ltl_pallet_pric_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pallet_pric_details (
    ltl_pallet_pric_det_id bigint NOT NULL,
    ltl_pric_prof_detail_id bigint NOT NULL,
    min_qty bigint,
    max_qty bigint,
    cost_type character varying(30),
    unit_cost numeric(10,2),
    cost_appl_min_wt numeric(10,2),
    cost_appl_max_wt numeric(10,2),
    cost_appl_wt_uom character varying(2),
    margin_percent numeric(10,2),
    eff_date timestamp without time zone,
    exp_date timestamp without time zone,
    transit_time bigint,
    service_type character varying(30),
    zone_to bigint,
    zone_from bigint,
    status character varying(1) DEFAULT 'A'::character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    copied_from bigint,
    movement_type character varying(30),
    exclude_fuel character(1) DEFAULT 'N'::bpchar
);


ALTER TABLE ltl_pallet_pric_details OWNER TO postgres;

--
-- TOC entry 669 (class 1259 OID 23292)
-- Name: ltl_pallet_price_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pallet_price_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pallet_price_details_seq OWNER TO postgres;

--
-- TOC entry 679 (class 1259 OID 23312)
-- Name: ltl_pric_blk_serv_dtl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pric_blk_serv_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pric_blk_serv_dtl_seq OWNER TO postgres;

--
-- TOC entry 680 (class 1259 OID 23314)
-- Name: ltl_pric_geo_serv_dtl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pric_geo_serv_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pric_geo_serv_dtl_seq OWNER TO postgres;

--
-- TOC entry 354 (class 1259 OID 19379)
-- Name: ltl_pric_geo_serv_dtls; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pric_geo_serv_dtls (
    ltl_pric_geo_serv_dtl_id numeric(30,0) NOT NULL,
    ltl_pricing_geo_service_id numeric(30,0),
    geo_value character varying(100),
    geo_type smallint,
    geo_serv_type smallint,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer,
    searchable_geo_value character varying(100)
);


ALTER TABLE ltl_pric_geo_serv_dtls OWNER TO postgres;

--
-- TOC entry 681 (class 1259 OID 23316)
-- Name: ltl_pric_geo_srv_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pric_geo_srv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pric_geo_srv_seq OWNER TO postgres;

--
-- TOC entry 682 (class 1259 OID 23318)
-- Name: ltl_pric_job_id_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pric_job_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pric_job_id_seq OWNER TO postgres;

--
-- TOC entry 355 (class 1259 OID 19386)
-- Name: ltl_pric_nw_dflt_margin; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pric_nw_dflt_margin (
    nw_dflt_mrgn_id bigint NOT NULL,
    network_id bigint NOT NULL,
    status character varying(1) NOT NULL,
    margin_perc numeric(10,2),
    min_margin_amt numeric(10,2),
    version integer,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint
);


ALTER TABLE ltl_pric_nw_dflt_margin OWNER TO postgres;

--
-- TOC entry 683 (class 1259 OID 23320)
-- Name: ltl_pric_nw_dflt_mrgn_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pric_nw_dflt_mrgn_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pric_nw_dflt_mrgn_seq OWNER TO postgres;

--
-- TOC entry 684 (class 1259 OID 23322)
-- Name: ltl_pric_prof_detail_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pric_prof_detail_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pric_prof_detail_seq OWNER TO postgres;

--
-- TOC entry 685 (class 1259 OID 23324)
-- Name: ltl_pric_prof_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pric_prof_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pric_prof_seq OWNER TO postgres;

--
-- TOC entry 356 (class 1259 OID 19391)
-- Name: ltl_pric_prop_cost_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pric_prop_cost_details (
    ltl_pric_prop_cost_det_id numeric(30,0) NOT NULL,
    ltl_pric_proposal_id numeric(30,0) NOT NULL,
    ltl_pricing_id bigint,
    ref_type character varying(10) NOT NULL,
    ship_carr character varying(1) NOT NULL,
    subtotal numeric(10,2) NOT NULL,
    billable character varying(1) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL
);


ALTER TABLE ltl_pric_prop_cost_details OWNER TO postgres;

--
-- TOC entry 687 (class 1259 OID 23328)
-- Name: ltl_pric_prop_mat_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pric_prop_mat_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pric_prop_mat_seq OWNER TO postgres;

--
-- TOC entry 357 (class 1259 OID 19399)
-- Name: ltl_pric_prop_materials; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pric_prop_materials (
    ltl_pric_prop_mat_id numeric(30,0) NOT NULL,
    load_id bigint,
    quote_id bigint,
    weight numeric(10,2) NOT NULL,
    commodity_class_code character varying(8) NOT NULL,
    quantity integer,
    pieces integer,
    hazmat character varying(1),
    ltl_package_type character varying(3),
    status character(1),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint NOT NULL,
    height numeric(10,2),
    width numeric(10,2),
    length numeric(10,2),
    part_description character varying(1000),
    ltl_product_id bigint
);


ALTER TABLE ltl_pric_prop_materials OWNER TO postgres;

--
-- TOC entry 686 (class 1259 OID 23326)
-- Name: ltl_pric_proposal_dtls_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pric_proposal_dtls_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pric_proposal_dtls_seq OWNER TO postgres;

--
-- TOC entry 670 (class 1259 OID 23294)
-- Name: ltl_pricing_api_addr_crit_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pricing_api_addr_crit_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pricing_api_addr_crit_seq OWNER TO postgres;

--
-- TOC entry 340 (class 1259 OID 19234)
-- Name: ltl_pricing_api_address_crit; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_api_address_crit (
    ltl_pricing_api_address_id bigint NOT NULL,
    address1 character varying(200),
    address2 character varying(200),
    city character varying(30),
    state_code character varying(6),
    postal_code character varying(10),
    country_code character varying(3) DEFAULT 'USA'::character varying,
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint DEFAULT 0 NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint DEFAULT 0 NOT NULL,
    version integer DEFAULT 1 NOT NULL
);


ALTER TABLE ltl_pricing_api_address_crit OWNER TO postgres;

--
-- TOC entry 341 (class 1259 OID 19245)
-- Name: ltl_pricing_api_crit; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_api_crit (
    ltl_pricing_api_crit_id bigint NOT NULL,
    shipper_org_id bigint,
    carrier_org_id bigint,
    ship_date timestamp without time zone,
    origin bigint,
    destination bigint,
    accessorial_types character varying(250),
    guaranteed_time integer,
    pallet_type character varying(1),
    gainshare_account character varying(1),
    movement_type character varying(10),
    userid character varying(50),
    request_type character varying(10),
    scac character varying(4),
    api_return_time numeric(10,2),
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint DEFAULT 0 NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint DEFAULT 0 NOT NULL,
    version integer DEFAULT 1 NOT NULL
);


ALTER TABLE ltl_pricing_api_crit OWNER TO postgres;

--
-- TOC entry 671 (class 1259 OID 23296)
-- Name: ltl_pricing_api_crit_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pricing_api_crit_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pricing_api_crit_seq OWNER TO postgres;

--
-- TOC entry 342 (class 1259 OID 19257)
-- Name: ltl_pricing_api_materials_crit; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_api_materials_crit (
    ltl_pricing_api_material_id bigint NOT NULL,
    ltl_pricing_api_crit_id bigint NOT NULL,
    weight numeric(10,2),
    commodity_class character varying(8),
    height numeric(10,2),
    width numeric(10,2),
    length numeric(10,2),
    dimension_unit character varying(10),
    quantity integer,
    package_type character varying(100),
    hazmat character(1),
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint DEFAULT 0 NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint DEFAULT 0 NOT NULL,
    version integer DEFAULT 1 NOT NULL
);


ALTER TABLE ltl_pricing_api_materials_crit OWNER TO postgres;

--
-- TOC entry 672 (class 1259 OID 23298)
-- Name: ltl_pricing_api_mats_crit_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pricing_api_mats_crit_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pricing_api_mats_crit_seq OWNER TO postgres;

--
-- TOC entry 343 (class 1259 OID 19268)
-- Name: ltl_pricing_appl_cust; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_appl_cust (
    ltl_appl_cust_id bigint NOT NULL,
    ltl_pricing_profile_id bigint NOT NULL,
    shipper_org_id bigint,
    status character varying(1),
    version integer NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint
);


ALTER TABLE ltl_pricing_appl_cust OWNER TO postgres;

--
-- TOC entry 673 (class 1259 OID 23300)
-- Name: ltl_pricing_appl_cust_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pricing_appl_cust_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pricing_appl_cust_seq OWNER TO postgres;

--
-- TOC entry 344 (class 1259 OID 19278)
-- Name: ltl_pricing_blocked_cust; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_blocked_cust (
    ltl_blocked_cust_id bigint NOT NULL,
    ltl_pricing_profile_id bigint NOT NULL,
    shipper_org_id numeric(20,0),
    status character varying(1),
    version integer NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint
);


ALTER TABLE ltl_pricing_blocked_cust OWNER TO postgres;

--
-- TOC entry 674 (class 1259 OID 23302)
-- Name: ltl_pricing_blocked_cust_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pricing_blocked_cust_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pricing_blocked_cust_seq OWNER TO postgres;

--
-- TOC entry 345 (class 1259 OID 19289)
-- Name: ltl_pricing_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_details (
    ltl_pricing_detail_id bigint NOT NULL,
    ltl_pric_prof_detail_id bigint NOT NULL,
    cost_type character varying(30),
    unit_cost numeric(10,2),
    cost_appl_min_wt numeric(10,2),
    cost_appl_max_wt numeric(10,2),
    cost_appl_wt_uom character varying(2),
    cost_appl_min_dist numeric(10,2),
    cost_appl_max_dist numeric(10,2),
    cost_appl_dist_uom character varying(2),
    min_cost numeric(10,2),
    margin_type character varying(30),
    unit_margin numeric(10,2),
    drop_me_margin_percent numeric(10,2),
    margin_dollar_amt numeric(10,2),
    eff_date timestamp without time zone,
    exp_date timestamp without time zone,
    service_type character varying(30),
    smc3_tariff character varying(30),
    commodity_class character varying(10),
    status character varying(1) DEFAULT 'A'::character varying,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    copied_from bigint,
    movement_type character varying(30),
    parent_id bigint
);


ALTER TABLE ltl_pricing_details OWNER TO postgres;

--
-- TOC entry 675 (class 1259 OID 23304)
-- Name: ltl_pricing_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pricing_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pricing_details_seq OWNER TO postgres;

--
-- TOC entry 346 (class 1259 OID 19296)
-- Name: ltl_pricing_geo_services; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_geo_services (
    ltl_pricing_geo_service_id bigint NOT NULL,
    ltl_pricing_detail_id bigint NOT NULL,
    drop_me_origin character varying(3000),
    drop_me_destination character varying(3000),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE ltl_pricing_geo_services OWNER TO postgres;

--
-- TOC entry 347 (class 1259 OID 19305)
-- Name: ltl_pricing_notes; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_notes (
    ltl_pricing_note_id bigint NOT NULL,
    ltl_pricing_profile_id bigint NOT NULL,
    notes character varying(3000) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    version integer NOT NULL
);


ALTER TABLE ltl_pricing_notes OWNER TO postgres;

--
-- TOC entry 676 (class 1259 OID 23306)
-- Name: ltl_pricing_notes_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pricing_notes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pricing_notes_seq OWNER TO postgres;

--
-- TOC entry 348 (class 1259 OID 19314)
-- Name: ltl_pricing_profile; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_profile (
    ltl_pricing_profile_id bigint NOT NULL,
    rate_name character varying(255),
    carrier_code character varying(30),
    ltl_pricing_type character varying(255) NOT NULL,
    eff_date timestamp without time zone,
    exp_date timestamp without time zone,
    blocked character(1) DEFAULT 'N'::bpchar,
    alias_name character varying(255),
    status character(1),
    carrier_website character varying(255),
    prohibited_commodities character varying(500),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    carrier_org_id bigint,
    shipper_org_id bigint,
    note character varying(2000),
    copied_from bigint,
    ext_notes character varying(2000),
    int_notes character varying(2000),
    act_carrier_org_id numeric(30,0),
    blocked_from_booking character(1) DEFAULT 'N'::bpchar
);


ALTER TABLE ltl_pricing_profile OWNER TO postgres;

--
-- TOC entry 349 (class 1259 OID 19327)
-- Name: ltl_pricing_profile_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_profile_details (
    ltl_pric_prof_detail_id bigint NOT NULL,
    ltl_pricing_profile_id bigint NOT NULL,
    pricing_detail_type character varying(255),
    ltl_rating_carrier_type character varying(255),
    mileage_type character varying(255),
    mileage_version character varying(255),
    smc3_scac character varying(255),
    smc3_tariff character varying(30),
    mscale character varying(255),
    organization_api_detail_id bigint,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint NOT NULL,
    use_blanket character(1) DEFAULT 'N'::bpchar
);


ALTER TABLE ltl_pricing_profile_details OWNER TO postgres;

--
-- TOC entry 350 (class 1259 OID 19342)
-- Name: ltl_pricing_proposals; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_proposals (
    ltl_pric_proposal_id numeric(30,0) NOT NULL,
    load_id bigint,
    ltl_pricing_profile_id bigint NOT NULL,
    guaranteed_time bigint,
    hazmat_flag character varying(1),
    total_weight numeric(10,2) NOT NULL,
    total_pieces integer,
    total_quantity integer NOT NULL,
    total_revenue numeric(10,2) NOT NULL,
    total_cost numeric(10,2) NOT NULL,
    total_benchmark numeric(10,2),
    transit_time integer NOT NULL,
    service_type character varying(10) NOT NULL,
    new_prod_liab_amt numeric(10,2),
    used_prod_liab_amt numeric(10,2),
    prohibited_commodities character varying(1000),
    pric_prof_note character varying(4000),
    addl_guaran_info character varying(500),
    guaran_bol_name character varying(250),
    smc3_tariff_name character varying(30),
    pallet_package_type character varying(1),
    proposal_selected character varying(1),
    quote_id bigint,
    status character(1),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint NOT NULL
);


ALTER TABLE ltl_pricing_proposals OWNER TO postgres;

--
-- TOC entry 677 (class 1259 OID 23308)
-- Name: ltl_pricing_proposals_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pricing_proposals_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pricing_proposals_seq OWNER TO postgres;

--
-- TOC entry 351 (class 1259 OID 19351)
-- Name: ltl_pricing_terminal_info; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_terminal_info (
    ltl_terminal_info_id bigint NOT NULL,
    ltl_pric_prof_detail_id bigint NOT NULL,
    terminal character varying(32),
    contact_name character varying(32),
    transit_time bigint,
    address_id bigint,
    visible character varying(1) DEFAULT 'Y'::character varying,
    phone_id bigint,
    fax_id bigint,
    email_address character varying(255),
    account_num character varying(32),
    status character varying(1) DEFAULT 'A'::character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    copied_from bigint
);


ALTER TABLE ltl_pricing_terminal_info OWNER TO postgres;

--
-- TOC entry 678 (class 1259 OID 23310)
-- Name: ltl_pricing_terminal_info_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_pricing_terminal_info_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_pricing_terminal_info_seq OWNER TO postgres;

--
-- TOC entry 352 (class 1259 OID 19362)
-- Name: ltl_pricing_third_party_info; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_third_party_info (
    ltl_third_party_info_id bigint NOT NULL,
    ltl_pric_prof_detail_id bigint NOT NULL,
    company character varying(200),
    contact_name character varying(100),
    address_id bigint,
    phone_id bigint,
    fax_id bigint,
    email_address character varying(255),
    account_num character varying(32),
    status character varying(1) DEFAULT 'A'::character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    copied_from bigint
);


ALTER TABLE ltl_pricing_third_party_info OWNER TO postgres;

--
-- TOC entry 353 (class 1259 OID 19374)
-- Name: ltl_pricing_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_pricing_types (
    ltl_pricing_type character varying(30) NOT NULL,
    description character varying(100) NOT NULL,
    group_type character varying(30)
);


ALTER TABLE ltl_pricing_types OWNER TO postgres;

--
-- TOC entry 6348 (class 0 OID 0)
-- Dependencies: 353
-- Name: COLUMN ltl_pricing_types.group_type; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN ltl_pricing_types.group_type IS 'Logical group for pricing types. Valid values are ''CARRIER'', ''CUSTOMER'' or ''BENCHMARK'' but the last value is not displayed in PLS2.0 application';


--
-- TOC entry 358 (class 1259 OID 19408)
-- Name: ltl_product; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_product (
    ltl_product_id bigint NOT NULL,
    ltl_product_tracking_id bigint NOT NULL,
    org_id bigint NOT NULL,
    location_id bigint NOT NULL,
    product_code character varying(30),
    description character varying(1000) NOT NULL,
    nmfc_num character varying(30),
    pieces integer,
    package_type character varying(100),
    weight numeric(10,2),
    commodity_class_code character varying(8),
    hazmat_flag character(1) DEFAULT 'N'::bpchar NOT NULL,
    hazmat_class character varying(100),
    un_num character varying(32),
    packing_group character varying(32),
    emergency_number character varying(32),
    created_by bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    modified_by bigint,
    date_modified timestamp without time zone,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
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
-- TOC entry 688 (class 1259 OID 23330)
-- Name: ltl_product_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_product_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_product_seq OWNER TO postgres;

--
-- TOC entry 689 (class 1259 OID 23332)
-- Name: ltl_rating_carr_code_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_rating_carr_code_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_rating_carr_code_seq OWNER TO postgres;

--
-- TOC entry 949 (class 1259 OID 44287)
-- Name: ltl_rating_carrier_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_rating_carrier_types (
    ltl_rating_carrier_type character varying(255) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE ltl_rating_carrier_types OWNER TO postgres;

--
-- TOC entry 690 (class 1259 OID 23334)
-- Name: ltl_region_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_region_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_region_seq OWNER TO postgres;

--
-- TOC entry 691 (class 1259 OID 23336)
-- Name: ltl_transit_time_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_transit_time_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_transit_time_seq OWNER TO postgres;

--
-- TOC entry 694 (class 1259 OID 23342)
-- Name: ltl_zone_geo_serv_dtl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_zone_geo_serv_dtl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_zone_geo_serv_dtl_seq OWNER TO postgres;

--
-- TOC entry 361 (class 1259 OID 19439)
-- Name: ltl_zone_geo_serv_dtls; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_zone_geo_serv_dtls (
    ltl_zone_geo_serv_dtl_id numeric(30,0),
    ltl_zone_geo_service_id numeric(30,0),
    geo_value character varying(100),
    geo_type smallint,
    geo_serv_type smallint,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer,
    searchable_geo_value character varying(100)
);


ALTER TABLE ltl_zone_geo_serv_dtls OWNER TO postgres;

--
-- TOC entry 360 (class 1259 OID 19430)
-- Name: ltl_zone_geo_services; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_zone_geo_services (
    ltl_zone_geo_service_id bigint NOT NULL,
    ltl_zone_id bigint NOT NULL,
    location character varying(500),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE ltl_zone_geo_services OWNER TO postgres;

--
-- TOC entry 693 (class 1259 OID 23340)
-- Name: ltl_zone_geo_services_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_zone_geo_services_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_zone_geo_services_seq OWNER TO postgres;

--
-- TOC entry 359 (class 1259 OID 19424)
-- Name: ltl_zones; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE ltl_zones (
    ltl_zone_id bigint NOT NULL,
    ltl_pric_prof_detail_id bigint NOT NULL,
    name character varying(50) NOT NULL,
    status character varying(1) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    copied_from bigint
);


ALTER TABLE ltl_zones OWNER TO postgres;

--
-- TOC entry 692 (class 1259 OID 23338)
-- Name: ltl_zones_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ltl_zones_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_zones_seq OWNER TO postgres;

--
-- TOC entry 696 (class 1259 OID 23346)
-- Name: mac_lm_group_id_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE mac_lm_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE mac_lm_group_id_seq OWNER TO postgres;

--
-- TOC entry 701 (class 1259 OID 23356)
-- Name: man_quote_data_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE man_quote_data_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE man_quote_data_seq OWNER TO postgres;

--
-- TOC entry 697 (class 1259 OID 23348)
-- Name: manaul_bol_job_numbers_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE manaul_bol_job_numbers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE manaul_bol_job_numbers_seq OWNER TO postgres;

--
-- TOC entry 362 (class 1259 OID 19442)
-- Name: manual_bol; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE manual_bol (
    manual_bol_id bigint NOT NULL,
    status character varying(2) NOT NULL,
    org_id integer NOT NULL,
    carrier_org_id integer NOT NULL,
    bol character varying(25),
    pro_number character varying(50),
    po_number character varying(50),
    pu_number character varying(50),
    shipper_ref_num character varying(50),
    frt_bill_pay_to_id bigint,
    trailer_num character varying(30),
    so_number character varying(50),
    gl_number character varying(30),
    image_meta_id bigint,
    bill_to bigint,
    pay_terms character varying(3),
    location_id bigint,
    pickup_date timestamp without time zone,
    pickup_notes character varying(250),
    delivery_notes character varying(250),
    shipping_label_notes character varying(250),
    shipping_hours_operation_from timestamp without time zone,
    shipping_hours_operation_to timestamp without time zone,
    receiving_hours_operation_from timestamp without time zone,
    receiving_hours_operation_to timestamp without time zone,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version bigint DEFAULT 1 NOT NULL,
    customs_broker character varying(50),
    customs_broker_phone character varying(20),
    manual_bol_inbound_outbound character varying(20)
);


ALTER TABLE manual_bol OWNER TO postgres;

--
-- TOC entry 363 (class 1259 OID 19456)
-- Name: manual_bol_address; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE manual_bol_address (
    id bigint NOT NULL,
    manual_bol_id bigint NOT NULL,
    address_id bigint,
    contact character varying(100),
    address_code character varying(50),
    address1 character varying(200),
    address2 character varying(200),
    contact_email character varying(100),
    contact_fax character varying(20),
    contact_name character varying(50),
    contact_phone character varying(30),
    pickup_notes character varying(3000),
    delivery_notes character varying(3000),
    point_type character varying(1) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version bigint DEFAULT 1 NOT NULL,
    int_delivery_notes character varying(3000),
    int_pickup_notes character varying(3000)
);


ALTER TABLE manual_bol_address OWNER TO postgres;

--
-- TOC entry 698 (class 1259 OID 23350)
-- Name: manual_bol_address_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE manual_bol_address_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE manual_bol_address_seq OWNER TO postgres;

--
-- TOC entry 364 (class 1259 OID 19469)
-- Name: manual_bol_job_numbers; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE manual_bol_job_numbers (
    manual_bol_job_number_id bigint NOT NULL,
    manual_bol_id bigint,
    job_number character varying(30),
    percentage numeric(10,2),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer
);


ALTER TABLE manual_bol_job_numbers OWNER TO postgres;

--
-- TOC entry 365 (class 1259 OID 19474)
-- Name: manual_bol_materials; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE manual_bol_materials (
    manual_bol_material_id bigint NOT NULL,
    manual_bol_id bigint NOT NULL,
    weight numeric(10,2),
    width numeric(10,2),
    height numeric(10,2),
    length numeric(10,2),
    status character varying(1),
    stackable character varying(1),
    hazmat character varying(1),
    part_description character varying(100),
    commodity_class_code character varying(8),
    ltl_product_id integer,
    ltl_package_type character varying(3),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version bigint DEFAULT 1 NOT NULL,
    product_code character varying(30),
    hazmat_class character varying(100),
    hazmat_instructions character varying(2000),
    emergency_company character varying(32),
    emergency_contract character varying(32),
    emergency_country_code character varying(10),
    emergency_area_code character varying(10),
    emergency_number character varying(30),
    un_num character varying(32),
    packing_group character varying(30),
    pickup_date timestamp without time zone,
    nmfc character varying(30),
    pieces integer,
    emergency_extension character varying(6),
    quantity bigint
);


ALTER TABLE manual_bol_materials OWNER TO postgres;

--
-- TOC entry 699 (class 1259 OID 23352)
-- Name: manual_bol_materials_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE manual_bol_materials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE manual_bol_materials_seq OWNER TO postgres;

--
-- TOC entry 700 (class 1259 OID 23354)
-- Name: manual_bol_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE manual_bol_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE manual_bol_seq OWNER TO postgres;

--
-- TOC entry 702 (class 1259 OID 23358)
-- Name: master_tendering_plan_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE master_tendering_plan_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE master_tendering_plan_seq OWNER TO postgres;

--
-- TOC entry 703 (class 1259 OID 23360)
-- Name: mdl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE mdl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE mdl_seq OWNER TO postgres;

--
-- TOC entry 367 (class 1259 OID 19499)
-- Name: mileage_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE mileage_types (
    mileage_type character varying(255) NOT NULL,
    mileage_version character varying(255) NOT NULL,
    description character varying(255) NOT NULL,
    status character(1) NOT NULL
);

ALTER TABLE mileage_types OWNER TO postgres;

--
-- TOC entry 704 (class 1259 OID 23362)
-- Name: minority_certification_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE minority_certification_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE minority_certification_seq OWNER TO postgres;

--
-- TOC entry 705 (class 1259 OID 23364)
-- Name: mne_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE mne_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE mne_seq OWNER TO postgres;

--
-- TOC entry 706 (class 1259 OID 23366)
-- Name: msg_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE msg_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE msg_seq OWNER TO postgres;

--
-- TOC entry 709 (class 1259 OID 23372)
-- Name: net_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE net_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE net_seq OWNER TO postgres;

--
-- TOC entry 710 (class 1259 OID 23374)
-- Name: net_usr_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE net_usr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE net_usr_seq OWNER TO postgres;

--
-- TOC entry 707 (class 1259 OID 23368)
-- Name: netc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE netc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE netc_seq OWNER TO postgres;

--
-- TOC entry 708 (class 1259 OID 23370)
-- Name: network_parameters_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE network_parameters_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE network_parameters_seq OWNER TO postgres;

--
-- TOC entry 369 (class 1259 OID 19518)
-- Name: network_users; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE network_users (
    network_user_id bigint NOT NULL,
    network_id bigint NOT NULL,
    person_id bigint NOT NULL,
    status character varying(1) DEFAULT 'Y'::character varying NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL
);


ALTER TABLE network_users OWNER TO postgres;

--
-- TOC entry 368 (class 1259 OID 19507)
-- Name: networks; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE networks (
    network_id bigint NOT NULL,
    name character varying(100) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    network_code character varying(3) NOT NULL,
    default_brand_id bigint,
    carrier_admin_id bigint,
    tender_base_network character(1),
    share_carrier_network_id bigint,
    root_org bigint NOT NULL,
    auto_credit_hold character varying(1),
    warning_no_of_days smallint,
    credit_limit_required character varying(1),
    version integer,
    visible character varying(1) DEFAULT 'N'::character varying,
    min_accept_margin numeric(10,2),
    default_ap_terms character varying(30),
    max_ap_duedays character varying(30)
);


ALTER TABLE networks OWNER TO postgres;

--
-- TOC entry 711 (class 1259 OID 23376)
-- Name: notable_load_email_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE notable_load_email_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE notable_load_email_seq OWNER TO postgres;

--
-- TOC entry 370 (class 1259 OID 19527)
-- Name: notes; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE notes (
    note_id bigint NOT NULL,
    ref_id bigint NOT NULL,
    note character varying(3000) NOT NULL,
    note_type character varying(10) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    visibility character(1) DEFAULT 'I'::bpchar NOT NULL
);


ALTER TABLE notes OWNER TO postgres;

--
-- TOC entry 371 (class 1259 OID 19542)
-- Name: notification_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE notification_types (
    notification_type character varying(32) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE notification_types OWNER TO postgres;

--
-- TOC entry 712 (class 1259 OID 23378)
-- Name: nte_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE nte_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE nte_seq OWNER TO postgres;

--
-- TOC entry 713 (class 1259 OID 23380)
-- Name: ofr_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ofr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ofr_seq OWNER TO postgres;

--
-- TOC entry 714 (class 1259 OID 23382)
-- Name: oh_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE oh_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE oh_seq OWNER TO postgres;

--
-- TOC entry 715 (class 1259 OID 23384)
-- Name: oio_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE oio_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE oio_seq OWNER TO postgres;

--
-- TOC entry 716 (class 1259 OID 23386)
-- Name: opr_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE opr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE opr_seq OWNER TO postgres;

--
-- TOC entry 717 (class 1259 OID 23388)
-- Name: opv_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE opv_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE opv_seq OWNER TO postgres;

--
-- TOC entry 377 (class 1259 OID 19656)
-- Name: org_frt_bill_pay_to; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE org_frt_bill_pay_to (
    org_frt_bill_pay_to_id bigint NOT NULL,
    org_id bigint NOT NULL,
    frt_bill_pay_to_id bigint NOT NULL,
    status character(1) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version bigint,
    fax_id bigint
);


ALTER TABLE org_frt_bill_pay_to OWNER TO postgres;

--
-- TOC entry 723 (class 1259 OID 23400)
-- Name: org_frt_bill_pay_to_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE org_frt_bill_pay_to_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE org_frt_bill_pay_to_seq OWNER TO postgres;

--
-- TOC entry 724 (class 1259 OID 23402)
-- Name: org_insurance_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE org_insurance_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE org_insurance_seq OWNER TO postgres;

--
-- TOC entry 725 (class 1259 OID 23404)
-- Name: org_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE org_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE org_seq OWNER TO postgres;

--
-- TOC entry 378 (class 1259 OID 19663)
-- Name: org_services; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE org_services (
    org_service_id bigint NOT NULL,
    org_id bigint NOT NULL,
    tracking character varying(3),
    pickup character varying(6),
    rating character varying(3),
    invoice character varying(3),
    imaging character varying(3),
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint DEFAULT 0 NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL,
    manual_type_email character varying(255)
);


ALTER TABLE org_services OWNER TO postgres;

--
-- TOC entry 726 (class 1259 OID 23406)
-- Name: org_services_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE org_services_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE org_services_seq OWNER TO postgres;


--
-- TOC entry 727 (class 1259 OID 23408)
-- Name: org_warehouse_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE org_warehouse_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE org_warehouse_seq OWNER TO postgres;

--
-- TOC entry 720 (class 1259 OID 23394)
-- Name: orga_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE orga_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE orga_seq OWNER TO postgres;

--
-- TOC entry 859 (class 1259 OID 34879)
-- Name: organization_api_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE organization_api_details (
    organization_api_detail_id bigint NOT NULL,
    org_id bigint NOT NULL,
    api_name character varying(255) NOT NULL,
    url character varying(1000),
    login character varying(255),
    password character varying(255),
    token character varying(255),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint NOT NULL,
    status character varying(1) NOT NULL
);


ALTER TABLE organization_api_details OWNER TO postgres;

--
-- TOC entry 718 (class 1259 OID 23390)
-- Name: organization_api_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE organization_api_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE organization_api_details_seq OWNER TO postgres;

--
-- TOC entry 373 (class 1259 OID 19588)
-- Name: organization_locations; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE organization_locations (
    location_id bigint NOT NULL,
    org_id bigint NOT NULL,
    location_name character varying(240) NOT NULL,
    status character varying(1) NOT NULL,
    contact_last_name character varying(30),
    contact_first_name character varying(20),
    contact_title character varying(50),
    contact_email character varying(50),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    bill_to bigint,
    op_org_id bigint,
    op_loc_id bigint,
    visible character(1),
    pricing_on_location character(1) DEFAULT 'Y'::bpchar NOT NULL,
    auto_auto_start_time integer,
    auto_auto_end_time integer,
    rate_contact_name character varying(100),
    rate_email_address character varying(100),
    rate_fax_area_code character varying(6),
    rate_fax_number character varying(10),
    rate_default_minimum numeric(10,2),
    rate_default_type character varying(10),
    rate_car_default_minimum numeric(10,2),
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
    is_default character(1) DEFAULT 'N'::bpchar
);


ALTER TABLE organization_locations OWNER TO postgres;

--
-- TOC entry 6357 (class 0 OID 0)
-- Dependencies: 373
-- Name: COLUMN organization_locations.sales_lead; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organization_locations.sales_lead IS 'Person who got this account';


--
-- TOC entry 6358 (class 0 OID 0)
-- Dependencies: 373
-- Name: COLUMN organization_locations.employer_num; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organization_locations.employer_num IS 'Organization location¿s federal tax id';


--
-- TOC entry 374 (class 1259 OID 19608)
-- Name: organization_phones; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE organization_phones (
    org_phone_id bigint NOT NULL,
    org_id bigint NOT NULL,
    location_id bigint,
    dialing_code character varying(3) DEFAULT '001'::character varying NOT NULL,
    area_code character varying(6),
    phone_number character varying(10) NOT NULL,
    extension character varying(6),
    phone_type character varying(10) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL
);


ALTER TABLE organization_phones OWNER TO postgres;

--
-- TOC entry 375 (class 1259 OID 19622)
-- Name: organization_pricing; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE organization_pricing (
    org_id bigint NOT NULL,
    status character varying(1) NOT NULL,
    min_accept_margin numeric(10,2),
    default_margin numeric(10,2),
    gainshare character varying(1),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer,
    gs_pls_pct numeric(10,2),
    gs_cust_pct numeric(10,2),
    include_bm_acc character varying(1),
    blk_serv_carrier_type smallint,
    block_service_type character varying(30),
    default_min_margin_amt numeric(10,2)
);


ALTER TABLE organization_pricing OWNER TO postgres;

--
-- TOC entry 719 (class 1259 OID 23392)
-- Name: organization_req_doc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE organization_req_doc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE organization_req_doc_seq OWNER TO postgres;

--
-- TOC entry 376 (class 1259 OID 19627)
-- Name: organization_users; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE organization_users (
    org_user_id bigint NOT NULL,
    status character varying(1) NOT NULL,
    person_id bigint NOT NULL,
    org_id bigint NOT NULL,
    location_id bigint,
    person_id_parent bigint,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    org_level_cd character varying(6) DEFAULT 'OTHER'::character varying NOT NULL,
    rate_approval_email_flg character(1),
    onetime_rate_email_flg character(1),
    miss_notify_flg character(1),
    award_conf_email_flg character(1) DEFAULT 'Y'::bpchar NOT NULL,
    unaward_email_flg character(1) DEFAULT 'Y'::bpchar NOT NULL,
    load_schedule_email_flg character(1) DEFAULT 'Y'::bpchar NOT NULL,
    load_unschedule_email_flg character(1) DEFAULT 'Y'::bpchar NOT NULL,
    load_reschedule_email_flg character(1) DEFAULT 'Y'::bpchar NOT NULL,
    offer_decline_email_flg character(1) DEFAULT 'Y'::bpchar NOT NULL,
    phone_ofr_cancel_email_flg character(1) DEFAULT 'Y'::bpchar NOT NULL,
    cancelled_load_email_flg character(1) DEFAULT 'Y'::bpchar NOT NULL,
    phone_ofr_conf_email_flg character(1) DEFAULT 'Y'::bpchar NOT NULL,
    login_default character(1) DEFAULT 'N'::bpchar NOT NULL,
    version integer,
    org_type character varying(10)
);


ALTER TABLE organization_users OWNER TO postgres;

--
-- TOC entry 372 (class 1259 OID 19547)
-- Name: organizations; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE organizations (
    org_id bigint NOT NULL,
    name character varying(240) NOT NULL,
    employer_num character varying(35),
    org_type character varying(10) NOT NULL,
    status character(1) NOT NULL,
    contact_last_name character varying(30),
    contact_first_name character varying(20),
    contact_title character varying(50),
    contact_email character varying(50),
    mc_num character varying(15),
    scac character varying(4),
    org_id_parent bigint,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    loc_rate_override character varying(1) DEFAULT 'N'::character varying NOT NULL,
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
    rate_default_minimum numeric(10,2),
    rate_default_type character varying(10),
    rate_auto_approve_days bigint,
    not_exceed_type character(1),
    max_carrier_rate numeric(10,2),
    max_carr_rate_unit_type character varying(10),
    rate_car_default_minimum numeric(10,2),
    rate_car_default_type character varying(10),
    address_id bigint,
    credit_limit bigint,
    sales_lead bigint,
    ltl_account_type character varying(3),
    ltl_client_code character varying(50),
    region_id bigint,
    accelerated_ind character(1),
    version integer DEFAULT 1 NOT NULL,
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
    product_list_primary_sort character varying(30) DEFAULT 'PRODUCT_DESCRIPTION'::character varying,
    display_logo_on_bol character(1),
    sent_to_finan character varying(1) DEFAULT 'N'::character varying,
    pay_terms_id bigint,
    key_account character(1),
    key_org_id bigint,
    display_logo_on_ship_label character(1),
    generate_consignee_invoice character(1),
    internal_note character varying(2000),
    PRINT_BARCODE character(1)
);


ALTER TABLE organizations OWNER TO postgres;

--
-- TOC entry 6360 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.employer_num; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.employer_num IS 'Organization''s Federal Tax ID';


--
-- TOC entry 6361 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.org_type; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.org_type IS 'Code identifying the possible organization types';


--
-- TOC entry 6362 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.contact_last_name; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.contact_last_name IS 'Last name of the primary contact for enrolling the organization into eflatbed';


--
-- TOC entry 6363 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.contact_first_name; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.contact_first_name IS 'First name of the primary contact for enrolling the organization into eflatbed';


--
-- TOC entry 6364 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.contact_title; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.contact_title IS 'Job title of primary contact for enrolling the organization into eflatbed';


--
-- TOC entry 6365 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.contact_email; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.contact_email IS 'email address of the primary contact for enrolling the organization into eflatbed';


--
-- TOC entry 6366 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.address_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.address_id IS 'For capturing a corporate address';


--
-- TOC entry 6367 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.credit_limit; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.credit_limit IS 'Company credit limit';


--
-- TOC entry 6368 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.sales_lead; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.sales_lead IS 'The sales person who got this account';


--
-- TOC entry 6369 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.key_account; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.key_account IS 'key accoount - for Cognos needs';


--
-- TOC entry 6370 (class 0 OID 0)
-- Dependencies: 372
-- Name: COLUMN organizations.key_org_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN organizations.key_org_id IS 'key org_id for child customers- for Cognos needs';


--
-- TOC entry 721 (class 1259 OID 23396)
-- Name: orgp_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE orgp_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE orgp_seq OWNER TO postgres;

--
-- TOC entry 722 (class 1259 OID 23398)
-- Name: orgu_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE orgu_seq
    START WITH 1
    INCREMENT BY 60
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE orgu_seq OWNER TO postgres;

--
-- TOC entry 728 (class 1259 OID 23410)
-- Name: orole_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE orole_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE orole_seq OWNER TO postgres;

--
-- TOC entry 729 (class 1259 OID 23412)
-- Name: ostat_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ostat_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ostat_seq OWNER TO postgres;

--
-- TOC entry 730 (class 1259 OID 23414)
-- Name: outbound_messages_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE outbound_messages_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE outbound_messages_seq OWNER TO postgres;

--
-- TOC entry 379 (class 1259 OID 19671)
-- Name: outbound_queue_map; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE outbound_queue_map (
    org_id bigint NOT NULL,
    scac character varying(4),
    queue_name character varying(50) NOT NULL,
    priority smallint,
    system character varying(10) NOT NULL
);


ALTER TABLE outbound_queue_map OWNER TO postgres;

--
-- TOC entry 380 (class 1259 OID 19676)
-- Name: package_types; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE package_types (
    package_type character varying(3) NOT NULL,
    description character varying(50) NOT NULL
);


ALTER TABLE package_types OWNER TO postgres;

--
-- TOC entry 381 (class 1259 OID 19681)
-- Name: paperwork_email; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE paperwork_email (
    paperwork_email_id integer NOT NULL,
    org_id bigint NOT NULL,
    dont_req_ppw character varying(1) DEFAULT 'Y'::character varying NOT NULL,
    email character varying(255),
    version integer DEFAULT 1 NOT NULL,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint
);


ALTER TABLE paperwork_email OWNER TO postgres;

--
-- TOC entry 731 (class 1259 OID 23416)
-- Name: paperwork_email_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE paperwork_email_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE paperwork_email_seq OWNER TO postgres;

--
-- TOC entry 382 (class 1259 OID 19686)
-- Name: paperwork_hold_documents; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE paperwork_hold_documents (
    paperwork_hold_document_id bigint NOT NULL,
    load_id bigint,
    document_type character varying(50),
    request_paperwork character(1) DEFAULT 'Y'::bpchar,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint
);


ALTER TABLE paperwork_hold_documents OWNER TO postgres;

--
-- TOC entry 732 (class 1259 OID 23418)
-- Name: paperwork_hold_documents_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE paperwork_hold_documents_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE paperwork_hold_documents_seq OWNER TO postgres;

--
-- TOC entry 733 (class 1259 OID 23420)
-- Name: password_reset_request_sq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE password_reset_request_sq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE password_reset_request_sq OWNER TO postgres;

--
-- TOC entry 735 (class 1259 OID 23424)
-- Name: payment_accts_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE payment_accts_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE payment_accts_seq OWNER TO postgres;


--
-- TOC entry 736 (class 1259 OID 23426)
-- Name: payment_types_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE payment_types_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE payment_types_seq OWNER TO postgres;


--
-- TOC entry 734 (class 1259 OID 23422)
-- Name: payments_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE payments_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE payments_seq OWNER TO postgres;

--
-- TOC entry 737 (class 1259 OID 23428)
-- Name: pcar_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE pcar_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE pcar_seq OWNER TO postgres;

--
-- TOC entry 384 (class 1259 OID 19704)
-- Name: phones; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE phones (
    phone_id bigint NOT NULL,
    phone_type character varying(10) NOT NULL,
    country_code character varying(3),
    area_code character varying(6),
    phone_number character varying(10) NOT NULL,
    extension character varying(6),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer NOT NULL
);


ALTER TABLE phones OWNER TO postgres;

--
-- TOC entry 738 (class 1259 OID 23430)
-- Name: phones_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE phones_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE phones_seq OWNER TO postgres;

--
-- TOC entry 739 (class 1259 OID 23432)
-- Name: pit_id_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE pit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE pit_id_seq OWNER TO postgres;


--
-- TOC entry 415 (class 1259 OID 19983)
-- Name: user_customer; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE user_customer (
    user_customer_id bigint NOT NULL,
    user_customer_tracking_id bigint NOT NULL,
    person_id bigint NOT NULL,
    org_id bigint NOT NULL,
    location_id bigint NOT NULL,
    status character(1) NOT NULL,
    eff_date timestamp without time zone NOT NULL,
    exp_date timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    modified_date timestamp without time zone NOT NULL,
    version integer NOT NULL
);


ALTER TABLE user_customer OWNER TO postgres;

--
-- TOC entry 6371 (class 0 OID 0)
-- Dependencies: 415
-- Name: TABLE user_customer; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON TABLE user_customer IS 'Defines which customers belong to a Freight Solutions TAC. Historic integrity in place.';


--
-- TOC entry 409 (class 1259 OID 19899)
-- Name: users; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE users (
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
    watched_push_enabled character varying(1),
    critical_push_enabled character varying(1),
    person_id_fin_data bigint,
    position_fin_data character varying(50),
    department_fin_data character varying(50)
);


ALTER TABLE users OWNER TO postgres;

--
-- TOC entry 6372 (class 0 OID 0)
-- Dependencies: 409
-- Name: COLUMN users.accept_email; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN users.accept_email IS 'Indicates whether person wants to receive email as part of email push functionality.';


--
-- TOC entry 6373 (class 0 OID 0)
-- Dependencies: 409
-- Name: COLUMN users.userid; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN users.userid IS 'User sign on ID for logging on to eFlatbed application';


--
-- TOC entry 851 (class 1259 OID 24657)
-- Name: pls_credit_limit_mview; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE pls_credit_limit_mview (
    creditmax integer,
    accountnum character varying(20),
    bill_to_id integer
);


ALTER TABLE pls_credit_limit_mview OWNER TO postgres;

--
-- TOC entry 385 (class 1259 OID 19710)
-- Name: pls_customer_terms; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE pls_customer_terms (
    term_id bigint NOT NULL,
    term_name character varying(100) NOT NULL,
    due_days bigint,
    ax_term character varying(100)
);


ALTER TABLE pls_customer_terms OWNER TO postgres;

--
-- TOC entry 854 (class 1259 OID 24667)
-- Name: pls_inv_history_all_view; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE pls_inv_history_all_view (
    org_id bigint,
    location_id integer,
    inv_type character varying(20),
    invoice_id bigint,
    gl_date timestamp(0) without time zone,
    group_inv_num character varying(25),
    cust_inv_num character varying(20),
    usr character varying(61),
    load_id bigint,
    faa_detail_id bigint,
    bill_to_id bigint,
    inv_type_flag numeric(10,2),
    edi_invoice character varying(1),
    bol character varying(25),
    carrier_reference_number character varying(30),
    carrier_name character varying(240),
    subtotal numeric(10,2),
    amt_applied numeric(10,2),
    netw_name character varying(100),
    network_id bigint,
    customer character varying(240),
    due_date timestamp(0) without time zone,
    is_adj_available character varying(1)
);


ALTER TABLE pls_inv_history_all_view OWNER TO postgres;

--
-- TOC entry 740 (class 1259 OID 23434)
-- Name: pls_jobs_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE pls_jobs_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE pls_jobs_seq OWNER TO postgres;

--
-- TOC entry 741 (class 1259 OID 23436)
-- Name: pls_log_details_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE pls_log_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE pls_log_details_seq OWNER TO postgres;

--
-- TOC entry 742 (class 1259 OID 23438)
-- Name: pls_ltl_metric_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE pls_ltl_metric_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE pls_ltl_metric_seq OWNER TO postgres;

--
-- TOC entry 853 (class 1259 OID 24664)
-- Name: pls_open_balance_mview; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE pls_open_balance_mview (
    amountdue integer,
    accountnum character varying(20),
    bill_to_id integer NOT NULL
);


ALTER TABLE pls_open_balance_mview OWNER TO postgres;

--
-- TOC entry 743 (class 1259 OID 23440)
-- Name: pls_schum_edi_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE pls_schum_edi_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE pls_schum_edi_seq OWNER TO postgres;

--
-- TOC entry 852 (class 1259 OID 24660)
-- Name: pls_unbilled_rev_t; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE pls_unbilled_rev_t (
    org_id integer NOT NULL,
    bill_to_id integer NOT NULL,
    network_id integer,
    company character varying(10),
    customer_name character varying(50) NOT NULL,
    customer_number character varying(31),
    unbilled_rev integer
);


ALTER TABLE pls_unbilled_rev_t OWNER TO postgres;

--
-- TOC entry 744 (class 1259 OID 23442)
-- Name: por_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE por_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE por_seq OWNER TO postgres;

--
-- TOC entry 386 (class 1259 OID 19715)
-- Name: prepaid_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE prepaid_details (
    id bigint NOT NULL,
    load_id bigint NOT NULL,
    payment_id character varying(50),
    amount numeric(10,2),
    payment_date timestamp without time zone,
    version integer,
    transaction_id character varying(50)
);


ALTER TABLE prepaid_details OWNER TO postgres;

--
-- TOC entry 745 (class 1259 OID 23444)
-- Name: pricing_third_party_info_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE pricing_third_party_info_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE pricing_third_party_info_seq OWNER TO postgres;

--
-- TOC entry 749 (class 1259 OID 23452)
-- Name: pro_number_block_lock_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE pro_number_block_lock_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE pro_number_block_lock_seq OWNER TO postgres;

--
-- TOC entry 750 (class 1259 OID 23454)
-- Name: pro_number_block_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE pro_number_block_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE pro_number_block_seq OWNER TO postgres;

--
-- TOC entry 746 (class 1259 OID 23446)
-- Name: prodorder_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE prodorder_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE prodorder_seq OWNER TO postgres;

--
-- TOC entry 387 (class 1259 OID 19720)
-- Name: promo_code_ae; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE promo_code_ae (
    promo_code_ae_id bigint NOT NULL,
    person_id bigint,
    promo_code character varying(100),
    percentage integer,
    status character varying(1),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    terms_and_conditions_version bigint
);


ALTER TABLE promo_code_ae OWNER TO postgres;

--
-- TOC entry 747 (class 1259 OID 23448)
-- Name: promo_code_ae_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE promo_code_ae_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE promo_code_ae_seq OWNER TO postgres;

--
-- TOC entry 388 (class 1259 OID 19725)
-- Name: promo_code_load; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE promo_code_load (
    promo_code_load_id bigint NOT NULL,
    promo_code_ae_id bigint,
    load_id bigint,
    status character varying(1),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint
);


ALTER TABLE promo_code_load OWNER TO postgres;

--
-- TOC entry 748 (class 1259 OID 23450)
-- Name: promo_code_load_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE promo_code_load_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE promo_code_load_seq OWNER TO postgres;

--
-- TOC entry 751 (class 1259 OID 23456)
-- Name: pur_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE pur_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE pur_seq OWNER TO postgres;

--
-- TOC entry 752 (class 1259 OID 23458)
-- Name: quote_requests_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE quote_requests_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE quote_requests_seq OWNER TO postgres;

--
-- TOC entry 754 (class 1259 OID 23462)
-- Name: ra_terms_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ra_terms_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ra_terms_seq OWNER TO postgres;

--
-- TOC entry 753 (class 1259 OID 23460)
-- Name: rail_tracking_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE rail_tracking_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rail_tracking_seq OWNER TO postgres;

--
-- TOC entry 755 (class 1259 OID 23464)
-- Name: rdf_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE rdf_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rdf_seq OWNER TO postgres;

--
-- TOC entry 756 (class 1259 OID 23466)
-- Name: rdoc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE rdoc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rdoc_seq OWNER TO postgres;

--
-- TOC entry 757 (class 1259 OID 23468)
-- Name: report_page_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE report_page_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE report_page_seq OWNER TO postgres;

--
-- TOC entry 758 (class 1259 OID 23470)
-- Name: report_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE report_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE report_seq OWNER TO postgres;

--
-- TOC entry 759 (class 1259 OID 23472)
-- Name: revenue_alloc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE revenue_alloc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE revenue_alloc_seq OWNER TO postgres;

--
-- TOC entry 760 (class 1259 OID 23474)
-- Name: rl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE rl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rl_seq OWNER TO postgres;

--
-- TOC entry 761 (class 1259 OID 23476)
-- Name: rol_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE rol_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rol_seq OWNER TO postgres;

--
-- TOC entry 392 (class 1259 OID 19754)
-- Name: routes; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE routes (
    route_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    orig_city character varying(30),
    orig_state character varying(6),
    orig_zip character varying(10),
    dest_city character varying(30),
    dest_state character varying(6),
    dest_zip character varying(10),
    orig_country character varying(3),
    dest_country character varying(3)
);


ALTER TABLE routes OWNER TO postgres;

--
-- TOC entry 762 (class 1259 OID 23478)
-- Name: routes_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE routes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE routes_seq OWNER TO postgres;

--
-- TOC entry 763 (class 1259 OID 23480)
-- Name: rtc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE rtc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rtc_seq OWNER TO postgres;

--
-- TOC entry 764 (class 1259 OID 23482)
-- Name: rtmp_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE rtmp_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rtmp_seq OWNER TO postgres;

--
-- TOC entry 765 (class 1259 OID 23484)
-- Name: rtnm_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE rtnm_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rtnm_seq OWNER TO postgres;

--
-- TOC entry 767 (class 1259 OID 23488)
-- Name: saved_quote_accessorials_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE saved_quote_accessorials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE saved_quote_accessorials_seq OWNER TO postgres;

--
-- TOC entry 768 (class 1259 OID 23490)
-- Name: saved_quote_cost_det_items_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE saved_quote_cost_det_items_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE saved_quote_cost_det_items_seq OWNER TO postgres;

--
-- TOC entry 769 (class 1259 OID 23492)
-- Name: saved_quote_cost_det_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE saved_quote_cost_det_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE saved_quote_cost_det_seq OWNER TO postgres;

--
-- TOC entry 394 (class 1259 OID 19777)
-- Name: saved_quote_materials; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE saved_quote_materials (
    quote_material_id bigint NOT NULL,
    quote_id bigint NOT NULL,
    weight numeric(10,2),
    length numeric(10,2),
    width numeric(10,2),
    height numeric(10,2),
    product_code character varying(30),
    product_description character varying(1000),
    commodity_class_code character varying(8),
    nmfc character varying(30),
    hazmat character(1) DEFAULT 'N'::bpchar NOT NULL,
    hazmat_class character varying(100),
    hazmat_instructions character varying(2000),
    packing_group character varying(32),
    un_num character varying(32),
    emergency_company character varying(32),
    emergency_contract character varying(32),
    emergency_country_code character varying(10),
    emergency_area_code character varying(10),
    emergency_number character varying(32),
    package_type character varying(100),
    pieces integer,
    stackable character(1) DEFAULT 'N'::bpchar NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer DEFAULT 1 NOT NULL,
    quantity integer,
    emergency_extension character varying(6)
);


ALTER TABLE saved_quote_materials OWNER TO postgres;

--
-- TOC entry 770 (class 1259 OID 23494)
-- Name: saved_quote_materials_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE saved_quote_materials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE saved_quote_materials_seq OWNER TO postgres;

--
-- TOC entry 395 (class 1259 OID 19789)
-- Name: saved_quote_pric_dtls; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE saved_quote_pric_dtls (
    saved_quote_pric_dtls_id bigint NOT NULL,
    quote_id bigint NOT NULL,
    smc3_minimum_charge numeric(10,2),
    total_charge_from_smc3 numeric(10,2),
    deficit_charge_from_smc3 numeric(10,2),
    cost_after_discount numeric(10,2),
    minimum_cost numeric(10,2),
    cost_discount numeric(10,2),
    carrier_fs_id bigint,
    carrier_fuel_discount numeric(10,2),
    pricing_type character varying(30),
    movement_type character varying(30),
    effective_date timestamp without time zone,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer
);


ALTER TABLE saved_quote_pric_dtls OWNER TO postgres;

--
-- TOC entry 771 (class 1259 OID 23496)
-- Name: saved_quote_pric_dtls_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE saved_quote_pric_dtls_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE saved_quote_pric_dtls_seq OWNER TO postgres;

--
-- TOC entry 396 (class 1259 OID 19794)
-- Name: saved_quote_pric_mat_dtls; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE saved_quote_pric_mat_dtls (
    saved_quote_pric_mat_dtls_id bigint NOT NULL,
    saved_quote_pric_dtls_id bigint NOT NULL,
    charge character varying(30),
    nmfc_class character varying(30),
    entered_nmfc_class character varying(30),
    rate character varying(30),
    weight character varying(30),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer
);


ALTER TABLE saved_quote_pric_mat_dtls OWNER TO postgres;

--
-- TOC entry 772 (class 1259 OID 23498)
-- Name: saved_quote_pric_mat_dtls_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE saved_quote_pric_mat_dtls_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE saved_quote_pric_mat_dtls_seq OWNER TO postgres;

--
-- TOC entry 393 (class 1259 OID 19764)
-- Name: saved_quotes; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE saved_quotes (
    quote_id bigint NOT NULL,
    org_id bigint,
    carrier_org_id bigint NOT NULL,
    route_id bigint NOT NULL,
    quote_reference_number character varying(30),
    special_message character varying(3000),
    status character varying(2) NOT NULL,
    carrier_reference_number character varying(50),
    po_num character varying(50),
    pickup_num character varying(2000),
    bol character varying(20),
    gl_number character varying(50),
    so_number character varying(50),
    trailer character varying(30),
    person_id bigint,
    mileage integer,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer DEFAULT 1 NOT NULL,
    mileage_type character varying(2),
    pickup timestamp without time zone,
    volume_quote_id character varying(50),
    revenue_override character(1),
    cost_override character(1),
    blocked_from_booking character(1) DEFAULT 'N'::bpchar
);


ALTER TABLE saved_quotes OWNER TO postgres;

--
-- TOC entry 766 (class 1259 OID 23486)
-- Name: saved_quotes_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE saved_quotes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE saved_quotes_seq OWNER TO postgres;

--
-- TOC entry 773 (class 1259 OID 23500)
-- Name: scheduled_task_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE scheduled_task_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE scheduled_task_seq OWNER TO postgres;

--
-- TOC entry 774 (class 1259 OID 23502)
-- Name: schedules_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE schedules_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE schedules_seq OWNER TO postgres;

--
-- TOC entry 775 (class 1259 OID 23504)
-- Name: schedules_xref_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE schedules_xref_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE schedules_xref_seq OWNER TO postgres;

--
-- TOC entry 776 (class 1259 OID 23506)
-- Name: search_filter_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE search_filter_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE search_filter_seq OWNER TO postgres;

--
-- TOC entry 777 (class 1259 OID 23508)
-- Name: search_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE search_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE search_seq OWNER TO postgres;

--
-- TOC entry 778 (class 1259 OID 23510)
-- Name: search_sort_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE search_sort_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE search_sort_seq OWNER TO postgres;

--
-- TOC entry 779 (class 1259 OID 23512)
-- Name: ship_alerts_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ship_alerts_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ship_alerts_seq OWNER TO postgres;

--
-- TOC entry 398 (class 1259 OID 19804)
-- Name: shipment_alerts; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE shipment_alerts (
    alert_id bigint NOT NULL,
    load_id bigint NOT NULL,
    acknow_user_id bigint,
    type character(3) NOT NULL,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint,
    version bigint NOT NULL,
    org_id bigint
);


ALTER TABLE shipment_alerts OWNER TO postgres;

--
-- TOC entry 780 (class 1259 OID 23514)
-- Name: shortcut_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE shortcut_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE shortcut_seq OWNER TO postgres;

--
-- TOC entry 399 (class 1259 OID 19814)
-- Name: smc3_error_codes; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE smc3_error_codes (
    smc3_error_code_id bigint NOT NULL,
    code character varying(3) NOT NULL,
    description character varying(256),
    error_type character varying(10),
    resolution character varying(4000)
);


ALTER TABLE smc3_error_codes OWNER TO postgres;

--
-- TOC entry 781 (class 1259 OID 23516)
-- Name: smc3_error_codes_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE smc3_error_codes_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE smc3_error_codes_seq OWNER TO postgres;

--
-- TOC entry 400 (class 1259 OID 19822)
-- Name: smc3_tariffs; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE smc3_tariffs (
    smc3_tariff_id bigint NOT NULL,
    tariff_name character varying(255) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL
);


ALTER TABLE smc3_tariffs OWNER TO postgres;

--
-- TOC entry 782 (class 1259 OID 23518)
-- Name: smc3_tariffs_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE smc3_tariffs_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE smc3_tariffs_seq OWNER TO postgres;

--
-- TOC entry 783 (class 1259 OID 23520)
-- Name: snedi_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE snedi_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE snedi_seq OWNER TO postgres;

--
-- TOC entry 784 (class 1259 OID 23522)
-- Name: splc_rail_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE splc_rail_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE splc_rail_seq OWNER TO postgres;

--
-- TOC entry 401 (class 1259 OID 19828)
-- Name: states; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE states (
    state_code character varying(6) NOT NULL,
    state_name character varying(50) NOT NULL,
    country_code character varying(3) NOT NULL
);


ALTER TABLE states OWNER TO postgres;

--
-- TOC entry 785 (class 1259 OID 23524)
-- Name: static_label_lang_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE static_label_lang_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE static_label_lang_seq OWNER TO postgres;

--
-- TOC entry 786 (class 1259 OID 23526)
-- Name: static_value_group_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE static_value_group_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE static_value_group_seq OWNER TO postgres;

--
-- TOC entry 787 (class 1259 OID 23528)
-- Name: static_value_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE static_value_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE static_value_seq OWNER TO postgres;

--
-- TOC entry 404 (class 1259 OID 19858)
-- Name: sv_qt_cost_detail_items; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE sv_qt_cost_detail_items (
    item_id bigint NOT NULL,
    quote_cost_detail_id bigint NOT NULL,
    ref_type character varying(10) NOT NULL,
    subtotal numeric(10,2),
    quantity numeric(10,2),
    ship_carr character(1),
    unit_type character varying(10),
    unit_cost numeric(10,2),
    amount_uom character varying(3),
    carrier_org_id bigint,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer DEFAULT 1 NOT NULL
);


ALTER TABLE sv_qt_cost_detail_items OWNER TO postgres;

--
-- TOC entry 403 (class 1259 OID 19848)
-- Name: sv_qt_cost_details; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE sv_qt_cost_details (
    quote_cost_detail_id bigint NOT NULL,
    quote_id bigint NOT NULL,
    total_revenue numeric(10,2),
    total_costs numeric(10,2),
    status character(1),
    tariff_name character varying(255),
    est_transit_date timestamp without time zone,
    travel_time integer,
    new_prod_liab_amt numeric(10,2),
    used_prod_liab_amt numeric(10,2),
    service_type character varying(20),
    prohibited_commodities character varying(500),
    guaran_time bigint,
    guaran_bol_name character varying(500),
    pric_prof_detail_id bigint,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer DEFAULT 1 NOT NULL
);


ALTER TABLE sv_qt_cost_details OWNER TO postgres;

--
-- TOC entry 405 (class 1259 OID 19865)
-- Name: sv_qt_ltl_accessorials; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE sv_qt_ltl_accessorials (
    accessorial_id bigint NOT NULL,
    quote_id bigint NOT NULL,
    accessorial_type_code character varying(10) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer DEFAULT 1 NOT NULL
);


ALTER TABLE sv_qt_ltl_accessorials OWNER TO postgres;

--
-- TOC entry 789 (class 1259 OID 23532)
-- Name: task_groups_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE task_groups_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE task_groups_seq OWNER TO postgres;

--
-- TOC entry 790 (class 1259 OID 23534)
-- Name: task_params_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE task_params_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE task_params_seq OWNER TO postgres;

--
-- TOC entry 791 (class 1259 OID 23536)
-- Name: task_scheduled_params_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE task_scheduled_params_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE task_scheduled_params_seq OWNER TO postgres;

--
-- TOC entry 788 (class 1259 OID 23530)
-- Name: tasks_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE tasks_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tasks_seq OWNER TO postgres;

--
-- TOC entry 792 (class 1259 OID 23538)
-- Name: tc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE tc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tc_seq OWNER TO postgres;

--
-- TOC entry 406 (class 1259 OID 19873)
-- Name: team; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE team (
    team_id bigint NOT NULL,
    name character varying(30) NOT NULL,
    branch_id bigint NOT NULL,
    created_by bigint NOT NULL,
    created_date timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    modified_date timestamp without time zone NOT NULL,
    version integer NOT NULL,
    description character varying(255),
    rev_alloc_tracking_id bigint NOT NULL,
    financials_code character(2) NOT NULL,
    visible character varying(1) DEFAULT 'Y'::character varying,
    alias_name character varying(200),
    distribution_list character varying(500),
    phone_number character varying(50),
    team_type character varying(10) DEFAULT 'AE'::character varying,
    its_login character varying(20),
    its_password character varying(40),
    address character varying(200),
    its_dispatch character varying(30),
    its_email character varying(40)
);


ALTER TABLE team OWNER TO postgres; 

--
-- TOC entry 6374 (class 0 OID 0)
-- Dependencies: 406
-- Name: TABLE team; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON TABLE team IS 'A Freight Solutions Team - belongs to a Branch and aggregates team members.';

--
-- TOC entry 6375 (class 0 OID 0)
-- Dependencies: 406
-- Name: COLUMN team.rev_alloc_tracking_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN team.rev_alloc_tracking_id IS 'Revenue sharing rules for the Team. Points to the tracking id since can change over time';


--
-- TOC entry 6376 (class 0 OID 0)
-- Dependencies: 406
-- Name: COLUMN team.phone_number; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN team.phone_number IS 'Phone number for team-used mostly in posting';


--
-- TOC entry 6377 (class 0 OID 0)
-- Dependencies: 406
-- Name: COLUMN team.its_login; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN team.its_login IS 'ITS_LOGIN';


--
-- TOC entry 6378 (class 0 OID 0)
-- Dependencies: 406
-- Name: COLUMN team.its_password; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN team.its_password IS 'ITS_PASSWORD';


--
-- TOC entry 6379 (class 0 OID 0)
-- Dependencies: 406
-- Name: COLUMN team.address; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN team.address IS 'ADDRESS';


--
-- TOC entry 6380 (class 0 OID 0)
-- Dependencies: 406
-- Name: COLUMN team.its_dispatch; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN team.its_dispatch IS 'ITS_DISPATCH';


--
-- TOC entry 6381 (class 0 OID 0)
-- Dependencies: 406
-- Name: COLUMN team.its_email; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN team.its_email IS 'ITS_EMAIL';

--
-- TOC entry 793 (class 1259 OID 23540)
-- Name: team_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE team_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE team_seq OWNER TO postgres;

--
-- TOC entry 794 (class 1259 OID 23542)
-- Name: team_user_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE team_user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE team_user_seq OWNER TO postgres;

--
-- TOC entry 795 (class 1259 OID 23544)
-- Name: temp_fs_bill_to_xref_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE temp_fs_bill_to_xref_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE temp_fs_bill_to_xref_seq OWNER TO postgres;

--
-- TOC entry 796 (class 1259 OID 23546)
-- Name: temp_fs_copy_comments_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE temp_fs_copy_comments_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE temp_fs_copy_comments_seq OWNER TO postgres;

--
-- TOC entry 797 (class 1259 OID 23548)
-- Name: temp_fs_docks_xref_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE temp_fs_docks_xref_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE temp_fs_docks_xref_seq OWNER TO postgres;

--
-- TOC entry 798 (class 1259 OID 23550)
-- Name: temp_fs_loc_xref_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE temp_fs_loc_xref_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE temp_fs_loc_xref_seq OWNER TO postgres;

--
-- TOC entry 799 (class 1259 OID 23552)
-- Name: temp_fs_org_user_xref_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE temp_fs_org_user_xref_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE temp_fs_org_user_xref_seq OWNER TO postgres;

--
-- TOC entry 800 (class 1259 OID 23554)
-- Name: temp_fs_org_xref_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE temp_fs_org_xref_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE temp_fs_org_xref_seq OWNER TO postgres;

--
-- TOC entry 801 (class 1259 OID 23556)
-- Name: temp_upd_lcd_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE temp_upd_lcd_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE temp_upd_lcd_seq OWNER TO postgres;

--
-- TOC entry 802 (class 1259 OID 23558)
-- Name: tendering_locks_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE tendering_locks_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tendering_locks_seq OWNER TO postgres;

--
-- TOC entry 803 (class 1259 OID 23560)
-- Name: tendering_plan_exception_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE tendering_plan_exception_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tendering_plan_exception_seq OWNER TO postgres;

--
-- TOC entry 804 (class 1259 OID 23562)
-- Name: tendering_plan_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE tendering_plan_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tendering_plan_seq OWNER TO postgres;

--
-- TOC entry 805 (class 1259 OID 23564)
-- Name: tendering_results_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE tendering_results_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tendering_results_seq OWNER TO postgres;

--
-- TOC entry 806 (class 1259 OID 23566)
-- Name: tendering_rule_markets_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE tendering_rule_markets_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tendering_rule_markets_seq OWNER TO postgres;

--
-- TOC entry 407 (class 1259 OID 19887)
-- Name: timezone; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE timezone (
    timezone_code character varying(10) NOT NULL,
    timezone_name character varying(40) NOT NULL,
    local_offset real NOT NULL,
    timezone real
);


ALTER TABLE timezone OWNER TO postgres;

--
-- TOC entry 807 (class 1259 OID 23568)
-- Name: tlog_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE tlog_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tlog_seq OWNER TO postgres;

--
-- TOC entry 808 (class 1259 OID 23570)
-- Name: toad_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE toad_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE toad_seq OWNER TO postgres;


--
-- TOC entry 809 (class 1259 OID 23572)
-- Name: tracking_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE tracking_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tracking_seq OWNER TO postgres;

--
-- TOC entry 810 (class 1259 OID 23574)
-- Name: transcore_dat_loads_batch_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE transcore_dat_loads_batch_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE transcore_dat_loads_batch_seq OWNER TO postgres;

--
-- TOC entry 811 (class 1259 OID 23576)
-- Name: tsl_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE tsl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tsl_seq OWNER TO postgres;

--
-- TOC entry 812 (class 1259 OID 23578)
-- Name: ttmp_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ttmp_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ttmp_seq OWNER TO postgres;

--
-- TOC entry 813 (class 1259 OID 23580)
-- Name: ua_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE ua_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ua_seq OWNER TO postgres;

--
-- TOC entry 814 (class 1259 OID 23582)
-- Name: uim_label_lang_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE uim_label_lang_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE uim_label_lang_seq OWNER TO postgres;

--
-- TOC entry 815 (class 1259 OID 23584)
-- Name: uim_label_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE uim_label_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE uim_label_seq OWNER TO postgres;

--
-- TOC entry 816 (class 1259 OID 23586)
-- Name: uim_lang_user_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE uim_lang_user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE uim_lang_user_seq OWNER TO postgres;

--
-- TOC entry 817 (class 1259 OID 23588)
-- Name: uim_property_assoc_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE uim_property_assoc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE uim_property_assoc_seq OWNER TO postgres;

--
-- TOC entry 818 (class 1259 OID 23590)
-- Name: uim_property_value_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE uim_property_value_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE uim_property_value_seq OWNER TO postgres;

--
-- TOC entry 819 (class 1259 OID 23592)
-- Name: uim_relationship_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE uim_relationship_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE uim_relationship_seq OWNER TO postgres;

--
-- TOC entry 820 (class 1259 OID 23594)
-- Name: uim_search_filter_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE uim_search_filter_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE uim_search_filter_seq OWNER TO postgres;

--
-- TOC entry 821 (class 1259 OID 23596)
-- Name: uim_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE uim_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE uim_seq OWNER TO postgres;

--
-- TOC entry 822 (class 1259 OID 23598)
-- Name: uim_view_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE uim_view_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE uim_view_seq OWNER TO postgres;

--
-- TOC entry 823 (class 1259 OID 23600)
-- Name: uim_view_type_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE uim_view_type_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE uim_view_type_seq OWNER TO postgres;

--
-- TOC entry 410 (class 1259 OID 19920)
-- Name: user_addl_contact_info; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE user_addl_contact_info (
    usr_addl_cnt_info_id bigint NOT NULL,
    person_id bigint NOT NULL,
    contact_name character varying(200),
    email_address character varying(100),
    phone_id bigint,
    contact_type character varying(30),
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version bigint DEFAULT 1
);


ALTER TABLE user_addl_contact_info OWNER TO postgres;

--
-- TOC entry 824 (class 1259 OID 23602)
-- Name: user_addl_contact_info_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE user_addl_contact_info_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE;


ALTER TABLE user_addl_contact_info_seq OWNER TO postgres;

--
-- TOC entry 412 (class 1259 OID 19941)
-- Name: user_address_book; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE user_address_book (
    addr_book_id bigint NOT NULL,
    address_name character varying(255) NOT NULL,
    person_id bigint,
    address_id bigint NOT NULL,
    address_code character varying(255) NOT NULL,
    email_address character varying(255),
    pickup_notes character varying(3000),
    delivery_notes character varying(3000),
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    phone_id bigint,
    fax_id bigint,
    contact_name character varying(255),
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    version bigint DEFAULT 1 NOT NULL,
    org_id bigint,
    type character varying(1) DEFAULT 'S'::character varying,
    is_default character(1) DEFAULT 'N'::bpchar,
    int_delivery_notes character varying(3000),
    int_pickup_notes character varying(3000),
    pickup_from_time time without time zone,
    pickup_to_time time without time zone,
    delivery_from_time time without time zone,
    delivery_to_time time without time zone
);


ALTER TABLE user_address_book OWNER TO postgres;

--
-- TOC entry 411 (class 1259 OID 19928)
-- Name: user_addresses; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE user_addresses (
    user_address_id bigint NOT NULL,
    person_id bigint NOT NULL,
    address_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    org_user_id bigint,
    version integer
);


ALTER TABLE user_addresses OWNER TO postgres;


--
-- TOC entry 414 (class 1259 OID 19966)
-- Name: user_capabilities_xref; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE user_capabilities_xref (
    user_capability_id bigint NOT NULL,
    capability_id bigint NOT NULL,
    person_id bigint NOT NULL,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    CONSTRAINT usr_cap_xref_status_values CHECK ((status = ANY (ARRAY['A'::bpchar, 'I'::bpchar])))
);


ALTER TABLE user_capabilities_xref OWNER TO postgres;

--
-- TOC entry 6384 (class 0 OID 0)
-- Dependencies: 414
-- Name: TABLE user_capabilities_xref; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON TABLE user_capabilities_xref IS 'Junction table for Users-Capabilities relation. LTL Users have dirrect association with user, not with ORG_USER.';


--
-- TOC entry 6385 (class 0 OID 0)
-- Dependencies: 414
-- Name: COLUMN user_capabilities_xref.user_capability_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_capabilities_xref.user_capability_id IS 'PK for this table';


--
-- TOC entry 6386 (class 0 OID 0)
-- Dependencies: 414
-- Name: COLUMN user_capabilities_xref.capability_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_capabilities_xref.capability_id IS 'Related capability Refers to CAPABILITIES.CAPABILITY_ID';


--
-- TOC entry 6387 (class 0 OID 0)
-- Dependencies: 414
-- Name: COLUMN user_capabilities_xref.person_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_capabilities_xref.person_id IS 'Related User Refers to USERS.PERSON_ID';


--
-- TOC entry 6388 (class 0 OID 0)
-- Dependencies: 414
-- Name: COLUMN user_capabilities_xref.status; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_capabilities_xref.status IS 'A - active, I - inactive';


--
-- TOC entry 6389 (class 0 OID 0)
-- Dependencies: 414
-- Name: COLUMN user_capabilities_xref.date_created; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_capabilities_xref.date_created IS 'When this record was created';


--
-- TOC entry 6390 (class 0 OID 0)
-- Dependencies: 414
-- Name: COLUMN user_capabilities_xref.created_by; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_capabilities_xref.created_by IS 'Who created this record. Refers to USERS.PERSON_ID';


--
-- TOC entry 6391 (class 0 OID 0)
-- Dependencies: 414
-- Name: COLUMN user_capabilities_xref.date_modified; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_capabilities_xref.date_modified IS 'When this record was updated last time';


--
-- TOC entry 6392 (class 0 OID 0)
-- Dependencies: 414
-- Name: COLUMN user_capabilities_xref.modified_by; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_capabilities_xref.modified_by IS 'Who updated this record last time. Refers to USERS.PERSON_ID';


--
-- TOC entry 6393 (class 0 OID 0)
-- Dependencies: 414
-- Name: COLUMN user_capabilities_xref.version; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_capabilities_xref.version IS 'Version for optimistic locking';


--
-- TOC entry 825 (class 1259 OID 23604)
-- Name: user_capabilities_xref_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE user_capabilities_xref_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE user_capabilities_xref_seq OWNER TO postgres;

--
-- TOC entry 826 (class 1259 OID 23606)
-- Name: user_customer_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE user_customer_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE user_customer_seq OWNER TO postgres;

--
-- TOC entry 416 (class 1259 OID 19994)
-- Name: user_groups; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE user_groups (
    user_group_id bigint NOT NULL,
    group_id bigint NOT NULL,
    person_id bigint NOT NULL,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    CONSTRAINT user_groups_status_values CHECK ((status = ANY (ARRAY['A'::bpchar, 'I'::bpchar])))
);


ALTER TABLE user_groups OWNER TO postgres;

--
-- TOC entry 6394 (class 0 OID 0)
-- Dependencies: 416
-- Name: TABLE user_groups; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON TABLE user_groups IS 'Junction table for Groups-Users relation. LTL User Groups have dirrect association with user, not with ORG_USER.';


--
-- TOC entry 6395 (class 0 OID 0)
-- Dependencies: 416
-- Name: COLUMN user_groups.user_group_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_groups.user_group_id IS 'PK for this table';


--
-- TOC entry 6396 (class 0 OID 0)
-- Dependencies: 416
-- Name: COLUMN user_groups.group_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_groups.group_id IS 'Related Group. Refers to GROUPS.GROUP_ID';


--
-- TOC entry 6397 (class 0 OID 0)
-- Dependencies: 416
-- Name: COLUMN user_groups.person_id; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_groups.person_id IS 'Related User Refers to USERS.PERSON_ID';


--
-- TOC entry 6398 (class 0 OID 0)
-- Dependencies: 416
-- Name: COLUMN user_groups.status; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_groups.status IS 'A - active, I - inactive';


--
-- TOC entry 6399 (class 0 OID 0)
-- Dependencies: 416
-- Name: COLUMN user_groups.date_created; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_groups.date_created IS 'When this record was created';


--
-- TOC entry 6400 (class 0 OID 0)
-- Dependencies: 416
-- Name: COLUMN user_groups.created_by; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_groups.created_by IS 'Who created this record. Refers to USERS.PERSON_ID';


--
-- TOC entry 6401 (class 0 OID 0)
-- Dependencies: 416
-- Name: COLUMN user_groups.date_modified; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_groups.date_modified IS 'When this record was updated last time';


--
-- TOC entry 6402 (class 0 OID 0)
-- Dependencies: 416
-- Name: COLUMN user_groups.modified_by; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_groups.modified_by IS 'Who updated this record last time. Refers to USERS.PERSON_ID';


--
-- TOC entry 6403 (class 0 OID 0)
-- Dependencies: 416
-- Name: COLUMN user_groups.version; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_groups.version IS 'Version for optimistic locking';


--
-- TOC entry 827 (class 1259 OID 23608)
-- Name: user_groups_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE user_groups_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE user_groups_seq OWNER TO postgres;

--
-- TOC entry 417 (class 1259 OID 20009)
-- Name: user_notifications; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE user_notifications (
    user_notification_id bigint NOT NULL,
    org_user_id bigint NOT NULL,
    notification_type character varying(32) NOT NULL,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    date_created timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone DEFAULT ('now'::text)::timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    CONSTRAINT user_nots_status_values CHECK ((status = ANY (ARRAY['A'::bpchar, 'I'::bpchar])))
);


ALTER TABLE user_notifications OWNER TO postgres;

--
-- TOC entry 6404 (class 0 OID 0)
-- Dependencies: 417
-- Name: COLUMN user_notifications.status; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_notifications.status IS 'A - active, I - inactive';


--
-- TOC entry 6405 (class 0 OID 0)
-- Dependencies: 417
-- Name: COLUMN user_notifications.date_created; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_notifications.date_created IS 'When this record was created';


--
-- TOC entry 6406 (class 0 OID 0)
-- Dependencies: 417
-- Name: COLUMN user_notifications.created_by; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_notifications.created_by IS 'Who created this record. Refers to USERS.PERSON_ID';


--
-- TOC entry 6407 (class 0 OID 0)
-- Dependencies: 417
-- Name: COLUMN user_notifications.date_modified; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_notifications.date_modified IS 'When this record was updated last time';


--
-- TOC entry 6408 (class 0 OID 0)
-- Dependencies: 417
-- Name: COLUMN user_notifications.modified_by; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_notifications.modified_by IS 'Who updated this record last time. Refers to USERS.PERSON_ID';


--
-- TOC entry 6409 (class 0 OID 0)
-- Dependencies: 417
-- Name: COLUMN user_notifications.version; Type: COMMENT; Schema: flatbed; Owner: postgres
--

COMMENT ON COLUMN user_notifications.version IS 'Version for optimistic locking';


--
-- TOC entry 828 (class 1259 OID 23610)
-- Name: user_notofications_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE user_notofications_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE user_notofications_seq OWNER TO postgres;

--
-- TOC entry 418 (class 1259 OID 20021)
-- Name: user_phones; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE user_phones (
    user_phone_id bigint NOT NULL,
    person_id bigint NOT NULL,
    dialing_code character varying(3) DEFAULT '001'::character varying NOT NULL,
    area_code character varying(6),
    phone_number character varying(10) NOT NULL,
    extension character varying(6),
    phone_type character varying(10) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone NOT NULL,
    modified_by bigint NOT NULL,
    org_user_id bigint,
    status character(1) DEFAULT 'A'::bpchar,
    version integer
);


ALTER TABLE user_phones OWNER TO postgres;

--
-- TOC entry 829 (class 1259 OID 23612)
-- Name: user_status_history_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE user_status_history_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE user_status_history_seq OWNER TO postgres;

--
-- TOC entry 830 (class 1259 OID 23614)
-- Name: user_token_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE user_token_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE user_token_seq OWNER TO postgres;

--
-- TOC entry 833 (class 1259 OID 23623)
-- Name: usr_addr_book_addr_code_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE usr_addr_book_addr_code_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE usr_addr_book_addr_code_seq OWNER TO postgres;

--
-- TOC entry 834 (class 1259 OID 23625)
-- Name: usr_addr_book_name_numb_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE usr_addr_book_name_numb_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE usr_addr_book_name_numb_seq OWNER TO postgres;

--
-- TOC entry 835 (class 1259 OID 23627)
-- Name: usr_addr_book_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE usr_addr_book_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE usr_addr_book_seq OWNER TO postgres;

--
-- TOC entry 836 (class 1259 OID 23629)
-- Name: usr_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE usr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE usr_seq OWNER TO postgres;

--
-- TOC entry 831 (class 1259 OID 23618)
-- Name: usra_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE usra_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE usra_seq OWNER TO postgres;

--
-- TOC entry 832 (class 1259 OID 23621)
-- Name: usrp_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE usrp_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE usrp_seq OWNER TO postgres;

--
-- TOC entry 837 (class 1259 OID 23631)
-- Name: vend_inb_exception_log_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE vend_inb_exception_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE vend_inb_exception_log_seq OWNER TO postgres;

--
-- TOC entry 838 (class 1259 OID 23633)
-- Name: virtual_radar_event_log_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE virtual_radar_event_log_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE virtual_radar_event_log_seq OWNER TO postgres;

--
-- TOC entry 840 (class 1259 OID 23637)
-- Name: virtual_radar_param_value_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE virtual_radar_param_value_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE virtual_radar_param_value_seq OWNER TO postgres;

--
-- TOC entry 839 (class 1259 OID 23635)
-- Name: virtual_radar_parameter_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE virtual_radar_parameter_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE virtual_radar_parameter_seq OWNER TO postgres;

--
-- TOC entry 841 (class 1259 OID 23639)
-- Name: w9_document_seq; Type: SEQUENCE; Schema: flatbed; Owner: postgres
--

CREATE SEQUENCE w9_document_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE w9_document_seq OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 17448)
-- Name: zipcodes; Type: TABLE; Schema: flatbed; Owner: postgres
--

CREATE TABLE zipcodes (
    warning_flag character varying(1),
    country_code character(3),
    detail_id integer,
    zip_code character varying(7),
    state_code character varying(20),
    city character varying(50),
    pref_last_line_name character varying(30),
    county_name character varying(25),
    latitude character varying(100),
    longitude character varying(100),
    time_zone character varying(40),
    day_light_saving character(1)
);


ALTER TABLE zipcodes OWNER TO postgres;

--
-- TOC entry 6225 (class 0 OID 0)
-- Dependencies: 6
-- Name: flatbed; Type: ACL; Schema: -; Owner: cloudsqlsuperuser
--

GRANT ALL ON SCHEMA flatbed TO PUBLIC;


-- Completed on 2017-10-26 05:53:31 EDT

--
-- PostgreSQL database dump complete
--

-- ACTIVE_MQ tables

CREATE TABLE flatbed.activemq_acks
(
    container character varying(250) COLLATE pg_catalog."default" NOT NULL,
    sub_dest character varying(250) COLLATE pg_catalog."default",
    client_id character varying(250) COLLATE pg_catalog."default" NOT NULL,
    sub_name character varying(250) COLLATE pg_catalog."default" NOT NULL,
    selector character varying(250) COLLATE pg_catalog."default",
    last_acked_id bigint,
    priority bigint NOT NULL DEFAULT 5,
    xid character varying(250) COLLATE pg_catalog."default",
    CONSTRAINT activemq_acks_pkey PRIMARY KEY (container, client_id, sub_name, priority)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE flatbed.activemq_acks
    OWNER to flatbed;

-- Index: activemq_acks_xidx

CREATE INDEX activemq_acks_xidx
    ON flatbed.activemq_acks USING btree
    (xid COLLATE pg_catalog."default")
    TABLESPACE pg_default;

CREATE TABLE flatbed.activemq_lock
(
    id bigint NOT NULL,
    "time" bigint,
    broker_name character varying(250) COLLATE pg_catalog."default",
    CONSTRAINT activemq_lock_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE flatbed.activemq_lock
    OWNER to flatbed;

-- Table: flatbed.activemq_msgs

-- DROP TABLE flatbed.activemq_msgs;

CREATE TABLE flatbed.activemq_msgs
(
    id bigint NOT NULL,
    container character varying(250) COLLATE pg_catalog."default",
    msgid_prod character varying(250) COLLATE pg_catalog."default",
    msgid_seq bigint,
    expiration bigint,
    msg bytea,
    priority bigint,
    xid character varying(250) COLLATE pg_catalog."default",
    CONSTRAINT activemq_msgs_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE flatbed.activemq_msgs
    OWNER to flatbed;

-- Index: activemq_msgs_cidx

-- DROP INDEX flatbed.activemq_msgs_cidx;

CREATE INDEX activemq_msgs_cidx
    ON flatbed.activemq_msgs USING btree
    (container COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: activemq_msgs_eidx

-- DROP INDEX flatbed.activemq_msgs_eidx;

CREATE INDEX activemq_msgs_eidx
    ON flatbed.activemq_msgs USING btree
    (expiration)
    TABLESPACE pg_default;

-- Index: activemq_msgs_midx

-- DROP INDEX flatbed.activemq_msgs_midx;

CREATE INDEX activemq_msgs_midx
    ON flatbed.activemq_msgs USING btree
    (msgid_prod COLLATE pg_catalog."default", msgid_seq)
    TABLESPACE pg_default;

-- Index: activemq_msgs_pidx

-- DROP INDEX flatbed.activemq_msgs_pidx;

CREATE INDEX activemq_msgs_pidx
    ON flatbed.activemq_msgs USING btree
    (priority)
    TABLESPACE pg_default;

-- Index: activemq_msgs_xidx

-- DROP INDEX flatbed.activemq_msgs_xidx;

CREATE INDEX activemq_msgs_xidx
    ON flatbed.activemq_msgs USING btree
    (xid COLLATE pg_catalog."default")
    TABLESPACE pg_default;


SET search_path = public;

commit;