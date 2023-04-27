package gaya.pe.kr.network.packet.global;

import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.network.packet.startDirection.server.response.ServerOption;
import gaya.pe.kr.qa.answer.packet.client.PlayerAnswerListByAnswerIdRequest;
import gaya.pe.kr.qa.answer.packet.client.PlayerTransientProceedingAnswerRequest;
import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.network.packet.startDirection.client.MinecraftOptionReloadRequest;
import gaya.pe.kr.network.packet.startDirection.server.MinecraftOption;
import gaya.pe.kr.network.packet.startDirection.server.PlayerMessage;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponse;
import gaya.pe.kr.qa.question.packet.client.PlayerQuestionListByQuestionIdRequest;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import lombok.Getter;

import java.util.HashMap;

@Getter
public enum PacketType {

    PLAYER_MESSAGE(0x01, PacketStartDirection.SERVER, PlayerMessage.class),
    MINECRAFT_OPTION(0x02, PacketStartDirection.SERVER, MinecraftOption.class),
    MINECRAFT_OPTION_RELOAD_REQUEST(0x03, PacketStartDirection.CLIENT, MinecraftOptionReloadRequest.class),

    PLAYER_REQUEST_RESPONSE_AS_OBJECT(0x4, PacketStartDirection.SERVER, AbstractPlayerRequestResponseAsObject.class),
    PLAYER_REQUEST_RESPONSE(0x05, PacketStartDirection.SERVER, AbstractPlayerRequestResponse.class),

    DISCORD_AUTHENTICATION_REQUEST(0x06, PacketStartDirection.CLIENT, DiscordAuthenticationRequest.class),
    PLAYER_TRANSIENT_PROCEEDING_QUESTION_REQUEST(0x07, PacketStartDirection.CLIENT, PlayerTransientProceedingQuestionRequest.class),

    PLAYER_TRANSIENT_PROCEEDING_ANSWER_REQUEST(0x08, PacketStartDirection.CLIENT, PlayerTransientProceedingAnswerRequest.class),

    PLAYER_ANSWER_LIST_BY_ANSWER_ID_REQUEST(0x09, PacketStartDirection.CLIENT, PlayerAnswerListByAnswerIdRequest.class),
    PLAYER_QUESTION_LIST_BY_QUESTION_ID_REQUEST(0x10, PacketStartDirection.CLIENT, PlayerQuestionListByQuestionIdRequest.class),

    SERVER_OPTION(0x11, PacketStartDirection.SERVER, ServerOption.class);


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
