CREATE TABLE cdr_log
(
    id                UUID         NOT NULL,
    file_name         VARCHAR(255) NOT NULL,
    upload_start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    upload_end_time   TIMESTAMP WITHOUT TIME ZONE,
    records_loaded    INTEGER      NOT NULL,
    records_failed    INTEGER      NOT NULL,
    CONSTRAINT pk_cdr_log PRIMARY KEY (id)
);

CREATE SCHEMA IF NOT EXISTS ussd;

CREATE TABLE ussd.call_detail_records
(
    id               UUID NOT NULL,
    record_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    l_spc            INTEGER,
    l_ssn            INTEGER,
    l_ri             INTEGER,
    l_gt_i           INTEGER,
    l_gt_digits      VARCHAR(18),
    r_spc            INTEGER,
    r_ssn            INTEGER,
    r_ri             INTEGER,
    r_gt_i           INTEGER,
    r_gt_digits      VARCHAR(18),
    service_code     VARCHAR(50),
    or_nature        INTEGER,
    or_plan          INTEGER,
    or_digits        VARCHAR(18),
    de_nature        INTEGER,
    de_plan          INTEGER,
    de_digits        VARCHAR(18),
    isdn_nature      INTEGER,
    isdn_plan        INTEGER,
    msisdn           VARCHAR(18),
    vlr_nature       INTEGER,
    vlr_plan         INTEGER,
    vlr_digits       VARCHAR(18),
    imsi             VARCHAR(100),
    status           VARCHAR(30) NOT NULL,
    type             VARCHAR(30) NOT NULL,
    tstamp           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    local_dialog_id  BIGINT,
    remote_dialog_id BIGINT,
    dialog_duration  BIGINT,
    ussd_string      VARCHAR(255),
    transaction_id   VARCHAR(150) NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_call_detail_records PRIMARY KEY (id)
);