
SET search_path = flatbed, pg_catalog;


--
-- TOC entry 922 (class 1259 OID 42538)
-- Name: pls_billing_invoice_audit_view; Type: VIEW; Schema: flatbed; Owner: postgres
--

CREATE VIEW pls_billing_invoice_audit_view AS
 SELECT DISTINCT l.org_id,
    l.location_id,
    corg.scac,
    ( SELECT min(date_trunc('day'::text, COALESCE((ad.audit_date)::timestamp with time zone, now()))) AS audit_date
           FROM ( SELECT audit_loads.load_id,
                    COALESCE(lag(audit_loads.finalization_status) OVER (PARTITION BY audit_loads.load_id ORDER BY audit_loads.audit_date), 'AB'::character varying) AS prev_status,
                    audit_loads.finalization_status,
                    audit_loads.audit_date
                   FROM audit_loads) ad
          WHERE (((ad.prev_status)::text <> (ad.finalization_status)::text) AND (ad.load_id = l.load_id) AND ((ad.finalization_status)::text = ANY (ARRAY[('PAH'::character varying)::text, ('ABH'::character varying)::text])))) AS price_audit_date,
    liau.rebill_audit_type AS carrier_sales,
    liau.rebill_assignee AS assignee,
    liau.date_modified AS assign_date,
    l.finalization_status,
    l.load_id,
    NULL::bigint AS faa_detail_id,
    l.bol,
    l.po_num,
    l.carrier_reference_number AS pro_num,
    lcd.total_revenue AS revenue,
    lcd.total_costs AS costs,
    round(((((COALESCE(lcd.total_revenue, (0)::double precision) - COALESCE(lcd.total_costs, (0)::double precision)) * (100)::double precision) /
        CASE COALESCE(lcd.total_revenue, (0)::double precision)
            WHEN 0 THEN (1)::double precision
            ELSE lcd.total_revenue
        END))::numeric, 2) AS margin,
    l.frt_bill_amount,
    ldd.departure AS dlv_date,
    ( SELECT string_agg((ld_bill_rsn.description)::text, ', '::text) AS string_agg
           FROM ( SELECT DISTINCT ld_bill_rsn_cd.description,
                    ld_bill_rsn_1.load_id
                   FROM (ld_bill_audit_reason_codes ld_bill_rsn_cd
                     JOIN ld_billing_audit_reasons ld_bill_rsn_1 ON ((((ld_bill_rsn_1.reason_cd)::text = (ld_bill_rsn_cd.reason_cd)::text) AND (ld_bill_rsn_1.status = 'A'::bpchar) AND (ld_bill_rsn_1.faa_detail_id IS NULL))))) ld_bill_rsn
          WHERE (ld_bill_rsn.load_id = l.load_id)) AS rsn_descr,
    corg.name AS carrier,
    sorg.name AS shipper,
    netw.name AS netw,
    (((us.first_name)::text || ' '::text) || (us.last_name)::text) AS username,
    nt.note,
    nt.date_created AS note_dt_created,
    (((us_created.first_name)::text || ' '::text) || (us_created.last_name)::text) AS created_username,
    nt.cnt AS note_cnt,
    ( SELECT
                CASE (min(date_part('day'::text, (now() - (ad.audit_date)::timestamp with time zone))))::integer
                    WHEN 0 THEN 1
                    ELSE (min(date_part('day'::text, ((('now'::text)::date)::timestamp without time zone - ad.audit_date))))::integer
                END AS diff_from_current_date
           FROM ( SELECT audit_loads.load_id,
                    COALESCE(lag(audit_loads.finalization_status) OVER (PARTITION BY audit_loads.load_id ORDER BY audit_loads.audit_date), 'AB'::character varying) AS prev_status,
                    audit_loads.finalization_status,
                    audit_loads.audit_date
                   FROM audit_loads) ad
          WHERE (((ad.prev_status)::text <> (ad.finalization_status)::text) AND (ad.load_id = l.load_id) AND ((ad.finalization_status)::text = ANY (ARRAY[('PAH'::character varying)::text, ('ABH'::character varying)::text])))) AS diff_from_current_date,
    inv_sett.invoice_type AS invoicetype,
    NULL::text AS date_modified,
    NULL::text AS isrebill
   FROM ((((((((((((loads l
     JOIN organizations corg ON ((l.award_carrier_org_id = corg.org_id)))
     JOIN organizations sorg ON ((l.org_id = sorg.org_id)))
     LEFT JOIN networks netw ON ((sorg.network_id = netw.network_id)))
     JOIN rater.load_cost_details lcd ON (((l.load_id = lcd.load_id) AND (lcd.status = 'A'::bpchar))))
     JOIN load_details ldd ON (((l.load_id = ldd.load_id) AND ('D'::text = (ldd.load_action)::text) AND ('D'::bpchar = ldd.point_type) AND (ldd.scheduled_arrival IS NOT NULL) AND (ldd.departure IS NOT NULL))))
     JOIN load_details ldo ON (((l.load_id = ldo.load_id) AND ('P'::text = (ldo.load_action)::text) AND ('O'::bpchar = ldo.point_type) AND (ldo.scheduled_arrival IS NOT NULL) AND (ldo.departure IS NOT NULL))))
     LEFT JOIN user_customer uc ON (((l.location_id = uc.location_id) AND (l.org_id = uc.org_id) AND (uc.status = 'A'::bpchar) AND (now() >= uc.eff_date) AND (now() <= uc.exp_date))))
     LEFT JOIN users us ON (((uc.person_id = us.person_id) AND ((us.status)::text = 'A'::text))))
     LEFT JOIN invoice_settings inv_sett ON ((inv_sett.bill_to_id = l.bill_to)))
     LEFT JOIN ( SELECT DISTINCT notes.ref_id,
            count(1) OVER (PARTITION BY notes.ref_id) AS cnt,
            max((notes.note)::text) OVER (PARTITION BY notes.ref_id) AS note,
            max(notes.created_by) OVER (PARTITION BY notes.ref_id) AS created_by,
            max(notes.date_created) OVER (PARTITION BY notes.ref_id) AS date_created
           FROM notes
          WHERE (((notes.note_type)::text = 'LOAD'::text) AND (notes.status = 'A'::bpchar))) nt ON ((l.load_id = nt.ref_id)))
     LEFT JOIN users us_created ON (((nt.created_by = us_created.person_id) AND ((us_created.status)::text = 'A'::text))))
     LEFT JOIN ld_inv_audit_info liau ON (((liau.load_id = l.load_id) AND ((liau.status)::text = 'A'::text))))
  WHERE (((l.container_cd)::text = 'VANLTL'::text) AND ((l.originating_system)::text = ANY (ARRAY[('PLS2_LT'::character varying)::text, ('GS'::character varying)::text])) AND ((l.load_status)::text = 'CD'::text) AND ((l.finalization_status)::text = ANY (ARRAY[('ABH'::character varying)::text, ('PAH'::character varying)::text])))
