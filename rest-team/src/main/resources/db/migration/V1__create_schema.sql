CREATE SEQUENCE country_seq START 1 INCREMENT 1;

CREATE SEQUENCE team_seq START 1 INCREMENT 1;

CREATE TABLE country
(
    id INT8 NOT NULL DEFAULT NEXTVAL('country_seq'),
    code VARCHAR(255),
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE team
(
    id INT8 NOT NULL DEFAULT NEXTVAL('team_seq'),
    deleted   BOOLEAN NOT NULL,
    founded   DATE,
    level     FLOAT8,
    name      VARCHAR(255),
    nick_name VARCHAR(255),
    picture   VARCHAR(255),
    country   INT8    NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE country
    ADD CONSTRAINT uc_country_code UNIQUE (code);

ALTER TABLE team
    ADD CONSTRAINT uc_team UNIQUE (name, country);

ALTER TABLE team
    ADD CONSTRAINT fk_team_country FOREIGN KEY (country) REFERENCES country;
