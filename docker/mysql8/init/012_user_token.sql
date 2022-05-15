use testdb;
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
ALTER TABLE `user_token`
ADD CONSTRAINT `user_token_user_uuid`
  FOREIGN KEY (`user_uuid`)
  REFERENCES `user` (`uuid`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;