# AGENTS.md — Yra Music Player

> Este documento da contexto a cualquier agente de IA (Claude Code, Antigravity, Copilot, etc.) que colabore en este repositorio. Léelo antes de proponer cambios de arquitectura, UI o alcance de funcionalidades.

## 1. Resumen del proyecto

- **Nombre (en discusión):** Yra Music Player — alternativas: Yva, Ythra, Ycho. Usar "Yra" como placeholder hasta que el autor confirme el nombre final. No asumir un nombre definitivo en código, strings o assets sin confirmarlo.
- **Autor:** Ardanny Romero (proyecto personal #1).
- **Idea principal:** Reproductor de música local (mp3) para Android, con estadísticas de escucha y metadatos enriquecidos.
- **Diferenciales clave:**
  - Base de datos local en el dispositivo para registrar estadísticas y metadatos de canciones.
  - Metadato extra `fuente` (y opcional `fuente secundaria`): enlace externo (idealmente YouTube) asociado a cada canción, para preservar su origen.
  - Función futura: descargar el mp3 directamente desde el enlace de `fuente` guardado.
  - Componente social (a futuro, fuera del alcance inicial): red social/foros para conectar usuarios por gustos musicales, con perfil que muestra estadísticas.
  - **Principio de diseño no negociable: la app debe funcionar 100% offline.** Lo único que requiere red es la funcionalidad opcional de compartir gustos/estadísticas en la nube (backend Node/Express + frontend Next.js), que es una fase posterior del proyecto.

## 2. Stack técnico

| Módulo | Tecnología |
|---|---|
| App Android | **Kotlin** (proyecto actual, en blanco) |
| Web Frontend | React - Next.js (fase futura) |
| Web Backend | Express.js (Node.js) (fase futura) |

El foco inmediato de desarrollo es **solo el módulo Android en Kotlin**. No introducir dependencias de backend/web en esta etapa salvo que se pida explícitamente.

## 3. Historias de usuario (prioridad y riesgo)

| # | Historia | Prioridad | Riesgo |
|---|---|---|---|
| 1 | CRUD de archivos mp3 (ver, añadir, eliminar, editar en el dispositivo) | Alto | Bajo |
| 2 | Reproducción en segundo plano (notificación/widget, controles por hardware externo, sobrevive a cierre de app, solo se detiene si se descarta la notificación) | Alto | Medio |
| 3 | Estadísticas de escucha (horas, canciones más escuchadas), 100% offline | Bajo | Bajo |
| 4 | Preferencias UI: modo claro/oscuro, tamaño de letra, etc. | Medio | Medio |
| 5 | Preferencias UX: temporizador de reproducción (tiempo o nº canciones), orden secuencial/aleatorio, ecualizador integrado | Bajo | Alto |
| 6 | Playlists/directorios personalizados + playlist especial "favoritas" (sin carpeta física) | Medio | Bajo |
| 7 | Edición de metadatos, incluyendo campos custom `fuente` y `fuente secundaria` (no todos los metadatos son editables, ej. duración) | Alto | Alto |
| 8 | Compartir/almacenar en la nube (estilo repo) canciones/playlists/estadísticas — **fase final del proyecto** | Medio-Bajo | Bajo |
| 9 | Foros musicales en el sitio web (agrupar usuarios por gustos, requiere moderación) — **altamente descartable, en evaluación** | Baja | Medio |
| 10 | Migración/descarga de canciones desde el repositorio en la nube, incluida descarga vía `fuente` (enlace) | Alta | Alto |

**Orden de implementación recomendado para el MVP Android:** 1 → 2 → 6 → 7 (metadatos básicos) → 4 → 3 → 5 → resto de historias son fase web/nube (8, 9, 10), fuera de alcance del proyecto Kotlin actual.

## 4. Diseño de UI

Estilo visual: **neumorfismo**.

### Tipografía
- **Space Grotesk** — logo, títulos (h1/h2), nombre de la app. Pesos 400 y 500.
- **Inter** — cuerpo, listas, metadatos, botones secundarios. Pesos 400 y 500.

### Paleta — Modo claro
- Fondos: `#FFFFFF` (inicio degradado) → `#F7F6FC` (fin degradado / superficie de tarjetas)
- Acento morado: `#8B86C4` (principal), `#B8B4E0` (claro/hover), `#5A5490` (oscuro/pressed)
- Texto: `#2E2A4A` (primario), `#3A3660` (secundario), `#8B87A8` (terciario/muted)
- Sombras neumorphism: `#D8D5EA` (oscura), `#FFFFFF` (clara/brillo)

### Paleta — Modo oscuro
- Fondos: `#1E1E24` (inicio degradado) → `#23232A` (fin degradado), `#2B2B33` (superficie de tarjetas)
- Acento morado (igual en ambos modos): `#8B86C4`, `#B8B4E0`, `#5A5490`
- Texto: `#E6E6EB` (primario), `#B6B6C2` (secundario), `#8E8E9E` (terciario/muted), `#B8B4E0` (íconos secundarios)
- Sombras neumorphism: `#111116` (oscura), `#2F2F38` (clara/brillo)

## 5. Arquitectura de navegación

- **Header** (hamburger button + search bar) y **Navbar** deben ser componentes persistentes/modulares presentes en todas las pantallas, **excepto** en `Song_play`.
- Al reproducir cualquier canción desde cualquier pantalla, debe aparecer automáticamente un **Mini_song_play** (mini reproductor flotante).
- Tocar el Mini_song_play abre `Song_play` (pantalla completa de reproducción).

### Hamburger menu
Stats, Web site, About us, Configuration

### Navbar (bottom)
Lobby, Songs, Playlists, Stats, Options

## 6. Pantallas principales

- **Lobby:** resumen de estadísticas básicas (tiempo escuchado hoy/semana/siempre) + secciones temáticas (favoritas, más escuchadas, recién agregadas).
- **Songs:** lista completa de canciones del dispositivo en cards horizontales; tap = reproducir. El directorio de búsqueda es configurable (por defecto uno predeterminado).
- **Playlists:** grid de 3 columnas con cards cuadradas. Una playlist puede ser una lista de canciones o un directorio específico del dispositivo.
- **Stats:** estadísticas de uso — nº de canciones, tiempo de escucha (diario/semanal/histórico), actividad por hora del día, canciones más escuchadas, etc.
- **Options:** ecualizador (nivel/tono), presets predefinidos o personalizados, velocidad y tono de reproducción. (Futuro: aplicar ajustes por canción específica, no solo global.)
- **Configuration:** tema claro/oscuro, tamaño de letra, idioma (futuro; por ahora todo en inglés), directorios a leer para mp3, etc.
- **Web_site:** información sobre el sitio web + botón de acceso + login (no funcional en esta etapa).
- **About_us:** info de la app, saludo del desarrollador, botón de donación estilo "buy me a coffee" (sutil, no forzado).

## 7. Pantallas secundarias

- **Mini_song_play:** imagen, título/artista, botones play/pause, next, anterior.
- **Song_play:** imagen grande, controles de reproducción, modo aleatorio, loop (1 vez / siempre), favorito, temporizador (nº canciones o tiempo), añadir a playlist, acceso a `Song_options`.
- **Song_options:** menú contextual — ir a `Options`, ver `Song_data`, ver `Song_detailed_data`, compartir, eliminar del dispositivo, agregar a playlist, ir al directorio del archivo.
- **Song_data:** metadatos editables — título, álbum, artista/compositor, fecha, género, lyrics, `fuente`, `fuente secundaria`, miniatura.
- **Song_detailed_data:** metadatos técnicos de solo lectura — nombre de archivo, ruta, etc.
- **Media/MediaStyle Notification:** notificación del sistema para controlar la reproducción; debe emular el diseño de la app dentro de las limitaciones del SO.

## 8. Reglas para el agente al trabajar en este repo

1. **No implementar** funcionalidades de red/nube/social (historias 8, 9, 10) a menos que se pida explícitamente — son fases posteriores.
2. **Respetar el modo offline-first**: ninguna funcionalidad core (reproducción, CRUD, estadísticas, metadatos, playlists) debe requerir conexión a internet.
3. Usar los tokens de color y tipografía definidos arriba al construir UI en Compose/XML — no inventar paleta nueva.
4. El header y navbar deben construirse como componentes reutilizables/modulares desde el inicio (evitar duplicar layout por pantalla).
5. Al tocar el campo `fuente`/`fuente secundaria`, recordar que es un campo custom (no estándar de ID3), así que el modelo de datos debe contemplarlo explícitamente.
6. Priorizar en este orden salvo indicación contraria: HU1 (CRUD mp3) → HU2 (reproducción background) → HU6 (playlists/favoritos) → HU7 (metadatos, incl. `fuente`) → HU4 (tema/UI) → HU3 (stats) → HU5 (temporizador/shuffle/ecualizador).
7. Cuando el nombre de la app aparezca en código/UI, usar "Yra" como placeholder y dejar un TODO señalando que el nombre final está pendiente de confirmación.
8. Los bocetos de UI (mockups) del PDF original son referencia visual, no especificación pixel-perfect; priorizar consistencia con la paleta/tipografía sobre replicar el mockup exacto.

## 9. Reglas permanentes de theming, navegación e i18n

1. **Fuente única de verdad para el tema.** Ningún composable debe inferir modo claro/oscuro comparando colores manualmente. Usa siempre `LocalYraDarkTheme.current` (definido en `ui/theme/Theme.kt`) o los tokens de `MaterialTheme.colorScheme`.
2. **Cero colores hardcodeados fuera de `ui/theme/`.** Ningún archivo fuera del paquete `theme` debe importar `Light*`/`Dark*` de `Color.kt` directamente. Todo color visible sale de `MaterialTheme.colorScheme.*`.
3. **Verificación obligatoria en ambos modos.** Cualquier pantalla o componente nuevo/modificado debe revisarse mentalmente (o en preview) en modo claro Y oscuro antes de considerarse terminado. Si un componente tiene `@Preview`, agrega variante `@Preview` en modo oscuro cuando sea razonable.
4. **Cero strings hardcodeados en Kotlin.** Todo texto visible en la UI (labels, títulos, mensajes de confirmación, contentDescription) va en `res/values/strings.xml`, en inglés, vía `stringResource(R.string.xxx)`. Ni siquiera texto "temporal" o de debug queda hardcodeado en un `Text(...)`.
5. **Toda pantalla nueva agregada a `YraNavGraph` debe usar las transiciones ya definidas** (o una variante justificada y consistente en estilo/duración) y decidir explícitamente si el header/navbar/MiniSongPlay aplican en esa ruta, siguiendo el patrón de `SONG_PLAY_ROUTE` como ejemplo de pantalla que los excluye.
6. **No dependencias nuevas sin aprobación explícita.** Antes de agregar cualquier librería a `libs.versions.toml`, indícalo en el resumen de cambios y justifica por qué lo existente no alcanza.
7. **Cambios no destructivos.** Nunca borres, renombres ni cambies la firma pública de un archivo/función/ruta como efecto secundario de una tarea no relacionada. Si una tarea requiere ese tipo de cambio, hazlo como paso explícito y menciónalo aparte en el resumen.
8. **Incrementalidad verificable.** Prefiere cambios pequeños que compilen en cada paso sobre una reescritura grande de una sola vez. Si vas a tocar más de un archivo por el mismo motivo, agrúpalos y verifica el conjunto antes de seguir con lo siguiente.
9. **Consistencia con este documento.** Si introduces un patrón nuevo y reutilizable (como el `CompositionLocal` de tema o el objeto de constantes de animación), documéntalo aquí mismo, en la sección correspondiente, para que sea el estándar del proyecto y no algo que solo vive en un commit.
10. **Alcance del MVP sigue vigente.** Estas reglas no cambian el orden de historias de usuario ni el alcance definido en la sección 3 de este documento — son reglas de calidad/consistencia transversales a cualquier historia que se implemente.
11. **Nunca uses `containerColor`/`background` transparente sin `contentColor` explícito.** Cualquier `Surface`, `Scaffold`, `TopAppBar` o `BottomAppBar` que reciba un color de contenedor transparente (para dejar ver un fondo custom) debe especificar también un `contentColor` derivado del `colorScheme` real (nunca dejar que se infiera de `Color.Transparent`), ya que de lo contrario todo `Text()` sin color explícito debajo de ese nodo pierde su adaptación a modo claro/oscuro.
12. **Usa `CircularDurationPicker` para entradas de tiempo visual.** Cuando necesites pedir al usuario una cantidad de minutos o tiempo de forma interactiva (como en temporizadores), usa el componente reutilizable `ui/components/CircularDurationPicker.kt` (basado en Canvas y gestos) en lugar de crear selectores visuales desde cero.
