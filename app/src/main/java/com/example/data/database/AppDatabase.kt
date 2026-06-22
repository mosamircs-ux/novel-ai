package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.NovelDao
import com.example.data.dao.CharacterDao
import com.example.data.dao.EventDao
import com.example.data.dao.DialogueDao
import com.example.data.model.Novel
import com.example.data.model.Character
import com.example.data.model.Event
import com.example.data.model.Dialogue

@Database(
    entities = [Novel::class, Character::class, Event::class, Dialogue::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun novelDao(): NovelDao
    abstract fun characterDao(): CharacterDao
    abstract fun eventDao(): EventDao
    abstract fun dialogueDao(): DialogueDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "novel_weaver_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
