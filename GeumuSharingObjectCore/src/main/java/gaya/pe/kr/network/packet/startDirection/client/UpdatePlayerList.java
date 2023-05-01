package gaya.pe.kr.network.packet.startDirection.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UpdatePlayerList extends AbstractMinecraftPacket {

    List<String> playerList = new ArrayList<>();

    public UpdatePlayerList(List<String> playerList) {
        super(PacketType.UPDATE_PLAYER_LIST_REQUEST);
        this.playerList = playerList;
    }
}
