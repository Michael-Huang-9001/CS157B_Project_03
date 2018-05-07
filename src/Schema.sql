DROP DATABASE IF EXISTS project03;
CREATE DATABASE project03;
USE project03;

/*
CREATE TABLE users(
user_id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
username VARCHAR(25) NOT NULL
);

CREATE TABLE items(
item_id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
item_name VARCHAR(50) NOT NULL,
price DOUBLE NOT NULL
);

CREATE TABLE sales(
sales_id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
user_id INT,
item_id INT,
total DOUBLE NOT NULL,
timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
CONSTRAINT u_id FOREIGN KEY(user_id) REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE SET NULL,
CONSTRAINT i_id FOREIGN KEY(item_id) REFERENCES items(item_id) ON UPDATE CASCADE ON DELETE SET NULL
);
*/

CREATE TABLE sales(
timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP PRIMARY KEY,
product_name VARCHAR(50) NOT NULL,
quantity int not null,
unit_cost double not null,
total_cost double not null
);