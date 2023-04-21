package gaya.pe.kr.velocity.minecraft.discord.handler;

import gaya.pe.kr.velocity.minecraft.discord.handler.abs.MessageChannelHandler;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class QuestionChannelMessageHandler extends MessageChannelHandler {

    public QuestionChannelMessageHandler(TextChannel textChannel) {
        super(textChannel);
    }

    @Override
    protected void handleEvent(MessageReceivedEvent event) {

        Message message = event.getMessage();

        User user = event.getAuthor();

        String username = user.getName(); // 사용자 이름을 가져옵니다.
        String discriminator = user.getDiscriminator(); // 사용자 태그 (예: #1234)를 가져옵니다.
        long userId = user.getIdLong(); // 사용자 고유 ID를 가져옵니다.

        String fullName = username + "#" + discriminator; // 전체 사용자 이름을 생성합니다.

        MessageType messageType = message.getType();

        if ( messageType.equals(MessageType.INLINE_REPLY ) ) {

            Message repliedMessage = event.getMessage().getReferencedMessage(); // 레퍼런스 메시지

            long repliedMessageId = repliedMessage.getIdLong();

            QuestionManager questionManager = QuestionManager.getInstance();

            if ( questionManager.existQuestionByDiscordMessageId(repliedMessageId) ) {
                //TODO 존재하는 메시지 일때
            }

        }


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
