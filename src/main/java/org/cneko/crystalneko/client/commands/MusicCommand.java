package org.cneko.crystalneko.client.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.cneko.crystalneko.client.music.MusicPlayer;

import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;
import static net.minecraft.network.chat.Component.translatable;

public class MusicCommand {
    public static void init(){
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("music")
                    .then(argument("music", StringArgumentType.greedyString()))
                            .executes(MusicCommand::playMusic)
            );
        });
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
