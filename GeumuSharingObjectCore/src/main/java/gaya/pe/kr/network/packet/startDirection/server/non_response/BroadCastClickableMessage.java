package gaya.pe.kr.network.packet.startDirection.server.non_response;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;

@Getter
public class BroadCastClickableMessage extends AbstractMinecraftPacket {

    String message;
    String hoverMessage;
    String command;

    public BroadCastClickableMessage(String message, String hoverMessage, String command) {
        super(PacketType.BROAD_CAST_CLICKABLE_MESSAGE);
        this.message = message;
        this.hoverMessage = hoverMessage;
        this.command = command;
    }
}
