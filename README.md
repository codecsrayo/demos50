# 📱 Demos50 — Aplicación Android con SQLite y PokeAPI

> Proyecto académico desarrollado en **Java para Android** que implementa autenticación, gestión de usuarios con SQLite y consumo de API REST.

---

## 🎯 Objetivo

Configurar un ambiente de desarrollo para dispositivos móviles mediante la codificación en Java para Android, implementando navegación entre actividades, persistencia de datos con SQLite y consumo de servicios web externos.

---

## 🗺️ Flujo de navegación

```mermaid
flowchart TD
    A([🚀 Inicio]) --> B[SplashActivity\n⏱️ 5 segundos]
    B --> C[LoginActivity\n🔐 Autenticación SQLite]
    C -->|Credenciales válidas| D[MenuActivity\n🏠 Menú principal]
    C -->|Credenciales inválidas| C
    D --> E[UserMenuActivity\n👤 Gestión de Usuarios]
    D --> F[PokemonActivity\n⚡ Pokédex]
    F -->|Click en pokémon| G[PokemonDetailActivity\n📊 Detalle completo]
    G -->|← Volver| F
    E -->|Cerrar sesión| C
```

---

## 🏗️ Arquitectura de actividades

```mermaid
classDiagram
    class SplashActivity {
        +SPLASH_DURATION: 5000ms
        +onCreate()
    }
    class LoginActivity {
        -DatabaseHelper dbHelper
        +checkLogin(user, pass) bool
    }
    class MenuActivity {
        +cardUsers
        +cardPokemon
    }
    class UserMenuActivity {
        -DatabaseHelper dbHelper
        +loadUsers()
        +showAddDialog()
        +showEditDialog(id)
        +confirmDelete(id)
    }
    class DatabaseHelper {
        +DATABASE: demos50.db
        +TABLE: users
        +insertUser() long
        +checkLogin() bool
        +getAllUsers() Cursor
        +updateUser() int
        +deleteUser() int
    }
    class PokemonActivity {
        -List~Pokemon~ pokemonList
        +fetchPokemon()
    }
    class PokemonDetailActivity {
        +fetchDetail(id)
    }

    SplashActivity --> LoginActivity
    LoginActivity --> MenuActivity
    LoginActivity ..> DatabaseHelper
    MenuActivity --> UserMenuActivity
    MenuActivity --> PokemonActivity
    UserMenuActivity ..> DatabaseHelper
    PokemonActivity --> PokemonDetailActivity
```

---

## 🗄️ Esquema de base de datos SQLite

```mermaid
erDiagram
    USERS {
        INTEGER id PK
        TEXT    username UK
        TEXT    password
        TEXT    email
    }
```

| Operación | Método | Descripción |
|-----------|--------|-------------|
| Create | `insertUser(username, password, email)` | Registro de nuevo usuario |
| Read | `getAllUsers()` / `checkLogin()` | Listado y autenticación |
| Update | `updateUser(id, username, password, email)` | Edición de datos |
| Delete | `deleteUser(id)` | Eliminación con confirmación |

---

## 🌐 Integración PokeAPI

```mermaid
sequenceDiagram
    participant U as Usuario
    participant A as PokemonActivity
    participant P as PokeAPI
    participant D as PokemonDetailActivity

    U->>A: Abre Pokédex
    A->>P: GET /api/v2/pokemon?limit=40
    P-->>A: JSON lista
    A-->>U: ListView con 40 pokémon

    U->>A: Click en pokémon
    A->>D: Intent(id, nombre)
    D->>P: GET /api/v2/pokemon/{id}
    P-->>D: JSON detalle
    D-->>U: Sprite + stats + tipos + habilidades
```

| Endpoint | Uso |
|----------|-----|
| `GET /api/v2/pokemon?limit=40` | Lista paginada |
| `GET /api/v2/pokemon/{id}` | Detalle por ID |
| `sprites/pokemon/{id}.png` | Sprite estático |
| `sprites/.../animated/front_default` | Sprite animado gen-V |

---

## 📁 Estructura del proyecto

```
app/src/main/java/com/example/demos50/
├── SplashActivity.java
├── LoginActivity.java
├── MenuActivity.java
├── UserMenuActivity.java
├── DatabaseHelper.java
├── PokemonActivity.java
├── PokemonDetailActivity.java
├── PokemonAdapter.java
└── Pokemon.java
```

---

## 🛠️ Stack tecnológico

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 11 | Lenguaje principal |
| Android SDK | 36 | Target platform |
| SQLite | built-in | Persistencia local |
| Glide | 4.16.0 | Carga de imágenes |
| PokeAPI | v2 | Datos de pokémon |
| Material 3 | 1.10.0 | Componentes UI |

---

## ▶️ Cómo ejecutar

1. Clonar el repositorio
2. Abrir en **Android Studio**
3. Sincronizar Gradle
4. Ejecutar en emulador o dispositivo (minSdk 24)
5. Credenciales por defecto: `admin` / `1234`

---

## 📸 Capturas

| Splash | Login | Menú |
|--------|-------|------|
| ![Loading](img/load.png) | ![Login](img/login.png) | ![nav](img/nav.png) |

| CRUD Usuarios | Pokédex | Card |
|---------------|---------|------|
| ![CRUD](img/crud.png) | ![scroll](img/scroll.png) | ![card](img/card.png) |

---

## 📋 Rúbrica

| Criterios | Excelente | Bueno | Deficiente | Pts |
| :--- | :--- | :--- | :--- | :--- |
| **Producto software** | CRUD completo, navegación y persistencia | 60% o CRUD incompleto | 20% o menos | 40 |
| **Construcción de BD** | SQLite con librerías y objetos correctos | BD incompleta | Entrega parcial | 30 |
| **Puntualidad** | Entrega puntual y correcta | Puntual con errores | Fuera de tiempo | 30 |
| **Total** | | | | **100** |

> 📖 [Guía APA Sexta Edición](https://www.um.es/documents/378246/2964900/Normas+APA+Sexta+Edici%C3%B3n.pdf/27f8511d-95b6-4096-8d3e-f8492f61c6dc)
