CREATE TABLE eid.tenant (
    id bigserial PRIMARY KEY,
    bucket character varying(255),
    endpoint character varying(255),
    name character varying(255) NOT NULL UNIQUE
);

CREATE TABLE eid.user (
    id bigserial PRIMARY KEY,
    enabled boolean NOT NULL,
    password character varying(255) NOT NULL,
    role character varying(255) NOT NULL,
    username character varying(255) NOT NULL UNIQUE,
    tenant bigint NOT NULL,
    CONSTRAINT fk_21hn1a5ja1tve7ae02fnn4cld FOREIGN KEY (tenant) REFERENCES eid.tenant(id)
);

CREATE TABLE eid.signatory (
    id bigserial PRIMARY KEY,
    consent boolean NOT NULL,
    email character varying(255) NOT NULL UNIQUE,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    national_registration_number bigint NOT NULL UNIQUE
);

CREATE TABLE eid.signatory_tenant (
    signatory bigint NOT NULL,
    tenant bigint NOT NULL,
    CONSTRAINT pk_signatory_tenant PRIMARY KEY (signatory, tenant),
    CONSTRAINT fk_3bao161ny6nc59rsq5jt9303n FOREIGN KEY (signatory) REFERENCES eid.signatory(id),
    CONSTRAINT fk_fs0966lrhnd4cx1p2595jj8qd FOREIGN KEY (tenant) REFERENCES eid.tenant(id)
);

CREATE TABLE eid.legal_person (
    id bigserial PRIMARY KEY,
    enterprise_number bigint NOT NULL UNIQUE
);

CREATE TABLE eid.legal_person_tenant (
    legal_person bigint NOT NULL,
    tenant bigint NOT NULL,
    CONSTRAINT pk_legal_person_tenant PRIMARY KEY (legal_person, tenant),
    CONSTRAINT fk_dhlk7d40yb1hasmuoih7wnk5e FOREIGN KEY (tenant) REFERENCES eid.tenant(id),
    CONSTRAINT fk_t2lku4wmeaefh1q69vwi94ktu FOREIGN KEY (legal_person) REFERENCES eid.legal_person(id)
);

CREATE TABLE eid.closing (
    id bigserial PRIMARY KEY,
    created_timestamp timestamp(6),
    date date NOT NULL,
    last_modified_timestamp timestamp(6),
    name character varying(255) NOT NULL,
    zone_offset character varying(6) NOT NULL,
    tenant bigint NOT NULL,
    CONSTRAINT fk_28r54jdf20uxapd9ll4xc9swl FOREIGN KEY (tenant) REFERENCES eid.tenant(id)
);

CREATE TABLE eid.document (
    id bigserial PRIMARY KEY,
    created_timestamp timestamp(6),
    filename character varying(255) NOT NULL,
    last_modified_timestamp timestamp(6),
    media_type character varying(255) NOT NULL,
    tenant bigint NOT NULL,
    CONSTRAINT fk_3n1fmnwawut96uv6etsoppyn2 FOREIGN KEY (tenant) REFERENCES eid.tenant(id)
);

CREATE TABLE eid.closing_legal_person (
    id bigserial PRIMARY KEY,
    status boolean DEFAULT false,
    closing bigint NOT NULL,
    legal_person integer NOT NULL,
    CONSTRAINT fk_sbfjnolasb7ki47onw6hs1fl5 FOREIGN KEY (closing) REFERENCES eid.closing(id),
    CONSTRAINT fk_hfnec6hphjm63bifwwp4o9pks FOREIGN KEY (legal_person) REFERENCES eid.legal_person(id)
);

CREATE TABLE eid.officer (
    id bigserial PRIMARY KEY,
    enterprise_number bigint,
    function character varying(255) NOT NULL,
    closing_legal_person bigint NOT NULL,
    signatory bigint NOT NULL,
    CONSTRAINT officer_unique_constraint UNIQUE (closing_legal_person, signatory),
    CONSTRAINT fk_7foy21sx48iatl2bfugmc9xn1 FOREIGN KEY (closing_legal_person) REFERENCES eid.closing_legal_person(id),
    CONSTRAINT fk_bu5hcphxwyu0413ea7buxeope FOREIGN KEY (signatory) REFERENCES eid.signatory(id)
);

CREATE TABLE eid.closing_signatory (
    id bigserial PRIMARY KEY,
    signature_timestamp timestamp(6),
    closing bigint NOT NULL,
    document bigint NOT NULL,
    signatory bigint NOT NULL,
    CONSTRAINT fk_amkea6t1p36344x6jeb2652jf FOREIGN KEY (closing) REFERENCES eid.closing(id),
    CONSTRAINT fk_4dpb1t6xryd3m4dsr65cs9e8e FOREIGN KEY (signatory) REFERENCES eid.signatory(id),
    CONSTRAINT fk_gv276xrnywepnpiw1h04johx FOREIGN KEY (document) REFERENCES eid.document(id)
);

ALTER TABLE eid.tenant OWNER TO graphioneid;
ALTER TABLE eid.user OWNER TO graphioneid;
ALTER TABLE eid.signatory OWNER TO graphioneid;
ALTER TABLE eid.signatory_tenant OWNER TO graphioneid;
ALTER TABLE eid.legal_person OWNER TO graphioneid;
ALTER TABLE eid.legal_person_tenant OWNER TO graphioneid;
ALTER TABLE eid.closing OWNER TO graphioneid;
ALTER TABLE eid.document OWNER TO graphioneid;
ALTER TABLE eid.closing_legal_person OWNER TO graphioneid;
ALTER TABLE eid.officer OWNER TO graphioneid;
ALTER TABLE eid.closing_signatory OWNER TO graphioneid;