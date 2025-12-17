
package dev.bltucker.lazypizza

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.bltucker.lazypizza.cart.CART_SCREEN_ROUTE
import dev.bltucker.lazypizza.cart.navigateToCart
import dev.bltucker.lazypizza.common.theme.LazyPizzaTheme
import dev.bltucker.lazypizza.home.HOME_SCREEN_ROUTE

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var navController: NavHostController? = null

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val rememberedNavController = rememberNavController()
            navController = rememberedNavController
            val windowSizeClass = calculateWindowSizeClass(this)

            LazyPizzaTheme {
                MainScaffold(
                    navController = rememberedNavController,
                    windowSizeClass = windowSizeClass.widthSizeClass
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController?.handleDeepLink(intent)
    }
}

@Composable
fun MainScaffold(
    navController: NavHostController,
    windowSizeClass: WindowWidthSizeClass
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isWideScreen = windowSizeClass >= WindowWidthSizeClass.Expanded
    // Requirements say > 840dp is wide. WindowWidthSizeClass.Expanded is usually > 840dp.
    // Medium is 600-840. So Wide is Expanded.

    val showNavigation = currentDestination?.route in listOf(
        HOME_SCREEN_ROUTE,
        CART_SCREEN_ROUTE,
        // Add History route later
    )

    Scaffold(
        bottomBar = {
            if (windowSizeClass < WindowWidthSizeClass.Expanded) {
                LazyPizzaNavigationBar(
                    currentDestination = currentDestination,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (windowSizeClass >= WindowWidthSizeClass.Expanded) {
                LazyPizzaNavigationRail(
                    currentDestination = currentDestination,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            AppNavigationGraph(
                navController = navController,
                modifier = Modifier.weight(1f),
                windowSizeClass = windowSizeClass
            )
        }
    }
}

@Composable
fun LazyPizzaNavigationBar(
    currentDestination: androidx.navigation.NavDestination?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == HOME_SCREEN_ROUTE } == true,
            onClick = { onNavigate(HOME_SCREEN_ROUTE) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Menu") },
            label = { Text("Menu") }
        )
        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.route == CART_SCREEN_ROUTE } == true,
            onClick = { onNavigate(CART_SCREEN_ROUTE) },
            icon = {
                // TODO: Add Badge logic here
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
            },
            label = { Text("Cart") }
        )
        NavigationBarItem(
            selected = false, // Placeholder for History
            onClick = { /* TODO */ },
            icon = { Icon(Icons.Default.History, contentDescription = "Orders") },
            label = { Text("orders") },
            enabled = false
        )
    }
}

@Composable
fun LazyPizzaNavigationRail(
    currentDestination: androidx.navigation.NavDestination?,
    onNavigate: (String) -> Unit
) {
    NavigationRail {
        NavigationRailItem(
            selected = currentDestination?.hierarchy?.any { it.route == HOME_SCREEN_ROUTE } == true,
            onClick = { onNavigate(HOME_SCREEN_ROUTE) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Menu") },
            label = { Text("Menu") }
        )
        NavigationRailItem(
            selected = currentDestination?.hierarchy?.any { it.route == CART_SCREEN_ROUTE } == true,
            onClick = { onNavigate(CART_SCREEN_ROUTE) },
            icon = {
                // TODO: Add Badge logic here
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
            },
            label = { Text("Cart") }
        )
        NavigationRailItem(
            selected = false, // Placeholder
            onClick = { /* TODO */ },
            icon = { Icon(Icons.Default.History, contentDescription = "Orders") },
            label = { Text("Orders") },
            enabled = false
        )
    }
}