UNION ALL
 SELECT DISTINCT l.org_id,
    l.location_id,
    corg.scac,
    date_trunc('day'::text, fin.date_modified) AS price_audit_date,
    liau.rebill_audit_type AS carrier_sales,
    liau.rebill_assignee AS assignee,
    liau.date_modified AS assign_date,
    fin.faa_status AS finalization_status,
    l.load_id,
    fin.faa_detail_id,
    COALESCE(fin.bol, l.bol) AS bol,
    l.po_num,
    l.carrier_reference_number AS pro_num,
    fin.total_revenue AS revenue,
    fin.total_costs AS costs,
    round(((((COALESCE(fin.total_revenue, (0)::double precision) - COALESCE(fin.total_costs, (0)::double precision)) * (100)::double precision) /
        CASE COALESCE(fin.total_revenue, (0)::double precision)
            WHEN 0 THEN (1)::double precision
            ELSE fin.total_revenue
        END))::numeric, 2) AS margin,
    l.frt_bill_amount,
    ldo.departure AS dlv_date,
    ( SELECT string_agg((ld_bill_rsn.description)::text, ', '::text) AS string_agg
           FROM ( SELECT DISTINCT ld_bill_rsn_cd.description,
                    ld_bill_rsn_1.faa_detail_id
                   FROM (ld_bill_audit_reason_codes ld_bill_rsn_cd
                     JOIN ld_billing_audit_reasons ld_bill_rsn_1 ON ((((ld_bill_rsn_1.reason_cd)::text = (ld_bill_rsn_cd.reason_cd)::text) AND (ld_bill_rsn_1.status = 'A'::bpchar) AND (ld_bill_rsn_1.faa_detail_id IS NOT NULL))))) ld_bill_rsn
          WHERE (ld_bill_rsn.faa_detail_id = fin.faa_detail_id)) AS rsn_descr,
    corg.name AS carrier,
    sorg.name AS shipper,
    netw.name AS netw,
    (((us.first_name)::text || ' '::text) || (us.last_name)::text) AS username,
    nt.note,
    nt.date_created AS note_dt_created,
    (((us_created.first_name)::text || ' '::text) || (us_created.last_name)::text) AS created_username,
    nt.cnt AS note_cnt,
        CASE (date_part('day'::text, (now() - (fin.date_modified)::timestamp with time zone)))::integer
            WHEN 0 THEN 1
            ELSE (date_part('day'::text, (now() - (fin.date_modified)::timestamp with time zone)))::integer
        END AS diff_from_current_date,
    inv_sett.invoice_type AS invoicetype,
    NULL::text AS date_modified,
        CASE
            WHEN (cdi.reason = ANY (ARRAY[(6)::bigint, (44)::bigint])) THEN 'TRUE'::text
            ELSE 'FALSE'::text
        END AS isrebill
   FROM (((((((((((((finan_adj_acc_detail fin
     JOIN loads l ON (((fin.load_id = l.load_id) AND ((l.load_status)::text = 'CD'::text) AND ((l.originating_system)::text = ANY (ARRAY[('PLS2_LT'::character varying)::text, ('GS'::character varying)::text])))))
     JOIN organizations corg ON ((l.award_carrier_org_id = corg.org_id)))
     JOIN rater.cost_detail_items cdi ON ((cdi.finan_adj_acc_detail_id = fin.faa_detail_id)))
     JOIN organizations sorg ON ((l.org_id = sorg.org_id)))
     LEFT JOIN networks netw ON ((sorg.network_id = netw.network_id)))
     JOIN load_details ldo ON (((l.load_id = ldo.load_id) AND ('D'::text = (ldo.load_action)::text) AND ('D'::bpchar = ldo.point_type) AND (ldo.scheduled_arrival IS NOT NULL) AND (ldo.departure IS NOT NULL))))
     JOIN organization_locations org_loc ON ((l.location_id = org_loc.location_id)))
     LEFT JOIN user_customer uc ON (((org_loc.location_id = uc.location_id) AND (uc.status = 'A'::bpchar) AND (now() >= uc.eff_date) AND (now() <= uc.exp_date))))
     LEFT JOIN users us ON (((uc.person_id = us.person_id) AND ((us.status)::text = 'A'::text))))
     LEFT JOIN invoice_settings inv_sett ON ((inv_sett.bill_to_id = l.bill_to)))
     LEFT JOIN ( SELECT DISTINCT notes.ref_id,
            count(1) OVER (PARTITION BY notes.ref_id) AS cnt,
            max((notes.note)::text) OVER (PARTITION BY notes.ref_id) AS note,
            max(notes.created_by) OVER (PARTITION BY notes.ref_id) AS created_by,
            max(notes.date_created) OVER (PARTITION BY notes.ref_id) AS date_created
           FROM notes
          WHERE (((notes.note_type)::text = 'LOAD'::text) AND (notes.status = 'A'::bpchar))) nt ON ((l.load_id = nt.ref_id)))
     LEFT JOIN users us_created ON (((nt.created_by = us_created.person_id) AND ((us_created.status)::text = 'A'::text))))
     LEFT JOIN ld_inv_audit_info liau ON (((liau.load_id = l.load_id) AND ((liau.status)::text = 'A'::text))))
  WHERE ((fin.status = 'A'::bpchar) AND ((fin.faa_status)::text = ANY (ARRAY[('ABHAA'::character varying)::text, ('PAH'::character varying)::text])))
  ORDER BY 9 DESC;


