package com.codex.hardcoredarkness;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class HardcoreDarknessHandshake {
    private static final AtomicBoolean SERVER_REGISTERED = new AtomicBoolean(false);

    private HardcoreDarknessHandshake() {
    }

    public static void registerServer() {
        if (!SERVER_REGISTERED.compareAndSet(false, true)) {
            return;
        }

        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeVarInt(HardcoreDarknessConstants.HANDSHAKE_PROTOCOL);
            sender.sendPacket(HardcoreDarknessConstants.HANDSHAKE_CHANNEL, buf);
        });

        ServerLoginNetworking.registerGlobalReceiver(
                HardcoreDarknessConstants.HANDSHAKE_CHANNEL,
                (server, handler, understood, buf, synchronizer, responseSender) -> {
                    if (!understood) {
                        handler.disconnect(Text.literal("You are missing the mod: " + HardcoreDarknessConstants.MOD_NAME));
                        return;
                    }

                    PacketByteBuf payload = Objects.requireNonNull(buf, "Hardcore Darkness handshake payload");
                    int clientVersion = payload.readVarInt();
                    if (clientVersion != HardcoreDarknessConstants.HANDSHAKE_PROTOCOL) {
                        handler.disconnect(Text.literal("Hardcore Darkness version mismatch (server="
                                + HardcoreDarknessConstants.HANDSHAKE_PROTOCOL + ", client=" + clientVersion + ")"));
                    }
                }
        );
    }
}
