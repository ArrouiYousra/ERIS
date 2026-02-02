# Eris Frontend

Frontend React pour l'application Eris, un chat en temps réel inspiré de Discord.

> **Note** : Eris est la déesse grecque de la discorde dans la mythologie. Le nom de l'application fait référence à Discord tout en conservant une identité unique.

## 🚀 Stack Technique

- **React 19** - Bibliothèque UI
- **TypeScript** - Typage statique
- **Vite** - Build tool et dev server
- **React Router DOM** - Routing
- **Axios** - Client HTTP
- **React Query** - Gestion des données (prévu)

## 📦 Installation

```bash
npm install
```

## 🏃 Développement

```bash
npm run dev
```

L'application sera accessible sur `http://localhost:5173`

## 🏗️ Structure du Projet

```
src/
├── api/              # Appels API
│   ├── client.ts     # Client Axios configuré
│   └── authApi.ts    # API d'authentification
├── components/       # Composants réutilisables
│   └── Header.tsx   # Header de navigation
├── hooks/           # Hooks personnalisés
│   └── useAuth.tsx  # Hook d'authentification global
├── pages/           # Pages de l'application
│   ├── HomePage.tsx      # Page d'accueil
│   ├── LoginPage.tsx     # Page de connexion
│   └── SignupPage.tsx    # Page d'inscription
├── routes/          # Configuration du routing
│   └── AppRouter.tsx
├── styles/          # Fichiers CSS par composant
│   ├── home.css
│   ├── login.css
│   ├── signup.css
│   └── header.css
└── types/           # Types TypeScript
```

## ✨ Fonctionnalités Implémentées

### Pages

- **Landing Page** (`/`)
  - Design style Discord avec animations
  - Header avec logo et bouton de connexion
  - Animations de blobs en arrière-plan

- **Page de Connexion** (`/login`)
  - Formulaire email/password
  - Gestion d'erreurs
  - Lien vers l'inscription

- **Page d'Inscription** (`/signup`)
  - Formulaire complet avec :
    - Email (obligatoire)
    - Display Name (optionnel)
    - Username (obligatoire)
    - Password (obligatoire)
    - Date de naissance (Month/Day/Year)
    - Acceptation des conditions d'utilisation
  - Validation côté client
  - Indicateurs de champs obligatoires (astérisques rouges)

### Authentification

- Client Axios configuré avec base URL `http://localhost:8080`
- Intercepteur pour ajouter le token JWT automatiquement
- Hook `useAuth` avec Context API pour l'état global
- Stockage du token dans `localStorage`
- **Note** : L'authentification est préparée mais temporairement désactivée (backend en cours de développement)

### Design

- **Thème** : Style Discord avec palette de couleurs
  - Noir (`#000`)
  - Rose (`#ec4899`)
  - Violet (`#a855f7`)
- **Layout** : Full-screen sans encadrements
- **Typographie** : System fonts pour une meilleure performance
- **Responsive** : Design adaptatif

## 🔧 Configuration

### API Backend

Le frontend est configuré pour communiquer avec le backend Spring Boot sur `http://localhost:8080`.

Endpoints attendus :
- `POST /auth/login` - Connexion
- `POST /auth/signup` - Inscription
- `GET /me` - Informations utilisateur

### Variables d'Environnement

Pour l'instant, l'URL du backend est codée en dur dans `src/api/client.ts`. Pour la production, il faudra utiliser des variables d'environnement.

## 📝 Scripts Disponibles

- `npm run dev` - Lance le serveur de développement
- `npm run build` - Build de production
- `npm run preview` - Prévisualise le build de production
- `npm run lint` - Vérifie le code avec ESLint

## 🚧 Prochaines Étapes

- [ ] Réactiver l'authentification quand le backend sera prêt
- [ ] Implémenter les pages de chat (servers, channels, messages)
- [ ] Intégrer WebSocket pour les fonctionnalités temps réel
- [ ] Gérer les rôles et permissions dans l'UI
- [ ] Ajouter la gestion d'erreurs réseau
- [ ] Implémenter React Query pour la gestion des données
- [ ] Ajouter des tests unitaires et d'intégration

## 🎨 Design System

### Couleurs Principales

- **Noir** : `#000` - Fond principal
- **Rose** : `#ec4899` - Accents et gradients
- **Violet** : `#a855f7` - Accents et gradients
- **Gris foncé** : `#1a1a1a` - Cartes et conteneurs
- **Gris clair** : `#b5bac1` - Textes secondaires

### Composants de Formulaire

- Labels alignés à gauche, en majuscules
- Champs sans placeholders (labels uniquement)
- Astérisques rouges pour les champs obligatoires
- Boutons avec gradient rose/violet
- Focus states avec bordure violette

## 🤝 Contribution

Ce projet fait partie d'un travail d'équipe. Le frontend est développé en parallèle avec le backend Spring Boot et la base de données PostgreSQL.

## 📄 Licence

Ce projet est développé dans le cadre d'un projet académique EPITECH.