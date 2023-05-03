package gaya.pe.kr.network.packet.startDirection.server.response;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.qa.answer.data.Answer;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

@Getter
@ToString
public abstract class AbstractPlayerRequestResponseAsObject<T> extends AbstractMinecraftPacket {

    T t;
    private final UUID requestPlayerUUID;
    private final long requestPacketId;

    public AbstractPlayerRequestResponseAsObject(T t,UUID requestPlayerUUID, long requestPacketId) {
        super(PacketType.PLAYER_REQUEST_RESPONSE_AS_OBJECT);
        this.requestPlayerUUID = requestPlayerUUID;
        this.requestPacketId = requestPacketId;
        this.t = t;
    }


}
