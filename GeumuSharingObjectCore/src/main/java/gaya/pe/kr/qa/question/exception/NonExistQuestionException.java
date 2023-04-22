package gaya.pe.kr.qa.question.exception;

public class NonExistQuestionException extends RuntimeException {

    public NonExistQuestionException() {
    }

    public NonExistQuestionException(String message) {
        super(message);
    }

    public NonExistQuestionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistQuestionException(Throwable cause) {
        super(cause);
    }
}
