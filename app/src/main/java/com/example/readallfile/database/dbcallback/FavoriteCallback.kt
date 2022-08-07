package com.example.readallfile.database.dbcallback

import androidx.lifecycle.LiveData
import com.example.readallfile.model.FavoriteFile

interface FavoriteCallback {
    fun getAllFavorite(favoriteFile: LiveData<List<FavoriteFile>>){}
}