package gaya.pe.kr.qa.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import org.bukkit.entity.Player;

/**
 * 보상을 요청하는 패킷 ( 게임 진입 시 요청함 )
 */
public class PlayerRewardRequest extends AbstractMinecraftPlayerRequestPacket {
    public PlayerRewardRequest(Player player) {
        super(PacketType.PLAYER_REWARD_REQUEST, player);
    }
}
