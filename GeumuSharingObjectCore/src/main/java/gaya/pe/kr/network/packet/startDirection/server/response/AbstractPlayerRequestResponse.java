package gaya.pe.kr.network.packet.startDirection.server.response;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@ToString
public abstract class AbstractPlayerRequestResponse extends AbstractMinecraftPacket {

    private final UUID requestPlayerUUID;
    private final long requestPacketId;

    public AbstractPlayerRequestResponse(UUID requestPlayerUUID, long requestPacketId) {
        super(PacketType.PLAYER_REQUEST_RESPONSE);
        this.requestPlayerUUID = requestPlayerUUID;
        this.requestPacketId = requestPacketId;
    }

    public abstract void sendData(Player player);


}
