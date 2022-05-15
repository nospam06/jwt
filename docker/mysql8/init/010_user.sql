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