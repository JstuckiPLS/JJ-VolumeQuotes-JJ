
SET search_path = flatbed, pg_catalog;

--
-- TOC entry 6086 (class 2620 OID 42957)
-- Name: addresses addresses_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER addresses_hist BEFORE DELETE OR UPDATE ON addresses FOR EACH ROW EXECUTE PROCEDURE trigger_fct_addresses_hist();


--
-- TOC entry 6054 (class 2620 OID 42959)
-- Name: loads ains_loads_invoice_audit; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER ains_loads_invoice_audit AFTER INSERT OR DELETE OR UPDATE ON loads FOR EACH ROW EXECUTE PROCEDURE trigger_fct_ains_loads_invoice_audit();


--
-- TOC entry 6072 (class 2620 OID 42960)
-- Name: organizations ains_organizations_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER ains_organizations_trg AFTER INSERT ON organizations FOR EACH ROW EXECUTE PROCEDURE trigger_fct_ains_organizations_trg();


--
-- TOC entry 6049 (class 2620 OID 42958)
-- Name: finan_account_receivables ainsupd_fin_acc_receivables; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER ainsupd_fin_acc_receivables AFTER INSERT OR UPDATE ON finan_account_receivables FOR EACH ROW EXECUTE PROCEDURE trigger_fct_ainsupd_fin_acc_receivables();


--
-- TOC entry 6077 (class 2620 OID 42961)
-- Name: organization_locations aiu_organization_locations_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER aiu_organization_locations_trg AFTER INSERT OR UPDATE ON organization_locations FOR EACH ROW EXECUTE PROCEDURE trigger_fct_aiu_organization_locations_trg();


--
-- TOC entry 6064 (class 2620 OID 42964)
-- Name: load_details audit_load_details_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER audit_load_details_trg AFTER INSERT OR UPDATE ON load_details FOR EACH ROW EXECUTE PROCEDURE trigger_fct_audit_load_details_trg();


--
-- TOC entry 6066 (class 2620 OID 42965)
-- Name: load_materials audit_load_materials_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER audit_load_materials_trg BEFORE INSERT OR DELETE OR UPDATE ON load_materials FOR EACH ROW EXECUTE PROCEDURE trigger_fct_audit_load_materials_trg();


--
-- TOC entry 6055 (class 2620 OID 42963)
-- Name: loads audit_loads_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER audit_loads_trg AFTER INSERT OR UPDATE ON loads FOR EACH ROW EXECUTE PROCEDURE trigger_fct_audit_loads_trg();


--
-- TOC entry 6084 (class 2620 OID 42966)
-- Name: users aupd_users_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER aupd_users_trg BEFORE UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE trigger_fct_aupd_users_trg();


--
-- TOC entry 6040 (class 2620 OID 42968)
-- Name: bill_to_default_values bill_to_default_values_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bill_to_default_values_hist BEFORE DELETE OR UPDATE ON bill_to_default_values FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bill_to_default_values_hist();


--
-- TOC entry 6037 (class 2620 OID 42969)
-- Name: bill_to bill_to_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bill_to_hist BEFORE DELETE OR UPDATE ON bill_to FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bill_to_hist();


--
-- TOC entry 6041 (class 2620 OID 42970)
-- Name: bill_to_req_field bill_to_req_field_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bill_to_req_field_hist BEFORE DELETE OR UPDATE ON bill_to_req_field FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bill_to_req_field_hist();


--
-- TOC entry 6042 (class 2620 OID 42971)
-- Name: bill_to_threshold_settings bill_to_thresh_sett_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bill_to_thresh_sett_hist BEFORE DELETE OR UPDATE ON bill_to_threshold_settings FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bill_to_thresh_sett_hist();


--
-- TOC entry 6033 (class 2620 OID 42967)
-- Name: billing_invoice_node billing_invoice_node_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER billing_invoice_node_trg BEFORE UPDATE ON billing_invoice_node FOR EACH ROW EXECUTE PROCEDURE trigger_fct_billing_invoice_node_trg();


--
-- TOC entry 6034 (class 2620 OID 42979)
-- Name: billing_invoice_node bin_bupd_bins_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bin_bupd_bins_trg BEFORE INSERT OR UPDATE ON billing_invoice_node FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bin_bupd_bins_trg();


--
-- TOC entry 6035 (class 2620 OID 42980)
-- Name: billing_invoice_node bin_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bin_hist BEFORE DELETE OR UPDATE ON billing_invoice_node FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bin_hist();


--
-- TOC entry 6036 (class 2620 OID 42981)
-- Name: billing_invoice_node bin_pls_cl_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bin_pls_cl_trg AFTER INSERT ON billing_invoice_node FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bin_pls_cl_trg();


--
-- TOC entry 6038 (class 2620 OID 42974)
-- Name: bill_to bins_bill_to_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bins_bill_to_trg BEFORE INSERT ON bill_to FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bins_bill_to_trg();


