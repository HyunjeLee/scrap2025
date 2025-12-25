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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ScrapDetailViewModel
@Inject
constructor(
    scrapRepository: ScrapRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val scrapId: String = savedStateHandle.toRoute<ScrapDetail>().scrapId

    val scrapDetailState: StateFlow<Result<ScrapItem>> = scrapRepository
        .getScrapItemByIdAsFlow(scrapId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

}