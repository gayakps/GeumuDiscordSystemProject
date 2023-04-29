package gaya.pe.kr.velocity.minecraft.discord.exception;

public class NonExistPlayerException extends Exception {


    public NonExistPlayerException() {
    }

    public NonExistPlayerException(String message) {
        super(message);
    }

    public NonExistPlayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistPlayerException(Throwable cause) {
        super(cause);
    }
}
