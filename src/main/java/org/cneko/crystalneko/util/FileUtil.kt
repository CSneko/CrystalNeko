package org.cneko.crystalneko.util

import org.cneko.crystalneko.client.music.MusicPlayer
import java.io.File
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.time.Duration
import java.time.LocalTime


class FileUtil {
    companion object{
        fun isExist(file: String) : Boolean{
            return File(file).exists()
        }
        private fun LocalTime.toMillis(): Long = Duration.between(LocalTime.MIN, this).toMillis()

        fun readLyrics(file: String): List<MusicPlayer.LyricLine> {
            val lyricLines = mutableListOf<MusicPlayer.LyricLine>()
            val pattern = Regex("\\[(\\d{1,2}:\\d{2}(\\.\\d{1,3})?)\\]")

            try {
                BufferedReader(FileReader(file)).use { reader ->
                    var currentLine = reader.readLine()
                    var currentTime: LocalTime? = null
                    var currentText = ""

                    while (currentLine != null) {
                        val matchResult = pattern.find(currentLine)
                        if (matchResult != null) {
                            // 如果找到时间标签，先保存上一句歌词（如果有）
                            if (currentTime != null && currentText.isNotBlank()) {
                                lyricLines.add(MusicPlayer.LyricLine(currentTime.toMillis(), currentText.trim()))
                                currentText = "" // 重置当前歌词文本
                            }

                            // 解析时间标签
                            val timeStr = matchResult.groupValues[1]
                            currentTime = LocalTime.parse(timeStr.replace(".", ":"))
                        } else {
                            // 如果没有时间标签，累加到当前歌词文本
                            currentText += " $currentLine"
                        }

                        currentLine = reader.readLine()
                    }

                    // 添加最后一句歌词（如果有的话）
                    if (currentTime != null && currentText.isNotBlank()) {
                        lyricLines.add(MusicPlayer.LyricLine(currentTime.toMillis(), currentText.trim()))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return lyricLines

        }
    }
}