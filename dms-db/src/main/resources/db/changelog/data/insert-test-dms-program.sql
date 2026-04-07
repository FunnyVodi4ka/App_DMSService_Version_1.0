DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'dmstype' AND typnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'dms_service')) THEN RAISE EXCEPTION 'Type dms_service.dmstype does not exist'; END IF; END $$;

INSERT INTO dms_service.dms_programs (dms_program_id, dms_type, cost, clinic_service, hospitalization, stomatology, ambulance, calling_doctor, telemedicine)
VALUES (
           'f47ac10b-58cc-4372-a567-0e02b2c3d479',
           'BASIC'::dms_service.dmstype,
           50000.00,
           true, false, false, false, true, false
       )
    ON CONFLICT (dms_program_id) DO NOTHING;
