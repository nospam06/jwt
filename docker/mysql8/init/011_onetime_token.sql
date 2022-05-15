use testdb;
create table onetime_token(
   `token` VARCHAR(255) NOT NULL,
   `email` VARCHAR(255) NOT NULL,
   `create_date` TIMESTAMP,
   `expiration_date` TIMESTAMP,
   `used_date` TIMESTAMP,
   PRIMARY KEY ( `token` )
);