--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.1
-- Dumped by pg_dump version 10.0

-- Started on 2017-10-23 18:12:04 MSK

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 10 (class 2615 OID 41053)
-- Name: rater; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA rater;


ALTER SCHEMA rater OWNER TO postgres;

SET search_path = rater, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 863 (class 1259 OID 41295)
-- Name: cost_detail_items; Type: TABLE; Schema: rater; Owner: postgres
--

CREATE TABLE cost_detail_items (
    item_id bigint NOT NULL,
    cost_detail_id bigint NOT NULL,
    ref_id bigint NOT NULL,
    ref_type character varying(10) NOT NULL,
    subtotal numeric(10,2),
    quantity numeric(10,2),
    ship_carr character(1) DEFAULT 'S'::bpchar NOT NULL,
    unit_type character varying(10),
    unit_cost numeric(10,2),
    percentage character(1),
    percentage_of character(1),
    amount_uom character varying(3),
    version integer DEFAULT 1 NOT NULL,
    billable_status character(1) DEFAULT 'Y'::bpchar NOT NULL,
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
    note character varying(2000),
    CONSTRAINT cdi_billable_status_chk CHECK ((billable_status = ANY (ARRAY['Y'::bpchar, 'N'::bpchar])))
);


ALTER TABLE cost_detail_items OWNER TO postgres;

--
-- TOC entry 864 (class 1259 OID 41501)
-- Name: load_cost_details; Type: TABLE; Schema: rater; Owner: postgres
--

CREATE TABLE load_cost_details (
    cost_detail_id bigint NOT NULL,
    load_id bigint NOT NULL,
    stopoffs smallint NOT NULL,
    weight bigint,
    miles numeric(10,2),
    total_revenue numeric(10,2) NOT NULL,
    total_costs numeric(10,2) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    unit_cost_id bigint,
    version integer DEFAULT 1 NOT NULL,
    empty_miles smallint,
    truck_weight bigint,
    ship_date timestamp without time zone NOT NULL,
    billing_action character varying(10),
    invoice_number character varying(20),
    bol character varying(20),
    gl_date timestamp without time zone,
    sent_to_finance character(1),
    shipment_status character varying(30),
    accelerated_ind character(1),
    pieces bigint,
    override_margin_discrepancy character(1),
    new_prod_liab_amt numeric(10,2),
    used_prod_liab_amt numeric(10,2),
    service_type character varying(20),
    prohibited_commodities character varying(500),
    customer_invoice_num character varying(20),
    guaran_time bigint,
    guaran_bol_name character varying(500),
    pric_prof_detail_id bigint,
    group_invoice_num character varying(25),
    revenue_override character(1),
    cost_override character(1),
    invoiced_in_finan character(1)
);


ALTER TABLE load_cost_details OWNER TO postgres;

--
-- TOC entry 865 (class 1259 OID 41994)
-- Name: acc_container_types_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE acc_container_types_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE acc_container_types_seq OWNER TO postgres;

--
-- TOC entry 866 (class 1259 OID 41996)
-- Name: acc_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE acc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE acc_seq OWNER TO postgres;

--
-- TOC entry 922 (class 1259 OID 42202)
-- Name: accessorial_types; Type: TABLE; Schema: rater; Owner: postgres
--

CREATE TABLE accessorial_types (
    accessorial_type_code character varying(10) NOT NULL,
    description character varying(50) NOT NULL,
    applicable_to character varying(10),
    accessorial_group character varying(30),
    status character varying(1) DEFAULT 'A'::character varying,
    date_created timestamp without time zone,
    created_by bigint,
    date_modified timestamp without time zone,
    modified_by bigint,
    version integer
);


ALTER TABLE accessorial_types OWNER TO postgres;

--
-- TOC entry 919 (class 1259 OID 42179)
-- Name: accessorials; Type: TABLE; Schema: rater; Owner: postgres
--

