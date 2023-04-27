package gaya.pe.kr.velocity.minecraft.discord.handler;

import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.velocity.minecraft.discord.exception.NonExistPlayerAuthenticationDataException;
import gaya.pe.kr.velocity.minecraft.discord.handler.abs.MessageChannelHandler;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.checkerframework.checker.units.qual.A;

public class QuestionChannelMessageHandler extends MessageChannelHandler {

    QuestionManager questionManager;
    AnswerManager answerManager;
    DiscordManager discordManager;

    public QuestionChannelMessageHandler(TextChannel textChannel) {
        super(textChannel);
        questionManager = QuestionManager.getInstance();
        answerManager = AnswerManager.getInstance();
        discordManager = DiscordManager.getInstance();
    }

    @Override
    protected void handleEvent(MessageReceivedEvent event) {

        Message receivedMessage = event.getMessage();
        User user = receivedMessage.getAuthor();
        long answerUserId = user.getIdLong(); // 사용자 고유 ID를 가져옵니다.


        MessageType messageType = receivedMessage.getType();

        if ( messageType.equals(MessageType.INLINE_REPLY ) ) {

            if ( !discordManager.isAuthenticationPlayer(answerUserId) ) {
                //TODO 미 인증 유저는 답변할 수 없음
                return;
            }

            Message repliedMessage = event.getMessage().getReferencedMessage(); // 레퍼런스 메시지

            long repliedMessageId = repliedMessage.getIdLong();

            if ( questionManager.existQuestionByDiscordMessageId(repliedMessageId) ) {
                //TODO 존재하는 메시지 일때

                Question question = questionManager.getQuestionByDiscordMessageId(repliedMessageId);

                String questionPlayerFullName = question.getQuestionPlayerName();

                try {
                    long questionPlayerDiscordID = discordManager.getAuthenticationPlayerByDiscordId(questionPlayerFullName);
                    User questionPlayerDiscordUser = discordManager.getJda().getUserById(questionPlayerDiscordID);

                    if ( questionPlayerDiscordUser != null ) {
                        questionPlayerFullName = discordManager.getFullName(questionPlayerDiscordUser);
                    } else {
                        // 디스코드 에서 떠난 유저
                        questionPlayerFullName = questionPlayerFullName + " ( 서버 탈퇴 유저 )";
                    }

                } catch ( NonExistPlayerAuthenticationDataException ignore ) {}

                // TODO 답변 시스템 진행



            } else {
                //TODO 삭제된 메세지 일때
            }

        }
        else {

            String prefix = questionManager.getQuestPrefix();
            String receivedMessageContent = receivedMessage.getContentRaw();

            if ( !receivedMessageContent.startsWith(prefix)) {

                //    @RequirePlaceHolder(placeholders = {"%playername%", "%prefix%"})
                MessageAction errorReply = event.getChannel().sendMessage(questionManager.getQuestPrefixHelpMessage()
                        .replace("%playername%", discordManager.getFullName(user))
                        .replace("%prefix%", prefix)
                );

                errorReply.queue( message -> {
                    VelocityThreadUtil.delayTask(() -> {
                        receivedMessage.delete().queue();
                        message.delete().queue();
                    }, 3000);
                });

                return;
            } else {

                //TODO prefix 를 제외하고 모두 질문으로 인식하여 질문 과정을 거치게됨

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
