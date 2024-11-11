-- V1__Create_tables.sql

BEGIN;

CREATE TABLE IF NOT EXISTS public.company_user (
                                                   id UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    login VARCHAR(255) NOT NULL,
    CONSTRAINT company_user_uniq_login UNIQUE (login)
    );

CREATE TABLE IF NOT EXISTS public.patient_profile (
                                                      id UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(255) NULL,
    last_name VARCHAR(255) NULL,
    old_client_guid VARCHAR(255) NULL,
    status_id INTEGER NOT NULL
    );

CREATE TABLE IF NOT EXISTS public.patient_note (
                                                   id UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    created_date_time TIMESTAMP NOT NULL,
    last_modified_date_time TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    last_modified_by_user_id UUID NULL,
    note VARCHAR(4000) NULL,
    patient_id UUID NOT NULL
    );

ALTER TABLE IF EXISTS public.patient_note
    ADD CONSTRAINT fk_pat_note_to_modified_user
    FOREIGN KEY (last_modified_by_user_id) REFERENCES public.company_user(id);

ALTER TABLE IF EXISTS public.patient_note
    ADD CONSTRAINT fk_pat_note_to_created_user
    FOREIGN KEY (created_by_user_id) REFERENCES public.company_user(id);

ALTER TABLE IF EXISTS public.patient_note
    ADD CONSTRAINT fk_pat_note_to_patient
    FOREIGN KEY (patient_id) REFERENCES public.patient_profile(id);

COMMIT;