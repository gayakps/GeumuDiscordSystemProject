package gaya.pe.kr.qa.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import org.bukkit.entity.Player;


@Getter
public class TargetQAUserDataRequest extends AbstractMinecraftPlayerRequestPacket {

    String[] targetQAUsers;
    boolean createdAndReturn;

    public TargetQAUserDataRequest(String[] targetQAUsers, Player player, boolean createdAndReturn) {
        super(PacketType.TARGET_QA_USER_DATA_REQUEST, player);
        this.targetQAUsers = targetQAUsers;
        this.createdAndReturn = createdAndReturn;
    }
}
