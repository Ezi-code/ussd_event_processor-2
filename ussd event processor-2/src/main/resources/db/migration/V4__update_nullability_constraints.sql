-- Make event_timestamp nullable and msisdn, imsi non-nullable in ussd.call_detail_records
ALTER TABLE ussd.call_detail_records ALTER COLUMN event_timestamp DROP NOT NULL;
ALTER TABLE ussd.call_detail_records ALTER COLUMN msisdn SET NOT NULL;
ALTER TABLE ussd.call_detail_records ALTER COLUMN imsi SET NOT NULL;