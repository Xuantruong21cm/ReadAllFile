package com.example.readallfile.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_table")
data class RecentFile(@ColumnInfo(name = "path") var path: String,
                      @ColumnInfo(name = "time") var time: Long){
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}