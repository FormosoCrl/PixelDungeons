# 🐉 PixelDungeons — Companion App para Rol de Mesa

PixelDungeons es una aplicación móvil desarrollada como Proyecto Intermodular para el ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM). Funciona como asistente virtual (*companion*) para partidas de rol de mesa, digitalizando la gestión de personajes, inventarios y mapas para agilizar el ritmo de juego.

---

## 📱 Tecnologías

| Capa | Tecnología |
|---|---|
| Frontend | Android Nativo (Java), XML (ConstraintLayout, RecyclerView) |
| Tema visual | Material Design 3 (paleta D&D personalizada) |
| Backend *(planificado)* | Django REST Framework |
| Comunicación *(planificada)* | HTTP/JSON (GET, POST, PUT, DELETE) |

---

## 🏗️ Arquitectura

La aplicación organiza el código en tres capas:

```
ui/          → Activities y Adapters (presentación)
data/        → Repositorios (fuente de datos, actualmente en memoria)
model/       → Entidades del dominio (Hero, Item, GameMap)
```

Los repositorios actúan como capa de abstracción entre la UI y los datos, de forma que cuando se integre el backend bastará con sustituir la implementación interna sin tocar las Activities.

> **Nota:** En la fase actual (prototipo sin backend) los datos se almacenan en listas estáticas en memoria y no persisten entre sesiones. La integración con la API REST de Django está prevista para la siguiente fase.

---

## 🗂️ Estructura del proyecto

### Modelos (`model/`)

| Clase | Descripción |
|---|---|
| `Hero` | Personaje jugador: nombre, raza, clase, HP, STR, DEX, DEF, MANA |
| `Item` | Objeto del juego: nombre, tipo, cantidad, consumible, descripción, bonus de stat y valor del bonus, estado equipado |
| `GameMap` | Mapa de partida: nombre y visibilidad para los jugadores |

### Repositorios (`data/`)

| Clase | Descripción |
|---|---|
| `HeroRepository` | Lista de personajes creados en la sesión |
| `ItemRepository` | Catálogo global de objetos disponibles (definidos por el GM) |
| `PlayerInventoryRepository` | Inventario real del jugador (separado del catálogo, empieza vacío) |
| `GameMapRepository` | Lista de mapas con control de visibilidad |

### Pantallas (`ui/`)

| Activity | Rol | Descripción |
|---|---|---|
| `LoginActivity` | Ambos | Inicio de sesión con email y contraseña |
| `RegisterActivity` | Ambos | Registro de nuevos usuarios |
| `LobbyActivity` | Ambos | Punto de entrada: buscar sala o crear sala |
| `SearchRoomActivity` | Jugador | Búsqueda de sala por código o nombre |
| `CreateRoomActivity` | GM | Creación y configuración de una nueva sala |
| `CharacterListActivity` | Jugador | Lista de personajes del jugador; vacía hasta que se crea uno |
| `CharacterCreateActivity` | Jugador | Formulario de creación: nombre, raza, clase; stats generados automáticamente por clase |
| `CharacterDetailActivity` | Ambos | Hoja de personaje con stats; el GM puede editarlos en campo libre; el jugador ve stats base + bonuses de objetos equipados |
| `MapActivity` | Ambos | Muestra el mapa actualmente visible; mensaje de espera si el GM no ha compartido ninguno |
| `InventoryActivity` | Ambos | Mochila del jugador: usar consumibles, equipar objetos; el GM puede dar objetos del catálogo o retirarlos |
| `MasterDashboardActivity` | GM | Panel de control: lista de jugadores, acceso a gestor de objetos y gestor de mapas |
| `MapManagerActivity` | GM | Crear mapas y activar cuál es visible para los jugadores |
| `ItemManagerActivity` | GM | Catálogo de objetos disponibles; crear nuevos objetos con tipo, descripción, stat que mejora y valor del bonus |

### Recursos XML (`res/`)

Toda la interfaz se construye declarativamente en XML usando `ConstraintLayout` como contenedor principal y `RecyclerView` para las listas dinámicas. Los layouts se dividen en dos grupos: **layouts de Activity** (una pantalla completa) y **layouts de item** (la vista de cada elemento dentro de un `RecyclerView`).

**Layouts de Activity (`res/layout/activity_*.xml`):**

