# Exemples de Tests pour l'API ProjectHub

Base URL : `https://projectHub.trapuce.tech`

## 🔐 Authentification

### 1. Inscription (Register)

```bash
curl -X POST https://projectHub.trapuce.tech/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+33612345678",
    "department": "IT",
    "role": "MEMBER"
  }'
```

**Réponse attendue :**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "email": "john.doe@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "MEMBER"
    }
  }
}
```

### 2. Connexion (Login)

```bash
curl -X POST https://projectHub.trapuce.tech/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

**Réponse attendue :**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "email": "john.doe@example.com",
      "firstName": "John",
      "lastName": "Doe"
    }
  }
}
```

**💡 Astuce :** Sauvegardez le token pour les requêtes suivantes :
```bash
# Sauvegarder le token dans une variable
TOKEN=$(curl -s -X POST https://projectHub.trapuce.tech/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com","password":"password123"}' \
  | jq -r '.data.accessToken')

echo "Token: $TOKEN"
```

### 3. Rafraîchir le Token (Refresh Token)

```bash
curl -X POST https://projectHub.trapuce.tech/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "votre-refresh-token-ici"
  }'
```

### 4. Déconnexion (Logout)

```bash
curl -X POST https://projectHub.trapuce.tech/api/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```

---

## 👤 Gestion des Utilisateurs

### 1. Obtenir tous les utilisateurs (paginated)

```bash
curl -X GET "https://projectHub.trapuce.tech/api/users?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

### 2. Obtenir un utilisateur par ID

```bash
curl -X GET https://projectHub.trapuce.tech/api/users/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Obtenir le profil de l'utilisateur actuel

```bash
curl -X GET https://projectHub.trapuce.tech/api/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Rechercher des utilisateurs

```bash
curl -X GET "https://projectHub.trapuce.tech/api/users/search?q=john&page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📁 Gestion des Projets

### 1. Créer un nouveau projet

```bash
curl -X POST https://projectHub.trapuce.tech/api/projects \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Nouveau Projet",
    "description": "Description du nouveau projet",
    "priority": "HIGH",
    "startDate": "2024-01-01",
    "dueDate": "2024-12-31",
    "memberIds": [2, 3]
  }'
```

**Réponse attendue :**
```json
{
  "success": true,
  "message": "Project created successfully",
  "data": {
    "id": 1,
    "name": "Nouveau Projet",
    "description": "Description du nouveau projet",
    "status": "TODO",
    "priority": "HIGH",
    "owner": {
      "id": 1,
      "email": "john.doe@example.com",
      "firstName": "John",
      "lastName": "Doe"
    },
    "members": [...],
    "startDate": "2024-01-01",
    "dueDate": "2024-12-31",
    "createdAt": "2024-11-02T17:00:00"
  }
}
```

### 2. Obtenir tous les projets

```bash
curl -X GET "https://projectHub.trapuce.tech/api/projects?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Obtenir mes projets

```bash
curl -X GET "https://projectHub.trapuce.tech/api/projects/my-projects?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Obtenir un projet par ID

```bash
curl -X GET https://projectHub.trapuce.tech/api/projects/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Rechercher des projets

```bash
curl -X GET "https://projectHub.trapuce.tech/api/projects/search?search=nouveau&page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

### 6. Obtenir les projets par statut

```bash
curl -X GET "https://projectHub.trapuce.tech/api/projects/status/IN_PROGRESS?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

**Statuts disponibles :** `TODO`, `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`, `ARCHIVED`

### 7. Mettre à jour un projet

```bash
curl -X PUT https://projectHub.trapuce.tech/api/projects/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Projet Modifié",
    "description": "Nouvelle description",
    "priority": "MEDIUM",
    "dueDate": "2024-12-31"
  }'
```

### 8. Changer le statut d'un projet

```bash
curl -X PUT "https://projectHub.trapuce.tech/api/projects/1/status?status=IN_PROGRESS" \
  -H "Authorization: Bearer $TOKEN"
```

### 9. Ajouter un membre à un projet

```bash
curl -X POST https://projectHub.trapuce.tech/api/projects/1/members/2 \
  -H "Authorization: Bearer $TOKEN"
```

### 10. Supprimer un membre d'un projet

```bash
curl -X DELETE https://projectHub.trapuce.tech/api/projects/1/members/2 \
  -H "Authorization: Bearer $TOKEN"
```

### 11. Supprimer un projet

```bash
curl -X DELETE https://projectHub.trapuce.tech/api/projects/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 12. Statistiques des projets

```bash
curl -X GET https://projectHub.trapuce.tech/api/projects/stats \
  -H "Authorization: Bearer $TOKEN"
```

---

## ✅ Gestion des Tâches

### 1. Créer une nouvelle tâche

```bash
curl -X POST https://projectHub.trapuce.tech/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Ma Tâche",
    "description": "Description de la tâche",
    "projectId": 1,
    "priority": "MEDIUM",
    "dueDate": "2024-06-30",
    "estimatedHours": 8,
    "assigneeId": 2,
    "parentTaskId": null
  }'
```

### 2. Obtenir toutes les tâches

```bash
curl -X GET "https://projectHub.trapuce.tech/api/tasks?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Obtenir mes tâches

```bash
curl -X GET "https://projectHub.trapuce.tech/api/tasks/my-tasks?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Obtenir une tâche par ID

