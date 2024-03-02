package com.example.musicplayer

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import com.example.musicplayer.databinding.ActivityMainBinding
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    private var totalTime = 0
    private var isPlaying = false


    private var songs = listOf(Song("doraemon", R.drawable.doraemon, R.raw.doraemon),
        Song("kiteretsu", R.drawable.kiteretsu, R.raw.kiteretsu),
        Song("ninjahattori", R.drawable.ninjahattori, R.raw.ninja_hattori),
        Song("pokemon", R.drawable.pokemon, R.raw.pokemon),
        Song("shinchan", R.drawable.shinchan, R.raw.shinchan))
    private var currentSong = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, songs[currentSong].songId)
        binding.songImage.setImageResource(songs[currentSong].songImage)
        binding.songName.text = songs[currentSong].songName
        totalTime = mediaPlayer.duration
//        var t = MediaPlayer.create(this, songs[currentSong].songId).duration

        binding.btnPlayPause.setOnClickListener {
            if(isPlaying) {
                isPlaying = !isPlaying
                mediaPlayer.pause()
                binding.btnPlayPause.setImageResource(R.drawable.play)
            } else {
                isPlaying = !isPlaying
                mediaPlayer.start()
                binding.btnPlayPause.setImageResource(R.drawable.pause)
            }
        }

        binding.btnPrev.setOnClickListener {
            currentSong--
            if(currentSong<0) {
                currentSong = 4
            }

            prevNextBtnAction()
        }
        binding.btnNext.setOnClickListener {
            currentSong++
            if(currentSong>4) {
                currentSong=0
            }

            prevNextBtnAction()
        }

        // song seek bar
        binding.songSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.songSeekBar.max = totalTime

                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
                if(binding.songSeekBar.progress == totalTime) {
                    currentSong++
                    if(currentSong>4) currentSong = 0
                    binding.songImage.setImageResource(songs[currentSong].songImage)
                    binding.songName.text = songs[currentSong].songName
                    startSong(songs[currentSong].songId)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        // change seekbar position according to song
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable{
            override fun run() {
                binding.songSeekBar.progress = mediaPlayer.currentPosition
                handler.postDelayed(this, 1000)
            }
        }, 1000)

        // volume seek Bar
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        binding.volSeekBar.max = maxVolume
        binding.volSeekBar.progress = currVolume

        binding.volSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                val adjustedVolume = (progress / seekBar!!.max.toFloat()) * maxVolume // Adjust based on max volume
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, adjustedVolume.toInt(), 0)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    private fun prevNextBtnAction() {
        mediaPlayer.pause()
        binding.btnPlayPause.setImageResource(R.drawable.pause)
        isPlaying = true
        binding.songImage.setImageResource(songs[currentSong].songImage)
        binding.songName.text = songs[currentSong].songName
        startSong(songs[currentSong].songId)
    }

    private fun startSong(songId: Int) {
        mediaPlayer = MediaPlayer.create(this, songId)
        mediaPlayer.start()
        totalTime = mediaPlayer.duration
    }
}