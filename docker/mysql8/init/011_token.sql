use testdb;
create table token(
   `token` VARCHAR(255) NOT NULL,
   `email` VARCHAR(255) NOT NULL,
   `create_date` TIMESTAMP,
   `expiration_date` TIMESTAMP,
   `used_date` TIMESTAMP,
   PRIMARY KEY ( `token` )
);