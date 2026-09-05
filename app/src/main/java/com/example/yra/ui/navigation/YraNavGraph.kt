package com.example.yra.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.yra.ui.components.YraHeader
import com.example.yra.ui.components.YraNavbar
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import com.example.yra.ui.theme.DarkBackgroundStart
import com.example.yra.ui.theme.DarkBackgroundEnd
import com.example.yra.ui.theme.LightBackgroundStart
import com.example.yra.ui.theme.LightBackgroundEnd
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yra.YraApplication
import com.example.yra.data.local.YraDatabase
import com.example.yra.data.repository.MediaStoreScanner
import com.example.yra.data.repository.SongRepositoryImpl
import com.example.yra.ui.playback.MiniSongPlay
import com.example.yra.ui.playback.SongPlayScreen
import com.example.yra.ui.playlists.PlaylistDetailScreen
import com.example.yra.ui.playlists.PlaylistsScreen
import com.example.yra.ui.playlists.PlaylistsViewModel
import com.example.yra.ui.playlists.PlaylistsViewModelFactory
import com.example.yra.ui.songs.SongsScreen
import com.example.yra.ui.songs.SongsViewModel
import com.example.yra.ui.songs.SongsViewModelFactory
import com.example.yra.ui.configuration.ConfigurationScreen
import com.example.yra.ui.configuration.ConfigurationViewModel
import com.example.yra.ui.configuration.ConfigurationViewModelFactory
import com.example.yra.ui.lobby.LobbyScreen
import com.example.yra.ui.lobby.LobbyViewModel
import com.example.yra.ui.lobby.LobbyViewModelFactory
import com.example.yra.ui.stats.StatsScreen
import com.example.yra.ui.stats.StatsViewModel
import com.example.yra.ui.stats.StatsViewModelFactory
import com.example.yra.ui.options.OptionsScreen
import com.example.yra.ui.options.OptionsViewModel
import com.example.yra.ui.options.OptionsViewModelFactory
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun YraNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = YraDestinations.LOBBY_ROUTE
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDestination
    
    val showTopBottomBars = currentRoute != YraDestinations.SONG_PLAY_ROUTE

    // Dependencies
    val context = LocalContext.current
    val application = context.applicationContext as YraApplication
    val playbackController = application.playbackController

    val database = remember { YraDatabase.getDatabase(context) }
    val scanner = remember { MediaStoreScanner(context) }
    
    val songRepository = remember { SongRepositoryImpl(database.songDao(), scanner) }
    val playlistRepository = remember { com.example.yra.data.repository.PlaylistRepositoryImpl(database.playlistDao()) }
    val userPreferencesRepository = application.userPreferencesRepository
    
    val songsViewModelFactory = remember { SongsViewModelFactory(songRepository, userPreferencesRepository) }
    val playlistsViewModelFactory = remember { PlaylistsViewModelFactory(playlistRepository) }
    val configurationViewModelFactory = remember { ConfigurationViewModelFactory(userPreferencesRepository, songRepository) }
    val lobbyViewModelFactory = remember { LobbyViewModelFactory(database.statsDao()) }
    val statsViewModelFactory = remember { StatsViewModelFactory(database.statsDao()) }
    val optionsViewModelFactory = remember { OptionsViewModelFactory(application.equalizerManager, application.sleepTimerManager) }
    
    // Playback state
    val isPlaying by playbackController.isPlaying.collectAsState()
    val currentSong by playbackController.currentSong.collectAsState()

    // Global viewmodels for global UI components
    val songsViewModel: SongsViewModel = viewModel(factory = songsViewModelFactory)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val isDarkTheme = com.example.yra.ui.theme.LocalYraDarkTheme.current
    val backgroundBrush = if (isDarkTheme) {
        Brush.verticalGradient(listOf(DarkBackgroundStart, DarkBackgroundEnd))
    } else {
        Brush.verticalGradient(listOf(LightBackgroundStart, LightBackgroundEnd))
    }
    
    val navigateToTopLevelDestination = { route: String ->
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Text("Main Menu", modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configuration") },
                    label = { Text("Configuration") },
                    selected = currentRoute == YraDestinations.CONFIGURATION_ROUTE,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigateToTopLevelDestination(YraDestinations.CONFIGURATION_ROUTE)
                    }
                )
                // TODO: Agregar Stats, Web site, About us
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.background(backgroundBrush),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            topBar = {
                if (showTopBottomBars) {
                    YraHeader(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSearchClick = { /* TODO: Open search */ }
                    )
                }
            },
        bottomBar = {
            if (showTopBottomBars) {
                Column {
                    AnimatedVisibility(
                        visible = currentSong != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        currentSong?.let { song ->
                            MiniSongPlay(
                                song = song,
                                isPlaying = isPlaying,
                                onPlayPauseClick = { playbackController.togglePlayPause() },
                                onToggleFavorite = { songsViewModel.toggleFavorite(song) },
                                onClick = { navController.navigate(YraDestinations.SONG_PLAY_ROUTE) }
                            )
                        }
                    }
                    YraNavbar(
                        currentRoute = currentRoute,
                        onNavigateToRoute = navigateToTopLevelDestination
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { YraMotion.enterTransition },
            exitTransition = { YraMotion.exitTransition },
            popEnterTransition = { YraMotion.popEnterTransition },
            popExitTransition = { YraMotion.popExitTransition }
        ) {
            composable(YraDestinations.LOBBY_ROUTE) {
                val lobbyViewModel: LobbyViewModel = viewModel(factory = lobbyViewModelFactory)
                LobbyScreen(viewModel = lobbyViewModel)
            }
            composable(YraDestinations.SONGS_ROUTE) {
                val playlistsViewModel: PlaylistsViewModel = viewModel(factory = playlistsViewModelFactory)
                SongsScreen(
                    viewModel = songsViewModel, 
                    playbackController = playbackController,
                    playlistsViewModel = playlistsViewModel
                )
            }
            composable(YraDestinations.PLAYLISTS_ROUTE) {
                val playlistsViewModel: PlaylistsViewModel = viewModel(factory = playlistsViewModelFactory)
                PlaylistsScreen(
                    viewModel = playlistsViewModel,
                    onNavigateToDetail = { playlistId ->
                        val route = playlistId?.let { "playlist_detail/$it" } ?: "playlist_detail/favorites"
                        navController.navigate(route)
                    }
                )
            }
            composable("playlist_detail/{playlistId}") { backStackEntry ->
                val idStr = backStackEntry.arguments?.getString("playlistId")
                val pId = if (idStr == "favorites") null else idStr?.toLongOrNull()
                val playlistsViewModel: PlaylistsViewModel = viewModel(factory = playlistsViewModelFactory)
                PlaylistDetailScreen(
                    playlistId = pId,
                    viewModel = playlistsViewModel,
                    playbackController = playbackController
                )
            }
            composable(YraDestinations.STATS_ROUTE) {
                val statsViewModel: StatsViewModel = viewModel(factory = statsViewModelFactory)
                StatsScreen(
                    viewModel = statsViewModel,
                    onSongClick = { song -> playbackController.playSong(song) }
                )
            }
            composable(YraDestinations.OPTIONS_ROUTE) {
                val optionsViewModel: OptionsViewModel = viewModel(factory = optionsViewModelFactory)
                OptionsScreen(viewModel = optionsViewModel)
            }
            composable(YraDestinations.CONFIGURATION_ROUTE) {
                val configurationViewModel: ConfigurationViewModel = viewModel(factory = configurationViewModelFactory)
                ConfigurationScreen(viewModel = configurationViewModel)
            }
            composable(YraDestinations.WEB_SITE_ROUTE) {
                PlaceholderScreen("Web Site Screen")
            }
            composable(YraDestinations.ABOUT_US_ROUTE) {
                PlaceholderScreen("About Us Screen")
            }
            composable(
                route = YraDestinations.SONG_PLAY_ROUTE,
                enterTransition = { YraMotion.modalEnterTransition },
                exitTransition = { YraMotion.modalExitTransition },
                popEnterTransition = { YraMotion.modalEnterTransition },
                popExitTransition = { YraMotion.modalExitTransition }
            ) {
                SongPlayScreen(
                    playbackController = playbackController,
                    onToggleFavorite = { currentSong?.let { songsViewModel.toggleFavorite(it) } },
                    onBackClick = { navController.popBackStack() }
                )
            }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
