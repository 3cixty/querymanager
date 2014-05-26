-- Create the tables
CREATE TABLE PRIVACY_USER
(
   id varchar(255),
   auth blob 
);

CREATE TABLE ENTITY
(
   ontology varchar(255),
   owner varchar(255),
   resource varchar(255),
   provider varchar(255)
);

-- Insert the mock data
INSERT INTO PRIVACY_USER (id, auth)
VALUES ('user@admin', null);

INSERT INTO PRIVACY_USER (id, auth)
VALUES ('user@guest', null);
