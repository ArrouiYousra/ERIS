# Eris

Eris est une application de messagerie en temps réel, type Discord, développée dans le cadre du projet T-JSF-600.

Le projet est séparé en deux parties :
- **backend/** : API REST en Spring Boot (Java 21) + WebSocket
- **frontend/** : SPA en React + TypeScript + Vite

## Prérequis

- Java 21
- Node.js >= 18
- PostgreSQL 16
- npm

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

## Configuration

Chaque dev a ses propres credentials. Les fichiers `.env` ne sont pas commités.

- `backend/.env` : credentials PostgreSQL et port du serveur
- `frontend/.env` : URL de l'API backend (lu par Vite via `VITE_API_URL`)

Voir les `.env.example` dans chaque dossier pour les variables attendues.

## Branches

- `main` : production
- `dev` : branche d'intégration
- `feature/*` : branches de feature

On merge les features dans `dev`, puis `dev` dans `main` quand c'est stable.
