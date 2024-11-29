
CREATE TABLE Tenants (
                         id BIGSERIAL PRIMARY KEY,
                         name TEXT NOT NULL,
                         created_at TIMESTAMP NOT NULL,
                         is_alive BOOLEAN,
                         owner_id BIGINT
);


CREATE TABLE Users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       surname VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       is_alive BOOLEAN,
                       tenant_id BIGINT NOT NULL,
                       department_id BIGINT
);

CREATE TABLE Documents(
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT,
                          type_id BIGINT,
                          state VARCHAR(255),
                          is_alive BOOLEAN
);

CREATE TABLE Document_Types(
                               id BIGSERIAL PRIMARY KEY,
                               name VARCHAR(255),
                               is_alive BOOLEAN,
                               tenant_id BIGINT NOT NULL,
                               CONSTRAINT uniq_name UNIQUE(name),
                               CONSTRAINT fk_tenant FOREIGN KEY (tenant_id) REFERENCES Tenants(id)
);

CREATE TABLE Document_Types_Attributes(
                                          id_attribute BIGSERIAL,
                                          id_document_type BIGSERIAL,
                                          constraint Document_Types_Attributes_pk PRIMARY KEY(id_attribute, id_document_type)
);


CREATE TABLE Attributes(
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255),
                           required BOOLEAN,
                           is_alive BOOLEAN,
                           tenant_id BIGINT NOT NULL,
                           CONSTRAINT fk_tenant FOREIGN KEY (tenant_id) REFERENCES Tenants(id)
);

CREATE TABLE Values(
                       id BIGSERIAL PRIMARY KEY,
                       attribute_id BIGINT,
                       document_version_id BIGINT,
                       value VARCHAR(255)
);


CREATE TABLE User_Roles(
                           user_id BIGINT,
                           role_id BIGINT,
                           constraint User_Roles_pk PRIMARY KEY(user_id, role_id)
);

CREATE TABLE Roles(
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(255)
);

CREATE TABLE Signatures(
                           id BIGSERIAL PRIMARY KEY,
                           hash INTEGER,
                           placeholder_title VARCHAR(255),
                           user_id BIGINT,
                           document_version_id BIGINT
);

CREATE TABLE Signature_Requests(
                                   id BIGSERIAL PRIMARY KEY,
                                   user_id_to BIGINT,
                                   voting_id BIGINT,
                                   document_version_id BIGINT,
                                   status VARCHAR(255)
);


CREATE TABLE Document_Version(
                                 id BIGSERIAL PRIMARY KEY,
                                 version_id BIGINT,
                                 document_id BIGINT,
                                 title VARCHAR(255),
                                 description VARCHAR(255),
                                 created_at TIMESTAMP,
                                 is_alive BOOLEAN DEFAULT TRUE
);

CREATE TABLE Votings (
                         id BIGSERIAL PRIMARY KEY,
                         document_version_id BIGINT,
                         status VARCHAR(255) NOT NULL,
                         approval_threshold FLOAT NOT NULL,
                         current_approval_rate FLOAT,
                         created_at TIMESTAMP NOT NULL,
                         deadline TIMESTAMP NOT NULL
);

CREATE TABLE Comments (
                          id BIGSERIAL PRIMARY KEY,
                          document_id BIGINT,
                          author_id BIGINT,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP NOT NULL
);

CREATE TABLE Departments (
                             id BIGSERIAL PRIMARY KEY,
                             name TEXT,
                             is_alive BOOLEAN,
                             leader_id BIGINT

);
CREATE TABLE Department_Departments (
                                        id BIGSERIAL PRIMARY KEY,
                                        root_id BIGSERIAL NOT NULL,
                                        children_id BIGSERIAL
);