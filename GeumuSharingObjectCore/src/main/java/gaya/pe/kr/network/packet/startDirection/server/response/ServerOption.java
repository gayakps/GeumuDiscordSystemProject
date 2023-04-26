package gaya.pe.kr.network.packet.startDirection.server.response;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.util.option.data.abs.AbstractOption;
import lombok.Getter;

import java.util.List;

@Getter
public class ServerOption extends AbstractMinecraftPacket {

    List<AbstractOption> abstractOptionList ;

    public ServerOption(List<AbstractOption> abstractOptionList) {
        super(PacketType.SERVER_OPTION);
        this.abstractOptionList = abstractOptionList;
    }


}
