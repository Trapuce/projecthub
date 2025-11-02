# Instructions d'Intégration Frontend - ProjectHub API

## 🔗 Informations de Connexion

### URL de Base de l'API
```
https://projectHub.trapuce.tech
```

### Documentation API (Swagger)
```
https://projectHub.trapuce.tech/swagger-ui/index.html
```

### Health Check
```
https://projectHub.trapuce.tech/actuator/health
```

---

## 🔐 Authentification JWT

### Points Importants

1. **Type d'authentification** : Bearer Token (JWT)
2. **Format du header** : `Authorization: Bearer <token>`
3. **Durée de validité Access Token** : 24 heures (86400 secondes)
4. **Durée de validité Refresh Token** : 7 jours
5. **Type de token** : `Bearer`

### Endpoints d'Authentification

#### Inscription (POST)
- **URL** : `/api/auth/register`
- **Méthode** : POST
- **Content-Type** : `application/json`
- **Authentification requise** : Non
- **Body requis** :
  - `email` (string, requis)
  - `password` (string, minimum 6 caractères, requis)
  - `firstName` (string, requis)
  - `lastName` (string, requis)
  - `phone` (string, optionnel)
  - `department` (string, optionnel)
  - `role` (enum: MEMBER, MANAGER, ADMIN, optionnel, défaut: MEMBER)

**Réponse** :
- Status : 200 OK
- Body contient : `accessToken`, `refreshToken`, `tokenType: "Bearer"`, `expiresIn`, `user`

#### Connexion (POST)
- **URL** : `/api/auth/login`
- **Méthode** : POST
- **Content-Type** : `application/json`
- **Authentification requise** : Non
- **Body requis** :
  - `email` (string, requis)
  - `password` (string, requis)

**Réponse** :
- Status : 200 OK
- Body contient : `accessToken`, `refreshToken`, `tokenType: "Bearer"`, `expiresIn`, `user`

#### Rafraîchir le Token (POST)
- **URL** : `/api/auth/refresh`
- **Méthode** : POST
- **Content-Type** : `application/json`
- **Authentification requise** : Non
- **Body requis** :
  - `refreshToken` (string, requis)

**Réponse** :
- Status : 200 OK
- Body contient : Nouveau `accessToken` et `refreshToken`

#### Déconnexion (POST)
- **URL** : `/api/auth/logout`
- **Méthode** : POST
- **Authentification requise** : Oui (Bearer Token)
- **Action** : Le client doit simplement supprimer les tokens localement

---

## 📋 Format des Réponses API

### Structure Standard de Réponse

Toutes les réponses suivent ce format :

```json
{
  "success": true/false,
  "message": "Message descriptif (optionnel)",
  "data": { ... } // Les données réelles
}
```

### Réponses d'Erreur

```json
{
  "success": false,
  "message": "Message d'erreur",
  "errors": [ ... ] // Détails des erreurs de validation (optionnel)
}
```

### Codes de Statut HTTP

- **200 OK** : Succès
- **201 Created** : Ressource créée (non utilisé, tout est en 200)
- **400 Bad Request** : Erreur de validation ou requête incorrecte
- **401 Unauthorized** : Token manquant ou invalide
- **403 Forbidden** : Permissions insuffisantes
- **404 Not Found** : Ressource non trouvée
- **500 Internal Server Error** : Erreur serveur

---

## 🔒 Gestion des Tokens

### Stockage Recommandé

1. **Access Token** : Stocker en mémoire ou dans une variable d'état sécurisée
2. **Refresh Token** : Stocker de manière sécurisée (localStorage/sessionStorage selon vos besoins de sécurité)

### Intercepteur HTTP

Créer un intercepteur pour :
1. Ajouter automatiquement le header `Authorization: Bearer <token>` à toutes les requêtes
2. Gérer la rafraîchissement automatique du token si l'access token expire
3. Rediriger vers la page de login si le refresh token est invalide

### Logique de Refresh

Si une requête retourne **401 Unauthorized** :
1. Utiliser le `refreshToken` pour obtenir un nouveau `accessToken`
2. Réessayer la requête originale avec le nouveau token
3. Si le refresh échoue, rediriger vers la page de login et supprimer les tokens

---

## 📡 Endpoints Principaux

### Base URL
Tous les endpoints commencent par : `https://projectHub.trapuce.tech`

### Préfixes des Routes

- **Authentification** : `/api/auth/*`
- **Utilisateurs** : `/api/users/*`
- **Projets** : `/api/projects/*`
- **Tâches** : `/api/tasks/*`
- **Fichiers** : `/api/files/*`

---

## 👥 Endpoints Utilisateurs

