-- To initialize your local MariaDB instance with data, just go into the
-- MariaDB command-line interface as the root MariaDB user, and paste this in.
-- However, WATCH OUT: This script will delete the "getpet" database if it exists.
-- Just be careful to avoid losing data.
DROP DATABASE getpet;
CREATE DATABASE getpet;
USE getpet;

CREATE TABLE Animals (
    intakeNumber INT,
    species VARCHAR(50) NOT NULL,
    vaccinated BOOLEAN,
    breed VARCHAR(50),
    gender CHAR(1) NOT NULL,
    name VARCHAR(50),
    color VARCHAR(50),
    weight DOUBLE NOT NULL, -- Pounds
    cageNumber INT NOT NULL,
    ownerCustomerId INT,
    missing BOOLEAN,
    date DATE NOT NULL,
    spayNeuter BOOLEAN,
    size VARCHAR(50)     -- Dogs: Small 2-22 lbs, Medium 23-57lb, Large 58+ lbs     *As an adult*
	                     -- Cats: Small 2-10 lbs, Medium 10-15 lbs, Large 15+ lbs   *As an adult*
);


------------ Add sample data ------------

INSERT INTO Animals SET
    intakeNumber = 0001,
    species = 'dog',
    vaccinated = TRUE,
    breed = 'Shiba Inu',
    gender = 'm',
    name = 'Doge',
    color = 'gold',
    weight = 42.0,
    cageNumber = 2,
    ownerCustomerId = NULL,
    missing = FALSE,
    date = CURRENT_DATE,
	spayNeuter = true,
	size = 'medium';

INSERT INTO Animals SET
    intakeNumber = 0002,
    species = 'cat',
    vaccinated = TRUE,
    breed = 'Somali Cat',
    gender = 'm',
    name = 'Tupac',
    color = 'gold',
    weight = 8,
    cageNumber = 1,
    ownerCustomerId = 1,
    missing = FALSE,
    date = CURRENT_DATE,
	spayNeuter = true,
	size = 'small';

INSERT INTO Animals SET
    intakeNumber = 0003,
    species = 'dog',
    vaccinated = TRUE,
    breed = 'Goberian',
    gender = 'm',
    name = 'Napolean',
    color = 'gold',
    weight = 79,
    cageNumber = 2,
    ownerCustomerId = 2,
    missing = TRUE,
    date = CURRENT_DATE,
	spayNeuter=true,
	size = 'large';

INSERT INTO Animals SET
    intakeNumber = 0004,
    species = 'dog',
    vaccinated = TRUE,
    breed = 'Springer Spaniel',
    gender = 'm',
    name = 'Sully',
    color = 'brown',
    weight = 46.0,
    cageNumber = 0,
    ownerCustomerId = NULL,
    missing = false,
    date = CURRENT_DATE,
	spayNeuter=true,
	size = 'medium';

INSERT INTO Animals SET
    intakeNumber = 0005,
    species = 'cat',
    vaccinated = TRUE,
    breed = 'Siamese',
    gender = 'f',
    name = 'Sammy',
    color = 'white',
    weight = 10,
    cageNumber = 2,
    ownerCustomerId = 2,
    missing = FALSE,
    date = CURRENT_DATE,
	spayNeuter=true,
	size = 'medium';

INSERT INTO Animals SET
    intakeNumber = 0006,
    species = 'cat',
    vaccinated = TRUE,
    breed = 'Munchkin',
    gender = 'm',
    name = 'Melvin',
    color = 'white',
    weight = 5,
    cageNumber = 2,
    ownerCustomerId = 2,
    missing = FALSE,
    date = CURRENT_DATE,
	spayNeuter=true,
	size = 'small';

INSERT INTO Animals SET
    intakeNumber = 0007,
    species = 'dog',
    vaccinated = true,
    breed = 'Golden Doodle',
    gender = 'm',
    name = 'Piper',
    color = 'black',
    weight = 46.7,
    cageNumber = 8,
    ownerCustomerId = 8,
    missing = false,
    date = CURRENT_DATE,
	spayNeuter=true,
	size = 'medium';

INSERT INTO Animals SET
    intakeNumber = 0008,
    species = 'dog',
    vaccinated = TRUE,
    breed = 'German Shepherd',
    gender = 'm',
    name = 'Bacon',
    color = 'black brown',
    weight = 72,
    cageNumber = 9,
    ownerCustomerId = 9,
    missing = FALSE,
    date = CURRENT_DATE,
	spayNeuter=true,
	size = 'large';

INSERT INTO Animals SET
    intakeNumber = 0009,
    species = 'dog',
    vaccinated = TRUE,
    breed = 'Corgi',
    gender = 'f',
    name = 'Peter Dinklage',
    color = 'gold white',
    weight = 5,
    cageNumber = 13,
    ownerCustomerId = 13,
    missing = FALSE,
    date = CURRENT_DATE,
	spayNeuter=true,
	size = 'small';