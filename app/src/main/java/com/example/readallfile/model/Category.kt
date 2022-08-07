package com.example.readallfile.model

import java.io.File

data class Category(val title : String, val type : String){
    var list : ArrayList<ItemFile> = ArrayList()

    fun clearData(){
        list.clear()
    }

    fun getList() : List<ItemFile>{
        val listItem = ArrayList<ItemFile>()
        for (itemFile in list){
            val size = File(itemFile.path).length()
            if (size > 0 ){
                listItem.add(itemFile)
            }
        }
        return listItem
    }

    fun addFile(file : File){
        list.add(ItemFile(file.path))
    }

    fun addFile(file : File,time : Long){
        list.add(ItemFile(file.path,time))
    }
}