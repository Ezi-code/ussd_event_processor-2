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
    id                 UUID NOT NULL,
    event_timestamp    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    lac                INTEGER,
    cell_id            INTEGER,
    event_type         INTEGER,
    service_type       INTEGER,
    originating_msisdn VARCHAR(20),
    optional_field     VARCHAR(50),
    protocol_version   INTEGER,
    status_code1       INTEGER,
    status_code2       INTEGER,
    destination_msisdn VARCHAR(20),
    ussd_string        VARCHAR(50),
    flag1              INTEGER,
    flag2              INTEGER,
    msisdn             VARCHAR(20),
    flag3              INTEGER,
    mcc_mnc            INTEGER,
    imsi               VARCHAR(20),
    flag4              INTEGER,
    flag5              INTEGER,
    third_party_msisdn VARCHAR(20),
    reserved1          VARCHAR(100),
    reserved2          VARCHAR(100),
    reserved3          VARCHAR(100),
    reserved4          VARCHAR(100),
    result             VARCHAR(100),
    session_type       VARCHAR(20),
    record_date        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    duration_ms        BIGINT,
    bytes_sent         BIGINT,
    bytes_received     BIGINT,
    metrics            VARCHAR(100),
    transaction_id     VARCHAR(100),
    created_at         TIMESTAMP WITHOUT TIME ZONE,
    file_name          VARCHAR(255),
    CONSTRAINT pk_call_detail_records PRIMARY KEY (id)
);