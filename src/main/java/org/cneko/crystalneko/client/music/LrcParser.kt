package org.cneko.crystalneko.client.music

import org.cneko.crystalneko.util.FileUtil

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
        // 寻找歌词开始的[] (包括有多个[]的情况)
        for (line in lines) {
            // 处理歌词文本
            val parsedText :Map<String, String> = parseText(line)
            //　将时间转换为毫秒
            val timeMillis = parseTime(parsedText["time"]!!)
            // 将时间与文本添加到列表中
            if (timeMillis != -1L) {
                lyrics.add(LyricLine(timeMillis, parsedText["text"]!!))
            }
        }
        // 对歌词按照时间排序
        return lyrics.sortedBy { it.timeMillis }
    }

    /**　处理歌词文本
     * @param input 输入的歌词文本，如"[00:01.00]歌词文本"
     * @return 返回一个Map，键为时间，值为歌词文本（Map的键值对数量取决于歌词文本中方括号的数量）
     */
    fun parseText(input: String): Map<String, String> {
        // 正则表达式匹配方括号及其中的内容，以及方括号之间的文本
        val pattern = Regex("\\[(.*?)\\]|(\\w+)")
        val matches = pattern.findAll(input).toList()

        // 处理匹配结果，构建目标格式
        val result = mutableMapOf<String, String>()
        var key: String? = null
        for (match in matches) {
            match.groups.forEach { group ->
                if (group != null) {
                    val value = group.value.trim()
                    if (value.startsWith("[") && value.endsWith("]")) {
                        // 这是键（去除方括号）
                        key = value.drop(1).dropLast(1)
                    } else {
                        // 这是值，与上一个键配对
                        if (key != null) {
                            result[key!!] = value
                            key = null // 重置键，准备下一次匹配
                        }
                    }
                }
            }
        }
        return result
    }

    /**　将时间转换为毫秒
     * 　@param strText 原始时间，形如：00:01.01
     *  @return 转换后的毫秒数
     */
    fun parseTime(strTime: String): Long{
        val pattern = Regex("(\\d+):(\\d+)\\.(\\d+)")
        val match = pattern.find(strTime)
        if (match != null) {
            val minutes = match.groupValues[1].toInt()
            val seconds = match.groupValues[2].toInt()
            val milliseconds = match.groupValues[3].toInt()
            return ((minutes * 60 + seconds) * 1000 + milliseconds).toLong()
        }
        return 0
    }
}