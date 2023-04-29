package gaya.pe.kr.velocity.minecraft.discord.handler;

import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class InitHandler extends ListenerAdapter {

    private final String authChannelID;
    private final String questionChannelID;

    public InitHandler(String authChannelID, String questionChannelID) {
        this.authChannelID = authChannelID;
        this.questionChannelID = questionChannelID;
    }

    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {
        if ( event instanceof ReadyEvent) {

            DiscordManager discordManager = DiscordManager.getInstance();

            JDA jda = event.getJDA();
            for (TextChannel textChannel : jda.getTextChannels()) {

                String textChannelID = textChannel.getId();

                if ( textChannelID.equals(authChannelID) ) {
                    jda.addEventListener(new AuthenticationChannelMessageHandler(textChannel));
                    discordManager.setAuthChannel(textChannel);
                }

                if ( textChannelID.equals(questionChannelID) ) {
                    jda.addEventListener(new QAChannelMessageHandler(textChannel));
                    discordManager.setQuestionChannel(textChannel);
                }

            }

        }
    }

}