```bash
curl -X GET https://projectHub.trapuce.tech/api/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Obtenir les tâches d'un projet

```bash
curl -X GET "https://projectHub.trapuce.tech/api/tasks/project/1?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

### 6. Mettre à jour une tâche

```bash
curl -X PUT https://projectHub.trapuce.tech/api/tasks/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tâche Modifiée",
    "description": "Nouvelle description",
    "priority": "HIGH",
    "dueDate": "2024-07-15"
  }'
```

### 7. Changer le statut d'une tâche

```bash
curl -X PUT "https://projectHub.trapuce.tech/api/tasks/1/status?status=IN_PROGRESS" \
  -H "Authorization: Bearer $TOKEN"
```

**Statuts disponibles :** `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `DONE`, `CANCELLED`

### 8. Supprimer une tâche

```bash
curl -X DELETE https://projectHub.trapuce.tech/api/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📎 Gestion des Fichiers

### 1. Uploader un fichier

```bash
curl -X POST https://projectHub.trapuce.tech/api/files/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/chemin/vers/fichier.pdf" \
  -F "projectId=1" \
  -F "taskId=1"
```

**Exemple avec un fichier local :**
```bash
curl -X POST https://projectHub.trapuce.tech/api/files/upload \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@document.pdf" \
  -F "projectId=1"
```

### 2. Télécharger un fichier

```bash
curl -X GET https://projectHub.trapuce.tech/api/files/download/1 \
  -H "Authorization: Bearer $TOKEN" \
  -o fichier-telecharge.pdf
```

### 3. Obtenir les fichiers d'un projet

```bash
curl -X GET https://projectHub.trapuce.tech/api/files/project/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🏥 Health Check

### Vérifier l'état de l'API

```bash
curl -X GET https://projectHub.trapuce.tech/actuator/health
```

**Réponse attendue :**
```json
{
  "status": "UP"
}
```

---

## 📊 Exemples de Scripts de Test Complets

### Script Bash complet

```bash
#!/bin/bash

BASE_URL="https://projectHub.trapuce.tech"

echo "=== Test d'inscription ==="
REGISTER_RESPONSE=$(curl -s -X POST ${BASE_URL}/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "department": "IT",
    "role": "MEMBER"
  }')

echo $REGISTER_RESPONSE | jq '.'

# Extraire le token
TOKEN=$(echo $REGISTER_RESPONSE | jq -r '.data.accessToken')
echo "Token: $TOKEN"

echo -e "\n=== Test de connexion ==="
LOGIN_RESPONSE=$(curl -s -X POST ${BASE_URL}/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }')

echo $LOGIN_RESPONSE | jq '.'
TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.accessToken')

echo -e "\n=== Test création de projet ==="
PROJECT_RESPONSE=$(curl -s -X POST ${BASE_URL}/api/projects \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Projet Test",
    "description": "Description du projet test",
    "priority": "HIGH",
    "startDate": "2024-01-01",
    "dueDate": "2024-12-31"
  }')

echo $PROJECT_RESPONSE | jq '.'
PROJECT_ID=$(echo $PROJECT_RESPONSE | jq -r '.data.id')

echo -e "\n=== Test création de tâche ==="
TASK_RESPONSE=$(curl -s -X POST ${BASE_URL}/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"title\": \"Tâche Test\",
    \"description\": \"Description de la tâche\",
    \"projectId\": $PROJECT_ID,
    \"priority\": \"MEDIUM\",
    \"dueDate\": \"2024-06-30\"
  }")

echo $TASK_RESPONSE | jq '.'

echo -e "\n=== Test Health Check ==="
curl -s ${BASE_URL}/actuator/health | jq '.'
```

---

## 🧪 Test avec httpie (alternative à curl)

Si vous avez `httpie` installé :

```bash
# Installation
pip install httpie

# Inscription
http POST https://projectHub.trapuce.tech/api/auth/register \
  email="test@example.com" \
  password="password123" \
  firstName="Test" \
  lastName="User"

# Connexion
http POST https://projectHub.trapuce.tech/api/auth/login \
  email="test@example.com" \
  password="password123"

# Créer un projet (avec token)
http POST https://projectHub.trapuce.tech/api/projects \
  Authorization:"Bearer $TOKEN" \
  name="Mon Projet" \
  description="Description" \
  priority="HIGH"
```

---

## 📝 Notes

1. **Token JWT** : Valide pendant 24 heures (86400000 ms)
2. **Refresh Token** : Valide pendant 7 jours
3. **Pagination** : Par défaut, 20 éléments par page
4. **Priorités** : `LOW`, `MEDIUM`, `HIGH`, `URGENT`
5. **Rôles** : `MEMBER`, `MANAGER`, `ADMIN`

---

## 🔍 Test avec Swagger UI

Accédez à : `https://projectHub.trapuce.tech/swagger-ui/index.html`

Vous pouvez tester toutes les API directement depuis l'interface Swagger !

---

## ⚠️ Erreurs Courantes

### 401 Unauthorized
```bash
# Vérifier que le token est valide et bien formaté
curl -X GET https://projectHub.trapuce.tech/api/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

### 403 Forbidden
- Vérifier les permissions de votre rôle
- Certaines opérations nécessitent le rôle `ADMIN` ou `MANAGER`

### 400 Bad Request
- Vérifier le format JSON
- Vérifier les champs requis
- Vérifier les validations (email, taille du mot de passe, etc.)

### 404 Not Found
- Vérifier que l'ID existe dans la base de données
- Vérifier l'URL de l'endpoint

