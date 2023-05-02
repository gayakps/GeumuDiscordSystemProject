package gaya.pe.kr.qa.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class TargetPlayerRemoveRewardRequest extends AbstractMinecraftPlayerRequestPacket {

    String targetPlayerName;
    public TargetPlayerRemoveRewardRequest(String targetPlayerName, Player player) {
        super(PacketType.TARGET_PLAYER_REMOVE_REWARD_REQUEST, player);
        this.targetPlayerName = targetPlayerName;
    }

}
