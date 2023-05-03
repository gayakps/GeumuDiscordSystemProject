package gaya.pe.kr.qa.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.qa.data.QAUser;
import lombok.Getter;

@Getter
public class UpdateQAUserRequest extends AbstractMinecraftPacket {

    QAUser[] qaUsers;

    public UpdateQAUserRequest(QAUser[] qaUsers) {
        super(PacketType.UPDATE_QA_USER_REQUEST);
        this.qaUsers = qaUsers;
    }

}
