package gaya.pe.kr.network.packet.startDirection.client;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;

import java.util.UUID;

/**
 * 디스코드 인증 요청을 명령어로 통해 전달하는 패킷
 */
@Getter
public class DiscordAuthenticationRequest extends MinecraftPacket {

    UUID playerUUID;
    String playerName;
    int authenticationCode;
    public DiscordAuthenticationRequest(UUID playerUUID, String playerName, int authenticationCode) {
        super(PacketType.DISCORD_AUTHENTICATION_REQUEST);
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.authenticationCode = authenticationCode;
    }

}
