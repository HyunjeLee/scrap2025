package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import com.scrap2025.scrap2025.navigation.NavRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _selectedTabRoute = MutableStateFlow(NavRoute.CATEGORY)
    val selectedTabRoute: StateFlow<String> = _selectedTabRoute.asStateFlow()

    fun selectTab(route: String) {
        _selectedTabRoute.value = route
    }
}
