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
  AUTO_INCREMENT = 1001
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `user`
--
INSERT INTO user (email, name, surname, password)
VALUES ('paolo@paolo.it', 'Paolo', 'Cerutti', 'asdasd'),
       ('niccolo@niccolo.it', 'Niccolò', 'Betto', 'pippo'),
       ('user1@user1.it', 'user', '1', 'asd'),
       ('user2@user2.it', 'user', '2', 'asd'),
       ('user2@user2.it', 'user', '3', 'asd');

--
-- Table structure for table `account`
--
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`
(
    `code`    int            NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `userID`  int            NOT NULL,
    `balance` decimal(10, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (userID) REFERENCES `user` (userID) ON UPDATE CASCADE ON DELETE NO ACTION
) DEFAULT CHARSET = utf8mb4
  AUTO_INCREMENT = 2001
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `expenses`
--
INSERT INTO account (userID, balance)
VALUES (1001, 100),
       (1002, 2000),
       (1001, 400),
       (1003, 500),
       (1003, 200),
       (1004, 500);


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
  AUTO_INCREMENT = 100000
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `transfer`
--
INSERT INTO transfer (date, amount, sender, recipient, reason)
VALUES ('2022-05-25', 30.22, 2001, 2002, 'Dinner'),
       ('2022-06-15', 100, 2001, 2003, 'Moving money to prepaid card'),
       ('2022-06-15', 1040, 2004, 2002, 'Test 1'),
       ('2022-06-18', 1200, 2006, 2005, 'Test 2'),
       ('2022-06-3', 110, 2002, 2003, 'Test 3'),
       ('2022-06-20', 100.50, 2006, 2004, 'Test 4');

--
-- Table structure for table `account_book`
--
DROP TABLE IF EXISTS account_book;
CREATE TABLE account_book
(
    `userID`    int         NOT NULL,
    `savedCode` int         NOT NULL,
    `name`      varchar(45) NOT NULL,
    PRIMARY KEY (userID, savedCode),
    FOREIGN KEY (userID) REFERENCES `user` (userID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (savedCode) REFERENCES `account` (code) ON UPDATE CASCADE ON DELETE CASCADE
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

--
-- Dumping data for table `account_book`
--
INSERT INTO account_book (userID, savedCode, name)
VALUES (1001, 2002, 'Niccolò_2'),
       (1002, 2001, 'Paolo_1'),
       (1002, 2003, 'Paolo_3');
