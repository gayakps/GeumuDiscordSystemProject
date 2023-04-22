package gaya.pe.kr.velocity.minecraft.qa.question.data;


import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Date;

@Getter
public class Question {

    private long id; // 질문 ID
    private String contents; // 질문 내용
    Date date = new Date();
    long discordMessageId; // 디스코드 메세지 번호
    String questionPlayerName; // 질문자 이름
    @Nullable long questionDiscordUserId = -1; // 디스코드 유저 ID

}
