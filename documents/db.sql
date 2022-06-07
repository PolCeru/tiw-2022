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
    FOREIGN KEY `account` (userID) REFERENCES `user` (userID) ON UPDATE CASCADE ON DELETE NO ACTION
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `expenses`
--
INSERT INTO account (userID, balance)
VALUES (1, 100),
       (2, 2000);

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
    FOREIGN KEY `transfer` (sender) REFERENCES `account` (code) ON UPDATE CASCADE ON DELETE NO ACTION,
    FOREIGN KEY `transfer` (recipient) REFERENCES `account` (code) ON UPDATE CASCADE ON DELETE NO ACTION
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `transfer`
--
INSERT INTO transfer (date, amount, sender, recipient, reason)
VALUES ('2022-05-25', 30.22, 1, 2, 'dinner');