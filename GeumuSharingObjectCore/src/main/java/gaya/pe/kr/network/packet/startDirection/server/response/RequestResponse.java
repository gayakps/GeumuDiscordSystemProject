package gaya.pe.kr.network.packet.startDirection.server.response;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RequestResponse extends AbstractPlayerRequestResponseAsObject<Boolean> {
    public RequestResponse(Boolean aBoolean, AbstractMinecraftPlayerRequestPacket abstractMinecraftPlayerRequestPacket) {
        super(aBoolean, abstractMinecraftPlayerRequestPacket.getPlayerUUID(), abstractMinecraftPlayerRequestPacket.getPacketID());
    }
}
