use testdb;
create table user(
   `uuid` VARCHAR(255) NOT NULL,
   `password` VARCHAR(255) NOT NULL,
   `first_name` VARCHAR(255),
   `last_name` VARCHAR(255),
   `email` VARCHAR(255) NOT NULL,
   `phone` VARCHAR(255),
   `create_date` TIMESTAMP,
   `update_date` TIMESTAMP,
   PRIMARY KEY ( `uuid` )
);

alter table user
add unique key user_email (`email` asc) visible;

create table token(
   `token` VARCHAR(255) NOT NULL,
   `email` VARCHAR(255) NOT NULL,
   `create_date` TIMESTAMP,
   `expiration_date` TIMESTAMP,
   `used_date` TIMESTAMP,
   PRIMARY KEY ( `token` )
);

create table user_token(
   `uuid` VARCHAR(255) NOT NULL,
   `user_uuid` VARCHAR(255) NOT NULL,
   `token` VARCHAR(255) NOT NULL,
   `create_date` TIMESTAMP,
   `expiration_date` TIMESTAMP,
   PRIMARY KEY ( `uuid` )
);
ALTER TABLE `user_token`
ADD INDEX `user_token_user_uuid_idx` (`user_uuid` ASC) VISIBLE;
;