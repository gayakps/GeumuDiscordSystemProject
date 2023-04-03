package gaya.pe.kr;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class DiscordManager {


    private static class SingleTon {
        private static final DiscordManager DISCORD_MANAGER = new DiscordManager();
    }

    public static DiscordManager getInstance() {
        return SingleTon.DISCORD_MANAGER;
    }

    //989196517803962428 App ID
    //1ec2fc1a60cba9efcf51e1874e905cf9817a3df67c038b74438215fd40ba975f public key
    //MTA5MjMxNDMzOTU1OTYwODMzMA.GTyUf-.i0c5vl5ztAnw8psyVe6gao5pCSx5emzPltQ4WQ token

    final String TOKEN = "OTg5MTk2NTE3ODAzOTYyNDI4.GmiO24.NAq6JH6S4ulMgXtjD4YAmPWwAgQiVPLt3QdSMc";

    public void init() {

        try {
            JDA jda = JDABuilder.createDefault(TOKEN).build();

            // First, get the channel object for the specific channel
            final TextChannel[] channel = {jda.getTextChannelById(1090859961845825566L)};


            jda.addEventListener(new ListenerAdapter() {
                @Override
                public void onGenericEvent(@NotNull GenericEvent event) {

                    if ( event instanceof ReadyEvent ) {
                        JDA jda1 = event.getJDA();
                        for (TextChannel textChannel : jda1.getTextChannels()) {
                            if ( textChannel.getIdLong() == 1090859961845825566L ) {
                                channel[0] = textChannel;
                                channel[0].sendMessage("tes123t\nasdlfjalskfjalkds\nlksdfjlafjkldsajflajfkdls").queue();
                            }
                        }
                    }

                }
            });

// Add a message listener for the channel
            jda.addEventListener(new ListenerAdapter() {
                @Override
                public void onMessageReceived(MessageReceivedEvent event) {

                    if (event.getChannel().getId().equals(channel[0].getId())) {

                        Message message = event.getMessage();
                        System.out.printf("[ID : %s] Message Type : %s\n",message.getId(), message.getType().name());

                        // Check if the message is a reply
                        if (message.getType().equals(MessageType.INLINE_REPLY)) {
                            // Get the replied message
                            Message repliedMessage = event.getMessage().getReferencedMessage();
                            System.out.printf("REPLIED MESSAGE : %s [ %s ]\n", repliedMessage.getContentDisplay(), message.getContentDisplay());
                            // Check if the replied message matches a certain condition
                            if ( message.getContentDisplay().equals("test")) {
                                // Do something with the reply
                                // For example, reply to the reply message

                                MessageAction messageAction = event.getChannel().sendMessage("I detected the reply!");

                                Message removeTargetMessage = event.getChannel().retrieveMessageById(message.getId()).complete();

                                Timer timer = new Timer();

                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        removeTargetMessage.delete().queue();
                                        System.out.println("메시지 제거");
                                    }
                                }, 5000);

                                messageAction.queue();
                            }
                        }
                    }
                }
            });
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }


}
