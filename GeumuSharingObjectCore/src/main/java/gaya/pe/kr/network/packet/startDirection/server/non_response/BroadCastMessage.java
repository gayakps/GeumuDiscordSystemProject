package gaya.pe.kr.network.packet.startDirection.server.non_response;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BroadCastMessage extends AbstractMinecraftPacket {

    List<String> messages = new ArrayList<>();

    public BroadCastMessage(String... messages) {
        super(PacketType.BROADCAST_MESSAGE);
        for (String message : messages) {
            this.messages.add(message.replace("&", "§"));
        }
    }


}
