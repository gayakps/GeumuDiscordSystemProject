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

        System.out.println("메시지 추가할게요");

        if ( message == null || message.length() == 0 ) return;



        messages.add(message);
        System.out.println("메세지 추가 " + message + " || " + messages);
    }

    @Override
    public void sendData(Player player) {

        if ( messages .isEmpty() ) {
            player.sendMessage("메세지가 없습니다");
        }

        for (String message : messages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

    }


}
