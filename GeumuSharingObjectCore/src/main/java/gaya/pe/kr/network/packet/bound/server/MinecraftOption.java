package gaya.pe.kr.network.packet.bound.server;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.util.option.data.abs.AbstractOption;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MinecraftOption extends MinecraftPacket {

    AbstractOption optionData;

    protected MinecraftOption(AbstractOption abstractOption) {
        super(PacketType.MINECRAFT_OPTION);
        this.optionData = abstractOption;
    }



}
