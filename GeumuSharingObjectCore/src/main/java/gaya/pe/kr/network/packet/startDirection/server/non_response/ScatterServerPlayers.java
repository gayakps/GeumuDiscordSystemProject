package gaya.pe.kr.network.packet.startDirection.server.non_response;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class ScatterServerPlayers extends AbstractMinecraftPacket {

    List<String> players = new ArrayList<>();

    public ScatterServerPlayers( List<String> players) {
        super(PacketType.SCATTER_SERVER_PLAYERS);
        this.players = players;
    }
}
