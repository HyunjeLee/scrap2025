package com.scrap2025.scrap2025.data.local

import androidx.room.TypeConverter
import com.scrap2025.scrap2025.data.model.SyncStatus
import java.time.LocalDateTime

class Converters {
    @TypeConverter fun toSyncStatus(value: String) = enumValueOf<SyncStatus>(value)

    @TypeConverter fun fromSyncStatus(value: SyncStatus) = value.name

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? = date?.toString()
}
