# Backend - Eris API

API REST + WebSocket en Spring Boot 4 / Java 21.

## Stack

- Spring Boot 4.0.2
- Spring Security + JWT
- Spring Data JPA (PostgreSQL)
- Flyway (migrations SQL)
- Lombok
- WebSocket (STOMP)

## Prérequis

- Java 21
- PostgreSQL 16
- Maven (wrapper inclus via `./mvnw`)

## Documentation API (Swagger)

Consultez la documentation interactive de l'API via Swagger ici :  
[http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)

## Installation

### 1. Configurer la base de données

```bash
sudo -u postgres psql
CREATE DATABASE fourth_argument;
-- Créer un rôle si besoin :
CREATE ROLE monuser WITH LOGIN PASSWORD 'monpassword';
GRANT ALL PRIVILEGES ON DATABASE fourth_argument TO monuser;
\q
```

### 2. Configurer les variables d'environnement

```bash
cp .env.example .env
```

Remplir `.env` avec vos valeurs :

```
DB_URL=jdbc:postgresql://localhost:5432/fourth_argument
DB_USER=monuser
DB_PASSWORD=monpassword
PORT=8081
```

### 3. Lancer le serveur

```bash
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

Le serveur démarre sur le port défini dans `.env` (8081 par défaut).

> Si erreur "role does not exist" : vérifier que le `.env` n'a pas de caractères Windows (`sed -i 's/\r$//' .env`).

> Si erreur Flyway "checksum mismatch" : la DB a été créée avec d'anciennes migrations. Il faut la reset :
> ```bash
> psql -U monuser -h 127.0.0.1 -d fourth_argument -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
> ```

## Structure

```
src/main/java/Fourth_Argument/eris/
├── api/
│   ├── config/          # Security, CORS, JWT filter
│   ├── controllers/     # REST endpoints
│   ├── dto/             # Data Transfer Objects
│   ├── mapper/          # Entity <-> DTO
│   ├── model/           # Entités JPA
│   └── repository/      # Spring Data repositories
├── services/            # Logique métier
└── ErisApplicationMain.java
```

## Migrations Flyway

Les fichiers SQL sont dans `src/main/resources/db/migration/`.

Convention de nommage : `V{numero}__{description}.sql`

Les migrations sont exécutées automatiquement au démarrage. Ne pas modifier une migration déjà appliquée, toujours en créer une nouvelle.

## Endpoints principaux

| Méthode | URL | Auth | Description |
|---------|-----|------|-------------|
| POST | `/api/auth/signup` | Non | Inscription |
| POST | `/api/auth/login` | Non | Connexion (retourne un JWT) |
| GET | `/api/servers` | Oui | Liste des serveurs |
| POST | `/api/servers` | Oui | Créer un serveur |
| GET | `/api/channels/server/{id}` | Oui | Channels d'un serveur |
| POST | `/api/channels` | Oui | Créer un channel |
| PUT | `/api/channels/{id}` | Oui | Modifier un channel |
| DELETE | `/api/channels/{id}` | Oui | Supprimer un channel |

L'auth se fait via le header `Authorization: Bearer <token>`.

## Tests

Les tests unitaires se trouvent dans `src/test/java/Fourth_Argument/eris/`.

### Lancer tous les tests

```bash
./mvnw test
```

### Lancer les tests d'une classe spécifique

```bash
./mvnw test -Dtest=ServerControllerTest
```

### Lancer les tests avec le rapport détaillé

```bash
./mvnw test -Dsurefire.useFile=false
```

> Les tests nécessitent les mêmes variables d'environnement que le lancement normal (`DB_URL`, `DB_USER`, `DB_PASSWORD`). Penser à les exporter avant ou à les charger via le `.env` :
> ```bash
> export $(cat .env | xargs) && ./mvnw test
> ```
