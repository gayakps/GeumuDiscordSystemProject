package gaya.pe.kr.qa.answer.packet.server;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Request 날리고 Response 가 돌아왔을때 ㅇㅇ
 */
@Getter
public class ExpectQuestionAnswerResponse extends AbstractMinecraftPacket {

    QAUser targetUser;
    QAUser answerUser;
    Question question;
    Answer answer;

    public ExpectQuestionAnswerResponse(@NotNull QAUser targetUser, @NotNull QAUser answerUser, @NotNull Question question, @NotNull Answer answer) {
        super(PacketType.EXPECT_QUESTION_ANSWER_RESPONSE);
        this.targetUser = targetUser;
        this.answerUser = answerUser;
        this.question = question;
        this.answer = answer;
    }
}
