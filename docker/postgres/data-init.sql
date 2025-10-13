-- Script d'insertion des données de test après création des tables
-- Ce script sera exécuté manuellement après le démarrage de l'application

-- Vérifier si les tables existent et insérer les données
DO $$
BEGIN
    -- Vérifier si la table users existe
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'users') THEN
        RAISE NOTICE 'Tables détectées, insertion des données de test...';
        
        -- Vérifier si des données existent déjà
        IF (SELECT COUNT(*) FROM users) = 0 THEN
        
            -- Insérer les utilisateurs de test (mots de passe: admin123, manager123, dev123, etc.)
            INSERT INTO users (id, email, password, first_name, last_name, phone, department, role, status, created_at, updated_at) VALUES
            -- Admins
            (1, 'admin@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin', 'System', '+1234567890', 'IT', 'ADMIN', 'ACTIVE', NOW(), NOW()),
            (2, 'admin2@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Sarah', 'Admin', '+1234567891', 'IT', 'ADMIN', 'ACTIVE', NOW(), NOW()),
            
            -- Managers
            (3, 'manager@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'John', 'Manager', '+1234567892', 'Management', 'MANAGER', 'ACTIVE', NOW(), NOW()),
            (4, 'manager2@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Emma', 'Wilson', '+1234567893', 'Management', 'MANAGER', 'ACTIVE', NOW(), NOW()),
            (5, 'manager3@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Michael', 'Brown', '+1234567894', 'Management', 'MANAGER', 'ACTIVE', NOW(), NOW()),
            
            -- Développeurs
            (6, 'dev1@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Alice', 'Developer', '+1234567895', 'Development', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (7, 'dev2@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Bob', 'Developer', '+1234567896', 'Development', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (8, 'dev3@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Carol', 'Johnson', '+1234567897', 'Development', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (9, 'dev4@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'David', 'Smith', '+1234567898', 'Development', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (10, 'dev5@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Eva', 'Garcia', '+1234567899', 'Development', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (11, 'dev6@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Frank', 'Miller', '+1234567900', 'Development', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            
            -- Designers
            (12, 'designer@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Charlie', 'Designer', '+1234567901', 'Design', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (13, 'designer2@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Grace', 'Lee', '+1234567902', 'Design', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (14, 'designer3@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Henry', 'Taylor', '+1234567903', 'Design', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            
            -- QA Testers
            (15, 'qa@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Diana', 'Tester', '+1234567904', 'Quality Assurance', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (16, 'qa2@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Ivy', 'Anderson', '+1234567905', 'Quality Assurance', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (17, 'qa3@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Jack', 'White', '+1234567906', 'Quality Assurance', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            
            -- DevOps
            (18, 'devops1@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Kevin', 'Clark', '+1234567907', 'DevOps', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (19, 'devops2@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Lisa', 'Rodriguez', '+1234567908', 'DevOps', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            
            -- Product Managers
            (20, 'pm1@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Mark', 'Davis', '+1234567909', 'Product', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (21, 'pm2@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Nancy', 'Martinez', '+1234567910', 'Product', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            
            -- Business Analysts
            (22, 'ba1@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Oliver', 'Thompson', '+1234567911', 'Business', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            (23, 'ba2@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Patricia', 'Garcia', '+1234567912', 'Business', 'MEMBER', 'ACTIVE', NOW(), NOW()),
            
            -- Utilisateurs inactifs pour les tests
            (24, 'inactive1@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Quentin', 'Inactive', '+1234567913', 'Development', 'MEMBER', 'INACTIVE', NOW(), NOW()),
            (25, 'pending1@projecthub.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Rachel', 'Pending', '+1234567914', 'Design', 'MEMBER', 'PENDING', NOW(), NOW());
            
            RAISE NOTICE 'Utilisateurs de test insérés (25 utilisateurs)';
            
            -- Insérer les projets de test
            INSERT INTO projects (id, name, description, priority, status, start_date, due_date, owner_id, created_at, updated_at) VALUES
            (1, 'Application Web E-commerce', 'Développement d''une plateforme e-commerce moderne avec React et Spring Boot', 'HIGH', 'IN_PROGRESS', CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE + INTERVAL '60 days', 3, NOW(), NOW()),
            (2, 'API Mobile Banking', 'API REST pour application mobile de banque en ligne', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '7 days', CURRENT_DATE + INTERVAL '90 days', 3, NOW(), NOW()),
            (3, 'Dashboard Analytics', 'Tableau de bord pour l''analyse des données de l''entreprise', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '45 days', 4, NOW(), NOW()),
            (4, 'Système de Gestion RH', 'Application de gestion des ressources humaines', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE - INTERVAL '10 days', CURRENT_DATE + INTERVAL '50 days', 4, NOW(), NOW()),
            (5, 'Application Mobile Fitness', 'Application mobile pour le suivi de fitness et nutrition', 'LOW', 'TODO', CURRENT_DATE + INTERVAL '14 days', CURRENT_DATE + INTERVAL '120 days', 5, NOW(), NOW()),
            (6, 'Plateforme de Streaming', 'Service de streaming vidéo en ligne', 'HIGH', 'PLANNING', CURRENT_DATE + INTERVAL '30 days', CURRENT_DATE + INTERVAL '180 days', 5, NOW(), NOW()),
            (7, 'Système de Réservation', 'Plateforme de réservation en ligne pour hôtels', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '75 days', 3, NOW(), NOW()),
            (8, 'Application IoT Smart Home', 'Contrôle intelligent de la maison connectée', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '21 days', CURRENT_DATE + INTERVAL '150 days', 4, NOW(), NOW()),
            (9, 'Plateforme E-learning', 'Système d''apprentissage en ligne avec vidéos', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE + INTERVAL '100 days', 5, NOW(), NOW()),
            (10, 'Application de Livraison', 'Service de livraison rapide en ville', 'HIGH', 'PLANNING', CURRENT_DATE + INTERVAL '45 days', CURRENT_DATE + INTERVAL '200 days', 3, NOW(), NOW()),
            (11, 'Système de Gestion Stock', 'Application de gestion d''inventaire', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE + INTERVAL '60 days', 4, NOW(), NOW()),
            (12, 'Plateforme Social Media', 'Réseau social d''entreprise', 'LOW', 'TODO', CURRENT_DATE + INTERVAL '60 days', CURRENT_DATE + INTERVAL '250 days', 5, NOW(), NOW()),
            (13, 'Application de Trading', 'Plateforme de trading en ligne', 'HIGH', 'IN_PROGRESS', CURRENT_DATE - INTERVAL '25 days', CURRENT_DATE + INTERVAL '90 days', 3, NOW(), NOW()),
            (14, 'Système de Facturation', 'Application de gestion de facturation', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE - INTERVAL '12 days', CURRENT_DATE + INTERVAL '45 days', 4, NOW(), NOW()),
            (15, 'Application de Santé', 'Suivi médical et rendez-vous', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '35 days', CURRENT_DATE + INTERVAL '160 days', 5, NOW(), NOW());
            
            RAISE NOTICE 'Projets de test insérés (15 projets)';
            
            -- Insérer les membres des projets
            INSERT INTO project_members (project_id, user_id) VALUES
            -- Projet 1: E-commerce
            (1, 3), (1, 6), (1, 7), (1, 12), (1, 15),
            -- Projet 2: API Banking
            (2, 3), (2, 6), (2, 15),
            -- Projet 3: Dashboard
            (3, 4), (3, 7), (3, 12),
            -- Projet 4: RH
            (4, 4), (4, 8), (4, 13), (4, 16),
            -- Projet 5: Fitness
            (5, 5), (5, 9), (5, 14), (5, 17),
            -- Projet 6: Streaming
            (6, 5), (6, 10), (6, 18), (6, 20),
            -- Projet 7: Réservation
            (7, 3), (7, 11), (7, 19), (7, 21),
            -- Projet 8: IoT
            (8, 4), (8, 6), (8, 12), (8, 15),
            -- Projet 9: E-learning
            (9, 5), (9, 7), (9, 13), (9, 16),
            -- Projet 10: Livraison
            (10, 3), (10, 8), (10, 14), (10, 17),
            -- Projet 11: Stock
            (11, 4), (11, 9), (11, 18), (11, 22),
            -- Projet 12: Social Media
            (12, 5), (12, 10), (12, 19), (12, 23),
            -- Projet 13: Trading
            (13, 3), (13, 11), (13, 20), (13, 15),
            -- Projet 14: Facturation
            (14, 4), (14, 6), (14, 12), (14, 16),
            -- Projet 15: Santé
            (15, 5), (15, 7), (15, 13), (15, 17);
            
            RAISE NOTICE 'Membres des projets assignés';
            
            -- Insérer les tâches de test
            INSERT INTO tasks (id, title, description, priority, status, due_date, estimated_hours, project_id, assignee_id, created_at, updated_at) VALUES
            -- Tâches pour le projet E-commerce (1)
            (1, 'Conception de l''architecture', 'Définir l''architecture technique de l''application e-commerce', 'HIGH', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '10 days', 16, 1, 6, NOW(), NOW()),
            (2, 'Développement du frontend', 'Créer l''interface utilisateur avec React', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '20 days', 40, 1, 7, NOW(), NOW()),
            (3, 'API Backend', 'Développer les APIs REST avec Spring Boot', 'HIGH', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '25 days', 32, 1, 6, NOW(), NOW()),
            (4, 'Design UI/UX', 'Créer les maquettes et prototypes', 'MEDIUM', 'DONE', CURRENT_DATE - INTERVAL '5 days', 24, 1, 12, NOW(), NOW()),
            (5, 'Tests d''intégration', 'Tests automatisés pour l''intégration', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '30 days', 20, 1, 15, NOW(), NOW()),
            
            -- Tâches pour le projet API Banking (2)
            (6, 'Sécurité API', 'Implémenter l''authentification et autorisation', 'HIGH', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '15 days', 24, 2, 6, NOW(), NOW()),
            (7, 'Documentation API', 'Créer la documentation Swagger', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '20 days', 12, 2, 6, NOW(), NOW()),
            (8, 'Tests de sécurité', 'Tests de pénétration et vulnérabilités', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '25 days', 16, 2, 15, NOW(), NOW()),
            
            -- Tâches pour le projet Dashboard (3)
            (9, 'Collecte de données', 'Intégrer les sources de données', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '12 days', 20, 3, 7, NOW(), NOW()),
            (10, 'Visualisations', 'Créer les graphiques et tableaux', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '18 days', 28, 3, 12, NOW(), NOW()),
            (11, 'Dashboard responsive', 'Adapter pour mobile et tablette', 'LOW', 'TODO', CURRENT_DATE + INTERVAL '25 days', 16, 3, 12, NOW(), NOW()),
            
            -- Tâches pour le projet RH (4)
            (12, 'Gestion des employés', 'Module de gestion des profils employés', 'HIGH', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '8 days', 24, 4, 8, NOW(), NOW()),
            (13, 'Système de congés', 'Application de demande et validation de congés', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '15 days', 20, 4, 8, NOW(), NOW()),
            (14, 'Rapports RH', 'Génération de rapports et statistiques', 'LOW', 'TODO', CURRENT_DATE + INTERVAL '22 days', 16, 4, 13, NOW(), NOW()),
            
            -- Tâches pour le projet Fitness (5)
            (15, 'Tracking d''activité', 'Suivi des exercices et calories', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '20 days', 32, 5, 9, NOW(), NOW()),
            (16, 'Interface utilisateur', 'Design de l''app mobile', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '25 days', 24, 5, 14, NOW(), NOW()),
            (17, 'Synchronisation', 'Sync avec appareils fitness', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '30 days', 20, 5, 9, NOW(), NOW()),
            
            -- Tâches pour le projet Streaming (6)
            (18, 'Infrastructure vidéo', 'CDN et streaming vidéo', 'HIGH', 'PLANNING', CURRENT_DATE + INTERVAL '40 days', 48, 6, 10, NOW(), NOW()),
            (19, 'Interface de lecture', 'Player vidéo personnalisé', 'HIGH', 'PLANNING', CURRENT_DATE + INTERVAL '45 days', 36, 6, 10, NOW(), NOW()),
            (20, 'Recommandations', 'Algorithme de recommandation', 'MEDIUM', 'PLANNING', CURRENT_DATE + INTERVAL '50 days', 28, 6, 20, NOW(), NOW()),
            
            -- Tâches pour le projet Réservation (7)
            (21, 'Système de réservation', 'Gestion des créneaux et disponibilités', 'HIGH', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '14 days', 28, 7, 11, NOW(), NOW()),
            (22, 'Paiement en ligne', 'Intégration des moyens de paiement', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '20 days', 24, 7, 11, NOW(), NOW()),
            (23, 'Notifications', 'Système de notifications email/SMS', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '25 days', 16, 7, 19, NOW(), NOW()),
            
            -- Tâches pour le projet IoT (8)
            (24, 'Protocole de communication', 'Communication avec les capteurs', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '25 days', 32, 8, 6, NOW(), NOW()),
            (25, 'Interface de contrôle', 'Application de contrôle des appareils', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '30 days', 28, 8, 12, NOW(), NOW()),
            (26, 'Sécurité IoT', 'Sécurisation des communications', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '35 days', 24, 8, 15, NOW(), NOW()),
            
            -- Tâches pour le projet E-learning (9)
            (27, 'Gestion des cours', 'Création et organisation des cours', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '16 days', 24, 9, 7, NOW(), NOW()),
            (28, 'Lecteur vidéo', 'Player pour les vidéos de cours', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '22 days', 20, 9, 7, NOW(), NOW()),
            (29, 'Système de quiz', 'Tests et évaluations en ligne', 'LOW', 'TODO', CURRENT_DATE + INTERVAL '28 days', 18, 9, 13, NOW(), NOW()),
            
            -- Tâches pour le projet Livraison (10)
            (30, 'Géolocalisation', 'Tracking en temps réel', 'HIGH', 'PLANNING', CURRENT_DATE + INTERVAL '50 days', 36, 10, 8, NOW(), NOW()),
            (31, 'Optimisation des routes', 'Algorithme de planification', 'HIGH', 'PLANNING', CURRENT_DATE + INTERVAL '55 days', 32, 10, 8, NOW(), NOW()),
            (32, 'Interface livreur', 'App pour les livreurs', 'MEDIUM', 'PLANNING', CURRENT_DATE + INTERVAL '60 days', 28, 10, 14, NOW(), NOW()),
            
            -- Tâches pour le projet Stock (11)
            (33, 'Gestion d''inventaire', 'Suivi des stocks en temps réel', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '10 days', 20, 11, 9, NOW(), NOW()),
            (34, 'Alertes de stock', 'Notifications de rupture de stock', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '15 days', 16, 11, 9, NOW(), NOW()),
            (35, 'Rapports d''inventaire', 'Génération de rapports', 'LOW', 'TODO', CURRENT_DATE + INTERVAL '20 days', 12, 11, 18, NOW(), NOW()),
            
            -- Tâches pour le projet Social Media (12)
            (36, 'Feed d''actualités', 'Fil d''actualités personnalisé', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '70 days', 40, 12, 10, NOW(), NOW()),
            (37, 'Système de messages', 'Chat et messagerie privée', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '75 days', 32, 12, 10, NOW(), NOW()),
            (38, 'Profils utilisateurs', 'Gestion des profils et paramètres', 'LOW', 'TODO', CURRENT_DATE + INTERVAL '80 days', 24, 12, 19, NOW(), NOW()),
            
            -- Tâches pour le projet Trading (13)
            (39, 'Moteur de trading', 'Exécution des ordres de trading', 'HIGH', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '18 days', 48, 13, 11, NOW(), NOW()),
            (40, 'Graphiques financiers', 'Visualisation des données de marché', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '25 days', 32, 13, 11, NOW(), NOW()),
            (41, 'Gestion des risques', 'Système de gestion des risques', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '30 days', 28, 13, 20, NOW(), NOW()),
            
            -- Tâches pour le projet Facturation (14)
            (42, 'Génération de factures', 'Création automatique de factures', 'MEDIUM', 'IN_PROGRESS', CURRENT_DATE + INTERVAL '8 days', 20, 14, 6, NOW(), NOW()),
            (43, 'Gestion des paiements', 'Suivi des paiements et relances', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '12 days', 18, 14, 6, NOW(), NOW()),
            (44, 'Rapports financiers', 'Tableaux de bord financiers', 'LOW', 'TODO', CURRENT_DATE + INTERVAL '18 days', 16, 14, 12, NOW(), NOW()),
            
            -- Tâches pour le projet Santé (15)
            (45, 'Gestion des rendez-vous', 'Planification et suivi des RDV', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '40 days', 32, 15, 7, NOW(), NOW()),
            (46, 'Dossiers médicaux', 'Gestion sécurisée des dossiers', 'HIGH', 'TODO', CURRENT_DATE + INTERVAL '45 days', 40, 15, 7, NOW(), NOW()),
            (47, 'Télémédecine', 'Consultations vidéo en ligne', 'MEDIUM', 'TODO', CURRENT_DATE + INTERVAL '50 days', 28, 15, 13, NOW(), NOW());
            
            RAISE NOTICE 'Tâches de test insérées (47 tâches)';
            
            -- Insérer quelques fichiers de test
            INSERT INTO files (id, name, original_name, size, content_type, upload_path, project_id, uploaded_by, created_at, updated_at) VALUES
            (1, 'architecture-ecommerce.pdf', 'architecture-ecommerce.pdf', 2048576, 'application/pdf', '/uploads/project1/architecture-ecommerce.pdf', 1, 6, NOW(), NOW()),
            (2, 'maquettes-ui.fig', 'maquettes-ui.fig', 1536000, 'application/octet-stream', '/uploads/project1/maquettes-ui.fig', 1, 12, NOW(), NOW()),
            (3, 'api-specification.yaml', 'api-specification.yaml', 51200, 'application/x-yaml', '/uploads/project2/api-specification.yaml', 2, 6, NOW(), NOW()),
            (4, 'security-guidelines.docx', 'security-guidelines.docx', 102400, 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', '/uploads/project2/security-guidelines.docx', 2, 15, NOW(), NOW()),
            (5, 'dashboard-mockups.png', 'dashboard-mockups.png', 307200, 'image/png', '/uploads/project3/dashboard-mockups.png', 3, 12, NOW(), NOW()),
            (6, 'data-sources.json', 'data-sources.json', 25600, 'application/json', '/uploads/project3/data-sources.json', 3, 7, NOW(), NOW()),
            (7, 'rh-requirements.pdf', 'rh-requirements.pdf', 1536000, 'application/pdf', '/uploads/project4/rh-requirements.pdf', 4, 8, NOW(), NOW()),
            (8, 'fitness-app-design.sketch', 'fitness-app-design.sketch', 2048000, 'application/octet-stream', '/uploads/project5/fitness-app-design.sketch', 5, 14, NOW(), NOW()),
            (9, 'streaming-architecture.md', 'streaming-architecture.md', 12800, 'text/markdown', '/uploads/project6/streaming-architecture.md', 6, 10, NOW(), NOW()),
            (10, 'reservation-flow.png', 'reservation-flow.png', 512000, 'image/png', '/uploads/project7/reservation-flow.png', 7, 11, NOW(), NOW());
            
            RAISE NOTICE 'Fichiers de test insérés (10 fichiers)';
            
            RAISE NOTICE '=== DONNÉES DE TEST INSÉRÉES AVEC SUCCÈS ===';
            RAISE NOTICE 'Utilisateurs: 25 (2 admins, 3 managers, 6 devs, 3 designers, 3 QA, 2 DevOps, 2 PM, 2 BA, 1 inactive, 1 pending)';
            RAISE NOTICE 'Projets: 15 (différents statuts et priorités)';
            RAISE NOTICE 'Tâches: 47 (réparties sur tous les projets)';
            RAISE NOTICE 'Fichiers: 10 (exemples de documents)';
            RAISE NOTICE '=== MOTS DE PASSE PAR DÉFAUT ===';
            RAISE NOTICE 'admin@projecthub.com / admin123';
            RAISE NOTICE 'manager@projecthub.com / manager123';
            RAISE NOTICE 'dev1@projecthub.com / dev123';
            RAISE NOTICE 'designer@projecthub.com / design123';
            RAISE NOTICE 'qa@projecthub.com / qa123';
            RAISE NOTICE 'devops1@projecthub.com / devops123';
            RAISE NOTICE 'pm1@projecthub.com / pm123';
            RAISE NOTICE 'ba1@projecthub.com / ba123';
            
        ELSE
            RAISE NOTICE 'Des données existent déjà, pas d''insertion nécessaire.';
        END IF;
        
    ELSE
        RAISE NOTICE 'Tables non encore créées, données non insérées.';
    END IF;
END $$;
