package com.example.readallfile.utils

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.readallfile.model.FileMediaItem
import com.example.readallfile.model.Folder
import java.io.File

class DocumentUtil {
    fun scanData(application: Application, list: ArrayList<File>) {

        val arrMedia = ArrayList<FileMediaItem>()
        val arrFolder = ArrayList<Folder>()
        val folders = HashMap<Long, String>()
        val dateMap = HashMap<String, String>()

        val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.RELATIVE_PATH,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.BUCKET_ID,
            )
        else arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns.BUCKET_ID,
        )

        val uri = MediaStore.Files.getContentUri("external")

        application.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                try {
                    val idMedia: Long =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                    val nameMedia: String =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val name = cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                    MediaStore.Files.FileColumns.DISPLAY_NAME
                                )
                            )
                            val index = name.lastIndexOf(".")
                            if (index != -1) {
                                val nameCut = name.substring(0, index)
                                nameCut
                            } else {
                                name
                            }
                        } else cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                MediaStore.Files.FileColumns.TITLE
                            )
                        )

                    val path =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            "sdcard/" + cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                    MediaStore.Files.FileColumns.RELATIVE_PATH
                                )
                            ) + cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                    MediaStore.Files.FileColumns.DISPLAY_NAME
                                )
                            )
                        else cursor.getString(
                            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                        )

                    val pathFolder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        "sdcard/" + cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                MediaStore.Files.FileColumns.RELATIVE_PATH
                            )
                        )
                    else cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                    )

                    val contentUri = Uri.withAppendedPath(uri, "" + idMedia)
                    val folderIdIndex: Int =
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)
                    val folderNameIndex: Int = cursor.getColumnIndexOrThrow(
                        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
                    )
                    val folderId: Long = cursor.getLong(folderIdIndex)
                    val timeModified = File(path).lastModified()

                    val sp = path.split(".")
                    var typeMedia = FileMediaItem.SONG
                    var typeMediaDetail = FileMediaItem.SONG

                    if (!sp.isNullOrEmpty()) {
                        typeMediaDetail = sp[sp.size - 1].trim()
                        typeMedia = if (typeMediaDetail == "mp3") FileMediaItem.SONG
                        else if (typeMediaDetail == "mp4" || typeMediaDetail == "avi") FileMediaItem.VIDEO
                        else if (typeMediaDetail == "wav" || typeMediaDetail == "m4a") FileMediaItem.RECORDING
                        else continue
                    }

                    if (File(path).exists() && File(path).length() > 1000) {
                        if (!folders.containsKey(folderId)) {
                            val folderName: String = cursor.getString(folderNameIndex)
                            val folder = Folder(folderId.toInt(), folderName)
                            folders[folderId] = folderName
                            arrFolder.add(folder)
                        }

                        Log.d("TruongDev", typeMediaDetail)
                        arrMedia.add(
                            FileMediaItem(
                                idDocument = idMedia.toInt(),
                                name = nameMedia,
                                uri = contentUri.toString(),
                                path = path,
                                pathFolder = pathFolder,
                                idFolder = folderId.toInt(),
                                typeMedia = typeMedia,
                                typeMediaDetail = typeMediaDetail,
                                timeCreated = timeModified,
                                timeModified = 0,
                                size = File(path).length() / 1024f / 1024f
                            )
                        )
                        list.add(File(path))
                    }

                } catch (ex: Exception) {
                }
            }
            cursor.close()
            folders.clear()
            dateMap.clear()
            Log.d("dsk1", "arrMedia: ${arrMedia.size}")
        }
    }

    fun renameFile(
        application: Context,
        fileMediaItemLocal: FileMediaItem,
        newName: String
    ): FileMediaItem {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            try {
                val path = fileMediaItemLocal.path.replace(
                    fileMediaItemLocal.name + "." + fileMediaItemLocal.typeMediaDetail,
                    ""
                )
                val type = fileMediaItemLocal.typeMediaDetail
                val name = newName
                val from = File(path, fileMediaItemLocal.name + "." + fileMediaItemLocal.typeMediaDetail)
                val to = File(path, "$name.$type")
                if (from.renameTo(to)) {
                    fileMediaItemLocal.name = name
                    fileMediaItemLocal.path =
                        "$path$name.$type"
                    fileMediaItemLocal.uri = Uri.fromFile(to).toString()

                    MediaScannerConnection.scanFile(
                        application, arrayOf(fileMediaItemLocal.path),
                        null, null
                    )
                    return fileMediaItemLocal
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            try {
                val type = fileMediaItemLocal.typeMediaDetail
                val name = newName
                val from = File(
                    fileMediaItemLocal.pathFolder,
                    fileMediaItemLocal.name + "." + fileMediaItemLocal.typeMediaDetail
                )
                val to = File(fileMediaItemLocal.pathFolder, "$name.$type")
                if (from.renameTo(to)) {
                    fileMediaItemLocal.name = name
                    fileMediaItemLocal.path =
                        fileMediaItemLocal.pathFolder + name + "." + type
                    fileMediaItemLocal.uri = Uri.fromFile(to).toString()

                    MediaScannerConnection.scanFile(
                        application, arrayOf(fileMediaItemLocal.path),
                        null, null
                    )
                    Log.d("HOneHUYAA", fileMediaItemLocal.toString())
                    return fileMediaItemLocal
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else {
            try {
                val type = fileMediaItemLocal.typeMediaDetail
                val name = newName
                val contentResolver: ContentResolver = application.contentResolver
                val contentValues = ContentValues()
                contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, name)
//                val uri = FileProvider.getUriForFile(
//                    application,
//                    BuildConfig.APPLICATION_ID+".provider",
//                    File(documentLocal.path)
//                )
                contentResolver.update(Uri.parse(fileMediaItemLocal.uri), contentValues, null, null)
//                documentLocal.name = name
//                documentLocal.path =
//                    documentLocal.pathFolder + name + "." + type

                MediaScannerConnection.scanFile(
                    application, arrayOf(fileMediaItemLocal.path),
                    null, null
                )
                fileMediaItemLocal.name = newName
                return fileMediaItemLocal
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return fileMediaItemLocal
    }

    private fun getUniqueName(
        newName: String,
        listFileMediaItem: List<FileMediaItem>,
        type: String,
        fileMediaItemLocal: FileMediaItem
    ): String {
        var index = 0
        while (checkName(newName, index, listFileMediaItem, type, fileMediaItemLocal)) {
            index++
        }
        return if (index == 0) newName
        else "$newName($index)"
    }

    private fun checkName(
        newName: String,
        index: Int,
        listFileMediaItem: List<FileMediaItem>,
        type: String,
        fileMediaItemLocal: FileMediaItem,
    ): Boolean {
        listFileMediaItem.find {
            if (index == 0) "${it.name}.${it.typeMediaDetail}" == "$newName.$type"
            else "${it.name}.${it.typeMediaDetail}" == "$newName($index).$type"
        }?.let {
            if (fileMediaItemLocal.idDocument != it.idDocument) {
                return true
            } else return true
        } ?: kotlin.runCatching {
            return false
        }

        return false
    }
}