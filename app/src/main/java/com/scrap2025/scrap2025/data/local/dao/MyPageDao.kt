package com.scrap2025.scrap2025.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scrap2025.scrap2025.data.local.entity.MyPageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyPageDao {
    @Query("SELECT * FROM mypage WHERE id = 0")
    fun getMyPage(): Flow<MyPageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyPage(myPage: MyPageEntity)

    @Query("DELETE FROM mypage")
    suspend fun clearMyPage()
}
