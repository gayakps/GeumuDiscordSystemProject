package gaya.pe.kr.plugin.util.exception;

public class IllegalResponseObjectException extends Exception{
    public IllegalResponseObjectException() {
    }

    public IllegalResponseObjectException(String message) {
        super(message);
    }

    public IllegalResponseObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalResponseObjectException(Throwable cause) {
        super(cause);
    }
}
