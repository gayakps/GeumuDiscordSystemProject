package gaya.pe.kr.qa.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import org.bukkit.entity.Player;


@Getter
public class AllQAUserDataRequest extends AbstractMinecraftPlayerRequestPacket {

    public AllQAUserDataRequest(Player player) {
        super(PacketType.ALL_QA_USER_DATA_REQUEST, player);
    }

}
