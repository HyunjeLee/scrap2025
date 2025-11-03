package com.scrap2025.scrap2025.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddCategoryViewModel : ViewModel() {
    private val _categoryName = MutableStateFlow("")
    val categoryName: StateFlow<String> = _categoryName.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun updateCategoryName(name: String) {
        _categoryName.value = name
    }

    fun validateAndAddCategory(onSuccess: (String) -> Unit): Boolean {
        val name = _categoryName.value.trim()

        return if (name.isEmpty()) {
            _toastMessage.value = "카테고리명을 입력해주세요"
            false
        } else {
            onSuccess(name)
            _categoryName.value = ""
            _toastMessage.value = null
            true
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}
