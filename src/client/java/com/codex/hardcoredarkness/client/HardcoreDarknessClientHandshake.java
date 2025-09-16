package com.codex.hardcoredarkness.client;

import com.codex.hardcoredarkness.HardcoreDarknessConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public final class HardcoreDarknessClientHandshake {
    private static final AtomicBoolean CLIENT_REGISTERED = new AtomicBoolean(false);

    private HardcoreDarknessClientHandshake() {
    }

    public static void register() {
        if (!CLIENT_REGISTERED.compareAndSet(false, true)) {
            return;
        }

        ClientLoginNetworking.registerGlobalReceiver(
                HardcoreDarknessConstants.HANDSHAKE_CHANNEL,
                (client, handler, buf, listenerAdder) -> {
                    PacketByteBuf reply = PacketByteBufs.create();
                    reply.writeVarInt(HardcoreDarknessConstants.HANDSHAKE_PROTOCOL);
                    return CompletableFuture.completedFuture(reply);
                }
        );
    }
}
