package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.data.local.ScrapDummyData
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScrapRepositoryImpl @Inject constructor() : ScrapRepository {

    // 더미 데이터를 기반으로 MutableStateFlow 생성
    private val _scrapItems = MutableStateFlow<List<ScrapItem>>(
        ScrapDummyData.dummyScrapItems
    )

    override fun getScrapItems(): Flow<Result<List<ScrapItem>>> {
        return _scrapItems.map { items ->
            try {
                Result.Success(items)
            } catch (e: Exception) {
                Result.Error(e, "스크랩 목록 조회 실패")
            }
        }
    }

    override suspend fun getScrapItemById(id: String): Result<ScrapItem> {
        return try {
            val item = _scrapItems.value.find { it.id == id }
            if (item != null) {
                Result.Success(item)
            } else {
                Result.Error(
                    NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."),
                    "스크랩을 찾을 수 없습니다"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 조회 실패")
        }
    }

    override suspend fun addScrapItem(item: ScrapItem): Result<Unit> {
        return try {
            val currentItems = _scrapItems.value.toMutableList()
            currentItems.add(item)
            _scrapItems.value = currentItems
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "스크랩 추가 실패")
        }
    }

    override suspend fun deleteScrapItem(id: String): Result<Unit> {
        return try {
            val currentItems = _scrapItems.value.toMutableList()
            val removed = currentItems.removeIf { it.id == id }
            if (removed) {
                _scrapItems.value = currentItems
                Result.Success(Unit)
            } else {
                Result.Error(
                    NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."),
                    "스크랩을 찾을 수 없습니다"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 삭제 실패")
        }
    }

    override suspend fun updateScrapItem(id: String, memo: String?): Result<Unit> {
        return try {
            val currentItems = _scrapItems.value.toMutableList()
            val index = currentItems.indexOfFirst { it.id == id }
            if (index != -1) {
                currentItems[index] = currentItems[index].copy(memo = memo)
                _scrapItems.value = currentItems
                Result.Success(Unit)
            } else {
                Result.Error(
                    NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."),
                    "스크랩을 찾을 수 없습니다"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 업데이트 실패")
        }
    }

    override suspend fun moveScrapItem(scrapId: String, categoryId: String): Result<Unit> {
        return try {
            val currentItems = _scrapItems.value.toMutableList()
            val index = currentItems.indexOfFirst { it.id == scrapId }
            if (index != -1) {
                currentItems[index] = currentItems[index].copy(categoryId = categoryId)
                _scrapItems.value = currentItems
                Result.Success(Unit)
            } else {
                Result.Error(
                    NoSuchElementException("ID가 $scrapId 인 스크랩을 찾을 수 없습니다."),
                    "스크랩을 찾을 수 없습니다"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 이동 실패")
        }
    }

    override suspend fun toggleFavorite(scrapId: String): Result<Unit> {
        return try {
            val currentItems = _scrapItems.value.toMutableList()
            val index = currentItems.indexOfFirst { it.id == scrapId }
            if (index != -1) {
                currentItems[index] = currentItems[index].copy(
                    isFavorite = !currentItems[index].isFavorite
                )
                _scrapItems.value = currentItems
                Result.Success(Unit)
            } else {
                Result.Error(
                    NoSuchElementException("ID가 $scrapId 인 스크랩을 찾을 수 없습니다."),
                    "스크랩을 찾을 수 없습니다"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "즐겨찾기 토글 실패")
        }
    }
}