### Obtenir le Profil Actuel
- **URL** : `/api/users/profile`
- **Méthode** : GET
- **Authentification** : Oui

### Obtenir Tous les Utilisateurs
- **URL** : `/api/users`
- **Méthode** : GET
- **Authentification** : Oui
- **Query Parameters** :
  - `page` (number, défaut: 0)
  - `size` (number, défaut: 20)
  - `sort` (string, optionnel)

### Obtenir un Utilisateur par ID
- **URL** : `/api/users/{id}`
- **Méthode** : GET
- **Authentification** : Oui

---

## 📁 Endpoints Projets

### Créer un Projet
- **URL** : `/api/projects`
- **Méthode** : POST
- **Authentification** : Oui
- **Body requis** :
  - `name` (string, requis)
  - `description` (string, requis)
  - `priority` (enum: LOW, MEDIUM, HIGH, URGENT, requis)
  - `startDate` (date ISO 8601, format: YYYY-MM-DD, optionnel)
  - `dueDate` (date ISO 8601, format: YYYY-MM-DD, optionnel)
  - `memberIds` (array de numbers, optionnel)

### Obtenir Mes Projets
- **URL** : `/api/projects/my-projects`
- **Méthode** : GET
- **Authentification** : Oui
- **Query Parameters** : `page`, `size`, `sort`

### Obtenir Tous les Projets
- **URL** : `/api/projects`
- **Méthode** : GET
- **Authentification** : Oui
- **Query Parameters** : `page`, `size`, `sort`

### Obtenir un Projet par ID
- **URL** : `/api/projects/{id}`
- **Méthode** : GET
- **Authentification** : Oui

### Rechercher des Projets
- **URL** : `/api/projects/search`
- **Méthode** : GET
- **Authentification** : Oui
- **Query Parameters** :
  - `search` (string, requis) - terme de recherche
  - `page`, `size`, `sort`

### Filtrer par Statut
- **URL** : `/api/projects/status/{status}`
- **Méthode** : GET
- **Authentification** : Oui
- **Statuts possibles** : `TODO`, `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`, `ARCHIVED`

### Mettre à Jour un Projet
- **URL** : `/api/projects/{id}`
- **Méthode** : PUT
- **Authentification** : Oui
- **Body** : Mêmes champs que la création (tous optionnels sauf ceux que vous voulez modifier)

### Changer le Statut d'un Projet
- **URL** : `/api/projects/{id}/status`
- **Méthode** : PUT
- **Authentification** : Oui
- **Query Parameter** : `status` (enum: TODO, IN_PROGRESS, ON_HOLD, COMPLETED, ARCHIVED)

### Supprimer un Projet
- **URL** : `/api/projects/{id}`
- **Méthode** : DELETE
- **Authentification** : Oui

---

## ✅ Endpoints Tâches

### Créer une Tâche
- **URL** : `/api/tasks`
- **Méthode** : POST
- **Authentification** : Oui
- **Body requis** :
  - `title` (string, requis)
  - `description` (string, requis)
  - `projectId` (number, requis)
  - `priority` (enum: LOW, MEDIUM, HIGH, URGENT, requis)
  - `dueDate` (date ISO 8601, format: YYYY-MM-DD, optionnel)
  - `estimatedHours` (number, optionnel)
  - `assigneeId` (number, optionnel)
  - `parentTaskId` (number, optionnel) - pour les sous-tâches

### Obtenir Mes Tâches
- **URL** : `/api/tasks/my-tasks`
- **Méthode** : GET
- **Authentification** : Oui
- **Query Parameters** : `page`, `size`, `sort`

### Obtenir les Tâches d'un Projet
- **URL** : `/api/tasks/project/{projectId}`
- **Méthode** : GET
- **Authentification** : Oui
- **Query Parameters** : `page`, `size`, `sort`

### Obtenir une Tâche par ID
- **URL** : `/api/tasks/{id}`
- **Méthode** : GET
- **Authentification** : Oui

### Mettre à Jour une Tâche
- **URL** : `/api/tasks/{id}`
- **Méthode** : PUT
- **Authentification** : Oui
- **Body** : Mêmes champs que la création (optionnels)

### Changer le Statut d'une Tâche
- **URL** : `/api/tasks/{id}/status`
- **Méthode** : PUT
- **Authentification** : Oui
- **Query Parameter** : `status` (enum: TODO, IN_PROGRESS, IN_REVIEW, DONE, CANCELLED)

### Supprimer une Tâche
- **URL** : `/api/tasks/{id}`
- **Méthode** : DELETE
- **Authentification** : Oui

---

## 📎 Endpoints Fichiers

