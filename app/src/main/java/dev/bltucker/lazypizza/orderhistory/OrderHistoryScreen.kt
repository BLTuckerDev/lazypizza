package dev.bltucker.lazypizza.orderhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.bltucker.lazypizza.common.composables.LazyPizzaButton
import dev.bltucker.lazypizza.common.theme.Grey
import dev.bltucker.lazypizza.common.theme.LazyPizzaTheme

const val ORDER_HISTORY_SCREEN_ROUTE = "order_history"

fun NavController.navigateToOrderHistory() {
    navigate(ORDER_HISTORY_SCREEN_ROUTE)
}

fun NavGraphBuilder.orderHistoryScreen(
    onNavigateToSignIn: () -> Unit
) {
    composable(route = ORDER_HISTORY_SCREEN_ROUTE) {
        val viewModel = hiltViewModel<OrderHistoryScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        LifecycleStartEffect(Unit) {
            viewModel.onStart()
            onStopOrDispose { }
        }

        OrderHistoryScreen(
            modifier = Modifier.fillMaxSize(),
            model = model,
            onSignInClick = onNavigateToSignIn
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderHistoryScreen(
    modifier: Modifier = Modifier,
    model: OrderHistoryScreenModel,
    onSignInClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Orders",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        UnauthorizedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            onSignInClick = onSignInClick
        )
    }
}

@Composable
private fun UnauthorizedContent(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Not Signed In",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Please sign in to view your order history",
                style = MaterialTheme.typography.bodyLarge,
                color = Grey,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            LazyPizzaButton(
                text = "Sign In",
                onClick = onSignInClick,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderHistoryScreenPreview() {
    LazyPizzaTheme {
        OrderHistoryScreen(
            model = OrderHistoryScreenModel(),
            onSignInClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderHistoryScreenPreviewDark() {
    LazyPizzaTheme(darkTheme = true) {
        OrderHistoryScreen(
            model = OrderHistoryScreenModel(),
            onSignInClick = {}
        )
    }
}
