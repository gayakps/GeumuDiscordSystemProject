package gaya.pe.kr.velocity.minecraft.discord.exception;

public class NonExistPlayerAuthenticationDataException extends RuntimeException  {

    public NonExistPlayerAuthenticationDataException() {
    }

    public NonExistPlayerAuthenticationDataException(String message) {
        super(message);
    }

    public NonExistPlayerAuthenticationDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistPlayerAuthenticationDataException(Throwable cause) {
        super(cause);
    }
}
