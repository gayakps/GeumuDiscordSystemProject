package gaya.pe.kr.network.packet.startDirection.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * 타겟 플레이어가 인증유저인지 아닌지 확인
 */
@Getter
public class DiscordAuthenticationUserConfirmRequest extends AbstractMinecraftPlayerRequestPacket {

    String targetPlayerName;

    public DiscordAuthenticationUserConfirmRequest(String playerName, UUID playerUUID, String targetPlayerName) {
        super(PacketType.DISCORD_AUTHENTICATION_USER_CONFIRM_REQUEST, playerName, playerUUID);
        this.targetPlayerName = targetPlayerName;
    }

    public DiscordAuthenticationUserConfirmRequest( Player player, String targetPlayerName) {
        super(PacketType.DISCORD_AUTHENTICATION_USER_CONFIRM_REQUEST, player);
        this.targetPlayerName = targetPlayerName;
    }
}
