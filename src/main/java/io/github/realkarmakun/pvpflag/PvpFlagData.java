package io.github.realkarmakun.pvpflag;

public class PvpFlagData {
    public static boolean getStatus(PersistentEntityData player) {
        final var tagData = player.getPersistentData();
        return tagData.getBoolean("is-up");
    }

    public static boolean switchStatus(PersistentEntityData player) {
        final var tagData = player.getPersistentData();
        final var newState = !tagData.getBoolean("is-up");
        tagData.putBoolean("is-up", newState);
        // sync data
        return newState;
    }
}
