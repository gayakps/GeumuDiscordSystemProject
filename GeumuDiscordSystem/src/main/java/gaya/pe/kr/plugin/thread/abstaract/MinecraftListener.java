package gaya.pe.kr.plugin.thread.abstaract;

import gaya.pe.kr.plugin.util.EventUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class MinecraftListener implements Listener {

    Player player;

    protected void init() {
        EventUtil.register(this);
    }

    protected void close() {
        HandlerList.unregisterAll(this);
    }

    public MinecraftListener(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
