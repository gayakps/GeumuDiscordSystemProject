package gaya.pe.kr.network.packet.startDirection.server.response;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerRequestResponseAsClickableCommandChat extends AbstractPlayerRequestResponse {

    private final String command;
    private final String chat;

    private final String hoverMessage;

    public PlayerRequestResponseAsClickableCommandChat(UUID requestPlayerUUID, long requestPacketId, String command, String chat, String hoverMessage) {
        super(requestPlayerUUID, requestPacketId);
        this.command = command;
        this.chat = chat;
        this.hoverMessage = hoverMessage;
    }

    @Override
    public void sendData(Player player) {
        TextComponent textComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', chat));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverMessage)));
        player.spigot().sendMessage(textComponent);
    }
}
