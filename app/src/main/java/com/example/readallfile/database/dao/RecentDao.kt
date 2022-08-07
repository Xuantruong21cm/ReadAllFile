package com.example.readallfile.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.readallfile.model.RecentFile

@Dao interface RecentDao {
    @Query("SELECT * FROM recent_table ORDER BY id DESC")
    fun getRecentList() : LiveData<List<RecentFile>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addFileToRecent(recentFile: RecentFile)

    @Query("DELETE  FROM recent_table WHERE path=:path")
    fun delete(path : String)

    @Query("DELETE  FROM recent_table WHERE id=:id")
    fun delete(id : Int)


}