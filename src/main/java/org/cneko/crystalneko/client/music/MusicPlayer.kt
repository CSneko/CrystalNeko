package org.cneko.crystalneko.client.music

import org.cneko.crystalneko.util.FileUtil

class MusicPlayer(var name: String) {
    companion object{
        var musicPath: String = "music/"
        var midiPath: String = musicPath + "midi/"
        var mp3Path: String = musicPath + "mp3/"
    }
    fun play(): Boolean {
        // 先停止播放现有的音乐
        MusicThread.stopPlay()
        // 依次判断文件夹下是否有音乐文件
        if (FileUtil.isExist(midiPath + name+".mid")) {
            MusicThread.playMidi(midiPath + name)
        } else if (FileUtil.isExist(mp3Path + name+".mp3")) {
            MusicThread.playMp3(mp3Path + name)
        }else{
            return false
        }
        return true
    }
}
