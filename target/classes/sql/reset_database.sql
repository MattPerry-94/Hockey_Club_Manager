-- Réinitialisation complète de la base Hockey Club Manager
-- ATTENTION: ce script supprime toutes les données existantes

-- 1) Recréation de la base
DROP DATABASE IF EXISTS club_manager;
CREATE DATABASE club_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE club_manager;

-- 2) Tables principales

-- Table des administrateurs (remplace l'ancienne table users)
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role ENUM('ADMIN') NOT NULL
) ENGINE=InnoDB;

-- Table des coachs (autonome, plus de user_id)
CREATE TABLE coaches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- Table des équipes associées aux coachs
CREATE TABLE coach_teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    coach_id INT NOT NULL,
    category VARCHAR(10) NOT NULL,
    CONSTRAINT fk_coach_teams_coach FOREIGN KEY (coach_id)
        REFERENCES coaches(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tarifs des licences par catégorie
CREATE TABLE category_fees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(10) NOT NULL UNIQUE,
    fee DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB;

-- Table des joueurs
CREATE TABLE players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    category VARCHAR(10) NOT NULL,
    role ENUM('CAPITAINE', 'ASSISTANT', 'JOUEUR') NOT NULL,
    position ENUM('GARDIEN', 'DEFENSEUR', 'ATTAQUANT') NOT NULL
) ENGINE=InnoDB;

-- Licences des joueurs
CREATE TABLE licenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_id INT NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    expiration_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_licenses_player FOREIGN KEY (player_id)
        REFERENCES players(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3) Données initiales
INSERT INTO category_fees (category, fee) VALUES
('U9', 100.00),
('U11', 150.00),
('U13', 180.00),
('U15', 220.00),
('U17', 250.00),
('U20', 300.00);

-- Admin par défaut: username=admin, password=admin (hash BCrypt)
INSERT INTO admins (username, password, first_name, last_name, email, role) VALUES
('admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5zsx.C0rFZ84h3vYbVU2vyjYcG8fS', 'Admin', 'System', 'admin@hockey-club.fr', 'ADMIN');