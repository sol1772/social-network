CREATE TABLE IF NOT EXISTS Account
(
    id           INT AUTO_INCREMENT,
    firstName    VARCHAR(30) NOT NULL,
    lastName     VARCHAR(30) NOT NULL,
    middleName   VARCHAR(30),
    username     VARCHAR(15) NOT NULL,
    email        VARCHAR(50) NOT NULL UNIQUE,
    dateOfBirth  DATE        NOT NULL,
    gender       ENUM ('M', 'F'),
    addInfo      VARCHAR(512),
    passwordHash VARCHAR(128),
    registeredAt TIMESTAMP,
    image        BLOB,
    role         ENUM ('ADMIN', 'USER'),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS Phone
(
    id        INT AUTO_INCREMENT,
    accId     INT,
    phoneNmr  VARCHAR(15)               NOT NULL,
    phoneType ENUM ('PERSONAL', 'WORK') NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT Uq_phones UNIQUE (accId, phoneNmr, phoneType)
);
CREATE TABLE IF NOT EXISTS Address
(
    id       INT AUTO_INCREMENT,
    accId    INT,
    addr     VARCHAR(100)          NOT NULL,
    addrType ENUM ('HOME', 'WORK') NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT Uq_addresses UNIQUE (accId, addr, addrType)
);
CREATE TABLE IF NOT EXISTS Messenger
(
    id       INT AUTO_INCREMENT,
    accId    INT,
    username VARCHAR(50) NOT NULL,
    msgrType ENUM ('SKYPE', 'TELEGRAM', 'WHATSAPP', 'ICQ'),
    PRIMARY KEY (id),
    CONSTRAINT Uq_messengers UNIQUE (accId, username, msgrType)
);
CREATE TABLE IF NOT EXISTS Friend
(
    id       INT AUTO_INCREMENT,
    accId    INT NOT NULL,
    friendId INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT Uq_friends UNIQUE (accId, friendID)
);
CREATE TABLE IF NOT EXISTS InterestGroup
(
    id        INT AUTO_INCREMENT,
    createdBy INT,
    title     VARCHAR(50) NOT NULL UNIQUE,
    metaTitle VARCHAR(512),
    createdAt TIMESTAMP,
    image     BLOB,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS Group_member
(
    id       INT AUTO_INCREMENT,
    accId    INT,
    groupId  INT,
    roleType ENUM ('ADMIN', 'MODER', 'MEMBER'),
    PRIMARY KEY (id),
    CONSTRAINT Uq_members UNIQUE (accId, groupId)
);
CREATE TABLE IF NOT EXISTS Message
(
    id           INT AUTO_INCREMENT,
    accId        INT          NOT NULL,
    trgId        INT          NOT NULL,
    txtContent   VARCHAR(500) NOT NULL,
    mediaContent BLOB,
    msgType      ENUM ('PERSONAL', 'PUBLIC', 'GROUP'),
    createdAt    TIMESTAMP    NOT NULL,
    updatedAt    TIMESTAMP,
    PRIMARY KEY (id)
);