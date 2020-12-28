package com.example.musicplayer

import android.app.Service
import android.icu.text.CaseMap
import android.icu.util.TimeUnit
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import java.lang.reflect.Field

class Player : AppCompatActivity() {
    private lateinit var playPause: ImageButton
    private lateinit var seekForw: ImageButton
    private lateinit var seekBack: ImageButton
    private lateinit var next: ImageButton
    private lateinit var back: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var title: TextView
    private lateinit var currentTime: TextView
    private lateinit var endTime: TextView

    private lateinit var mediaPlayer: MediaPlayer
    private var handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val fields: Array<Field> = R.raw::class.java.fields
    private val songsTitles: MutableList<String> = mutableListOf()
    private val songsIDs: MutableList<Int> = mutableListOf()
    private var currentSongIndex = -1

    private var startTime = 0
    private var finalTime = 0
    private val forwTime = 5000
    private val backTime = 5000

    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        var songID = intent.getIntExtra("PlayID", -1)

        fields.forEach { field ->
            songsTitles.add(field.name)
            songsIDs.add(field.getInt(field))
        }

        currentSongIndex = songsIDs.indexOf(songID)

        // bind layout
        playPause = findViewById(R.id.playPauseIB)
        seekForw = findViewById(R.id.seekForwIB)
        seekBack = findViewById(R.id.seekBackIB)
        next = findViewById(R.id.nextIB)
        back = findViewById(R.id.backIB)
        seekBar = findViewById(R.id.seekBarSB)
        title = findViewById(R.id.titleTV)
        currentTime = findViewById(R.id.currTimeTV)
        endTime = findViewById(R.id.endTimeTV)

        // play song
        initializeMediaPlayer(songID)
        mediaPlayer.start()
        title.text = songsTitles[currentSongIndex]

        // clickability
        seekBar.isClickable = false
        playPause.setOnClickListener{
            isPlaying = if(isPlaying) {
                mediaPlayer.pause()
                playPause.setImageResource(android.R.drawable.ic_media_play)
                false
            } else {
                mediaPlayer.start()
                playPause.setImageResource(android.R.drawable.ic_media_pause)
                true
            }
        }
        seekForw.setOnClickListener{
            val temp = mediaPlayer.currentPosition
            if(temp+forwTime<=finalTime)
            {
                mediaPlayer.seekTo(temp+forwTime)
                currentTime.text = String.format("%d:%d", mediaPlayer.currentPosition/1000/60,
                        mediaPlayer.currentPosition/1000%60 )
            }
        }
        seekBack.setOnClickListener{
            val temp = mediaPlayer.currentPosition
            if(temp-backTime > 0)
            {
                mediaPlayer.seekTo(temp-backTime)
                currentTime.text = String.format("%d:%d", mediaPlayer.currentPosition/1000/60,
                        mediaPlayer.currentPosition/1000%60 )
            }
        }
        next.setOnClickListener{
            nextSong()
        }
        back.setOnClickListener{
            mediaPlayer.stop()
            mediaPlayer.release()
            currentSongIndex--
            if(currentSongIndex >= 0)
            {
                Log.d("current song", currentSongIndex.toString())
                initializeMediaPlayer(songsIDs[currentSongIndex])
                mediaPlayer.start()
                title.text = songsTitles[currentSongIndex]
            }
            else
            {
                Log.d("next song", "end of list")
                handler.removeCallbacks(runnable)
                currentTime.text = "0:00"
                isPlaying = false
                playPause.setImageResource(android.R.drawable.ic_media_play)
                currentSongIndex++
                initializeMediaPlayer(songsIDs[currentSongIndex])
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //mediaPlayer.pause()
        startTime = mediaPlayer.currentPosition
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.seekTo(startTime)
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        //mediaPlayer.stop()
        //mediaPlayer.release()
        //handler.removeCallbacks(runnable)
        MusicService.startService(this, "playing: "+songsTitles[currentSongIndex])
    }
    private fun nextSong()
    {
        mediaPlayer.stop()
        mediaPlayer.release()
        currentSongIndex++
        if(currentSongIndex < songsIDs.size)
        {
            Log.d("current song", currentSongIndex.toString())
            initializeMediaPlayer(songsIDs[currentSongIndex])
            mediaPlayer.start()
            title.text = songsTitles[currentSongIndex]
        }
        else
        {
            Log.d("next song", "end of list")
            handler.removeCallbacks(runnable)
            currentTime.text = "0:00"
            isPlaying = false
            playPause.setImageResource(android.R.drawable.ic_media_play)
            currentSongIndex--
            initializeMediaPlayer(songsIDs[currentSongIndex])
        }
    }

    private fun initializeMediaPlayer(songID:Int)
    {
        Log.d("initializing with", songID.toString())
        mediaPlayer = MediaPlayer.create(this, songID)
        mediaPlayer.setOnCompletionListener { nextSong() }
        finalTime = mediaPlayer.duration
        startTime = mediaPlayer.currentPosition
        initializeSeekBar()
        endTime.text = String.format("%d:%d", finalTime/1000/60, finalTime/1000%60 )
        currentTime.text = String.format("%d:%d", startTime/1000/60, startTime/1000%60 )
    }

    private fun initializeSeekBar(){
        seekBar.max = finalTime
        seekBar.isClickable = false
        runnable = Runnable {
            seekBar.progress = mediaPlayer.currentPosition
            currentTime.text = String.format("%d:%d", mediaPlayer.currentPosition/1000/60,
                    mediaPlayer.currentPosition/1000%60 )
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }
}