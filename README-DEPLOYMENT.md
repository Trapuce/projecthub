# Guide de Déploiement en Production - ProjectHub

Ce guide explique comment déployer l'application ProjectHub en production avec Docker et Docker Compose.

## 🏗️ Architecture de Production

L'architecture de production comprend :

- **Application Spring Boot** : API REST containerisée
- **PostgreSQL** : Base de données principale
- **pgAdmin** : Interface d'administration de la base de données
- **Nginx** : Reverse proxy avec SSL/TLS
- **Volumes persistants** : Pour les données, uploads et logs

## 📋 Prérequis

- Docker 20.10+
- Docker Compose 2.0+
- 2GB RAM minimum
- 10GB espace disque libre
- Ports 80, 443, 8080, 5050, 5432 disponibles

## 🚀 Déploiement Rapide

### 1. Cloner et configurer

```bash
# Cloner le projet
git clone <votre-repo>
cd projectHub

# Configurer les variables d'environnement
cp env.prod.example .env
# Modifier .env avec vos valeurs de production
```

### 2. Déployer

```bash
# Construire et démarrer tous les services
./deploy-prod.sh build
./deploy-prod.sh start

# Vérifier le statut
./deploy-prod.sh status
```

## ⚙️ Configuration Détaillée

### Variables d'Environnement (.env)

```bash
# Base de données
DB_PASSWORD=your_secure_database_password_here

# JWT
JWT_SECRET=your_super_secret_jwt_key_for_production_2024

# pgAdmin
PGADMIN_EMAIL=admin@yourdomain.com
PGADMIN_PASSWORD=your_pgadmin_password_here

# CORS
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
```

### Configuration SSL (Optionnel)

Pour activer HTTPS :

```bash
# Créer le répertoire SSL
mkdir -p docker/nginx/ssl

# Copier vos certificats
cp your-cert.pem docker/nginx/ssl/cert.pem
cp your-key.pem docker/nginx/ssl/key.pem
```

## 🛠️ Commandes de Gestion

### Script de Déploiement

```bash
# Construire l'application
./deploy-prod.sh build

# Démarrer les services
./deploy-prod.sh start

# Arrêter les services
./deploy-prod.sh stop

# Redémarrer les services
./deploy-prod.sh restart

# Voir les logs
./deploy-prod.sh logs

# Vérifier le statut
./deploy-prod.sh status
```

### Commandes Docker Compose Directes

```bash
# Démarrer en arrière-plan
docker-compose -f docker-compose.prod.yml up -d

# Voir les logs
docker-compose -f docker-compose.prod.yml logs -f

# Arrêter
docker-compose -f docker-compose.prod.yml down

# Redémarrer un service spécifique
docker-compose -f docker-compose.prod.yml restart app
```

## 📊 Monitoring et Logs

### Endpoints de Monitoring

- **Health Check** : `http://localhost:8080/actuator/health`
- **Métriques** : `http://localhost:8080/actuator/metrics`
- **Info** : `http://localhost:8080/actuator/info`

### Logs

```bash
# Logs de l'application
docker-compose -f docker-compose.prod.yml logs -f app

# Logs de la base de données
docker-compose -f docker-compose.prod.yml logs -f db

# Logs de Nginx
docker-compose -f docker-compose.prod.yml logs -f nginx

# Tous les logs
docker-compose -f docker-compose.prod.yml logs -f
```

### Accès aux Services

- **Application** : http://localhost:8080
- **pgAdmin** : http://localhost:5050
- **API Documentation** : http://localhost:8080/swagger-ui.html

## 🔧 Maintenance

### Sauvegarde de la Base de Données

```bash
# Sauvegarde
docker-compose -f docker-compose.prod.yml exec db pg_dump -U projecthub_user projecthub > backup.sql

# Restauration
docker-compose -f docker-compose.prod.yml exec -T db psql -U projecthub_user projecthub < backup.sql
```

### Mise à Jour de l'Application

```bash
# Arrêter les services
./deploy-prod.sh stop

# Mettre à jour le code
git pull

# Reconstruire et redémarrer
./deploy-prod.sh build
./deploy-prod.sh start
```

### Nettoyage

```bash
# Supprimer les images inutilisées
docker image prune -f

# Supprimer les volumes inutilisés
docker volume prune -f

# Nettoyage complet
docker system prune -f
```

## 🔒 Sécurité

### Recommandations

1. **Changez tous les mots de passe par défaut**
2. **Configurez SSL/TLS pour HTTPS**
3. **Limitez l'accès aux ports d'administration**
4. **Configurez un firewall**
5. **Surveillez les logs régulièrement**

### Configuration Firewall (Exemple)

```bash
# Autoriser uniquement les ports nécessaires
ufw allow 80/tcp
ufw allow 443/tcp
ufw deny 8080/tcp  # Bloquer l'accès direct à l'app
ufw deny 5050/tcp  # Bloquer l'accès direct à pgAdmin
ufw deny 5432/tcp  # Bloquer l'accès direct à PostgreSQL
```

## 🐛 Dépannage

### Problèmes Courants

#### Application ne démarre pas
```bash
# Vérifier les logs
./deploy-prod.sh logs

# Vérifier la base de données
docker-compose -f docker-compose.prod.yml exec db pg_isready -U projecthub_user
```

#### Erreur de connexion à la base de données
```bash
# Vérifier que PostgreSQL est démarré
docker-compose -f docker-compose.prod.yml ps db

# Vérifier les logs de la base de données
docker-compose -f docker-compose.prod.yml logs db
```

#### Problème de permissions
```bash
# Vérifier les permissions des volumes
ls -la uploads/
ls -la logs/

# Corriger les permissions si nécessaire
sudo chown -R 1000:1000 uploads/ logs/
```

### Logs d'Erreur

```bash
# Logs d'erreur de l'application
docker-compose -f docker-compose.prod.yml logs app | grep ERROR

# Logs d'erreur de Nginx
docker-compose -f docker-compose.prod.yml logs nginx | grep error
```

## 📈 Performance

### Optimisations Recommandées

1. **Augmenter la mémoire JVM** : Modifier `JAVA_OPTS` dans le Dockerfile
2. **Optimiser PostgreSQL** : Ajuster les paramètres dans `docker-compose.prod.yml`
3. **Configurer le cache** : Activer le cache dans `application-prod.properties`
4. **Utiliser un CDN** : Pour les fichiers statiques

### Monitoring des Performances

```bash
# Utilisation des ressources
docker stats

# Métriques de l'application
curl http://localhost:8080/actuator/metrics
```

## 📞 Support

En cas de problème :

1. Vérifiez les logs avec `./deploy-prod.sh logs`
2. Consultez la section dépannage
3. Vérifiez la configuration des variables d'environnement
4. Assurez-vous que tous les prérequis sont installés

---

**Note** : Ce guide est conçu pour un déploiement de base. Pour un environnement de production critique, consultez un administrateur système pour des configurations avancées de sécurité et de performance.
