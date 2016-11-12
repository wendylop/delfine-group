-- MySQL definition script for auction broker application
SET CHARACTER SET utf8;
DROP DATABASE IF EXISTS broker;
CREATE DATABASE broker CHARACTER SET utf8;
USE broker;

-- define tables, indices, etc.
CREATE TABLE BaseEntity (
	identity BIGINT NOT NULL AUTO_INCREMENT,
	discriminator ENUM("Person", "Auction", "Bid") NOT NULL,
	version INTEGER UNSIGNED NOT NULL DEFAULT 1,
	creationTimestamp BIGINT NOT NULL,
	KEY (discriminator),
	PRIMARY KEY (identity)
) ENGINE=InnoDB;

CREATE TABLE Person (
	personIdentity BIGINT NOT NULL,
	alias CHAR(16) NOT NULL,
	passwordHash BINARY(32) NOT NULL,
	groupAlias ENUM("USER", "ADMIN") NOT NULL,
	givenName VARCHAR(31) NOT NULL,
	familyName VARCHAR(31) NOT NULL,
	street VARCHAR(63) NULL,
	postCode VARCHAR(15) NULL,
	city VARCHAR(63) NOT NULL,
	email VARCHAR(63) NOT NULL,
	phone VARCHAR(63) NULL,
	PRIMARY KEY (PersonIdentity),
	UNIQUE KEY (alias),
	UNIQUE KEY (email),
	FOREIGN KEY (personIdentity) REFERENCES BaseEntity (identity) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Auction (
	auctionIdentity BIGINT NOT NULL,
	sellerReference BIGINT NOT NULL,
	title VARCHAR(255) NOT NULL,
	unitCount SMALLINT UNSIGNED NOT NULL,
	askingPrice BIGINT UNSIGNED NOT NULL,
	closureTimestamp BIGINT NOT NULL,
	description VARCHAR(8189) NOT NULL,
	PRIMARY KEY (auctionIdentity),
	KEY (closureTimestamp),
	FOREIGN KEY (auctionIdentity) REFERENCES BaseEntity (identity) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (sellerReference) REFERENCES Person (personIdentity) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE Bid (
	bidIdentity BIGINT NOT NULL,
	bidderReference BIGINT NOT NULL,
	auctionReference BIGINT NOT NULL,
	price BIGINT UNSIGNED NOT NULL,
	PRIMARY KEY (bidIdentity),
	UNIQUE KEY (bidderReference, auctionReference),
	FOREIGN KEY (bidIdentity) REFERENCES BaseEntity (identity) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (bidderReference) REFERENCES Person (personIdentity) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (auctionReference) REFERENCES Auction (auctionIdentity) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- define views
CREATE ALGORITHM=MERGE VIEW JoinedEntity AS
SELECT *
FROM BaseEntity
LEFT OUTER JOIN Person ON personIdentity = identity
LEFT OUTER JOIN Auction ON auctionIdentity = identity
LEFT OUTER JOIN Bid ON bidIdentity = identity;
