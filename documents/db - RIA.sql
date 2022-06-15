CREATE DATABASE IF NOT EXISTS `db_tiw_2022`;
USE `db_tiw_2022`;

--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    `userID`   int PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `email`    varchar(45)     NOT NULL,
    `name`     varchar(45)     NOT NULL,
    `surname`  varchar(45)     NOT NULL,
    `password` varchar(45)     NOT NULL
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `user`
--
INSERT INTO user (email, name, surname, password)
VALUES ('paolo@paolo.it', 'Paolo', 'Cerutti', 'asdasd'),
       ('niccolo@niccolo.it', 'Niccolò', 'Betto', 'pincopallino');

--
-- Table structure for table `account`accountuser
--
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`
(
    `code`    int            NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `userID`  int            NOT NULL,
    `balance` decimal(10, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (userID) REFERENCES `user` (userID) ON UPDATE CASCADE ON DELETE NO ACTION
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `expenses`
--
INSERT INTO account (userID, balance)
VALUES (1, 100),
       (2, 2000),
       (1, 400);

--
-- Table structure for table `transfer`
--
DROP TABLE IF EXISTS transfer;
CREATE TABLE transfer
(
    `ID`        int PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `date`      date            NOT NULL,
    `amount`    decimal(10, 2)  NOT NULL,
    `sender`    int             NOT NULL,
    `recipient` int             NOT NULL,
    `reason`    varchar(150)    NOT NULL,
    FOREIGN KEY (sender) REFERENCES `account` (code) ON UPDATE CASCADE ON DELETE NO ACTION,
    FOREIGN KEY (recipient) REFERENCES `account` (code) ON UPDATE CASCADE ON DELETE NO ACTION
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `transfer`
--
INSERT INTO transfer (date, amount, sender, recipient, reason)
VALUES 	('2022-05-25', 30.22, 1, 2, 'dinner'), 
		('2022-06-15', 100, 1, 3, 'moving money to prepaid card');

--
-- Table structure for table `account_book`
--
DROP TABLE IF EXISTS account_book;
CREATE TABLE account_book
(
    `userID`		int NOT NULL PRIMARY KEY,
    `savedCode`		int	NOT NULL,
    `name`     		varchar(45) NOT NULL,
    FOREIGN KEY (userID) REFERENCES `user` (userID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY accountaccount(savedCode) REFERENCES `account` (code) ON UPDATE CASCADE ON DELETE CASCADE
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `account_book`
--
INSERT INTO account_book (userID, savedCode, name)
VALUES 	(1, 2, 'Niccolò_2'),
		(2, 1, 'Paolo_1');
