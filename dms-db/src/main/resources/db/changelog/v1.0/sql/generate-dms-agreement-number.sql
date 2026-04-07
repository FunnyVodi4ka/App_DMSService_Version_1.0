CREATE SEQUENCE IF NOT EXISTS dms_service.dms_agreement_number_sequence
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9999
    NO CYCLE;

CREATE OR REPLACE FUNCTION dms_service.generate_dms_agreement_number(p_dms_insurance_id UUID)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS
$$
DECLARE
    current_year               TEXT;
    current_month              TEXT;
    next_sequence_number       TEXT;
    generated_agreement_number VARCHAR(13);
    insurance_type             INT;
BEGIN
    current_year := to_char(current_date, 'YY');
    current_month := to_char(current_date, 'MM');

    IF to_char(current_date, 'YYYY-MM') <> (SELECT MAX(to_char(date, 'YYYY-MM'))
                                            FROM dms_service.agreement) THEN
        PERFORM setval('dms_service.dms_agreement_number_sequence', 1, false);
    END IF;

    SELECT CASE dp.dms_type
            WHEN 'BASIC'
                THEN 1
            WHEN 'COMPREHENSIVE'
                THEN 2
            WHEN 'MAXIMUM'
                THEN 3
            END INTO insurance_type
        FROM dms_service.dms_insurance di
        LEFT JOIN dms_service.dms_program dp
    ON di.dms_program_id = dp.dms_program_id
        WHERE di.dms_insurance_id = p_dms_insurance_id;

    next_sequence_number := LPAD(nextval('dms_service.dms_agreement_number_sequence')::text, 4, '0');
    generated_agreement_number := 'DMS-' || insurance_type || current_year || current_month || next_sequence_number;
RETURN generated_agreement_number;
END;
$$;