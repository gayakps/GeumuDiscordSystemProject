package gaya.pe.kr.velocity.minecraft.discord.exception;

public class NonExistPlayerAuthenticationDataException extends Exception {

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
