package gaya.pe.kr.qa.answer.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 최근 질문에 대한 요청
 */
@Getter
public class PlayerRecentQuestionAnswerRequest extends AbstractMinecraftPlayerRequestPacket {

    String targetPlayerName; // 질문자 이름
    String answerContent; // 답변
    public PlayerRecentQuestionAnswerRequest(String targetPlayerName, String answerContent, Player requestPlayer) {
        super(PacketType.PLAYER_RECENT_QUESTION_ANSWER_REQUEST, requestPlayer);
        this.targetPlayerName = targetPlayerName;
        this.answerContent = answerContent;
    }

}
