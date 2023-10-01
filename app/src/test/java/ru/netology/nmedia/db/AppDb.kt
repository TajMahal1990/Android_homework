package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity

@Database(entities = [PostEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var instanse: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instanse ?: synchronized(this) {
                instanse ?: buildDatabase(context).also { instanse = it }
            }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .allowMainThreadQueries()
                .build()
    }
}