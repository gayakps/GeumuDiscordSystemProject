package gaya.pe.kr.qa.question.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;


@Getter
public class TargetPlayerGetQuestionRequest extends AbstractMinecraftPlayerRequestPacket {

    String targetPlayerName;

    public TargetPlayerGetQuestionRequest(String playerName, UUID playerUUID, String targetPlayerName) {
        super(PacketType.GET_TARGET_PLAYER_QUESTION_REQUEST, playerName, playerUUID);
        this.targetPlayerName = targetPlayerName;
    }

    public TargetPlayerGetQuestionRequest(Player player, String targetPlayerName) {
        super(PacketType.GET_TARGET_PLAYER_QUESTION_REQUEST, player);
        this.targetPlayerName = targetPlayerName;
    }


}
