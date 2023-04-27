package gaya.pe.kr.qa.data;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QARequestResult {

    String message;
    Type type = Type.FAIL;

    public QARequestResult() {
    }

    public QARequestResult(Type type) {
        this.type = type;
    }

    public QARequestResult(String message, Type type) {
        this.message = message;
        this.type = type;
    }

    public void clearMessages() {
        this.message = null;
    }

    public enum Type {

        SUCCESS,
        FAIL;

    }

}
