package com.example.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Field

class MainActivity : AppCompatActivity() {
    private val fields: Array<Field> = R.raw::class.java.fields
    private val songsIDs: MutableList<Int> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fields.forEach { field ->
            songsIDs.add(field.getInt(field))
        }

        val listRV = findViewById<RecyclerView>(R.id.listRV)
        listRV.adapter = ListAdapter(this, songsIDs)
        listRV.layoutManager = LinearLayoutManager(this)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}