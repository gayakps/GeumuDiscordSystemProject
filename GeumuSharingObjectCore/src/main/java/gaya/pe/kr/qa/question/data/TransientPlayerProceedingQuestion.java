package gaya.pe.kr.qa.question.data;

import lombok.Getter;

import javax.annotation.Nullable;

/**
 * In-Game Packet 혹은 Discord Message 에 의해 수신된 데이터가 Netty Handler 에 수신이 되면,
 * 그 때 답변의 정보를 확인해서 일시적으로 해당 객체를 이용하고 이후 @see {@link Question} 객체를 활용하여 데이터에 삽입함
 */

@Getter
public class TransientPlayerProceedingQuestion {

    @Nullable String requestPlayerName;
    @Nullable long requestPlayerDiscordUserId = -1;

    String questionContents;


    public TransientPlayerProceedingQuestion(@Nullable String requestPlayerName, String questionContents) {
        this.requestPlayerName = requestPlayerName;
        this.questionContents = questionContents;
    }

    public TransientPlayerProceedingQuestion(long requestPlayerDiscordUserId, String questionContents) {
        this.requestPlayerDiscordUserId = requestPlayerDiscordUserId;
        this.questionContents = questionContents;
    }

}
