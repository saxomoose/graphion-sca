CREATE TABLE sca.user (
    id bigserial PRIMARY KEY,
    enabled boolean DEFAULT false,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    username character varying(255) NOT NULL UNIQUE,
    date_of_birth date NOT NULL,
    electronic_signature_consent boolean DEFAULT false,
    created_by bigint NOT NULL,
    last_updated_by bigint NOT NULL,
    created_timestamp timestamp(0) NOT NULL,
    last_updated_timestamp timestamp(0) NOT NULL,
    CONSTRAINT fk_xrm5kc7fcipmupq47qb7erbca FOREIGN KEY (created_by) REFERENCES sca.user(id),
    CONSTRAINT fk_fvofgwr9u29o5c9wm5t7rxmf3 FOREIGN KEY (last_updated_by) REFERENCES sca.user(id)
);

CREATE TABLE sca.tenant (
    id bigserial PRIMARY KEY,
    name character varying(255) NOT NULL UNIQUE,
    bucket character varying(255) NOT NULL,
    created_by bigint NOT NULL,
    last_updated_by bigint NOT NULL,
    created_timestamp timestamp(0) NOT NULL,
    last_updated_timestamp timestamp(0) NOT NULL,
    CONSTRAINT fk_r3nv54ng4jy2uaw7vh7a2vv45 FOREIGN KEY (created_by) REFERENCES sca.user(id),
    CONSTRAINT fk_f5vjpc5aodwj2ahcf9zxhu4an FOREIGN KEY (last_updated_by) REFERENCES sca.user(id)
);

CREATE TABLE sca.legal_person (
    tenant bigint NOT NULL,
    id bigserial PRIMARY KEY,
    enterprise_number bigint NOT NULL,
    created_by bigint NOT NULL,
    last_updated_by bigint NOT NULL,
    created_timestamp timestamp(0) NOT NULL,
    last_updated_timestamp timestamp(0) NOT NULL,
    CONSTRAINT unique_constraint_legal_person_enterprise_number UNIQUE(tenant, enterprise_number),
    CONSTRAINT fk_3w7dqdxa2eq4q4q2rpzdid2c2 FOREIGN KEY (tenant) REFERENCES sca.tenant(id),
    CONSTRAINT fk_fo9m7etyd4e3weag5mig43ebu FOREIGN KEY (created_by) REFERENCES sca.user(id),
    CONSTRAINT fk_mzprcu2kgq2hjs5bia2bpaia2 FOREIGN KEY (last_updated_by) REFERENCES sca.user(id)
);

CREATE TABLE sca.closing (
    tenant bigint NOT NULL,
    id bigserial PRIMARY KEY,
    name character varying(255) NOT NULL,
    timestamp timestamp(0) NOT NULL,
    zone_id character varying(255) NOT NULL,
    location character varying(255) NOT NULL,
    expiration_timestamp timestamp(0) NOT NULL,
    invites_sent boolean DEFAULT false,
    created_by bigint NOT NULL,
    last_updated_by bigint NOT NULL,
    created_timestamp timestamp(0) NOT NULL,
    last_updated_timestamp timestamp(0) NOT NULL,
    CONSTRAINT unique_constraint_closing_name UNIQUE (tenant, name),
    CONSTRAINT fk_28r54jdf20uxapd9ll4xc9swl FOREIGN KEY (tenant) REFERENCES sca.tenant(id),
    CONSTRAINT fk_npngjh2wzvv74ptuxts7cu9ct FOREIGN KEY (created_by) REFERENCES sca.user(id),
    CONSTRAINT fk_dyd3m7iqwxpovnzso95b3qees FOREIGN KEY (last_updated_by) REFERENCES sca.user(id)
);

CREATE TABLE sca.document (
    tenant bigint NOT NULL,
    id bigserial PRIMARY KEY,
    filename character varying(255) NOT NULL,
    file_uuid uuid DEFAULT gen_random_uuid (),
    created_by bigint NOT NULL,
    last_updated_by bigint NOT NULL,
    created_timestamp timestamp(0) NOT NULL,
    last_updated_timestamp timestamp(0) NOT NULL,
    CONSTRAINT unique_constraint_document_filename UNIQUE (tenant, filename),
    CONSTRAINT unique_constraint_document_file_uuid UNIQUE (tenant, file_uuid),
    CONSTRAINT fk_3n1fmnwawut96uv6etsoppyn2 FOREIGN KEY (tenant) REFERENCES sca.tenant(id),
    CONSTRAINT fk_4e9wkjnymp324zm7fofswoicj FOREIGN KEY (created_by) REFERENCES sca.user(id),
    CONSTRAINT fk_igz4yymunfjtm3m7aj7cx2z34 FOREIGN KEY (last_updated_by) REFERENCES sca.user(id)
);

