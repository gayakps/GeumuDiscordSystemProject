package gaya.pe.kr.plugin.player.listener;

import gaya.pe.kr.network.packet.startDirection.client.UpdatePlayerList;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.stream.Collectors;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void syncComplete(PlayerJoinEvent event) {
        NetworkManager.getInstance().sendPacket(new UpdatePlayerList(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList())));
        //TODO 오프라인일 떄 답변이 달린 경우의 대비함
    }

}
