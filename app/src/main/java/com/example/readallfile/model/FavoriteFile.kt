package com.example.readallfile.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class FavoriteFile(@ColumnInfo(name = "path") var path: String) {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}