--
-- TOC entry 6050 (class 2620 OID 42975)
-- Name: finan_account_receivables bins_finan_account_rec_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bins_finan_account_rec_trg BEFORE INSERT ON finan_account_receivables FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bins_finan_account_rec_trg();


--
-- TOC entry 6069 (class 2620 OID 42976)
-- Name: ltl_product bins_ltl_product_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bins_ltl_product_trg BEFORE INSERT ON ltl_product FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bins_ltl_product_trg();


--
-- TOC entry 6073 (class 2620 OID 42977)
-- Name: organizations bins_organizations_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bins_organizations_trg BEFORE INSERT ON organizations FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bins_organizations_trg();


--
-- TOC entry 6085 (class 2620 OID 42978)
-- Name: user_address_book bins_user_addr_book_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bins_user_addr_book_trg BEFORE INSERT ON user_address_book FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bins_user_addr_book_trg();


--
-- TOC entry 6056 (class 2620 OID 42972)
-- Name: loads binsupd_loads_disp_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER binsupd_loads_disp_trg BEFORE INSERT OR DELETE OR UPDATE ON loads FOR EACH ROW EXECUTE PROCEDURE trigger_fct_binsupd_loads_disp_trg();


--
-- TOC entry 6057 (class 2620 OID 42982)
-- Name: loads biu_load_part_num_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER biu_load_part_num_trg BEFORE INSERT OR UPDATE ON loads FOR EACH ROW EXECUTE PROCEDURE trigger_fct_biu_load_part_num_trg();


--
-- TOC entry 6039 (class 2620 OID 42983)
-- Name: bill_to bupd_bill_to_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bupd_bill_to_trg BEFORE UPDATE ON bill_to FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bupd_bill_to_trg();


--
-- TOC entry 6051 (class 2620 OID 42984)
-- Name: finan_account_receivables bupd_finan_account_rec_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bupd_finan_account_rec_trg BEFORE UPDATE ON finan_account_receivables FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bupd_finan_account_rec_trg();


--
-- TOC entry 6074 (class 2620 OID 42985)
-- Name: organizations bupd_organizations_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER bupd_organizations_trg BEFORE UPDATE ON organizations FOR EACH ROW EXECUTE PROCEDURE trigger_fct_bupd_organizations_trg();


--
-- TOC entry 6043 (class 2620 OID 42987)
-- Name: carrier_invoice_addr_details carrier_inv_addr_details_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER carrier_inv_addr_details_hist BEFORE DELETE OR UPDATE ON carrier_invoice_addr_details FOR EACH ROW EXECUTE PROCEDURE trigger_fct_carrier_inv_addr_details_hist();


--
-- TOC entry 6044 (class 2620 OID 42988)
-- Name: carrier_invoice_cost_items carrier_inv_cost_items_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER carrier_inv_cost_items_hist BEFORE DELETE OR UPDATE ON carrier_invoice_cost_items FOR EACH ROW EXECUTE PROCEDURE trigger_fct_carrier_inv_cost_items_hist();


--
-- TOC entry 6045 (class 2620 OID 42986)
-- Name: carrier_invoice_details carrier_invoice_details_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER carrier_invoice_details_hist BEFORE DELETE OR UPDATE ON carrier_invoice_details FOR EACH ROW EXECUTE PROCEDURE trigger_fct_carrier_invoice_details_hist();


--
-- TOC entry 6046 (class 2620 OID 42989)
-- Name: countries countries_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER countries_hist BEFORE DELETE OR UPDATE ON countries FOR EACH ROW EXECUTE PROCEDURE trigger_fct_countries_hist();


--
-- TOC entry 6047 (class 2620 OID 42990)
-- Name: edi edi_bins_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER edi_bins_trg BEFORE INSERT ON edi FOR EACH ROW EXECUTE PROCEDURE trigger_fct_edi_bins_trg();


--
-- TOC entry 6048 (class 2620 OID 42991)
-- Name: edi_settings edi_settings_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER edi_settings_hist BEFORE DELETE OR UPDATE ON edi_settings FOR EACH ROW EXECUTE PROCEDURE trigger_fct_edi_settings_hist();


--
-- TOC entry 6052 (class 2620 OID 42992)
-- Name: finan_adj_acc_detail finan_adj_acc_detail_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER finan_adj_acc_detail_hist BEFORE DELETE OR UPDATE ON finan_adj_acc_detail FOR EACH ROW EXECUTE PROCEDURE trigger_fct_finan_adj_acc_detail_hist();


--
-- TOC entry 6053 (class 2620 OID 42993)
-- Name: invoice_settings invset_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER invset_hist BEFORE DELETE OR UPDATE ON invoice_settings FOR EACH ROW EXECUTE PROCEDURE trigger_fct_invset_hist();


--
-- TOC entry 6065 (class 2620 OID 43002)
-- Name: load_details load_details_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER load_details_hist BEFORE DELETE OR UPDATE ON load_details FOR EACH ROW EXECUTE PROCEDURE trigger_fct_load_details_hist();


