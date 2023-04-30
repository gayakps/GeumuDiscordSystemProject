package gaya.pe.kr.qa.question.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;


@Getter
public class TargetPlayerQuestionRequest extends AbstractMinecraftPlayerRequestPacket {

    String targetPlayerName;

    public TargetPlayerQuestionRequest(String playerName, UUID playerUUID, String targetPlayerName) {
        super(PacketType.TARGET_PLAYER_QUESTION_REQUEST, playerName, playerUUID);
        this.targetPlayerName = targetPlayerName;
    }

    public TargetPlayerQuestionRequest(Player player, String targetPlayerName) {
        super(PacketType.TARGET_PLAYER_QUESTION_REQUEST, player);
        this.targetPlayerName = targetPlayerName;
    }


}
