package org.cneko.crystalneko.client.music

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import org.cneko.crystalneko.util.FileUtil
import org.cneko.crystalneko.client.music.LrcParser.Companion.LyricLine
class MusicPlayer(var name: String) {

    companion object{
        var musicPath: String = "crystalneko/music/"
        var midiPath: String = musicPath + "midi/"
        var mp3Path: String = musicPath + "mp3/"
    }

    var mcPlayer: Player? = null
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
            LrcParser("$midiPath$name.lrc").parse()
        } else if (FileUtil.isExist("$mp3Path$name.lrc")) {
            LrcParser("$mp3Path$name.lrc").parse()
        }else{
            emptyList()
        }
    }
    fun showLyrics(l : String){
        val lyric = "§a♪$l♪"
        // 如果有玩家参数,则将歌词发给玩家
        if(mcPlayer!=null){
            mcPlayer?.displayClientMessage(Component.literal(lyric), true)
        }
    }
}
