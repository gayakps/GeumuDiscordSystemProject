package gaya.pe.kr.qa.packet.server;

import gaya.pe.kr.network.packet.startDirection.server.response.AbstractPlayerRequestResponseAsObject;
import gaya.pe.kr.qa.data.QuestionAndAnswerMatch;
import lombok.Getter;

import java.util.UUID;

@Getter
public class QAListResponse extends AbstractPlayerRequestResponseAsObject<QuestionAndAnswerMatch[]> {
    public QAListResponse(QuestionAndAnswerMatch[] questionAndAnswerMatches, UUID requestPlayerUUID, long requestPacketId) {
        super(questionAndAnswerMatches, requestPlayerUUID, requestPacketId);
    }

}
