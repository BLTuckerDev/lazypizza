package dev.bltucker.lazypizza

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.bltucker.lazypizza.cart.cartScreen
import dev.bltucker.lazypizza.home.HOME_SCREEN_ROUTE
import dev.bltucker.lazypizza.home.homeScreen
import dev.bltucker.lazypizza.orderhistory.orderHistoryScreen
import dev.bltucker.lazypizza.productdetails.navigateToProductDetails
import dev.bltucker.lazypizza.productdetails.productDetailsScreen

@Composable
fun AppNavigationGraph(
    navController: NavHostController,
    windowSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HOME_SCREEN_ROUTE,
        modifier = modifier
    ) {
        homeScreen(
            windowSizeClass = windowSizeClass,
            onNavigateToProductDetails = { productId ->
                navController.navigateToProductDetails(productId)
            }
        )

        productDetailsScreen(
            onNavigateBack = {
              navController.popBackStack()
            }
        )


        cartScreen(
            windowSizeClass = windowSizeClass,
            onNavigateToMenu = {
                navController.popBackStack()
            }
        )

        orderHistoryScreen(
            onNavigateToSignIn = {
            }
        )
    }
}
