package gaya.pe.kr.network.packet.global;

import gaya.pe.kr.network.packet.startDirection.client.*;
import gaya.pe.kr.network.packet.startDirection.server.non_response.BroadCastMessage;
import gaya.pe.kr.network.packet.startDirection.server.non_response.ScatterServerPlayers;
import gaya.pe.kr.network.packet.startDirection.server.non_response.StartRewardGiving;
import gaya.pe.kr.network.packet.startDirection.server.non_response.TargetPlayerChat;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.network.packet.startDirection.server.response.ServerOption;
import gaya.pe.kr.qa.answer.packet.client.*;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponse;
import gaya.pe.kr.qa.answer.packet.server.ExpectQuestionAnswerResponse;
import gaya.pe.kr.qa.packet.client.*;
import gaya.pe.kr.qa.packet.server.BukkitAnswerModify;
import gaya.pe.kr.qa.packet.server.BukkitQuestionModify;
import gaya.pe.kr.qa.question.packet.client.PlayerQuestionListByQuestionIdRequest;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.qa.question.packet.client.QuestionModifyRequest;
import gaya.pe.kr.qa.question.packet.client.TargetPlayerGetQuestionRequest;
import lombok.Getter;

import java.util.HashMap;

@Getter
public enum PacketType {

    BROADCAST_MESSAGE(0x01, PacketStartDirection.SERVER, BroadCastMessage.class),
    MINECRAFT_OPTION_RELOAD_REQUEST(0x03, PacketStartDirection.CLIENT, MinecraftOptionReloadRequest.class),

    PLAYER_REQUEST_RESPONSE_AS_OBJECT(0x4, PacketStartDirection.SERVER, AbstractPlayerRequestResponseAsObject.class),
    PLAYER_REQUEST_RESPONSE(0x05, PacketStartDirection.SERVER, AbstractPlayerRequestResponse.class),

    DISCORD_AUTHENTICATION_REQUEST(0x06, PacketStartDirection.CLIENT, DiscordAuthenticationRequest.class),
    PLAYER_TRANSIENT_PROCEEDING_QUESTION_REQUEST(0x07, PacketStartDirection.CLIENT, PlayerTransientProceedingQuestionRequest.class),

    PLAYER_TRANSIENT_PROCEEDING_ANSWER_REQUEST(0x08, PacketStartDirection.CLIENT, PlayerTransientProceedingAnswerRequest.class),

    PLAYER_ANSWER_LIST_BY_ANSWER_ID_REQUEST(0x09, PacketStartDirection.CLIENT, PlayerAnswerListByAnswerIdRequest.class),
    PLAYER_QUESTION_LIST_BY_QUESTION_ID_REQUEST(0x10, PacketStartDirection.CLIENT, PlayerQuestionListByQuestionIdRequest.class),

    SERVER_OPTION(0x11, PacketStartDirection.SERVER, ServerOption.class),

    PLAYER_RECENT_QUESTION_ANSWER_REQUEST(0x11, PacketStartDirection.CLIENT, PlayerRecentQuestionAnswerRequest.class),

    GET_TARGET_PLAYER_ANSWER_REQUEST(0x12, PacketStartDirection.CLIENT, TargetPlayerGetAnswerRequest.class),
    GET_TARGET_PLAYER_QUESTION_REQUEST(0x13, PacketStartDirection.CLIENT, TargetPlayerGetQuestionRequest.class),
    DISCORD_AUTHENTICATION_USER_CONFIRM_REQUEST(0x14, PacketStartDirection.CLIENT, DiscordAuthenticationUserConfirmRequest.class),

    SCATTER_SERVER_PLAYERS(0x15, PacketStartDirection.SERVER, ScatterServerPlayers.class),
    UPDATE_PLAYER_LIST_REQUEST(0x16, PacketStartDirection.CLIENT, UpdatePlayerList.class),

    EXPECT_QUESTION_ANSWER_RESPONSE(0x17, PacketStartDirection.SERVER, ExpectQuestionAnswerResponse.class),

    BUKKIT_ANSWER_MODIFY(0x18, PacketStartDirection.SERVER, BukkitAnswerModify.class),
    BUKKIT_QUESTION_MODIFY(0x19, PacketStartDirection.SERVER, BukkitQuestionModify.class),

    TARGET_QA_USER_DATA_REQUEST(0x20, PacketStartDirection.CLIENT, TargetQAUserDataRequest.class),
    ALL_QA_USER_DATA_REQUEST(0x21, PacketStartDirection.CLIENT, AllQAUserDataRequest.class),

    TARGET_ANSWER_BY_QUESTION_ID_REMOVE_REQUEST(0x22, PacketStartDirection.CLIENT, TargetAnswerByQuestionIdRemoveRequest.class),
    TARGET_QUESTION_REMOVE_REQUEST(0x22, PacketStartDirection.CLIENT, TargetAnswerByQuestionIdRemoveRequest.class),

    TARGET_PLAYER_CHAT(0x23, PacketStartDirection.SERVER, TargetPlayerChat.class),

    TARGET_PLAYER_REMOVE_REWARD_REQUEST(0x24, PacketStartDirection.CLIENT, TargetPlayerRemoveRewardRequest.class),

    PLAYER_REWARD_REQUEST(0x25, PacketStartDirection.CLIENT, PlayerRewardRequest.class),

    ANSWER_MODIFY_REQUEST(0x26, PacketStartDirection.CLIENT, AnswerModifyRequest.class),
    QUESTION_MODIFY_REQUEST(0x27, PacketStartDirection.CLIENT, QuestionModifyRequest.class),

    START_REWARD_GIVING(0x28, PacketStartDirection.SERVER, StartRewardGiving.class),

    UPDATE_QA_USER_REQUEST(0x29, PacketStartDirection.CLIENT, UpdateQAUserRequest.class);



//    PLAYER_TITLE(0x02, PacketStartDirection.SERVER, PlayerTitle.class);

    private final byte id;
    private final PacketStartDirection packetStartDirection;
    private final Class<? extends AbstractMinecraftPacket> clazz;
    private static HashMap<Byte, PacketType> packetTypeHashMap = new HashMap<>();
    private static HashMap<Class<?>, PacketType> classTypeAsPacketTypeHashMap = new HashMap<>();

    static {
        for (PacketType value : PacketType.values()) {
            packetTypeHashMap.put(value.getId(), value);
            classTypeAsPacketTypeHashMap.put(value.getClazz(), value);
        }
    }

    PacketType(int id, PacketStartDirection packetStartDirection, Class<? extends AbstractMinecraftPacket> clazz) {
        this.id = (byte) id;
        this.packetStartDirection = packetStartDirection;
        this.clazz = clazz;
    }

    public static PacketType fromId(byte id) {

        PacketType packetType = packetTypeHashMap.get(id);

        if ( packetType == null ) throw new IllegalArgumentException("알 수 없는 패킷 ID 입니다 : " + id);

        return packetType;

    }

    public static PacketType fromClass(Class<?> clazz) {

        PacketType packetType = classTypeAsPacketTypeHashMap.get(clazz);

        if ( packetType == null ) throw new IllegalArgumentException("알 수 없는 패킷 Class 입니다 : " + clazz.getSimpleName());

        return packetType;

    }

}
