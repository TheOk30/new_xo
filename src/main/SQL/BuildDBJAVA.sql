-- Create the database
CREATE DATABASE IF NOT EXISTS javaxo;

-- Switch to the created database
USE javaxo;

-- Create the 'players' table
CREATE TABLE IF NOT EXISTS `players` (
  `playerId` int unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create the 'playerstats' table
CREATE TABLE IF NOT EXISTS `playerstats` (
  `playerId` int unsigned NOT NULL,
  `wins` int DEFAULT NULL,
  `losses` int DEFAULT NULL,
  `draws` int DEFAULT NULL,
  PRIMARY KEY (`playerId`),
  FOREIGN KEY (`playerId`) REFERENCES `players`(`playerId`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
