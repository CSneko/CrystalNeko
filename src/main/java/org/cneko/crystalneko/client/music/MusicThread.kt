package org.cneko.crystalneko.client.music

import org.cneko.crystalneko.util.ThreadExecutor
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import javax.sound.midi.MidiSystem
import javazoom.jl.player.Player
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class MusicThread {
    companion object {
        val musicThreadExecutor = MusicThreadExecutor()
        fun isPlaying(): Boolean {
            return musicThreadExecutor.isPlaying.get()
        }
        fun stopPlay() {
            musicThreadExecutor.stopPlay()
        }
        fun playMidi(file: String) {
            musicThreadExecutor.playMidi(file)
        }
        fun playMp3(file: String, player: MusicPlayer) {
            musicThreadExecutor.playMp3(file,player)
        }
    }

class MusicThreadExecutor : ThreadExecutor("MusicThread"){
    var isPlaying = AtomicBoolean(false)
    private var mp3PlayerRef = AtomicReference<Player?>(null)
    var midiSequencer = MidiSystem.getSequencer()
    private var mp3Player: Player? = null
    val shouldDisplayLyrics = AtomicBoolean(true)
    fun playMidi(file: String){
        isPlaying.set(true)
        midiSequencer.open()
        val `is`: InputStream = BufferedInputStream(FileInputStream(File(file)))
        midiSequencer.setSequence(`is`)
        midiSequencer.start()
        // 播放完成后设置状态
        midiSequencer.addMetaEventListener {
            isPlaying.set(false)
        }
    }
    fun playMp3(file: String,p: MusicPlayer){
        stopPlay()
        Thread {
            try {
                val inputStream = BufferedInputStream(FileInputStream(File(file)))
                val player = Player(inputStream)
                isPlaying.set(true)
                mp3PlayerRef.set(player)
                if(p.hasLyrics()){
                    shouldDisplayLyrics.set(true)
                    // 创建一个新的线程来处理歌词显示
                    thread {
                        // 获取歌词
                        val lyrics = p.getLyrics()
                        println("歌词组准备")
                        while (shouldDisplayLyrics.get()) {
                            // 获取当前播放时间
                            val currentTime = player.getPosition()
                            // 显示与播放时间100ms内的歌词
                            for (lyric in lyrics) {
                                if (currentTime >= lyric.timeMillis - 100 && currentTime <= lyric.timeMillis + 100) {
                                    p.showLyrics(lyric.text)
                                    //　等待100ms
                                    Thread.sleep(100)
                                    break
                                }
                            }
                        }
                        // 关闭线程
                        shutdown()
                    }
                }
                player.play()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isPlaying.set(false)
                mp3PlayerRef.set(null)
            }
        }.start()
    }
    // 停止播放正在播放的所有音乐
    fun stopPlay(){
        if (midiSequencer.isOpen) {
            if (midiSequencer.isRunning) {
                midiSequencer.stop()
            }
            midiSequencer.close()
        }

        mp3PlayerRef.getAndSet(null)?.let { player ->
            try {
                player.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isPlaying.set(false)
        shouldDisplayLyrics.set(false)
    }
}



}