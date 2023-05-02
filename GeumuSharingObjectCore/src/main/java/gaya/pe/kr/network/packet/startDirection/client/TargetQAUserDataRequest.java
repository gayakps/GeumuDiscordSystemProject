package gaya.pe.kr.network.packet.startDirection.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.qa.data.QAUser;
import lombok.Getter;
import org.bukkit.entity.Player;


@Getter
public class TargetQAUserDataRequest extends AbstractMinecraftPlayerRequestPacket {

    String[] targetQAUsers;

    public TargetQAUserDataRequest(String[] targetQAUsers, Player player) {
        super(PacketType.TARGET_QA_USER_DATA_REQUEST, player);
        this.targetQAUsers = targetQAUsers;
    }
}
