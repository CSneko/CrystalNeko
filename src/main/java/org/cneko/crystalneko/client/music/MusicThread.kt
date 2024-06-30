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
        var musicThreadExecutor = MusicThreadExecutor()
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
        Thread {
            try {
                val inputStream = BufferedInputStream(FileInputStream(File(file)))
                val player = Player(inputStream)
                isPlaying.set(true)
                mp3PlayerRef.set(player)
                player.play()
                if(p.hasLyrics()){
                    val lyrics = p.getLyrics()
                    val startTime = System.currentTimeMillis() // 记录播放开始的时间
                    // 创建一个新的线程来处理歌词显示
                    val lyricDisplayThread = thread {
                        lyrics.forEach { lyricLine ->
                            // 计算歌词应该显示的实际时间
                            val displayTime = lyricLine.timeMillis - startTime
                            // 等待直到达到该歌词的显示时间
                            Thread.sleep(displayTime - System.currentTimeMillis())
                            if (shouldDisplayLyrics.get()) {
                                // 把歌词发送到播放器
                                p.showLyrics(lyricLine.text)
                            }else {
                                shutdown()
                            }
                        }
                    }
                }
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
        if (isPlaying.get()) {
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
        }
        shouldDisplayLyrics.set(false)
    }
}



}