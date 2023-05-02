package gaya.pe.kr.qa.question.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TargetQuestionRemoveRequest extends AbstractMinecraftPlayerRequestPacket {

    long questId;
    public TargetQuestionRemoveRequest(long questId, String playerName, UUID playerUUID) {
        super(PacketType.TARGET_QUESTION_REMOVE_REQUEST, playerName, playerUUID);
        this.questId = questId;
    }

}
