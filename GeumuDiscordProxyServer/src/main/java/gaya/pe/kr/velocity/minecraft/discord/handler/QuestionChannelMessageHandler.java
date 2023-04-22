package gaya.pe.kr.velocity.minecraft.discord.handler;

import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.velocity.minecraft.discord.exception.NonExistPlayerAuthenticationDataException;
import gaya.pe.kr.velocity.minecraft.discord.handler.abs.MessageChannelHandler;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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

        Message message = event.getMessage();

        User answerUser = event.getAuthor();

        String username = answerUser.getName(); // 사용자 이름을 가져옵니다.
        String discriminator = answerUser.getDiscriminator(); // 사용자 태그 (예: #1234)를 가져옵니다.
        long answerUserId = answerUser.getIdLong(); // 사용자 고유 ID를 가져옵니다.

        String answerFullName = username + "#" + discriminator; // 전체 사용자 이름을 생성합니다.

        MessageType messageType = message.getType();

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
