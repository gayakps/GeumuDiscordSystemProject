package gaya.pe.kr.qa.answer.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class TargetPlayerAnswerRequest extends AbstractMinecraftPlayerRequestPacket {

    String targetPlayerName;

    public TargetPlayerAnswerRequest(String playerName, UUID playerUUID, String targetPlayerName) {
        super(PacketType.TARGET_PLAYER_ANSWER_REQUEST, playerName, playerUUID);
        this.targetPlayerName = targetPlayerName;
    }

    public TargetPlayerAnswerRequest(Player player, String targetPlayerName) {
        super(PacketType.TARGET_PLAYER_ANSWER_REQUEST, player);
        this.targetPlayerName = targetPlayerName;
    }

}
