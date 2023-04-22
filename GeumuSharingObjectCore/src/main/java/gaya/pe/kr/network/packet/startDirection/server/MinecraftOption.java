package gaya.pe.kr.network.packet.startDirection.server;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.util.option.data.abs.AbstractOption;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MinecraftOption extends AbstractMinecraftPacket {

    AbstractOption optionData;

    protected MinecraftOption(AbstractOption abstractOption) {
        super(PacketType.MINECRAFT_OPTION);
        this.optionData = abstractOption;
    }



}