--
-- TOC entry 6067 (class 2620 OID 43006)
-- Name: load_materials load_materials_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER load_materials_hist BEFORE DELETE OR UPDATE ON load_materials FOR EACH ROW EXECUTE PROCEDURE trigger_fct_load_materials_hist();


--
-- TOC entry 6068 (class 2620 OID 43007)
-- Name: load_materials load_materials_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER load_materials_trg BEFORE INSERT OR UPDATE ON load_materials FOR EACH ROW EXECUTE PROCEDURE trigger_fct_load_materials_trg();


--
-- TOC entry 6058 (class 2620 OID 42994)
-- Name: loads loads_ains_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER loads_ains_trg AFTER INSERT ON loads FOR EACH ROW EXECUTE PROCEDURE trigger_fct_loads_ains_trg();


--
-- TOC entry 6059 (class 2620 OID 42997)
-- Name: loads loads_bins_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER loads_bins_trg BEFORE INSERT ON loads FOR EACH ROW EXECUTE PROCEDURE trigger_fct_loads_bins_trg();


--
-- TOC entry 6060 (class 2620 OID 42998)
-- Name: loads loads_bupd_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER loads_bupd_trg BEFORE UPDATE ON loads FOR EACH ROW EXECUTE PROCEDURE trigger_fct_loads_bupd_trg();


--
-- TOC entry 6061 (class 2620 OID 43000)
-- Name: loads loads_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER loads_hist BEFORE DELETE OR UPDATE ON loads FOR EACH ROW EXECUTE PROCEDURE trigger_fct_loads_hist();


--
-- TOC entry 6062 (class 2620 OID 43001)
-- Name: loads loads_macsteel_bol_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER loads_macsteel_bol_trg BEFORE UPDATE ON loads FOR EACH ROW EXECUTE PROCEDURE trigger_fct_loads_macsteel_bol_trg();


--
-- TOC entry 6063 (class 2620 OID 43008)
-- Name: loads lod_broker_ref_num_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER lod_broker_ref_num_trg BEFORE INSERT ON loads FOR EACH ROW EXECUTE PROCEDURE trigger_fct_lod_broker_ref_num_trg();


--
-- TOC entry 6070 (class 2620 OID 43009)
-- Name: ltl_product ltl_product_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER ltl_product_hist BEFORE DELETE OR UPDATE ON ltl_product FOR EACH ROW EXECUTE PROCEDURE trigger_fct_ltl_product_hist();


--
-- TOC entry 6071 (class 2620 OID 43010)
-- Name: notes notes_bins_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER notes_bins_trg BEFORE INSERT ON notes FOR EACH ROW EXECUTE PROCEDURE trigger_fct_notes_bins_trg();


--
-- TOC entry 6078 (class 2620 OID 43014)
-- Name: organization_locations ol_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER ol_hist BEFORE DELETE OR UPDATE ON organization_locations FOR EACH ROW EXECUTE PROCEDURE trigger_fct_ol_hist();


--
-- TOC entry 6080 (class 2620 OID 43018)
-- Name: organization_users org_users_bins_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER org_users_bins_trg BEFORE INSERT ON organization_users FOR EACH ROW EXECUTE PROCEDURE trigger_fct_org_users_bins_trg();


--
-- TOC entry 6079 (class 2620 OID 43017)
-- Name: organization_phones organization_phones_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER organization_phones_hist BEFORE DELETE OR UPDATE ON organization_phones FOR EACH ROW EXECUTE PROCEDURE trigger_fct_organization_phones_hist();


--
-- TOC entry 6075 (class 2620 OID 43015)
-- Name: organizations organizations_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER organizations_hist BEFORE DELETE OR UPDATE ON organizations FOR EACH ROW EXECUTE PROCEDURE trigger_fct_organizations_hist();


--
-- TOC entry 6076 (class 2620 OID 43016)
-- Name: organizations organizations_trg; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER organizations_trg BEFORE INSERT ON organizations FOR EACH ROW EXECUTE PROCEDURE trigger_fct_organizations_trg();


--
-- TOC entry 6081 (class 2620 OID 43019)
-- Name: phones phones_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER phones_hist BEFORE DELETE OR UPDATE ON phones FOR EACH ROW EXECUTE PROCEDURE trigger_fct_phones_hist();


--
-- TOC entry 6082 (class 2620 OID 43020)
-- Name: pls_customer_terms pls_customer_terms_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER pls_customer_terms_hist BEFORE DELETE OR UPDATE ON pls_customer_terms FOR EACH ROW EXECUTE PROCEDURE trigger_fct_pls_customer_terms_hist();


--
-- TOC entry 6083 (class 2620 OID 43021)
-- Name: timezone timezone_hist; Type: TRIGGER; Schema: flatbed; Owner: postgres
--

CREATE TRIGGER timezone_hist BEFORE DELETE OR UPDATE ON timezone FOR EACH ROW EXECUTE PROCEDURE trigger_fct_timezone_hist();


SET search_path = public;

commit;

