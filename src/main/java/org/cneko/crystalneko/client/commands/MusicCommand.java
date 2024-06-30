package org.cneko.crystalneko.client.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.cneko.crystalneko.client.music.MusicPlayer;
import org.cneko.crystalneko.client.music.MusicThread;


import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.FormattedText.composite;

public class MusicCommand {
    public static void init(){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("music")
                    .then(argument("music", StringArgumentType.greedyString())
                            .executes(MusicCommand::playMusic)
                    )
                    .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("stop")
                            .executes(MusicCommand::stopMusic)
                    )
            );
        });
    }

    public static int stopMusic(CommandContext<FabricClientCommandSource> context) {
        MusicThread.Companion.stopPlay();
        context.getSource().sendFeedback(translatable("command.neko.music.stop.successful"));
        return 1;
    }

    public static int playMusic(CommandContext<FabricClientCommandSource> context) {
        String music = StringArgumentType.getString(context, "music");
        // 播放音乐
        MusicPlayer musicPlayer = new MusicPlayer(music);
        boolean result = musicPlayer.play();
        if (result) {
            context.getSource().sendFeedback(translatable("command.neko.music.play.successful"));
        } else {
            context.getSource().sendFeedback(translatable("command.neko.music.play.failed"));
        }
        return 1;
    }
}
