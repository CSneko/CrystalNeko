package org.cneko.crystalneko.client.music

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import org.cneko.crystalneko.util.FileUtil

class MusicPlayer(var name: String) {
    data class LyricLine(val timeMillis: Long, val text: String)
    companion object{
        var musicPath: String = "music/"
        var midiPath: String = musicPath + "midi/"
        var mp3Path: String = musicPath + "mp3/"
    }

    var mcPlayer: Player? = null
    fun setMCPlayer(player: Player){
        mcPlayer = player
    }
    fun play(): Boolean {
        // 先停止播放现有的音乐
        MusicThread.stopPlay()
        // 依次判断文件夹下是否有音乐文件
        if (FileUtil.isExist("$midiPath$name.mid")) {
            MusicThread.playMidi("$midiPath$name.mid")
        } else if (FileUtil.isExist("$mp3Path$name.mp3")) {
            MusicThread.playMp3("$mp3Path$name.mp3",this)
        }else{
            return false
        }
        return true
    }
    fun hasLyrics(): Boolean {
        return if (FileUtil.isExist("$midiPath$name.lrc")) {
            true
        } else if (FileUtil.isExist("$mp3Path$name.lrc")) {
            true
        }else{
            false
        }
    }
    fun getLyrics(): List<LyricLine> {
        return if (FileUtil.isExist("$midiPath$name.lrc")) {
            FileUtil.readLyrics("$midiPath$name.lrc")
        } else if (FileUtil.isExist("$mp3Path$name.lrc")) {
            FileUtil.readLyrics("$mp3Path$name.lrc")
        }else{
            emptyList()
        }
    }
    fun showLyrics(lyric : String){
        // 如果有玩家参数,则将歌词发给玩家
        if(mcPlayer!=null){
            mcPlayer?.displayClientMessage(Component.literal(lyric), true)
        }
    }
}
