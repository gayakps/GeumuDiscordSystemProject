package gaya.pe.kr.network.packet.global;

import gaya.pe.kr.network.packet.question.PlayerAnswerRequest;
import gaya.pe.kr.network.packet.question.PlayerQuestionRequest;
import gaya.pe.kr.network.packet.startDirection.client.DiscordAuthenticationRequest;
import gaya.pe.kr.network.packet.startDirection.client.MinecraftOptionReloadRequest;
import gaya.pe.kr.network.packet.startDirection.client.ServerPacketResponse;
import gaya.pe.kr.network.packet.startDirection.server.MinecraftOption;
import gaya.pe.kr.network.packet.startDirection.server.PlayerMessage;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponse;
import lombok.Getter;

import java.util.HashMap;

@Getter
public enum PacketType {

    PLAYER_MESSAGE(0x01, PacketStartDirection.SERVER, PlayerMessage.class),
    SERVER_PACKET_RESPONSE(0x02, PacketStartDirection.CLIENT, ServerPacketResponse.class),
    MINECRAFT_OPTION(0x03, PacketStartDirection.SERVER, MinecraftOption.class),
    MINECRAFT_OPTION_RELOAD_REQUEST(0x04, PacketStartDirection.CLIENT, MinecraftOptionReloadRequest.class),
    PLAYER_REQUEST_RESPONSE(0x05, PacketStartDirection.SERVER, AbstractPlayerRequestResponse.class),
    DISCORD_AUTHENTICATION_REQUEST(0x06, PacketStartDirection.CLIENT, DiscordAuthenticationRequest.class),
    PLAYER_QUESTION_REQUEST(0x07, PacketStartDirection.CLIENT, PlayerQuestionRequest.class),

    PLAYER_ANSWER_REQUEST(0x08, PacketStartDirection.CLIENT, PlayerAnswerRequest.class);


//    PLAYER_TITLE(0x02, PacketStartDirection.SERVER, PlayerTitle.class);

    private final byte id;
    private final PacketStartDirection packetStartDirection;
    private final Class<? extends MinecraftPacket> clazz;
    private static HashMap<Byte, PacketType> packetTypeHashMap = new HashMap<>();
    private static HashMap<Class<?>, PacketType> classTypeAsPacketTypeHashMap = new HashMap<>();

    static {
        for (PacketType value : PacketType.values()) {
            packetTypeHashMap.put(value.getId(), value);
            classTypeAsPacketTypeHashMap.put(value.getClazz(), value);
        }
    }

    PacketType(int id, PacketStartDirection packetStartDirection, Class<? extends MinecraftPacket> clazz) {
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
