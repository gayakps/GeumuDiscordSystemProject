package gaya.pe.kr.velocity.minecraft.discord.handler.abs;

import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import lombok.Getter;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;


@Getter
public abstract class MessageChannelHandler extends ListenerAdapter {

    private final TextChannel textChannel;

    public MessageChannelHandler(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {

        if ( event.getChannel().getType().equals(ChannelType.TEXT) ) {
            TextChannel paramTextChannel = event.getTextChannel();

            if ( !textChannel.getId().equals(paramTextChannel.getId()) ) return;
            if ( event.getAuthor().isBot() ) return;
            handleEvent(event);
        }


    }

    protected abstract void handleEvent(MessageReceivedEvent event);


}
