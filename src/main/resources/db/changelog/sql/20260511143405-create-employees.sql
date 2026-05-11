CREATE TABLE IF NOT EXISTS company.employees (
    id uuid NOT NULL,
    name varchar(255) NOT NULL,
    age integer NOT NULL,
    company_id uuid NOT NULL,
    CONSTRAINT pk_employees PRIMARY KEY (id),
    CONSTRAINT fk_employees_companies FOREIGN KEY (company_id) REFERENCES company.companies (id) ON DELETE CASCADE
);
