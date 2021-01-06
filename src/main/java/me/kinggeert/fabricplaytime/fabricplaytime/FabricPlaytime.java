package me.kinggeert.fabricplaytime.fabricplaytime;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.server.MinecraftServer;

public class FabricPlaytime implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        ServerTickCallback.EVENT.register((MinecraftServer) -> tick(MinecraftServer));
    }

    private void tick(MinecraftServer server) {

    }
}
