package gaya.pe.kr.network.packet.startDirection.server.non_response;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;

public class StartRewardGiving extends AbstractMinecraftPacket {
    public StartRewardGiving() {
        super(PacketType.START_REWARD_GIVING);
    }
}
