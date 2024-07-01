package org.cneko.crystalneko.util

import org.cneko.crystalneko.client.music.MusicPlayer
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileUtil {
    companion object{
        fun isExist(file: String) : Boolean{
            return File(file).exists()
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

        // 读取文件内容
        fun readFile(filePath: String): String {
            val file = File(filePath)
            return if (file.exists()) {
                file.readText()
            } else {
                ""
            }
        }
    }
}