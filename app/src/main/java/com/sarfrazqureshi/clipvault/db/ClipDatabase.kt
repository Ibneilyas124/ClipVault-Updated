package com.sarfrazqureshi.clipvault.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromType(type: ClipType): String = type.name

    @TypeConverter
    fun toType(value: String): ClipType = ClipType.valueOf(value)
}

@Database(entities = [ClipItem::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ClipDatabase : RoomDatabase() {
    abstract fun clipDao(): ClipDao

    companion object {
        @Volatile private var INSTANCE: ClipDatabase? = null

        fun getInstance(context: Context): ClipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClipDatabase::class.java,
                    "clipvault.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
