package gaya.pe.kr.velocity.minecraft.discord.handler;

import gaya.pe.kr.velocity.minecraft.discord.handler.abs.MessageChannelHandler;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class QuestionChannelMessageHandler extends MessageChannelHandler {

    public QuestionChannelMessageHandler(TextChannel textChannel) {
        super(textChannel);
    }

    @Override
    protected void handleEvent(MessageReceivedEvent event) {
        // Check if the message is a reply
//                        if (message.getType().equals(MessageType.INLINE_REPLY)) {
//                            // Get the replied message
//                            Message repliedMessage = event.getMessage().getReferencedMessage();
//                            // Check if the replied message matches a certain condition
//                            if (repliedMessage.getContentDisplay().equals("test")) {
//                                // Do something with the reply
//                                // For example, reply to the reply message
//
//                                MessageAction messageAction = event.getChannel().sendMessage("I detected the reply!");
//
//                                Message removeTargetMessage = event.getChannel().retrieveMessageById(message.getId()).complete();
//
//                                VelocityThreadUtil.delayTask( ()-> {
//                                    removeTargetMessage.delete().queue();
//                                    System.out.println("메시지 제거");
//                                }, 5000);
//
//                                messageAction.queue();
//                            }
//                        }
    }
}
