package gaya.pe.kr.qa.answer.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;


@Getter
public class PlayerTransientProceedingAnswerRequest extends AbstractMinecraftPacket {


    RequestType requestType;
    long questionId;
    String answerContent;
    @Nullable String playerName;
    @Nullable UUID playerUUID;
    long discordUserId = -1;

    public PlayerTransientProceedingAnswerRequest(long questionId, String answerContent, @NotNull String playerName, @NotNull UUID playerUUID) {
        super(PacketType.PLAYER_TRANSIENT_PROCEEDING_ANSWER_REQUEST);
        this.questionId = questionId;
        this.answerContent = answerContent;
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.requestType = RequestType.IN_GAME;
    }

    public PlayerTransientProceedingAnswerRequest(long questionId, String answerContent, long discordUserId) {
        super(PacketType.PLAYER_TRANSIENT_PROCEEDING_ANSWER_REQUEST);
        this.questionId = questionId;
        this.answerContent = answerContent;
        this.discordUserId = discordUserId;
        this.requestType = RequestType.DISCORD;
    }

    public enum RequestType {

        DISCORD,
        IN_GAME;

    }


}
