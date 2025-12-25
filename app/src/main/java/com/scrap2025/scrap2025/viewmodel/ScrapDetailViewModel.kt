package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import com.scrap2025.scrap2025.navigation.ScrapDetail
import com.scrap2025.scrap2025.repository.ScrapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScrapDetailViewModel
@Inject
constructor(
    private val scrapRepository: ScrapRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val scrapId: String = savedStateHandle.toRoute<ScrapDetail>().scrapId

    private val _scrapDetailState = MutableStateFlow<Result<ScrapItem>>(Result.Loading)
    val scrapDetailState: StateFlow<Result<ScrapItem>> = _scrapDetailState.asStateFlow()

    init {
        fetchScrapDetail(scrapId)
    }

    private fun fetchScrapDetail(scrapId: String) {
        viewModelScope.launch {
            val result = scrapRepository.getScrapItemById(scrapId)

            _scrapDetailState.value = result
        }
    }

}