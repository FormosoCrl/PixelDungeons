# 🐉 PixelDungeons - Companion App para Rol de Mesa

**PixelDungeons** es una aplicación móvil desarrollada como Proyecto Intermodular para el ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM). Funciona como un asistente virtual (*companion*) para partidas de rol de mesa, digitalizando y automatizando la gestión de personajes, inventarios y mapas para agilizar el ritmo de juego.

## 📱 Estructura del Cliente (Android Nativo)

La aplicación sigue el patrón de arquitectura **MVVM (Model-View-ViewModel)** y está compuesta por las siguientes pantallas principales (Activities):

* **`LoginActivity`**: Pantalla de entrada a la aplicación. Gestiona la autenticación de usuarios (Login y Registro).

* **`LobbyActivity`**: El cruce de caminos inicial. Permite al usuario elegir entre crear una nueva sala (convirtiéndose en Dungeon Master) o unirse a una sala existente mediante un código (como Jugador).

* **`CharacterListActivity`**: Pantalla exclusiva para Jugadores. Muestra una lista de los personajes creados en esa sala y ofrece la opción de crear uno nuevo.

* **`MasterDashboardActivity`**: Panel de control exclusivo para el Dungeon Master (DM). Muestra la lista de todos los jugadores conectados a la sala para poder gestionar la partida.

* **`CharacterDetailActivity`**: La hoja de personaje interactiva. Muestra las estadísticas (Fuerza, Vida, etc.) y el inventario. Si accede el jugador, puede gestionar sus propios ítems; si accede el DM, puede alterar estadísticas o repartir *loot* (botín) en tiempo real.

* **`MapActivity`**: El tablero visual de la partida. Los jugadores pueden ver el mapa actual con opciones de zoom y desplazamiento. El DM tiene controles adicionales para importar e imponer nuevos mapas a la vista de los jugadores.


## ⚙️ Tecnologías Utilizadas

* **Frontend:** Android Nativo (Java) con vistas clásicas en XML.
* **Backend:** Django (API REST propia).
* **Formatos de datos:** JSON para la comunicación HTTP (GET, POST, PUT, DELETE).

## 🚀 Estado del Proyecto
*En desarrollo - Fase inicial (Estructura de UI y navegación).*