package com.scrap2025.scrap2025.repository

import com.scrap2025.scrap2025.data.local.dao.CategoryDao
import com.scrap2025.scrap2025.data.local.dao.ScrapDao
import com.scrap2025.scrap2025.data.local.entity.ScrapEntity
import com.scrap2025.scrap2025.model.Result
import com.scrap2025.scrap2025.model.ScrapItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScrapRepositoryImpl
@Inject
constructor(private val scrapDao: ScrapDao, private val categoryDao: CategoryDao) :
    ScrapRepository {

    override fun getScrapItems(categoryId: String?): Flow<Result<List<ScrapItem>>> {
        val flow =
            if (categoryId != null) {
                scrapDao.getScrapsByCategoryId(categoryId)
            } else {
                scrapDao.getAllScraps()
            }

        return flow.map { entities ->
            try {
                Result.Success(entities.map { it.toDomainModel() })
            } catch (e: Exception) {
                Result.Error(e, "스크랩 목록 조회 실패")
            }
        }
    }

    override suspend fun getScrapItemById(id: String): Result<ScrapItem> {
        return try {
            val entity = scrapDao.getScrapById(id)
            if (entity != null) {
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error(NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."), "스크랩을 찾을 수 없습니다")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 조회 실패")
        }
    }

    override fun getScrapItemByIdAsFlow(id: String): Flow<Result<ScrapItem>> {
        return scrapDao.getScrapByIdFlow(id).map { entity ->
            if (entity != null) {
                Result.Success(entity.toDomainModel())
            } else {
                Result.Error(NoSuchElementException(), "스크랩을 찾을 수 없습니다")
            }
        }.catch { e -> // 예외 처리도 Flow 흐름 안에서 처리
            emit(Result.Error(e, "스크랩 연동 오류"))
        }
    }

    override suspend fun addScrapItem(item: ScrapItem): Result<Unit> {
        return try {
            scrapDao.insertScrap(ScrapEntity.fromDomainModel(item))
            // 카테고리 카운트 증가
            categoryDao.incrementScrapCount(item.categoryId!!)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "스크랩 추가 실패")
        }
    }

    override suspend fun deleteScrapItem(id: String): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(id)
            if (existing != null) {
                scrapDao.deleteScrap(id)
                // 카테고리 카운트 감소
                categoryDao.decrementScrapCount(existing.categoryId!!)

                Result.Success(Unit)
            } else {
                Result.Error(NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."), "스크랩을 찾을 수 없습니다")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 삭제 실패")
        }
    }

    override suspend fun updateScrapItem(id: String, memo: String?): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(id)
            if (existing != null) {
                scrapDao.updateScrapMemo(id, memo)
                Result.Success(Unit)
            } else {
                Result.Error(NoSuchElementException("ID가 $id 인 스크랩을 찾을 수 없습니다."), "스크랩을 찾을 수 없습니다")
            }
        } catch (e: Exception) {
            Result.Error(e, "스크랩 업데이트 실패")
        }
    }

    override suspend fun moveScrapItem(scrapId: String, categoryId: String): Result<Unit> {
        return try {
            val existing = scrapDao.getScrapById(scrapId)
            if (existing != null) {
                scrapDao.moveScrap(scrapId, categoryId)
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
            val existing = scrapDao.getScrapById(scrapId)
            if (existing != null) {
                scrapDao.updateIsFavorite(scrapId, !existing.isFavorite)
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

    override suspend fun moveScrapsToCategory(fromId: String, toId: String): Result<Unit> {
        return try {
            val movedCount = scrapDao.moveScraps(fromId, toId)
            if (movedCount > 0) {
                categoryDao.updateScrapCount(fromId, -movedCount)
                categoryDao.updateScrapCount(toId, movedCount)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "스크랩 이동 실패")
        }
    }

    override fun getScrapCount(): Flow<Int> = scrapDao.getScrapCount()
}
