package gaya.pe.kr.qa.answer.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;

import java.util.UUID;


@Getter
public class TargetAnswerByQuestionIdRemoveRequest extends AbstractMinecraftPlayerRequestPacket {
    long questId;
    public TargetAnswerByQuestionIdRemoveRequest(long questId,String playerName, UUID playerUUID) {
        super(PacketType.TARGET_ANSWER_BY_QUESTION_ID_REMOVE_REQUEST, playerName, playerUUID);
        this.questId = questId;
    }


}
