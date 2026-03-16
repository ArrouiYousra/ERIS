

[![Frontend CI](https://github.com/EpitechMscProPromo2028/T-DEV-600-TLS_2/actions/workflows/frontend.yml/badge.svg)](https://github.com/EpitechMscProPromo2028/T-DEV-600-TLS_2/actions/workflows/frontend.yml)

# Eris

Eris est une application de messagerie en temps réel, type Discord, développée dans le cadre du projet T-JSF-600.

Le projet est séparé en deux parties :
- [**backend/** : API REST en Spring Boot (Java 21) + WebSocket](backend/README.md)
- [**frontend/** : SPA en React + TypeScript + Vite](frontend/README.md)

## Prérequis

- Java 21
- Node.js >= 18
- PostgreSQL 16
- npm
- Docker + Docker Compose

## Structure du projet

```
T-JSF-600-TLS_2/
├── backend/          # API Spring Boot
├── frontend/         # Client React
└── README.md
```

## Installation rapide

### 1. Base de données

Créer une base PostgreSQL :

```bash
sudo -u postgres psql
CREATE DATABASE fourth_argument;
\q
```

### 2. Backend

```bash
cd backend
cp .env.example .env
# Modifier .env avec vos identifiants PostgreSQL
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

> Si le fichier `.env` vient de Windows, penser à faire `sed -i 's/\r$//' .env` avant.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Le frontend tourne sur `http://localhost:5173` par défaut.

## Lancement avec Docker (recommandé)

Depuis la racine du projet :

```bash
cp .env.docker.example .env
docker compose up --build -d
```

Services disponibles :

- Frontend : `http://localhost:5173`
- Backend API : `http://localhost:8081`
- Swagger : `http://localhost:8081/swagger-ui/index.html`

Commandes utiles :

```bash
# Voir les logs
docker compose logs -f

# Arrêter la stack
docker compose down

# Arrêter et supprimer les données PostgreSQL
docker compose down -v
```

## Configuration

Chaque dev a ses propres credentials. Les fichiers `.env` ne sont pas commités.

- `backend/.env` : credentials PostgreSQL et port du serveur
- `frontend/.env` : URL de l'API backend (lu par Vite via `VITE_API_URL`)

Voir les `.env.example` dans chaque dossier pour les variables attendues.

## Tests

### Backend

```bash
cd backend
export $(cat .env | xargs) && ./mvnw test
```

### Frontend

```bash
cd frontend
npm test
```

Pour plus de détails (watch mode, couverture, tests spécifiques), voir les README de chaque partie.

## Branches

- `main` : production
- `dev` : branche d'intégration
- `feature/*` : branches de feature

On merge les features dans `dev`, puis `dev` dans `main` quand c'est stable.

