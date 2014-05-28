-- Create the tables for production use
CREATE TABLE user
(
   _id numeric PRIMARY KEY AUTOINCREMENT,
   user varchar(255),
   auth blob 
);

CREATE TABLE entity
(
   _id numeric PRIMARY KEY AUTOINCREMENT,
   ontology varchar(255),
   owner numeric,
   resource varchar(255),
   provider varchar(255),
   FOREIGN KEY (owner) REFERENCES user (_id)
);
