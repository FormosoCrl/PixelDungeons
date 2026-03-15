🐉 PixelDungeons - Companion App para Rol de Mesa
PixelDungeons es una aplicación móvil desarrollada como Proyecto Intermodular para el ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM). Funciona como un asistente virtual (companion) para partidas de rol de mesa, digitalizando y automatizando la gestión de personajes, inventarios y mapas para agilizar el ritmo de juego.

📱 Estructura del Cliente (Android Nativo)
La aplicación sigue el patrón de arquitectura MVVM (Model-View-ViewModel) y está compuesta por las siguientes pantallas principales (Activities):

LoginActivity: Gestión de inicio de sesión de usuarios.

RegisterActivity: Registro de nuevos aventureros en el sistema.

LobbyActivity: Nodo central para elegir rol (Dungeon Master o Jugador).

SearchRoomActivity: Buscador de salas activas mediante código o nombre.

CreateRoomActivity: Creación y configuración de nuevas salas de juego.

CharacterListActivity: Gestión de los personajes del usuario en una sala específica.

CharacterCreateActivity: Formulario detallado para la creación de nuevos héroes.

CharacterDetailActivity: Hoja de personaje interactiva con visualización de estadísticas base (STR, DEX, DEF, MANA) y salud.

InventoryActivity: Mochila del jugador que lista objetos y equipo mediante un RecyclerView.

MapActivity: Centro neurálgico de la partida con visualización del mapa y accesos rápidos.

MasterDashboardActivity: Panel de control exclusivo para el DM para la gestión de la sala y jugadores.

🗺️ Flujo de Navegación Principal
El flujo lógico diseñado para la aplicación es el siguiente:

Autenticación: El usuario accede vía LoginActivity o se registra en RegisterActivity.

Selección de Rol (LobbyActivity):

   Ruta Jugador: SearchRoomActivity → CharacterListActivity. Si no existen personajes previos, se redirige a CharacterCreateActivity. Una vez seleccionado el héroe, se accede a CharacterDetailActivity.

   Ruta Dungeon Master: CreateRoomActivity → MasterDashboardActivity.

Modo Partida: Interconexión entre CharacterDetailActivity, InventoryActivity y MapActivity para el desarrollo del juego.

⚙️ Tecnologías Utilizadas
Frontend: Android Nativo (Java) con vistas en XML (ConstraintLayout, RecyclerView).

Backend: Django (API REST propia).

Formatos de datos: JSON para la comunicación HTTP (GET, POST, PUT, DELETE).

🚀 Estado del Proyecto
En desarrollo - Fase de diseño de interfaces (UI) y flujo de navegación completados.
