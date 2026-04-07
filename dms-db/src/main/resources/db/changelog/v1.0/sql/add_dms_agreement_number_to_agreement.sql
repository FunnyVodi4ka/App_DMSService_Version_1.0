ALTER TABLE dms_service.agreement
    ADD COLUMN dms_agreement_number VARCHAR(13)
    DEFAULT 'ХХХ-000000000'
    NOT NULL;