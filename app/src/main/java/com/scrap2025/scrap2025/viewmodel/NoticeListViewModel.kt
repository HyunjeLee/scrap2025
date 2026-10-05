package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.scrap2025.scrap2025.model.NoticeSummary
import com.scrap2025.scrap2025.repository.ServerNoticeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class NoticeListViewModel
@Inject
constructor(serverNoticeRepository: ServerNoticeRepository) :
    ViewModel() {
    val notices: Flow<PagingData<NoticeSummary>> =
        serverNoticeRepository.getNoticePagingFlow().cachedIn(viewModelScope)
}
