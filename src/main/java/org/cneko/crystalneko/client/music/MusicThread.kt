package org.cneko.crystalneko.client.music

import org.cneko.crystalneko.util.ThreadExecutor
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import javax.sound.midi.MidiSystem
import javazoom.jl.player.Player

class MusicThread {
    companion object {
        var musicThreadExecutor = MusicThreadExecutor()
        fun isPlaying(): Boolean {
            return musicThreadExecutor.isPlaying
        }
        fun stopPlay() {
            musicThreadExecutor.stopPlay()
        }
        fun playMidi(file: String) {
            musicThreadExecutor.playMidi(file)
        }
        fun playMp3(file: String) {
            musicThreadExecutor.playMp3(file)
        }
    }

class MusicThreadExecutor : ThreadExecutor("MusicThread"){
    var isPlaying: Boolean = false
    var midiSequencer = MidiSystem.getSequencer()
    private var mp3Player: Player? = null

    fun playMidi(file: String){
        isPlaying = true
        midiSequencer.open()
        val `is`: InputStream = BufferedInputStream(FileInputStream(File(file)))
        midiSequencer.setSequence(`is`)
        midiSequencer.start()
        // 播放完成后设置状态
        midiSequencer.addMetaEventListener {
            isPlaying = false
        }
    }
    fun playMp3(file: String){
        try {
            mp3Player = Player(BufferedInputStream(FileInputStream(File(file))))
            isPlaying = true
            mp3Player?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // 停止播放正在播放的所有音乐
    fun stopPlay(){
        if (isPlaying) {
            if (midiSequencer.isOpen) {
                if (midiSequencer.isRunning) {
                    midiSequencer.stop()
                }
                midiSequencer.close()
            }

            mp3Player?.let {
                it.close()
                mp3Player = null
            }
        }
    }
}



}