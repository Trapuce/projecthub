# ProjectHub Backend API

## 🚀 Overview

ProjectHub est une API REST complète développée avec Spring Boot pour la gestion de projets, tâches et utilisateurs. L'API fournit une authentification JWT sécurisée, une gestion complète des projets et tâches, ainsi qu'un système d'upload de fichiers.

## 🏗️ Architecture

### Technologies Utilisées
- **Spring Boot 3.5.5** - Framework principal
- **Spring Security** - Authentification et autorisation
- **Spring Data JPA** - Persistance des données
- **PostgreSQL** - Base de données
- **JWT** - Authentification par tokens
- **Swagger/OpenAPI** - Documentation API
- **Docker Compose** - Orchestration des services
- **Redis** - Cache (optionnel)

### Structure du Projet
```
src/main/java/com/trapuce/projectHub/
├── config/          # Configurations (Security, Swagger, CORS)
├── controller/      # Contrôleurs REST
├── dto/            # Data Transfer Objects
├── entity/         # Entités JPA
├── enums/          # Énumérations
├── exception/      # Gestion des erreurs
├── repository/     # Repositories JPA
├── security/       # Configuration sécurité JWT
└── service/        # Services métier
```

## 🔧 Installation et Configuration

### Prérequis
- Java 17+
- Maven 3.6+
- Docker et Docker Compose
- PostgreSQL (via Docker)

### 1. Cloner le projet
```bash
git clone <repository-url>
cd projectHub
```

### 2. Configuration de l'environnement
```bash
# Créer un fichier .env
echo "JWT_SECRET=your-256-bit-secret-key-here-must-be-at-least-256-bits-long-for-security" > .env
```

### 3. Démarrer les services
```bash
# Démarrer PostgreSQL
docker-compose up -d

# Compiler et démarrer l'application
mvn clean install
mvn spring-boot:run
```

### 4. Accéder à l'API
- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 📚 Documentation API

### Authentification

#### POST /api/auth/register
Créer un nouveau compte utilisateur
```json
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "department": "IT",
  "role": "MEMBER"
}
```

#### POST /api/auth/login
Se connecter
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### POST /api/auth/refresh
Rafraîchir le token JWT
```json
{
  "refreshToken": "your-refresh-token"
}
```

### Gestion des Utilisateurs

#### GET /api/users
Récupérer tous les utilisateurs (paginé)
- **Headers**: `Authorization: Bearer <token>`
- **Query Parameters**: `page`, `size`, `sort`

#### GET /api/users/{id}
Récupérer un utilisateur par ID

#### PUT /api/users/{id}
Mettre à jour un utilisateur

#### DELETE /api/users/{id}
Supprimer un utilisateur (admin seulement)

### Gestion des Projets

#### GET /api/projects
Récupérer tous les projets (paginé)

#### POST /api/projects
Créer un nouveau projet
```json
{
  "name": "Mon Projet",
  "description": "Description du projet",
  "priority": "HIGH",
  "startDate": "2024-01-01",
  "dueDate": "2024-12-31",
  "memberIds": [1, 2, 3]
}
```

#### GET /api/projects/{id}
Récupérer un projet par ID

#### PUT /api/projects/{id}
Mettre à jour un projet

#### PUT /api/projects/{id}/status
Changer le statut d'un projet

### Gestion des Tâches

#### GET /api/tasks
Récupérer toutes les tâches (paginé)

#### POST /api/tasks
Créer une nouvelle tâche
```json
{
  "title": "Ma Tâche",
  "description": "Description de la tâche",
  "projectId": 1,
  "priority": "MEDIUM",
  "dueDate": "2024-06-30",
  "estimatedHours": 8,
  "assigneeId": 2,
  "parentTaskId": null
}
```

#### GET /api/tasks/{id}
Récupérer une tâche par ID

#### PUT /api/tasks/{id}
Mettre à jour une tâche

#### PUT /api/tasks/{id}/status
Changer le statut d'une tâche

### Gestion des Fichiers

#### POST /api/files/upload
Uploader un fichier
- **Content-Type**: `multipart/form-data`
- **Parameters**: `file`, `projectId` (optionnel), `taskId` (optionnel)

#### GET /api/files/download/{fileId}
Télécharger un fichier

#### GET /api/files/project/{projectId}
Récupérer les fichiers d'un projet

## 🔒 Sécurité

### Authentification JWT
- **Access Token**: Expire après 24h
- **Refresh Token**: Expire après 7 jours
- **Secret**: Configuré via variable d'environnement `JWT_SECRET`

### Rôles et Permissions
- **ADMIN**: Accès complet à toutes les fonctionnalités
- **MANAGER**: Peut gérer les projets et tâches
- **MEMBER**: Peut voir et modifier ses propres tâches

### CORS
- **Origins autorisées**: `http://localhost:3000`, `http://127.0.0.1:3000`
- **Méthodes**: GET, POST, PUT, DELETE, OPTIONS
- **Headers**: Tous autorisés

## 📊 Base de Données

### Entités Principales
- **User**: Utilisateurs du système
- **Project**: Projets
- **Task**: Tâches
- **Comment**: Commentaires sur les tâches
- **FileAttachment**: Fichiers attachés

### Relations
- Un utilisateur peut être propriétaire de plusieurs projets
- Un projet peut avoir plusieurs membres
- Une tâche appartient à un projet et peut être assignée à un utilisateur
- Une tâche peut avoir des sous-tâches (relation parent-enfant)

## 🚀 Déploiement

### Variables d'Environnement
```bash
JWT_SECRET=your-256-bit-secret-key
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mydatabase
SPRING_DATASOURCE_USERNAME=myuser
SPRING_DATASOURCE_PASSWORD=secret
```

### Docker
```bash
# Build de l'image
docker build -t projecthub-backend .

# Run du container
docker run -p 8080:8080 --env-file .env projecthub-backend
```

## 🧪 Tests

### Tests d'Intégration
```bash
mvn test
```

### Tests avec Testcontainers
Les tests utilisent Testcontainers pour une base de données PostgreSQL isolée.

## 📈 Monitoring

### Actuator Endpoints
- **Health**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`

### Logs
- **Niveau**: DEBUG pour le développement
- **Format**: JSON structuré
- **Rotation**: Configurée automatiquement

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 🆘 Support

Pour toute question ou problème :
- Créer une issue sur GitHub
- Consulter la documentation Swagger UI
- Vérifier les logs de l'application

---

**Développé avec ❤️ par l'équipe ProjectHub**

