package dev.bltucker.lazypizza.home

import dev.bltucker.lazypizza.common.MenuRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val menuRepository: MenuRepository
) {

    suspend fun getMenuItems(): List<MenuItemDto> {
        return menuRepository.getAllMenuItems()
    }
}
