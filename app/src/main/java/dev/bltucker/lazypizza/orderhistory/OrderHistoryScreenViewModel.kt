package dev.bltucker.lazypizza.orderhistory

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OrderHistoryScreenViewModel @Inject constructor(
    private val modelReducer: OrderHistoryScreenModelReducer
) : ViewModel() {

    @VisibleForTesting
    val mutableModel = MutableStateFlow(modelReducer.createInitialState())
    val observableModel: StateFlow<OrderHistoryScreenModel> = mutableModel

    private var hasStarted = false

    fun onStart() {
        if (hasStarted) {
            return
        }
        hasStarted = true
    }
}