### Uploader un Fichier
- **URL** : `/api/files/upload`
- **Méthode** : POST
- **Content-Type** : `multipart/form-data`
- **Authentification** : Oui
- **Form Data** :
  - `file` (file, requis)
  - `projectId` (number, optionnel)
  - `taskId` (number, optionnel)

### Télécharger un Fichier
- **URL** : `/api/files/download/{fileId}`
- **Méthode** : GET
- **Authentification** : Oui
- **Réponse** : Fichier binaire (ajuster le Content-Type selon le type de fichier)

### Obtenir les Fichiers d'un Projet
- **URL** : `/api/files/project/{projectId}`
- **Méthode** : GET
- **Authentification** : Oui

---

## 📄 Pagination

### Format de Réponse Paginée

```json
{
  "success": true,
  "data": {
    "content": [ ... ], // Tableau des éléments
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 100,
    "totalPages": 5,
    "last": false,
    "first": true,
    "numberOfElements": 20
  }
}
```

### Paramètres de Pagination

- **page** : Numéro de page (commence à 0)
- **size** : Nombre d'éléments par page (défaut: 20, maximum: 100)
- **sort** : Tri (exemple: `sort=name,asc` ou `sort=createdAt,desc`)

---

## 🎯 Enums et Valeurs Possibles

### Rôles
- `MEMBER`
- `MANAGER`
- `ADMIN`

### Priorités
- `LOW`
- `MEDIUM`
- `HIGH`
- `URGENT`

### Statuts Projet
- `TODO`
- `IN_PROGRESS`
- `ON_HOLD`
- `COMPLETED`
- `ARCHIVED`

### Statuts Tâche
- `TODO`
- `IN_PROGRESS`
- `IN_REVIEW`
- `DONE`
- `CANCELLED`

---

## 🌐 CORS Configuration

### Origines Autorisées
- `https://projectHub.trapuce.tech`
- `https://www.projectHub.trapuce.tech`
- Votre domaine frontend (à confirmer avec l'équipe backend si différent)

### Headers CORS Autorisés
- Tous les headers sont autorisés (`*`)

### Méthodes Autorisées
- `GET`
- `POST`
- `PUT`
- `DELETE`
- `OPTIONS`

### Credentials
- `allow-credentials: true` - Vous pouvez inclure les cookies/credentials dans les requêtes

---

## 🔍 Tests et Validation

### Endpoints de Test Disponibles

1. **Health Check** : `/actuator/health` - Vérifier que l'API est accessible
2. **Swagger UI** : `/swagger-ui/index.html` - Interface interactive pour tester tous les endpoints

### Checklist d'Intégration

- [ ] Configuration de la base URL de l'API
- [ ] Implémentation de l'authentification (login/register)
- [ ] Stockage sécurisé des tokens JWT
- [ ] Intercepteur HTTP pour ajouter automatiquement le token Bearer
- [ ] Gestion du refresh token automatique
- [ ] Gestion des erreurs 401/403/404
- [ ] Gestion de la pagination
- [ ] Gestion des uploads de fichiers (multipart/form-data)
- [ ] Gestion des dates (format ISO 8601: YYYY-MM-DD)
- [ ] Gestion des enums (rôles, priorités, statuts)

---

## ⚠️ Points d'Attention

### Dates
- Format attendu : `YYYY-MM-DD` (ISO 8601 date only)
- Exemple : `2024-11-02`

### Tokens
- Ne jamais exposer les tokens dans les logs côté client
- Supprimer les tokens lors de la déconnexion
- Gérer le cas où le token expire pendant une requête

### Fichiers
- Taille maximale : 10MB (peut être ajustée)
- Types autorisés : images (JPEG, PNG, GIF), PDF, documents Office (Word), texte

### Erreurs de Validation
- Les erreurs 400 contiennent un tableau `errors` avec les détails
- Toujours afficher les messages d'erreur à l'utilisateur

### Performance
- Utiliser la pagination pour les listes
- Implémenter le cache côté client si nécessaire
- Considérer le lazy loading pour les grandes listes

---

## 📞 Support

En cas de problème :
1. Vérifier le Health Check : `https://projectHub.trapuce.tech/actuator/health`
2. Consulter Swagger UI : `https://projectHub.trapuce.tech/swagger-ui/index.html`
3. Vérifier les logs de l'application backend
4. Vérifier la console du navigateur pour les erreurs CORS ou réseau

---

## 🔄 Mises à Jour

Dernière mise à jour : 2 novembre 2024
- Base URL : `https://projectHub.trapuce.tech`
- Version API : 1.0.0
- Swagger disponible et fonctionnel

