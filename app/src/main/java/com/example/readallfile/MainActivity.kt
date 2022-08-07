package com.example.readallfile

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.readallfile.utils.DocumentUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    private var list = ArrayList<File>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //LoadFile().execute()
        GlobalScope.launch(Dispatchers.IO){
            list.clear()
            DocumentUtil().scanData(application,list)
            withContext(Dispatchers.Main){
                for (result in list){
                    Log.d("result", "result: ${result}")
                }
            }
        }
    }
}