CREATE TABLE accessorials (
    accessorial_id bigint NOT NULL,
    ship_carr character(1),
    shipper_org_id bigint NOT NULL,
    shipper_loc_id bigint,
    carrier_org_id bigint,
    acc_unit_type character varying(10) NOT NULL,
    amount numeric(10,2) NOT NULL,
    eff_date timestamp without time zone NOT NULL,
    exp_date timestamp without time zone NOT NULL,
    status character(1) DEFAULT 'A'::bpchar NOT NULL,
    date_created timestamp without time zone NOT NULL,
    created_by bigint NOT NULL,
    date_modified timestamp without time zone,
    modified_by bigint,
    applicable_linehaul character(1) DEFAULT 'S'::bpchar NOT NULL,
    rank smallint NOT NULL,
    amount_uom character varying(3) NOT NULL,
    dedicated_program_id bigint DEFAULT '-1'::integer NOT NULL,
    minimum numeric(10,2),
    ded_pgm_ind character(1) NOT NULL,
    percentage character(1),
    quantity_unit_type character varying(10),
    billing_base_type character(1) DEFAULT 'L'::bpchar NOT NULL,
    version integer NOT NULL,
    accessorial_tracking_id bigint,
    weight_min numeric(10,2),
    weight_max numeric(10,2),
    pieces_min integer,
    pieces_max integer,
    party character(1),
    terms character(1),
    inbound_outbound character(1),
    charge_freight_to character varying(5),
    note character varying(1000),
    radioactive_flag character(1) DEFAULT 'N'::bpchar,
    rate_class_id bigint,
    origin_city character varying(30),
    origin_state character(2),
    origin_country character varying(3),
    dest_city character varying(30),
    dest_state character(2),
    dest_country character varying(3),
    origin_node character varying(50),
    dest_node character varying(50),
    origin_zone bigint,
    dest_zone bigint,
    priority character varying(1),
    CONSTRAINT avcon_1114278544_appli_000 CHECK ((applicable_linehaul = ANY (ARRAY['C'::bpchar, 'S'::bpchar]))),
    CONSTRAINT avcon_1139073665_ship__001 CHECK ((ship_carr = ANY (ARRAY['S'::bpchar, 'C'::bpchar, 'B'::bpchar])))
);


ALTER TABLE accessorials OWNER TO postgres;

--
-- TOC entry 4759 (class 0 OID 0)
-- Dependencies: 919
-- Name: COLUMN accessorials.dedicated_program_id; Type: COMMENT; Schema: rater; Owner: postgres
--

COMMENT ON COLUMN accessorials.dedicated_program_id IS 'If not null, the dedicated program this accessorial is associated with.';


--
-- TOC entry 867 (class 1259 OID 41998)
-- Name: cacc_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE cacc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cacc_seq OWNER TO postgres;

--
-- TOC entry 868 (class 1259 OID 42000)
-- Name: cdi_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE cdi_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE cdi_seq OWNER TO postgres;

--
-- TOC entry 869 (class 1259 OID 42002)
-- Name: dedicated_unit_cost_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE dedicated_unit_cost_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE dedicated_unit_cost_seq OWNER TO postgres;

--
-- TOC entry 870 (class 1259 OID 42004)
-- Name: gain_share_accessorials_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE gain_share_accessorials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE gain_share_accessorials_seq OWNER TO postgres;

--
-- TOC entry 873 (class 1259 OID 42010)
-- Name: gs_acc_container_types_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE gs_acc_container_types_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE gs_acc_container_types_seq OWNER TO postgres;

--
-- TOC entry 874 (class 1259 OID 42012)
-- Name: gs_acc_ded_programs_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE gs_acc_ded_programs_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE gs_acc_ded_programs_seq OWNER TO postgres;

--
-- TOC entry 875 (class 1259 OID 42014)
-- Name: gs_acc_excl_acc_types_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE gs_acc_excl_acc_types_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE gs_acc_excl_acc_types_seq OWNER TO postgres;

--
-- TOC entry 876 (class 1259 OID 42016)
-- Name: gs_acc_excl_carriers_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE gs_acc_excl_carriers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE gs_acc_excl_carriers_seq OWNER TO postgres;

