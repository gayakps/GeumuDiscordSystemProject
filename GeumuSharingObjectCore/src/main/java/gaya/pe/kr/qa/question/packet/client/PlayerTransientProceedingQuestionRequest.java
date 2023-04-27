package gaya.pe.kr.qa.question.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;


@Getter
@ToString
public class PlayerTransientProceedingQuestionRequest extends AbstractMinecraftPacket {


    RequestType requestType;
    @Nullable String playerName;
    @Nullable UUID playerUUID;
    String content;

    long discordUserId;

    public PlayerTransientProceedingQuestionRequest(@NotNull String playerName, @NotNull UUID playerUUID, String content) {
        super(PacketType.PLAYER_TRANSIENT_PROCEEDING_QUESTION_REQUEST);
        requestType = RequestType.IN_GAME;
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.content = content;
    }

    public PlayerTransientProceedingQuestionRequest(long discordUserId, String content) {
        super(PacketType.PLAYER_TRANSIENT_PROCEEDING_QUESTION_REQUEST);
        requestType = RequestType.DISCORD;
        this.discordUserId = discordUserId;
        this.content = content;
    }


    public enum RequestType {

        DISCORD,
        IN_GAME;

    }



}
