CREATE TABLE Documents(
                          document_id SERIAL PRIMARY KEY,
                          title VARCHAR(255),
                          user_id INTEGER,
                          type_id INTEGER,
                          description VARCHAR(255),
                          created_at DATE,
                          version INTEGER
);

CREATE TABLE Document_Types(
                               type_id SERIAL PRIMARY KEY,
                               name VARCHAR(255)
);


CREATE TABLE Attributes(
                           attribute_id SERIAL PRIMARY KEY,
                           document_type_id INTEGER,
                           name VARCHAR(255),
                           required BOOLEAN
);

CREATE TABLE Values(
                       value_id SERIAL PRIMARY KEY,
                       attribute_id INTEGER,
                       document_id INTEGER,
                       value VARCHAR(255)
);

CREATE TABLE Document_Content(
                                 document_id INTEGER PRIMARY KEY,
                                 content VARCHAR(255)
);

CREATE TABLE Users(
                      user_id SERIAL PRIMARY KEY,
                      name VARCHAR(255),
                      surname VARCHAR(255),
                      email VARCHAR(255),
                      password VARCHAR(255),
                      constraint uniq_email UNIQUE(email)
);

CREATE TABLE User_Roles(
                           user_id INTEGER,
                           role_id INTEGER,
                           constraint User_Roles_pk PRIMARY KEY(user_id, role_id)
);

CREATE TABLE Roles(
                      role_id SERIAL PRIMARY KEY,
                      name VARCHAR(255)
);

