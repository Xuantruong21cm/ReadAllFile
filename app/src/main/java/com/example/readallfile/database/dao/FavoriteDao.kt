package com.example.readallfile.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.readallfile.model.FavoriteFile

@Dao interface FavoriteDao {
    @Query("SELECT * FROM favorite_table ORDER BY id DESC")
    fun getFavoritelists() : LiveData<List<FavoriteFile>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addFavorite(favoriteFile: FavoriteFile)

    @Query("DELETE FROM favorite_table WHERE path =:path ")
    fun delete(path:String)
}