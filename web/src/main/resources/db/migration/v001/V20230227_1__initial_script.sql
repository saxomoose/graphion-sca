CREATE TABLE sca.tenant (
    id bigserial PRIMARY KEY,
    name character varying(255) NOT NULL UNIQUE,
    bucket character varying(255) NOT NULL,
    version bigint NOT NULL,
    created_date timestamp(6) NOT NULL,
    last_modified_date timestamp(6) NOT NULL
);

CREATE TABLE sca.user (
    tenant bigint NOT NULL,
    id bigserial PRIMARY KEY,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    enabled boolean NOT NULL,
    role character varying(255) NOT NULL,
    version bigint NOT NULL,
    created_by bigint,
    last_modified_by bigint,
    created_date timestamp(6) NOT NULL,
    last_modified_date timestamp(6) NOT NULL,
    CONSTRAINT unique_constraint_user UNIQUE(tenant, username),
    CONSTRAINT fk_21hn1a5ja1tve7ae02fnn4cld FOREIGN KEY (tenant) REFERENCES sca.tenant(id),
    CONSTRAINT fk_xrm5kc7fcipmupq47qb7erbca FOREIGN KEY (created_by) REFERENCES sca.user(id),
    CONSTRAINT fk_fvofgwr9u29o5c9wm5t7rxmf3 FOREIGN KEY (last_modified_by) REFERENCES sca.user(id)
);

CREATE TABLE sca.signatory (
    tenant bigint NOT NULL,
    id bigserial PRIMARY KEY,
    uuid uuid,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    date_of_birth date, 
    email character varying(255) NOT NULL,
    consent boolean DEFAULT false,
    version bigint NOT NULL,
    created_by bigint,
    last_modified_by bigint,
    created_date timestamp(6) NOT NULL,
    last_modified_date timestamp(6) NOT NULL,
    CONSTRAINT unique_constraint_signatory UNIQUE(tenant,email),
    CONSTRAINT fk_gy9ohx5nb3iqb3w4zmiztitnh FOREIGN KEY (tenant) REFERENCES sca.tenant(id),
    CONSTRAINT fk_9oyvpg7hbksavchjtdxdqdjbn FOREIGN KEY (created_by) REFERENCES sca.user(id),
    CONSTRAINT fk_k5ro5xzq35qp34s2d3jtww2u5 FOREIGN KEY (last_modified_by) REFERENCES sca.user(id)
);

CREATE TABLE sca.legal_person (
    tenant bigint NOT NULL,
    id bigserial PRIMARY KEY,
    enterprise_number bigint NOT NULL,
    version bigint NOT NULL,
    created_by bigint,
    last_modified_by bigint,
    created_date timestamp(6) NOT NULL,
    last_modified_date timestamp(6) NOT NULL,
    CONSTRAINT unique_constraint_legal_person UNIQUE(tenant, enterprise_number),
    CONSTRAINT fk_3w7dqdxa2eq4q4q2rpzdid2c2 FOREIGN KEY (tenant) REFERENCES sca.tenant(id),
    CONSTRAINT fk_fo9m7etyd4e3weag5mig43ebu FOREIGN KEY (created_by) REFERENCES sca.user(id),
    CONSTRAINT fk_mzprcu2kgq2hjs5bia2bpaia2 FOREIGN KEY (last_modified_by) REFERENCES sca.user(id)
);

CREATE TABLE sca.closing (
    tenant bigint NOT NULL,
    id bigserial PRIMARY KEY,
    name character varying(255) NOT NULL,
    zone_id character varying(255) NOT NULL,
    timestamp timestamp(6) NOT NULL,
    version bigint NOT NULL,
    created_by bigint,
    last_modified_by bigint,
    created_date timestamp(6) NOT NULL,
    last_modified_date timestamp(6) NOT NULL,
    CONSTRAINT unique_constraint_closing UNIQUE(tenant, name),
    CONSTRAINT fk_28r54jdf20uxapd9ll4xc9swl FOREIGN KEY (tenant) REFERENCES sca.tenant(id),
    CONSTRAINT fk_npngjh2wzvv74ptuxts7cu9ct FOREIGN KEY (created_by) REFERENCES sca.user(id),
    CONSTRAINT fk_dyd3m7iqwxpovnzso95b3qees FOREIGN KEY (last_modified_by) REFERENCES sca.user(id)
);

CREATE TABLE sca.document (
    tenant bigint NOT NULL,
    id bigserial PRIMARY KEY,
    filename character varying(255) NOT NULL,
    file_uuid uuid DEFAULT gen_random_uuid (),
    version bigint NOT NULL,
    created_by bigint,
    last_modified_by bigint,
    created_date timestamp(6) NOT NULL,
    last_modified_date timestamp(6) NOT NULL,
    CONSTRAINT unique_constraint_document_filename UNIQUE (tenant, filename),
    CONSTRAINT fk_3n1fmnwawut96uv6etsoppyn2 FOREIGN KEY (tenant) REFERENCES sca.tenant(id),
    CONSTRAINT fk_4e9wkjnymp324zm7fofswoicj FOREIGN KEY (created_by) REFERENCES sca.user(id),
    CONSTRAINT fk_igz4yymunfjtm3m7aj7cx2z34 FOREIGN KEY (last_modified_by) REFERENCES sca.user(id)
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
    function character varying(255) NOT NULL,
    enterprise_number bigint,
    CONSTRAINT pk_officer PRIMARY KEY (legal_person, signatory),
    CONSTRAINT fk_7foy21sx48iatl2bfugmc9xn1 FOREIGN KEY (legal_person) REFERENCES sca.legal_person(id),
    CONSTRAINT fk_bu5hcphxwyu0413ea7buxeope FOREIGN KEY (signatory) REFERENCES sca.signatory(id)
);

CREATE TABLE sca.closing_signatory (
    closing bigint NOT NULL,
    signatory bigint NOT NULL,
    document bigint NOT NULL,
    signature_timestamp timestamp(6) with time zone,
    CONSTRAINT pk_closing_signatory PRIMARY KEY (closing, signatory, document),
    CONSTRAINT fk_amkea6t1p36344x6jeb2652jf FOREIGN KEY (closing) REFERENCES sca.closing(id),
    CONSTRAINT fk_gv276xrnywepnpiw1h04johxf FOREIGN KEY (document) REFERENCES sca.document(id),
    CONSTRAINT fk_4dpb1t6xryd3m4dsr65cs9e8e FOREIGN KEY (signatory) REFERENCES sca.signatory(id)
);

ALTER TABLE sca.tenant OWNER TO graphionscaweb;
ALTER TABLE sca.user OWNER TO graphionscaweb;
ALTER TABLE sca.signatory OWNER TO graphionscaweb;
ALTER TABLE sca.legal_person OWNER TO graphionscaweb;
ALTER TABLE sca.closing OWNER TO graphionscaweb;
ALTER TABLE sca.document OWNER TO graphionscaweb;
ALTER TABLE sca.closing_legal_person OWNER TO graphionscaweb;
ALTER TABLE sca.officer OWNER TO graphionscaweb;
ALTER TABLE sca.closing_signatory OWNER TO graphionscaweb;
