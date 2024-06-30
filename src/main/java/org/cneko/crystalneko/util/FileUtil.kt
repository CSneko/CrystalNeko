package org.cneko.crystalneko.util

import org.cneko.crystalneko.client.music.MusicPlayer
import org.cneko.crystalneko.client.music.MusicPlayer.LyricLine
import java.io.File
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class FileUtil {
    companion object{
        fun isExist(file: String) : Boolean{
            return File(file).exists()
        }
        private fun LocalTime.toMillis(): Long = Duration.between(LocalTime.MIN, this).toMillis()

        fun readLyrics(file: String): List<LyricLine> {
            val lyricLines = mutableListOf<LyricLine>()
            val pattern = Regex("\\[(\\d{1,2}:\\d{2}(\\.\\d{1,3})?)\\]")
            val formatter = DateTimeFormatter.ofPattern("mm:ss.SSS")

            try {
                BufferedReader(FileReader(file)).use { reader ->
                    var currentLine = reader.readLine()
                    var currentTimeMillis: Long? = null
                    var currentText = ""

                    while (currentLine != null) {
                        val matchResult = pattern.find(currentLine)
                        if (matchResult != null) {
                            // 如果找到时间标签，先保存上一句歌词（如果有）
                            if (currentTimeMillis != null && currentText.isNotBlank()) {
                                lyricLines.add(LyricLine(currentTimeMillis, currentText.trim()))
                                currentText = "" // 重置当前歌词文本
                            }

                            // 解析时间标签
                            val timeStr = matchResult.groupValues[1].replace(".", ":") // 确保格式统一
                            val time = LocalTime.parse(timeStr, formatter)
                            currentTimeMillis = time.toMillis()
                        } else {
                            // 如果没有时间标签，累加到当前歌词文本
                            currentText += " $currentLine"
                        }

                        currentLine = reader.readLine()
                    }

                    // 添加最后一句歌词（如果有的话）
                    if (currentTimeMillis != null && currentText.isNotBlank()) {
                        lyricLines.add(LyricLine(currentTimeMillis, currentText.trim()))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return lyricLines
        }

        // 扫描文件夹下的所有文件
        fun scanFiles(folderPath: String): List<String> {
            val files = mutableListOf<String>()
            val folder = File(folderPath)

            if (folder.exists() && folder.isDirectory) {
                folder.listFiles()?.forEach { file ->
                    if (file.isFile && file.extension == "mid") {
                        files.add(file.name)
                    }
                }
            }
            return files
        }
        // 扫描midi文件夹下的所有文件
        fun scanMidiFiles(): List<String> {
            var files = scanFiles(MusicPlayer.midiPath)
            // 排除.lrc文件
            files = files.filter { !it.endsWith(".lrc") }
            return files
        }
        // 扫描mp3文件夹下的所有文件
        fun scanMp3Files(): List<String> {
            var files = scanFiles(MusicPlayer.mp3Path)
            // 排除.lrc文件
            files = files.filter { !it.endsWith(".lrc") }
            return files
        }


        // 创建文件夹,如果为多重路径也创建
        fun createFolder(folderPath: String) {
            try {
                val folder: Path = Paths.get(folderPath)
                Files.createDirectories(folder)
                println("Folder created: $folderPath")
            }catch (_: Exception){}
        }
    }
}