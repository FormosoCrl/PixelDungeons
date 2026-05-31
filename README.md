# 🐉 PixelDungeons — Companion App para Rol de Mesa

PixelDungeons es una aplicación Android para acompañar partidas de rol de mesa. Digitaliza la gestión de personajes, inventarios y mapas para que ni el GM ni los jugadores tengan que parar la partida con trámites de papel.

El proyecto se compone de un **Backend API REST** desarrollado con Django (sin DRF, usando `JsonResponse` nativo) y una **aplicación móvil nativa** para Android escrita en Java.

---

## 📐 Arquitectura General del Sistema

El sistema implementa una arquitectura desacoplada cliente-servidor:

**Servidor (Backend):** Expone endpoints REST que devuelven y reciben JSON. Gestiona toda la lógica de negocio: creación de salas con código único, control de personajes (stats, nivel, XP), sistema de inventario con ítems equipables y consumibles, y mapas que el GM activa o desactiva en tiempo real. Corre en producción sobre Gunicorn en un VPS público.

**Cliente (Frontend):** Aplicación móvil nativa con Material Design 3. Se conecta a la API mediante Retrofit 2 + OkHttp, gestiona la sesión activa con un `SessionManager` singleton y ofrece dos vistas diferenciadas según el rol del usuario: Jugador o Game Master.

---

## ✨ Características Principales

### 🖥️ Backend API (Django nativo)

- **Autenticación propia:** Registro e inicio de sesión sin librerías externas. Las credenciales se verifican con `make_password` / `check_password` de Django.
- **Sistema de Salas:** El GM crea una sala con nombre y código único; los jugadores buscan por código y se unen.
- **Gestión de Héroes:** Creación con 6 clases (Guerrero, Arquero, Mago, Berserker, Pícaro, Clérigo) y 5 razas (Humano, Elfo, Enano, Orco, Mediano), cada combinación con su propia tabla de crecimiento de stats por nivel.
- **Sistema de Niveles y XP:** Curva de progresión `50·n·(n+1)`. El GM puede subir o bajar el nivel manualmente; el servidor recalcula stats y escala la XP automáticamente.
- **Inventario relacional (N:N):** Un mismo ítem del catálogo puede pertenecer a varios héroes con cantidades y estados de equipamiento independientes. Equipar o desequipar aplica o retira el bonus del ítem en los stats del héroe de forma simétrica.
- **Mapas por sala:** El GM sube imágenes de mapas (base64) y controla cuál está visible para los jugadores en cada momento.
- **VPS en producción:** API accesible en `http://161.97.73.46:8000/` sin configuración adicional.

### 📱 Aplicación Móvil (Android Java)

- **13 actividades** organizadas según el flujo de cada rol.
- **Flujo del Jugador:** Login → Lobby → SearchRoom → CharacterList → CharacterDetail / Inventory / Map.
- **Flujo del Game Master:** Login → Lobby → CreateRoom → MasterDashboard → (gestión de héroes, ítems y mapas).
- **Ficha de personaje (CharacterDetailActivity):** Visualización de HP, Fuerza, Destreza, Defensa, Maná, Nivel y barra de XP. El GM puede editar stats y nivel directamente desde esta pantalla.
- **Inventario interactivo (InventoryActivity):** Los jugadores usan consumibles (descuenta cantidad o borra el ítem) y equipan/desequipan objetos. El GM da ítems desde el catálogo de la sala o los retira.
- **Mapas compartidos (MapActivity):** El jugador ve el mapa que el GM ha marcado como visible. El GM gestiona la galería de mapas desde MapManagerActivity.
- **Tema oscuro/claro:** Selector de tema persistente disponible en todas las pantallas con barra de navegación inferior.
- **Reintentos automáticos de red:** OkHttp configurado con `Connection: close` y hasta 3 reintentos ante fallos de conexión con Gunicorn.

---

## 🛠️ Tecnologías y Dependencias

### Backend

