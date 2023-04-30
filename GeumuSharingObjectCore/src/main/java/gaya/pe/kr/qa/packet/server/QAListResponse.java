package gaya.pe.kr.qa.packet.server;

import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.QA;
import lombok.Getter;

import java.util.UUID;

@Getter
public class QAListResponse extends AbstractPlayerRequestResponseAsObject<QA[]> {
    public QAListResponse(QA[] qas, UUID requestPlayerUUID, long requestPacketId) {
        super(qas, requestPlayerUUID, requestPacketId);
    }

}
