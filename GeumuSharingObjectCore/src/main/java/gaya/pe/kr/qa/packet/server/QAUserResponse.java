package gaya.pe.kr.qa.packet.server;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.qa.data.QAUser;
import lombok.Getter;

import java.util.UUID;

@Getter
public class QAUserResponse extends AbstractPlayerRequestResponseAsObject<QAUser[]> {

    QAUser[] qaUser;

    public QAUserResponse(QAUser[] qaUsers, UUID requestPlayerUUID, long requestPacketId) {
        super(qaUsers, requestPlayerUUID, requestPacketId);
    }

}