| Tecnología | Versión | Uso |
|---|---|---|
| Python | 3.10+ | Lenguaje del servidor |
| Django | 6.0.5 | Framework web |
| Gunicorn | 23.0.0 | Servidor WSGI en producción |
| SQLite3 | — | Base de datos relacional |

> ⚠️ El backend usa **Django nativo sin DRF**. Todas las respuestas se construyen con `JsonResponse` y la lógica reside en funciones de vista decoradas con `@csrf_exempt`.

### Frontend (Android)

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 8 (JDK 17) | Lenguaje de la app |
| Android Gradle Plugin | 8.x | Build system |
| Retrofit 2 + OkHttp | 2.x | Peticiones HTTP y deserialización JSON |
| Gson | — | Conversión JSON ↔ objetos Java |
| Material Design 3 | — | Componentes de interfaz |

---

## 📂 Estructura del Proyecto

```
PixelDungeons/
├── PixelDungeons-BackEnd/
│   ├── api/
│   │   ├── migrations/          # 4 migraciones (0001–0004)
│   │   ├── models.py            # Usuario, Sala, Hero, Item, InventoryEntry, GameMap
│   │   ├── views.py             # 22 endpoints — lógica completa de la API
│   │   └── urls.py              # Enrutamiento de la aplicación
│   ├── pixeldungeons_backend/
│   │   ├── settings.py
│   │   └── urls.py
│   ├── requirements.txt
│   └── manage.py
│
├── PixelDungeons-FrontEnd/
│   └── app/src/main/java/com/example/pixeldungeons/
│       ├── PixelDungeonsApp.java    # Application class (inicialización global)
│       ├── network/
│       │   ├── ApiClient.java       # Singleton Retrofit + OkHttp
│       │   └── ApiService.java      # Declaración de todos los endpoints
│       ├── model/                   # Modelos de datos (Hero, Item, GameMap, StatItem)
│       ├── data/                    # Capa de repositorio
│       │   ├── HeroRepository.java
│       │   ├── ItemRepository.java
│       │   ├── PlayerInventoryRepository.java
│       │   └── GameMapRepository.java
│       └── ui/
│           ├── adapter/             # Adaptadores RecyclerView
│           │   ├── ItemAdapter.java
│           │   ├── HeroAdapter.java
│           │   ├── PlayerAdapter.java
│           │   ├── SalaAdapter.java
│           │   ├── MapAdapter.java
│           │   └── StatAdapter.java
│           ├── SessionManager.java      # Singleton de sesión activa
│           ├── ThemeHelper.java         # Gestión del tema oscuro/claro
│           ├── LoginActivity.java
│           ├── RegisterActivity.java
│           ├── LobbyActivity.java
│           ├── CreateRoomActivity.java
│           ├── SearchRoomActivity.java
│           ├── MasterDashboardActivity.java
│           ├── CharacterListActivity.java
│           ├── CharacterCreateActivity.java
│           ├── CharacterDetailActivity.java
│           ├── InventoryActivity.java
│           ├── MapActivity.java
│           ├── MapManagerActivity.java
│           └── ItemManagerActivity.java
│
├── docs/
│   ├── MEMORIA VERSION FINAL.md
│   ├── MEMORIA VERSION FINAL.pdf
│   └── Diagramas/                   # Imágenes de los 6 diagramas técnicos
└── README.md
```

---

## 📋 Documentación de la API (Endpoints)

Todas las respuestas son JSON. La base es `http://161.97.73.46:8000/` en producción.

