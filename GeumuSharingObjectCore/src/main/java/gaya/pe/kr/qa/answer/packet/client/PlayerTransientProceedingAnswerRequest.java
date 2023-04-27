package gaya.pe.kr.qa.answer.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;

import javax.annotation.Nullable;


@Getter
public class PlayerTransientProceedingAnswerRequest extends AbstractMinecraftPacket {


    RequestType requestType;
    int questionId;
    String answer;
    @Nullable String answerPlayerName;

    long discordUserId = -1;

    public PlayerTransientProceedingAnswerRequest(int questionId, String answer, String answerPlayerName) {
        super(PacketType.PLAYER_TRANSIENT_PROCEEDING_ANSWER_REQUEST);
        requestType = RequestType.IN_GAME;
        this.questionId = questionId;
        this.answer = answer;
        this.answerPlayerName = answerPlayerName;
    }

    public PlayerTransientProceedingAnswerRequest(int questionId, String answer, long discordUserId) {
        super(PacketType.PLAYER_TRANSIENT_PROCEEDING_ANSWER_REQUEST);
        requestType = RequestType.DISCORD;
        this.questionId = questionId;
        this.answer = answer;
        this.discordUserId = discordUserId;
    }

    public enum RequestType {

        DISCORD,
        IN_GAME;

    }


}
