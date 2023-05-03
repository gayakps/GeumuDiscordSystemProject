package gaya.pe.kr.qa.question.data;


import gaya.pe.kr.qa.data.QAUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long id; // 질문 ID
    @NotNull private final QAUser qaUser;
    @NotNull private final String contents; // 질문 내용
    @NotNull private Date questionDate = new Date();
    long discordMessageId; // 디스코드 메세지 번호
    boolean answer; // 답장을 했는지 안했는지

    public Question(long id, @NotNull String contents, @NotNull QAUser qaUser) {
        this.id = id;
        this.contents = contents;
        this.qaUser = qaUser;
    }

    public Question(long id, @NotNull QAUser qaUser, @NotNull String contents, @NotNull Date questionDate, long discordMessageId, boolean answer) {
        this.id = id;
        this.qaUser = qaUser;
        this.contents = contents;
        this.questionDate = questionDate;
        this.discordMessageId = discordMessageId;
        this.answer = answer;
    }
}