| Recurso | Método | Endpoint | Descripción |
|---|---|---|---|
| Autenticación | POST | `/api/registro/` | Registra un nuevo usuario |
| Autenticación | POST | `/api/login/` | Inicia sesión, devuelve id y username |
| Salas | GET | `/api/salas/` | Lista todas las salas o busca por `?codigo=` |
| Salas | POST | `/api/salas/` | Crea una nueva sala (GM) |
| Salas | DELETE | `/api/salas/{sala_id}/` | Elimina una sala (GM) |
| Héroes | GET | `/api/salas/{sala_id}/heroes/` | Lista los héroes de una sala |
| Héroes | POST | `/api/heroes/` | Crea un héroe nuevo |
| Héroes | GET | `/api/heroes/{hero_id}/` | Detalle de un héroe (stats, nivel, XP) |
| Héroes | PUT | `/api/heroes/{hero_id}/` | Actualiza stats, nivel o XP de un héroe |
| Héroes | DELETE | `/api/heroes/{hero_id}/` | Elimina un héroe |
| Inventario | GET | `/api/heroes/{hero_id}/inventario/` | Inventario completo del héroe |
| Inventario | POST | `/api/heroes/{hero_id}/inventario/` | Da un ítem al héroe |
| Inventario | PUT | `/api/heroes/{hero_id}/inventario/{item_id}/` | Equipa/desequipa o actualiza cantidad |
| Inventario | DELETE | `/api/heroes/{hero_id}/inventario/{item_id}/` | Quita un ítem del inventario |
| Ítems | GET | `/api/items/` | Catálogo de ítems (filtrable por `?sala_id=`) |
| Ítems | POST | `/api/items/` | Crea un ítem en el catálogo de la sala |
| Ítems | DELETE | `/api/items/{item_id}/` | Elimina un ítem del catálogo |
| Mapas | GET | `/api/salas/{sala_id}/mapas/` | Lista los mapas de una sala |
| Mapas | POST | `/api/salas/{sala_id}/mapas/` | Sube un nuevo mapa (imagen en base64) |
| Mapas | GET | `/api/mapas/{mapa_id}/` | Detalle de un mapa |
| Mapas | PUT | `/api/mapas/{mapa_id}/` | Edita nombre, imagen o visibilidad |
| Mapas | DELETE | `/api/mapas/{mapa_id}/` | Elimina un mapa |

---

## 🚀 Guía de Instalación y Ejecución

### Opción A — Servidor activo (lo normal)

El backend está desplegado en un VPS público. Solo hace falta:

1. Compilar e instalar el APK en un dispositivo Android (API 24 o superior).
2. Abrir la app y registrarse o iniciar sesión.

El cliente ya apunta a `http://161.97.73.46:8000/` por defecto; no hay que configurar nada más.

---

### Opción B — Servidor caído o entorno local

Si el VPS deja de responder, puedes levantar el backend en local:

**1. Preparar el entorno Python:**

```bash
cd PixelDungeons-BackEnd
python -m venv .venv

# Windows
.venv\Scripts\activate
# Linux / macOS
source .venv/bin/activate

pip install -r requirements.txt
```

**2. Inicializar la base de datos:**

```bash
python manage.py migrate
```

**3. Arrancar el servidor:**

```bash
# Desarrollo (más sencillo):
python manage.py runserver 0.0.0.0:8000

# Producción (equivalente al VPS):
gunicorn pixeldungeons_backend.wsgi:application --bind 0.0.0.0:8000
```

---

## 📱 Inicialización del Frontend (Android)

1. Abre Android Studio y selecciona **Open An Existing Project**.
2. Dirígete a la carpeta `PixelDungeons-FrontEnd` y ábrela. Deja que Gradle descargue las dependencias y sincronice el proyecto.
3. **Configuración de la URL base** (solo necesario si usas servidor local):

   Edita la constante `BASE_URL` en:
   ```
   app/src/main/java/com/example/pixeldungeons/network/ApiClient.java
   ```

   ```java
   // Emulador de Android Studio:
   private static final String BASE_URL = "http://10.0.2.2:8000/";

   // Dispositivo físico en la misma red Wi-Fi:
   private static final String BASE_URL = "http://192.168.1.XX:8000/";
   ```

4. Haz clic en **Run 'app'** (`Shift + F10`) para compilar y desplegar la app en el emulador o dispositivo.

---

## 🛠️ Requisitos

### Frontend (Android)

- Android Studio Narwhal o superior
- JDK 17 o superior (AGP 8.x lo requiere aunque el código compile en Java 11)
- Android SDK: `minSdk = 24`, `targetSdk = 36`

### Backend (Python)

- Python 3.10 o superior
- Dependencias en `PixelDungeons-BackEnd/requirements.txt`
