package gaya.pe.kr.qa.answer.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 최근 질문에 대한 요청
 */
@Getter
public class PlayerRecentQuestionAnswerRequest extends AbstractMinecraftPacket {

    String targetPlayerName; // 질문자 이름
    String answerContent; // 답변
    String playerName; // 요청자 이름
    UUID playerUUID; // 요청자 UUID

    public PlayerRecentQuestionAnswerRequest(String targetPlayerName, String answerContent, String playerName, UUID playerUUID) {
        super(PacketType.PLAYER_RECENT_QUESTION_ANSWER_REQUEST);
        this.targetPlayerName = targetPlayerName;
        this.answerContent = answerContent;
        this.playerName = playerName;
        this.playerUUID = playerUUID;
    }

}
