package org.cneko.crystalneko.util

import java.io.File

class FileUtil {
    companion object{
        fun isExist(file: String) : Boolean{
            return File(file).exists()
        }
    }
}