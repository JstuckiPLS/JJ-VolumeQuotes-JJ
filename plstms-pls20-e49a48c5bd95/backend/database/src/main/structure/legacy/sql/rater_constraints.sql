SET search_path = rater, pg_catalog;


--
-- TOC entry 4606 (class 2606 OID 42302)
-- Name: accessorial_types accessorial_types_pkey; Type: CONSTRAINT; Schema: rater; Owner: postgres
--

ALTER TABLE ONLY accessorial_types
    ADD CONSTRAINT accessorial_types_pkey PRIMARY KEY (accessorial_type_code);


--
-- TOC entry 4597 (class 2606 OID 42294)
-- Name: accessorials accessorials_pkey; Type: CONSTRAINT; Schema: rater; Owner: postgres
--

ALTER TABLE ONLY accessorials
    ADD CONSTRAINT accessorials_pkey PRIMARY KEY (accessorial_id);


--
-- TOC entry 4533 (class 2606 OID 42289)
-- Name: cost_detail_items cost_detail_items_pkey; Type: CONSTRAINT; Schema: rater; Owner: postgres
--

ALTER TABLE ONLY cost_detail_items
    ADD CONSTRAINT cost_detail_items_pkey PRIMARY KEY (item_id);


--
-- TOC entry 4545 (class 2606 OID 41518)
-- Name: load_cost_details load_cost_details_pkey; Type: CONSTRAINT; Schema: rater; Owner: postgres
--

ALTER TABLE ONLY load_cost_details
    ADD CONSTRAINT load_cost_details_pkey PRIMARY KEY (cost_detail_id);


--
-- TOC entry 4618 (class 2606 OID 42328)
-- Name: accessorials acc_loc_fk; Type: FK CONSTRAINT; Schema: rater; Owner: postgres
--

ALTER TABLE ONLY accessorials
    ADD CONSTRAINT acc_loc_fk FOREIGN KEY (shipper_loc_id) REFERENCES flatbed.organization_locations(location_id);


--
-- TOC entry 4617 (class 2606 OID 42333)
-- Name: accessorials acc_org_fk; Type: FK CONSTRAINT; Schema: rater; Owner: postgres
--

ALTER TABLE ONLY accessorials
    ADD CONSTRAINT acc_org_fk FOREIGN KEY (shipper_org_id) REFERENCES flatbed.organizations(org_id);


--
-- TOC entry 4621 (class 2606 OID 42313)
-- Name: accessorials acc_org_fk2; Type: FK CONSTRAINT; Schema: rater; Owner: postgres
--

ALTER TABLE ONLY accessorials
    ADD CONSTRAINT acc_org_fk2 FOREIGN KEY (carrier_org_id) REFERENCES flatbed.organizations(org_id);


--
-- TOC entry 4608 (class 2606 OID 42398)
-- Name: cost_detail_items fk_cost_detail_items_reason; Type: FK CONSTRAINT; Schema: rater; Owner: postgres
--

ALTER TABLE ONLY cost_detail_items
    ADD CONSTRAINT fk_cost_detail_items_reason FOREIGN KEY (reason) REFERENCES flatbed.finan_adj_acc_reasons(adj_acc_type_id);

SET search_path = public;

commit;