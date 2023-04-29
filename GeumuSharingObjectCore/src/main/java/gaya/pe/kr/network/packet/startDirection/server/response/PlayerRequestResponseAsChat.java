package gaya.pe.kr.network.packet.startDirection.server.response;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerRequestResponseAsChat extends AbstractPlayerRequestResponse {

    private List<String> messages = new ArrayList<>();

    public PlayerRequestResponseAsChat(UUID requestPlayerUUID, long requestPacketId, String... messages) {
        super(requestPlayerUUID, requestPacketId);
        this.messages.addAll(Arrays.asList(messages));
    }

    public PlayerRequestResponseAsChat(UUID requestPlayerUUID, long requestPacketId, List<String> messages) {
        super(requestPlayerUUID, requestPacketId);
        this.messages = messages;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    @Override
    public void sendData(Player player) {

        for (String message : messages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

    }


}
