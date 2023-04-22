package gaya.pe.kr.network.packet.startDirection.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class MinecraftOptionReloadRequest extends AbstractMinecraftPlayerRequestPacket {


    protected MinecraftOptionReloadRequest(Player player) {
        super(PacketType.MINECRAFT_OPTION_RELOAD_REQUEST, player);
    }

}
