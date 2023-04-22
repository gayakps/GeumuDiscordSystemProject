package gaya.pe.kr.network.packet.startDirection.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * 디스코드 인증 요청을 명령어로 통해 전달하는 패킷
 */
@Getter
public class DiscordAuthenticationRequest extends AbstractMinecraftPlayerRequestPacket {

    int authenticationCode;
    public DiscordAuthenticationRequest(UUID playerUUID, String playerName, int authenticationCode) {
        super(PacketType.DISCORD_AUTHENTICATION_REQUEST, playerName, playerUUID);
        this.authenticationCode = authenticationCode;
    }

    public DiscordAuthenticationRequest(Player player, int authenticationCode) {
        super(PacketType.DISCORD_AUTHENTICATION_REQUEST, player);
        this.authenticationCode = authenticationCode;
    }

}
