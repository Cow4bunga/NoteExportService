-- V2__update_tables.sql

BEGIN;

CREATE TABLE public.old_guids
(
    patient_profile_id uuid,
    index bigint,
    old_client_guid varchar(255)
);

COMMIT;