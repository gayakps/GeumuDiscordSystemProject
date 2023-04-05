package gaya.pe.kr.network.packet.startDirection.server.response;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerRequestResponseAsChat extends AbstractPlayerRequestResponse {

    private final String[] messages;

    public PlayerRequestResponseAsChat(UUID requestPlayerUUID, long requestPacketId, String... messages) {
        super(requestPlayerUUID, requestPacketId);
        this.messages = messages;
    }

    @Override
    public void sendData(Player player) {

        for (String message : messages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

    }


}
