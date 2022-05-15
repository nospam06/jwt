CREATE SCHEMA testdb DEFAULT CHARACTER SET utf8;
CREATE USER 'testuser'@'%' IDENTIFIED BY 'testuser';
GRANT ALL PRIVILEGES ON testdb.* TO 'testuser'@'%';