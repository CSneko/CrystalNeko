package org.cneko.crystalneko.client;

import net.fabricmc.api.ClientModInitializer;
import org.cneko.crystalneko.client.commands.MusicCommand;
import org.cneko.crystalneko.client.music.MusicPlayer;
import org.cneko.crystalneko.util.FileUtil;

public class CrystalNekoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MusicCommand.init();
        FileUtil.Companion.createFolder(MusicPlayer.Companion.getMidiPath());
        FileUtil.Companion.createFolder(MusicPlayer.Companion.getMp3Path());
    }
}
