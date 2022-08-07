package com.example.readallfile.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.readallfile.database.dao.FavoriteDao
import com.example.readallfile.database.dao.RecentDao
import com.example.readallfile.database.dbcallback.FavoriteCallback
import com.example.readallfile.database.dbcallback.RecentCallback
import com.example.readallfile.model.FavoriteFile
import com.example.readallfile.model.RecentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Database(
    entities = arrayOf(FavoriteFile::class, RecentFile::class),
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentDao(): RecentDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "musicapp_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    fun getFavoritelist(callback: FavoriteCallback) {
        GlobalScope.launch(Dispatchers.IO) {
            val favorite = favoriteDao().getFavoritelists()
            withContext(Dispatchers.Main) {
                callback.getAllFavorite(favorite)
            }
        }
    }

    fun addFavorite(path: String) {
        favoriteDao().addFavorite(FavoriteFile(path))
    }

    fun deleteFavorite(path: String) {
        favoriteDao().delete(path)
    }

    fun getRecentlist(callback : RecentCallback){
        GlobalScope.launch(Dispatchers.IO){
            val recent = recentDao().getRecentList()
            withContext(Dispatchers.Main){
                callback.getAllRecent(recent)
            }
        }
    }
}