CREATE TABLE sca.tenant_user (
    tenant bigint NOT NULL,
    "user" bigint NOT NULL,
    role character varying(255) NOT NULL,
    default_connection boolean DEFAULT false,
    CONSTRAINT pk_tenant_user PRIMARY KEY (tenant, "user"),
    CONSTRAINT fk_qsj3awwwowmeyixcg2tnkev42 FOREIGN KEY (tenant) REFERENCES sca.tenant(id),
    CONSTRAINT fk_rmhjty3j34w7h2phsg4rgr22a FOREIGN KEY ("user") REFERENCES sca.user(id)
);

CREATE TABLE sca.closing_legal_person (
    closing bigint NOT NULL,
    legal_person integer NOT NULL,
    status boolean DEFAULT false,
    CONSTRAINT pk_closing_legal_person PRIMARY KEY (closing, legal_person),
    CONSTRAINT fk_sbfjnolasb7ki47onw6hs1fl5 FOREIGN KEY (closing) REFERENCES sca.closing(id),
    CONSTRAINT fk_hfnec6hphjm63bifwwp4o9pks FOREIGN KEY (legal_person) REFERENCES sca.legal_person(id)
);

CREATE TABLE sca.officer (
    legal_person bigint NOT NULL,
    signatory bigint NOT NULL,
    "function" character varying(255) NOT NULL,
    enterprise_number bigint,
    CONSTRAINT pk_officer PRIMARY KEY (legal_person, signatory, "function"),
    CONSTRAINT fk_7foy21sx48iatl2bfugmc9xn1 FOREIGN KEY (legal_person) REFERENCES sca.legal_person(id),
    CONSTRAINT fk_bu5hcphxwyu0413ea7buxeope FOREIGN KEY (signatory) REFERENCES sca.user(id)
);

CREATE TABLE sca.closing_signatory (
    closing bigint NOT NULL,
    signatory bigint NOT NULL,
    document bigint NOT NULL,
    signature_timestamp timestamp(0) DEFAULT NULL,
    CONSTRAINT pk_closing_signatory PRIMARY KEY (closing, signatory, document),
    CONSTRAINT fk_amkea6t1p36344x6jeb2652jf FOREIGN KEY (closing) REFERENCES sca.closing(id),
    CONSTRAINT fk_gv276xrnywepnpiw1h04johxf FOREIGN KEY (document) REFERENCES sca.document(id),
    CONSTRAINT fk_4dpb1t6xryd3m4dsr65cs9e8e FOREIGN KEY (signatory) REFERENCES sca.user(id)
);

CREATE TRIGGER insert_update_timestamp
BEFORE INSERT OR UPDATE ON sca.user
FOR EACH ROW
EXECUTE FUNCTION public.update_timestamps();

CREATE TRIGGER insert_update_timestamp
BEFORE INSERT OR UPDATE ON sca.tenant
FOR EACH ROW
EXECUTE FUNCTION public.update_timestamps();

CREATE TRIGGER insert_update_timestamp
BEFORE INSERT OR UPDATE ON sca.legal_person
FOR EACH ROW
EXECUTE FUNCTION public.update_timestamps();

CREATE TRIGGER insert_update_timestamp
BEFORE INSERT OR UPDATE ON sca.closing
FOR EACH ROW
EXECUTE FUNCTION public.update_timestamps();

CREATE TRIGGER insert_update_timestamp
BEFORE INSERT OR UPDATE ON sca.document
FOR EACH ROW
EXECUTE FUNCTION public.update_timestamps();

-- CREATE OR REPLACE FUNCTION sca.set_default_expiration_timestamp() 
-- RETURNS TRIGGER AS $$
-- BEGIN
--     NEW.expiration_timestamp := NEW.timestamp + INTERVAL '30 days';
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;

-- CREATE TRIGGER set_default_expiration_timestamp_trigger
-- BEFORE INSERT OR UPDATE ON sca.closing
-- FOR EACH ROW
-- EXECUTE FUNCTION sca.set_default_expiration_timestamp();

INSERT INTO sca.user
    VALUES (1, true, 'Mathieu', 'Tulpinck', 'admin@graphion.eu', '1990-11-08', true, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT setval('sca.user_id_seq', 1);

INSERT INTO sca.tenant
    VALUES (1, 'graphion-test', 'graphion-test', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SELECT setval('sca.tenant_id_seq', 1);

INSERT INTO sca.tenant_user
    VALUES (1, 1, 'ROLE_ADMIN', false);


