-- Migration sans DROP pour aligner le schéma avec l’application
-- Exécuter dans MySQL: USE club_manager;

USE club_manager;

-- 1) Table admins (remplace la logique de l’ancienne table users)
CREATE TABLE IF NOT EXISTS admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role ENUM('ADMIN') NOT NULL
) ENGINE=InnoDB;

-- Admin par défaut (mot de passe = "admin")
INSERT INTO admins (username, password, first_name, last_name, email, role)
VALUES ('admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5zsx.C0rFZ84h3vYbVU2vyjYcG8fS', 'Admin', 'System', 'admin@hockey-club.fr', 'ADMIN')
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- 2) Table coaches: ajout des colonnes manquantes sans casser les données existantes
-- Étape A: ajouter colonnes en NULL si elles n’existent pas
-- NOTE: MySQL < 8 peut ne pas supporter IF NOT EXISTS pour ADD COLUMN.
-- Si votre serveur ne supporte pas IF NOT EXISTS, exécutez manuellement les 3 lignes ADD COLUMN
-- et ignorez les erreurs "Duplicate column name" si elles apparaissent.

ALTER TABLE coaches ADD COLUMN username VARCHAR(50) NULL;
ALTER TABLE coaches ADD COLUMN email VARCHAR(100) NULL;
ALTER TABLE coaches ADD COLUMN password VARCHAR(255) NULL;

-- Étape B: pré-remplir les valeurs manquantes
UPDATE coaches SET username = CONCAT(LOWER(first_name), '.', LOWER(last_name))
WHERE username IS NULL OR username = '';

UPDATE coaches SET email = CONCAT(LOWER(first_name), '.', LOWER(last_name), '@club.local')
WHERE email IS NULL OR email = '';

-- Mot de passe par défaut (à remplacer via l’interface ensuite)
UPDATE coaches SET password = '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5zsx.C0rFZ84h3vYbVU2vyjYcG8fS'
WHERE password IS NULL OR password = '';

-- Étape C: rendre les colonnes obligatoires et unique pour username
ALTER TABLE coaches MODIFY COLUMN username VARCHAR(50) NOT NULL;
ALTER TABLE coaches ADD UNIQUE KEY uq_coaches_username (username);
ALTER TABLE coaches MODIFY COLUMN email VARCHAR(100) NOT NULL;
ALTER TABLE coaches MODIFY COLUMN password VARCHAR(255) NOT NULL;

-- 3) Tables annexes si absentes
CREATE TABLE IF NOT EXISTS coach_teams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    coach_id INT NOT NULL,
    category VARCHAR(10) NOT NULL,
    CONSTRAINT fk_coach_teams_coach FOREIGN KEY (coach_id)
        REFERENCES coaches(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS category_fees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(10) NOT NULL UNIQUE,
    fee DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    category VARCHAR(10) NOT NULL,
    role ENUM('CAPITAINE', 'ASSISTANT', 'JOUEUR') NOT NULL,
    position ENUM('GARDIEN', 'DEFENSEUR', 'ATTAQUANT') NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS licenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_id INT NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    expiration_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_licenses_player FOREIGN KEY (player_id)
        REFERENCES players(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4) Tarifs initiaux (insertion idempotente)
INSERT IGNORE INTO category_fees (category, fee) VALUES
('U9', 100.00),
('U11', 150.00),
('U13', 180.00),
('U15', 220.00),
('U17', 250.00),
('U20', 300.00);

-- Fin de la migration sans DROP