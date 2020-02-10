package com.simplemobiletools.clock

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.PowerManager
import android.util.Log
import java.io.File

class AlarmAudioManager(context: Context) {

    var mediaPlayer: MediaPlayer?

    init {
        val attributes = AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        mediaPlayer?.setAudioAttributes(attributes)
    }

    public fun play(url: String) {
        val isPlaying = mediaPlayer?.isPlaying == true
        if (isPlaying) {
            Log.w("Plat=yer", "Already playing an item, did you mean to play another?")
        }
        if (isPlaying) {
            mediaPlayer?.stop()
        }
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(url)
        try {
            mediaPlayer?.prepareAsync()
        } catch (e: IllegalStateException) {
            Log.e("Plat=yer", "Cannot play the url")
        }
    }

    public fun stop() {
        mediaPlayer?.stop()
    }

    public fun release() {
        if (mediaPlayer != null) {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.reset()
            mediaPlayer?.release()
        }
        mediaPlayer = null
        sharedInstance = null
    }

    companion object {
        var sharedInstance: AlarmAudioManager? = null

        fun getInstance(context: Context): AlarmAudioManager {
            if (sharedInstance == null) {
                sharedInstance = AlarmAudioManager(context)
                clearCache(context)
            }
            return sharedInstance!!
        }

        private fun clearCache(context: Context) {
            try {
                val dir = context.cacheDir
                if (dir != null && dir.isDirectory) {
                    deleteDir(dir)
                }
            } catch (e: Exception) {
                Log.d("Plat=yer", "cannot clear cache")
            }

        }

        private fun deleteDir(dir: File?): Boolean {
            if (dir != null && dir.isDirectory) {
                val children = dir.list()
                for (i in children!!.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
            }
            return dir?.delete() ?: false
        }
    }

}