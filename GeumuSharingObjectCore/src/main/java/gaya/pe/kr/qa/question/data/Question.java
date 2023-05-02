package gaya.pe.kr.qa.question.data;


import gaya.pe.kr.qa.data.QAUser;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Getter
@Setter
public class Question {

    private final long id; // 질문 ID
    @NotNull private final QAUser qaUser;
    @NotNull private final String contents; // 질문 내용
    @NotNull private final Date questionDate = new Date();
    long discordMessageId; // 디스코드 메세지 번호
    boolean answer; // 답장을 했는지 안했는지

    public Question(long id, @NotNull String contents, @NotNull QAUser qaUser) {
        this.id = id;
        this.contents = contents;
        this.qaUser = qaUser;
    }
}
