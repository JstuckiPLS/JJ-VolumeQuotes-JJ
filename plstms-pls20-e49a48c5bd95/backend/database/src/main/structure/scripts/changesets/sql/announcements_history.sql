
CREATE TABLE flatbed.announcements_history
(
    announcement_hist_id bigint NOT NULL,
    announcement_id bigint,
    person_id bigint,
    read_date timestamp(0) without time zone,
    CONSTRAINT announcement_hist_id_pk PRIMARY KEY (announcement_hist_id),
    CONSTRAINT ann_hist_person_id_fk FOREIGN KEY (person_id)
        REFERENCES flatbed.users (person_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT announcement_id_fk FOREIGN KEY (announcement_id)
        REFERENCES flatbed.announcements (announcement_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE flatbed.announcements_history
    OWNER to postgres;

-- Index: ann_hist_person_id_fk_idx

-- DROP INDEX flatbed.ann_hist_person_id_fk_idx;

CREATE INDEX ann_hist_person_id_fk_idx
    ON flatbed.announcements_history USING btree
    (person_id)
    TABLESPACE pg_default;

CREATE SEQUENCE flatbed.announcements_hist_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    NO MAXVALUE;

ALTER SEQUENCE flatbed.announcements_hist_seq
    OWNER TO flatbed;
