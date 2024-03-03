CREATE USER 'JavaUser'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON javaxo.* TO 'JavaUser'@'localhost';
FLUSH PRIVILEGES;