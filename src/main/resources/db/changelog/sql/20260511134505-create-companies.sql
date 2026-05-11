CREATE TABLE IF NOT EXISTS company.companies (
    id uuid NOT NULL,
    name varchar(255) NOT NULL,
    CONSTRAINT pk_companies PRIMARY KEY (id)
);
