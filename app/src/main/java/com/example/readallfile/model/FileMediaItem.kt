package com.example.readallfile.model

data class FileMediaItem(
    val idDocument: Int,
    val idFolder: Int,
    var name: String,
    var uri: String,
    var path: String,
    var pathFolder: String,
    var typeMedia: String,
    var typeMediaDetail: String,
    var isNew: Boolean = false,
    var isFavorite: Boolean = false,
    var numberPage: Int = -1,
    var size: Float=0f,
    var timeCreated: Long,
    var timeModified: Long
){
    companion object{
        const val SONG = "song"
        const val VIDEO = "video"
        const val RECORDING = "recording"
    }
}
