package dev.bltucker.lazypizza.orderhistory

import javax.inject.Inject

data class OrderHistoryScreenModel(
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class OrderHistoryScreenModelReducer @Inject constructor() {

    fun createInitialState() = OrderHistoryScreenModel(
        isSignedIn = false,
        isLoading = false
    )
}
