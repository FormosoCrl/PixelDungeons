# 🐉 PixelDungeons - Companion App para Rol de Mesa

**PixelDungeons** es una aplicación móvil desarrollada como Proyecto Intermodular para el ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM). Funciona como un asistente virtual (*companion*) para partidas de rol de mesa, digitalizando y automatizando la gestión de personajes, inventarios y mapas para agilizar el ritmo de juego.

## 📱 Estructura del Cliente (Android Nativo)

La aplicación sigue el patrón de arquitectura **MVVM (Model-View-ViewModel)** y está compuesta por las siguientes pantallas principales (Activities):

* **`LoginActivity`**: Pantalla de entrada a la aplicación. Gestiona el inicio de sesión.
* **`RegisterActivity`**: Pantalla para el registro de nuevos usuarios en el sistema.
* **`LobbyActivity`**: Menú principal donde el usuario elige su rol (Dungeon Master o Jugador).
* **`SearchActivity`**: Buscador de salas existentes para Jugadores.
* **`CreateRoomActivity`**: Configuración de nueva sala para el Dungeon Master.
* **`CharacterListActivity`**: Listado de personajes del jugador en una sala. Incluye estado vacío y acceso a creación.
* **`CharacterCreateActivity`**: Formulario de creación de personaje (Nombre, Raza y Clase).
* **`MasterDashboardActivity`**: Panel de control del DM para gestionar jugadores y sala.
* **`CharacterDetailActivity`**: Hoja de personaje interactiva con estadísticas e inventario.
* **`MapActivity`**: Visualizador de mapas interactivo para la partida.

## 🗺️ Flujo de Navegación Principal

El flujo que sigue la aplicación desde que el usuario la abre es el siguiente:

1. **Autenticación:** El usuario inicia en `LoginActivity` -> Opcional `RegisterActivity`.
2. **Selección de Camino (`LobbyActivity`):**
   * **Ruta Jugador:** `SearchActivity` -> `CharacterListActivity`.
     * Si no tiene personajes: `CharacterCreateActivity`.
     * Si tiene: Selecciona uno y va a `CharacterDetailActivity`.
   * **Ruta Dungeon Master:** `CreateRoomActivity` -> `MasterDashboardActivity`.
3. **En Partida:** Acceso común a `MapActivity` para visualización del entorno.

## ⚙️ Tecnologías Utilizadas

* **Frontend:** Android Nativo (Java) con vistas clásicas en XML (`ConstraintLayout`, `RecyclerView`, `Spinners`).
* **Backend:** Django (API REST propia).
* **Formatos de datos:** JSON para la comunicación HTTP.

## 🚀 Estado del Proyecto
*En desarrollo - Fase inicial (Diseño de interfaces de usuario y flujo de navegación completo).*