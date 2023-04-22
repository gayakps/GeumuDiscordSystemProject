package gaya.pe.kr.qa.question.packet.server;

import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.qa.question.data.Question;

import java.util.UUID;

public class QuestionListResponse extends AbstractPlayerRequestResponseAsObject<Question[] > {

    public QuestionListResponse(Question[] questions, UUID requestPlayerUUID, long requestPacketId) {
        super(questions, requestPlayerUUID, requestPacketId);
    }

}
