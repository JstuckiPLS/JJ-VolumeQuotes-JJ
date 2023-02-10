
CREATE TABLE flatbed.announcements
(
    announcement_id bigint NOT NULL,
    start_date timestamp(0) without time zone,
    end_date timestamp(0) without time zone,
    theme character varying(500) COLLATE pg_catalog."default",
    text character varying(4000) COLLATE pg_catalog."default",
    announcer_id bigint,
    created_by bigint,
    modified_by bigint,
    date_created timestamp(0) without time zone,
    date_modified timestamp(0) without time zone,
    status character(1) COLLATE pg_catalog."default",
    published_date timestamp(0) without time zone,
    CONSTRAINT announcement_id_pk PRIMARY KEY (announcement_id),
    CONSTRAINT ann_created_by_fk FOREIGN KEY (created_by)
        REFERENCES flatbed.users (person_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT ann_modified_by_fk FOREIGN KEY (modified_by)
        REFERENCES flatbed.users (person_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT announcer_id_fk FOREIGN KEY (announcer_id)
        REFERENCES flatbed.users (person_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE flatbed.announcements OWNER to postgres;

-- Index: ann_created_by_fk_idx

-- DROP INDEX flatbed.ann_created_by_fk_idx;

CREATE INDEX ann_created_by_fk_idx
    ON flatbed.announcements USING btree
    (created_by)
    TABLESPACE pg_default;

-- Index: ann_modified_by_fk_idx

-- DROP INDEX flatbed.ann_modified_by_fk_idx;

CREATE INDEX ann_modified_by_fk_idx
    ON flatbed.announcements USING btree
    (modified_by)
    TABLESPACE pg_default;

-- Index: announcer_id_fk_idx

-- DROP INDEX flatbed.announcer_id_fk_idx;

CREATE INDEX announcer_id_fk_idx
    ON flatbed.announcements USING btree
    (announcer_id)
    TABLESPACE pg_default;

CREATE SEQUENCE flatbed.announcements_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    NO MAXVALUE;

ALTER SEQUENCE flatbed.announcements_seq
    OWNER TO flatbed;
