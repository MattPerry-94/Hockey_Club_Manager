-- Création de la base de données
CREATE DATABASE IF NOT EXISTS club_manager;
USE club_manager;

-- Table des administrateurs
CREATE TABLE IF NOT EXISTS admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role ENUM('ADMIN') NOT NULL
);

-- Table des coachs
CREATE TABLE IF NOT EXISTS coaches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Table des équipes par coach
CREATE TABLE IF NOT EXISTS coach_teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    coach_id INT NOT NULL,
    category VARCHAR(10) NOT NULL,
    FOREIGN KEY (coach_id) REFERENCES coaches(id) ON DELETE CASCADE
);

-- Table des tarifs par catégorie
CREATE TABLE IF NOT EXISTS category_fees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(10) NOT NULL UNIQUE,
    fee DECIMAL(10,2) NOT NULL
);

-- Table des joueurs
CREATE TABLE IF NOT EXISTS players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    category VARCHAR(10) NOT NULL,
    role ENUM('CAPITAINE', 'ASSISTANT', 'JOUEUR') NOT NULL,
    position ENUM('GARDIEN', 'DEFENSEUR', 'ATTAQUANT') NOT NULL,
    number INT NULL
);

-- Table des licences
CREATE TABLE IF NOT EXISTS licenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_id INT NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    expiration_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

-- Insertion des données initiales pour les tarifs
INSERT INTO category_fees (category, fee) VALUES
('U9', 100.00),
('U11', 150.00),
('U13', 180.00),
('U15', 220.00),
('U17', 250.00),
('U20', 300.00);

-- Insertion d'un utilisateur administrateur par défaut (mot de passe: admin)
INSERT INTO admins (username, password, first_name, last_name, email, role) VALUES
('admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5zsx.C0rFZ84h3vYbVU2vyjYcG8fS', 'Admin', 'System', 'admin@hockey-club.fr', 'ADMIN');