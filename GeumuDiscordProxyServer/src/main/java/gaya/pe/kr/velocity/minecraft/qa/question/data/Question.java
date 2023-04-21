package gaya.pe.kr.velocity.minecraft.qa.question.data;


import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.util.Date;

@Getter
public class Question {

    private long id; // 질문 ID
    private String contents; // 질문 내용
    Date date = new Date();
    long discordMessageId; // 디스코드 메세지 번호
    String requestPlayerId; // 질문자 이름
    @Nullable String discordUserId; // 디스코드 유저 ID

}
