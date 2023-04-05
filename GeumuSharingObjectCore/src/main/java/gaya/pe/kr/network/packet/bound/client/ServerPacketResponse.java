package gaya.pe.kr.network.packet.bound.client;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import lombok.ToString;

import java.util.Random;


@Getter
@ToString
public class ServerPacketResponse extends MinecraftPacket {

    boolean success;
    public ServerPacketResponse() {
        super(PacketType.SERVER_PACKET_RESPONSE);
        Random random = new Random();
        success = random.nextInt(100) < 50;
    }

}
