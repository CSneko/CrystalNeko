package org.cneko.crystalneko.client.util;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.cneko.crystalneko.util.FileUtil;

import java.util.List;

public class CommandUtil {
    public static final SuggestionProvider<FabricClientCommandSource> getAllMusic= (context, builder) -> {
        // 获取所有midi文件
        List<String> midiFiles = FileUtil.Companion.scanMidiFiles();
        for (String midiFile : midiFiles) {
            // 删除最后的.midi
            builder.suggest(midiFile.substring(0, midiFile.length() - 5));
        }
        // 获取所有mp3文件
        List<String> mp3Files = FileUtil.Companion.scanMp3Files();
        for (String mp3File : mp3Files) {
            // 删除最后的.mp3
            builder.suggest(mp3File.substring(0, mp3File.length() - 4));
        }
        return builder.buildFuture();
    };
}
