package ohior.app.askbox

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ohior.app.askbox.model.ScreenModel
import ohior.app.askbox.service.database.DatabaseManager
import ohior.app.askbox.service.database.PrefManager
import ohior.app.askbox.ui.screens.ask_home.AskHomeScreen
import ohior.app.askbox.ui.theme.AskBoxTheme
import ohior.app.askbox.utils.AppConstants
import ohior.app.askbox.utils.Screens

class MainActivity : ComponentActivity() {

    private val screenList = listOf(
        ScreenModel(
            text = "Ask Bot",
            icon = R.drawable.outline_interests_24,
            screen = Screens.AskHome
        ),
        ScreenModel(
            text = "Chat Bot",
            icon = R.drawable.outline_chat_24,
            screen = Screens.ChatBot
        ),
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            AskBoxTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        var showDeletePopup by remember {
                            mutableStateOf(false)
                        }
                        ConfirmDeletePopup(showDeletePopup) { showDeletePopup = !showDeletePopup }
                        TopAppBar(
                            navigationIcon = {
                                TextButton(
                                    onClick = {
                                        AppConstants.isChatModeEnabled =
                                            !AppConstants.isChatModeEnabled
                                        Toast.makeText(
                                            this,
                                            if (AppConstants.isChatModeEnabled) "You Are now on chat mode" else "chat mode disabled",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }) {
                                    Icon(
                                        modifier = Modifier.size(width = 100.dp, height = 50.dp),
                                        painter = painterResource(id = R.drawable.chat_ai),
                                        contentDescription = "app icon",
                                        tint = if (AppConstants.isChatModeEnabled) MaterialTheme.colorScheme.onSurface
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceTint,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            title = {
                                Text(
                                    text = if (AppConstants.isChatModeEnabled) "ChatBot" else stringResource(
                                        id = R.string.app_name
                                    )
                                )
                            },
                            actions = {
                                IconButton(onClick = { showDeletePopup = !showDeletePopup }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_cleaning_services_24),
                                        contentDescription = "clean chat history"
                                    )
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .navigationBarsPadding(),
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        Row {
                            currentDestination?.let {
                                screenList.forEach { screen ->
                                    BottomItemNavigation(
                                        selected = currentDestination.hierarchy.any { it.route == screen.screen.javaClass.canonicalName },
                                        onClick = {
                                            navController.navigate(screen.screen) {
                                                // Pop up to the start destination of the graph to
                                                // avoid building up a large stack of destinations
                                                // on the back stack as users select items
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                // Avoid multiple copies of the same destination when
                                                // re-selecting the same item
                                                launchSingleTop = true
                                                // Restore state when re-selecting a previously selected item
                                                restoreState = true
                                            }
                                        },
                                        label = { Text(text = screen.text) },
                                        icon = {
                                            Icon(
                                                painterResource(screen.icon),
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    AskHomeScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    private fun RowScope.BottomItemNavigation(
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        label: @Composable () -> Unit
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = !selected) { onClick() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            if (selected) label()
        }
    }

    @Composable
    private fun ConfirmDeletePopup(showDeletePopup: Boolean, onDismiss: () -> Unit) {
        if (showDeletePopup) {
            AlertDialog(
                backgroundColor = MaterialTheme.colorScheme.background,
                onDismissRequest = { onDismiss() },
                confirmButton = {
                    TextButton(onClick = {
                        PrefManager.clearAllData()
                        DatabaseManager.deleteAllMessage()
                        onDismiss()
                    }) {
                        Text(text = "Delete")
                    }
                },
                text = { Text(text = "Are you sure you want to delete all chat and query history? Once this is done, it can not be undone") },
                title = { Text(text = "Delete Chat History") })
        }
    }

//    @Composable
//    private fun NavigationComponent(navController: NavHostController, modifier: Modifier) {
//        NavHost(navController, modifier = modifier, startDestination = Screens.AskHome) {
//            composable<Screens.AskHome> {
//                AskHomeScreen(modifier)
//            }
//            composable<Screens.ChatBot> {
//                ChatBotScreen(modifier)
//            }
//        }
//    }
}