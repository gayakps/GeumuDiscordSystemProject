package gaya.pe.kr.plugin.util.data;

import gaya.pe.kr.plugin.util.exception.IllegalResponseObjectException;
import gaya.pe.kr.util.data.ConsumerTwoObject;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class WaitingTicket<T> {

    UUID requestUUID;
    T responseObject;
    Player player;

    Class<?> expectResponseClazz;
    ConsumerTwoObject<Player, T> consumerTwoObject;

    public WaitingTicket(Player player, @NotNull UUID requestUUID, ConsumerTwoObject<Player, T> consumerTwoObject, Class<?> expectResponseClazz) {
        this.player = player;
        this.requestUUID = requestUUID;
        this.consumerTwoObject = consumerTwoObject;
        this.expectResponseClazz = expectResponseClazz;
    }

    public synchronized void executeWaitingTicket() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumerTwoObject.accept(player, responseObject);
    }
    
    public synchronized void setResult(T response) throws IllegalResponseObjectException {

        if ( response != null ) {
            this.responseObject = response;

            if ( !response.getClass().getTypeName().equals(expectResponseClazz.getTypeName()) ) {
                throw new IllegalResponseObjectException(String.format("기대 값 : %s 현재 값 : %s 이 서로 일치하지 않습니다", this.expectResponseClazz.getSimpleName(), response.getClass().getSimpleName()));
            }

        }

        notifyAll();
    }


}
