package org.cneko.crystalneko.client;

import net.fabricmc.api.ClientModInitializer;
import org.cneko.crystalneko.client.commands.MusicCommand;

public class CrystalNekoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MusicCommand.init();
    }
}
