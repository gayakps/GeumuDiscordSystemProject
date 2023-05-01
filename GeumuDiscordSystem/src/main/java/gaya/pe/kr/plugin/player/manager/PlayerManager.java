package gaya.pe.kr.plugin.player.manager;

import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.player.listener.PlayerConnectionListener;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {

    private static class SingleTon {
        private static final PlayerManager PLAYER_MANAGER = new PlayerManager();
    }

    public static PlayerManager getInstance() {
        return SingleTon.PLAYER_MANAGER;
    }

    public void init() {

        GeumuDiscordSystem.registerEvent(new PlayerConnectionListener());

    }

    List<String> playerList = new ArrayList<>();
    public List<String> getPlayerList() {
        return new ArrayList<>(playerList);
    }

    public void setPlayerList(List<String> playerList) {
        this.playerList = playerList;
    }

}
