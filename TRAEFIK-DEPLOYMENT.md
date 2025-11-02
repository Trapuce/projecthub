# Guide de Déploiement avec Traefik

Ce guide explique comment déployer ProjectHub sur un VPS avec Traefik comme reverse proxy.

## Prérequis

1. Un VPS avec Docker et Docker Compose installés
2. Traefik déjà configuré et fonctionnel sur le réseau `web`
3. Le domaine `projectHub.trapuce.tech` pointant vers votre VPS
4. Les ports 80 et 443 ouverts et gérés par Traefik

## Configuration Traefik

Assurez-vous que votre Traefik utilise le réseau Docker nommé `web` (réseau externe). Voici un exemple de configuration Traefik :

```yaml
services:
  traefik:
    image: traefik:latest
    container_name: traefik
    command:
      - "--providers.docker=true"
      - "--providers.docker.exposedbydefault=false"
      - "--entrypoints.web.address=:80"
      - "--entrypoints.websecure.address=:443"
      - "--certificatesresolvers.myresolver.acme.httpchallenge=true"
      - "--certificatesresolvers.myresolver.acme.httpchallenge.entrypoint=web"
      - "--certificatesresolvers.myresolver.acme.email=trapucework33@gmail.com"
      - "--certificatesresolvers.myresolver.acme.storage=/letsencrypt/acme.json"
      - "--api.dashboard=true"
      - "--api.insecure=true"
    ports:
      - "80:80"
      - "443:443"
      - "8080:8080"
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock:ro"
      - "./letsencrypt:/letsencrypt"
    networks:
      - web

networks:
  web:
    external: true
```

## Étapes de Déploiement

### 1. Préparer l'environnement

Sur votre VPS, créez un répertoire pour votre application :

```bash
mkdir -p /opt/projecthub
cd /opt/projecthub
```

### 2. Cloner ou transférer le code

```bash
# Si vous utilisez Git
git clone <votre-repo> .

# Ou transférez les fichiers via SCP/SFTP
```

### 3. Vérifier la configuration

Assurez-vous que le fichier `docker-compose.prod.yml` contient bien :
- Le réseau `web` comme réseau externe
- Les labels Traefik corrects
- Le domaine `projectHub.trapuce.tech`

### 4. Configurer les variables d'environnement

Si nécessaire, créez un fichier `.env` pour les variables sensibles (non versionné) :

```bash
# .env (optionnel si vous modifiez directement docker-compose.prod.yml)
JWT_SECRET=votre-secret-jwt-tres-long-et-securise
SPRING_DATASOURCE_PASSWORD=votre-mot-de-passe-securise
```

### 5. Construire et démarrer les services

```bash
# Construire l'image Docker
docker-compose -f docker-compose.prod.yml build

# Démarrer les services
docker-compose -f docker-compose.prod.yml up -d

# Vérifier les logs
docker-compose -f docker-compose.prod.yml logs -f app
```

### 6. Vérifier le déploiement

Une fois les services démarrés, vérifiez :

1. **Les conteneurs sont en cours d'exécution** :
   ```bash
   docker-compose -f docker-compose.prod.yml ps
   ```

2. **L'application répond** :
   ```bash
   curl https://projectHub.trapuce.tech/actuator/health
   ```

3. **Swagger est accessible** :
   - Ouvrez votre navigateur : `https://projectHub.trapuce.tech/swagger-ui.html`
   - Ou : `https://projectHub.trapuce.tech/swagger-ui/index.html`

4. **L'API est accessible** :
   - Testez : `https://projectHub.trapuce.tech/api/auth/register` (POST)

## URLs Disponibles

Une fois déployé, les URLs suivantes seront accessibles :

- **API Base** : `https://projectHub.trapuce.tech/api`
- **Swagger UI** : `https://projectHub.trapuce.tech/swagger-ui.html`
- **API Docs JSON** : `https://projectHub.trapuce.tech/v3/api-docs`
- **Health Check** : `https://projectHub.trapuce.tech/actuator/health`
- **Actuator Metrics** : `https://projectHub.trapuce.tech/actuator/metrics`

