package com.example.verity;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Общее состояние: кто сейчас превращён в Verity (отдельно для сервера и для клиента). */
public final class VerityState {
    public static final Map<UUID, String> SERVER = new ConcurrentHashMap<>();
    public static final Map<UUID, String> CLIENT = new ConcurrentHashMap<>();

    public static boolean isActive(PlayerEntity player) {
        Map<UUID, String> map = player.getWorld().isClient() ? CLIENT : SERVER;
        return map.containsKey(player.getUuid());
    }

    private VerityState() {}
}
