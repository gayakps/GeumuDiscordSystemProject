package gaya.pe.kr.qa.answer.packet.server;

import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.qa.answer.data.Answer;

import java.util.UUID;

public class AnswerListResponse extends AbstractPlayerRequestResponseAsObject<Answer[]> {

    public AnswerListResponse(Answer[] answers, UUID requestPlayerUUID, long requestPacketId) {
        super(answers, requestPlayerUUID, requestPacketId);
    }

}
