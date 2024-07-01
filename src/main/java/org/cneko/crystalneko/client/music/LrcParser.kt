package org.cneko.crystalneko.client.music

import org.cneko.crystalneko.util.FileUtil
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class LrcParser(val file: String){
    companion object{
        // 数据格式
        data class LyricLine(val timeMillis: Long, val text: String)
    }

    // 解析歌词文件
    fun parse(): List<LyricLine> {
        // 读取歌词文件
        val content = FileUtil.readFile(file)

        //　文件内容为空则返回空列表
        if (content.isEmpty()) {
            return emptyList()
        }

        val lyrics = mutableListOf<LyricLine>()

        // 将歌词按行分割
        val lines = content.split("\n")
        println("开始处理歌词文件:$file")
        // 寻找歌词开始的[] (包括有多个[]的情况)
        for (line in lines) {
            // 处理歌词文本
            val parsedText: Map<String, String> = parseText(line)
            println(parsedText)
            // 遍历map并将时间转换为毫秒
            for ((time, text) in parsedText) {
                // 将时间转换为毫秒
                val timeMillis = parseTime(time)
                // 将时间与文本添加到列表中
                lyrics.add(LyricLine(timeMillis, text))
            }
        }
        println("歌词文件处理完成:$file")
        // 对歌词按照时间排序
        println("结果：$lyrics")
        return lyrics.sortedBy { it.timeMillis }
    }

    /**　处理歌词文本
     * @param input 输入的歌词文本，如"[00:01.00]歌词文本"
     * @return 返回一个Map，键为时间，值为歌词文本（Map的键值对数量取决于歌词文本中方括号的数量）
     */
    fun parseText(input: String): Map<String, String> {
        // 正则表达式匹配时间戳（方括号内的内容）和之后直到下一个时间戳或字符串结束的歌词文本
        val pattern = Pattern.compile("\\[(.*?)\\](.*?)(?=\\[|$)")
        val matcher = pattern.matcher(input)

        val resultMap = mutableMapOf<String, String>()

        // 遍历所有匹配项
        while (matcher.find()) {
            // 第一组匹配是时间戳（去除方括号），第二组是对应的歌词文本，直到下一个时间戳出现或字符串结束
            val time = matcher.group(1).trim() // 时间戳
            val text = matcher.group(2).trim() // 歌词文本

            // 更新时间戳对应的歌词文本，这样如果有连续时间戳，最后一个时间戳会对应正确的歌词文本
            resultMap[time] = text
        }

        return resultMap
    }

    /**
     * 将时间转换为毫秒
     * @param time 原始时间，形如：00:01 或 01:01
     * @return 转换后的毫秒数
     */
    fun parseTime(time: String): Long {
        val (minutes, seconds) = time.split(":").map { it.toInt() }
        return (minutes * 60 * 1000 + seconds * 1000).toLong()
    }
}