ALTER TABLE pls_billing_invoice_audit_view OWNER TO postgres;

--
-- TOC entry 895 (class 1259 OID 42063)
-- Name: pls_inv_history_view; Type: VIEW; Schema: flatbed; Owner: postgres
--

CREATE VIEW pls_inv_history_view AS
 SELECT subq.org_id,
    subq.location_id,
    subq.inv_type,
    subq.invoice_id,
    subq.gl_date,
    subq.group_inv_num,
    subq.cust_inv_num,
    subq.usr,
    subq.load_id,
    subq.faa_detail_id,
    subq.bill_to_id,
    subq.inv_type_flag,
    subq.edi_invoice,
    subq.bol,
    subq.carrier_reference_number,
    subq.carrier_name,
    COALESCE(sum(subq.subtotal) OVER (PARTITION BY subq.invoice_id, subq.bill_to_id), (0)::double precision) AS subtotal,
    COALESCE(sum(subq.amt_applied) OVER (PARTITION BY subq.invoice_id, subq.bill_to_id), (0)::double precision) AS amt_applied,
    subq.netw_name,
    subq.network_id,
    subq.customer,
    subq.due_date,
        CASE sum(COALESCE(subq.faa_detail_id, (0)::bigint)) OVER (PARTITION BY subq.group_inv_num, subq.invoice_id)
            WHEN 0 THEN 'N'::text
            ELSE 'Y'::text
        END AS is_adj_available
   FROM ( SELECT DISTINCT sorg.org_id,
            l.location_id,
            inv_hist.invoice_type AS inv_type,
            inv_hist.invoice_id,
            lcd.gl_date,
            lcd.group_invoice_num AS group_inv_num,
            lcd.customer_invoice_num AS cust_inv_num,
            max((((us.first_name)::text || ' '::text) || (us.last_name)::text)) OVER (PARTITION BY inv_hist.invoice_id, bt.bill_to_id) AS usr,
            l.load_id,
            bt.bill_to_id,
                CASE
                    WHEN ((inv_set.cbi_invoice_type)::text = 'FIN'::text) THEN 1
                    ELSE 0
                END AS inv_type_flag,
            inv_set.edi_invoice,
            upper((l.bol)::text) AS bol,
            upper((l.carrier_reference_number)::text) AS carrier_reference_number,
                CASE
                    WHEN ((inv_hist.invoice_type)::text = 'TRANSACTIONAL'::text) THEN max((corg.name)::text) OVER (PARTITION BY inv_hist.invoice_id, bt.bill_to_id)
                    ELSE NULL::text
                END AS carrier_name,
            lcd.total_revenue AS subtotal,
            far.amt_applied,
            netw.name AS netw_name,
            netw.network_id,
            sorg.name AS customer,
                CASE
                    WHEN (lcd.gl_date IS NOT NULL) THEN (lcd.gl_date + ((cust_trm.due_days)::double precision * '1 day'::interval day))
                    ELSE (('now'::text)::date + ((cust_trm.due_days)::double precision * '1 day'::interval day))
                END AS due_date,
            inv_hist.faa_detail_id
           FROM ((((((((((invoice_history inv_hist
             JOIN loads l ON ((inv_hist.load_id = l.load_id)))
             JOIN rater.load_cost_details lcd ON (((l.load_id = lcd.load_id) AND (lcd.status = 'A'::bpchar) AND (date_trunc('day'::text, inv_hist.date_modified) = date_trunc('day'::text, now())))))
             JOIN users us ON ((lcd.modified_by = us.person_id)))
             JOIN organizations corg ON ((l.award_carrier_org_id = corg.org_id)))
             JOIN bill_to bt ON ((l.bill_to = bt.bill_to_id)))
             JOIN organizations sorg ON ((l.org_id = sorg.org_id)))
             LEFT JOIN invoice_settings inv_set ON ((bt.bill_to_id = inv_set.bill_to_id)))
             LEFT JOIN pls_customer_terms cust_trm ON ((bt.pay_terms_id = cust_trm.term_id)))
             LEFT JOIN networks netw ON ((sorg.network_id = netw.network_id)))
             LEFT JOIN finan_account_receivables far ON (((l.load_id = far.load_id) AND (far.faa_detail_id IS NULL))))
          WHERE (((inv_hist.release_status)::text = 'S'::text) AND (inv_hist.faa_detail_id IS NULL))
        UNION
         SELECT sorg.org_id,
            l.location_id,
            inv_hist.invoice_type AS inv_type,
            inv_hist.invoice_id,
            faa.gl_date,
            faa.group_invoice_num AS group_inv_num,
            faa.customer_invoice_num AS cust_inv_num,
            max((((usr.first_name)::text || ' '::text) || (usr.last_name)::text)) OVER (PARTITION BY inv_hist.invoice_id, bt.bill_to_id) AS usr,
            l.load_id,
            bt.bill_to_id,
                CASE
                    WHEN ((inv_sett.cbi_invoice_type)::text = 'FIN'::text) THEN 1
                    ELSE 0
                END AS inv_type_flag,
            inv_sett.edi_invoice,
            upper((l.bol)::text) AS bol,
            upper((l.carrier_reference_number)::text) AS carrier_reference_number,
                CASE
                    WHEN ((inv_hist.invoice_type)::text = 'TRANSACTIONAL'::text) THEN max((corg.name)::text) OVER (PARTITION BY inv_hist.invoice_id, bt.bill_to_id)
                    ELSE NULL::text
                END AS carrier_name,
            cdi.subtotal,
            far.amt_applied,
            netw.name AS netw_name,
            netw.network_id,
            sorg.name AS customer,
                CASE
                    WHEN (faa.gl_date IS NOT NULL) THEN (faa.gl_date + ((cust_trm.due_days)::double precision * '1 day'::interval day))
                    ELSE (('now'::text)::date + ((cust_trm.due_days)::double precision * '1 day'::interval day))
                END AS due_date,
            inv_hist.faa_detail_id
           FROM (((((((((((invoice_history inv_hist
             JOIN finan_adj_acc_detail faa ON (((inv_hist.faa_detail_id = faa.faa_detail_id) AND ((faa.short_pay IS NULL) OR (faa.short_pay = 'N'::bpchar)) AND (date_trunc('day'::text, inv_hist.date_modified) = date_trunc('day'::text, now())))))
             JOIN rater.cost_detail_items cdi ON (((faa.faa_detail_id = cdi.finan_adj_acc_detail_id) AND (cdi.ship_carr = 'S'::bpchar))))
             JOIN loads l ON ((faa.load_id = l.load_id)))
             JOIN organizations sorg ON ((l.org_id = sorg.org_id)))
             JOIN networks netw ON ((sorg.network_id = netw.network_id)))
             JOIN organizations corg ON ((l.award_carrier_org_id = corg.org_id)))
             LEFT JOIN bill_to bt ON ((cdi.bill_to_id = bt.bill_to_id)))
             LEFT JOIN invoice_settings inv_sett ON ((bt.bill_to_id = inv_sett.bill_to_id)))
             LEFT JOIN pls_customer_terms cust_trm ON ((bt.pay_terms_id = cust_trm.term_id)))
             LEFT JOIN users usr ON ((faa.modified_by = usr.person_id)))
             LEFT JOIN finan_account_receivables far ON ((faa.faa_detail_id = far.faa_detail_id)))
          WHERE (((inv_hist.release_status)::text = 'S'::text) AND (inv_hist.faa_detail_id IS NOT NULL))) subq
