-- Create the tables
CREATE TABLE user
(
   _id numeric,
   user varchar(255),
   auth blob 
);

CREATE TABLE entity
(
   _id numeric,
   ontology varchar(255),
   owner integer,
   resource varchar(255),
   provider varchar(255)
);

-- Insert the mock data
insert into user (_id, user, auth)
values (1,'admin','admin');

insert into user (_id, user, auth)
values (2,'guest',null);
