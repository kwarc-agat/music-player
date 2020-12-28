package com.example.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val songsIDs: Array<Int> = arrayOf(R.raw.song_01, R.raw.song_02, R.raw.song_03)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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