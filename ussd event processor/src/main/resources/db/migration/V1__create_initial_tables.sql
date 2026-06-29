CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE call_detail_records
(
    id                UUID        NOT NULL,
    record_start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    record_end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    msisdn            VARCHAR(12) NOT NULL,
    imsi              VARCHAR(10),
    raw_line          VARCHAR(255),
    CONSTRAINT pk_call_detail_records PRIMARY KEY (id)
);

CREATE TABLE cdr_log
(
    id                UUID         NOT NULL,
    file_name         VARCHAR(255) NOT NULL,
    upload_start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    upload_end_time   TIMESTAMP WITHOUT TIME ZONE,
    records_loaded    INTEGER      NOT NULL,
    records_failed    INTEGER      NOT NULL,
    CONSTRAINT pk_cdrlog PRIMARY KEY (id)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);