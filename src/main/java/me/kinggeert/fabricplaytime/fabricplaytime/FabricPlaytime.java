package me.kinggeert.fabricplaytime.fabricplaytime;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;

public class FabricPlaytime implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        ServerTickCallback.EVENT.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {             //gets all players, adds 1 tick of playtime and displays in minutes.
            String uuid = player.getUuidAsString();
            String name = player.getEntityName();
            ScoreboardPlayerScore playtime = scoreboard.getPlayerScore(uuid, scoreboard.getObjective("kg.playtime"));
            ScoreboardPlayerScore playtimeshow = scoreboard.getPlayerScore(name, scoreboard.getObjective("kg.playtimeshow"));
            playtime.setScore(playtime.getScore() + 1);
            playtimeshow.setScore(playtime.getScore() / 1200);
        }
    }
}