## Configuration DNS

Assurez-vous que votre DNS est correctement configuré :

```
Type    Name                 Value
A       projectHub.trapuce   <IP_DE_VOTRE_VPS>
```

## Certificats SSL

Traefik gérera automatiquement les certificats SSL via Let's Encrypt. Le premier démarrage peut prendre quelques minutes pour obtenir le certificat.

Vérifiez les certificats dans Traefik :
- Dashboard Traefik : `http://<VPS_IP>:8080`
- Ou vérifiez les logs : `docker logs traefik`

## Maintenance

### Redémarrer l'application

```bash
docker-compose -f docker-compose.prod.yml restart app
```

### Mettre à jour l'application

```bash
# Arrêter les services
docker-compose -f docker-compose.prod.yml down

# Reconstruire l'image
docker-compose -f docker-compose.prod.yml build

# Redémarrer
docker-compose -f docker-compose.prod.yml up -d
```

### Voir les logs

```bash
# Logs de l'application
docker-compose -f docker-compose.prod.yml logs -f app

# Logs de la base de données
docker-compose -f docker-compose.prod.yml logs -f db

# Tous les logs
docker-compose -f docker-compose.prod.yml logs -f
```

### Sauvegarder la base de données

```bash
# Créer une sauvegarde
docker exec projecthub_db_prod pg_dump -U projecthub_user projecthub > backup_$(date +%Y%m%d_%H%M%S).sql

# Restaurer une sauvegarde
cat backup_YYYYMMDD_HHMMSS.sql | docker exec -i projecthub_db_prod psql -U projecthub_user projecthub
```

## Dépannage

### L'application ne démarre pas

1. Vérifiez les logs :
   ```bash
   docker-compose -f docker-compose.prod.yml logs app
   ```

2. Vérifiez que la base de données est accessible :
   ```bash
   docker exec projecthub_db_prod pg_isready -U projecthub_user
   ```

3. Vérifiez que le réseau `web` existe :
   ```bash
   docker network ls | grep web
   ```

### Traefik ne route pas le trafic

1. Vérifiez les logs Traefik :
   ```bash
   docker logs traefik
   ```

2. Vérifiez que les labels Traefik sont corrects :
   ```bash
   docker inspect projecthub_app_prod | grep -A 20 Labels
   ```

3. Vérifiez le dashboard Traefik : `http://<VPS_IP>:8080`

### Certificat SSL non généré

1. Vérifiez que le domaine pointe bien vers votre VPS
2. Vérifiez les logs Traefik pour les erreurs Let's Encrypt
3. Vérifiez que le port 80 est accessible (nécessaire pour le challenge HTTP)

### Swagger ne fonctionne pas

1. Vérifiez que l'application démarre correctement
2. Vérifiez les logs de l'application
3. Essayez d'accéder directement aux endpoints :
   - `https://projectHub.trapuce.tech/v3/api-docs`
   - `https://projectHub.trapuce.tech/swagger-ui.html`

## Sécurité

### Recommandations

1. **Mots de passe** : Changez les mots de passe par défaut dans `docker-compose.prod.yml`
2. **JWT Secret** : Utilisez un secret JWT fort et unique
3. **CORS** : Configurez correctement les origines CORS dans `application-prod.properties`
4. **Firewall** : Ne laissez que les ports 80, 443 ouverts (Traefik les gère)
5. **Backups** : Configurez des sauvegardes régulières de la base de données

### Variables d'environnement sensibles

Pour plus de sécurité, vous pouvez utiliser un fichier `.env` séparé (non versionné) :

```bash
# Créer .env
cat > .env << EOF
JWT_SECRET=$(openssl rand -base64 64)
POSTGRES_PASSWORD=$(openssl rand -base64 32)
EOF

# Mettre à jour docker-compose.prod.yml pour utiliser ces variables
```

## Support

Pour toute question ou problème, consultez :
- Les logs des conteneurs
- Le dashboard Traefik
- La documentation Traefik : https://doc.traefik.io/traefik/

