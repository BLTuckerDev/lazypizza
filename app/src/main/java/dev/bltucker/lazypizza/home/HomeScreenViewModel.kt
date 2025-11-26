package dev.bltucker.lazypizza.home

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val modelReducer: HomeScreenModelReducer
) : ViewModel() {

    @VisibleForTesting
    val mutableModel = MutableStateFlow(modelReducer.createInitialState())
    val observableModel: StateFlow<HomeScreenModel> = mutableModel

    private var hasStarted = false

    fun onStart() {
        if (hasStarted) {
            return
        }
        hasStarted = true

        loadMenuItems()
    }

    private fun loadMenuItems() {
        viewModelScope.launch {
            try {
                val items = repository.getMenuItems()
                mutableModel.update {
                    modelReducer.updateWithMenuItems(it, items)
                }
            } catch (e: Exception) {
                mutableModel.update {
                    modelReducer.updateWithError(it)
                }
            }
        }
    }

    fun onCategorySelected(category: MenuCategory) {
        mutableModel.update {
            modelReducer.updateSelectedCategory(it, category)
        }
    }

    fun onSearchQueryChanged(query: String) {
        mutableModel.update {
            modelReducer.updateSearchQuery(it, query)
        }
    }

    fun onAddToCart(itemId: String) {
        val currentQuantity = mutableModel.value.getQuantity(itemId)
        mutableModel.update {
            modelReducer.updateItemQuantity(it, itemId, currentQuantity + 1)
        }
    }

    fun onIncreaseQuantity(itemId: String) {
        val currentQuantity = mutableModel.value.getQuantity(itemId)
        mutableModel.update {
            modelReducer.updateItemQuantity(it, itemId, currentQuantity + 1)
        }
    }

    fun onDecreaseQuantity(itemId: String) {
        val currentQuantity = mutableModel.value.getQuantity(itemId)
        if (currentQuantity > 0) {
            mutableModel.update {
                modelReducer.updateItemQuantity(it, itemId, currentQuantity - 1)
            }
        }
    }

    fun onRemoveFromCart(itemId: String) {
        mutableModel.update {
            modelReducer.updateItemQuantity(it, itemId, 0)
        }
    }
}
