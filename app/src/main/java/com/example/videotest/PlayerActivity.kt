package com.example.videotest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {

    private var uri: String? = ""
    private var myPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        uri = intent.getStringExtra("uri")
        Log.e("jhjh", "uri : $uri")

        initPlayer()
    }

    private fun initPlayer() {
        val mediaItem = MediaItem.fromUri(uri!!)
        myPlayer = SimpleExoPlayer.Builder(baseContext).build()
        playerView.player = myPlayer
        myPlayer!!.setMediaItem(mediaItem)
        myPlayer!!.prepare()
    }

    override fun onResume() {
        super.onResume()
        myPlayer!!.play()
    }

    override fun onPause() {
        super.onPause()
        myPlayer!!.pause()
    }

}