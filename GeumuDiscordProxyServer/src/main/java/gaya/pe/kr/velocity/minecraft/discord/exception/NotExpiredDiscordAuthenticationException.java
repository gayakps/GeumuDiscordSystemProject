package gaya.pe.kr.velocity.minecraft.discord.exception;

public class NotExpiredDiscordAuthenticationException extends Exception {

    public NotExpiredDiscordAuthenticationException() {
    }

    public NotExpiredDiscordAuthenticationException(String message) {
        super(message);
    }

    public NotExpiredDiscordAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExpiredDiscordAuthenticationException(Throwable cause) {
        super(cause);
    }
}
