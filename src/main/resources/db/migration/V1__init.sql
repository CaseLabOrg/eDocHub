CREATE TABLE Documents(
                          id BIGSERIAL PRIMARY KEY,
                          title VARCHAR(255),
                          user_id BIGINT,
                          type_id BIGINT,
                          description VARCHAR(255),
                          created_at DATE,
                          version INTEGER
);

CREATE TABLE Document_Types(
                               id BIGSERIAL PRIMARY KEY,
                               name VARCHAR(255)
);


CREATE TABLE Attributes(
                           id BIGSERIAL PRIMARY KEY,
                           document_type_id INTEGER,
                           name VARCHAR(255),
                           required BOOLEAN
);

CREATE TABLE Values(
                       id BIGSERIAL PRIMARY KEY,
                       attribute_id INTEGER,
                       document_id INTEGER,
                       value VARCHAR(255)
);

CREATE TABLE Document_Content(
                                 id BIGSERIAL PRIMARY KEY,
                                 content VARCHAR(255)
);

CREATE TABLE Users(
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(255),
                      surname VARCHAR(255),
                      email VARCHAR(255),
                      password VARCHAR(255),
                      constraint uniq_email UNIQUE(email)
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