--
-- TOC entry 872 (class 1259 OID 42008)
-- Name: gst_acc_excl_acc_types_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE gst_acc_excl_acc_types_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE gst_acc_excl_acc_types_seq OWNER TO postgres;

--
-- TOC entry 871 (class 1259 OID 42006)
-- Name: gst_accessorials_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE gst_accessorials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE gst_accessorials_seq OWNER TO postgres;

--
-- TOC entry 877 (class 1259 OID 42018)
-- Name: lcl_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE lcl_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lcl_seq OWNER TO postgres;

--
-- TOC entry 878 (class 1259 OID 42020)
-- Name: lpe_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE lpe_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE lpe_seq OWNER TO postgres;

--
-- TOC entry 879 (class 1259 OID 42022)
-- Name: ltl_cost_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE ltl_cost_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE ltl_cost_seq OWNER TO postgres;

--
-- TOC entry 880 (class 1259 OID 42024)
-- Name: noe_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE noe_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE noe_seq OWNER TO postgres;

--
-- TOC entry 881 (class 1259 OID 42026)
-- Name: oacc_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE oacc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE oacc_seq OWNER TO postgres;

--
-- TOC entry 882 (class 1259 OID 42028)
-- Name: owacc_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE owacc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE owacc_seq OWNER TO postgres;

--
-- TOC entry 883 (class 1259 OID 42030)
-- Name: rail_authorities_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE rail_authorities_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rail_authorities_seq OWNER TO postgres;

--
-- TOC entry 884 (class 1259 OID 42032)
-- Name: rail_rates_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE rail_rates_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rail_rates_seq OWNER TO postgres;

--
-- TOC entry 887 (class 1259 OID 42038)
-- Name: rat_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE rat_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rat_seq OWNER TO postgres;

--
-- TOC entry 885 (class 1259 OID 42034)
-- Name: rate_copy_requests_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE rate_copy_requests_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rate_copy_requests_seq OWNER TO postgres;

--
-- TOC entry 886 (class 1259 OID 42036)
-- Name: rate_quote_mileage_lookup_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE rate_quote_mileage_lookup_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rate_quote_mileage_lookup_seq OWNER TO postgres;

--
-- TOC entry 888 (class 1259 OID 42040)
-- Name: rrt_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE rrt_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rrt_seq OWNER TO postgres;

--
-- TOC entry 889 (class 1259 OID 42042)
-- Name: rtc_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE rtc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE rtc_seq OWNER TO postgres;

--
-- TOC entry 890 (class 1259 OID 42044)
-- Name: sacc_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE sacc_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE sacc_seq OWNER TO postgres;

--
-- TOC entry 891 (class 1259 OID 42046)
-- Name: shipper_margin_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE shipper_margin_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE shipper_margin_seq OWNER TO postgres;

--
-- TOC entry 892 (class 1259 OID 42048)
-- Name: thr_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE thr_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE thr_seq OWNER TO postgres;

--
-- TOC entry 893 (class 1259 OID 42050)
-- Name: trans_fee_accessorials_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE trans_fee_accessorials_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE trans_fee_accessorials_seq OWNER TO postgres;

--
-- TOC entry 894 (class 1259 OID 42052)
-- Name: tx_acc_container_types_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE tx_acc_container_types_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tx_acc_container_types_seq OWNER TO postgres;

--
-- TOC entry 895 (class 1259 OID 42054)
-- Name: tx_acc_excl_acc_types_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE tx_acc_excl_acc_types_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE tx_acc_excl_acc_types_seq OWNER TO postgres;

--
-- TOC entry 896 (class 1259 OID 42056)
-- Name: zet_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE zet_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE zet_seq OWNER TO postgres;

--
-- TOC entry 897 (class 1259 OID 42058)
-- Name: zon_seq; Type: SEQUENCE; Schema: rater; Owner: postgres
--

CREATE SEQUENCE zon_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE;


ALTER TABLE zon_seq OWNER TO postgres;

-- Completed on 2017-10-23 18:12:49 MSK

--
-- PostgreSQL database dump complete
--

SET search_path = public;

commit;