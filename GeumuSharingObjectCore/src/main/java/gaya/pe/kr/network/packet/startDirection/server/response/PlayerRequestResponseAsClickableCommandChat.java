package gaya.pe.kr.network.packet.startDirection.server.response;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerRequestResponseAsClickableCommandChat extends AbstractPlayerRequestResponse {

    private final String command;
    private final String chat;

    public PlayerRequestResponseAsClickableCommandChat(UUID requestPlayerUUID, long requestPacketId, String command, String chat) {
        super(requestPlayerUUID, requestPacketId);
        this.command = command;
        this.chat = chat;
    }

    @Override
    public void sendData(Player player) {
        TextComponent textComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', chat));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        player.spigot().sendMessage(textComponent);
    }
}
