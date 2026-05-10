# Memoria del Proyecto Intermodular

## PixelDungeons — Companion App para Rol de Mesa

> Ciclo Formativo de Grado Superior — Desarrollo de Aplicaciones Multiplataforma (DAM)
> Curso 2025 / 2026
> Centro de Formación Profesional Afundación
>
> *Entrega parcial — versión sin backend*

---

## Índice

1. [Descripción del proyecto y ámbito de implantación](#1-descripción-del-proyecto-y-ámbito-de-implantación)
2. [Temporalización del proyecto y fases del desarrollo](#2-temporalización-del-proyecto-y-fases-del-desarrollo)
3. [Requisitos hardware y software](#3-requisitos-hardware-y-software)
4. [Arquitectura de la aplicación](#4-arquitectura-de-la-aplicación)
5. [Descripción de datos](#5-descripción-de-datos)

---

## 1. Descripción del proyecto y ámbito de implantación

**PixelDungeons** es una aplicación móvil nativa para Android cuyo propósito es servir como *companion app* (aplicación acompañante) durante las partidas de rol de mesa. Su función principal es digitalizar la gestión de personajes, inventarios y mapas que tradicionalmente se realiza con hojas de papel, dados y minis sobre la mesa, de forma que el ritmo de juego no se vea interrumpido por trámites administrativos.

### Justificación

Cualquiera que haya jugado una partida de rol largas (Dungeons & Dragons, Pathfinder, Vampiro, Cthulhu…) sabe que las pausas para "actualizar la hoja", "recalcular la armadura" o "buscar dónde estaba el mapa de la cripta" rompen la inmersión. La idea del proyecto es trasladar esos cálculos y el seguimiento de objetos a una aplicación móvil con dos roles diferenciados:

- **Jugador**: lleva su personaje, su mochila y consulta el mapa que el master comparte.
- **Game Master (GM)**: gestiona el catálogo de objetos del mundo, los mapas que enseña a sus jugadores y puede modificar las hojas de personaje en cualquier momento.

### Usuarios objetivo

- Grupos de rol de mesa (3 a 6 personas habitualmente) que quieran agilizar sus sesiones.
- Dungeon Masters que necesiten una herramienta sencilla para preparar su contenido y compartirlo en directo.
- Jugadores que prefieran tener su hoja en el móvil antes que en una hoja de papel reutilizada decenas de veces.

### Tecnologías empleadas

| Capa | Tecnología | Estado |
|---|---|---|
| Cliente | Android nativo en Java | Implementado |
| Interfaz | XML declarativo (`ConstraintLayout`, `RecyclerView`) | Implementado |
| Sistema de tema | Material Design 3 (Material Theme Builder) | Implementado |
| Sistema de construcción | Gradle (scripts `.gradle.kts`) | Implementado |
| Servidor *(planificado)* | Django (sin `django-rest-framework`) | Pendiente |
| Persistencia *(planificada)* | SQLite gestionada por Django | Pendiente |
| Comunicación *(planificada)* | API REST propia, JSON | Pendiente |

### Cambios y evolución previstos

La fase actual cubre íntegramente el cliente Android. Para la entrega final se completará:

1. **API REST propia en Django** que exponga al menos GET, POST, PUT y DELETE.
2. **Persistencia real** en base de datos relacional, sustituyendo los repositorios estáticos en memoria que actualmente almacenan los datos.
3. **Autenticación de usuarios** real (actualmente la pantalla de login es funcional pero acepta cualquier credencial).
4. **Inventario por personaje**: ahora mismo `PlayerInventoryRepository` es un único inventario global; con el backend cada `Hero` tendrá su propio inventario asociado por relación.
5. **Sincronización en tiempo real** entre dispositivos del GM y los jugadores (el GM cambia el mapa visible y los jugadores lo ven al instante). Esta funcionalidad puede requerir un mecanismo de *polling* periódico contra la API.

---

## 2. Temporalización del proyecto y fases del desarrollo

El desarrollo se ha planificado en **siete fases** (la última, F7, queda pendiente para la entrega final por requerir el backend). Para planificarlo de forma rigurosa se ha aplicado la metodología PERT/CPM: primero se identifican actividades y dependencias, después se calculan los tiempos más tempranos y más tardíos de cada actividad, se determinan holguras y se identifica el **camino crítico**. A partir de ese análisis se construye el diagrama de Gantt como representación temporal final.

### 2.1 Identificación de actividades y dependencias

| ID | Actividad | Predecesoras | Duración estimada |
|----|-----------|--------------|-------------------|
| F1 | Análisis y diseño preliminar | — | 4 h |
| F2 | Maquetación XML y navegación entre Activities | F1 | 8 h |
| F3 | Implementación del rol Jugador (lista, creación, hoja, mochila) | F2 | 10 h |
| F4 | Implementación del rol GM (dashboard, gestor de objetos y mapas) | F2 | 8 h |
| F5 | Sistema de equipamiento, bonus de stats y exclusividad por categoría | F3, F4 | 6 h |
| F6 | Pulido visual (Material 3, empty states) y entrega parcial | F5 | 6 h |
| F7 *(pendiente)* | Backend Django, integración HTTP y persistencia | F6 | 8 h |

> **Total acumulado:** 50 h de trabajo, en línea con la duración estimada del Proyecto Intermodular indicada en el documento de orientaciones.

### 2.2 Análisis PERT — cálculo de tiempos y holguras

A partir de las dependencias y duraciones se han calculado los tiempos más tempranos y más tardíos siguiendo el método del camino crítico:

- **Pasada hacia adelante (forward pass):** se obtienen ES (*Earliest Start*) y EF (*Earliest Finish*) recorriendo el grafo desde F1.
  *EF = ES + Duración*. Para una actividad con varias predecesoras, *ES = máximo de los EF de las predecesoras*.

- **Pasada hacia atrás (backward pass):** se obtienen LS (*Latest Start*) y LF (*Latest Finish*) recorriendo el grafo desde la actividad final hacia atrás. *LS = LF − Duración*. Para una actividad con varias sucesoras, *LF = mínimo de los LS de las sucesoras*.

- **Holgura:** *H = LS − ES*. Las actividades con H = 0 forman el camino crítico.

| ID | Actividad | D | ES | EF | LS | LF | H | Crítica |
|----|-----------|---|----|----|----|----|---|---------|
| F1 | Análisis y diseño | 4 | 0 | 4 | 0 | 4 | 0 | ✅ |
| F2 | Maquetación + navegación | 8 | 4 | 12 | 4 | 12 | 0 | ✅ |
| F3 | Rol Jugador | 10 | 12 | 22 | 12 | 22 | 0 | ✅ |
| F4 | Rol GM | 8 | 12 | 20 | 14 | 22 | 2 | — |
| F5 | Equipamiento + stats | 6 | 22 | 28 | 22 | 28 | 0 | ✅ |
| F6 | Pulido + entrega parcial | 6 | 28 | 34 | 28 | 34 | 0 | ✅ |
| F7 | Backend + integración | 8 | 34 | 42 | 34 | 42 | 0 | ✅ |

**Camino crítico:** **F1 → F2 → F3 → F5 → F6 → F7** con duración total de **42 horas**. Cualquier retraso en una de estas actividades retrasa todo el proyecto.

La actividad F4 (Rol GM) presenta una holgura de 2 horas: aunque su duración es 8 h, F3 tarda 10 h, por lo que F4 puede empezar al mismo tiempo que F3 y aun así terminar 2 h antes de que F5 pueda empezar. Esto da margen para reorganizar tareas si surge algún imprevisto en el desarrollo del rol del GM.

### 2.3 Diagrama PERT (notación AON, *Activity on Node*)

> *Para el PDF de entrega final, este diagrama se generará en una herramienta visual (draw.io, Lucidchart o similar) con los nodos críticos resaltados en rojo y borde grueso. La representación textual siguiente respeta la misma información.*

**Formato de cada nodo (notación AON estándar):**

```
┌──────────────────────────┐
│  ES   │  Nombre  │  EF   │
│       │   (ID)   │       │
│       │  D / H   │       │
│  LS   │          │  LF   │
└──────────────────────────┘
```

**Diagrama:**

```
                ╔═══════════════════════╗
                ║  0  │  F1: Análisis │ 4 ║
                ║     │   D=4  H=0    ║
                ║  0  │               │ 4 ║      ← crítica (doble línea)
                ╚═══════════╤═══════════╝
                            │
                ╔═══════════▼═══════════╗
                ║  4  │ F2: Maquetac. │12 ║
                ║     │   D=8  H=0    ║
                ║  4  │               │12 ║      ← crítica
                ╚═══════════╤═══════════╝
                            │
                ┌───────────┴────────────┐
                │                        │
    ╔═══════════▼════════════╗  ┌────────▼────────────┐
    ║ 12 │ F3: Rol Jugador │22 ║  │ 12 │ F4: Rol GM │ 20 │
    ║    │  D=10  H=0      ║  │    │  D=8   H=2  │     │
    ║ 12 │                 │22 ║  │ 14 │            │ 22 │
    ╚═══════════╤════════════╝  └────────┬───────────┘
                │  (crítica)             │  (holgura = 2h)
                └────────────┬───────────┘
                             │
                ╔════════════▼═══════════╗
                ║ 22 │ F5: Equipamiento│28 ║
                ║    │   D=6  H=0      ║
                ║ 22 │                 │28 ║      ← crítica
                ╚════════════╤═══════════╝
                             │
                ╔════════════▼═══════════╗
                ║ 28 │ F6: Pulido      │34 ║
                ║    │   D=6  H=0      ║
                ║ 28 │                 │34 ║      ← crítica
                ╚════════════╤═══════════╝
                             │
                ╔════════════▼═══════════╗
                ║ 34 │ F7: Backend     │42 ║
                ║    │   D=8  H=0      ║
                ║ 34 │                 │42 ║      ← crítica (entrega final)
                ╚════════════════════════╝

  Leyenda:  ╔═╗ borde doble → actividad en el camino crítico (H=0)
            ┌─┐ borde simple → actividad con holgura > 0
            ES = Earliest Start | EF = Earliest Finish
            LS = Latest Start  | LF = Latest Finish
            D  = Duración (h)  | H  = Holgura (h)
```

### 2.4 Diagrama de Gantt

Una vez calculado el PERT, la planificación temporal queda como sigue. Las barras del **camino crítico** se marcan con bloque sólido `█`; la actividad con holgura (F4) se marca con `░`. La escala superior representa horas acumuladas de trabajo.

> *Para el PDF de entrega final, sustituir por una imagen generada con GanttProject u otra herramienta, manteniendo la misma planificación.*

```
            Horas:  0   5   10   15   20   25   30   35   40  42
                    │   │   │    │    │    │    │    │    │   │
F1  Análisis (CR)   ████
F2  Maquetación (CR)    ████████
F3  Rol Jugador (CR)            ██████████
F4  Rol GM (h=2)                ░░░░░░░░
F5  Equipam. (CR)                         ██████
F6  Pulido (CR)                                 ██████
F7  Backend (CR)                                      ████████
                    │   │   │    │    │    │    │    │    │   │
                    0   5   10   15   20   25   30   35   40  42

   █ = camino crítico (holgura 0)    ░ = actividad con holgura (F4: 2 h)
```

La duración total del proyecto es de **42 horas** (camino crítico). El paralelismo posible entre F3 y F4 — al estar ambas tras F2 y ser independientes — es lo que permite no acumular sus duraciones (10 + 8 = 18 h) y reducir el plazo total.

### 2.5 Retos encontrados y aprendizajes significativos

#### Reto 1 — Propagar el rol del usuario por la pila de Activities

El primer reto técnico fue diferenciar la misma `Activity` cuando la abre un jugador y cuando la abre el GM. Por ejemplo, `CharacterDetailActivity` muestra los stats de la misma manera, pero el GM debe poder editarlos y el jugador no.

La solución fue propagar un *flag* booleano `is_master` a través de los `Intent.putExtra(...)` y, en la `Activity`, usar `received.getBooleanExtra("is_master", false)` para decidir qué UI presentar. En `CharacterDetailActivity` esto implicó **reemplazar dinámicamente los `TextView` de las stats por `EditText`** cuando el rol es de GM, conservando el mismo `id` de la vista para que el resto del código no notara la diferencia.

Aprendizaje: en Android se pueden añadir y quitar vistas en tiempo de ejecución manteniendo la consistencia del layout, pero hay que copiar manualmente los `LayoutParams` de la vista original.

#### Reto 2 — Separar el catálogo del inventario real

El primer diseño del inventario tenía un único `ItemRepository` que servía como catálogo para el GM y como mochila para el jugador a la vez. El problema fue evidente al probarlo: cuando el GM creaba un objeto en su catálogo, ese objeto aparecía mágicamente en la mochila del jugador. Tampoco tenía sentido que los objetos del catálogo tuvieran una "cantidad", porque un catálogo no es un inventario.

La solución fue introducir un segundo repositorio, `PlayerInventoryRepository`, encargado en exclusiva del inventario del jugador. El método `giveItem(Item catalogItem)` recibe una *plantilla* del catálogo y crea (o incrementa) la entrada correspondiente en el inventario.

Aprendizaje: separar las responsabilidades de los datos antes de que la UI se vuelva confusa. Si dos vistas piden cosas semánticamente distintas a la misma fuente, probablemente esa fuente debería partirse en dos.

#### Reto 3 — Exclusividad de armas y armaduras

El sistema de equipamiento debía cumplir una regla del rol clásico: **no puedes llevar dos espadas a la vez ni dos armaduras**. La primera implementación marcaba el item como equipado sin más, lo que permitía equipar varios objetos del mismo tipo y acumular bonus indebidamente.

Se añadió el método `equipItem(int position)` en `PlayerInventoryRepository` que, antes de equipar el nuevo objeto, recorre el inventario en busca de cualquier otro item ya equipado del mismo `type` (`Arma` o `Armadura`) y lo desequipa automáticamente. Otros tipos (Hechizo, Llave, Otro…) no tienen restricción.

Aprendizaje: las reglas de negocio (incluso pequeñas) ganan claridad si se encapsulan en el repositorio en lugar de dispersarlas por las Activities.

#### Reto 4 — Cálculo de stats con equipamiento sin tocar el modelo `Hero`

Cuando un jugador equipa un objeto que da `+2 STR`, su fuerza efectiva sube en 2 puntos, pero solo mientras lo lleve puesto. Para evitar mutar el `Hero` original (lo que crearía bugs si se desequipa o se quita el objeto), los bonus se calculan **al vuelo** en el momento de pintar la hoja de personaje, leyendo de `PlayerInventoryRepository.getEquippedBonus(stat)` y sumándolo al valor base recibido por `Intent`.

Aprendizaje: si lo que muestras en pantalla se puede recalcular, mejor recalcularlo cada vez que mantenerlo guardado en otro sitio. Así nunca está desincronizado.

#### Reto 5 — Estados vacíos (*empty states*)

Una pantalla con un `RecyclerView` en blanco no informa al usuario. Cuando un jugador entra por primera vez a la app, no tiene personajes ni objetos, y un `RecyclerView` vacío parece un bug. Se añadió la lógica de mostrar/ocultar un `TextView` de mensaje en función de si la lista está vacía, refrescada en `onResume()` y tras cada acción que pueda cambiar el estado.

Aprendizaje: el empty state no es decoración, es información. Toda lista debería tener uno.

---

## 3. Requisitos hardware y software

### 3.1 Requisitos del entorno de desarrollo

El proyecto se ha desarrollado en un equipo con Windows 11. Las cifras que aparecen como mínimas son las que la documentación oficial de Android Studio recomienda como punto de partida razonable.

| Componente | Mínimo recomendado | Equipo de desarrollo utilizado |
|---|---|---|
| Sistema operativo | Windows 10 (64 bits) | Windows 11 |
| Procesador | x86_64 de 4 núcleos | *(rellenar con el procesador real)* |
| Memoria RAM | 8 GB | *(rellenar con la RAM real)* |
| Almacenamiento | 20 GB libres | *(rellenar)* |
| Emulador / dispositivo | AVD compatible con API 24+ o teléfono físico con depuración USB activa | *(rellenar)* |

### 3.2 Software de desarrollo

| Software | Versión usada en el proyecto | Uso |
|---|---|---|
| Android Studio | *(rellenar — la versión que tengas instalada, p. ej. Iguana 2023.2 o Koala 2024.1)* | IDE principal |
| Android Gradle Plugin (AGP) | 8.13.2 | Plugin de build |
| Gradle | 8.x (envuelto por el wrapper del proyecto) | Sistema de construcción |
| Java | Compatibilidad de fuente y destino: **Java 11** (`sourceCompatibility = VERSION_11`) | Lenguaje de la app |
| Android SDK | `minSdk = 24` (Android 7.0) — `compileSdk` y `targetSdk = 36` | Plataforma |
| `androidx.appcompat` | 1.7.1 | Compatibilidad hacia versiones antiguas |
| `androidx.constraintlayout` | 2.2.1 | Layouts adaptables |
| `com.google.android.material` | 1.13.0 | Componentes Material 3 |
| Git | 2.40+ | Control de versiones |

> Las versiones exactas se obtienen del fichero `gradle/libs.versions.toml` del proyecto, que centraliza el catálogo de dependencias.

### 3.3 Requisitos del dispositivo Android

| Componente | Mínimo | Recomendado |
|---|---|---|
| Versión de Android | 7.0 (API 24, *Nougat*) | 12 o superior (API 31+) |
| Memoria RAM | 2 GB | 4 GB o más |
| Espacio libre | 100 MB | 200 MB |
| Resolución | 720 × 1280 px (HD) | 1080 × 2400 px (Full HD+) |
| Conexión de red *(necesaria al integrar el backend)* | Wi-Fi o datos móviles | Wi-Fi |

> **Nota:** la versión actual no requiere conexión a internet porque todos los datos son locales. Cuando se integre el backend Django, será necesaria conexión de red para sincronizar con el servidor.

### 3.4 Requisitos del servidor *(fase final, planificación)*

| Componente | Mínimo |
|---|---|
| Sistema operativo | Linux (Ubuntu 22.04 LTS recomendado) |
| Python | 3.10+ |
| Django | 4.2 LTS (sin `django-rest-framework`) |
| Base de datos | SQLite (desarrollo) / PostgreSQL (producción) |

---

## 4. Arquitectura de la aplicación

### 4.1 Visión general — capas

La aplicación organiza el código en **tres capas** claramente delimitadas:

```
┌────────────────────────────────────────────────────────┐
│  ui/                                                   │
│   Activities + Adapters + Layouts XML                  │
│   (presentación, eventos de usuario, navegación)       │
└─────────────────────────┬──────────────────────────────┘
                          │
                          ▼
┌────────────────────────────────────────────────────────┐
│  data/                                                 │
│   Repositorios estáticos                               │
│   (HeroRepository, ItemRepository,                     │
│    PlayerInventoryRepository, GameMapRepository)       │
└─────────────────────────┬──────────────────────────────┘
                          │
                          ▼
┌────────────────────────────────────────────────────────┐
│  model/                                                │
│   POJOs del dominio                                    │
│   (Hero, Item, GameMap)                                │
└────────────────────────────────────────────────────────┘
```

**Justificación de no haber implementado MVVM completo:** el documento de orientaciones del proyecto indica explícitamente que la aplicación Android puede emplear el patrón MVVM "*usando o no*" dicho patrón. Para esta fase de prototipo se ha optado por un patrón **Repositorio + Activity** simplificado, en el que las Activities consumen directamente los repositorios. La capa `data/` está deliberadamente diseñada como una abstracción: cuando se integre el backend Django, la sustitución de los `static List<…>` por llamadas HTTP no obligará a modificar las Activities. Esta decisión prioriza la legibilidad y la velocidad de iteración sobre la pureza arquitectónica, justificada por el alcance reducido del prototipo.

### 4.2 Wireframes y navegación

> *Los wireframes de la aplicación se completan con capturas de la app real ejecutándose en el emulador para la versión final del PDF. La estructura visual de cada pantalla se describe a continuación.*

#### Pantalla 1 — Login

```
┌─────────────────────────┐
│       PixelDungeons     │
│                         │
│  [ email             ]  │
│  [ contraseña        ]  │
│                         │
│   ( Iniciar sesión )    │
│   (    Registrarse  )   │
└─────────────────────────┘
```

#### Pantalla 2 — Lobby (selección de rol)

```
┌─────────────────────────┐
│       PixelDungeons     │
│                         │
│  ( Buscar partida    )  │
│                         │
│  ( Crear partida     )  │
└─────────────────────────┘
```

#### Pantalla 3 — Lista de personajes (jugador)

```
┌─────────────────────────┐
│     Mis Personajes      │
│                         │
│   Aún no tienes…        │  ← empty state cuando no hay personajes
│                         │
│   ┌─────────────────┐   │
│   │ Nombre — Clase  │   │  ← cada item del RecyclerView
│   └─────────────────┘   │
│                         │
│                  ( + )  │  ← FAB de crear
└─────────────────────────┘
```

#### Pantalla 4 — Hoja de personaje

```
┌─────────────────────────┐
│      Aragorn            │
│                         │
│   HP    30 / 30         │  ← editables solo en modo GM
│   STR   18              │
│   DEX   12              │
│   DEF   16  (+2 escudo) │  ← suma del bonus equipado
│   MANA   0              │
│                         │
│   ( Mapa )  ( Mochila ) │
└─────────────────────────┘
```

#### Pantalla 5 — Mochila

```
┌─────────────────────────┐
│       Mochila           │
│                         │
│  ┌───────────────────┐  │
│  │ Espada larga      │  │
│  │ Arma · +2 STR  x1 │  │
│  │       (Equipado)  │  │  ← botón gris si ya equipado
│  └───────────────────┘  │
│  ┌───────────────────┐  │
│  │ Poción de vida    │  │
│  │ Consumible    x3  │  │
│  │           (Usar)  │  │
│  └───────────────────┘  │
│                  ( + )  │  ← FAB visible solo para GM
│   ( Stats )    ( Mapa ) │
└─────────────────────────┘
```

#### Pantalla 6 — Dashboard del Game Master

```
┌─────────────────────────┐
│  Sala: La Cripta        │
│                         │
│  Jugadores en la sala:  │
│  ┌───────────────────┐  │
│  │ Aragorn (Humano)  │  │
│  └───────────────────┘  │
│                         │
│  ( Gestionar objetos )  │
│  ( Gestionar mapas   )  │
│  ( Cerrar sala       )  │
└─────────────────────────┘
```

#### Diagrama de navegación completo

```
LoginActivity ──────────► RegisterActivity
     │
     ▼
LobbyActivity
     │
     ├── (Jugador) ──► SearchRoomActivity
     │                       │
     │                       ▼
     │                 CharacterListActivity ──► CharacterCreateActivity
     │                       │
     │                       ▼
     │                 ┌─────────────────────────────────┐
     │                 │   triángulo de navegación       │
     │                 │                                 │
     │                 │   CharacterDetailActivity       │
     │                 │           ▲   │                 │
     │                 │           │   ▼                 │
     │                 │      MapActivity ◄──► InventoryActivity │
     │                 └─────────────────────────────────┘
     │
     └── (GM) ──► CreateRoomActivity
                       │
                       ▼
                 MasterDashboardActivity
                       │
                       ├──► ItemManagerActivity
                       ├──► MapManagerActivity
                       └──► CharacterDetailActivity (modo GM)
                                  │
                                  └──► InventoryActivity (modo GM, FAB Añadir)
```

### 4.3 Diagrama de secuencia — equipar un objeto

La lógica más interesante de la aplicación es el flujo de equipar un objeto, que toca tres componentes: la `Activity`, el `Adapter` y el `Repository`. Es un buen ejemplo del patrón listener-callback de Android.

```
Usuario        InventoryActivity     ItemAdapter      PlayerInventoryRepository    Item
   │                  │                   │                       │                  │
   │  click "Equipar" │                   │                       │                  │
   ├─────────────────►│                   │                       │                  │
   │                  │                   │                       │                  │
   │                  │  (ya estaba       │                       │                  │
   │                  │   bindeado al     │                       │                  │
   │                  │   listener en     │                       │                  │
   │                  │   onBindViewHolder)                       │                  │
   │                  │                   │                       │                  │
   │                  │  onItemAction(pos)│                       │                  │
   │                  │◄──────────────────┤                       │                  │
   │                  │                   │                       │                  │
   │                  │  equipItem(pos)                           │                  │
   │                  ├──────────────────────────────────────────►│                  │
   │                  │                                           │                  │
   │                  │                                           │  para cada item  │
   │                  │                                           │  equipado del    │
   │                  │                                           │  mismo tipo:     │
   │                  │                                           │                  │
   │                  │                                           │  setEquipped(false)
   │                  │                                           ├─────────────────►│
   │                  │                                           │                  │
   │                  │                                           │  setEquipped(true)
   │                  │                                           ├─────────────────►│
   │                  │                                           │                  │
   │                  │  (vuelta de equipItem)                    │                  │
   │                  │◄──────────────────────────────────────────┤                  │
   │                  │                                                              │
   │                  │  notifyDataSetChanged()                                      │
   │                  ├──────────────────►│                                          │
   │                  │                   │                                          │
   │                  │  Toast "Equipado: ..."                                       │
   │                  │                                                              │
   │  ◄───── pantalla refrescada con el botón nuevo "Equipado" en gris ──────       │
```

### 4.4 UML de clases

> *El UML se acompañará en el PDF final con una imagen generada en Visual Paradigm o draw.io. La representación textual siguiente refleja las clases, atributos y relaciones reales del proyecto.*

#### Capa `model/`

```
┌─────────────────────────────┐
│           Hero              │
├─────────────────────────────┤
│ - name: String              │
│ - race: String              │
│ - heroClass: String         │
│ - hp: int                   │
│ - maxHp: int                │
│ - str: int                  │
│ - dex: int                  │
│ - defence: int              │
│ - mana: int                 │
├─────────────────────────────┤
│ + getters() para cada campo │
└─────────────────────────────┘

┌─────────────────────────────────┐
│              Item               │
├─────────────────────────────────┤
│ - name: String                  │
│ - type: String                  │
│ - quantity: int                 │
│ - consumable: boolean           │
│ - description: String           │
│ - equipped: boolean             │
│ - bonusStat: String             │
│ - bonusValue: int               │
├─────────────────────────────────┤
│ + getName() : String            │
│ + getType() : String            │
│ + getQuantity() : int           │
│ + isConsumable() : boolean      │
│ + getDescription() : String     │
│ + isEquipped() : boolean        │
│ + setEquipped(boolean): void    │
│ + getBonusStat() : String       │
│ + getBonusValue() : int         │
│ + decreaseQuantity() : void     │
│ + increaseQuantity() : void     │
└─────────────────────────────────┘

┌─────────────────────────────┐
│          GameMap            │
├─────────────────────────────┤
│ - name: String              │
│ - visible: boolean          │
├─────────────────────────────┤
│ + getName() : String        │
│ + isVisible() : boolean     │
│ + setVisible(boolean): void │
└─────────────────────────────┘
```

#### Capa `data/`

```
┌──────────────────────────────────────────┐
│          HeroRepository  (static)        │
├──────────────────────────────────────────┤
│ - heroes: List<Hero>                     │
├──────────────────────────────────────────┤
│ + getHeroes() : List<Hero>               │
│ + addHero(Hero) : void                   │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│          ItemRepository  (static)        │
├──────────────────────────────────────────┤
│ - items: List<Item>   ← catálogo         │
├──────────────────────────────────────────┤
│ + getItems() : List<Item>                │
│ + addItem(Item) : void                   │
│ + useItem(int) : boolean                 │
│ + removeOne(int) : void                  │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│   PlayerInventoryRepository  (static)    │
├──────────────────────────────────────────┤
│ - inventory: List<Item>  ← mochila       │
├──────────────────────────────────────────┤
│ + getInventory() : List<Item>            │
│ + giveItem(Item) : void                  │
│ + equipItem(int) : void                  │
│ + useItem(int) : void                    │
│ + removeOne(int) : void                  │
│ + getEquippedBonus(String) : int         │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│        GameMapRepository  (static)       │
├──────────────────────────────────────────┤
│ - maps: List<GameMap>                    │
├──────────────────────────────────────────┤
│ + getMaps() : List<GameMap>              │
│ + addMap(GameMap) : void                 │
│ + setVisible(int) : void                 │
└──────────────────────────────────────────┘
```

#### Capa `ui/`

```
                  AppCompatActivity
                          ▲
        ┌─────────────────┼─────────────────┐
        │                 │                 │
LoginActivity      LobbyActivity   …  (13 Activities en total)
        │
        ▼  (intent + extras)
LobbyActivity ──► SearchRoomActivity / CreateRoomActivity
                         │
                         ▼
                 CharacterListActivity ──► CharacterCreateActivity
                         │
                         ▼
                 CharacterDetailActivity ◄──► MapActivity
                         ▲                        ▲
                         └──────► InventoryActivity ──┘

                  RecyclerView.Adapter
                          ▲
        ┌─────────┬───────┴───────┬──────────┐
HeroAdapter  PlayerAdapter   ItemAdapter  MapAdapter
```

**Relaciones clave entre clases:**

- `InventoryActivity` **usa** `PlayerInventoryRepository` y `ItemRepository`. Crea instancias de `ItemAdapter` y le pasa el callback `OnItemActionListener` (interfaz interna del adapter).
- `ItemAdapter.OnItemActionListener` es una interfaz funcional que la `Activity` implementa con una expresión lambda.
- `CharacterDetailActivity` **lee** `PlayerInventoryRepository.getEquippedBonus(stat)` para calcular las stats efectivas.
- `MasterDashboardActivity` **observa** `HeroRepository.getHeroes()` mediante `notifyDataSetChanged()` en `onResume()`.

### 4.5 Servidor *(pendiente para entrega final)*

> Esta sección queda pendiente para la entrega final del proyecto, una vez se haya implementado el backend. Incluirá:
>
> - **Diagrama E/R completo** con las entidades del modelo (`Usuario`, `Sala`, `Hero`, `Item`, `GameMap`, `PlayerInventoryEntry`) y sus relaciones, incluyendo al menos una relación N:N (por ejemplo, jugadores ↔ salas).
> - **Especificación OpenAPI 3.0** de la fachada REST con todos los endpoints documentados (parámetros, cuerpos de petición/respuesta, códigos HTTP).
> - Mapeo entre los repositorios actuales (`HeroRepository`, `ItemRepository`, etc.) y los nuevos endpoints HTTP.

---

## 5. Descripción de datos

A continuación se analizan los fragmentos de código más representativos, eligiéndolos por su carga de lógica de negocio y porque ejemplifican decisiones de diseño justificadas en la sección de arquitectura.

### 5.1 Modelo `Item` — el corazón del sistema de objetos

```java
public class Item {
    private String name;        // ej. "Espada larga"
    private String type;        // ej. "Arma" / "Armadura" / "Consumible"
    private int quantity;       // unidades en inventario
    private boolean consumable; // se gasta al usar
    private String description; // texto de saborización ("Daño: 1d8 cortante")
    private boolean equipped;   // estado actual
    private String bonusStat;   // "str", "dex", "def", "mana", "hp" o "none"
    private int bonusValue;     // p. ej. +2

    public Item(String name, String type, int quantity, boolean consumable,
                String description, String bonusStat, int bonusValue) { … }

    // Constructor abreviado para creación rápida sin bonus:
    public Item(String name, String type, int quantity, boolean consumable, String description) {
        this(name, type, quantity, consumable, description, "none", 0);
    }

    public void decreaseQuantity() {
        if (quantity > 0) quantity--;
    }

    public void increaseQuantity() { quantity++; }
}
```

**Tipos de los atributos:**

| Atributo | Tipo Java | Justificación |
|---|---|---|
| `name`, `type`, `description`, `bonusStat` | `String` | Texto libre o etiqueta de categoría |
| `quantity`, `bonusValue` | `int` | Enteros positivos, suficientes para el rango esperado |
| `consumable`, `equipped` | `boolean` | Estados binarios |

**Decisiones de diseño:**

1. **Dos constructores**: el segundo constructor delega al primero pasando `"none"` y `0` como bonus por defecto. Cuando ampliamos el modelo para incluir bonus de stats, esto evitó tener que modificar todos los sitios donde ya se creaban `Item` (por ejemplo, los items por defecto del catálogo).
2. **Bonus como `String + int`** en vez de usar un `enum`: cuando llegue el backend, el campo se mapea directamente a una columna de texto en la base de datos sin pasos intermedios.
3. **`decreaseQuantity()` con guarda `if (quantity > 0)`**: evita que la cantidad baje a negativo. No lanza excepción porque la UI ya deshabilita el botón en cuanto la cantidad llega a 0.

### 5.2 `PlayerInventoryRepository.equipItem` — exclusividad por categoría

```java
public static void equipItem(int position) {
    Item newItem = inventory.get(position);
    String type = newItem.getType();

    if (type.equals("Arma") || type.equals("Armadura")) {
        for (Item i : inventory) {
            if (i != newItem && i.isEquipped() && i.getType().equals(type)) {
                i.setEquipped(false);
            }
        }
    }
    newItem.setEquipped(true);
}
```

**Análisis del flujo:**

1. Se obtiene el objeto que se quiere equipar (`newItem`) y su tipo (`type`).
2. Si el tipo es `"Arma"` o `"Armadura"`, se recorre el inventario buscando *otros* objetos equipados del mismo tipo. La comparación `i != newItem` es para no desequipar el propio objeto que estamos intentando equipar (en el caso, poco frecuente pero posible, de que ya estuviera marcado como equipado).
3. Cualquier otro objeto encontrado en esa categoría se desequipa con `setEquipped(false)`, perdiendo automáticamente su bonus en el siguiente cálculo de stats.
4. Finalmente, el nuevo objeto se marca como equipado.

**Pseudocódigo equivalente:**

```
función equipItem(posición):
    nuevo ← inventario[posición]
    si nuevo.tipo es Arma o Armadura entonces
        para cada item en inventario:
            si item ≠ nuevo  Y  item.equipado  Y  item.tipo == nuevo.tipo:
                item.equipado ← falso
    fin si
    nuevo.equipado ← verdadero
```

**¿Por qué solo Arma y Armadura?** El resto de tipos (Hechizo, Llave, Otro) pueden estar equipados varios a la vez sin que sea raro: tener varios pergaminos preparados o varias llaves en el cinto es perfectamente normal en una partida de rol. Limitar la exclusividad a las dos categorías que sí lo necesitan evita complicar el sistema con casos especiales.

### 5.3 `getEquippedBonus` — cálculo de stats al vuelo

```java
public static int getEquippedBonus(String stat) {
    int bonus = 0;
    for (Item item : inventory) {
        if (item.isEquipped() && stat.equals(item.getBonusStat())) {
            bonus += item.getBonusValue();
        }
    }
    return bonus;
}
```

**Tipos:**

- `stat`: `String` (clave de la stat: `"str"`, `"dex"`, `"def"`, `"mana"`, `"hp"`).
- Devuelve un `int`: la suma de bonus aplicables.

**Consumido en `CharacterDetailActivity`:**

```java
int strBonus  = isMaster ? 0 : PlayerInventoryRepository.getEquippedBonus("str");
int defBonus  = isMaster ? 0 : PlayerInventoryRepository.getEquippedBonus("def");

strText.setText(String.valueOf(received.getIntExtra("str", 0) + strBonus));
defText.setText(String.valueOf(received.getIntExtra("defence", 0) + defBonus));
```

**Lectura del `if` ternario:** si la pantalla la abre el GM, los bonus se ponen a cero porque el GM debe ver y editar los stats **base** del personaje, no los modificados por equipo. El jugador siempre ve el valor efectivo.

### 5.4 Reemplazo dinámico de `TextView` por `EditText` en modo GM

Esta es probablemente la decisión más sutil del proyecto: cuando el GM abre la hoja de un personaje, los stats deben ser editables. Pero el layout XML está pensado para presentar `TextView` estáticos. La solución es **sustituir las vistas en tiempo de ejecución**.

```java
private void replaceWithEditText(TextView source, ConstraintLayout parent, int inputType) {
    int index = parent.indexOfChild(source);
    ViewGroup.LayoutParams params = source.getLayoutParams();

    EditText edit = new EditText(this);
    edit.setId(source.getId());                                  // mantiene el id
    edit.setText(source.getText());                              // copia el texto
    edit.setTextColor(source.getCurrentTextColor());             // copia color
    edit.setTypeface(source.getTypeface(),
            source.getTypeface() != null ? source.getTypeface().getStyle() : 0);
    edit.setTextSize(TypedValue.COMPLEX_UNIT_PX, source.getTextSize());
    edit.setInputType(inputType);
    edit.setLayoutParams(params);                                // copia constraints

    parent.removeView(source);
    parent.addView(edit, index);                                 // misma posición
}
```

**Puntos importantes:**

- **`source.getId()` se reasigna al `EditText`** para que el resto del código siga encontrando la vista por `findViewById(R.id.str_value)`.
- **Se preserva el índice (`indexOfChild`)** para mantener el orden visual dentro del `ConstraintLayout`.
- **Se copian los `LayoutParams`** para no perder las constraints definidas en el XML (cómo se ancla la vista al resto del layout).
- **`inputType`** parametriza si el campo acepta texto general o solo números (los stats numéricos como STR usan `TYPE_CLASS_NUMBER`).

Esta función se invoca cinco veces, una por cada stat editable:

```java
if (isMaster) {
    ConstraintLayout content = findViewById(R.id.detail_content);
    replaceWithEditText(hpText,   content, InputType.TYPE_CLASS_TEXT);
    replaceWithEditText(strText,  content, InputType.TYPE_CLASS_NUMBER);
    replaceWithEditText(dexText,  content, InputType.TYPE_CLASS_NUMBER);
    replaceWithEditText(defText,  content, InputType.TYPE_CLASS_NUMBER);
    replaceWithEditText(manaText, content, InputType.TYPE_CLASS_NUMBER);
}
```

### 5.5 `ItemAdapter.onBindViewHolder` — UI contextual según rol y estado

```java
if (listener == null) {
    // modo catálogo: ni botón ni cantidad
    holder.actionButton.setVisibility(View.GONE);
    holder.quantityText.setVisibility(View.GONE);
} else {
    holder.quantityText.setVisibility(View.VISIBLE);
    holder.actionButton.setVisibility(View.VISIBLE);
    holder.actionButton.setAlpha(1.0f);

    if (masterMode) {
        holder.actionButton.setText("Quitar");
        holder.actionButton.setEnabled(item.getQuantity() > 0);
    } else if (item.isConsumable()) {
        holder.actionButton.setText("Usar");
        holder.actionButton.setEnabled(item.getQuantity() > 0);
    } else {
        if (item.isEquipped()) {
            holder.actionButton.setText("Equipado");
            holder.actionButton.setEnabled(false);
            holder.actionButton.setAlpha(0.4f);   // gris translúcido
        } else {
            holder.actionButton.setText("Equipar");
            holder.actionButton.setEnabled(item.getQuantity() > 0);
        }
    }
    // …
}
```

**Diagrama de decisión (árbol if/else):**

```
                  ┌─── listener == null? ───┐
                  │                         │
                 sí                         no
                  │                         │
        ocultar botón y cantidad            │
        (vista de catálogo)                 │
                                ┌─── masterMode? ───┐
                                │                   │
                               sí                  no
                                │                   │
                          "Quitar"                  │
                                            ┌─── consumable? ───┐
                                           sí                  no
                                            │                   │
                                         "Usar"        ┌─── equipped? ───┐
                                                      sí                no
                                                       │                 │
                                                "Equipado"          "Equipar"
                                                 (gris,
                                                  α=0.4,
                                                  disabled)
```

**Decisión clave:** el mismo `RecyclerView.Adapter` sirve para **tres contextos distintos** (catálogo del GM, mochila del jugador, mochila vista por el GM) cambiando solo el listener y un *flag*. Esto evita duplicar código de adaptador y centraliza la presentación de un `Item` en un único lugar.

### 5.6 Generación de stats por clase de personaje

```java
private int[] generateStatsForClass(String heroClass) {
    switch (heroClass) {
        case "Guerrero":   return new int[]{30, 18, 12, 14,  0};
        case "Arquero":    return new int[]{22, 10, 24,  8,  6};
        case "Mago":       return new int[]{18,  6, 12,  6, 20};
        case "Berserker":  return new int[]{35, 22,  8, 10,  0};
        case "Pícaro":     return new int[]{20, 12, 22,  8,  4};
        case "Clérigo":    return new int[]{24, 12, 10, 12, 16};
        default:           return new int[]{20, 10, 10, 10,  0};
    }
}
```

El array devuelto se interpreta posicionalmente como `[hp, str, dex, def, mana]`. Cada clase tiene un perfil de stats coherente con el rol clásico del rol de mesa: el Guerrero acumula HP y STR, el Mago apuesta todo a MANA y minimiza HP físico, el Arquero gira en torno a DEX, etc.

**Por qué un `switch` en lugar de un `Map<String, int[]>`:** el `switch` es más eficiente y, en Java moderno, igual de legible. Para seis casos no merece la pena el overhead de inicializar un mapa estático. Si en el futuro las clases se cargasen desde el backend, esta función se sustituirá por una llamada que devuelva el perfil del servidor.

---

## Conclusión de la entrega parcial

Esta entrega cubre íntegramente el **cliente Android funcional** del proyecto, con:

- **13 Activities** que cubren todos los flujos de los dos roles (Jugador y Game Master).
- **3 modelos** del dominio (`Hero`, `Item`, `GameMap`).
- **4 repositorios** con responsabilidades claramente separadas.
- **4 adapters** de `RecyclerView`.
- **Sistema de equipamiento** completo con bonus dinámicos y exclusividad por categoría.
- **Tema Material Design 3** con paleta personalizada y soporte de modo oscuro.

Para la entrega final se completará el **backend Django con API REST**, la persistencia real, la documentación OpenAPI, el diagrama E/R y la integración HTTP de las pantallas existentes (sustituyendo las llamadas a los repositorios estáticos por llamadas a la API).

---

*Memoria preparada para la entrega parcial del Proyecto Intermodular del ciclo de Desarrollo de Aplicaciones Multiplataforma.*
