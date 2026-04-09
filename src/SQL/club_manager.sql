-- --------------------------------------------------------
-- Hôte:                         127.0.0.1
-- Version du serveur:           8.4.3 - MySQL Community Server - GPL
-- SE du serveur:                Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Listage de la structure de table club_manager. admins
CREATE TABLE IF NOT EXISTS `admins` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `role` enum('ADMIN') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table club_manager.admins : ~2 rows (environ)
INSERT INTO `admins` (`id`, `username`, `password`, `first_name`, `last_name`, `email`, `role`) VALUES
	(1, 'admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5zsx.C0rFZ84h3vYbVU2vyjYcG8fS', 'Admin', 'System', 'admin@hockey-club.fr', 'ADMIN'),
	(2, 'MattP', '$2a$10$VYJ2/y6g4/8nncdUxmUFv.0xlSkHkF3CJMmXfCYMkC/7AyprDPb1C', 'Matt', 'Perry', 'matt.perry@club.fr', 'ADMIN');

-- Listage de la structure de table club_manager. category_fees
CREATE TABLE IF NOT EXISTS `category_fees` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(10) NOT NULL,
  `fee` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `category` (`category`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table club_manager.category_fees : ~6 rows (environ)
INSERT INTO `category_fees` (`id`, `category`, `fee`) VALUES
	(1, 'U9', 100.00),
	(2, 'U11', 150.00),
	(3, 'U13', 180.00),
	(4, 'U15', 220.00),
	(5, 'U17', 250.00),
	(6, 'U20', 300.00);

-- Listage de la structure de table club_manager. coaches
CREATE TABLE IF NOT EXISTS `coaches` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table club_manager.coaches : ~6 rows (environ)
INSERT INTO `coaches` (`id`, `first_name`, `last_name`, `username`, `email`, `password`) VALUES
	(1, 'Julien', 'Moreau', 'JulienM', 'julien.moreau@hawks.fr', '$2a$10$MwawkKjb.c1FUYctdJaAYuTnNpWymMs9ofoFzwARC7HMSI22cM7tq'),
	(2, 'Stephane', 'Lemoine', 'StephaneL', 'stephane.lemoine@hawks.fr', '$2a$10$PkEN0lSJ3Cj1x5rYrv4vI.ZeqOe4NwBd1khOiAFcXn3XwcDVKvgyC'),
	(3, 'Olivier', 'Renaud', 'OlivierR', 'olivier.renaud@hawks.fr', '$2a$10$nHB7rdlIwX51xb5XnbplPOIY3Fp6dJSW0VYg80cKTtgXsqnDxJDF.'),
	(4, 'Nicolas', 'Perrin', 'NicolasP', 'nicolas.perrin@hawks.fr', '$2a$10$HjPcxpH/pZuQ0SW5J2VlN.6KcLmlzy9h8L9TLMWP/9JFIPdt0cQGy'),
	(5, 'Laurent', 'Dumas', 'LaurentD', 'laurent.dumas@hawks.fr', '$2a$10$Sezn0.1JHxhV4jjDqu7dt.bgGmAU8EUkfeHLbbG.NRAcAYzx1MAb6'),
	(6, 'Francois', 'Delcourt', 'FrancoisD', 'francois.dumas@hawks.fr', '$2a$10$N8XFZ0MQe0fQfZ6iV1Dxz.jQYkK1anIB0DQv.jp6sIBAXpODOlBPm');

-- Listage de la structure de table club_manager. coach_teams
CREATE TABLE IF NOT EXISTS `coach_teams` (
  `id` int NOT NULL AUTO_INCREMENT,
  `coach_id` int NOT NULL,
  `category` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `coach_id` (`coach_id`),
  CONSTRAINT `coach_teams_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `coaches` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table club_manager.coach_teams : ~6 rows (environ)
INSERT INTO `coach_teams` (`id`, `coach_id`, `category`) VALUES
	(2, 2, 'U11'),
	(3, 1, 'U9'),
	(4, 3, 'U13'),
	(5, 4, 'U15'),
	(6, 5, 'U17'),
	(7, 6, 'U20');

-- Listage de la structure de table club_manager. elements
CREATE TABLE IF NOT EXISTS `elements` (
  `id` int NOT NULL AUTO_INCREMENT,
  `abreviation` varchar(255) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `abreviation` (`abreviation`),
  UNIQUE KEY `nom` (`nom`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Listage des données de la table club_manager.elements : ~0 rows (environ)

-- Listage de la structure de table club_manager. legal_informations
CREATE TABLE IF NOT EXISTS `legal_informations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `address` text NOT NULL,
  `reg_no` varchar(50) NOT NULL,
  `publisher` varchar(100) NOT NULL,
  `hosting` text NOT NULL,
  `contact` varchar(100) NOT NULL,
  `privacy` text NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table club_manager.legal_informations : ~2 rows (environ)
INSERT INTO `legal_informations` (`id`, `name`, `address`, `reg_no`, `publisher`, `hosting`, `contact`, `privacy`, `created_at`, `updated_at`) VALUES
	(1, 'Nice Hawks Côte d’Azur', 'Palais des Sports Jean Bouin\n2 Rue Jean Allègre\n06000 Nice\nFrance', 'RNA : W062009874', 'Association Nice Hawks Côte d’Azur', 'Hébergeur : OVHcloud\nSAS au capital de 10 174 560 €\nRCS Lille Métropole 424 761 419 00045\n2 rue Kellermann\n59100 Roubaix\nFrance', 'contact@nicehawks-cotedazur.fr', 'Les informations personnelles collectées via le site du club Nice Hawks Côte d’Azur sont utilisées exclusivement dans le cadre des activités sportives et administratives de l’association. \r\nConformément au Règlement Général sur la Protection des Données (RGPD), vous disposez d’un droit d’accès, de rectification, d’opposition et de suppression des données vous concernant. \r\nToute demande peut être adressée par email à contact@nicehawks-cotedazur.fr.', '2026-01-07 09:41:01', '2026-01-07 09:41:01'),
	(2, 'Nice Hawks Côte d’Azur', 'Palais des Sports Jean Bouin\n2 Rue Jean Allègre\n06000 Nice\nFrance', 'RNA : W062009874', 'Association Nice Hawks Côte d’Azur', 'Hébergeur : OVHcloud\nSAS au capital de 10 174 560 €\nRCS Lille Métropole 424 761 419 00045\n2 rue Kellermann\n59100 Roubaix\nFrance', 'contact@nicehawks-cotedazur.fr', 'Les informations personnelles collectées via le site du club Nice Hawks Côte d’Azur sont utilisées exclusivement dans le cadre des activités sportives et administratives de l’association. \r\nConformément au Règlement Général sur la Protection des Données (RGPD), vous disposez d’un droit d’accès, de rectification, d’opposition et de suppression des données vous concernant. \r\nToute demande peut être adressée par email à contact@nicehawks-cotedazur.fr.', '2026-01-07 09:41:10', '2026-01-07 09:41:10');

-- Listage de la structure de table club_manager. licenses
CREATE TABLE IF NOT EXISTS `licenses` (
  `id` int NOT NULL AUTO_INCREMENT,
  `player_id` int NOT NULL,
  `paid` tinyint(1) NOT NULL DEFAULT '0',
  `expiration_date` date NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `player_id` (`player_id`),
  CONSTRAINT `licenses_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table club_manager.licenses : ~102 rows (environ)
INSERT INTO `licenses` (`id`, `player_id`, `paid`, `expiration_date`, `amount`) VALUES
	(2, 33, 1, '2027-01-07', 150.00),
	(3, 66, 1, '2027-01-07', 220.00),
	(4, 86, 1, '2027-01-07', 250.00),
	(5, 62, 1, '2027-01-07', 220.00),
	(6, 39, 1, '2027-01-07', 180.00),
	(7, 102, 1, '2027-01-07', 300.00),
	(8, 58, 1, '2027-01-07', 220.00),
	(9, 83, 1, '2027-01-07', 250.00),
	(10, 82, 1, '2027-01-07', 250.00),
	(11, 31, 1, '2027-01-07', 150.00),
	(12, 7, 1, '2027-01-07', 100.00),
	(13, 16, 1, '2027-01-07', 100.00),
	(14, 40, 1, '2027-01-07', 180.00),
	(15, 67, 1, '2027-01-07', 220.00),
	(16, 76, 1, '2027-01-07', 250.00),
	(17, 48, 1, '2027-01-07', 180.00),
	(18, 47, 1, '2027-01-07', 180.00),
	(19, 101, 1, '2027-01-07', 300.00),
	(20, 43, 1, '2027-01-07', 180.00),
	(21, 21, 1, '2027-01-07', 150.00),
	(22, 30, 1, '2027-01-07', 150.00),
	(23, 60, 1, '2027-01-07', 220.00),
	(24, 90, 1, '2027-01-07', 300.00),
	(25, 89, 1, '2027-01-07', 300.00),
	(26, 103, 1, '2027-01-07', 300.00),
	(27, 85, 1, '2027-01-07', 250.00),
	(28, 11, 1, '2027-01-07', 100.00),
	(29, 56, 1, '2027-01-07', 220.00),
	(30, 25, 1, '2027-01-07', 150.00),
	(31, 95, 1, '2027-01-07', 300.00),
	(32, 3, 1, '2027-01-07', 100.00),
	(33, 55, 1, '2027-01-07', 220.00),
	(34, 38, 1, '2027-01-07', 180.00),
	(35, 81, 1, '2027-01-07', 250.00),
	(36, 20, 1, '2027-01-07', 150.00),
	(37, 8, 1, '2027-01-07', 100.00),
	(38, 50, 1, '2027-01-07', 180.00),
	(39, 9, 1, '2027-01-07', 100.00),
	(40, 96, 1, '2027-01-07', 300.00),
	(41, 70, 1, '2027-01-07', 250.00),
	(42, 97, 1, '2027-01-07', 300.00),
	(43, 17, 1, '2027-01-07', 100.00),
	(44, 57, 1, '2027-01-07', 220.00),
	(45, 49, 1, '2027-01-07', 180.00),
	(46, 51, 1, '2027-01-07', 180.00),
	(47, 34, 1, '2027-01-07', 150.00),
	(48, 35, 1, '2027-01-07', 150.00),
	(49, 72, 1, '2027-01-07', 250.00),
	(50, 91, 1, '2027-01-07', 300.00),
	(51, 88, 1, '2027-01-07', 300.00),
	(52, 14, 1, '2027-01-07', 100.00),
	(53, 36, 1, '2027-01-07', 180.00),
	(54, 12, 1, '2027-01-07', 100.00),
	(55, 59, 1, '2027-01-07', 220.00),
	(56, 41, 1, '2027-01-07', 180.00),
	(57, 100, 1, '2027-01-07', 300.00),
	(58, 19, 1, '2027-01-07', 150.00),
	(59, 26, 1, '2027-01-07', 150.00),
	(60, 78, 1, '2027-01-07', 250.00),
	(61, 80, 1, '2027-01-07', 250.00),
	(62, 23, 1, '2027-01-07', 150.00),
	(63, 61, 1, '2027-01-07', 220.00),
	(64, 2, 1, '2027-01-07', 100.00),
	(65, 22, 1, '2027-01-07', 150.00),
	(66, 46, 1, '2027-01-07', 180.00),
	(67, 71, 1, '2027-01-07', 250.00),
	(68, 15, 1, '2027-01-07', 100.00),
	(69, 93, 1, '2027-01-07', 300.00),
	(70, 28, 1, '2027-01-07', 150.00),
	(71, 5, 1, '2027-01-07', 100.00),
	(72, 52, 1, '2027-01-07', 180.00),
	(73, 54, 1, '2027-01-07', 220.00),
	(74, 32, 1, '2027-01-07', 150.00),
	(75, 69, 1, '2027-01-07', 220.00),
	(76, 63, 1, '2027-01-07', 220.00),
	(77, 99, 1, '2027-01-07', 300.00),
	(78, 27, 1, '2027-01-07', 150.00),
	(79, 79, 1, '2027-01-07', 250.00),
	(80, 87, 1, '2027-01-07', 300.00),
	(81, 18, 1, '2027-01-07', 100.00),
	(82, 45, 1, '2027-01-07', 180.00),
	(83, 4, 1, '2027-01-07', 100.00),
	(84, 53, 1, '2027-01-07', 220.00),
	(85, 29, 1, '2027-01-07', 150.00),
	(86, 42, 1, '2027-01-07', 180.00),
	(87, 92, 1, '2027-01-07', 300.00),
	(88, 77, 1, '2027-01-07', 250.00),
	(89, 64, 1, '2027-01-07', 220.00),
	(90, 10, 1, '2027-01-07', 100.00),
	(91, 24, 1, '2027-01-07', 150.00),
	(92, 94, 1, '2027-01-07', 300.00),
	(93, 37, 1, '2027-01-07', 180.00),
	(94, 98, 1, '2027-01-07', 300.00),
	(95, 44, 1, '2027-01-07', 180.00),
	(96, 84, 1, '2027-01-07', 250.00),
	(97, 6, 1, '2027-01-07', 100.00),
	(98, 74, 1, '2027-01-07', 250.00),
	(99, 65, 1, '2027-01-07', 220.00),
	(100, 13, 1, '2027-01-07', 100.00),
	(101, 75, 1, '2027-01-07', 250.00),
	(102, 68, 1, '2027-01-07', 220.00),
	(103, 73, 1, '2027-01-07', 250.00);

-- Listage de la structure de table club_manager. players
CREATE TABLE IF NOT EXISTS `players` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `category` varchar(10) NOT NULL,
  `role` enum('CAPITAINE','ASSISTANT','JOUEUR') NOT NULL,
  `position` enum('GARDIEN','DEFENSEUR','ATTAQUANT') NOT NULL,
  `number` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Listage des données de la table club_manager.players : ~102 rows (environ)
INSERT INTO `players` (`id`, `first_name`, `last_name`, `category`, `role`, `position`, `number`) VALUES
	(2, 'Léo', 'Martin', 'U9', 'CAPITAINE', 'ATTAQUANT', 7),
	(3, 'Noah', 'Durand', 'U9', 'ASSISTANT', 'DEFENSEUR', 4),
	(4, 'Adam', 'Petit', 'U9', 'ASSISTANT', 'ATTAQUANT', 9),
	(5, 'Lucas', 'Morel', 'U9', 'JOUEUR', 'GARDIEN', 30),
	(6, 'Hugo', 'Roux', 'U9', 'JOUEUR', 'GARDIEN', 31),
	(7, 'Evan', 'Bernard', 'U9', 'JOUEUR', 'DEFENSEUR', 5),
	(8, 'Tom', 'Fournier', 'U9', 'JOUEUR', 'DEFENSEUR', 6),
	(9, 'Jules', 'Garcia', 'U9', 'JOUEUR', 'DEFENSEUR', 8),
	(10, 'Louis', 'Robert', 'U9', 'JOUEUR', 'DEFENSEUR', 10),
	(11, 'Mathis', 'Dubois', 'U9', 'JOUEUR', 'DEFENSEUR', 12),
	(12, 'Théo', 'Lefevre', 'U9', 'JOUEUR', 'ATTAQUANT', 11),
	(13, 'Paul', 'Simon', 'U9', 'JOUEUR', 'ATTAQUANT', 13),
	(14, 'Nathan', 'Laurent', 'U9', 'JOUEUR', 'ATTAQUANT', 14),
	(15, 'Enzo', 'Michel', 'U9', 'JOUEUR', 'ATTAQUANT', 15),
	(16, 'Raphaël', 'Blanc', 'U9', 'JOUEUR', 'ATTAQUANT', 16),
	(17, 'Sacha', 'Giraud', 'U9', 'JOUEUR', 'ATTAQUANT', 17),
	(18, 'Maël', 'Perrin', 'U9', 'JOUEUR', 'ATTAQUANT', 18),
	(19, 'Maxime', 'Leroy', 'U11', 'CAPITAINE', 'DEFENSEUR', 4),
	(20, 'Arthur', 'Fontaine', 'U11', 'ASSISTANT', 'ATTAQUANT', 9),
	(21, 'Clément', 'Chevalier', 'U11', 'ASSISTANT', 'ATTAQUANT', 11),
	(22, 'Nolan', 'Masson', 'U11', 'JOUEUR', 'GARDIEN', 30),
	(23, 'Alexis', 'Marchand', 'U11', 'JOUEUR', 'GARDIEN', 31),
	(24, 'Baptiste', 'Robin', 'U11', 'JOUEUR', 'DEFENSEUR', 5),
	(25, 'Gabin', 'Dupont', 'U11', 'JOUEUR', 'DEFENSEUR', 6),
	(26, 'Victor', 'Lopez', 'U11', 'JOUEUR', 'DEFENSEUR', 7),
	(27, 'Eliott', 'Perez', 'U11', 'JOUEUR', 'DEFENSEUR', 8),
	(28, 'Oscar', 'Moreau', 'U11', 'JOUEUR', 'DEFENSEUR', 10),
	(29, 'Samuel', 'Renaud', 'U11', 'JOUEUR', 'ATTAQUANT', 12),
	(30, 'Liam', 'Colin', 'U11', 'JOUEUR', 'ATTAQUANT', 13),
	(31, 'Malo', 'Benoit', 'U11', 'JOUEUR', 'ATTAQUANT', 14),
	(32, 'Aaron', 'Nguyen', 'U11', 'JOUEUR', 'ATTAQUANT', 15),
	(33, 'Ilan', 'Adam', 'U11', 'JOUEUR', 'ATTAQUANT', 16),
	(34, 'Timéo', 'Hoarau', 'U11', 'JOUEUR', 'ATTAQUANT', 17),
	(35, 'Naël', 'Klein', 'U11', 'JOUEUR', 'ATTAQUANT', 18),
	(36, 'Antoine', 'Leclerc', 'U13', 'CAPITAINE', 'ATTAQUANT', 10),
	(37, 'Benjamin', 'Rossi', 'U13', 'ASSISTANT', 'DEFENSEUR', 4),
	(38, 'Mathéo', 'Fernandez', 'U13', 'ASSISTANT', 'ATTAQUANT', 9),
	(39, 'Killian', 'Aubert', 'U13', 'JOUEUR', 'GARDIEN', 30),
	(40, 'Florian', 'Bonnet', 'U13', 'JOUEUR', 'GARDIEN', 31),
	(41, 'Corentin', 'Lemoine', 'U13', 'JOUEUR', 'DEFENSEUR', 5),
	(42, 'Quentin', 'Poulain', 'U13', 'JOUEUR', 'DEFENSEUR', 6),
	(43, 'Bastien', 'Chauvin', 'U13', 'JOUEUR', 'DEFENSEUR', 7),
	(44, 'Valentin', 'Roy', 'U13', 'JOUEUR', 'DEFENSEUR', 8),
	(45, 'Julien', 'Perrot', 'U13', 'JOUEUR', 'DEFENSEUR', 11),
	(46, 'Dylan', 'Meunier', 'U13', 'JOUEUR', 'ATTAQUANT', 12),
	(47, 'Noé', 'Caron', 'U13', 'JOUEUR', 'ATTAQUANT', 13),
	(48, 'Lenny', 'Briand', 'U13', 'JOUEUR', 'ATTAQUANT', 14),
	(49, 'Yanis', 'Haddad', 'U13', 'JOUEUR', 'ATTAQUANT', 15),
	(50, 'Romain', 'Gaillard', 'U13', 'JOUEUR', 'ATTAQUANT', 16),
	(51, 'Axel', 'Henry', 'U13', 'JOUEUR', 'ATTAQUANT', 17),
	(52, 'Esteban', 'Morin', 'U13', 'JOUEUR', 'ATTAQUANT', 18),
	(53, 'Thomas', 'Picard', 'U15', 'CAPITAINE', 'DEFENSEUR', 5),
	(54, 'Kylian', 'Muller', 'U15', 'ASSISTANT', 'ATTAQUANT', 9),
	(55, 'Adrien', 'Faure', 'U15', 'ASSISTANT', 'ATTAQUANT', 11),
	(56, 'Nicolas', 'Dumas', 'U15', 'JOUEUR', 'GARDIEN', 30),
	(57, 'Robin', 'Guillot', 'U15', 'JOUEUR', 'GARDIEN', 31),
	(58, 'Loïc', 'Barbier', 'U15', 'JOUEUR', 'DEFENSEUR', 4),
	(59, 'Max', 'Legrand', 'U15', 'JOUEUR', 'DEFENSEUR', 6),
	(60, 'Thibault', 'Cordier', 'U15', 'JOUEUR', 'DEFENSEUR', 7),
	(61, 'Rémi', 'Marechal', 'U15', 'JOUEUR', 'DEFENSEUR', 8),
	(62, 'Pierre', 'Arnaud', 'U15', 'JOUEUR', 'DEFENSEUR', 10),
	(63, 'Cédric', 'Pascal', 'U15', 'JOUEUR', 'ATTAQUANT', 12),
	(64, 'Jordan', 'Ribeiro', 'U15', 'JOUEUR', 'ATTAQUANT', 13),
	(65, 'Mathieu', 'Silva', 'U15', 'JOUEUR', 'ATTAQUANT', 14),
	(66, 'Kevin', 'Alves', 'U15', 'JOUEUR', 'ATTAQUANT', 15),
	(67, 'Loris', 'Boucher', 'U15', 'JOUEUR', 'ATTAQUANT', 16),
	(68, 'Damien', 'Tanguy', 'U15', 'JOUEUR', 'ATTAQUANT', 17),
	(69, 'Gaëtan', 'Noel', 'U15', 'JOUEUR', 'ATTAQUANT', 18),
	(70, 'Alexandre', 'Gauthier', 'U17', 'CAPITAINE', 'ATTAQUANT', 9),
	(71, 'Julian', 'Meyer', 'U17', 'ASSISTANT', 'DEFENSEUR', 4),
	(72, 'Flavien', 'Kovac', 'U17', 'ASSISTANT', 'ATTAQUANT', 11),
	(73, 'Hadrien', 'Weber', 'U17', 'JOUEUR', 'GARDIEN', 30),
	(74, 'Martin', 'Schmitt', 'U17', 'JOUEUR', 'GARDIEN', 31),
	(75, 'Loïs', 'Steiner', 'U17', 'JOUEUR', 'DEFENSEUR', 5),
	(76, 'Simon', 'Bourgeois', 'U17', 'JOUEUR', 'DEFENSEUR', 6),
	(77, 'Yohan', 'Rey', 'U17', 'JOUEUR', 'DEFENSEUR', 7),
	(78, 'Tristan', 'Maillard', 'U17', 'JOUEUR', 'DEFENSEUR', 8),
	(79, 'Mickaël', 'Pelletier', 'U17', 'JOUEUR', 'DEFENSEUR', 10),
	(80, 'Jérémy', 'Marchal', 'U17', 'JOUEUR', 'ATTAQUANT', 12),
	(81, 'Lucas', 'Ferreira', 'U17', 'JOUEUR', 'ATTAQUANT', 13),
	(82, 'Nassim', 'Benali', 'U17', 'JOUEUR', 'ATTAQUANT', 14),
	(83, 'Rayan', 'Belkacem', 'U17', 'JOUEUR', 'ATTAQUANT', 15),
	(84, 'Mehdi', 'Saidi', 'U17', 'JOUEUR', 'ATTAQUANT', 16),
	(85, 'Ibrahim', 'Diop', 'U17', 'JOUEUR', 'ATTAQUANT', 17),
	(86, 'Soufiane', 'Amrani', 'U17', 'JOUEUR', 'ATTAQUANT', 18),
	(87, 'Clovis', 'Perin', 'U20', 'CAPITAINE', 'DEFENSEUR', 4),
	(88, 'Benoît', 'Lambert', 'U20', 'ASSISTANT', 'ATTAQUANT', 9),
	(89, 'Romain', 'Delcourt', 'U20', 'ASSISTANT', 'ATTAQUANT', 11),
	(90, 'Julien', 'Couturier', 'U20', 'JOUEUR', 'GARDIEN', 30),
	(91, 'François', 'Lacroix', 'U20', 'JOUEUR', 'GARDIEN', 31),
	(92, 'Olivier', 'Renard', 'U20', 'JOUEUR', 'DEFENSEUR', 5),
	(93, 'Guillaume', 'Moreau', 'U20', 'JOUEUR', 'DEFENSEUR', 6),
	(94, 'Sébastien', 'Rolland', 'U20', 'JOUEUR', 'DEFENSEUR', 7),
	(95, 'Laurent', 'Dupuis', 'U20', 'JOUEUR', 'DEFENSEUR', 8),
	(96, 'Patrick', 'Garnier', 'U20', 'JOUEUR', 'DEFENSEUR', 10),
	(97, 'Antony', 'Girard', 'U20', 'JOUEUR', 'ATTAQUANT', 12),
	(98, 'Cyril', 'Roussel', 'U20', 'JOUEUR', 'ATTAQUANT', 13),
	(99, 'Nicolas', 'Pellet', 'U20', 'JOUEUR', 'ATTAQUANT', 14),
	(100, 'Damien', 'Lemoine', 'U20', 'JOUEUR', 'ATTAQUANT', 15),
	(101, 'Julien', 'Charpentier', 'U20', 'JOUEUR', 'ATTAQUANT', 16),
	(102, 'Fabien', 'Bailly', 'U20', 'JOUEUR', 'ATTAQUANT', 17),
	(103, 'Arnaud', 'Deschamps', 'U20', 'JOUEUR', 'ATTAQUANT', 18);

-- Listage de la structure de table club_manager. users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `pwd` varchar(255) NOT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `token_expire` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

-- Listage des données de la table club_manager.users : ~2 rows (environ)
INSERT INTO `users` (`id`, `email`, `pwd`, `reset_token`, `token_expire`) VALUES
	(1, 'killyan@gmail.com', '$2y$10$6JFeXDqvYLZqJljgCpM.GuRBmpuNSlKvw0f6Q8JPyEevY2SmZnzbK', NULL, NULL),
	(2, 'killyan.laugier@gmail.com', '$2y$10$RKifVoteJ5GtzU8HnlwOWuhJVQ.7gBxigjcQaelduARw5yaG.ABaS', NULL, NULL);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
