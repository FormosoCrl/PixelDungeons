# 🐉 PixelDungeons - Companion App para Rol de Mesa

**PixelDungeons** es una aplicación móvil desarrollada como Proyecto Intermodular para el ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM). Funciona como un asistente virtual (*companion*) para partidas de rol de mesa, digitalizando y automatizando la gestión de personajes, inventarios y mapas para agilizar el ritmo de juego.

## 📱 Estructura del Cliente (Android Nativo)

La aplicación sigue el patrón de arquitectura **MVVM (Model-View-ViewModel)** y está compuesta por las siguientes pantallas principales (Activities):

* **`LoginActivity`**: Pantalla de entrada a la aplicación. Gestiona el inicio de sesión.
* **`RegisterActivity`**: Pantalla para el registro de nuevos usuarios en el sistema.
* **`LobbyActivity`**: El cruce de caminos inicial. Permite al usuario elegir entre crear una nueva sala (Dungeon Master) o unirse a una sala existente (Jugador).
* **`SearchActivity`**: Pantalla para que los Jugadores introduzcan el nombre/código de una sala y se unan a ella.
* **`CreateRoomActivity`**: Pantalla para que el Dungeon Master configure (nombre y contraseña) y funde una nueva sala.
* **`CharacterListActivity`**: Pantalla exclusiva para Jugadores. Muestra una lista de los personajes creados en esa sala y ofrece la opción de crear uno nuevo.
* **`MasterDashboardActivity`**: Panel de control exclusivo para el DM. Muestra la lista de todos los jugadores conectados a la sala para poder gestionar la partida.
* **`CharacterDetailActivity`**: La hoja de personaje interactiva. Muestra estadísticas y el inventario. El jugador gestiona sus propios ítems; el DM puede alterar estadísticas o repartir botín en tiempo real.
* **`MapActivity`**: El tablero visual de la partida. Los jugadores ven el mapa actual (con zoom y scroll). El DM tiene controles adicionales para importar e imponer nuevos mapas.

## 🗺️ Flujo de Navegación Principal

El flujo que sigue la aplicación desde que el usuario la abre es el siguiente:

1. **Autenticación:** El usuario inicia en `LoginActivity`. Si no tiene cuenta, navega a `RegisterActivity`. Una vez autenticado con éxito, avanza al menú principal.
2. **Selección de Camino:** El usuario llega a `LobbyActivity`, donde decide su rol para la sesión:
   * **Camino del Jugador:** Pulsa "Buscar Partida" -> Navega a `SearchActivity`. Al conectar con éxito, accederá a la gestión de su personaje (`CharacterListActivity`).
   * **Camino del Dungeon Master:** Pulsa "Crear Partida" -> Navega a `CreateRoomActivity`. Al crear la sala, accederá directamente a su panel de control (`MasterDashboardActivity`).

## ⚙️ Tecnologías Utilizadas

* **Frontend:** Android Nativo (Java) con vistas clásicas en XML (ConstraintLayout).
* **Backend:** Django (API REST propia).
* **Formatos de datos:** JSON para la comunicación HTTP (GET, POST, PUT, DELETE).

## 🚀 Estado del Proyecto
*En desarrollo - Fase inicial (Diseño de interfaz de autenticación y navegación base estructurado).*