UNION ALL
 SELECT pls_inv_history_all_view.org_id,
    pls_inv_history_all_view.location_id,
    pls_inv_history_all_view.inv_type,
    pls_inv_history_all_view.invoice_id,
    pls_inv_history_all_view.gl_date,
    pls_inv_history_all_view.group_inv_num,
    pls_inv_history_all_view.cust_inv_num,
    pls_inv_history_all_view.usr,
    pls_inv_history_all_view.load_id,
    pls_inv_history_all_view.faa_detail_id,
    pls_inv_history_all_view.bill_to_id,
    pls_inv_history_all_view.inv_type_flag,
    pls_inv_history_all_view.edi_invoice,
    pls_inv_history_all_view.bol,
    pls_inv_history_all_view.carrier_reference_number,
    pls_inv_history_all_view.carrier_name,
    pls_inv_history_all_view.subtotal,
    pls_inv_history_all_view.amt_applied,
    pls_inv_history_all_view.netw_name,
    pls_inv_history_all_view.network_id,
    pls_inv_history_all_view.customer,
    pls_inv_history_all_view.due_date,
    pls_inv_history_all_view.is_adj_available
   FROM pls_inv_history_all_view;


ALTER TABLE pls_inv_history_view OWNER TO postgres;

--
-- TOC entry 850 (class 1259 OID 24644)
-- Name: pricing_blocked_geo_details; Type: VIEW; Schema: flatbed; Owner: postgres
--

