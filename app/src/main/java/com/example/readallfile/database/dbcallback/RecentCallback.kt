package com.example.readallfile.database.dbcallback

import androidx.lifecycle.LiveData
import com.example.readallfile.model.RecentFile

interface RecentCallback {
    fun getAllRecent(recent : LiveData<List<RecentFile>>){}
}