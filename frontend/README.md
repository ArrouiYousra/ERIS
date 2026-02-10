# Frontend - Eris

Client web en React / TypeScript, build avec Vite.

## Stack

- React 19
- TypeScript
- Vite 6
- Tailwind CSS 4
- React Router 7
- TanStack Query (React Query)
- Axios
- Lucide React (icones)
- STOMP.js + SockJS (WebSocket)

## Prérequis

- Node.js >= 18
- npm

## Installation

```bash
npm install
```

## Lancer le projet

```bash
npm run dev
```

Le serveur de dev tourne sur `http://localhost:5173`.

## Configuration

Le frontend a besoin de connaître l'URL du backend. Par défaut c'est `http://localhost:8081`.

Créer un fichier `.env` (non commité) :

```
VITE_API_URL=http://localhost:8081
VITE_WS_URL=http://localhost:8081/ws
```

ou

```bash
cp .env.example .env
```

## Structure

```
src/
├── api/              # Clients HTTP (axios) et WebSocket
│   ├── client.ts     # Instance axios configurée
│   ├── authApi.ts    # Endpoints auth (login, signup)
│   ├── serversApi.ts # Endpoints serveurs
│   ├── channelsApi.ts# Endpoints channels
│   └── wsApi.tsx     # WebSocket (STOMP)
├── components/       # Composants réutilisables
│   ├── ChannelList.tsx
│   ├── ChannelSettings.tsx
│   ├── ChannelWizard.tsx
│   ├── Header.tsx
│   ├── MessageList.tsx
│   ├── ServerList.tsx
│   └── ServerWizard.tsx
├── hooks/            # Custom hooks (useAuth, useServers, useChannels)
├── pages/            # Pages principales
│   ├── HomePage.tsx
│   ├── LoginPage.tsx
│   ├── SignupPage.tsx
│   └── ChatLayout.tsx
├── routes/           # Configuration du routeur
├── styles/           # Fichiers CSS par composant
└── main.tsx          # Point d'entrée
```

## Scripts

| Commande | Description |
|----------|-------------|
| `npm run dev` | Serveur de dev (HMR) |
| `npm run build` | Build de production |
| `npm run lint` | Linter ESLint |
| `npm run preview` | Preview du build |

## Auth

Le token JWT est stocké dans `localStorage` sous la clé `access_token`. Il est automatiquement ajouté aux requêtes via l'intercepteur axios dans `client.ts`.
