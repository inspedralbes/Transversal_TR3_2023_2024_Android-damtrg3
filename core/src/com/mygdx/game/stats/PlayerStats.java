package com.mygdx.game.stats;

public class PlayerStats {
    private String playerName;
    private int position;
    private float damageReceived;

    public PlayerStats(String playerName, int position, float damageReceived) {
        this.playerName = playerName;
        this.position = position;
        this.damageReceived = damageReceived;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPosition() {
        return position;
    }

    public float getDamageReceived() {
        return damageReceived;
    }
}
