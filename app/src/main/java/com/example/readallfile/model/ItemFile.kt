package com.example.readallfile.model

import java.io.Serializable
 class ItemFile : Serializable{
     var path : String = ""
     var uri : String = ""
     var time : Long = 0

     constructor(path: String, uri: String) {
         this.path = path
         this.uri = uri
     }

     constructor(path: String) {
         this.path = path
     }

     constructor(path: String, time: Long) {
         this.path = path
         this.time = time
     }



 }