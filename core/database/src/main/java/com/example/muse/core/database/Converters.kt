package com.example.muse.core.database

import androidx.room.TypeConverter
import com.example.muse.core.model.EventType

class Converters {
    @TypeConverter
    fun fromEventType(type: EventType): String = type.name

    @TypeConverter
    fun toEventType(value: String): EventType = EventType.valueOf(value)
}