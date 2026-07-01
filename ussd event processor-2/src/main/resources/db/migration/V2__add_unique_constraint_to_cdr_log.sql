DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uc_cdr_log_file_name'
    ) THEN
        DELETE FROM cdr_log a
            USING cdr_log b
            WHERE a.id < b.id AND a.file_name = b.file_name;
        ALTER TABLE cdr_log ADD CONSTRAINT uc_cdr_log_file_name UNIQUE (file_name);
    END IF;
END $$;
