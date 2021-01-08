package me.kinggeert.fabricplaytime.fabricplaytime;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.server.command.CommandManager.argument;

public class FabricPlaytime implements DedicatedServerModInitializer {

    Map<String, Integer> AFKtime = new HashMap<String, Integer>();
    Map<String, double[]> rotation = new HashMap<String, double[]>();

    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("switchPlaytime")
                .then(argument("target", EntityArgumentType.player()))
                    .executes(ctx -> {
                        return switchCommand(ctx, getPlayer(ctx, "target"));
                    }));
            }));
        ServerTickCallback.EVENT.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
            countAFK(player);
            String uuid = player.getUuidAsString();
            String name = player.getEntityName();
            ScoreboardPlayerScore playtime = scoreboard.getPlayerScore(uuid, scoreboard.getObjective("kg.playtime"));
            ScoreboardPlayerScore playtimeshow = scoreboard.getPlayerScore(name, scoreboard.getObjective("kg.playtimeshow"));
            if (isAFK(player)) playtime.setScore(playtime.getScore() + 1);
            playtimeshow.setScore(playtime.getScore() / 1200);
        }
    }

    private void countAFK(PlayerEntity player) {
        String uuid = player.getUuidAsString();
        double[] rotationList = new double[3];
        rotationList[0] = player.getRotationVector().x;
        rotationList[1] = player.getRotationVector().y;
        rotationList[2] = player.getRotationVector().z;
        if (rotation.get(uuid) == null) {
            rotation.put(uuid, rotationList);
        }
        if (AFKtime.get(uuid) == null) AFKtime.put(uuid, 0);
        double[] oldRotationList = rotation.get(uuid);
        if (oldRotationList[0] == rotationList[0] && oldRotationList[1] == rotationList[1] && oldRotationList[2] == rotationList[2]) {
            AFKtime.put(uuid, AFKtime.get(uuid) + 1);
        }
        else AFKtime.put(uuid, 0);
        rotation.put(uuid, rotationList);
    }

    private boolean isAFK(PlayerEntity player) {
        String uuid = player.getUuidAsString();
        if (AFKtime.get(uuid) == null) AFKtime.put(uuid, 0);
        Integer time = AFKtime.get(uuid);
        if (time >= 6000) {
            player.sendMessage(new LiteralText("AFK"), true);
            return false;
        }
        else return true;
    }

    private int switchCommand(CommandContext<ServerCommandSource> ctx, PlayerEntity target) {
        System.out.println(target.getName());
        return 1;
    }
}