CREATE VIEW pricing_blocked_geo_details AS
 SELECT NULL::bigint AS customerid,
    NULL::timestamp without time zone AS eff_date,
    NULL::timestamp without time zone AS exp_date,
    NULL::bigint AS carrierid,
    lbcgs.ltl_pric_prof_detail_id,
    orig.geo_serv_type AS orig_geo_serv_type,
    orig.searchable_geo_value AS orig_searchable_geo_value,
        CASE
            WHEN ((orig.geo_serv_type = 3) AND (length((orig.searchable_geo_value)::text) = 11)) THEN to_number(substr((orig.searchable_geo_value)::text, 1, 5), '999999999'::text)
            ELSE
            CASE
                WHEN ((orig.geo_serv_type = 3) AND (length((orig.searchable_geo_value)::text) = 25)) THEN to_number(substr((orig.searchable_geo_value)::text, 1, 12), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS origminzip,
        CASE
            WHEN ((orig.geo_serv_type = 3) AND (length((orig.searchable_geo_value)::text) = 11)) THEN to_number(substr((orig.searchable_geo_value)::text, '-5'::integer, 5), '999999999'::text)
            ELSE
            CASE
                WHEN ((orig.geo_serv_type = 3) AND (length((orig.searchable_geo_value)::text) = 25)) THEN to_number(substr((orig.searchable_geo_value)::text, '-12'::integer, 12), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS origmaxzip,
        CASE
            WHEN ((orig.geo_serv_type = 4) AND (length((orig.searchable_geo_value)::text) = 7)) THEN to_number(substr((orig.searchable_geo_value)::text, 1, 3), '999999999'::text)
            ELSE
            CASE
                WHEN ((orig.geo_serv_type = 4) AND (length((orig.searchable_geo_value)::text) = 13)) THEN to_number(substr((orig.searchable_geo_value)::text, 1, 6), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS origminshortzip,
        CASE
            WHEN ((orig.geo_serv_type = 4) AND (length((orig.searchable_geo_value)::text) = 7)) THEN to_number(substr((orig.searchable_geo_value)::text, '-3'::integer, 3), '999999999'::text)
            ELSE
            CASE
                WHEN ((orig.geo_serv_type = 4) AND (length((orig.searchable_geo_value)::text) = 13)) THEN to_number(substr((orig.searchable_geo_value)::text, '-6'::integer, 6), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS origmaxshortzip,
    dest.geo_serv_type AS dest_geo_serv_type,
    dest.searchable_geo_value AS dest_searchable_geo_value,
        CASE
            WHEN ((dest.geo_serv_type = 3) AND (length((dest.searchable_geo_value)::text) = 11)) THEN to_number(substr((dest.searchable_geo_value)::text, 1, 5), '999999999'::text)
            ELSE
            CASE
                WHEN ((dest.geo_serv_type = 3) AND (length((dest.searchable_geo_value)::text) = 25)) THEN to_number(substr((dest.searchable_geo_value)::text, 1, 12), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS destminzip,
        CASE
            WHEN ((dest.geo_serv_type = 3) AND (length((dest.searchable_geo_value)::text) = 11)) THEN to_number(substr((dest.searchable_geo_value)::text, '-5'::integer, 5), '999999999'::text)
            ELSE
            CASE
                WHEN ((dest.geo_serv_type = 3) AND (length((dest.searchable_geo_value)::text) = 25)) THEN to_number(substr((dest.searchable_geo_value)::text, '-12'::integer, 12), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS destmaxzip,
        CASE
            WHEN ((dest.geo_serv_type = 4) AND (length((dest.searchable_geo_value)::text) = 7)) THEN to_number(substr((dest.searchable_geo_value)::text, 1, 3), '999999999'::text)
            ELSE
            CASE
                WHEN ((dest.geo_serv_type = 4) AND (length((dest.searchable_geo_value)::text) = 13)) THEN to_number(substr((dest.searchable_geo_value)::text, 1, 6), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS destminshortzip,
        CASE
            WHEN ((dest.geo_serv_type = 4) AND (length((dest.searchable_geo_value)::text) = 7)) THEN to_number(substr((dest.searchable_geo_value)::text, '-3'::integer, 3), '999999999'::text)
            ELSE
            CASE
                WHEN ((dest.geo_serv_type = 4) AND (length((dest.searchable_geo_value)::text) = 13)) THEN to_number(substr((dest.searchable_geo_value)::text, '-6'::integer, 6), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS destmaxshortzip
   FROM ltl_bk_carr_geo_serv_dtls orig,
    ltl_bk_carr_geo_serv_dtls dest,
    ltl_block_carr_geo_services lbcgs
  WHERE (((lbcgs.status)::text = 'A'::text) AND (orig.geo_type = 1) AND (dest.geo_type = 2) AND (orig.ltl_block_carr_geo_service_id = (lbcgs.ltl_block_carr_geo_service_id)::numeric) AND (dest.ltl_block_carr_geo_service_id = (lbcgs.ltl_block_carr_geo_service_id)::numeric))
UNION
 SELECT lobl.shipper_org_id AS customerid,
    lobl.eff_date,
    COALESCE(lobl.exp_date, ((('now'::text)::date + 10000))::timestamp without time zone) AS exp_date,
    lobl.carrier_org_id AS carrierid,
    NULL::bigint AS ltl_pric_prof_detail_id,
    orig.geo_serv_type AS orig_geo_serv_type,
    orig.searchable_geo_value AS orig_searchable_geo_value,
        CASE
            WHEN ((orig.geo_serv_type = 3) AND (length((orig.searchable_geo_value)::text) = 11)) THEN to_number(substr((orig.searchable_geo_value)::text, 1, 5), '999999999'::text)
            ELSE
            CASE
                WHEN ((orig.geo_serv_type = 3) AND (length((orig.searchable_geo_value)::text) = 25)) THEN to_number(substr((orig.searchable_geo_value)::text, 1, 12), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS origminzip,
        CASE
            WHEN ((orig.geo_serv_type = 3) AND (length((orig.searchable_geo_value)::text) = 11)) THEN to_number(substr((orig.searchable_geo_value)::text, '-5'::integer, 5), '999999999'::text)
            ELSE
            CASE
                WHEN ((orig.geo_serv_type = 3) AND (length((orig.searchable_geo_value)::text) = 25)) THEN to_number(substr((orig.searchable_geo_value)::text, '-12'::integer, 12), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS origmaxzip,
        CASE
            WHEN ((orig.geo_serv_type = 4) AND (length((orig.searchable_geo_value)::text) = 7)) THEN to_number(substr((orig.searchable_geo_value)::text, 1, 3), '999999999'::text)
            ELSE
            CASE
                WHEN ((orig.geo_serv_type = 4) AND (length((orig.searchable_geo_value)::text) = 13)) THEN to_number(substr((orig.searchable_geo_value)::text, 1, 6), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS origminshortzip,
        CASE
            WHEN ((orig.geo_serv_type = 4) AND (length((orig.searchable_geo_value)::text) = 7)) THEN to_number(substr((orig.searchable_geo_value)::text, '-3'::integer, 3), '999999999'::text)
            ELSE
            CASE
                WHEN ((orig.geo_serv_type = 4) AND (length((orig.searchable_geo_value)::text) = 13)) THEN to_number(substr((orig.searchable_geo_value)::text, '-6'::integer, 6), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS origmaxshortzip,
    dest.geo_serv_type AS dest_geo_serv_type,
    dest.searchable_geo_value AS dest_searchable_geo_value,
        CASE
            WHEN ((dest.geo_serv_type = 3) AND (length((dest.searchable_geo_value)::text) = 11)) THEN to_number(substr((dest.searchable_geo_value)::text, 1, 5), '999999999'::text)
            ELSE
            CASE
                WHEN ((dest.geo_serv_type = 3) AND (length((dest.searchable_geo_value)::text) = 25)) THEN to_number(substr((dest.searchable_geo_value)::text, 1, 12), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS destminzip,
        CASE
            WHEN ((dest.geo_serv_type = 3) AND (length((dest.searchable_geo_value)::text) = 11)) THEN to_number(substr((dest.searchable_geo_value)::text, '-5'::integer, 5), '999999999'::text)
            ELSE
            CASE
                WHEN ((dest.geo_serv_type = 3) AND (length((dest.searchable_geo_value)::text) = 25)) THEN to_number(substr((dest.searchable_geo_value)::text, '-12'::integer, 12), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS destmaxzip,
        CASE
            WHEN ((dest.geo_serv_type = 4) AND (length((dest.searchable_geo_value)::text) = 7)) THEN to_number(substr((dest.searchable_geo_value)::text, 1, 3), '999999999'::text)
            ELSE
            CASE
                WHEN ((dest.geo_serv_type = 4) AND (length((dest.searchable_geo_value)::text) = 13)) THEN to_number(substr((dest.searchable_geo_value)::text, 1, 6), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS destminshortzip,
        CASE
            WHEN ((dest.geo_serv_type = 4) AND (length((dest.searchable_geo_value)::text) = 7)) THEN to_number(substr((dest.searchable_geo_value)::text, '-3'::integer, 3), '999999999'::text)
            ELSE
            CASE
                WHEN ((dest.geo_serv_type = 4) AND (length((dest.searchable_geo_value)::text) = 13)) THEN to_number(substr((dest.searchable_geo_value)::text, '-6'::integer, 6), '999999999'::text)
                ELSE NULL::numeric
            END
        END AS destmaxshortzip
   FROM ltl_bk_lane_geo_serv_dtls orig,
    ltl_bk_lane_geo_serv_dtls dest,
    ltl_org_block_lane lobl
  WHERE (((lobl.status)::text = 'A'::text) AND (orig.geo_type = 1) AND (dest.geo_type = 2) AND (orig.ltl_block_lane_id = lobl.ltl_block_lane_id) AND (dest.ltl_block_lane_id = lobl.ltl_block_lane_id));


ALTER TABLE pricing_blocked_geo_details OWNER TO postgres;

--
-- TOC entry 939 (class 1259 OID 43544)
-- Name: pricing_geo_details_dest; Type: VIEW; Schema: flatbed; Owner: postgres
--

CREATE VIEW pricing_geo_details_dest AS
 select distinct
  lpgs.ltl_pricing_detail_id,
  lpgs.ltl_pricing_geo_service_id,
  dest.geo_value as dest_geo_value,
  dest.geo_serv_type as dest_geo_serv_type,
  dest.searchable_Geo_Value as dest_searchable_Geo_Value,
  case when dest.geo_serv_type = 3 and length(dest.searchable_Geo_Value) = 11 then cast(substr(dest.searchable_Geo_Value, 1, 5) as numeric)
    else
      case when dest.geo_serv_type = 3 and length(dest.searchable_Geo_Value) = 25 then cast(substr(dest.searchable_Geo_Value, 1, 12) as numeric)
        else null end
    end as destMinZip,
  case when dest.geo_serv_type = 3 and length(dest.searchable_Geo_Value) = 11 then cast(substr(dest.searchable_Geo_Value, 7,11) as numeric)
    else
      case when dest.geo_serv_type = 3 and length(dest.searchable_Geo_Value) = 25 then cast(substr(dest.searchable_Geo_Value, 14,25) as numeric)
        else null end
    end as destMaxZip,
  case when dest.geo_serv_type = 4 and length(dest.searchable_Geo_Value) = 7 then cast(substr(dest.searchable_Geo_Value, 1, 3) as numeric)
    else
      case when dest.geo_serv_type = 4 and length(dest.searchable_Geo_Value) = 13 then cast(substr(dest.searchable_Geo_Value, 1, 6) as numeric)
        else null end
    end as destMinShortZip,
  case when dest.geo_serv_type = 4 and length(dest.searchable_Geo_Value) = 7 then cast(substr(dest.searchable_Geo_Value, 5, 7) as numeric)
    else
      case when dest.geo_serv_type = 4 and length(dest.searchable_Geo_Value) = 13 then cast(substr(dest.searchable_Geo_Value, 8,13) as numeric)
        else null end
    end as destMaxShortZip
  from flatbed.LTL_PRICING_GEO_SERVICES lpgs
  join flatbed.ltl_pric_geo_serv_dtls dest
    on dest.ltl_pricing_geo_service_id = lpgs.ltl_pricing_geo_service_id
    and dest.geo_type = 2;


ALTER TABLE pricing_geo_details_dest OWNER TO postgres;

--
-- TOC entry 938 (class 1259 OID 43538)
-- Name: pricing_geo_details_orig; Type: VIEW; Schema: flatbed; Owner: postgres
--

CREATE VIEW pricing_geo_details_orig AS
 select distinct
  lpgs.ltl_pricing_detail_id,
  lpgs.ltl_pricing_geo_service_id,
  orig.geo_value as orig_geo_value,
  orig.geo_serv_type as orig_geo_serv_type,
  orig.searchable_Geo_Value as orig_searchable_Geo_Value,
  case when orig.geo_serv_type = 3 and length(orig.searchable_Geo_Value) = 11 then cast(substr(orig.searchable_Geo_Value, 1, 5) as numeric)
    else
      case when orig.geo_serv_type = 3 and length(orig.searchable_Geo_Value) = 25 then cast(substr(orig.searchable_Geo_Value, 1, 12) as numeric)
        else null end
    end as origMinZip,
  case when orig.geo_serv_type = 3 and length(orig.searchable_Geo_Value) = 11 then cast(substr(orig.searchable_Geo_Value, 7,11) as numeric)
    else
      case when orig.geo_serv_type = 3 and length(orig.searchable_Geo_Value) = 25 then cast(substr(orig.searchable_Geo_Value, 14,25) as numeric)
        else null end
    end as origMaxZip,
  case when orig.geo_serv_type = 4 and length(orig.searchable_Geo_Value) = 7 then cast(substr(orig.searchable_Geo_Value, 1, 3) as numeric)
    else
      case when orig.geo_serv_type = 4 and length(orig.searchable_Geo_Value) = 13 then cast(substr(orig.searchable_Geo_Value, 1, 6) as numeric)
        else null end
    end as origMinShortZip,
  case when orig.geo_serv_type = 4 and length(orig.searchable_Geo_Value) = 7 then cast(substr(orig.searchable_Geo_Value, 5, 7) as numeric)
    else
      case when orig.geo_serv_type = 4 and length(orig.searchable_Geo_Value) = 13 then cast(substr(orig.searchable_Geo_Value, 8,13) as numeric)
        else null end
    end as origMaxShortZip
  from flatbed.LTL_PRICING_GEO_SERVICES lpgs
  join flatbed.ltl_pric_geo_serv_dtls orig
    on orig.ltl_pricing_geo_service_id = lpgs.ltl_pricing_geo_service_id
    and orig.geo_type = 1;


ALTER TABLE pricing_geo_details_orig OWNER TO postgres;

SET search_path = public;

commit;