package gaya.pe.kr.plugin.player.listener;

import gaya.pe.kr.network.packet.startDirection.client.UpdatePlayerList;
import gaya.pe.kr.plugin.network.manager.NetworkManager;
import gaya.pe.kr.plugin.qa.manager.OptionManager;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.packet.client.PlayerRewardRequest;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.stream.Collectors;

public class PlayerConnectionListener implements Listener {

    @EventHandler
    public void syncComplete(PlayerJoinEvent event) {
        NetworkManager networkManager = NetworkManager.getInstance();
        Player player = event.getPlayer();
        networkManager.sendPacket(new UpdatePlayerList(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList())));
        //TODO 오프라인일 떄 답변이 달린 경우의 대비함
    }

}
