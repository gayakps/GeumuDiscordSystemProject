package gaya.pe.kr.network.packet.bound.client;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;

/**
 * 디스코드 인증 요청을 명령어로 통해 전달하는 패킷
 */
@Getter
public class DiscordAuthenticationRequest extends MinecraftPacket {

    String playerName;
    int authenticationCode;

    public DiscordAuthenticationRequest(String playerName, int authenticationCode) {
        super(PacketType.DISCORD_AUTHENTICATION_REQUEST);
        this.playerName = playerName;
        this.authenticationCode = authenticationCode;
    }


}