| Layout | Activity asociada |
|---|---|
| `activity_login.xml` | Formulario de inicio de sesión (email + contraseña + botones) |
| `activity_register.xml` | Formulario de registro |
| `activity_lobby.xml` | Pantalla con dos botones: buscar sala / crear sala |
| `activity_search_room.xml` | Buscador de salas con campo de código |
| `activity_create_room.xml` | Formulario de creación de sala |
| `activity_character_list.xml` | Título + RecyclerView de personajes + FAB de crear, con empty state |
| `activity_character_create.xml` | Formulario con nombre, Spinner de raza, Spinner de clase |
| `activity_character_detail.xml` | Hoja de personaje con bloque de stats en `ScrollView` y botones de navegación a Mapa e Inventario |
| `activity_map.xml` | Vista del mapa activo (etiqueta + nombre del mapa) con empty state cuando no hay mapa visible |
| `activity_inventory.xml` | Mochila: RecyclerView de objetos, FAB de añadir (solo GM) y empty state |
| `activity_master_dashboard.xml` | Título de sala, lista de jugadores y tres botones de gestión |
| `activity_map_manager.xml` | Lista de mapas + FAB para crear |
| `activity_item_manager.xml` | Lista del catálogo de objetos + FAB para crear |

**Layouts de item (`res/layout/item_*.xml`):**

| Layout | Usado en |
|---|---|
| `item_player.xml` | Tarjeta de personaje (lista de la dashboard del GM y de la lista del jugador) |
| `item_map.xml` | Tarjeta de mapa con su nombre y un indicador de visibilidad (gestor de mapas) |
| `item_inventory.xml` | Tarjeta de objeto: icono, nombre, tipo · descripción · bonus, cantidad y botón de acción contextual |

**Recursos compartidos (`res/values/` y `res/values-night/`):**

| Fichero | Descripción |
|---|---|
| `themes.xml` | Tema `Theme.PixelDungeons` heredando de `Theme.Material3.DayNight.NoActionBar`. Define la paleta principal sobre los atributos `colorPrimary`, `colorOnPrimary`, `colorSurface`, `colorBackground`, etc. |
| `colors.xml` | Paleta completa Material 3 con tono D&D (oro/ámbar `#735C0C` como primario, fondo crema `#FFF8F1`). Incluye variantes de contraste medio y alto, además de los colores de compatibilidad heredados (`black`, `white`, `purple_*`, `teal_*`). |
| `theme_overlays.xml` | Overlays de Material 3 (`ThemeOverlay`) para los modos *medium contrast* y *high contrast*. |
| `strings.xml` | Cadenas globales (nombre de la app y literales reutilizables). |
| `values-night/` | Variantes oscuras de los tres ficheros anteriores; el sistema cambia automáticamente entre claro y oscuro según los ajustes del dispositivo. |

**Manifest (`AndroidManifest.xml`):**

Declara las 13 Activities y aplica `Theme.PixelDungeons` a toda la app. `LoginActivity` es la actividad lanzadora (`MAIN` + `LAUNCHER`).

---

## 🗺️ Flujo de navegación

```
LoginActivity
    └── LobbyActivity
            ├── [Jugador] SearchRoomActivity
            │       └── CharacterListActivity
            │               ├── CharacterCreateActivity
            │               └── CharacterDetailActivity ──┐
            │                       └── MapActivity       ├── triángulo de navegación
            │                       └── InventoryActivity ┘
            │
            └── [GM] CreateRoomActivity
                    └── MasterDashboardActivity
                            ├── ItemManagerActivity
                            ├── MapManagerActivity
                            └── CharacterDetailActivity (vista GM)
                                    └── InventoryActivity (vista GM)
```

---

## ⚔️ Funcionalidades implementadas

### Rol Jugador
- Crear personajes con nombre, raza y clase (stats generados automáticamente según clase)
- Ver hoja de personaje con stats en tiempo real
- Navegación fluida entre Stats, Mapa e Inventario
- Mochila: usar objetos consumibles (reduce cantidad), equipar objetos defensivos/ofensivos
- Al equipar un objeto se aplica su bonus a la stat correspondiente (STR, DEX, DEF, MANA o HP)
- Solo se puede tener un arma y una armadura equipadas a la vez; equipar una nueva desequipa la anterior automáticamente
- Visualización del mapa activo compartido por el GM

### Rol Game Master
- Crear sala y gestionar jugadores
- Ver y editar los stats de cualquier personaje
- Crear objetos en el catálogo: nombre, tipo, descripción, stat que mejora y valor del bonus
- Entrar al inventario de un jugador y darle objetos del catálogo o retirarlos
- Crear mapas y controlar cuál es visible para los jugadores en cada momento

---

## 🚀 Estado del proyecto

**Fase actual:** Prototipo de interfaz funcional sin backend.

| Módulo | Estado |
|---|---|
| Navegación y flujo completo | ✅ Completado |
| Gestión de personajes | ✅ Completado |
| Sistema de inventario y equipamiento | ✅ Completado |
| Catálogo de objetos (GM) | ✅ Completado |
| Gestión de mapas y visibilidad | ✅ Completado |
| Tema Material Design 3 | ✅ Completado |
| Integración con API REST (Django) | 🔲 Pendiente |
| Persistencia real (base de datos) | 🔲 Pendiente |
| Autenticación real | 🔲 Pendiente |
