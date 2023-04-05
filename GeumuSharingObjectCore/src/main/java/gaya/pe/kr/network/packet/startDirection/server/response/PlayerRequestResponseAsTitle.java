package gaya.pe.kr.network.packet.startDirection.server.response;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerRequestResponseAsTitle extends AbstractPlayerRequestResponse {

    private final String title;
    private final String subTitle;
    private final short duration;
    private final short fadeIn;
    private final short fadeOut;

    public PlayerRequestResponseAsTitle(UUID requestPlayerUUID, long requestPacketId, String title, String subTitle, short duration, short fadeIn, short fadeOut) {
        super(requestPlayerUUID, requestPacketId);
        this.title = title;
        this.subTitle = subTitle;
        this.duration = duration;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
    }

    @Override
    public void sendData(Player player) {
        player.sendTitle(title, subTitle, fadeIn, duration, fadeOut);
    }
}
